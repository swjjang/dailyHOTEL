package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.network.model.GourmetTicket;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.TimeZone;

public class GourmetPaymentInformation extends PlacePaymentInformation
{
    public String placeName;
    public String dateTime;
    public int ticketCount;
    public int ticketMaxCount; // 최대 결제 가능한 티켓 개수
    public long ticketTime;
    public long[] ticketTimes;
    public String category;
    private GourmetTicket mGourmetTicket;

    public GourmetPaymentInformation()
    {
        super();

        // Default Ticket count
        ticketCount = 1;
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
        dest.writeString(dateTime);
        dest.writeInt(ticketCount);
        dest.writeInt(ticketMaxCount);
        dest.writeLong(ticketTime);
        dest.writeLongArray(ticketTimes);
        dest.writeParcelable(mGourmetTicket, flags);
        dest.writeString(category);
    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        placeName = in.readString();
        dateTime = in.readString();
        ticketCount = in.readInt();
        ticketMaxCount = in.readInt();
        ticketTime = in.readLong();
        ticketTimes = in.createLongArray();
        mGourmetTicket = in.readParcelable(GourmetTicket.class.getClassLoader());
        category = in.readString();
    }

    public GourmetTicket getTicket()
    {
        return mGourmetTicket;
    }

    public void setTicket(GourmetTicket gourmetTicket)
    {
        mGourmetTicket = gourmetTicket;
    }

    public String[] getTicketTimes()
    {
        if (ticketTimes == null)
        {
            return null;
        }

        int length = ticketTimes.length;
        String[] times = new String[length];

        //        Calendar calendarTime = DailyCalendar.getInstance();
        //        calendarTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        //
        //        SimpleDateFormat formatDay = new SimpleDateFormat("HH:mm", Locale.KOREA);
        //        formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));

        for (int i = 0; i < length; i++)
        {
            //            calendarTime.setTimeInMillis(ticketTimes[i]);
            //            times[i] = formatDay.format(calendarTime.getTime());
            times[i] = DailyCalendar.format(ticketTimes[i], "HH:mm", TimeZone.getTimeZone("GMT"));
        }

        return times;
    }

    public int getPaymentToPay()
    {
        return mGourmetTicket.discountPrice * ticketCount;
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
