package com.twoheart.dailyhotel.network;

import com.daily.dailyhotel.repository.remote.model.BookingData;
import com.daily.dailyhotel.repository.remote.model.CardData;
import com.daily.dailyhotel.repository.remote.model.CommonDateTimeData;
import com.daily.dailyhotel.repository.remote.model.PaymentResultData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundBookingDetailData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundDetailData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundPaymentData;
import com.daily.dailyhotel.repository.remote.model.StayOutboundsData;
import com.daily.dailyhotel.repository.remote.model.SuggestsData;
import com.daily.dailyhotel.repository.remote.model.UserBenefitData;
import com.daily.dailyhotel.repository.remote.model.UserData;
import com.daily.dailyhotel.repository.remote.model.UserInformationData;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetWishItem;
import com.twoheart.dailyhotel.network.model.Holiday;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.HomePlaces;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.PlaceReviews;
import com.twoheart.dailyhotel.network.model.PlaceWishItems;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.network.model.Stamp;
import com.twoheart.dailyhotel.network.model.Status;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayWishItem;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.network.model.TrueVRParams;

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
    Call<JSONObject> requestStayList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
                                     @QueryMap Map<String, Object> queryMap, //
                                     @Query("bedType") List<String> bedTypeList, //
                                     @Query("luxury") List<String> luxuryList);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseListDto<Keyword>> requestStaySearchAutoCompleteList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, //
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

    @GET("{mobileAPI}")
    Call<JSONObject> requestGourmetSearchAutoCompleteList(@Path(value = "mobileAPI", encoded = true) String mobileAPI, @Query("reserveDate") String date, @Query("term") String term);

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
    @POST("{mobileAPI}")
    Call<JSONObject> requestAddReviewInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                 @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Call<JSONObject> requestAddReviewDetailInformation(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                       @Body JSONObject jsonObject);

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
    Call<BaseDto<HomePlaces<HomePlace>>> requestHomeWishList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Call<BaseDto<HomePlaces<HomePlace>>> requestHomeRecentList(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                                               @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Call<BaseDto<Stamp>> requestUserStamps(@Path(value = "mobileAPI", encoded = true) String mobileAPI,//
                                           @Query("details") boolean details);

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

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // ProfileRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<UserData>> getUserProfile(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseDto<UserBenefitData>> getUserBenefit(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // SuggestRemoteImpl
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<SuggestsData>> getSuggestsByStayOutbound(@Url String mobileAPI, @Query(value = "keyword") String keyword);

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // SuggestRemoteImpl
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
    Observable<BaseDto<StayOutboundPaymentData>> getStayOutboundPayment(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST("{mobileAPI}")
    Observable<BaseListDto<CardData>> getSimpleCardList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @GET("{mobileAPI}")
    Observable<BaseDto<UserInformationData>> getUserInformationForPayment(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<PaymentResultData>> getPaymentTypeEasy(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto<PaymentResultData>> getPaymentTypeBonus(@Url String mobileAPI, @Body JSONObject jsonObject);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseListDto<BookingData>> getStayOutboundBookingList(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET("{mobileAPI}")
    Observable<BaseListDto<BookingData>> getBookingList(@Path(value = "mobileAPI", encoded = true) String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @POST()
    Observable<BaseDto> getStayOutboundHideBooking(@Url String mobileAPI);

    @Headers({"Accept: application/json;charset=UTF-8", "Content-type: application/json;charset=UTF-8"})
    @GET()
    Observable<BaseDto<StayOutboundBookingDetailData>> getStayOutboundBookingDetail(@Url String mobileAPI);
}
