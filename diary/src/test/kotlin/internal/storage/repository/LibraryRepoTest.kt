package internal.storage.repository

import PostgresDBConnector
import internal.service.dto.*
import logger.Logger
import model.StudentAssessment
import org.example.internal.storage.repository.AssessmentRepo
import org.example.internal.storage.repository.LibraryRepo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.sql.Date

class LibraryRepoTest {

    @Test
    fun createBook() {
        var request_create_1 = CreateBookRequest(3, "NoTitle", "author", Date.valueOf("2023-11-13"), "publ", 0)
        var sut = LibraryRepo(Logger(), PostgresDBConnector())
        var actual = sut.CreateBook(request_create_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun getBook() {
        var request_get_1 = GetBookRequest(1)
        var sut = LibraryRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetBook(request_get_1)
        println(actual.res_Book)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun getBooks() {
        var request_get_1 = GetBooksRequest("t")
        var sut = LibraryRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetBooks(request_get_1)
        println(actual.res_Books)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun takeBook() {
        var request_take_1 = TakeBookRequest(1, 3)
        var sut = LibraryRepo(Logger(), PostgresDBConnector())
        var actual = sut.TakeBook(request_take_1)
        var expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun returnBook() {
        var request_return_1 = ReturnBookRequest(1, 1)
        var sut = LibraryRepo(Logger(), PostgresDBConnector())
        var actual = sut.ReturnBook(request_return_1)
        var expected = 0
        assertEquals(expected, actual)
    }

//    @Test
//    fun deleteBook() {
//        var request_delete_1 = DeleteBookRequest(3)
//        var sut = LibraryRepo(Logger(), PostgresDBConnector())
//        var actual = sut.DeleteBook(request_delete_1)
//        var expected = 0
//        assertEquals(expected, actual)
//    }

    @Test
    fun getStudentBooks() {
        var request_get_1 = GetStudentBooks(3)
        var sut = LibraryRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetStudentBooks(request_get_1)
        println(actual.res_Books)
        var expected = 0
        assertEquals(expected, actual.error)
    }

    @Test
    fun getStudentHaveBook() {
        var request_get_1 = GetStudentHaveBook(1)
        var sut = LibraryRepo(Logger(), PostgresDBConnector())
        var actual = sut.GetStudentHaveBook(request_get_1)
        println(actual.res_Books)
        var expected = 0
        assertEquals(expected, actual.error)
    }
}