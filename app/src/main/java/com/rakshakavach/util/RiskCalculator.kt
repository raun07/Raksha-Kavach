package com.rakshakavach.util

import com.rakshakavach.domain.model.PPEItem
import com.rakshakavach.domain.model.RiskLevel
import com.rakshakavach.domain.model.TaskModel

data class RiskResult(
    val level: RiskLevel,
    val likelyInjuries: List<String>
)

object RiskCalculator {

    /**
     * Calculates the risk level and likely injuries based on missing PPE items for a given task.
     * 
     * @param task The task being performed.
     * @param uncheckedPPEs The list of PPE items that the worker has NOT checked (i.e. missing).
     */
    fun calculateRisk(task: TaskModel, uncheckedPPEs: List<PPEItem>): RiskResult {
        if (uncheckedPPEs.isEmpty()) {
            return RiskResult(RiskLevel.LOW, emptyList())
        }

        var totalRiskScore = 0
        val injuries = mutableListOf<String>()

        for (ppe in uncheckedPPEs) {
            val weight = task.riskWeight[ppe] ?: 0
            totalRiskScore += weight

            when (ppe) {
                PPEItem.HELMET -> injuries.add("Head Injury / Concussion")
                PPEItem.GLOVES -> if (task.taskId == "T004") injuries.add("Electric Shock / Hand Burns") else injuries.add("Hand Cuts / Abrasions")
                PPEItem.BOOTS -> injuries.add("Foot Crushing / Puncture Wounds")
                PPEItem.GOGGLES -> injuries.add("Eye Damage / Blindness")
                PPEItem.HARNESS -> injuries.add("Fatal Fall from Height")
                PPEItem.APRON -> injuries.add("Body Burns / Chemical Splash")
                PPEItem.RESPIRATOR -> injuries.add("Toxic Inhalation / Lung Damage")
                PPEItem.EARPLUGS -> injuries.add("Hearing Loss")
                PPEItem.VEST -> injuries.add("Struck by Vehicle / Machinery")
                PPEItem.FACE_SHIELD -> injuries.add("Severe Facial Burns / Cuts")
            }
        }

        val level = when {
            totalRiskScore >= 100 -> RiskLevel.CRITICAL
            totalRiskScore >= 60 -> RiskLevel.HIGH
            totalRiskScore >= 30 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        return RiskResult(level, injuries.distinct())
    }
}
