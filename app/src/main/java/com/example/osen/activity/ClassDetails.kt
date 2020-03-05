package com.example.osen.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.osen.R
import com.example.osen.adapter.StudentList
import com.example.osen.database.database
import com.example.osen.model.Absent
import com.example.osen.model.AbsentOfDay
import com.example.osen.model.Classroom
import com.example.osen.model.Student
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_class_details.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class ClassDetails : AppCompatActivity() {

    companion object{
        val data = "data"
    }

    var listStudent: MutableList<Student> = mutableListOf()
    var dataClass: MutableList<Classroom> = mutableListOf()
    var absentOfDay: MutableList<AbsentOfDay> = mutableListOf()
    private lateinit var adapter: StudentList
    private lateinit var studentList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_details)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())

        showClass()

        if(currentDate < dataClass[0].startDate.toString()){
            textClassFinish.text = "Kelas Belum Dimulai"
        }

        if(currentDate >= dataClass[0].startDate.toString() && currentDate <= dataClass[0].endDate.toString()){
            textClassFinish.text = "Kelas Sedang Berlangsung"
        }

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        collapsingToolbar.setContentScrimColor(Color.parseColor("#48cfad"))

        supportActionBar?.title = dataClass[0].name

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        studentList.layoutManager = layoutManager
        studentList.adapter = adapter

        showStudent()
    }

    override fun onResume() {
        super.onResume()
        showClass()
        initUI()
        showStudent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_class_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
            }
            R.id.editClass -> {
                val classroom: Classroom? = intent.getParcelableExtra(data)
                startActivity<EditClass>(
                    EditClass.data to classroom
                )
            }
            R.id.deleteClass -> {
                val temp = dataClass[0].name.toString()
                val dialogWarningDelete = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                dialogWarningDelete.progressHelper.barColor = Color.parseColor("#A5DC86")
                dialogWarningDelete.titleText = "Apakah Anda Yakin Ingin Menghapus " + dataClass[0].name + " dan " + studentList.size + " Murid didalamnya ?"
                dialogWarningDelete.confirmText = "Hapus"
                dialogWarningDelete.cancelText = "Batalkan"
                dialogWarningDelete.showCancelButton(true)
                dialogWarningDelete.setConfirmClickListener {
                    database.use {
                        val queryDeleteClass = delete(Classroom.TABLE_CLASSROOM, "(ID_ = {class_id})", "class_id" to dataClass[0].id.toString())
                        val queryDeleteStudent = delete(Student.TABLE_STUDENT, "(CLASS = {class_name})", "class_name" to dataClass[0].name.toString())
                        delete(Absent.TABLE_ABSENT, "(CLASS = {class_name}) AND (TEACHER_ID = {teacher_id})", "class_name" to dataClass[0].name.toString(), "teacher_id" to dataClass[0].teacher_id.toString())
                        delete(AbsentOfDay.TABLE_ABSENTOFDAY, "(CLASS = {class_name}) AND (TEACHER_ID = {teacher_id})", "class_name" to dataClass[0].name.toString(), "teacher_id" to dataClass[0].teacher_id.toString())
                        if(queryDeleteClass > 0){
                            dialogWarningDelete.titleText = "Berhasil Menghapus $temp"
                            dialogWarningDelete.confirmText = "OK"
                            dialogWarningDelete.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                            dialogWarningDelete.showCancelButton(false)
                            dialogWarningDelete.setCancelable(false)
                            dialogWarningDelete.setConfirmClickListener {
                                dialogWarningDelete.dismissWithAnimation()
                                finish()
                            }
                        }
                    }
                }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUI(){
        Glide.with(this).load(resources.getDrawable(dataClass[0].image!!.toInt())).apply(
            RequestOptions.overrideOf(500,500)).into(classImage)

        className.text = dataClass[0].name
        classCategory.text = dataClass[0].category
        startTime.text = dataClass[0].startTime + " WIB"
        endTime.text = dataClass[0].endTime + " WIB"
        startDate.text = dataClass[0].startDate
        endDate.text = dataClass[0].endDate
        day.text = dataClass[0].day

        studentList = findViewById(R.id.recyclerView)

        adapter = StudentList(listStudent, dataClass[0].startDate.toString(), dataClass[0].endDate.toString(), dataClass[0].image.toString())
    }

    private fun showStudent(){
        val classroom: Classroom? = intent.getParcelableExtra(data)
        listStudent.clear()
        database.use {
            val result = select(Student.TABLE_STUDENT).whereArgs("(TEACHER_ID = {teacher_id}) AND (CLASS = {class}) ORDER BY ID_ ASC", "teacher_id" to 1, "class" to classroom?.name.toString())
            val category = result.parseList(classParser<Student>())
            if (category.isNotEmpty()){
                listStudent.addAll(category)
            }else{
                textNoData.visibility = View.VISIBLE
            }
        }
    }

    private fun showClass(){
        val classroom: Classroom? = intent.getParcelableExtra(data)
        dataClass.clear()
        database.use {
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs("(ID_ = {id}) AND (TEACHER_ID = {teacher_id})", "id" to classroom?.id.toString(),"teacher_id" to classroom?.teacher_id.toString())
            val data = result.parseList(classParser<Classroom>())
            if (data.isNotEmpty()){
                dataClass.addAll(data)
            }
        }
    }
}
