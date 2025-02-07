package internal.storage.repository

import PostgresDBConnector
import internal.service.dto.*
import logger.Logger
import model.GroupSchedule
import model.StudentAssessment
import org.example.internal.storage.repository.AssessmentRepo
import org.example.internal.storage.repository.ScheduleRepo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.sql.Date
import java.time.DayOfWeek

class ScheduleRepoTest {

    @Test
    fun createSchedule() {
        var request_create_1 = CreateScheduleRequest(DayOfWeek.MONDAY, 2, GroupSchedule.JOINT, 1, 2, 10)
        var sut = ScheduleRepo(Logger(), PostgresDBConnector())
        var actual = sut.CreateSchedule(request_create_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun getScheduleForTeacher() {
        var request_get_1 = GetScheduleForTeacherRequest(1)
        var sut = ScheduleRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetScheduleForTeacher(request_get_1)
        println(actual.res_Schedule)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun getScheduleForClass() {
        var request_get_1 = GetScheduleForClassRequest(1)
        var sut = ScheduleRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetScheduleForClass(request_get_1)
        println(actual.res_Schedule)
        var expected = 0
        assertEquals(expected, actual.error)
    }

//    @Test
//    fun deleteSchedule() {
//        var request_delete_1 = DeleteScheduleRequest(DayOfWeek.TUESDAY, 4, 1)
//        var sut = ScheduleRepo(Logger(), PostgresDBConnector())
//        var actual = sut.DeleteSchedule(request_delete_1)
//        var expected = 0
//        assertEquals(expected, actual)
//    }
}