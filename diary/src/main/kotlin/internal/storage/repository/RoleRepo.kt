package org.example.internal.storage.repository

import PostgresDBConnector
import logger.Logger
import org.example.internal.service.dto.GetRoleByNameRequest
import java.sql.*

class RoleRepo (
    private val logger: Logger,
    private val dbConnector: PostgresDBConnector
): RoleStorage
{
    val res_error = 3
    val res_successful = 0

    override fun GetRoleByName(request: GetRoleByNameRequest): get_role {
        val query = "SELECT * FROM diary.role WHERE NameRole = ?"

        var role: Int? = null

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setObject(1, request.NameRole.name, Types.OTHER)

                preparedStatement.executeQuery()?.use { resultSet ->
                    if (resultSet.next()) {
                        val RoleId = resultSet.getInt("RoleId")
                        role = RoleId
                    }
                }
            }
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
        }

        if (role != null)
            return get_role(role, 0)
        return get_role(null, 3)
    }
}
