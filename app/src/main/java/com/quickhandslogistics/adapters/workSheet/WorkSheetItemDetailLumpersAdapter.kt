package com.quickhandslogistics.adapters.workSheet

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailLumpersContract
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.PauseTime
import com.quickhandslogistics.data.workSheet.PauseTimeRequest
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_DATE_DISPLAY_CUSTOMER_SHEET
import com.quickhandslogistics.utils.DateUtils.Companion.changeDateString
import com.quickhandslogistics.utils.DateUtils.Companion.convertDateStringToTime
import com.quickhandslogistics.utils.DateUtils.Companion.sharedPref
import com.quickhandslogistics.utils.ScheduleUtils.calculatePercent
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import com.quickhandslogistics.utils.ValueUtils.isNumeric
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_work_item_detail_lumper_time.view.*

class WorkSheetItemDetailLumpersAdapter(private val resources: Resources, private var onAdapterClick: WorkSheetItemDetailLumpersContract.View.OnAdapterItemClickListener) :
    Adapter<WorkSheetItemDetailLumpersAdapter.ViewHolder>() {

    private var workItemStatus = ""
    private var totalCases = ""
    private var isCompleted: Boolean= false
    private var isOldWork: Boolean= false
    private var selectedTime: Long= 0
    private var tempLumperIds = ArrayList<String>()
    private var lumperList = ArrayList<LumperAttendanceData>()
    private var timingsData = HashMap<String, LumpersTimeSchedule>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_work_item_detail_lumper_time, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return lumperList.size
    }

    private fun getItem(position: Int): LumperAttendanceData {
        return lumperList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val linearLayoutLumperTime: ConstraintLayout = view.linearLayoutLumperTime as ConstraintLayout
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val textViewLeadType: CustomTextView = view.textViewLeadType
        private val textViewAddTime: TextView = view.textViewAddTime
        private val textViewWorkTime: TextView = view.textViewWorkTime
        private val textViewWaitingTime: TextView = view.textViewWaitingTime
        private val textViewBreakTime: TextView = view.textViewBreakTime
        private val textViewWorkDone: TextView = view.textViewWorkDone
        private val textViewLastDate: TextView = view.textViewLastDate
        private val workTimeHeader: TextView = view.workTimeHeader
        private val breakTimeHeader: TextView = view.breakTimeHeader
        private val waitingTimeHeader: TextView = view.waitingTimeHeader
        private val workDoneHeader: TextView = view.workDoneHeader
        private val viewAttendanceStatus: View = view.viewAttendanceStatus
        private val imageViewCancelLumper: ImageView = view.imageViewCancelLumper
        private val editIcon: ImageView = view.edit_icon

        fun bind(employeeData: LumperAttendanceData) {
            val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            val buildingDetailData = ScheduleUtils.getBuildingDetailData(leadProfile?.buildingDetailData)
            var buildingId = ""
            buildingDetailData?.id?.let { id ->
                buildingId = id
            }
            UIUtils.showEmployeeProfileImage(
                context,
                employeeData.profileImageUrl,
                circleImageViewProfile
            )
            UIUtils.updateProfileBorder(
                context,
                buildingId != employeeData.buildingIdAsLumper,
                circleImageViewProfile
            )
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData)
            ishHasClockOut(employeeData)
            imageViewCancelLumper.visibility= if (workItemStatus == AppConstant.WORK_ITEM_STATUS_COMPLETED || isOldWork) View.GONE else View.VISIBLE
            checkForOldData()

            textViewLeadType.visibility=if (employeeData.role.equals(AppConstant.LEADS)) View.VISIBLE else View.INVISIBLE

            if (timingsData.containsKey(employeeData.id)) {
                val timingDetail = timingsData[employeeData.id]
                timingDetail?.let {
                    val startTime = convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.startTime)
                    val endTime = convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.endTime)
                    if (startTime.isNotEmpty() && endTime.isNotEmpty())
                        textViewWorkTime.text = String.format("%s - %s ; %s", startTime, endTime, DateUtils.getDateTimeCalculeted(timingDetail.startTime!!, timingDetail.endTime!!))
                    else textViewWorkTime.text = String.format("%s - %s", if (startTime.isNotEmpty()) startTime else AppConstant.NOTES_NOT_AVAILABLE,
                        if (endTime.isNotEmpty()) endTime else AppConstant.NOTES_NOT_AVAILABLE
                    )

                    val waitingTime = getDefaultOrValue(timingDetail.waitingTime)
                    if (waitingTime.isNotEmpty() && waitingTime.toInt() != 0) {
                        val waitingTimeHours = ValueUtils.getHoursFromMinutes(timingDetail.waitingTime)
                        val waitingTimeMinutes = ValueUtils.getRemainingMinutes(timingDetail.waitingTime)
                        textViewWaitingTime.text = String.format("%s H %s M", waitingTimeHours, waitingTimeMinutes )
                    } else {
                        textViewWaitingTime.text = AppConstant.NOTES_NOT_AVAILABLE
                    }

                    val mBreakTimeList = getBreakTimeList(timingDetail.breakTimes)
                    if (mBreakTimeList.isNotEmpty() && checkStartEndTime(mBreakTimeList)) {
                        showPauseTimeDuration(mBreakTimeList, textViewBreakTime)
                    } else textViewBreakTime.text = AppConstant.NOTES_NOT_AVAILABLE

                    if (!timingDetail.partWorkDone.isNullOrEmpty() && timingDetail.partWorkDone!!.toInt() != 0) {
                        if (!totalCases.isNullOrEmpty() && isNumeric(totalCases)) {
                            val parcetage = String.format("%.2f", calculatePercent(timingDetail.partWorkDone!!, totalCases)) + "%"
                            textViewWorkDone.text = String.format("%s / %s ; %s", timingDetail.partWorkDone, totalCases, parcetage) }
                    } else {
                        textViewWorkDone.text = AppConstant.NOTES_NOT_AVAILABLE
                    }
                }
                if (isOldWork){
                    textViewLastDate.visibility=View.VISIBLE
                    timingDetail?.createdAt?.let {textViewLastDate.text=resources.getString(R.string.work_date,  changeDateString(PATTERN_API_RESPONSE,PATTERN_DATE_DISPLAY_CUSTOMER_SHEET, it) )}
                }else{
                    textViewLastDate.visibility=View.GONE
                }
            } else {
                textViewWorkTime.text = String.format("%s - %s", AppConstant.NOTES_NOT_AVAILABLE, AppConstant.NOTES_NOT_AVAILABLE)
                textViewWaitingTime.text = AppConstant.NOTES_NOT_AVAILABLE
                textViewWorkDone.text = AppConstant.NOTES_NOT_AVAILABLE
                textViewBreakTime.text = String.format("%s - %s", AppConstant.NOTES_NOT_AVAILABLE, AppConstant.NOTES_NOT_AVAILABLE)
            }

            checkPastDayWorkTime()
            changeAddButtonVisibility()
            textViewAddTime.setOnClickListener(this)
            linearLayoutLumperTime.setOnClickListener(this)
            imageViewCancelLumper.setOnClickListener(this)
        }

        private fun checkPastDayWorkTime() {
            if (!DateUtils.isCurrentDate(selectedTime) && !DateUtils.isFutureDate(selectedTime) && !isOldWork) {
                val color=ContextCompat.getColor(context, R.color.detailHeader)
                waitingTimeHeader.setTextColor(color)
                textViewWaitingTime.setTextColor(color)
            }
        }

        private fun checkForOldData() {
            when {
                isOldWork -> {
                    val color=ContextCompat.getColor(context, R.color.textBlack)
                    linearLayoutLumperTime.background = ContextCompat.getDrawable(context, R.drawable.schedule_item_background_grey)
                    textViewWorkTime.setTextColor(color)
                    textViewBreakTime.setTextColor(color)
                    textViewWorkDone.setTextColor(color)
                    textViewWaitingTime.setTextColor(color)
                    workTimeHeader.setTextColor(color)
                    breakTimeHeader.setTextColor(color)
                    waitingTimeHeader.setTextColor(color)
                    workDoneHeader.setTextColor(color)
                    editIcon.setColorFilter(color)
                }
                else -> linearLayoutLumperTime.background = ContextCompat.getDrawable(context, R.drawable.schedule_item_background)
            }
        }

        private fun ishHasClockOut(lumperAttendance: LumperAttendanceData) {
            lumperAttendance.attendanceDetail?.let {
                if (it.isPresent!! && !it.morningPunchIn.isNullOrEmpty() && it.eveningPunchOut.isNullOrEmpty()){
                    viewAttendanceStatus.setBackgroundResource( R.drawable.online_dot )
                }else if(it.isPresent!! && !it.morningPunchIn.isNullOrEmpty() && !it.eveningPunchOut.isNullOrEmpty()){
                    viewAttendanceStatus.setBackgroundResource( R.drawable.offline_dot)

                }else viewAttendanceStatus.setBackgroundResource( R.drawable.offline_dot)
            }
        }

        private fun getBreakTimeList(breakTimes: ArrayList<PauseTime>?): ArrayList<PauseTimeRequest> {
            val pauseTimeList: ArrayList<PauseTimeRequest> = ArrayList()
            breakTimes?.let {
                it.forEach {
                    val breakTime = PauseTimeRequest()
                    breakTime.startTime = convertInitialTime(it.startTime)
                    breakTime.endTime = convertInitialTime(it.endTime)
                    pauseTimeList.add(breakTime)
                }
            }
            return pauseTimeList
        }

        private fun convertInitialTime(dateStamp: String?): Long {
            var milliseconds: Long = 0
            val time = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, dateStamp)
            if (time.isNotEmpty()) {
                val currentDateString = DateUtils.convertUTCDateStringToLocalDateString(
                    DateUtils.PATTERN_API_RESPONSE,
                    dateStamp
                )
                milliseconds = DateUtils.getMillisecondsFromDateString(
                    DateUtils.PATTERN_API_RESPONSE,
                    currentDateString
                )
            }
            return milliseconds
        }

        private fun showPauseTimeDuration(
            mBreakTimeList: ArrayList<PauseTimeRequest>,
            textViewBreakTime: TextView
        ) {
            var dateTime: Long = 0
            for (pauseTime in mBreakTimeList) {
                dateTime += (pauseTime.endTime!! - pauseTime.startTime!!)
            }
            textViewBreakTime.text = "${DateUtils.getDateTimeCalculatedLong(dateTime)}"
        }

        private fun changeAddButtonVisibility() {
            if (workItemStatus == AppConstant.WORK_ITEM_STATUS_IN_PROGRESS || workItemStatus == AppConstant.WORK_ITEM_STATUS_ON_HOLD ||workItemStatus == AppConstant.WORK_ITEM_STATUS_SCHEDULED||workItemStatus == AppConstant.WORK_ITEM_STATUS_CANCELLED ||workItemStatus == AppConstant.WORK_ITEM_STATUS_COMPLETED) {
//                textViewAddTime.visibility = if (isCompleted) View.GONE else View.VISIBLE
                textViewAddTime.visibility = View.GONE
                linearLayoutLumperTime.isClickable = !isCompleted
            } else {
                textViewAddTime.visibility = View.GONE
                linearLayoutLumperTime.isClickable = false
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    linearLayoutLumperTime.id -> {
                        if (!isOldWork) {
                            val employeeData = getItem(adapterPosition)
                            val timingData = timingsData[employeeData.id]
                            onAdapterClick.onAddTimeClick(employeeData, timingData)
                        }
                    }
                    imageViewCancelLumper.id->{
                        if (!isOldWork) {
                            val employeeData = getItem(adapterPosition)
                            onAdapterClick.onRemoveLumperClick(employeeData, adapterPosition)
                        }
                    }
                }
            }
        }
    }

    private fun checkStartEndTime(mBreakTimeList: ArrayList<PauseTimeRequest>): Boolean {
        var isValid = true
        mBreakTimeList.forEach {
            if (it.startTime == 0L || it.endTime == 0L) {
                isValid = false
                return@forEach
            }
        }
        return isValid
    }

    fun updateList(
        lumperList: ArrayList<LumperAttendanceData>?,
        timingsData: LinkedHashMap<String, LumpersTimeSchedule>,
        status: String? = "",
        tempLumperIds: ArrayList<String>,
        totalCases: String?,
        isCompleted: Boolean?,
        isOldWork: Boolean?,
        selectedTime: Long
    ) {
        this.timingsData.clear()
        this.lumperList.clear()
        lumperList?.let {
            this.lumperList.addAll(lumperList)
            this.timingsData.putAll(timingsData)
        }
        this.workItemStatus = getDefaultOrValue(status)
        this.totalCases = getDefaultOrValue(totalCases)
        this.isOldWork = getDefaultOrValue(isOldWork)
        this.selectedTime=selectedTime

        this.tempLumperIds.clear()
        this.tempLumperIds.addAll(tempLumperIds)
        notifyDataSetChanged()
    }
}