package com.example.osen.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.osen.R
import com.example.osen.fragment.AddFragment
import com.example.osen.fragment.HomeFragment
import com.example.osen.fragment.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        const val REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.home ->{
                    loadHomeFragment(savedInstanceState)
                }
                R.id.add -> {
                    loadAddFragment(savedInstanceState)
                }
                R.id.profile ->{
//                    loadProfileFragment(savedInstanceState)
                    val intent = Intent(this, Login::class.java)
                    startActivityForResult(intent, REQUEST_CODE)
                }
            }
            true
        }
        bottom_navigation.selectedItemId = R.id.home
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
                .replace(R.id.container_layout, ProfileFragment(), ProfileFragment::class.java.simpleName)
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE){
            bottom_navigation.selectedItemId = R.id.home
        }
    }
}
