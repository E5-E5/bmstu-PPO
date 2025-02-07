@file:Suppress("ktlint:standard:no-wildcard-imports")
package com.example.repository

import PostgresDBConnector
import com.example.service.dto.*
import com.example.repository_interface.*
import com.example.logger.Logger
import com.example.model.Gender
import com.example.model.Teacher
import com.example.*
import java.sql.*

class TeacherRepo(
    private val logger: Logger,
    private val dbConnector: PostgresDBConnector
): TeacherStorage
{
    val res_error = 3
    val res_successful = 0
    data class quary_update(val quary: String, val fields: MutableMap<String, Any?>)

    private fun TeacherFormation(resultSet: ResultSet): Teacher {
        val TeacherId = resultSet.getInt("TeacherId")
        val DateStartTeaching = resultSet.getDate("DateStartTeaching")
        val FirstName = resultSet.getString("FirstName")
        val LastName = resultSet.getString("LastName")
        val Patronymic = resultSet.getString("Patronymic")
        val Birthday = resultSet.getDate("Birthday")
        val Password = resultSet.getString("Password")
        val Identifier = resultSet.getString("Identifier")
        val Phone = resultSet.getString("Phone")
        val Gender = Gender.valueOf(resultSet.getString("Gender"))
        val Cabinet = resultSet.getInt("Cabinet")

        return Teacher(TeacherId, DateStartTeaching, FirstName, LastName, Patronymic, Birthday,
            Password, Identifier, Phone, Gender, Cabinet)
    }

    override fun CreateTeacher(request: CreateTeacherWithRoleRequest): Int {
        val queryAddUser = "INSERT INTO diary.user " +
                "(FirstName, LastName, Patronymic, Birthday, Password, Identifier, Phone, Gender) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"

        val queryAddTeacher = "INSERT INTO diary.teacher " +
                "(TeacherId, DateStartTeaching, Cabinet) " +
                "VALUES (?, ?, ?)"

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
                var teacherId: Int? = null
                if (generatedKeys.next()) {
                    teacherId = generatedKeys.getInt(1)
                }

                if (teacherId != null) {
                    val preparedStatementAddTeacher = connection.prepareStatement(queryAddTeacher)
                    preparedStatementAddTeacher.setInt(1, teacherId)
                    preparedStatementAddTeacher.setDate(2, java.sql.Date.valueOf(request.DateStartTeaching.toLocalDate()))
                    preparedStatementAddTeacher.setInt(3, request.Cabinet)

                    preparedStatementAddTeacher.executeUpdate()

                    val preparedStatementAddRole = connection.prepareStatement(queryAddRole)
                    preparedStatementAddRole.setInt(1, teacherId)
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

    override fun GetTeacher(request: GetTeacherRequest): get_teacher {
        val query = "SELECT * FROM diary.user U " +
                    "JOIN diary.teacher T ON T.TeacherId = U.UserId " +
                    "WHERE UserId = ?"

        var teacher: Teacher? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.TeacherId)

                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next())
                        teacher = TeacherFormation(resultSet)
                }
            }
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
        }

        if (teacher != null)
            return get_teacher(teacher, 0)
        return get_teacher(null, 3)
    }

    private fun PrepareUserQuery(request: UpdateTeacherRequest): quary_update {
        val queryBuilder = StringBuilder("UPDATE diary.user SET")

        val updateFields = mutableMapOf<String, Any?>()
        request.FirstName?.let { updateFields["FirstName"] = it }
        request.LastName?.let { updateFields["LastName"] = it }
        request.Patronymic?.let { updateFields["Patronymic"] = it }
        request.Birthday?.let { updateFields["Birthday"] = it.toLocalDate() }
        request.Phone?.let { updateFields["Phone"] = it }
        request.Gender?.let { updateFields["Gender"] = it.name }

        println(updateFields)

        for ((index, field) in updateFields.keys.withIndex()) {
            queryBuilder.append(" $field = ?")
            if (index < updateFields.size - 1) {
                queryBuilder.append(",")
            }
        }

        queryBuilder.append(" WHERE UserId = ?")

        val query = queryBuilder.toString()
        println(query)
        return quary_update(query, updateFields)
    }

    private fun PrepareTeacherQuery(request: UpdateTeacherRequest): quary_update {
        val queryBuilder = StringBuilder("UPDATE diary.teacher SET")

        val updateFields = mutableMapOf<String, Any?>()
        request.DateStartTeaching?.let { updateFields["DateStartTeaching"] = it.toLocalDate() }
        request.Cabinet?.let { updateFields["Cabinet"] = it }

        println(updateFields)

        for ((index, field) in updateFields.keys.withIndex()) {
            queryBuilder.append(" $field = ?")
            if (index < updateFields.size - 1) {
                queryBuilder.append(",")
            }
        }

        queryBuilder.append(" WHERE TeacherId = ?")

        val query = queryBuilder.toString()
        println(query)
        return quary_update(query, updateFields)
    }

    override fun UpdateTeacher(request: UpdateTeacherRequest): Int {
        val quary_user = PrepareUserQuery(request)
        val quary_teacher = PrepareTeacherQuery(request)

        return try {
            dbConnector.getConnection()?.use { connection ->
                connection.autoCommit = false // Начало транзакции

                try {
                    if (quary_user.fields.isNotEmpty()) {
                        val preparedStatement_user = connection.prepareStatement(quary_user.quary)
                        var parameterIndex = 1
                        for (value in quary_user.fields.values) {
                            preparedStatement_user.setObject(parameterIndex, value, Types.OTHER)
                            parameterIndex++
                        }
                        preparedStatement_user.setInt(parameterIndex, request.TeacherId)
                        println(preparedStatement_user.toString())
                        preparedStatement_user.executeUpdate()
                    }
                    if (quary_teacher.fields.isNotEmpty()) {
                        val preparedStatement_teacher = connection.prepareStatement(quary_teacher.quary)
                        var parameterIndex = 1
                        for (value in quary_teacher.fields.values) {
                            preparedStatement_teacher.setObject(parameterIndex, value, Types.OTHER)
                            parameterIndex++
                        }
                        preparedStatement_teacher.setInt(parameterIndex, request.TeacherId)
                        println(preparedStatement_teacher.toString())
                        preparedStatement_teacher.executeUpdate()
                    }
                    connection.commit()
                } catch (e: Exception) {
                    connection.rollback() // Откат транзакции в случае ошибки
                    res_error
                }
                res_successful
            } ?: res_error
        } catch (e: Exception) {
            println("Error updating teacher: ${e.message}")
            res_error
        }
    }
    override fun DeleteTeacher(request: DeleteTeacherRequest): Int {
        val query = "DELETE FROM diary.user WHERE UserId = ?"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.TeacherId)
                preparedStatement.executeUpdate()
            }
            res_successful
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            res_error
        }
    }

    override fun SingInTeacher(request: SingInTeacherRequest): get_teacher {
        val query = "SELECT * FROM diary.user U " +
                "JOIN diary.teacher T ON T.TeacherId = U.UserId " +
                "JOIN diary.userrole R ON U.userid = R.Userid " +
                "WHERE Password = ? AND Identifier = ? AND R.roleid = 1"
//                "WHERE Password = ? AND Identifier = ?"

        var teacher: Teacher? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, request.Password)
                preparedStatement.setString(2, request.Identifier)

                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next())
                        teacher = TeacherFormation(resultSet)
                }
            } ?: throw ConnectBDException()
        } catch (e: ConnectBDException) {
//            println("Error creating user: ${e.message}")
            throw ConnectBDException()
        }

        if (teacher == null)
            throw SingInException()

        return get_teacher(teacher, 0)
    }

    override fun AddSubjectToTeacher(request: AddSubjectToTeacherRequest): Int {
        val query = "INSERT INTO diary.teachersubject " +
                    "VALUES (?, ?)"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.TeacherId)
                preparedStatement.setInt(2, request.SubjectId)

                preparedStatement.executeUpdate()
                res_successful
            } ?: res_error
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
            res_error
        }
    }

    override fun DeleteSubjectFromTeacher(request: DeleteSubjectFromTeacherRequest): Int {
        val query = "DELETE FROM diary.teachersubject WHERE " +
                "TeacherId = ? AND SubjectId = ?"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.TeacherId)
                preparedStatement.setInt(2, request.SubjectId)
                preparedStatement.executeUpdate()
            }
            res_successful
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            res_error
        }
    }

    override fun GetTeacherBySubject(request: GetTeacherBySubjectRequest): List<Teacher> {
        val query = "SELECT * FROM diary.teacher S " +
                "JOIN diary.teachersubject T ON T.TeacherId = S.TeacherId "+
                "JOIN diary.user U ON U.UserId = S.TeacherId "+
                "WHERE SubjectId = ?"

        val res_teachers = mutableListOf<Teacher>()

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.SubjectId)
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_teachers.add(TeacherFormation(resultSet))

                res_teachers
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            throw GetTeachersException()
        }
    }
}