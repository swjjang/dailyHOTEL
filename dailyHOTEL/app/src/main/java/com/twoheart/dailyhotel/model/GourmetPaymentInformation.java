package com.twoheart.dailyhotel.model;

import android.os.Parcel;

import com.twoheart.dailyhotel.util.DailyCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class GourmetPaymentInformation extends PlacePaymentInformation
{
    public String checkInTime;
    public String checkOutTime;
    public int ticketCount;
    public int ticketMaxCount; // 최대 결제 가능한 티켓 개수
    public long ticketTime;
    public long[] ticketTimes;
    public String category;
    private TicketInformation mTicketInformation;

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

        dest.writeString(checkInTime);
        dest.writeString(checkOutTime);
        dest.writeInt(ticketCount);
        dest.writeInt(ticketMaxCount);
        dest.writeLong(ticketTime);
        dest.writeLongArray(ticketTimes);
        dest.writeParcelable(mTicketInformation, flags);
        dest.writeString(category);
    }

    @Override
    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        checkInTime = in.readString();
        checkOutTime = in.readString();
        ticketCount = in.readInt();
        ticketMaxCount = in.readInt();
        ticketTime = in.readLong();
        ticketTimes = in.createLongArray();
        mTicketInformation = in.readParcelable(TicketInformation.class.getClassLoader());
        category = in.readString();
    }

    public TicketInformation getTicketInformation()
    {
        return mTicketInformation;
    }

    public void setTicketInformation(TicketInformation information)
    {
        mTicketInformation = information;
    }

    public String[] getTicketTimes()
    {
        if (ticketTimes == null)
        {
            return null;
        }

        int length = ticketTimes.length;
        String[] times = new String[length];

        Calendar calendarTime = DailyCalendar.getInstance();
        calendarTime.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat formatDay = new SimpleDateFormat("HH:mm", Locale.KOREA);
        formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));

        for (int i = 0; i < length; i++)
        {
            calendarTime.setTimeInMillis(ticketTimes[i]);
            times[i] = formatDay.format(calendarTime.getTime());
        }

        return times;
    }


    /**
     * 적립금이 반영된 가격
     *
     * @return
     */
    public int getPaymentToPay()
    {
        int price = (mTicketInformation.discountPrice * ticketCount) - (isEnabledBonus ? bonus : 0);

        if (price < 0)
        {
            price = 0;
        }

        return price;
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
