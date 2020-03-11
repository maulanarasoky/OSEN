package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.osen.R
import com.example.osen.model.data.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast
import java.math.BigInteger
import java.security.MessageDigest

class Register : AppCompatActivity() {
    lateinit var user: User
    lateinit var database: DatabaseReference

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        database = FirebaseDatabase.getInstance().reference.child("Account")
        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                count = p0.childrenCount.toInt()
            }

        })

        btnRegister.setOnClickListener {
            if(password.text.toString() == reTypePass.text.toString()){

                val id = database.push().key.toString()

                user = User(id, username.text.toString(), password.text.toString().md5())

                database.child((count + 1).toString()).setValue(user)
                toast("Registrasi berhasil").show()
            }
        }
    }

    private fun checkUsername(username: String): Boolean{
        var peopleUsername: String = ""
        var condition: Boolean = false
        for(i in 1 until count){
            database.child(i.toString())
            database.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    peopleUsername = p0.child("username").value.toString()
                    Log.d("username", peopleUsername)
                }

            })
            if(username == peopleUsername){
                condition = true
                break
            }
        }
        if(condition){
            return true
        }

        return false
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
