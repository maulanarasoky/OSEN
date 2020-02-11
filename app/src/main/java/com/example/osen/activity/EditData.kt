package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Absent
import com.example.osen.model.Student
import kotlinx.android.synthetic.main.activity_edit_data.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

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

        studentName.text = parcel?.name
        gender.text = parcel?.gender
        score.text = parcel?.score.toString()

        Glide.with(this).load(resources.getDrawable(intent?.getStringExtra(image)!!.toInt())).apply(
            RequestOptions.overrideOf(500,500)).into(classImage)

        showAbsent()

        alfa.text = list[0].alfa.toString()
        izin.text = list[0].izin.toString()
        hadir.text = list[0].hadir.toString()
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
