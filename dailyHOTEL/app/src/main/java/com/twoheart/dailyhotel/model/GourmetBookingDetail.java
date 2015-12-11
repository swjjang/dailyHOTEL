package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GourmetBookingDetail extends PlaceBookingDetail
{
    public int ticketCount;
    public String ticketName;
    public String sday;
    public String category;

    public GourmetBookingDetail()
    {
    }

    public GourmetBookingDetail(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(ticketCount);
        dest.writeString(ticketName);
        dest.writeString(sday);
        dest.writeString(category);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        ticketCount = in.readInt();
        ticketName = in.readString();
        sday = in.readString();
        category = in.readString();
    }

    public void setData(JSONObject jsonObject) throws Exception
    {
        index = jsonObject.getInt("idx");
        address = jsonObject.getString("address");
        latitude = jsonObject.getDouble("latitude");
        longitude = jsonObject.getDouble("longitude");
        placeName = jsonObject.getString("restaurant_name");

        grade = Place.Grade.gourmet;
        category = jsonObject.getString("category");
        guestName = jsonObject.getString("customer_name");
        guestPhone = jsonObject.getString("customer_phone");
        guestEmail = jsonObject.getString("customer_email");
        addressSummary = jsonObject.getString("customer_email");

        //
        JSONObject wrapJSONObject = new JSONObject(jsonObject.getString("description"));
        JSONArray jsonArray = wrapJSONObject.getJSONArray("wrap");

        setSpecification(jsonArray);

        ticketCount = jsonObject.getInt("ticket_count");
        ticketName = jsonObject.getString("ticket_name");
        long day = jsonObject.getLong("arrival_time");

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd(EEE) HH:mm", Locale.KOREA);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        sday = format.format(new Date(day));

        String phone1 = jsonObject.getString("phone1");
        String phone2 = jsonObject.getString("phone2");
        String phone3 = jsonObject.getString("phone3");

        if (Util.isTextEmpty(phone1) == false)
        {
            gourmetPhone = phone1;
        } else if (Util.isTextEmpty(phone2) == false)
        {
            gourmetPhone = phone2;
        } else if (Util.isTextEmpty(phone3) == false)
        {
            gourmetPhone = phone3;
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public GourmetBookingDetail createFromParcel(Parcel in)
        {
            return new GourmetBookingDetail(in);
        }

        @Override
        public GourmetBookingDetail[] newArray(int size)
        {
            return new GourmetBookingDetail[size];
        }
    };
}
