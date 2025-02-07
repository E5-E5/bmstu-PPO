package internal.service

import internal.service.dto.*
import logger.*
import internal.storage.*
import model.Subject
import org.example.`package`.exceptions.*

interface ISubjectService {
    fun CreateSubject(request: CreateSubjectRequest): Int
    fun GetSubject(request: GetSubjectRequestById): get_subject
    fun GetSubjectsForClass(request: GetSubjectsForClassRequest): get_subjects
    fun DeleteSubject(request: DeleteSubjectRequest): Int
    fun GetSubjects() : List<Subject>
}

class SubjectService(
    private val logger: Logger,
    private val repository: SubjectStorage
): ISubjectService
{
    val res_error = 3
    val res_successful = 0

    override fun GetSubject(request: GetSubjectRequestById) : get_subject {
        val (res_subject, error) = repository.GetSubjectById(request)

        logger.logMessage(error, "Get: Subject")

        return get_subject(res_subject, error)
    }

    override fun GetSubjects() : List<Subject> {
        val subjects = repository.GetSubjects()

        return subjects
    }

    override fun GetSubjectsForClass(request: GetSubjectsForClassRequest): get_subjects {
        val (res_subjects, error) = repository.GetSubjectsForClass(request)

        logger.logMessage(error, "Get: Subject")

        return get_subjects(res_subjects, error)
    }


    override fun CreateSubject(request: CreateSubjectRequest): Int {
        if(repository.GetSubjectByName(GetSubjectRequestByName(request.Name)).error == res_successful)
            throw CreateSameSubjectException()

        val error = repository.CreateSubject(request)

        logger.logMessage(error,"Create: Subject")
        return error
    }

    override fun DeleteSubject(request: DeleteSubjectRequest): Int {
        if(repository.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful)
            throw GetSubjectException()

        val error = repository.DeleteSubject(request)

        logger.logMessage(error, "Delete: Subject")
        return error
    }

}