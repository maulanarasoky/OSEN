package com.example.osen.fragment


import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import com.example.osen.R

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


}
