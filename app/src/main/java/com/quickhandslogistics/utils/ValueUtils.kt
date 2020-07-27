package com.quickhandslogistics.utils

object ValueUtils {

    fun getDefaultOrValue(value: Boolean?): Boolean {
        var ret = false
        value?.let {
            ret = value
        }
        return ret
    }

    fun getDefaultOrValue(value: String?): String {
        var ret = ""
        value?.let {
            ret = value
        }
        return ret
    }

    fun getDefaultOrValue(value: Int?): Int {
        var ret = 0
        value?.let {
            ret = value
        }
        return ret
    }

    fun isNumeric(value: String): Boolean {
        return try {
            if (value.equals("NaN")) {
                return false
            } else {
                value.toInt()
                true
            }
        } catch (e: NumberFormatException) {
            false
        }
    }
}

