package com.example.repository_interface

import com.example.service.dto.*
import com.example.model.Teacher

data class get_teacher(val res_Teacher: Teacher?, val error: Int)

interface TeacherStorage {
    fun CreateTeacher(request: CreateTeacherWithRoleRequest): Int
    fun DeleteTeacher(request: DeleteTeacherRequest): Int
    fun GetTeacher(request: GetTeacherRequest): get_teacher
    fun UpdateTeacher(request: UpdateTeacherRequest): Int
    fun SingInTeacher(request: SingInTeacherRequest): get_teacher
    fun AddSubjectToTeacher(request: AddSubjectToTeacherRequest): Int
    fun DeleteSubjectFromTeacher(request: DeleteSubjectFromTeacherRequest): Int
    fun GetTeacherBySubject(request: GetTeacherBySubjectRequest): List<Teacher>
}
