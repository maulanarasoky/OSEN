package com.example.osen.adapter

import android.graphics.Color
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
import kotlinx.android.synthetic.main.student_list.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

class StudentList(private val studentItems: MutableList<Student>, private val image: String?) : RecyclerView.Adapter<StudentList.ViewHolder>() {

    fun delete(position: Int){
        studentItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, studentItems.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.student_list, parent, false)
        )

    override fun getItemCount() = studentItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (image != null) {
            holder.bindItem(studentItems[position], image, position)
        }
    }

    inner class ViewHolder(override val containerView : View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        val list: MutableList<AbsentOfDay> = mutableListOf()
        var keterangan: String = ""

        val absentData: MutableList<Absent> = mutableListOf()


        fun bindItem(student: Student, image:String, position: Int) {

            studentId.text = student.id.toString()
            studentName.text = student.name

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
                        if(action.selectedItem.toString() != "-"){
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

                                showAbsentData(student.teacher_id, student.id)

                                val queryUpdate = when(action.selectedItem.toString()){
                                    "Alfa" ->{
                                        val totalAlfa = absentData[0].alfa?.plus(1)
                                        update(Absent.TABLE_ABSENT,
                                            Absent.ALFA to totalAlfa).whereArgs("(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id})", "student_id" to student.id.toString(), "teacher_id" to student.teacher_id.toString())
                                    }
                                    "Izin" -> {
                                        val totalIzin = absentData[0].izin?.plus(1)
                                        update(Absent.TABLE_ABSENT,
                                            Absent.IZIN to totalIzin).whereArgs("(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id})", "student_id" to student.id.toString(), "teacher_id" to student.teacher_id.toString())
                                    }
                                    "Hadir" -> {
                                        val totalHadir = absentData[0].hadir?.plus(1)
                                        update(Absent.TABLE_ABSENT,
                                            Absent.HADIR to totalHadir).whereArgs("(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id})", "student_id" to student.id.toString(), "teacher_id" to student.teacher_id.toString())
                                    }
                                    else -> error("Error")
                                }
                                queryUpdate.exec()
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
            deleteStudent.setOnClickListener {
                val tempName = student.name
                val dialogWarningDelete = SweetAlertDialog(itemView.context, SweetAlertDialog.WARNING_TYPE)
                dialogWarningDelete.progressHelper.barColor = Color.parseColor("#A5DC86")
                dialogWarningDelete.titleText = "Apakah Anda Yakin Ingin Menghapus " + student.name + " ?"
                dialogWarningDelete.setCancelable(false)
                dialogWarningDelete.show()
                dialogWarningDelete.setConfirmClickListener {
                    itemView.context.database.use {
                        val queryDeleteStudent = delete(Student.TABLE_STUDENT, "(ID_) = {student_id}", "student_id" to student.id.toString())
                        delete(position)
                        if(queryDeleteStudent > 0){
                            dialogWarningDelete.dismissWithAnimation()
                            val dialogSuccessDelete = SweetAlertDialog(itemView.context, SweetAlertDialog.SUCCESS_TYPE)
                            dialogSuccessDelete.progressHelper.barColor = Color.parseColor("#A5DC86")
                            dialogSuccessDelete.titleText = "Berhasil Menghapus Data $tempName !"
                            dialogSuccessDelete.setCancelable(false)
                            dialogSuccessDelete.show()
                        }
                    }
                }
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

        fun showAbsentData(teacherId: Int?, studentId: Int?){
            itemView.context.database.use {
                val result = select(Absent.TABLE_ABSENT).whereArgs("(TEACHER_ID = {teacher_id}) AND (STUDENT_ID = {student_id}) LIMIT 1", "teacher_id" to teacherId.toString(), "student_id" to studentId.toString())
                val data = result.parseList(classParser<Absent>())
                if (data.isNotEmpty()){
                    absentData.addAll(data)
                }
            }
        }
    }
}