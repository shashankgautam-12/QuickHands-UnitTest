package com.quickhandslogistics.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.view.LumperModel
import com.quickhandslogistics.view.activities.LumperJobHistoryActivity
import com.quickhandslogistics.view.activities.LumperSheetDetailActivity
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_sheet_layout.view.*

class LumperSheetAdapter(var items: ArrayList<LumperModel>, val context: Context, val statusItems: ArrayList<String>) : RecyclerView.Adapter<LumperSheetAdapter.LumperViewHolder>() {

    var faker = Faker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LumperViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_lumper_sheet_layout, parent, false)
        return LumperViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LumperViewHolder, position: Int) {
        holder?.lumperName?.text = items.get(position)?.name + " " + items.get(position)?.lastName
        holder?.lumperDate?.text = faker.date.backward(6).toString()
        holder?.lumperStatus.text = statusItems.get(position).toUpperCase()


        if(TextUtils.equals(statusItems.get(position), context.resources.getString(R.string.complete))){
            holder?.lumperStatus.setBackgroundResource(R.drawable.chip_complete)
        }else  holder?.lumperStatus.setBackgroundResource(R.drawable.chip_in_progress)

        holder.constraintRoot.setOnClickListener {
            context.startActivity(Intent(context, LumperSheetDetailActivity::class.java))
        }
    }

    fun filterLumperList(filteredName: ArrayList<LumperModel>) {
        this.items = filteredName
        notifyDataSetChanged()
    }

    class LumperViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperName = view.text_lumper_name
        var profilePic = view.image_lumper_logo
        var constraintRoot = view.constraint_root
        var lumperDate = view.text_date
        var lumperStatus = view.text_status
    }
}