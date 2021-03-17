package com.quickhandslogistics.utils

import android.content.res.Resources
import android.text.Spanned
import android.widget.RelativeLayout
import android.widget.TextView
import com.quickhandslogistics.R
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumperSheet.LumperDaySheet
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.data.schedule.ScheduleDetailData
import com.quickhandslogistics.data.schedule.ScheduleWorkItem
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.scheduleTime.RequestLumpersRecord
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.data.workSheet.WorkItemScheduleDetails
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

object ScheduleUtils {

    fun getAllAssignedLumpersList(workItems: List<WorkItemDetail>?): ArrayList<EmployeeData> {
        val assignedLumpers = ArrayList<EmployeeData>()
        workItems?.let {
            for (workItem in workItems) {
                workItem.assignedLumpersList?.let { assignedLumpersList ->
                    assignedLumpers.addAll(assignedLumpersList)
                }
            }
        }
        return assignedLumpers
    }

    fun getScheduleTypeName(workItems: List<WorkItemDetail>?, scheduleTypeNames: String, scheduleType: String): String {
        var scheduleTypes = scheduleTypeNames

        if (!workItems.isNullOrEmpty()) {
            if (scheduleTypes.isNotEmpty()) {
                scheduleTypes += ", "
            }
            scheduleTypes += scheduleType
        }
        return scheduleTypes
    }

    fun getWorkItemTypeDisplayName(workItemType: String?, resources: Resources): String {
        var workItemTypeDisplayName = ""
        workItemType?.let {
            workItemTypeDisplayName = when (workItemType) {
                "LIVE" -> resources.getString(R.string.live_loads)
                "DROP" -> resources.getString(R.string.drops)
                else -> resources.getString(R.string.out_bounds)
            }
        }
        return workItemTypeDisplayName
    }

    fun getWorkItemTypeDisplay(workItemType: String?, resources: Resources): String {
        var workItemTypeDisplayName = ""
        workItemType?.let {
            workItemTypeDisplayName = when (workItemType) {
                "LIVE" -> resources.getString(R.string.live_load)
                "DROP" -> resources.getString(R.string.drop)
                else -> resources.getString(R.string.out_bound)
            }
        }
        return workItemTypeDisplayName
    }

    fun getWorkItemTypeCounts(list: ArrayList<WorkItemDetail>): Triple<Int, Int, Int> {
        var liveLoadsCount = 0
        var dropsCount = 0
        var outBoundsCount = 0

        for (workItemDetail in list) {
            when (workItemDetail.type) {
                "LIVE" -> liveLoadsCount++
                "DROP" -> dropsCount++
                else -> outBoundsCount++
            }
        }
        return Triple(liveLoadsCount, dropsCount, outBoundsCount)
    }

