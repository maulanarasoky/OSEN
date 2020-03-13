package com.example.osen.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.osen.R
import kotlinx.android.synthetic.main.activity_export_import.*
import org.jetbrains.anko.startActivity

class ExportImport : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_import)

        linear1.setOnClickListener {
            startActivity<Export>()
        }

        linear2.setOnClickListener {

        }
    }
}
