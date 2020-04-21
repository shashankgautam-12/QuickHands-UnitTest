package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.models.schedule.AddWorkItemLumpersModel
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.utils.StringUtils

class AddWorkItemLumpersPresenter(
    private var addWorkItemLumpersView: AddWorkItemLumpersContract.View?,
    private val resources: Resources,
    sharedPref: SharedPref
) : AddWorkItemLumpersContract.Presenter, AddWorkItemLumpersContract.Model.OnFinishedListener {

    private val addWorkItemLumpersModel = AddWorkItemLumpersModel(sharedPref)

    override fun fetchLumpersList() {
        addWorkItemLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        addWorkItemLumpersModel.fetchLumpersList(this)
    }

    override fun onDestroy() {
        addWorkItemLumpersView = null
    }

    override fun initiateAssigningLumpers(
        selectedLumperIdsList: ArrayList<String>,
        workItemId: String,
        workItemType: String
    ) {
        addWorkItemLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        addWorkItemLumpersModel.assignLumpersList(workItemId, workItemType, selectedLumperIdsList, this)
    }

    override fun onFailure(message: String) {
        addWorkItemLumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            addWorkItemLumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            addWorkItemLumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchLumpers(allLumpersResponse: AllLumpersResponse) {
        addWorkItemLumpersView?.hideProgressDialog()
        allLumpersResponse.data.sortWith(Comparator { lumper1, lumper2 ->
            if (!StringUtils.isNullOrEmpty(lumper1.firstName) && !StringUtils.isNullOrEmpty(lumper2.firstName)
            ) {
                lumper1.firstName?.toLowerCase()!!.compareTo(lumper2.firstName?.toLowerCase()!!)
            } else {
                0
            }
        })
        addWorkItemLumpersView?.showLumpersData(allLumpersResponse.data)
    }

    override fun onSuccessAssignLumpers() {
        addWorkItemLumpersView?.hideProgressDialog()
        addWorkItemLumpersView?.lumperAssignmentFinished()
    }
}