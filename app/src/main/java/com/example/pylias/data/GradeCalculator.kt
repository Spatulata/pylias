package com.example.pylias.data

object GradeCalculator {
    fun toGrade(percent: Int): Int = when {
        percent >= 90 -> 5
        percent >= 70 -> 4
        percent >= 50 -> 3
        percent >= 30 -> 2
        else -> 1
    }
}
