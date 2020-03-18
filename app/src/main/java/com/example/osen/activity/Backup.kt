package com.example.osen.activity

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.osen.R
import com.example.osen.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_backup.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter

class Backup : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var storage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance().reference

        backupClasses.setOnClickListener {
            val fileName = "Osen_Classes.json"
            export(this, Classroom.TABLE_CLASSROOM, fileName, "Kelas")
        }
        backupStudents.setOnClickListener {
            val fileName = "Osen_Students.json"
            export(this, Student.TABLE_STUDENT, fileName, "Murid")
        }
        backupAbsents.setOnClickListener {
            val fileName = "Osen_Absents.json"
            export(this, Absent.TABLE_ABSENT, fileName, "Absen")
        }
        backupScores.setOnClickListener {
            val fileName = "Osen_Scores.json"
            export(this, Score.TABLE_SCORE, fileName, "Nilai")
        }
        backupCategories.setOnClickListener {
            val fileName = "Osen_Categories.json"
            export(this, Category.TABLE_CATEGORY, fileName, "Kategori")
        }
    }

    private fun getResults(myTable: String): JSONArray? {
        val myPath: String = getDatabasePath("Osen.db").toString()
        val searchQuery = "SELECT  * FROM $myTable WHERE TEACHER_ID = '${auth.currentUser?.uid}'"
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

    private fun export(context: Context, myTable: String, fileName: String, description: String) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.write(getResults(myTable).toString())
            outputStreamWriter.close()

            val fileLocation: File = File(filesDir, fileName)
            val path: Uri =
                FileProvider.getUriForFile(this, "com.example.osen.fileprovider", fileLocation)
            val folder = storage.child("${auth.currentUser?.email}/${fileName}")
            folder.putFile(path)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data $description berhasil di backup", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener{
                    Log.d("TAG", "GAGAL")
                }
        } catch (e: IOException) {
            Log.e("ERROR", "File write failed :", e)
        }
    }
}
