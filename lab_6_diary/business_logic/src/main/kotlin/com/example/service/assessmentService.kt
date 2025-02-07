package com.example.service

import com.example.logging.Log4jLogger
import com.example.service.dto.*
import com.example.logger.*
import com.example.repository_interface.*
import com.example.model.*
import com.example.*
import java.time.LocalDate

interface IAssessmentService {
    fun CreateAssessment(request: CreateAssessmentRequest): Int
    fun DeleteAssessment(request: DeleteAssessmentRequest): Int
    fun GetStudentAssessments(request: GetAssessmentsRequest): get_assessment
    fun UpdateAssessment(request: UpdateAssessmentRequest): Int
}

class AssessmentService(
    private val log: Logger,
    private val repository: AssessmentStorage,
    private val rep_student: StudentStorage,
    private val rep_teacher: TeacherStorage,
    private val rep_subject: SubjectStorage,
    private val rep_schedule: ScheduleStorage
): IAssessmentService
{
    private val res_error = 3
    private val res_successful = 0
    private val difference_between_days: Long = 1
    private val logger = Log4jLogger(AssessmentService::class.java)

    override fun GetStudentAssessments(request: GetAssessmentsRequest) : get_assessment {
        if(rep_student.GetStudent(GetStudentRequest(request.StudentId)).error != res_successful)
            return get_assessment(listOf<Assessment>(), res_error)

        if(rep_subject.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful)
            return get_assessment(listOf<Assessment>(), res_error)

        if(request.StartDate.toLocalDate().isAfter(request.FinishDate.toLocalDate().plusDays(difference_between_days)))
            return get_assessment(listOf<Assessment>(), res_error)

        val (res_assessment, error) = repository.GetStudentAssessments(request)
        logger.info("Assessment was got")

        return get_assessment(res_assessment, error)
    }

    private fun CheckTeacherTeachSubject(res_schedule: List<Schedule>, SubjectId: Int, TeacherId: Int,
                                         res_student: Student): Int {
        if(res_schedule.filter { it.SubjectId == SubjectId && it.TeacherId == TeacherId &&
                    (it.Group == GroupSchedule.JOINT || it.Group.ordinal == res_student.Group.ordinal)}.isEmpty())
            return res_error

        return res_successful
    }

    private fun CheckSubjectInSchedule(res_schedule: List<Schedule>, request: CreateAssessmentRequest): Int {
        if(res_schedule.filter { it.SubjectId == request.SubjectId &&
                    it.DayOfWeek == request.Date.toLocalDate().dayOfWeek }.isEmpty())
            return res_error

        return res_successful
    }

    override fun CreateAssessment(request: CreateAssessmentRequest): Int {
        val res_student = rep_student.GetStudent(GetStudentRequest(request.StudentId)).res_Student ?: throw GetStudentException()

        if(rep_teacher.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful) {
            logger.warn("Error while get teacher")
            throw GetTeacherException()
        }

        if(request.Date.toLocalDate().isAfter(LocalDate.now())) {
            logger.warn("Error with date")
            throw DataException()
        }

        if(repository.GetStudentAssessments(GetAssessmentsRequest(request.StudentId, request.SubjectId,
                request.Date, request.Date)).res_Assessment.filter { it.Date == request.Date }.isNotEmpty()) {
            logger.warn("Error while get student assessment")
            throw CreateAssessmentSameException()
        }

        val res_schedule = rep_schedule.GetScheduleForClass(GetScheduleForClassRequest(res_student.ClassId)).res_Schedule

        if (CheckTeacherTeachSubject(res_schedule, request.SubjectId, request.TeacherId, res_student) == res_error) {
            logger.warn("Error while check teacher and subject")
            throw TeacherTeachSubjectException()
        }
        if (CheckSubjectInSchedule(res_schedule, request) == res_error) {
            logger.warn("Error while check subject in schedule")
            throw SubjectInScheduleException()
        }

        val error = repository.CreateAssessment(request)
        logger.info("Assessment was created. ID student ${request.StudentId}, teacher ${request.TeacherId}")
        return error
    }

    override fun DeleteAssessment(request: DeleteAssessmentRequest): Int {
        val res_student =
            rep_student.GetStudent(GetStudentRequest(request.StudentId)).res_Student ?: throw GetStudentException()

        if (repository.GetStudentAssessments(
                GetAssessmentsRequest(
                    request.StudentId, request.SubjectId,
                    request.Date, request.Date
                )
            ).res_Assessment.filter { it.Date == request.Date }.isEmpty()
        ) {
            logger.warn("Error while get student assessment")
            throw StudentHaveAssessmentException()
        }

        if (rep_teacher.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful) {
            logger.warn("Error while get teacher")
            throw GetTeacherException()
        }

        if (request.Date.toLocalDate().isAfter(LocalDate.now())) {
            logger.warn("Error with date")
            throw DataException()
        }

        val res_schedule =
            rep_schedule.GetScheduleForClass(GetScheduleForClassRequest(res_student.ClassId)).res_Schedule

        if (CheckTeacherTeachSubject(res_schedule, request.SubjectId, request.TeacherId, res_student) == res_error) {
            logger.warn("Error while check teacher schedule")
            throw TeacherTeachSubjectException()
        }

        val error = repository.DeleteAssessment(request)
        logger.info("Assessment was deleted. ID student ${request.StudentId}, teacher ${request.TeacherId}")

        return error
    }

    override fun UpdateAssessment(request: UpdateAssessmentRequest): Int {
        val res_student = rep_student.GetStudent(GetStudentRequest(request.StudentId)).res_Student ?: throw GetStudentException()

        if (rep_teacher.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful) {
            logger.warn("Error while get teacher")
            throw GetTeacherException()
        }

        if (request.Date.toLocalDate().isAfter(LocalDate.now())) {
            logger.warn("Error with date")
            throw DataException()
        }

        if(repository.GetStudentAssessments(GetAssessmentsRequest(request.StudentId, request.SubjectId,
                request.Date, request.Date)).res_Assessment.filter { it.Date == request.Date }.isEmpty()) {
            logger.warn("Error while check student assessment")
            throw StudentHaveAssessmentException()
        }

        val res_schedule = rep_schedule.GetScheduleForClass(GetScheduleForClassRequest(res_student.ClassId)).res_Schedule

        if (CheckTeacherTeachSubject(res_schedule, request.SubjectId, request.TeacherId, res_student) == res_error) {
            logger.warn("Error while check teacher schedule")
            throw TeacherTeachSubjectException()
        }

        val error = repository.UpdateAssessment(request)
        logger.info("Assessment was updated. ID student ${request.StudentId}, teacher ${request.TeacherId}")

        return error
    }

}

