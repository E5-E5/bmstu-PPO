package internal.storage

import internal.service.dto.*
import model.Class

data class get_class(val res_Class: Class?, val error: Int)
data class classes(val res_Class: Class?, val error: Int)

interface ClassStorage {
    fun CreateClass(request: CreateClassRequest): Int
    fun GetClassById(request: GetClassByIdRequest) : get_class
    fun GetClassByName(request: GetClassByNameRequest) : get_class
    fun DeleteClass(request: DeleteClassRequest): Int
    fun GetAllClasses() : List<Class>
}