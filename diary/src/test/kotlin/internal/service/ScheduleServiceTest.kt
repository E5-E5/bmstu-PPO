package internal.service

import internal.service.dto.*
import internal.storage.*
import logger.Logger
import model.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.sql.Date
import java.time.DayOfWeek

class ScheduleServiceTest {
    private val mockScheduleRepository = Mockito.mock(ScheduleStorage::class.java)
    private val mockClassRepository = Mockito.mock(ClassStorage::class.java)
    private val mockSubjectRepository = Mockito.mock(SubjectStorage::class.java)
    private val mockTeacherRepository = Mockito.mock(TeacherStorage::class.java)

    @Test
    fun getScheduleForTeacher_OK() {
        val request_get_teacher_1 = GetTeacherRequest(1)
        val request_get_schedule_teacher_1 = GetScheduleForTeacherRequest(request_get_teacher_1.TeacherId)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_1)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForTeacher(request_get_schedule_teacher_1)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 1)), 0)
        )

        val expected = 0

        val sut = ScheduleService(
            Logger(),
            mockScheduleRepository,
            mockClassRepository,
            mockSubjectRepository,
            mockTeacherRepository
        )
        val actual = sut.GetScheduleForTeacher(request_get_schedule_teacher_1).error
        assertEquals(expected, actual)

    }
    @Test
    fun createSchedule_OK() {
        //test 1. OK (one teacher)
        val request_create_1 = CreateScheduleRequest(DayOfWeek.MONDAY, 2, GroupSchedule.JOINT, 1, 1, 1)
        val request_get_schedule_class_1 = GetScheduleForClassRequest(request_create_1.ClassId)
        val request_get_schedule_teacher_1 = GetScheduleForTeacherRequest(request_create_1.TeacherId)
        val request_get_class_1 = GetClassByIdRequest(request_create_1.ClassId)
        val request_get_teacher_1 = GetTeacherRequest(request_create_1.TeacherId)
        val request_get_subject = GetSubjectRequestById(request_create_1.SubjectId)

        Mockito.`when`(mockClassRepository.GetClassById(request_get_class_1)).thenReturn(
            get_class(
                Class(
                    1,
                    "e", ClassNumber.ONE
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_class_1)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 1)), 0)
        )
        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_1)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForTeacher(request_get_schedule_teacher_1)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 1)), 0)
        )
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject)).thenReturn(
            get_subject(
                Subject(
                    1,
                    "Temp", Count.ONE
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.CreateSchedule(request_create_1)).thenReturn(0)

        val expected = 0

        val sut = ScheduleService(
            Logger(),
            mockScheduleRepository,
            mockClassRepository,
            mockSubjectRepository,
            mockTeacherRepository
        )
        val actual = sut.CreateSchedule(request_create_1)
        assertEquals(expected, actual)
    }
    @Test
    fun createSchedule_OKTwoTeachers() {
        //test 2. OK (two teacher)
        val request_create_2 = CreateScheduleRequest(DayOfWeek.MONDAY, 2, GroupSchedule.FIRST, 1, 1, 1)
        val request_get_schedule_class_2 = GetScheduleForClassRequest(request_create_2.ClassId)
        val request_get_schedule_teacher_2 = GetScheduleForTeacherRequest(request_create_2.TeacherId)
        val request_get_class_2 = GetClassByIdRequest(request_create_2.ClassId)
        val request_get_teacher_2 = GetTeacherRequest(request_create_2.TeacherId)
        val request_get_subject_2 = GetSubjectRequestById(request_create_2.SubjectId)

        Mockito.`when`(mockClassRepository.GetClassById(request_get_class_2)).thenReturn(
            get_class(
                Class(
                    1,
                    "e", ClassNumber.ONE
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_class_2)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.SECOND, 1, 1, 1)), 0)
        )
        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_2)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForTeacher(request_get_schedule_teacher_2)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.SECOND, 1, 1, 1)), 0)
        )
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_2)).thenReturn(
            get_subject(
                Subject(
                    1,
                    "Temp", Count.TWO
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.CreateSchedule(request_create_2)).thenReturn(0)

        val expected = 0

        val sut = ScheduleService(
            Logger(),
            mockScheduleRepository,
            mockClassRepository,
            mockSubjectRepository,
            mockTeacherRepository
        )
        val actual = sut.CreateSchedule(request_create_2)
        assertEquals(expected, actual)
    }
    @Test
    fun createSchedule_8LessonsError() {
        //test 3. 8 lessons
        val request_create_3 = CreateScheduleRequest(DayOfWeek.MONDAY, 10, GroupSchedule.FIRST, 1, 1, 1)
        val request_get_schedule_class_3 = GetScheduleForClassRequest(request_create_3.ClassId)
        val request_get_schedule_teather_3 = GetScheduleForTeacherRequest(request_create_3.TeacherId)
        val request_get_class_3 = GetClassByIdRequest(request_create_3.ClassId)
        val request_get_teacher_3 = GetTeacherRequest(request_create_3.TeacherId)

        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_3)).thenReturn(get_teacher(Teacher(
            1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
            Gender.MALE, 12), 0))
        Mockito.`when`(mockClassRepository.GetClassById(request_get_class_3)).thenReturn(
            get_class(
                Class(
                    1,
                    "e", ClassNumber.ONE
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_class_3)).thenReturn(
            get_schedule(
                listOf(
                    Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.SECOND, 1, 1, 1),
                    Schedule(DayOfWeek.MONDAY, 2, GroupSchedule.SECOND, 1, 1, 1),
                    Schedule(DayOfWeek.MONDAY, 3, GroupSchedule.SECOND, 1, 1, 1),
                    Schedule(DayOfWeek.MONDAY, 4, GroupSchedule.SECOND, 1, 1, 1),
                    Schedule(DayOfWeek.MONDAY, 5, GroupSchedule.SECOND, 1, 1, 1),
                    Schedule(DayOfWeek.MONDAY, 6, GroupSchedule.SECOND, 1, 1, 1),
                    Schedule(DayOfWeek.MONDAY, 7, GroupSchedule.SECOND, 1, 1, 1),
                    Schedule(DayOfWeek.MONDAY, 8, GroupSchedule.SECOND, 1, 1, 1)
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForTeacher(request_get_schedule_teather_3)).thenReturn(
            get_schedule(
                listOf(
                    Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.SECOND, 1, 1, 1),
                ), 0
            )
        )


        val expected = 3

        val sut = ScheduleService(
            Logger(),
            mockScheduleRepository,
            mockClassRepository,
            mockSubjectRepository,
            mockTeacherRepository
        )
        val actual = sut.CreateSchedule(request_create_3)
        assertEquals(expected, actual)
    }
    @Test
    fun createSchedule_ClassHaveThisLesson() {
        //test 4. Lesson (class) is already there
        val request_create_4 = CreateScheduleRequest(DayOfWeek.MONDAY, 1, GroupSchedule.FIRST, 1, 1, 1)
        val request_get_schedule_class_4 = GetScheduleForClassRequest(request_create_4.ClassId)
        val request_get_class_4 = GetClassByIdRequest(request_create_4.ClassId)
        val request_get_teacher_4 = GetTeacherRequest(request_create_4.TeacherId)
        val request_get_schedule_teather_4 = GetScheduleForTeacherRequest(request_create_4.TeacherId)


        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_4)).thenReturn(get_teacher(Teacher(
            1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
            Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
            Gender.MALE, 12), 0))
        Mockito.`when`(mockScheduleRepository.GetScheduleForTeacher(request_get_schedule_teather_4)).thenReturn(
            get_schedule(
                listOf(
                    Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.SECOND, 1, 1, 1),
                ), 0
            )
        )
        Mockito.`when`(mockClassRepository.GetClassById(request_get_class_4)).thenReturn(
            get_class(
                Class(
                    1,
                    "e", ClassNumber.ONE
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_class_4)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.SECOND, 1, 1, 1)), 0)
        )


        val expected = 3

        val sut = ScheduleService(
            Logger(),
            mockScheduleRepository,
            mockClassRepository,
            mockSubjectRepository,
            mockTeacherRepository
        )
        val actual = sut.CreateSchedule(request_create_4)
        assertEquals(expected, actual)
    }
    @Test
    fun createSchedule_TeacherHaveThisLesson() {
        //test 5. Lesson (teacher) is already there
        val request_create_5 = CreateScheduleRequest(DayOfWeek.MONDAY, 2, GroupSchedule.FIRST, 1, 1, 1)
        val request_get_schedule_class_5 = GetScheduleForClassRequest(request_create_5.ClassId)
        val request_get_schedule_teacher_5 = GetScheduleForTeacherRequest(request_create_5.TeacherId)
        val request_get_class_5 = GetClassByIdRequest(request_create_5.ClassId)
        val request_get_teacher_5 = GetTeacherRequest(request_create_5.TeacherId)

        Mockito.`when`(mockClassRepository.GetClassById(request_get_class_5)).thenReturn(
            get_class(
                Class(
                    1,
                    "e", ClassNumber.ONE
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_class_5)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.SECOND, 1, 1, 1)), 0)
        )
        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_5)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForTeacher(request_get_schedule_teacher_5)).thenReturn(
            get_schedule(
                listOf(
                    Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.SECOND, 1, 1, 1),
                    Schedule(DayOfWeek.MONDAY, 2, GroupSchedule.SECOND, 1, 1, 1)
                ), 0
            )
        )

        val expected = 3

        val sut = ScheduleService(
            Logger(),
            mockScheduleRepository,
            mockClassRepository,
            mockSubjectRepository,
            mockTeacherRepository
        )
        val actual = sut.CreateSchedule(request_create_5)
        assertEquals(expected, actual)
    }
    @Test
    fun createSchedule_AnotherTeacherError() {
        //test 6. Another teacher (one)
        val request_create_6 = CreateScheduleRequest(DayOfWeek.MONDAY, 2, GroupSchedule.JOINT, 1, 1, 2)
        val request_get_schedule_class_6 = GetScheduleForClassRequest(request_create_6.ClassId)
        val request_get_schedule_teacher_6 = GetScheduleForTeacherRequest(request_create_6.TeacherId)
        val request_get_class_6 = GetClassByIdRequest(request_create_6.ClassId)
        val request_get_teacher_6 = GetTeacherRequest(request_create_6.TeacherId)
        val request_get_subject_6 = GetSubjectRequestById(request_create_6.SubjectId)

        Mockito.`when`(mockClassRepository.GetClassById(request_get_class_6)).thenReturn(
            get_class(
                Class(
                    1,
                    "e", ClassNumber.ONE
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_class_6)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 1)), 0)
        )
        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_6)).thenReturn(
            get_teacher(
                Teacher(
                    2, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"),  "12345", "a1s2", "+79743895929",
                    Gender.MALE, 13
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForTeacher(request_get_schedule_teacher_6)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 2)), 0)
        )
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_6)).thenReturn(
            get_subject(
                Subject(
                    1,
                    "Temp", Count.ONE
                ), 0
            )
        )

        val expected = 3

        val sut = ScheduleService(
            Logger(),
            mockScheduleRepository,
            mockClassRepository,
            mockSubjectRepository,
            mockTeacherRepository
        )
        val actual = sut.CreateSchedule(request_create_6)
        assertEquals(expected, actual)
    }
    @Test
    fun createSchedule_AnotherTeacherTwo() {
        //test 7. Another teacher (two)
        val request_create_7 = CreateScheduleRequest(DayOfWeek.MONDAY, 2, GroupSchedule.FIRST,1, 1, 3)
        val request_get_schedule_class_7 = GetScheduleForClassRequest(request_create_7.ClassId)
        val request_get_schedule_teacher_7 = GetScheduleForTeacherRequest(request_create_7.TeacherId)
        val request_get_class_7 = GetClassByIdRequest(request_create_7.ClassId)
        val request_get_teacher_7 = GetTeacherRequest(request_create_7.TeacherId)
        val request_get_subject_7 = GetSubjectRequestById(request_create_7.SubjectId)

        Mockito.`when`(mockClassRepository.GetClassById(request_get_class_7)).thenReturn(get_class(Class(1,
            "e", ClassNumber.ONE),0))
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_class_7)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.FIRST, 1, 1, 1),
                Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.SECOND, 1, 1, 2)),0))
        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_7)).thenReturn(get_teacher(
            Teacher(2, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                Gender.MALE, 13),0))
        Mockito.`when`(mockScheduleRepository.GetScheduleForTeacher(request_get_schedule_teacher_7)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.FIRST, 1, 1, 2)),0))
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_7)).thenReturn(get_subject(Subject(1,
            "Temp", Count.TWO),0))

        val expected = 3

        val sut = ScheduleService(Logger(), mockScheduleRepository, mockClassRepository, mockSubjectRepository, mockTeacherRepository)
        val actual = sut.CreateSchedule(request_create_7)
        assertEquals(expected, actual)

    }
}