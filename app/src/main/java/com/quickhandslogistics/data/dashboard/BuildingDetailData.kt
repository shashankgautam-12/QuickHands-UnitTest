package com.quickhandslogistics.data.dashboard

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails

class BuildingDetailData() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("buildingName")
    @Expose
    var buildingName: String? = null

    @SerializedName("buildingCity")
    @Expose
    var buildingCity: String? = null

    @SerializedName("buildingState")
    @Expose
    var buildingState: String? = null

    @SerializedName("buildingZipcode")
    @Expose
    var buildingZipcode: String? = null

    @SerializedName("buildingAddress")
    @Expose
    var buildingAddress: String? = null

    @SerializedName("buildingNumber")
    @Expose
    var buildingNumber: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("parameters")
    @Expose
    var parameters: ArrayList<String>? = null

    @SerializedName("addedBy")
    @Expose
    var addedBy: String? = null

    @SerializedName("isBuildingVerified")
    @Expose
    var isBuildingVerified: Boolean? = null

    @SerializedName("morningShift")
    @Expose
    var morningShift: ShiftDetail? = null

    @SerializedName("swingShift")
    @Expose
    var swingShift: ShiftDetail? = null

    @SerializedName("nightShift")
    @Expose
    var nightShift: ShiftDetail? = null

    @SerializedName("leadIds")
    @Expose
    var leadIds: ArrayList<String>? = null

    @SerializedName("lumperIds")
    @Expose
    var lumperIds: ArrayList<String>? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("belongsTo")
    @Expose
    var customerDetail: CustomerDetail? = null

    @SerializedName("districtManager")
    @Expose
    var districtManager: EmployeeData? = null

    @SerializedName("leads")
    @Expose
    var leads: ArrayList<EmployeeData>? = null
        get() = if (field.isNullOrEmpty()) ArrayList() else field

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        buildingName = parcel.readString()
        buildingCity = parcel.readString()
        buildingState = parcel.readString()
        buildingZipcode = parcel.readString()
        buildingAddress = parcel.readString()
        buildingNumber = parcel.readString()
        phone = parcel.readString()
        parameters = parcel.createStringArrayList()
        addedBy = parcel.readString()
        isBuildingVerified = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        morningShift = parcel.readParcelable(ShiftDetail::class.java.classLoader)
        swingShift = parcel.readParcelable(ShiftDetail::class.java.classLoader)
        nightShift = parcel.readParcelable(ShiftDetail::class.java.classLoader)
        leadIds = parcel.createStringArrayList()
        lumperIds = parcel.createStringArrayList()
        isActive = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
        customerDetail = parcel.readParcelable(CustomerDetail::class.java.classLoader)
        districtManager = parcel.readParcelable(EmployeeData::class.java.classLoader)
        leads = parcel.createTypedArrayList(EmployeeData)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(buildingName)
        parcel.writeString(buildingCity)
        parcel.writeString(buildingState)
        parcel.writeString(buildingZipcode)
        parcel.writeString(buildingAddress)
        parcel.writeString(buildingNumber)
        parcel.writeString(phone)
        parcel.writeStringList(parameters)
        parcel.writeString(addedBy)
        parcel.writeValue(isBuildingVerified)
        parcel.writeParcelable(morningShift, flags)
        parcel.writeParcelable(swingShift, flags)
        parcel.writeParcelable(nightShift, flags)
        parcel.writeStringList(leadIds)
        parcel.writeStringList(lumperIds)
        parcel.writeValue(isActive)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeParcelable(customerDetail, flags)
        parcel.writeParcelable(districtManager, flags)
        parcel.writeTypedList(leads)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BuildingDetailData> {
        override fun createFromParcel(parcel: Parcel): BuildingDetailData {
            return BuildingDetailData(parcel)
        }

        override fun newArray(size: Int): Array<BuildingDetailData?> {
            return arrayOfNulls(size)
        }
    }
}