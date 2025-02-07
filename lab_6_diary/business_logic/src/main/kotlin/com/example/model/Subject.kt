package com.example.model

enum class Count {
    ONE, TWO
}

data class Subject (
    var SubjectId: Int,
    var Name: String,
    var CountTeachers: Count
)

fun main(){
    print(Count.TWO.name)
}