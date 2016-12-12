package com.twoheart.dailyhotel.network;

import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

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
    void requestCheckServer(Object tag, Object listener);

    /**
     * common/ver_dual
     * 버전을 받아 업데이트를 체크한다
     *
     * @return
     */
    void requestCommonVer(Object tag, Object listener);

    /**
     * api/common/datetime
     * 서비시간 앱 운영시간 등등의 기본이되는 시간값을 받아온다
     *
     * @return
     */
    void requestCommonDateTime(Object tag, Object listener);

    /**
     * 나의 정보를 요청한다
     *
     * @return
     */
    void requestUserProfile(Object tag, Object listener);

    /**
     * user/session/bonus/all
     * 적립금 정보를 요청한다
     *
     * @return
     */
    void requestUserBonus(Object tag, Object listener);

    /**
     * user/update
     * 데일리 유저 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserInformationUpdate(Object tag, Map<String, String> params, Object listener);

    void requestUserProfileBenefit(Object tag, Object listener);

    /**
     * user/check/email_auth
     * 비번찾기의 이메일 정보를 확인한다
     *
     * @return
     */
    void requestUserCheckEmail(Object tag, String userEmail, Object listener);

    /**
     * user/change_pw
     * 비번찾기를 요청한다
     *
     * @return
     */
    void requestUserChangePassword(Object tag, String userEmail, Object listener);

    /**
     * api/user/information/omission;
     * 유저의 정보를 얻어온다. 소셜유저 판단가능
     *
     * @return
     */
    void requestUserInformationEx(Object tag, Object listener);

    /**
     * api/user/information
     * 결제를 위한 유저 정보
     *
     * @return
     */
    void requestUserInformationForPayment(Object tag, Object listener);


    /**
     * api/user/session/update/fb_user
     * 소셜유저의 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserUpdateInformationForSocial(Object tag, Map<String, String> params, Object listener);


    /**
     * api/user/session/billing/card/info
     * 신용카드 목록을 요청한다
     *
     * @return
     */
    void requestUserBillingCardList(Object tag, Object listener);


    /**
     * api/user/session/billing/card/del
     * 신용카드를 삭제한다
     *
     * @return
     */
    void requestUserDeleteBillingCard(Object tag, String billkey, Object listener);

    /**
     * /api/v3/hotels/sales
     * Stay 리스트를 요청한다
     *
     * @return
     */
    void requestStayList(Object tag, String stayParams, Object listener);

    /**
     * /api/v3/hotels/sales
     * StaySearch 리스트를 요청한다
     *
     * @return
     */
    void requestStaySearchList(Object tag, String stayParams, Object listener);

    /**
     * @param tag
     * @param date
     * @param text
     * @param listener
     */
    void requestHotelSearchAutoCompleteList(Object tag, String date, int stay, String text, Object listener);

    /**
     * api/sale/region/all
     * 호텔 지역 리스트를 요청한다
     *
     * @return
     */
    void requestHotelRegionList(Object tag, Object listener);

    /**
     * api/hotel/v1/payment/detail
     * 호텔 결제 정보 내역을 요청한다
     *
     * @return
     */
    void requestHotelPaymentInformation(Object tag, int roomIndex, String date, int nights, Object listener);

    /**
     * api/hotel/v1/sale/detail
     * 호텔 상세 정보를 요청한다
     *
     * @return
     */
    void requestHotelDetailInformation(Object tag, int index, String date, int nights, Object listener);

    /**
     * api/hotel/v1/payment/session/easy
     * 호텔의 간편결제를 요청한다
     *
     * @return
     */
    void requestHotelPayment(Object tag, Map<String, String> params, Object listener);

    /**
     * api/fnb/reservation/booking/list
     * 예약 리스트(호텔 고메)를 요청한다
     *
     * @return
     */
    void requestBookingList(Object tag, Object listener);

    /**
     * api/fnb/reservation/booking/detail
     * 고메의 상세 예약 내역을 요청한다
     *
     * @return
     */
    void requestGourmetBookingDetailInformation(Object tag, int index, Object listener);

    /**
     * api/fnb/reservation/booking/receipt
     * 고메의 영수증 내역을 불러온다
     *
     * @return
     */
    void requestGourmetReceipt(Object tag, int index, Object listener);

    /**
     * api/fnb/reservation/session/hidden
     * 고메 예약 내역 숨기기
     *
     * @return
     */
    void requestGourmetHiddenBooking(Object tag, int index, Object listener);


    /**
     * api/fnb/reservation/session/vbank/account/info
     * 고메 가상계좌 정보
     *
     * @return
     */
    void requestGourmetAccountInformation(Object tag, String tid, Object listener);


    /**
     * api/fnb/sale/region/province/list
     * 고메 지역 리스트 요청
     *
     * @return
     */
    //
    void requestGourmetRegionList(Object tag, Object listener);

    /**
     * api/fnb/sale/list
     * 고메 리스트 요청
     *
     * @return
     */
    void requestGourmetList(Object tag, String gourmetParams, Object listener);

    void requestGourmetSearchAutoCompleteList(Object tag, String date, String text, Object listener);

    void requestGourmetSearchList(Object tag, String gourmetParams, Object listener);

    /**
     * api/fnb/sale/restaurant/info
     * 고메 상세 정보 내용
     *
     * @return
     */
    void requestGourmetDetailInformation(Object tag, int index, String day, Object listener);

    /**
     * api/fnb/sale/ticket/payment/info
     * 고메 결제 정보 요청
     *
     * @return
     */
    void requestGourmetPaymentInformation(Object tag, int index, Object listener);


    /**
     * api/fnb/sale/session/ticket/sell/check
     * 고메 티켓이 구매가 가능한지 체크한다.
     *
     * @return
     */
    void requestGourmetCheckTicket(Object tag, int index, String day, int count, String time, Object listener);

    /**
     * api/fnb/payment/session/easy
     * 고메 간편결제
     *
     * @return
     */
    void requestGourmetPayment(Object tag, Map<String, String> params, Object listener);

    /**
     * reserv/mine/detail
     * 입금 대기 계좌이체 상세 내용 요청
     *
     * @return
     */
    void requestDepositWaitDetailInformation(Object tag, String tid, Object listener);

    /**
     * api/reserv/detail
     * 호텔 예약 화면 상세 내용
     *
     * @return
     */
    void requestHotelBookingDetailInformation(Object tag, int index, Object listener);

    /**
     * api/reserv/mine/hidden
     * 호텔 예약내용 숨기기
     *
     * @return
     */
    void requestHotelHiddenBooking(Object tag, int index, Object listener);

    /**
     * api/reserv/receipt
     * 호텔 영수증
     *
     * @return
     */
    void requestHotelReceipt(Object tag, String index, Object listener);

    /**
     * api/daily/event/list
     * 이벤트 리스트 요청
     *
     * @return
     */
    void requestEventList(Object tag, Object listener);

    /**
     * api/daily/event/count
     * 신규 이벤트 개수
     *
     * @return
     */
    void requestEventNCouponNNoticeNewCount(Object tag, String eventLatestDate, String couponLatestDate, String noticeLatestDate, Object listener);

    /**
     * api/daily/event/page
     * 이벤트 페이지 Url
     *
     * @return
     */
    void requestEventPageUrl(Object tag, int eventIndex, String store, Object listener);

    /**
     * 회사 정보 얻어오기
     *
     * @return
     */
    void requestCompanyInformation(Object tag, Object listener);

    /**
     * 호텔 이벤트 배너 리스트를 얻어온다
     *
     * @param tag
     * @param place
     * @param listener
     */
    void requestEventBannerList(Object tag, String place, Object listener);

    /**
     * @param tag
     * @param phone
     * @param listener
     */
    void requestDailyUserVerfication(Object tag, String phone, boolean force, Object listener);

    /**
     * @param tag
     * @param phone
     * @param code
     * @param listener
     */
    void requestDailyUserUpdatePhoneNumber(Object tag, String phone, String code, Object listener);

    /**
     * 회원 가입 step1단계 검증
     *
     * @param tag
     * @param params
     * @param listener
     */
    void requestSignupValidation(Object tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param signupKey
     * @param phone
     * @param listener
     */
    void requestDailyUserSignupVerfication(Object tag, String signupKey, String phone, boolean force, Object listener);

    /**
     * @param tag
     * @param signupKey
     * @param code
     * @param listener
     */
    void requestDailyUserSignup(Object tag, String signupKey, String code, String phone, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestFacebookUserSignup(Object tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestKakaoUserSignup(Object tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestDailyUserSignin(Object tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestFacebookUserSignin(Object tag, Map<String, String> params, Object listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestKakaoUserSignin(Object tag, Map<String, String> params, Object listener);

    /**
     * /api/v3/users/coupons
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    void requestCouponList(Object tag, Object listener);

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
    void requestCouponList(Object tag, int hotelIdx, int roomIdx, String checkIn, String checkOut, Object listener);

    void requestCouponList(Object tag, int ticketIdx, int countOfTicket, Object listener);

    /**
     * /api/v3/users/coupons/history
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    void requestCouponHistoryList(Object tag, Object listener);

    /**
     * 키워드를 사용한 쿠폰 등록
     *
     * @param tag
     * @param keyword
     * @param listener
     */
    void requestRegistKeywordCoupon(Object tag, String keyword, Object listener);

    /**
     * 혜택 알림을 받아야 하는지 알려준다.
     *
     * @param tag
     * @param listener
     */
    void requestNoticeAgreement(Object tag, Object listener);

    /**
     * 혜택 알림을 받아야 하는지 알려준다.
     *
     * @param tag
     * @param listener
     */
    void requestNoticeAgreementResult(Object tag, boolean isAgree, Object listener);

    /**
     * api/v1/notice/benefit - GET
     *
     * @param tag
     * @param listener
     */
    void requestBenefitMessage(Object tag, Object listener);

    /**
     * @param tag
     * @param userCouponCode
     * @param listener
     */
    void requestDownloadCoupon(Object tag, String userCouponCode, Object listener);

    void requestHasCoupon(Object tag, int placeIndex, String date, int nights, Object listener);

    void requestHasCoupon(Object tag, int placeIndex, String date, Object listener);

    void requestCouponList(Object tag, int placeIndex, String date, int nights, Object listener);

    void requestCouponList(Object tag, int placeIndex, String date, Object listener);

    /**
     * @param tag
     * @param couponCode
     * @param listener
     */
    void requestDownloadEventCoupon(Object tag, String couponCode, Object listener);

    /**
     * api/v1/notice/benefit - PUT
     *
     * @param tag
     * @param isAgree  required
     * @param listener
     */
    void requestUpdateBenefitAgreement(Object tag, boolean isAgree, Object listener);

    void requestUserTracking(Object tag, Object listener);

    void requestNoticeList(Object tag, Object listener);

    /**
     * /api/v3/hotels/sales
     * RecentStay 리스트를 요청한다
     *
     * @return
     */
    void requestRecentStayList(Object tag, String stayParams, Object listener);

    /**
     * api/fnb/sale/list
     * RecentGourmet 리스트 요청
     *
     * @return
     */
    void requestRecentGourmetList(Object tag, String gourmetParams, Object listener);

    void requestReceiptByEmail(Object tag, String placeType, String reservationIdx, String email, Object listener);

    void requestWishListCount(Object tag, Object listener);

    void requestWishList(Object tag, Constants.PlaceType placeType, Object listener);

    void requestAddWishList(Object tag, Constants.PlaceType placeType, int placeIndex, Object listener);

    void requestRemoveWishList(Object tag, Constants.PlaceType placeType, int placeIndex, Object listener);

    void requestPolicyRefund(Object tag, int placeIndex, int ticketIndex, String dateCheckIn, String transactionType, Object listener);

    void requestPolicyRefund(Object tag, int hotelReservationIdx, String transactionType, Object listener);

    void requestRefund(Object tag, int hotelIdx, String dateCheckIn, String transactionType, int hotelReservationIdx//
        , String reasonCancel, String accountHolder, String bankAccount, String bankCode, Object listener);

    void requestBankList(Object tag, Object listener);

    // 리뷰
    void requestStayReviewInformation(Object tag, Object listener);

    void requestGourmetReviewInformation(Object tag, Object listener);

    void requestStayReviewInformation(Object tag, int reserveIdx, Object listener);

    void requestGourmetReviewInformation(Object tag, int reserveIdx, Object listener);

    void requestAddReviewInformation(Object tag, JSONObject jsonObject, Object listener);

    void requestAddReviewDetailInformation(Object tag, JSONObject jsonObject, Object listener);
}
