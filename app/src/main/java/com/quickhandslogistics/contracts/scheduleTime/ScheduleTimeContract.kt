package com.quickhandslogistics.contracts.scheduleTime

import LeadWorkInfo
import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.schedule.GetPastFutureDateResponse
import com.quickhandslogistics.data.schedule.PastFutureDates
import com.quickhandslogistics.data.scheduleTime.leadinfo.GetLeadInfoResponse
import com.quickhandslogistics.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeNoteRequest
import java.util.*
import kotlin.collections.ArrayList

class ScheduleTimeContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchSchedulesTimeByDate(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchLeadScheduleByDate(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchPastFutureDate(onFinishedListener: OnFinishedListener)
        fun cancelScheduleLumpers(lumperId: ArrayList<String>, date: Date, cancelReason: String?, onFinishedListener: OnFinishedListener)
        fun editScheduleLumpers(
            lumperId: String,
            date: Date,
            timeMilsec: Long,
            request: ScheduleTimeNoteRequest,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(selectedDate: Date, scheduleTimeAPIResponse: GetScheduleTimeAPIResponse)
            fun onSuccessLeadInfo(getLeadInfoResponse: GetLeadInfoResponse)
            fun onSuccessGetHeaderInfo(dateString: String)
            fun onSuccessRequest(date: Date, cancelScheduleLumper: String)
            fun onSuccessPastFutureDate(response: GetPastFutureDateResponse?)
        }
    }

    interface View : BaseContract.View {
        fun showDateString(dateString: String)
        fun showLeadInfo(leadWorkInfo: LeadWorkInfo?)
        fun showAPIErrorMessage(message: String)
        fun showNotesData(notes: String?)
        fun showSuccessDialog(message:String, date: Date)
        fun showPastFutureDate(message: ArrayList<PastFutureDates>)
        fun showScheduleTimeData(
            selectedDate: Date,
            scheduleTimeDetailList: ArrayList<ScheduleTimeDetail>,
            tempLumperIds: ArrayList<String>,
            notes: String?
        )
        fun showLoginScreen()
        interface OnAdapterItemClickListener {
            fun onEditTimeClick(
                adapterPosition: Int,
                timeInMillis: Long,
                details: ScheduleTimeDetail
            )
            fun onScheduleNoteClick(
                adapterPosition: Int,
                notes: String?,
                item: ScheduleTimeDetail
            )
            fun onAddRemoveClick(
                adapterPosition: Int,
                details: ScheduleTimeDetail
            )
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getSchedulesTimeByDate(date: Date)
        fun cancelScheduleLumpers(lumperId: ArrayList<String>, date: Date, cancelReason: String?)
        fun editScheduleLumpers(
            lumperId: String,
            date: Date,
            timeMilsec: Long,
            request: ScheduleTimeNoteRequest
        )
    }
}