package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sam Lee on 2016. 5. 19..
 * <p>
 * 현재 임의로 만든 모델로 실제 서버에서 내려오는 값에 따라 변경 될 수 있음
 */
public class Coupon implements Parcelable
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
    public String stayFrom; // 투숙일 정보 - 시작일
    public String stayTo; // 투숙일 정보 - 종료일

    public String downloadedAt; // 유저가 해당 쿠폰을 다운로드한 시각
    public String disabledAt; // 쿠폰 사용 완료 또는 만료된 시각 (쿠폰 상태가 완료되면, 없어집니다.)
    public String description; // 쿠폰 설명
    public boolean availableInOutboundHotel; // 해외 업소만 쿠폰 적용 여부
    public boolean availableInStay; // 호텔 쿠폰인지 여부 (아이콘으로 쓰세요)
    public boolean availableInGourmet; // 고메 쿠폰인지 여부 (아이콘으로 쓰세요)
    public boolean isRedeemed; // 이미 사용한 쿠폰인지 여부
    public boolean isExpired; // 만료된 쿠폰인지 여부

    public Type type;

    public Coupon(Parcel in)
    {
        readFromParcel(in);
    }

    public Coupon()
    {
        // do nothing
    }

    // Own Coupon && Own Coupon for Payment
    public Coupon(int amount, String title, String validFrom, //
                  String validTo, int amountMinimum, boolean isDownloaded, String availableItem, //
                  String serverDate, String couponCode, String stayFrom, String stayTo, //
                  //                  String downloadedAt, boolean availableInDomestic, boolean availableInOverseas, //
                  //                  boolean availableInStay, boolean availableInGourmet)
                  String downloadedAt, String disabledAt, //
                  boolean availableInStay, boolean availableInOutboundHotel, boolean availableInGourmet, boolean isRedeemed, boolean isExpired, Coupon.Type couponType)
    {
        this.amount = amount;
        this.title = title;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.amountMinimum = amountMinimum;
        this.isDownloaded = isDownloaded;
        this.availableItem = availableItem;
        this.serverDate = serverDate;
        this.couponCode = couponCode; // 결제시에는 쿠폰코드 없음
        this.stayFrom = stayFrom;
        this.stayTo = stayTo;
        this.downloadedAt = downloadedAt;
        this.disabledAt = disabledAt;
        this.availableInOutboundHotel = availableInOutboundHotel;
        this.availableInStay = availableInStay;
        this.availableInGourmet = availableInGourmet;
        this.isRedeemed = isRedeemed;
        this.isExpired = isExpired;
        this.type = couponType;
    }

    public Coupon(com.daily.dailyhotel.entity.Coupon coupon)
    {
        if (coupon == null)
        {
            return;
        }

        amount = coupon.amount; // 쿠폰금액 ,,,
        title = coupon.title; // 쿠폰명 ,,,
        validFrom = coupon.validFrom; // 쿠폰 사용가능한 시작 시각 ,,,
        validTo = coupon.validTo; // 쿠폰 사용가능한 마지막 시각 ,,,
        amountMinimum = coupon.amountMinimum; // 최소주문금액 ,,,
        isDownloaded = coupon.isDownloaded; // 유저가 다운로드 했는지 여부 ,,
        availableItem = coupon.availableItem; // 사용가능처 ,,
        serverDate = coupon.serverDate; // 서버시간 ,,
        couponCode = coupon.couponCode; // 이벤트 웹뷰, 쿠폰주의사항 사용용 쿠폰 코드 - 쿠폰별 유니크 코드
        stayFrom = coupon.stayFrom; // 투숙일 정보 - 시작일
        stayTo = coupon.stayTo; // 투숙일 정보 - 종료일
        downloadedAt = coupon.downloadedAt; // 유저가 해당 쿠폰을 다운로드한 시각
        disabledAt = coupon.disabledAt; // 쿠폰 사용 완료 또는 만료된 시각 (쿠폰 상태가 완료되면, 없어집니다.)
        description = coupon.description; // 쿠폰 설명
        availableInOutboundHotel = coupon.availableInOutboundHotel; // 해외 업소만 쿠폰 적용 여부
        availableInStay = coupon.availableInStay; // 호텔 쿠폰인지 여부 (아이콘으로 쓰세요)
        availableInGourmet = coupon.availableInGourmet; // 고메 쿠폰인지 여부 (아이콘으로 쓰세요)
        isRedeemed = coupon.isRedeemed; // 이미 사용한 쿠폰인지 여부
        isExpired = coupon.isExpired; // 만료된 쿠폰인지 여부

        if (coupon.type != null)
        {
            type = Type.valueOf(coupon.type.name());
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(amount);
        dest.writeString(title);
        dest.writeString(validFrom);
        dest.writeString(validTo);
        dest.writeInt(amountMinimum);
        dest.writeInt(isDownloaded == true ? 1 : 0);
        dest.writeString(availableItem);
        dest.writeString(serverDate);
        dest.writeString(couponCode);
        dest.writeString(stayFrom);
        dest.writeString(stayTo);
        dest.writeString(downloadedAt);
        dest.writeString(disabledAt);
        dest.writeInt(availableInOutboundHotel == true ? 1 : 0);
        dest.writeInt(availableInStay == true ? 1 : 0);
        dest.writeInt(availableInGourmet == true ? 1 : 0);
        dest.writeInt(isRedeemed == true ? 1 : 0);
        dest.writeInt(isExpired == true ? 1 : 0);

        if (type != null)
        {
            dest.writeString(type.name());
        }
    }

    public void readFromParcel(Parcel in)
    {
        amount = in.readInt();
        title = in.readString();
        validFrom = in.readString();
        validTo = in.readString();
        amountMinimum = in.readInt();
        isDownloaded = in.readInt() == 1;
        availableItem = in.readString();
        serverDate = in.readString();
        couponCode = in.readString();
        stayFrom = in.readString();
        stayTo = in.readString();
        downloadedAt = in.readString();
        disabledAt = in.readString();
        availableInOutboundHotel = in.readInt() == 1;
        availableInStay = in.readInt() == 1;
        availableInGourmet = in.readInt() == 1;
        isRedeemed = in.readInt() == 1;
        isExpired = in.readInt() == 1;

        try
        {
            type = Type.valueOf(in.readString());
        } catch (Exception e)
        {
            type = Type.NORMAL;
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Coupon createFromParcel(Parcel in)
        {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size)
        {
            return new Coupon[size];
        }
    };
}
