package com.quickhandslogistics.modified.contracts.buildingOperations

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.buildingOperations.BuildingOperationAPIResponse

class BuildingOperationsContract {
    interface Model {
        fun fetchBuildingOperationDetails(workItemId: String, onFinishedListener: OnFinishedListener)

        fun saveBuildingOperationDetails(workItemId: String, data: HashMap<String, String>, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessGetBuildingOperations(buildingOperationAPIResponse: BuildingOperationAPIResponse)
            fun onSuccessSaveBuildingOperations()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showBuildingOperationsData(data: HashMap<String, String>?)
        fun buildingOperationsDataSaved()
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchBuildingOperationDetails(workItemId: String)
        fun saveBuildingOperationsData(workItemId: String, data: HashMap<String, String>)
    }
}