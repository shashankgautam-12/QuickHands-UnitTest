package com.quickhandslogistics.adapters.qhlContact

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.qhlContact.QhlContactContract
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.UIUtils
import kotlinx.android.synthetic.main.item_customer_contact_layout.view.*

class QhlContactAdapter(
    val resources: Resources,
    var adapterItemClickListener: QhlContactContract.View.OnAdapterItemClickListener
) : Adapter<QhlContactAdapter.ViewHolder>() {
    private var items: ArrayList<EmployeeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer_contact_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): EmployeeData {
        return items[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewCustomerName: TextView = view.textViewCustomerName
        private val textViewEmployeeRole: CustomTextView = view.textViewEmployeeRole
        private val textViewEmployeeTitle: CustomTextView = view.textViewEmployeeTitle
        private val textViewEmployeeShift: CustomTextView = view.textViewEmployeeShift
        private val textViewEmail: CustomTextView = view.textViewEmail
        private val textVieWContact: CustomTextView = view.textVieWContact
        private val textViewMessageTime: TextView = view.textViewMessageTime
        private val imageViewCall: ImageView = view.imageViewCall
        private val constraintViewCall: ConstraintLayout = view.constraintViewCall


        fun bind(item: EmployeeData) {

            val leadName = String.format("%s %s", item.firstName, item.lastName)
            val shift =
                if (!item.shift.isNullOrEmpty()) item.shift?.capitalize() else resources.getString(R.string.na)
            val dept =
                if (!item.department.isNullOrEmpty()) UIUtils.getDisplayEmployeeDepartmentHeader(item) else resources.getString(
                    R.string.na
                )
            textViewCustomerName.text =
                if (!leadName.isNullOrEmpty()) leadName.capitalize() else resources.getString(R.string.na)
            textViewEmployeeRole.text =
                if (!item.role.isNullOrEmpty()) item.role?.capitalize() else resources.getString(R.string.na)
            textViewEmployeeTitle.text =
                String.format(resources.getString(R.string.department_noraml), dept)
            textViewEmployeeShift.text =
                String.format(resources.getString(R.string.shift_normal), shift)
            textViewEmail.text =
                if (!item.email.isNullOrEmpty()) item.email else resources.getString(R.string.na)
            textViewMessageTime.text = "12:22 PM"
            textVieWContact.text =
                if (!item.phone.isNullOrEmpty()) UIUtils.formetMobileNumber(item.phone!!) else resources.getString(
                    R.string.na
                )

            if (item.role?.equals(AppConstant.DISTRICT_MANAGER)!!) {
                textViewEmployeeTitle.visibility = View.INVISIBLE
                textViewEmployeeShift.visibility = View.INVISIBLE
            } else {
                textViewEmployeeTitle.visibility = View.VISIBLE
                textViewEmployeeShift.visibility = View.VISIBLE
            }

            constraintViewCall.setOnClickListener(this)
            textVieWContact.setOnClickListener(this)
            textViewEmail.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                    }
                    textVieWContact.id -> {
                        val item = getItem(adapterPosition)
                        item.phone?.let { phone ->
                            adapterItemClickListener.onPhoneViewClick(item.firstName!!, phone)
                        }
                    }
                    textViewEmail.id -> {
                        val item = getItem(adapterPosition)
                        item.email?.let { email ->
                            adapterItemClickListener.onEmailViewClick(item.firstName!!, email)
                        }
                    }
                    constraintViewCall.id -> {
                        val item = getItem(adapterPosition)
                        adapterItemClickListener.onChatViewClick(item)
                    }

                    else -> {
                    }
                }
            }
        }
    }

    fun updateLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        this.items.clear()
        this.items.addAll(employeeDataList)
        notifyDataSetChanged()
    }
}