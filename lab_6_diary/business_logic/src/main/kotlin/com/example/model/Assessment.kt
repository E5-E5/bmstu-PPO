package com.example.model

import java.sql.*

enum class StudentAssessment {
    ONE, TWO, THREE, FOUR, FIVE
}

data class Assessment (
    var StudentId: Int,
    var SubjectId: Int,
    var TeacherId: Int,
    var Assessment: StudentAssessment,
    var Date: Date
)