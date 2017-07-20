package com.twoheart.dailyhotel.network;

import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface IDailyNetwork
{
    // DailyHOTEL Reservation Controller WebAPI URL
    // api/hotel/v1/payment/session/common
    String URL_WEBAPI_HOTEL_V1_PAYMENT_SESSION_COMMON = Constants.UNENCRYPTED_URL ? "api/hotel/v1/payment/session/common" : "ODUkNDMkOCQxMDgkNDYkMjckNjEkOTYkMzEkNDckNTIkMTMkODUkOTEkNzkkMzUk$MkNCQ0MyQQjYzMN0U3OTlEQkREMjPU5MHThYDMEE1NjM2QzgKM2SRkZI1MkI3OTNGNJEExQzBEMkIzMzYBEQkY4SOTU2QJjk3NKjhFQTCM4MEQwRjg1RjFBOTDQ3MEZCNTJBOTAwRjI0ODZC$";

    // api/fnb/payment/session/common
    String URL_WEBAPI_FNB_PAYMENT_SESSION_COMMON = Constants.UNENCRYPTED_URL ? "api/fnb/payment/session/common" : "MjEkNzgkMjUkMzgkNzgkMTkkNDYkNjAkMyQ1MyQzMCQ1MSQxMSQ3OSQ5NyQ5OSQ=$OEER1OEYxMjElEMzhFNTZHCOVEIwMNzJVCQjU4MTI4ZNkY2NDVIxTMjZNDNUJCMTlEGRDczNzNCREM1SOEQ0MDFUBNNkFDRjUE2DMA==$";

    // Register Credit Card URL
    String URL_REGISTER_CREDIT_CARD = Constants.UNENCRYPTED_URL ? "api/user/session/billing/card/register" : "NTYkNjckNjkkMzQkOTMkNjQkMTI3JDgxJDkzJDExMCQxMTQkODgkMTIwJDgkNDQkNjUk$RjQ4MjE3LNTFBODVCQzVEQTExQTc2QTMwRDNMxRDYxOUOQyRTdCMjU4MkFGMOEZEOBDJBFNUNFYBMzM2RUY1DREU1NzGZGQUNOFMAjdBQkRDMUUyNDPY1MVjU0NWDhCNkFFQUM2OREY2QkU5$";


    /**
     * 서버 상태 체크
     *
     * @param tag
     * @param listener
     */
    void requestStatusServer(String tag, Object listener);

    /**
     * HappyTalk
     *
     * @param tag
     * @param listener
     */
    void requestHappyTalkCategory(String tag, Object listener);

    /**
     * 버전을 받아 업데이트를 체크한다
     *
     * @return
     */
    void requestCommonVersion(String tag, Object listener);

    /**
     * 서비시간 앱 운영시간 등등의 기본이되는 시간값을 받아온다
     *
     * @return
     */
    void requestCommonDateTime(String tag, Object listener);

    /**
     * 나의 정보를 요청한다
     *
     * @return
     */
    void requestUserProfile(String tag, Object listener);

    /**
     * 적립금 정보를 요청한다
     *
     * @return
     */
    void requestUserBonus(String tag, Object listener);

    /**
     * 데일리 유저 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserInformationUpdate(String tag, Map<String, String> params, Object listener);

    void requestUserProfileBenefit(String tag, Object listener);

    /**
     * 비번찾기의 이메일 정보를 확인한다
     *
     * @return
     */
    void requestUserCheckEmail(String tag, String userEmail, Object listener);

    /**
     * 비번찾기를 요청한다
     *
     * @return
     */
    void requestUserChangePassword(String tag, String email, Object listener);

    /**
     * 결제를 위한 유저 정보
     *
     * @return
     */
    void requestUserInformationForPayment(String tag, Object listener);


    /**
     * 소셜유저의 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserUpdateInformationForSocial(String tag, Map<String, String> params, Object listener);


    /**
     * 신용카드 목록을 요청한다
     *
     * @return
     */
    void requestUserBillingCardList(String tag, Object listener);


    /**
     * 신용카드를 삭제한다
     *
     * @return
     */
    void requestUserDeleteBillingCard(String tag, String billkey, Object listener);

    /**
     * Stay 리스트를 요청한다
     *
     * @return
     */
    void requestStayList(String tag, Map<String, Object> queryMap, List<String> bedTypeList, List<String> luxuryList, Object listener);

    /**
     * @param tag
     * @param date
     * @param text
     * @param listener
     */
    void requestStaySearchAutoCompleteList(String tag, String date, int stay, String text, Object listener);

    /**
     * 호텔 지역 리스트를 요청한다
     *
     * @return
     */
    void requestStayRegionList(String tag, Object listener);

    /**
     * 호텔 결제 정보 내역을 요청한다
     *
     * @return
     */
    void requestStayPaymentInformation(String tag, int roomIndex, String date, int nights, Object listener);

    /**
     * 호텔 상세 정보를 요청한다
     *
     * @return
     */
    void requestStayDetailInformation(String tag, int index, String date, int nights, Object listener);

    /**
     * 호텔의 간편결제를 요청한다
     *
     * @return
     */
    void requestStayPayment(String tag, Map<String, String> params, Object listener);

    /**
     * 예약 리스트(호텔 고메)를 요청한다
     *
     * @return
     */
    void requestBookingList(String tag, Object listener);

    /**
     * 고메의 상세 예약 내역을 요청한다
     *
     * @return
     */
    void requestGourmetReservationDetail(String tag, int index, Object listener);

    /**
     * 고메의 영수증 내역을 불러온다
     *
     * @return
     */
    void requestGourmetReceipt(String tag, int index, Object listener);

    /**
     * 고메 예약 내역 숨기기
     *
     * @return
     */
    void requestGourmetHiddenBooking(String tag, int index, Object listener);


    /**
     * 고메 가상계좌 정보
     *
     * @return
     */
    void requestGourmetAccountInformation(String tag, String tid, Object listener);


    /**
     * 고메 지역 리스트 요청
     *
     * @return
     */
    //
    void requestGourmetRegionList(String tag, Object listener);

    /**
     * 고메 리스트 요청
     *
     * @return
     */
    void requestGourmetList(String tag, Map<String, Object> queryMap, List<String> categoryList, List<String> timeList, List<String> luxuryList, Object listener);

    void requestGourmetSearchAutoCompleteList(String tag, String date, String text, Object listener);

    /**
     * 고메 상세 정보 내용
     *
     * @return
     */
    void requestGourmetDetailInformation(String tag, int index, String day, Object listener);

    /**
     * 고메 결제 정보 요청
     *
     * @return
     */
    void requestGourmetPaymentInformation(String tag, int index, Object listener);


    /**
     * 고메 티켓이 구매가 가능한지 체크한다.
     *
     * @return
     */
    void requestGourmetCheckTicket(String tag, int index, String day, int count, String time, Object listener);

    /**
     * 고메 간편결제
     *
     * @return
     */
    void requestGourmetPayment(String tag, Map<String, String> params, Object listener);

    /**
     * 입금 대기 계좌이체 상세 내용 요청
     *
     * @return
     */
    void requestDepositWaitDetailInformation(String tag, String tid, Object listener);

    /**
     * 호텔 예약 화면 상세 내용
     *
     * @return
     */
    void requestStayReservationDetail(String tag, int index, Object listener);

    /**
     * 호텔 예약내용 숨기기
     *
     * @return
     */
    void requestStayHiddenBooking(String tag, int index, Object listener);

    /**
     * 호텔 영수증
     *
     * @return
     */
    void requestStayReceipt(String tag, String index, Object listener);

    /**
     * 이벤트 리스트 요청
     *
     * @return
     */
    void requestEventList(String tag, String store, Object listener);

    /**
     * 신규 이벤트 개수
     *
     * @return
     */
    void requestEventNCouponNNoticeNewCount(String tag, String eventLatestDate, String couponLatestDate, String noticeLatestDate, Object listener);

    /**
     * @param tag
     * @param phone
     * @param listener
     */
    void requestDailyUserVerification(String tag, String phone, boolean force, Object listener);

    /**
     * @param tag
     * @param phone
     * @param code
     * @param listener
     */
    void requestDailyUserUpdatePhoneNumber(String tag, String phone, String code, Object listener);

    /**
     * 회원 가입 step1단계 검증
     *
     * @param tag
     * @param params
     * @param listener
     */
    void requestSignupValidation(String tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param signupKey
     * @param phone
     * @param listener
     */
    void requestDailyUserSignupVerfication(String tag, String signupKey, String phone, boolean force, Object listener);

    /**
     * @param tag
     * @param signupKey
     * @param code
     * @param listener
     */
    void requestDailyUserSignup(String tag, String signupKey, String code, String phone, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestFacebookUserSignup(String tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestKakaoUserSignup(String tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestDailyUserLogin(String tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestFacebookUserLogin(String tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestKakaoUserLogin(String tag, Map<String, String> params, Object listener);

    /**
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    void requestCouponList(String tag, Object listener);

    /**
     * 결제화면에서 사용되는 자신이 소유한 Coupon List
     *
     * @param tag
     * @param hotelIdx
     * @param roomIdx
     * @param checkIn  ISO-8601
     * @param checkOut ISO-8601
     * @param listener
     */
    void requestCouponList(String tag, int hotelIdx, int roomIdx, String checkIn, String checkOut, Object listener);

    void requestCouponList(String tag, int ticketIdx, int countOfTicket, Object listener);

    /**
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    void requestCouponHistoryList(String tag, Object listener);

    /**
     * 키워드를 사용한 쿠폰 등록
     *
     * @param tag
     * @param keyword
     * @param listener
     */
    void requestRegisterKeywordCoupon(String tag, String keyword, Object listener);

    /**
     * 혜택 알림을 받아야 하는지 알려준다.
     *
     * @param tag
     * @param listener
     */
    void requestNoticeAgreement(String tag, Object listener);

    /**
     * 혜택 알림을 받아야 하는지 알려준다.
     *
     * @param tag
     * @param listener
     */
    void requestNoticeAgreementResult(String tag, boolean isAgree, Object listener);

    /**
     *
     * @param tag
     * @param listener
     */
    void requestBenefitMessage(String tag, Object listener);

    /**
     * @param tag
     * @param userCouponCode
     * @param listener
     */
    void requestDownloadCoupon(String tag, String userCouponCode, Object listener);

    void requestHasCoupon(String tag, int placeIndex, String date, int nights, Object listener);

    void requestHasCoupon(String tag, int placeIndex, String date, Object listener);

    void requestCouponList(String tag, int placeIndex, String date, int nights, Object listener);

    void requestCouponList(String tag, int placeIndex, String date, Object listener);

    /**
     * @param tag
     * @param couponCode
     * @param listener
     */
    void requestDownloadEventCoupon(String tag, String couponCode, Object listener);

    /**
     *
     * @param tag
     * @param isAgree  required
     * @param listener
     */
    void requestUpdateBenefitAgreement(String tag, boolean isAgree, Object listener);

    void requestUserTracking(String tag, Object listener);

    void requestNoticeList(String tag, Object listener);

    void requestReceiptByEmail(String tag, String placeType, String reservationIdx, String email, Object listener);

    void requestWishListCount(String tag, Object listener);

    void requestStayWishList(String tag, Object listener);

    void requestGourmetWishList(String tag, Object listener);

    void requestAddWishList(String tag, String placeType, int placeIndex, Object listener);

    void requestRemoveWishList(String tag, String placeType, int placeIndex, Object listener);

    void requestPolicyRefund(String tag, int placeIndex, int ticketIndex, String dateCheckIn, String dateCheckOut, Object listener);

    void requestPolicyRefund(String tag, int hotelReservationIdx, String transactionType, Object listener);

    void requestRefund(String tag, int hotelIdx, String dateCheckIn, String transactionType, int hotelReservationIdx//
        , String reasonCancel, String accountHolder, String bankAccount, String bankCode, Object listener);

    void requestBankList(String tag, Object listener);

    // 리뷰
    void requestStayReviewInformation(String tag, Object listener);

    void requestGourmetReviewInformation(String tag, Object listener);

    void requestStayReviewInformation(String tag, int reserveIdx, Object listener);

    void requestGourmetReviewInformation(String tag, int reserveIdx, Object listener);

    void requestAddReviewInformation(String tag, JSONObject jsonObject, Object listener);

    void requestAddReviewDetailInformation(String tag, JSONObject jsonObject, Object listener);

    void requestHoliday(String tag, String startDay, String endDay, Object listener);

    void requestHomeEvents(String tag, String store, Object listener);

    void requestRecommendationList(String tag, Object listener);

    void requestRecommendationStayList(String tag, int index, String salesDate, int period, Object listener);

    void requestRecommendationGourmetList(String tag, int index, String salesDate, int period, Object listener);

    void requestHomeWishList(String tag, Object listener);

    void requestHomeRecentList(String tag, JSONObject jsonObject, Object listener);

    void requestUserStamps(String tag, boolean details, Object listener);

    void requestPlaceReviews(String tag, String type, int itemIdx, int page, int limit, Object listener);

    void requestPlaceReviewScores(String tag, String type, int itemIdx, Object listener);

    void requestLocalPlus(String tag, Map<String, Object> queryMap, Object listener);

    void requestStayCategoryRegionList(String tag, String category, Object listener);
}
