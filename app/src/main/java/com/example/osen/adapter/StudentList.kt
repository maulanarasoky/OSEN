package com.example.osen.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.activity.EditData
import com.example.osen.database.database
import com.example.osen.model.Absent
import com.example.osen.model.AbsentOfDay
import com.example.osen.model.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.table_row.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class StudentList(private val studentItems : List<Student>, private val image: String) : RecyclerView.Adapter<StudentList.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.table_row, parent, false)
        )

    override fun getItemCount() = studentItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(studentItems[position], image)
    }

    class ViewHolder(override val containerView : View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        val list: MutableList<AbsentOfDay> = mutableListOf()
        var keterangan: String = ""

        fun bindItem(student: Student, image:String) {

            studentId.text = student.id.toString()
            studentName.text = student.name
            studentGender.text = student.gender

            itemView.context.database.use {
                val result = select(AbsentOfDay.TABLE_ABSENTOFDAY).whereArgs(
                    "(TEACHER_ID = {teacher_id}) AND (STUDENT_ID = {student_id}) LIMIT 1",
                    "teacher_id" to student.teacher_id.toString(),
                    "student_id" to student.id.toString()
                )
                val data = result.parseList(classParser<AbsentOfDay>())
                if (data.isNotEmpty()) {
                    list.addAll(data)
                    keterangan = list[0].keterangan.toString()
                    action.visibility = View.GONE
                    done.visibility = View.VISIBLE
                    done.text = keterangan
                }else{
                    ArrayAdapter.createFromResource(
                        itemView.context,
                        R.array.keterangan_hadir,
                        R.layout.spinner_item
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        action.adapter = adapter
                    }
                }
                action.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if(action.selectedItem.toString() != "Keterangan"){
                            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                            val currentDate = sdf.format(Date())
                            val res = itemView.context.database.use {
                                insert(
                                    AbsentOfDay.TABLE_ABSENTOFDAY,
                                    AbsentOfDay.DATE to currentDate,
                                    AbsentOfDay.STUDENT_ID to student.id,
                                    AbsentOfDay.KETERANGAN to action.selectedItem.toString(),
                                    AbsentOfDay.CLASS to student.className,
                                    AbsentOfDay.TEACHER_ID to student.teacher_id
                                )

                                insert(
                                    Absent.TABLE_ABSENT,
                                    Absent.STUDENT_ID to student.id,
                                    Absent.ALFA to 0,
                                    Absent.IZIN to 0,
                                    Absent.HADIR to 0,
                                    Absent.TEACHER_ID to student.teacher_id)
                            }
                            if(res > 0){
                                Log.d("keterangan", res.toString())
                            }else{
                                itemView.context.database.use {
                                    update(AbsentOfDay.TABLE_ABSENTOFDAY,
                                        AbsentOfDay.DATE to currentDate,
                                        AbsentOfDay.KETERANGAN to action.selectedItem.toString())
                                        .whereArgs("(TEACHER_ID = {teacher_id}) AND (ID_ = {student_id})", "teacher_id" to student.teacher_id.toString(), "student_id" to student.id.toString())
                                }
                            }
                            keterangan = action.selectedItem.toString()
                            setSpinnerAdapter()
                            action.visibility = View.GONE
                            done.visibility = View.VISIBLE
                            done.text = keterangan
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                }
            }
            itemView.setOnClickListener {
                itemView.context.startActivity<EditData>(
                    EditData.data to student,
                    EditData.image to image
                )
            }
        }

        fun setSpinnerAdapter(){
            ArrayAdapter.createFromResource(
                itemView.context,
                R.array.keterangan_hadir,
                R.layout.spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                action.adapter = adapter

                val spinnerPosition = adapter.getPosition(keterangan)
                action.setSelection(spinnerPosition)
            }
        }
    }
}