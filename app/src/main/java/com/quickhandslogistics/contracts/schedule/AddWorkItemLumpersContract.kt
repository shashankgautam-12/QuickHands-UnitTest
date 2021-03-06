package com.quickhandslogistics.contracts.schedule

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.common.AllLumpersResponse
import com.quickhandslogistics.data.lumpers.EmployeeData

class AddWorkItemLumpersContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)
        fun assignLumpersList(
            workItemId: String, workItemType: String, selectedLumperIdsList: ArrayList<String>,
            tempLumperIdsList: ArrayList<String>, onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchLumpers(response: AllLumpersResponse)
            fun onSuccessAssignLumpers()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(permanentLumpers: ArrayList<EmployeeData>, temporaryLumpers: ArrayList<EmployeeData>)
        fun lumperAssignmentFinished()
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList()
        fun initiateAssigningLumpers(selectedLumperIdsList: ArrayList<String>, tempLumperIdsList: ArrayList<String>, workItemId: String, workItemType: String)
    }
}