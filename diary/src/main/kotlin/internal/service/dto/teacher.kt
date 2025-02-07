package internal.service.dto

import model.Gender
import java.sql.*


data class CreateTeacherRequest(
    var TeacherId: Int,
    var DateStartTeaching: Date,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var Password: String? = null,
    var Identifier: String? = null,
    var Phone: String,
    var Gender: Gender,
    var Cabinet: Int
)

data class CreateTeacherWithRoleRequest(
    var TeacherId: Int,
    var DateStartTeaching: Date,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var Password: String? = null,
    var Identifier: String? = null,
    var Phone: String,
    var Gender: Gender,
    var Cabinet: Int,
    var RoleId: Int
)

data class DeleteTeacherRequest(
    var TeacherId: Int
)

data class GetTeacherRequest(
    var TeacherId: Int
)

data class UpdateTeacherRequest(
    var TeacherId: Int,
    var DateStartTeaching: Date? = null,
    var FirstName: String? = null,
    var LastName: String? = null,
    var Patronymic: String? = null,
    var Birthday: Date? = null,
//    var Password: String? = null,
//    var Identifier: String? = null,
    var Phone: String? = null,
    var Gender: Gender? = null,
    var Cabinet: Int? = null
)

data class SingInTeacherRequest(
    var Password: String,
    var Identifier: String,
)

data class AddSubjectToTeacherRequest(
    var TeacherId: Int,
    var SubjectId: Int,
)

data class DeleteSubjectFromTeacherRequest(
    var TeacherId: Int,
    var SubjectId: Int,
)

data class GetTeacherBySubjectRequest(
    var SubjectId: Int,
)

