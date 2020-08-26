package com.quickhandslogistics.adapters.customerSheet

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.views.customerSheet.CustomerSheetFragment
import kotlin.collections.ArrayList

class WorkItemAdapter(
   val resources: Resources,
    val customerSheetFragment: CustomerSheetFragment
) : RecyclerView.Adapter<WorkItemAdapter.CustomeViewHolder>() {
    private var onGoingWorkItems = ArrayList<ArrayList<WorkItemDetail>>()


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomeViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_workitem_type, viewGroup, false)
        return CustomeViewHolder(itemView)
    }

    private fun getItem(position: Int): ArrayList<WorkItemDetail> {
        return onGoingWorkItems[position]
    }

    override fun onBindViewHolder(viewHolder: CustomeViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return onGoingWorkItems.size
    }

    fun update(dropItem: ArrayList<ArrayList<WorkItemDetail>>) {
        onGoingWorkItems.clear()
        onGoingWorkItems.addAll(dropItem)
        notifyDataSetChanged()
    }

    inner class CustomeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var textViewStatusitme: TextView = itemView.findViewById(R.id.textViewStatusitme)
        var recycelrViewStatusType: RecyclerView = itemView.findViewById(R.id.recycelrViewStatusType)

        fun bind(workItemDetail: ArrayList<WorkItemDetail>) {
            if(adapterPosition.equals(0)){
                textViewStatusitme.text=String.format("Complete %s", workItemDetail.size )
                val completed=getCompletedItemArray(workItemDetail)
                if (completed.size>0)
                setAdapter(completed)

            }else if(adapterPosition.equals(1)){
                textViewStatusitme.text=String.format("Cancel %s", workItemDetail.size )
                val cancel= getCompletedItemArray(workItemDetail)
                if (cancel.size>0)
                setAdapter(cancel)

            }else if (adapterPosition.equals(2)){
                textViewStatusitme.text=String.format("Schedule %s", workItemDetail.size )
                var schedule=getCompletedItemArray(workItemDetail )
                if (schedule.size>0)
                setAdapter(schedule)


            }

        }

        private fun setAdapter(ongoing: ArrayList<ArrayList<WorkItemDetail>>) {
            val workItemAdapter= WorkTypeAdpter(resources, customerSheetFragment.context, ongoing)
            val layoutManager = LinearLayoutManager(customerSheetFragment.context, LinearLayoutManager.VERTICAL, false)
            recycelrViewStatusType.layoutManager=layoutManager
            recycelrViewStatusType.adapter=workItemAdapter
        }
    }

    fun getCompletedItemArray(workItemDetail: ArrayList<WorkItemDetail>):ArrayList<ArrayList<WorkItemDetail>>  {
        val outboundItme = ArrayList<WorkItemDetail>()
        val liveLodeItem = ArrayList<WorkItemDetail>()
        val dropItem = ArrayList<WorkItemDetail>()
        val itemType = ArrayList<ArrayList<WorkItemDetail>>()

        workItemDetail.forEach {
            when {
                it.workItemType.equals(AppConstant.WORKSHEET_WORK_ITEM_INBOUND) -> dropItem.add(it)
                it.workItemType.equals(AppConstant.WORKSHEET_WORK_ITEM_OUTBOUND) -> outboundItme.add(it)
                it.workItemType.equals(AppConstant.WORKSHEET_WORK_ITEM_LIVE) -> liveLodeItem.add(it)
            }
        }
        itemType.add(outboundItme)
        itemType.add(liveLodeItem)
        itemType.add(dropItem)

        return itemType

    }

}