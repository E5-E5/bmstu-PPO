@file:Suppress("ktlint:standard:no-wildcard-imports")
package com.example.repository

import PostgresDBConnector
import com.example.service.dto.*
import com.example.repository_interface.*
import com.example.logger.Logger
import com.example.model.*
import com.example.*
import java.sql.*

class AssessmentRepo(
    private val logger: Logger,
    private val dbConnector: PostgresDBConnector
): AssessmentStorage {
    val res_error = 3
    val res_successful = 0

    private fun AssessmentFormation(resultSet: ResultSet): Assessment {
        val StudentId = resultSet.getInt("StudentId")
        val SubjectId = resultSet.getInt("SubjectId")
        val TeacherId = resultSet.getInt("TeacherId")
        val Assessment = StudentAssessment.valueOf(resultSet.getString("Assessment"))
        val Date = resultSet.getDate("Date")

        return Assessment(StudentId, SubjectId, TeacherId, Assessment, Date)
    }

    override fun CreateAssessment(request: CreateAssessmentRequest): Int {
        val query = "INSERT INTO diary.assessment (StudentId, SubjectId, TeacherId, Assessment, Date) " +
                "VALUES (?, ?, ?, ?, ?)"

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.StudentId)
                preparedStatement.setInt(2, request.SubjectId)
                preparedStatement.setInt(3, request.TeacherId)
                preparedStatement.setObject(4, request.Assessment.name, Types.OTHER)
                preparedStatement.setDate(5, java.sql.Date.valueOf(request.Date.toLocalDate()))

                preparedStatement.executeUpdate()
                return res_successful
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
            throw CreateAssessmentException()
        }
    }
    override fun DeleteAssessment(request: DeleteAssessmentRequest): Int {
        val query = "DELETE FROM diary.assessment WHERE StudentId = ? AND TeacherId = ? AND SubjectId = ? AND Date = ?"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.StudentId)
                preparedStatement.setInt(2, request.TeacherId)
                preparedStatement.setInt(3, request.SubjectId)
                preparedStatement.setDate(4, java.sql.Date.valueOf(request.Date.toLocalDate()))

                preparedStatement.executeUpdate()
                res_successful
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            throw DeleteAssessmentException()
        }
    }
    override fun GetStudentAssessments(request: GetAssessmentsRequest): get_assessment {
        val query = "SELECT * FROM diary.assessment WHERE StudentId = ? AND SubjectId = ? " +
                "AND Date >= ? AND Date <= ?"
        val res_assessment = mutableListOf<Assessment>()

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.StudentId)
                preparedStatement.setInt(2, request.SubjectId)
                preparedStatement.setDate(3, java.sql.Date.valueOf(request.StartDate.toLocalDate()))
                preparedStatement.setDate(4, java.sql.Date.valueOf(request.FinishDate.toLocalDate()))

                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_assessment.add(AssessmentFormation(resultSet))

                get_assessment(res_assessment, 0)
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error getting all students: ${e.message}")
            get_assessment(listOf(), 3)
        }
    }

    override fun UpdateAssessment(request: UpdateAssessmentRequest): Int {
        val query = "UPDATE diary.assessment SET Assessment = ? WHERE " +
                "StudentId = ? AND SubjectId = ? AND TeacherId = ? AND Date = ?"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setObject(1, request.Assessment.name, Types.OTHER)
                preparedStatement.setInt(2, request.StudentId)
                preparedStatement.setInt(3, request.SubjectId)
                preparedStatement.setInt(4, request.TeacherId)
                preparedStatement.setDate(5, java.sql.Date.valueOf(request.Date.toLocalDate()))

                preparedStatement.executeUpdate()
                res_successful
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error updating student: ${e.message}")
            throw UpdateAssessmentException()
        }
    }
}