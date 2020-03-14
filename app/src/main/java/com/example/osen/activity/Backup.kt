package com.example.osen.activity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.example.osen.R
import com.example.osen.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_backup.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter


class Backup : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    var teacher_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        auth = FirebaseAuth.getInstance()

        backupClasses.setOnClickListener {
            val fileName = "Osen_Classeses.json"
            export(this, Classroom.TABLE_CLASSROOM, fileName)
        }
        backupStudents.setOnClickListener {
            val fileName = "Osen_Students.json"
            export(this, Student.TABLE_STUDENT, fileName)
        }
        backupAbsents.setOnClickListener {
            val fileName = "Osen_Absents.json"
            export(this, Absent.TABLE_ABSENT, fileName)
        }
        backupScores.setOnClickListener {
            val fileName = "Osen_Scores.json"
            export(this, Score.TABLE_SCORE, fileName)
        }
        backupCategories.setOnClickListener {
            val fileName = "Osen_Categories.json"
            export(this, Category.TABLE_CATEGORY, fileName)
        }
    }

    private fun getResults(myTable: String): JSONArray? {
        val myPath: String = getDatabasePath("Osen.db").toString()
        val searchQuery = "SELECT  * FROM $myTable WHERE TEACHER_ID = '$teacher_id'"
        val myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)
        val cursor: Cursor = myDataBase.rawQuery(searchQuery, null)
        val resultSet = JSONArray()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val totalColumn: Int = cursor.columnCount
            val rowObject = JSONObject()
            for (i in 0 until totalColumn) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            Log.d("TAG_NAME", cursor.getString(i))
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i))
                        } else {
                            rowObject.put(cursor.getColumnName(i), "")
                        }
                    } catch (e: Exception) {
                        Log.d("TAG_NAME", e.message.toString())
                    }
                }
            }
            resultSet.put(rowObject)
            cursor.moveToNext()
        }
        cursor.close()
        Log.d("TAG_NAME", resultSet.toString())
        return resultSet
    }
    private val TAG = Export::class.java.name

    private fun export(context: Context, myTable: String, fileName: String) {
        try {
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.write(getResults(myTable).toString())
            outputStreamWriter.close()

            val fileLocation: File = File(filesDir, fileName)
            val path: Uri = FileProvider.getUriForFile(this, "com.example.osen.fileprovider", fileLocation)
            onFileClick(path)
        } catch (e: IOException) {
            Log.e(TAG, "File write failed :", e)
        }
    }

    private fun onFileClick(data: Uri) {
        val file = File(data.toString())
        val split = file.path.split(":").toTypedArray()
        val uploadUri = Uri.parse(split[1])
        Toast.makeText(this, "This file is located at: $uploadUri", Toast.LENGTH_SHORT).show()
        if (data.path?.contains(".json")!!) {
            val uploadIntent = ShareCompat.IntentBuilder.from(this)
                .setText("Share Document")
                .setType("application/json")
                .setStream(uploadUri)
                .intent
                .setPackage("com.google.android.apps.docs")
            startActivity(uploadIntent)
        } else {
            //TODO: what to do when it is other file format
        }
    }
}
