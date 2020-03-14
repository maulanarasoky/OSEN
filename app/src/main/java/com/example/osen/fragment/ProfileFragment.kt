package com.example.osen.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide

import com.example.osen.R
import com.example.osen.activity.BackupRestore
import com.example.osen.activity.ChangePassword
import com.example.osen.activity.ExportImport
import com.example.osen.activity.Login
import com.example.osen.database.database
import com.example.osen.model.Category
import com.example.osen.model.Classroom
import com.example.osen.model.Student
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.support.v4.startActivity

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    lateinit var auth: FirebaseAuth

    lateinit var navigation: BottomNavigationView

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

        navigation = activity!!.findViewById(R.id.bottom_navigation)

        auth = FirebaseAuth.getInstance()

        countClass(auth.currentUser?.uid.toString())
        countStudent(auth.currentUser?.uid.toString())
        countCategory(auth.currentUser?.uid.toString())

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

    private fun countClass(teacher_id: String){
        database.use {
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs("(TEACHER_ID = {teacher_id})", "teacher_id" to teacher_id)
            val data = result.parseList(classParser<Classroom>())
            if(data.isNotEmpty()){
                totalClasses = data.size
            }
        }
    }

    private fun countStudent(teacher_id: String){
        database.use {
            val result = select(Student.TABLE_STUDENT).whereArgs("(TEACHER_ID = {teacher_id})", "teacher_id" to teacher_id)
            val data = result.parseList(classParser<Student>())
            if(data.isNotEmpty()){
                totalStudents = data.size
            }
        }
    }

    private fun countCategory(teacher_id: String){
        database.use {
            val result = select(Category.TABLE_CATEGORY).whereArgs("(TEACHER_ID = {teacher_id})", "teacher_id" to teacher_id)
            val data = result.parseList(classParser<Category>())
            if(data.isNotEmpty()){
                totalCategories = data.size
            }
        }
    }

}
