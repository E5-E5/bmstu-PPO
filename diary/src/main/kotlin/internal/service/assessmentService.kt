package internal.service

import internal.service.dto.*
import logger.*
import internal.storage.*
import model.*
import org.example.`package`.exceptions.*
import java.time.LocalDate

interface IAssessmentService {
    fun CreateAssessment(request: CreateAssessmentRequest): Int
    fun DeleteAssessment(request: DeleteAssessmentRequest): Int
    fun GetStudentAssessments(request: GetAssessmentsRequest): get_assessment
    fun UpdateAssessment(request: UpdateAssessmentRequest): Int
}

class AssessmentService(
    private val logger: Logger,
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

    override fun GetStudentAssessments(request: GetAssessmentsRequest) : get_assessment {
        if(rep_student.GetStudent(GetStudentRequest(request.StudentId)).error != res_successful)
            return get_assessment(listOf<Assessment>(), res_error)

        if(rep_subject.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful)
            return get_assessment(listOf<Assessment>(), res_error)

        if(request.StartDate.toLocalDate().isAfter(request.FinishDate.toLocalDate().plusDays(difference_between_days)))
            return get_assessment(listOf<Assessment>(), res_error)

        val (res_assessment, error) = repository.GetStudentAssessments(request)
        logger.logMessage(error, "Get: Assessment")

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

        if(rep_teacher.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful)
            throw GetTeacherException()

        if(request.Date.toLocalDate().isAfter(LocalDate.now()))
            throw DataException()

        if(repository.GetStudentAssessments(GetAssessmentsRequest(request.StudentId, request.SubjectId,
                request.Date, request.Date)).res_Assessment.filter { it.Date == request.Date }.isNotEmpty())
            throw CreateAssessmentSameException()

        val res_schedule = rep_schedule.GetScheduleForClass(GetScheduleForClassRequest(res_student.ClassId)).res_Schedule

        if (CheckTeacherTeachSubject(res_schedule, request.SubjectId, request.TeacherId, res_student) == res_error)
            throw TeacherTeachSubjectException()

        if (CheckSubjectInSchedule(res_schedule, request) == res_error)
            throw SubjectInScheduleException()

        val error = repository.CreateAssessment(request)
        logger.logMessage(error, "Create: Assessment")

        return error
    }

    override fun DeleteAssessment(request: DeleteAssessmentRequest): Int {
        val res_student = rep_student.GetStudent(GetStudentRequest(request.StudentId)).res_Student ?: throw GetStudentException()

        if(repository.GetStudentAssessments(GetAssessmentsRequest(request.StudentId, request.SubjectId,
                request.Date, request.Date)).res_Assessment.filter { it.Date == request.Date }.isEmpty())
            throw StudentHaveAssessmentException()

        if(rep_teacher.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful)
            throw GetTeacherException()

        if(request.Date.toLocalDate().isAfter(LocalDate.now()))
            throw DataException()

        val res_schedule = rep_schedule.GetScheduleForClass(GetScheduleForClassRequest(res_student.ClassId)).res_Schedule

        if(CheckTeacherTeachSubject(res_schedule, request.SubjectId, request.TeacherId, res_student) == res_error)
            throw TeacherTeachSubjectException()

        val error = repository.DeleteAssessment(request)
        logger.logMessage(error, "Delete: Assessment")

        return error
    }

    override fun UpdateAssessment(request: UpdateAssessmentRequest): Int {
        val res_student = rep_student.GetStudent(GetStudentRequest(request.StudentId)).res_Student ?: throw GetStudentException()

        if(rep_teacher.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful)
            throw GetTeacherException()

        if(request.Date.toLocalDate().isAfter(LocalDate.now()))
            throw DataException()

        if(repository.GetStudentAssessments(GetAssessmentsRequest(request.StudentId, request.SubjectId,
                request.Date, request.Date)).res_Assessment.filter { it.Date == request.Date }.isEmpty())
            throw StudentHaveAssessmentException()

        val res_schedule = rep_schedule.GetScheduleForClass(GetScheduleForClassRequest(res_student.ClassId)).res_Schedule

        if(CheckTeacherTeachSubject(res_schedule, request.SubjectId, request.TeacherId, res_student) == res_error)
            throw TeacherTeachSubjectException()

        val error = repository.UpdateAssessment(request)
        logger.logMessage(error, "Update: Assessment")

        return error
    }

}

