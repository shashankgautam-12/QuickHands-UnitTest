package com.quickhandslogistics.data.schedule

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.lumperSheet.CorrectionRequest
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.workSheet.WorkItemScheduleDetails

class WorkItemDetail() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("workItemType")
    @Expose
    var workItemType: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("sequence")
    @Expose
    var sequence: Int? = null

    @SerializedName("quantity")
    @Expose
    var quantity: Int? = null

    @SerializedName("label")
    @Expose
    var label: String? = null

    @SerializedName("origin")
    @Expose
    var origin: String? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: String? = null

    @SerializedName("startTime")
    @Expose
    var startTime: String? = null

    @SerializedName("scheduleIdentity")
    @Expose
    var scheduleIdentity: String? = null

    @SerializedName("scheduledFrom")
    @Expose
    var scheduledFrom: String? = null
    @SerializedName("scheduleNote")
    @Expose
    var scheduleNote: String? = null

    @SerializedName("endDateForThisWorkItem")
    @Expose
    var endDateForThisWorkItem: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("notesQHL")
    @Expose
    var notesQHL: String? = null

    @SerializedName("notesQHLCustomer")
    @Expose
    var notesQHLCustomer: String? = null

    @SerializedName("isScheduledByLead")
    @Expose
    var isScheduledByLead: Boolean? = null

    @SerializedName("scheduleForWeek")
    @Expose
    var scheduleForWeek: Boolean? = null

    @SerializedName("scheduleForMonth")
    @Expose
    var scheduleForMonth: Boolean? = null

    @SerializedName("specificDates")
    @Expose
    var specificDates: List<Any>? = null

    @SerializedName("isCompleted")
    @Expose
    var isCompleted: Boolean? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("numberOfDrops")
    @Expose
    var numberOfDrops: Int? = null

    @SerializedName("buildingThisWorkItemAssignedTo")
    @Expose
    var buildingDetailData: String? = null

    @SerializedName("schedule")
    @Expose
    var schedule: WorkItemScheduleDetails? = null

    @SerializedName("lumperThisWorkItemAssignedTo")
    @Expose
    var assignedLumpersList: ArrayList<EmployeeData>? = null
        get() = ScheduleUtils.sortEmployeesList(field)

    @SerializedName("oldWork")
    @Expose
    var oldWork: WorkItemDetail? = null

    @SerializedName("lumperAttendance")
    @Expose
    var attendanceDetail: AttendanceDetail? = null

    @SerializedName("corrections")
    @Expose
    var corrections: CorrectionRequest? = null

    @SerializedName("buildingOps")
    @Expose
    var buildingOps: HashMap<String, String>? = null

    @SerializedName("buildingParams")
    @Expose
    var buildingParams: ArrayList<String>? = null

    @Transient
    var containerNumber = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        workItemType = parcel.readString()
        type = parcel.readString()
        label = parcel.readString()
        sequence = parcel.readValue(Int::class.java.classLoader) as? Int
        quantity = parcel.readValue(Int::class.java.classLoader) as? Int
        createdBy = parcel.readString()
        startTime = parcel.readString()
        scheduleIdentity = parcel.readString()
        scheduledFrom = parcel.readString()
        scheduleNote = parcel.readString()
        origin = parcel.readString()
        endDateForThisWorkItem = parcel.readString()
        status = parcel.readString()
        notesQHL = parcel.readString()
        notesQHLCustomer = parcel.readString()
        isScheduledByLead = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        scheduleForWeek = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        scheduleForMonth = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isCompleted = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
        numberOfDrops = parcel.readValue(Int::class.java.classLoader) as? Int
        buildingDetailData = parcel.readString()
        schedule = parcel.readParcelable(BuildingDetailData::class.java.classLoader)
        assignedLumpersList = parcel.createTypedArrayList(EmployeeData)
        attendanceDetail = parcel.readParcelable(AttendanceDetail::class.java.classLoader)
        corrections = parcel.readParcelable(CorrectionRequest::class.java.classLoader)
        oldWork = parcel.readParcelable(WorkItemDetail::class.java.classLoader)
        buildingParams = parcel.createStringArrayList()
//        buildingOps = HashMap()
//        readFromParcel(parcel)
    }

    private fun readFromParcel(parcel: Parcel) {
        val count = parcel.readInt()
        for (i in 0 until count) {
            val value1 = parcel.readString()
            val value2 = parcel.readString()
            if (!value1.isNullOrEmpty() && !value2.isNullOrEmpty()) {
                buildingOps?.put(value1, value2)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(workItemType)
        parcel.writeString(type)
        parcel.writeString(label)
        parcel.writeValue(sequence)
        parcel.writeValue(quantity)
        parcel.writeString(createdBy)
        parcel.writeString(startTime)
        parcel.writeString(scheduleIdentity)
        parcel.writeString(scheduledFrom)
        parcel.writeString(scheduleNote)
        parcel.writeString(endDateForThisWorkItem)
        parcel.writeString(status)
        parcel.writeString(notesQHL)
        parcel.writeString(origin)
        parcel.writeString(notesQHLCustomer)
        parcel.writeValue(isScheduledByLead)
        parcel.writeValue(scheduleForWeek)
        parcel.writeValue(scheduleForMonth)
        parcel.writeValue(isCompleted)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeStringList(buildingParams)
        parcel.writeValue(numberOfDrops)
        parcel.writeString(buildingDetailData)
        parcel.writeParcelable(schedule, flags)
        parcel.writeTypedList(assignedLumpersList)
        parcel.writeParcelable(attendanceDetail, flags)
        parcel.writeParcelable(oldWork, flags)
        parcel.writeParcelable(corrections, flags)
        /*buildingOps?.let { data ->
            parcel.writeInt(data.size)
            for (s in data.keys) {
                parcel.writeString(s)
                parcel.writeString(data[s])
            }
        }*/
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkItemDetail> {
        override fun createFromParcel(parcel: Parcel): WorkItemDetail {
            return WorkItemDetail(parcel)
        }

        override fun newArray(size: Int): Array<WorkItemDetail?> {
            return arrayOfNulls(size)
        }
    }
}