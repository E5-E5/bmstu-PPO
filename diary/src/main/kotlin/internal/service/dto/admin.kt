package org.example.internal.service.dto

import model.Gender
import java.sql.*

data class CreateAdminRequest(
    var AdminId: Int,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var Password: String? = null,
    var Identifier: String? = null,
    var Phone: String,
    var Gender: Gender
)

data class CreateAdminWithRoleRequest(
    var AdminId: Int,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var Password: String? = null,
    var Identifier: String? = null,
    var Phone: String,
    var Gender: Gender,
    var RoleId: Int
)

data class DeleteAdminRequest(
    var AdminId: Int
)

data class SingInAdminRequest(
    var Password: String,
    var Identifier: String,
)