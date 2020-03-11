package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class Login : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var email: EditText
    lateinit var password: EditText

    companion object{
        const val VERIFIED = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            checkEmail(email.text.toString(), password.text.toString())
        }

        btnRegister.setOnClickListener {
            finish()
            registerActivity()
        }
    }

    private fun checkEmail(email: String, password: String){
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.setCancelable(false)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { check ->
            if(check.isSuccessful){
                if(auth.currentUser?.isEmailVerified!!){
                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    dialog.titleText = "Login Berhasil"
                    dialog.setConfirmClickListener {
                        dialog.dismissWithAnimation()
                        finish()
                        startActivity<MainActivity>()
                    }
                }else{
                    dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    dialog.titleText = "Silahkan Lakukan Verifikasi Email Terlebih Dahulu"
                }
            }else{
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = check.exception?.message
            }
        }
        dialog.show()
    }

    private fun registerActivity(){
        startActivity<Register>()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(MainActivity.RESULT_CODE)
        finish()
    }
}
