package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.Spellcheck
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.curriculum.EnglishCurriculum
import com.example.data.model.LearningWord
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.MeadowGreen
import com.example.ui.theme.MeadowGreenContainer
import com.example.ui.theme.StarGold
import com.example.ui.theme.StarGoldContainer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryExploreScreen(
    categoryId: String,
    masteredWordIds: Set<String>,
    onBack: () -> Unit,
    onSpeak: (String, Boolean) -> Unit,
    onToggleMastered: (LearningWord) -> Unit,
    onStartQuiz: (String) -> Unit,
    onStartSpelling: (String) -> Unit
) {
    val category = remember(categoryId) { EnglishCurriculum.categoryById(categoryId) }
    val words = remember(categoryId) { EnglishCurriculum.wordsForCategory(categoryId) }
    var selectedIndex by remember(categoryId) { mutableIntStateOf(0) }
    val activeWord = words.getOrNull(selectedIndex) ?: words.first()
    val isMastered = activeWord.id in masteredWordIds

    LaunchedEffect(activeWord.id) {
        onSpeak(activeWord.englishWord, false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${category.emoji} ${category.titleAr}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = category.titleEn,
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = FredokaFontFamily),
                            color = category.accentColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("category_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 680.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Spotlight Learning Card
                AnimatedContent(targetState = activeWord, label = "spotlight_word") { word ->
                    SpotlightWordCard(
                        word = word,
                        accentColor = category.accentColor,
                        containerColor = category.containerColor,
                        isMastered = word.id in masteredWordIds,
                        onSpeak = onSpeak,
                        onToggleMastered = { onToggleMastered(word) },
                        onPrev = {
                            if (selectedIndex > 0) selectedIndex--
                        },
                        onNext = {
                            if (selectedIndex < words.lastIndex) selectedIndex++
                        },
                        hasPrev = selectedIndex > 0,
                        hasNext = selectedIndex < words.lastIndex,
                        positionLabel = "${selectedIndex + 1} / ${words.size}"
                    )
                }

                // Quick word selector strip
                Text(
                    text = "اختر كلمة أو حرفاً",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    itemsIndexed(words, key = { _, w -> w.id }) { index, word ->
                        val selected = index == selectedIndex
                        val mastered = word.id in masteredWordIds
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = when {
                                selected -> category.accentColor
                                mastered -> MeadowGreenContainer
                                else -> Color.White
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(18.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = if (selected) category.accentColor else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable { selectedIndex = index }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = word.emoji, fontSize = 20.sp)
                                Text(
                                    text = word.displayPrimary,
                                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = FredokaFontFamily),
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                if (mastered && !selected) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "متقنة",
                                        tint = MeadowGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Category Games Bar
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = category.containerColor.copy(alpha = 0.65f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "اختبر نفسك في ${category.titleAr}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onStartQuiz(categoryId) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = category.accentColor),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("category_quiz_button")
                            ) {
                                Icon(Icons.Rounded.Headphones, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("لعبة الاستماع")
                            }
                            if (EnglishCurriculum.spellingEligibleWords(categoryId).isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { onStartSpelling(categoryId) },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .testTag("category_spelling_button")
                                ) {
                                    Icon(Icons.Rounded.Spellcheck, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تركيب الحروف")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpotlightWordCard(
    word: LearningWord,
    accentColor: Color,
    containerColor: Color,
    isMastered: Boolean,
    onSpeak: (String, Boolean) -> Unit,
    onToggleMastered: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    hasPrev: Boolean,
    hasNext: Boolean,
    positionLabel: String
) {
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(30.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrev, enabled = hasPrev) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "السابق"
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = containerColor
                ) {
                    Text(
                        text = positionLabel,
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FredokaFontFamily),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
                IconButton(onClick = onNext, enabled = hasNext) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "التالي"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Visual centerpiece: color swatch, emoji, or big letter
            if (word.swatchColor != null) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(word.swatchColor)
                        .border(4.dp, Color.White, CircleShape)
                        .border(2.dp, accentColor.copy(alpha = 0.3f), CircleShape)
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = containerColor,
                    modifier = Modifier.size(112.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = word.emoji, fontSize = 54.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // English display forced LTR so letters & words always read left-to-right
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                if (word.displayPrimary != word.englishWord) {
                    Text(
                        text = word.displayPrimary,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = FredokaFontFamily,
                            fontSize = 46.sp
                        ),
                        color = accentColor,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = word.englishWord,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            // Countable dots/stars for numbers
            if (word.countNumber != null) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(word.countNumber) {
                        Text(text = "⭐", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = word.arabicMeaning,
                style = MaterialTheme.typography.headlineMedium,
                color = accentColor,
                textAlign = TextAlign.Center
            )
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = "النطق التقريبي: ${word.arabicPronunciation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onSpeak(word.englishWord, false) },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("speak_word_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("استمع", style = MaterialTheme.typography.labelLarge)
                }
                FilledTonalButton(
                    onClick = { onSpeak(word.englishWord, true) },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("speak_slow_button")
                ) {
                    Icon(Icons.Rounded.SlowMotionVideo, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ببطء 🐢", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Example sentence card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onSpeak(word.exampleSentenceEn, false) }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = "🔊 ${word.exampleSentenceEn}",
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FredokaFontFamily),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = word.exampleSentenceAr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mark mastered toggle
            Button(
                onClick = onToggleMastered,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMastered) MeadowGreen else StarGoldContainer,
                    contentColor = if (isMastered) Color.White else Color(0xFF5A3B00)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("master_word_button")
            ) {
                Icon(
                    imageVector = if (isMastered) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMastered) "تم حفظ الكلمة! ⭐ (اضغط للإلغاء)" else "حفظت الكلمة! (+5 نجوم ⭐)",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
