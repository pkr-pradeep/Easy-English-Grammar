package com.grammaradventure.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grammaradventure.app.di.AppContainer
import com.grammaradventure.app.ui.screens.assessment.AssessmentResultScreen
import com.grammaradventure.app.ui.screens.assessment.AssessmentResultViewModel
import com.grammaradventure.app.ui.screens.assessment.AssessmentScreen
import com.grammaradventure.app.ui.screens.assessment.AssessmentViewModel
import com.grammaradventure.app.ui.screens.home.HomeDashboardScreen
import com.grammaradventure.app.ui.screens.home.HomeViewModel
import com.grammaradventure.app.ui.screens.lesson.LessonEngineViewModel
import com.grammaradventure.app.ui.screens.lesson.LessonScreen
import com.grammaradventure.app.ui.screens.notebook.GrammarNotebookScreen
import com.grammaradventure.app.ui.screens.onboarding.OnboardingScreen
import com.grammaradventure.app.ui.screens.onboarding.OnboardingViewModel
import com.grammaradventure.app.ui.screens.onboarding.ProfileSetupScreen
import com.grammaradventure.app.ui.screens.onboarding.SplashScreen
import com.grammaradventure.app.ui.screens.progress.ProgressAndSettingsViewModel
import com.grammaradventure.app.ui.screens.progress.ProgressDashboardScreen
import com.grammaradventure.app.ui.screens.progress.RevisionCenterScreen
import com.grammaradventure.app.ui.screens.progress.SettingsScreen
import com.grammaradventure.app.ui.screens.worlds.ChapterListScreen
import com.grammaradventure.app.ui.screens.worlds.ChapterOverviewScreen
import com.grammaradventure.app.ui.screens.worlds.CompositionSectionScreen
import com.grammaradventure.app.ui.screens.worlds.CurriculumViewModel
import com.grammaradventure.app.ui.screens.worlds.WorldsOverviewScreen

private data class BottomNavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomTabs = listOf(
    BottomNavTab(AppRoute.Home.route, "Home", Icons.Rounded.Home),
    BottomNavTab(AppRoute.Worlds.route, "Worlds", Icons.Rounded.Explore),
    BottomNavTab(AppRoute.Revision.route, "Revision", Icons.Rounded.HistoryEdu),
    BottomNavTab(AppRoute.Progress.route, "Progress", Icons.Rounded.AutoGraph)
)

