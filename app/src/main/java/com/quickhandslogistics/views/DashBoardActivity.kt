package com.quickhandslogistics.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.quickhandslogistics.BuildConfig
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.controls.NavDrawer
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.presenters.DashBoardPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.addContainer.AddContainerActivity
import com.quickhandslogistics.views.attendance.TimeClockAttendanceFragment
import com.quickhandslogistics.views.customerContact.CustomerContactFragment
import com.quickhandslogistics.views.customerSheet.CustomerSheetFragment
import com.quickhandslogistics.views.lumperSheet.LumperSheetFragment
import com.quickhandslogistics.views.lumpers.LumpersFragment
import com.quickhandslogistics.views.qhlContact.QhlContactFragment
import com.quickhandslogistics.views.reports.ReportsFragment
import com.quickhandslogistics.views.schedule.ScheduleFragment
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment
import com.quickhandslogistics.views.workSheet.AllWorkScheduleCancelActivity
import com.quickhandslogistics.views.workSheet.WorkSheetFragment
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.custome_dashbord_toolbar.*
import kotlinx.android.synthetic.main.include_main_nav_drawer.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*
import java.util.*

class DashBoardActivity : BaseActivity(), View.OnClickListener, DashBoardContract.View, DashBoardContract.View.OnFragmentInteractionListener {

    private var showTabName = ""
    private var scheduleTimeSelectedDate = ""
    private var selectedFragmentTitle: String = ""
    private var scheduleTimeNotes: String = ""
    private var isCancelAllScheduleVisible: Boolean = false
    private var isAddContainerVisible: Boolean = true
     var isShowLeavePopup: Boolean = false
     var isPerformLogout: Boolean = false
     var isPrivousTab: Boolean = false

    private lateinit var dashBoardPresenter: DashBoardPresenter

    private var navDrawer: NavDrawer? = null
    private var snackBar: Snackbar? = null

    companion object {
        const val ARG_SHOW_TAB_NAME = "ARG_SHOW_TAB_NAME"
        const val ARG_SCHEDULE_TIME_SELECTED_DATE = "ARG_SCHEDULE_TIME_SELECTED_DATE"
        const val TAB_NAME = "TAB_NAME"
        const val PRIVIOUS_TAB = "PRIVIOUS_TAB"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_dashboard)
        setupToolbar(showBackButton = false)

        intent.extras?.also { bundle ->
            showTabName = bundle.getString(ARG_SHOW_TAB_NAME, getString(R.string.today_s_work_sheet))
            isPrivousTab = bundle.getBoolean(PRIVIOUS_TAB, false)

            //if Tab is Schedule Lumper Time, then check for date
            if (showTabName == getString(R.string.scheduled_lumpers)) {
                scheduleTimeSelectedDate = bundle.getString(ARG_SCHEDULE_TIME_SELECTED_DATE, "")
            }


        } ?: run {
            showTabName = getString(R.string.today_s_work_sheet)
        }

        savedInstanceState?.let {
            if (savedInstanceState.containsKey(TAB_NAME)) {
                showTabName = savedInstanceState.getString(TAB_NAME, getString(R.string.today_s_work_sheet))
            }
        }

        setUpNavigationBar()
        headerLayout.setOnClickListener(this)

