package com.example.osen.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Absent
import com.example.osen.model.Student
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_edit_data.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class EditData : AppCompatActivity() {

    companion object{
        const val data = "data"
        const val image = "image"
    }

    val list: MutableList<Absent> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_data)

        val parcel: Student? = intent.getParcelableExtra(data)

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        collapsingToolbar.setContentScrimColor(Color.parseColor("#48cfad"))

        supportActionBar?.title = parcel?.name

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        studentName.text = parcel?.name
        gender.text = parcel?.gender
        score.text = parcel?.score.toString()

        Glide.with(this).load(resources.getDrawable(intent?.getStringExtra(image)!!.toInt())).apply(
            RequestOptions.overrideOf(500,500)).into(classImage)

        showAbsent()

        alfa.text = list[0].alfa.toString()
        sakit.text = list[0].sakit.toString()
        izin.text = list[0].izin.toString()
        hadir.text = list[0].hadir.toString()

        inputScore.setOnClickListener {
            startActivity<InputScore>(
                InputScore.data to parcel
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAbsent(){
        val parcel: Student? = intent.getParcelableExtra(data)
        list.clear()
        database.use {
            val result = select(Absent.TABLE_ABSENT).whereArgs("(TEACHER_ID = {teacher_id}) AND (STUDENT_ID = {student_id})", "teacher_id" to parcel?.teacher_id.toString(), "student_id" to parcel?.id.toString())
            val data = result.parseList(classParser<Absent>())
            if (data.isNotEmpty()){
                list.addAll(data)
            }
        }
    }
}
