package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class PlaceBookingDetail implements Parcelable
{
    public int placeIndex;

    public String address;
    public double latitude;
    public double longitude;
    public String placeName;
    public String guestName;
    public String guestPhone;
    public String guestEmail;
    public String addressSummary;
    public String gourmetPhone;
    //
    public String paymentDate;
    public int price;
    public int bonus;
    public int coupon;
    public int paymentPrice;
    public long currentDateTime;
    public long dailyDateTime;

    public int reservationIndex;

    private Map<String, List<String>> mSpecification = new LinkedHashMap<>();

    public PlaceBookingDetail()
    {
    }

    public abstract void setData(JSONObject jsonObject) throws Exception;

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(placeIndex);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeMap(mSpecification);
        dest.writeString(placeName);
        dest.writeString(guestName);
        dest.writeString(guestPhone);
        dest.writeString(guestEmail);
        dest.writeString(addressSummary);
        dest.writeString(gourmetPhone);
        //
        dest.writeString(paymentDate);
        dest.writeInt(price);
        dest.writeInt(bonus);
        dest.writeInt(coupon);
        dest.writeInt(paymentPrice);
        dest.writeLong(currentDateTime);
        dest.writeLong(dailyDateTime);

        dest.writeInt(reservationIndex);
    }

    protected void readFromParcel(Parcel in)
    {
        placeIndex = in.readInt();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        in.readMap(mSpecification, Map.class.getClassLoader());
        placeName = in.readString();
        guestName = in.readString();
        guestPhone = in.readString();
        guestEmail = in.readString();
        addressSummary = in.readString();
        gourmetPhone = in.readString();
        //
        paymentDate = in.readString();
        price = in.readInt();
        bonus = in.readInt();
        coupon = in.readInt();
        paymentPrice = in.readInt();
        currentDateTime = in.readLong();
        dailyDateTime = in.readLong();

        reservationIndex = in.readInt();
    }

    public Map<String, List<String>> getSpecification()
    {
        return mSpecification;
    }

    protected void setSpecification(JSONArray jsonArray) throws Exception
    {
        if (jsonArray == null)
        {
            return;
        }

        int length = jsonArray.length();

        mSpecification.clear();

        for (int i = 0; i < length; i++)
        {
            JSONObject specObj = jsonArray.getJSONObject(i);

            if (specObj == null || specObj.has("key") == false || specObj.has("value") == false)
            {
                continue;
            }

            String key = specObj.getString("key");
            JSONArray valueArr = specObj.getJSONArray("value");
            List<String> valueList = new ArrayList<>(valueArr.length());

            for (int j = 0; j < valueArr.length(); j++)
            {
                JSONObject valueObj = valueArr.getJSONObject(j);

                if (valueObj == null || valueObj.has("value") == false)
                {
                    continue;
                }

                String value = valueObj.getString("value");
                valueList.add(value);
            }

            mSpecification.put(key, valueList);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}
