package com.quickhandslogistics.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SharedPref

open class BaseFragment : Fragment(), BaseContract.View {

    var fragmentActivity: FragmentActivity? = null
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    protected lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let { activity ->
            fragmentActivity = activity
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        }
        sharedPref = SharedPref.getInstance()
    }

    protected fun startIntent(className: Class<*>, bundle: Bundle? = null, isFinish: Boolean = false, flags: Array<Int>? = null, requestCode: Int? = null) {
        val intent = Intent(fragmentActivity, className)
        flags?.let {
            for (flag in flags) {
                intent.addFlags(flag)
            }
        }
        bundle?.let {
            intent.putExtras(bundle)
        }
        fragmentActivity?.let { fragmentActivity ->
            requestCode?.also {
                startActivityForResult(intent, requestCode)
            } ?: run {
                startActivity(intent)
            }
            if (isFinish) fragmentActivity.finish()
            fragmentActivity.overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun addNotesTouchListener(editText: EditText) {
        editText.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
    }

    /** Presenter Listeners */
    override fun showProgressDialog(message: String) {
        fragmentActivity?.let { context ->
            CustomProgressBar.getInstance().show(message = message, activityContext = context)
        }
    }

    override fun hideProgressDialog() {
        CustomProgressBar.getInstance().hide()
    }
}