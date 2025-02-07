@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.repository

import com.example.*
import PostgresDBConnector
import com.example.logger.Logger
import com.example.model.*
import com.example.model.User
import com.example.service.dto.CreateAdminWithRoleRequest
import com.example.service.dto.DeleteAdminRequest
import com.example.service.dto.SingInAdminRequest
import com.example.*
import com.example.repository_interface.AdminStorage
import com.example.repository_interface.get_admin

import java.sql.*

class AdminRepo(
    private val logger: Logger,
    private val dbConnector: PostgresDBConnector
): AdminStorage
{
    val res_error = 3
    val res_successful = 0

    private fun AdminFormation(resultSet: ResultSet): Admin {
        val AdminId = resultSet.getInt("UserId")
        val FirstName = resultSet.getString("FirstName")
        val LastName = resultSet.getString("LastName")
        val Patronymic = resultSet.getString("Patronymic")
        val Birthday = resultSet.getDate("Birthday")
        val Password = resultSet.getString("Password")
        val Identifier = resultSet.getString("Identifier")
        val Phone = resultSet.getString("Phone")
        val Gender = Gender.valueOf(resultSet.getString("Gender"))

        return Admin(AdminId,  FirstName, LastName, Patronymic, Birthday,
            Password, Identifier, Phone, Gender)
    }

    private fun UserFormation(resultSet: ResultSet): User {
        val admin = AdminFormation(resultSet)
        val RoleId = resultSet.getInt("RoleId")

        return User(admin.AdmintId,  admin.FirstName, admin.LastName, admin.Patronymic, admin.Birthday,
            admin.Password, admin.Identifier, admin.Phone, admin.Gender, RoleId)
    }

    override fun CreateAdmin(request: CreateAdminWithRoleRequest): Int {
        val queryAddUser = "INSERT INTO diary.user " +
                "(FirstName, LastName, Patronymic, Birthday, Password, Identifier, Phone, Gender) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"

        val queryAddRole = "INSERT INTO diary.UserRole " +
                "(UserID, RoleId) " +
                "VALUES (?, ?)"

        return try {
            dbConnector.getConnection()?.use { connection ->
                connection.autoCommit = false // Отключаем автоматическую фиксацию

                val preparedStatementAddUser = connection.prepareStatement(queryAddUser, Statement.RETURN_GENERATED_KEYS)
                preparedStatementAddUser.setString(1, request.FirstName)
                preparedStatementAddUser.setString(2, request.LastName)
                preparedStatementAddUser.setString(3, request.Patronymic)
                preparedStatementAddUser.setDate(4, java.sql.Date.valueOf(request.Birthday.toLocalDate()))
                preparedStatementAddUser.setString(5, request.Password)
                preparedStatementAddUser.setString(6, request.Identifier)
                preparedStatementAddUser.setString(7, request.Phone)
                preparedStatementAddUser.setObject(8, request.Gender.name, Types.OTHER)

                preparedStatementAddUser.executeUpdate()
                val generatedKeys = preparedStatementAddUser.generatedKeys
                var adminId: Int? = null
                if (generatedKeys.next()) {
                    adminId = generatedKeys.getInt(1)
                }

                if (adminId != null) {
                    val preparedStatementAddRole = connection.prepareStatement(queryAddRole)
                    preparedStatementAddRole.setInt(1, adminId)
                    preparedStatementAddRole.setInt(2, request.RoleId)

                    preparedStatementAddRole.executeUpdate()

                    connection.commit() // Фиксируем транзакцию
                    res_successful
                } else {
                    connection.rollback() // Откатываем транзакцию
                    println("Error creating user: Failed to retrieve generated ID")
                    res_error
                }
            } ?: res_error
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
            res_error
        }
    }

    override fun DeleteAdmin(request: DeleteAdminRequest): Int {
        val query = "DELETE FROM diary.user WHERE UserId = ?"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.AdminId)

                preparedStatement.executeUpdate()
                res_successful
            } ?: res_error
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            res_error
        }
    }

    override fun SingInAdmin(request: SingInAdminRequest): get_admin {
        val query = "SELECT * FROM diary.user U " +
                "JOIN diary.userrole R ON U.userid = R.Userid " +
                "WHERE Password = ? AND Identifier = ? AND R.roleid = 3"
//                "WHERE Password = ? AND Identifier = ?"

        var admin: Admin? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, request.Password)
                preparedStatement.setString(2, request.Identifier)

                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next())
                        admin = AdminFormation(resultSet)
                }
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
//            println("Error creating user: ${e.message}")
            throw ConnectBDException()
        }

        if (admin == null)
            throw SingInException()

        return get_admin(admin, 0)
    }

    override fun ViewAllUsers(): List<User> {
        val query = "SELECT * FROM diary.user U " +
                "JOIN diary.userrole R ON R.UserId = U.UserId "


        val res_users = mutableListOf<User>()

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_users.add(UserFormation(resultSet))

                res_users
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error getting all students: ${e.message}")
            throw GetStudentsException()
        }
    }
}