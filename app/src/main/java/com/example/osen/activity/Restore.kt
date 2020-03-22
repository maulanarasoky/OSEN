package com.example.osen.activity

import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_restore.*
import org.json.JSONArray
import org.json.JSONException
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class Restore : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var storage: StorageReference
    lateinit var dialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance().reference

        restoreClasses.setOnClickListener {
            downloadFile("Osen_Classes.json", "Kelas")
        }
        restoreStudents.setOnClickListener {
            downloadFile("Osen_Students.json", "Murid")
        }
        restoreAbsents.setOnClickListener {
            downloadFile("Osen_Absents.json", "Absen")
        }
        restoreScores.setOnClickListener {
            downloadFile("Osen_Scores.json", "Nilai")
        }
        restoreCategories.setOnClickListener {
            downloadFile("Osen_Categories.json", "Kategori")
        }
    }

    private fun downloadFile(fileName: String, restoreName: String){
        if(!isNetworkConnected()){
            dialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            dialog.setCancelable(false)
            dialog.titleText = "Pastikan Anda terhubung ke Internet"
            dialog.show()
            return
        }

        dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.setCancelable(false)
        dialog.show()
        try {
            val folder = storage.child("${auth.currentUser?.email}/${fileName}")
            val location = File.createTempFile("application", ".json")

            folder.getFile(location)
                .addOnSuccessListener {
                    when(restoreName){
                        "Kelas" -> importClasses(location.toString(), restoreName)
                        "Murid" -> importStudent(location.toString(), restoreName)
                        "Absen" -> importAbsents(location.toString(), restoreName)
                        "Nilai" -> importScores(location.toString(), restoreName)
                        "Kategori" -> importCategories(location.toString(), restoreName)
                    }
                }
                .addOnFailureListener{
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    dialog.confirmText = "OK"
                    dialog.titleText = "Anda belum melakukan backup data $restoreName"
                }
        }catch (e: StorageException){
            e.printStackTrace()
            Log.d("ERROR", e.message.toString())
        }
    }

    @Throws(IOException::class)
    private fun readJsonFile(data: String): String {
        val builder = StringBuilder()
        var inputStream: InputStream? = null
        try {
            var jsonDataString: String?
            inputStream = FileInputStream(data)
            val bufferReader =
                BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            while (bufferReader.readLine().also { jsonDataString = it } != null) {
                builder.append(jsonDataString)
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        Log.d("OUTPUT", builder.toString())
        return String(builder)
    }

    @Throws(IOException::class, JSONException::class)
    fun importClasses(data: String, restoreName: String) {
        val ID_ = "ID_"
        val IMAGE = "IMAGE"
        val NAME = "NAME"
        val TYPE = "TYPE"
        val CATEGORY = "CATEGORY"
        val START_DATE = "START_DATE"
        val END_DATE = "END_DATE"
        val START_TIME = "START_TIME"
        val END_TIME = "END_TIME"
        val DAY = "DAY"
        val TEACHER_ID = "TEACHER_ID"
        try {
            val jsonDataString = readJsonFile(data)
            if (jsonDataString == "[]"){
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Data $restoreName kosong"
                dialog.confirmText = "OK"
                return
            }
            val menuItemJsonArray = JSONArray(jsonDataString)
            for (i in 0 until menuItemJsonArray.length()) {
                val jsonObject = menuItemJsonArray.getJSONObject(i)
                val contentValue = ContentValues()
                contentValue.put(ID_, jsonObject.getString(ID_))
                contentValue.put(IMAGE, jsonObject.getString(IMAGE))
                contentValue.put(NAME, jsonObject.getString(NAME))
                contentValue.put(TYPE, jsonObject.getString(TYPE))
                contentValue.put(CATEGORY, jsonObject.getString(CATEGORY))
                contentValue.put(START_DATE, jsonObject.getString(START_DATE))
                contentValue.put(END_DATE, jsonObject.getString(END_DATE))
                contentValue.put(START_TIME, jsonObject.getString(START_TIME))
                contentValue.put(END_TIME, jsonObject.getString(END_TIME))
                contentValue.put(DAY, jsonObject.getString(DAY))
                contentValue.put(TEACHER_ID, jsonObject.getString(TEACHER_ID))

                database.use {
                    val insert = insert(Classroom.TABLE_CLASSROOM, null, contentValue)
                    if(insert > 0){
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        dialog.titleText = "Data $restoreName berhasil di restore"
                        dialog.confirmText = "OK"
                    }
                }

                Log.d("IMPORT CLASSES SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun importStudent(data: String, restoreName: String) {
        val ID_ = "ID_"
        val NAME = "NAME"
        val CLASS_NAME = "CLASS"
        val GENDER = "GENDER"
        val SCORE = "SCORE"
        val TEACHER_ID = "TEACHER_ID"
        try {
            val jsonDataString = readJsonFile(data)
            if (jsonDataString == "[]"){
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Data $restoreName kosong"
                dialog.confirmText = "OK"
                return
            }
            val menuItemJsonArray = JSONArray(jsonDataString)
            for (i in 0 until menuItemJsonArray.length()) {
                val jsonObject = menuItemJsonArray.getJSONObject(i)
                val contentValue = ContentValues()
                contentValue.put(ID_, jsonObject.getString(ID_))
                contentValue.put(NAME, jsonObject.getString(NAME))
                contentValue.put(CLASS_NAME, jsonObject.getString(CLASS_NAME))
                contentValue.put(GENDER, jsonObject.getString(GENDER))
                contentValue.put(SCORE, jsonObject.getString(SCORE))
                contentValue.put(TEACHER_ID, jsonObject.getString(TEACHER_ID))

                database.use {
                    val insert = insert(Student.TABLE_STUDENT, null, contentValue)
                    if (insert > 0){
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        dialog.titleText = "Data $restoreName berhasil di restore"
                        dialog.confirmText = "OK"
                    }
                }

                Log.d("IMPORT STUDENTS SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun importAbsents(data: String, restoreName: String) {
        val ID_ = "ID_"
        val STUDENT_ID = "STUDENT_ID"
        val HADIR = "HADIR"
        val IZIN = "IZIN"
        val SAKIT = "SAKIT"
        val ALFA = "ALFA"
        val CLASS = "CLASS"
        val TEACHER_ID = "TEACHER_ID"
        try {
            val jsonDataString = readJsonFile(data)
            if (jsonDataString == "[]"){
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Data $restoreName kosong"
                dialog.confirmText = "OK"
                return
            }
            val menuItemJsonArray = JSONArray(jsonDataString)
            for (i in 0 until menuItemJsonArray.length()) {
                val jsonObject = menuItemJsonArray.getJSONObject(i)
                val contentValue = ContentValues()
                contentValue.put(ID_, jsonObject.getString(ID_))
                contentValue.put(STUDENT_ID, jsonObject.getString(STUDENT_ID))
                contentValue.put(HADIR, jsonObject.getString(HADIR))
                contentValue.put(IZIN, jsonObject.getString(IZIN))
                contentValue.put(SAKIT, jsonObject.getString(SAKIT))
                contentValue.put(ALFA, jsonObject.getString(ALFA))
                contentValue.put(CLASS, jsonObject.getString(CLASS))
                contentValue.put(TEACHER_ID, jsonObject.getString(TEACHER_ID))

                database.use {
                    val insert = insert(Absent.TABLE_ABSENT, null, contentValue)
                    if (insert > 0){
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        dialog.titleText = "Data $restoreName berhasil di restore"
                        dialog.confirmText = "OK"
                    }
                }

                Log.d("IMPORT ABSENTS SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun importScores(data: String, restoreName: String) {
        val ID_ = "ID_"
        val STUDENT_ID = "STUDENT_ID"
        val UTS = "UTS"
        val PERSENTASE_UTS = "PERSENTASE_UTS"
        val UAS = "UAS"
        val PERSENTASE_UAS = "PERSENTASE_UAS"
        val ASSESSMENT_1 = "ASSESSMENT_1"
        val PERSENTASE_ASSESSMENT_1 = "PERSENTASE_ASSESSMENT_1"
        val ASSESSMENT_2 = "ASSESSMENT_2"
        val PERSENTASE_ASSESSMENT_2 = "PERSENTASE_ASSESSMENT_2"
        val ASSESSMENT_3 = "ASSESSMENT_3"
        val PERSENTASE_ASSESSMENT_3 = "PERSENTASE_ASSESSMENT_3"
        val CLASS_NAME = "CLASS"
        val TEACHER_ID = "TEACHER_ID"
        try {
            val jsonDataString = readJsonFile(data)
            if (jsonDataString == "[]"){
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Data $restoreName kosong"
                dialog.confirmText = "OK"
                return
            }
            val menuItemJsonArray = JSONArray(jsonDataString)
            for (i in 0 until menuItemJsonArray.length()) {
                val jsonObject = menuItemJsonArray.getJSONObject(i)
                val contentValue = ContentValues()
                contentValue.put(ID_, jsonObject.getString(ID_))
                contentValue.put(STUDENT_ID, jsonObject.getString(STUDENT_ID))
                contentValue.put(UTS, jsonObject.getString(UTS))
                contentValue.put(PERSENTASE_UTS, jsonObject.getString(PERSENTASE_UTS))
                contentValue.put(UAS, jsonObject.getString(UAS))
                contentValue.put(PERSENTASE_UAS, jsonObject.getString(PERSENTASE_UAS))
                contentValue.put(ASSESSMENT_1, jsonObject.getString(ASSESSMENT_1))
                contentValue.put(
                    PERSENTASE_ASSESSMENT_1,
                    jsonObject.getString(PERSENTASE_ASSESSMENT_1)
                )
                contentValue.put(ASSESSMENT_2, jsonObject.getString(ASSESSMENT_2))
                contentValue.put(
                    PERSENTASE_ASSESSMENT_2,
                    jsonObject.getString(PERSENTASE_ASSESSMENT_2)
                )
                contentValue.put(ASSESSMENT_3, jsonObject.getString(ASSESSMENT_3))
                contentValue.put(
                    PERSENTASE_ASSESSMENT_3,
                    jsonObject.getString(PERSENTASE_ASSESSMENT_3)
                )
                contentValue.put(CLASS_NAME, jsonObject.getString(CLASS_NAME))
                contentValue.put(TEACHER_ID, jsonObject.getString(TEACHER_ID))

                database.use {
                    val insert = insert(Score.TABLE_SCORE, null, contentValue)
                    if (insert > 0){
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        dialog.titleText = "Data $restoreName berhasil di restore"
                        dialog.confirmText = "OK"
                    }
                }

                Log.d("IMPORT SCORES SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun importCategories(data: String, restoreName: String) {
        val ID_: String = "ID_"
        val NAME: String = "NAME"
        val TEACHER_ID: String = "TEACHER_ID"
        try {
            val jsonDataString = readJsonFile(data)
            if (jsonDataString == "[]"){
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Data $restoreName kosong"
                dialog.confirmText = "OK"
                return
            }
            val menuItemJsonArray = JSONArray(jsonDataString)
            for (i in 0 until menuItemJsonArray.length()) {
                val jsonObject = menuItemJsonArray.getJSONObject(i)
                val contentValue = ContentValues()
                contentValue.put(ID_, jsonObject.getString(ID_))
                contentValue.put(NAME, jsonObject.getString(NAME))
                contentValue.put(TEACHER_ID, jsonObject.getString(TEACHER_ID))

                database.use {
                    val insert = insert(Category.TABLE_CATEGORY, null, contentValue)
                    if (insert > 0){
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        dialog.titleText = "Data $restoreName berhasil di restore"
                        dialog.confirmText = "OK"
                    }
                }

                Log.d("IMPORT CATEGORY SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }

    private fun isNetworkConnected(): Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }
}
