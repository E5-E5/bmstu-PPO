package com.example.service.dto

import com.example.model.StudentAssessment

import java.sql.*


data class CreateAssessmentRequest(
    var StudentId: Int,
    var SubjectId: Int,
    var TeacherId: Int,
    var Assessment: StudentAssessment,
    var Date: Date
)

data class DeleteAssessmentRequest(
    var StudentId: Int,
    var SubjectId: Int,
    var TeacherId: Int,
    var Date: Date
)

data class GetAssessmentsRequest(
    var StudentId: Int,
    var SubjectId: Int,
    var StartDate: Date,
    var FinishDate: Date
)

data class UpdateAssessmentRequest(
    var StudentId: Int,
    var SubjectId: Int,
    var TeacherId: Int,
    var Assessment: StudentAssessment,
    var Date: Date
)