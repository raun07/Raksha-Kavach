# 🛡️ Raksha-Kavach — Worker Safety Auditor

> **Raksha-Kavach** (meaning: Shield-Armour) is an AI-powered Android app that helps construction and factory workers in India stay safe on the job through visual PPE compliance, real-time risk assessment, and daily safety education.

---

## 📱 Screenshots

<p align="center">
  <img src="screenshots/ss1_home.jpeg" width="180" alt="Home Screen"/>
  <img src="screenshots/ss2_task_selector.jpeg" width="180" alt="Task Selector"/>
  <img src="screenshots/ss3_task_selector2.jpeg" width="180" alt="All Tasks"/>
  <img src="screenshots/ss4_task_selected.jpeg" width="180" alt="Task Selected"/>
</p>

<p align="center">
  <img src="screenshots/ss5_checklist.jpeg" width="180" alt="Safety Checklist"/>
  <img src="screenshots/ss6_avatar.jpeg" width="180" alt="PPE Avatar"/>
  <img src="screenshots/ss7_quiz.jpeg" width="180" alt="Daily Quiz"/>
  <img src="screenshots/ss8_incident.jpeg" width="180" alt="Report Incident"/>
  <img src="screenshots/ss9_profile.jpeg" width="180" alt="Worker Profile"/>
</p>

---

## 🚨 The Problem

Construction and factory workers in India's unorganized sector routinely skip Personal Protective Equipment (PPE) because they find it cumbersome or believe they are safe enough. There is no visual, task-specific mechanism that communicates the real risk level of each job — leading to thousands of preventable workplace fatalities every year.

---

## ✅ The Solution

Raksha-Kavach is a **Worker Safety Auditor**. A worker or supervisor picks the task for the day, gets a checklist of exact safety gear needed, and sees through an animated avatar what happens when they skip it. A risk meter shows real injury probabilities, a Gemini AI-powered daily quiz builds safety awareness, and a scoring system rewards accident-free streaks.

---

## ✨ Features

| Feature | Description |
|---|---|
| 📋 **Task-Based PPE Checklist** | Dynamic checklist that changes based on selected work task (10+ tasks) |
| ⚠️ **Real-Time Risk Meter** | Visual gauge showing danger level as PPE items are unchecked |
| 🧑‍🏭 **Animated PPE Avatar** | Canvas-drawn worker avatar that equips/removes gear in real time |
| 🧠 **AI Safety Quiz** | Gemini AI generates task-specific MCQ safety questions daily |
| 📊 **Safety Score & Streaks** | Gamified scoring with badges for accident-free consecutive days |
| 🚨 **Incident Log** | Offline near-miss and accident reporting stored in Room DB |
| ⏰ **Daily Reminders** | WorkManager-scheduled morning notifications to complete safety checks |
| 🌐 **Offline-First** | Core features work fully without internet connectivity |
| 🇮🇳 **Bilingual UI** | English + Hindi labels for wider accessibility |

---

## 🛠️ Tech Stack

```
Language        : Kotlin (100%)
UI              : Jetpack Compose + Material 3
Architecture    : MVVM with Repository Pattern
DI              : Dagger Hilt
Local Database  : Room Persistence Library (SQLite)
Background Work : Android WorkManager
Settings        : Jetpack DataStore
Networking      : Retrofit 2 + OkHttp
AI Integration  : Google Gemini API (gemini-pro)
Animations      : Lottie + Compose Canvas
Build System    : AGP 9.2.1 + Kotlin 2.3.0 + KSP 2.3.7
Min SDK         : 26 (Android 8.0)
Target SDK      : 36
```

---

## 🏗️ Project Structure

```
com.rakshakavach
├── data/
│   ├── local/          # Room DB, DAOs, Entities, TypeConverters
│   ├── remote/         # Gemini API Service + Response Models
│   └── repository/     # GeminiRepository
├── domain/
│   ├── model/          # TaskData, PPEItem, RiskLevel, QuizQuestion
│   └── repository/     # IncidentRepository, SafetyScoreRepository
├── di/                 # Hilt Modules (DB, Network, Repository, DataStore)
├── ui/
│   ├── theme/          # Yellow/Black Industrial Theme
│   ├── navigation/     # NavGraph, Screen routes, BottomNavBar
│   ├── home/           # Home Screen + SafetyScoreViewModel
│   ├── taskselector/   # Task Grid Screen
│   ├── checklist/      # PPE Checklist + Risk Meter Screen
│   ├── avatar/         # Animated PPE Avatar Screen
│   ├── quiz/           # AI Quiz Screen + QuizViewModel
│   ├── incidentlog/    # Incident Report + History Screens
│   ├── profile/        # Profile + Badges Screen
│   ├── settings/       # Settings Screen + ViewModel
│   └── splash/         # Splash Screen
├── util/               # RiskCalculator, NotificationScheduler
└── workers/            # DailyReminderWorker
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android device or emulator with API 26+
- Google Gemini API key (free at [aistudio.google.com](https://aistudio.google.com))

### Setup

1. **Clone the repository**
```bash
git clone https://github.com/raun07/Raksha-Kavach.git
cd Raksha-Kavach
```

2. **Add your Gemini API key**

Create or open `local.properties` in the project root and add:
```properties
GEMINI_API_KEY=your_gemini_api_key_here
```

3. **Build and Run**

Open in Android Studio → Sync Gradle → Hit ▶ Run

---

## 🎯 Impact Goals

- **Occupational Health** — Reduce workplace fatalities in India's unorganized construction sector
- **Worker Dignity** — Ensure every laborer's safety is prioritized and valued
- **Safety Culture** — Build a Safety-First mindset through daily education and gamification
- **Accident Prevention** — Near-miss logs help supervisors identify hazard patterns

---

## 🏆 Built With

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Google Gemini AI](https://aistudio.google.com)
- [Dagger Hilt](https://dagger.dev/hilt)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Android WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Retrofit](https://square.github.io/retrofit)
- [Lottie](https://airbnb.io/lottie)

---

## 👨‍💻 Developer

**Vaibhav** — Final Year CSE Student, Sir M Visvesvaraya Institute of Technology, Bengaluru

---

## 📄 License

```
MIT License — feel free to use, modify and distribute
```
