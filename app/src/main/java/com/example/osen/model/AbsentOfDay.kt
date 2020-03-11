package com.example.osen.model

class AbsentOfDay(
    val id: Int?,
    val studentId: Int?,
    val date: String?,
    val keterangan: String?,
    val className: String?,
    val teacherId: String?
){
    companion object{
        const val TABLE_ABSENTOFDAY = "ABSENT_OF_DAY"
        const val ID_ = "ID_"
        const val STUDENT_ID = "STUDENT_ID"
        const val DATE = "DATE"
        const val KETERANGAN = "KETERANGAN"
        const val CLASS = "CLASS"
        const val TEACHER_ID = "TEACHER_ID"
    }
}