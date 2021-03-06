package com.quickhandslogistics.views.customerContact

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.customerContact.CustomerContactAdapter
import com.quickhandslogistics.contracts.customerContact.CustomerContactContract
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.customerContact.CustomerContactPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.content_customer_contact_header.*
import kotlinx.android.synthetic.main.fragment_customer_contect.*

class CustomerContactFragment : BaseFragment(), CustomerContactContract.View, View.OnClickListener, CustomerContactContract.View.OnAdapterItemClickListener {
    private lateinit var customerContactPresenter: CustomerContactPresenter
    private lateinit var customerContactAdapter: CustomerContactAdapter
    private var buildingDetailData: BuildingDetailData? = null
    private var customerContactList: ArrayList<EmployeeData> = ArrayList()
    private var phone: String? = null

    companion object {
        const val CUSTOMER_CONTACT_LIST = "CUSTOMER_CONTACT_LIST"
        const val HEADER_INFO = "HEADER_INFO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerContactPresenter = CustomerContactPresenter(this, resources, sharedPref)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customer_contect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerListContact.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            customerContactAdapter = CustomerContactAdapter(resources, this@CustomerContactFragment)
            adapter = customerContactAdapter
        }

        customerContactAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(CUSTOMER_CONTACT_LIST)) {
                customerContactList =
                    savedInstanceState.getSerializable(CUSTOMER_CONTACT_LIST) as ArrayList<EmployeeData>
                showCustomerContactData(customerContactList)
            }

            if (savedInstanceState.containsKey(HEADER_INFO)) {
                buildingDetailData = savedInstanceState.getParcelable(HEADER_INFO)
                showHeaderInfo(buildingDetailData)
            }
        } ?: run {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            customerContactPresenter.fetchCustomerContactList()
        }
        textViewCompanyContact.setOnClickListener(this)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        if (buildingDetailData != null)
            outState.putParcelable(HEADER_INFO, buildingDetailData)

        outState.putSerializable(CUSTOMER_CONTACT_LIST, customerContactList)
        super.onSaveInstanceState(outState)
    }

    private fun invalidateEmptyView() {
        if (customerContactAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            textViewEmptyData.text = getString(R.string.empty_contact_list_message)
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_contact_list_message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        customerContactPresenter.onDestroy()
    }

    override fun showHeaderInfo(buildingDetailData: BuildingDetailData?) {
        this.buildingDetailData = buildingDetailData
        buildingDetailData?.let {
            phone = it.phone?.replace("+1", "")?.replace("-", "")?.trim()
            textViewCustomerName.text = it.buildingName!!.capitalize()
            textViewCompanyName.text =
                it.buildingAddress!!.capitalize().trim() + ", " + it.buildingCity + ", " + it.buildingState + " " + it.buildingZipcode
            textViewCompanyContact.text =
                if (!phone.isNullOrEmpty()) UIUtils.formetMobileNumber(phone!!) else getString(R.string.na)
        }
        activity?.let {
            Glide.with(it).load(R.drawable.building_icon).into(circleImageViewProfile)
        }
    }

    override fun showAPIErrorMessage(message: String) {
        customerListContact.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        if (message.equals(AppConstant.ERROR_MESSAGE, ignoreCase = true)) {
            CustomProgressBar.getInstance().showValidationErrorDialog(message, fragmentActivity!!)
        } else SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showCustomerContactData(customerContactList: ArrayList<EmployeeData>) {
        this.customerContactList = customerContactList
        val qhlMangerList: ArrayList<EmployeeData> = ArrayList()
        val qhlSuperVisorList: ArrayList<EmployeeData> = ArrayList()
        val sortedList: ArrayList<EmployeeData> = ArrayList()

        customerContactList.forEach {
            if (it.role?.equals(AppConstant.MANAGER)!!) {
                qhlMangerList.add(it)
            } else if (it.role?.equals(AppConstant.SUPERVISOR)!!) {
                qhlSuperVisorList.add(it)
            }
        }
        sortedList.addAll(qhlMangerList)
        sortedList.addAll(qhlSuperVisorList)
        customerContactAdapter.updateLumpersData(sortedList)

    }

    override fun showLoginScreen() {
        startIntent(
            LoginActivity::class.java,
            isFinish = true,
            flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                textViewCompanyContact.id -> {
                    if (!phone.isNullOrEmpty())
                        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
                }
            }
        }
    }

    override fun onItemClick(employeeData: EmployeeData) {}

    override fun onPhoneViewClick(lumperName: String, phone: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
    }

    override fun onEmailViewClick(lumperName: String, email: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    override fun onChatViewClick(employeeData: EmployeeData) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", employeeData.email, null))
        startActivity(Intent.createChooser(emailIntent, "Send email..."))

    }
}