package com.quickhandslogistics.modified.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.modified.models.workSheet.WorkSheetItemDetailModel

class WorkSheetItemDetailPresenter(
    private var workSheetItemDetailView: WorkSheetItemDetailContract.View?,
    private val resources: Resources
) : WorkSheetItemDetailContract.Presenter,
    WorkSheetItemDetailContract.Model.OnFinishedListener {

    private val workSheetItemDetailModel = WorkSheetItemDetailModel()

    override fun onDestroy() {
        workSheetItemDetailView = null
    }

    override fun fetchWorkItemDetail(workItemId: String) {
        workSheetItemDetailView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        workSheetItemDetailModel.fetchWorkItemDetail(workItemId, this)
    }

    override fun changeWorkItemStatus(workItemId: String, status: String) {
        workSheetItemDetailView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        workSheetItemDetailModel.changeWorkItemStatus(workItemId, status, this)
    }

    override fun onFailure(message: String) {
        workSheetItemDetailView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            workSheetItemDetailView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            workSheetItemDetailView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: WorkItemDetailAPIResponse) {
        workSheetItemDetailView?.hideProgressDialog()
        response.data?.workItemDetail?.let { workItemDetail ->
            workSheetItemDetailView?.showWorkItemDetail(workItemDetail)
        }
    }

    override fun onSuccessChangeStatus(workItemId: String) {
        workSheetItemDetailModel.fetchWorkItemDetail(workItemId, this)
    }
}