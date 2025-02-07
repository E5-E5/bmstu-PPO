package internal.storage.repository

import PostgresDBConnector
import internal.service.dto.*
import internal.storage.StudentStorage
import logger.Logger
import model.ClassNumber
import model.Gender
import model.GroupStudent
import model.Student
import org.example.internal.storage.repository.ClassRepo
import org.example.internal.storage.repository.StudentRepo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.sql.Date

class StudentRepoTest {

    @Test
    fun createStudent() {
        var request_create_1 = CreateStudentWithRoleRequest(
            1, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
            Gender.MALE, GroupStudent.FIRST, 2)
        var sut = StudentRepo(Logger(), PostgresDBConnector())
        var actual = sut.CreateStudent(request_create_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun getStudent() {
        var request_get_1 = GetStudentRequest(6)
        var sut = StudentRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetStudent(request_get_1)
        println(actual.res_Student)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun getStudents() {
        var request_get_1 = GetStudentsRequest(1)
        var sut = StudentRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetStudents(request_get_1)
        println(actual.res_Students)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun updateStudent() {
        var request_update_1 = UpdateStudentRequest(6, FirstName = "1111")
        var sut = StudentRepo(Logger(), PostgresDBConnector())
        var actual = sut.UpdateStudent(request_update_1)
        println(actual)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun deleteStudent() {
        var request_delete_1 = DeleteStudentRequest(6)
        var sut = StudentRepo(Logger(), PostgresDBConnector())
        var actual = sut.DeleteStudent(request_delete_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun singInStudent() {
        var request_singin_1 = SingInStudentRequest("12345", "a1s2")
        var sut = StudentRepo(Logger(), PostgresDBConnector())
        var actual = sut.SingInStudent(request_singin_1)
        println(actual.res_Student)
        var expected = 0
        assertEquals(expected, actual.error)
    }
}