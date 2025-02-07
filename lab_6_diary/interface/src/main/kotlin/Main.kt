import com.example.ConsoleMenu
import com.example.service.*
import com.example.logger.Logger
import com.example.repository.*

fun main() {
    val logger = Logger()
    val db_connector = PostgresDBConnector()

    val rep_student = StudentRepo(logger, db_connector)
    val rep_class = ClassRepo(logger, db_connector)
    val rep_role = RoleRepo(logger, db_connector)
    val rep_admin = AdminRepo(logger, db_connector)
    val rep_assessment = AssessmentRepo(logger, db_connector)
    val rep_library = LibraryRepo(logger, db_connector)
    val rep_schedule = ScheduleRepo(logger, db_connector)
    val rep_subject = SubjectRepo(logger, db_connector)
    val rep_teacher = TeacherRepo(logger, db_connector)

    val ser_student = StudentService(logger, rep_student, rep_class, rep_role)
    val ser_assessment = AssessmentService(logger, rep_assessment, rep_student, rep_teacher, rep_subject, rep_schedule)
    val ser_subject = SubjectService(logger, rep_subject)
    val ser_schedule = ScheduleService(logger, rep_schedule, rep_class, rep_subject, rep_teacher)
    val ser_library = LibraryService(logger, rep_library, rep_student)
    val ser_teacher = TeacherService(logger, rep_teacher, rep_subject, rep_role)
    val ser_admin = AdminService(logger, rep_admin, rep_role)
    val ser_class = ClassService(logger, rep_class)

    val menu = ConsoleMenu(ser_student, ser_assessment, ser_subject, ser_schedule, ser_library, ser_teacher, ser_admin, ser_class)
    menu.menu_authorization()
}