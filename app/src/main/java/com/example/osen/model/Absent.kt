package com.example.osen.model

data class Absent(
    val id: Long?,
    val student_id: Int?,
    val hadir: Int?,
    val izin: Int?,
    val alfa: Int?,
    val teacher_id: Int?
){
    companion object{
        const val TABLE_ABSENT = "ABSENT"
        const val ID_ = "ID_"
        const val STUDENT_ID = "STUDENT_ID"
        const val ALFA = "ALFA"
        const val IZIN = "IZIN"
        const val HADIR = "HADIR"
        const val TEACHER_ID = "TEACHER_ID"
    }
}