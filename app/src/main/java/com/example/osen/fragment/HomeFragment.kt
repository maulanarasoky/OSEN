package com.example.osen.fragment


import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.osen.R
import com.example.osen.activity.MainActivity
import com.example.osen.adapter.ClassList
import com.example.osen.database.database
import com.example.osen.interfaces.MyAsyncCallback
import com.example.osen.model.Classroom
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.support.v4.find
import java.lang.ref.WeakReference

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), MyAsyncCallback {

    private var list: MutableList<Classroom> = mutableListOf()
    private lateinit var adapter: ClassList
    private lateinit var classList: RecyclerView

    lateinit var auth: FirebaseAuth

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

        classList = find(R.id.recyclerView)

        val demoAsync = DemoAsync(this)
        demoAsync.execute()
    }

    fun showClass() {
        list.clear()
        context?.database?.use {
            val result = select(Classroom.TABLE_CLASSROOM).whereArgs(
                "(TEACHER_ID = {teacher_id})",
                "teacher_id" to auth.currentUser?.uid.toString()
            )
            val favorite = result.parseList(classParser<Classroom>())
            if (favorite.isNotEmpty()) {
                list.addAll(favorite)
            } else {
                textNoData.visibility = View.VISIBLE
            }
        }
    }

    inner class DemoAsync(listener: MyAsyncCallback): AsyncTask<Void, Unit, Unit>(){

        private val myListener: WeakReference<MyAsyncCallback> = WeakReference(listener)

        override fun onPreExecute() {
            super.onPreExecute()
            val myListener = myListener.get()
            myListener?.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?) {
            showClass()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val myListener = myListener.get()
            myListener?.onPostExecute()
        }

    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onPostExecute() {
        progressBar.visibility = View.GONE

        adapter = ClassList(activity!!, list)

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
        classList.layoutManager = layoutManager
        classList.adapter = adapter
    }

}
