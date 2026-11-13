package com.grammaradventure.app.ui.navigation

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")
    data object Onboarding : AppRoute("onboarding")
    data object ProfileSetup : AppRoute("profile_setup")
    data object Home : AppRoute("home")
    data object Worlds : AppRoute("worlds")
    data object Revision : AppRoute("revision")
    data object Progress : AppRoute("progress")
    data object Composition : AppRoute("composition")
    data object Settings : AppRoute("settings")

    data object ChapterList : AppRoute("chapters/{worldId}") {
        fun create(worldId: String) = "chapters/$worldId"
    }

    data object ChapterOverview : AppRoute("chapter_overview/{moduleId}") {
        fun create(moduleId: String) = "chapter_overview/$moduleId"
    }

    data object Lesson : AppRoute("lesson/{moduleId}?stepId={stepId}") {
        fun create(moduleId: String, stepId: String? = null): String =
            if (stepId.isNullOrBlank()) "lesson/$moduleId" else "lesson/$moduleId?stepId=$stepId"
    }

    data object GrammarNotebook : AppRoute("notebook/{moduleId}") {
        fun create(moduleId: String) = "notebook/$moduleId"
    }

    data object Assessment : AppRoute("assessment/{moduleId}") {
        fun create(moduleId: String) = "assessment/$moduleId"
    }

    data object AssessmentResult : AppRoute("assessment_result/{attemptId}") {
        fun create(attemptId: Long) = "assessment_result/$attemptId"
    }
}
