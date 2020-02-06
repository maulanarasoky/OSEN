package com.example.osen.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.osen.model.Classroom
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
            Classroom.CLASS_NAME to TEXT,
            Classroom.CLASS_START to TEXT,
            Classroom.CLASS_END to TEXT,
            Classroom.CLASS_DAY to TEXT,
            Classroom.CLASS_TYPE to TEXT,
            Classroom.TEACHER_ID to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(Classroom.TABLE_CLASSROOM, true)
    }
}
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)