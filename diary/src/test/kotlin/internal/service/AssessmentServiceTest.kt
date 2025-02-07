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

class AssessmentServiceTest {
    private val mockAssessmentRepository = Mockito.mock(AssessmentStorage::class.java)
    private val mockStudentRepository = Mockito.mock(StudentStorage::class.java)
    private val mockTeacherRepository = Mockito.mock(TeacherStorage::class.java)
    private val mockSubjectRepository = Mockito.mock(SubjectStorage::class.java)
    private val mockScheduleRepository = Mockito.mock(ScheduleStorage::class.java)

    private val allAssessment = listOf(
        Assessment(1, 1,4, StudentAssessment.TWO, Date.valueOf("2023-11-13")),
        Assessment(1, 2,5, StudentAssessment.FIVE, Date.valueOf("2023-11-24")),
        Assessment(2, 1,3, StudentAssessment.FOUR, Date.valueOf("2023-11-14")),
        Assessment(1, 1,4, StudentAssessment.THREE, Date.valueOf("2023-11-15"))
    )

    @Test
    fun getStudentAssessments_OK() {
        // test 1. OK
        val request_get_1 = GetAssessmentsRequest(2, 1, Date.valueOf("2023-11-12"), Date.valueOf("2023-12-12"))
        val request_get_student_1 = GetStudentRequest(2)
        val request_get_subject_1 = GetSubjectRequestById(1)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_1)).thenReturn(
            get_subject(Subject(1, "Temp", Count.ONE), 0)
        )
        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_1)).thenReturn(
            get_assessment(allAssessment.filter {
                it.StudentId == request_get_1.StudentId &&
                        it.SubjectId == request_get_1.SubjectId
            }, 0)
        )
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.GetStudentAssessments(request_get_1).error
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun getStudentAssessments_StudentError() {
        // test 2. Get student error
        val request_get_2 = GetAssessmentsRequest(2, 1, Date.valueOf("2023-11-12"), Date.valueOf("2023-12-12"))
        val request_get_student_2 = GetStudentRequest(2)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_2)).thenReturn(
            get_student(null, 3)
        )
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.GetStudentAssessments(request_get_2).error
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun getStudentAssessments_DateError() {
        // test 4. Date error
        val request_get_4 = GetAssessmentsRequest(2, 1, Date.valueOf("2023-12-12"), Date.valueOf("2023-11-12"))
        val request_get_student_4 = GetStudentRequest(2)
        val request_get_subject_4 = GetSubjectRequestById(1)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_4)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_4)).thenReturn(
            get_subject(Subject(1, "TEamp", Count.TWO), 3)
        )
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.GetStudentAssessments(request_get_4).error
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun getStudentAssessments_Error() {
        // test 5. Get error
        val request_get_5 = GetAssessmentsRequest(2, 1, Date.valueOf("2023-12-12"), Date.valueOf("2023-11-12"))
        val request_get_student_5 = GetStudentRequest(2)
        val request_get_subject_5 = GetSubjectRequestById(1)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_5)).thenReturn(
            get_student(Student(2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                Gender.MALE, GroupStudent.FIRST),0))
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_5)).thenReturn(
            get_subject(Subject(1, "TEamp", Count.TWO),3))
        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_5)).thenReturn(
            get_assessment(listOf(),3))
        val sut = AssessmentService(Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository)

        val actual = sut.GetStudentAssessments(request_get_5).error
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun createAssessment_OK() {
        // test 1. OK
        val request_get_1 = GetAssessmentsRequest(2, 1, Date.valueOf("2024-04-01"), Date.valueOf("2024-04-01"))
        val request_get_student_1 = GetStudentRequest(2)
        val request_get_subject_1 = GetSubjectRequestById(1)
        val request_get_teacher_1 = GetTeacherRequest(1)
        val request_get_schedule_1 = GetScheduleForClassRequest(1)
        val request_create_1 = CreateAssessmentRequest(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
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
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_1)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 1)), 0)
        )
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_1)).thenReturn(
            get_subject(Subject(1, "Temp", Count.ONE), 0)
        )
        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_1)).thenReturn(
            get_assessment(listOf(), 3)
        )
        Mockito.`when`(mockAssessmentRepository.CreateAssessment(request_create_1)).thenReturn(0)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.CreateAssessment(request_create_1)
        val expected = 0
        assertEquals(expected, actual)
    }
    @Test
    fun createAssessment_StudentError() {
        // test 2. Get Student error
        val request_get_student_2 = GetStudentRequest(2)
        val request_create_2 = CreateAssessmentRequest(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_2)).thenReturn(
            get_student(null, 3)
        )

        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )
        try {
            sut.CreateAssessment(request_create_2)
        } catch (e: Exception) {
            assertEquals(3, 3)
        }

    }
    @Test
    fun createAssessment_DateError() {
        // test 3. Data error
        val request_get_3 = GetAssessmentsRequest(2, 1, Date.valueOf("2026-04-01"), Date.valueOf("2026-04-01"))
        val request_get_student_3 = GetStudentRequest(2)
        val request_get_subject_3 = GetSubjectRequestById(1)
        val request_get_teacher_3 = GetTeacherRequest(1)
        val request_get_schedule_3 = GetScheduleForClassRequest(1)
        val request_create_3 = CreateAssessmentRequest(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2026-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_3)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_3)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_3)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 1)), 0)
        )
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_3)).thenReturn(
            get_subject(Subject(1, "Temp", Count.ONE), 0)
        )
        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_3)).thenReturn(
            get_assessment(listOf(), 3)
        )
        Mockito.`when`(mockAssessmentRepository.CreateAssessment(request_create_3)).thenReturn(0)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.CreateAssessment(request_create_3)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun createAssessment_TeacherNotInSchedule() {
        // test 4. Teacher not in schedule error
        val request_get_4 = GetAssessmentsRequest(2, 1, Date.valueOf("2024-04-01"), Date.valueOf("2024-04-01"))
        val request_get_student_4 = GetStudentRequest(2)
        val request_get_subject_4 = GetSubjectRequestById(1)
        val request_get_teacher_4 = GetTeacherRequest(1)
        val request_get_schedule_4 = GetScheduleForClassRequest(1)
        val request_create_4 = CreateAssessmentRequest(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_4)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_4)).thenReturn(
            get_teacher(
                Teacher(
                    1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                    Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                    Gender.MALE, 12
                ), 0
            )
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_4)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 2)), 0)
        )
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_4)).thenReturn(
            get_subject(Subject(1, "Temp", Count.ONE), 0)
        )
        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_4)).thenReturn(
            get_assessment(listOf(), 3)
        )
        Mockito.`when`(mockAssessmentRepository.CreateAssessment(request_create_4)).thenReturn(0)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.CreateAssessment(request_create_4)
        val expected = 3
        assertEquals(expected, actual)
    }
    @Test
    fun createAssessment_DayNotInSchedule() {
        // test 5. Day not in schedule
        val request_get_5 = GetAssessmentsRequest(2, 1, Date.valueOf("2024-04-01"), Date.valueOf("2024-04-01"))
        val request_get_student_5 = GetStudentRequest(2)
        val request_get_subject_5 = GetSubjectRequestById(1)
        val request_get_teacher_5 = GetTeacherRequest(1)
        val request_get_schedule_5 = GetScheduleForClassRequest(1)
        val request_create_5 = CreateAssessmentRequest(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_5)).thenReturn(
            get_student(Student(2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                Gender.MALE, GroupStudent.FIRST),0))
        Mockito.`when`(mockTeacherRepository.GetTeacher(request_get_teacher_5)).thenReturn(
            get_teacher(Teacher(1, Date.valueOf("2003-11-13"), "t1", "f_t1", "pat_t1",
                Date.valueOf("2023-11-13"), "12345", "a1s2", "+79743895929",
                Gender.MALE, 12),0)
        )
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_5)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.TUESDAY, 1, GroupSchedule.JOINT, 1, 1, 1)),0)
        )
        Mockito.`when`(mockSubjectRepository.GetSubjectById(request_get_subject_5)).thenReturn(
            get_subject(Subject(1, "Temp", Count.ONE),0))
        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_5)).thenReturn(
            get_assessment(listOf(),3))
        Mockito.`when`(mockAssessmentRepository.CreateAssessment(request_create_5)).thenReturn(0)
        val sut = AssessmentService(Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository)

        val actual = sut.CreateAssessment(request_create_5)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun deleteAssessment_OK() {
        val request_get_1 = GetAssessmentsRequest(2, 1, Date.valueOf("2024-04-01"), Date.valueOf("2024-04-01"))
        val request_get_student_1 = GetStudentRequest(2)
        val request_get_teacher_1 = GetTeacherRequest(1)
        val request_get_schedule_1 = GetScheduleForClassRequest(1)
        val request_delete_1 = DeleteAssessmentRequest(2, 1, 1, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
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
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_1)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 1)), 0)
        )

        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_1)).thenReturn(
            get_assessment(listOf(Assessment(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))), 0)
        )
        Mockito.`when`(mockAssessmentRepository.DeleteAssessment(request_delete_1)).thenReturn(0)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.DeleteAssessment(request_delete_1)
        val expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun deleteAssessment_StudentError() {
        val request_get_student_1 = GetStudentRequest(2)
        val request_delete_1 = DeleteAssessmentRequest(2, 1, 1, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(null, 3))

        Mockito.`when`(mockAssessmentRepository.DeleteAssessment(request_delete_1)).thenReturn(3)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.DeleteAssessment(request_delete_1)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun deleteAssessment_DateError() {
        val request_get_1 = GetAssessmentsRequest(2, 1, Date.valueOf("2024-07-01"), Date.valueOf("2024-07-01"))
        val request_get_student_1 = GetStudentRequest(2)
        val request_get_teacher_1 = GetTeacherRequest(1)
        val request_delete_1 = DeleteAssessmentRequest(2, 1, 1, Date.valueOf("2024-07-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
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
        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_1)).thenReturn(
            get_assessment(listOf(), 3)
        )
        Mockito.`when`(mockAssessmentRepository.DeleteAssessment(request_delete_1)).thenReturn(3)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.DeleteAssessment(request_delete_1)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun deleteAssessment_TeacherNotInSchedule() {
        val request_get_1 = GetAssessmentsRequest(2, 1, Date.valueOf("2024-04-01"), Date.valueOf("2024-04-01"))
        val request_get_student_1 = GetStudentRequest(2)
        val request_get_teacher_1 = GetTeacherRequest(1)
        val request_get_schedule_1 = GetScheduleForClassRequest(1)
        val request_delete_1 = DeleteAssessmentRequest(2, 1, 1, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
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
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_1)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 2)), 0)
        )

        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_1)).thenReturn(
            get_assessment(listOf(Assessment(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))), 0)
        )
        Mockito.`when`(mockAssessmentRepository.DeleteAssessment(request_delete_1)).thenReturn(3)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.DeleteAssessment(request_delete_1)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun updateAssessment_OK() {
        val request_get_1 = GetAssessmentsRequest(2, 1, Date.valueOf("2024-04-01"), Date.valueOf("2024-04-01"))
        val request_get_student_1 = GetStudentRequest(2)
        val request_get_teacher_1 = GetTeacherRequest(1)
        val request_get_schedule_1 = GetScheduleForClassRequest(1)
        val request_Update_1 = UpdateAssessmentRequest(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
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
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_1)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 1)), 0)
        )

        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_1)).thenReturn(
            get_assessment(listOf(Assessment(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))), 0)
        )
        Mockito.`when`(mockAssessmentRepository.UpdateAssessment(request_Update_1)).thenReturn(0)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.UpdateAssessment(request_Update_1)
        val expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun updateAssessment_StudentError() {
        val request_get_student_1 = GetStudentRequest(2)
        val request_Update_1 = UpdateAssessmentRequest(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(null, 3))

        Mockito.`when`(mockAssessmentRepository.UpdateAssessment(request_Update_1)).thenReturn(3)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.UpdateAssessment(request_Update_1)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun updateAssessment_DateError() {
        val request_get_1 = GetAssessmentsRequest(2, 1, Date.valueOf("2024-04-01"), Date.valueOf("2024-04-01"))
        val request_get_student_1 = GetStudentRequest(2)
        val request_get_teacher_1 = GetTeacherRequest(1)
        val request_Update_1 = UpdateAssessmentRequest(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
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
        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_1)).thenReturn(
            get_assessment(listOf(), 3)
        )
        Mockito.`when`(mockAssessmentRepository.UpdateAssessment(request_Update_1)).thenReturn(3)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.UpdateAssessment(request_Update_1)
        val expected = 3
        assertEquals(expected, actual)
    }

    @Test
    fun updateAssessment_TeacherNotInSchedule() {
        val request_get_1 = GetAssessmentsRequest(2, 1, Date.valueOf("2024-04-01"), Date.valueOf("2024-04-01"))
        val request_get_student_1 = GetStudentRequest(2)
        val request_get_teacher_1 = GetTeacherRequest(1)
        val request_get_schedule_1 = GetScheduleForClassRequest(1)
        val request_Update_1 = UpdateAssessmentRequest(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(
                Student(
                    2, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
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
        Mockito.`when`(mockScheduleRepository.GetScheduleForClass(request_get_schedule_1)).thenReturn(
            get_schedule(listOf(Schedule(DayOfWeek.MONDAY, 1, GroupSchedule.JOINT, 1, 1, 2)), 0)
        )

        Mockito.`when`(mockAssessmentRepository.GetStudentAssessments(request_get_1)).thenReturn(
            get_assessment(listOf(Assessment(2, 1, 1, StudentAssessment.ONE, Date.valueOf("2024-04-01"))), 0)
        )
        Mockito.`when`(mockAssessmentRepository.UpdateAssessment(request_Update_1)).thenReturn(3)
        val sut = AssessmentService(
            Logger(), mockAssessmentRepository, mockStudentRepository, mockTeacherRepository,
            mockSubjectRepository, mockScheduleRepository
        )

        val actual = sut.UpdateAssessment(request_Update_1)
        val expected = 3
        assertEquals(expected, actual)
    }
}