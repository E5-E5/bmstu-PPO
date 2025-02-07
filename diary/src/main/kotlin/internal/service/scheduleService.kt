package internal.service

import internal.service.dto.*
import internal.storage.*
import logger.Logger
import model.Count
import model.Schedule
import model.Subject
import org.example.`package`.exceptions.*

interface IScheduleService {
    fun CreateSchedule(request: CreateScheduleRequest): Int
    fun GetScheduleForTeacher(request: GetScheduleForTeacherRequest): get_schedule
    fun GetScheduleForClass(request: GetScheduleForClassRequest): get_schedule
    fun DeleteSchedule(request: DeleteScheduleRequest): Int
}

class ScheduleService(
    private val logger: Logger,
    private val repository: ScheduleStorage,
    private val rep_class: ClassStorage,
    private val rep_subject: SubjectStorage,
    private val rep_teacher: TeacherStorage
): IScheduleService {
    private val res_error = 3
    private val res_successful = 0
    private val max_count_lessons = 8

    override fun GetScheduleForTeacher(request: GetScheduleForTeacherRequest) : get_schedule {
        if(rep_teacher.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful)
            return get_schedule(listOf<Schedule>(), res_error)

        val (res_schedule, error) = repository.GetScheduleForTeacher(request)

        logger.logMessage(error, "Get (for teacher): Schedule")

        return get_schedule(res_schedule, error)
    }

    override fun GetScheduleForClass(request: GetScheduleForClassRequest) : get_schedule {
        if(rep_class.GetClassById(GetClassByIdRequest(request.ClassId)).error != res_successful)
            return get_schedule(listOf<Schedule>(), res_error)

        val (res_schedule, error) = repository.GetScheduleForClass(request)

        logger.logMessage(error,"Get (for class): Schedule")

        return get_schedule(res_schedule, error)
    }

    private fun CheckClassAlreadyHaveLesson(res_class_schedule: List<Schedule>, request: CreateScheduleRequest): Int {
        if(res_class_schedule.filter { it.DayOfWeek == request.DayWeek && it.LessonNumber == request.LessonNumber
                    && it.Group == request.Group}.isNotEmpty())
            return res_error
        return res_successful
    }

    private fun CheckClassAlreadyHaveOneTeacherOnSubject(get_subject: Subject, res_class_schedule: List<Schedule>, request: CreateScheduleRequest): Int {
        if (get_subject.CountTeachers == Count.ONE &&
            res_class_schedule.filter { it.SubjectId == request.SubjectId && it.TeacherId == request.TeacherId }.isEmpty())
            return res_error
        return res_successful
    }

    private fun CheckClassAlreadyHaveTwoTeacherOnSubject(get_subject: Subject, res_class_schedule: List<Schedule>,
                                                         request: CreateScheduleRequest): Int {
        if (get_subject.CountTeachers == Count.TWO &&
            res_class_schedule.filter { it.SubjectId == request.SubjectId }.map { it.TeacherId }.size != 1 &&
            res_class_schedule.filter { it.SubjectId == request.SubjectId }.map { it.TeacherId }.find {
                it == request.TeacherId } == null)
            return res_error
        return res_successful
    }

    private fun CheckCorrectNewScheduleForClass(request: CreateScheduleRequest): Int {
        val res_class_schedule = GetScheduleForClass(GetScheduleForClassRequest(request.ClassId)).res_Schedule
        if(res_class_schedule.filter { it.DayOfWeek == request.DayWeek }.size >= max_count_lessons)
            throw MaxCountSubjectException()

        if(CheckClassAlreadyHaveLesson(res_class_schedule, request) == res_error)
            throw ClassAlreadyHaveLessonException()

        val get_subject = rep_subject.GetSubjectById(GetSubjectRequestById(request.SubjectId)).res_Subject
        if(get_subject == null)
            return res_error

        if(res_class_schedule.filter { it.SubjectId == request.SubjectId }.isNotEmpty()) {
            if (CheckClassAlreadyHaveOneTeacherOnSubject(get_subject, res_class_schedule, request) == res_error)
                throw TeacherOnSubjectException()

            if (CheckClassAlreadyHaveTwoTeacherOnSubject(get_subject, res_class_schedule, request) == res_error)
                throw TeacherOnSubjectException()
        }

        return res_successful
    }

    override fun CreateSchedule(request: CreateScheduleRequest) : Int {
        val (res_teacher_schedule, error_teacher) = GetScheduleForTeacher(GetScheduleForTeacherRequest(request.TeacherId))
//        if(error_teacher != res_successful)
//            throw TeacherHaveScheduleException()
        if (rep_class.GetClassById(GetClassByIdRequest(request.ClassId)).res_Class == null)
            throw GetClassException()

        if(res_teacher_schedule.filter { it.DayOfWeek == request.DayWeek && it.LessonNumber == request.LessonNumber }.isNotEmpty())
            throw TeacherAlreadyHaveLessonException()

        val teachers = rep_teacher.GetTeacherBySubject(GetTeacherBySubjectRequest(request.SubjectId))
        if(teachers.filter { it.TeacherId == request.TeacherId }.isEmpty())
            throw TeacherNotTeachSubjectException()

        if(CheckCorrectNewScheduleForClass(request) != res_successful)
            throw CreateScheduleException()

        val error = repository.CreateSchedule(request)

        logger.logMessage(error,"Create: Schedule")

        return error
    }

    override fun DeleteSchedule(request: DeleteScheduleRequest) : Int {
        val (res_class_schedule, error_class) = GetScheduleForClass(GetScheduleForClassRequest(request.ClassId))
        if(error_class != res_successful)
            throw ClassHaveScheduleException()

        if(res_class_schedule.filter { it.DayOfWeek == request.DayWeek && it.LessonNumber == request.LessonNumber }.isEmpty())
            throw ClassNotHaveLessonException()

        val error = repository.DeleteSchedule(request)
        logger.logMessage(error, "Delete: Schedule")

        return error
    }
}