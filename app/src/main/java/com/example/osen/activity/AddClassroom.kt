package com.example.osen.activity

import android.app.DatePickerDialog
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Classroom
import kotlinx.android.synthetic.main.activity_add_classroom.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.toast
import java.util.*

class AddClassroom : AppCompatActivity() {

    lateinit var className: TextView
    lateinit var classType : Spinner
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
        classStart = findViewById(R.id.classStart)
        classEnd = findViewById(R.id.classEnd)
        firstDay = findViewById(R.id.classFirstDay)
        secondDay = findViewById(R.id.classSecondDay)
        thirdDay = findViewById(R.id.classThirdDay)

        ArrayAdapter.createFromResource(this, R.array.class_type, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classType.adapter = adapter
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
            if(className.text == "" || classStart.text == "Pilih" || classEnd.text == "Pilih"){
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
        val start = classStart.text.toString()
        val end = classEnd.text.toString()
        val day = "" + firstDay.selectedItem.toString() + ", " + secondDay.selectedItem.toString() + ", " + thirdDay.selectedItem.toString()
        var image = R.drawable.ic_class
        val split = name.split(" ")


        for (i in 0..split.size - 1){
            when {
                split[i].equals("Programming", ignoreCase = true) ||  (split[i].equals("Pemrograman", ignoreCase = true)) -> {
                    image = R.drawable.ic_programming
                }
                split[i].equals("Science", ignoreCase = true) || (split[i].equals("Ipa", ignoreCase = true)) -> {
                    image = R.drawable.ic_science
                }
                split[i].equals("Math", ignoreCase = true) || (split[i].equals("Matematika", ignoreCase = true)) -> {
                    image = R.drawable.ic_class
                }
                split[i].equals("Indonesia", ignoreCase = true) -> {
                    image = R.drawable.ic_indonesia
                }
                split[i].equals("English", ignoreCase = true) || (split[i].equals("Inggris", ignoreCase = true)) -> {
                    image = R.drawable.ic_english
                }else -> image = R.drawable.ic_science
            }
        }

        try {
            database.use {
                insert(
                    Classroom.TABLE_CLASSROOM,
                    Classroom.CLASS_NAME to name,
                    Classroom.CLASS_IMAGE to image,
                    Classroom.CLASS_START to start,
                    Classroom.CLASS_END to end,
                    Classroom.CLASS_DAY to day,
                    Classroom.CLASS_TYPE to type,
                    Classroom.TEACHER_ID to 1)
            }
            clear()
            val dialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Kelas Berhasil Dibuat"
            dialog.setCancelable(false)
            dialog.show()

            Log.d("Image : ", split[1])
        }catch (e: SQLiteConstraintException){
            val dialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Gagal membuat kelas"
            dialog.setCancelable(false)
            dialog.show()
        }
    }
}
