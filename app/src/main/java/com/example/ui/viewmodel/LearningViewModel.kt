package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.curriculum.EnglishCurriculum
import com.example.data.model.ChildProfile
import com.example.data.model.LearningWord
import com.example.data.model.MasteredWord
import com.example.data.model.QuizSession
import com.example.data.repository.LearningRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class LearningDashboardData(
    val profile: ChildProfile,
    val masteredWordIds: Set<String>,
    val masteredCountByCategory: Map<String, Int>,
    val recentSessions: List<QuizSession>
)

data class QuizQuestion(
    val targetWord: LearningWord,
    val options: List<LearningWord>
)

data class ListenQuizUiState(
    val categoryId: String = "alphabet",
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionId: String? = null,
    val score: Int = 0,
    val streak: Int = 0,
    val starsEarned: Int = 0,
    val isFinished: Boolean = false
)

data class LetterTile(
    val id: Int,
    val letter: Char
)

data class SpellingUiState(
    val categoryId: String = "alphabet",
    val words: List<LearningWord> = emptyList(),
    val currentIndex: Int = 0,
    val targetCleanWord: String = "",
    val availableTiles: List<LetterTile> = emptyList(),
    val chosenTileIds: List<Int> = emptyList(),
    val isChecked: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val starsEarned: Int = 0,
    val isFinished: Boolean = false
)

