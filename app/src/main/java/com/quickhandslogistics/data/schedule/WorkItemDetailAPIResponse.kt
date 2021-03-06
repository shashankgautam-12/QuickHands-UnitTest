package com.quickhandslogistics.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails

class WorkItemDetailAPIResponse : BaseResponse() {

    @SerializedName("data")
    @Expose
    var data: Data? = null


    inner class Data {
//        @SerializedName("workItem")
//        @Expose
//        var workItemDetail: ScheduleWorkItem? = null

        @SerializedName("lumpersTimeSchedule")
        @Expose
        var lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>? = null

        //
//        @SerializedName("tempLumperIds")
//        @Expose
//        val tempLumperIds: ArrayList<String>? = null
//            get() = if (!field.isNullOrEmpty()) field else ArrayList()//
//
//
        @SerializedName("buildingParams")
        @Expose
        val buildingParams: ArrayList<String>? = null
            get() = if (!field.isNullOrEmpty()) field else ArrayList()


        @SerializedName("container")
        @Expose
        var container: WorkItemContainerDetails? = null
    }
}