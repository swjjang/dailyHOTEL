/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * VolleyHttpClient
 * <p/>
 * 네트워크 이미지 처리 및 네트워크 처리 작업을 담당하는 외부 라이브러리 Vol
 * ley를 네트워크 처리 작업을 목적으로 사용하기 위해 설정하는 유틸 클래스이다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.network;

import com.twoheart.dailyhotel.network.request.DailyHotelRequest;

public interface IDailyNetwork
{
    /**
     * common/ver_dual
     * 버전을 받아 업데이트를 체크한다
     *
     * @return
     */
    public DailyHotelRequest requestCommonVer();

    /**
     * api/common/code/review
     * 만족도 평가대한 결과를 보낸다.(만족함, 만족안함)
     *
     * @return
     */
    public DailyHotelRequest requestCommonReview();

    /**
     * api/common/datetime
     * 서비시간 앱 운영시간 등등의 기본이되는 시간값을 받아온다
     *
     * @return
     */
    public DailyHotelRequest requestCommonDatetime();


    /**
     * user/logout/mobile
     * 로그아웃을 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestUserLogout();

    /**
     * user/session/myinfo
     * 나의 정보를 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestUserInformation();

    /**
     * user/session/bonus/all
     * 적립금 정보를 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestUserBonus();

    /**
     * user/alive
     * 유저가 로그인되어있고 세션이 살아있는지 확인한다.
     *
     * @return
     */
    public DailyHotelRequest requestUserAlive();

    /**
     * user/update
     * 데일리 유저 정보를 업데이트 한다
     *
     * @return
     */
    public DailyHotelRequest requestUserInformationUpdate();


    /**
     * user/check/email_auth
     * 비번찾기의 이메일 정보를 확인한다
     *
     * @return
     */
    public DailyHotelRequest requestUserCheckEmail();


    /**
     * user/change_pw
     * 비번찾기를 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestUserChangePassword();


    /**
     * user/notification/register
     * 푸시 아이디를 등록한다
     *
     * @return
     */
    public DailyHotelRequest requestUserRegisterNotification();


    /**
     * api/user/information/omission;
     * 유저의 정보를 얻어온다. 소셜유저 판단가능
     *
     * @return
     */
    public DailyHotelRequest requestUserInformationEx();

    /**
     * api/user/signin
     * 로그인
     *
     * @return
     */
    public DailyHotelRequest requestUserSignin();

    /**
     * api/user/signup
     * 회원 가입
     *
     * @return
     */
    public DailyHotelRequest requestUserSignup();

    /**
     * api/user/information
     * 결제를 위한 유저 정보
     *
     * @return
     */
    //    public DailyHotelRequest requestUserInformation();


    /**
     * api/user/session/update/fb_user
     * 소셜유저의 정보를 업데이트 한다
     *
     * @return
     */
    public DailyHotelRequest requestUserUpdateInformationForSocial();


    /**
     * api/user/session/billing/card/info
     * 신용카드 목록을 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestUserBillingCardList();


    /**
     * api/user/session/billing/card/del
     * 신용카드를 삭제한다
     *
     * @return
     */
    public DailyHotelRequest requestUserDeleteBillingCard();

    /**
     * api/user/session/billing/card/register
     * 신용카드를 등록한다
     *
     * @return
     */
    public DailyHotelRequest requestUserAddBillingCard();

    /**
     * api/sale/hotel_list
     * 호텔 리스트를 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestHotelList();

    /**
     * api/sale/region/all
     * 호텔 지역 리스트를 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestHotelRegionList();


    /**
     * api/hotel/v1/payment/detail
     * 호텔 결제 정보 내역을 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestHotelPaymentInformation();

    /**
     * api/hotel/v1/sale/detail
     * 호텔 상세 정보를 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestHotelDetailInformation();


    /**
     * api/hotel/v1/payment/session/easy
     * 호텔의 간편결제를 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestHotelPayment();

    /**
     * api/fnb/reservation/session/rating/msg/update
     * 고메 상세 만족도 결과를 업데이트 한다
     *
     * @return
     */
    public DailyHotelRequest requestGourmetDetailRating();


