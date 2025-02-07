package internal.service

import internal.service.dto.*
import internal.storage.*
import logger.Logger
import org.example.`package`.exceptions.*
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
    private val logger: Logger,
    private val repository: LibraryStorage,
    private val rep_student: StudentStorage
): ILibraryService {
    private val res_error = 3
    private val res_successful = 0
    private val error_count = 0

    override fun GetBook(request: GetBookRequest): get_book {
        val (res_book, error) = repository.GetBook(request)

        logger.logMessage(error, "Get (one): Library")

        return get_book(res_book, error)
    }

    override fun GetBooks(request: GetBooksRequest): get_books {
        val (res_books, error) = repository.GetBooks(request)

        logger.logMessage(error, "Get (list): Library")

        return get_books(res_books, error)
    }

    override fun CreateBook(request: CreateBookRequest): Int {
        if(request.Count <= error_count)
            throw CountBookException()

        if(request.DatePublisher.toLocalDate().isAfter(LocalDate.now()))
            throw PublicationDateIncorrectException()

        val error = repository.CreateBook(request)
        logger.logMessage(error, "Create: Book")

        return error
    }

    override fun GetStudentBooks(request: GetStudentBooks): get_books {
        if(rep_student.GetStudent(GetStudentRequest(request.StudentId)).error != res_successful)
            return get_books(listOf(), res_error)

        val (res_books, error) = repository.GetStudentBooks(request)
        logger.logMessage(error, "Get student books: Book")

        return get_books(res_books, error)
    }

    override fun TakeBook(request: TakeBookRequest): Int {
        val res_books = GetStudentBooks(GetStudentBooks(request.StudentId)).res_Books

        if(res_books.find { it.BookId == request.BookId } != null)
            throw BookAlreadyTakenException()

        repository.GetBook(GetBookRequest(request.BookId)).res_Book ?: return res_error

        val error = repository.TakeBook(request)
        logger.logMessage(error, "Take: Book")

        return error
    }

    override fun ReturnBook(request: ReturnBookRequest): Int {
        var (res_books, error) = GetStudentBooks(GetStudentBooks(request.StudentId))
        if(error != res_successful)
            throw StudentNotHaveBookException()

        if(res_books.find { it.BookId == request.BookId } == null)
            throw BookNotTakenException()

        if(repository.GetBook(GetBookRequest(request.BookId)).error != res_successful)
            throw NoBooksAvailableException()

        error = repository.ReturnBook(request)
        logger.logMessage(error, "Return: Book")

        return error
    }

    override fun DeleteBook(request: DeleteBookRequest): Int {
        if(repository.GetBook(GetBookRequest(request.BookId)).error != res_successful)
            throw NoBooksAvailableException()

        if(repository.GetStudentHaveBook(GetStudentHaveBook(request.BookId)).res_Books.isNotEmpty())
            throw StudentHaveBookException()

        val error = repository.DeleteBook(request)
        logger.logMessage(error, "Delete: Book")

        return error
    }
}