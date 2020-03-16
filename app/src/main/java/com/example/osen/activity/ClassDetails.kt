package com.example.osen.activity

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.osen.R
import com.example.osen.adapter.StudentList
import com.example.osen.database.database
import com.example.osen.interfaces.MyAsyncCallback
import com.example.osen.model.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_class_details.*
import org.jetbrains.anko.db.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class ClassDetails : AppCompatActivity(), MyAsyncCallback {

    companion object {
        val data = "data"

        const val REQUEST_CODE_EDIT = 100
        const val REQUEST_CODE_DETAILS = 101
        const val RESULT_DELETE = 200
        const val RESULT_UPDATE = 300
    }

    var listStudent: MutableList<Student> = mutableListOf()
    var dataClass: MutableList<Classroom> = mutableListOf()
    var absentOfDay: MutableList<AbsentOfDay> = mutableListOf()
    var absent: MutableList<Absent> = mutableListOf()
    private lateinit var adapter: StudentList
    private lateinit var studentList: RecyclerView

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_details)

        auth = FirebaseAuth.getInstance()

        showClass()

        val demoAsync = DemoAsync(this)
        demoAsync.execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT) {
            showClass()
            initUI()
        } else if (requestCode == EditStudent.REQUEST_CODE_ABSENT) {
            adapter.notifyDataSetChanged()
            studentList.adapter = adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_class_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.editClass -> {
                val classroom: Classroom? = intent.getParcelableExtra(data)
                val intent = Intent(this, EditClass::class.java)
                intent.putExtra(EditClass.data, classroom)
                startActivityForResult(intent, REQUEST_CODE_EDIT)
            }
            R.id.deleteClass -> {
                val temp = dataClass[0].name.toString()
                val dialogWarningDelete = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                dialogWarningDelete.progressHelper.barColor = Color.parseColor("#A5DC86")
                dialogWarningDelete.titleText =
                    "Apakah Anda Yakin Ingin Menghapus " + dataClass[0].name + " dan " + studentList.size + " Murid didalamnya ?"
                dialogWarningDelete.confirmText = "Hapus"
                dialogWarningDelete.cancelText = "Batalkan"
                dialogWarningDelete.showCancelButton(true)
                dialogWarningDelete.setConfirmClickListener {
                    database.use {
                        val queryDeleteClass = delete(
                            Classroom.TABLE_CLASSROOM,
                            "(ID_ = {class_id})",
                            "class_id" to dataClass[0].id.toString()
                        )
                        delete(
                            Student.TABLE_STUDENT,
                            "(CLASS = {class_name})",
                            "class_name" to dataClass[0].name.toString()
                        )
                        delete(
                            Absent.TABLE_ABSENT,
                            "(CLASS = {class_name}) AND (TEACHER_ID = {teacher_id})",
                            "class_name" to dataClass[0].name.toString(),
                            "teacher_id" to dataClass[0].teacher_id.toString()
                        )
                        delete(
                            AbsentOfDay.TABLE_ABSENTOFDAY,
                            "(CLASS = {class_name}) AND (TEACHER_ID = {teacher_id})",
                            "class_name" to dataClass[0].name.toString(),
                            "teacher_id" to dataClass[0].teacher_id.toString()
                        )
                        delete(
                            Score.TABLE_SCORE,
                            "(CLASS = {class_name})",
                            "class_name" to dataClass[0].name.toString()
                        )
                        if (queryDeleteClass > 0) {
                            dialogWarningDelete.titleText = "Berhasil Menghapus $temp"
                            dialogWarningDelete.confirmText = "OK"
                            dialogWarningDelete.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                            dialogWarningDelete.showCancelButton(false)
                            dialogWarningDelete.setCancelable(false)
                            dialogWarningDelete.setConfirmClickListener {
                                dialogWarningDelete.dismissWithAnimation()
                                setResult(RESULT_DELETE)
                                finish()
                            }
                        }
                    }
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkAbsentOfToday(currentDate: String) {
        database.use {
            val result = select(AbsentOfDay.TABLE_ABSENTOFDAY).whereArgs(
                "(CLASS = {class_name}) AND (DATE = {date})",
                "class_name" to dataClass[0].name.toString(),
                "date" to currentDate
            )
            val data = result.parseList(classParser<AbsentOfDay>())
            if (data.isNotEmpty()) {
                absentOfDay.addAll(data)
            }
        }
    }

    private fun checkAbsentData(student_id: String) {
        database.use {
            val result = select(Absent.TABLE_ABSENT).whereArgs(
                "(STUDENT_ID = {student_id}) AND (CLASS = {class_name}) AND (TEACHER_ID = {teacher_id}) LIMIT 1",
                "student_id" to student_id,
                "class_name" to dataClass[0].name.toString(),
                "teacher_id" to dataClass[0].teacher_id.toString()
            )
            val data = result.parseList(classParser<Absent>())
            if (data.isNotEmpty()) {
                absent.addAll(data)
            }
        }
    }

    private fun insertAllAbsentToday(currentDate: String, description: String) {
        database.use {
            for (i in 0 until listStudent.size) {
                insert(
                    AbsentOfDay.TABLE_ABSENTOFDAY,
                    AbsentOfDay.STUDENT_ID to listStudent[i].id,
                    AbsentOfDay.DATE to currentDate,
                    AbsentOfDay.KETERANGAN to description,
                    AbsentOfDay.CLASS to dataClass[0].name,
                    AbsentOfDay.TEACHER_ID to dataClass[0].teacher_id
                )
            }
        }
    }

    private fun updateAbsent(description: String) {
        database.use {
            for (i in 0 until studentList.size) {
                if (spinnerTandai.selectedItem.toString() != "Pilih") {
                    checkAbsentData(listStudent[i].id.toString())
                    val queryUpdate = when (description) {
                        "Alfa" -> {
                            val totalAlfa = absent[0].alfa?.plus(1)
                            update(
                                Absent.TABLE_ABSENT,
                                Absent.ALFA to totalAlfa
                            ).whereArgs(
                                "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id})",
                                "student_id" to listStudent[i].id.toString(),
                                "teacher_id" to listStudent[i].teacher_id.toString()
                            )
                        }
                        "Sakit" -> {
                            val totalSakit = absent[0].sakit?.plus(1)
                            update(
                                Absent.TABLE_ABSENT,
                                Absent.SAKIT to totalSakit
                            ).whereArgs(
                                "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id})",
                                "student_id" to listStudent[i].id.toString(),
                                "teacher_id" to listStudent[i].teacher_id.toString()
                            )
                        }
                        "Izin" -> {
                            val totalIzin = absent[0].izin?.plus(1)
                            update(
                                Absent.TABLE_ABSENT,
                                Absent.IZIN to totalIzin
                            ).whereArgs(
                                "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id})",
                                "student_id" to listStudent[i].id.toString(),
                                "teacher_id" to listStudent[i].teacher_id.toString()
                            )
                        }
                        "Hadir" -> {
                            val totalHadir = absent[0].hadir?.plus(1)
                            update(
                                Absent.TABLE_ABSENT,
                                Absent.HADIR to totalHadir
                            ).whereArgs(
                                "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id})",
                                "student_id" to listStudent[i].id.toString(),
                                "teacher_id" to listStudent[i].teacher_id.toString()
                            )
                        }
                        else -> error("Error")
                    }
                    queryUpdate.exec()
                }
            }
        }
    }

    private fun initUI() {
        Glide.with(this).load(resources.getDrawable(dataClass[0].image!!.toInt())).apply(
            RequestOptions.overrideOf(500, 500)
        ).into(classImage)

        className.text = dataClass[0].name
        classCategory.text = dataClass[0].category
        startTime.text = dataClass[0].startTime + " WIB"
        endTime.text = dataClass[0].endTime + " WIB"
        startDate.text = dataClass[0].startDate
        endDate.text = dataClass[0].endDate
        day.text = dataClass[0].day

        studentList = findViewById(R.id.recyclerView)

        adapter = StudentList(
            this,
            listStudent,
            dataClass[0].startDate.toString(),
            dataClass[0].endDate.toString(),
            dataClass[0].image.toString()
        )
    }

    private fun showStudent() {
        val classroom: Classroom? = intent.getParcelableExtra(data)
        listStudent.clear()
        database.use {
            val result = select(Student.TABLE_STUDENT).whereArgs(
                "(TEACHER_ID = {teacher_id}) AND (CLASS = {class}) ORDER BY ID_ ASC",
                "teacher_id" to auth.currentUser?.uid.toString(),
                "class" to classroom?.name.toString()
            )
            val category = result.parseList(classParser<Student>())
            if (category.isNotEmpty()) {
                listStudent.addAll(category)
            } else {
                spinnerTandai.visibility = View.GONE
                btnTandai.visibility = View.GONE
                warning.visibility = View.GONE
                textNoData.visibility = View.VISIBLE
            }
        }
    }

    private fun showClass() {
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

    inner class DemoAsync(listener: MyAsyncCallback): AsyncTask<Void, Unit, Unit>(){

        private val myListener: WeakReference<MyAsyncCallback> = WeakReference(listener)

        override fun onPreExecute() {
            super.onPreExecute()
            val myListener = myListener.get()
            myListener?.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?) {
            showClass()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val myListener = myListener.get()
            myListener?.onPostExecute()
        }

    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onPostExecute() {
        progressBar.visibility = View.GONE
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())

        if (currentDate < dataClass[0].startDate.toString()) {
            textClassFinish.text = "Kelas Belum Dimulai"
            linear4.visibility = View.GONE
            warning.visibility = View.GONE
        } else if (currentDate >= dataClass[0].startDate.toString() && currentDate <= dataClass[0].endDate.toString()) {
            textClassFinish.text = "Kelas Sedang Berlangsung"
        } else if (currentDate > dataClass[0].endDate.toString()) {
            linear4.visibility = View.GONE
            warning.visibility = View.GONE
        }

        ArrayAdapter.createFromResource(this, R.array.keterangan_hadir, R.layout.spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTandai.adapter = adapter
            }

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(
            ContextCompat.getColor(
                applicationContext,
                android.R.color.transparent
            )
        )

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        collapsingToolbar.setContentScrimColor(Color.parseColor("#48cfad"))

        supportActionBar?.title = dataClass[0].name

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showStudent()

        if (listStudent.isEmpty()){
            linear4.visibility = View.GONE
        }

        initUI()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        studentList.layoutManager = layoutManager
        studentList.adapter = adapter

        checkAbsentOfToday(currentDate)

        if (absentOfDay.isNotEmpty()) {
            btnTandai.isEnabled = false
            spinnerTandai.isEnabled = false
        }

        btnTandai.setOnClickListener {
            val description = spinnerTandai.selectedItem.toString()

            if (description != "-") {
                insertAllAbsentToday(currentDate, description)

                updateAbsent(description)
                adapter.notifyDataSetChanged()
                studentList.adapter = adapter
                btnTandai.isEnabled = false
                spinnerTandai.isEnabled = false
            }
        }
    }
}
