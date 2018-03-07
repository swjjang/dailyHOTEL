package com.twoheart.dailyhotel.network;

import com.daily.dailyhotel.repository.remote.model.BankData;
import com.daily.dailyhotel.repository.remote.model.BookingCancelData;
import com.daily.dailyhotel.repository.remote.model.BookingData;
import com.daily.dailyhotel.repository.remote.model.BookingHiddenData;
import com.daily.dailyhotel.repository.remote.model.BookingHideData;
import com.daily.dailyhotel.repository.remote.model.CampaignTagData;
import com.daily.dailyhotel.repository.remote.model.CardData;
import com.daily.dailyhotel.repository.remote.model.CommonDateTimeData;
import com.daily.dailyhotel.repository.remote.model.ConfigurationsData;
import com.daily.dailyhotel.repository.remote.model.CouponsData;
import com.daily.dailyhotel.repository.remote.model.ExistCouponsData;
import com.daily.dailyhotel.repository.remote.model.GoogleAddressData;
import com.daily.dailyhotel.repository.remote.model.GourmetBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.GourmetCampaignTagsData;
import com.daily.dailyhotel.repository.remote.model.GourmetDetailData;
import com.daily.dailyhotel.repository.remote.model.GourmetListData;
import com.daily.dailyhotel.repository.remote.model.GourmetMultiBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.GourmetOldWaitingDepositData;
import com.daily.dailyhotel.repository.remote.model.GourmetPaymentData;
import com.daily.dailyhotel.repository.remote.model.GourmetReceiptData;
import com.daily.dailyhotel.repository.remote.model.GourmetSuggestsData;
import com.daily.dailyhotel.repository.remote.model.LeaveInfoData;
import com.daily.dailyhotel.repository.remote.model.NotificationData;
import com.daily.dailyhotel.repository.remote.model.OldRefundData;
import com.daily.dailyhotel.repository.remote.model.PaymentResultData;
import com.daily.dailyhotel.repository.remote.model.RecentlyPlacesData;
import com.daily.dailyhotel.repository.remote.model.RefundPolicyData;
import com.daily.dailyhotel.repository.remote.model.ReviewData;
import com.daily.dailyhotel.repository.remote.model.ReviewScoresData;
import com.daily.dailyhotel.repository.remote.model.RewardCardHistoryDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardHistoryDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardInformationData;
import com.daily.dailyhotel.repository.remote.model.ShortUrlData;
import com.daily.dailyhotel.repository.remote.model.StayAreasData;
import com.daily.dailyhotel.repository.remote.model.StayBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.StayCampaignTagsData;
import com.daily.dailyhotel.repository.remote.model.StayDetailData;
import com.daily.dailyhotel.repository.remote.model.StayFilterCountData;
import com.daily.dailyhotel.repository.remote.model.StayOldWaitingDepositData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundDetailData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundEmailReceiptData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundPaymentData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundReceiptData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundRefundDetailData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundSuggestsData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundWishCountData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundsData;
import com.daily.dailyhotel.repository.remote.model.StayPaymentData;
import com.daily.dailyhotel.repository.remote.model.StayReceiptData;
import com.daily.dailyhotel.repository.remote.model.StayRefundPolicyData;
import com.daily.dailyhotel.repository.remote.model.StayRegionData;
import com.daily.dailyhotel.repository.remote.model.StaySuggestsData;
import com.daily.dailyhotel.repository.remote.model.StaysData;
import com.daily.dailyhotel.repository.remote.model.SubwayAreasData;
import com.daily.dailyhotel.repository.remote.model.TrueReviewsData;
import com.daily.dailyhotel.repository.remote.model.TrueVRData;
import com.daily.dailyhotel.repository.remote.model.UserBenefitData;
import com.daily.dailyhotel.repository.remote.model.UserData;
import com.daily.dailyhotel.repository.remote.model.UserInformationData;
import com.daily.dailyhotel.repository.remote.model.UserTrackingData;
import com.daily.dailyhotel.repository.remote.model.WaitingDepositData;
import com.daily.dailyhotel.repository.remote.model.WishCountData;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.dto.GoogleMapListDto;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.network.model.GourmetWishItem;
import com.twoheart.dailyhotel.network.model.Holiday;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.PlaceReviews;
import com.twoheart.dailyhotel.network.model.PlaceWishItems;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.network.model.Status;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayKeyword;
import com.twoheart.dailyhotel.network.model.StayWishItem;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.network.model.TrueVRParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
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
    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Call<BaseDto<Status>> requestStatusServer(@Url String url);

    @GET()
    Call<JSONObject> requestHappyTalkCategory(@Url String url);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCommonVersion(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestCommonDateTime(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseDto<TodayDateTime>> requestCommonDateTimeRefactoring(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

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
    Call<BaseDto<Object>> requestUserChangePassword(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("email") String userEmail);

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
    Call<JSONObject> requestStayList(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @QueryMap Map<String, Object> queryMap//
        , @Query("bedType") List<String> bedTypeList//
        , @Query("luxury") List<String> luxuryList//
        , @Query("abtest") String abtest);

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
    Call<BaseDto<StayDetailParams>> requestStayDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("dateCheckIn") String date, @Query("stays") int nights);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestStayPayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @FieldMap Map<String, String> fieldMap);

    @GET("{mobileAPI}")
    Call<JSONObject> requestBookingList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetReservationDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetReceipt(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestGourmetHiddenBooking(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("reservation_rec_idx") int index);

    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetAccountInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("tid") String tid);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetRegionList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                        @QueryMap Map<String, Object> queryMap,//
                                        @Query("category") List<String> categoryList,//
                                        @Query("timeFrame") List<String> timeList,//
                                        @Query("luxury") List<String> luxuryList);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseDto<GourmetDetailParams>> requestGourmetDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("dateSale") String date);

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
    Call<JSONObject> requestStayReservationDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Call<JSONObject> requestStayHiddenBooking(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("idx") int index);

    @GET("{mobileAPI}")
    Call<JSONObject> requestStayReceipt(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("reservation_idx") String index);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseListDto<Event>> requestEventList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("storeType") String store);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestEventNCouponNNoticeNewCount(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                        @Query("eventLatestDate") String eventLatestDate,//
                                                        @Query("couponLatestDate") String couponLatestDate,//
                                                        @Query("noticesLatestDate") String noticeLatestDate);

    @GET("{mobileAPI}")
    Call<JSONObject> requestEventPageUrl(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                         @Query("daily_event_idx") int eventIndex, //
                                         @Query("store_type") String store);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
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
    Call<JSONObject> requestDailyUserSignupVerification(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
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
    Call<JSONObject> requestDownloadCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("couponCode") String couponCode);

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

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseListDto<TrueVRParams>> requestHasVRList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("type") String type);

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
    Call<BaseDto<PlaceWishItems<StayWishItem>>> requestStayWishList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseDto<PlaceWishItems<GourmetWishItem>>> requestGourmetWishList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

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
    @GET()
    Call<JSONObject> requestStayOutboundReviewInformation(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Call<JSONObject> requestAddReviewInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                 @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Call<JSONObject> requestStayOutboundAddReviewInformation(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Call<JSONObject> requestAddReviewDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                       @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Call<JSONObject> requestStayOutboundAddReviewDetailInformation(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseListDto<Holiday>> requestHoliday(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("from") String startDay, @Query("to") String endDay, @Query("isNationalHoliday") boolean isNationalHoliday);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseListDto<Event>> requestHomeEvents(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                               @Query("storeType") String storeType);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseListDto<Recommendation>> requestRecommendationList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseDto<RecommendationPlaceList<RecommendationStay>>> requestRecommendationStayList(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("salesDate") String salesDate, @Query("period") int period);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseDto<RecommendationPlaceList<RecommendationGourmet>>> requestRecommendationGourmetList(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("salesDate") String salesDate, @Query("period") int period);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseDto<RecentlyPlacesData>> requestHomeWishList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestIssuingCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseDto<PlaceReviewScores>> requestPlaceReviewScores(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseDto<PlaceReviews>> requestPlaceReviews(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("page") int page, @Query("limit") int limit, @Query("sortProperty") String sortProperty, @Query("sortDirection") String sortDirection);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestLocalPlus(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                      @QueryMap Map<String, Object> queryMap);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<JSONObject> requestStayCategoryRegions(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // RxJava2 API
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // CommonRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<CommonDateTimeData>> getCommonDateTime(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<ReviewData>> getReview(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<ReviewData>> getStayOutboundReview(@Url String mobileAPI);

    @Headers({"Content-type: application/json"})
    @POST()
    Observable<ShortUrlData> getShortUrl(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("{mobileAPI}")
    Observable<BaseDto<NotificationData>> updateNotification(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("isAgreed") boolean agreed);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<ConfigurationsData>> getConfigurations(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Content-type: application/json"})
    @GET()
    Observable<GoogleMapListDto<GoogleAddressData>> getSearchAddress(@Url String mobileAPI);


    /////////////////////////////////////////////////////////////////////////////////////////////////
    // ProfileRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<UserData>> getUserProfile(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<UserBenefitData>> getUserBenefit(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<UserTrackingData>> getUserTracking(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @GET("{mobileAPI}")
    Observable<BaseDto<UserInformationData>> getUserSimpleInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Observable<BaseDto<UserData>> updateUserInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @FieldMap Map<String, String> params);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<Object>> getCheckPassword(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("pw") String password);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // SuggestRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundSuggestsData>> getSuggestsByStayOutbound(@Url String mobileAPI, @Query(value = "keyword") String keyword);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundSuggestsData>> getPopularAreaSuggestsByStayOutbound(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<LeaveInfoData>> getLeaveInfo(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<Object>> doUserLeaveDaily(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query(value = "leaveReasonIdx") int leaveReasonIdx);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // StayOutboundRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundsData>> getStayOutboundList(@Url String mobileAPI//
        , @Query(value = "arrivalDate") String arrivalDate//
        , @Query(value = "departureDate") String departureDate//
        , @Query(value = "rooms[0].numberOfAdults") int numberOfAdults//
        , @Query(value = "rooms[0].numberOfChildren") int numberOfChildren//
        , @Query(value = "rooms[0].childAges") String childAges //
        , @Query(value = "numberOfRooms") int numberOfRooms//
        , @Query(value = "countryCode") String countryCode//
        , @Query(value = "city") String city//
        , @Query(value = "numberOfResults") int numberOfResults//
        , @Query(value = "cacheKey") String cacheKey//
        , @Query(value = "cacheLocation") String cacheLocation//
        , @Query(value = "apiExperience") String apiExperience// 디폴트 인자들
        , @Query(value = "locale") String locale// 디폴트 인자들
        , @Query(value = "sort") String sort// 디폴트 인자들
    );

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundsData>> getStayOutboundList(@Url String mobileAPI//
        , @Query(value = "arrivalDate") String arrivalDate//
        , @Query(value = "departureDate") String departureDate//
        , @Query(value = "rooms[0].numberOfAdults") int numberOfAdults//
        , @Query(value = "rooms[0].numberOfChildren") int numberOfChildren//
        , @Query(value = "rooms[0].childAges") String childAges //
        , @Query(value = "filter.maxStarRating") double maxStarRating //
        , @Query(value = "filter.minStarRating") double minStarRating //
        , @Query(value = "numberOfRooms") int numberOfRooms//
        , @Query(value = "geographyId") long geographyId//
        , @Query(value = "geographyType") String geographyType//
        , @Query(value = "numberOfResults") int numberOfResults//
        , @Query(value = "cacheKey") String cacheKey//
        , @Query(value = "cacheLocation") String cacheLocation//
        , @Query(value = "apiExperience") String apiExperience// 디폴트 인자들
        , @Query(value = "locale") String locale// 디폴트 인자들
        , @Query(value = "sort") String sort// 디폴트 인자들
    );

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundsData>> getStayOutboundList(@Url String mobileAPI//
        , @Query(value = "arrivalDate") String arrivalDate//
        , @Query(value = "departureDate") String departureDate//
        , @Query(value = "rooms[0].numberOfAdults") int numberOfAdults//
        , @Query(value = "rooms[0].numberOfChildren") int numberOfChildren//
        , @Query(value = "rooms[0].childAges") String childAges //
        , @Query(value = "filter.maxStarRating") double maxStarRating //
        , @Query(value = "filter.minStarRating") double minStarRating //
        , @Query(value = "numberOfRooms") int numberOfRooms//
        , @Query(value = "geographyId") long geographyId//
        , @Query(value = "geographyType") String geographyType//
        , @Query(value = "latitude") double latitude//
        , @Query(value = "longitude") double longitude//
        , @Query(value = "numberOfResults") int numberOfResults//
        , @Query(value = "cacheKey") String cacheKey//
        , @Query(value = "cacheLocation") String cacheLocation//
        , @Query(value = "apiExperience") String apiExperience// 디폴트 인자들
        , @Query(value = "locale") String locale// 디폴트 인자들
        , @Query(value = "sort") String sort// 디폴트 인자들
    );

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundsData>> getStayOutboundList(@Url String mobileAPI//
        , @Query(value = "filter.includeSurrounding") boolean filterIncludeSurrounding//
        , @Query(value = "filter.maxStarRating") int filterMaxStarRating//
        , @Query(value = "filter.minStarRating") int filterMinStarRating//
        , @Query(value = "arrivalDate") String arrivalDate//
        , @Query(value = "departureDate") String departureDate//
        , @Query(value = "rooms[0].numberOfAdults") int numberOfAdults//
        , @Query(value = "rooms[0].numberOfChildren") int numberOfChildren//
        , @Query(value = "rooms[0].childAges") String childAges //
        , @Query(value = "numberOfRooms") int numberOfRooms//
        , @Query(value = "latitude") double latitude//
        , @Query(value = "longitude") double longitude//
        , @Query(value = "searchRadius") int searchRadius//
        , @Query(value = "searchRadiusUnit") String searchRadiusUnit//
        , @Query(value = "numberOfResults") int numberOfResults//
        , @Query(value = "cacheKey") String cacheKey//
        , @Query(value = "cacheLocation") String cacheLocation//
        , @Query(value = "apiExperience") String apiExperience// 디폴트 인자들
        , @Query(value = "locale") String locale// 디폴트 인자들
        , @Query(value = "sort") String sort// 디폴트 인자들
    );

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<StayOutboundsData>> getStayOutboundList(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<StayOutboundDetailData>> getStayOutboundDetail(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<StayOutboundsData>> getStayOutboundRecommendAroundList(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundReceiptData>> getStayOutboundReceipt(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundEmailReceiptData>> getStayOutboundEmailReceipt(@Url String mobileAPI//
        , @Query("receiverEmail") String receiverEmail);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // GourmetRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<GourmetDetailData>> getGourmetDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("dateSale") String visitDate);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<ExistCouponsData>> getGourmetHasCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("dateSale") String visitDate);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<ReviewScoresData>> getReviewScores(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<TrueReviewsData>> getTrueReviews(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("page") int page, @Query("limit") int limit, @Query("sortProperty") String sortProperty, @Query("sortDirection") String sortDirection);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<TrueVRData>> getTrueReviews(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // StayRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StaysData>> getStayList(@Url String url);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayFilterCountData>> getStayListCountByFilter(@Url String url);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StayDetailData>> getStayDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("dateCheckIn") String date, @Query("stays") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<ExistCouponsData>> getStayHasCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("dateCheckIn") String date, @Query("stays") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StayAreasData>> getStayAreaList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<SubwayAreasData>> getStaySubwayAreaList(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("category") String category);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StayRegionData>> getStayCategoryAreaList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // WishRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<RecentlyPlacesData>> getHomeWishList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<PlaceWishItems<StayWishItem>>> getStayWishList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundsData>> getStayOutboundWishList(@Url String mobileAPI, @Query("searchMaxCount") int maxCount);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<String>> addStayOutboundWish(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<String>> removeStayOutboundWish(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<String>> addWish(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<String>> removeWish(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<WishCountData>> getWishCount(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundWishCountData>> getStayOutboundWishCount(@Url String mobileAPI);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // BookingRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseListDto<BookingData>> getStayOutboundBookingList(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<BookingData>> getBookingList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<BookingHideData>> getStayOutboundHideBooking(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundBookingDetailData>> getStayOutboundBookingDetail(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StayBookingDetailData>> getStayBookingDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Observable<BaseDto<BookingHiddenData>> getStayHiddenBooking(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Field("idx") int index);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<GourmetBookingDetailData>> getGourmetBookingDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<GourmetMultiBookingDetailData>> getGourmetMultiBookingDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<Object>> getGourmetHiddenBooking(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<WaitingDepositData>> getWaitingDeposit(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StayOldWaitingDepositData>> getStayOldWaitingDeposit(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<GourmetOldWaitingDepositData>> getGourmetOldWaitingDeposit( //
                                                                                   @Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("tid") String tid);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseListDto<BookingCancelData>> getStayOutboundBookingCancelList(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<BookingCancelData>> getBookingCancelList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<RefundPolicyData>> getRefundPolicy(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                          @Query("hotelReservationIdx") int hotelReservationIdx, //
                                                          @Query("transactionType") String transactionType);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // RefundRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundRefundDetailData>> getStayOutboundRefundDetail(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<StayOutboundRefundData>> getStayOutboundRefund(@Url String mobileAPI//
        , @Query("refundType") String refundType//
        , @Query("cancelReasonType") String cancelReasonType//
        , @Query("reasons") String reasons);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<Object>> getRefund(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Body JSONObject jsonObject);

    @FormUrlEncoded
    @POST("{mobileAPI}")
    Observable<BaseDto<OldRefundData>> getRefund(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                 @Field("hotelIdx") int hotelIdx, @Field("dateCheckIn") String dateCheckIn,//
                                                 @Field("transactionType") String transactionType, @Field("hotelReservationIdx") int hotelReservationIdx, //
                                                 @Field("reasonRefund") String reasonRefund, @Field("accountHolder") String accountHolder, //
                                                 @Field("bankAccount") String bankAccount, @Field("bankCode") String bankCode);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<BankData>> getBankList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // PaymentRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<StayOutboundPaymentData>> getStayOutboundPayment(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StayPaymentData>> getStayPayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("roomIdx") int roomIndex//
        , @Query("dateCheckIn") String date//
        , @Query("stays") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<GourmetPaymentData>> getGourmetPayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseListDto<CardData>> getEasyCardList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<PaymentResultData>> getStayOutboundPaymentTypeEasy(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<PaymentResultData>> getPaymentTypeEasy(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<PaymentResultData>> getStayOutboundPaymentTypeBonus(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<String>> getStayOutboundHasDuplicatePayment(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<String>> getStayHasDuplicatePayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("checkInDate") String checkInDate, @Query("days") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<PaymentResultData>> getPaymentTypeBonus(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StayRefundPolicyData>> getStayRefundPolicy(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @Query("hotelIdx") int stayIndex, @Query("roomIdx") int roomIndex//
        , @Query("dateCheckIn") String checkInDate, @Query("dateCheckOut") String checkOutDate);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // RecentlyRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundsData>> getStayOutboundRecentlyList(@Url String mobileAPI//
        , @Query(value = "filter.includeSurrounding") boolean includeSurrounding//
        , @Query(value = "hotelIds") String hotelIds//
        , @Query(value = "numberOfResults") int numberOfResults//
        , @Query(value = "sort") String sort//
    );

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<RecentlyPlacesData>> getInboundRecentlyList(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                                   @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StaysData>> getStayInboundList(@Path(value = "mobileAPI", encoded = true) String mobileAPI//
        , @QueryMap Map<String, Object> queryMap//
        , @Query("bedType") List<String> bedTypeList//
        , @Query("luxury") List<String> luxuryList//
        , @Query("abtest") String abtest);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<GourmetListData>> getGourmetList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                        @QueryMap Map<String, Object> queryMap,//
                                                        @Query("category") List<String> categoryList,//
                                                        @Query("timeFrame") List<String> timeList,//
                                                        @Query("luxury") List<String> luxuryList);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<String>> getStayUnavailableCheckInDates(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                   @Query("dateRange") int dateRange, //
                                                                   @Query("reverse") boolean isReverse);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<String>> getStayAvailableCheckOutDates(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                  @Query("dateRange") int dateRange, //
                                                                  @Query("dateCheckIn") String checkInDate);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<String>> getGourmetUnavailableDates(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                               @Query("dateRange") int dateRange, //
                                                               @Query("reverse") boolean isReverse);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<CampaignTagData>> getCampaignTagList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                @Query("serviceType") String serviceType);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StayCampaignTagsData>> getStayCampaignTags(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                  @Query("salesDate") String checkInDate, //
                                                                  @Query("period") int nights);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<GourmetCampaignTagsData>> getGourmetCampaignTags(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                        @Query("salesDate") String visitDate);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<StayKeyword>> getSuggestsByStayInbound(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                  @Query("dateCheckIn") String date, //
                                                                  @Query("stays") int stays, //
                                                                  @Query("term") String term);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<GourmetKeyword>> getSuggestsByGourmet(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                 @Query("reservationDate") String date, //
                                                                 @Query("term") String term);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<StaySuggestsData>> getSuggestsByStayInboundV2(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                   @Query("dateCheckIn") String date, //
                                                                   @Query("stays") int stays, //
                                                                   @Query("term") String term);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<GourmetSuggestsData>> getSuggestsByGourmetV2(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                  @Query("reservationDate") String date, //
                                                                  @Query("term") String term);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // CouponRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<CouponsData>> getCouponHistoryList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseDto<CouponsData>> getGourmetCouponListByPayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Body JSONArray jsonArray);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<CouponsData>> getStayCouponListByPayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                                @Query("hotelIdx") int stayIndex, @Query("roomIdx") int roomIndex, //
                                                                @Query("checkIn") String checkIn, @Query("checkOut") String checkOut);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<CouponsData>> getStayOutboundCouponListByPayment(@Url String mobileAPI, @Body JSONObject jsonObject);


    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @PUT("{mobileAPI}")
    Observable<BaseDto<Object>> getDownloadCoupon(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("couponCode") String couponCode);


    /////////////////////////////////////////////////////////////////////////////////////////////////
    // RewardRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<RewardInformationData>> getRewardStickerCount(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<RewardDetailData>> getRewardDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<RewardHistoryDetailData>> getRewardHistoryDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<RewardCardHistoryDetailData>> getRewardCardHistoryDetail(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // ReceiptRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<GourmetReceiptData>> getGourmetReceipt(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @POST("{mobileAPI}")
    Observable<BaseDto<Object>> getGourmetReceiptByEmail(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                         @Query("emails") String emails);

    @GET("{mobileAPI}")
    Observable<BaseDto<StayReceiptData>> getStayReceipt(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("reservation_idx") String index);

    @POST("{mobileAPI}")
    Observable<BaseDto<Object>> getStayReceiptByEmail(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                                      @Query("emails") String emails);
}
