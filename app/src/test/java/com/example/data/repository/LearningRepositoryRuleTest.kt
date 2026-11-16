package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.LearningWord
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class LearningRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private val sampleWord = LearningWord(
        id = "alpha_a",
        categoryId = "alphabet",
        displayPrimary = "A a",
        englishWord = "Apple",
        arabicMeaning = "تفاحة",
        arabicPronunciation = "آبل",
        emoji = "🍎",
        exampleSentenceEn = "A is for Apple.",
        exampleSentenceAr = "حرف A لكلمة تفاحة."
    )

    @Test
    fun ensureProfileExists_authenticatedOwner_createsAndObservesProfile() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = LearningRepository(firestore, auth)

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.ensureProfileExists("ياسين")
        }
        assertTrue(result.isSuccess)
        val profile = result.getOrThrow()
        assertEquals(uid, profile.userId)

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeProfile(uid).first { it != null }
        }
        assertEquals(uid, observed?.userId)
    }

    @Test
    fun markWordMastered_and_recordQuizSession_updatesOwnerProgress() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = LearningRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) { repository.ensureProfileExists("نور") }

        val uniqueWord = sampleWord.copy(id = "word_${UUID.randomUUID().toString().take(8)}")
        val wordResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.markWordMastered(uniqueWord)
        }
        assertTrue(wordResult.isSuccess)
        assertEquals(true, wordResult.getOrThrow())

        val words = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeMasteredWords(uid).first { list -> list.any { it.wordId == uniqueWord.id } }
        }
        assertTrue(words.any { it.wordId == uniqueWord.id })

        val quizResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.recordQuizSession(
                categoryId = "alphabet",
                gameType = "listen_quiz",
                score = 4,
                totalQuestions = 5,
                starsEarned = 40
            )
        }
        assertTrue(quizResult.isSuccess)
    }

    @Test
    fun crossUserRead_deniedWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = LearningRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) { aliceRepo.ensureProfileExists("أحمد") }

        signInTestUser(BOB_EMAIL)
        val bobRepo = LearningRepository(firestore, auth)

        var caught: Exception? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                bobRepo.observeProfile(aliceUid).first()
            }
        } catch (e: Exception) {
            caught = e
        }
        val firestoreException = checkNotNull(caught?.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $caught"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    @Test
    fun unauthenticatedObserve_deniedWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = LearningRepository(firestore, auth)

        var caught: Exception? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeMasteredWords("some_user_123").first()
            }
        } catch (e: Exception) {
            caught = e
        }
        val firestoreException = checkNotNull(caught?.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $caught"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_kids@test.com"
        const val BOB_EMAIL = "bob_kids@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
