package com.daily.dailyhotel.entity;

public class Coupon
{
    public enum Type
    {
        NORMAL,
        REWARD
    }

    public int amount; // 쿠폰금액 ,,,
    public String title; // 쿠폰명 ,,,
    public String validFrom; // 쿠폰 사용가능한 시작 시각 ,,,
    public String validTo; // 쿠폰 사용가능한 마지막 시각 ,,,
    public int amountMinimum; // 최소주문금액 ,,,
    public boolean isDownloaded; // 유저가 다운로드 했는지 여부 ,,
    public String availableItem; // 사용가능처 ,,
    public String serverDate; // 서버시간 ,,
    public String couponCode; // 이벤트 웹뷰, 쿠폰주의사항 사용용 쿠폰 코드 - 쿠폰별 유니크 코드
    public String userCouponIndex;
    public String stayFrom; // 투숙일 정보 - 시작일
    public String stayTo; // 투숙일 정보 - 종료일

    public String downloadedAt; // 유저가 해당 쿠폰을 다운로드한 시각
    public String disabledAt; // 쿠폰 사용 완료 또는 만료된 시각 (쿠폰 상태가 완료되면, 없어집니다.)
    public String description; // 쿠폰 설명
    public boolean availableInDomestic; // 국내 업소만 쿠폰 적용 여부
    public boolean availableInOverseas; // 해외 업소만 쿠폰 적용 여부
    public boolean availableInStay; // 호텔 쿠폰인지 여부 (아이콘으로 쓰세요)
    public boolean availableInGourmet; // 고메 쿠폰인지 여부 (아이콘으로 쓰세요)
    public boolean isRedeemed; // 이미 사용한 쿠폰인지 여부
    public boolean isExpired; // 만료된 쿠폰인지 여부

    public Type type;
}
