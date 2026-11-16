package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.curriculum.EnglishCurriculum
import com.example.ui.theme.BerryPurple
import com.example.ui.theme.BerryPurpleContainer
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.MeadowGreen
import com.example.ui.theme.MeadowGreenContainer
import com.example.ui.theme.SkyTurquoise
import com.example.ui.theme.StarGold
import com.example.ui.theme.StarGoldContainer
import com.example.ui.theme.SunnyOrange
import com.example.ui.theme.SunnyOrangeContainer
import com.example.ui.viewmodel.LearningDashboardData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileRewardsScreen(
    data: LearningDashboardData,
    onSaveProfile: (String, String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember(data.profile.childName) { mutableStateOf(data.profile.childName) }
    var selectedAvatar by remember(data.profile.avatarId) { mutableStateOf(data.profile.avatarId) }

    val avatarOptions = listOf(
        "owl" to "بومة ذكية",
        "lion" to "أسد شجاع",
        "star" to "نجمة لامعة",
        "rocket" to "صاروخ الفضاء",
        "cat" to "قطة لطيفة",
        "bunny" to "أرنب سريع"
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.widthIn(max = 720.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBadgeCard(
                        icon = Icons.Rounded.Star,
                        value = data.profile.totalStars.toString(),
                        label = "مجموع النجوم",
                        accent = Color(0xFFD98A00),
                        container = StarGoldContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatBadgeCard(
                        icon = Icons.Rounded.AutoAwesome,
                        value = data.masteredWordIds.size.toString(),
                        label = "كلمة متقنة",
                        accent = MeadowGreen,
                        container = MeadowGreenContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatBadgeCard(
                        icon = Icons.Rounded.EmojiEvents,
                        value = data.profile.completedQuizzes.toString(),
                        label = "اختبارات منجزة",
                        accent = BerryPurple,
                        container = BerryPurpleContainer,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Edit child profile card
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "ملف البطل الصغير",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { if (it.length <= 40) nameInput = it },
                            label = { Text("اسم الطفل") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("child_name_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "اختر الشخصية المفضلة",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            avatarOptions.forEach { (id, label) ->
                                val isSelected = selectedAvatar == id
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = if (isSelected) SunnyOrangeContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(18.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) SunnyOrange else MaterialTheme.colorScheme.outline,
                                            shape = RoundedCornerShape(18.dp)
                                        )
                                        .clickable { selectedAvatar = id }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = avatarEmojiFor(id), fontSize = 22.sp)
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onSaveProfile(nameInput, selectedAvatar) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SunnyOrange),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("save_profile_button")
                        ) {
                            Icon(Icons.Rounded.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حفظ التغييرات", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                // Recent quiz history header
                Text(
                    text = "سجل الألعاب الأخيرة",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (data.recentSessions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎯", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "لم تلعب أي اختبار بعد — جرب لعبة الاستماع أو تركيب الحروف الآن!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(data.recentSessions, key = { it.id }) { session ->
                val catName = if (session.categoryId == "mixed") {
                    "التحدي الشامل"
                } else {
                    EnglishCurriculum.categoryById(session.categoryId).titleAr
                }
                val gameTitle = if (session.gameType == "spelling_game") "تركيب الحروف" else "لعبة الاستماع"

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (session.gameType == "spelling_game") BerryPurpleContainer else SunnyOrangeContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (session.gameType == "spelling_game") "🧩" else "🎧",
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            Column {
                                Text(text = "$gameTitle • $catName", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "النتيجة: ${session.score} / ${session.totalQuestions}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SkyTurquoise
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = StarGoldContainer
                        ) {
                            Text(
                                text = "+${session.starsEarned} ⭐",
                                style = MaterialTheme.typography.labelMedium.copy(fontFamily = FredokaFontFamily),
                                color = Color(0xFF5A3B00),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onSignOut,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sign_out_button")
            ) {
                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.sign_out), style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StatBadgeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    accent: Color,
    container: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FredokaFontFamily),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
