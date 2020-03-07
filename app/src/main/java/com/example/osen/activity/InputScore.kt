package com.example.osen.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.osen.R
import com.example.osen.model.Student
import com.google.android.material.appbar.CollapsingToolbarLayout

class InputScore : AppCompatActivity() {

    companion object{
        const val data = "data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_score)

        val parcel: Student? = intent.getParcelableExtra(EditData.data)

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        collapsingToolbar.setContentScrimColor(Color.parseColor("#48cfad"))

        supportActionBar?.title = parcel?.name

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
