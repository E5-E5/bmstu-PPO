package org.example.internal.storage.repository

import PostgresDBConnector
import internal.service.dto.*
import internal.storage.*
import logger.Logger
import model.*
import org.example.`package`.exceptions.ConnectBDException
import org.example.`package`.exceptions.GetClassesException
import org.example.`package`.exceptions.GetStudentsException
import java.sql.*

class SubjectRepo(
    private val logger: Logger,
    private val dbConnector: PostgresDBConnector
): SubjectStorage
{
    val res_error = 3
    val res_successful = 0

    private fun SubjectFormation(resultSet: ResultSet): Subject {
        val SubjectId = resultSet.getInt("SubjectId")
        val Name = resultSet.getString("Name")
        val CountTeachers = Count.valueOf(resultSet.getString("CountTeachers"))

        return Subject(SubjectId, Name, CountTeachers)
    }

    private fun GetSubjectFromBD(query: String): get_subject {
        var subject: Subject? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                connection.createStatement().executeQuery(query)?.use { resultSet ->
                    if (resultSet.next())
                        subject = SubjectFormation(resultSet)
                }
            }
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
        }

        if (subject != null)
            return get_subject(subject, 0)
        return get_subject(null, 3)
    }

    override fun CreateSubject(request: CreateSubjectRequest): Int {
        val query = "INSERT INTO diary.subject (Name, CountTeachers) VALUES (?, ?)"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, request.Name)
                preparedStatement.setObject(2, request.CountTeachers.name, Types.OTHER)

                preparedStatement.executeUpdate()
                res_successful
            } ?: res_error
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
            res_error
        }
    }

    override fun GetSubjects(): List<Subject> {
        val query = "SELECT * FROM diary.subject"


        val res_subjects = mutableListOf<Subject>()

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_subjects.add(SubjectFormation(resultSet))

                res_subjects
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error getting all students: ${e.message}")
            throw GetClassesException()
        }
    }

    override fun GetSubjectById(request: GetSubjectRequestById): get_subject {
        val query = "SELECT * FROM diary.subject WHERE SubjectId = ?"

        var subject: Subject? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.SubjectId)

                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next())
                        subject = SubjectFormation(resultSet)
                }
            }
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
        }

        if (subject != null)
            return get_subject(subject, 0)
        return get_subject(null, 3)
    }

    override fun GetSubjectByName(request: GetSubjectRequestByName): get_subject {
        val query = "SELECT * FROM diary.subject WHERE Name = ?"

        var subject: Subject? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, request.Name)

                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next())
                        subject = SubjectFormation(resultSet)
                }
            }
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
        }

        if (subject != null)
            return get_subject(subject, 0)
        return get_subject(null, 3)
    }

    override fun GetSubjectsForClass(request: GetSubjectsForClassRequest): get_subjects {
        val query = "SELECT * FROM diary.subject S " +
                "JOIN diary.schedule C ON C.SubjectId = S.SubjectId " +
                "WHERE ClassId = ?"


        val res_subjects = mutableSetOf<Subject>()

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.ClassId)
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_subjects.add(SubjectFormation(resultSet))

                get_subjects(res_subjects.toList(), 0)
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error getting all students: ${e.message}")
            throw GetStudentsException()
        }
    }

    override fun DeleteSubject(request: DeleteSubjectRequest): Int {
        val query = "DELETE FROM diary.subject WHERE SubjectId = ?"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.SubjectId)

                preparedStatement.executeUpdate()
                res_successful
            } ?: res_error
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            res_error
        }
    }
}