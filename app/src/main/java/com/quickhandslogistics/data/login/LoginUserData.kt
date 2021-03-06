package com.quickhandslogistics.data.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.lumpers.EmployeeData
import java.io.Serializable

class LoginUserData : EmployeeData(), Serializable {
    @SerializedName("token")
    @Expose
    var token: String? = null
}