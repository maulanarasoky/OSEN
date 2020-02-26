package com.example.osen.adapter

import android.app.Activity
import android.content.Intent
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
import com.example.osen.activity.ClassDetails
import com.example.osen.activity.EditData
import com.example.osen.database.database
import com.example.osen.model.Absent
import com.example.osen.model.AbsentOfDay
import com.example.osen.model.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.student_list.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
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

        private val list: MutableList<AbsentOfDay> = mutableListOf()
        var keterangan: String = ""

        val absentData: MutableList<Absent> = mutableListOf()
        val absentOfDay: MutableList<AbsentOfDay> = mutableListOf()


        fun bindItem(student: Student, image:String, position: Int) {

            studentId.text = student.id.toString()
            studentName.text = student.name

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = sdf.format(Date())

            checkTodayAbsent(student.teacher_id, student.className, currentDate, student.id)
            Log.d("coba", absentOfDay.size.toString())

            if(absentOfDay.isNotEmpty()){
                action.visibility = View.GONE
                done.visibility = View.VISIBLE
                done.text = absentOfDay[0].keterangan
            }else{
                Log.d("kosong", "array")
                initSpinnerKehadiran()
                action.visibility = View.VISIBLE
                action.setSelection(0)
                done.visibility = View.GONE

                action.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if(action.selectedItem.toString() != "-"){
                            itemView.context.database.use {
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
                deleteRow(student, position)
            }
        }

        fun deleteRow(student: Student, position: Int){
            val tempName = student.name
            val dialogWarningDelete = SweetAlertDialog(itemView.context, SweetAlertDialog.WARNING_TYPE)
            dialogWarningDelete.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialogWarningDelete.titleText = "Apakah Anda Yakin Ingin Menghapus " + student.name + " ?"
            dialogWarningDelete.confirmText = "Hapus"
            dialogWarningDelete.cancelText = "Batalkan"
            dialogWarningDelete.showCancelButton(true)
            dialogWarningDelete.setConfirmClickListener {
                itemView.context.database.use {
                    delete(Absent.TABLE_ABSENT, "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id}) AND (CLASS = {class_name})", "student_id" to student.id.toString(), "teacher_id" to student.teacher_id.toString(), "class_name" to student.className.toString())
                    delete(AbsentOfDay.TABLE_ABSENTOFDAY, "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id}) AND (CLASS = {class_name})", "student_id" to student.id.toString(), "teacher_id" to student.teacher_id.toString(), "class_name" to student.className.toString())
                    val queryDeleteStudent = delete(Student.TABLE_STUDENT, "(ID_ = {student_id}) AND (CLASS = {class_name}) AND (TEACHER_ID = {teacher_id})", "student_id" to student.id.toString(), "class_name" to student.className.toString(), "teacher_id" to student.teacher_id.toString())
                    if(queryDeleteStudent > 0){
                        delete(position)
                        dialogWarningDelete.titleText = "Berhasil Menghapus $tempName"
                        dialogWarningDelete.confirmText = "OK"
                        dialogWarningDelete.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        dialogWarningDelete.showCancelButton(false)
                        dialogWarningDelete.setCancelable(false)
                        dialogWarningDelete.setConfirmClickListener {
                            dialogWarningDelete.dismissWithAnimation()
                        }
                    }
                }
            }.show()
        }

        fun initSpinnerKehadiran(){
            ArrayAdapter.createFromResource(
                itemView.context,
                R.array.keterangan_hadir,
                R.layout.spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                action.adapter = adapter
            }
        }

        fun checkTodayAbsent(teacherId: Int?, className: String?, date: String?, studentId: Int?){
            itemView.context.database.use {
                val result = select(AbsentOfDay.TABLE_ABSENTOFDAY).whereArgs("(TEACHER_ID = {teacher_id}) AND (STUDENT_ID = {student_id}) AND (CLASS = {className}) AND (DATE = {todayDate}) LIMIT 1", "teacher_id" to teacherId.toString(), "student_id" to studentId.toString(), "className" to className.toString(), "todayDate" to date.toString())
                val data = result.parseList(classParser<AbsentOfDay>())
                if (data.isNotEmpty()){
                    absentOfDay.addAll(data)
                }
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