package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2017. 12. 4..
 */

public class RefundPolicy
{
    public static final String STATUS_NO_CHARGE_REFUND = "NO_CHARGE_REFUND"; // 무료 환불
    public static final String STATUS_SURCHARGE_REFUND = "SURCHARGE_REFUND"; // 부분 환불
    public static final String STATUS_NRD = "NRD";
    public static final String STATUS_WAIT_REFUND = "WAIT_REFUND";
    public static final String STATUS_NONE = "NONE";

    public String comment; // (string, optional),
    public boolean refundManual; // (boolean, optional),
    public String refundPolicy; // (string, optional) = ['NO_CHARGE_REFUND', 'SURCHARGE_REFUND', 'NRD', 'NONE']
    public String message; // 서버 메시지 - baseDto 에서 가져옴
}
