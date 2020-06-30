package com.quickhandslogistics.views.workSheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.WorkSheetItemDetailLumpersAdapter
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailLumpersContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.lumpers.LumperDetailActivity
import com.quickhandslogistics.views.schedule.AddWorkItemLumpersActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE
import kotlinx.android.synthetic.main.content_work_sheet_item_detail_lumpers.*

class WorkSheetItemDetailLumpersFragment : BaseFragment(), View.OnClickListener, WorkSheetItemDetailLumpersContract.View.OnAdapterItemClickListener {

    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? = null

    private lateinit var workSheetItemDetailLumpersAdapter: WorkSheetItemDetailLumpersAdapter

    private var workItemDetail: WorkItemDetail? = null

    companion object {
        private const val LUMPER_WORK_DETALS = "LUMPER_WORK_DETALS"
        private const val LUMPER_SCHEDULE = "LUMPER_SCHEDULE"
        private const val TEMP_LUMPER_IDS = "TEMP_LUMPER_IDS"
        @JvmStatic
        fun newInstance(
            allWorkItem: WorkItemDetail?,
            lumperTimeSchedule: ArrayList<LumpersTimeSchedule>?,
            tempLumperIds: ArrayList<String>?
        ) = WorkSheetItemDetailLumpersFragment()
            .apply {
                arguments = Bundle().apply {
                    if(allWorkItem!=null){
                        putParcelable(LUMPER_WORK_DETALS, allWorkItem)
                        putParcelableArrayList(LUMPER_SCHEDULE, lumperTimeSchedule)
                        putStringArrayList(TEMP_LUMPER_IDS, tempLumperIds)
                    }
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is WorkSheetItemDetailContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = activity as WorkSheetItemDetailContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(LUMPER_WORK_DETALS))
            workItemDetail = it.getParcelable<WorkItemDetail>(LUMPER_WORK_DETALS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_lumpers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            workSheetItemDetailLumpersAdapter = WorkSheetItemDetailLumpersAdapter(this@WorkSheetItemDetailLumpersFragment)
            adapter = workSheetItemDetailLumpersAdapter
        }

        workSheetItemDetailLumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (workSheetItemDetailLumpersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        buttonAddLumpers.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            onFragmentInteractionListener?.fetchWorkItemDetail(changeResultCode = true)
        }
    }

    fun showLumpersData(workItemDetail: WorkItemDetail, lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?, tempLumperIds: ArrayList<String>) {
        this.workItemDetail = workItemDetail

        val timingsData = LinkedHashMap<String, LumpersTimeSchedule>()
        workItemDetail.assignedLumpersList?.let { assignedLumpersList ->
            if (!lumpersTimeSchedule.isNullOrEmpty()) {
                for (lumper in assignedLumpersList) {
                    for (timing in lumpersTimeSchedule) {
                        if (lumper.id == timing.lumperId) {
                            timingsData[lumper.id!!] = timing
                            break
                        }
                    }
                }
            }
        }

        workSheetItemDetailLumpersAdapter.updateList(workItemDetail.assignedLumpersList, timingsData, workItemDetail.status, tempLumperIds)

        if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
            buttonAddLumpers.text = getString(R.string.add_lumpers)
        } else {
            buttonAddLumpers.text = getString(R.string.update_lumpers)
        }

        workItemDetail.status?.let { status ->
            if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED || status == AppConstant.WORK_ITEM_STATUS_CANCELLED) {
                buttonAddLumpers.visibility = View.GONE
                textViewEmptyData.text = getString(R.string.empty_work_item_lumpers_past_date_info_message)
            } else {
                buttonAddLumpers.visibility = View.VISIBLE
                textViewEmptyData.text = getString(R.string.empty_work_item_lumpers_info_message)
            }
        }
    }

    fun showEmptyData() {
        workSheetItemDetailLumpersAdapter.updateList(ArrayList(), LinkedHashMap(), tempLumperIds = ArrayList())
        buttonAddLumpers.visibility = View.GONE
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

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAddLumpers.id -> showAddLumpersScreen()
            }
        }
    }

    /** Adapter Listeners */
    override fun onAddTimeClick(employeeData: EmployeeData, timingData: LumpersTimeSchedule?) {
        val bundle = Bundle()
        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail?.id)
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_TIMING_DATA, timingData)
        startIntent(AddLumperTimeWorkSheetItemActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }
}