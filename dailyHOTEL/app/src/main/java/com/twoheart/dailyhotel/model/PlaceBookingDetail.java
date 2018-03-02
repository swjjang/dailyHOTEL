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
    public class ReviewStatusType
    {
        public static final String ADDABLE = "ADDABLE"; // 리뷰가능기간, 리뷰없음
        public static final String MODIFIABLE = "MODIFIABLE"; // 리뷰가능기간, 만족도만 선택 된 상태
        public static final String COMPLETE = "COMPLETE"; // 리뷰있음
        public static final String NONE = "NONE"; // (리뷰불가능기간, 리뷰없음) or (마이그레이션 데이터)
    }

    public int placeIndex;

    public String address;
    public double latitude;
    public double longitude;
    public String placeName;
    public String userName;
    public String guestName;
    public String guestPhone;
    public String guestEmail;
    public String addressSummary;
    public boolean isOverseas;
    //
    public String paymentDate;
    public int price;
    public int bonus;
    public int coupon;
    public int paymentPrice;
    public int reservationIndex;
    public String reviewStatusType;

    public String phone1;
    public String phone2;
    public String phone3;

    public String cancelDateTime; // 취소 일시(취소가 아닐때에는 내려오지 않음)

    private Map<String, List<String>> mSpecification = new LinkedHashMap<>();

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(placeIndex);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeMap(mSpecification);
        dest.writeString(placeName);
        dest.writeString(userName);
        dest.writeString(guestName);
        dest.writeString(guestPhone);
        dest.writeString(guestEmail);
        dest.writeString(addressSummary);
        dest.writeInt(isOverseas ? 1 : 0);
        dest.writeString(phone1);
        dest.writeString(phone2);
        dest.writeString(phone3);
        //
        dest.writeString(paymentDate);
        dest.writeInt(price);
        dest.writeInt(bonus);
        dest.writeInt(coupon);
        dest.writeInt(paymentPrice);
        dest.writeInt(reservationIndex);
        dest.writeString(reviewStatusType);
        dest.writeString(cancelDateTime);
    }

    protected void readFromParcel(Parcel in)
    {
        placeIndex = in.readInt();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        in.readMap(mSpecification, Map.class.getClassLoader());
        placeName = in.readString();
        userName = in.readString();
        guestName = in.readString();
        guestPhone = in.readString();
        guestEmail = in.readString();
        addressSummary = in.readString();
        isOverseas = in.readInt() == 1;
        phone1 = in.readString();
        phone2 = in.readString();
        phone3 = in.readString();
        //
        paymentDate = in.readString();
        price = in.readInt();
        bonus = in.readInt();
        coupon = in.readInt();
        paymentPrice = in.readInt();
        reservationIndex = in.readInt();
        reviewStatusType = in.readString();
        cancelDateTime = in.readString();
    }

    public Map<String, List<String>> getSpecification()
    {
        return mSpecification;
    }

    public void setSpecification(Map<String, List<String>> specificationMap)
    {
        mSpecification = specificationMap;
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
