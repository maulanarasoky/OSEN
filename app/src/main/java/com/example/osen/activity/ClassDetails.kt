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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.osen.R
import com.example.osen.adapter.StudentList
import com.example.osen.database.database
import com.example.osen.model.Classroom
import com.example.osen.model.Student
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_class_details.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity

class ClassDetails : AppCompatActivity() {

    companion object{
        val data = "data"
    }

    var list: MutableList<Student> = mutableListOf()
    private lateinit var adapter: StudentList
    private lateinit var studentList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_details)

        val classroom: Classroom? = intent.getParcelableExtra(data)

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        collapsingToolbar.setContentScrimColor(Color.parseColor("#48cfad"))

        supportActionBar?.title = classroom?.name

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Glide.with(this).load(resources.getDrawable(classroom?.image!!.toInt())).apply(
            RequestOptions.overrideOf(500,500)).into(classImage)

        className.text = classroom.name
        classCategory.text = classroom.category
        startTime.text = classroom.startTime + " WIB"
        endTime.text = classroom.endTime + " WIB"
        startDate.text = classroom.startDate
        endDate.text = classroom.endDate
        day.text = classroom.day

        studentList = findViewById(R.id.recyclerView)

        adapter = StudentList(list, classroom.image)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        studentList.layoutManager = layoutManager
        studentList.adapter = adapter

        showStudent()

        Log.d("array size", list.size.toString())
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showStudent(){
        val classroom: Classroom? = intent.getParcelableExtra(data)
        list.clear()
        database.use {
            val result = select(Student.TABLE_STUDENT).whereArgs("(TEACHER_ID = {teacher_id}) AND (CLASS = {class}) ORDER BY ID_ ASC", "teacher_id" to 1, "class" to classroom?.name.toString())
            val category = result.parseList(classParser<Student>())
            if (category.isNotEmpty()){
                list.addAll(category)
            }else{
                textNoData.visibility = View.VISIBLE
            }
        }
    }
}
