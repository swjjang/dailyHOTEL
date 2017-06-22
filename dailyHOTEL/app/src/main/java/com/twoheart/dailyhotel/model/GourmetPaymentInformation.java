package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.TimeZone;

public class GourmetPaymentInformation extends PlacePaymentInformation
{
    public String placeName;
    public int ticketCount;
    public int ticketMaxCount; // 최대 결제 가능한 티켓 개수
    public int ticketMinCount; // 최소 구매 수량
    public long ticketTime;
    public long[] ticketTimes;
    public String category;
    private GourmetProduct mGourmetProduct;

    public GourmetPaymentInformation()
    {
        super();

        // Default Ticket count
        ticketCount = 1;
        ticketMinCount = 1;
        ticketMaxCount = 1;
    }

    public GourmetPaymentInformation(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(placeName);
        dest.writeInt(ticketCount);
        dest.writeInt(ticketMaxCount);
        dest.writeLong(ticketTime);
        dest.writeLongArray(ticketTimes);
        dest.writeParcelable(mGourmetProduct, flags);
        dest.writeString(category);
    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        placeName = in.readString();
        ticketCount = in.readInt();
        ticketMaxCount = in.readInt();
        ticketTime = in.readLong();
        ticketTimes = in.createLongArray();
        mGourmetProduct = in.readParcelable(GourmetProduct.class.getClassLoader());
        category = in.readString();
    }

    public GourmetProduct getTicket()
    {
        return mGourmetProduct;
    }

    public void setTicket(GourmetProduct gourmetProduct)
    {
        mGourmetProduct = gourmetProduct;
    }

    public String[] getTicketTimes()
    {
        if (ticketTimes == null)
        {
            return null;
        }

        int length = ticketTimes.length;
        String[] times = new String[length];

        for (int i = 0; i < length; i++)
        {
            times[i] = DailyCalendar.format(ticketTimes[i], "HH:mm", TimeZone.getTimeZone("GMT+09:00"));
        }

        return times;
    }

    public int getPaymentToPay()
    {
        return mGourmetProduct.discountPrice * ticketCount;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetPaymentInformation createFromParcel(Parcel in)
        {
            return new GourmetPaymentInformation(in);
        }

        @Override
        public GourmetPaymentInformation[] newArray(int size)
        {
            return new GourmetPaymentInformation[size];
        }
    };
}
