package com.quickhandslogistics.utils

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import java.util.*

object CustomeDialog : AppConstant {
    private var mActivity: Activity? = null
    fun getDialog(view: Int, activity: Activity?): Dialog {
        mActivity = activity
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.color.colorOpaque)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(view)
        val layoutParams = dialog.window!!.attributes
        val window = dialog.window
        layoutParams.copyFrom(window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = layoutParams
        layoutParams.gravity = Gravity.CENTER
        return dialog
    }

    fun showGroupNoteDialog(activity: Activity?, title: String?, customerGroupNote: Triple<Pair<ArrayList<String>?, ArrayList<String>?>?, ArrayList<String>?, ArrayList<String>?>) {
        mActivity = activity
        val dialog =
            getDialog(R.layout.custome_alert_dialog, activity)
        //        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val titleTextView = dialog.findViewById<TextView>(R.id.title_text)
        val layoutDailyNote = dialog.findViewById<LinearLayout>(R.id.layoutDailyNote)
        val layoutWeeklyNote = dialog.findViewById<LinearLayout>(R.id.layoutWeeklyNote)
        val layoutMonthlyNote = dialog.findViewById<LinearLayout>(R.id.layoutMonthlyNote)
        val layoutCustomNote = dialog.findViewById<LinearLayout>(R.id.layoutCustomNote)
        val dailyNoteRecycler: RecyclerView = dialog.findViewById(R.id.recycler_view_daily_note)
        val weeklyNoteRecycler: RecyclerView = dialog.findViewById(R.id.recycler_view_weekly_note)
        val MonthlyNoteRecycelr: RecyclerView = dialog.findViewById(R.id.recycler_view_monthly_note)
        val CustomNoteRecycler: RecyclerView = dialog.findViewById(R.id.recycler_view_custom_note)
        val confirm = dialog.findViewById<Button>(R.id.confirm_button)
        val dailyNoteList = customerGroupNote.first?.first
        val weeklyNoteList = customerGroupNote.second
        val monthlyNoteList = customerGroupNote.third
        val customNoteList = customerGroupNote.first?.second
        if (dailyNoteList != null && dailyNoteList.size > 0) {
            val mDailyNoteAdapter = ListContentAdapter(dailyNoteList)
            val manager1: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            dailyNoteRecycler.layoutManager = manager1
            dailyNoteRecycler.adapter = mDailyNoteAdapter
            dailyNoteRecycler.visibility = View.VISIBLE
            layoutDailyNote.visibility = View.VISIBLE
        } else {
            dailyNoteRecycler.visibility = View.GONE
            layoutDailyNote.visibility = View.GONE
        }
        if (weeklyNoteList != null && weeklyNoteList.size > 0) {
            val mWeeklyNoteAdapter = ListContentAdapter(weeklyNoteList)
            val manager2: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            weeklyNoteRecycler.layoutManager = manager2
            weeklyNoteRecycler.adapter = mWeeklyNoteAdapter
            weeklyNoteRecycler.visibility = View.VISIBLE
            layoutWeeklyNote.visibility = View.VISIBLE
        } else {
            weeklyNoteRecycler.visibility = View.GONE
            layoutWeeklyNote.visibility = View.GONE
        }
        if (monthlyNoteList != null && monthlyNoteList.size > 0) {
            val mMonthlyNoteAdaptor = ListContentAdapter(monthlyNoteList)
            val manager3: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            MonthlyNoteRecycelr.layoutManager = manager3
            MonthlyNoteRecycelr.adapter = mMonthlyNoteAdaptor
            MonthlyNoteRecycelr.visibility = View.VISIBLE
            layoutMonthlyNote.visibility = View.VISIBLE
        } else {
            MonthlyNoteRecycelr.visibility = View.GONE
            layoutMonthlyNote.visibility = View.GONE
        }
        if (customNoteList != null && customNoteList.size > 0) {
            val mCustomNoteAdaptor = ListContentAdapter(customNoteList)
            val manager3: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            CustomNoteRecycler.layoutManager = manager3
            CustomNoteRecycler.adapter = mCustomNoteAdaptor
            CustomNoteRecycler.visibility = View.VISIBLE
            layoutCustomNote.visibility = View.VISIBLE
        } else {
            CustomNoteRecycler.visibility = View.GONE
            layoutCustomNote.visibility = View.GONE
        }
        titleTextView.text = title
        confirm.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    fun showLeadNoteDialog(activity: Activity?, title: String?, individualNote: String?, groupNote: String?) {
        mActivity = activity
        val dialog =
            getDialog(R.layout.lead_note_dialog, activity)
        //        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val titleTextView = dialog.findViewById<TextView>(R.id.title_text)
        val layoutIndividualNote = dialog.findViewById<LinearLayout>(R.id.layoutIndividualNote)
        val layoutGroupNote = dialog.findViewById<LinearLayout>(R.id.layoutGroupNote)
        val individualNoteText: TextView = dialog.findViewById(R.id.individual_note)
        val groupNoteText: TextView = dialog.findViewById(R.id.groupNote)
        val confirm = dialog.findViewById<Button>(R.id.confirm_button)

        if(individualNote.isNullOrEmpty() || individualNote.equals("NA")){
            layoutIndividualNote.visibility=View.GONE
        }else{
            layoutIndividualNote.visibility=View.VISIBLE
        }

        if(groupNote.isNullOrEmpty()){
            layoutGroupNote.visibility=View.GONE
        }else{
            layoutGroupNote.visibility=View.VISIBLE
        }

        individualNoteText.text= individualNote
        groupNoteText.text= groupNote
        titleTextView.text = title
        confirm.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    interface IDialogOnClick
}