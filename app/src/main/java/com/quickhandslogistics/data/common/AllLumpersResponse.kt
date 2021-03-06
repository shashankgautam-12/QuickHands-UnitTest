package com.quickhandslogistics.data.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.ScheduleUtils

class AllLumpersResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {
        @SerializedName("permanentLumpersAttendances")
        @Expose
        var permanentLumpersList: ArrayList<EmployeeData>? = null
            get() = ScheduleUtils.sortEmployeesList(field)

        @SerializedName("tempLumpersAttendances")
        @Expose
        var temporaryLumpers: ArrayList<EmployeeData>? = null
            get() = ScheduleUtils.sortEmployeesList(field, isTemporaryLumpers = true)
    }
}