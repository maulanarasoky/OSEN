package com.example.osen.activity

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.osen.R
import kotlinx.android.synthetic.main.activity_add_classroom.*
import org.jetbrains.anko.toast
import java.util.*

class AddClassroom : AppCompatActivity() {

    lateinit var className: TextView
    lateinit var classType : Spinner
    lateinit var classStart : Button
    lateinit var classEnd : Button
    lateinit var firstDay : Spinner
    lateinit var secondDay : Spinner
    lateinit var thirdDay : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_classroom)

        className = findViewById(R.id.className)
        classType = findViewById(R.id.classType)
        classStart = findViewById(R.id.classStart)
        classEnd = findViewById(R.id.classEnd)
        firstDay = findViewById(R.id.classFirstDay)
        secondDay = findViewById(R.id.classSecondDay)
        thirdDay = findViewById(R.id.classThirdDay)

        ArrayAdapter.createFromResource(this, R.array.class_type, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classType.adapter = adapter
        }

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
            submit()
        }
    }

    private fun clear(){
        className.setText("")
        classType.setSelection(0)
        classStart.text = "Pilih"
        classEnd.text = "Pilih"
        firstDay.setSelection(0)
        secondDay.setSelection(0)
        thirdDay.setSelection(0)
    }

    private fun submit(){
        toast("Berhasil").show()
    }
}
