package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Refresh
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BerryPurple
import com.example.ui.theme.BerryPurpleContainer
import com.example.ui.theme.CoralPink
import com.example.ui.theme.CoralPinkContainer
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.MeadowGreen
import com.example.ui.theme.MeadowGreenContainer
import com.example.ui.theme.StarGoldContainer
import com.example.ui.theme.SunnyOrange
import com.example.ui.viewmodel.SpellingUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SpellingGameScreen(
    state: SpellingUiState,
    onBack: () -> Unit,
    onSpeak: (String, Boolean) -> Unit,
    onTapTile: (Int) -> Unit,
    onUndoTile: () -> Unit,
    onRetryWord: () -> Unit,
    onNextWord: () -> Unit,
    onPlayAgain: () -> Unit
) {
    val activeWord = state.words.getOrNull(state.currentIndex)

    LaunchedEffect(state.currentIndex, state.isFinished, activeWord?.id) {
        if (!state.isFinished && activeWord != null) {
            onSpeak(activeWord.englishWord, false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "لعبة تركيب الحروف",
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
                if (state.isFinished || activeWord == null) {
                    QuizCelebrationCard(
                        score = state.score,
                        total = state.words.size,
                        starsEarned = state.starsEarned,
                        onPlayAgain = onPlayAgain,
                        onBack = onBack
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الكلمة ${state.currentIndex + 1} من ${state.words.size}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = StarGoldContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
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

                    LinearProgressIndicator(
                        progress = { (state.currentIndex + 1).toFloat() / state.words.size.coerceAtLeast(1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                        color = BerryPurple,
                        trackColor = BerryPurpleContainer
                    )

                    // Prompt card
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
                            if (activeWord.swatchColor != null) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(activeWord.swatchColor)
                                )
                            } else {
                                Text(text = activeWord.emoji, fontSize = 54.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = activeWord.arabicMeaning,
                                style = MaterialTheme.typography.headlineMedium,
                                color = BerryPurple,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            FilledTonalButton(
                                onClick = { onSpeak(activeWord.englishWord, true) },
                                shape = RoundedCornerShape(50)
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("اسمع الكلمة")
                            }
                        }
                    }

                    // Answer slots (strictly LTR!)
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(state.targetCleanWord.length) { slotIndex ->
                                val tileId = state.chosenTileIds.getOrNull(slotIndex)
                                val letter = tileId?.let { id ->
                                    state.availableTiles.firstOrNull { it.id == id }?.letter
                                }
                                val slotBg = when {
                                    state.isChecked && state.isCorrect -> MeadowGreenContainer
                                    state.isChecked && !state.isCorrect -> CoralPinkContainer
                                    letter != null -> BerryPurpleContainer
                                    else -> Color.White
                                }
                                val slotBorder = when {
                                    state.isChecked && state.isCorrect -> MeadowGreen
                                    state.isChecked && !state.isCorrect -> CoralPink
                                    letter != null -> BerryPurple
                                    else -> MaterialTheme.colorScheme.outline
                                }

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(slotBg)
                                        .border(2.dp, slotBorder, RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letter?.toString() ?: "",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontFamily = FredokaFontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (state.isChecked && state.isCorrect) MeadowGreen else BerryPurple
                                    )
                                }
                            }
                        }
                    }

                    // Letter tiles bank (LTR)
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            state.availableTiles.forEach { tile ->
                                val isUsed = tile.id in state.chosenTileIds
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isUsed) MaterialTheme.colorScheme.surfaceVariant else SunnyOrange,
                                    shadowElevation = if (isUsed) 0.dp else 3.dp,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable(enabled = !isUsed && !state.isChecked) {
                                            onTapTile(tile.id)
                                        }
                                        .testTag("spelling_tile_${tile.id}")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = tile.letter.toString(),
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontFamily = FredokaFontFamily,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = if (isUsed) Color.Transparent else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Controls / Feedback
                    if (!state.isChecked) {
                        OutlinedButton(
                            onClick = onUndoTile,
                            enabled = state.chosenTileIds.isNotEmpty(),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Backspace, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مسح آخر حرف")
                        }
                    } else if (state.isCorrect) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MeadowGreenContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ممتاز! تهجئة صحيحة: ${state.targetCleanWord} 🌟",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF0A3D1F),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                        Button(
                            onClick = onNextWord,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MeadowGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("spelling_next_button")
                        ) {
                            Text(
                                text = if (state.currentIndex == state.words.lastIndex) "إنهاء اللعبة 🏆" else "الكلمة التالية",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CoralPinkContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ليست مطابقة تماماً! الكلمة الصحيحة هي ${state.targetCleanWord}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF59001B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onRetryWord,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Icon(Icons.Rounded.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("حاول مجدداً")
                            }
                            Button(
                                onClick = onNextWord,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SunnyOrange),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Text("الكلمة التالية")
                            }
                        }
                    }
                }
            }
        }
    }
}
