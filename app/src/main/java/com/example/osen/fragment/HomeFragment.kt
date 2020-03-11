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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
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

    lateinit var auth: FirebaseAuth

    var id = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(activity)

        if(auth.currentUser != null){
            id = auth.currentUser?.uid.toString()
        }else if(account != null){
            id = account.id.toString()
        }

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
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs("(TEACHER_ID = {teacher_id})", "teacher_id" to id)
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
