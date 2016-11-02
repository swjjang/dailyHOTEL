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
    public String userCouponCode; // 유저 쿠폰 코드 (이벤트 페이지의 쿠폰코드와 틀림),,
    public int amount; // 쿠폰금액 ,,,
    public String title; // 쿠폰명 ,,,
    public String validFrom; // 시작시간 ,,,
    public String validTo; // 종료시간 ,,,
    public int amountMinimum; // 최소주문금액 ,,,
    public boolean isDownloaded; // 상태표시 ,,
    public String availableItem; // 사용가능처 ,,
    public String serverDate; // 서버시간 ,,
    public String couponCode; // 이벤트 웹뷰, 쿠폰주의사항 사용용 쿠폰 코드
    public String stayFrom; // 투숙일 정보 - 시작일
    public String stayTo; // 투숙일 정보 - 종료일

    public boolean isStay; // 스테이 쿠폰인지
    public boolean isGourmet; // 고메 쿠폰인지


    public Coupon(Parcel in)
    {
        readFromParcel(in);
    }

    public Coupon()
    {
        // do nothing
    }

    // Own Coupon && Own Coupon for Payment
    public Coupon(String userCouponCode, int amount, String title, String validFrom, //
                  String validTo, int amountMinimum, boolean isDownloaded, String availableItem, //
                  String serverDate, String couponCode, String stayFrom, String stayTo)
    {
        this.userCouponCode = userCouponCode;
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(userCouponCode);
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
        dest.writeInt(isStay == true ? 1 : 0);
        dest.writeInt(isGourmet == true ? 1 : 0);
    }

    public void readFromParcel(Parcel in)
    {
        userCouponCode = in.readString();
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
        isStay = in.readInt() == 1;
        isGourmet = in.readInt() == 1;
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
