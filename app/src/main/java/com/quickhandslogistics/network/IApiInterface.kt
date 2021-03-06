package com.quickhandslogistics.network

import com.quickhandslogistics.contracts.workSheet.UploadImageResponse
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.addContainer.AddContainerRequest
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.data.common.AllLumpersResponse
import com.quickhandslogistics.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.data.login.LoginRequest
import com.quickhandslogistics.data.login.LoginResponse
import com.quickhandslogistics.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.data.lumperSheet.SubmitLumperSheetRequest
import com.quickhandslogistics.data.lumpers.BuildingDetailsResponse
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.data.qhlContact.QhlContactListResponse
import com.quickhandslogistics.data.qhlContact.QhlOfficeInfoResponse
import com.quickhandslogistics.data.reports.ReportRequest
import com.quickhandslogistics.data.reports.ReportResponse
import com.quickhandslogistics.data.schedule.*
import com.quickhandslogistics.data.scheduleTime.*
import com.quickhandslogistics.data.scheduleTime.leadinfo.GetLeadInfoResponse
import com.quickhandslogistics.data.workSheet.*
import com.quickhandslogistics.data.lumperSheet.LumperCorrectionRequest
import com.quickhandslogistics.data.qhlContact.ChatMessageRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface IApiInterface {
    @POST("employees/lead/login")
    fun doLogin(@Body request: LoginRequest): Call<LoginResponse>

    @GET("employees/lead/lumpers")
    fun getAllLumpersData(@Header("Authorization") auth: String, @Query("day") day: String,  @Query("unavailable") isAvailable: Boolean): Call<LumperListAPIResponse>

    @GET("employees/me")
    fun getLeadProfile(@Header("Authorization") auth: String): Call<LeadProfileAPIResponse>

    @POST("employees/logout")
    fun logout(@Header("Authorization") auth: String): Call<BaseResponse>

    @POST("emails/forgot-password/lead")
    fun doResetPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    // Schedule /////////////////////////////////////////////////
    @GET("schedule/lookup/date")
    fun getSchedulesList(
        @Header("Authorization") auth: String, @Query("date") date: String,
        @Query("page") page: Int, @Query("pageSize") pageSize: Int
    ): Call<ScheduleListAPIResponse>


    @GET("schedule/lookup/date")
    fun getSchedulesDetails(
        @Header("Authorization") auth: String, @Query("date") date: String, @Query("department") department: String,
        @Query("page") page: Int, @Query("pageSize") pageSize: Int
    ): Call<ScheduleListAPIResponse>


    @GET("schedule/unscheduled")
    fun getUnSchedulesList(@Header("Authorization") auth: String): Call<UnScheduleListAPIResponse>

    @GET("schedule/{scheduleIdentityId}")
    fun getScheduleDetail(
        @Header("Authorization") auth: String, @Path("scheduleIdentityId") scheduleIdentityId: String, @Query("day") day: String
    ): Call<ScheduleDetailAPIResponse>

    @GET("schedule/{workItemId}")
    fun getWorkItemDetail(@Header("Authorization") auth: String, @Path("workItemId") workItemId: String): Call<WorkItemDetailAPIResponse>

    @GET("schedule/work-item/{workItemId}")
    fun getWorkItemContainerDetail(@Header("Authorization") auth: String, @Path("workItemId") workItemId: String): Call<WorkItemDetailAPIResponse>

    @PUT("schedule/lumpers")
    fun assignLumpers(
        @Header("Authorization") auth: String, @Query("containerId") containerId: String, @Body request: AssignLumpersRequest
    ): Call<BaseResponse>

    @HTTP(method = "DELETE", path = "schedule/lumpers", hasBody = true)
    fun removeLumperFromWork(@Header("Authorization") auth: String, @Query("containerId") containerId: String, @Body request: AssignLumpersRequest
    ): Call<BaseResponse>

        @POST("employees/lead/add-schedule")
    fun addSchedulesWorkItem(@Header("Authorization") auth: String, @Body request: AddContainerRequest
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Building Operations /////////////////////////////////////////////////
    @GET("schedule/{workItemId}/operations")
    fun getBuildingOperationsDetail(@Header("Authorization") auth: String, @Path("workItemId") workItemId: String): Call<BuildingOperationAPIResponse>

    @POST("schedule/{workItemId}/operations")
    fun saveBuildingOperationsDetail(
        @Header("Authorization") auth: String, @Path("workItemId") workItemId: String, @Body request: BuildingOperationRequest
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Attendance /////////////////////////////////////////////////
    @GET("employees/lead/attendance")
    fun getAttendanceList(@Header("Authorization") auth: String, @Query("day") day: String): Call<GetAttendanceAPIResponse>

    @POST("employees/lead/attendance")
    fun saveAttendanceDetails(@Header("Authorization") auth: String, @Query("day") day: String, @Body request: List<AttendanceDetail>): Call<BaseResponse>

    @GET("employees/lumpers/present")
    fun getPresentLumpersList(@Header("Authorization") auth: String, @Query("day") day: String): Call<AllLumpersResponse>
    /////////////////////////////////////////////////////////////

    // Schedule Lumper Time /////////////////////////////////////////////////
    @GET("employees/scheduled/lumpers")
    fun getScheduleTimeList(@Header("Authorization") auth: String, @Query("day") day: String): Call<GetScheduleTimeAPIResponse>

    @GET("employees/lead/work-info")
    fun getLeadWorkInfo(@Header("Authorization") auth: String, @Query("day") day: String): Call<GetLeadInfoResponse>

    @POST("employees/schedule/lumpers")
    fun saveScheduleTimeDetails(@Header("Authorization") auth: String, @Body request: ScheduleTimeRequest): Call<BaseResponse>

    @GET("employees/lead/lumpers/requests")
    fun getRequestLumpersList(@Header("Authorization") auth: String, @Query("day") day: String): Call<RequestLumpersListAPIResponse>

    @POST("employees/lead/lumpers/request")
    fun createRequestLumpers(@Header("Authorization") auth: String, @Body request: RequestLumpersRequest): Call<BaseResponse>

    @PUT("employees/lead/lumpers/request/{requestId}")
    fun updateRequestLumpers(
        @Header("Authorization") auth: String, @Path("requestId") requestId: String, @Body request: RequestLumpersRequest
    ): Call<BaseResponse>

    @POST("employees/lumpers-request/cancel")
    fun cancelRequestLumpers(@Header("Authorization") auth: String, @Body request: CancelRequestLumpersRequest): Call<BaseResponse>

    @HTTP(method = "DELETE", path = "employees/scheduled/lumpers", hasBody = true)
    fun cancelScheduleLumper(@Header("Authorization") auth: String, @Query("day") day: String , @Body request: CancelLumperRequest): Call<BaseResponse>

    @PUT("employees/scheduled/lumpers/{lumperId}")
    fun editScheduleLumper(@Header("Authorization") auth: String, @Path("lumperId") lumperId: String, @Query("day") day: String, @Query("reportingTime") reportingTime: Long, @Body request: ScheduleTimeNoteRequest): Call<BaseResponse>

    /////////////////////////////////////////////////////////////

    // Work Sheet /////////////////////////////////////////////////
    @GET("buildings/workitems")
    fun getWorkSheetList(@Header("Authorization") auth: String, @Query("day") day: String): Call<WorkSheetListAPIResponse>

    @PUT("schedule/status/workitem/{workItemId}")
    fun changeWorkItemStatus(
        @Header("Authorization") auth: String, @Path("workItemId") workItemId: String, @Body request: ChangeStatusRequest
    ): Call<BaseResponse>

    @PUT("schedule/update/{workItemId}")
    fun updateWorkItemNotes(
        @Header("Authorization") auth: String, @Path("workItemId") workItemId: String, @Body request: UpdateNotesRequest
    ): Call<BaseResponse>

    @POST("employees/timings")
    fun updateLumperTimeInWorkItem(@Header("Authorization") auth: String, @Body request: UpdateLumperTimeRequest): Call<BaseResponse>

    @POST("schedule/cancel/all/{day}")
    fun cancelAllSchedules(
        @Header("Authorization") auth: String, @Path("day") day: String, @Body request: CancelAllSchedulesRequest
    ): Call<BaseResponse>

    @POST("schedule/notes")
    fun saveGroupNoteSchedules(@Header("Authorization") auth: String, @Query("day") day: String, @Body request: SaveNoteWorkItemRequest
    ): Call<BaseResponse>

    @PUT("schedule/notes/{noteId}")
    fun updateGroupNoteSchedules(@Header("Authorization") auth: String, @Path("noteId") noteId: String, @Body request: UpdateGroupNoteRequest
    ): Call<BaseResponse>

    @DELETE("schedule/notes")
    fun saveGroupNoteSchedules(@Header("Authorization") auth: String, @Query("id") id: String): Call<BaseResponse>

    /////////////////////////////////////////////////////////////

    // Customer Sheet /////////////////////////////////////////////////
    @GET("customers/sheet")
    fun getCustomerSheetList(@Header("Authorization") auth: String, @Query("day") day: String): Call<CustomerSheetListAPIResponse>

    @Multipart
    @POST("customers/sheet")
    fun saveCustomerSheet(
        @Header("Authorization") auth: String,
        @Query ("day") date: String,
        @Part("customerRepresentativeName") customerRepresentativeName: RequestBody,
        @Part("note") note: RequestBody,
        @Part signature: MultipartBody.Part? = null,
        @Part("customerSheetId") customerIdBody: RequestBody
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Lumper Sheet /////////////////////////////////////////////////
    @GET("employees/lumper-signature")
    fun getLumperSheetList(@Header("Authorization") auth: String, @Query("day") day: String): Call<LumperSheetListAPIResponse>

    @GET("employees/daily/lumpers-worksheet")
    fun getLumperWorkDetail(
        @Header("Authorization") auth: String, @Query("day") day: String, @Query("lumperId") lumperId: String
    ): Call<LumperWorkDetailAPIResponse>

    @Multipart
    @POST("employees/lumper-signature")
    fun saveLumperSignature(
        @Header("Authorization") auth: String, @Part("day") day: RequestBody,
        @Part("lumperId") lumperId: RequestBody, @Part signature: MultipartBody.Part
    ): Call<BaseResponse>

    @POST("employees/lumpersheet/finalize")
    fun submitLumperSheet(@Header("Authorization") auth: String, @Body request: SubmitLumperSheetRequest): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Reports /////////////////////////////////////////////////
    @POST("employees/worksheet/reports/lumpers")
    fun createLumperJobReport(
        @Header("Authorization") auth: String, @Query("start") startDate: String,
        @Query("end") endDate: String, @Query("type") type: String, @Body request: ReportRequest
    ): Call<ReportResponse>

    @POST("employees/timeclock/reports/lumpers")
    fun createTimeClockReport(
        @Header("Authorization") auth: String, @Query("start") startDate: String,
        @Query("end") endDate: String, @Query("type") type: String, @Body request: ReportRequest
    ): Call<ReportResponse>

    @POST("customers/reports")
    fun createCustomerReport(
        @Header("Authorization") auth: String, @Query("start") startDate: String,
        @Query("end") endDate: String, @Query("type") type: String
    ): Call<ReportResponse>

    @GET("employees/lead/lumpers/date-range")
    fun getAllLumpersSelectedDates(@Header("Authorization") auth: String, @Query("dayStart") dayStart: String, @Query("dayEnd") dayEnd: String): Call<LumperListAPIResponse>
    /////////////////////////////////////////////////////////////

    //QHL Contact////////////////////////////////////////////////
    @GET("employees/lead/qhl-contacts")
    fun getQhlContactList(@Header("Authorization") auth: String): Call<QhlContactListResponse>

    @GET("employees/admin/office")
    fun getQhlOfficeInfo(@Header("Authorization") auth: String): Call<QhlOfficeInfoResponse>
    ////////////////////////////////////////////////////////////

    //Customer Contact/////////////////////////////////////////
    @GET("employees/lead/customer-contacts")
    fun getCustomerContactList(@Header("Authorization") auth: String): Call<QhlContactListResponse>
    ////////////////////////////////////////////////////////////

    //Upload Image/////////////////////////////////////////////
    @Multipart
    @POST("employees/upload/image")
    fun uploadImage(@Header("Authorization") auth: String, @Part image: MultipartBody.Part): Call<UploadImageResponse>
    ///////////////////////////////////////////////////////////

    //Building Details////////////////////////////////////////
    @GET("employees/lead/buildings/{buildingId}")
    fun getBuildingDetails(@Header("Authorization") auth: String, @Path("buildingId") buildingId: String
    ): Call<BuildingDetailsResponse>
    ////////////////////////////////////////////////////////

    //past future date calender details
    @GET("schedule/meta/lookup-availability")
    fun schedulePastFutureDate(@Header("Authorization") auth: String): Call<GetPastFutureDateResponse>

    @GET("employees/meta/lumpers-signature")
    fun lumperSheetPastFutureDate(@Header("Authorization") auth: String): Call<GetPastFutureDateResponse>

    @GET("employees/meta/lumpers-availability")
    fun scheduleTimePastFutureDate(@Header("Authorization") auth: String): Call<GetPastFutureDateResponse>

    @GET("employees/meta/lumpers-timeclock")
    fun timeClockPastFutureDate(@Header("Authorization") auth: String): Call<GetPastFutureDateResponse>
    /////////////////////////////////////////////////////////

    //Request Correction/////////////////////////////////////
    @POST("schedule/containers/{id}/correction-requests")
    fun saveLumperRequestCorrection(@Header("Authorization") auth: String, @Path("id") containerId: String, @Body request: LumperCorrectionRequest): Call<BaseResponse>

    @POST("employees/submit-past-corrections")
    fun editLumperParams(@Header("Authorization") auth: String, @Query("containerId") containerId: String, @Body request: LumperCorrectionRequest): Call<BaseResponse>

    @POST("schedule/containers/correction-requests/cancel/{id}")
    fun lumperCancelCorrection(@Header("Authorization") auth: String, @Path("id") correctionId: String): Call<BaseResponse>
    /////////////////////////////////////////////////////////

    //Chats/////////////////////////////////////////////////
    @POST("employees/communicate/{id}")
    fun contactChat(@Header("Authorization") auth: String, @Path("id") buildingId: String, @Body request: ChatMessageRequest): Call<BaseResponse>

    @POST("employees/preferred-language/toggle")
    fun changeLanguage(@Header("Authorization") auth: String): Call<BaseResponse>


}