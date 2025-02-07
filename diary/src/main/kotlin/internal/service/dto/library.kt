package internal.service.dto

import java.sql.*

data class CreateBookRequest(
    var BookId: Int,
    var Title: String,
    var Author: String,
    var DatePublisher: Date,
    var Publisher: String,
    var Count: Int
)

data class GetBookRequest(
    var BookId: Int,
)

data class GetBooksRequest(
    var Title: String
)

data class GetStudentBooks(
    var StudentId: Int
)

data class GetStudentHaveBook(
    var BookId: Int
)

data class TakeBookRequest(
    var BookId: Int,
    var StudentId: Int
)

data class ReturnBookRequest(
    var BookId: Int,
    var StudentId: Int
)

data class DeleteBookRequest(
    var BookId: Int
)
