package com.twoheart.dailyhotel.network;

import android.location.Location;

import com.android.volley.Response;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;

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
    void requestCommonReview(Object tag, String type, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/common/datetime
     * 서비시간 앱 운영시간 등등의 기본이되는 시간값을 받아온다
     *
     * @return
     */
    void requestCommonDatetime(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * user/session/myinfo
     * 나의 정보를 요청한다
     *
     * @return
     */
    void requestUserInformation(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * user/session/bonus/all
     * 적립금 정보를 요청한다
     *
     * @return
     */
    void requestUserBonus(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * user/update
     * 데일리 유저 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserInformationUpdate(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * user/check/email_auth
     * 비번찾기의 이메일 정보를 확인한다
     *
     * @return
     */
    void requestUserCheckEmail(Object tag, String userEmail, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * user/change_pw
     * 비번찾기를 요청한다
     *
     * @return
     */
    void requestUserChangePassword(Object tag, String userEmail, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * user/notification/register
     * 푸시 아이디를 등록한다
     *
     * @return
     */
    void requestUserRegisterNotification(Object tag, String registrationId, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * @param tag
     * @param userIdx
     * @param changedRegistrationId
     * @param uid
     * @param listener
     * @param errorListener
     */
    void requestUserUpdateNotification(Object tag, String userIdx, String changedRegistrationId, String uid, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/user/information/omission;
     * 유저의 정보를 얻어온다. 소셜유저 판단가능
     *
     * @return
     */
    void requestUserInformationEx(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/user/information
     * 결제를 위한 유저 정보
     *
     * @return
     */
    void requestUserInformationForPayment(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * api/user/session/update/fb_user
     * 소셜유저의 정보를 업데이트 한다
     *
     * @return
     */
    void requestUserUpdateInformationForSocial(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * api/user/session/billing/card/info
     * 신용카드 목록을 요청한다
     *
     * @return
     */
    void requestUserBillingCardList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * api/user/session/billing/card/del
     * 신용카드를 삭제한다
     *
     * @return
     */
    void requestUserDeleteBillingCard(Object tag, String billkey, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/sale/hotel_list
     * 호텔 리스트를 요청한다
     *
     * @return
     */
    void requestHotelList(Object tag, Province province, SaleTime saleTime, int nights, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * /api/v3/hotels/sales
     * Stay 리스트를 요청한다
     *
     * @return
     */
    void requestStayList(Object tag, StayParams stayParams, DailyHotelJsonResponseListener listener);

    /**
     * @param tag
     * @param saleTime
     * @param nights
     * @param text
     * @param listener
     * @param errorListener
     */
    void requestHotelSearchList(Object tag, SaleTime saleTime, int nights, String text, int offset, int count, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    void requestHotelSearchList(Object tag, SaleTime saleTime, int nights, Location location, int offset, int count, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * @param tag
     * @param date
     * @param text
     * @param listener
     * @param errorListener
     */
    void requestHotelSearchAutoCompleteList(Object tag, String date, int lengthStay, String text, DailyHotelJsonArrayResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/sale/region/all
     * 호텔 지역 리스트를 요청한다
     *
     * @return
     */
    void requestHotelRegionList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/hotel/v1/payment/detail
     * 호텔 결제 정보 내역을 요청한다
     *
     * @return
     */
    void requestHotelPaymentInformation(Object tag, int roomIndex, String date, int nights, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/hotel/v1/sale/detail
     * 호텔 상세 정보를 요청한다
     *
     * @return
     */
    void requestHotelDetailInformation(Object tag, int index, String date, int nights, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/hotel/v1/payment/session/easy
     * 호텔의 간편결제를 요청한다
     *
     * @return
     */
    void requestHotelPayment(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/reservation/session/rating/msg/update
     * 고메 상세 만족도 결과를 업데이트 한다
     *
     * @return
     */
    void requestGourmetDetailRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * api/fnb/reservation/booking/list
     * 예약 리스트(호텔 고메)를 요청한다
     *
     * @return
     */
    void requestBookingList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/reservation/booking/detail
     * 고메의 상세 예약 내역을 요청한다
     *
     * @return
     */
    void requestGourmetBookingDetailInformation(Object tag, int index, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/reservation/booking/receipt
     * 고메의 영수증 내역을 불러온다
     *
     * @return
     */
    void requestGourmetReceipt(Object tag, int index, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/reservation/session/rating/exist
     * 고메 평가가 존재하는지 문의한다
     *
     * @return
     */
    void requestGourmetIsExistRating(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/reservation/session/rating/update
     * 고메 평가를 업데이트 한다.(만족함 만족안함)
     *
     * @return
     */
    void requestGourmetRating(Object tag, String result, String index, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/reservation/session/hidden
     * 고메 예약 내역 숨기기
     *
     * @return
     */
    void requestGourmetHiddenBooking(Object tag, int index, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * api/fnb/reservation/session/vbank/account/info
     * 고메 가상계좌 정보
     *
     * @return
     */
    void requestGourmetAccountInformation(Object tag, String tid, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * api/fnb/sale/region/province/list
     * 고메 지역 리스트 요청
     *
     * @return
     */
    //
    void requestGourmetRegionList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/sale/list
     * 고메 리스트 요청
     *
     * @return
     */
    void requestGourmetList(Object tag, Province province, SaleTime saleTime, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    void requestGourmetSearchList(Object tag, SaleTime saleTime, String text, int offeset, int count, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    void requestGourmetSearchList(Object tag, SaleTime saleTime, Location location, int offeset, int count, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    void requestGourmetSearchAutoCompleteList(Object tag, String date, String text, DailyHotelJsonArrayResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/sale/restaurant/info
     * 고메 상세 정보 내용
     *
     * @return
     */
    void requestGourmetDetailInformation(Object tag, int index, String day, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/sale/ticket/payment/info
     * 고메 결제 정보 요청
     *
     * @return
     */
    void requestGourmetPaymentInformation(Object tag, int index, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * api/fnb/sale/session/ticket/sell/check
     * 고메 티켓이 구매가 가능한지 체크한다.
     *
     * @return
     */
    void requestGourmetCheckTicket(Object tag, int index, String day, int count, String time, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/fnb/payment/session/easy
     * 고메 간편결제
     *
     * @return
     */
    void requestGourmetPayment(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * reserv/mine/detail
     * 입금 대기 계좌이체 상세 내용 요청
     *
     * @return
     */
    void requestDepositWaitDetailInformation(Object tag, String tid, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * reserv/bonus
     * 적립금 보너스 요청
     *
     * @return
     */
    void requestBonus(Object tag, DailyHotelStringResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/reserv/satisfaction_rating/update
     * 호텔 만족도 간단 평가
     *
     * @return
     */
    void requestHotelRating(Object tag, String result, String index, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/reserv/satisfaction_rating/exist
     * 호텔 만족도 평가 유무
     *
     * @return
     */
    void requestHotelIsExistRating(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/reserv/detail
     * 호텔 예약 화면 상세 내용
     *
     * @return
     */
    void requestHotelBookingDetailInformation(Object tag, int index, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/reserv/mine/hidden
     * 호텔 예약내용 숨기기
     *
     * @return
     */
    void requestHotelHiddenBooking(Object tag, int index, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/reserv/receipt
     * 호텔 영수증
     *
     * @return
     */
    void requestHotelReceipt(Object tag, String index, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);


    /**
     * api/reserv/satisfaction_rating/msg/update
     * 호텔 상세 만족도 평가
     *
     * @return
     */
    void requestHotelDetailRating(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/daily/event/list
     * 이벤트 리스트 요청
     *
     * @return
     */
    void requestEventList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * api/daily/event/count
     * 신규 이벤트 개수
     *
     * @return
     */
    void requestEventNCouponNewCount(Object tag, String eventLatestDate, String couponLatestDate, boolean isAuthorization, DailyHotelJsonResponseListener listener);

    /**
     * api/daily/event/page
     * 이벤트 페이지 Url
     *
     * @return
     */
    void requestEventPageUrl(Object tag, int eventIndex, String store, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * 회사 정보 얻어오기
     *
     * @return
     */
    void requestCompanyInformation(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

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
     * @param errorListener
     */
    void requestDailyUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * @param tag
     * @param params
     * @param listener
     * @param errorListener
     */
    void requestFacebookUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * @param tag
     * @param params
     * @param listener
     * @param errorListener
     */
    void requestKakaoUserSignin(Object tag, Map<String, String> params, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

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

    /**
     * /api/v3/users/coupons/history
     * 자신이 소유한 Coupon List
     *
     * @param tag
     * @param listener
     * @param errorListener
     */
    void requestCouponHistoryList(Object tag, DailyHotelJsonResponseListener listener, Response.ErrorListener errorListener);

    /**
     * 혜택 알림을 받아야 하는지 알려준다.
     *
     * @param tag
     * @param isAuthorization
     * @param listener
     */
    void requestNoticeAgreement(Object tag, boolean isAuthorization, DailyHotelJsonResponseListener listener);

    /**
     * 혜택 알림을 받아야 하는지 알려준다.
     *
     * @param tag
     * @param isAuthorization
     * @param listener
     */
    void requestNoticeAgreementResult(Object tag, boolean isAuthorization, boolean isAgree, DailyHotelJsonResponseListener listener);

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
    void requestUpdateBenefitAgreement(Object tag, boolean isAuthorization, boolean isAgree, DailyHotelJsonResponseListener listener);
}
