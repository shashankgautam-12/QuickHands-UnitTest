package com.quickhandslogistics.contracts.customerSheet

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.data.customerSheet.CustomerSheetScheduleDetails
import java.util.*

class CustomerSheetContract {
    interface Model {
        fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun fetchCustomerSheetList(selectedDate: Date, onFinishedListener: OnFinishedListener)
        fun saveCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessFetchCustomerSheet(customerSheetListAPIResponse: CustomerSheetListAPIResponse, selectedDate: Date)
            fun onSuccessGetHeaderInfo(companyName: String, date: String)
            fun onSuccessSaveCustomerSheet()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showCustomerSheets(scheduleDetails: CustomerSheetScheduleDetails, customerSheet: CustomerSheetData?, selectedDate: Date)
        fun showHeaderInfo(companyName: String, date: String)
        fun customerSavedSuccessfully()

        interface OnFragmentInteractionListener {
            fun saveCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String)
            fun saveSateCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun getCustomerSheetByDate(date: Date)
        fun saveCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String)
    }
}