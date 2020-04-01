package com.quickhandslogistics.modified.views.activities.schedule

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogWarningContract
import com.quickhandslogistics.modified.contracts.schedule.WorkItemDetailContract
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.ChooseLumperActivity
import com.quickhandslogistics.modified.views.adapters.schedule.ScheduledWorkItemDetailAdapter
import com.quickhandslogistics.modified.views.fragments.InfoWarningDialogFragment
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_ALLOW_UPDATE
import kotlinx.android.synthetic.main.activity_scheduled_work_item_detail.*

class ScheduledWorkItemDetailActivity : BaseActivity(),
    WorkItemDetailContract.View.OnAdapterItemClickListener, View.OnClickListener {

    private var allowUpdate: Boolean = true
    private var scheduledWorkItemDetailAdapter: ScheduledWorkItemDetailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled_work_item_detail)
        setupToolbar()

        intent.extras?.let { it ->
            if (it.containsKey(ARG_ALLOW_UPDATE)) {
                allowUpdate = it.getBoolean(ARG_ALLOW_UPDATE, true)

                recyclerViewLumpers.apply {
                    val linearLayoutManager =
                        LinearLayoutManager(this@ScheduledWorkItemDetailActivity)
                    layoutManager = linearLayoutManager
                    val dividerItemDecoration =
                        DividerItemDecoration(activity, linearLayoutManager.orientation)
                    addItemDecoration(dividerItemDecoration)
                    scheduledWorkItemDetailAdapter =
                        ScheduledWorkItemDetailAdapter(
                            this@ScheduledWorkItemDetailActivity,
                            allowUpdate
                        )
                    adapter = scheduledWorkItemDetailAdapter
                }

                if (allowUpdate) {
                    buttonAddBuildingOperations.text = getString(R.string.add_building_operations)
                } else {
                    buttonAddBuildingOperations.text = getString(R.string.building_operations)
                }
            }
        }

        buttonAddBuildingOperations.setOnClickListener(this)
    }

    override fun onReplaceItemClick(position: Int) {
        startIntent(ChooseLumperActivity::class.java)
//        val dialog = InfoWarningDialogFragment.newInstance(
//            getString(R.string.string_ask_new_lumper),
//            positiveButtonText = getString(R.string.string_yes),
//            negativeButtonText = getString(R.string.string_no),
//            onClickListener = object : InfoDialogWarningContract.View.OnClickListener {
//                override fun onPositiveButtonClick() {
//                }
//
//                override fun onNegativeButtonClick() {
//                }
//            })
//        dialog.show(supportFragmentManager, InfoWarningDialogFragment::class.simpleName)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAddBuildingOperations.id -> {
                    val bundle = Bundle()
                    bundle.putBoolean(BuildingOperationsActivity.ARG_ALLOW_UPDATE, allowUpdate)
                    startIntent(BuildingOperationsActivity::class.java, bundle = bundle)
                }
            }
        }
    }
}
