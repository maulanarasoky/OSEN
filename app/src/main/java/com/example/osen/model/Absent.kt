package com.example.osen.model

data class Absent(
    val id: Int?,
    val student_id: Int?,
    val hadir: Int?,
    val izin: Int?,
    val sakit: Int?,
    val alfa: Int?,
    val class_name: String?,
    val teacher_id: String?
) {
    companion object {
        const val TABLE_ABSENT = "ABSENT"
        const val ID_ = "ID_"
        const val STUDENT_ID = "STUDENT_ID"
        const val HADIR = "HADIR"
        const val IZIN = "IZIN"
        const val SAKIT = "SAKIT"
        const val ALFA = "ALFA"
        const val CLASS = "CLASS"
        const val TEACHER_ID = "TEACHER_ID"
    }
}