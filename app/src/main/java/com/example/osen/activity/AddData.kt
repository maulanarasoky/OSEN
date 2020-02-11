package com.example.osen.activity

import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Classroom
import com.example.osen.model.Student
import kotlinx.android.synthetic.main.activity_add_data.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class AddData : AppCompatActivity() {

    lateinit var studentName : EditText
    lateinit var className : Spinner
    private var list: MutableList<Classroom> = mutableListOf()
    private var listClass: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)

        studentName = findViewById(R.id.studentName)
        className = findViewById(R.id.className)

        showClass()

        if(list.isNotEmpty()){
            for(i in 0 until list.size){
                listClass.add(list.get(i).name.toString())
            }
        }else{
            listClass.add("Belum ada kelas")
        }

        if(listClass[0].equals("Belum ada kelas", ignoreCase = true)){
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Harap membuat kelas terlebih dahulu"
            dialog.setCancelable(false)
            dialog.setConfirmClickListener {
                finish()
            }
            dialog.show()
        }else{
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listClass)
            className.adapter = adapter
        }

        clear.setOnClickListener {
            clear()
        }
        submit.setOnClickListener {
            if(studentName.text.toString() == ""){
                val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                dialog.titleText = "Harap masukkan data secara lengkap dan benar"
                dialog.setCancelable(false)
                dialog.show()
            }else{
                addStudent()
            }
        }
    }

    private fun addStudent(){
        try {
            database.use {
                insert(
                    Student.TABLE_STUDENT,
                    Student.NAME to studentName.text.toString(),
                    Student.CLASS_NAME to className.selectedItem.toString(),
                    Student.SCORE to 0,
                    Student.TEACHER_ID to 1)
            }
            clear()
            val dialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Data berhasil ditambahkan"
            dialog.setCancelable(false)
            dialog.show()
        }catch (e: SQLiteConstraintException){
            val dialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Gagal menambah data"
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    private fun clear(){
        studentName.setText("")
        className.setSelection(0)
    }

    private fun showClass(){
        list.clear()
        database.use {
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs("(TEACHER_ID = {teacher_id})", "teacher_id" to 1)
            val favorite = result.parseList(classParser<Classroom>())
            if (favorite.isNotEmpty()){
                list.addAll(favorite)
            }
        }
    }
}
