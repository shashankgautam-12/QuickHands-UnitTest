package com.quickhandslogistics.views.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.common.DisplayLumpersListAdapter
import com.quickhandslogistics.contracts.lumpers.LumpersContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.AppUtils
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.lumpers.LumperDetailActivity
import kotlinx.android.synthetic.main.content_choose_lumper.*
import java.util.ArrayList
import kotlin.Comparator

class DisplayLumpersListActivity : BaseActivity(), View.OnClickListener, TextWatcher, LumpersContract.View.OnAdapterItemClickListener {

    private lateinit var displayLumpersListAdapter: DisplayLumpersListAdapter

    companion object {
        const val ARG_LUMPERS_LIST = "ARG_LUMPERS_LIST"
        const val LUMPER_DISPLAY_LIST = "LUMPER_DISPLAY_LIST"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lumpers_list)
        setupToolbar(title = getString(R.string.lumpers))

        intent.extras?.let { bundle ->
            val lumpersList = bundle.getParcelableArrayList<EmployeeData>(ARG_LUMPERS_LIST)

            lumpersList?.let {
                lumpersList.sortWith(Comparator { lumper1, lumper2 ->
                    if (!lumper1.firstName.isNullOrEmpty() && !lumper2.firstName.isNullOrEmpty()) {
                        lumper1.firstName?.toLowerCase()!!.compareTo(lumper2.firstName?.toLowerCase()!!)
                    } else {
                        0
                    }
                })

                initializeUI(lumpersList)
            }
        }
    }

    private fun initializeUI(lumpersList: ArrayList<EmployeeData>) {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            displayLumpersListAdapter = DisplayLumpersListAdapter(lumpersList, sharedPref,this@DisplayLumpersListActivity)
            adapter = displayLumpersListAdapter
        }

        buttonAdd.visibility = View.GONE
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(activity)
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        text?.let {
            displayLumpersListAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Adapter Listeners */
    override fun onItemClick(employeeData: EmployeeData) {
//        val bundle = Bundle()
//        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
//        startIntent(LumperDetailActivity::class.java, bundle = bundle)
    }

    override fun onPhoneViewClick(lumperName: String, phone: String) {
        CustomProgressBar.getInstance().showWarningDialog(String.format(getString(R.string.call_lumper_alert_message), lumperName), activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
            }

            override fun onCancelClick() {
            }
        })
    }

    override fun message(name: String, mNumber: String) {
    }
}