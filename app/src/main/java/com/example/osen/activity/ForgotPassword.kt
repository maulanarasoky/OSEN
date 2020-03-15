package com.example.osen.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        btnForgotPass.setOnClickListener {
            if (TextUtils.isEmpty(email.text.toString().trim())) {
                email.error = "Email harus diisi"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString().trim()).matches()) {
                email.error = "Email tidak valid"
                return@setOnClickListener
            }

            forgotPassword(email.text.toString())
        }
    }

    private fun forgotPassword(email: String) {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.setCancelable(false)
        auth.sendPasswordResetEmail(email).addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                dialog.titleText = "Email Reset Ulang Password Telah Dikirimkan Ke Email Anda"
                dialog.setConfirmClickListener {
                    dialog.dismissWithAnimation()
                    finish()
                }
            } else {
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Email Belum Terdaftar"
            }
        }
        dialog.show()
    }
}
