package com.example.osen.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Student (
    val id: Int?,
    val name: String?,
    val className: String?,
    val gender: String?,
    val score: Int?,
    val teacher_id: String?
): Parcelable{
    companion object{
        const val TABLE_STUDENT: String = "STUDENT"
        const val ID_: String = "ID_"
        const val NAME = "NAME"
        const val CLASS_NAME = "CLASS"
        const val GENDER = "GENDER"
        const val SCORE = "SCORE"
        const val TEACHER_ID = "TEACHER_ID"
    }
}
