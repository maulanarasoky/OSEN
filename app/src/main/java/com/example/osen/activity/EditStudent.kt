package com.example.osen.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Absent
import com.example.osen.model.AbsentOfDay
import com.example.osen.model.Score
import com.example.osen.model.Student
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_edit_student.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import java.text.SimpleDateFormat
import java.util.*

class EditStudent : AppCompatActivity() {

    companion object{
        const val data = "data"
        const val startDate = "startDate"
        const val endDate = "endDate"
        const val attendanceStatus = "attendanceStatus"
        const val statusAbsent = "statusAbsent"

        const val REQUEST_CODE_SCORE = 100

        const val REQUEST_CODE_ABSENT = 101
    }

    val list: MutableList<Absent> = mutableListOf()

    val scoreList: MutableList<Score> = mutableListOf()

    var checkDailyAbsent = false
    var description = ""

    var totalAlfa: Int? = 0
    var totalSakit: Int? = 0
    var totalIzin: Int? = 0
    var totalHadir: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val parcel: Student? = intent.getParcelableExtra(data)
        val startDate: String? = intent.getStringExtra(startDate)
        val endDate: String? = intent.getStringExtra(endDate)
        val status = intent.getStringExtra(statusAbsent)

        if(currentDate < startDate.toString() || currentDate > endDate.toString() || status == "Tidak Ada"){
            btnAbsent.visibility = View.GONE
            spinnerAbsent.visibility = View.GONE
        }

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.transparent))

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        collapsingToolbar.setContentScrimColor(Color.parseColor("#48cfad"))

        supportActionBar?.title = parcel?.name

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        studentName.text = parcel?.name
        gender.text = parcel?.gender
        showScore(parcel?.id, parcel?.teacher_id)
        countScore()

        Glide.with(this).load(resources.getDrawable(R.drawable.ic_student)).apply(
            RequestOptions.overrideOf(500,500)).into(classImage)

        initAbsentData()
        countPersentaseKehadiran()

        ArrayAdapter.createFromResource(this, R.array.keterangan_hadir, R.layout.spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAbsent.adapter = adapter

            val selectPosition = adapter.getPosition(intent.getStringExtra(attendanceStatus))
            spinnerAbsent.setSelection(selectPosition)
        }

        btnAbsent.setOnClickListener {
            if(spinnerAbsent.selectedItem.toString() == "-"){
                val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Silahkan Pilih Keterangan Terlebih Dahulu"
                dialog.setCancelable(false)
                dialog.confirmText = "OK"
                dialog.show()
                return@setOnClickListener
            }

            if(spinnerAbsent.selectedItem.toString() != "-"){
                checkDailyAbsent(parcel?.id, parcel?.teacher_id, parcel?.className, currentDate)
                if(description == spinnerAbsent.selectedItem.toString()){
                    val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    dialog.titleText = "Silahkan Pilih Keterangan Yang Berbeda Dari Sebelumnya"
                    dialog.setCancelable(false)
                    dialog.confirmText = "OK"
                    dialog.setConfirmClickListener {
                        dialog.dismissWithAnimation()
                    }.show()
                }else{
                    val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    dialog.titleText = "Apakah Anda Yakin ?"
                    dialog.setCancelable(false)
                    dialog.showCancelButton(true)
                    dialog.cancelText = "Tidak"
                    dialog.confirmText = "Ya"
                    dialog.setConfirmClickListener {
                        dailyAbsent(parcel?.id, parcel?.teacher_id, parcel?.className, currentDate)
                        absent(parcel?.id, parcel?.teacher_id, parcel?.className)
                        initAbsentData()
                        countPersentaseKehadiran()
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        dialog.showCancelButton(false)
                        dialog.titleText = "Absen Berhasil Dilakukan"
                        dialog.confirmText = "OK"
                        dialog.setConfirmClickListener {
                            dialog.dismissWithAnimation()
                        }
                    }.show()
                }
            }
        }

        inputScore.setOnClickListener {
            val intent = Intent(this, InputScore::class.java)
            startActivityForResult(intent, REQUEST_CODE_SCORE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SCORE){
            val parcel: Student? = intent.getParcelableExtra(Companion.data)
            showScore(parcel?.id, parcel?.teacher_id)
            countScore()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAbsentData(){
        showAbsent()

        alfa.text = list[0].alfa.toString()
        sakit.text = list[0].sakit.toString()
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
                totalAlfa = data[0].alfa
                totalSakit = data[0].sakit
                totalIzin = data[0].izin
                totalHadir = data[0].hadir
            }
        }
    }

    private fun checkDailyAbsent(student_id: Int?, teacher_id: String?, class_name: String?, date: String?){
        database.use {
            val result = select(AbsentOfDay.TABLE_ABSENTOFDAY).whereArgs("(STUDENT_ID = {student_id}) AND (CLASS = {class_name}) AND (TEACHER_ID = {teacher_id}) AND (DATE = {date}) LIMIT 1", "student_id" to student_id.toString(), "class_name" to class_name.toString(), "teacher_id" to teacher_id.toString(), "date" to date.toString())
            val data = result.parseList(classParser<AbsentOfDay>())
            if(data.isNotEmpty()){
                checkDailyAbsent = true
                description = data[0].keterangan.toString()
            }
        }
    }

    private fun dailyAbsent(student_id: Int?, teacher_id: String?, class_name: String?, date: String?){
        if(checkDailyAbsent){
            database.use {
                val queryUpdate = update(AbsentOfDay.TABLE_ABSENTOFDAY,
                AbsentOfDay.KETERANGAN to spinnerAbsent.selectedItem.toString()).whereArgs("(STUDENT_ID = {student_id}) AND (CLASS = {class_name}) AND (TEACHER_ID = {teacher_id}) AND (DATE = {date})", "student_id" to student_id.toString(), "class_name" to class_name.toString(), "teacher_id" to teacher_id.toString(), "date" to date.toString())
                queryUpdate.exec()
            }
        }else{
            database.use {
                insert(
                    AbsentOfDay.TABLE_ABSENTOFDAY,
                    AbsentOfDay.DATE to date,
                    AbsentOfDay.STUDENT_ID to student_id,
                    AbsentOfDay.KETERANGAN to spinnerAbsent.selectedItem.toString(),
                    AbsentOfDay.CLASS to class_name,
                    AbsentOfDay.TEACHER_ID to teacher_id
                )
            }
        }
    }

    private fun absent(student_id: Int?, teacher_id: String?, class_name: String?){
        var columnSubtract = ""
        var columnAdd = ""
        var subtract = 0
        var add = 0
        when (spinnerAbsent.selectedItem.toString()){
            "Alfa" ->{
                columnAdd = Absent.ALFA
                add = totalAlfa?.plus(1) ?: 0
                if(description == "Sakit"){
                    columnSubtract = Absent.SAKIT
                    subtract = totalSakit?.minus(1) ?: 0
                }

                if(description == "Izin"){
                    columnSubtract = Absent.IZIN
                    subtract = totalIzin?.minus(1) ?: 0
                }

                if(description == "Hadir"){
                    columnSubtract = Absent.HADIR
                    subtract = totalHadir?.minus(1) ?: 0
                }
            }
            "Sakit" -> {
                columnAdd = Absent.SAKIT
                add = totalSakit?.plus(1) ?: 0
                if(description == "Alfa"){
                    columnSubtract = Absent.ALFA
                    subtract = totalAlfa?.minus(1) ?: 0
                }

                if(description == "Izin"){
                    columnSubtract = Absent.IZIN
                    subtract = totalIzin?.minus(1) ?: 0
                }

                if(description == "Hadir"){
                    columnSubtract = Absent.HADIR
                    subtract = totalHadir?.minus(1) ?: 0
                }
            }
            "Izin" -> {
                columnAdd = Absent.IZIN
                add = totalIzin?.plus(1) ?: 0
                if(description == "Alfa"){
                    columnSubtract = Absent.ALFA
                    subtract = totalAlfa?.minus(1) ?: 0
                }

                if(description == "Sakit"){
                    columnSubtract = Absent.SAKIT
                    subtract = totalSakit?.minus(1) ?: 0
                }

                if(description == "Hadir"){
                    columnSubtract = Absent.HADIR
                    subtract = totalHadir?.minus(1) ?: 0
                }
            }
            "Hadir" -> {
                columnAdd = Absent.HADIR
                add = totalHadir?.plus(1) ?: 0
                if(description == "Alfa"){
                    columnSubtract = Absent.ALFA
                    subtract = totalAlfa?.minus(1) ?: 0
                }

                if(description == "Sakit"){
                    columnSubtract = Absent.SAKIT
                    subtract = totalSakit?.minus(1) ?: 0
                }

                if(description == "Izin"){
                    columnSubtract = Absent.IZIN
                    subtract = totalIzin?.minus(1) ?: 0
                }
            }
        }
        database.use {
            val queryUpdate = update(Absent.TABLE_ABSENT, columnAdd to add, columnSubtract to subtract).whereArgs("(STUDENT_ID = {student_id}) AND (CLASS = {class_name}) AND (TEACHER_ID = {teacher_id})", "student_id" to student_id.toString(), "class_name" to class_name.toString(), "teacher_id" to teacher_id.toString())
            queryUpdate.exec()
        }
    }

    private fun showScore(student_id: Int?, teacher_id: String?){
        scoreList.clear()
        database.use {
            val result = select(Score.TABLE_SCORE).whereArgs("(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id}) LIMIT 1", "student_id" to student_id.toString(), "teacher_id" to teacher_id.toString())
            val data = result.parseList(classParser<Score>())
            if(data.isNotEmpty()){
                scoreList.addAll(data)
            }
            Log.d("wew", data.toString())
        }
    }

    private fun countScore(){
        val uts: Float? = scoreList[0].persentaseUts?.times(scoreList[0].uts!!)?.div(100)?.toFloat()
        val uas: Float? = scoreList[0].persentaseUas?.times(scoreList[0].uas!!)?.div(100)?.toFloat()
        val ass1: Float? = scoreList[0].persentaseAssessment1?.times(scoreList[0].assessment1!!)?.div(100)?.toFloat()
        val ass2: Float? = scoreList[0].persentaseAssessment2?.times(scoreList[0].assessment2!!)?.div(100)?.toFloat()
        val ass3: Float? = scoreList[0].persentaseAssessment3?.times(scoreList[0].assessment3!!)?.div(100)?.toFloat()

        val result: Float? = uts?.plus(uas!!)?.plus(ass1!!)?.plus(ass2!!)?.plus(ass3!!)?.div(4)

        score.text = result.toString()
    }

    private fun countPersentaseKehadiran(){
        val totalAbsen: Float? = totalAlfa?.plus(totalSakit!!)?.plus(totalIzin!!)?.plus(totalHadir!!)?.toFloat()
        var persentase: Float? = totalHadir?.plus(totalIzin!!)?.div(totalAbsen!!)?.times(100)
        if(persentase.toString() == "NaN"){
            persentase = 0F
        }
        persentaseKehadiran.text = "Persentase Kehadiran $persentase%"
    }
}
