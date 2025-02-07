package internal.storage.repository

import PostgresDBConnector
import internal.service.AssessmentService
import internal.service.dto.CreateClassRequest
import internal.service.dto.DeleteClassRequest
import internal.service.dto.GetClassByIdRequest
import internal.service.dto.GetClassByNameRequest
import internal.storage.ClassStorage
import logger.Logger
import model.ClassNumber
import org.example.internal.storage.repository.ClassRepo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ClassRepoTest {

    @Test
    fun createClass() {
        var request_create_1 = CreateClassRequest(3, "A", ClassNumber.ONE)
        var sut = ClassRepo(Logger(), PostgresDBConnector())
        var actual = sut.CreateClass(request_create_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun getClassById() {
        var request_get_1 = GetClassByIdRequest(1)
        var sut = ClassRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetClassById(request_get_1)
        println(actual.res_Class)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun getClassByName() {
        var request_get_1 = GetClassByNameRequest("A", ClassNumber.ONE)
        var sut = ClassRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetClassByName(request_get_1)
        println(actual.res_Class)
        var expected = 0
        assertEquals(expected, actual.error)
    }

//    @Test
//    fun deleteClass() {
//        var request_delete_1 = DeleteClassRequest(1)
//        var sut = ClassRepo(Logger(), PostgresDBConnector())
//        var actual = sut.DeleteClass(request_delete_1)
//        var expected = 0
//        assertEquals(expected, actual)
//    }
}