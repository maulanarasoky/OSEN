package com.example.osen.fragment


import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import com.example.osen.R
import com.example.osen.activity.AddClassroom
import com.example.osen.activity.AddData
import kotlinx.android.synthetic.main.fragment_add.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * A simple [Fragment] subclass.
 */
class AddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addClass.setOnClickListener {
            startActivity<AddClassroom>()
        }

        addData.setOnClickListener{
            startActivity<AddData>()
        }
    }

}
