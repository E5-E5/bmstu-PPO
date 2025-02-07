package internal.service

import internal.service.dto.*
import internal.storage.SubjectStorage
import internal.storage.get_student
import internal.storage.get_subject
import logger.Logger
import model.Count
import model.Subject
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito

class SubjectServiceTest {
    private val mockSubjectRepository = Mockito.mock(SubjectStorage::class.java)
    private val allSubjects = listOf(
        Subject(1, "Math", Count.ONE),
        Subject(2, "Russian", Count.ONE),
        Subject(3, "English", Count.TWO)
    )

    @Test
    fun getSubject_OK() {
        //test 1. OK
        val request_get_1 = GetSubjectRequestById(1)
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_1))
            .thenReturn(get_subject(Subject(1, "Math", Count.ONE), 0))
        val sut = SubjectService(Logger(), mockSubjectRepository)

        val actual = sut.GetSubject(request_get_1).res_Subject
        val expected: Subject? = allSubjects.find { it.SubjectId == request_get_1.SubjectId }
        assertEquals(expected, actual)
    }
    @Test
    fun getSubject_SubjectError() {
        //test 2. Get subject error
        val request_get_2 = GetSubjectRequestById(4)
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_2)).thenReturn(get_subject(null, 3))
        val expected = allSubjects.find { it.SubjectId == request_get_2.SubjectId }

        val sut = SubjectService(Logger(), mockSubjectRepository)
        val actual = sut.GetSubject(request_get_2).res_Subject
        assertEquals(expected, actual)
    }

    @Test
    fun createSubject_OK() {
        //test 1. OK
        val request_create_1 = CreateSubjectRequest(1, "TEMP2", Count.TWO)
        val request_get_1 = GetSubjectRequestByName("TEMP2")

        Mockito.`when`(mockSubjectRepository.GetSubjectByName(request_get_1)).thenReturn(
            get_subject(null, 3)
        )
        Mockito.`when`(mockSubjectRepository.CreateSubject(request_create_1)).thenReturn(0)
        val sut = SubjectService(Logger(), mockSubjectRepository)

        val actual = sut.CreateSubject(request_create_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun createSubject_SubjectError() {
        //test 2. Get subject error
        val request_create_2 = CreateSubjectRequest(1, "TEMP", Count.ONE)
        val request_get_2 = GetSubjectRequestByName("TEMP")

        Mockito.`when`(mockSubjectRepository.GetSubjectByName(request_get_2)).thenReturn(
            get_subject(Subject(1, "TEMP", Count.ONE),0))
        val sut = SubjectService(Logger(), mockSubjectRepository)

        val actual = sut.CreateSubject(request_create_2)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun deleteSubject_OK() {
        //test 1. OK
        val request_delete_1 = DeleteSubjectRequest(1)
        val request_get_1 = GetSubjectRequestById(1)

        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_1)).thenReturn(
            get_subject(Subject(1, "TEMP", Count.ONE), 0)
        )
        Mockito.`when`(mockSubjectRepository.DeleteSubject(request_delete_1)).thenReturn(0)
        val sut = SubjectService(Logger(), mockSubjectRepository)

        val actual = sut.DeleteSubject(request_delete_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun deleteSubject_SubjectError() {
        //test 2. Get subject error
        val request_delete_2 = DeleteSubjectRequest(1)
        val request_get_2 = GetSubjectRequestById(1)

        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_2)).thenReturn(
            get_subject(null,3))
        val sut = SubjectService(Logger(), mockSubjectRepository)

        val actual = sut.DeleteSubject(request_delete_2)
        val expected = 3
        assertEquals(expected, actual)
    }
}