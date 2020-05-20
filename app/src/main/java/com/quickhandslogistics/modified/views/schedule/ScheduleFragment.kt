package com.quickhandslogistics.modified.views.schedule

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
import com.quickhandslogistics.modified.adapters.schedule.ScheduleAdapter
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.presenters.schedule.SchedulePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.common.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_IDENTITY
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CalendarUtils
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.util.*

class ScheduleFragment : BaseFragment(), ScheduleContract.View, ScheduleContract.View.OnAdapterItemClickListener, CalendarUtils.CalendarSelectionListener {

    private var onFragmentInteractionListener: ScheduleMainContract.View.OnFragmentInteractionListener? = null

    private var currentPageIndex: Int = 1
    private var nextPageIndex: Int = 1
    private var totalPagesCount: Int = 1

    private var selectedTime: Long = 0
    private var isCurrentDate: Boolean = true
    private lateinit var availableDates: List<Date>

    private lateinit var schedulePresenter: SchedulePresenter
    private lateinit var scheduleAdapter: ScheduleAdapter

    companion object {
        @JvmStatic
        fun newInstance() = ScheduleFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is ScheduleMainContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = parentFragment as ScheduleMainContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        schedulePresenter = SchedulePresenter(this, resources)

        // Setup DatePicker Dates
        selectedTime = Date().time
        availableDates = CalendarUtils.getPastCalendarDates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewSchedule.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            scheduleAdapter = ScheduleAdapter(resources, this@ScheduleFragment)
            adapter = scheduleAdapter
            addOnScrollListener(onScrollListener)
        }

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarSchedule, availableDates, this)
        singleRowCalendarSchedule.select(availableDates.size - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        schedulePresenter.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            resetPaginationValues()
            fetchScheduledWorkItems()
        }
    }

    private fun fetchScheduledWorkItems() {
        if (singleRowCalendarSchedule.getSelectedDates().isNotEmpty()) {
            schedulePresenter.getScheduledWorkItemsByDate(singleRowCalendarSchedule.getSelectedDates()[0], currentPageIndex)
        }
    }

    private fun resetPaginationValues() {
        currentPageIndex = 1
        nextPageIndex = 1
        totalPagesCount = 1
    }

    /** Native Views Listeners */
    private val onScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView.layoutManager?.let { layoutManager ->
                if (layoutManager is LinearLayoutManager) {
                    val visibleItemCount: Int = layoutManager.childCount
                    val totalItemCount: Int = layoutManager.itemCount
                    val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                    if (currentPageIndex != totalPagesCount) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                            currentPageIndex = nextPageIndex
                            fetchScheduledWorkItems()
                        }
                    }
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun showDateString(dateString: String) {
        textViewDate.text = dateString
    }

    override fun showScheduleData(selectedDate: Date, workItemsList: ArrayList<ScheduleDetail>, totalPagesCount: Int, nextPageIndex: Int, currentPageIndex: Int) {
        selectedTime = selectedDate.time
        isCurrentDate = com.quickhandslogistics.utils.DateUtils.isCurrentDate(selectedTime)
        scheduleAdapter.updateList(workItemsList, currentPageIndex)

        textViewEmptyData.visibility = View.GONE
        recyclerViewSchedule.visibility = View.VISIBLE
        textViewDate.visibility = View.VISIBLE

        this.totalPagesCount = totalPagesCount
        this.nextPageIndex = nextPageIndex
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewSchedule.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun fetchUnScheduledWorkItems() {
        onFragmentInteractionListener?.fetchUnScheduledWorkItems()
    }

    override fun showEmptyData() {
        textViewEmptyData.visibility = View.VISIBLE
        recyclerViewSchedule.visibility = View.GONE
        textViewDate.visibility = View.GONE
    }

    /** Adapter Listeners */
    override fun onScheduleItemClick(scheduleDetail: ScheduleDetail) {
        val bundle = Bundle()
        bundle.putBoolean(ARG_ALLOW_UPDATE, isCurrentDate)
        bundle.putString(ARG_SCHEDULE_IDENTITY, scheduleDetail.scheduleIdentity)
        bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        startIntent(ScheduleDetailActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    override fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(DisplayLumpersListActivity.ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }

    /** Calendar Listeners */
    override fun onSelectCalendarDate(date: Date) {
        resetPaginationValues()
        schedulePresenter.getScheduledWorkItemsByDate(date, currentPageIndex)
    }
}