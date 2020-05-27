package com.quickhandslogistics.views.workSheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.common.ContainerDetailAdapter
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.buildingOperations.BuildingOperationsActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.utils.AppConstant
import kotlinx.android.synthetic.main.fragment_work_sheet_item_detail_bo.*

class WorkSheetItemDetailBOFragment : BaseFragment(), View.OnClickListener {

    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? = null

    private lateinit var containerDetailAdapter: ContainerDetailAdapter

    private var workItemDetail: WorkItemDetail? = null

    companion object {
        @JvmStatic
        fun newInstance() = WorkSheetItemDetailBOFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is WorkSheetItemDetailContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = activity as WorkSheetItemDetailContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_bo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewBuildingOperations.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            containerDetailAdapter = ContainerDetailAdapter()
            adapter = containerDetailAdapter
        }

        containerDetailAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (containerDetailAdapter.itemCount == 0) {
                    textViewEmptyData.visibility = View.VISIBLE
                    buttonUpdate.isEnabled = false
                } else {
                    textViewEmptyData.visibility = View.GONE
                    buttonUpdate.isEnabled = true
                }
            }
        })

        buttonUpdate.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            onFragmentInteractionListener?.fetchWorkItemDetail(changeResultCode = false)
        }
    }

    fun showBuildingOperationsData(workItemDetail: WorkItemDetail) {
        this.workItemDetail = workItemDetail
        workItemDetail.status?.let { status ->
            if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED || status == AppConstant.WORK_ITEM_STATUS_CANCELLED) {
                buttonUpdate.visibility = View.GONE
            } else {
                buttonUpdate.visibility = View.VISIBLE
            }
        }

        containerDetailAdapter.updateData(workItemDetail.buildingOps, workItemDetail.buildingDetailData?.parameters)
    }

    fun showEmptyData() {
        containerDetailAdapter.updateData(HashMap(), ArrayList())
        buttonUpdate.visibility = View.GONE
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonUpdate.id -> {
                    val bundle = Bundle()
                    bundle.putString(ARG_WORK_ITEM_ID, workItemDetail?.id)
                    bundle.putStringArrayList(ARG_BUILDING_PARAMETERS, workItemDetail?.buildingDetailData?.parameters)
                    startIntent(BuildingOperationsActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                }
            }
        }
    }
}