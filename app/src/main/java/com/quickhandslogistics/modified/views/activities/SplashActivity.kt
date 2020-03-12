package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import android.view.animation.AnimationUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.SplashContract
import com.quickhandslogistics.modified.presenters.SplashPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.utils.AppConstant
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity(), SplashContract.View {

    private lateinit var splashPresenter: SplashPresenter

    companion object {
        private const val LOGO_ANIMATION_DURATION: Long = 700
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        showLogoAnimation()

        splashPresenter = SplashPresenter(this)
        splashPresenter.decideNextActivity()
    }

    private fun showLogoAnimation() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_up)
        anim.duration = LOGO_ANIMATION_DURATION
        imageViewSplashLogo.startAnimation(anim)
    }

    override fun showNextScreen() {
        if (sharedPref.getBoolean(AppConstant.PREFERENCE_IS_ACTIVE)) {
            startIntent(DashBoardActivity::class.java, isFinish = true)
        } else {
            startIntent(LoginActivity::class.java, isFinish = true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        splashPresenter.onDestroy()
    }
}
