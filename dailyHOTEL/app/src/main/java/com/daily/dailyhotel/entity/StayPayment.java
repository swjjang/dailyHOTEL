package com.daily.dailyhotel.entity;

public class StayPayment
{
    public static final String VISIT_TYPE_NONE = "NONE"; // 아무것도 표시하지 않음
    public static final String VISIT_TYPE_PARKING = "PARKING"; // 도보/주차 표시
    public static final String VISIT_TYPE_NO_PARKING = "NO_PARKING"; // 주차 불가능

    public boolean soldOut;
    public String checkInDate;
    public String checkOutDate;
    public String refundType;
    public int totalPrice; // 결재할 총금액
    public String businessName;
    public String transportation;
    public boolean waitingForBooking;

    public String mWarningMessage; // 현재 시간부터 날짜 바뀌기 전시간(새벽 3시, 3시부터 9시까지 경고 팝업 메시지

    public StayPayment()
    {

    }
}