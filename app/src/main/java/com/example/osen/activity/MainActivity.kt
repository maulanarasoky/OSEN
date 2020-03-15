package com.example.osen.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.osen.R
import com.example.osen.fragment.AddFragment
import com.example.osen.fragment.HomeFragment
import com.example.osen.fragment.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 100
        const val RESULT_CODE = 222
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth.currentUser

        if (user == null) {
            startActivity<Login>()
            this.finish()
        } else {
            bottom_navigation.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        loadHomeFragment(savedInstanceState)
                    }
                    R.id.add -> {
                        loadAddFragment(savedInstanceState)
                    }
                    R.id.profile -> {
                        loadProfileFragment(savedInstanceState)
                    }
                }
                true
            }
            bottom_navigation.selectedItemId = R.id.home
        }
    }

    private fun loadHomeFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_layout, HomeFragment(), HomeFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun loadAddFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_layout, AddFragment(), AddFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun loadProfileFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.container_layout,
                    ProfileFragment(),
                    ProfileFragment::class.java.simpleName
                )
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("resultCode", resultCode.toString())
        if (requestCode == REQUEST_CODE) {
            bottom_navigation.selectedItemId = R.id.home
        }
    }
}
