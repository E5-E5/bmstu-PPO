package internal.service

import internal.service.dto.*
import internal.storage.*
import logger.Logger
import model.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.awt.print.Book
import java.sql.Date

class LibraryServiceTest {
    private val mockLibraryRepository = Mockito.mock(LibraryStorage::class.java)
    private val mockStudentRepository = Mockito.mock(StudentStorage::class.java)
    private val allBooks = listOf(
        Library(1, "A", "A", Date.valueOf("2003-11-13"), "A", 2),
        Library(2, "B", "B", Date.valueOf("2003-11-13"), "B", 4),
        Library(3, "C", "C", Date.valueOf("2003-11-13"), "C", 1)
    )

    @Test
    fun getBook_OK() {
        var request_1 = GetBookRequest(1)
        Mockito.`when`(mockLibraryRepository.GetBook(request_1)).thenReturn(
            get_book(allBooks.filter { it.BookId == request_1.BookId }.get(0), 0)
        )

        var expected: Library? = Library(1, "A", "A", Date.valueOf("2003-11-13"), "A", 2)

        var sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        var actual = sut.GetBook(request_1).res_Book
        assertEquals(expected, actual)
    }
    @Test
    fun getBook_BookError() {
        var request_2 = GetBookRequest(4)
        Mockito.`when`(mockLibraryRepository.GetBook(request_2)).thenReturn(
            get_book(null, 3))
        val expected = null

        val sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        val actual = sut.GetBook(request_2).res_Book
        assertEquals(expected, actual)
    }

    @Test
    fun getBooks_OK() {
        var request_1 = GetBooksRequest("A")
        Mockito.`when`(mockLibraryRepository.GetBooks(request_1)).thenReturn(
            get_books(allBooks.filter { Regex(request_1.Title).containsMatchIn(it.Title) }, 0)
        )
        var expected = 0

        var sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        var actual = sut.GetBooks(request_1).error
        assertEquals(expected, actual)
    }
    @Test
    fun getBooks_Error() {
        var request_2 = GetBooksRequest("D")
        Mockito.`when`(mockLibraryRepository.GetBooks(request_2)).thenReturn(
            get_books(allBooks.filter { Regex(request_2.Title).containsMatchIn(it.Title) }, 3)
        )
        val expected = listOf<Library>()

        val sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        val actual = sut.GetBooks(request_2).res_Books
        assertEquals(expected, actual)
    }

    @Test
    fun createBook_OK() {
        var request_1 = CreateBookRequest(1, "E", "E", Date.valueOf("2003-11-13"), "E", 5)
        var request_11 = GetBookRequest(1)
        Mockito.`when`(mockLibraryRepository.CreateBook(request_1)).thenReturn(0)
        Mockito.`when`(mockLibraryRepository.GetBook(request_11)).thenReturn(get_book(null, 3))
        var expected = 0

        var sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        var actual = sut.CreateBook(request_1)
        assertEquals(expected, actual)
    }
    @Test
    fun createBook_CountError() {

        var request_2 = CreateBookRequest(1, "E", "E", Date.valueOf("2003-11-13"), "E", -5)
        val expected = 3

        val sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        val actual = sut.CreateBook(request_2)
        assertEquals(expected, actual)
    }
    @Test
    fun createBook_BookError() {
        var request_3 = CreateBookRequest(1, "E", "E", Date.valueOf("2003-11-13"), "E", 5)
        var request_33 = GetBookRequest(1)
        Mockito.`when`(mockLibraryRepository.GetBook(request_33)).thenReturn(get_book(Library(1, "E", "E", Date.valueOf("2003-11-13"), "E", 5), 0))
        val expected = 3

        val sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        val actual = sut.CreateBook(request_3)
        assertEquals(expected, actual)
    }

