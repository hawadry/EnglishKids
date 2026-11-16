package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.ChildProfile
import com.example.data.model.LearningWord
import com.example.data.model.MasteredWord
import com.example.data.model.QuizSession
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class LearningRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeProfile(userId: String): Flow<ChildProfile?> = flow {
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            ChildProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    logStreamError(error, OperationType.GET, docRef.path, userId)
                    throw error
                }
        )
    }

    fun observeMasteredWords(userId: String): Flow<List<MasteredWord>> = flow {
        val colRef = db.collection("users").document(userId).collection("masteredWords")
        emitAll(
            colRef.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        MasteredWord::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamError(error, OperationType.LIST, colRef.path, userId)
                    throw error
                }
        )
    }

    fun observeQuizSessions(userId: String): Flow<List<QuizSession>> = flow {
        val colRef = db.collection("users").document(userId).collection("quizSessions")
        emitAll(
            colRef.orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(20)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        QuizSession::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamError(error, OperationType.LIST, colRef.path, userId)
                    throw error
                }
        )
    }

    suspend fun ensureProfileExists(defaultName: String = "بطل الإنجليزية"): Result<ChildProfile> {
        val uid = try {
            requireUserId()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        val docRef = db.collection("users").document(uid)
        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val existing = snapshot.toObject(
                    ChildProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ) ?: ChildProfile(id = uid, userId = uid)
                Result.success(existing)
            } else {
                val safeName = defaultName.trim().ifBlank { "بطل الإنجليزية" }.take(40)
                val payload = mapOf(
                    "userId" to uid,
                    "childName" to safeName,
                    "avatarId" to "owl",
                    "totalStars" to 0L,
                    "streakDays" to 1L,
                    "completedQuizzes" to 0L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                val created = docRef.get().await().toObject(
                    ChildProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ) ?: ChildProfile(id = uid, userId = uid, childName = safeName)
                Result.success(created)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun updateProfileDetails(childName: String, avatarId: String): Result<Unit> {
        val uid = try {
            requireUserId()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        val docRef = db.collection("users").document(uid)
        val safeName = childName.trim().ifBlank { "بطل الإنجليزية" }.take(40)
        val safeAvatar = if (avatarId in VALID_AVATARS) avatarId else "owl"
        return try {
            docRef.update(
                mapOf(
                    "childName" to safeName,
                    "avatarId" to safeAvatar,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun markWordMastered(word: LearningWord): Result<Boolean> {
        val uid = try {
            requireUserId()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        val userRef = db.collection("users").document(uid)
        val wordRef = userRef.collection("masteredWords").document(word.id)
        return try {
            val existing = wordRef.get().await()
            if (existing.exists()) {
                wordRef.delete().await()
                Result.success(false)
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "wordId" to word.id,
                    "categoryId" to word.categoryId,
                    "englishWord" to word.englishWord.take(64),
                    "starsEarned" to STARS_PER_WORD,
                    "masteredAt" to FieldValue.serverTimestamp()
                )
                val batch = db.batch()
                batch.set(wordRef, payload)
                batch.update(
                    userRef,
                    mapOf(
                        "totalStars" to FieldValue.increment(STARS_PER_WORD),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                batch.commit().await()
                Result.success(true)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, wordRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun recordQuizSession(
        categoryId: String,
        gameType: String,
        score: Int,
        totalQuestions: Int,
        starsEarned: Int
    ): Result<String> {
        val uid = try {
            requireUserId()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        val sessionId = "quiz_${UUID.randomUUID().toString().replace("-", "")}"
        val userRef = db.collection("users").document(uid)
        val sessionRef = userRef.collection("quizSessions").document(sessionId)
        val payload = mapOf(
            "userId" to uid,
            "categoryId" to categoryId,
            "gameType" to gameType,
            "score" to score.toLong(),
            "totalQuestions" to totalQuestions.toLong(),
            "starsEarned" to starsEarned.toLong(),
            "createdAt" to FieldValue.serverTimestamp()
        )
        return try {
            val batch = db.batch()
            batch.set(sessionRef, payload)
            batch.update(
                userRef,
                mapOf(
                    "totalStars" to FieldValue.increment(starsEarned.toLong()),
                    "completedQuizzes" to FieldValue.increment(1L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            Result.success(sessionId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, sessionRef.path, auth)
            Result.failure(e)
        }
    }

    private fun logStreamError(
        error: Throwable,
        operationType: OperationType,
        path: String,
        expectedUid: String
    ) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) return
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == expectedUid) {
            handleFirestoreError(exceptionToLog, operationType, path, auth)
        }
    }

    companion object {
        const val STARS_PER_WORD = 5L
        val VALID_AVATARS = setOf("owl", "lion", "star", "rocket", "cat", "bunny")
    }
}
