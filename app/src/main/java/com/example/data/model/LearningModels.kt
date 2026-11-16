package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class ChildProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val childName: String = "بطل الإنجليزية",
    val avatarId: String = "owl",
    val totalStars: Long = 0L,
    val streakDays: Long = 1L,
    val completedQuizzes: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MasteredWord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val wordId: String = "",
    val categoryId: String = "",
    val englishWord: String = "",
    val starsEarned: Long = 5L,
    @ServerTimestamp val masteredAt: Timestamp? = null
)

@IgnoreExtraProperties
data class QuizSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val categoryId: String = "",
    val gameType: String = "listen_quiz",
    val score: Long = 0L,
    val totalQuestions: Long = 5L,
    val starsEarned: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class CurriculumCategory(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val subtitleAr: String,
    val emoji: String,
    val accentColor: Color,
    val containerColor: Color
)

data class LearningWord(
    val id: String,
    val categoryId: String,
    val displayPrimary: String,
    val englishWord: String,
    val arabicMeaning: String,
    val arabicPronunciation: String,
    val emoji: String,
    val exampleSentenceEn: String,
    val exampleSentenceAr: String,
    val swatchColor: Color? = null,
    val countNumber: Int? = null
)
