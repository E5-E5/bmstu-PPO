package com.example.service

import com.example.logging.Log4jLogger
import com.example.service.dto.*
import com.example.repository_interface.*
import com.example.logger.Logger
import com.example.*
import java.time.LocalDate

interface ILibraryService {
    fun CreateBook(request: CreateBookRequest): Int
    fun GetBook(request: GetBookRequest): get_book
    fun GetBooks(request: GetBooksRequest): get_books
    fun TakeBook(request: TakeBookRequest): Int
    fun ReturnBook(request: ReturnBookRequest): Int
    fun DeleteBook(request: DeleteBookRequest): Int
    fun GetStudentBooks(request: GetStudentBooks): get_books
}

class LibraryService (
    private val log: Logger,
    private val repository: LibraryStorage,
    private val rep_student: StudentStorage
): ILibraryService {
    private val res_error = 3
    private val res_successful = 0
    private val error_count = 0
    private val logger = Log4jLogger(LibraryService::class.java)

    override fun GetBook(request: GetBookRequest): get_book {
        return try {
            val (res_book, error) = repository.GetBook(request)
            logger.info("Book was got. ID book ${request.BookId}")
            get_book(res_book, error)
        } catch (e: Exception){
            logger.warn("Error while get book. ID book ${request.BookId}")
            throw Exception("Error while get book")
        }
    }

    override fun GetBooks(request: GetBooksRequest): get_books {
        return try {
            val (res_books, error) = repository.GetBooks(request)
            logger.info("Books were got")
            get_books(res_books, error)
        } catch (e: Exception){
            logger.warn("Error while get books. ${request.Title}")
            throw Exception("Error while get books")
        }
    }

    override fun CreateBook(request: CreateBookRequest): Int {
        if(request.Count <= error_count) {
            logger.warn("Error with book count. ${request.Title}")
            throw CountBookException()
        }

        if(request.DatePublisher.toLocalDate().isAfter(LocalDate.now())) {
            logger.warn("Error with date")
            throw PublicationDateIncorrectException()
        }

        val error = repository.CreateBook(request)
        logger.info("Book was created. Book ${request.Title}")

        return error
    }

    override fun GetStudentBooks(request: GetStudentBooks): get_books {
        logger.info("Get student books. ID student ${request.StudentId}")

        if(rep_student.GetStudent(GetStudentRequest(request.StudentId)).error != res_successful)
            return get_books(listOf(), res_error)

        val (res_books, error) = repository.GetStudentBooks(request)
        return get_books(res_books, error)
    }

    private fun TakeRequest(StudentId: Int, BookId: Int): Int {
        logger.warn("Error while take book. ID student ${StudentId}, book ${BookId}")
        return res_error
    }

    override fun TakeBook(request: TakeBookRequest): Int {
        val res_books = GetStudentBooks(GetStudentBooks(request.StudentId)).res_Books

        if(res_books.find { it.BookId == request.BookId } != null) {
            logger.warn("Error while take book. ID student ${request.StudentId}, book ${request.BookId}")
            throw BookAlreadyTakenException()
        }

        repository.GetBook(GetBookRequest(request.BookId)).res_Book ?: return TakeRequest(request.StudentId, request.BookId)

        val error = repository.TakeBook(request)
        logger.info("Book was taken. ID student ${request.StudentId}, book ${request.BookId}")
        return error
    }

    override fun ReturnBook(request: ReturnBookRequest): Int {
        var (res_books, error) = GetStudentBooks(GetStudentBooks(request.StudentId))
        if(error != res_successful) {
            logger.warn("Error while get student book. ID student ${request.StudentId}, book ${request.BookId}")
            throw StudentNotHaveBookException()
        }

        if(res_books.find { it.BookId == request.BookId } == null) {
            logger.warn("Error while return no taken book. ID student ${request.StudentId}, book ${request.BookId}")
            throw BookNotTakenException()
        }

        error = repository.ReturnBook(request)
        logger.info("Book was returned. ID student ${request.StudentId}, book ${request.BookId}")
        return error
    }

    override fun DeleteBook(request: DeleteBookRequest): Int {
        if(repository.GetStudentHaveBook(GetStudentHaveBook(request.BookId)).res_Books.isNotEmpty()) {
            logger.warn("Error while get student books. ID book ${request.BookId}")
            throw StudentHaveBookException()
        }
        val error = repository.DeleteBook(request)
        logger.info("Book was deleted. ID book ${request.BookId}")
        return error
    }
}