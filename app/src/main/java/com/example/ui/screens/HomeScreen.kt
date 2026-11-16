package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Spellcheck
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.curriculum.EnglishCurriculum
import com.example.data.model.CurriculumCategory
import com.example.ui.theme.BerryPurple
import com.example.ui.theme.BerryPurpleContainer
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.SkyTurquoise
import com.example.ui.theme.SkyTurquoiseContainer
import com.example.ui.theme.StarGold
import com.example.ui.theme.StarGoldContainer
import com.example.ui.theme.SunnyOrange
import com.example.ui.viewmodel.LearningDashboardData

@Composable
fun HomeScreen(
    data: LearningDashboardData,
    onCategoryClick: (String) -> Unit,
    onQuickListenQuiz: () -> Unit,
    onQuickSpellingGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalWords = EnglishCurriculum.words.size
    val masteredCount = data.masteredWordIds.size
    val overallProgress by animateFloatAsState(
        targetValue = if (totalWords > 0) masteredCount.toFloat() / totalWords else 0f,
        animationSpec = spring(),
        label = "overall_progress"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 600.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 960.dp)
                .align(Alignment.TopCenter),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeroGreetingCard(
                    childName = data.profile.childName,
                    avatarId = data.profile.avatarId,
                    totalStars = data.profile.totalStars,
                    masteredCount = masteredCount,
                    totalWords = totalWords,
                    overallProgress = overallProgress
                )
            }

            item {
                Text(
                    text = "العب وتدرّب الآن",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickGameCard(
                        title = "اسمع واختر",
                        subtitle = "اختبار النطق",
                        icon = Icons.Rounded.Headphones,
                        accentColor = SkyTurquoise,
                        containerColor = SkyTurquoiseContainer,
                        onClick = onQuickListenQuiz,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_listen_quiz_button")
                    )
                    QuickGameCard(
                        title = "ركّب الكلمة",
                        subtitle = "تحدي الحروف",
                        icon = Icons.Rounded.Spellcheck,
                        accentColor = BerryPurple,
                        containerColor = BerryPurpleContainer,
                        onClick = onQuickSpellingGame,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_spelling_game_button")
                    )
                }
            }

            item {
                Text(
                    text = "عوالم التعلم",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            val categories = EnglishCurriculum.categories
            val columns = if (isWide) 3 else 2
            val rows = categories.chunked(columns)

            items(rows.size) { rowIndex ->
                val rowItems = rows[rowIndex]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { category ->
                        val catWords = EnglishCurriculum.wordsForCategory(category.id).size
                        val catMastered = data.masteredCountByCategory[category.id] ?: 0
                        CategoryWorldCard(
                            category = category,
                            masteredCount = catMastered,
                            totalCount = catWords,
                            onClick = { onCategoryClick(category.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size < columns) {
                        repeat(columns - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HeroGreetingCard(
    childName: String,
    avatarId: String,
    totalStars: Long,
    masteredCount: Int,
    totalWords: Int,
    overallProgress: Float
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = stringResource(R.string.hero_banner_content_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCC182032),
                                Color(0xD91E2942)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier
                                .size(54.dp)
                                .border(2.5.dp, StarGold, CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = avatarEmojiFor(avatarId),
                                    fontSize = 28.sp
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "أهلاً يا $childName!",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "هيّا نتعلم كلمات إنجليزية جديدة",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.88f)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = StarGoldContainer,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "النجوم",
                                tint = Color(0xFFD98A00),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = totalStars.toString(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FredokaFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF5A3B00)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = StarGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "الكلمات المتقنة",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "$masteredCount / $totalWords",
                        style = MaterialTheme.typography.labelLarge.copy(fontFamily = FredokaFontFamily),
                        color = StarGoldContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { overallProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                    color = StarGold,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )
            }
        }
    }
}

@Composable
private fun QuickGameCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = accentColor,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CategoryWorldCard(
    category: CurriculumCategory,
    masteredCount: Int,
    totalCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalCount > 0) masteredCount.toFloat() / totalCount else 0f
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .testTag("category_card_${category.id}")
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(24.dp))
            .border(1.5.dp, category.accentColor.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = category.containerColor,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = category.emoji,
                            fontSize = 28.sp
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = category.containerColor
                ) {
                    Text(
                        text = "$masteredCount/$totalCount",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FredokaFontFamily),
                        color = category.accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = category.titleAr,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = category.titleEn,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.SemiBold
                ),
                color = category.accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50)),
                color = category.accentColor,
                trackColor = category.containerColor
            )
        }
    }
}

fun avatarEmojiFor(avatarId: String): String = when (avatarId) {
    "owl" -> "🦉"
    "lion" -> "🦁"
    "star" -> "⭐"
    "rocket" -> "🚀"
    "cat" -> "🐱"
    "bunny" -> "🐰"
    else -> "🦉"
}
