package com.daily.dailyhotel.parcel.analytics;

import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.view.DailyBookingPaymentTypeView;

public class StayOutboundThankYouAnalyticsParam implements Parcelable
{
    public DailyBookingPaymentTypeView.PaymentType paymentType;
    public boolean registerEasyCard;
    public boolean fullBonus;
    public boolean usedBonus;
    public int stayIndex;

    public StayOutboundThankYouAnalyticsParam()
    {
    }

    public StayOutboundThankYouAnalyticsParam(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(paymentType.name());
        dest.writeInt(registerEasyCard == true ? 1 : 0);
        dest.writeInt(fullBonus == true ? 1 : 0);
        dest.writeInt(usedBonus == true ? 1 : 0);
        dest.writeInt(stayIndex);
    }

    void readFromParcel(Parcel in)
    {
        paymentType = DailyBookingPaymentTypeView.PaymentType.valueOf(in.readString());
        registerEasyCard = in.readInt() == 1 ? true : false;
        fullBonus = in.readInt() == 1 ? true : false;
        usedBonus = in.readInt() == 1 ? true : false;
        stayIndex = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayOutboundThankYouAnalyticsParam createFromParcel(Parcel in)
        {
            return new StayOutboundThankYouAnalyticsParam(in);
        }

        @Override
        public StayOutboundThankYouAnalyticsParam[] newArray(int size)
        {
            return new StayOutboundThankYouAnalyticsParam[size];
        }
    };
}
