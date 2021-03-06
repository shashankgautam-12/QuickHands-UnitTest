package com.quickhandslogistics.views.schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.schedule.ScheduledWorkItemDetailAdapter
import com.quickhandslogistics.contracts.schedule.ScheduledWorkItemDetailContract
import com.quickhandslogistics.data.schedule.ScheduleWorkItem
import com.quickhandslogistics.presenters.schedule.ScheduledWorkItemDetailPresenter
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_REQUEST_PARAMETER
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.buildingOperations.BuildingOperationsActivity
import com.quickhandslogistics.views.common.BuildingOperationsViewActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETER_VALUES
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import kotlinx.android.synthetic.main.activity_scheduled_work_item_detail.*

class ScheduledWorkItemDetailActivity : BaseActivity(), View.OnClickListener, ScheduledWorkItemDetailContract.View {

    private var workItemId: String = ""
    private var workItemTypeDisplayName: String = ""
    private var allowUpdate: Boolean = true
    private var workItemDetail: ScheduleWorkItem? = null

    private lateinit var lumpersAdapter: ScheduledWorkItemDetailAdapter
    private lateinit var scheduledWorkItemDetailPresenter: ScheduledWorkItemDetailPresenter

    companion object {
        const val WORK_ITEM_DETAIL = "WORK_ITEM_DETAIL"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled_work_item_detail)
        setupToolbar(getString(R.string.work_item_detail))

        intent.extras?.let { it ->
            allowUpdate = it.getBoolean(ARG_ALLOW_UPDATE, true)
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            workItemTypeDisplayName = it.getString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, "")
        }

        initializeUI()

        scheduledWorkItemDetailPresenter = ScheduledWorkItemDetailPresenter(this, resources)
        savedInstanceState?.also {
            if (savedInstanceState.containsKey(WORK_ITEM_DETAIL)) {
                workItemDetail = savedInstanceState.getParcelable(WORK_ITEM_DETAIL)!!
                showWorkItemDetail(workItemDetail!!)
            }
        } ?: run {
            scheduledWorkItemDetailPresenter.fetchWorkItemDetail(workItemId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (workItemDetail != null)
            outState.putParcelable(WORK_ITEM_DETAIL, workItemDetail)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            scheduledWorkItemDetailPresenter.fetchWorkItemDetail(workItemId)
            setResult(RESULT_OK)
        }
    }

    private fun initializeUI() {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(this@ScheduledWorkItemDetailActivity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumpersAdapter = ScheduledWorkItemDetailAdapter()
            adapter = lumpersAdapter
        }

        lumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (lumpersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        if (allowUpdate) {
            textViewEmptyData.text = getString(R.string.empty_work_item_lumpers_info_message)
            buttonAddBuildingOperations.text = getString(R.string.update_building_operations)
            buttonUpdateLumpers.visibility = View.VISIBLE
        } else {
            textViewEmptyData.text = getString(R.string.empty_work_item_lumpers_past_date_info_message)
            buttonAddBuildingOperations.text = getString(R.string.view_building_operations)
            buttonUpdateLumpers.visibility = View.GONE
        }

        buttonUpdateLumpers.setOnClickListener(this)
        buttonAddBuildingOperations.setOnClickListener(this)
    }

    private fun showAddLumpersScreen() {
        workItemDetail?.let { workItemDetail ->
            val bundle = Bundle()
            bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id)
            bundle.putString(ARG_WORK_ITEM_TYPE, workItemDetail.workItemType)
            if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
                bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, true)
            } else {
                bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, false)
                bundle.putParcelableArrayList(AddWorkItemLumpersActivity.ARG_ASSIGNED_LUMPERS_LIST, workItemDetail.assignedLumpersList)
            }
            startIntent(AddWorkItemLumpersActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
        }
    }

    private fun showBuildingOperationsScreen() {
        workItemDetail?.let { workItemDetail ->
            if (allowUpdate) {
                val bundle = Bundle()
                bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id)
                bundle.putStringArrayList(ARG_BUILDING_PARAMETERS, workItemDetail.buildingDetailData?.parameters)
                startIntent(BuildingOperationsActivity::class.java, bundle = bundle)
            } else {
                val bundle = Bundle()
                bundle.putStringArrayList(ARG_BUILDING_PARAMETERS, workItemDetail.buildingDetailData?.parameters)
                bundle.putSerializable(ARG_BUILDING_PARAMETER_VALUES, workItemDetail.buildingOps)
                startIntent(BuildingOperationsViewActivity::class.java, bundle = bundle)
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
                buttonUpdateLumpers.id -> showAddLumpersScreen()
                buttonAddBuildingOperations.id -> showBuildingOperationsScreen()
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showWorkItemDetail(workItemDetail: ScheduleWorkItem) {
        this.workItemDetail = workItemDetail
        textViewStartTime.text = String.format(getString(R.string.start_time_s), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))
        workItemDetail.scheduledFrom?.let {
            textViewScheduledDate.text = DateUtils.changeDateString(PATTERN_API_REQUEST_PARAMETER, PATTERN_NORMAL, it)
        }

        when (workItemTypeDisplayName) {
            getString(R.string.drops) -> textViewWorkItemsCount.text = String.format(getString(R.string.no_of_drops_s), workItemDetail.sequence)
            getString(R.string.live_loads) -> textViewWorkItemsCount.text = String.format(getString(R.string.live_load_s), workItemDetail.sequence)
            else -> textViewWorkItemsCount.text = String.format(getString(R.string.out_bound_s), workItemDetail.sequence)
        }

        ScheduleUtils.changeStatusUIByValue(resources, workItemDetail.status, textViewStatus)

        workItemDetail.assignedLumpersList?.let { assignedLumpersList ->
            lumpersAdapter.updateData(assignedLumpersList)

            if (assignedLumpersList.size > 0) {
                buttonUpdateLumpers.text = getString(R.string.update_lumpers)
            } else {
                buttonUpdateLumpers.text = getString(R.string.add_lumpers)
            }
        }
    }
}