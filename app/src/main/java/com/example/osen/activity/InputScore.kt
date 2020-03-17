package com.example.osen.activity

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.interfaces.MyAsyncCallback
import com.example.osen.model.Score
import com.example.osen.model.Student
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_input_score.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import java.lang.Exception
import java.lang.ref.WeakReference

class InputScore : AppCompatActivity(), MyAsyncCallback {

    companion object {
        const val data = "data"
    }

    var mUts: Int? = 0
    var mPersentaseUts: Int? = 0
    var mUas: Int? = 0
    var mPersentaseUas: Int? = 0
    var mAss1: Int? = 0
    var mPersentaseAss1: Int? = 0
    var mAss2: Int? = 0
    var mPersentaseAss2: Int? = 0
    var mAss3: Int? = 0
    var mPersentaseAss3: Int? = 0
    var student_id: Int = 0
    var teacher_id: String = ""
    var class_name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_score)

        val parcel: Student? = intent.getParcelableExtra(data)

        student_id = parcel?.id.toString().toInt()
        teacher_id = parcel?.teacher_id.toString()
        class_name = parcel?.className.toString()

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(
            ContextCompat.getColor(
                applicationContext,
                android.R.color.transparent
            )
        )

        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        collapsingToolbar.setContentScrimColor(Color.parseColor("#48cfad"))

        supportActionBar?.title = parcel?.name

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        DemoAsync(this).execute()
    }

    private fun initData() {

        uts.setText(mUts.toString(), TextView.BufferType.EDITABLE)
        persentaseUts.setText(mPersentaseUts.toString(), TextView.BufferType.EDITABLE)

        uas.setText(mUas.toString(), TextView.BufferType.EDITABLE)
        persentaseUas.setText(mPersentaseUas.toString(), TextView.BufferType.EDITABLE)

        ass1.setText(mAss1.toString(), TextView.BufferType.EDITABLE)
        persentaseAss1.setText(mPersentaseAss1.toString(), TextView.BufferType.EDITABLE)

        ass2.setText(mAss2.toString(), TextView.BufferType.EDITABLE)
        persentaseAss2.setText(mPersentaseAss2.toString(), TextView.BufferType.EDITABLE)

        ass3.setText(mAss3.toString(), TextView.BufferType.EDITABLE)
        persentaseAss3.setText(mPersentaseAss3.toString(), TextView.BufferType.EDITABLE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showScore(student_id: Int?, teacher_id: String?) {
        database.use {
            val result = select(Score.TABLE_SCORE).whereArgs(
                "(STUDENT_ID = {student_id}) AND (TEACHER_ID = {teacher_id}) LIMIT 1",
                "student_id" to student_id.toString(),
                "teacher_id" to teacher_id.toString()
            )
            val data = result.parseList(classParser<Score>())
            if (data.isNotEmpty()) {
                mUts = data[0].uts
                mPersentaseUts = data[0].persentaseUts
                mUas = data[0].uas
                mPersentaseUas = data[0].persentaseUas
                mAss1 = data[0].assessment1
                mPersentaseAss1 = data[0].persentaseAssessment1
                mAss2 = data[0].assessment2
                mPersentaseAss2 = data[0].persentaseAssessment2
                mAss3 = data[0].assessment3
                mPersentaseAss3 = data[0].persentaseAssessment3
            }
        }
    }

    private fun checkConditionScore(student_id: Int?, teacher_id: String?, class_name: String?) {
        if (uts.text.toString() == "" || persentaseUts.text.toString() == "") {
            if (uts.text.toString() == "") {
                uts.error = "Isi Field Dengan Benar"
            }

            if (persentaseUts.text.toString() == "") {
                persentaseUts.error = "Isi Field Dengan Benar"
            }
            return
        }
        if (uas.text.toString() == "" || persentaseUas.text.toString() == "") {
            if (uas.text.toString() == "") {
                uas.error = "Isi Field Dengan Benar"
            }

            if (persentaseUas.text.toString() == "") {
                persentaseUas.error = "Isi Field Dengan Benar"
            }
            return
        }
        if (ass1.text.toString() == "" || persentaseAss1.text.toString() == "") {
            if (ass1.text.toString() == "") {
                ass1.error = "Isi Field Dengan Benar"
            }

            if (persentaseAss1.text.toString() == "") {
                persentaseAss1.error = "Isi Field Dengan Benar"
            }
            return
        }
        if (ass2.text.toString() == "" || persentaseAss2.text.toString() == "") {
            if (ass2.text.toString() == "") {
                ass2.error = "Isi Field Dengan Benar"
            }

            if (persentaseAss2.text.toString() == "") {
                persentaseAss2.error = "Isi Field Dengan Benar"
            }
            return
        }
        if (ass3.text.toString() == "" || persentaseAss3.text.toString() == "") {
            if (ass3.text.toString() == "") {
                ass3.error = "Isi Field Dengan Benar"
            }

            if (persentaseAss3.text.toString() == "") {
                persentaseAss3.error = "Isi Field Dengan Benar"
            }
            return
        }

        if(uts.text.toString().length > 9){
            uts.error = "Nilai field tidak valid"
            return
        }

        if(persentaseUts.text.toString().length > 9){
            persentaseUts.error = "Nilai field tidak valid"
            return
        }

        if(uas.text.toString().length > 9){
            uas.error = "Nilai field tidak valid"
            return
        }

        if(persentaseUas.text.toString().length > 9){
            persentaseUas.error = "Nilai field tidak valid"
            return
        }

        if(ass1.text.toString().length > 9){
            ass1.error = "Nilai field tidak valid"
            return
        }

        if(persentaseAss1.text.toString().length > 9){
            persentaseAss1.error = "Nilai field tidak valid"
            return
        }

        if(ass2.text.toString().length > 9){
            ass2.error = "Nilai field tidak valid"
            return
        }

        if(persentaseAss2.text.toString().length > 9){
            persentaseAss2.error = "Nilai field tidak valid"
            return
        }

        if(ass3.text.toString().length > 9){
            ass3.error = "Nilai field tidak valid"
            return
        }

        if(persentaseAss3.text.toString().length > 9){
            persentaseAss3.error = "Nilai field tidak valid"
            return
        }

        if (persentaseUts.text.toString().toInt().plus(persentaseUas.text.toString().toInt())
                .plus(persentaseAss1.text.toString().toInt())
                .plus(persentaseAss2.text.toString().toInt())
                .plus(persentaseAss3.text.toString().toInt()) > 100
        ) {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Total Seluruh Persentase Tidak Boleh Lebih Dari 100"
            dialog.setOnCancelListener {
                dialog.dismissWithAnimation()
            }
            dialog.show()
            return
        }

        if (persentaseUts.text.toString().toInt().plus(persentaseUas.text.toString().toInt())
                .plus(persentaseAss1.text.toString().toInt())
                .plus(persentaseAss2.text.toString().toInt())
                .plus(persentaseAss3.text.toString().toInt()) < 100
        ) {
            val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Total Seluruh Persentase Tidak Boleh Kurang Dari 100"
            dialog.setOnCancelListener {
                dialog.dismissWithAnimation()
            }
            dialog.show()
            return
        }

        if (persentaseUts.text.toString().toInt().plus(persentaseUas.text.toString().toInt())
                .plus(persentaseAss1.text.toString().toInt())
                .plus(persentaseAss2.text.toString().toInt())
                .plus(persentaseAss3.text.toString().toInt()) == 100
        ) {
            var condition = ""
            if (uts.text.toString().toInt() > 100) {
                condition = "UTS"
            } else if (uas.text.toString().toInt() > 100) {
                condition = "UAS"
            } else if (ass1.text.toString().toInt() > 100) {
                condition = "Assessment 1"
            } else if (ass2.text.toString().toInt() > 100) {
                condition = "Assessment 2"
            } else if (ass3.text.toString().toInt() > 100) {
                condition = "Assessment 3"
            }

            if(condition != ""){
                val dialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Nilai $condition Tidak Boleh Lebih Dari 100"
                dialog.setOnCancelListener {
                    dialog.dismissWithAnimation()
                }
                dialog.show()
                return
            }
            inputScore(student_id, teacher_id, class_name)
        }
    }

    private fun inputScore(student_id: Int?, teacher_id: String?, class_name: String?) {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        try {
            database.use {
                val query = update(
                    Score.TABLE_SCORE,
                    Score.UTS to uts.text.toString().toInt(),
                    Score.PERSENTASE_UTS to persentaseUts.text.toString().toInt(),
                    Score.UAS to uas.text.toString().toInt(),
                    Score.PERSENTASE_UAS to persentaseUas.text.toString().toInt(),
                    Score.ASSESSMENT_1 to ass1.text.toString().toInt(),
                    Score.PERSENTASE_ASSESSMENT_1 to persentaseAss1.text.toString().toInt(),
                    Score.ASSESSMENT_2 to ass2.text.toString().toInt(),
                    Score.PERSENTASE_ASSESSMENT_2 to persentaseAss2.text.toString().toInt(),
                    Score.ASSESSMENT_3 to ass3.text.toString().toInt(),
                    Score.PERSENTASE_ASSESSMENT_3 to persentaseAss3.text.toString().toInt()
                ).whereArgs(
                    "(STUDENT_ID = {student_id}) AND (CLASS = {class_name}) AND (TEACHER_ID = {teacher_id})",
                    "student_id" to student_id.toString(),
                    "teacher_id" to teacher_id.toString(),
                    "class_name" to class_name.toString()
                )
                Log.d("update", query.exec().toString())
                if(query.exec() > 0){
                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    dialog.titleText = "Nilai Berhasil Di Input"
                    dialog.setOnCancelListener {
                        dialog.dismissWithAnimation()
                    }
                }else{
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    dialog.titleText = "Nilai Gagal Di Input"
                    dialog.setOnCancelListener {
                        dialog.dismissWithAnimation()
                    }
                }
            }
            dialog.show()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    inner class DemoAsync(listener: MyAsyncCallback): AsyncTask<String, Unit, Unit>(){

        private val myListener: WeakReference<MyAsyncCallback> = WeakReference(listener)

        override fun onPreExecute() {
            super.onPreExecute()
            val listener = myListener.get()
            listener?.onPreExecute()
        }

        override fun doInBackground(vararg params: String?) {
            showScore(student_id, teacher_id)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val listener = myListener.get()
            listener?.onPostExecute()
        }

    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
        textInputScore.visibility = View.GONE
        table1.visibility = View.GONE
        table2.visibility = View.GONE
        table3.visibility = View.GONE
        table4.visibility = View.GONE
        table5.visibility = View.GONE
    }

    override fun onPostExecute() {
        progressBar.visibility = View.GONE
        textInputScore.visibility = View.VISIBLE
        table1.visibility = View.VISIBLE
        table2.visibility = View.VISIBLE
        table3.visibility = View.VISIBLE
        table4.visibility = View.VISIBLE
        table5.visibility = View.VISIBLE
        initData()
        inputScore.setOnClickListener {
            checkConditionScore(student_id, teacher_id, class_name)
        }
    }
}
