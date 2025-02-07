package internal.service.dto

import model.*
import java.sql.*

data class CreateStudentRequest(
    var StudentId: Int,
    var DateStartStuding: Date,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var ClassId: Int,
    var Password: String? = null,
    var Identifier: String? = null,
    var Phone: String,
    var Gender: Gender,
    var Group: GroupStudent
)

data class CreateStudentWithRoleRequest(
    var StudentId: Int,
    var DateStartStuding: Date,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var ClassId: Int,
    var Password: String? = null,
    var Identifier: String? = null,
    var Phone: String,
    var Gender: Gender,
    var Group: GroupStudent,
    var RoleId: Int
)

data class DeleteStudentRequest(
    var StudentId: Int
)

data class GetStudentRequest(
    var StudentId: Int
)

data class GetStudentsRequest(
    var ClassId: Int
)

data class UpdateStudentRequest(
    var StudentId: Int,
    var DateStartStuding: Date? = null,
    var FirstName: String? = null,
    var LastName: String? = null,
    var Patronymic: String? = null,
    var Birthday: Date? = null,
    var ClassId: Int? = null,
//    var Password: String? = null,
//    var Identifier: String? = null,
    var Phone: String? = null,
    var Gender: Gender? = null,
    var Group: GroupStudent? = null
)

data class SingInStudentRequest(
    var Password: String,
    var Identifier: String,
)