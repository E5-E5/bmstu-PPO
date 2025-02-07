package com.example.service.dto

import com.example.model.GroupSchedule
import java.time.DayOfWeek

data class CreateScheduleRequest(
    var DayWeek: DayOfWeek,
    var LessonNumber: Int,
    var Group: GroupSchedule,
    var ClassId: Int,
    var SubjectId: Int,
    var TeacherId: Int
)

data class GetScheduleForTeacherRequest(
    var TeacherId: Int
)

data class GetScheduleForClassRequest(
    var ClassId: Int
)

data class DeleteScheduleRequest(
    var DayWeek: DayOfWeek,
    var LessonNumber: Int,
    var ClassId: Int,
)
