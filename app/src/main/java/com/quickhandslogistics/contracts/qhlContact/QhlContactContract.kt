package com.quickhandslogistics.contracts.qhlContact

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.contracts.customerContact.CustomerContactContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.data.qhlContact.QhlContactListResponse
import com.quickhandslogistics.data.qhlContact.QhlOfficeInfo
import com.quickhandslogistics.data.qhlContact.QhlOfficeInfoResponse

interface QhlContactContract {

    interface Model {
        fun fetchQhlHeaderInfo(onFinishedListener: OnFinishedListener)
        fun fetchQhlContactList(onFinishedListener: OnFinishedListener)
        fun sendCustomerContactMessage(id: String, message: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: QhlContactListResponse)
            fun onSuccessGetHeaderInfo(leadProfileData: QhlOfficeInfoResponse?)
            fun onSuccessMessageSend(message: BaseResponse?)
        }
    }

    interface View : BaseContract.View {
        fun showQhlHeaderInfo(leadProfileData: QhlOfficeInfo?)
        fun showAPIErrorMessage(message: String)
        fun qhlContactList(employeeDataList: ArrayList<EmployeeData>)
        fun showLoginScreen()
        fun showSuccessMessageSend(message: String)

        interface OnAdapterItemClickListener {
            fun onItemClick(employeeData: EmployeeData)
            fun onPhoneViewClick(lumperName: String, phone: String)
            fun onEmailViewClick(lumperName: String, email: String)
            fun onChatViewClick(employeeData: EmployeeData)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchQhlContactList()
        fun sendCustomerContactMessage(id: String, message: String)
    }
}