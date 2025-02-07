package com.example

class ConnectBDException : Exception("Не удалось установить подключение к базе данных.")

class AddStudentException : Exception("Не удалось добавить ученика.")

class GetStudentException : Exception("Ученик не найден.")
class GetTeacherException : Exception("Учитель не найден.")
class GetClassException : Exception("Класс не найден.")
class GetSubjectException : Exception("Предмет не найден.")
class GetBookException : Exception("Книги не найдены.")

class GetStudentsException : Exception("Ученики из класса не найдены.")
class GetClassesException : Exception("Классы не найдены.")
class GetTeachersException : Exception("Учителя по данному предмету не найдены.")
class GetSubjectsException : Exception("Предметы не найдены.")

class UpdateStudentException : Exception("Не удалось обновить данные ученика.")

class DeleteStudentException : Exception("Не удалось удалить ученика.")

class DateStartStudyException : Exception("Неверно указана дата начала обучения.")

class SingInException : Exception("Неверный логин или пароль.")


class CreateAssessmentException : Exception("Не удалось добавить оценку.")

class UpdateAssessmentException : Exception("Не удалось изменить оценку.")

class DeleteAssessmentException : Exception("Не удалось удалить оценку.")


class NoBooksAvailableException : Exception("Нет в наличии данной книги.")

class PublicationDateIncorrectException : Exception("Неверно указана дата публикации.")

class CreateBookException : Exception("Не удалость добавить книгу в библиотеку.")

class BookAlreadyTakenException : Exception("Вы уже оформили данную книгу.")

class BookNotTakenException : Exception("Вы не оформляли данную книгу.")

class StudentHaveBookException : Exception("Не все ученики вернули данную книгу.")


class CreateScheduleException : Exception("Не удалось создать расписание.")

class DeleteScheduleException : Exception("Не удалось удалить расписание.")

class MaxCountSubjectException : Exception("Превышено максимальное количество уроков.")

class ClassAlreadyHaveLessonException : Exception("Уже существует урок на указанное время у класса.")

class TeacherOnSubjectException : Exception("Указан неверный учитель.")

class TeacherAlreadyHaveLessonException : Exception("Уже существует урок на указанное время у учителя.")

class ClassNotHaveLessonException : Exception("Класс не имеет урока на данное время.")


class IncorrectAssessmentException : Exception("Неверно указана оценка.")

class TeacherTeachSubjectException : Exception("Учитель не преподает данный предмет.")

class SubjectInScheduleException : Exception("Неверно указана дата урока.")

class DataException : Exception("Неверно указана дата получения оценки.")

class StudentHaveAssessmentException : Exception("У ученика нет полученной оценки на указанную дату.")

class CreateAssessmentSameException : Exception("Ученик уже получил оценку на указанном уроке.")


class ClassStudentException : Exception("В классе нет учеников.")

class TeacherHaveScheduleException : Exception("У учителя еще нет рассписания.")
class ClassHaveScheduleException : Exception("У класса еще нет рассписания.")

class StudentGroupGetException : Exception("Неверно указана группа ученика.")
class StudentGenderGetException : Exception("Неверно указан гендер ученика.")
class DayOfWeekGetException : Exception("Неверно указан день недели.")
class ScheduleGroupGetException : Exception("Неверна группа класса.")

class PhoneNumberException : Exception("Неверно указан номер телефона.")

class BirthdayException : Exception("Неверно указан день рождения.")

class StudentNotHaveBookException : Exception("У ученика нет оформленных книг.")

class CreateSameSubjectException : Exception("Данный предмет уже был добавлен.")

class StartTeachException : Exception("Неверно указана дата начала преподавания.")

class CountLettersException : Exception("Неверно введено количество букв.")
class ClassNumberException : Exception("Неверно введен номер класса.")
class CountTeachersException : Exception("Неверно введено количество подгрупп для предмета.")
class ClassExistException : Exception("Данный класс уже существует.")

class TeacherAlreadyHaveSubjectException: Exception("Учить уже преподает данный предмет")

class TeacherNotTeachSubjectException: Exception("Данный учитель не преподает этот предмет")

class SoBigNUmberOfLessonException: Exception("Превышен максимальный номер урока (10)")

class CountBookException: Exception("Неверное количество книг")
class LoadConfigError : Exception("Error load configuration file")

