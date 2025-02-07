package org.example.internal.storage.repository

import PostgresDBConnector
import internal.service.dto.*
import internal.storage.ScheduleStorage
import internal.storage.get_schedule
import logger.Logger
import model.*
import org.example.`package`.exceptions.*
import java.sql.*
import java.time.DayOfWeek

class ScheduleRepo(
    private val logger: Logger,
    private val dbConnector: PostgresDBConnector
): ScheduleStorage {
    val res_error = 3
    val res_successful = 0

    private fun ScheduleFormation(resultSet: ResultSet): Schedule {
        val DayWeek = DayOfWeek.valueOf(resultSet.getString("DayOfWeek"))
        val LessonNumber = resultSet.getInt("LessonNumber")
        val GroupSchedule = GroupSchedule.valueOf(resultSet.getString("GroupStudent"))
        val ClassId = resultSet.getInt("ClassId")
        val SubjectId = resultSet.getInt("SubjectId")
        val TeacherId = resultSet.getInt("TeacherId")
        return Schedule(DayWeek, LessonNumber, GroupSchedule, ClassId, SubjectId, TeacherId)
    }

    private fun GetScheduleFromBD(query: String, Id: Int): get_schedule {
        val res_schedule = mutableListOf<Schedule>()

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, Id)
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_schedule.add(ScheduleFormation(resultSet))
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error getting schedule: ${e.message}")
            res_schedule.clear()
        }

//        if (res_schedule.isNotEmpty())
        return get_schedule(res_schedule, 0)
//        return get_schedule(listOf(), 3)
    }

    override fun CreateSchedule(request: CreateScheduleRequest): Int {
        val query = "INSERT INTO diary.schedule " +
                "(DayOfWeek, LessonNumber, GroupStudent, ClassId, SubjectId, TeacherId) " +
                "VALUES (?, ?, ?, ?, ?, ?)"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setObject(1, request.DayWeek.name, Types.OTHER)
                preparedStatement.setInt(2, request.LessonNumber)
                preparedStatement.setObject(3, request.Group.name, Types.OTHER)
                preparedStatement.setInt(4, request.ClassId)
                preparedStatement.setInt(5, request.SubjectId)
                preparedStatement.setInt(6, request.TeacherId)

                preparedStatement.executeUpdate()
                res_successful
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error creating book: ${e.message}")
            throw CreateScheduleException()
        }
    }

    override fun GetScheduleForTeacher(request: GetScheduleForTeacherRequest): get_schedule {
        val query = "SELECT * FROM diary.schedule WHERE TeacherId = ?"

        return GetScheduleFromBD(query, request.TeacherId)
    }

    override fun GetScheduleForClass(request: GetScheduleForClassRequest): get_schedule {
        val query = "SELECT * FROM diary.schedule WHERE ClassId = ?"

        return GetScheduleFromBD(query, request.ClassId)
    }

    override fun DeleteSchedule(request: DeleteScheduleRequest): Int {
        val query = "DELETE FROM diary.schedule WHERE DayOfWeek = ? AND ClassId = ? AND LessonNumber = ?"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setObject(1, request.DayWeek.name, Types.OTHER)
                preparedStatement.setInt(2, request.ClassId)
                preparedStatement.setInt(3, request.LessonNumber)

                preparedStatement.executeUpdate()
                res_successful
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error deleting book: ${e.message}")
            throw DeleteScheduleException()
        }
    }
}