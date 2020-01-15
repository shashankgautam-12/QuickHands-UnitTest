package com.quickhandslogistics.utils

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.TextView
import com.quickhandslogistics.R

class DialogHelper {

    companion object {
        fun showDialog(title: String, activity : Activity) {
            val dialog = Dialog(activity)
            dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog .setCancelable(false)
            dialog .setContentView(R.layout.layout_dialog)

            var text = dialog.findViewById<TextView>(R.id.text_message)
            var yes = dialog.findViewById<TextView>(R.id.text_yes)
            var no = dialog.findViewById<TextView>(R.id.text_no)

            text.text = title

            yes.setOnClickListener {
                dialog.dismiss()
            }

            no.setOnClickListener {
                dialog.dismiss()
            }

            dialog .show()
        }
    }
}