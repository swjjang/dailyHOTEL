package com.twoheart.dailyhotel.network;

import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

interface IDailyNetwork
{
    /**
     * http://status.dailyhotel.kr/status/health/check
     * 서버 상태 체크
     *
     * @param tag
     * @param listener
     */
    void requestStatusServer(String tag, Object listener);

    /**
     * common/ver_dual
     * 버전을 받아 업데이트를 체크한다
     *
     * @return
     */
    void requestCommonVersion(String tag, Object listener);

    /**
     * api/common/datetime
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
     * user/session/bonus/all
     * 적립금 정보를 요청한다
     *
     * @return
     */
    void requestUserBonus(String tag, Object listener);

    /**
     * user/update
     * 데일리 유저 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserInformationUpdate(String tag, Map<String, String> params, Object listener);

    void requestUserProfileBenefit(String tag, Object listener);

    /**
     * user/check/email_auth
     * 비번찾기의 이메일 정보를 확인한다
     *
     * @return
     */
    void requestUserCheckEmail(String tag, String userEmail, Object listener);

    /**
     * user/change_pw
     * 비번찾기를 요청한다
     *
     * @return
     */
    void requestUserChangePassword(String tag, String userEmail, Object listener);

    /**
     * api/user/information
     * 결제를 위한 유저 정보
     *
     * @return
     */
    void requestUserInformationForPayment(String tag, Object listener);


    /**
     * api/user/session/update/fb_user
     * 소셜유저의 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserUpdateInformationForSocial(String tag, Map<String, String> params, Object listener);


    /**
     * api/user/session/billing/card/info
     * 신용카드 목록을 요청한다
     *
     * @return
     */
    void requestUserBillingCardList(String tag, Object listener);


    /**
     * api/user/session/billing/card/del
     * 신용카드를 삭제한다
     *
     * @return
     */
    void requestUserDeleteBillingCard(String tag, String billkey, Object listener);

    /**
     * /api/v3/hotels/sales
     * Stay 리스트를 요청한다
     *
     * @return
     */
    void requestStayList(String tag, Map<String, Object> queryMap, List<String> bedTypeList, List<String> luxuryList, Object listener);

    /**
     * /api/v3/hotels/sales
     * StaySearch 리스트를 요청한다
     *
     * @return
     */
    void requestStaySearchList(String tag, String stayParams, Object listener);

    /**
     * @param tag
     * @param date
     * @param text
     * @param listener
     */
    void requestHotelSearchAutoCompleteList(String tag, String date, int stay, String text, Object listener);

    /**
     * api/sale/region/all
     * 호텔 지역 리스트를 요청한다
     *
     * @return
     */
    void requestHotelRegionList(String tag, Object listener);

    /**
     * api/hotel/v1/payment/detail
     * 호텔 결제 정보 내역을 요청한다
     *
     * @return
     */
    void requestHotelPaymentInformation(String tag, int roomIndex, String date, int nights, Object listener);

    /**
     * api/hotel/v1/sale/detail
     * 호텔 상세 정보를 요청한다
     *
     * @return
     */
    void requestHotelDetailInformation(String tag, int index, String date, int nights, Object listener);

    /**
     * api/hotel/v1/payment/session/easy
     * 호텔의 간편결제를 요청한다
     *
     * @return
     */
    void requestHotelPayment(String tag, Map<String, String> params, Object listener);

    /**
     * api/fnb/reservation/booking/list
     * 예약 리스트(호텔 고메)를 요청한다
     *
     * @return
     */
    void requestBookingList(String tag, Object listener);

    /**
     * api/fnb/reservation/booking/detail
     * 고메의 상세 예약 내역을 요청한다
     *
     * @return
     */
    void requestGourmetBookingDetailInformation(String tag, int index, Object listener);

    /**
     * api/fnb/reservation/booking/receipt
     * 고메의 영수증 내역을 불러온다
     *
     * @return
     */
    void requestGourmetReceipt(String tag, int index, Object listener);

    /**
     * api/fnb/reservation/session/hidden
     * 고메 예약 내역 숨기기
     *
     * @return
     */
    void requestGourmetHiddenBooking(String tag, int index, Object listener);


    /**
     * api/fnb/reservation/session/vbank/account/info
     * 고메 가상계좌 정보
     *
     * @return
     */
    void requestGourmetAccountInformation(String tag, String tid, Object listener);


    /**
     * api/fnb/sale/region/province/list
     * 고메 지역 리스트 요청
     *
     * @return
     */
    //
    void requestGourmetRegionList(String tag, Object listener);

