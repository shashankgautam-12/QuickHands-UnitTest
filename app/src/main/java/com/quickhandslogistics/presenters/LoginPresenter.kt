package com.quickhandslogistics.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.LoginContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.data.login.LoginResponse
import com.quickhandslogistics.models.LoginModel
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.utils.ValidationUtils

class LoginPresenter(private var loginView: LoginContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    LoginContract.Presenter, LoginContract.Model.OnFinishedListener {

    private val loginModel = LoginModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        loginView = null
    }

    override fun loadEmployeeId() {
        loginModel.fetchEmployeeId(this)
    }

    override fun validateLoginDetails(employeeLoginId: String, password: String) {
        when (ValidationUtils.getInstance().loginValidation(employeeLoginId, password)) {
            ValidationUtils.EMPTY_USERID -> loginView?.showEmptyEmployeeIdError()
            ValidationUtils.EMPTY_PASSWORD -> loginView?.showEmptyPasswordError()
            ValidationUtils.INVALID_PASSWORD -> loginView?.showInvalidPasswordError()
            ValidationUtils.VALID_PASSWORD -> {
                loginView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
                loginModel.fetchRegistrationToken(employeeLoginId, password, this)
            }
        }
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        loginView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            loginView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            loginView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        onFailure(errorCode.message)
    }

    override fun onLeadProfileSuccess(leadProfileAPIResponse: LeadProfileAPIResponse) {
        loginView?.hideProgressDialog()
        leadProfileAPIResponse.data?.let {
            loginModel.processLeadProfileData(it, this)
        }
    }

    override fun onLoadEmployeeId(employeeId: String) {
        loginView?.loadEmployeeId(employeeId)
    }

    override fun onLoginSuccess(loginResponse: LoginResponse) {
        loginModel.processLoginData(loginResponse.data)
        loginModel.fetchLeadProfileInfo(this)
    }

    override fun onRegistrationTakenSaved(employeeLoginId: String, password: String) {
        loginModel.loginUsingEmployeeDetails(employeeLoginId, password, this)
    }


    override fun showNextScreen() {
        loginView?.showNextScreen()
    }
}