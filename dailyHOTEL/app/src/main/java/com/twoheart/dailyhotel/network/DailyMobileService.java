package com.twoheart.dailyhotel.network;

import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.Constants;

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
    @GET("api/v3/common/version")
    Call<JSONObject> requestCommonVersion();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/common/datetime")
    Call<JSONObject> requestCommonDateTime();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/users/profile")
    Call<JSONObject> requestUserProfile();

    @GET("user/session/bonus/all")
    Call<JSONObject> requestUserBonus();

    @FormUrlEncoded
    @POST("api/v4/users/profile")
    Call<JSONObject> requestUserInformationUpdate(@FieldMap Map<String, String> params);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/users/profile/benefit")
    Call<JSONObject> requestUserProfileBenefit();

    @FormUrlEncoded
    @POST("user/check/email_auth")
    Call<JSONObject> requestUserCheckEmail(@Field("userEmail") String userEmail);

    @FormUrlEncoded
    @POST("user/change_pw")
    Call<JSONObject> requestUserChangePassword(@Field("userEmail") String userEmail);

    @GET("api/user/information")
    Call<JSONObject> requestUserInformationForPayment();

    @FormUrlEncoded
    @POST("api/user/session/update/fb_user")
    Call<JSONObject> requestUserUpdateInformationForSocial(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/user/session/billing/card/info")
    Call<JSONObject> requestUserBillingCardList();

    @FormUrlEncoded
    @POST("api/user/session/billing/card/del")
    Call<JSONObject> requestUserDeleteBillingCard(@Field("billkey") String billkey);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/hotels/sales")
    Call<JSONObject> requestStayList(@QueryMap Map<String, Object> queryMap, @Query("bedType") List<String> bedTypeList, @Query("luxury") List<String> luxuryList);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/hotels/sales/search/auto_complete")
    Call<JSONObject> requestStaySearchAutoCompleteList(@Query("dateCheckIn") String date, @Query("stays") int stays, @Query("term") String term);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/hotel/region")
    Call<JSONObject> requestStayRegionList();

    @GET("api/hotel/v1/payment/detail")
    Call<JSONObject> requestStayPaymentInformation(@Query("room_idx") int roomIndex,//
                                                    @Query("checkin_date") String date,//
                                                    @Query("nights") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/hotel/{hotelIdx}")
    Call<JSONObject> requestStayDetailInformation(@Path("hotelIdx") int index, @Query("dateCheckIn") String date, @Query("stays") int nights);

    @FormUrlEncoded
    @POST("api/hotel/v1/payment/session/easy")
    Call<JSONObject> requestStayPayment(@FieldMap Map<String, String> fieldMap);

    @FormUrlEncoded
    @POST("api/fnb/reservation/session/rating/msg/update")
    Call<JSONObject> requestGourmetDetailRating(@FieldMap Map<String, String> fieldMap);

    @GET("api/fnb/reservation/booking/list")
    Call<JSONObject> requestBookingList();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v2/reservation/fnb/{fnbReservationIdx}")
    Call<JSONObject> requestGourmetBookingDetailInformation(@Path("fnbReservationIdx") int index);

    @GET("api/v2/reservation/fnb/{reservationIdx}/receipt")
    Call<JSONObject> requestGourmetReceipt(@Query("reservation_rec_idx") int index);

    @FormUrlEncoded
    @POST("api/fnb/reservation/session/hidden")
    Call<JSONObject> requestGourmetHiddenBooking(@Field("reservation_rec_idx") int index);

    @GET("api/fnb/reservation/session/vbank/account/info")
    Call<JSONObject> requestGourmetAccountInformation(@Query("tid") String tid);

    @GET("gourmet/region/v1/list")
    Call<JSONObject> requestGourmetRegionList();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/gourmet/sales")
    Call<JSONObject> requestGourmetList(@QueryMap Map<String, Object> queryMap,//
                                        @Query("category")List<String> categoryList,//
                                        @Query("timeFrame")List<String> timeList,//
                                        @Query("luxury") List<String> luxuryList);

    @GET("api/v3/gourmet/sales/search/auto_complete")
    Call<JSONObject> requestGourmetSearchAutoCompleteList(@Query("reserveDate") String date, @Query("term") String term);

    @GET("api/v3/gourmet/{restaurantIdx}")
    Call<JSONObject> requestGourmetDetailInformation(@Path("restaurantIdx") int index, @Query("dateSale") String date);

    @GET("api/fnb/sale/ticket/payment/info")
    Call<JSONObject> requestGourmetPaymentInformation(@Query("sale_reco_idx") int index);

    @GET("api/fnb/sale/session/ticket/sell/check")
    Call<JSONObject> requestGourmetCheckTicket(@Query("sale_reco_idx") int index,//
                                               @Query("sday") String day,//
                                               @Query("ticket_count") int count,//
                                               @Query("arrival_time") String time);

    @FormUrlEncoded
    @POST("api/fnb/payment/session/easy")
    Call<JSONObject> requestGourmetPayment(@FieldMap Map<String, String> fieldMap);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v2/reservation/account/{tid}")
    Call<JSONObject> requestDepositWaitDetailInformation(@Path("tid") String tid);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v2/reservation/hotel/{hotelReservationIdx}")
    Call<JSONObject> requestStayBookingDetailInformation(@Path("hotelReservationIdx") int index);

    @FormUrlEncoded
    @POST("api/reserv/mine/hidden")
    Call<JSONObject> requestStayHiddenBooking(@Field("idx") int index);

    @GET("api/reserv/receipt")
    Call<JSONObject> requestStayReceipt(@Query("reservation_idx") String index);

    @GET("api/daily/event/list")
    Call<JSONObject> requestEventList();

    @GET("api/v1/notice/new")
    Call<JSONObject> requestEventNCouponNNoticeNewCount(@Query("eventLatestDate") String eventLatestDate,//
                                                        @Query("couponLatestDate") String couponLatestDate,//
                                                        @Query("noticesLatestDate") String noticeLatestDate);

    @GET("api/daily/event/page")
    Call<JSONObject> requestEventPageUrl(@Query("daily_event_idx") int eventIndex, @Query("store_type") String store);

    @GET("event/v1/banner")
    Call<JSONObject> requestEventBannerList(@Query("type") String place);

    @FormUrlEncoded
    @POST("api/v3/users/myself/phones/verification/start")
    Call<JSONObject> requestDailyUserVerification(@Field("phone") String phone, @Field("force_to_proceed") boolean force);

    @FormUrlEncoded
    @POST("api/v3/users/myself/phones/verification/check")
    Call<JSONObject> requestDailyUserUpdatePhoneNumber(@Field("phone") String phone, @Field("code") String code);

    @FormUrlEncoded
    @POST("api/v3/users/signup/normal/validation")
    Call<JSONObject> requestSignupValidation(@FieldMap Map<String, String> fieldMap);

    @FormUrlEncoded
    @POST("api/v3/users/signup/normal/phones/verification/start")
    Call<JSONObject> requestDailyUserSignupVerfication(@Field("signup_key") String signupKey,//
                                                       @Field("phone") String phone,//
                                                       @Field("force_to_proceed") boolean force);

    @FormUrlEncoded
    @POST("api/v3/users/signup/normal/phones/verification/check")
    Call<JSONObject> requestDailyUserSignup(@Field("signup_key") String signupKey,//
                                            @Field("code") String code, @Field("phone") String phone);

    @FormUrlEncoded
    @POST("api/v3/users/signup/{type}")
    Call<JSONObject> requestUserSignup(@Path("type") String type, @FieldMap Map<String, String> fieldMap);

    @FormUrlEncoded
    @POST("api/v3/users/signin/{type}")
    Call<JSONObject> requestUserSignin(@Path("type") String type, @FieldMap Map<String, String> fieldMap);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/users/coupons")
    Call<JSONObject> requestCouponList();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v2/payment/coupons")
    Call<JSONObject> requestCouponList(@Query("hotelIdx") int hotelIdx, @Query("roomIdx") int roomIdx, //
                                       @Query("checkIn") String checkIn, @Query("checkOut") String checkOut);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/gourmet/payment/coupons")
    Call<JSONObject> requestCouponList(@Query("saleIdx") int saleIdx, @Query("countOfTicket") int countOfTicket);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/users/coupons/history")
    Call<JSONObject> requestCouponHistoryList();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v1/notice/agreement/confirm")
    Call<JSONObject> requestNoticeAgreement();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("api/v1/notice/agreement/result")
    Call<JSONObject> requestNoticeAgreementResult(@Query("isAgreed") boolean isAgree);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v1/notice/benefit")
    Call<JSONObject> requestBenefitMessage();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("api/v3/users/coupons/download")
    Call<JSONObject> requestDownloadCoupon(@Query("userCouponCode") String userCouponCode);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("api/v3/users/coupons/download")
    Call<JSONObject> requestDownloadEventCoupon(@Query("couponCode") String couponCode);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/hotel/{hotelIdx}/coupons/exist")
    Call<JSONObject> requestHasCoupon(@Path("hotelIdx") int placeIndex, @Query("dateCheckIn") String date,//
                                      @Query("stays") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/gourmet/{restaurantIdx}/coupons/exist")
    Call<JSONObject> requestHasCoupon(@Path("restaurantIdx") int placeIndex, @Query("dateSale") String dateSale);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/hotel/{hotelIdx}/coupons")
    Call<JSONObject> requestCouponList(@Path("hotelIdx") int placeIndex, @Query("dateCheckIn") String date,//
                                       @Query("stays") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/gourmet/{restaurantIdx}/coupons")
    Call<JSONObject> requestCouponList(@Path("restaurantIdx") int placeIndex, @Query("dateSale") String dateSale);

    @FormUrlEncoded
    @POST("api/v3/users/coupons/keyword")
    Call<JSONObject> requestRegisterKeywordCoupon(@Field("keyword") String keyword);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("api/v1/notice/benefit")
    Call<JSONObject> requestUpdateBenefitAgreement(@Query("isAgreed") boolean isAgree);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/users/tracking")
    Call<JSONObject> requestUserTracking();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/common/notices")
    Call<JSONObject> requestNoticeList();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/hotels/sales")
    Call<JSONObject> requestRecentStayList(@QueryMap Map<String, String> stayParams);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v3/gourmet/sales")
    Call<JSONObject> requestRecentGourmetList(@QueryMap Map<String, String> gourmetParams);

    @POST("api/v3/users/reservations/{kind}/{reservationIdx}/receipts")
    Call<JSONObject> requestReceiptByEmail(@Path("kind") String placeType, //
                                           @Path("reservationIdx") String reservationIdx, //
                                           @Query("emails") String emails);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v4/wishes")
    Call<JSONObject> requestWishListCount();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v4/wishes/{type}")
    Call<JSONObject> requestWishList(@Path("type") String placeType);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("api/v4/wishes/{type}/add/{itemIdx}")
    Call<JSONObject> requestAddWishList(@Path("type") String placeType, @Path("itemIdx") int placeIndex);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("api/v4/wishes/{type}/remove/{itemIdx}")
    Call<JSONObject> requestRemoveWishList(@Path("type") String placeType, @Path("itemIdx") int placeIndex);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v2/payment/policy_refund")
    Call<JSONObject> requestPolicyRefund(@Query("hotelIdx") int hotelIdx, @Query("roomIdx") int roomIdx,//
                                         @Query("dateCheckIn") String dateCheckIn, @Query("transactionType") String transactionType);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v2/payment/policy_refund")
    Call<JSONObject> requestPolicyRefund(@Query("hotelIdx") int hotelIdx, @Query("dateCheckIn") String dateCheckIn);

    @FormUrlEncoded
    @POST("api/v2/payment/refund")
    Call<JSONObject> requestRefund(@Field("hotelIdx") int hotelIdx, @Field("dateCheckIn") String dateCheckIn,//
                                   @Field("transactionType") String transactionType, @Field("hotelReservationIdx") int hotelReservationIdx, //
                                   @Field("reasonRefund") String reasonRefund, @Field("accountHolder") String accountHolder, //
                                   @Field("bankAccount") String bankAccount, @Field("bankCode") String bankCode);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v2/payment/bank")
    Call<JSONObject> requestBankList();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v4/review/hotel/question")
    Call<JSONObject> requestStayReviewInformation();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v4/review/gourmet/question")
    Call<JSONObject> requestGourmetReviewInformation();

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v4/review/hotel/{reserveIdx}/question")
    Call<JSONObject> requestStayReviewInformation(@Path("reserveIdx") int reserveIdx);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("api/v4/review/gourmet/{reserveIdx}/question")
    Call<JSONObject> requestGourmetReviewInformation(@Path("reserveIdx") int reserveIdx);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("api/v4/review/add")
    Call<JSONObject> requestAddReviewInformation(@Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("api/v4/review/add/detail")
    Call<JSONObject> requestAddReviewDetailInformation(@Body JSONObject jsonObject);
}