    @Test
    fun takeBook_OK() {
        //test 1. OK
        var request_take_1 = TakeBookRequest(1, 1)
        var request_get_student_1 = GetStudentRequest(1)
        var request_get_student_books_1 = GetStudentBooks(1)
        var request_get_book_1 = GetBookRequest(1)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_1)).thenReturn(
            get_student(
                Student(
                    1, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        Mockito.`when`(mockLibraryRepository.GetStudentBooks(request_get_student_books_1))
            .thenReturn(get_books(listOf(), 0))
        Mockito.`when`(mockLibraryRepository.GetBook(request_get_book_1)).thenReturn(
            get_book(Library(1, "E", "E", Date.valueOf("2003-11-13"), "E", 5), 0)
        )
        Mockito.`when`(mockLibraryRepository.TakeBook(request_take_1)).thenReturn(0)

        var expected = 0

        var sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        var actual = sut.TakeBook(request_take_1)
        assertEquals(expected, actual)
    }
    @Test
    fun takeBook_AlreadyTaken() {
        //test 2. book already taken
        var request_take_2 = TakeBookRequest(1, 1)
        var request_get_student_2 = GetStudentRequest(1)
        var request_get_student_books_2 = GetStudentBooks(1)
        var request_get_book_2 = GetBookRequest(1)

        Mockito.`when`(mockStudentRepository.GetStudent(request_get_student_2)).thenReturn(
            get_student(
                Student(
                    1, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                    Date.valueOf("2023-11-13"), 1, "12345", "a1s2", "+79743895929",
                    Gender.MALE, GroupStudent.FIRST
                ), 0
            )
        )
        Mockito.`when`(mockLibraryRepository.GetStudentBooks(request_get_student_books_2)).thenReturn(
            get_books(listOf(Library(1, "E", "E", Date.valueOf("2003-11-13"), "E", 5)), 0)
        )
        Mockito.`when`(mockLibraryRepository.GetBook(request_get_book_2)).thenReturn(
            get_book(Library(1, "E", "E", Date.valueOf("2003-11-13"), "E", 5), 0)
        )
        Mockito.`when`(mockLibraryRepository.TakeBook(request_take_2)).thenReturn(0)

        val expected = 3

        val sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        val actual = sut.TakeBook(request_take_2)
        assertEquals(expected, actual)
    }


    @Test
    fun returnBook_OK() {
        var request_1 = ReturnBookRequest(1, 1)
        var request_11 = GetBookRequest(1)
        var request_1get_student = GetStudentRequest(1)
        val request_getStudentBook = GetStudentBooks(1)
        Mockito.`when`(mockLibraryRepository.GetBook(request_11)).thenReturn(
            get_book(Library(1, "E", "E", Date.valueOf("2003-11-13"), "E", 5), 0)
        )
        Mockito.`when`(mockStudentRepository.GetStudent(request_1get_student)).thenReturn(
            get_student(Student(
                    1, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                Date.valueOf("2023-11-13"), 2, "12345", "a1s2", "+79743895929",
                Gender.MALE, GroupStudent.FIRST), 0))
        Mockito.`when`(mockLibraryRepository.GetStudentBooks(request_getStudentBook)).thenReturn(
            get_books(listOf(Library(1, "E", "E", Date.valueOf("2003-11-13"), "E", 5)), 0)
        )

        Mockito.`when`(mockLibraryRepository.ReturnBook(request_1)).thenReturn(0)
        var expected = 0

        var sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        var actual = sut.ReturnBook(request_1)
        assertEquals(expected, actual)
    }
    @Test
    fun returnBook_StudentNotTakeBook() {
        var request_1 = ReturnBookRequest(1, 1)
        var request_11 = GetBookRequest(1)
        var request_1get_student = GetStudentRequest(1)
        val request_getStudentBook = GetStudentBooks(1)
        Mockito.`when`(mockLibraryRepository.GetBook(request_11)).thenReturn(
            get_book(Library(1, "E", "E", Date.valueOf("2003-11-13"), "E", 5), 0)
        )
        Mockito.`when`(mockStudentRepository.GetStudent(request_1get_student)).thenReturn(
            get_student(Student(
                1, Date.valueOf("2003-11-13"), "st1", "f_st1", "pat_st1",
                Date.valueOf("2023-11-13"), 2, "12345", "a1s2", "+79743895929",
                Gender.MALE, GroupStudent.FIRST), 0))
        Mockito.`when`(mockLibraryRepository.GetStudentBooks(request_getStudentBook)).thenReturn(
            get_books(listOf(), 3)
        )

        Mockito.`when`(mockLibraryRepository.ReturnBook(request_1)).thenReturn(0)
        var expected = 3

        var sut = LibraryService(Logger(), mockLibraryRepository, mockStudentRepository)
        var actual = sut.ReturnBook(request_1)
        assertEquals(expected, actual)
    }
}