@Composable
fun AppNavHost(appContainer: AppContainer) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomTabs.map { it.route }

    val onboardingVm: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.Factory(
            appContainer.preferencesManager,
            appContainer.progressRepository
        )
    )
    val homeVm: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            appContainer.curriculumRepository,
            appContainer.progressRepository
        )
    )
    val curriculumVm: CurriculumViewModel = viewModel(
        factory = CurriculumViewModel.Factory(
            appContainer.curriculumRepository,
            appContainer.progressRepository,
            appContainer.preferencesManager,
            appContainer.prerequisiteChecker
        )
    )
    val progressVm: ProgressAndSettingsViewModel = viewModel(
        factory = ProgressAndSettingsViewModel.Factory(
            appContainer.progressRepository,
            appContainer.preferencesManager
        )
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        val selected = backStackEntry?.destination?.hierarchy?.any {
                            it.route == tab.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoute.Splash.route) {
                val state by onboardingVm.uiState.collectAsState()
                SplashScreen(
                    uiState = state,
                    onNavigateToOnboarding = {
                        navController.navigate(AppRoute.Onboarding.route) {
                            popUpTo(AppRoute.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(AppRoute.Home.route) {
                            popUpTo(AppRoute.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoute.Onboarding.route) {
                OnboardingScreen(
                    onFinished = {
                        navController.navigate(AppRoute.ProfileSetup.route) {
                            popUpTo(AppRoute.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoute.ProfileSetup.route) {
                val state by onboardingVm.uiState.collectAsState()
                ProfileSetupScreen(
                    initialName = state.existingName,
                    initialClass = state.existingClass,
                    initialBoard = state.existingBoard,
                    onSaveAndContinue = { name, selectedClass, board ->
                        onboardingVm.completeProfileSetup(name, selectedClass, board) {
                            navController.navigate(AppRoute.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(AppRoute.Home.route) {
                val state by homeVm.uiState.collectAsState()
                HomeDashboardScreen(
                    uiState = state,
                    onOpenWorld = { worldId ->
                        navController.navigate(AppRoute.ChapterList.create(worldId))
                    },
                    onOpenModulePreview = { moduleId ->
                        navController.navigate(AppRoute.ChapterOverview.create(moduleId))
                    },
                    onOpenRevision = {
                        navController.navigate(AppRoute.Revision.route)
                    },
                    onOpenComposition = {
                        navController.navigate(AppRoute.Composition.route)
                    },
                    onOpenSettings = {
                        navController.navigate(AppRoute.Settings.route)
                    }
                )
            }

            composable(AppRoute.Worlds.route) {
                val state by curriculumVm.uiState.collectAsState()
                WorldsOverviewScreen(
                    uiState = state,
                    onOpenWorld = { worldId ->
                        navController.navigate(AppRoute.ChapterList.create(worldId))
                    }
                )
            }

            composable(
                route = AppRoute.ChapterList.route,
                arguments = listOf(navArgument("worldId") { type = NavType.StringType })
            ) { entry ->
                val worldId = entry.arguments?.getString("worldId").orEmpty()
                val state by curriculumVm.uiState.collectAsState()
                ChapterListScreen(
                    worldId = worldId,
                    uiState = state,
                    prerequisiteChecker = curriculumVm.prerequisiteChecker,
                    onToggleSyllabusFilter = curriculumVm::setFilterBySyllabusOnly,
                    onSelectChapter = { moduleId ->
                        navController.navigate(AppRoute.ChapterOverview.create(moduleId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoute.ChapterOverview.route,
                arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
            ) { entry ->
                val moduleId = entry.arguments?.getString("moduleId").orEmpty()
                val state by curriculumVm.uiState.collectAsState()
                ChapterOverviewScreen(
                    moduleId = moduleId,
                    uiState = state,
                    prerequisiteChecker = curriculumVm.prerequisiteChecker,
                    hasLessonContent = appContainer.lessonRepository.hasLessonAsset(moduleId),
                    onStartLesson = { id ->
                        navController.navigate(AppRoute.Lesson.create(id))
                    },
                    onOpenNotebook = { id ->
                        navController.navigate(AppRoute.GrammarNotebook.create(id))
                    },
                    onStartAssessment = { id ->
                        navController.navigate(AppRoute.Assessment.create(id))
                    },
                    onOpenPrerequisite = { prereqId ->
                        navController.navigate(AppRoute.ChapterOverview.create(prereqId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoute.Lesson.route,
                arguments = listOf(
                    navArgument("moduleId") { type = NavType.StringType },
                    navArgument("stepId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entry ->
                val moduleId = entry.arguments?.getString("moduleId").orEmpty()
                val stepId = entry.arguments?.getString("stepId")
                val lessonVm: LessonEngineViewModel = viewModel(
                    key = "lesson_${moduleId}_${stepId.orEmpty()}",
                    factory = LessonEngineViewModel.Factory(
                        moduleId = moduleId,
                        initialStepId = stepId,
                        lessonRepository = appContainer.lessonRepository,
                        progressRepository = appContainer.progressRepository,
                        checkpointEvaluator = appContainer.checkpointEvaluator
                    )
                )
                val lessonState by lessonVm.uiState.collectAsState()
                LessonScreen(
                    uiState = lessonState,
                    onSelectOption = lessonVm::selectOption,
                    onUpdateTypedAnswer = lessonVm::updateTypedAnswer,
                    onToggleWordToken = lessonVm::toggleWordToken,
                    onClearTokens = lessonVm::clearArrangedTokens,
                    onToggleHint = lessonVm::toggleHint,
                    onSubmitCheckpoint = lessonVm::submitCheckpointAnswer,
                    onRetryCheckpoint = lessonVm::retryCheckpoint,
                    onPreviousStep = lessonVm::goToPreviousStep,
                    onNextStep = lessonVm::advanceToNextStep,
                    onOpenNotebook = {
                        navController.navigate(AppRoute.GrammarNotebook.create(moduleId))
                    },
                    onStartAssessment = {
                        navController.navigate(AppRoute.Assessment.create(moduleId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoute.GrammarNotebook.route,
                arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
            ) { entry ->
                val moduleId = entry.arguments?.getString("moduleId").orEmpty()
                GrammarNotebookScreen(
                    moduleId = moduleId,
                    lessonRepository = appContainer.lessonRepository,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoute.Assessment.route,
                arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
            ) { entry ->
                val moduleId = entry.arguments?.getString("moduleId").orEmpty()
                val assessmentVm: AssessmentViewModel = viewModel(
                    key = "assessment_$moduleId",
                    factory = AssessmentViewModel.Factory(
                        moduleId = moduleId,
                        lessonRepository = appContainer.lessonRepository,
                        curriculumRepository = appContainer.curriculumRepository,
                        progressRepository = appContainer.progressRepository,
                        preferencesManager = appContainer.preferencesManager,
                        assessmentScorer = appContainer.assessmentScorer
                    )
                )
                val assessmentState by assessmentVm.uiState.collectAsState()
                AssessmentScreen(
                    uiState = assessmentState,
                    onSelectOption = assessmentVm::selectOption,
                    onGoToQuestion = assessmentVm::goToQuestion,
                    onSubmit = {
                        assessmentVm.submitAssessment { attemptId ->
                            navController.navigate(AppRoute.AssessmentResult.create(attemptId)) {
                                popUpTo(AppRoute.Assessment.route) { inclusive = true }
                            }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoute.AssessmentResult.route,
                arguments = listOf(navArgument("attemptId") { type = NavType.LongType })
            ) { entry ->
                val attemptId = entry.arguments?.getLong("attemptId") ?: 0L
                val resultVm: AssessmentResultViewModel = viewModel(
                    key = "result_$attemptId",
                    factory = AssessmentResultViewModel.Factory(
                        attemptId = attemptId,
                        lessonRepository = appContainer.lessonRepository,
                        progressRepository = appContainer.progressRepository
                    )
                )
                val resultState by resultVm.uiState.collectAsState()
                AssessmentResultScreen(
                    uiState = resultState,
                    onReviewStepInLesson = { modId, stepRef ->
                        navController.navigate(AppRoute.Lesson.create(modId, stepRef))
                    },
                    onRetryAssessment = { modId ->
                        navController.navigate(AppRoute.Assessment.create(modId)) {
                            popUpTo(AppRoute.AssessmentResult.route) { inclusive = true }
                        }
                    },
                    onOpenRevisionCenter = {
                        navController.navigate(AppRoute.Revision.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(AppRoute.Revision.route) {
                RevisionCenterScreen(
                    viewModel = progressVm,
                    onOpenChapter = { moduleId ->
                        navController.navigate(AppRoute.ChapterOverview.create(moduleId))
                    }
                )
            }

            composable(AppRoute.Progress.route) {
                ProgressDashboardScreen(
                    viewModel = progressVm,
                    onOpenAttemptResult = { attemptId ->
                        navController.navigate(AppRoute.AssessmentResult.create(attemptId))
                    }
                )
            }

            composable(AppRoute.Composition.route) {
                val state by curriculumVm.uiState.collectAsState()
                CompositionSectionScreen(
                    uiState = state,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(AppRoute.Settings.route) {
                SettingsScreen(
                    viewModel = progressVm,
                    onEditProfile = {
                        navController.navigate(AppRoute.ProfileSetup.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
