package com.quickhandslogistics.modified.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.SharedPref
import kotlinx.android.synthetic.main.layout_toolbar.*

open class BaseActivity : AppCompatActivity() {

    protected lateinit var activity: Activity
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    protected lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        sharedPref = SharedPref.getInstance()
    }

    protected fun setupToolbar(title: String) {
        toolbar.title = ""
        setSupportActionBar(toolbar)

        textViewTitle.text = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    protected fun startIntent(
        className: Class<*>,
        bundle: Bundle? = null,
        isFinish: Boolean = false,
        flags: Array<Int>? = null
    ) {
        val intent = Intent(this, className)
        flags?.let {
            for (flag in flags) {
                intent.addFlags(flag)
            }
        }
        bundle?.let {
            intent.putExtras(bundle)
        }
        startActivity(intent)
        if (isFinish) finish()
        overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
    }
}