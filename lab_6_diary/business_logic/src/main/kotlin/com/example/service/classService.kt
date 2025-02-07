package com.example.service

import com.example.logging.Log4jLogger
import com.example.service.dto.*
import com.example.logger.*
import com.example.repository_interface.*
import com.example.model.Class
import com.example.*

interface IClassService {
    fun CreateClass(request: CreateClassRequest): Int
    fun GetClassById(request: GetClassByIdRequest): get_class
    fun DeleteClass(request: DeleteClassRequest): Int
    fun GetAllClasses() : List<Class>
}

class ClassService(
    private val log: Logger,
    private val repository: ClassStorage
): IClassService
{
    val res_error = 3
    val res_successful = 0
    private val logger = Log4jLogger(ClassService::class.java)

    override fun GetClassById(request: GetClassByIdRequest) : get_class {
        return try {
            val (res_class, error) = repository.GetClassById(request)
            logger.info("Class was got. ID class ${request.ClassId}")
            get_class(res_class, error)
        } catch (e: Exception) {
            logger.warn("Error while get class")
            throw Exception("Error while get class")
        }
    }

    override fun GetAllClasses() : List<Class> {
        val classes = repository.GetAllClasses()
        logger.info("Successfully retrieved all classes.")
        return classes
    }

    override fun CreateClass(request: CreateClassRequest): Int {
        if (repository.GetClassByName(GetClassByNameRequest(request.Letter, request.Number)).error == res_successful) {
            logger.warn("Class already exists")
            throw ClassExistException()
        }

        var error = repository.CreateClass(request)
        logger.info("Class was created. ${request.Number}${request.Letter}")

        return error
    }

    override fun DeleteClass(request: DeleteClassRequest): Int {
        if (repository.GetClassById(GetClassByIdRequest(request.ClassId)).error != res_successful) {
            logger.warn("Error while getting class by ID")
            throw GetClassException()
        }

        val error = repository.DeleteClass(request)
        logger.info("Successfully deleted class. ID class ${request.ClassId}")
        return error
    }
}

