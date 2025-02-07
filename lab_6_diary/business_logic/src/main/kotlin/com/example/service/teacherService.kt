@file:Suppress("ktlint:standard:no-wildcard-imports", "DEPRECATION")
package com.example.service

import com.example.logging.Log4jLogger
import com.example.service.dto.*
import com.example.repository_interface.*
import com.example.logger.Logger
import com.example.model.Teacher
import com.example.model.NameRole
import com.example.service.dto.GetRoleByNameRequest
import com.example.*
import java.io.FileWriter
import java.security.MessageDigest
import java.time.LocalDate

interface ITeacherService {
    fun CreateTeacher(request: CreateTeacherRequest): Int
    fun DeleteTeacher(request: DeleteTeacherRequest): Int
    fun GetTeacher(request: GetTeacherRequest): get_teacher
    fun UpdateTeacher(request: UpdateTeacherRequest): Int
    fun SingInTeacher(request: SingInTeacherRequest): get_teacher
    fun AddSubjectToTeacher(request: AddSubjectToTeacherRequest): Int
    fun DeleteSubjectFromTeacher(request: DeleteSubjectFromTeacherRequest): Int
    fun GetTeacherBySubject(request: GetTeacherBySubjectRequest): List<Teacher>
}

class TeacherService(
    private val log: Logger,
    private val repository: TeacherStorage,
    private val rep_subject: SubjectStorage,
    private val rep_role: RoleStorage
): ITeacherService
{
    val res_error = 3
    val res_successful = 0
    private val logger = Log4jLogger(TeacherService::class.java)

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

    override fun GetTeacher(request: GetTeacherRequest) : get_teacher {
        return try {
            val (res_teacher, error) = repository.GetTeacher(request)
            logger.info("Teacher was got. ID teacher ${request.TeacherId}")
            get_teacher(res_teacher, error)
        } catch (e: Exception){
            logger.error("Error while get teacher", Exception("Error while get teacher"))
            throw Exception("Error while get teacher")
        }
    }

    private fun getCreateRequest(): Int? {
        return rep_role.GetRoleByName(GetRoleByNameRequest(NameRole.TEACHER)).res_Role
    }

    override fun CreateTeacher(request: CreateTeacherRequest): Int {
        if(!isValidPhoneNumber(request.Phone)) {
            logger.error("Error with phone number", PhoneNumberException())
            throw PhoneNumberException()
        }

        val currentDate = LocalDate.now()
        if(request.Birthday.toLocalDate().isAfter(currentDate)) {
            logger.error("Error with birthday", BirthdayException())
            throw BirthdayException()
        }
        if(request.DateStartTeaching.toLocalDate().isAfter(currentDate)) {
            logger.error("Error with start teach date", StartTeachException())
            throw StartTeachException()
        }

        val password = request.FirstName + request.Birthday.year
        request.Identifier = generateLogin()

        println(request.Identifier)
        println(password)

        try {
            val fileWriter = FileWriter("C:\\Users\\Admin\\Desktop\\43\\63\\PPO\\PPO\\untitled\\src\\main\\kotlin\\console\\data\\data.txt", true)
            fileWriter.write("[Teacher] Password: $password Login: ${request.Identifier}\n")
            fileWriter.close()
        } catch (e: Exception) {
            print("")
        }

        request.Password = hashPassword(password)
        val role = getCreateRequest() ?: return res_error

        val res_request = CreateTeacherWithRoleRequest(request.TeacherId, request.DateStartTeaching, request.FirstName,
            request.LastName, request.Patronymic, request.Birthday, request.Password, request.Identifier, request.Phone,
            request.Gender, request.Cabinet, role)

        val error = repository.CreateTeacher(res_request)
        logger.info("Teacher was created.")
        return error
    }

    override fun DeleteTeacher(request: DeleteTeacherRequest): Int {
        if(repository.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful) {
            logger.error("Error while get teacher", GetTeacherException())
            throw GetTeacherException()
        }

        val error = repository.DeleteTeacher(request)
        logger.info("Teacher was deleted. ID teacher ${request.TeacherId}")
        return error
    }

    override fun UpdateTeacher(request: UpdateTeacherRequest): Int {
        if(repository.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful)
            throw GetTeacherException()

        val currentDate = LocalDate.now()
        val birthday = request.Birthday
        if(birthday != null && birthday.toLocalDate().isAfter(currentDate))
            throw BirthdayException()
        val start_teach = request.DateStartTeaching
        if(start_teach != null && start_teach.toLocalDate().isAfter(currentDate))
            throw StartTeachException()

        val error = repository.UpdateTeacher(request)

        return error
    }

    override fun SingInTeacher(request: SingInTeacherRequest): get_teacher {
        return try {
            val (res_teacher, error) = repository.SingInTeacher(
                SingInTeacherRequest(hashPassword(request.Password), request.Identifier))
            logger.info("Teacher was sign in. ID teacher ${res_teacher?.TeacherId}")
            get_teacher(res_teacher, error)
        } catch (e: SingInException) {
            logger.warn("Invalid login or password of teacher")
            throw SingInException()
        } catch (e: Exception) {
            logger.error("Error while sign in", ConnectBDException())
            throw Exception("Error while sign in")
        }
    }

    override fun AddSubjectToTeacher(request: AddSubjectToTeacherRequest): Int {
        if(repository.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful) {
            logger.error("Error while get teacher", GetTeacherException())
            throw GetTeacherException()
        }

        if(rep_subject.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful) {
            logger.error("Error while get subject", GetSubjectException())
            throw GetSubjectException()
        }

        val teachers = GetTeacherBySubject(GetTeacherBySubjectRequest(request.SubjectId))
        if (teachers.filter { it.TeacherId == request.TeacherId }.isNotEmpty()) {
            logger.error("Error while get teacher by subject", TeacherAlreadyHaveSubjectException())
            throw TeacherAlreadyHaveSubjectException()
        }

        val error = repository.AddSubjectToTeacher(request)

        logger.info("Subject was added to teacher")

        return error
    }

    override fun DeleteSubjectFromTeacher(request: DeleteSubjectFromTeacherRequest): Int {
        if(repository.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful) {
            logger.error("Error while get teacher", GetTeacherException())
            throw GetTeacherException()
        }

        if(rep_subject.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful) {
            logger.error("Error while get subject", GetSubjectException())
            throw GetSubjectException()
        }

        val teachers = GetTeacherBySubject(GetTeacherBySubjectRequest(request.SubjectId))
        if (teachers.filter { it.TeacherId == request.TeacherId }.isEmpty()) {
            logger.error("Error while get teacher by subject", TeacherNotTeachSubjectException())
            throw TeacherNotTeachSubjectException()
        }

        val error = repository.DeleteSubjectFromTeacher(request)

        logger.info("Subject was deleted from teacher")

        return error
    }

    override fun GetTeacherBySubject(request: GetTeacherBySubjectRequest): List<Teacher> {
        if(rep_subject.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful) {
            logger.error("Error while get subject", TeacherNotTeachSubjectException())
            throw GetSubjectException()
        }

        return try {
            val teacher = repository.GetTeacherBySubject(request)
            logger.info("Teacher was get teacher by subject")
            teacher
        } catch (e: Exception) {
            logger.error("Error while get teacher by subject", Exception("Error while get teacher by subject"))
            throw Exception("Error while get teacher by subject")
        }
    }

}