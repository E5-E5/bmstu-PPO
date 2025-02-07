@file:Suppress("ktlint:standard:no-wildcard-imports")
package com.example.service

import com.example.logging.Log4jLogger
import com.example.service.dto.*
import com.example.repository_interface.*
import com.example.logger.Logger
import com.example.model.Count
import com.example.model.Schedule
import com.example.model.Subject
import com.example.*
import com.example.model.GroupSchedule

interface IScheduleService {
    fun CreateSchedule(request: CreateScheduleRequest): Int
    fun GetScheduleForTeacher(request: GetScheduleForTeacherRequest): get_schedule
    fun GetScheduleForClass(request: GetScheduleForClassRequest): get_schedule
    fun DeleteSchedule(request: DeleteScheduleRequest): Int
}

class ScheduleService(
    private val log: Logger,
    private val repository: ScheduleStorage,
    private val rep_class: ClassStorage,
    private val rep_subject: SubjectStorage,
    private val rep_teacher: TeacherStorage
): IScheduleService {
    private val res_error = 3
    private val res_successful = 0
    private val max_count_lessons = 8

    private val logger = Log4jLogger(ScheduleService::class.java)

    override fun GetScheduleForTeacher(request: GetScheduleForTeacherRequest) : get_schedule {
        if(rep_teacher.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful)
        {
            logger.warn("Error while get teacher")
            return get_schedule(listOf<Schedule>(), res_error)
        }

        try {
            val (res_schedule, error) = repository.GetScheduleForTeacher(request)
            logger.info("Schedule was got. ID teacher ${request.TeacherId}")
            return get_schedule(res_schedule, error)
        }
        catch (e: ConnectBDException) {
            logger.warn("Error sql request")
            throw e
        } catch (e: Exception) {
            logger.warn("Error while getting schedule for teacher")
            throw e
        }
    }

    override fun GetScheduleForClass(request: GetScheduleForClassRequest) : get_schedule {
        if(rep_class.GetClassById(GetClassByIdRequest(request.ClassId)).error != res_successful)
        {
            logger.warn("Error while get class")
            return get_schedule(listOf<Schedule>(), res_error)
        }

        try {
            val (res_schedule, error) = repository.GetScheduleForClass(request)
            logger.info("Schedule was got. ID class ${request.ClassId}")
            return get_schedule(res_schedule, error)
        }
        catch (e: ConnectBDException) {
            logger.warn("Error sql request")
            throw e
        } catch (e: Exception) {
            logger.info("Error while getting schedule for class")
            throw e
        }
    }

    private fun CheckClassAlreadyHaveLesson(res_class_schedule: List<Schedule>, request: CreateScheduleRequest): Int {
        if(res_class_schedule.filter { it.DayOfWeek == request.DayWeek && it.LessonNumber == request.LessonNumber
                    && it.Group == request.Group}.isNotEmpty())
            return res_error
        if(res_class_schedule.filter { it.DayOfWeek == request.DayWeek && it.LessonNumber ==
                    request.LessonNumber}.isNotEmpty() && request.Group == GroupSchedule.JOINT)
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
        try {
            val res_teacher_schedule = GetScheduleForTeacher(GetScheduleForTeacherRequest(request.TeacherId)).res_Schedule
//        if(error_teacher != res_successful)
//            throw TeacherHaveScheduleException()
            if (rep_class.GetClassById(GetClassByIdRequest(request.ClassId)).res_Class == null) {
                logger.warn("Error while get class")
                throw GetClassException()
            }
            if (res_teacher_schedule.filter { it.DayOfWeek == request.DayWeek && it.LessonNumber == request.LessonNumber }
                    .isNotEmpty()) {
                logger.warn("Error while check teacher subject in schedule")
                throw TeacherAlreadyHaveLessonException()
            }

            val teachers = rep_teacher.GetTeacherBySubject(GetTeacherBySubjectRequest(request.SubjectId))
            if (teachers.filter { it.TeacherId == request.TeacherId }.isEmpty()) {
                logger.warn("Error while check teacher subject")
                throw TeacherNotTeachSubjectException()
            }

            if (CheckCorrectNewScheduleForClass(request) != res_successful) {
                logger.warn("Error while check schedule for class")
                throw CreateScheduleException()
            }
            val error = repository.CreateSchedule(request)
            logger.info("Schedule was created. ID class ${request.ClassId}, teacher ${request.TeacherId}")
            return error
        } catch (e: Exception) {
            logger.info("Error while creating schedule")
            throw e
        }
    }

    override fun DeleteSchedule(request: DeleteScheduleRequest) : Int {
        val (res_class_schedule, error_class) = GetScheduleForClass(GetScheduleForClassRequest(request.ClassId))
        if(error_class != res_successful) {
            logger.warn("Error while delete schedule")
            throw ClassHaveScheduleException()
        }


        if(res_class_schedule.filter { it.DayOfWeek == request.DayWeek && it.LessonNumber == request.LessonNumber }.isEmpty()) {
            logger.warn("Error while delete schedule")
            throw ClassNotHaveLessonException()
        }
        val error = repository.DeleteSchedule(request)
        logger.info("Schedule was deleted. ID class ${request.ClassId}, day ${request.DayWeek}")

        return error
    }
}