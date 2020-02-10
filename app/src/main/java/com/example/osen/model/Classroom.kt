package com.example.osen.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Classroom (
    val id: Long?,
    val classImage: String?,
    val className: String?,
    val classType: String?,
    val classStart: String?,
    val classEnd: String?,
    val classDay: String?,
    val teacherId: Int?
    ): Parcelable{
    companion object{
        const val TABLE_CLASSROOM: String = "CLASSROOM"
        const val ID: String = "ID_"
        const val CLASS_IMAGE: String = "CLASS_IMAGE"
        const val CLASS_NAME: String = "CLASS_NAME"
        const val CLASS_TYPE: String = "CLASS_TYPE"
        const val CLASS_START: String = "CLASS_START"
        const val CLASS_END: String = "CLASS_END"
        const val CLASS_DAY: String = "CLASS_DAY"
        const val TEACHER_ID: String = "TEACHER_ID"

    }
}