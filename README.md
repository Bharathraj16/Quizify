# 🎯 QuizGenie (Quizify)

> AI-Powered Quiz Generator for Indian Competitive Exams

QuizGenie transforms any document — PDF, DOCX, TXT, or pasted text — into a fully customized multiple-choice quiz using AI. Built specifically for Indian competitive exam aspirants (Banking, SSC, RRB, TNPSC, UPSC, and more), it analyzes your study material and generates relevant questions, complete with explanations and performance analytics.

---

## ✨ Features

- **📄 Multi-format input** — Upload PDF, DOCX, TXT files, or simply paste text copied from ChatGPT, Grok, or any source
- **🎓 Exam-specific question generation** — Tailored to Banking, SSC, RRB, TNPSC, UPSC, and custom exam patterns
- **⚡ Adjustable difficulty** — Easy, Medium, Hard, or Mixed difficulty levels
- **🔢 Flexible question count** — Generate anywhere from 5 to 100 questions per quiz
- **⏱️ Optional timer** — Set time limits (15 min / 30 min / 1 hour) or go untimed
- **📊 Detailed result analytics** — Score breakdown, correct/wrong/skipped stats, and topic-wise weak area identification
- **🤖 AI performance tips** — Personalized feedback based on your quiz performance
- **📤 Share results** — Share your quiz scores directly to WhatsApp, Telegram, or any app
- **🌙 Modern dark UI** — Clean, distraction-free interface built entirely with Jetpack Compose

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM + Repository Pattern |
| **Dependency Injection** | Dagger Hilt |
| **Networking** | Retrofit + OkHttp |
| **AI Inference** | Groq API (Llama 3.3 70B) |
| **Document Parsing** | PDFBox-Android, Apache POI (DOCX) |
| **Local Storage** | Jetpack DataStore |
| **Navigation** | Navigation Compose |
| **Async** | Kotlin Coroutines + Flow |

---

## 📱 Screens

1. **Splash Screen** — Onboarding intro shown on first launch only
2. **Upload/Configure Screen** — Upload a document or paste text, select exam type, difficulty, question count, and timer
3. **Loading Screen** — AI generation progress indicator
4. **Quiz Screen** — Question navigation with progress bar, question grid overview, and answer selection
5. **Result Screen** — Score circle, stats breakdown, AI tip, weak topics, and full answer review

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 11+
- Android device/emulator running API 26 (Android 8.0) or higher
- A free [Groq API key](https://console.groq.com)

### Setup

1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/quizify.git
   ```

2. Open the project in Android Studio

3. Add your Groq API key in `QuizRepository.kt`:
   ```kotlin
   private val API_KEY = "Bearer your_groq_api_key_here"
   ```

   > ⚠️ **For production**, do not hardcode the key in the app. Route requests through a backend proxy (e.g., Cloudflare Workers) to keep your key secure and apply per-user rate limiting.

4. Sync Gradle and run the app

---

## 🔐 Security Note

This project currently calls the Groq API directly from the client for development purposes. Before publishing to the Play Store, it's strongly recommended to:

- Move the API key to a backend proxy (Cloudflare Workers, Render, etc.)
- Implement per-user rate limiting to prevent quota exhaustion
- Never commit API keys to version control

---

## 📂 Project Structure

```
com.example.quizify/
├── data/
│   ├── model/          # Data classes (Question, QuizConfig, QuizResult, etc.)
│   └── repository/     # QuizRepository — AI API communication
├── domain/
│   └── ai/              # Retrofit API service interfaces
├── di/                  # Hilt dependency injection modules
├── ui/
│   ├── screens/         # Composable screens
│   ├── components/      # Reusable Composable components
│   ├── navhost/          # Navigation graph
│   ├── theme/            # Colors, typography, theming
│   └── viewModel/        # QuizViewModel
└── utils/                # DocumentParser, TextSummarizer, UserPreferences
```

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome. Feel free to check the [issues page](../../issues) or submit a pull request.

---

## 📄 License

This project is open source and available for educational and personal use.

---

## 🙏 Acknowledgements

- [Groq](https://groq.com) for fast LLM inference
- [PDFBox-Android](https://github.com/TomRoush/PdfBox-Android) for PDF text extraction
- [Apache POI](https://poi.apache.org) for DOCX parsing
