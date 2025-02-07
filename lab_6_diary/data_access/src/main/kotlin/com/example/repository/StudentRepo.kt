@file:Suppress("ktlint:standard:no-wildcard-imports")
package com.example.repository

import PostgresDBConnector
import com.example.service.dto.*
import com.example.repository_interface.*
import com.example.logger.Logger
import com.example.model.*
import com.example.*
import java.sql.*

class StudentRepo(
    private val logger: Logger,
    private val dbConnector: PostgresDBConnector
): StudentStorage
{
    val res_error = 3
    val res_successful = 0
    data class quary_update(val quary: String, val fields: MutableMap<String, Any?>)

    private fun StudentFormation(resultSet: ResultSet): Student {
        val StudentId = resultSet.getInt("StudentId")
        val DateStartStuding = resultSet.getDate("DateStartStuding")
        val FirstName = resultSet.getString("FirstName")
        val LastName = resultSet.getString("LastName")
        val Patronymic = resultSet.getString("Patronymic")
        val Birthday = resultSet.getDate("Birthday")
        val ClassId = resultSet.getInt("ClassId")
        val Password = resultSet.getString("Password")
        val Identifier = resultSet.getString("Identifier")
        val Phone = resultSet.getString("Phone")
        val Gender = Gender.valueOf(resultSet.getString("Gender"))
        val GroupStudent = GroupStudent.valueOf(resultSet.getString("GroupStudent"))

        return Student(StudentId, DateStartStuding, FirstName, LastName, Patronymic, Birthday,
            ClassId, Password, Identifier, Phone, Gender, GroupStudent)
    }

    override fun CreateStudent(request: CreateStudentWithRoleRequest): Int {
        val queryAddUser = "INSERT INTO diary.user " +
                "(FirstName, LastName, Patronymic, Birthday, Password, Identifier, Phone, Gender) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"

        val queryAddTeacher = "INSERT INTO diary.student " +
                "(StudentId, DateStartStuding, ClassId, GroupStudent) " +
                "VALUES (?, ?, ?, ?)"

        val queryAddRole = "INSERT INTO diary.UserRole " +
                "(UserID, RoleId) " +
                "VALUES (?, ?)"

        try {
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
                var studentId: Int? = null
                if (generatedKeys.next()) {
                    studentId = generatedKeys.getInt(1)
                }

                if (studentId != null) {
                    val preparedStatementAddStudent = connection.prepareStatement(queryAddTeacher)
                    preparedStatementAddStudent.setInt(1, studentId)
                    preparedStatementAddStudent.setDate(2, java.sql.Date.valueOf(request.DateStartStuding.toLocalDate()))
                    preparedStatementAddStudent.setInt(3, request.ClassId)
                    preparedStatementAddStudent.setObject(4, request.Group, Types.OTHER)

                    preparedStatementAddStudent.executeUpdate()

                    val preparedStatementAddRole = connection.prepareStatement(queryAddRole)
                    preparedStatementAddRole.setInt(1, studentId)
                    preparedStatementAddRole.setInt(2, request.RoleId)

                    preparedStatementAddRole.executeUpdate()

                    connection.commit() // Фиксируем транзакцию
                    return res_successful
                } else {
                    connection.rollback() // Откатываем транзакцию
                    throw AddStudentException()
                }
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
            throw AddStudentException()
        }
    }

    override fun GetStudent(request: GetStudentRequest): get_student {
        val query = "SELECT * FROM diary.user U " +
                "JOIN diary.student T ON T.StudentId = U.UserId " +
                "WHERE UserId = ?"

        var student: Student? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.StudentId)

                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next())
                        student = StudentFormation(resultSet)
                }
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
            return get_student(null, 3)
        }

        if (student == null)
            return get_student(null, 3)

        return get_student(student, 0)
    }

    override fun GetStudents(request: GetStudentsRequest): get_students {
        val query = "SELECT * FROM diary.user U " +
                "JOIN diary.student T ON T.StudentId = U.UserId " +
                "WHERE ClassId = ?"


        val res_student = mutableListOf<Student>()

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.ClassId)
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_student.add(StudentFormation(resultSet))

                get_students(res_student, 0)
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error getting all students: ${e.message}")
            throw GetStudentsException()
        }
    }

    private fun PrepareUserQuery(request: UpdateStudentRequest): quary_update {
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

    private fun PrepareStudentQuery(request: UpdateStudentRequest): quary_update {
        val queryBuilder = StringBuilder("UPDATE diary.student SET")

        val updateFields = mutableMapOf<String, Any?>()
        request.DateStartStuding?.let { updateFields["DateStartStuding"] = it.toLocalDate() }
        request.ClassId?.let { updateFields["ClassId"] = it }
        request.Group?.let { updateFields["GroupStudent"] = it.name }

        println(updateFields)

        for ((index, field) in updateFields.keys.withIndex()) {
            queryBuilder.append(" $field = ?")
            if (index < updateFields.size - 1) {
                queryBuilder.append(",")
            }
        }

        queryBuilder.append(" WHERE StudentId = ?")

        val query = queryBuilder.toString()
        println(query)
        return quary_update(query, updateFields)
    }

    override fun UpdateStudent(request: UpdateStudentRequest): Int {
        val quary_user = PrepareUserQuery(request)
        val quary_student = PrepareStudentQuery(request)

        try {
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
                        preparedStatement_user.setInt(parameterIndex, request.StudentId)
                        println(preparedStatement_user.toString())
                        preparedStatement_user.executeUpdate()
                    }
                    if (quary_student.fields.isNotEmpty()) {
                        val preparedStatement_teacher = connection.prepareStatement(quary_student.quary)
                        var parameterIndex = 1
                        for (value in quary_student.fields.values) {
                            preparedStatement_teacher.setObject(parameterIndex, value, Types.OTHER)
                            parameterIndex++
                        }
                        preparedStatement_teacher.setInt(parameterIndex, request.StudentId)
                        println(preparedStatement_teacher.toString())
                        preparedStatement_teacher.executeUpdate()
                    }
                    connection.commit()
                } catch (e: Exception) {
                    connection.rollback() // Откат транзакции в случае ошибки
                    throw UpdateStudentException()
                }

                return res_successful
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error updating teacher: ${e.message}")
            throw UpdateStudentException()
        }
    }

    override fun DeleteStudent(request: DeleteStudentRequest): Int {
        val query = "DELETE FROM diary.user WHERE UserId = ?"

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.StudentId)

                preparedStatement.executeUpdate()
                return res_successful
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            throw DeleteStudentException()
        }
    }

    override fun SingInStudent(request: SingInStudentRequest): get_student {
        val query = "SELECT * FROM diary.user U " +
                "JOIN diary.student T ON T.StudentId = U.UserId " +
                "JOIN diary.userrole R ON U.userid = R.Userid " +
                "WHERE Password = ? AND Identifier = ? AND R.roleid = 2"
//                "WHERE Password = ? AND Identifier = ?"

        var student: Student? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, request.Password)
                preparedStatement.setString(2, request.Identifier)

                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next())
                        student = StudentFormation(resultSet)
                }
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
//            println("Error sign in: ${e.message}")
            throw ConnectBDException()
        }

        if (student == null)
            throw SingInException()

        return get_student(student, 0)
    }
}