package com.quickhandslogistics.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.view.activities.CustomerActivity
import com.quickhandslogistics.view.activities.LumperListActivity
import kotlinx.android.synthetic.main.fragment_send.*

class ReportFragment : BaseFragment(),  View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_send, container, false)
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewJobHistory.setOnClickListener(this)
        textViewLumperJobHistory.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewJobHistory.id -> {
                    val bundle = Bundle()
                    bundle.putSerializable(LumperListActivity.ARG_STRING_LUMPER, R.string.string_lumper)
                    startIntent(LumperListActivity::class.java, bundle = bundle)
                }
                textViewLumperJobHistory.id -> {
                    startIntent(CustomerActivity::class.java, isFinish = false)
                }
            }
        }
    }
}