    /**
     * api/fnb/sale/list
     * 고메 리스트 요청
     *
     * @return
     */
    void requestGourmetList(String tag, String gourmetParams, Object listener);

    void requestGourmetSearchAutoCompleteList(String tag, String date, String text, Object listener);

    void requestGourmetSearchList(String tag, String gourmetParams, Object listener);

    /**
     * api/fnb/sale/restaurant/info
     * 고메 상세 정보 내용
     *
     * @return
     */
    void requestGourmetDetailInformation(String tag, int index, String day, Object listener);

    /**
     * api/fnb/sale/ticket/payment/info
     * 고메 결제 정보 요청
     *
     * @return
     */
    void requestGourmetPaymentInformation(String tag, int index, Object listener);


    /**
     * api/fnb/sale/session/ticket/sell/check
     * 고메 티켓이 구매가 가능한지 체크한다.
     *
     * @return
     */
    void requestGourmetCheckTicket(String tag, int index, String day, int count, String time, Object listener);

    /**
     * api/fnb/payment/session/easy
     * 고메 간편결제
     *
     * @return
     */
    void requestGourmetPayment(String tag, Map<String, String> params, Object listener);

    /**
     * reserv/mine/detail
     * 입금 대기 계좌이체 상세 내용 요청
     *
     * @return
     */
    void requestDepositWaitDetailInformation(String tag, String tid, Object listener);

    /**
     * api/reserv/detail
     * 호텔 예약 화면 상세 내용
     *
     * @return
     */
    void requestHotelBookingDetailInformation(String tag, int index, Object listener);

    /**
     * api/reserv/mine/hidden
     * 호텔 예약내용 숨기기
     *
     * @return
     */
    void requestHotelHiddenBooking(String tag, int index, Object listener);

    /**
     * api/reserv/receipt
     * 호텔 영수증
     *
     * @return
     */
    void requestHotelReceipt(String tag, String index, Object listener);

    /**
     * api/daily/event/list
     * 이벤트 리스트 요청
     *
     * @return
     */
    void requestEventList(String tag, Object listener);

    /**
     * api/daily/event/count
     * 신규 이벤트 개수
     *
     * @return
     */
    void requestEventNCouponNNoticeNewCount(String tag, String eventLatestDate, String couponLatestDate, String noticeLatestDate, Object listener);

    /**
     * api/daily/event/page
     * 이벤트 페이지 Url
     *
     * @return
     */
    void requestEventPageUrl(String tag, int eventIndex, String store, Object listener);

    /**
     * 회사 정보 얻어오기
     *
     * @return
     */
    void requestCompanyInformation(String tag, Object listener);

    /**
     * 호텔 이벤트 배너 리스트를 얻어온다
     *
     * @param tag
     * @param place
     * @param listener
     */
    void requestEventBannerList(String tag, String place, Object listener);

    /**
     * @param tag
     * @param phone
     * @param listener
     */
    void requestDailyUserVerfication(String tag, String phone, boolean force, Object listener);

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
    void requestDailyUserSignin(String tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestFacebookUserSignin(String tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestKakaoUserSignin(String tag, Map<String, String> params, Object listener);

    /**
     * /api/v3/users/coupons
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    void requestCouponList(String tag, Object listener);

    /**
     * /api/v3/users/coupons
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
     * /api/v3/users/coupons/history
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
    void requestRegistKeywordCoupon(String tag, String keyword, Object listener);

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
     * api/v1/notice/benefit - GET
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
     * api/v1/notice/benefit - PUT
     *
     * @param tag
     * @param isAgree  required
     * @param listener
     */
    void requestUpdateBenefitAgreement(String tag, boolean isAgree, Object listener);

    void requestUserTracking(String tag, Object listener);

    void requestNoticeList(String tag, Object listener);

    /**
     * /api/v3/hotels/sales
     * RecentStay 리스트를 요청한다
     *
     * @return
     */
    void requestRecentStayList(String tag, String stayParams, Object listener);

    /**
     * api/fnb/sale/list
     * RecentGourmet 리스트 요청
     *
     * @return
     */
    void requestRecentGourmetList(String tag, String gourmetParams, Object listener);

    void requestReceiptByEmail(String tag, String placeType, String reservationIdx, String email, Object listener);

    void requestWishListCount(String tag, Object listener);

    void requestWishList(String tag, Constants.PlaceType placeType, Object listener);

    void requestAddWishList(String tag, Constants.PlaceType placeType, int placeIndex, Object listener);

    void requestRemoveWishList(String tag, Constants.PlaceType placeType, int placeIndex, Object listener);

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
}
