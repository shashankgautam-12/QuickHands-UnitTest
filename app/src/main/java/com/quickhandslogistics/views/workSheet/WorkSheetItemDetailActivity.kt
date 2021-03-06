package com.quickhandslogistics.views.workSheet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.WorkSheetItemDetailPagerAdapter
import com.quickhandslogistics.adapters.workSheet.WorkSheetItemStatusAdapter
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.presenters.workSheet.WorkSheetItemDetailPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ORIGIN
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import kotlinx.android.synthetic.main.activity_work_sheet_item_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_select_status.*
import kotlinx.android.synthetic.main.content_work_sheet_item_detail.*
import okhttp3.MultipartBody
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.content_work_sheet_item_detail.textViewWorkSheetNote as textViewWorkSheetNote1

class WorkSheetItemDetailActivity : BaseActivity(), View.OnClickListener, WorkSheetItemDetailContract.View,
        WorkSheetItemDetailContract.View.OnAdapterItemClickListener, WorkSheetItemDetailContract.View.OnFragmentInteractionListener {

    private var workItemId: String = ""
    private var workItemTypeDisplayName: String = ""
    private var origin: String = ""
    private var containerNumber: Int = 0
    private var workItemDetail: WorkItemContainerDetails = WorkItemContainerDetails()
    private var lumpersTimeSchedule: ArrayList<LumpersTimeSchedule> = ArrayList()
    private var tempLumperIds: ArrayList<String> = ArrayList()
    private var buildingParams: ArrayList<String> = ArrayList()
    private var selectedTime: Long = 0
    private lateinit var workSheetItemDetailPresenter: WorkSheetItemDetailPresenter
    private var workSheetItemStatusAdapter: WorkSheetItemStatusAdapter? = null
    private var workSheetItemDetailPagerAdapter: WorkSheetItemDetailPagerAdapter? = null

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    companion object {
        const val WORK_DETAIL_LIST = "WORK_DETAIL_LIST"
        const val TEMP_LUMPER_ID_LIST = "TEMP_LUMPER_ID_LIST"
        const val SCHEDULE_BUILDING_PARAMETETER = "SCHEDULE_BUILDING_PARAMETETER"
        const val LUMPER_TIME_SCHEDULE = "LUMPER_TIME_SCHEDULE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_sheet_item_detail)
        setupToolbar(getString(R.string.container_details))


        intent.extras?.let { it ->
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            origin = it.getString(ARG_WORK_ITEM_ORIGIN, "")
            workItemTypeDisplayName = it.getString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, "")
            selectedTime = it.getLong(ARG_SELECTED_DATE_MILLISECONDS)
            containerNumber = it.getInt(ScheduleFragment.ARG_WORK_ITEM_TYPE_DISPLAY_NUMBER)
        }

        workSheetItemDetailPresenter = WorkSheetItemDetailPresenter(this, resources)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(LUMPER_TIME_SCHEDULE)) {
                lumpersTimeSchedule = savedInstanceState.getParcelableArrayList(LUMPER_TIME_SCHEDULE)!!
            }
            if (savedInstanceState.containsKey(SCHEDULE_BUILDING_PARAMETETER)) {
                buildingParams = savedInstanceState.getStringArrayList(SCHEDULE_BUILDING_PARAMETETER)!!
            }
            if (savedInstanceState.containsKey(TEMP_LUMPER_ID_LIST)) tempLumperIds =
                    savedInstanceState.getStringArrayList(TEMP_LUMPER_ID_LIST)!!
            if (savedInstanceState.containsKey(WORK_DETAIL_LIST)) {
                workItemDetail = savedInstanceState.getParcelable(WORK_DETAIL_LIST)!!
//                showWorkItemDetail(workItemDetail, lumpersTimeSchedule, tempLumperIds)
                val allWorkItemLists = createDifferentListData(workItemDetail)
                initializeUI(allWorkItemLists, tempLumperIds, lumpersTimeSchedule, buildingParams)
            }
        } ?: run {
            initializeUI()
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            workSheetItemDetailPresenter.fetchWorkItemDetail(workItemId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(WORK_DETAIL_LIST, workItemDetail)
        outState.putParcelableArrayList(LUMPER_TIME_SCHEDULE, lumpersTimeSchedule)
        outState.putStringArrayList(TEMP_LUMPER_ID_LIST, tempLumperIds)
        outState.putStringArrayList(SCHEDULE_BUILDING_PARAMETETER, buildingParams)
        super.onSaveInstanceState(outState)
    }

    private fun createDifferentListData(workItemDetail: WorkItemContainerDetails): WorkItemContainerDetails {
        textViewStartTime.text = UIUtils.getSpannableText(getString(R.string.start_time_bold), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime).toString())

        when (workItemTypeDisplayName) {
            getString(R.string.drops) -> textViewDropItems.text = if (origin == AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME) {
                    UIUtils.getSpannableText(resources.getString(R.string.unfinished_no_of_drops_bold_has),containerNumber.toString())
                } else  UIUtils.getSpannableText(resources.getString(R.string.no_of_drops_bold_has), containerNumber.toString())

            getString(R.string.live_loads) -> textViewDropItems.text = if (origin == AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME) {
                     UIUtils.getSpannableText(resources.getString(R.string.unfinished_live_load_bold_has), containerNumber.toString())
                } else UIUtils.getSpannableText(resources.getString(R.string.live_load_bold_has), containerNumber.toString())

            else -> textViewDropItems.text = if (origin == AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME) {
                 UIUtils.getSpannableText(resources.getString(R.string.unfinished_out_bound_bold_has), containerNumber.toString())
            } else UIUtils.getSpannableText(resources.getString(R.string.out_bound_bold_has), containerNumber.toString())
        }

        if (!workItemDetail.status.isNullOrEmpty()) {
            updateStatusBackground(workItemDetail.status!!)
        }

        textViewIsScheduleLead.visibility=if (workItemDetail.isScheduledByLead!!) View.VISIBLE else View.GONE
        return workItemDetail
    }

    private fun initializeUI(allWorkItem: WorkItemContainerDetails? = null, tampLumpId: ArrayList<String>? = null, lumperTimeSchedule: ArrayList<LumpersTimeSchedule>? = null, buildingParams: ArrayList<String>? = null) {
        workSheetItemDetailPagerAdapter = if (allWorkItem != null)
            WorkSheetItemDetailPagerAdapter(
                supportFragmentManager,
                resources,
                selectedTime,
                allWorkItem,
                tampLumpId,
                lumperTimeSchedule,
                buildingParams
            )
        else
            WorkSheetItemDetailPagerAdapter(
                supportFragmentManager,
                resources,
                selectedTime
            )
        viewPagerWorkSheetDetail.offscreenPageLimit = workSheetItemDetailPagerAdapter?.count!!
        viewPagerWorkSheetDetail.adapter = workSheetItemDetailPagerAdapter
        tabLayoutWorkSheetDetail.setupWithViewPager(viewPagerWorkSheetDetail)

        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetStatus)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        recyclerViewStatus.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(
                activity,
                linearLayoutManager.orientation
            )
            addItemDecoration(dividerItemDecoration)
            workSheetItemStatusAdapter = WorkSheetItemStatusAdapter(
                resources,
                this@WorkSheetItemDetailActivity
            )
            adapter = workSheetItemStatusAdapter
        }

        textViewRequestCorrection.visibility= if (!DateUtils.isFutureDate(selectedTime) && !DateUtils.isCurrentDate(selectedTime)) View.GONE else View.GONE
        textViewStatus.setOnClickListener(this)
        textViewRequestCorrection.setOnClickListener(this)
        textViewWorkSheetNote1.setOnClickListener(this)
        bottomSheetBackgroundStatus.setOnClickListener(this)

        textViewStatus.isEnabled = DateUtils.isCurrentDate(selectedTime)
    }

    private fun updateStatusBackground(status: String) {
        //val statusList: LinkedHashMap<String, String> = LinkedHashMap()
        //statusList.putAll(ScheduleUtils.createStatusList(resources, status))

        ScheduleUtils.changeStatusUIByValue(resources, status, textViewStatus, isEditable = true)

        //workSheetItemStatusAdapter?.updateStatusList(statusList)
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackgroundStatus.visibility = View.GONE
    }

    private fun requestCorrection(workId: String?) {
        if (!workId.isNullOrEmpty())
            CustomBottomSheetDialog.requestCorrectionBottomSheetDialog(
                activity, object : CustomBottomSheetDialog.IDialogRequestCorrectionClick {
                    override fun onSendRequest(dialog: Dialog, request: String) {
                        dialog.dismiss()
                    }
                })
        AppUtils.hideSoftKeyboard(this)
    }

    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                bottomSheetBackgroundStatus.id -> closeBottomSheet()
                textViewRequestCorrection.id -> requestCorrection(workItemId)
                textViewStatus.id -> {
                    if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        workSheetItemStatusAdapter?.updateInitialStatus(
                            textViewStatus.text.toString(),
                            workItemDetail.isCompleted!!
                        )
                        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        bottomSheetBackgroundStatus.visibility = View.VISIBLE
                    } else {
                        closeBottomSheet()
                    }

                }
                textViewWorkSheetNote1.id -> {
                    if (!workItemDetail.schedule?.scheduleNote.isNullOrEmpty() && !workItemDetail.schedule?.scheduleNote.equals(
                            "NA"
                        )
                    ) {
                        val title =
                            ScheduleUtils.scheduleNotePopupTitle(
                                workItemDetail.schedule,
                                resources
                            )
                        CustomProgressBar.getInstance()
                            .showInfoDialog(
                                title,
                                workItemDetail.schedule?.scheduleNote!!,
                                this
                            )
                    }
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
//        workSheetItemDetailPagerAdapter?.showEmptyData()
    }

    override fun showWorkItemDetail(
        workItemDetail: WorkItemContainerDetails,
        lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?,
        buildingParams: ArrayList<String>?
    ) {
        lumpersTimeSchedule?.let {
            this.lumpersTimeSchedule = it
        }

        this.workItemDetail = workItemDetail
        this.tempLumperIds = tempLumperIds
        if (!DateUtils.isFutureDate(selectedTime) && !DateUtils.isCurrentDate(selectedTime)) {
            this.buildingParams =
                ScheduleUtils.getFilledBuildingOpsParameterList(workItemDetail.buildingOps)
        } else {
            buildingParams?.let { this.buildingParams = it }
        }

        textViewStartTime.text = UIUtils.getSpannableText(getString(R.string.start_time_bold),
            DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime).toString()
        )
        if (workItemDetail.schedule!=null) {
            textViewWorkSheetNote1.text = ScheduleUtils.scheduleTypeNote(
                workItemDetail.schedule,
                resources
            )
        }
        textViewWorkSheetNote1.isEnabled=!workItemDetail.schedule?.scheduleNote.isNullOrEmpty() && !workItemDetail.schedule?.scheduleNote.equals(
            "NA"
        )

        when (workItemTypeDisplayName) {
            getString(R.string.drops) -> textViewDropItems.text = if (origin == AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME) {
                UIUtils.getSpannableText(resources.getString(R.string.unfinished_no_of_drops_bold_has),containerNumber.toString())
            } else  UIUtils.getSpannableText(resources.getString(R.string.no_of_drops_bold_has),containerNumber.toString())

            getString(R.string.live_loads) -> textViewDropItems.text = if (origin == AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME) {
                UIUtils.getSpannableText(resources.getString(R.string.unfinished_live_load_bold_has), containerNumber.toString())
            } else UIUtils.getSpannableText(resources.getString(R.string.live_load_bold_has), containerNumber.toString())

            else -> textViewDropItems.text = if (origin == AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME) {
                UIUtils.getSpannableText(resources.getString(R.string.unfinished_out_bound_bold_has), containerNumber.toString())
            } else UIUtils.getSpannableText(resources.getString(R.string.out_bound_bold_has), containerNumber.toString())
        }

        if (!workItemDetail.status.isNullOrEmpty()) {
            updateStatusBackground(workItemDetail.status!!)
        }
        textViewIsScheduleLead.visibility=if (workItemDetail.isScheduledByLead!!) View.VISIBLE else View.GONE

        workSheetItemDetailPagerAdapter?.showWorkItemData(
            workItemDetail,
            lumpersTimeSchedule,
            tempLumperIds,
            this.buildingParams
        )
    }

    override fun statusChangedSuccessfully() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        setResult(RESULT_OK)
    }

    override fun notesSavedSuccessfully() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        isDataSave(true)
        CustomProgressBar.getInstance().showSuccessDialog(
            getString(R.string.notes_saved_success_alert_message),
            activity,
            object : CustomDialogListener {
                override fun onConfirmClick() {

                }
            })
