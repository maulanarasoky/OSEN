package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.osen.R
import com.example.osen.fragment.AddFragment
import com.example.osen.fragment.HomeFragment
import com.example.osen.fragment.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
                    loadProfileFragment(savedInstanceState)
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
}
