@file:Suppress("ktlint:standard:no-wildcard-imports")
package com.example.service

import com.example.logging.Log4jLogger
import com.example.service.dto.*
import com.example.repository_interface.*
import com.example.logger.Logger
import com.example.model.NameRole
import com.example.*
import java.io.FileWriter
import java.security.MessageDigest
import java.time.LocalDate

interface IStudentService {
    fun CreateStudent(request: CreateStudentRequest): Int
    fun DeleteStudent(request: DeleteStudentRequest): Int
    fun GetStudent(request: GetStudentRequest): get_student
    fun GetStudents(request: GetStudentsRequest): get_students
    fun UpdateStudent(request: UpdateStudentRequest): Int
    fun SingInStudent(request: SingInStudentRequest): get_student
}

class StudentService(
    private val log: Logger,
    private val repository: StudentStorage,
    private val rep_class: ClassStorage,
    private val rep_role: RoleStorage
): IStudentService
{
    val res_error = 3
    val res_successful = 0
    private val logger = Log4jLogger(StudentService::class.java)

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

    override fun GetStudent(request: GetStudentRequest) : get_student {
        return try {
            val (res_student, error) = repository.GetStudent(request)
            logger.info("Student was got. ID student ${request.StudentId}")
            get_student(res_student, error)
        } catch (e: Exception){
            logger.warn("Error while get student")
            throw Exception("Error while get student")
        }
    }

    override fun GetStudents(request: GetStudentsRequest): get_students {
        return try {
            val (res_students, error) = repository.GetStudents(request)
            logger.info("Students were got")
            get_students(res_students, error)
        } catch (e: Exception){
            logger.warn("Error while get students")
            throw Exception("Error while get students")
        }
    }

    private fun getCreateRequest(): Int? {
        return rep_role.GetRoleByName(GetRoleByNameRequest(NameRole.STUDENT)).res_Role
    }

    override fun CreateStudent(request: CreateStudentRequest): Int {

        if(!isValidPhoneNumber(request.Phone)) {
            logger.warn("Error with phone number")
            throw PhoneNumberException()
        }

        if(rep_class.GetClassById(GetClassByIdRequest(request.ClassId)).error != res_successful) {
            logger.warn("Error while get class")
            throw GetClassException()
        }
        val currentDate = LocalDate.now()
        if(request.Birthday.toLocalDate().isAfter(currentDate)) {
            logger.warn("Error with birthday")
            throw BirthdayException()
        }
        if(request.DateStartStuding.toLocalDate().isAfter(currentDate)) {
            logger.warn("Error with date of start studying")
            throw DateStartStudyException()
        }

        val password = request.FirstName + request.StudentId
        request.Identifier = generateLogin()

        try {
            val fileWriter = FileWriter("C:\\Users\\Admin\\Desktop\\43\\63\\PPO\\PPO\\untitled\\src\\main\\kotlin\\console\\data\\data.txt", true)
            fileWriter.write("[Student] Password: $password Login: ${request.Identifier}\n")
            fileWriter.close()
        } catch (e: Exception) {
            print("")
        }

        println(request.Identifier)
        println(password)

        request.Password = hashPassword(password)
        val role = getCreateRequest() ?: return res_error

        val res_request = CreateStudentWithRoleRequest(request.StudentId, request.DateStartStuding, request.FirstName,
            request.LastName, request.Patronymic, request.Birthday, request.ClassId, request.Password, request.Identifier, request.Phone,
            request.Gender, request.Group, role)

        val error = repository.CreateStudent(res_request)
        logger.info("Student was create. ID student ${request.StudentId}, class ${request.ClassId}")
        return error
    }

    override fun DeleteStudent(request: DeleteStudentRequest): Int {
        if(repository.GetStudent(GetStudentRequest(request.StudentId)).error != res_successful) {
            logger.warn("Error while get student")
            throw GetStudentException()
        }

        val error = repository.DeleteStudent(request)
        logger.info("Student was deleted, ID student ${request.StudentId}")
        return error
    }

    override fun UpdateStudent(request: UpdateStudentRequest): Int {
        if(repository.GetStudent(GetStudentRequest(request.StudentId)).error != res_successful) {
            logger.warn("Error while get student")
            throw GetStudentException()
        }

        val classId: Int? = request.ClassId
        if(classId != null && rep_class.GetClassById(GetClassByIdRequest(classId)).error != res_successful)
            throw GetClassException()

        val currentDate = LocalDate.now()
        val birthday = request.Birthday
        if(birthday != null && birthday.toLocalDate().isAfter(currentDate))
            throw BirthdayException()

        val start_stud = request.DateStartStuding
        if(start_stud != null && start_stud.toLocalDate().isAfter(currentDate))
            throw DateStartStudyException()


        val error = repository.UpdateStudent(request)
        logger.info("Update: Student. ID student ${request.StudentId}")

        return error
    }

    override fun SingInStudent(request: SingInStudentRequest): get_student {
        logger.info("Student was sign in. ID student ${111111111}")
        logger.warn("Student was sign in. ID student ${123123}")
        return try {
            val (res_student, error) = repository.SingInStudent(
            SingInStudentRequest(hashPassword(request.Password), request.Identifier))
            logger.info("Student was sign in. ID student ${res_student?.StudentId}")
            get_student(res_student, error)
        } catch (e: SingInException) {
            logger.info("Invalid login or password of student")
            throw SingInException()
        } catch (e: Exception) {
            logger.error("Error while sign in", ConnectBDException())
            throw Exception("Error while sign in.")
        }
    }
}