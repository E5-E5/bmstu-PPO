package model

import java.sql.*

data class Teacher (
    var TeacherId: Int,
    var DateStartTeaching: Date,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var Password: String,
    var Identifier: String,
    var Phone: String,
    var Gender: Gender,
    var Cabinet: Int
)