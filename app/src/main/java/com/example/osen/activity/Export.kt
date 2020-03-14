package com.example.osen.activity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.osen.R
import com.example.osen.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_export.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter

class Export : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    var teacher_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)

        auth = FirebaseAuth.getInstance()

        teacher_id = auth.currentUser?.uid.toString()

        exportClass.setOnClickListener {
            val fileName = "Osen_Classes.json"
            export(this, Classroom.TABLE_CLASSROOM, fileName)
        }

        exportStudent.setOnClickListener {
            val fileName = "Osen_Students.json"
            export(this, Student.TABLE_STUDENT, fileName)
        }

        exportAbsent.setOnClickListener {
            val fileName = "Osen_Absents.json"
            export(this, Absent.TABLE_ABSENT, fileName)
        }

        exportScore.setOnClickListener {
            val fileName = "Osen_Scores.json"
            export(this, Score.TABLE_SCORE, fileName)
        }

        exportCategories.setOnClickListener {
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
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/json"
            intent.putExtra(Intent.EXTRA_SUBJECT, fileName)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_STREAM, path)
            startActivity(Intent.createChooser(intent, "Share With"))
        } catch (e: IOException) {
            Log.e(TAG, "File write failed :", e)
        }
    }
}
