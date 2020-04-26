package com.example.osen.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.interfaces.MyAsyncCallback
import com.example.osen.model.Category
import com.example.osen.model.Classroom
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_edit_class.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.jetbrains.anko.textColor
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class EditClass : AppCompatActivity(), MyAsyncCallback {

    companion object {
        const val data = "data"
    }

    private var listCategory: MutableList<Category> = mutableListOf()
    private var dataClass: MutableList<Classroom> = mutableListOf()

    lateinit var auth: FirebaseAuth

    var count = 0
    var classExist = false
    var nothingChange = false
    var availableCategory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_class)

        auth = FirebaseAuth.getInstance()


        val demoAsync = DemoAsync(this)
        demoAsync.execute()
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

    private fun initUI() {
        classType.isEnabled = false
        classType.isClickable = false

        classStart.text = dataClass[0].startDate
        classEnd.text = dataClass[0].endDate
        timeStart.text = dataClass[0].startTime
        timeEnd.text = dataClass[0].endTime
    }

    private fun showCategorySpinner(currentCategory: String?) {
        showCategory()
        val categories: MutableList<String> = mutableListOf()
        for (i in 0 until listCategory.size) {
            categories.add(listCategory[i].name.toString())
        }
        categories.add("Tidak ada pilihan")
        val adapter = ArrayAdapter<String>(this, R.layout.spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classCategory.adapter = adapter

        val categoryPosition = adapter.getPosition(currentCategory)
        classCategory.setSelection(categoryPosition)

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
        database.use {
            val result = select(Category.TABLE_CATEGORY).whereArgs(
                "(TEACHER_ID = {teacher_id}) AND (NAME = {category_name})",
                "teacher_id" to auth.currentUser?.uid.toString(),
                "category_name" to categoryName
            )
            val category = result.parseList(classParser<Category>())
            if (category.isNotEmpty()) {
                availableCategory = true
            }
        }
    }

    private fun checkData() {
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
        database.use {
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs(
                "(NAME = {name}) AND (TYPE = {type}) " +
                        "AND (CATEGORY = {category}) AND (START_DATE = {start_date}) AND (END_DATE = {end_date}) AND (START_TIME = {start_time}) " +
                        "AND (END_TIME = {end_time}) AND (DAY = {day}) AND (TEACHER_ID = {teacher_id})",
                "name" to className.text,
                "type" to classType.selectedItem.toString(),
                "category" to classCategory.selectedItem.toString(),
                "start_date" to classStart.text,
                "end_date" to classEnd.text,
                "start_time" to timeStart.text,
                "end_time" to timeEnd.text,
                "day" to day,
                "teacher_id" to auth.currentUser?.uid.toString()
            )
            val data = result.parseList(classParser<Classroom>())
            nothingChange = data.isNotEmpty()
        }
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

        if (rowNewCategory.visibility == View.VISIBLE) {
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

        checkData()
        if (nothingChange == true) {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.contentText = "Anda Tidak Melakukan Perubahan Apapun"
            dialog.setCancelable(false)
            dialog.show()
        } else {
            val classroom: Classroom? = intent.getParcelableExtra(data)
            if (className.text.toString() != classroom?.name.toString()) {
                checkClass(className.text.toString())
                if (classExist == true) {
                    val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                    dialog.titleText = "Nama Kelas Sudah Terpakai"
                    dialog.setCancelable(false)
                    dialog.show()
                    return
                }
            }
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
            val name = className.text.toString()
            var category: String = ""
            if (rowOldCategory.visibility == View.VISIBLE) {
                if (classCategory.selectedItem.toString()
                        .equals("Tidak ada pilihan", ignoreCase = true)
                ) {
                    category = newCategory.text.toString()
                    checkAvailableCategory(category)
                    if (!availableCategory) {
                        addCategory(category)
                    }
                } else {
                    category = classCategory.selectedItem.toString()
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
            val startDate = classStart.text.toString()
            val endDate = classEnd.text.toString()
            val startTime = timeStart.text.toString()
            val endTime = timeEnd.text.toString()
            database.use {
                val queryUpdate = update(
                    Classroom.TABLE_CLASSROOM,
                    Classroom.NAME to name,
                    Classroom.IMAGE to image,
                    Classroom.CATEGORY to category,
                    Classroom.START_DATE to startDate,
                    Classroom.END_DATE to endDate,
                    Classroom.START_TIME to startTime,
                    Classroom.END_TIME to endTime,
                    Classroom.DAY to day
                ).whereArgs(
                    "(ID_ = {id}) AND (TEACHER_ID = {teacher_id})",
                    "id" to classroom?.id.toString(),
                    "teacher_id" to classroom?.teacher_id.toString()
                )

                queryUpdate.exec()
            }
            val dialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.contentText = "Perubahan Berhasil Dilakukan"
            dialog.setCancelable(false)
            dialog.setConfirmClickListener {
                dialog.dismissWithAnimation()
                setResult(ClassDetails.RESULT_UPDATE)
                finish()
            }.show()
        }
    }

    private fun addCategory(categoryName: String) {
        try {
            database.use {
                insert(
                    Category.TABLE_CATEGORY,
                    Category.NAME to categoryName,
                    Category.TEACHER_ID to auth.currentUser?.uid.toString()
                )
            }
        } catch (e: SQLiteConstraintException) {
        }
    }

    private fun showDataClass() {
        val classroom: Classroom? = intent.getParcelableExtra(data)
        dataClass.clear()
        database.use {
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs(
                "(ID_ = {id}) AND (TEACHER_ID = {teacher_id})",
                "id" to classroom?.id.toString(),
                "teacher_id" to classroom?.teacher_id.toString()
            )
            val data = result.parseList(classParser<Classroom>())
            if (data.isNotEmpty()) {
                dataClass.addAll(data)
            }
        }
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

    private fun checkDay() {
        val split = dataClass[0].day?.split(", ")
        count = split?.size!!

        when (split.size) {
            1 -> {
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        firstDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[0])
                        firstDay.setSelection(classPosition)
                    }
            }
            2 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        firstDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[0])
                        firstDay.setSelection(classPosition)
                    }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        secondDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[1])
                        secondDay.setSelection(classPosition)
                    }
            }
            3 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        firstDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[0])
                        firstDay.setSelection(classPosition)
                    }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        secondDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[1])
                        secondDay.setSelection(classPosition)
                    }

                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        thirdDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[2])
                        thirdDay.setSelection(classPosition)
                    }
            }
            4 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        firstDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[0])
                        firstDay.setSelection(classPosition)
                    }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        secondDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[1])
                        secondDay.setSelection(classPosition)
                    }

                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        thirdDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[2])
                        thirdDay.setSelection(classPosition)
                    }

                rowDay4.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        fourthDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[3])
                        fourthDay.setSelection(classPosition)
                    }
            }
            5 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        firstDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[0])
                        firstDay.setSelection(classPosition)
                    }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        secondDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[1])
                        secondDay.setSelection(classPosition)
                    }

                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        thirdDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[2])
                        thirdDay.setSelection(classPosition)
                    }

                rowDay4.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        fourthDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[3])
                        fourthDay.setSelection(classPosition)
                    }

                rowDay5.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        fifthDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[4])
                        fifthDay.setSelection(classPosition)
                    }
            }
            6 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        firstDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[0])
                        firstDay.setSelection(classPosition)
                    }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        secondDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[1])
                        secondDay.setSelection(classPosition)
                    }

                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        thirdDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[2])
                        thirdDay.setSelection(classPosition)
                    }

                rowDay4.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        fourthDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[3])
                        fourthDay.setSelection(classPosition)
                    }

                rowDay5.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        fifthDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[4])
                        fifthDay.setSelection(classPosition)
                    }

                addDay.visibility = View.GONE
                rowDay6.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item)
                    .also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sixthDay.adapter = adapter

                        val classPosition = adapter.getPosition(split[5])
                        sixthDay.setSelection(classPosition)
                    }
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
            classExist = data.isNotEmpty()
        }
    }

    inner class DemoAsync(listener: MyAsyncCallback) : AsyncTask<String, Unit, Unit>() {

        private val myListener: WeakReference<MyAsyncCallback> = WeakReference(listener)

        override fun onPreExecute() {
            super.onPreExecute()
            val listener = myListener.get()
            listener?.onPreExecute()
        }

        override fun doInBackground(vararg params: String?) {
            showDataClass()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val listener = myListener.get()
            listener?.onPostExecute()
        }

    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
        background.visibility = View.GONE
    }

    override fun onPostExecute() {
        progressBar.visibility = View.GONE
        background.visibility = View.VISIBLE
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        initUI()

        if (currentDate > dataClass[0].endDate.toString()) {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Tidak Dapat Mengubah Kelas Yang Sudah Selesai"
            dialog.setCancelable(false)
            dialog.show()
            dialog.setConfirmClickListener {
                dialog.dismissWithAnimation()
                finish()
            }
            return
        }

        if (dataClass[0].type == "Reguler") {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            dialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialog.titleText = "Tidak Dapat Mengubah Kelas Reguler"
            dialog.setCancelable(false)
            dialog.show()
            dialog.setConfirmClickListener {
                dialog.dismissWithAnimation()
                finish()
            }
            return
        }

        className.setText(dataClass[0].name, TextView.BufferType.EDITABLE)

        ArrayAdapter.createFromResource(this, R.array.class_type, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                classType.adapter = adapter

                val classPosition = adapter.getPosition(dataClass[0].type)
                classType.setSelection(classPosition)
            }

        classType.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent?.getChildAt(0) as TextView
                item.textColor = resources.getColor(android.R.color.darker_gray)
                item.typeface = Typeface.DEFAULT
            }

        })

        showCategorySpinner(dataClass[0].category)

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

        checkDay()

        submit.setOnClickListener {
            submit()
        }
        addDay.setOnClickListener {
            addDay()
        }
    }
}
