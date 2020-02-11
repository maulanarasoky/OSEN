package com.example.osen.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.osen.model.Category
import com.example.osen.model.Classroom
import com.example.osen.model.Student
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx: Context): ManagedSQLiteOpenHelper(ctx, "FavoriteMatch.db", null, 1) {

    companion object{
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper{
            if (instance == null){
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance as MyDatabaseOpenHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(
            Classroom.TABLE_CLASSROOM, true,
            Classroom.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            Classroom.IMAGE to TEXT,
            Classroom.NAME to TEXT + UNIQUE,
            Classroom.TYPE to TEXT,
            Classroom.CATEGORY to TEXT,
            Classroom.START to TEXT,
            Classroom.END to TEXT,
            Classroom.DAY to TEXT,
            Classroom.TEACHER_ID to INTEGER)

        db?.createTable(
            Student.TABLE_STUDENT, true,
            Student.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            Student.NAME to TEXT,
            Student.CLASS_NAME to TEXT,
            Student.SCORE to INTEGER,
            Student.TEACHER_ID to INTEGER)

        db?.createTable(
            Category.TABLE_CATEGORY, true,
            Category.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            Category.NAME to TEXT,
            Category.TEACHER_ID to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(Classroom.TABLE_CLASSROOM, true)
        db?.dropTable(Student.TABLE_STUDENT, true)
        db?.dropTable(Category.TABLE_CATEGORY, true)
    }
}
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)