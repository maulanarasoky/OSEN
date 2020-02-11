package com.example.osen.activity

import android.app.DatePickerDialog
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Category
import com.example.osen.model.Classroom
import kotlinx.android.synthetic.main.activity_add_classroom.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import java.util.*

class AddClassroom : AppCompatActivity() {

    lateinit var className: EditText
    lateinit var classType : Spinner
    lateinit var classCategory: Spinner
    lateinit var newCategory: EditText
    lateinit var classStart : Button
    lateinit var classEnd : Button
    lateinit var firstDay : Spinner
    lateinit var secondDay : Spinner
    lateinit var thirdDay : Spinner

    private var listCategory: MutableList<Category> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_classroom)

        className = findViewById(R.id.className)
        classType = findViewById(R.id.classType)
        classCategory = findViewById(R.id.classCategory)
        newCategory = findViewById(R.id.newCategory)
        classStart = findViewById(R.id.classStart)
        classEnd = findViewById(R.id.classEnd)
        firstDay = findViewById(R.id.classFirstDay)
        secondDay = findViewById(R.id.classSecondDay)
        thirdDay = findViewById(R.id.classThirdDay)

        ArrayAdapter.createFromResource(this, R.array.class_type, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classType.adapter = adapter
        }

        showCategorySpinner()

        Log.d("list category", listCategory.size.toString())

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
            if(newCategory.visibility == View.VISIBLE){
                if(className.text.toString() == "" || classStart.text == "Pilih" || classEnd.text == "Pilih" || newCategory.text.toString() == ""){
                    val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                    dialog.titleText = "Harap masukkan data secara lengkap dan benar"
                    dialog.setCancelable(false)
                    dialog.show()
                    return@setOnClickListener
                }else{
                    addCategory()
                }
            }else if(rowNewCategory.visibility == View.VISIBLE){
                if(className.text.toString() == "" || classStart.text == "Pilih" || classEnd.text == "Pilih" || newCategoryAdd.text.toString() == ""){
                    val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                    dialog.titleText = "Harap masukkan data secara lengkap dan benar"
                    dialog.setCancelable(false)
                    dialog.show()
                    return@setOnClickListener
                }else{
                    addCategory()
                }
            } else{
                if(className.text.toString() == "" || classStart.text == "Pilih" || classEnd.text == "Pilih"){
                    val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                    dialog.titleText = "Harap masukkan data secara lengkap dan benar"
                    dialog.setCancelable(false)
                    dialog.show()
                    return@setOnClickListener
                }
            }
            addClassroom()
        }
    }

    private fun showCategorySpinner(){
        showCategory()
        if(listCategory.isEmpty()){
            classCategory.visibility = View.GONE
            newCategory.visibility = View.VISIBLE
        }else{
            val categories: MutableList<String> = mutableListOf()
            for(i in 0 until listCategory.size){
                categories.add(listCategory[i].name.toString())
            }
            categories.add("Tidak ada pilihan")
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories)
            classCategory.adapter = adapter

            classCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(classCategory.selectedItem.toString() == "Tidak ada pilihan"){
                        rowNewCategory.visibility = View.VISIBLE
                    }else{
                        rowNewCategory.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
        }

    }

    private fun clear(){
        showCategorySpinner()
        className.setText("")
        classType.setSelection(0)
        classStart.text = "Pilih"
        classEnd.text = "Pilih"
        firstDay.setSelection(0)
        secondDay.setSelection(0)
        thirdDay.setSelection(0)
        if(listCategory.isNotEmpty()){
            newCategory.visibility = View.GONE
            classCategory.visibility = View.VISIBLE
            classCategory.setSelection(0)
        }
        rowNewCategory.visibility = View.GONE
    }

    private fun addClassroom(){
        val name = className.text.toString()
        val type = classType.selectedItem.toString()
        var category = ""
        if(classCategory.visibility == View.VISIBLE){
            if (classCategory.selectedItem.toString().equals("Tidak ada pilihan", ignoreCase = true)){
                category = newCategoryAdd.text.toString()
            }else{
                category = classCategory.selectedItem.toString()
            }
        }else{
            category = newCategory.text.toString()
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

    private fun addCategory(){
        var category = ""
        if(rowNewCategory.visibility == View.VISIBLE){
            category = newCategoryAdd.text.toString()
        }else{
            category = newCategory.text.toString()
        }
        try {
            database.use {
                insert(
                    Category.TABLE_CATEGORY,
                    Category.NAME to category,
                    Category.TEACHER_ID to 1)
            }
        }catch (e: SQLiteConstraintException){
        }
    }

    private fun showCategory(){
        listCategory.clear()
        database.use {
            val result = select(Category.TABLE_CATEGORY).whereArgs("(TEACHER_ID = {teacher_id})", "teacher_id" to 1)
            val category = result.parseList(classParser<Category>())
            if (category.isNotEmpty()){
                listCategory.addAll(category)
            }
        }
    }
}