        dashBoardPresenter = DashBoardPresenter(this, resources, sharedPref)

        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }
        dashBoardPresenter.loadLeadProfileData()

        snackBar = SnackBarFactory.createShortSnackBar(activity, frameLayoutMain, getString(R.string.press_back_again_to_exit), isShow = false)
    }

    private fun replaceScheduleFragment() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            when (selectedFragmentTitle) {
                getString(R.string.today_s_work_sheet) -> {
                    menu.findItem(R.id.actionCancelAllWork).isVisible = isCancelAllScheduleVisible
                    menu.findItem(R.id.actionAddContainer).isVisible = isAddContainerVisible
                }
                getString(R.string.scheduled_lumpers) -> {
                    menu.findItem(R.id.actionNotes).isVisible = scheduleTimeNotes.isNotEmpty()
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionNotes -> {
                CustomProgressBar.getInstance().showInfoDialog(getString(R.string.note), scheduleTimeNotes, activity)
            }
            R.id.actionCancelAllWork -> {
                startIntent(AllWorkScheduleCancelActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
            }
            R.id.actionAddContainer -> {
                startIntent(AddContainerActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        dashBoardPresenter.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            navDrawer?.updateWorkSheetList()
        }
    }

    override fun onBackPressed() {
        snackBar?.also { snackBar ->
            if (snackBar.isShown) super.onBackPressed() else snackBar.show()
        } ?: run {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        var tabName = ""
        val fragment = supportFragmentManager.findFragmentById(R.id.frameLayoutMain)
        fragment?.let {
            when(fragment){
                is LumpersFragment ->
                    tabName = getString(R.string.lumper_contact)
                is WorkSheetFragment->
                    tabName = getString(R.string.today_s_work_sheet)
                is ScheduleTimeFragment->
                    tabName = getString(R.string.scheduled_lumpers)
                is ScheduleFragment->
                    tabName = getString(R.string.schedule)
                is LumperSheetFragment->
                    tabName = getString(R.string.l_sheet)
                is CustomerSheetFragment->
                    tabName = getString(R.string.customer_sheet)
                is ReportsFragment->
                    tabName = getString(R.string.reports)
                is TimeClockAttendanceFragment->
                    tabName = getString(R.string.time_clock)
                is CustomerContactFragment->
                    tabName = getString(R.string.customer_contect)
                is QhlContactFragment->
                    tabName = getString(R.string.qhl_contect)
            }
        }
        outState.putString(TAB_NAME, tabName)
        super.onSaveInstanceState(outState)
    }

    private fun setUpNavigationBar() {
        navDrawer = NavDrawer(this, toolbar, supportFragmentManager.beginTransaction(), this)
        navDrawer?.let {
            it.addItem(NavDrawer.AppNavDrawerItem(WorkSheetFragment(), R.drawable.menu_work_sheet_icon, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.today_s_work_sheet), showTabName)))
            val scheduleTimeFragment = ScheduleTimeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SCHEDULE_TIME_SELECTED_DATE, scheduleTimeSelectedDate)
                    putBoolean(PRIVIOUS_TAB, isPrivousTab)
                }
            }

            it.addItem(NavDrawer.AppNavDrawerItem(TimeClockAttendanceFragment(), R.drawable.menu_time_clock_icon, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.time_clock), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(CustomerSheetFragment(), R.drawable.menu_customer_sheet_icon, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.customer_sheet), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(LumperSheetFragment(), R.drawable.menu_lumper_sheet_icon, R.id.linearLayoutTopItems, isShowOnLaunch(getString(R.string.l_sheet), showTabName)))

            it.addItem(NavDrawer.AppNavDrawerItem(ScheduleFragment(), R.drawable.menu_work_schedule_icon, R.id.linearLayoutSecondItems, isShowOnLaunch(getString(R.string.schedule), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(scheduleTimeFragment, R.drawable.menu_schedule_lumper_icon, R.id.linearLayoutSecondItems, isShowOnLaunch(getString(R.string.scheduled_lumpers), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(ReportsFragment(), R.drawable.menu_roports_icon, R.id.linearLayoutSecondItems, isShowOnLaunch(getString(R.string.reports), showTabName)))

            it.addItem(NavDrawer.AppNavDrawerItem(LumpersFragment(), R.drawable.menu_lumper_contact_icon, R.id.linearLayoutThirdItems, isShowOnLaunch(getString(R.string.lumper_contact), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(QhlContactFragment(), R.drawable.menu_qhl_contact_icon, R.id.linearLayoutThirdItems, isShowOnLaunch(getString(R.string.qhl_contect), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(CustomerContactFragment(), R.drawable.menu_customer_contact_icon, R.id.linearLayoutThirdItems, isShowOnLaunch(getString(R.string.customer_contect), showTabName)))

            it.addItem(NavDrawer.AppNavDrawerItem(SettingsFragment(), R.drawable.menu_setting_icon, R.id.linearLayoutBottomItems, isShowOnLaunch(getString(R.string.settings), showTabName)))
            it.addItem(NavDrawer.AppNavDrawerItem(null, R.drawable.menu_logout_icon, R.id.linearLayoutBottomItems, isShowOnLaunch(getString(R.string.logout), showTabName)))

            it.create()
        }
    }

    private fun isShowOnLaunch(tabName: String, showTabName: String): Pair<String, Boolean> {
        val isShowOnLaunch = showTabName == tabName
        return Pair(tabName, isShowOnLaunch)
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                headerLayout.id -> {
                    val currentFragment: Fragment? =
                        supportFragmentManager.findFragmentById(R.id.frameLayoutMain)
                    if (currentFragment is TimeClockAttendanceFragment) {
                        if (currentFragment.onDataChanges()) showLeavePopup()
                        else openLeadActivity()
                    } else if (currentFragment is CustomerSheetFragment) {
                        if (currentFragment.onDataChanges()) showLeavePopup()
                        else openLeadActivity()
                    } else openLeadActivity()
                }
            }
        }
    }

    private fun showLeavePopup() {
        CustomProgressBar.getInstance().showLeaveDialog(
            getString(R.string.discard_leave_alert_message),
            activity,
            object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    openLeadActivity()
                }
                override fun onCancelClick() {
                }
            })
    }

    private fun openLeadActivity() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        navDrawer?.setOpen(false)
        startIntent(LeadProfileActivity::class.java)
    }


    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        if (message.equals(AppConstant.ERROR_MESSAGE, ignoreCase = true)) {
            CustomProgressBar.getInstance().showValidationErrorDialog(message, activity)
        } else SnackBarFactory.createSnackBar(activity, frameLayoutMain, message)
    }

    override fun showLeadProfile(leadProfileData: LeadProfileData) {
        UIUtils.showEmployeeProfileImage(activity, leadProfileData, circleImageViewProfile)
        val buildingDetailData =ScheduleUtils.getBuildingDetailData(leadProfileData.buildingDetailData)

        textViewLeadName.text = UIUtils.getEmployeeFullName(leadProfileData)
        textViewEmail.text =
            if (!leadProfileData.email.isNullOrEmpty()) leadProfileData.email else "-"
        textViewEmployeeId.text =
            if (!leadProfileData.employeeId.isNullOrEmpty()) leadProfileData.employeeId else "-"
        if (!buildingDetailData?.buildingName.isNullOrEmpty())
            textViewRole.text =
                if (!leadProfileData.role.isNullOrEmpty()) "QHL " + leadProfileData.role!!.capitalize() + " at " + buildingDetailData?.buildingName?.capitalize() else "-"
        else textViewRole.text =
            if (!leadProfileData.role.isNullOrEmpty()) "QHL " + leadProfileData.role!!.capitalize() else "-"

        textViewVersionName.text = String.format("v%s", BuildConfig.VERSION_NAME)
    }

    override fun showLoginScreen() {
        LanguageManager.setLanguage(this, sharedPref.getString(AppConstant.PREFERENCE_LANGUAGE, AppConstant.LANGUAGE_ENGLISH_CODE))
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun showPreformLogout(): Boolean {
        return isPerformLogout
    }

    /** Child Fragment Interaction Listeners */
    override fun onNewFragmentReplaced(title: String) {
        selectedFragmentTitle = title
        if(title.equals(getString(R.string.lumper_contact))){
            toolbar.background = ContextCompat.getDrawable(this, R.drawable.header_background_lumper)
            textViewToolbar.setTextColor(resources.getColor(android.R.color.white))
            toolbar.setNavigationIcon(R.drawable.ic_hamburger)
            headerLogoImage.visibility=View.VISIBLE
        }else if(title.equals(getString(R.string.customer_contect))){
            toolbar.background = ContextCompat.getDrawable(this, R.drawable.header_background_lumper)
            textViewToolbar.setTextColor(resources.getColor(android.R.color.white))
            toolbar.setNavigationIcon(R.drawable.ic_hamburger)
            headerLogoImage.visibility=View.GONE
        }else if(title.equals(getString(R.string.qhl_contect))){
            toolbar.setBackgroundColor(resources.getColor(R.color.textWhite));
            textViewToolbar.setTextColor(resources.getColor(android.R.color.white))
            toolbar.setNavigationIcon(R.drawable.ic_sidemenu)
            headerLogoImage.visibility=View.GONE
        }else{
            toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary));
            textViewToolbar.setTextColor(resources.getColor(android.R.color.black))
            toolbar.setNavigationIcon(R.drawable.ic_sidemenu)
            headerLogoImage.visibility=View.GONE

        }
        if (title.equals(getString(R.string.today_s_work_sheet))|| title.equals(getString(R.string.customer_sheet)) || title.equals(getString(R.string.reports))|| title.equals(getString(R.string.lumper_contact))|| title.equals(getString(R.string.settings)) || title.equals(getString(R.string.customer_contect)) || title.equals(getString(R.string.qhl_contect))){
            textViewDate.visibility=View.GONE
            textViewDate.text = ""
        }else{
            textViewDate.visibility=View.VISIBLE
            textViewDate.text = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, Date())
        }
        textViewToolbar.text = title
        invalidateOptionsMenu()

    }

    override fun invalidateScheduleTimeNotes(notes: String) {
        this.scheduleTimeNotes = notes
        invalidateOptionsMenu()
    }

    override fun invalidateCancelAllSchedulesOption(isShown: Boolean) {
        this.isCancelAllScheduleVisible = isShown
        invalidateOptionsMenu()
    }

    override fun invalidateAddNoteOption(isShown: Boolean) {
        this.isAddContainerVisible = isShown
        invalidateOptionsMenu()
    }

    override fun onLogoutOptionSelected() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.logout_alert_message), activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                isPerformLogout=true
                dashBoardPresenter.performLogout()
            }

            override fun onCancelClick() {
            }
        })
    }



}