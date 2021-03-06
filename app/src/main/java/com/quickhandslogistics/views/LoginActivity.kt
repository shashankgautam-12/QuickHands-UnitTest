package com.quickhandslogistics.views

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.LoginContract
import com.quickhandslogistics.presenters.LoginPresenter
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), LoginContract.View, View.OnClickListener {

    private lateinit var loginPresenter: LoginPresenter
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextPassword.transformationMethod = PasswordTransformationMethod()
        textViewForgotPassword.setOnClickListener(this)
        buttonLogin.setOnClickListener(this)
        loginPresenter = LoginPresenter(this, resources, sharedPref)
        loginPresenter.loadEmployeeId()
        snackBar = SnackBarFactory.createShortSnackBar(activity, mainConstraintLayout, getString(R.string.press_back_again_to_exit), isShow = false)
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.onDestroy()
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                textViewForgotPassword.id -> {
                    startIntent(ForgotPasswordActivity::class.java, isFinish = false)
                }
                buttonLogin.id -> {
                    AppUtils.hideSoftKeyboard(activity)

                    val employeeLoginId = editTextEmployeeId.text.toString().trim()
                    val password = editTextPassword.text.toString().trim()

                    loginPresenter.validateLoginDetails(employeeLoginId, password)
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun loadEmployeeId(employeeId: String) {
        editTextEmployeeId.setText(employeeId)
        editTextEmployeeId.setSelection(employeeId.length)
        LanguageManager.setLanguage(this, sharedPref.getString(AppConstant.PREFERENCE_LANGUAGE, AppConstant.LANGUAGE_ENGLISH_CODE))
    }

    override fun showEmptyEmployeeIdError() {
        editTextEmployeeId.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, resources.getString(R.string.empty_employee_id_message))
    }

    override fun showEmptyPasswordError() {
        editTextPassword.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, resources.getString(R.string.empty_password_message))
    }

    override fun showInvalidPasswordError() {
        editTextPassword.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, resources.getString(R.string.password_short_length_message))
    }

    override fun showAPIErrorMessage(message: String) {
        editTextEmployeeId.requestFocus()
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showNextScreen() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }
        LanguageManager.setLanguage(this, sharedPref.getString(AppConstant.PREFERENCE_LANGUAGE, AppConstant.LANGUAGE_ENGLISH_CODE))

        startIntent(DashBoardActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onBackPressed() {
        snackBar?.also { snackBar ->
            if (snackBar.isShown) super.onBackPressed() else snackBar.show()
        } ?: run {
            super.onBackPressed()
        }
    }
}