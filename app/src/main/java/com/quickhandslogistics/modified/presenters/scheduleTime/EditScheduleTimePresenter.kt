package com.quickhandslogistics.modified.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.modified.models.scheduleTime.EditScheduleTimeModel
import java.util.*

class EditScheduleTimePresenter(
    private var editScheduleTimeView: EditScheduleTimeContract.View?, private val resources: Resources
) : EditScheduleTimeContract.Presenter, EditScheduleTimeContract.Model.OnFinishedListener {

    private val editScheduleTimeModel = EditScheduleTimeModel()

    override fun onDestroy() {
        editScheduleTimeView = null
    }

    override fun initiateScheduleTime(
        scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, requiredLumpersCount: Int, notesDM: String, selectedDate: Date
    ) {
        editScheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        editScheduleTimeModel.assignScheduleTime(scheduledLumpersIdsTimeMap, notes, requiredLumpersCount, notesDM, selectedDate, this)
    }

    override fun onFailure(message: String) {
        editScheduleTimeView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            editScheduleTimeView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            editScheduleTimeView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessScheduleTime() {
        editScheduleTimeView?.hideProgressDialog()
        editScheduleTimeView?.scheduleTimeFinished()
    }
}