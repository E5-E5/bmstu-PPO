package internal.storage

import internal.service.dto.*
import model.Schedule

data class get_schedule(val res_Schedule: List<Schedule>, val error: Int)

interface ScheduleStorage {
    fun CreateSchedule(request: CreateScheduleRequest): Int
    fun GetScheduleForTeacher(request: GetScheduleForTeacherRequest): get_schedule
    fun GetScheduleForClass(request: GetScheduleForClassRequest): get_schedule
    fun DeleteSchedule(request: DeleteScheduleRequest): Int
}