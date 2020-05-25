package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LeadProfileContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.models.LeadProfileModel
import com.quickhandslogistics.utils.SharedPref

class LeadProfilePresenter(private var leadProfileView: LeadProfileContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    LeadProfileContract.Presenter, LeadProfileContract.Model.OnFinishedListener {

    private val leadProfileModel = LeadProfileModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        leadProfileView = null
    }

    override fun loadLeadProfileData() {
        leadProfileView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        leadProfileModel.fetchLeadProfileDataAPI(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        leadProfileView?.hideProgressDialog()
        leadProfileModel.fetchLeadProfileDataLocal(this)
    }

    override fun onFetchLeadProfileSuccess(response: LeadProfileAPIResponse) {
        leadProfileView?.hideProgressDialog()
        response.data?.let {
            leadProfileModel.processLeadProfileData(it, this)
        }
    }

    override fun onLoadLeadProfile(employeeData: LeadProfileData) {
        leadProfileView?.loadLeadProfile(employeeData)
    }
}