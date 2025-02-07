package com.example.repository_interface

import com.example.service.dto.GetRoleByNameRequest

data class get_role(val res_Role: Int?, val error: Int)

interface RoleStorage {
    fun GetRoleByName(request: GetRoleByNameRequest): get_role
}