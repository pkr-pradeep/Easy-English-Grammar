package com.grammaradventure.app.domain.engine

import com.grammaradventure.app.domain.model.GrammarModuleMeta

data class PrerequisiteEvaluation(
    val isSatisfied: Boolean,
    val missingPrerequisites: List<GrammarModuleMeta>,
    val isHardLocked: Boolean
)

class PrerequisiteChecker {
    fun evaluate(
        module: GrammarModuleMeta,
        allModulesById: Map<String, GrammarModuleMeta>,
        completedModuleIds: Set<String>,
        strictMode: Boolean
    ): PrerequisiteEvaluation {
        val missing = module.prerequisiteIds.mapNotNull { prereqId ->
            if (completedModuleIds.contains(prereqId)) null else allModulesById[prereqId]
        }
        val satisfied = missing.isEmpty()
        // Never hard-lock the demonstration chapter so learners/reviewers can validate it directly.
        val hardLocked = strictMode && !satisfied && !module.isDemoAvailable
        return PrerequisiteEvaluation(
            isSatisfied = satisfied,
            missingPrerequisites = missing,
            isHardLocked = hardLocked
        )
    }
}
