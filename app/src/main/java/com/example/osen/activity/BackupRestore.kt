package com.example.osen.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.osen.R
import kotlinx.android.synthetic.main.activity_backup_restore.*
import org.jetbrains.anko.startActivity

class BackupRestore : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_restore)

        linear1.setOnClickListener {
            startActivity<Backup>()
        }
        linear2.setOnClickListener {
            startActivity<Restore>()
        }
    }
}
