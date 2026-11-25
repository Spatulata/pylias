package com.example.pylias.data

data class GameConfig(
    val moduleId: String,
    val turnTimeSeconds: Int,
    val extraTimeSeconds: Int
)

data class GameResult(
    val correct: Int,
    val skipped: Int,
    val moduleName: String
) {
    val total: Int get() = correct + skipped
    val percent: Int
        get() = if (total == 0) 0 else ((correct * 100f) / total).toInt()
    val grade: Int get() = GradeCalculator.toGrade(percent)
}
