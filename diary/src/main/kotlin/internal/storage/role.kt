package org.example.internal.storage.repository

import org.example.internal.service.dto.GetRoleByNameRequest

data class get_role(val res_Role: Int?, val error: Int)

interface RoleStorage {
    fun GetRoleByName(request: GetRoleByNameRequest): get_role
}