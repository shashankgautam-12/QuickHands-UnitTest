package com.quickhandslogistics.adapters.scheduleTime

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.item_request_lumper_info.view.*


class RequestLumperInfoAdaptor(private val resources: Resources, val lumpersAllocated: List<String>) :
    RecyclerView.Adapter<RequestLumperInfoAdaptor.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_request_lumper_info, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return lumpersAllocated.size
    }

    private fun getItem(position: Int): String {
        return lumpersAllocated?.get(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {

        private val textViewLumperInfo: TextView = view.textViewLumperInfo

        fun bind(lumpersAllocated: String) {
            val countNumber =adapterPosition+1
            textViewLumperInfo.text= "$countNumber $lumpersAllocated "
        }

    }


}