package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.model.Hotel.HotelGrade;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class BookingHotelDetail implements Parcelable
{
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public BookingHotelDetail createFromParcel(Parcel in)
        {
            return new BookingHotelDetail(in);
        }

        @Override
        public BookingHotelDetail[] newArray(int size)
        {
            return new BookingHotelDetail[size];
        }
    };

    public int isOverseas; // 0 : 국내 , 1 : 해외
    public String roomName;
    public String guestName;
    public String guestPhone;
    public String checkInDay;
    public String checkOutDay;
    private Hotel mHotel;
    private double mLatitude;
    private double mLongitude;
    private Map<String, List<String>> mSpecification = new HashMap<String, List<String>>();
    private List<String> mImageUrl = new ArrayList<String>();

    public BookingHotelDetail()
    {
    }

    public BookingHotelDetail(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeValue(mHotel);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeMap(mSpecification);
        dest.writeList(mImageUrl);
        dest.writeInt(isOverseas);
        dest.writeString(roomName);
        dest.writeString(guestName);
        dest.writeString(guestPhone);
        dest.writeString(checkInDay);
        dest.writeString(checkOutDay);
    }

    public boolean setData(JSONObject jsonObject)
    {
        // Hotel Setting
        if (mHotel == null)
        {
            mHotel = new Hotel();
        }

        try
        {
            mHotel.setName(jsonObject.getString("hotel_name"));

            try
            {
                mHotel.setCategory(jsonObject.getString("cat"));
            } catch (Exception e)
            {
                mHotel.setCategory(HotelGrade.etc.name());
            }

            mHotel.setAddress(jsonObject.getString("address"));

            //
            JSONObject wrapJSONObject = new JSONObject(jsonObject.getString("spec"));
            JSONArray jsonArray = wrapJSONObject.getJSONArray("wrap");

            setSpecification(jsonArray);

            mLatitude = jsonObject.getDouble("lat");
            mLongitude = jsonObject.getDouble("lng");

            roomName = jsonObject.getString("room_name");
            guestPhone = jsonObject.getString("guest_phone");
            guestName = jsonObject.getString("guest_name");

            long checkin = jsonObject.getLong("checkin_date");
            long checkout = jsonObject.getLong("checkout_date");

            SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH시", Locale.KOREA);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));

            // Check In
            checkInDay = format.format(new Date(checkin));

            // Check Out
            checkOutDay = format.format(new Date(checkout));
        } catch (Exception e)
        {
            return false;
        }

        return true;
    }

    private void readFromParcel(Parcel in)
    {
        mHotel = (Hotel) in.readValue(Hotel.class.getClassLoader());
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        in.readMap(mSpecification, Map.class.getClassLoader());
        in.readList(mImageUrl, List.class.getClassLoader());
        isOverseas = in.readInt();
        roomName = in.readString();
        guestName = in.readString();
        guestPhone = in.readString();
        checkInDay = in.readString();
        checkOutDay = in.readString();
    }

    public List<String> getImageUrl()
    {
        return mImageUrl;
    }

    public void setImageUrl(List<String> imageUrl)
    {
        this.mImageUrl = imageUrl;
    }

    public Hotel getHotel()
    {
        return mHotel;
    }

    public void setHotel(Hotel hotel)
    {
        this.mHotel = hotel;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public void setLatitude(double latitude)
    {
        this.mLatitude = latitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public void setLongitude(double longitude)
    {
        this.mLongitude = longitude;
    }

    public Map<String, List<String>> getSpecification()
    {
        return mSpecification;
    }

    public void setSpecification(Map<String, List<String>> specification)
    {
        this.mSpecification = specification;
    }

    public void setSpecification(JSONArray jsonArray)
    {
        if (jsonArray == null)
        {
            return;
        }

        try
        {
            int length = jsonArray.length();

            Map<String, List<String>> contentList = new LinkedHashMap<String, List<String>>(length);

            for (int i = 0; i < length; i++)
            {
                JSONObject specObj = jsonArray.getJSONObject(i);

                if (specObj == null || specObj.has("key") == false || specObj.has("value") == false)
                {
                    continue;
                }

                String key = specObj.getString("key");
                JSONArray valueArr = specObj.getJSONArray("value");
                List<String> valueList = new ArrayList<String>(valueArr.length());

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

                contentList.put(key, valueList);
                setSpecification(contentList);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

}
