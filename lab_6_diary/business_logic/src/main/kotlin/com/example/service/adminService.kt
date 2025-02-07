@file:Suppress("ktlint:standard:no-wildcard-imports", "DEPRECATION")

package com.example.service

import com.example.*
import com.example.logging.Log4jLogger
import com.example.service.ScheduleService

import com.example.logger.Logger
import com.example.model.NameRole
import com.example.model.User
import com.example.service.dto.*
import com.example.repository_interface.AdminStorage
import com.example.repository_interface.get_admin
import com.example.repository_interface.RoleStorage
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
    private val log: Logger,
    private val repository: AdminStorage,
    private val rep_role: RoleStorage
): IAdminService
{
    val res_error = 3
    val res_successful = 0

    private val logger = Log4jLogger(AdminService::class.java)

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

        if(!isValidPhoneNumber(request.Phone)) {
            logger.warn("Error with phone number")
            throw PhoneNumberException()
        }

        val currentDate = LocalDate.now()
        if(request.Birthday.toLocalDate().isAfter(currentDate)) {
            logger.warn("Error with birthday")
            throw BirthdayException()
        }

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
        logger.info("Admin was create")

        return error
    }

    override fun DeleteAdmin(request: DeleteAdminRequest): Int {
        return try {
            val error = repository.DeleteAdmin(request)
            logger.info("Admin was deleted. ID ${request.AdminId}")
            error
        } catch (e: Exception){
            logger.warn("Error while delete admin")
            throw Exception("Error while delete admin")
        }
    }

    override fun ViewAllUsers(): List<User> {
        return try {
            val users = repository.ViewAllUsers()
            logger.info("Users was got")
            users
        } catch (e: Exception){
            logger.warn("Error while view all users")
            throw Exception("Error while view all users")
        }
    }

    override fun SingInAdmin(request: SingInAdminRequest): get_admin {
        return try {
            val (res_dmin, error) = repository.SingInAdmin(SingInAdminRequest(
                hashPassword(request.Password), request.Identifier))
            logger.info("Admin was sign in. ID ${res_dmin?.FirstName}")
            get_admin(res_dmin, error)
        } catch (e: SingInException) {
            logger.info("Invalid login or password of admin")
            throw SingInException()
        } catch (e: Exception){
            logger.error("Error while sign in", ConnectBDException())
            throw Exception("Error while sign in")
        }
    }
}