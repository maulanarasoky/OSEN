package com.example.osen.fragment


import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.osen.R
import com.example.osen.adapter.ClassList
import com.example.osen.database.database
import com.example.osen.model.Classroom
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.support.v4.find

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private var list: MutableList<Classroom> = mutableListOf()
    private lateinit var adapter: ClassList
    private lateinit var classList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        classList = find(R.id.recyclerView)

        adapter = ClassList(list)

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
        classList.layoutManager = layoutManager
        classList.adapter = adapter

        showClass()
    }

    override fun onResume() {
        super.onResume()
        showClass()
    }

    private fun showClass(){
        list.clear()
        context?.database?.use {
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs("(TEACHER_ID = {teacher_id})", "teacher_id" to 1)
            val favorite = result.parseList(classParser<Classroom>())
            if (favorite.isNotEmpty()){
                list.addAll(favorite)
            }else{
                textNoData.visibility = View.VISIBLE
            }
            adapter.notifyDataSetChanged()
        }
    }

}
