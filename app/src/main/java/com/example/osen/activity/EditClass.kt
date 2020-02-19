package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Category
import com.example.osen.model.Classroom
import kotlinx.android.synthetic.main.activity_edit_class.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class EditClass : AppCompatActivity() {

    companion object{
        const val data = "data"
    }

    lateinit var className: EditText
    lateinit var classType : Spinner
    lateinit var classCategory: Spinner
    lateinit var newCategory: EditText
    lateinit var startDate : Button
    lateinit var endDate : Button
    lateinit var startTime: Button
    lateinit var endTime: Button
    lateinit var firstDay : Spinner
    lateinit var secondDay : Spinner
    lateinit var thirdDay : Spinner
    lateinit var fourthDay : Spinner
    lateinit var fifthDay : Spinner
    lateinit var sixthDay : Spinner

    private var listCategory: MutableList<Category> = mutableListOf()

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_class)

        initUI()

        val classroom: Classroom? = intent.getParcelableExtra(data)

        className.setText(classroom?.name, TextView.BufferType.EDITABLE)

        ArrayAdapter.createFromResource(this, R.array.class_type, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classType.adapter = adapter

            val classPosition = adapter.getPosition(classroom?.type)
            classType.setSelection(classPosition)
        }

        showCategorySpinner(classroom?.category)
        startDate.text = classroom?.startDate
        endDate.text = classroom?.endDate
        startTime.text = classroom?.startTime
        endTime.text = classroom?.endTime

        val split = classroom?.day?.split(", ")
        count = split?.size!!

        when(split.size){
            1 -> {
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    firstDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[0])
                    firstDay.setSelection(classPosition)
                }
            }
            2 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    firstDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[0])
                    firstDay.setSelection(classPosition)
                }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    secondDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[1])
                    secondDay.setSelection(classPosition)
                }
            }
            3 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    firstDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[0])
                    firstDay.setSelection(classPosition)
                }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    secondDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[1])
                    secondDay.setSelection(classPosition)
                }

                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    thirdDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[2])
                    thirdDay.setSelection(classPosition)
                }
            }
            4 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    firstDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[0])
                    firstDay.setSelection(classPosition)
                }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    secondDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[1])
                    secondDay.setSelection(classPosition)
                }

                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    thirdDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[2])
                    thirdDay.setSelection(classPosition)
                }

                rowDay4.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    fourthDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[3])
                    fourthDay.setSelection(classPosition)
                }
            }
            5 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    firstDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[0])
                    firstDay.setSelection(classPosition)
                }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    secondDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[1])
                    secondDay.setSelection(classPosition)
                }

                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    thirdDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[2])
                    thirdDay.setSelection(classPosition)
                }

                rowDay4.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    fourthDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[3])
                    fourthDay.setSelection(classPosition)
                }

                rowDay5.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    fifthDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[4])
                    fifthDay.setSelection(classPosition)
                }
            }
            6 -> {

                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    firstDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[0])
                    firstDay.setSelection(classPosition)
                }

                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    secondDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[1])
                    secondDay.setSelection(classPosition)
                }

                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    thirdDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[2])
                    thirdDay.setSelection(classPosition)
                }

                rowDay4.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    fourthDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[3])
                    fourthDay.setSelection(classPosition)
                }

                rowDay5.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    fifthDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[4])
                    fifthDay.setSelection(classPosition)
                }

                addDay.visibility = View.GONE
                rowDay6.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    sixthDay.adapter = adapter

                    val classPosition = adapter.getPosition(split[5])
                    sixthDay.setSelection(classPosition)
                }
            }
        }

        addDay.setOnClickListener {
            addDay()
        }
    }

    private fun addDay(){
        count++
        when(count) {
            2 -> {
                rowDay2.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    secondDay.adapter = adapter
                }
            }
            3 -> {
                rowDay3.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    thirdDay.adapter = adapter
                }
            }
            4 -> {
                rowDay4.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    fourthDay.adapter = adapter
                }
            }
            5 -> {
                rowDay5.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    fifthDay.adapter = adapter
                }
            }
            6 -> {
                rowDay6.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(this, R.array.day_name, R.layout.spinner_item).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    sixthDay.adapter = adapter
                }
            }
        }
        if(count == 6){
            addDay.visibility = View.GONE
        }
    }

    private fun initUI(){
        className = findViewById(R.id.className)
        classType = findViewById(R.id.classType)
        classCategory = findViewById(R.id.classCategory)
        newCategory = findViewById(R.id.newCategory)
        startDate = findViewById(R.id.classStart)
        endDate = findViewById(R.id.classEnd)
        startTime = findViewById(R.id.timeStart)
        endTime = findViewById(R.id.timeEnd)
        firstDay = findViewById(R.id.firstDay)
        secondDay = findViewById(R.id.secondDay)
        thirdDay = findViewById(R.id.thirdDay)
        fourthDay = findViewById(R.id.fourthDay)
        fifthDay = findViewById(R.id.fifthDay)
        sixthDay = findViewById(R.id.sixthDay)
    }

    private fun showCategorySpinner(currentCategory: String?){
        showCategory()
        if(listCategory.isEmpty()){
            rowOldCategory.visibility = View.GONE
            rowFirstCategory.visibility = View.VISIBLE
        }else{
            val categories: MutableList<String> = mutableListOf()
            for(i in 0 until listCategory.size){
                categories.add(listCategory[i].name.toString())
            }
            categories.add("Tidak ada pilihan")
            val adapter = ArrayAdapter<String>(this, R.layout.spinner_item, categories)
            classCategory.adapter = adapter

            val categoryPosition = adapter.getPosition(currentCategory)
            classCategory.setSelection(categoryPosition)

            classCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(classCategory.selectedItem.toString() == "Tidak ada pilihan"){
                        rowNewCategory.visibility = View.VISIBLE
                    }else{
                        rowNewCategory.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
        }
    }

    private fun showCategory(){
        listCategory.clear()
        database.use {
            val result = select(Category.TABLE_CATEGORY).whereArgs("(TEACHER_ID = {teacher_id})", "teacher_id" to 1)
            val category = result.parseList(classParser<Category>())
            if (category.isNotEmpty()){
                listCategory.addAll(category)
            }
        }
    }
}
