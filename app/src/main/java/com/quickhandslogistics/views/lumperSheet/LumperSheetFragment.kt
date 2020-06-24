package com.quickhandslogistics.views.lumperSheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.lumperSheet.LumperSheetAdapter
import com.quickhandslogistics.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.presenters.lumperSheet.LumperSheetPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.schedule.ScheduleFragment
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment
import kotlinx.android.synthetic.main.fragment_lumper_sheet.*
import kotlinx.android.synthetic.main.fragment_lumper_sheet.editTextSearch
import kotlinx.android.synthetic.main.fragment_lumper_sheet.imageViewCancel
import kotlinx.android.synthetic.main.fragment_lumper_sheet.mainConstraintLayout
import kotlinx.android.synthetic.main.fragment_lumper_sheet.textViewDate
import kotlinx.android.synthetic.main.fragment_lumper_sheet.textViewEmptyData
import kotlinx.android.synthetic.main.fragment_schedule_time.*
import java.util.*

class LumperSheetFragment : BaseFragment(), LumperSheetContract.View, TextWatcher, View.OnClickListener,
    LumperSheetContract.View.OnAdapterItemClickListener, CalendarUtils.CalendarSelectionListener {

    private var selectedTime: Long = 0
    private lateinit var availableDates: List<Date>
    private lateinit var lumperInfoList: ArrayList<LumpersInfo>
    private var sheetSubmitted: Boolean= false
    private lateinit var selectedDate: Date
    private lateinit var tempLumperIds: ArrayList<String>
    private  var dateString: String ?=null
    private var datePosition: Int = 0
    private var isSavedState: Boolean = false

    private lateinit var lumperSheetAdapter: LumperSheetAdapter
    private lateinit var lumperSheetPresenter: LumperSheetPresenter

    companion object {
        const val ARG_LUMPER_INFO = "ARG_LUMPER_INFO"
        const val LUMPER_INFO_LIST = "LUMPER_INFO_LIST"
        const val DATE = "DATE"
        const val DATE_SELECTED = "DATE_SELECTED"
        const val SHEET_SUBMITTED= "SHEET_SUBMITTED"
        const val TEMP_LUMPER = "TEMP_LUMPER"
        const val SELECTED_DATE = "SELECTED_DATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumperSheetPresenter = LumperSheetPresenter(this, resources)

        // Setup Calendar Dates
        selectedTime = Date().time
        availableDates = CalendarUtils.getPastCalendarDates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lumper_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpersSheet.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumperSheetAdapter = LumperSheetAdapter(resources, this@LumperSheetFragment)
            adapter = lumperSheetAdapter
        }

        lumperSheetAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarLumperSheet, availableDates, this)
        savedInstanceState?.also {
            isSavedState=true
            if (savedInstanceState.containsKey(ScheduleTimeFragment.SELECTED_DATE)) {
                datePosition = savedInstanceState.getInt(ScheduleTimeFragment.SELECTED_DATE)!!
                singleRowCalendarLumperSheet.select(datePosition)
            }
            if (savedInstanceState.containsKey(SHEET_SUBMITTED)) {
                sheetSubmitted = savedInstanceState.getBoolean(SHEET_SUBMITTED)!!
            }
            if (savedInstanceState.containsKey(TEMP_LUMPER)) {
                tempLumperIds = savedInstanceState.getStringArrayList(TEMP_LUMPER)!!

            }
            if(savedInstanceState.containsKey(DATE)) {
                selectedDate = savedInstanceState.getSerializable(DATE) as Date
            }
            if (savedInstanceState.containsKey(LUMPER_INFO_LIST)) {
                lumperInfoList = savedInstanceState.getParcelableArrayList(LUMPER_INFO_LIST)!!
                showLumperSheetData(
                    lumperInfoList,
                    sheetSubmitted,
                    selectedDate,
                    tempLumperIds
                )
            }
            if (savedInstanceState.containsKey(DATE_SELECTED)) {
                dateString = savedInstanceState.getString(DATE_SELECTED)!!
                showDateString(dateString!!)
            }

        } ?: run {
            isSavedState=false
            singleRowCalendarLumperSheet.select(availableDates.size - 1)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        lumperSheetPresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(LUMPER_INFO_LIST, lumperInfoList)
        outState.putBoolean(SHEET_SUBMITTED, sheetSubmitted)
        outState.putStringArrayList(TEMP_LUMPER, tempLumperIds)
        outState.putSerializable(DATE, selectedDate)
        outState.putString(DATE_SELECTED, dateString)
        outState.putInt(SELECTED_DATE,datePosition)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            lumperSheetPresenter.getLumpersSheetByDate(Date(selectedTime))
        }
    }

    private fun invalidateEmptyView() {
        if (lumperSheetAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (lumperSheetAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_lumper_sheet_info_message)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_lumper_sheet_info_message)
        }
    }

    private fun showConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(
            getString(R.string.submit_lumper_sheet_alert_message), fragmentActivity!!, object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    lumperSheetPresenter.initiateSheetSubmission(Date(selectedTime))
                }

                override fun onCancelClick() {
                }
            })
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(fragmentActivity!!)
                }
                buttonSubmit.id -> showConfirmationDialog()
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumperSheetAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showDateString(dateString: String) {
        this.dateString=dateString
        textViewDate.text = dateString
    }

    override fun showLumperSheetData(mlumperInfoList: ArrayList<LumpersInfo>, msheetSubmitted: Boolean, mselectedDate: Date, mtempLumperIds: ArrayList<String>) {
        lumperInfoList=mlumperInfoList
        sheetSubmitted=msheetSubmitted
        selectedDate=mselectedDate
        tempLumperIds=mtempLumperIds

        selectedTime = selectedDate.time

        var isSignatureLeft = 0
        for (lumperInfo in lumperInfoList) {
            if (!ValueUtils.getDefaultOrValue(lumperInfo.sheetSigned)) {
                isSignatureLeft++
            }
        }

        if ((DateUtils.isCurrentDate(selectedTime) && lumperInfoList.size > 0)) {
            buttonSubmit.visibility = View.VISIBLE
            buttonSubmit.isEnabled = !sheetSubmitted && isSignatureLeft == 0
            if (sheetSubmitted) {
                buttonSubmit.text = getText(R.string.sheet_submitted)
            } else {
                buttonSubmit.text = getText(R.string.submit)
            }
        } else {
            buttonSubmit.visibility = View.GONE
        }

        lumperSheetAdapter.updateLumperSheetData(lumperInfoList, tempLumperIds)
        if (lumperInfoList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpersSheet.visibility = View.VISIBLE
        } else {
            recyclerViewLumpersSheet.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun sheetSubmittedSuccessfully() {
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.lumper_sheet_success_message), fragmentActivity!!, object : CustomDialogListener {
            override fun onConfirmClick() {
                lumperSheetPresenter.getLumpersSheetByDate(Date(selectedTime))
            }
        })
    }

    /** Adapter Listeners */
    override fun onItemClick(lumperInfo: LumpersInfo) {
        val bundle = Bundle()
        bundle.putParcelable(ARG_LUMPER_INFO, lumperInfo)
        bundle.putLong(ScheduleFragment.ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        startIntent(LumperWorkDetailActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    /** Calendar Listeners */
    override fun onSelectCalendarDate(
        date: Date,
        selected: Boolean,
        position: Int
    ) {
        if (!isSavedState)
        lumperSheetPresenter.getLumpersSheetByDate(date)
        isSavedState=false
        datePosition=position
    }
}