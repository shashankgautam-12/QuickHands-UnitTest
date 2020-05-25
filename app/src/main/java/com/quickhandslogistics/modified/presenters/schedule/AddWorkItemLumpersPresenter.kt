package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.modified.data.common.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.models.schedule.AddWorkItemLumpersModel
import com.quickhandslogistics.utils.SharedPref

class AddWorkItemLumpersPresenter(
    private var addWorkItemLumpersView: AddWorkItemLumpersContract.View?, private val resources: Resources, sharedPref: SharedPref
) : AddWorkItemLumpersContract.Presenter, AddWorkItemLumpersContract.Model.OnFinishedListener {

    private val addWorkItemLumpersModel = AddWorkItemLumpersModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        addWorkItemLumpersView = null
    }

    override fun fetchLumpersList() {
        addWorkItemLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        addWorkItemLumpersModel.fetchLumpersList(this)
    }

    override fun initiateAssigningLumpers(selectedLumperIdsList: ArrayList<String>, workItemId: String, workItemType: String) {
        addWorkItemLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        addWorkItemLumpersModel.assignLumpersList(workItemId, workItemType, selectedLumperIdsList, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        addWorkItemLumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            addWorkItemLumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            addWorkItemLumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchLumpers(response: AllLumpersResponse) {
        addWorkItemLumpersView?.hideProgressDialog()

        val allLumpersList = ArrayList<EmployeeData>()
        allLumpersList.addAll(response.data?.permanentLumpersList!!)
        allLumpersList.addAll(response.data?.temporaryLumpers!!)

        addWorkItemLumpersView?.showLumpersData(allLumpersList)
    }

    override fun onSuccessAssignLumpers() {
        addWorkItemLumpersView?.hideProgressDialog()
        addWorkItemLumpersView?.lumperAssignmentFinished()
    }
}