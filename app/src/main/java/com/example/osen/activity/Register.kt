package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.osen.R
import com.example.osen.model.data.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast
import java.math.BigInteger
import java.security.MessageDigest
import java.util.regex.Pattern

class Register : AppCompatActivity() {
    lateinit var user: User
    lateinit var database: DatabaseReference
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var reTypePass: EditText

    var valid = true
    var validUsername = true

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        reTypePass = findViewById(R.id.reTypePass)

        database = FirebaseDatabase.getInstance().reference.child("Account")
        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                count = p0.childrenCount.toInt()
            }

        })

        username.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = stringFilter(username.text.toString())
                if(username.text.toString() != str){
                    validUsername = false
                    username.setError("Username hanya terdiri dari a-z, A-Z, 0-9, _")
                }else{
                    validUsername = true
                }
            }

        })

        btnRegister.setOnClickListener {
            if (validUsername){
                if(TextUtils.isEmpty(username.text.toString().trim())){
                    valid = false
                    username.setError("Username harus diisi")
                }

                if(TextUtils.isEmpty(password.text.toString().trim())){
                    valid = false
                    username.setError("Username harus diisi")
                }

                if(TextUtils.isEmpty(reTypePass.text.toString().trim())){
                    valid = false
                    username.setError("Username harus diisi")
                }

                if(valid){
                    if(password.text.toString() == reTypePass.text.toString()){
                        checkUsername(username.text.toString())
                    }
                }
            }
        }
    }

    private fun stringFilter(str: String): String {
        // Only letters, numbers and English blank characters are allowed
        val regEx = "[^a-zA-Z0-9_]";
        val p = Pattern.compile(regEx);
        val m = p.matcher(str);
        return m.replaceAll("");
    }

    private fun checkUsername(username: String){
        val dialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
        database.child(username).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(!p0.exists()){
                    val id = database.push().key.toString()

                    user = User(id, username, password.text.toString().md5())

                    database.child(username).setValue(user)
                    dialog.titleText = "Registrasi Berhasil"
                }else{
                    dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    dialog.titleText = "Username Sudah Terdaftar"
                }
            }
        })
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
