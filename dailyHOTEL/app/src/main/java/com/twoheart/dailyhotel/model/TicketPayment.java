package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.DailyCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TicketPayment implements Parcelable
{
    public int bonus;
    public boolean isEnabledBonus;
    public String checkInTime;
    public String checkOutTime;
    public PaymentType paymentType;
    public int ticketCount;
    public int ticketMaxCount; // 최대 결제 가능한 티켓 개수
    public long ticketTime;
    public long[] ticketTimes;
    private TicketInformation mTicketInformation;
    private Customer mCustomer; // 로그인 유저 정보
    private Guest mGuest; // 실제 예약 혹은 투숙하는 사람

    public long startTicketTime;
    public long endTicketTime;
    public int placeIndex;
    public boolean isDBenefit;
    public String category;

    public TicketPayment()
    {
        // Default Ticket count
        ticketCount = 1;
        ticketMaxCount = 1;
        paymentType = PaymentType.EASY_CARD;
    }

    public TicketPayment(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeValue(mTicketInformation);
        dest.writeInt(bonus);
        dest.writeValue(mCustomer);
        dest.writeByte((byte) (isEnabledBonus ? 1 : 0));
        dest.writeString(checkInTime);
        dest.writeString(checkOutTime);
        dest.writeSerializable(paymentType);
        dest.writeValue(mGuest);
        dest.writeInt(ticketCount);
        dest.writeInt(ticketMaxCount);
        dest.writeLong(ticketTime);
        dest.writeLongArray(ticketTimes);
        dest.writeInt(placeIndex);
        dest.writeByte((byte) (isDBenefit ? 1 : 0));
        dest.writeString(category);
    }

    private void readFromParcel(Parcel in)
    {
        mTicketInformation = (TicketInformation) in.readValue(TicketInformation.class.getClassLoader());
        bonus = in.readInt();
        mCustomer = (Customer) in.readValue(Customer.class.getClassLoader());
        isEnabledBonus = in.readByte() != 0;
        checkInTime = in.readString();
        checkOutTime = in.readString();
        paymentType = (PaymentType) in.readSerializable();
        mGuest = (Guest) in.readValue(Guest.class.getClassLoader());
        ticketCount = in.readInt();
        ticketMaxCount = in.readInt();
        ticketTime = in.readLong();
        ticketTimes = in.createLongArray();
        placeIndex = in.readInt();
        isDBenefit = in.readByte() != 0;
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

    public Customer getCustomer()
    {
        return mCustomer;
    }

    public void setCustomer(Customer customer)
    {
        mCustomer = customer;
    }

    public Guest getGuest()
    {
        return mGuest;
    }

    public void setGuest(Guest guest)
    {
        mGuest = guest;
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public TicketPayment createFromParcel(Parcel in)
        {
            return new TicketPayment(in);
        }

        @Override
        public TicketPayment[] newArray(int size)
        {
            return new TicketPayment[size];
        }
    };

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
}
