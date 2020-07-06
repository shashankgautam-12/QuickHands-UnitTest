package com.quickhandslogistics.models.schedule

import android.util.Log
import com.quickhandslogistics.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.contracts.schedule.ScheduleContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.schedule.ScheduleListAPIResponse
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

class ScheduleModel(private val sharedPref: SharedPref) : ScheduleContract.Model {

    override fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: ScheduleContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        val date = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        val dateShiftDetail = "$date - ${ScheduleUtils.getShiftDetailString(leadProfile)}"
        onFinishedListener.onSuccessGetHeaderInfo(dateShiftDetail)
    }

    override fun fetchSchedulesByDate(selectedDate: Date, pageIndex: Int, onFinishedListener: ScheduleContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getSchedulesList(getAuthToken(), dateString, pageIndex, AppConstant.API_PAGE_SIZE).enqueue(object : Callback<ScheduleListAPIResponse> {
            override fun onResponse(call: Call<ScheduleListAPIResponse>, response: Response<ScheduleListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(selectedDate, response.body()!!, pageIndex)
                }
            }

            override fun onFailure(call: Call<ScheduleListAPIResponse>, t: Throwable) {
                Log.e(ScheduleModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}