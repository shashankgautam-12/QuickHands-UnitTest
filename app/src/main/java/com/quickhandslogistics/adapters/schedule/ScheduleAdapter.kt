package com.quickhandslogistics.adapters.schedule

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.common.LumperImagesAdapter
import com.quickhandslogistics.contracts.common.LumperImagesContract
import com.quickhandslogistics.contracts.schedule.ScheduleContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetailData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.EMPLOYEE_DEPARTMENT_INBOUND
import com.quickhandslogistics.utils.AppConstant.Companion.EMPLOYEE_DEPARTMENT_OUTBOUND
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.sharedPref
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.UIUtils
import kotlinx.android.synthetic.main.item_schedule.view.*
import java.util.*
import kotlin.collections.ArrayList

class ScheduleAdapter(private val resources: Resources, var adapterItemClickListener: ScheduleContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private var workItemsList: ArrayList<ScheduleDetailData> = ArrayList()
    private var selectedDate: Date = Date()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_schedule, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    fun getItem(position: Int): ScheduleDetailData {
        return workItemsList[position]
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, LumperImagesContract.OnItemClickListener {

        private val textViewBuildingName: TextView = itemView.textViewBuildingName
        private val textViewStatus: TextView = itemView.textViewStatus
        private val textViewScheduleType: TextView = itemView.textViewScheduleType
        private val textViewWorkItemsCount: TextView = itemView.textViewWorkItemsCount
        private val textViewScheduleTypeLiveLoad: TextView = itemView.textViewScheduleTypeLiveLoad
        private val textViewScheduleTypeDrops: TextView = itemView.textViewScheduleTypeDrops
        private val textViewWorkItemsLeadName: TextView = itemView.textViewWorkItemsLeadName
        private val textViewScheduleTypeStartTime: TextView = itemView.textViewScheduleTypeStartTime
        private val textViewScheduleTypeLiveLoadStartTime: TextView = itemView.textViewScheduleTypeLiveLoadStartTime
        private val textViewScheduleTypeDropsStartTime: TextView = itemView.textViewScheduleTypeDropsStartTime
        private val textViewScheduleTypeUnfinished: TextView = itemView.textViewScheduleTypeUnfinished
        private val textViewScheduleTypeUnfinishedStartTime: TextView = itemView.textViewScheduleTypeUnfinishedStartTime
        private val recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList
        private val relativeLayoutSide: RelativeLayout = itemView.relativeLayoutSide

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind(scheduleDetail: ScheduleDetailData) {
            val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            textViewBuildingName.text = UIUtils.getSpannableText(resources.getString(R.string.department_full),"${scheduleDetail.scheduleDepartment.toLowerCase().capitalize()}s")
            if (!DateUtils.isCurrentDate(selectedDate.time) && !DateUtils.isFutureDate(selectedDate.time)) {
                if (checkAllContainerComplete(scheduleDetail)) {
                    textViewStatus.text = resources.getString(R.string.completed)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                    relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_completed)
                } else {
                    textViewStatus.text = resources.getString(R.string.pending)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                    relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_on_hold)
                }

            } else {
                textViewStatus.text = resources.getString(R.string.view_details)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
            }

            textViewScheduleType.text = String.format(resources.getString(R.string.out_bound_s),scheduleDetail.outbounds?.size.toString())
            textViewScheduleTypeLiveLoad.text = String.format(resources.getString(R.string.live_load_s),scheduleDetail.liveLoads?.size.toString())
            textViewScheduleTypeDrops.text = String.format(resources.getString(R.string.drops_s),scheduleDetail.drops?.size.toString())
            textViewScheduleTypeUnfinished.text = String.format(resources.getString(R.string.unfinished_drop),0)
            val totalContainer=(scheduleDetail.outbounds?.size!!) + (scheduleDetail.liveLoads?.size!!)+(scheduleDetail.drops?.size!!)
            textViewWorkItemsCount.text = String.format(resources.getString(R.string.total_containers_s),totalContainer)
            leadProfile?.buildingDetailData?.get(0)?.leads?.let {
                val leadName= getDepartmentlead(it, scheduleDetail.scheduleDepartment)
                textViewWorkItemsLeadName.text = String.format(resources.getString(R.string.lead_name),leadName)
            }

            if (scheduleDetail.outbounds!!.size>0 && !scheduleDetail.outbounds!![0].startTime.isNullOrEmpty())
                textViewScheduleTypeStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleDetail.outbounds!![0].startTime)!!.toLong())
            if (scheduleDetail.liveLoads!!.size>0 && !scheduleDetail.liveLoads!![0].startTime.isNullOrEmpty())
                textViewScheduleTypeLiveLoadStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleDetail.liveLoads!![0].startTime)!!.toLong())
            if (scheduleDetail.drops!!.size>0 && !scheduleDetail.drops!![0].startTime.isNullOrEmpty())
                textViewScheduleTypeDropsStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleDetail.drops!![0].startTime)!!.toLong())
//            if (scheduleDetail.drops!!.size>0 && !scheduleDetail.drops!![0].startTime.isNullOrEmpty())
//            textViewScheduleTypeUnfinishedStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleDetail.drops!![0].startTime)!!.toLong())
//            ScheduleUtils.changeStatusUIByValue(resources, VIEW_DETAILS, textViewStatus, relativeLayoutSide)

            val assignedLumperList=ScheduleUtils.getAssignedLumperList(scheduleDetail) as ArrayList<EmployeeData>
            recyclerViewLumpersImagesList.apply {
                adapter = LumperImagesAdapter(assignedLumperList , sharedPref,this@ViewHolder)
            }

            if (scheduleDetail.scheduleDepartment==(EMPLOYEE_DEPARTMENT_INBOUND)){
                textViewScheduleType.visibility=View.GONE
                textViewScheduleTypeStartTime.visibility=View.GONE
            }else if (scheduleDetail.scheduleDepartment == EMPLOYEE_DEPARTMENT_OUTBOUND){
                textViewScheduleTypeLiveLoad.visibility=View.GONE
                textViewScheduleTypeDrops.visibility=View.GONE
                textViewScheduleTypeLiveLoadStartTime.visibility=View.GONE
                textViewScheduleTypeDropsStartTime.visibility=View.GONE
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onScheduleItemClick(getItem(adapterPosition))
                }
            }
        }

        override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
            adapterItemClickListener.onLumperImagesClick(lumpersList)
        }

        private fun getDepartmentlead(leads: ArrayList<EmployeeData>, scheduleDepartment: String): String? {
            var leadName=""
            leads.forEach {
                if (it.department.equals(scheduleDepartment, ignoreCase = true))
                    leadName= it.fullName!!
            }
        return leadName
        }
    }

    private fun checkAllContainerComplete(scheduleDetail: ScheduleDetailData): Boolean {
        var allWorkItem =ArrayList<WorkItemDetail>()
        var allContainerDone= true
        scheduleDetail.drops?.let { allWorkItem.addAll(it) }
        scheduleDetail.liveLoads?.let { allWorkItem.addAll(it) }
        scheduleDetail.outbounds?.let { allWorkItem.addAll(it) }
        allWorkItem.forEach {
            if (it.isCompleted ==false)
                allContainerDone=false

        }
        return allContainerDone
    }


    fun updateList(
        scheduledData: ArrayList<ScheduleDetailData>,
        currentPageIndex: Int,
        selectedDate: Date
    ) {
        if (currentPageIndex == 1) {
            this.workItemsList.clear()
        }
        this.workItemsList.addAll(scheduledData)
        this.selectedDate= selectedDate
        notifyDataSetChanged()
    }
}