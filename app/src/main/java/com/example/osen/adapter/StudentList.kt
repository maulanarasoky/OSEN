package com.example.osen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.osen.R
import com.example.osen.model.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.table_row.*

class StudentList(private val studentItems : List<Student>) : RecyclerView.Adapter<StudentList.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.table_row, parent, false)
        )

    override fun getItemCount() = studentItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(studentItems[position])
    }

    class ViewHolder(override val containerView : View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindItem(student : Student){
            studentId.text = student.id.toString()
            studentName.text = student.name
            studentGender.text = student.gender

            ArrayAdapter.createFromResource(itemView.context, R.array.keterangan_hadir, R.layout.spinner_item).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                action.adapter = adapter

                val spinnerPosition = adapter.getPosition(student.ketereangan)
                action.setSelection(spinnerPosition)
            }
        }
    }
}