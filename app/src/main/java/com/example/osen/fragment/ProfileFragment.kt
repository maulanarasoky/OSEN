package com.example.osen.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.activity.BackupRestore
import com.example.osen.activity.ChangePassword
import com.example.osen.activity.ExportImport
import com.example.osen.activity.Login
import com.example.osen.database.database
import com.example.osen.interfaces.MyAsyncCallback
import com.example.osen.model.Category
import com.example.osen.model.Classroom
import com.example.osen.model.Student
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.support.v4.startActivity
import java.lang.ref.WeakReference

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), MyAsyncCallback {

    lateinit var auth: FirebaseAuth

    var totalClasses: Int? = 0
    var totalStudents: Int? = 0
    var totalCategories: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val demoAsync = DemoAsync(this)
        demoAsync.execute()
    }

    private fun countClass(teacher_id: String) {
        database.use {
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs(
                "(TEACHER_ID = {teacher_id})",
                "teacher_id" to teacher_id
            )
            val data = result.parseList(classParser<Classroom>())
            if (data.isNotEmpty()) {
                totalClasses = data.size
            }
        }
    }

    private fun countStudent(teacher_id: String) {
        database.use {
            val result = select(Student.TABLE_STUDENT).whereArgs(
                "(TEACHER_ID = {teacher_id})",
                "teacher_id" to teacher_id
            )
            val data = result.parseList(classParser<Student>())
            if (data.isNotEmpty()) {
                totalStudents = data.size
            }
        }
    }

    private fun countCategory(teacher_id: String) {
        database.use {
            val result = select(Category.TABLE_CATEGORY).whereArgs(
                "(TEACHER_ID = {teacher_id})",
                "teacher_id" to teacher_id
            )
            val data = result.parseList(classParser<Category>())
            if (data.isNotEmpty()) {
                totalCategories = data.size
            }
        }
    }

    inner class DemoAsync(listener: MyAsyncCallback): AsyncTask<Void, Unit, Unit>(){

        private val myListener: WeakReference<MyAsyncCallback> = WeakReference(listener)

        override fun onPreExecute() {
            super.onPreExecute()
            val myListener = myListener.get()
            myListener?.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?) {
            countClass(auth.currentUser?.uid.toString())
            countStudent(auth.currentUser?.uid.toString())
            countCategory(auth.currentUser?.uid.toString())
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val myListener = myListener.get()
            myListener?.onPostExecute()
        }

    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
        imageProfile.visibility = View.GONE
        email.visibility = View.GONE
        linear1.visibility = View.GONE
        linear2.visibility = View.GONE
        linear3.visibility = View.GONE
        linear4.visibility = View.GONE
        linear5.visibility = View.GONE
    }

    override fun onPostExecute() {
        progressBar.visibility = View.GONE
        imageProfile.visibility = View.VISIBLE
        email.visibility = View.VISIBLE
        linear1.visibility = View.VISIBLE
        linear2.visibility = View.VISIBLE
        linear3.visibility = View.VISIBLE
        linear4.visibility = View.VISIBLE
        linear5.visibility = View.VISIBLE

        email.text = auth.currentUser?.email

        totalClass.text = "$totalClasses Kelas"
        totalStudent.text = "$totalStudents Murid"
        totalCategory.text = "$totalCategories Kategori"


        linear2.setOnClickListener {
            startActivity<ChangePassword>()
        }

        linear3.setOnClickListener {
            startActivity<ExportImport>()
        }

        linear4.setOnClickListener {
            startActivity<BackupRestore>()
        }

        linear5.setOnClickListener {
            val dialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Apakah Anda ingin keluar ?"
            dialog.setCancelable(false)
            dialog.showCancelButton(true)
            dialog.cancelText = "Batal"
            dialog.confirmText = "Keluar"
            dialog.setConfirmClickListener {
                dialog.dismissWithAnimation()
                auth.signOut()
                startActivity<Login>()
                activity!!.finish()
            }
            dialog.show()
        }
    }

}
