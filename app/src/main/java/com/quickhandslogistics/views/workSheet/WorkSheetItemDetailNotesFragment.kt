package com.quickhandslogistics.views.workSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import kotlinx.android.synthetic.main.fragment_work_sheet_item_detail_notes.*

class WorkSheetItemDetailNotesFragment : BaseFragment(), View.OnClickListener {

    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? = null

    private var workItemDetail: WorkItemDetail? = null

    companion object {
        @JvmStatic
        fun newInstance() = WorkSheetItemDetailNotesFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is WorkSheetItemDetailContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = activity as WorkSheetItemDetailContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSubmit.setOnClickListener(this)
    }

    fun showNotesData(workItemDetail: WorkItemDetail) {
        this.workItemDetail = workItemDetail

        if (!workItemDetail.notesQHLCustomer.isNullOrEmpty() && workItemDetail.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE) {
            editTextQHLCustomerNotes.setText(workItemDetail.notesQHLCustomer)
        }

        if (!workItemDetail.notesQHL.isNullOrEmpty() && workItemDetail.notesQHL != AppConstant.NOTES_NOT_AVAILABLE) {
            editTextQHLNotes.setText(workItemDetail.notesQHL)
        }

        workItemDetail.status?.let { status ->
            if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED || status == AppConstant.WORK_ITEM_STATUS_CANCELLED) {
                editTextQHLCustomerNotes.isEnabled = false
                editTextQHLNotes.isEnabled = false
                buttonSubmit.visibility = View.GONE
            } else {
                editTextQHLCustomerNotes.isEnabled = true
                editTextQHLNotes.isEnabled = true
                buttonSubmit.visibility = View.VISIBLE
            }
        }
    }

    private fun saveWorkItemNotes() {
        CustomProgressBar.getInstance().showWarningDialog(activityContext = fragmentActivity!!, listener = object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                workItemDetail?.let {
                    val notesQHLCustomer = editTextQHLCustomerNotes.text.toString()
                    val notesQHL = editTextQHLNotes.text.toString()

                    onFragmentInteractionListener?.updateWorkItemNotes(notesQHLCustomer, notesQHL)
                }
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonSubmit.id -> saveWorkItemNotes()
            }
        }
    }
}