//        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, getString(R.string.notes_saved_success_alert_message))
    }

    override fun showLoginScreen() {
        startIntent(
            LoginActivity::class.java, isFinish = true, flags = arrayOf(
                Intent.FLAG_ACTIVITY_CLEAR_TASK,
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        )
    }

    override fun onSuccessUploadImage(imageUrl: String) {
        workSheetItemDetailPagerAdapter?.updateUploadedImage(imageUrl)
    }

    /** Adapter Listeners */
    override fun onSelectStatus(status: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        val filledParameterCount = ScheduleUtils.getFilledBuildingParametersCounts(workItemDetail, buildingParams)
        val parameters = ScheduleUtils.getBuildingParametersList(buildingParams)


        if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED) {
            if (workItemDetail.buildingOps.isNullOrEmpty() || filledParameterCount != parameters.size) {
                CustomProgressBar.getInstance().showErrorDialog(getString(R.string.fill_building_parameters_message), activity)
                closeBottomSheet()
                return
            } else if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
                CustomProgressBar.getInstance().showErrorDialog(getString(R.string.assign_lumpers_message), activity)
                closeBottomSheet()
                return
            } else {
                if (lumpersTimeSchedule.isNullOrEmpty() || lumpersTimeSchedule.size < workItemDetail!!.assignedLumpersList!!.size) {
                    val message = getString(R.string.assign_lumpers_endtime_starttime_message)
                    CustomProgressBar.getInstance().showErrorDialog(message, this.activity)
                    closeBottomSheet()
                    return
                } else if (!lumpersTimeSchedule.isNullOrEmpty()) {
                    val message = getStartTimeCount(lumpersTimeSchedule)
                    if (message.isNotEmpty()) {
                        CustomProgressBar.getInstance().showErrorDialog(message, this.activity)
                        closeBottomSheet()
                        return
                    }
                }

            }
        } else if (AppConstant.WORK_ITEM_STATUS_UNFINISHED == status) {
            if (workItemDetail.buildingOps.isNullOrEmpty() || filledParameterCount != parameters.size) {
                CustomerDialog.showUnfinishedErrorDialog(activity)
                closeBottomSheet()
                return
            } else if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
                CustomerDialog.showUnfinishedErrorDialog(activity)
                closeBottomSheet()
                return
            } else {
                if (lumpersTimeSchedule.isNullOrEmpty() || lumpersTimeSchedule.size < workItemDetail!!.assignedLumpersList!!.size) {
                    CustomerDialog.showUnfinishedErrorDialog(activity)
                    closeBottomSheet()
                    return
                } else if (!lumpersTimeSchedule.isNullOrEmpty()) {
                    val message = getStartTimeCount(lumpersTimeSchedule)
                    if (message.isNotEmpty()) {
                        CustomerDialog.showUnfinishedErrorDialog(activity)
                        closeBottomSheet()
                        return
                    }
                }
            }

            closeBottomSheet()
            CustomBottomSheetDialog.unfinishedBottomSheetDialog(
                activity,
                object : CustomBottomSheetDialog.IDialogOnClick {
                    override fun onSendRequest(dialog: Dialog, selectedDate: Date, selectedTime: Long) {
                        dialog.dismiss()
                        workSheetItemDetailPresenter.changeWorkItemStatus(workItemId, status, selectedDate, selectedTime )
                    }
                })
            return
        }

        var message = getString(R.string.change_status_alert_message)
        if (status == AppConstant.WORK_ITEM_STATUS_CANCELLED || status == AppConstant.WORK_ITEM_STATUS_COMPLETED) {
            message = getString(R.string.change_status_permanently_alert_message)
        }
        closeBottomSheet()
        workSheetItemDetailPresenter.changeWorkItemStatus(workItemId, status)
    }

    private fun getStartTimeCount(lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>): String {
        var message = ""
        lumpersTimeSchedule.forEach {
            if (it.startTime.isNullOrEmpty() || it.endTime.isNullOrEmpty()) {
                message = getString(R.string.assign_lumpers_endtime_starttime_message)
            } else if (!it.breakTimeStart.isNullOrEmpty() && it.breakTimeEnd.isNullOrEmpty()) {
                message = getString(R.string.assign_lumpers_bracktime_message)
            } else if (it.partWorkDone.isNullOrEmpty() || it.partWorkDone!!.toInt() == 0) {
                message = getString(R.string.assign_work_done_message)
            }
        }
        return message
    }

    /** Child Fragment Interaction Listeners */
    override fun fetchWorkItemDetail(changeResultCode: Boolean) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        if (changeResultCode)
            setResult(RESULT_OK)
        isDataSave(true)
        workSheetItemDetailPresenter.fetchWorkItemDetail(workItemId)
    }

    override fun updateWorkItemNotes(notesQHLCustomer: String, notesQHL: String, noteImageArrayList: ArrayList<String>) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }
        workSheetItemDetailPresenter.updateWorkItemNotes(workItemId, notesQHLCustomer, notesQHL, noteImageArrayList)
    }

    override fun dataChanged(isChanged: Boolean) {
        if (isChanged) isDataSave(false) else isDataSave(true)
    }

    override fun removeLumperFromSchedule(
        lumperIds: ArrayList<String>,
        tempLumperIds: ArrayList<String>
    ) {

        CustomProgressBar.getInstance().showWarningDialog(
            getString(R.string.remove_lumper_warning),
            activity,
            object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    workSheetItemDetailPresenter.removeLumper(lumperIds, tempLumperIds, workItemId)
                }

                override fun onCancelClick() {
                }
            })
    }

    override fun uploadNoteImage(imageFileName: MultipartBody.Part) {
        workSheetItemDetailPresenter.uploadNoteImage(imageFileName)
    }
}