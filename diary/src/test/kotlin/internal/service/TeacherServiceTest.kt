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

class TeacherServiceTest {
    private val mockTeacherRepository = Mockito.mock(TeacherStorage::class.java)
    private val mockSubjectRepository = Mockito.mock(SubjectStorage::class.java)
    private val mockRoleRepository = Mockito.mock(RoleStorage::class.java)

    private val allTeachers = listOf(
        Teacher(1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 12),
        Teacher(2, Date.valueOf("2002-11-13"), "t2", "f_t2", "pat_t2",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 13),
        Teacher(3, Date.valueOf("2003-11-15"), "t3", "f_t3", "pat_t3",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 15),
        Teacher(4, Date.valueOf("2023-11-13"), "t4", "f_t4", "pat_t4",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 546),
    )

    @Test
    fun getTeacher_OK() {
        // test 1. OK
        val request_get_1 = GetTeacherRequest(1)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_1)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.GetTeacher(request_get_1).res_Teacher
        val expected: Teacher? = allTeachers.filter { it.TeacherId == request_get_1.TeacherId }[0]
        assertEquals(expected, actual)
    }
    @Test
    fun getTeacher_TeacherError() {
        // test 2. Get teacher error
        val request_get_2 = GetTeacherRequest(5)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_2)).thenReturn(get_teacher(null, 3))
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.GetTeacher(request_get_2).res_Teacher
        val expected = allTeachers.find { it.TeacherId == request_get_2.TeacherId }
        assertEquals(expected, actual)
    }

    @Test
    fun createTeacher_OK() {
        // test 1. OK
        val request_get_1 = GetTeacherRequest(1)
        val request_create_1 = CreateTeacherRequest(
            1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 12
        )
        val request_create_role_1 = CreateTeacherWithRoleRequest(
            1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 12, 1
        )

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_1)).thenReturn(
            get_teacher(null, 3)
        )
        Mockito.`when`(mockTeacherRepository.CreateTeacher(request_create_role_1)).thenReturn(0)
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.CreateTeacher(request_create_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun createTeacher_TeacherError() {
        // test 2. Get teacher error
        val request_get_2 = GetTeacherRequest(1)
        val request_create_2 = CreateTeacherRequest(
            1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 12
        )

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_2)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.CreateTeacher(request_create_2)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun createTeacher_BirthdayError() {
        // test 4. Late birthday
        val request_get_4 = GetTeacherRequest(1)
        val request_create_4 = CreateTeacherRequest(
            1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
            Date.valueOf("2034-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 12
        )

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_4)).thenReturn(
            get_teacher(null, 3)
        )

        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.CreateTeacher(request_create_4)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun createTeacher_CreteError() {
        // test 5. Create error
        val request_get_5 = GetTeacherRequest(1)
        val request_create_5 = CreateTeacherRequest(1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 12)
        val request_create_role_1 = CreateTeacherWithRoleRequest(
            1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
            Gender.MALE, 12, 1
        )
        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_5)).thenReturn(
            get_teacher(null, 3))

        Mockito.`when`(mockTeacherRepository.CreateTeacher(request_create_role_1)).thenReturn(3)
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.CreateTeacher(request_create_5)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun deleteTeacher_OK() {
        // test 1. OK
        val request_get_1 = GetTeacherRequest(1)
        val request_delete_1 = DeleteTeacherRequest(1)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_1)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        Mockito.`when`(mockTeacherRepository.DeleteTeacher(request_delete_1)).thenReturn(0)
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.DeleteTeacher(request_delete_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun deleteTeacher_TeacherError() {
        // test 2. Get teacher error
        val request_get_2 = GetTeacherRequest(1)
        val request_delete_2 = DeleteTeacherRequest(1)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_2)).thenReturn(
            get_teacher(null, 3)
        )
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.DeleteTeacher(request_delete_2)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun deleteTeacher_DeleteError() {
        // test 3. Delete error
        val request_get_3 = GetTeacherRequest(1)
        val request_delete_3 = DeleteTeacherRequest(1)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_3)).thenReturn(
            get_teacher(Teacher(1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
                Gender.MALE, 12), 0))
        Mockito.`when`(mockTeacherRepository.DeleteTeacher(request_delete_3)).thenReturn(3)
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.DeleteTeacher(request_delete_3)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun updateTeacher_OK() {
        // test 1. OK
        val request_get_1 = GetTeacherRequest(1)
        val request_update_1 = UpdateTeacherRequest(1)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_1)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        Mockito.`when`(mockTeacherRepository.UpdateTeacher(request_update_1)).thenReturn(0)
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.UpdateTeacher(request_update_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun updateTeacher_TeacherError() {
        // test 2. Get teacher error
        val request_get_2 = GetTeacherRequest(1)
        val request_update_2 = UpdateTeacherRequest(1)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_2)).thenReturn(
            get_teacher(null, 3)
        )
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.UpdateTeacher(request_update_2)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun updateTeacher_BirtdayError() {
        // test 4. Late birthday
        val request_get_4 = GetTeacherRequest(1)
        val request_update_4 = UpdateTeacherRequest(1, Birthday = Date.valueOf("2034-11-13"))

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_4)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "9743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.UpdateTeacher(request_update_4)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun updateTeacher_Error() {
        // test 5. Update error
        val request_get_5 = GetTeacherRequest(1)
        val request_update_5 = UpdateTeacherRequest(1)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_5)).thenReturn(
            get_teacher(Teacher(1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                Gender.MALE, 12), 0))
        Mockito.`when`(mockTeacherRepository.UpdateTeacher(request_update_5)).thenReturn(3)
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.UpdateTeacher(request_update_5)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun singInTeacher_OK() {
        // test 1. OK
        val request_singin_1 = SingInTeacherRequest("123", "123aa")
        Mockito.`when`(mockTeacherRepository.SingInTeacher(request_singin_1)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.SingInTeacher(request_singin_1).error
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun singInTeacher_Error() {
        //test 2. Error
        val request_singin_2 = SingInTeacherRequest("1213", "123aa")
        Mockito.`when`(mockTeacherRepository.SingInTeacher(request_singin_2)).thenReturn(get_teacher(null,3))
        val sut = TeacherService(Logger(), mockTeacherRepository, mockSubjectRepository, mockRoleRepository)

        val actual = sut.SingInTeacher(request_singin_2).error
        val expected = 3
        assertEquals(expected, actual)
    }
}