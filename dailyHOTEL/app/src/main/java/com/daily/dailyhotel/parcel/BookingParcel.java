package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.Booking;

public class BookingParcel implements Parcelable
{
    private Booking mBooking;

    public BookingParcel(@NonNull Booking booking)
    {
        if (booking == null)
        {
            throw new NullPointerException("reservation == null");
        }

        mBooking = booking;
    }

    public BookingParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public Booking getBooking()
    {
        return mBooking;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mBooking.index);
        dest.writeString(mBooking.imageUrl);
        dest.writeInt(mBooking.statePayment);
        dest.writeString(mBooking.placeName);
        dest.writeString(mBooking.placeType.name());
        dest.writeString(mBooking.checkInDateTime);
        dest.writeString(mBooking.checkOutDateTime);
        dest.writeString(mBooking.comment);
        dest.writeInt(mBooking.remainingDays);
        dest.writeString(mBooking.tid);
        dest.writeInt(mBooking.readyForRefund == true ? 1 : 0);
        dest.writeInt(mBooking.isUsed == true ? 1 : 0);
    }

    private void readFromParcel(Parcel in)
    {
        mBooking = new Booking();

        mBooking.index = in.readInt();
        mBooking.imageUrl = in.readString();
        mBooking.statePayment = in.readInt();
        mBooking.placeName = in.readString();
        mBooking.placeType = Booking.PlaceType.valueOf(in.readString());
        mBooking.checkInDateTime = in.readString();
        mBooking.checkOutDateTime = in.readString();
        mBooking.comment = in.readString();
        mBooking.remainingDays = in.readInt();
        mBooking.tid = in.readString();
        mBooking.readyForRefund = in.readInt() == 1 ? true : false;
        mBooking.isUsed = in.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public BookingParcel createFromParcel(Parcel in)
        {
            return new BookingParcel(in);
        }

        @Override
        public BookingParcel[] newArray(int size)
        {
            return new BookingParcel[size];
        }
    };
}
