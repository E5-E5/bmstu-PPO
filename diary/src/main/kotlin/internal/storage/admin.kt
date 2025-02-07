package org.example.internal.storage

import org.example.internal.model.Admin
import org.example.internal.model.User
import org.example.internal.service.dto.CreateAdminWithRoleRequest
import org.example.internal.service.dto.DeleteAdminRequest
import org.example.internal.service.dto.SingInAdminRequest

data class get_admin(val res_Admin: Admin?, val error: Int)

interface AdminStorage {
    fun CreateAdmin(request: CreateAdminWithRoleRequest): Int
    fun DeleteAdmin(request: DeleteAdminRequest): Int
    fun SingInAdmin(request: SingInAdminRequest): get_admin
    fun ViewAllUsers(): List<User>
}
