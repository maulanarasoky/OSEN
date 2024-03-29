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
import com.example.osen.activity.EditStudent
import com.example.osen.database.database
import com.example.osen.model.Absent
import com.example.osen.model.AbsentOfDay
import com.example.osen.model.Score
import com.example.osen.model.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.student_list.*
import org.jetbrains.anko.db.*
import java.text.SimpleDateFormat
import java.util.*

class StudentList(
    private val activity: Activity,
    private val studentItems: MutableList<Student>,
    private val startDate: String,
    private val endDate: String,
    private val image: String
) : RecyclerView.Adapter<StudentList.ViewHolder>() {

    fun delete(position: Int) {
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
        holder.bindItem(activity, studentItems[position], startDate, endDate, image, position)
    }

    inner class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        private val list: MutableList<AbsentOfDay> = mutableListOf()
        var attendanceStatus: String = ""

        val absentData: MutableList<Absent> = mutableListOf()
        val absentOfDay: MutableList<AbsentOfDay> = mutableListOf()


        fun bindItem(
            activity: Activity,
            student: Student,
            startDate: String,
            endDate: String,
            image: String,
            position: Int
        ) {

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = sdf.format(Date())

            studentId.text = student.id.toString()
            studentName.text = student.name

            if (!(currentDate >= startDate && currentDate <= endDate)) {
                action.visibility = View.GONE
                done.text = "-"
                done.visibility = View.VISIBLE
            } else {
                checkTodayAbsent(student.teacher_id, student.className, currentDate, student.id)

                if (absentOfDay.isNotEmpty()) {
                    action.visibility = View.GONE
                    done.visibility = View.VISIBLE
                    done.text = absentOfDay[0].keterangan
                } else {
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
                            if (action.selectedItem.toString() != "Pilih") {
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
                                    var column = ""
                                    var add: Int? = 0
                                    when (action.selectedItem.toString()) {
                                        "Alfa" -> {
                                            column = Absent.ALFA
                                            add = absentData[0].alfa?.plus(1)
                                        }
                                        "Sakit" -> {
                                            column = Absent.SAKIT
                                            add = absentData[0].sakit?.plus(1)
                                        }
                                        "Izin" -> {
                                            column = Absent.IZIN
                                            add = absentData[0].izin?.plus(1)
                                        }
                                        "Hadir" -> {
                                            column = Absent.HADIR
                                            add = absentData[0].hadir?.plus(1)
                                        }
                                        else -> error("Error")
                                    }
                                    val queryUpdate = update(Absent.TABLE_ABSENT, column to add)
                                        .whereArgs(
                                            "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id}) AND (CLASS = {class_name})",
                                            "student_id" to student.id.toString(),
                                            "teacher_id" to student.teacher_id.toString(),
                                            "class_name" to student.className.toString()
                                        )

                                    queryUpdate.exec()
                                }
                                action.visibility = View.GONE
                                done.visibility = View.VISIBLE
                                done.text = action.selectedItem.toString()
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    }
                }
            }

            itemView.setOnClickListener {
                var statusAbsent = ""
                if (absentOfDay.isNotEmpty()) {
                    attendanceStatus = absentOfDay[0].keterangan.toString()
                } else {
                    attendanceStatus = "Pilih"
                }

                if (absentOfDay.isEmpty()) {
                    statusAbsent = "Tidak Ada"
                } else {
                    statusAbsent = "Ada"
                }
                val intent = Intent(activity, EditStudent::class.java)
                intent.putExtra(EditStudent.data, student)
                intent.putExtra(EditStudent.startDate, startDate)
                intent.putExtra(EditStudent.endDate, endDate)
                intent.putExtra(EditStudent.attendanceStatus, attendanceStatus)
                intent.putExtra(EditStudent.statusAbsent, statusAbsent)
                activity.startActivityForResult(intent, EditStudent.REQUEST_CODE_ABSENT)
            }

            deleteStudent.setOnClickListener {
                deleteRow(student, position)
            }
        }

        fun deleteRow(student: Student, position: Int) {
            val tempName = student.name
            val dialogWarningDelete =
                SweetAlertDialog(itemView.context, SweetAlertDialog.WARNING_TYPE)
            dialogWarningDelete.progressHelper.barColor = Color.parseColor("#A5DC86")
            dialogWarningDelete.titleText =
                "Apakah Anda Yakin Ingin Menghapus " + student.name + " ?"
            dialogWarningDelete.confirmText = "Hapus"
            dialogWarningDelete.cancelText = "Batalkan"
            dialogWarningDelete.showCancelButton(true)
            dialogWarningDelete.setConfirmClickListener {
                itemView.context.database.use {
                    delete(
                        Absent.TABLE_ABSENT,
                        "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id}) AND (CLASS = {class_name})",
                        "student_id" to student.id.toString(),
                        "teacher_id" to student.teacher_id.toString(),
                        "class_name" to student.className.toString()
                    )
                    delete(
                        AbsentOfDay.TABLE_ABSENTOFDAY,
                        "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id}) AND (CLASS = {class_name})",
                        "student_id" to student.id.toString(),
                        "teacher_id" to student.teacher_id.toString(),
                        "class_name" to student.className.toString()
                    )
                    delete(
                        Score.TABLE_SCORE,
                        "(STUDENT_ID = {student_id}) AND (CLASS = {class_name})",
                        "student_id" to student.id.toString(),
                        "class_name" to student.className.toString()
                    )
                    val queryDeleteStudent = delete(
                        Student.TABLE_STUDENT,
                        "(ID_ = {student_id}) AND (CLASS = {class_name}) AND (TEACHER_ID = {teacher_id})",
                        "student_id" to student.id.toString(),
                        "class_name" to student.className.toString(),
                        "teacher_id" to student.teacher_id.toString()
                    )
                    if (queryDeleteStudent > 0) {
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

        fun initSpinnerKehadiran() {
            ArrayAdapter.createFromResource(
                itemView.context,
                R.array.keterangan_hadir,
                R.layout.spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                action.adapter = adapter
            }
        }

        fun checkTodayAbsent(
            teacherId: String?,
            className: String?,
            date: String?,
            studentId: Int?
        ) {
            itemView.context.database.use {
                val result = select(AbsentOfDay.TABLE_ABSENTOFDAY).whereArgs(
                    "(TEACHER_ID = {teacher_id}) AND (STUDENT_ID = {student_id}) AND (CLASS = {className}) AND (DATE = {todayDate}) LIMIT 1",
                    "teacher_id" to teacherId.toString(),
                    "student_id" to studentId.toString(),
                    "className" to className.toString(),
                    "todayDate" to date.toString()
                )
                val data = result.parseList(classParser<AbsentOfDay>())
                if (data.isNotEmpty()) {
                    absentOfDay.addAll(data)
                }
            }
        }

        fun showAbsentData(teacherId: String?, studentId: Int?) {
            itemView.context.database.use {
                val result = select(Absent.TABLE_ABSENT).whereArgs(
                    "(TEACHER_ID = {teacher_id}) AND (STUDENT_ID = {student_id}) LIMIT 1",
                    "teacher_id" to teacherId.toString(),
                    "student_id" to studentId.toString()
                )
                val data = result.parseList(classParser<Absent>())
                if (data.isNotEmpty()) {
                    absentData.addAll(data)
                }
            }
        }
    }
}