    fun changeStatusUIByValue(
        resources: Resources, status: String?, textViewStatus: TextView,
        relativeLayoutSide: RelativeLayout? = null, isEditable: Boolean = false
    ) {
        when (status) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED -> {
                textViewStatus.text = resources.getString(R.string.scheduled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.WORK_ITEM_STATUS_ON_HOLD -> {
                textViewStatus.text = resources.getString(R.string.on_hold)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_on_hold)
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> {
                textViewStatus.text = resources.getString(R.string.in_progress)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_in_progress)
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.WORK_ITEM_STATUS_CANCELLED -> {
                textViewStatus.text = resources.getString(R.string.cancelled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_cancelled)
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.WORK_ITEM_STATUS_COMPLETED -> {
                textViewStatus.text = resources.getString(R.string.completed)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_completed)
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.VIEW_DETAILS -> {
                textViewStatus.text = resources.getString(R.string.view_details)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
                setStatusViewEditable(isEditable, textViewStatus)
            }
        }
    }

    private fun setStatusViewEditable(isEditable: Boolean, textViewStatus: TextView) {
        if (isEditable) {
            textViewStatus.isClickable = true
            textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0)
        } else {
            textViewStatus.isClickable = false
            textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    fun createStatusList(resources: Resources, status: String): LinkedHashMap<String, String> {
        val statusList: LinkedHashMap<String, String> = LinkedHashMap()
        when (status) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED -> {
                statusList[resources.getString(R.string.scheduled)] = AppConstant.WORK_ITEM_STATUS_SCHEDULED
                statusList[resources.getString(R.string.in_progress)] = AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
                statusList[resources.getString(R.string.on_hold)] = AppConstant.WORK_ITEM_STATUS_ON_HOLD
                statusList[resources.getString(R.string.cancelled)] = AppConstant.WORK_ITEM_STATUS_CANCELLED
            }
            AppConstant.WORK_ITEM_STATUS_ON_HOLD -> {
                statusList[resources.getString(R.string.in_progress)] = AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
                statusList[resources.getString(R.string.on_hold)] = AppConstant.WORK_ITEM_STATUS_ON_HOLD
                statusList[resources.getString(R.string.cancelled)] = AppConstant.WORK_ITEM_STATUS_CANCELLED
                statusList[resources.getString(R.string.completed)] = AppConstant.WORK_ITEM_STATUS_COMPLETED
            }
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> {
                statusList[resources.getString(R.string.in_progress)] = AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
                statusList[resources.getString(R.string.on_hold)] = AppConstant.WORK_ITEM_STATUS_ON_HOLD
                statusList[resources.getString(R.string.cancelled)] = AppConstant.WORK_ITEM_STATUS_CANCELLED
                statusList[resources.getString(R.string.completed)] = AppConstant.WORK_ITEM_STATUS_COMPLETED
            }
        }
        return statusList
    }

    fun sortEmployeesList(employeesList: ArrayList<EmployeeData>?, isTemporaryLumpers: Boolean = false): ArrayList<EmployeeData> {
        return if (!employeesList.isNullOrEmpty()) {
            employeesList.sortWith(Comparator { lumper1, lumper2 ->
                if (!lumper1.firstName.isNullOrEmpty() && !lumper2.firstName.isNullOrEmpty()) {
                    lumper1.firstName?.toLowerCase(Locale.getDefault())!!.compareTo(lumper2.firstName?.toLowerCase(Locale.getDefault())!!)
                } else {
                    0
                }
            })

            if (isTemporaryLumpers) {
                val iterate = employeesList.listIterator()
                while (iterate.hasNext()) {
                    val oldValue = iterate.next()
                    oldValue.isTemporaryAssigned = true
                    iterate.set(oldValue)
                }
            }
            employeesList
        } else ArrayList()
    }


    fun sortEmployeesAttendanceList(employeesList: ArrayList<LumperAttendanceData>?, isTemporaryLumpers: Boolean = false): ArrayList<LumperAttendanceData> {
        return if (!employeesList.isNullOrEmpty()) {
            employeesList.sortWith(Comparator { lumper1, lumper2 ->
                if (!lumper1.firstName.isNullOrEmpty() && !lumper2.firstName.isNullOrEmpty()) {
                    lumper1.firstName?.toLowerCase(Locale.getDefault())!!.compareTo(lumper2.firstName?.toLowerCase(Locale.getDefault())!!)
                } else {
                    0
                }
            })

            if (isTemporaryLumpers) {
                val iterate = employeesList.listIterator()
                while (iterate.hasNext()) {
                    val oldValue = iterate.next()
                    oldValue.isTemporaryAssigned = true
                    iterate.set(oldValue)
                }
            }
            employeesList
        } else ArrayList()
    }

    fun getWholeScheduleStatus(scheduleTypes: ScheduleDetail.ScheduleTypes): String {
        var scheduledCount = 0
        var inProgressCount = 0
        var onHoldCount = 0
        var cancelledCount = 0
        var completedCount = 0

        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(scheduleTypes.liveLoads!!)
        allWorkItems.addAll(scheduleTypes.outbounds!!)
        allWorkItems.addAll(scheduleTypes.drops!!)

        for (workItem in allWorkItems) {
            when (workItem.status) {
                AppConstant.WORK_ITEM_STATUS_SCHEDULED -> scheduledCount++
                AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> inProgressCount++
                AppConstant.WORK_ITEM_STATUS_ON_HOLD -> onHoldCount++
                AppConstant.WORK_ITEM_STATUS_CANCELLED -> cancelledCount++
                AppConstant.WORK_ITEM_STATUS_COMPLETED -> completedCount++
            }
        }

        return if (inProgressCount > 0 || onHoldCount > 0) {
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
        } else if (scheduledCount > 0) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED
        } else if (cancelledCount > 0 && completedCount == 0) {
            AppConstant.WORK_ITEM_STATUS_CANCELLED
        } else {
            AppConstant.WORK_ITEM_STATUS_COMPLETED
        }
    }

    fun getFilledBuildingParametersCount(buildingOps: HashMap<String, String>?): Int {
        var count = 0

        buildingOps?.let {
            for (key in buildingOps.keys) {
                val value = buildingOps[key]
                if (!value.isNullOrEmpty()) {
                    count++
                }
            }
        }

        return count
    }

    fun getBuildingParametersList(sharedPref: SharedPref): ArrayList<String> {
        val parameters = ArrayList<String>()
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        leadProfile?.buildingDetailData?.let { buildingDetailData ->
            if (!buildingDetailData.parameters.isNullOrEmpty()) {
                parameters.addAll(buildingDetailData.parameters!!)
            }
        }
        return parameters
    }

    fun getShiftDetailString(leadProfile: LeadProfileData?): String {
        var shiftName = ""
        leadProfile?.shift?.let { name ->
            shiftName = name.capitalize()
        }

        return " ${ResourceManager.getInstance().getString(R.string.shift_bold)} $shiftName  "
    }


    fun getOldShiftDetailString(leadProfile: LeadProfileData?): String {
        var shiftName = ""
        var shiftStartTime = ""
        var shiftEndTime = ""
        leadProfile?.shift?.let { name ->
            shiftName = name.capitalize()
            val shiftDetail = when (leadProfile.shift) {
                AppConstant.EMPLOYEE_SHIFT_MORNING -> {
                    leadProfile.buildingDetailData?.morningShift
                }
                AppConstant.EMPLOYEE_SHIFT_SWING -> {
                    leadProfile.buildingDetailData?.swingShift
                }
                AppConstant.EMPLOYEE_SHIFT_NIGHT -> {
                    leadProfile.buildingDetailData?.nightShift
                }
                else -> null
            }
            shiftDetail?.let {
                shiftStartTime = DateUtils.convertMillisecondsToTimeString(shiftDetail.startTime!!)
                shiftEndTime = DateUtils.convertMillisecondsToTimeString(shiftDetail.endTime!!)
            }
        }
        return "$shiftName ($shiftStartTime - $shiftEndTime)"
    }

     fun getSortRequestLumper(records: ArrayList<RequestLumpersRecord>): ArrayList<RequestLumpersRecord> {
        var pendingRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        var completedRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        var rejectedRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        var cancelRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        var sortedlRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        records.forEach {
            when {
                it.requestStatus.equals(AppConstant.REQUEST_LUMPERS_STATUS_PENDING) -> {
                    pendingRecords.add(it)
                }
                it.requestStatus.equals(AppConstant.REQUEST_LUMPERS_STATUS_APPROVED) -> {
                    completedRecords.add(it)
                }
                it.requestStatus.equals(AppConstant.REQUEST_LUMPERS_STATUS_REJECTED) -> {
                    rejectedRecords.add(it)
                }
                it.requestStatus.equals(AppConstant.REQUEST_LUMPERS_STATUS_CANCELLED) -> {
                    cancelRecords.add(it)
                }
            }
        }

        sortedlRecords.addAll(getSortedDate(pendingRecords))
        sortedlRecords.addAll(getSortedDate(completedRecords))
        sortedlRecords.addAll(getSortedDate(rejectedRecords))
        sortedlRecords.addAll(getSortedDate(cancelRecords))
        return sortedlRecords
    }

    private fun getSortedDate(records: ArrayList<RequestLumpersRecord>): ArrayList<RequestLumpersRecord> {
        records.sortWith(Comparator { data: RequestLumpersRecord, t1: RequestLumpersRecord ->
            (data.createdAt)?.compareTo(t1.createdAt!!)!!
        })
        return records
    }

    fun calculatePercent(lumperCase: String, totalCases: String): Double {
        return (lumperCase.toDouble() / totalCases.toDouble()) * 100
    }

    fun getCancelHeaderDetails(scheduleDetails: ScheduleTimeDetail, rawString:String): Spanned? {
        var lumperName =String.format( "%s %s", scheduleDetails.lumperInfo!!.firstName,scheduleDetails.lumperInfo!!.lastName)
        var lumperScheduleTime =DateUtils.changeDateString(DateUtils.PATTERN_API_RESPONSE ,DateUtils.PATTERN_NORMAL,
            scheduleDetails.reportingTimeAndDay!!
        )
        var formetString= String.format(rawString, lumperName, lumperScheduleTime)
        return UIUtils.getSpannedText(formetString)
    }

    fun scheduleTypeNote(workItemDetail: WorkItemScheduleDetails?, resources: Resources): String {
        return when(workItemDetail?.type) {
            AppConstant.SCHEDULE_WORK_ITEM_WEEKLY -> {
                resources.getString(R.string.weekly)
            }
            AppConstant.SCHEDULE_WORK_ITEM_MONTHLY -> {
                resources.getString(R.string.monthly)
            }
            AppConstant.SCHEDULE_WORK_ITEM_CUSTOM -> {
                resources.getString(R.string.custom_s)
            }
            AppConstant.SCHEDULE_WORK_ITEM_DAY -> {
                resources.getString(R.string.daily)
            }
            else -> ""
        }

    }

    fun scheduleNotePopupTitle(workItemDetail: WorkItemScheduleDetails?, resources: Resources): String {
        return when (workItemDetail?.type){
            AppConstant.SCHEDULE_WORK_ITEM_WEEKLY -> {
                resources.getString(R.string.weekly_scheduled)
            }
           AppConstant.SCHEDULE_WORK_ITEM_MONTHLY -> {
                resources.getString(R.string.monthly_scheduled)
            }
            AppConstant.SCHEDULE_WORK_ITEM_CUSTOM -> {
                resources.getString(R.string.custom_scheduled)
            }
            AppConstant.SCHEDULE_WORK_ITEM_DAY -> {
                resources.getString(R.string.daily_scheduled)
            }
            else -> ""
        }
    }

    fun getGroupNoteList(workItemData: WorkSheetListAPIResponse.Data): Triple<Pair<ArrayList<String>,ArrayList<String>>, ArrayList<String>, ArrayList<String>>  {
        val dailyNoteList: ArrayList<String> = ArrayList()
        val weeklyNoteList: ArrayList<String> = ArrayList()
        val monthlyNoteList: ArrayList<String> = ArrayList()
        val customNoteList: ArrayList<String> = ArrayList()
        val workItemDetail: ArrayList<WorkItemDetail> = ArrayList()

        workItemData?.let{
            workItemDetail.addAll(it.cancelled!!)
            workItemDetail.addAll(it.onHold!!)
            workItemDetail.addAll(it.inProgress!!)
            workItemDetail.addAll(it.scheduled!!)
            workItemDetail.addAll(it.completed!!)
        }

        workItemDetail.forEach { workItemData ->
            workItemData.schedule?.let {
                when (it.type) {
                    AppConstant.SCHEDULE_WORK_ITEM_DAY-> {
                        if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                            if (!dailyNoteList.contains(it.scheduleNote!!))
                                dailyNoteList.add(it.scheduleNote!!)
                    }
                   AppConstant.SCHEDULE_WORK_ITEM_WEEKLY -> {
                        if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                            if (!weeklyNoteList.contains(it.scheduleNote!!))
                                weeklyNoteList.add(it.scheduleNote!!)
                    }
                   AppConstant.SCHEDULE_WORK_ITEM_MONTHLY -> {
                        if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                            if (!monthlyNoteList.contains(it.scheduleNote!!))
                                monthlyNoteList.add(it.scheduleNote!!)
                    }
                    AppConstant.SCHEDULE_WORK_ITEM_CUSTOM-> {
                        if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                            if (!customNoteList.contains(it.scheduleNote!!))
                                customNoteList.add(it.scheduleNote!!)
                    }
                }
            }
        }
        return Triple(Pair(dailyNoteList, customNoteList),weeklyNoteList ,monthlyNoteList)
    }

    fun getGroupNoteListWorkSchedule(workItemData: ScheduleDetailData?): Triple<Pair<ArrayList<String>,ArrayList<String>>, ArrayList<String>, ArrayList<String>>  {
        val dailyNoteList: ArrayList<String> = ArrayList()
        val weeklyNoteList: ArrayList<String> = ArrayList()
        val monthlyNoteList: ArrayList<String> = ArrayList()
        val customNoteList: ArrayList<String> = ArrayList()
        val workItemDetail: ArrayList<WorkItemContainerDetails> = ArrayList()

        workItemData?.let{
            workItemDetail.addAll(it?.liveLoads!!)
            workItemDetail.addAll(it?.outbounds!!)
            workItemDetail.addAll(it?.drops!!)
        }

        workItemDetail.forEach{workItemData ->
            workItemData.schedule?.let {
                when (it.type) {
                    AppConstant.SCHEDULE_WORK_ITEM_DAY -> {
                        if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                            if (!dailyNoteList.contains(it.scheduleNote!!))
                                dailyNoteList.add(it.scheduleNote!!)
                    }
                    AppConstant.SCHEDULE_WORK_ITEM_WEEKLY -> {
                        if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                            if (!weeklyNoteList.contains(it.scheduleNote!!))
                                weeklyNoteList.add(it.scheduleNote!!)
                    }
                    AppConstant.SCHEDULE_WORK_ITEM_MONTHLY  -> {
                        if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                            if (!monthlyNoteList.contains(it.scheduleNote!!))
                                monthlyNoteList.add(it.scheduleNote!!)
                    }
                    AppConstant.SCHEDULE_WORK_ITEM_CUSTOM -> {
                        if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                            if (!customNoteList.contains(it.scheduleNote!!))
                                customNoteList.add(it.scheduleNote!!)
                    }
                }
            }
        }
        return Triple(Pair(dailyNoteList, customNoteList),weeklyNoteList ,monthlyNoteList)
    }

    fun getBuildingParametersList(buildingDetailData: BuildingDetailData ?): ArrayList<String> {
        val parameters = ArrayList<String>()

        buildingDetailData?.let { buildingDetailData ->
            if (!buildingDetailData.parameters.isNullOrEmpty()) {
                parameters.addAll(buildingDetailData.parameters!!)
            }
        }
        return parameters
    }

    fun getFilledBuildingParametersCounts(workItemDetail: ScheduleWorkItem): Int {
        var count = 0
        val parameters = ScheduleUtils.getBuildingParametersList(workItemDetail.buildingDetailData)

        workItemDetail.buildingOps?.let {
            for (key in it.keys) {
                val value = it[key]
                if (!value.isNullOrEmpty() && parameters.contains(key)) {
                    count++
                }
            }
        }
        return count
    }

    fun getFilteredLumperWorkList(lumperDaySheetList: ArrayList<LumperDaySheet>): ArrayList<LumperDaySheet> {
        val outBound: ArrayList<LumperDaySheet> = ArrayList()
        val live: ArrayList<LumperDaySheet> =ArrayList()
        val drop: ArrayList<LumperDaySheet> =ArrayList()
        val filterLumperDaySheetList: ArrayList<LumperDaySheet> =ArrayList()

        lumperDaySheetList.forEach {
            it.workItemDetail?.workItemType.let { type->
                when (type){
                    AppConstant.WORKSHEET_WORK_ITEM_LIVE ->{live.add(it) }
                    AppConstant.WORKSHEET_WORK_ITEM_OUTBOUND ->{outBound.add(it) }
                    else -> {drop.add(it)}
                }
            }

        }

        filterLumperDaySheetList.addAll(outBound)
        filterLumperDaySheetList.addAll(live)
        filterLumperDaySheetList.addAll(drop)

        return filterLumperDaySheetList

    }


    fun sortAccordingly(parameters: ArrayList<String>): ArrayList<String> {
        val sortedPerameter : ArrayList<String> = ArrayList()
        val sortedSubPerameter : ArrayList<String> = ArrayList()

        if (parameters.contains("Door")){
            sortedPerameter.add("Door")
        }

        if (parameters.contains("Container Number")){
            sortedPerameter.add("Container Number")
        }

        if (parameters.contains("Cases")){
            sortedPerameter.add("Cases")
        }

        if (parameters.contains("Items")){
            sortedPerameter.add("Items")
        }

        if (parameters.contains("Sort")){
            sortedPerameter.add("Sort")
        }

        for (it in parameters) {
            if (!it.equals("Door", ignoreCase = true) && !it.equals("container number", ignoreCase = true) && !it.equals("Cases", ignoreCase = true) && !it.equals("items",ignoreCase = true)&& !it.equals("sort",ignoreCase = true))
                sortedSubPerameter.add(it)
        }

        sortedSubPerameter.sortWith(Comparator { value1: String, value2: String ->
            value1.toLowerCase().compareTo(value2.toLowerCase())
        })

        sortedPerameter.addAll(sortedSubPerameter)

        return sortedPerameter
    }


    fun getSortedAttendenceData(lumperAttendanceList: ArrayList<LumperAttendanceData>): ArrayList<LumperAttendanceData> {
        val shortedList: ArrayList<LumperAttendanceData> = ArrayList()
        val shortedPunchInOutList: ArrayList<LumperAttendanceData> = ArrayList()
        val shortedAbsentList: ArrayList<LumperAttendanceData> = ArrayList()

        lumperAttendanceList.forEach {
            if (!it.attendanceDetail?.eveningPunchOut.isNullOrEmpty() && !it.attendanceDetail?.morningPunchIn.isNullOrEmpty()){
                shortedPunchInOutList.add(it)
            }else if (!it.attendanceDetail?.morningPunchIn.isNullOrEmpty()){
                shortedList.add(it)
            }else{
                shortedAbsentList.add(it)
            }
        }

        sortEmployeesAttendanceList(shortedList)
        shortedList.addAll(sortEmployeesAttendanceList(shortedPunchInOutList))
        shortedList.addAll(sortEmployeesAttendanceList(shortedAbsentList))

        return shortedList
    }


    fun sortLumperList(lumperList: ArrayList<LumpersInfo>?): ArrayList<LumpersInfo> {
        return if (!lumperList.isNullOrEmpty()) {
            lumperList.sortWith(Comparator { lumper1, lumper2 ->
                if (!lumper1.lumperName.isNullOrEmpty() && !lumper2.lumperName.isNullOrEmpty()) {
                    lumper1.lumperName?.toLowerCase(Locale.getDefault())!!.compareTo(lumper2.lumperName?.toLowerCase(Locale.getDefault())!!)
                } else {
                    0
                }
            })

            lumperList
        } else ArrayList()
    }
}
