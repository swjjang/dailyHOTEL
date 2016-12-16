package com.twoheart.dailyhotel.network;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface DailyMobileService
{
    @GET()
    Call<JSONObject> requestStatusServer(@Url String url);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCommonVersion(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCommonDateTime(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestUserProfile(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @GET("{mobileAPI}")
    Call<JSONObject> requestUserBonus(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestUserInformationUpdate(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @FieldMap Map<String, String> params);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestUserProfileBenefit(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestUserCheckEmail(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("userEmail") String userEmail);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestUserChangePassword(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("userEmail") String userEmail);

    @GET("{mobileAPI}")
    Call<JSONObject> requestUserInformationForPayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestUserUpdateInformationForSocial(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @FieldMap Map<String, String> params);

    @POST("{mobileAPI}")
    Call<JSONObject> requestUserBillingCardList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestUserDeleteBillingCard(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("billkey") String billkey);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestStayList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                     @QueryMap Map<String, Object> queryMap, //
                                     @Query("bedType") List<String> bedTypeList, //
                                     @Query("luxury") List<String> luxuryList);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestStaySearchAutoCompleteList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                       @Query("dateCheckIn") String date, //
                                                       @Query("stays") int stays, //
                                                       @Query("term") String term);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestStayRegionList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @GET("{mobileAPI}")
    Call<JSONObject> requestStayPaymentInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                   @Query("room_idx") int roomIndex,//
                                                   @Query("checkin_date") String date,//
                                                   @Query("nights") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestStayDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("dateCheckIn") String date, @Query("stays") int nights);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestStayPayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @FieldMap Map<String, String> fieldMap);

    @GET("{mobileAPI}")
    Call<JSONObject> requestBookingList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetBookingDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetReceipt(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestGourmetHiddenBooking(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("reservation_rec_idx") int index);

    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetAccountInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("tid") String tid);

    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetRegionList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                        @QueryMap Map<String, Object> queryMap,//
                                        @Query("category") List<String> categoryList,//
                                        @Query("timeFrame") List<String> timeList,//
                                        @Query("luxury") List<String> luxuryList);

    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetSearchAutoCompleteList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("reserveDate") String date, @Query("term") String term);

    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("dateSale") String date);

    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetPaymentInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("sale_reco_idx") int index);

    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetCheckTicket(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                               @Query("sale_reco_idx") int index,//
                                               @Query("sday") String day,//
                                               @Query("ticket_count") int count,//
                                               @Query("arrival_time") String time);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestGourmetPayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @FieldMap Map<String, String> fieldMap);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestDepositWaitDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestStayBookingDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestStayHiddenBooking(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("idx") int index);

    @GET("{mobileAPI}")
    Call<JSONObject> requestStayReceipt(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("reservation_idx") String index);

    @GET("{mobileAPI}")
    Call<JSONObject> requestEventList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @GET("{mobileAPI}")
    Call<JSONObject> requestEventNCouponNNoticeNewCount(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                        @Query("eventLatestDate") String eventLatestDate,//
                                                        @Query("couponLatestDate") String couponLatestDate,//
                                                        @Query("noticesLatestDate") String noticeLatestDate);

    @GET("{mobileAPI}")
    Call<JSONObject> requestEventPageUrl(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                         @Query("daily_event_idx") int eventIndex, //
                                         @Query("store_type") String store);

    @GET("{mobileAPI}")
    Call<JSONObject> requestEventBannerList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("type") String place);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestDailyUserVerification(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                  @Field("phone") String phone, //
                                                  @Field("force_to_proceed") boolean force);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestDailyUserUpdatePhoneNumber(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                       @Field("phone") String phone, //
                                                       @Field("code") String code);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestSignupValidation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @FieldMap Map<String, String> fieldMap);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestDailyUserSignupVerfication(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                       @Field("signup_key") String signupKey,//
                                                       @Field("phone") String phone,//
                                                       @Field("force_to_proceed") boolean force);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestDailyUserSignup(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                            @Field("signup_key") String signupKey,//
                                            @Field("code") String code, @Field("phone") String phone);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestUserSignup(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @FieldMap Map<String, String> fieldMap);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestUserLogin(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @FieldMap Map<String, String> fieldMap);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCouponList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCouponList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                       @Query("hotelIdx") int hotelIdx, @Query("roomIdx") int roomIdx, //
                                       @Query("checkIn") String checkIn, @Query("checkOut") String checkOut);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCouponList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                       @Query("saleIdx") int saleIdx, @Query("countOfTicket") int countOfTicket);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCouponHistoryList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestNoticeAgreement(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("{mobileAPI}")
    Call<JSONObject> requestNoticeAgreementResult(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("isAgreed") boolean isAgree);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestBenefitMessage(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("{mobileAPI}")
    Call<JSONObject> requestDownloadCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("userCouponCode") String userCouponCode);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("{mobileAPI}")
    Call<JSONObject> requestDownloadEventCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("couponCode") String couponCode);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestHasCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                      @Query("dateCheckIn") String date, @Query("stays") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestHasCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("dateSale") String dateSale);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCouponList(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                       @Query("dateCheckIn") String date, @Query("stays") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCouponList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("dateSale") String dateSale);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestRegisterKeywordCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("keyword") String keyword);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("{mobileAPI}")
    Call<JSONObject> requestUpdateBenefitAgreement(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("isAgreed") boolean isAgree);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestUserTracking(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestNoticeList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @POST("{mobileAPI}")
    Call<JSONObject> requestReceiptByEmail(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                           @Query("emails") String emails);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestWishListCount(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestWishList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Call<JSONObject> requestAddWishList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Call<JSONObject> requestRemoveWishList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestPolicyRefund(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                         @Query("hotelIdx") int hotelIdx, @Query("roomIdx") int roomIdx,//
                                         @Query("dateCheckIn") String dateCheckIn, @Query("dateCheckOut") String dateCheckOut);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestPolicyRefund(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                         @Query("hotelReservationIdx") int hotelReservationIdx, //
                                         @Query("transactionType") String transactionType);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestRefund(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                   @Field("hotelIdx") int hotelIdx, @Field("dateCheckIn") String dateCheckIn,//
                                   @Field("transactionType") String transactionType, @Field("hotelReservationIdx") int hotelReservationIdx, //
                                   @Field("reasonRefund") String reasonRefund, @Field("accountHolder") String accountHolder, //
                                   @Field("bankAccount") String bankAccount, @Field("bankCode") String bankCode);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestBankList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestReviewInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Call<JSONObject> requestAddReviewInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                 @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Call<JSONObject> requestAddReviewDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                       @Body JSONObject jsonObject);
}
