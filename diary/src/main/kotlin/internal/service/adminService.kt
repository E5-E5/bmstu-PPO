package org.example.internal.service

import logger.Logger
import org.example.internal.model.NameRole
import org.example.internal.model.User
import org.example.internal.service.dto.*
import org.example.internal.storage.AdminStorage
import org.example.internal.storage.get_admin
import org.example.internal.storage.repository.RoleStorage
import org.example.`package`.exceptions.*
import java.io.FileWriter
import java.security.MessageDigest
import java.time.LocalDate

interface IAdminService {
    fun CreateAdmin(request: CreateAdminRequest): Int
    fun DeleteAdmin(request: DeleteAdminRequest): Int
    fun SingInAdmin(request: SingInAdminRequest): get_admin
    fun ViewAllUsers(): List<User>
}

class AdminService(
    private val logger: Logger,
    private val repository: AdminStorage,
    private val rep_role: RoleStorage
): IAdminService
{
    val res_error = 3
    val res_successful = 0

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        val hexString = StringBuilder()

        for (byte in hashBytes) {
            val hex = Integer.toHexString(0xff and byte.toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }

    fun generateLogin(): String {
        val randomString = generateRandomString(8)
        return randomString
    }

    fun generateRandomString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneRegex = Regex("^\\+\\d{11}$")
        return phoneNumber.matches(phoneRegex)
    }

    private fun getCreateRequest(): Int? {
        return rep_role.GetRoleByName(GetRoleByNameRequest(NameRole.ADMIN)).res_Role
    }

    override fun CreateAdmin(request: CreateAdminRequest): Int {

        if(!isValidPhoneNumber(request.Phone))
            throw PhoneNumberException()

        val currentDate = LocalDate.now()
        if(request.Birthday.toLocalDate().isAfter(currentDate))
            throw BirthdayException()

        val password = request.FirstName + request.Birthday.year
        request.Identifier = generateLogin()

        try {
            val fileWriter = FileWriter("C:\\Users\\Admin\\Desktop\\43\\63\\PPO\\PPO\\untitled\\src\\main\\kotlin\\console\\data\\data.txt", true)
            fileWriter.write("[Admin] Password: $password Login: ${request.Identifier}\n")
            fileWriter.close()
        } catch (e: Exception) {
            print("")
        }

        println(request.Identifier)
        println(password)

        request.Password = hashPassword(password)
        val role = getCreateRequest() ?: return res_error

        val res_request = CreateAdminWithRoleRequest(request.AdminId, request.FirstName,
            request.LastName, request.Patronymic, request.Birthday, request.Password, request.Identifier, request.Phone,
            request.Gender, role)

        val error = repository.CreateAdmin(res_request)
        logger.logMessage(error, "Create: Student")

        return error
    }

    override fun DeleteAdmin(request: DeleteAdminRequest): Int {
        val error = repository.DeleteAdmin(request)

        logger.logMessage(error,"Delete: Student")

        return error
    }

    override fun ViewAllUsers(): List<User> {
        val users = repository.ViewAllUsers()

        return users
    }

    override fun SingInAdmin(request: SingInAdminRequest): get_admin {
        val (res_dmin, error) = repository.SingInAdmin(SingInAdminRequest(
            hashPassword(request.Password), request.Identifier))
        logger.logMessage(error, "Sing in: Student")

        return get_admin(res_dmin, error)
    }

}