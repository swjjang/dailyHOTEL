package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONObject;

public class Booking implements Parcelable
{
    public static final int TYPE_ENTRY = 0;
    public static final int TYPE_SECTION = 1;

    public int reservationIndex; // 호텔 예약 고유 번호.
    public int type = TYPE_ENTRY;
    public String placeName;
    public int payType;
    public String tid;
    public String comment;
    public long checkinTime;
    public long checkoutTime;

    public String hotelImageUrl;
    public boolean isUsed;
    public boolean readyForRefund;
    public Constants.PlaceType placeType;

    public Booking(String sectionName)
    {
        placeName = sectionName;
        type = TYPE_SECTION;
    }

    public Booking(Parcel in)
    {
        readFromParcel(in);
    }

    public Booking(JSONObject jsonObject)
    {
        try
        {
            if (jsonObject.has("reservation_rec_idx") == true)
            {
                reservationIndex = jsonObject.getInt("reservation_rec_idx");
            }

            placeName = jsonObject.getString("hotel_name");
            payType = jsonObject.getInt("pay_type");

            comment = jsonObject.getString("comment");
            tid = jsonObject.getString("tid");
            checkinTime = jsonObject.getLong("checkin_time");
            checkoutTime = jsonObject.getLong("checkout_time");

            JSONArray jsonArray = jsonObject.getJSONArray("img");
            hotelImageUrl = jsonArray.getJSONObject(0).getString("path");

            readyForRefund = jsonObject.getBoolean("readyForRefund");

            placeType = Constants.PlaceType.valueOf(jsonObject.getString("type").toUpperCase());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(reservationIndex);
        dest.writeInt(type);
        dest.writeString(placeName);
        dest.writeInt(payType);
        dest.writeString(tid);
        dest.writeString(comment);

        dest.writeLong(checkinTime);
        dest.writeLong(checkoutTime);
        dest.writeString(hotelImageUrl);
        dest.writeInt(isUsed ? 1 : 0);
        dest.writeInt(readyForRefund ? 1 : 0);
        dest.writeString(placeType.name());
    }

    private void readFromParcel(Parcel in)
    {
        reservationIndex = in.readInt();
        type = in.readInt();
        placeName = in.readString();
        payType = in.readInt();
        tid = in.readString();
        comment = in.readString();

        checkinTime = in.readLong();
        checkoutTime = in.readLong();
        hotelImageUrl = in.readString();
        isUsed = in.readInt() == 1;
        readyForRefund = in.readInt() == 1;
        placeType = Constants.PlaceType.valueOf(in.readString());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Booking createFromParcel(Parcel in)
        {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size)
        {
            return new Booking[size];
        }
    };
}
