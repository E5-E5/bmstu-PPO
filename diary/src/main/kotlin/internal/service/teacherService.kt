package internal.service

import internal.service.dto.*
import internal.storage.*
import logger.Logger
import model.Teacher
import org.example.internal.model.NameRole
import org.example.internal.service.dto.GetRoleByNameRequest
import org.example.internal.storage.repository.*
import org.example.`package`.exceptions.*
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
    private val logger: Logger,
    private val repository: TeacherStorage,
    private val rep_subject: SubjectStorage,
    private val rep_role: RoleStorage
): ITeacherService
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

    override fun GetTeacher(request: GetTeacherRequest) : get_teacher {
        val (res_teacher, error) = repository.GetTeacher(request)

        logger.logMessage(error, "Get: Teacher")

        return get_teacher(res_teacher, error)
    }

    private fun getCreateRequest(): Int? {
        return rep_role.GetRoleByName(GetRoleByNameRequest(NameRole.TEACHER)).res_Role
    }

    override fun CreateTeacher(request: CreateTeacherRequest): Int {
        if(!isValidPhoneNumber(request.Phone))
            throw PhoneNumberException()

        val currentDate = LocalDate.now()
        if(request.Birthday.toLocalDate().isAfter(currentDate))
            throw BirthdayException()
        if(request.DateStartTeaching.toLocalDate().isAfter(currentDate))
            throw StartTeachException()

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
        logger.logMessage(error, "Create: Teacher")

        return error
    }

    override fun DeleteTeacher(request: DeleteTeacherRequest): Int {
        if(repository.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful)
            throw GetTeacherException()

        val error = repository.DeleteTeacher(request)
        logger.logMessage(error,"Delete: Teacher")

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
        logger.logMessage(error, "Update: Teacher")

        return error
    }

    override fun SingInTeacher(request: SingInTeacherRequest): get_teacher {
        val (res_student, error) = repository.SingInTeacher(
            SingInTeacherRequest(hashPassword(request.Password), request.Identifier))
//        val (res_student, error) = repository.SingInTeacher(SingInTeacherRequest(request.Password, request.Identifier))

        logger.logMessage(error, "Sing in: Teacher")

        return get_teacher(res_student, error)
    }

    override fun AddSubjectToTeacher(request: AddSubjectToTeacherRequest): Int {
        if(repository.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful)
            throw GetTeacherException()

        if(rep_subject.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful)
            throw GetSubjectException()

        val teachers = GetTeacherBySubject(GetTeacherBySubjectRequest(request.SubjectId))
        if (teachers.filter { it.TeacherId == request.TeacherId }.isNotEmpty())
            throw TeacherAlreadyHaveSubjectException()

        val error = repository.AddSubjectToTeacher(request)

        logger.logMessage(error, "Sing in: Teacher")

        return error
    }

    override fun DeleteSubjectFromTeacher(request: DeleteSubjectFromTeacherRequest): Int {
        if(repository.GetTeacher(GetTeacherRequest(request.TeacherId)).error != res_successful)
            throw GetTeacherException()

        if(rep_subject.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful)
            throw GetSubjectException()

        val teachers = GetTeacherBySubject(GetTeacherBySubjectRequest(request.SubjectId))
        if (teachers.filter { it.TeacherId == request.TeacherId }.isEmpty())
            throw TeacherNotTeachSubjectException()

        val error = repository.DeleteSubjectFromTeacher(request)

        logger.logMessage(error, "Sing in: Teacher")

        return error
    }

    override fun GetTeacherBySubject(request: GetTeacherBySubjectRequest): List<Teacher> {
        if(rep_subject.GetSubjectById(GetSubjectRequestById(request.SubjectId)).error != res_successful)
            throw GetSubjectException()

        val teacher = repository.GetTeacherBySubject(request)

        return teacher
    }

}