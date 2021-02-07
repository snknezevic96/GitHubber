package com.futuradev.githubber.utils.manager

import android.app.Activity
import android.view.inputmethod.InputMethodManager

class KeyboardManager {

    fun showKeyboard(activity: Activity?) {
        activity ?: return

        val token = activity.currentFocus?.windowToken

        activity.getInputMethodManage()
            .apply { toggleSoftInputFromWindow(token, InputMethodManager.SHOW_FORCED, 0) }
    }

    fun hideKeyboard(activity: Activity?) {
        activity ?: return

        val token = activity.currentFocus?.windowToken

        activity.getInputMethodManage()
            .apply { hideSoftInputFromWindow(token,0) }
    }

    private fun Activity.getInputMethodManage() : InputMethodManager {
        return this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    }
}