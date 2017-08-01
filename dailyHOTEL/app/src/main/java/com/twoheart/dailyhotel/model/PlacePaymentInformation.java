package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public abstract class PlacePaymentInformation implements Parcelable
{
    public int placeIndex; // 호텔 호텔인덱스 고메 고메 인덱스
    public int bonus; // 적립금
    public DiscountType discountType; // 할인 타입!
    public PaymentType paymentType; //
    public boolean isDBenefit;
    public int ratingValue;
    public String showOriginalPriceYn;
    public int entryPosition;
    public boolean isDailyChoice;

    private Province province;
    public String addressAreaName;

    private Customer mCustomer;
    private Guest mGuest;
    private Coupon mCoupon;

    public boolean isFree; // 무료 결제 인지

    public PlacePaymentInformation()
    {
        //        paymentType = PaymentType.EASY_CARD;
        discountType = DiscountType.NONE;
    }

    public PlacePaymentInformation(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(placeIndex);
        dest.writeInt(bonus);
        dest.writeString(discountType.name());
        dest.writeString(paymentType.name());
        dest.writeInt(isDBenefit ? 1 : 0);
        dest.writeInt(ratingValue);
        dest.writeString(showOriginalPriceYn);
        dest.writeInt(entryPosition);
        dest.writeInt(isDailyChoice ? 1 : 0);
        dest.writeParcelable(province, flags);
        dest.writeString(addressAreaName);
        dest.writeParcelable(mCustomer, flags);
        dest.writeParcelable(mGuest, flags);
        dest.writeParcelable(mCoupon, flags);
        dest.writeInt(isFree ? 1 : 0);
    }

    protected void readFromParcel(Parcel in)
    {
        placeIndex = in.readInt();
        bonus = in.readInt();
        discountType = DiscountType.valueOf(in.readString());
        paymentType = PaymentType.valueOf(in.readString());
        isDBenefit = in.readInt() == 1;
        ratingValue = in.readInt();
        showOriginalPriceYn = in.readString();
        entryPosition = in.readInt();
        isDailyChoice = in.readInt() == 1;
        province = in.readParcelable(Province.class.getClassLoader());
        addressAreaName = in.readString();
        mCustomer = in.readParcelable(Customer.class.getClassLoader());
        mGuest = in.readParcelable(Guest.class.getClassLoader());
        mCoupon = in.readParcelable(Coupon.class.getClassLoader());
        isFree = in.readInt() == 1;
    }

    public Customer getCustomer()
    {
        return mCustomer;
    }

    public void setCustomer(Customer customer)
    {
        this.mCustomer = customer;
    }

    public Coupon getCoupon()
    {
        return mCoupon;
    }

    public void setCoupon(Coupon coupon)
    {
        mCoupon = coupon;
    }

    public Guest getGuest()
    {
        return mGuest;
    }

    public void setGuest(Guest guest)
    {
        mGuest = guest;
    }

    public void setProvince(Province province)
    {
        if (province == null)
        {
            this.province = null;
            return;
        }

        this.province = province;
    }

    public Province getProvince()
    {
        return province;
    }

    public String getAnalyticsProvinceName()
    {
        if (this.province == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        if (this.province instanceof Area)
        {
            Area area = (Area) this.province;
            return area.getProvince().name;
        }

        return this.province.name;
    }

    public String getAnalyticsDistrictName()
    {
        if (this.province == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        if (this.province instanceof Area)
        {
            Area area = (Area) this.province;
            String provinceName = area.getProvince().name;
            return DailyTextUtils.isTextEmpty(provinceName) == false ? area.name : AnalyticsManager.ValueType.EMPTY;
        }

        return AnalyticsManager.ValueType.ALL_LOCALE_KR;
    }

    public String getAnalyticsAddressAreaName()
    {
        if (this.province == null)
        {
            return AnalyticsManager.ValueType.EMPTY;
        }

        return addressAreaName;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    // 명칭 변경하면 안됨 서버와 약속되어있음.
    public enum PaymentType
    {
        EASY_CARD("EasyCardPay"),
        CARD("CardPay"),
        PHONE_PAY("PhoneBillPay"),
        VBANK("VirtualAccountPay");

        private String mName;

        PaymentType(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }

    /**
     * 할인 타입 , NONE, BONUS, COUPON
     */
    public enum DiscountType
    {
        NONE("None"),
        BONUS("Bonus"),
        COUPON("Coupon");

        private String mName;

        DiscountType(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }
}
