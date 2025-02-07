package com.example.repository_interface

import com.example.service.dto.*
import com.example.model.Library

data class get_book(val res_Book: Library?, val error: Int)
data class get_books(val res_Books: List<Library>, val error: Int)

interface LibraryStorage {
    fun CreateBook(request: CreateBookRequest): Int
    fun GetBook(request: GetBookRequest): get_book
    fun GetBooks(request: GetBooksRequest): get_books
    fun TakeBook(request: TakeBookRequest): Int
    fun ReturnBook(request: ReturnBookRequest): Int
    fun DeleteBook(request: DeleteBookRequest): Int
    fun GetStudentBooks(request: GetStudentBooks): get_books
    fun GetStudentHaveBook(request: GetStudentHaveBook): get_books
}