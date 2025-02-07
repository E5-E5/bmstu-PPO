package com.example.model

import java.sql.*

enum class Gender {
    MALE, FEMALE
}
enum class GroupStudent {
    FIRST, SECOND
}

data class Student (
    var StudentId: Int,
    var DateStartStuding: Date,
    var FirstName: String,
    var LastName: String,
    var Patronymic: String,
    var Birthday: Date,
    var ClassId: Int,
    var Password: String,
    var Identifier: String,
    var Phone: String,
    var Gender: Gender,
    var Group: GroupStudent
)