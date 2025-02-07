@file:Suppress("ktlint:standard:no-wildcard-imports", "NAME_SHADOWING", "DEPRECATION")
package com.example

import PostgresDBConnector
import com.example.service.SubjectService
import com.example.service.*
import com.example.service.dto.*
import com.example.model.*
import com.example.*
import com.example.reader.config.ConfigReader
import java.sql.*
import java.time.DayOfWeek
import java.util.InputMismatchException
import java.util.Scanner
import kotlin.system.exitProcess

class ConsoleMenu (
    private val student_service: StudentService,
    private val assessment_service: AssessmentService,
    private val subject_service: SubjectService,
    private val schedule_service: ScheduleService,
    private val library_service: LibraryService,
    private val teacher_service: TeacherService,
    private val admin_service: AdminService,
    private val class_service: ClassService,
    )
{
    private val scanner = Scanner(System.`in`)

    private var student: Student? = null
    private var teacher: Teacher? = null
    private var admin: Admin? = null

    private val connector = PostgresDBConnector()
//    private val userService: UserService

//    private fun changeUser(
//        name: String,
//        password: String,
//    ) {
//        connector.changeUser(name, password)
////        this.user = name
////        this.password = password
//    }

    fun menu_authorization() {
        val menuFunctions =
            arrayOf(
                ::exit,
                ::sing_in_student,
                ::sing_in_teacher,
                ::sing_in_admin
            )

        while (true) {
            println("\n## Авторизация ##")
            println("1. Войти как ученик")
            println("2. Войти как учитель")
            println("3. Войти как администратор")
            println("0. Выход")

            print("Выберите опцию: ")

            try {
                val choice = scanner.nextInt()

                if (choice in menuFunctions.indices) {
                    menuFunctions[choice]()
                } else {
                    println("Некорректный номер опции.")
                }
            } catch (e: InputMismatchException) {
                println("Ошибка: введено некорректное значение. Введите число.")
                scanner.next()
            }
        }
    }

    fun menu_student() {
        val menuFunctions =
            arrayOf(
                ::exit,
                ::viewAssessment,
                ::viewAssessmentAll,
                ::viewSchedule_student,
                ::takeBook,
                ::returnBook
            )

        while (true) {
            println("\n## Пользовательские сценарии ##")
            println("1. Посмотреть оценки по предмету")
            println("2. Посмотреть оценки по всем предметам")
            println("3. Просмотреть расписание на неделю")
            println("4. Оформить книгу")
            println("5. Вернуть книгу")
            println("0. Выход")

            print("\nВыберите опцию: ")

            try {
                val choice = scanner.nextInt()

                if (choice == 0) {
                    return
                }

                if (choice in menuFunctions.indices) {
                    menuFunctions[choice]()
                } else {
                    println("Некорректный номер опции.")
                }
            } catch (e: InputMismatchException) {
                println("Ошибка: введено некорректное значение. Введите число.")
//                scanner.next()
            }
        }
    }

    fun menu_teacher() {
        val menuFunctions =
            arrayOf(
                ::exit,
                ::add_assessment,
                ::change_assessment,
                ::delete_assessment,
                ::watch_assassment_class,
                ::watch_assassment_student,
                ::viewSchedule_teacher
            )

        while (true) {
            println("\n## Пользовательские сценарии ##")
            println("1. Добавить оценку ученику")
            println("2. Изменить оценку ученику")
            println("3. Удалить оценку ученику")
            println("4. Посмотреть оценки класса")
            println("5. Посмотреть оценки ученика")
            println("6. Посмотреть расписание")
            println("0. Выход")

            print("\nВыберите опцию: ")

            try {
                val choice = scanner.nextInt()

                if (choice == 0) {
                    return
                }

                if (choice in menuFunctions.indices) {
                    menuFunctions[choice]()
                } else {
                    println("Некорректный номер опции.")
                }
            } catch (e: InputMismatchException) {
                println("Ошибка: введено некорректное значение. Введите число.")
//                scanner.next()
            }
        }
    }

    fun menu_admin() {
        val menuFunctions =
            arrayOf(
                ::exit,
                ::view_all_users,
                ::add_student,
                ::delete_student,
                ::add_teacher,
                ::delete_teacher,
                ::add_admin,
                ::delete_admin,
                ::add_schedule,
                ::delete_schedule,
                ::add_class,
                ::delete_class,
                ::add_subject,
                ::delete_subject,
                ::add_subject_to_teacher,
                ::delete_subject_from_teacher,
                ::add_book,
                ::delete_book
            )

        while (true) {
            println("\n## Пользовательские сценарии Администратора ##")
            println("1. Посмотреть список пользователей")
            println("2. Добавить ученика")
            println("3. Удалить ученика")
            println("4. Добавить учителя")
            println("5. Удалить учителя")
            println("6. Добавить администратора")
            println("7. Удалить администратора")
            println("8. Добавить рассписание")
            println("9. Удалить рассписание")
            println("10. Добавить класс")
            println("11. Удалить класс")
            println("12. Добавить предмет")
            println("13. Удалить предмет")
            println("14. Добавить предмет учителю")
            println("15. Удалить предмет у учителя")
            println("16. Добавить книги")
            println("17. Удалить книгу")
            println("0. Выход")

            print("\nВыберите опцию: ")

            try {
                val choice = scanner.nextInt()

                if (choice == 0) {
                    return
                }

                if (choice in menuFunctions.indices) {
                    menuFunctions[choice]()
                } else {
                    println("Некорректный номер опции.")
                }
            } catch (e: InputMismatchException) {
                println("Ошибка: введено некорректное значение. Введите число.")
                scanner.next()
            }

            println()
        }
    }

    private fun exit() {
        println("\nРабота программы завершена")
        scanner.close()
        exitProcess(0)
    }

    private fun sing_in_student(): Int {
        val scanner = Scanner(System.`in`)

        println("\nВведите логин:")
        val identifier = scanner.next()

        println("\nВведите пароль:")
        val password = scanner.next()

        try {
            val user = student_service.SingInStudent(SingInStudentRequest(password, identifier))
            student = user.res_Student
            println("\nАвторизация успешна. Добро пожаловать, ${student?.FirstName}!")
            menu_student()
            return 0
        } catch (e: SingInException) {
            println("${e.message}")
        } catch (e: Exception) {
            println("Ошибка авторизации: ${e.message}")
        }
        return 1
    }

    private fun sing_in_teacher(): Int {
        val scanner = Scanner(System.`in`)

        println("\nВведите логин:")
        val identifier = scanner.next()

        println("\nВведите пароль:")
        val password = scanner.next()

        try {
            val user = teacher_service.SingInTeacher(SingInTeacherRequest(password, identifier))
            teacher = user.res_Teacher
            println("\nАвторизация успешна. Добро пожаловать, ${teacher?.FirstName}!")

            menu_teacher()
            return 0
        } catch (e: SingInException) {
            println("${e.message}")
        } catch (e: Exception) {
            println("Ошибка авторизации: ${e.message}")
        }
        return 1
    }

    private fun sing_in_admin(): Int {
        val scanner = Scanner(System.`in`)

        println("\nВведите логин:")
        val identifier = scanner.next()

        println("\nВведите пароль:")
        val password = scanner.next()

        try {
            val user = admin_service.SingInAdmin(SingInAdminRequest(password, identifier))
            admin = user.res_Admin
            println("Авторизация успешна. Добро пожаловать, ${admin?.FirstName}!")

            menu_admin()
            return 0
        } catch (e: SingInException) {
            println("${e.message}")
        } catch (e: Exception) {
            println("Ошибка авторизации: ${e.message}")
        }
        return 1
    }

    fun translateDayOfWeekToRussian(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Понедельник"
            DayOfWeek.TUESDAY -> "Вторник"
            DayOfWeek.WEDNESDAY -> "Среда"
            DayOfWeek.THURSDAY -> "Четверг"
            DayOfWeek.FRIDAY -> "Пятница"
            DayOfWeek.SATURDAY -> "Суббота"
            DayOfWeek.SUNDAY -> "Воскресенье"
        }
    }

    fun translateIntToDayOfWeek(dayOfWeek: Int): DayOfWeek {
        return when (dayOfWeek) {
            1 -> DayOfWeek.MONDAY
            2 -> DayOfWeek.TUESDAY
            3 -> DayOfWeek.WEDNESDAY
            4 -> DayOfWeek.THURSDAY
            5 -> DayOfWeek.FRIDAY
            6 -> DayOfWeek.SATURDAY
            else -> throw DayOfWeekGetException()
        }
    }

    fun translateAssessmentToInt(a: StudentAssessment): String {
        return when (a) {
            StudentAssessment.ONE -> "1"
            StudentAssessment.TWO -> "2"
            StudentAssessment.THREE -> "3"
            StudentAssessment.FOUR -> "4"
            StudentAssessment.FIVE -> "5"
        }
    }

    fun translateInttoAssessment(a: Int): StudentAssessment {
        return when (a) {
            1 -> StudentAssessment.ONE
            2 -> StudentAssessment.TWO
            3 -> StudentAssessment.THREE
            4 -> StudentAssessment.FOUR
            5 -> StudentAssessment.FIVE
            else -> throw Exception("Неверно указана оценка.")
        }

    }

    fun translateGroup(g: GroupSchedule): String {
        return when (g) {
            GroupSchedule.FIRST -> "Первая подгруппа"
            GroupSchedule.SECOND -> "Вторая подгруппа"
            GroupSchedule.JOINT -> "Общая"
        }
    }

    fun translateIntToGroup(g: Int): GroupSchedule {
        return when (g) {
            1 -> GroupSchedule.FIRST
            2 -> GroupSchedule.SECOND
            3 -> GroupSchedule.JOINT
            else -> throw ScheduleGroupGetException()
        }
    }

    fun translateToGroup(g: Int): GroupStudent {
        return when (g) {
            1 -> GroupStudent.FIRST
            2 -> GroupStudent.SECOND
            else -> throw StudentGroupGetException()
        }
    }

    fun translateToGender(g: Int): Gender {
        return when (g) {
            1 -> Gender.MALE
            2 -> Gender.FEMALE
            else -> throw StudentGenderGetException()
        }
    }

    fun isValidDateFormat(input: String): Boolean {
        val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")
        return regex.matches(input)
    }


    private fun print_assessment(assessents: List<Assessment>, name: String) {
        println("\nОценки по $name:")
        if (assessents.isNotEmpty()) {
            for (a in assessents) {
                println("${a.Date} - ${translateAssessmentToInt(a.Assessment)}")
            }
            println()
        }
        else
            println("Оценки еще не были получены.")
    }

    private fun print_schedule(schedule: List<Schedule>) {

        val groupedSchedule = schedule.groupBy { it.DayOfWeek }

        for (dayOfWeek in DayOfWeek.entries.filter { it != DayOfWeek.SUNDAY }) {
            println("\nДень недели: ${translateDayOfWeekToRussian(dayOfWeek)}")
            val scheduleForDay = groupedSchedule[dayOfWeek]
            if (scheduleForDay != null) {
                for (schedule in scheduleForDay.sortedBy { it.LessonNumber }) {
                    val subject = subject_service.GetSubject(GetSubjectRequestById(schedule.SubjectId))
                    val teacher = teacher_service.GetTeacher(GetTeacherRequest(schedule.TeacherId)).res_Teacher
                    println("${schedule.LessonNumber} урок: ${subject.res_Subject!!.Name}")
                    if (schedule.Group != GroupSchedule.JOINT)
                        println("\t Группа: ${translateGroup(schedule.Group)}")
                    println("\t Учитель: ${teacher!!.LastName} ${teacher.FirstName.first()}. ${teacher.Patronymic.first()}.")
                    println("\t Кабинет: ${teacher.Cabinet}")
                }
            } else {
                println("Выходной")
            }
        }
    }

    private fun print_subject(subjects: List<Subject>) {
        for (subject in subjects) {
            println("${subject.SubjectId}: ${subject.Name}")
        }
    }

    private fun print_books(books: List<Library>, take: Boolean = true) {
        for (book in books) {
            println("\n${book.BookId}: ${book.Title}")
            println("\t Автор: ${book.Author}")
            println("\t Издатель: ${book.Publisher}")
            println("\t Дата издания: ${book.DatePublication}")
            if (take)
                println("\t Количество книг: ${book.Count}")
        }
        println()
    }

    private fun viewAssessment() {
        val scanner = Scanner(System.`in`)
        try {
            val subjects = subject_service.GetSubjectsForClass(GetSubjectsForClassRequest(student!!.ClassId))
            print_subject(subjects.res_Subjects)

            println("\nВведите id предмета:")
            val subject_id = scanner.nextInt()

            println("\nВведите начальную дату в формате ГГГГ-ММ-ДД:")
            val date_start = scanner.next()
            if (!isValidDateFormat(date_start))
                throw DataException()

            println("\nВведите конечную дату в формате ГГГГ-ММ-ДД:")
            val date_finish = scanner.next()
            if (!isValidDateFormat(date_finish))
                throw DataException()


            val res_assessment = assessment_service.GetStudentAssessments(GetAssessmentsRequest(
                student!!.StudentId, subject_id,
                Date.valueOf(date_start) as Date, Date.valueOf(date_finish) as Date))

            print_assessment(res_assessment.res_Assessment, subjects.res_Subjects.find { it.SubjectId == subject_id }!!.Name)


            println()
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun viewAssessmentAll() {
        try {
            val subjects = subject_service.GetSubjectsForClass(GetSubjectsForClassRequest(student!!.ClassId))

            val scanner = Scanner(System.`in`)
            println("\nВведите начальную дату в формате ГГГГ-ММ-ДД:")
            val date_start = scanner.next()
            if (!isValidDateFormat(date_start))
                throw DataException()

            println("\nВведите конечную дату в формате ГГГГ-ММ-ДД:")
            val date_finish = scanner.next()
            if (!isValidDateFormat(date_finish))
                throw DataException()

            for (subject in subjects.res_Subjects) {
                val res_assessment = assessment_service.GetStudentAssessments(
                    GetAssessmentsRequest(
                        student!!.StudentId, subject.SubjectId,
                        Date.valueOf(date_start) as Date, Date.valueOf(date_finish) as Date
                    )
                )
                print_assessment(res_assessment.res_Assessment, subjects.res_Subjects.find { it.SubjectId == subject.SubjectId }!!.Name)
            }
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun viewSchedule_student() {
        try {
            val schedule = schedule_service.GetScheduleForClass(GetScheduleForClassRequest(student!!.ClassId))

            print_schedule(schedule.res_Schedule)
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun takeBook() {
        val scanner = Scanner(System.`in`)

        try {
            println("\nВведите название книги для поиска:")
            val title = scanner.next()

            val books = library_service.GetBooks(GetBooksRequest(title))
            print_books(books.res_Books)

            println("\nВведите id книги:")
            val book_id = scanner.nextInt()

            val error = library_service.TakeBook(TakeBookRequest(book_id, student!!.StudentId))
            if (error == 0)
                println("\nКнига успешно оформлена")
            else
                println("\nКнига не была оформлена")
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun returnBook() {
        val scanner = Scanner(System.`in`)

        try {
            val books = library_service.GetStudentBooks(GetStudentBooks(student!!.StudentId))

            if (books.res_Books.isNotEmpty())
            {
                println("\nВаши книги:")
                print_books(books.res_Books, false)

                println("\nВведите id книги:")
                val book_id = scanner.nextInt()

                val error = library_service.ReturnBook(ReturnBookRequest(book_id, student!!.StudentId))
                if (error == 0)
                    println("Книга успешно возвращена")
                else
                    println("Книга не была возвращена")
            }
            else
                println("У вас нет оформленных книг")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun prepare_assessment(teacher_id: Int): Pair<Int, Int> {
        val schedule = schedule_service.GetScheduleForTeacher(GetScheduleForTeacherRequest(teacher_id))
        if (schedule.res_Schedule.isEmpty())
            throw TeacherHaveScheduleException()

        println("\nВаши классы: ")
        schedule.res_Schedule.distinctBy { it.ClassId }.forEachIndexed { index, element ->
            println("${index + 1}): ${element.ClassId}")
        }

        println("\nВведите id класса:")
        val class_id = scanner.nextInt()

        val students = student_service.GetStudents(GetStudentsRequest(class_id))

        println("\nУченики в классе $class_id: ")
        if (students.res_Students.isNotEmpty())
        students.res_Students.forEachIndexed { index, element ->
            println("${index + 1}): ${element.StudentId}")
        }
        else
            throw ClassStudentException()

        println("\nВведите id ученика:")
        val student_id = scanner.nextInt()

        println("\nВаши предметы: ")
        schedule.res_Schedule.distinctBy { it.SubjectId }.forEachIndexed { index, element ->
            println("${index + 1}): ${element.SubjectId}")
        }

        println("\nВведите id предмета:")
        val subject_id = scanner.nextInt()

        return Pair(student_id, subject_id)
    }

    private fun add_assessment() {
        val scanner = Scanner(System.`in`)
        try {
            val (student_id, subject_id) = prepare_assessment(teacher!!.TeacherId)

            println("\nВведите полученную оценку:")
            val mark = translateInttoAssessment(scanner.nextInt())

            println("\nВведите дату получения оценки в формате ГГГГ-ММ-ДД:")
            val date = scanner.next()
            if (!isValidDateFormat(date))
                throw DataException()

            val error = assessment_service.CreateAssessment(
                CreateAssessmentRequest(
                    student_id, subject_id, teacher!!.TeacherId,
                    mark, Date.valueOf(date)
                )
            )

            if (error == 0)
                println("Оценка успешно проставлена")
            else
                println("Оценка не была проставлена")
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun delete_assessment() {
        val scanner = Scanner(System.`in`)

        try {
            val (student_id, subject_id) = prepare_assessment(teacher!!.TeacherId)

            println("\nВведите дату получения оценки, которую желаете удалить в формате ГГГГ-ММ-ДД:")
            val date = scanner.next()
            if (!isValidDateFormat(date))
                throw DataException()

            assessment_service.DeleteAssessment(
                DeleteAssessmentRequest(
                    student_id, subject_id, teacher!!.TeacherId,
                    Date.valueOf(date)
                )
            )
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun change_assessment() {
        val scanner = Scanner(System.`in`)

        try {
            val (student_id, subject_id) = prepare_assessment(teacher!!.TeacherId)

            println("\nВведите дату получения оценки, которую желаете изменить в формате ГГГГ-ММ-ДД:")
            val date = Date.valueOf(scanner.next())

            println("\nВведите измененную оценку:")
            val mark = translateInttoAssessment(scanner.nextInt())

            assessment_service.UpdateAssessment(
                UpdateAssessmentRequest(
                    student_id, subject_id, teacher!!.TeacherId,
                    mark, date
                )
            )

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun watch_assassment_class() {
        val scanner = Scanner(System.`in`)

        try {
            val schedule = schedule_service.GetScheduleForTeacher(GetScheduleForTeacherRequest(teacher!!.TeacherId))
            println("\nВаши классы: ")
            schedule.res_Schedule.distinctBy { it.ClassId }.forEachIndexed { index, element ->
                println("${index + 1}): ${element.ClassId}")
            }

            println("\nВведите id класса:")
            val class_id = scanner.nextInt()

            println("\nВаши предметы: ")
            schedule.res_Schedule.distinctBy { it.SubjectId }.forEachIndexed { index, element ->
                println("${index + 1}): ${element.SubjectId}")
            }

            println("\nВведите id предмета:")
            val subject_id = scanner.nextInt()

            println("\nВведите начальную дату в формате ГГГГ-ММ-ДД:")
            val date_start = scanner.next()
            if (!isValidDateFormat(date_start))
                throw DataException()

            println("\nВведите конечную дату в формате ГГГГ-ММ-ДД:")
            val date_finish = scanner.next()
            if (!isValidDateFormat(date_finish))
                throw DataException()

            val students = student_service.GetStudents(GetStudentsRequest(class_id))

            if (students.res_Students.isEmpty())
                throw ClassStudentException()

            println("\nУченики в классе $class_id: ")
            students.res_Students.forEachIndexed { index, element ->
                println("${index + 1}) ${element.StudentId}")
                val assassment = assessment_service.GetStudentAssessments(GetAssessmentsRequest(element.StudentId,
                    subject_id, Date.valueOf(date_start), Date.valueOf(date_finish)))
                if (assassment.res_Assessment.isNotEmpty()) {
                    assassment.res_Assessment.filter { it.SubjectId == subject_id }.forEachIndexed { index_mark, mark ->
                        println("\t${index_mark + 1}) ${mark.Date} --- ${translateAssessmentToInt(mark.Assessment)}")
                    }
                }
                else
                    println("У ученика нет оценок")
            }

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun watch_assassment_student() {
        val scanner = Scanner(System.`in`)

        try {
            val (student_id, subject_id) = prepare_assessment(teacher!!.TeacherId)

            println("\nВведите начальную дату в формате ГГГГ-ММ-ДД:")
            val date_start = scanner.next()
            if (!isValidDateFormat(date_start))
                throw DataException()

            println("\nВведите конечную дату в формате ГГГГ-ММ-ДД:")
            val date_finish = scanner.next()
            if (!isValidDateFormat(date_finish))
                throw DataException()

            println("${student_id}:")
            val assassment = assessment_service.GetStudentAssessments(GetAssessmentsRequest(student_id,
                subject_id, Date.valueOf(date_start), Date.valueOf(date_finish)))
            if (assassment.res_Assessment.isNotEmpty()) {
                assassment.res_Assessment.filter { it.SubjectId == subject_id }.forEachIndexed { index_mark, mark ->
                    println("\t${index_mark + 1}) ${mark.Date} --- ${translateAssessmentToInt(mark.Assessment)}")
                }
            }
            else
                println("Ученик не получил оценок.")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun viewSchedule_teacher() {
        try {
            val schedule = schedule_service.GetScheduleForTeacher(GetScheduleForTeacherRequest(teacher!!.TeacherId))

            print_schedule(schedule.res_Schedule)
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }


    private fun view_all_users() {
        try {
            val users = admin_service.ViewAllUsers()

            println("\nУчителя:")
            users.filter { it.RoleId == 1 }.forEachIndexed { index, user ->
                println("\t${index + 1}) ${user.UserId} ${user.FirstName} ${user.LastName} ${user.Patronymic} ${user.Birthday} ${user.Gender} ${user.Phone}")
            }

            println("\nУченики:")
            users.filter { it.RoleId == 2 }.forEachIndexed { index, user ->
                println("\t${index + 1}) ${user.UserId} ${user.FirstName} ${user.LastName} ${user.Patronymic} ${user.Birthday} ${user.Gender} ${user.Phone}")
            }

            println("\nАдминистраторы:")
            users.filter { it.RoleId == 3 }.forEachIndexed { index, user ->
                println("\t${index + 1}) ${user.UserId} ${user.FirstName} ${user.LastName} ${user.Patronymic} ${user.Birthday} ${user.Gender} ${user.Phone}")
            }

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun add_student() {
        try {
            println("\nВведите дату поступления в формате ГГГГ-ММ-ДД:")
            val date_start = scanner.next()
            if (!isValidDateFormat(date_start))
                throw DataException()

            println("\nВведите фамилию ученика:")
            val last_name = scanner.next().capitalize()

            println("\nВведите имя ученика:")
            val first_name = scanner.next().capitalize()

            println("\nВведите отчество ученика:")
            val patronymic = scanner.next().capitalize()

            println("\nВведите день рождения в формате ГГГГ-ММ-ДД:")
            val birthday = scanner.next()
            if (!isValidDateFormat(birthday))
                throw DataException()

            val classes = class_service.GetAllClasses()
            if (classes.isEmpty())
                throw GetClassesException()
            println("\nКлассы: ")
            for (cl in classes) {
                println("${cl.ClassId}) ${cl.Number.ordinal + 1}${cl.Letter}")
            }

            println("\nВведите id класса:")
            val classID = scanner.nextInt()

            println("\nВведите номер телефона ученика (например +79999999999):")
            val phone = scanner.next()

            println("\nВведите гендер ученика (1 - мужчина, 2 - женщина):")
            val gender = translateToGender(scanner.nextInt())

            println("\nВведите группу ученика (1 - первая группа, 2 - вторая группа):")
            val group = translateToGroup(scanner.nextInt())

            val error = student_service.CreateStudent(CreateStudentRequest(1, Date.valueOf(date_start),
                first_name, last_name, patronymic, Date.valueOf(birthday), classID, Phone = phone, Gender = gender,
                Group = group))

            if (error == 0)
                println("Ученик был успешно добавлен")
            else
                println("Ученик не был добавлен")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun delete_student() {
        try {
            val users = admin_service.ViewAllUsers()

            println("\nУченики:")
            users.filter { it.RoleId == 2 }.forEachIndexed { index, user ->
                println("\t${index + 1}) ${user.UserId} ${user.FirstName} ${user.LastName} ${user.Patronymic} ${user.Birthday} ${user.Gender} ${user.Phone}")
            }

            println("\nВведите id ученика:")
            val student_id = scanner.nextInt()

            val error = student_service.DeleteStudent(DeleteStudentRequest(student_id))

            if (error == 0)
                println("Ученик был успешно удален")
            else
                println("Ученик не был удален")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun add_teacher() {
        try {
            println("\nВведите дату начала работы в формате ГГГГ-ММ-ДД:")
            val date_start = scanner.next()
            if (!isValidDateFormat(date_start))
                throw DataException()

            println("\nВведите фамилию учителя:")
            val last_name = scanner.next().capitalize()

            println("\nВведите имя учителя:")
            val first_name = scanner.next().capitalize()

            println("\nВведите отчество учителя:")
            val patronymic = scanner.next().capitalize()

            println("\nВведите день рождения в формате ГГГГ-ММ-ДД:")
            val birthday = scanner.next()
            if (!isValidDateFormat(birthday))
                throw DataException()

            println("\nВведите номер телефона учителя (например +79999999999):")
            val phone = scanner.next()

            println("\nВведите гендер учителя (1 - мужчина, 2 - женщина):")
            val gender = translateToGender(scanner.nextInt())

            println("\nВведите кабинет учителя:")
            val cabinet = scanner.nextInt()

            val error = teacher_service.CreateTeacher(CreateTeacherRequest(1, Date.valueOf(date_start),
                first_name, last_name, patronymic, Date.valueOf(birthday), Phone = phone, Gender = gender,
                Cabinet = cabinet))

            if (error == 0)
                println("Учитель был успешно добавлен")
            else
                println("Учитель не был добавлен")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun delete_teacher() {
        try {
            val users = admin_service.ViewAllUsers()

            println("\nУчителя:")
            users.filter { it.RoleId == 1 }.forEachIndexed { index, user ->
                println("\t${index + 1}) ${user.UserId} ${user.FirstName} ${user.LastName} ${user.Patronymic} ${user.Birthday} ${user.Gender} ${user.Phone}")
            }

            println("\nВведите id учителя:")
            val teacher_id = scanner.nextInt()

            val error = teacher_service.DeleteTeacher(DeleteTeacherRequest(teacher_id))

            if (error == 0)
                println("Учитель был успешно удален")
            else
                println("Учитель не был удален")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun add_admin() {
        try {
            println("\nВведите фамилию администратора:")
            val last_name = scanner.next().capitalize()

            println("\nВведите имя администратора:")
            val first_name = scanner.next().capitalize()

            println("\nВведите отчество администратора:")
            val patronymic = scanner.next().capitalize()

            println("\nВведите день рождения в формате ГГГГ-ММ-ДД:")
            val birthday = scanner.next()
            if (!isValidDateFormat(birthday))
                throw DataException()

            println("\nВведите номер телефона администратора (например +79999999999):")
            val phone = scanner.next()

            println("\nВведите гендер администратора (1 - мужчина, 2 - женщина):")
            val gender = translateToGender(scanner.nextInt())

            val error = admin_service.CreateAdmin(
                CreateAdminRequest(1, first_name, last_name, patronymic, Date.valueOf(birthday),
                Phone = phone, Gender = gender,)
            )

            if (error == 0)
                println("Администратор был успешно добавлен")
            else
                println("Администратор не был добавлен")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun delete_admin() {
        try {
            val users = admin_service.ViewAllUsers()

            println("\nАдминистраторы:")
            users.filter { it.RoleId == 3 }.forEachIndexed { index, user ->
                println("\t${index + 1}) ${user.UserId} ${user.FirstName} ${user.LastName} ${user.Patronymic} ${user.Birthday} ${user.Gender} ${user.Phone}")
            }

            println("\nВведите id администратора:")
            val teacher_id = scanner.nextInt()

            val error = admin_service.DeleteAdmin(DeleteAdminRequest(teacher_id))

            if (error == 0)
                println("Администратор был успешно удален")
            else
                println("Администратор не был удален")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun add_schedule() {
        try {
            println("\nВведите день недели (1 - понедельник, 2 - вторник, ...):")
            val dayOfWeek = translateIntToDayOfWeek(scanner.nextInt())

            println("\nВведите номер урока:")
            val lesson_number = scanner.nextInt()
            if (lesson_number > 10)
                throw SoBigNUmberOfLessonException()

            println("\nВведите группу (1- первая, 2 - вторая, 3 - совместный урок):")
            val group = translateIntToGroup(scanner.nextInt())

            val classes = class_service.GetAllClasses()
            if (classes.isEmpty())
                throw GetClassesException()
            println("\nКлассы: ")
            for (cl in classes) {
                println("${cl.ClassId}) ${cl.Number.ordinal + 1}${cl.Letter}")
            }

            println("\nВведите id класса:")
            val class_id = scanner.nextInt()

            val subjects = subject_service.GetSubjects()
            if (subjects.isEmpty())
                throw GetSubjectsException()
            println("\nПредметы: ")
            for (subject in subjects) {
                println("${subject.SubjectId}) ${subject.Name}")
            }

            println("\nВведите id предмета:")
            val subject_id = scanner.nextInt()

            val teachers = teacher_service.GetTeacherBySubject(GetTeacherBySubjectRequest(subject_id))
            println("\nУчителя:")
            if (teachers.isEmpty())
                throw GetTeachersException()

            for (teacher in teachers) {
                println("${teacher.TeacherId}) ${teacher.LastName} ${teacher.FirstName} ${teacher.Patronymic}")
            }

            println("\nВведите id учителя:")
            val teacher_id = scanner.nextInt()

            val error = schedule_service.CreateSchedule(
                CreateScheduleRequest(dayOfWeek, lesson_number, group, class_id, subject_id, teacher_id)
            )

            if (error == 0)
                println("Урок был успешно добавлен")
            else
                println("Урок не был добавлен")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun delete_schedule() {
        try {
            val classes = class_service.GetAllClasses()
            if (classes.isEmpty())
                throw GetClassesException()
            println("\nКлассы: ")
            for (cl in classes) {
                println("${cl.ClassId}) ${cl.Number.ordinal + 1}${cl.Letter}")
            }
            println("\nВведите id класса:\n")
            val class_id = scanner.nextInt()

            val schedule = schedule_service.GetScheduleForClass(GetScheduleForClassRequest(class_id)).res_Schedule
            print_schedule(schedule)

            println("\nВведите день недели (1 - понедельник, 2 - вторник, ...):")
            val dayOfWeek = translateIntToDayOfWeek(scanner.nextInt())

            println("\nВведите номер урока класса:")
            val lesson_number = scanner.nextInt()

            val error = schedule_service.DeleteSchedule(DeleteScheduleRequest(dayOfWeek, lesson_number, class_id))

            if (error == 0)
                println("Урое был успешно удален")
            else
                println("Урок не был удален")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun add_class() {
        try {
            println("\nВведите номер класса (от 1 до 11):")
            val class_number = ClassNumber.entries.getOrNull(scanner.nextInt() - 1) ?: throw ClassNumberException()

            println("\nВведите букву класса:")
            val class_letter = scanner.next().toUpperCase()
            if (!class_letter.matches(Regex("[А-ЯA-Z]")))
                throw CountLettersException()

            val error = class_service.CreateClass(
                CreateClassRequest(1, class_letter, class_number)
            )

            if (error == 0)
                println("Класс был успешно добавлен")
            else
                println("Класс не был добавлен")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun delete_class() {
        try {
            val classes = class_service.GetAllClasses()
            if (classes.isEmpty())
                throw GetClassesException()
            println("\nКлассы: ")
            for (cl in classes) {
                println("${cl.ClassId}) ${cl.Number.ordinal + 1}${cl.Letter}")
            }
            println("\nВведите id класса для удаления:")
            val class_id = scanner.nextInt()

            val error = class_service.DeleteClass(DeleteClassRequest(class_id))

            if (error == 0)
                println("\nКласс был успешно удален")
            else
                println("\nКласс не был удален")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun add_subject() {
        try {
            println("\nВведите название предмета:")
            val name_subject = scanner.next().capitalize()

            println("\nПредмет делится на группы (1 - да, 2 - нет):")
            val count_teachers = Count.entries.getOrNull(scanner.nextInt() - 1) ?: throw CountTeachersException()

            val error = subject_service.CreateSubject(
                CreateSubjectRequest(1, name_subject, count_teachers)
            )

            if (error == 0)
                println("Предмет был успешно добавлен")
            else
                println("Предмет не был добавлен")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun delete_subject() {
        try {

            val subjects = subject_service.GetSubjects()
            if (subjects.isEmpty())
                throw GetSubjectsException()
            println("\nПредметы: ")
            for (subject in subjects) {
                println("${subject.SubjectId}) ${subject.Name}")
            }
            println("\nВведите id предмета для удаления:")
            val subject_id = scanner.nextInt()

            val error = subject_service.DeleteSubject(DeleteSubjectRequest(subject_id))

            if (error == 0)
                println("Предмет был успешно удален")
            else
                println("Предмет не был удален")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun add_subject_to_teacher() {
        try {
            val users = admin_service.ViewAllUsers()
            println("\nУчителя:")
            users.filter { it.RoleId == 1 }.forEachIndexed { index, user ->
                println("\t${index + 1}) ${user.UserId} ${user.FirstName} ${user.LastName} ${user.Patronymic} ${user.Birthday} ${user.Gender} ${user.Phone}")
            }
            println("\nВведите id учителя:")
            val teacher_id = scanner.nextInt()

            val subjects = subject_service.GetSubjects()
            if (subjects.isEmpty())
                throw GetSubjectsException()
            println("\nПредметы: ")
            for (subject in subjects) {
                println("${subject.SubjectId}) ${subject.Name}")
            }
            println("\nВведите id предмета:")
            val subject_id = scanner.nextInt()

            val error = teacher_service.AddSubjectToTeacher(
                AddSubjectToTeacherRequest(teacher_id, subject_id)
            )

            if (error == 0)
                println("Учителю был успешно добавлен предмет")
            else
                println("Учителю не был добавлен предмет")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun delete_subject_from_teacher() {
        try {
            val users = admin_service.ViewAllUsers()
            println("\nУчителя:")
            users.filter { it.RoleId == 1 }.forEachIndexed { index, user ->
                println("\t${index + 1}) ${user.UserId} ${user.FirstName} ${user.LastName} ${user.Patronymic} ${user.Birthday} ${user.Gender} ${user.Phone}")
            }
            println("\nВведите id учителя:")
            val teacher_id = scanner.nextInt()

            val subjects = subject_service.GetSubjects()
            if (subjects.isEmpty())
                throw GetSubjectsException()
            println("\nПредметы: ")
            for (subject in subjects) {
                println("${subject.SubjectId}) ${subject.Name}")
            }
            println("\nВведите id предмета:")
            val subject_id = scanner.nextInt()

            val error = teacher_service.DeleteSubjectFromTeacher(DeleteSubjectFromTeacherRequest(teacher_id, subject_id))

            if (error == 0)
                println("У учителя был успешно удален предмет")
            else
                println("У учителя не был удален предмет")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun add_book() {
        try {
            println("\nВведите название книги:")
            val title = scanner.next()

            println("\nВведите автора книги:")
            val author = scanner.next()

            println("\nВведите издательство, выпустившее книгу:")
            val publisher = scanner.next()

            println("\nВведите дату издания в формате ГГГГ-ММ-ДД:")
            val date_publish = scanner.next()
            if (!isValidDateFormat(date_publish))
                throw DataException()

            println("\nВведите количество полученных книг:")
            val count = scanner.nextInt()

            val error = library_service.CreateBook(
                CreateBookRequest(1, title, author, Date.valueOf(date_publish), publisher, count)
            )

            if (error == 0)
                println("Книга была успешно добавлена")
            else
                println("Книга не был добавлена")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

    private fun delete_book() {
        try {
            println("\nВведите название книги для поиска:")
            val book_name = scanner.next()

            val books = library_service.GetBooks(GetBooksRequest(book_name))
            println("\nКниги:")
            if(books.res_Books.isEmpty())
                throw GetBookException()
            books.res_Books.forEachIndexed { index, book ->
                println("\t${index + 1}) ${book.BookId} ${book.Title} ${book.Count}")
            }

            println("\nВведите id книги для удаления:")
            val book_id = scanner.nextInt()

            val error = library_service.DeleteBook(DeleteBookRequest(book_id))

            if (error == 0)
                println("Книга была успешно удалена")
            else
                println("Книга не был удалена")

        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }

}