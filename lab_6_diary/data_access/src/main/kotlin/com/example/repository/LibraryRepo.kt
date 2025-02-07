@file:Suppress("ktlint:standard:no-wildcard-imports")
package com.example.repository

import PostgresDBConnector
import com.example.service.dto.*
import com.example.repository_interface.*
import com.example.logger.Logger
import com.example.model.*
import com.example.*
import java.sql.*

class LibraryRepo(
    private val logger: Logger,
    private val dbConnector: PostgresDBConnector
): LibraryStorage {
    val res_error = 3
    val res_successful = 0

    private fun BookFormation(resultSet: ResultSet): Library {
        val BookId = resultSet.getInt("BookId")
        val Title = resultSet.getString("Title")
        val Author = resultSet.getString("Author")
        val DatePublication = resultSet.getDate("DatePublication")
        val Publisher = resultSet.getString("Publisher")
        val Count = resultSet.getInt("Count")
        return Library(BookId, Title, Author, DatePublication, Publisher, Count)
    }

    private fun GetBooksFromBD(query: String, Id: Int): get_books {
        val res_books = mutableListOf<Library>()

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, Id)
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_books.add(BookFormation(resultSet))
            } ?: throw ConnectBDException()

            if (res_books.isNotEmpty())
                return get_books(res_books, 0)
            else
                return get_books(listOf(), 3)
        } catch (e: Exception) {
            println("Error getting all books: ${e.message}")
            return get_books(listOf(), 3)
        }
    }

    override fun CreateBook(request: CreateBookRequest): Int {
        val query = "INSERT INTO diary.library (Title, Author, DatePublication, Publisher, Count) " +
                "VALUES (?, ?, ?, ?, ?)"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, request.Title)
                preparedStatement.setString(2, request.Author)
                preparedStatement.setDate(3, java.sql.Date.valueOf(request.DatePublisher.toLocalDate()))
                preparedStatement.setString(4, request.Publisher)
                preparedStatement.setInt(5, request.Count)

                preparedStatement.executeUpdate()
                res_successful
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error creating book: ${e.message}")
            throw CreateBookException()
        }
    }
    override fun GetBook(request: GetBookRequest): get_book {
        val query = "SELECT * FROM diary.library WHERE BookId = ?"

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.BookId)
                val resultSet = preparedStatement.executeQuery()

                if (resultSet.next())
                    return get_book(BookFormation(resultSet), 0)
            } ?: throw ConnectBDException()
            return get_book(null, 3)
        } catch (e: Exception) {
            println("Error get book: ${e.message}")
            return get_book(null, 3)
        }
    }

    override fun GetBooks(request: GetBooksRequest): get_books {
        val query = "SELECT * FROM diary.library WHERE Title LIKE ?"
        val res_books = mutableListOf<Library>()

        try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, "%${request.Title}%")
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next())
                    res_books.add(BookFormation(resultSet))
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error getting all books: ${e.message}")
            res_books.clear()
        }

        if (res_books.isNotEmpty())
            return get_books(res_books, 0)
        return get_books(listOf(), 3)
    }

    override fun TakeBook(request: TakeBookRequest): Int {
        val queryCheckBooks = "SELECT Count FROM diary.library WHERE BookId = ?"
        val queryUpdateBook = "UPDATE diary.library SET Count = Count - 1 WHERE BookId = ?"
        val queryInsertBook = "INSERT INTO diary.studentbook VALUES (?, ?)"

        try {
            dbConnector.getConnection()?.use { connection ->
                connection.autoCommit = false

                try {
                    val preparedStatementCheck = connection.prepareStatement(queryCheckBooks)
                    preparedStatementCheck.setInt(1, request.BookId)
                    val resultSet = preparedStatementCheck.executeQuery()

                    if (resultSet.next()) {
                        val bookCount = resultSet.getInt(1)
                        if (bookCount > 0) {
                            val preparedStatementUpdate = connection.prepareStatement(queryUpdateBook)
                            preparedStatementUpdate.setInt(1, request.BookId)
                            preparedStatementUpdate.executeUpdate()

                            val preparedStatementInsert = connection.prepareStatement(queryInsertBook)
                            preparedStatementInsert.setInt(1, request.BookId)
                            preparedStatementInsert.setInt(2, request.StudentId)
                            preparedStatementInsert.executeUpdate()

                            connection.commit()
                            return res_successful
                        } else {
                            return res_error
                        }
                    }
                } catch (e: Exception) {
                    connection.rollback()
                    println("Error issuing book to student: ${e.message}")
                    return res_error
                }
            } ?: throw ConnectBDException()
            return res_error
        } catch (e: Exception) {
            println("Error issuing book to student: ${e.message}")
            return res_error
        }
    }

    override fun ReturnBook(request: ReturnBookRequest): Int {
        val queryUpdateBook = "UPDATE diary.library SET Count = Count + 1 WHERE BookId = ?"
        val queryInsertBook = "DELETE FROM diary.studentbook WHERE BookId = ? AND StudentId = ?"

        try {
            dbConnector.getConnection()?.use { connection ->
                connection.autoCommit = false

                try {
                    val preparedStatementUpdate = connection.prepareStatement(queryUpdateBook)
                    preparedStatementUpdate.setInt(1, request.BookId)
                    preparedStatementUpdate.executeUpdate()

                    val preparedStatementInsert = connection.prepareStatement(queryInsertBook)
                    preparedStatementInsert.setInt(1, request.BookId)
                    preparedStatementInsert.setInt(2, request.StudentId)
                    preparedStatementInsert.executeUpdate()

                    connection.commit()
                    return res_successful
                } catch (e: Exception) {
                    connection.rollback()
                    println("Error returning book from student: ${e.message}")
                    return res_error
                }
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error returning book from student: ${e.message}")
            return res_error
        }
    }

    override fun DeleteBook(request: DeleteBookRequest): Int {
        val query = "DELETE FROM diary.library WHERE BookId = ?"

        return try {
            dbConnector.getConnection()?.use { connection ->
                val preparedStatement = connection.prepareStatement(query)
                preparedStatement.setInt(1, request.BookId)
                preparedStatement.executeUpdate()

                res_successful
            } ?: throw ConnectBDException()
        } catch (e: Exception) {
            println("Error deleting book: ${e.message}")
            res_error
        }
    }

    override fun GetStudentBooks(request: GetStudentBooks): get_books {
        val query = "SELECT * FROM diary.library as L1 " +
                "JOIN diary.studentbook as S1 on L1.BookId = S1.BookId " +
                "WHERE S1.StudentId = ?"

        return GetBooksFromBD(query, request.StudentId)
    }

    override fun GetStudentHaveBook(request: GetStudentHaveBook): get_books {
        val query = "SELECT * FROM diary.library as L1 " +
                "JOIN diary.studentbook as S1 on L1.BookId = S1.BookId " +
                "WHERE S1.BookId = ?"

        return GetBooksFromBD(query, request.BookId)
    }
}