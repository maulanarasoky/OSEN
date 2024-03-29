package com.example.osen.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class Login : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            checkEmail(email.text.toString(), password.text.toString())
        }

        btnRegister.setOnClickListener {
            finish()
            registerActivity()
        }

        forgotPass.setOnClickListener {
            startActivity<ForgotPassword>()
        }
    }

    private fun checkEmail(email: String, password: String) {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.setCancelable(false)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { check ->
            if (check.isSuccessful) {
                if (auth.currentUser?.isEmailVerified!!) {
                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    dialog.titleText = "Login Berhasil"
                    dialog.setConfirmClickListener {
                        dialog.dismissWithAnimation()
                        startActivity<MainActivity>()
                        this.finish()
                    }
                } else {
                    dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    dialog.titleText = "Silahkan Lakukan Verifikasi Email Terlebih Dahulu"
                }
            } else {
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = "Password Salah"
            }
        }
        dialog.show()
    }

    private fun registerActivity() {
        startActivity<Register>()
    }
}
