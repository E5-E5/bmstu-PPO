package internal.storage.repository

import PostgresDBConnector
import internal.service.dto.CreateStudentWithRoleRequest
import logger.Logger
import model.Gender
import model.GroupStudent
import org.example.internal.service.dto.CreateAdminRequest
import org.example.internal.service.dto.CreateAdminWithRoleRequest
import org.example.internal.storage.repository.AdminRepo
import org.example.internal.storage.repository.StudentRepo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.sql.Date

class AdminRepoTest {

    @Test
    fun createAdmin() {
        var request_create_1 = CreateAdminWithRoleRequest(
            1, "st1", "f_st1", "pat_st1",
            Date.valueOf("2001-11-13"), "12345", "bbb", "+79743895929",
            Gender.MALE, 3)
        var sut = AdminRepo(Logger(), PostgresDBConnector())
        var actual = sut.CreateAdmin(request_create_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun deleteAdmin() {
    }

    @Test
    fun singInAdmin() {
    }

    @Test
    fun viewAllUsers() {
    }
}