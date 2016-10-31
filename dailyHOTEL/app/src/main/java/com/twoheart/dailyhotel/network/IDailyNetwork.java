package com.twoheart.dailyhotel.network;

import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;

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
    void requestCheckServer(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * common/ver_dual
     * 버전을 받아 업데이트를 체크한다
     *
     * @return
     */
    void requestCommonVer(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * api/common/code/review
     * 만족도 평가대한 결과를 보낸다.(만족함, 만족안함)
     *
     * @return
     */
    void requestCommonReview(Object tag, String type, DailyHotelJsonResponseListener listener);

    /**
     * api/common/datetime
     * 서비시간 앱 운영시간 등등의 기본이되는 시간값을 받아온다
     *
     * @return
     */
    void requestCommonDateTime(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * 나의 정보를 요청한다
     *
     * @return
     */
    void requestUserProfile(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * user/session/bonus/all
     * 적립금 정보를 요청한다
     *
     * @return
     */
    void requestUserBonus(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * user/update
     * 데일리 유저 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserInformationUpdate(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    void requestUserProfileBenefit(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * user/check/email_auth
     * 비번찾기의 이메일 정보를 확인한다
     *
     * @return
     */
    void requestUserCheckEmail(Object tag, String userEmail, DailyHotelJsonResponseListener listener);


    /**
     * user/change_pw
     * 비번찾기를 요청한다
     *
     * @return
     */
    void requestUserChangePassword(Object tag, String userEmail, DailyHotelJsonResponseListener listener);


    /**
     * user/notification/register
     * 푸시 아이디를 등록한다
     *
     * @return
     */
    void requestUserRegisterNotification(Object tag, String registrationId, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param userIdx
     * @param changedRegistrationId
     * @param uid
     * @param listener
     */
    void requestUserUpdateNotification(Object tag, String userIdx, String changedRegistrationId, String uid, DailyHotelJsonResponseListener listener);

    /**
     * api/user/information/omission;
     * 유저의 정보를 얻어온다. 소셜유저 판단가능
     *
     * @return
     */
    void requestUserInformationEx(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * api/user/information
     * 결제를 위한 유저 정보
     *
     * @return
     */
    void requestUserInformationForPayment(Object tag, DailyHotelJsonResponseListener listener);


    /**
     * api/user/session/update/fb_user
     * 소셜유저의 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserUpdateInformationForSocial(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);


    /**
     * api/user/session/billing/card/info
     * 신용카드 목록을 요청한다
     *
     * @return
     */
    void requestUserBillingCardList(Object tag, DailyHotelJsonResponseListener listener);


    /**
     * api/user/session/billing/card/del
     * 신용카드를 삭제한다
     *
     * @return
     */
    void requestUserDeleteBillingCard(Object tag, String billkey, DailyHotelJsonResponseListener listener);

    /**
     * /api/v3/hotels/sales
     * Stay 리스트를 요청한다
     *
     * @return
     */
    void requestStayList(Object tag, String stayParams, DailyHotelJsonResponseListener listener);

    /**
     * /api/v3/hotels/sales
     * StaySearch 리스트를 요청한다
     *
     * @return
     */
    void requestStaySearchList(Object tag, String stayParams, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param date
     * @param text
     * @param listener
     */
    void requestHotelSearchAutoCompleteList(Object tag, String date, int stay, String text, DailyHotelJsonResponseListener listener);

    /**
     * api/sale/region/all
     * 호텔 지역 리스트를 요청한다
     *
     * @return
     */
    void requestHotelRegionList(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * api/hotel/v1/payment/detail
     * 호텔 결제 정보 내역을 요청한다
     *
     * @return
     */
    void requestHotelPaymentInformation(Object tag, int roomIndex, String date, int nights, DailyHotelJsonResponseListener listener);

    /**
     * api/hotel/v1/sale/detail
     * 호텔 상세 정보를 요청한다
     *
     * @return
     */
    void requestHotelDetailInformation(Object tag, int index, String date, int nights, DailyHotelJsonResponseListener listener);

    /**
     * api/hotel/v1/payment/session/easy
     * 호텔의 간편결제를 요청한다
     *
     * @return
     */
    void requestHotelPayment(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/reservation/session/rating/msg/update
     * 고메 상세 만족도 결과를 업데이트 한다
     *
     * @return
     */
    void requestGourmetDetailRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);


    /**
     * api/fnb/reservation/booking/list
     * 예약 리스트(호텔 고메)를 요청한다
     *
     * @return
     */
    void requestBookingList(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/reservation/booking/detail
     * 고메의 상세 예약 내역을 요청한다
     *
     * @return
     */
    void requestGourmetBookingDetailInformation(Object tag, int index, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/reservation/booking/receipt
     * 고메의 영수증 내역을 불러온다
     *
     * @return
     */
    void requestGourmetReceipt(Object tag, int index, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/reservation/session/rating/exist
     * 고메 평가가 존재하는지 문의한다
     *
     * @return
     */
    void requestGourmetIsExistRating(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/reservation/session/rating/update
     * 고메 평가를 업데이트 한다.(만족함 만족안함)
     *
     * @return
     */
    void requestGourmetRating(Object tag, String result, String index, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/reservation/session/hidden
     * 고메 예약 내역 숨기기
     *
     * @return
     */
    void requestGourmetHiddenBooking(Object tag, int index, DailyHotelJsonResponseListener listener);


    /**
     * api/fnb/reservation/session/vbank/account/info
     * 고메 가상계좌 정보
     *
     * @return
     */
    void requestGourmetAccountInformation(Object tag, String tid, DailyHotelJsonResponseListener listener);


    /**
     * api/fnb/sale/region/province/list
     * 고메 지역 리스트 요청
     *
     * @return
     */
    //
    void requestGourmetRegionList(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/sale/list
     * 고메 리스트 요청
     *
     * @return
     */
    void requestGourmetList(Object tag, String gourmetParams, DailyHotelJsonResponseListener listener);

    void requestGourmetSearchAutoCompleteList(Object tag, String date, String text, DailyHotelJsonResponseListener listener);

    void requestGourmetSearchList(Object tag, String gourmetParams, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/sale/restaurant/info
     * 고메 상세 정보 내용
     *
     * @return
     */
    void requestGourmetDetailInformation(Object tag, int index, String day, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/sale/ticket/payment/info
     * 고메 결제 정보 요청
     *
     * @return
     */
    void requestGourmetPaymentInformation(Object tag, int index, DailyHotelJsonResponseListener listener);


    /**
     * api/fnb/sale/session/ticket/sell/check
     * 고메 티켓이 구매가 가능한지 체크한다.
     *
     * @return
     */
    void requestGourmetCheckTicket(Object tag, int index, String day, int count, String time, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/payment/session/easy
     * 고메 간편결제
     *
     * @return
     */
    void requestGourmetPayment(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    /**
     * reserv/mine/detail
     * 입금 대기 계좌이체 상세 내용 요청
     *
     * @return
     */
    void requestDepositWaitDetailInformation(Object tag, String tid, DailyHotelJsonResponseListener listener);

    /**
     * api/reserv/satisfaction_rating/update
     * 호텔 만족도 간단 평가
     *
     * @return
     */
    void requestHotelRating(Object tag, String result, String index, DailyHotelJsonResponseListener listener);

    /**
     * api/reserv/satisfaction_rating/exist
     * 호텔 만족도 평가 유무
     *
     * @return
     */
    void requestHotelIsExistRating(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * api/reserv/detail
     * 호텔 예약 화면 상세 내용
     *
     * @return
     */
    void requestHotelBookingDetailInformation(Object tag, int index, DailyHotelJsonResponseListener listener);

    /**
     * api/reserv/mine/hidden
     * 호텔 예약내용 숨기기
     *
     * @return
     */
    void requestHotelHiddenBooking(Object tag, int index, DailyHotelJsonResponseListener listener);

    /**
     * api/reserv/receipt
     * 호텔 영수증
     *
     * @return
     */
    void requestHotelReceipt(Object tag, String index, DailyHotelJsonResponseListener listener);


    /**
     * api/reserv/satisfaction_rating/msg/update
     * 호텔 상세 만족도 평가
     *
     * @return
     */
    void requestHotelDetailRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    /**
     * api/daily/event/list
     * 이벤트 리스트 요청
     *
     * @return
     */
    void requestEventList(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * api/daily/event/count
     * 신규 이벤트 개수
     *
     * @return
     */
    void requestEventNCouponNNoticeNewCount(Object tag, String eventLatestDate, String couponLatestDate, String noticeLatestDate, DailyHotelJsonResponseListener listener);

    /**
     * api/daily/event/page
     * 이벤트 페이지 Url
     *
     * @return
     */
    void requestEventPageUrl(Object tag, int eventIndex, String store, DailyHotelJsonResponseListener listener);

    /**
     * 회사 정보 얻어오기
     *
     * @return
     */
    void requestCompanyInformation(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * 호텔 이벤트 배너 리스트를 얻어온다
     *
     * @param tag
     * @param place
     * @param listener
     */
    void requestEventBannerList(Object tag, String place, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param phone
     * @param listener
     */
    void requestDailyUserVerfication(Object tag, String phone, boolean force, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param phone
     * @param code
     * @param listener
     */
    void requestDailyUserUpdatePhoneNumber(Object tag, String phone, String code, DailyHotelJsonResponseListener listener);

    /**
     * 회원 가입 step1단계 검증
     *
     * @param tag
     * @param params
     * @param listener
     */
    void requestSignupValidation(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param signupKey
     * @param phone
     * @param listener
     */
    void requestDailyUserSignupVerfication(Object tag, String signupKey, String phone, boolean force, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param signupKey
     * @param code
     * @param listener
     */
    void requestDailyUserSignup(Object tag, String signupKey, String code, String phone, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestFacebookUserSignup(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestKakaoUserSignup(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestDailyUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestFacebookUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param params
     * @param listener
     */
    void requestKakaoUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener);

    /**
     * /api/v3/users/coupons
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    void requestCouponList(Object tag, DailyHotelJsonResponseListener listener);

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
    void requestCouponList(Object tag, int hotelIdx, int roomIdx, String checkIn, String checkOut, DailyHotelJsonResponseListener listener);

    void requestCouponList(Object tag, int gourmetIdx, int ticketIdx, String date, DailyHotelJsonResponseListener listener);

    /**
     * /api/v3/users/coupons/history
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     */
    void requestCouponHistoryList(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * 키워드를 사용한 쿠폰 등록
     *
     * @param tag
     * @param keyword
     * @param listener
     */
    void requestRegistKeywordCoupon(Object tag, String keyword, DailyHotelJsonResponseListener listener);

    /**
     * 혜택 알림을 받아야 하는지 알려준다.
     *
     * @param tag
     * @param listener
     */
    void requestNoticeAgreement(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * 혜택 알림을 받아야 하는지 알려준다.
     *
     * @param tag
     * @param listener
     */
    void requestNoticeAgreementResult(Object tag, boolean isAgree, DailyHotelJsonResponseListener listener);

    /**
     * api/v1/notice/benefit - GET
     *
     * @param tag
     * @param listener
     */
    void requestBenefitMessage(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param userCouponCode
     * @param listener
     */
    void requestDownloadCoupon(Object tag, String userCouponCode, DailyHotelJsonResponseListener listener);

    void requestHasCoupon(Object tag, int placeIndex, String date, int nights, DailyHotelJsonResponseListener listener);

    void requestHasCoupon(Object tag, int placeIndex, String date, DailyHotelJsonResponseListener listener);

    void requestCouponList(Object tag, int placeIndex, String date, int nights, DailyHotelJsonResponseListener listener);

    void requestCouponList(Object tag, int placeIndex, String date, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param couponCode
     * @param listener
     */
    void requestDownloadEventCoupon(Object tag, String couponCode, DailyHotelJsonResponseListener listener);

    /**
     * api/v1/notice/benefit - PUT
     *
     * @param tag
     * @param isAgree  required
     * @param listener
     */
    void requestUpdateBenefitAgreement(Object tag, boolean isAgree, DailyHotelJsonResponseListener listener);

    void requestUserTracking(Object tag, DailyHotelJsonResponseListener listener);

    void requestNoticeList(Object tag, DailyHotelJsonResponseListener listener);

    /**
     * /api/v3/hotels/sales
     * RecentStay 리스트를 요청한다
     *
     * @return
     */
    void requestRecentStayList(Object tag, String stayParams, DailyHotelJsonResponseListener listener);

    /**
     * api/fnb/sale/list
     * RecentGourmet 리스트 요청
     *
     * @return
     */
    void requestRecentGourmetList(Object tag, String gourmetParams, DailyHotelJsonResponseListener listener);
}
