package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Games
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Games
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.audio.EnglishSpeechHelper
import com.example.data.repository.LearningRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOutUser
import com.example.ui.screens.CategoryExploreScreen
import com.example.ui.screens.GamesHubScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ListenQuizScreen
import com.example.ui.screens.ProfileRewardsScreen
import com.example.ui.screens.SpellingGameScreen
import com.example.ui.theme.EnglishStarsTheme
import com.example.ui.viewmodel.LearningViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val ROUTE_HOME = "home"
const val ROUTE_GAMES = "games"
const val ROUTE_PROFILE = "profile"
const val ROUTE_CATEGORY = "category/{categoryId}"
const val ROUTE_QUIZ = "quiz/{categoryId}"
const val ROUTE_SPELLING = "spelling/{categoryId}"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnglishStarsTheme {
                AppNavigation()
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        val uid = user.uid
        val defaultName = user.displayName?.takeIf { it.isNotBlank() } ?: "بطل الإنجليزية"
        key(uid) {
            AuthenticatedAppShell(
                currentUserId = uid,
                defaultDisplayName = defaultName
            )
        }
    }
}

@Composable
fun AuthenticatedAppShell(
    currentUserId: String,
    defaultDisplayName: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val speechHelper = remember(context) { EnglishSpeechHelper(context) }
    DisposableEffect(speechHelper) {
        onDispose { speechHelper.shutdown() }
    }

    val viewModel: LearningViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                LearningViewModel(
                    repository = LearningRepository(db, Firebase.auth),
                    currentUserId = currentUserId,
                    defaultDisplayName = defaultDisplayName
                )
            }
        }
    )

    val dashboardState by viewModel.dashboardState.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()
    val quizState by viewModel.quizState.collectAsStateWithLifecycle()
    val spellingState by viewModel.spellingState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(actionMessage) {
        val msg = actionMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearActionMessage()
        }
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val topLevelRoutes = setOf(ROUTE_HOME, ROUTE_GAMES, ROUTE_PROFILE)
    val showTopLevelNav = currentRoute in topLevelRoutes

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 600.dp

            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    if (showTopLevelNav && !useRail) {
                        NavigationBar(
                            windowInsets = WindowInsets.navigationBars,
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            TopLevelNavItems(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                isRail = false
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (showTopLevelNav && useRail) {
                        NavigationRail(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            TopLevelNavItems(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                isRail = true
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                        when (val state = dashboardState) {
                            is UiState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is UiState.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "حدث خطأ أثناء تحميل بياناتك",
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = state.message,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = {
                                                signOutUser(credentialManager, scope)
                                            }
                                        ) {
                                            Text("إعادة تسجيل الدخول")
                                        }
                                    }
                                }
                            }
                            is UiState.Success -> {
                                val data = state.data
                                NavHost(
                                    navController = navController,
                                    startDestination = ROUTE_HOME
                                ) {
                                    composable(ROUTE_HOME) {
                                        HomeScreen(
                                            data = data,
                                            onCategoryClick = { categoryId ->
                                                navController.navigate("category/$categoryId")
                                            },
                                            onQuickListenQuiz = {
                                                viewModel.startListenQuiz("mixed")
                                                navController.navigate("quiz/mixed")
                                            },
                                            onQuickSpellingGame = {
                                                viewModel.startSpellingGame("mixed")
                                                navController.navigate("spelling/mixed")
                                            }
                                        )
                                    }

                                    composable(ROUTE_GAMES) {
                                        GamesHubScreen(
                                            onStartListenQuiz = { catId ->
                                                viewModel.startListenQuiz(catId)
                                                navController.navigate("quiz/$catId")
                                            },
                                            onStartSpellingGame = { catId ->
                                                viewModel.startSpellingGame(catId)
                                                navController.navigate("spelling/$catId")
                                            }
                                        )
                                    }

                                    composable(ROUTE_PROFILE) {
                                        ProfileRewardsScreen(
                                            data = data,
                                            onSaveProfile = { name, avatar ->
                                                viewModel.updateProfile(name, avatar)
                                            },
                                            onSignOut = {
                                                signOutUser(credentialManager, scope)
                                            }
                                        )
                                    }

                                    composable(
                                        route = ROUTE_CATEGORY,
                                        arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                                    ) { entry ->
                                        val categoryId = entry.arguments?.getString("categoryId") ?: "alphabet"
                                        CategoryExploreScreen(
                                            categoryId = categoryId,
                                            masteredWordIds = data.masteredWordIds,
                                            onBack = { navController.popBackStack() },
                                            onSpeak = { text, slow -> speechHelper.speakEnglish(text, slow) },
                                            onToggleMastered = { word -> viewModel.toggleMastered(word) },
                                            onStartQuiz = { catId ->
                                                viewModel.startListenQuiz(catId)
                                                navController.navigate("quiz/$catId")
                                            },
                                            onStartSpelling = { catId ->
                                                viewModel.startSpellingGame(catId)
                                                navController.navigate("spelling/$catId")
                                            }
                                        )
                                    }

                                    composable(
                                        route = ROUTE_QUIZ,
                                        arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                                    ) { entry ->
                                        val categoryId = entry.arguments?.getString("categoryId") ?: "mixed"
                                        ListenQuizScreen(
                                            state = quizState,
                                            onBack = { navController.popBackStack() },
                                            onSpeak = { text, slow -> speechHelper.speakEnglish(text, slow) },
                                            onSelectOption = { optId -> viewModel.selectQuizOption(optId) },
                                            onNextQuestion = { viewModel.advanceQuizQuestion() },
                                            onPlayAgain = { viewModel.startListenQuiz(categoryId) }
                                        )
                                    }

                                    composable(
                                        route = ROUTE_SPELLING,
                                        arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                                    ) { entry ->
                                        val categoryId = entry.arguments?.getString("categoryId") ?: "mixed"
                                        SpellingGameScreen(
                                            state = spellingState,
                                            onBack = { navController.popBackStack() },
                                            onSpeak = { text, slow -> speechHelper.speakEnglish(text, slow) },
                                            onTapTile = { tileId -> viewModel.tapSpellingTile(tileId) },
                                            onUndoTile = { viewModel.undoSpellingTile() },
                                            onRetryWord = { viewModel.retryCurrentSpellingWord() },
                                            onNextWord = { viewModel.advanceSpellingWord() },
                                            onPlayAgain = { viewModel.startSpellingGame(categoryId) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TopLevelNavItems(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isRail: Boolean
) {
    // Only used for NavigationBar RowScope
    val learnSelected = currentRoute == ROUTE_HOME
    NavigationBarItem(
        selected = learnSelected,
        onClick = { onNavigate(ROUTE_HOME) },
        icon = {
            Icon(
                imageVector = if (learnSelected) Icons.Rounded.Home else Icons.Outlined.Home,
                contentDescription = stringResource(R.string.nav_learn)
            )
        },
        label = { Text(stringResource(R.string.nav_learn)) }
    )
    val gamesSelected = currentRoute == ROUTE_GAMES
    NavigationBarItem(
        selected = gamesSelected,
        onClick = { onNavigate(ROUTE_GAMES) },
        icon = {
            Icon(
                imageVector = if (gamesSelected) Icons.Rounded.Games else Icons.Outlined.Games,
                contentDescription = stringResource(R.string.nav_games)
            )
        },
        label = { Text(stringResource(R.string.nav_games)) }
    )
    val profileSelected = currentRoute == ROUTE_PROFILE
    NavigationBarItem(
        selected = profileSelected,
        onClick = { onNavigate(ROUTE_PROFILE) },
        icon = {
            Icon(
                imageVector = if (profileSelected) Icons.Rounded.Person else Icons.Outlined.Person,
                contentDescription = stringResource(R.string.nav_profile)
            )
        },
        label = { Text(stringResource(R.string.nav_profile)) }
    )
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.TopLevelNavItems(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isRail: Boolean
) {
    val learnSelected = currentRoute == ROUTE_HOME
    NavigationRailItem(
        selected = learnSelected,
        onClick = { onNavigate(ROUTE_HOME) },
        icon = {
            Icon(
                imageVector = if (learnSelected) Icons.Rounded.Home else Icons.Outlined.Home,
                contentDescription = stringResource(R.string.nav_learn)
            )
        },
        label = { Text(stringResource(R.string.nav_learn)) }
    )
    val gamesSelected = currentRoute == ROUTE_GAMES
    NavigationRailItem(
        selected = gamesSelected,
        onClick = { onNavigate(ROUTE_GAMES) },
        icon = {
            Icon(
                imageVector = if (gamesSelected) Icons.Rounded.Games else Icons.Outlined.Games,
                contentDescription = stringResource(R.string.nav_games)
            )
        },
        label = { Text(stringResource(R.string.nav_games)) }
    )
    val profileSelected = currentRoute == ROUTE_PROFILE
    NavigationRailItem(
        selected = profileSelected,
        onClick = { onNavigate(ROUTE_PROFILE) },
        icon = {
            Icon(
                imageVector = if (profileSelected) Icons.Rounded.Person else Icons.Outlined.Person,
                contentDescription = stringResource(R.string.nav_profile)
            )
        },
        label = { Text(stringResource(R.string.nav_profile)) }
    )
}
