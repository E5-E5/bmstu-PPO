package internal.storage.repository

import PostgresDBConnector
import internal.service.dto.*
import logger.Logger
import model.ClassNumber
import model.Count
import org.example.internal.storage.repository.ClassRepo
import org.example.internal.storage.repository.SubjectRepo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class SubjectRepoTest {

    @Test
    fun createSubject() {
        var request_create_1 = CreateSubjectRequest(1, "Temp3", Count.TWO)
        var sut = SubjectRepo(Logger(), PostgresDBConnector())
        var actual = sut.CreateSubject(request_create_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun getSubjectById() {
        var request_get_1 = GetSubjectRequestById(1)
        var sut = SubjectRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetSubjectById(request_get_1)
        println(actual.res_Subject)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun getSubjectByName() {
        var request_get_1 = GetSubjectRequestByName("Temp2")
        var sut = SubjectRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetSubjectByName(request_get_1)
        println(actual.res_Subject)
        var expected = 0
        assertEquals(expected, actual.error)
    }

//    @Test
//    fun deleteSubject() {
//        var request_delete_1 = DeleteSubjectRequest(1)
//        var sut = SubjectRepo(Logger(), PostgresDBConnector())
//        var actual = sut.DeleteSubject(request_delete_1)
//        var expected = 0
//        assertEquals(expected, actual)
//    }
}