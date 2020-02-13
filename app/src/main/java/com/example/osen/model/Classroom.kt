package com.example.osen.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Classroom (
    val id: Int?,
    val image: String?,
    val name: String?,
    val type: String?,
    val category: String?,
    val start: String?,
    val end: String?,
    val day: String?,
    val teacher_id: Int?
    ): Parcelable{
    companion object{
        const val TABLE_CLASSROOM: String = "CLASSROOM"
        const val ID: String = "ID_"
        const val IMAGE: String = "IMAGE"
        const val NAME: String = "NAME"
        const val TYPE: String = "TYPE"
        const val CATEGORY: String = "CATEGORY"
        const val START: String = "START"
        const val END: String = "END"
        const val DAY: String = "DAY"
        const val TEACHER_ID: String = "TEACHER_ID"

    }
}