package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import android.view.MenuItem
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.modified.views.BaseActivity
import kotlinx.android.synthetic.main.content_lumper_detail_new.*

class LumperDetailActivity : BaseActivity() {

    private var lumperData: LumperData? = null

    companion object {
        const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_detail)
        setupToolbar(title = getString(R.string.string_lumper_details))

        displayLumperDetails()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayLumperDetails() {
        intent.extras?.let {
            if (it.containsKey(ARG_LUMPER_DATA)) {
                lumperData = it.getSerializable(ARG_LUMPER_DATA) as LumperData

                lumperData?.let {
                    textViewLumperName.text = String.format(
                        "%s %s",
                        lumperData?.firstName,
                        lumperData?.lastName
                    )
                    textViewEmail.text = lumperData?.email
                    textViewPhone.text = lumperData?.phone
                    textViewRole.text = lumperData?.role
                }
            }
        }
    }
}
