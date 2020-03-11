package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        btnForgotPass.setOnClickListener {
            forgotPassword(email.text.toString())
        }
    }

    private fun forgotPassword(email: String){
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.setCancelable(false)
        auth.sendPasswordResetEmail(email).addOnCompleteListener { p0 ->
            if(p0.isSuccessful){
                dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                dialog.titleText = "Email Reset Ulang Password Telah Dikirimkan Ke Email Anda"
                dialog.setConfirmClickListener {
                    dialog.dismissWithAnimation()
                    finish()
                }
            }else{
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Email Belum Terdaftar"
            }
        }
        dialog.show()
    }
}
