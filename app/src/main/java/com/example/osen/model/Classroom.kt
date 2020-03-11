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
    val startDate: String?,
    val endDate: String?,
    val startTime: String,
    val endTime: String,
    val day: String?,
    val teacher_id: String?
    ): Parcelable{
    companion object{
        const val TABLE_CLASSROOM: String = "CLASSROOM"
        const val ID: String = "ID_"
        const val IMAGE: String = "IMAGE"
        const val NAME: String = "NAME"
        const val TYPE: String = "TYPE"
        const val CATEGORY: String = "CATEGORY"
        const val START_DATE: String = "START_DATE"
        const val END_DATE: String = "END_DATE"
        const val START_TIME: String = "START_TIME"
        const val END_TIME: String = "END_TIME"
        const val DAY: String = "DAY"
        const val TEACHER_ID: String = "TEACHER_ID"

    }
}