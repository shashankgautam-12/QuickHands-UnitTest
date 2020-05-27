package com.quickhandslogistics.adapters.scheduleTime

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.RequestLumpersContract
import com.quickhandslogistics.data.scheduleTime.RequestLumpersRecord
import com.quickhandslogistics.utils.AppConstant
import kotlinx.android.synthetic.main.item_request_lumpers.view.*

class RequestLumpersAdapter(private val resources: Resources, private val isFutureDate: Boolean, private val onAdapterClick: RequestLumpersContract.View.OnAdapterItemClickListener) :
    Adapter<RequestLumpersAdapter.ViewHolder>() {

    private val requestList: ArrayList<RequestLumpersRecord> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_request_lumpers, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    private fun getItem(position: Int): RequestLumpersRecord {
        return requestList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewStatus: TextView = view.textViewStatus
        private val textViewRequestedLumpersCount: TextView = view.textViewRequestedLumpersCount
        private val textViewNote: TextView = view.textViewNote
        private val textViewUpdateRequest: TextView = view.textViewUpdateRequest
        private val linearLayoutNotes: LinearLayout = view.linearLayoutNotes

        fun bind(requestLumpersRecord: RequestLumpersRecord) {
            textViewRequestedLumpersCount.text = String.format(resources.getString(R.string.requested_lumpers_s), requestLumpersRecord.requestedLumpersCount)
            textViewNote.text = requestLumpersRecord.notesForDM

            when (requestLumpersRecord.requestStatus) {
                AppConstant.REQUEST_LUMPERS_STATUS_PENDING -> {
                    textViewStatus.text = resources.getString(R.string.pending)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                    textViewUpdateRequest.visibility = if (isFutureDate) View.VISIBLE else View.GONE
                }
                AppConstant.REQUEST_LUMPERS_STATUS_APPROVED -> {
                    textViewStatus.text = resources.getString(R.string.approved)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
                    textViewUpdateRequest.visibility = View.GONE
                }
                else -> {
                    textViewStatus.text = resources.getString(R.string.rejected)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                    textViewUpdateRequest.visibility = View.GONE
                }
            }

            textViewUpdateRequest.setOnClickListener(this)
            linearLayoutNotes.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    linearLayoutNotes.id -> {
                        val record = getItem(adapterPosition)
                        onAdapterClick.onNotesItemClick(record.notesForDM)
                    }
                    textViewUpdateRequest.id -> {
                        val record = getItem(adapterPosition)
                        onAdapterClick.onUpdateItemClick(record)
                    }
                }
            }
        }
    }

    fun updateList(records: List<RequestLumpersRecord>) {
        this.requestList.clear()
        this.requestList.addAll(records)

        notifyDataSetChanged()
    }
}