package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sam Lee on 2016. 5. 19..
 *
 * 현재 임의로 만든 모델로 실제 서버에서 내려오는 값에 따라 변경 될 수 있음
 */
public class Coupon implements Parcelable
{
    public String name; // 쿠폰이름
    public int price; // 쿠폰금액
    public String description; //설명 ??  있을지수도 있고 없을수도 있음???
    public String expiredTime; // 유효기간
    public int dueDate; // 남은기간
    public int minPrice; // 최소주문금액
    public int state; // 상태표시
    public String useablePlace; // 사용가능처
    public String notice; // 유의사항 , 노출여부로만 사용될수도...

    public Coupon(Parcel in)
    {
        readFromParcel(in);
    }

    public Coupon(String name, int price, String description, String expiredTime, int dueDate, int minPrice, int state, String useablePlace, String notice)
    {
        this.name = name;
        this.price = price;
        this.description = description;
        this.expiredTime = expiredTime;
        this.dueDate = dueDate;
        this.minPrice = minPrice;
        this.state = state;
        this.useablePlace = useablePlace;
        this.notice = notice;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeString(description);
        dest.writeString(expiredTime);
        dest.writeInt(dueDate);
        dest.writeInt(minPrice);
        dest.writeInt(state);
        dest.writeString(useablePlace);
        dest.writeString(notice);
    }

    private void readFromParcel(Parcel in)
    {
        name = in.readString();
        price = in.readInt();
        description = in.readString();
        expiredTime = in.readString();
        dueDate = in.readInt();
        minPrice = in.readInt();
        state = in.readInt();
        useablePlace = in.readString();
        notice = in.readString();
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
