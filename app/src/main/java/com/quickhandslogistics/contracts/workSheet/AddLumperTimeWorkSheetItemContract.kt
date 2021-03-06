package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.workSheet.PauseTimeRequest

class AddLumperTimeWorkSheetItemContract {
    interface Model {
        fun saveLumperTimings(
            id: String,
            workItemId: String,
            selectedStartTime: Long,
            selectedEndTime: Long,
            breakTimeRequestList: ArrayList<PauseTimeRequest>,
            waitingTime: String,
            onFinishedListener1: Int,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun lumpersTimingSaved()
        fun showLoginScreen()

        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun saveLumperTimings(
            id: String,
            workItemId: String,
            selectedStartTime: Long,
            selectedEndTime: Long,
            breakTimeRequestList: ArrayList<PauseTimeRequest>,
            waitingTime: String,
            percentageTime: Int
        )
    }
}