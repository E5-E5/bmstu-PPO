package com.example.model

enum class ClassNumber {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    ELEVEN
}

data class Class (
    var ClassId: Int,
    var Letter: String,
    var Number: ClassNumber
)