package internal.storage.repository

import PostgresDBConnector
import internal.service.dto.*
import logger.Logger
import model.Gender
import org.example.internal.storage.repository.StudentRepo
import org.example.internal.storage.repository.TeacherRepo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.sql.Date

class TeacherRepoTest {

    @Test
    fun createTeacher() {
        var request_create_1 = CreateTeacherWithRoleRequest(
            1, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), "12345", "aaa", "+79743895929",
            Gender.MALE, 321, 1)
        var sut = TeacherRepo(Logger(), PostgresDBConnector())
        var actual = sut.CreateTeacher(request_create_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun getTeacher() {
        var request_get_1 = GetTeacherRequest(2)
        var sut = TeacherRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetTeacher(request_get_1)
        println(actual.res_Teacher)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun updateTeacher() {
        var request_update_1 = UpdateTeacherRequest(3, Gender = Gender.FEMALE)
        var sut = TeacherRepo(Logger(), PostgresDBConnector())
        var actual = sut.UpdateTeacher(request_update_1)
        println(actual)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun deleteTeacher() {
        var request_delete_1 = DeleteTeacherRequest(3)
        var sut = TeacherRepo(Logger(), PostgresDBConnector())
        var actual = sut.DeleteTeacher(request_delete_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun singInTeacher() {
        var request_singin_1 = SingInTeacherRequest("12345", "a1s2")
        var sut = TeacherRepo(Logger(), PostgresDBConnector())
        var actual = sut.SingInTeacher(request_singin_1)
        println(actual.res_Teacher)
        var expected = 0
        assertEquals(expected, actual.error)
    }
}