package internal.service

import internal.service.dto.*
import logger.*
import internal.storage.*
import model.Class
import org.example.`package`.exceptions.*

interface IClassService {
    fun CreateClass(request: CreateClassRequest): Int
    fun GetClassById(request: GetClassByIdRequest): get_class
    fun DeleteClass(request: DeleteClassRequest): Int
    fun GetAllClasses() : List<Class>
}

class ClassService(
    private val logger: Logger,
    private val repository: ClassStorage
): IClassService
{
    val res_error = 3
    val res_successful = 0

    override fun GetClassById(request: GetClassByIdRequest) : get_class {
        val (res_class, error) = repository.GetClassById(request)

        logger.logMessage(error, "Get: Class")

        return get_class(res_class, error)
    }

    override fun GetAllClasses() : List<Class> {
        val classes = repository.GetAllClasses()

        return classes
    }

    override fun CreateClass(request: CreateClassRequest): Int {
        if(repository.GetClassByName(GetClassByNameRequest(request.Letter, request.Number)).error == res_successful)
            throw ClassExistException()

        var error = repository.CreateClass(request)
        logger.logMessage(error, "Create: Class")

        return error
    }

    override fun DeleteClass(request: DeleteClassRequest): Int {
        if(repository.GetClassById(GetClassByIdRequest(request.ClassId)).error != res_successful)
            throw GetClassException()

        val error = repository.DeleteClass(request)
        logger.logMessage(error, "Delete: Class")

        return error
    }
}

