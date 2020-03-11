package com.example.osen.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.model.data.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.math.BigInteger
import java.security.MessageDigest

class Register : AppCompatActivity() {
    lateinit var user: User
    lateinit var auth: FirebaseAuth
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var reTypePass: EditText

    var valid = true
    var validUsername = true

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        reTypePass = findViewById(R.id.reTypePass)

//        username.addTextChangedListener(object : TextWatcher{
//            override fun afterTextChanged(s: Editable?) {
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val str = stringFilter(username.text.toString())
//                if(username.text.toString() != str){
//                    validUsername = false
//                    username.setError("Username hanya terdiri dari a-z, A-Z, 0-9, _")
//                }else{
//                    validUsername = true
//                }
//            }
//
//        })

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
    }

//    private fun stringFilter(str: String): String {
//        // Only letters, numbers and English blank characters are allowed
//        val regEx = "[^a-zA-Z0-9_]";
//        val p = Pattern.compile(regEx);
//        val m = p.matcher(str);
//        return m.replaceAll("");
//    }

    private fun checkEmail(email: String, password: String){
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
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

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    private fun shuffle(): String{
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz1234567890"
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
