package com.example.service

import com.example.logging.Log4jLogger
import com.example.service.dto.*
import com.example.logger.*
import com.example.repository_interface.*
import com.example.model.Subject
import com.example.*

interface ISubjectService {
    fun CreateSubject(request: CreateSubjectRequest): Int
    fun GetSubject(request: GetSubjectRequestById): get_subject
    fun GetSubjectsForClass(request: GetSubjectsForClassRequest): get_subjects
    fun DeleteSubject(request: DeleteSubjectRequest): Int
    fun GetSubjects() : List<Subject>
}

class SubjectService(
    private val log: Logger,
    private val repository: SubjectStorage
): ISubjectService
{
    val res_error = 3
    val res_successful = 0
    private val logger = Log4jLogger(SubjectService::class.java)

    override fun GetSubject(request: GetSubjectRequestById) : get_subject {
        return try {
            val (res_subject, error) = repository.GetSubjectById(request)
            logger.info("Subject was got. ID subject ${request.SubjectId}")
            get_subject(res_subject, error)
        } catch (e: Exception) {
            logger.warn("Error while get subject")
            throw Exception("Error while get subject")
        }
    }

    override fun GetSubjects() : List<Subject> {
        return try {
            val subjects = repository.GetSubjects()
            logger.info("Subjects were got")
            subjects
        } catch (e: Exception){
            logger.warn("Error while get subjects")
            throw Exception("Error while get subjects")
        }
    }

    override fun GetSubjectsForClass(request: GetSubjectsForClassRequest): get_subjects {
        return try {
            val (res_subjects, error) = repository.GetSubjectsForClass(request)
            logger.info("Subjects for class were got. ID class ${request.ClassId}")
            get_subjects(res_subjects, error)
        } catch (e: Exception) {
            logger.warn("Error while get subjects for class")
            throw Exception("Error while get subjects for class")
        }
    }


    override fun CreateSubject(request: CreateSubjectRequest): Int {
        if(repository.GetSubjectByName(GetSubjectRequestByName(request.Name)).error == res_successful) {
            logger.warn("Error while get subject by name")
            throw CreateSameSubjectException()
        }

        val error = repository.CreateSubject(request)
        logger.info("Subject was created. ${request.Name}")
        return error
    }

    override fun DeleteSubject(request: DeleteSubjectRequest): Int {
        if(repository.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful) {
            logger.warn("Error while get subject by ID")
            throw GetSubjectException()
        }

        val error = repository.DeleteSubject(request)
        logger.info("Subject was deleted. ID subject ${request.SubjectId}")
        return error
    }
}