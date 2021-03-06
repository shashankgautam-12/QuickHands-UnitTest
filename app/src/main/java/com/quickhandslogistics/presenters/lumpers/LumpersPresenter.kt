package com.quickhandslogistics.presenters.lumpers

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.lumpers.LumpersContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.models.lumpers.LumpersModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class LumpersPresenter(private var lumpersView: LumpersContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    LumpersContract.Presenter, LumpersContract.Model.OnFinishedListener {

    private val lumpersModel = LumpersModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        lumpersView = null
    }

    override fun fetchLumpersList() {
        lumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumpersModel.fetchHeaderInfo(this)
        lumpersModel.fetchLumpersList(this)
    }

    override fun sendCustomerContactMessage(id: String, message: String) {
        lumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumpersModel.sendCustomerContactMessage(id, message, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        lumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            lumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        lumpersView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            lumpersView?.showLoginScreen()
        }
    }

    override fun onSuccessMessageSend(baseResponse: BaseResponse?) {
        lumpersView?.hideProgressDialog()
        baseResponse?.message?.let {
            lumpersView?.showSuccessMessageSend(it)
        }
    }

    override fun onSuccess(response: LumperListAPIResponse) {
        lumpersView?.hideProgressDialog()

        val allLumpersList = ArrayList<EmployeeData>()
        allLumpersList.addAll(response.data?.permanentLumpersList!!)
        allLumpersList.addAll(response.data?.temporaryLumpers!!)

        lumpersView?.showLumpersData(allLumpersList)
    }

    override fun onSuccessGetHeaderInfo(dateString: String) {
        lumpersView?.showDateString(dateString)
    }
}