class LearningViewModel(
    private val repository: LearningRepository,
    private val currentUserId: String,
    private val defaultDisplayName: String
) : ViewModel() {

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _quizState = MutableStateFlow(ListenQuizUiState())
    val quizState: StateFlow<ListenQuizUiState> = _quizState.asStateFlow()

    private val _spellingState = MutableStateFlow(SpellingUiState())
    val spellingState: StateFlow<SpellingUiState> = _spellingState.asStateFlow()

    val dashboardState: StateFlow<UiState<LearningDashboardData>> = combine(
        repository.observeProfile(currentUserId),
        repository.observeMasteredWords(currentUserId),
        repository.observeQuizSessions(currentUserId)
    ) { profile, masteredWords, sessions ->
        val safeProfile = profile ?: ChildProfile(
            id = currentUserId,
            userId = currentUserId,
            childName = defaultDisplayName.ifBlank { "بطل الإنجليزية" }
        )
        val masteredIds = masteredWords.map { it.wordId }.toSet()
        val countByCat = masteredWords.groupingBy { it.categoryId }.eachCount()
        UiState.Success(
            LearningDashboardData(
                profile = safeProfile,
                masteredWordIds = masteredIds,
                masteredCountByCategory = countByCat,
                recentSessions = sessions
            )
        ) as UiState<LearningDashboardData>
    }.catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing learning dashboard", rootError)
        emit(UiState.Error(rootError.message ?: "تعذر تحميل بيانات التعلم"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading
    )

    init {
        viewModelScope.launch {
            repository.ensureProfileExists(defaultDisplayName)
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun toggleMastered(word: LearningWord) {
        viewModelScope.launch {
            repository.ensureProfileExists(defaultDisplayName)
            val result = repository.markWordMastered(word)
            result.onSuccess { nowMastered ->
                _actionMessage.value = if (nowMastered) {
                    "أحسنت! ربحت 5 نجوم لحفظ ${word.englishWord} ⭐"
                } else {
                    "تمت إزالة ${word.englishWord} من المحفوظات"
                }
            }.onFailure { err ->
                _actionMessage.value = "تعذر حفظ التقدم: ${err.localizedMessage ?: "خطأ غير متوقع"}"
            }
        }
    }

    fun updateProfile(childName: String, avatarId: String) {
        viewModelScope.launch {
            repository.ensureProfileExists(defaultDisplayName)
            val result = repository.updateProfileDetails(childName, avatarId)
            result.onSuccess {
                _actionMessage.value = "تم تحديث ملف البطل بنجاح!"
            }.onFailure { err ->
                _actionMessage.value = "تعذر تحديث الملف: ${err.localizedMessage ?: "خطأ"}"
            }
        }
    }

    fun startListenQuiz(categoryId: String) {
        val pool = EnglishCurriculum.wordsForCategory(categoryId)
        val allPool = if (pool.size >= 4) pool else EnglishCurriculum.words
        val targets = pool.shuffled().take(QUIZ_QUESTION_COUNT)
        val questions = targets.map { target ->
            val distractors = allPool.filter { it.id != target.id }.shuffled().take(3)
            val options = (distractors + target).shuffled()
            QuizQuestion(targetWord = target, options = options)
        }
        _quizState.value = ListenQuizUiState(
            categoryId = categoryId,
            questions = questions,
            currentIndex = 0,
            selectedOptionId = null,
            score = 0,
            streak = 0,
            starsEarned = 0,
            isFinished = false
        )
    }

    fun selectQuizOption(optionId: String) {
        val current = _quizState.value
        if (current.selectedOptionId != null || current.isFinished) return
        val activeQuestion = current.questions.getOrNull(current.currentIndex) ?: return
        val isCorrect = optionId == activeQuestion.targetWord.id
        val newScore = if (isCorrect) current.score + 1 else current.score
        val newStreak = if (isCorrect) current.streak + 1 else 0
        val bonus = if (isCorrect && newStreak >= 2) 5 else 0
        val addedStars = if (isCorrect) 10 + bonus else 2

        _quizState.value = current.copy(
            selectedOptionId = optionId,
            score = newScore,
            streak = newStreak,
            starsEarned = current.starsEarned + addedStars
        )
    }

    fun advanceQuizQuestion() {
        val current = _quizState.value
        val nextIndex = current.currentIndex + 1
        if (nextIndex >= current.questions.size) {
            _quizState.value = current.copy(isFinished = true)
            viewModelScope.launch {
                repository.ensureProfileExists(defaultDisplayName)
                repository.recordQuizSession(
                    categoryId = current.categoryId,
                    gameType = "listen_quiz",
                    score = current.score,
                    totalQuestions = current.questions.size.coerceAtLeast(1),
                    starsEarned = current.starsEarned
                )
            }
        } else {
            _quizState.value = current.copy(
                currentIndex = nextIndex,
                selectedOptionId = null
            )
        }
    }

    fun startSpellingGame(categoryId: String) {
        val eligible = EnglishCurriculum.spellingEligibleWords(categoryId)
            .ifEmpty { EnglishCurriculum.spellingEligibleWords("mixed") }
            .shuffled()
            .take(SPELLING_PUZZLE_COUNT)
        val firstWord = eligible.firstOrNull()
        val firstClean = firstWord?.englishWord?.uppercase() ?: ""
        _spellingState.value = SpellingUiState(
            categoryId = categoryId,
            words = eligible,
            currentIndex = 0,
            targetCleanWord = firstClean,
            availableTiles = buildScrambledTiles(firstClean),
            chosenTileIds = emptyList(),
            isChecked = false,
            isCorrect = false,
            score = 0,
            starsEarned = 0,
            isFinished = eligible.isEmpty()
        )
    }

    fun tapSpellingTile(tileId: Int) {
        val current = _spellingState.value
        if (current.isChecked || current.isFinished) return
        if (tileId in current.chosenTileIds) return
        if (current.chosenTileIds.size >= current.targetCleanWord.length) return

        val updatedChosen = current.chosenTileIds + tileId
        if (updatedChosen.size == current.targetCleanWord.length) {
            val spelled = updatedChosen.mapNotNull { id ->
                current.availableTiles.firstOrNull { it.id == id }?.letter
            }.joinToString("")
            val correct = spelled == current.targetCleanWord
            _spellingState.value = current.copy(
                chosenTileIds = updatedChosen,
                isChecked = true,
                isCorrect = correct,
                score = if (correct) current.score + 1 else current.score,
                starsEarned = current.starsEarned + if (correct) 15 else 3
            )
        } else {
            _spellingState.value = current.copy(chosenTileIds = updatedChosen)
        }
    }

    fun undoSpellingTile() {
        val current = _spellingState.value
        if (current.isFinished || current.chosenTileIds.isEmpty()) return
        if (current.isChecked && current.isCorrect) return
        _spellingState.value = current.copy(
            chosenTileIds = current.chosenTileIds.dropLast(1),
            isChecked = false,
            isCorrect = false
        )
    }

    fun retryCurrentSpellingWord() {
        val current = _spellingState.value
        if (current.isFinished || current.isCorrect) return
        _spellingState.value = current.copy(
            chosenTileIds = emptyList(),
            isChecked = false,
            isCorrect = false
        )
    }

    fun advanceSpellingWord() {
        val current = _spellingState.value
        val nextIndex = current.currentIndex + 1
        if (nextIndex >= current.words.size) {
            _spellingState.value = current.copy(isFinished = true)
            viewModelScope.launch {
                repository.ensureProfileExists(defaultDisplayName)
                repository.recordQuizSession(
                    categoryId = current.categoryId,
                    gameType = "spelling_game",
                    score = current.score,
                    totalQuestions = current.words.size.coerceAtLeast(1),
                    starsEarned = current.starsEarned
                )
            }
        } else {
            val nextWord = current.words[nextIndex]
            val clean = nextWord.englishWord.uppercase()
            _spellingState.value = current.copy(
                currentIndex = nextIndex,
                targetCleanWord = clean,
                availableTiles = buildScrambledTiles(clean),
                chosenTileIds = emptyList(),
                isChecked = false,
                isCorrect = false
            )
        }
    }

    private fun buildScrambledTiles(cleanWord: String): List<LetterTile> {
        val indexed = cleanWord.mapIndexed { idx, ch -> LetterTile(id = idx, letter = ch) }
        if (indexed.size <= 1) return indexed
        var shuffled = indexed.shuffled()
        if (shuffled.map { it.letter }.joinToString("") == cleanWord) {
            shuffled = indexed.reversed()
        }
        return shuffled
    }

    private companion object {
        const val TAG = "LearningVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
        const val QUIZ_QUESTION_COUNT = 5
        const val SPELLING_PUZZLE_COUNT = 5
    }
}
