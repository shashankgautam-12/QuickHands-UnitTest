package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails

class WorkSheetItemDetailContract {
    interface Model {
        fun fetchWorkItemDetail(workItemId: String, onFinishedListener: OnFinishedListener)
        fun changeWorkItemStatus(
            workItemId: String,
            status: String,
            onFinishedListener: OnFinishedListener
        )

        fun updateWorkItemNotes(
            workItemId: String,
            notesQHLCustomer: String,
            notesQHL: String,
            onFinishedListener: OnFinishedListener
        )

        fun removeLumper(lumperIds: ArrayList<String>, tempLumperIds: ArrayList<String>, workItemId: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: WorkItemDetailAPIResponse)
            fun onSuccessChangeStatus(workItemId: String)
            fun onSuccessUpdateNotes(workItemId: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showWorkItemDetail(
            container: WorkItemContainerDetails,
            lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?
        )

        fun statusChangedSuccessfully()
        fun notesSavedSuccessfully()
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onSelectStatus(status: String)
        }

        interface OnFragmentInteractionListener {
            fun fetchWorkItemDetail(changeResultCode: Boolean)
            fun updateWorkItemNotes(notesQHLCustomer: String, notesQHL: String)
            fun dataChanged(isChanged: Boolean)
            fun removeLumperFromSchedule(lumperIds: ArrayList<String>, tempLumperIds: ArrayList<String>)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchWorkItemDetail(workItemId: String)
        fun changeWorkItemStatus(workItemId: String, status: String)
        fun updateWorkItemNotes(workItemId: String, notesQHLCustomer: String, notesQHL: String)
        fun removeLumper(lumperIds: ArrayList<String>, tempLumperIds: ArrayList<String>, workItemId: String)
    }
}