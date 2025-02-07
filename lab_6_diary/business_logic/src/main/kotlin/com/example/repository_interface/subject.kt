package com.example.repository_interface

import com.example.service.dto.*
import com.example.model.Subject

data class get_subject(val res_Subject: Subject?, val error: Int)
data class get_subjects(val res_Subjects: List<Subject>, val error: Int)

interface SubjectStorage {
    fun CreateSubject(request: CreateSubjectRequest): Int
    fun GetSubjectById(request: GetSubjectRequestById): get_subject
    fun GetSubjectByName(request: GetSubjectRequestByName): get_subject
    fun GetSubjectsForClass(request: GetSubjectsForClassRequest): get_subjects
    fun DeleteSubject(request: DeleteSubjectRequest): Int
    fun GetSubjects() : List<Subject>
}