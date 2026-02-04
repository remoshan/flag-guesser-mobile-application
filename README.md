## flag-guesser-mobile-application  
**A fast, lightweight Android quiz app to test your knowledge of world flags.**

> Guess the flag, race the timer, and sharpen your geography skills — powered by modern Kotlin and API-driven data.

---

### Visuals

![Home Screen](https://github.com/user-attachments/assets/2a2dc120-97d6-4bfb-b849-045af162ac7e)
![Game Screen](https://github.com/user-attachments/assets/8b8b672e-c7dc-40b9-a4cc-18c3f5431619)
![Score Screen](https://github.com/user-attachments/assets/27e9a039-bab5-4487-8d2c-fec94fbff377)

---

### Features

- **Interactive Flag Quizzes**: Multiple-choice questions with increasing difficulty.
- **API-Driven Data**: Fetches up-to-date flag and country data from remote APIs.
- **Score Tracking**: Keeps track of your correct answers and best streaks.
- **Lightweight & Fast**: Built natively with Kotlin for smooth Android performance.
- **Extensible Architecture**: Clear separation of concerns (data, domain, UI) for easy feature additions.

---

### Getting Started

#### Prerequisites

- **Android Studio** (latest stable version)
- **JDK 17** (or version required by your Android Studio setup)
- **Android SDK** with:
  - Minimum SDK: `XX` (update as appropriate, e.g. `23`)
  - Target SDK: `YY` (e.g. `34`)
- **Git** (for cloning the repository)

#### Installation

1. **Clone the repository**

```bash
git clone https://github.com/<your-username>/flag-guesser-mobile-application.git
cd flag-guesser-mobile-application
```

2. **Open in Android Studio**

- Open Android Studio  
- Click **"Open"** and select the project directory  
- Let Gradle sync and download dependencies

3. **Configure environment variables**

Create a `local.properties` file or use your preferred secrets management approach, based on the configuration table below.

4. **Run the app**

- Select a device or emulator from the device dropdown  
- Click **Run** (▶) in Android Studio

---

### Usage

Below is a simplified example of how the core quiz flow might be implemented in Kotlin using a ViewModel and a repository that fetches flags from an API.

```kotlin
// QuizViewModel.kt
class QuizViewModel(
    private val flagRepository: FlagRepository
) : ViewModel() {

    private val _currentQuestion = MutableLiveData<FlagQuestion>()
    val currentQuestion: LiveData<FlagQuestion> = _currentQuestion

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    fun loadNextQuestion() {
        viewModelScope.launch {
            val flags = flagRepository.getRandomFlags(count = 4)
            val correctFlag = flags.random()

            _currentQuestion.value = FlagQuestion(
                correctFlag = correctFlag,
                options = flags
            )
        }
    }

    fun submitAnswer(selected: Flag) {
        val question = _currentQuestion.value ?: return
        if (selected.code == question.correctFlag.code) {
            _score.value = (_score.value ?: 0) + 1
        }
        loadNextQuestion()
    }
}

// Example usage in an Activity / Fragment (pseudo-code)
class QuizFragment : Fragment() {

    private val viewModel: QuizViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            // Update UI with flag image and options
            // imageView.load(question.correctFlag.imageUrl)
            // optionsAdapter.submitList(question.options)
        }

        viewModel.score.observe(viewLifecycleOwner) { score ->
            // Update score text view
            // scoreTextView.text = "Score: $score"
        }

        // Called when user selects an answer
        // optionsAdapter.onOptionClicked = { selectedFlag ->
        //     viewModel.submitAnswer(selectedFlag)
        // }

        viewModel.loadNextQuestion()
    }
}
```

This example illustrates the typical usage pattern:

- **Fetch** flags from an API via `FlagRepository`.
- **Generate** a `FlagQuestion` with one correct flag and multiple options.
- **Observe** `LiveData` in the UI layer to render the current question and score.
- **Handle** user selection with `submitAnswer` and automatically load the next question.

---

### Configuration

Use these environment variables (or equivalent secure config) to control API access and behavior.

| Variable Name          | Description                                   | Example Value                        | Required |
|------------------------|-----------------------------------------------|--------------------------------------|----------|
| `FLAG_API_BASE_URL`    | Base URL for the flag/country API            | `https://restcountries.com/v3.1`     | Yes      |
| `FLAG_API_KEY`         | API key or token (if your provider requires) | `your-api-key-here`                  | Maybe    |
| `FLAG_API_TIMEOUT_MS`  | Network timeout in milliseconds              | `10000`                              | No       |
| `QUIZ_QUESTION_COUNT`  | Default number of questions per quiz session | `10`                                 | No       |

Example `local.properties` snippet (do not commit secrets):

```properties
FLAG_API_BASE_URL=https://restcountries.com/v3.1
FLAG_API_KEY=your-api-key-here
FLAG_API_TIMEOUT_MS=10000
QUIZ_QUESTION_COUNT=10
```

---

### Contributing

Contributions are welcome and appreciated.

1. **Fork** the repository on GitHub.
2. **Create a feature branch** from `main`:

   ```bash
   git checkout -b feature/my-new-feature
   ```

3. **Make your changes** with clear commit messages.
4. **Add tests** or update existing ones where applicable.
5. **Open a Pull Request**:
   - Describe the change and motivation.
   - Reference related issues (e.g. `Closes #123`).
   - Ensure CI checks pass before requesting review.

Please keep your changes focused and small; large, unrelated changes may be requested to be split into multiple PRs.

---

### License

This project is licensed under the **MIT License**.  
See the `LICENSE` file for full license text.
