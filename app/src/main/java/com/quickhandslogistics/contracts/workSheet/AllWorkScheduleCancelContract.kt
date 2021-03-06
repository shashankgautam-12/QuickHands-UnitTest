package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.common.AllLumpersResponse
import com.quickhandslogistics.data.lumpers.EmployeeData

class AllWorkScheduleCancelContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)
        fun cancelAllWorkSchedules(
            selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String, onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchLumpers(response: AllLumpersResponse)
            fun onSuccessCancelWorkSchedules()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)
        fun cancellingWorkScheduleFinished()
        fun showLoginScreen()
        interface OnAdapterItemClickListener {
            fun onLumperSelectionChanged()
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList()
        fun initiateCancellingWorkSchedules(selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String)
    }
}