package com.quickhandslogistics.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.LeadProfileContract
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.presenters.LeadProfilePresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.common.FullScreenImageActivity
import kotlinx.android.synthetic.main.content_lead_profile.*


class LeadProfileActivity : BaseActivity(), LeadProfileContract.View, View.OnClickListener {

    private var employeeData: LeadProfileData? = null

    private lateinit var leadProfilePresenter: LeadProfilePresenter

    companion object {
        const val LEAD_DATA = "LEAD_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_profile)
        setupToolbar(title = getString(R.string.my_profile))

//        layoutDMEmail.setOnClickListener(this)
        //  circleImageViewProfile.setOnClickListener(this)

        leadProfilePresenter = LeadProfilePresenter(this, resources, sharedPref)
        initializeUI()

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(LEAD_DATA)) {
                employeeData = savedInstanceState.getParcelable(LEAD_DATA)!!
                loadLeadProfile(employeeData!!)
            }
        } ?: run {
            if (!ConnectionDetector.isNetworkConnected(this)) {
                ConnectionDetector.createSnackBar(this)
                return
            }
            leadProfilePresenter.loadLeadProfileData()
        }
    }

    private fun initializeUI() {
        textViewScheduleNote.text = UIUtils.getSpannedText(getString(R.string.schedule_note_lead))
    }

    override fun onDestroy() {
        super.onDestroy()
        leadProfilePresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (employeeData != null)
            outState.putParcelable(LEAD_DATA, employeeData)
        super.onSaveInstanceState(outState)
    }

    private fun showEmailDialog() {
        val buildingDetailData= ScheduleUtils.getBuildingDetailData(employeeData?.buildingDetailData)
        if (buildingDetailData != null) {
            val name =
                UIUtils.getEmployeeFullName(buildingDetailData?.districtManager)
            buildingDetailData?.districtManager?.email?.let { email ->
                CustomProgressBar.getInstance().showWarningDialog(String.format(
                    getString(R.string.email_lumper_alert_message),
                    name
                ),
                    activity, object : CustomDialogWarningListener {
                        override fun onConfirmClick() {
                            val emailIntent =
                                Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                            startActivity(Intent.createChooser(emailIntent, "Send email..."))
                        }

                        override fun onCancelClick() {
                        }
                    })
            }
        }
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                circleImageViewProfile.id -> {
                    if (!ConnectionDetector.isNetworkConnected(this)) {
                        ConnectionDetector.createSnackBar(this)
                        return
                    }

                    if (!employeeData?.profileImageUrl.isNullOrEmpty()) {
                        val bundle = Bundle()
                        bundle.putString(FullScreenImageActivity.ARG_IMAGE_URL, employeeData?.profileImageUrl)
                        startZoomIntent(FullScreenImageActivity::class.java, bundle, circleImageViewProfile)
                    }
                }
//                layoutDMEmail.id -> {
//                    showEmailDialog()
//                }

            }
        }
    }

    /** Presenter Listeners */
    override fun loadLeadProfile(employeeData: LeadProfileData) {
        this.employeeData = employeeData
        UIUtils.showEmployeeProfileImage(activity, employeeData, circleImageViewProfile)
        textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
        val buildingData = ScheduleUtils.getBuildingDetailData(employeeData?.buildingDetailData)
        if (!buildingData?.buildingName.isNullOrEmpty() && !employeeData.role.isNullOrEmpty()) {
            textViewCompanyName.text =
                employeeData.role!!.capitalize() + " at " + buildingData?.buildingName!!.capitalize()
        } else textViewCompanyName.visibility = View.GONE

        textViewEmailAddress.text = if (!employeeData.email.isNullOrEmpty()) employeeData.email else "---"
        val phoneNumber =if (!employeeData.phone.isNullOrEmpty())UIUtils.formetMobileNumber(employeeData.phone!!)else "---"
        textViewPhoneNumber.text = phoneNumber

        textViewEmployeeId.text = if (!employeeData.employeeId.isNullOrEmpty()) employeeData.employeeId else "---"
        textViewRole.text = if (!employeeData.role.isNullOrEmpty()) employeeData.role!!.capitalize() else "---"
        textViewDepartment.text = if (!employeeData.department.isNullOrEmpty()) UIUtils.getDisplayEmployeeDepartment(employeeData) else "---"
        textViewTitle.text = if (!employeeData.title.isNullOrEmpty()) employeeData.title!!.capitalize() else "---"

        textViewShiftHours.text = if (!employeeData.shiftHours.isNullOrEmpty()) employeeData.shiftHours else "---"
        textViewShift.text = if (!employeeData.shift.isNullOrEmpty()) employeeData.shift?.capitalize() else "---"
        textViewScheduleNote.text = if (!employeeData.scheduleNotes.isNullOrEmpty()) UIUtils.getSpannedText(getString(R.string.schedule_note) + employeeData.scheduleNotes) else UIUtils.getSpannedText(getString(R.string.schedule_note_lead))
        textViewAvailability.text = if (employeeData.fullTime!!) getString(R.string.full_time_ud) else getString(R.string.part_time_ud)

        textViewBuildingName.text = if (!buildingData?.buildingName.isNullOrEmpty()) buildingData?.buildingName!!.capitalize() else "---"
        textViewCustomerName.text = if (!buildingData?.customerDetail?.name.isNullOrEmpty()) buildingData?.customerDetail?.name!!.capitalize() else "---"
    }


}