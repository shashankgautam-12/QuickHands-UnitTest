package com.quickhandslogistics.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.contracts.schedule.ScheduleContract
import com.quickhandslogistics.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ScheduleTimeModel(private val sharedPref: SharedPref) : ScheduleTimeContract.Model {

    override fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        val date = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        val dateShiftDetail = "$date  ${ScheduleUtils.getShiftDetailString(leadProfile)}"
        onFinishedListener.onSuccessGetHeaderInfo(dateShiftDetail)
    }

    override fun fetchSchedulesTimeByDate(selectedDate: Date, onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getScheduleTimeList(getAuthToken(), dateString).enqueue(object : Callback<GetScheduleTimeAPIResponse> {
            override fun onResponse(call: Call<GetScheduleTimeAPIResponse>, response: Response<GetScheduleTimeAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(selectedDate, response.body()!!)
                }
            }

            override fun onFailure(call: Call<GetScheduleTimeAPIResponse>, t: Throwable) {
                Log.e(ScheduleTimeModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}