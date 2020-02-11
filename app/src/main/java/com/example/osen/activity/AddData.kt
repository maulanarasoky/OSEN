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
import com.example.osen.model.Absent
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
    private var listStudent: MutableList<Student> = mutableListOf()
    private var listClass: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)

        studentName = findViewById(R.id.studentName)
        className = findViewById(R.id.className)

        ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            gender.adapter = adapter
        }

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
                    Student.GENDER to gender.selectedItem.toString(),
                    Student.SCORE to 0,
                    Student.TEACHER_ID to 1)

                getStudentId(className.selectedItem.toString(), 1, studentName.text.toString())

                insert(
                    Absent.TABLE_ABSENT,
                    Absent.STUDENT_ID to listStudent[0].id,
                    Absent.ALFA to 0,
                    Absent.IZIN to 0,
                    Absent.HADIR to 0,
                    Absent.TEACHER_ID to 1)
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
            val data = result.parseList(classParser<Classroom>())
            if (data.isNotEmpty()){
                list.addAll(data)
            }
        }
    }

    private fun getStudentId(className: String, teacherId: Int, studentName: String){
        listStudent.clear()
        database.use {
            val result = select(Student.TABLE_STUDENT).whereArgs("(TEACHER_ID = {teacher_id}) AND (CLASS = {class_name}) AND (NAME = {student_name}) LIMIT 1", "teacher_id" to teacherId, "class_name" to className, "student_name" to studentName)
            val data = result.parseList(classParser<Student>())
            if (data.isNotEmpty()){
                listStudent.addAll(data)
            }
        }
    }
}
