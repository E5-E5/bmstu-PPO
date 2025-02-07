package com.example.model

import com.example.model.Gender
import java.sql.*

data class User (
    var UserId: Int,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var Password: String,
    var Identifier: String,
    var Phone: String,
    var Gender: Gender,
    var RoleId: Int
)

data class Admin (
    var AdmintId: Int,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var Password: String,
    var Identifier: String,
    var Phone: String,
    var Gender: Gender
)