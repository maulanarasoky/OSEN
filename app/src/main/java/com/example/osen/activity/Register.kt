package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.osen.R
import com.example.osen.model.data.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast

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
                if(p0.exists()){
                    count = p0.childrenCount.toInt()
                }
            }

        })

        btnRegister.setOnClickListener {
            if(password.text.toString() == reTypePass.text.toString()){
                user = User(shuffle(), username.text.toString(), password.text.toString())

                database.child((count + 1).toString()).setValue(user)
                toast("Registrasi berhasil").show()
            }
        }
    }

    private fun shuffle(): String{
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz1234567890"
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
