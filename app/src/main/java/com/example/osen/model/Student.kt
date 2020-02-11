package com.example.osen.model

class Student (
    val id: Long?,
    val name: String?,
    val className: String?,
    val gender: String?,
    val score: Int?,
    val teacher_id: Int?
){
    companion object{
        const val TABLE_STUDENT: String = "STUDENT"
        const val ID: String = "ID_"
        const val NAME = "NAME"
        const val CLASS_NAME = "CLASS"
        const val GENDER = "GENDER"
        const val SCORE = "SCORE"
        const val TEACHER_ID = "TEACHER_ID"
    }
}
