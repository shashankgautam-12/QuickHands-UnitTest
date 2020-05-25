package com.quickhandslogistics.modified.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.modified.models.workSheet.WorkSheetModel
import com.quickhandslogistics.utils.SharedPref

class WorkSheetPresenter(private var workSheetView: WorkSheetContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    WorkSheetContract.Presenter, WorkSheetContract.Model.OnFinishedListener {

    private val workSheetModel = WorkSheetModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        workSheetView = null
    }

    override fun fetchWorkSheetList() {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        workSheetModel.fetchHeaderInfo(this)
        workSheetModel.fetchWorkSheetList(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        workSheetView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            workSheetView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            workSheetView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchWorkSheet(workSheetListAPIResponse: WorkSheetListAPIResponse) {
        workSheetView?.hideProgressDialog()
        workSheetListAPIResponse.data?.let { data ->

            // Sort all the work Items by their Start Time
            data.inProgress?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            data.onHold?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            data.scheduled?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            data.cancelled?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            data.completed?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })

            workSheetView?.showWorkSheets(data)
        }
    }

    override fun onSuccessGetHeaderInfo(companyName: String, date: String) {
        workSheetView?.showHeaderInfo(companyName, date)
    }
}