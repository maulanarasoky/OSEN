package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.osen.R
import com.example.osen.model.Classroom
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_class_details.*

class ClassDetails : AppCompatActivity() {

    companion object{
        val data = "data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_details)

        val classroom: Classroom? = intent.getParcelableExtra(data)

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = classroom?.name

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Glide.with(this).load(resources.getDrawable(classroom?.image!!.toInt())).apply(
            RequestOptions.overrideOf(500,500)).into(classImage)

        className.text = classroom.name
        classCategory.text = classroom.category
        classType.text = classroom.type
        classStart.text = classroom.start
        classEnd.text = classroom.end
        classDay.text = classroom.day

    }
}
