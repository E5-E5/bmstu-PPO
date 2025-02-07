package model

import java.sql.*

data class Library (
    var BookId: Int,
    var Title: String,
    var Author: String,
    var DatePublication: Date,
    var Publisher: String,
    var Count: Int
)