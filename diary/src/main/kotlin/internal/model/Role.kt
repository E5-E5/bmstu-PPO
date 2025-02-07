package org.example.internal.model

enum class NameRole {
    STUDENT, TEACHER, ADMIN
}

data class Role_model (
    var RoleId: Int,
    var NameRole: NameRole
)