package internal.service

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import internal.storage.*
import internal.service.dto.*
import logger.*
import model.Class
import model.ClassNumber
import org.junit.jupiter.api.Test
import java.sql.Date


class ClassServiceTest {
    private  val mockClassRepository = Mockito.mock(ClassStorage::class.java)
    private val allClasses = listOf(
        Class(1, "A", ClassNumber.TWO),
        Class(2, "Б", ClassNumber.EIGHT),
        Class(3, "В", ClassNumber.SEVEN))
    @Test
    fun getClass_OK() {
        //test 1. Корректный
        val request_1 = GetClassByIdRequest(1)

        Mockito.`when`(mockClassRepository.GetClassById(request_1))
            .thenReturn(get_class(Class(1, "A", ClassNumber.TWO), 0))
        val sut = ClassService(Logger(), mockClassRepository)

        val actual = sut.GetClassById(request_1).res_Class
        val expected: Class? = allClasses.find { it.ClassId == request_1.ClassId }
        assertEquals(expected, actual)
    }
    @Test
    fun getClass_ClassError() {
        //test 2. Нет класса
        val request_2 = GetClassByIdRequest(4)

        Mockito.`when`(mockClassRepository.GetClassById(request_2)).thenReturn(get_class(null, 3))
        val sut = ClassService(Logger(), mockClassRepository)

        val actual = sut.GetClassById(request_2).res_Class
        val expected = allClasses.find { it.ClassId == request_2.ClassId }
        assertEquals(expected, actual)
    }

    @Test
    fun createClass_OK() {
        //test 1. Корректный
        val request_create_1 = CreateClassRequest(1, "E", ClassNumber.SEVEN)
        val request_get_1 = GetClassByNameRequest("E", ClassNumber.SEVEN)

        Mockito.`when`(mockClassRepository.CreateClass(request_create_1)).thenReturn(0)
        Mockito.`when`(mockClassRepository.GetClassByName(request_get_1)).thenReturn(get_class(null, 3))
        val sut = ClassService(Logger(), mockClassRepository)

        val actual = sut.CreateClass(request_create_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun createClass_ClassAlreadyAdd() {
        //test 2. Класс уже был добавлен
        val request_create_2 = CreateClassRequest(1, "E", ClassNumber.FIVE)
        val request_get_2 = GetClassByNameRequest("E", ClassNumber.FIVE)

        Mockito.`when`(mockClassRepository.GetClassByName(request_get_2)).thenReturn(
            get_class(Class(1, "E", ClassNumber.FIVE),0))
        val sut = ClassService(Logger(), mockClassRepository)

        val actual = sut.CreateClass(request_create_2)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun deleteClass_OK() {
        //test 1. Корректный
        val request_delete_1 = DeleteClassRequest(1)
        val request_get_1 = GetClassByIdRequest(1)

        Mockito.`when`(mockClassRepository.GetClassById(request_get_1)).thenReturn(
            get_class(Class(1, "E", ClassNumber.NINE), 0)
        )
        Mockito.`when`(mockClassRepository.DeleteClass(request_delete_1)).thenReturn(0)
        val sut = ClassService(Logger(), mockClassRepository)

        val actual = sut.DeleteClass(request_delete_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun deleteClass_ClassError() {
        //test 2. Get class error
        val request_delete_2 = DeleteClassRequest(1)
        val request_get_2 = GetClassByIdRequest(1)

        Mockito.`when`(mockClassRepository.GetClassById(request_get_2)).thenReturn(get_class(null,3))
        val sut = ClassService(Logger(), mockClassRepository)

        val actual = sut.DeleteClass(request_delete_2)
        val expected = 3
        assertEquals(expected, actual)
    }
}