    /**
     * api/fnb/reservation/booking/list
     * 예약 리스트(호텔 고메)를 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestBookingList();

    /**
     * api/fnb/reservation/booking/detail
     * 고메의 상세 예약 내역을 요청한다
     *
     * @return
     */
    public DailyHotelRequest requestGourmetBookingDetailInformation();

    /**
     * api/fnb/reservation/booking/receipt
     * 고메의 영수증 내역을 불러온다
     *
     * @return
     */
    public DailyHotelRequest requestGourmetReceipt();

    /**
     * api/fnb/reservation/session/rating/exist
     * 고메 평가가 존재하는지 문의한다
     *
     * @return
     */
    public DailyHotelRequest requestGourmetIsExistRating();

    /**
     * api/fnb/reservation/session/rating/update
     * 고메 평가를 업데이트 한다.(만족함 만족안함)
     *
     * @return
     */
    public DailyHotelRequest requestGourmetRating();

    /**
     * api/fnb/reservation/session/hidden
     * 고메 예약 내역 숨기기
     *
     * @return
     */
    public DailyHotelRequest requestGourmetHiddenBooking();


    /**
     * api/fnb/reservation/session/vbank/account/info
     * 고메 가상계좌 정보
     *
     * @return
     */
    public DailyHotelRequest requestGourmetAccountInformation();


    /**
     * api/fnb/sale/region/province/list
     * 고메 지역 리스트 요청
     *
     * @return
     */
    //
    public DailyHotelRequest requestGourmetRegionList();

    /**
     * api/fnb/sale/list
     * 고메 리스트 요청
     *
     * @return
     */
    public DailyHotelRequest requestGourmetList();


    /**
     * api/fnb/sale/restaurant/info
     * 고메 상세 정보 내용
     *
     * @return
     */
    public DailyHotelRequest requestGourmetDetailInformation();

    /**
     * api/fnb/sale/ticket/payment/info
     * 고메 결제 정보 요청
     *
     * @return
     */
    public DailyHotelRequest requestGourmetPaymentInformation();


    /**
     * api/fnb/sale/session/ticket/sell/check
     * 고메 티켓이 구매가 가능한지 체크한다.
     *
     * @return
     */
    public DailyHotelRequest requestGourmetCheckTicket();

    /**
     * api/fnb/payment/session/easy
     * 고메 간편결제
     *
     * @return
     */
    public DailyHotelRequest requestGourmetPayment();


    /**
     * reserv/mine/detail
     * 입금 대기 계좌이체 상세 내용 요청
     *
     * @return
     */
    public DailyHotelRequest requestDepositWaitDetailInformation();


    /**
     * reserv/bonus
     * 적립금 보너스 요청
     *
     * @return
     */
    public DailyHotelRequest requestBonus();

    /**
     * api/reserv/satisfaction_rating/update
     * 호텔 만족도 간단 평가
     *
     * @return
     */
    public DailyHotelRequest requestHotelRating();

    /**
     * api/reserv/satisfaction_rating/exist
     * 호텔 만족도 평가 유무
     *
     * @return
     */
    public DailyHotelRequest requestHotelIsExistRating();

    /**
     * api/reserv/detail
     * 호텔 예약 화면 상세 내용
     *
     * @return
     */
    public DailyHotelRequest requestHotelBookingDetailInformation();

    /**
     * api/reserv/mine/hidden
     * 호텔 예약내용 숨기기
     *
     * @return
     */
    public DailyHotelRequest requestHotelHiddenBooking();

    /**
     * api/reserv/receipt
     * 호텔 영수증
     *
     * @return
     */
    public DailyHotelRequest requestHotelReceipt();


    /**
     * api/reserv/satisfaction_rating/msg/update
     * 호텔 상세 만족도 평가
     *
     * @return
     */
    public DailyHotelRequest requestHotelDetailRating();

    /**
     * api/daily/event/list
     * 이벤트 리스트 요청
     *
     * @return
     */
    public DailyHotelRequest requestEventList();

    /**
     * api/daily/event/count
     * 신규 이벤트 개수
     *
     * @return
     */
    public DailyHotelRequest requestEventNewCount();

    /**
     * api/daily/event/page
     * 이벤트 페이지 Url
     * @return
     */
    public DailyHotelRequest requestEventPageUrl();
}
