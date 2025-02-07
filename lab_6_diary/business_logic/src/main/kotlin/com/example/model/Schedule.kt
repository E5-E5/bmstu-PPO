package com.example.model

import java.time.DayOfWeek

enum class GroupSchedule {
    FIRST, SECOND, JOINT
}

data class Schedule (
    var DayOfWeek: DayOfWeek,
    var LessonNumber: Int,
    var Group: GroupSchedule,
    var ClassId: Int,
    var SubjectId: Int,
    var TeacherId: Int
)