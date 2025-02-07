package com.example.repository_interface

import com.example.model.Admin
import com.example.model.User
import com.example.service.dto.CreateAdminWithRoleRequest
import com.example.service.dto.DeleteAdminRequest
import com.example.service.dto.SingInAdminRequest

data class get_admin(val res_Admin: Admin?, val error: Int)

interface AdminStorage {
    fun CreateAdmin(request: CreateAdminWithRoleRequest): Int
    fun DeleteAdmin(request: DeleteAdminRequest): Int
    fun SingInAdmin(request: SingInAdminRequest): get_admin
    fun ViewAllUsers(): List<User>
}
