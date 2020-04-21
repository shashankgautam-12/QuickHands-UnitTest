package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class ScheduleModel(private val sharedPref: SharedPref) : ScheduleContract.Model {

    override fun fetchSchedulesByDate(
        selectedDate: Date,
        onFinishedListener: ScheduleContract.Model.OnFinishedListener
    ) {
        val dateString =
            DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)
        val buildingId = sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID)

        DataManager.getSchedulesList(
            dateString,
            buildingId,
            object : ResponseListener<ScheduleListAPIResponse> {
                override fun onSuccess(response: ScheduleListAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccess(selectedDate, response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(ScheduleModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}