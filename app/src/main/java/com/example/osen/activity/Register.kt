package com.example.osen.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.model.data.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class Register : AppCompatActivity() {
    lateinit var user: User
    lateinit var auth: FirebaseAuth
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var reTypePass: EditText

    var valid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        reTypePass = findViewById(R.id.reTypePass)

        btnRegister.setOnClickListener {
            if(TextUtils.isEmpty(email.text.toString().trim())){
                valid = false
                email.setError("Username harus diisi")
            }

            if(TextUtils.isEmpty(password.text.toString().trim())){
                valid = false
                password.setError("Password harus diisi")
            }

            if(TextUtils.isEmpty(reTypePass.text.toString().trim())){
                valid = false
                reTypePass.setError("Re-Type Password harus diisi")
            }

            if(valid){
                if(password.text.toString() == reTypePass.text.toString()){
                    checkEmail(email.text.toString(), password.text.toString())
                }
            }
        }

        haveAccount.setOnClickListener {
            startActivity<Login>()
            this.finish()
        }
    }

    private fun checkEmail(email: String, password: String){
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.setCancelable(false)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { insert ->
            if(insert.isSuccessful){
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verify ->
                    if(verify.isSuccessful){
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        dialog.titleText = "Registrasi Berhasil"
                        dialog.contentText = "Silahkan lakukan verifikasi"
                        dialog.setConfirmClickListener {
                            dialog.dismissWithAnimation()
                            finish()
                            startActivity<Login>()
                        }
                    }
                }
            }else{
                dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                dialog.titleText = insert.exception?.message
            }
        }
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(MainActivity.RESULT_CODE)
        finish()
    }
}
