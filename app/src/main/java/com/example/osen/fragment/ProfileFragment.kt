package com.example.osen.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide

import com.example.osen.R
import com.example.osen.activity.ChangePassword
import com.example.osen.activity.ExportImport
import com.example.osen.activity.Login
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    lateinit var auth: FirebaseAuth

    lateinit var navigation: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigation = activity!!.findViewById(R.id.bottom_navigation)

        auth = FirebaseAuth.getInstance()

        email.text = auth.currentUser?.email

        linear2.setOnClickListener {
            startActivity<ChangePassword>()
        }

        linear3.setOnClickListener {
            startActivity<ExportImport>()
        }

        linear5.setOnClickListener {
            val dialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
            dialog.titleText = "Apakah Anda ingin keluar ?"
            dialog.setCancelable(false)
            dialog.showCancelButton(true)
            dialog.cancelText = "Batal"
            dialog.confirmText = "Keluar"
            dialog.setConfirmClickListener {
                dialog.dismissWithAnimation()
                auth.signOut()
                startActivity<Login>()
                activity!!.finish()
            }
            dialog.show()
        }
    }

}
