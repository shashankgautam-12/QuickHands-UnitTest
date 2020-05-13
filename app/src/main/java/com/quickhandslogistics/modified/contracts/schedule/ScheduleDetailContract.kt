package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import java.util.*

class ScheduleDetailContract {

    interface Model {
        fun fetchScheduleDetail(scheduleIdentityId: String, selectedDate: Date, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(scheduleDetailAPIResponse: ScheduleDetailAPIResponse)
        }
    }

    interface View : BaseContract.View {
        fun showScheduleData(scheduleDetail: ScheduleDetail)
        fun showAPIErrorMessage(message: String)

        interface OnAdapterItemClickListener {
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
            fun onWorkItemClick(workItemDetail: WorkItemDetail, workItemTypeDisplayName: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getScheduleDetail(scheduleIdentityId: String, selectedDate: Date)
    }
}