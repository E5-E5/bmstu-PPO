package internal.service

import internal.service.dto.*
import internal.storage.*
import logger.Logger
import org.example.internal.model.NameRole
import org.example.internal.service.dto.GetRoleByNameRequest
import org.example.internal.storage.repository.RoleStorage
import org.example.`package`.exceptions.*
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
    private val logger: Logger,
    private val repository: StudentStorage,
    private val rep_class: ClassStorage,
    private val rep_role: RoleStorage
): IStudentService
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

    override fun GetStudent(request: GetStudentRequest) : get_student {
        val (res_student, error) = repository.GetStudent(request)

        logger.logMessage(error,"Get (one): Student")

        return get_student(res_student, error)
    }

    override fun GetStudents(request: GetStudentsRequest): get_students {
        val (res_students, error) = repository.GetStudents(request)

        logger.logMessage(error, "Get (list): Student")

        return get_students(res_students, error)
    }

    private fun getCreateRequest(): Int? {
        return rep_role.GetRoleByName(GetRoleByNameRequest(NameRole.STUDENT)).res_Role
    }

    override fun CreateStudent(request: CreateStudentRequest): Int {

        if(!isValidPhoneNumber(request.Phone))
            throw PhoneNumberException()

        if(rep_class.GetClassById(GetClassByIdRequest(request.ClassId)).error != res_successful)
            throw GetClassException()

        val currentDate = LocalDate.now()
        if(request.Birthday.toLocalDate().isAfter(currentDate))
            throw BirthdayException()
        if(request.DateStartStuding.toLocalDate().isAfter(currentDate))
            throw DateStartStudyException()

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
        logger.logMessage(error, "Create: Student")

        return error
    }

    override fun DeleteStudent(request: DeleteStudentRequest): Int {
        if(repository.GetStudent(GetStudentRequest(request.StudentId)).error != res_successful)
            throw GetStudentException()

        val error = repository.DeleteStudent(request)

        logger.logMessage(error,"Delete: Student")

        return error
    }

    override fun UpdateStudent(request: UpdateStudentRequest): Int {
        if(repository.GetStudent(GetStudentRequest(request.StudentId)).error != res_successful)
            throw GetStudentException()

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
        logger.logMessage(error, "Update: Student")

        return error
    }

    override fun SingInStudent(request: SingInStudentRequest): get_student {
        val (res_student, error) = repository.SingInStudent(SingInStudentRequest(
            hashPassword(request.Password), request.Identifier))

        logger.logMessage(error, "Sing in: Student")

        return get_student(res_student, error)
    }

}