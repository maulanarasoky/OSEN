package com.example.osen.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Category
import com.example.osen.model.Classroom
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_add_classroom.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import java.text.SimpleDateFormat
import java.util.*

class AddClassroom : AppCompatActivity() {

    lateinit var className: EditText
    lateinit var classType: Spinner
    lateinit var classCategory: Spinner
    lateinit var newCategory: EditText
    lateinit var classStart: Button
    lateinit var classEnd: Button
    lateinit var timeStart: Button
    lateinit var timeEnd: Button
    lateinit var firstDay: Spinner
    lateinit var secondDay: Spinner
    lateinit var thirdDay: Spinner
    lateinit var fourthDay: Spinner
    lateinit var fifthDay: Spinner
    lateinit var sixthDay: Spinner

    var count = 1

    private var listCategory: MutableList<Category> = mutableListOf()
    private var checkCategory: MutableList<Category> = mutableListOf()

    lateinit var auth: FirebaseAuth

    var classExist = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_classroom)

        auth = FirebaseAuth.getInstance()

        initUI()

        ArrayAdapter.createFromResource(this, R.array.class_type, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                classType.adapter = adapter
            }

        showCategorySpinner()

        ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                firstDay.adapter = adapter
            }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        classStart.setOnClickListener {
            initDateStart(year, month, day)
        }

        classEnd.setOnClickListener {
            initDateEnd(year, month, day)
        }

        timeStart.setOnClickListener {
            initTimeStart(hour, minute)
        }

        timeEnd.setOnClickListener {
            initTimeEnd(hour, minute)
        }

        clear.setOnClickListener {
            clear()
        }
        submit.setOnClickListener {
            submit()
        }

        addDay.setOnClickListener {
            addDay()
        }
    }

    private fun initUI() {
        className = findViewById(R.id.className)
        classType = findViewById(R.id.classType)
        classCategory = findViewById(R.id.classCategory)
        newCategory = findViewById(R.id.newCategory)
        classStart = findViewById(R.id.classStart)
        classEnd = findViewById(R.id.classEnd)
        timeStart = findViewById(R.id.timeStart)
        timeEnd = findViewById(R.id.timeEnd)
        firstDay = findViewById(R.id.firstDay)
        secondDay = findViewById(R.id.secondDay)
        thirdDay = findViewById(R.id.thirdDay)
        fourthDay = findViewById(R.id.fourthDay)
        fifthDay = findViewById(R.id.fifthDay)
        sixthDay = findViewById(R.id.sixthDay)
    }

    private fun initDateStart(year: Int, month: Int, day: Int) {
        val datePickerDialog =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val formatDay = if (mDay < 10) {
                    "0$mDay"
                } else {
                    mDay.toString()
                }
                val formatMonth = if ((mMonth + 1) < 10) {
                    "0" + (mMonth + 1)
                } else {
                    (mMonth + 1).toString()
                }
                val text = "$formatDay/$formatMonth/$mYear"
                classStart.text = text
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun initDateEnd(year: Int, month: Int, day: Int) {
        val datePickerDialog =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val formatDay = if (mDay < 10) {
                    "0$mDay"
                } else {
                    mDay.toString()
                }
                val formatMonth = if ((mMonth + 1) < 10) {
                    "0" + (mMonth + 1)
                } else {
                    (mMonth + 1).toString()
                }
                val text = "$formatDay/$formatMonth/$mYear"
                classEnd.text = text
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun initTimeStart(hour: Int, minute: Int) {
        val timePickerDialog =
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minutes ->
                val formatHours: String = if (hourOfDay < 10) {
                    "0$hourOfDay"
                } else {
                    hourOfDay.toString()
                }
                val formatMinutes: String = if (minutes < 10) {
                    "0$minutes"
                } else {
                    minutes.toString()
                }
                val text = "$formatHours : $formatMinutes"
                timeStart.text = text
            }, hour, minute, true)
        timePickerDialog.show()
    }

    private fun initTimeEnd(hour: Int, minute: Int) {
        val timePickerDialog =
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minutes ->
                val formatHours: String = if (hourOfDay < 10) {
                    "0$hourOfDay"
                } else {
                    hourOfDay.toString()
                }
                val formatMinutes: String = if (minutes < 10) {
                    "0$minutes"
                } else {
                    minutes.toString()
                }
                val text = "$formatHours : $formatMinutes"
                timeEnd.text = text
            }, hour, minute, true)
        timePickerDialog.show()
    }

    private fun addDay() {
        count++
        when (count) {
            2 -> {
                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        secondDay.adapter = adapter
                    }
            }
            3 -> {
                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        thirdDay.adapter = adapter
                    }
            }
            4 -> {
                rowDay4.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        fourthDay.adapter = adapter
                    }
            }
            5 -> {
                rowDay5.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        fifthDay.adapter = adapter
                    }
            }
            6 -> {
                rowDay6.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sixthDay.adapter = adapter
                    }
            }
        }
        if (count == 6) {
            addDay.visibility = View.GONE
        }
    }

    private fun clear() {
        showCategorySpinner()
        className.setText("")
        classType.setSelection(0)
        classStart.text = "Pilih"
        classEnd.text = "Pilih"
        timeStart.text = "Pilih"
        timeEnd.text = "Pilih"
        firstDay.setSelection(0)
        if (listCategory.isNotEmpty()) {
            rowFirstCategory.visibility = View.GONE
            rowOldCategory.visibility = View.VISIBLE
            classCategory.setSelection(0)
        }
        rowNewCategory.visibility = View.GONE
        rowDay2.visibility = View.GONE
        secondDay.setSelection(0)
        rowDay3.visibility = View.GONE
        thirdDay.setSelection(0)
        rowDay4.visibility = View.GONE
        fourthDay.setSelection(0)
        rowDay5.visibility = View.GONE
        fifthDay.setSelection(0)
        rowDay6.visibility = View.GONE
        addDay.visibility = View.VISIBLE
        sixthDay.setSelection(0)
    }

    private fun submit() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val countWords = className.text.toString().split("")
        if (!TextUtils.isEmpty(className.text.toString())) {
            if (countWords.size > 17) {
                className.error = "Judul Terdiri Dari 1-15 Huruf"
                return
            }
        } else {
            className.error = "Nama Kelas Tidak Boleh Kosong"
            return
        }

        if (rowFirstCategory.visibility == View.VISIBLE) {
            if (TextUtils.isEmpty(firstCategoryAdd.text.toString())) {
                firstCategoryAdd.error = "Kategori Tidak Boleh Kosong"
                return
            }
        } else if (rowNewCategory.visibility == View.VISIBLE) {
            if (TextUtils.isEmpty(newCategory.text.toString())) {
                newCategory.error = "Kategori Tidak Boleh Kosong"
                return
            }
        }

        if (classStart.text.toString() == "Pilih") {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Silahkan Tentukan Tanggal Mulai Kelas"
            dialog.show()
            return
        }

        if (classEnd.text.toString() == "Pilih") {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Silahkan Tentukan Tanggal Selesai Kelas"
            dialog.show()
            return
        }

        if (classStart.text.toString() < currentDate) {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Tanggal Mulai Kelas Harus Dimulai Dari Hari Ini atau Setelahnya"
            dialog.show()
            return
        }

        if (classEnd.text.toString() < classStart.text.toString()) {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Tanggal Selesai Kelas Tidak Boleh Sebelum Tanggal Mulai Kelas"
            dialog.show()
            return
        }

        if (timeStart.text.toString() == "Pilih") {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Silahkan Tentukan Jadwal Mulai Kelas"
            dialog.show()
            return
        }

        if (timeEnd.text.toString() == "Pilih") {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Silahkan Tentukan Jadwal Selesai Kelas"
            dialog.show()
            return
        }

        if (timeEnd.text.toString() < timeStart.text.toString()) {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Jadwal Selesai Kelas Harus Setelah Jadwal Mulai Kelas"
            dialog.show()
            return
        }

        checkClass(className.text.toString())
        if (classExist) {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Nama Kelas Sudah Terpakai"
            dialog.setCancelable(false)
            dialog.show()
            return
        }

        if (rowNewCategory.visibility == View.VISIBLE) {
            checkAvailableCategory(newCategory.text.toString())
            if (checkCategory.isEmpty()) {
                addCategory()
            }
        } else if (rowFirstCategory.visibility == View.VISIBLE) {
            addCategory()
        }
        addClassroom()
    }

    private fun showCategorySpinner() {
        showCategory()
        if (listCategory.isEmpty()) {
            rowOldCategory.visibility = View.GONE
            rowFirstCategory.visibility = View.VISIBLE
        } else {
            val categories: MutableList<String> = mutableListOf()
            for (i in 0 until listCategory.size) {
                categories.add(listCategory[i].name.toString())
            }
            categories.add("Tidak ada pilihan")
            val adapter = ArrayAdapter<String>(this, R.layout.spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classCategory.adapter = adapter

            classCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (classCategory.selectedItem.toString() == "Tidak ada pilihan") {
                        rowNewCategory.visibility = View.VISIBLE
                    } else {
                        rowNewCategory.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
        }
    }


    private fun addClassroom() {
        val name = className.text.toString()
        val type = classType.selectedItem.toString()
        var category = ""
        if (rowOldCategory.visibility == View.VISIBLE) {
            if (classCategory.selectedItem.toString()
                    .equals("Tidak ada pilihan", ignoreCase = true)
            ) {
                category = newCategory.text.toString()
            } else {
                category = classCategory.selectedItem.toString()
            }
        } else {
            category = firstCategoryAdd.text.toString()
        }
        val startDate = classStart.text.toString()
        val endDate = classEnd.text.toString()
        val startTime = timeStart.text.toString()
        val endTime = timeEnd.text.toString()
        var day = ""
        when (count) {
            1 -> {
                day = "" + firstDay.selectedItem.toString()
            }
            2 -> {
                day =
                    "" + firstDay.selectedItem.toString() + ", " + secondDay.selectedItem.toString()
            }

            3 -> {
                day =
                    "" + firstDay.selectedItem.toString() + ", " + secondDay.selectedItem.toString() + ", " + thirdDay.selectedItem.toString()
            }

            4 -> {
                day =
                    "" + firstDay.selectedItem.toString() + ", " + secondDay.selectedItem.toString() + ", " + thirdDay.selectedItem.toString() + ", " + fourthDay.selectedItem.toString()
            }

            5 -> {
                day =
                    "" + firstDay.selectedItem.toString() + ", " + secondDay.selectedItem.toString() + ", " + thirdDay.selectedItem.toString() + ", " + fourthDay.selectedItem.toString() + ", " + fifthDay.selectedItem.toString()
            }

            6 -> {
                day =
                    "" + firstDay.selectedItem.toString() + ", " + secondDay.selectedItem.toString() + ", " + thirdDay.selectedItem.toString() + ", " + fourthDay.selectedItem.toString() + ", " + fifthDay.selectedItem.toString() + ", " + sixthDay.selectedItem.toString()

            }
        }
        var image = R.drawable.ic_class
        when (category) {
            "Bahasa Indonesia" -> image = R.drawable.ic_indonesia
            "Bahasa Inggris" -> image = R.drawable.ic_english
            "IPA" -> image = R.drawable.ic_science
            "Matematika" -> image = R.drawable.ic_math
            "Pemrograman" -> image = R.drawable.ic_programming
        }

        try {
            val result = database.use {
                insert(
                    Classroom.TABLE_CLASSROOM,
                    Classroom.NAME to name,
                    Classroom.IMAGE to image,
                    Classroom.START_DATE to startDate,
                    Classroom.END_DATE to endDate,
                    Classroom.START_TIME to startTime,
                    Classroom.END_TIME to endTime,
                    Classroom.DAY to day,
                    Classroom.TYPE to type,
                    Classroom.CATEGORY to category,
                    Classroom.TEACHER_ID to auth.currentUser?.uid
                )
            }
            if (result > 0) {
                clear()
                val dialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                dialog.titleText = "Kelas Berhasil Dibuat"
                dialog.setCancelable(false)
                dialog.show()
            }
        } catch (e: SQLiteConstraintException) {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Gagal membuat kelas"
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    private fun addCategory() {
        var category = ""
        if (rowNewCategory.visibility == View.VISIBLE) {
            category = newCategory.text.toString()
        } else {
            category = firstCategoryAdd.text.toString()
        }
        try {
            database.use {
                insert(
                    Category.TABLE_CATEGORY,
                    Category.NAME to category,
                    Category.TEACHER_ID to auth.currentUser?.uid
                )
            }
        } catch (e: SQLiteConstraintException) {
        }
    }

    private fun showCategory() {
        listCategory.clear()
        database.use {
            val result = select(Category.TABLE_CATEGORY).whereArgs(
                "(TEACHER_ID = {teacher_id})",
                "teacher_id" to auth.currentUser?.uid.toString()
            )
            val category = result.parseList(classParser<Category>())
            if (category.isNotEmpty()) {
                listCategory.addAll(category)
            }
        }
    }

    private fun checkAvailableCategory(categoryName: String) {
        checkCategory.clear()
        database.use {
            val result = select(Category.TABLE_CATEGORY).whereArgs(
                "(TEACHER_ID = {teacher_id}) AND (NAME = {category_name})",
                "teacher_id" to auth.currentUser?.uid.toString(),
                "category_name" to categoryName
            )
            val category = result.parseList(classParser<Category>())
            if (category.isNotEmpty()) {
                checkCategory.addAll(category)
            }
        }
    }

    private fun checkClass(class_name: String) {
        database.use {
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs(
                "(NAME = {class_name}) AND (TEACHER_ID = {teacher_id})",
                "class_name" to class_name,
                "teacher_id" to auth.currentUser?.uid.toString()
            )
            val data = result.parseList(classParser<Classroom>())
            if (data.isNotEmpty()) {
                classExist = true
            }
        }
    }
}
