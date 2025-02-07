package com.example.service.dto

import com.example.model.ClassNumber


data class CreateClassRequest(
    var ClassId: Int,
    var Letter: String,
    var Number: ClassNumber
)

data class GetClassByIdRequest(
    var ClassId: Int
)

data class GetClassByNameRequest(
    var Letter: String,
    var Number: ClassNumber
)

data class DeleteClassRequest(
    var ClassId: Int
)