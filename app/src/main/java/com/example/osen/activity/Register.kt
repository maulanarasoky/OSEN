package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity

class Register : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var reTypePass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        reTypePass = findViewById(R.id.reTypePass)

        btnRegister.setOnClickListener {
            if(TextUtils.isEmpty(email.text.toString().trim())){
                email.setError("Email harus diisi")
                return@setOnClickListener
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString().trim()).matches()){
                email.setError("Email tidak valid")
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password.text.toString().trim())){
                password.setError("Password harus diisi")
                return@setOnClickListener
            }

            if(password.text.length < 6){
                password.setError("Minimal 6 digit angka atau huruf")
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(reTypePass.text.toString().trim())){
                reTypePass.setError("Re-Type Password harus diisi")
                return@setOnClickListener
            }

            if(reTypePass.text.length < 6){
                reTypePass.setError("Minimal 6 digit angka atau huruf")
                return@setOnClickListener
            }

            if(reTypePass.text.toString() != password.text.toString()){
                reTypePass.setError("Kedua kolom tidak sama")
                password.setError("Kedua kolom tidak sama")
                return@setOnClickListener
            }

            if(password.text.toString() == reTypePass.text.toString()){
                checkEmail(email.text.toString(), password.text.toString())
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
