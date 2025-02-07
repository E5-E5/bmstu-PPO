package internal.service

import internal.service.dto.*
import internal.storage.*
import logger.Logger
import model.*
import org.example.internal.storage.repository.RoleStorage
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.sql.Date

class StudentServiceTest {
    private val mockStudentRepository = Mockito.mock(StudentStorage::class.java)
    private val mockClassRepository = Mockito.mock(ClassStorage::class.java)
    private val mockRoleRepository = Mockito.mock(RoleStorage::class.java)
    private val allStudents = listOf(
        Student(1, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
            Gender.MALE, GroupStudent.FIRST),
        Student(2, Date.valueOf("2002-11-13"), "st2", "f_st2", "pat_st2",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
            Gender.MALE, GroupStudent.FIRST),
        Student(3, Date.valueOf("2003-11-15"), "st3", "f_st3", "pat_st3",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
            Gender.MALE, GroupStudent.FIRST),
        Student(4, Date.valueOf("2023-11-13"), "st4", "f_st4", "pat_st4",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
            Gender.MALE, GroupStudent.SECOND),
    )

    @Test
    fun getStudent_OK() {
        // test 1. OK
        val request_get_1 = GetStudentRequest(1)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_1)).thenReturn(
            get_student(
                Student(
                    1, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.GetStudent(request_get_1).res_Student
        val expected: Student? = allStudents.filter { it.StudentId == request_get_1.StudentId }[0]
        assertEquals(expected, actual)
    }
    @Test
    fun getStudent_StudentError() {
        // test 2. Get student error
        val request_get_2 = GetStudentRequest(5)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_2)).thenReturn(get_student(null, 3))
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.GetStudent(request_get_2).res_Student
        val expected = allStudents.find { it.StudentId == request_get_2.StudentId }
        assertEquals(expected, actual)
    }

    @Test
    fun getStudents_OK() {
        // test 1. OK
        val request_get_1 = GetStudentsRequest(1)
        Mockito.`when`(mockStudentRepository.GetStudents(request_get_1)).thenReturn(
            get_students(allStudents.filter { it.ClassId == request_get_1.ClassId }, 0)
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.GetStudents(request_get_1).error
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun getStudents_StudentError() {
        //test 2. Get students error
        val request_get_2 = GetStudentsRequest(4)

        Mockito.`when`(mockStudentRepository.GetStudents(request_get_2)).thenReturn(
            get_students(allStudents.filter { it.ClassId == request_get_2.ClassId }, 3))
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.GetStudents(request_get_2).res_Students
        val expected = listOf<Student>()
        assertEquals(expected, actual)
    }

    @Test
    fun createStudent_OK() {
        // test 1. OK
        val request_create_1 = CreateStudentRequest(
            6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "9743895929",
            Gender.MALE, GroupStudent.FIRST
        )

        val request_create_role_1 = CreateStudentWithRoleRequest(
            6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "9743895929",
            Gender.MALE, GroupStudent.FIRST, 2
        )
        val request_get_1 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_1)).thenReturn(
            get_student(null, 3)
        )
        Mockito.`when`(mockClassRepository.GetClassById(GetClassByIdRequest(request_create_1.ClassId))).thenReturn(
            get_class(Class(1, "E", ClassNumber.TWO), 0)
        )
        Mockito.`when`(mockStudentRepository.CreateStudent(request_create_role_1)).thenReturn(0)
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.CreateStudent(request_create_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun createStudent_StudentError() {
        // test 2. get same student error
        val request_create_2 = CreateStudentRequest(
            6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "9743895929",
            Gender.MALE, GroupStudent.FIRST
        )
        val request_get_2 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_2)).thenReturn(
            get_student(
                Student(
                    6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "9743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.CreateStudent(request_create_2)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun createStudent_ClassError() {
        //test 3. not get class error
        val request_create_3 = CreateStudentRequest(
            6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "9743895929",
            Gender.MALE, GroupStudent.FIRST
        )
        val request_get_3 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_3)).thenReturn(
            get_student(null, 3)
        )
        Mockito.`when`(mockClassRepository.GetClassById(GetClassByIdRequest(request_create_3.ClassId))).thenReturn(
            get_class(null, 3)
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.CreateStudent(request_create_3)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun createStudent_BirthdayError() {
        // test 4. birthday in 2034 year error
        val request_create_4 = CreateStudentRequest(
            6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2034-11-13"), 1, "12345", "a1s2", "9743895929",
            Gender.MALE, GroupStudent.FIRST
        )
        val request_get_4 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_4)).thenReturn(
            get_student(null, 3)
        )
        Mockito.`when`(mockClassRepository.GetClassById(GetClassByIdRequest(request_create_4.ClassId))).thenReturn(
            get_class(Class(1, "E", ClassNumber.TWO), 0)
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.CreateStudent(request_create_4)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun createStudent_GroupStudentError() {
        // test 5. not correct GroupStudent
        val request_create_5 = CreateStudentRequest(
            6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "9743895929",
            Gender.MALE, GroupStudent.FIRST
        )
        val request_get_5 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_5)).thenReturn(
            get_student(null, 3)
        )
        Mockito.`when`(mockClassRepository.GetClassById(GetClassByIdRequest(request_create_5.ClassId))).thenReturn(
            get_class(Class(1, "E", ClassNumber.TWO), 0)
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.CreateStudent(request_create_5)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun createStudent_CreateError() {
        // test 6. create student error
        val request_create_6 = CreateStudentRequest(6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "9743895929",
            Gender.MALE, GroupStudent.FIRST)
        val request_create_role_1 = CreateStudentWithRoleRequest(
            6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
            Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "9743895929",
            Gender.MALE, GroupStudent.FIRST, 2
        )
        val request_get_6 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_6)).thenReturn(
            get_student(null,3))
        Mockito.`when`(mockClassRepository.GetClassById(GetClassByIdRequest(request_create_6.ClassId))).thenReturn(
            get_class(Class(1, "E", ClassNumber.TWO),0))
        Mockito.`when`(mockStudentRepository.CreateStudent(request_create_role_1)).thenReturn(3)
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.CreateStudent(request_create_6)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun deleteStudent_OK() {
        // test 1. OK
        val request_delete_1 = DeleteStudentRequest(6)
        val request_get_1 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_1)).thenReturn(
            get_student(
                Student(
                    6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "9743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        Mockito.`when`(mockStudentRepository.DeleteStudent(request_delete_1)).thenReturn(0)
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.DeleteStudent(request_delete_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun deleteStudent_StudentError() {
        // test 2. Get student error
        val request_delete_2 = DeleteStudentRequest(1)
        val request_get_2 = GetStudentRequest(1)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_2)).thenReturn(
            get_student(null, 3)
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.DeleteStudent(request_delete_2)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun deleteStudent_DeleteError() {
        // test 3. Delete student error
        val request_delete_3 = DeleteStudentRequest(6)
        val request_get_3 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_3)).thenReturn(
            get_student(Student(6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                Gender.MALE, GroupStudent.FIRST),0))
        Mockito.`when`(mockStudentRepository.DeleteStudent(request_delete_3)).thenReturn(3)
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.DeleteStudent(request_delete_3)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun updateStudent_OK() {
        //test 1. OK
        val request_update_1 = UpdateStudentRequest(6, ClassId = 5)
        val request_get_1 = GetStudentRequest(6)
        val request_class_1 = GetClassByIdRequest(5)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_1)).thenReturn(
            get_student(
                Student(
                    6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        Mockito.`when`(mockClassRepository.GetClassById(request_class_1)).thenReturn(
            get_class(Class(5, "E", ClassNumber.TWO), 0)
        )
        Mockito.`when`(mockStudentRepository.UpdateStudent(request_update_1)).thenReturn(0)
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.UpdateStudent(request_update_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun updateStudent_StudentError() {
        //test 2. Get student error
        val request_update_2 = UpdateStudentRequest(6, ClassId = 5)
        val request_get_2 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_2)).thenReturn(
            get_student(null, 3)
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.UpdateStudent(request_update_2)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun updateStudent_ClassError() {
        //test 3. Get class error
        val request_update_3 = UpdateStudentRequest(6, ClassId = 5)
        val request_get_3 = GetStudentRequest(6)
        val request_class_3 = GetClassByIdRequest(5)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_3)).thenReturn(
            get_student(
                Student(
                    6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        Mockito.`when`(mockClassRepository.GetClassById(request_class_3)).thenReturn(
            get_class(null, 3)
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.UpdateStudent(request_update_3)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun updateStudent_UpdateError() {
        //test 4. Update student error
        val request_update_4 = UpdateStudentRequest(6)
        val request_get_4 = GetStudentRequest(6)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_4)).thenReturn(
            get_student(Student(6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                Gender.MALE, GroupStudent.FIRST),0))
        Mockito.`when`(mockStudentRepository.UpdateStudent(request_update_4)).thenReturn(3)
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.UpdateStudent(request_update_4)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun singInStudent_OK() {
        // test 1. OK
        val request_singin_1 = SingInStudentRequest("123", "123aa")
        Mockito.`when`(mockStudentRepository.SingInStudent(request_singin_1)).thenReturn(
            get_student(
                Student(
                    6, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "123", "123aa", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.SingInStudent(request_singin_1).error
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun singInStudent_Error() {
        //test 2. Error
        val request_singin_2 = SingInStudentRequest("1213", "123aa")
        Mockito.`when`(mockStudentRepository.SingInStudent(request_singin_2)).thenReturn(get_student(null,3))
        val sut = StudentService(Logger(), mockStudentRepository, mockClassRepository, mockRoleRepository)

        val actual = sut.SingInStudent(request_singin_2).error
        val expected = 3
        assertEquals(expected, actual)
    }
}