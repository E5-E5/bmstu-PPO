package internal.storage

import internal.service.dto.*
import model.Student

data class get_student(val res_Student: Student?, val error: Int)
data class get_students(val res_Students: List<Student>, val error: Int)

interface StudentStorage {
    fun CreateStudent(request: CreateStudentWithRoleRequest): Int
    fun DeleteStudent(request: DeleteStudentRequest): Int
    fun GetStudent(request: GetStudentRequest): get_student
    fun GetStudents(request: GetStudentsRequest): get_students
    fun UpdateStudent(request: UpdateStudentRequest): Int
    fun SingInStudent(request: SingInStudentRequest): get_student
}
