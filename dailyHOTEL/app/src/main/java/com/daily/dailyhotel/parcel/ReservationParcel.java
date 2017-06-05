package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.Reservation;

public class ReservationParcel implements Parcelable
{
    private Reservation mReservation;

    public ReservationParcel(@NonNull Reservation reservation)
    {
        if (reservation == null)
        {
            throw new NullPointerException("reservation == null");
        }

        mReservation = reservation;
    }

    public ReservationParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public Reservation getReservation()
    {
        return mReservation;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mReservation.reservationIndex);
        dest.writeString(mReservation.imageUrl);
        dest.writeInt(mReservation.payType);
        dest.writeString(mReservation.placeName);
        dest.writeString(mReservation.placeType.name());
        dest.writeString(mReservation.checkInDateTime);
        dest.writeString(mReservation.checkOutDateTime);
        dest.writeString(mReservation.comment);
        dest.writeInt(mReservation.remainingDays);
        dest.writeString(mReservation.tid);
        dest.writeInt(mReservation.readyForRefund == true ? 1 : 0);
        dest.writeInt(mReservation.isUsed == true ? 1 : 0);
    }

    private void readFromParcel(Parcel in)
    {
        mReservation = new Reservation();

        mReservation.reservationIndex = in.readInt();
        mReservation.imageUrl = in.readString();
        mReservation.payType = in.readInt();
        mReservation.placeName = in.readString();
        mReservation.placeType = Reservation.PlaceType.valueOf(in.readString());
        mReservation.checkInDateTime = in.readString();
        mReservation.checkOutDateTime = in.readString();
        mReservation.comment = in.readString();
        mReservation.remainingDays = in.readInt();
        mReservation.tid = in.readString();
        mReservation.readyForRefund = in.readInt() == 1 ? true : false;
        mReservation.isUsed = in.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public ReservationParcel createFromParcel(Parcel in)
        {
            return new ReservationParcel(in);
        }

        @Override
        public ReservationParcel[] newArray(int size)
        {
            return new ReservationParcel[size];
        }

    };
}
