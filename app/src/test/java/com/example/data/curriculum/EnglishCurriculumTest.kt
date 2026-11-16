package com.example.data.curriculum

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnglishCurriculumTest {

    @Test
    fun allCategoriesHaveWordsAndUniqueValidIds() {
        val idRegex = Regex("^[a-zA-Z0-9_\\-]+$")
        val ids = EnglishCurriculum.words.map { it.id }
        assertEquals("Word IDs must be unique", ids.size, ids.toSet().size)
        ids.forEach { id ->
            assertTrue("Invalid ID $id", idRegex.matches(id))
        }
        EnglishCurriculum.categories.forEach { category ->
            val words = EnglishCurriculum.wordsForCategory(category.id)
            assertTrue("Category ${category.id} should not be empty", words.isNotEmpty())
        }
    }

    @Test
    fun spellingEligibleWordsAreValid() {
        val eligible = EnglishCurriculum.spellingEligibleWords("mixed")
        assertTrue("Should have spelling eligible words", eligible.size >= 15)
        eligible.forEach { word ->
            val clean = word.englishWord.uppercase()
            assertTrue("Word $clean length invalid", clean.length in 3..8)
            assertTrue("Word $clean must be A-Z", clean.all { it in 'A'..'Z' })
        }
    }
}
