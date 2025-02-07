package org.example.internal.storage.repository

import PostgresDBConnector
import internal.service.dto.CreateClassRequest
import internal.service.dto.DeleteClassRequest
import internal.service.dto.GetClassByIdRequest
import internal.service.dto.GetClassByNameRequest
import internal.storage.*
import logger.Logger
import model.*
import org.example.`package`.exceptions.*
import java.sql.*
//RETURNING *
class ClassRepo(
    private val logger: Logger,
    private val dbConnector: PostgresDBConnector
): ClassStorage
{
    val res_error = 3
    val res_successful = 0

    private fun ClassFormation(resultSet: ResultSet): Class {
        val ClassId = resultSet.getInt("ClassId")
        val ClassLetter = resultSet.getString("ClassLetter")
        val ClassNumber = ClassNumber.valueOf(resultSet.getString("ClassNumber"))

        return Class(ClassId, ClassLetter, ClassNumber)
    }

    override fun CreateClass(request: CreateClassRequest): Int {
        val query = "INSERT INTO diary.class (ClassLetter, ClassNumber) VALUES (?, ?)"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, request.Letter)
                preparedStatement.setObject(2, request.Number.name, Types.OTHER)

                preparedStatement.executeUpdate()
                res_successful
            } ?: res_error
        } catch (e: Exception) {
            println("Error creating class: ${e.message}")
            res_error
        }
    }

    override fun GetAllClasses() : List<Class> {
        val query = "SELECT * FROM diary.class"


        val res_classes = mutableListOf<Class>()

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_classes.add(ClassFormation(resultSet))

                res_classes
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error getting all students: ${e.message}")
            throw GetClassesException()
        }
    }

    override fun GetClassById(request: GetClassByIdRequest): get_class{
        val query = "SELECT * FROM diary.class WHERE ClassId = ?"

        var res_class: Class? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.ClassId)
                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next())
                        res_class = ClassFormation(resultSet)
                }
            }
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
        }

        if (res_class != null)
            return get_class(res_class, 0)
        return get_class(null, 3)
    }

    override fun GetClassByName(request: GetClassByNameRequest) : get_class {
        val query = "SELECT * FROM diary.class WHERE ClassLetter = ? AND ClassNumber = ?"

        var res_class: Class? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, request.Letter.uppercase())
                preparedStatement.setObject(2, request.Number.name, Types.OTHER)

                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next())
                        res_class = ClassFormation(resultSet)
                }
            }
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
        }

        if (res_class != null)
            return get_class(res_class, 0)
        return get_class(null, 3)
    }

    override fun DeleteClass(request: DeleteClassRequest): Int {
        val query = "DELETE FROM diary.class WHERE ClassId = ?"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.ClassId)
                preparedStatement.executeUpdate()
                res_successful
            } ?: res_error
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            res_error
        }
    }
}