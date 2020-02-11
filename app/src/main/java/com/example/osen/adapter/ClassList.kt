package com.example.osen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.osen.R
import com.example.osen.activity.ClassDetails
import com.example.osen.activity.EditData
import com.example.osen.model.Classroom
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.class_list.*
import org.jetbrains.anko.startActivity

class ClassList(private val items : List<Classroom>) : RecyclerView.Adapter<ClassList.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.class_list, parent, false)
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    class ViewHolder(override val containerView : View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindItem(items : Classroom){
            className.text = items.name

            Glide.with(itemView.context).load(containerView.resources.getDrawable(items.image!!.toInt())).apply(RequestOptions.overrideOf(500,500)).into(classImage)

            itemView.setOnClickListener {
                itemView.context.startActivity<ClassDetails>(
                    ClassDetails.data to items
                )
            }
        }
    }
}