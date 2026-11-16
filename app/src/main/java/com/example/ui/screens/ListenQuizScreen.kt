package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CoralPink
import com.example.ui.theme.CoralPinkContainer
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.MeadowGreen
import com.example.ui.theme.MeadowGreenContainer
import com.example.ui.theme.SkyTurquoise
import com.example.ui.theme.StarGold
import com.example.ui.theme.StarGoldContainer
import com.example.ui.theme.SunnyOrange
import com.example.ui.viewmodel.ListenQuizUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenQuizScreen(
    state: ListenQuizUiState,
    onBack: () -> Unit,
    onSpeak: (String, Boolean) -> Unit,
    onSelectOption: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onPlayAgain: () -> Unit
) {
    val activeQuestion = state.questions.getOrNull(state.currentIndex)

    LaunchedEffect(state.currentIndex, state.isFinished, activeQuestion?.targetWord?.id) {
        if (!state.isFinished && activeQuestion != null) {
            onSpeak(activeQuestion.targetWord.englishWord, false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "لعبة الاستماع والاختيار",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "رجوع")
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 620.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.isFinished || activeQuestion == null) {
                    QuizCelebrationCard(
                        score = state.score,
                        total = state.questions.size,
                        starsEarned = state.starsEarned,
                        onPlayAgain = onPlayAgain,
                        onBack = onBack
                    )
                } else {
                    // Progress & streak header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "السؤال ${state.currentIndex + 1} من ${state.questions.size}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (state.streak >= 2) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFFFFE6D6)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Rounded.LocalFireDepartment,
                                            contentDescription = null,
                                            tint = SunnyOrange,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "سلسلة ${state.streak}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = SunnyOrange
                                        )
                                    }
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = StarGoldContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFD98A00),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "+${state.starsEarned}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FredokaFontFamily),
                                        color = Color(0xFF5A3B00)
                                    )
                                }
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = { (state.currentIndex + 1).toFloat() / state.questions.size.coerceAtLeast(1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                        color = SkyTurquoise,
                        trackColor = SkyTurquoise.copy(alpha = 0.2f)
                    )

                    // Speaker Prompt Card
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "استمع جيداً ثم اختر الإجابة الصحيحة",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = CircleShape,
                                color = SkyTurquoise,
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(CircleShape)
                                    .clickable { onSpeak(activeQuestion.targetWord.englishWord, false) }
                                    .testTag("quiz_speak_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                        contentDescription = "استمع للكلمة",
                                        tint = Color.White,
                                        modifier = Modifier.size(42.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = { onSpeak(activeQuestion.targetWord.englishWord, true) },
                                shape = RoundedCornerShape(50)
                            ) {
                                Icon(Icons.Rounded.SlowMotionVideo, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نطق بطيء 🐢")
                            }
                        }
                    }

                    // 2x2 Options Grid
                    val answered = state.selectedOptionId != null
                    val rows = activeQuestion.options.chunked(2)
                    rows.forEach { rowOptions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowOptions.forEach { option ->
                                val isSelected = state.selectedOptionId == option.id
                                val isTarget = option.id == activeQuestion.targetWord.id
                                val cardBg = when {
                                    !answered -> Color.White
                                    isTarget -> MeadowGreenContainer
                                    isSelected && !isTarget -> CoralPinkContainer
                                    else -> Color.White.copy(alpha = 0.7f)
                                }
                                val borderColor = when {
                                    !answered -> MaterialTheme.colorScheme.outline
                                    isTarget -> MeadowGreen
                                    isSelected && !isTarget -> CoralPink
                                    else -> MaterialTheme.colorScheme.outline
                                }

                                Card(
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(24.dp))
                                        .border(2.dp, borderColor, RoundedCornerShape(24.dp))
                                        .clickable(enabled = !answered) {
                                            onSelectOption(option.id)
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (option.swatchColor != null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                                    .background(option.swatchColor)
                                            )
                                        } else {
                                            Text(text = option.emoji, fontSize = 42.sp)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = option.arabicMeaning,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                        if (answered) {
                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                Text(
                                                    text = option.englishWord,
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        fontFamily = FredokaFontFamily
                                                    ),
                                                    color = if (isTarget) MeadowGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (answered) {
                        val isCorrect = state.selectedOptionId == activeQuestion.targetWord.id
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isCorrect) MeadowGreenContainer else CoralPinkContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isCorrect) {
                                    "رائع جداً! الإجابة صحيحة 🌟"
                                } else {
                                    "محاولة جميلة! الكلمة الصحيحة هي ${activeQuestion.targetWord.englishWord} (${activeQuestion.targetWord.arabicMeaning})"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isCorrect) Color(0xFF0A3D1F) else Color(0xFF59001B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Button(
                            onClick = onNextQuestion,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SunnyOrange),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("quiz_next_button")
                        ) {
                            Text(
                                text = if (state.currentIndex == state.questions.lastIndex) "عرض النتيجة 🏆" else "السؤال التالي",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizCelebrationCard(
    score: Int,
    total: Int,
    starsEarned: Int,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_trophy_stars),
                contentDescription = stringResource(R.string.trophy_content_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(22.dp))
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "أحسنت يا بطل! 🎉",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "أجبت على $score من أصل $total بشكل صحيح",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = StarGoldContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color(0xFFD98A00),
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "+$starsEarned نجمة",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF5A3B00)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onPlayAgain,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SunnyOrange),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("العب مجدداً")
                }
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text("الرئيسية")
                }
            }
        }
    }
}
