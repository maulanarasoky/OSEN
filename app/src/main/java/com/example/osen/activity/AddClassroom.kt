package com.example.osen.activity

import android.app.DatePickerDialog
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Classroom
import kotlinx.android.synthetic.main.activity_add_classroom.*
import org.jetbrains.anko.db.insert
import java.util.*

class AddClassroom : AppCompatActivity() {

    lateinit var className: EditText
    lateinit var classType : Spinner
    lateinit var classCategory: Spinner
    lateinit var classStart : Button
    lateinit var classEnd : Button
    lateinit var firstDay : Spinner
    lateinit var secondDay : Spinner
    lateinit var thirdDay : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_classroom)



        className = findViewById(R.id.className)
        classType = findViewById(R.id.classType)
        classCategory = findViewById(R.id.classCategory)
        classStart = findViewById(R.id.classStart)
        classEnd = findViewById(R.id.classEnd)
        firstDay = findViewById(R.id.classFirstDay)
        secondDay = findViewById(R.id.classSecondDay)
        thirdDay = findViewById(R.id.classThirdDay)

        ArrayAdapter.createFromResource(this, R.array.class_type, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classType.adapter = adapter
        }

        ArrayAdapter.createFromResource(this, R.array.category, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classCategory.adapter = adapter
        }

        ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            firstDay.adapter = adapter
        }

        ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            secondDay.adapter = adapter
        }

        ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            thirdDay.adapter = adapter
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        classStart.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                val text = "" + mDay + "/" + (mMonth + 1) + "/" + mYear
                classStart.text = text
            }, year, month, day)

            datePickerDialog.show()
        }

        classEnd.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                val text = "" + mDay + "/" + (mMonth + 1) + "/" + mYear
                classEnd.text = text
            }, year, month, day)

            datePickerDialog.show()
        }

        clear.setOnClickListener {
            clear()
        }
        submit.setOnClickListener {
            if(className.text.toString() == "" || classStart.text == "Pilih" || classEnd.text == "Pilih"){
                val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                dialog.titleText = "Harap masukkan data secara lengkap dan benar"
                dialog.setCancelable(false)
                dialog.show()
            }else{
                addClassroom()
            }
        }
    }

    private fun clear(){
        className.setText("")
        classType.setSelection(0)
        classStart.text = "Pilih"
        classEnd.text = "Pilih"
        firstDay.setSelection(0)
        secondDay.setSelection(0)
        thirdDay.setSelection(0)
    }

    private fun addClassroom(){
        val name = className.text.toString()
        val type = classType.selectedItem.toString()
        var category = ""
        if (classCategory.selectedItem.toString().equals("Tidak ada pilihan", ignoreCase = true)){
            category = "-"
        }else{
            category = classCategory.selectedItem.toString()
        }
        val start = classStart.text.toString()
        val end = classEnd.text.toString()
        val day = "" + firstDay.selectedItem.toString() + ", " + secondDay.selectedItem.toString() + ", " + thirdDay.selectedItem.toString()
        var image = R.drawable.ic_class
        when(category){
            "Bahasa Indonesia" -> image = R.drawable.ic_indonesia
            "Bahasa Inggris" -> image = R.drawable.ic_english
            "IPA" -> image = R.drawable.ic_science
            "Matematika" -> image = R.drawable.ic_class
            "Pemrograman" -> image = R.drawable.ic_programming
        }

        try {
            database.use {
                insert(
                    Classroom.TABLE_CLASSROOM,
                    Classroom.NAME to name,
                    Classroom.IMAGE to image,
                    Classroom.START to start,
                    Classroom.END to end,
                    Classroom.DAY to day,
                    Classroom.TYPE to type,
                    Classroom.CATEGORY to category,
                    Classroom.TEACHER_ID to 1)
            }
            clear()
            val dialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Kelas Berhasil Dibuat"
            dialog.setCancelable(false)
            dialog.show()
        }catch (e: SQLiteConstraintException){
            val dialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Gagal membuat kelas"
            dialog.setCancelable(false)
            dialog.show()
        }
    }
}
