package com.quickhandslogistics.models.reports

import android.util.Log
import com.quickhandslogistics.contracts.reports.TimeClockReportContract
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.data.reports.ReportRequest
import com.quickhandslogistics.data.reports.ReportResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.FetchMimeType
import com.quickhandslogistics.utils.OnFetchCompleteListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TimeClockReportModel : TimeClockReportContract.Model {

    override fun fetchLumpersList(
        onFinishedListener: TimeClockReportContract.Model.OnFinishedListener,
        startdate: String,
        endDate: String
    ) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift()

        DataManager.getService().getAllLumpersSelectedDates(getAuthToken(), startdate,endDate ).enqueue(object : Callback<LumperListAPIResponse> {
            override fun onResponse(call: Call<LumperListAPIResponse>, response: Response<LumperListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LumperListAPIResponse>, t: Throwable) {
                Log.e(TimeClockReportModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun createTimeClockReport(startDate: Date, endDate: Date, reportType: String, lumperIdsList: ArrayList<String>, onFinishedListener: TimeClockReportContract.Model.OnFinishedListener) {
        val startDateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, startDate)
        val endDateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, endDate)
        val request = ReportRequest(lumperIdsList)

        DataManager.getService().createTimeClockReport(getAuthToken(), startDateString, endDateString, reportType, request).enqueue(object : Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessCreateReport(response.body()!!)
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e(TimeClockReportModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun getUrlMimeType(reportUrl: String, onFinishedListener: TimeClockReportContract.Model.OnFinishedListener) {
        FetchMimeType(reportUrl, object : OnFetchCompleteListener {
            override fun onFetchMimeType(mimeType: String) {
                onFinishedListener.onSuccessFetchMimeType(reportUrl, mimeType)
            }
        }).execute()
    }
}