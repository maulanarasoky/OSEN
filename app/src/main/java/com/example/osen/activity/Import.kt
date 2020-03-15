package com.example.osen.activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.*
import kotlinx.android.synthetic.main.activity_import.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONException
import java.io.*


class Import : AppCompatActivity() {

    val REQUEST_CLASS = 1
    val REQUEST_STUDENT = 2
    val REQUEST_ABSENT = 3
    val REQUEST_SCORE = 4
    val REQUEST_CATEGORY = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)

        linear1.setOnClickListener {
            readFile(REQUEST_CLASS)
        }
        linear2.setOnClickListener {
            readFile(REQUEST_STUDENT)
        }
        linear3.setOnClickListener {
            readFile(REQUEST_ABSENT)
        }
        linear4.setOnClickListener {
            readFile(REQUEST_SCORE)
        }
        linear5.setOnClickListener {
            readFile(REQUEST_CATEGORY)
        }
    }

    private fun readFile(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/*"
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val selectedFile: Uri? = data.data
            val file = File(selectedFile?.path.toString())
            Log.d("FILE DIR", selectedFile.toString())
            when (requestCode) {
                REQUEST_CLASS -> {
                    textImportClass.text = file.name
                    textImportClass.textSize = 10f
                    importClasses.setOnClickListener {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                toast("Permission Denied").show()
                            } else {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    0
                                )
                            }
                        } else {
                            if (selectedFile != null) {
                                val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                dialog.setCancelable(false)
                                dialog.showCancelButton(true)
                                dialog.cancelText = "Batal"
                                dialog.confirmText = "Import"
                                dialog.titleText = "Import data kelas ?"
                                dialog.setConfirmClickListener {
                                    importClasses(selectedFile)
                                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    dialog.showCancelButton(false)
                                    dialog.confirmText = "Ok"
                                    dialog.titleText = "Berhasil import data kelas"
                                    dialog.setConfirmClickListener {
                                        dialog.dismissWithAnimation()
                                    }
                                }
                                dialog.show()
                            }
                        }
                    }
                }
                REQUEST_STUDENT -> {
                    textImportStudent.text = file.name
                    textImportStudent.textSize = 10f

                    importStudents.setOnClickListener {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                toast("Permission Denied").show()
                            } else {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    0
                                )
                            }
                        } else {
                            if (selectedFile != null) {
                                val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                dialog.setCancelable(false)
                                dialog.showCancelButton(true)
                                dialog.cancelText = "Batal"
                                dialog.confirmText = "Import"
                                dialog.titleText = "Import data murid ?"
                                dialog.setConfirmClickListener {
                                    importStudent(selectedFile)
                                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    dialog.showCancelButton(false)
                                    dialog.titleText = "Berhasil import data murid"
                                    dialog.confirmText = "Ok"
                                    dialog.setConfirmClickListener {
                                        dialog.dismissWithAnimation()
                                    }
                                }
                                dialog.show()
                            }
                        }
                    }
                }
                REQUEST_ABSENT -> {
                    textImportAbsent.text = file.name
                    textImportAbsent.textSize = 10f

                    importAbsents.setOnClickListener {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                toast("Permission Denied").show()
                            } else {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    0
                                )
                            }
                        } else {
                            if (selectedFile != null) {
                                val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                dialog.setCancelable(false)
                                dialog.showCancelButton(true)
                                dialog.cancelText = "Batal"
                                dialog.confirmText = "Import"
                                dialog.titleText = "Import data absen ?"
                                dialog.setConfirmClickListener {
                                    importAbsents(selectedFile)
                                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    dialog.showCancelButton(false)
                                    dialog.confirmText = "Ok"
                                    dialog.titleText = "Berhasil import data absen"
                                    dialog.setConfirmClickListener {
                                        dialog.dismissWithAnimation()
                                    }
                                }
                                dialog.show()
                            }
                        }
                    }
                }
                REQUEST_SCORE -> {
                    textImportScore.text = file.name
                    textImportScore.textSize = 10f

                    importScores.setOnClickListener {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                toast("Permission Denied").show()
                            } else {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    0
                                )
                            }
                        } else {
                            if (selectedFile != null) {
                                val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                dialog.setCancelable(false)
                                dialog.showCancelButton(true)
                                dialog.cancelText = "Batal"
                                dialog.confirmText = "Import"
                                dialog.titleText = "Import data nilai ?"
                                dialog.setConfirmClickListener {
                                    importScores(selectedFile)
                                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    dialog.confirmText = "Ok"
                                    dialog.titleText = "Berhasil import data nilai"
                                    dialog.showCancelButton(false)
                                    dialog.setConfirmClickListener {
                                        dialog.dismissWithAnimation()
                                    }
                                }
                                dialog.show()
                            }
                        }
                    }
                }
                REQUEST_CATEGORY -> {
                    textImportCategory.text = file.name
                    textImportCategory.textSize = 10f

                    importCategories.setOnClickListener {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                toast("Permission Denied").show()
                            } else {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    0
                                )
                            }
                        } else {
                            if (selectedFile != null) {
                                val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                dialog.setCancelable(false)
                                dialog.showCancelButton(true)
                                dialog.cancelText = "Batal"
                                dialog.confirmText = "Import"
                                dialog.titleText = "Import data kategori ?"
                                dialog.setConfirmClickListener {
                                    importCategories(selectedFile)
                                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    dialog.showCancelButton(false)
                                    dialog.confirmText = "Ok"
                                    dialog.titleText = "Berhasil import data kategori"
                                    dialog.setConfirmClickListener {
                                        dialog.dismissWithAnimation()
                                    }
                                }
                                dialog.show()
                            }
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun readJsonFile(data: Uri): String {
        val builder = StringBuilder()
        var inputStream: InputStream? = null
        try {
            val file = File(data.path)
            val split =
                file.path.split(":").toTypedArray() //split the path.
            val filePath = split[1]
            var jsonDataString: String? = null
            inputStream = FileInputStream(filePath)
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
    fun importClasses(data: Uri) {
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
                    insert(Classroom.TABLE_CLASSROOM, null, contentValue)
                }

                Log.d("IMPORT CLASSES SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun importStudent(data: Uri) {
        val ID_ = "ID_"
        val NAME = "NAME"
        val CLASS_NAME = "CLASS"
        val GENDER = "GENDER"
        val SCORE = "SCORE"
        val TEACHER_ID = "TEACHER_ID"
        try {
            val jsonDataString = readJsonFile(data)
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
                    insert(Student.TABLE_STUDENT, null, contentValue)
                }

                Log.d("IMPORT STUDENTS SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun importAbsents(data: Uri) {
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
                    insert(Absent.TABLE_ABSENT, null, contentValue)
                }

                Log.d("IMPORT ABSENTS SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun importScores(data: Uri) {
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
                    insert(Score.TABLE_SCORE, null, contentValue)
                }

                Log.d("IMPORT SCORES SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun importCategories(data: Uri) {
        val ID_: String = "ID_"
        val NAME: String = "NAME"
        val TEACHER_ID: String = "TEACHER_ID"
        try {
            val jsonDataString = readJsonFile(data)
            val menuItemJsonArray = JSONArray(jsonDataString)
            for (i in 0 until menuItemJsonArray.length()) {
                val jsonObject = menuItemJsonArray.getJSONObject(i)
                val contentValue = ContentValues()
                contentValue.put(ID_, jsonObject.getString(ID_))
                contentValue.put(NAME, jsonObject.getString(NAME))
                contentValue.put(TEACHER_ID, jsonObject.getString(TEACHER_ID))

                database.use {
                    insert(Category.TABLE_CATEGORY, null, contentValue)
                }

                Log.d("IMPORT CATEGORY SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }
}
