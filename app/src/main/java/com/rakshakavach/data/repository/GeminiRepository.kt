package com.rakshakavach.data.repository

import com.rakshakavach.BuildConfig
import com.rakshakavach.data.remote.Content
import com.rakshakavach.data.remote.GeminiApiService
import com.rakshakavach.data.remote.GeminiRequest
import com.rakshakavach.data.remote.Part
import com.rakshakavach.domain.model.QuizQuestion
import org.json.JSONArray
import org.json.JSONException

class GeminiRepository(private val apiService: GeminiApiService) {

    suspend fun generateSafetyQuiz(taskName: String): List<QuizQuestion> {
        val prompt = "Generate 5 multiple choice safety quiz questions for a worker doing $taskName on a construction site in India. For each question provide: question text, 4 options (A/B/C/D), correct answer, and a one-line explanation. Return as JSON array of objects with keys: \"question\", \"options\" (array of strings), \"correctAnswer\", \"explanation\"."
        
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            )
        )

        return try {
            val response = apiService.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            
            // Clean up the response if it's wrapped in markdown blocks
            val cleanedJson = jsonText.replace("```json", "").replace("```", "").trim()
            parseQuizJson(cleanedJson)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getSafetyTip(taskName: String): String {
        val prompt = "Provide a single, short, impactful daily safety tip for a worker doing $taskName."
        
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            )
        )

        return try {
            val response = apiService.generateContent(BuildConfig.GEMINI_API_KEY, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "Stay alert and stay safe."
        } catch (e: Exception) {
            e.printStackTrace()
            "Always wear your mandatory PPE and stay alert."
        }
    }

    private fun parseQuizJson(jsonString: String): List<QuizQuestion> {
        val quizList = mutableListOf<QuizQuestion>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val optionsArray = jsonObject.getJSONArray("options")
                val optionsList = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    optionsList.add(optionsArray.getString(j))
                }
                
                quizList.add(
                    QuizQuestion(
                        question = jsonObject.getString("question"),
                        options = optionsList,
                        correctAnswer = jsonObject.getString("correctAnswer"),
                        explanation = jsonObject.getString("explanation")
                    )
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return quizList
    }
}
