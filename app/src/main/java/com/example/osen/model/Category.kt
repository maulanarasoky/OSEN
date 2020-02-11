package com.example.osen.model

class Category (
    val id: Long?,
    val name: String?,
    val teacher_id: Int?
){
    companion object{
        const val TABLE_CATEGORY: String = "CATEGORY"
        const val ID: String = "ID_"
        const val NAME: String = "NAME"
        const val TEACHER_ID: String = "TEACHER_ID"
    }
}