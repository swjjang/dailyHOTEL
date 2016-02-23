package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Hotel implements Parcelable
{
    public int averageDiscount;
    public double latitude;
    public double longitude;
    public boolean isDailyChoice;
    public int saleIndex;
    public boolean isDBenefit;
    public int nights;
    public String imageUrl;
    private String name;
    private int price;
    private String address;
    private HotelGrade grade;
    private int idx;
    private int availableRoom;
    private int sequence;
    private String detailRegion;
    public int satisfaction;
    public float distance; // 정렬시에 보여주는 내용
    public String categoryCode;

    private HotelFilters mHotelFilters;

    public Hotel()
    {
        super();
    }

    public Hotel(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(averageDiscount);
        dest.writeString(address);
        dest.writeSerializable(grade);
        dest.writeInt(idx);
        dest.writeInt(availableRoom);
        dest.writeInt(sequence);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(isDailyChoice ? 1 : 0);
        dest.writeInt(saleIndex);
        dest.writeInt(isDBenefit ? 1 : 0);
        dest.writeInt(satisfaction);
        dest.writeString(categoryCode);
    }

    private void readFromParcel(Parcel in)
    {
        imageUrl = in.readString();
        name = in.readString();
        price = in.readInt();
        averageDiscount = in.readInt();
        address = in.readString();
        grade = (HotelGrade) in.readSerializable();
        idx = in.readInt();
        availableRoom = in.readInt();
        sequence = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        isDailyChoice = in.readInt() == 1 ? true : false;
        saleIndex = in.readInt();
        isDBenefit = in.readInt() == 1 ? true : false;
        satisfaction = in.readInt();
        categoryCode = in.readString();
    }

    public HotelGrade getGrade()
    {
        return grade;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public int getIdx()
    {
        return idx;
    }

    public void setIdx(int idx)
    {
        this.idx = idx;
    }

    public int getAvailableRoom()
    {
        return availableRoom;
    }

    public void setAvailableRoom(int availableRoom)
    {
        this.availableRoom = availableRoom;
    }

    public int getSequence()
    {
        return sequence;
    }

    public void setSequence(int sequence)
    {
        this.sequence = sequence;
    }

    public String getDetailRegion()
    {
        return detailRegion;
    }

    public void setDetailRegion(String detailRegion)
    {
        this.detailRegion = detailRegion;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public boolean setHotel(JSONObject jsonObject, String imageUrl, int nights)
    {
        this.nights = nights;

        try
        {
            name = jsonObject.getString("name");
            price = Integer.parseInt(jsonObject.getString("price"));
            averageDiscount = Integer.parseInt(jsonObject.getString("discount_avg"));
            address = jsonObject.getString("addr_summary");

            try
            {
                grade = HotelGrade.valueOf(jsonObject.getString("cat"));
            } catch (Exception e)
            {
                grade = HotelGrade.etc;
            }

            idx = jsonObject.getInt("idx");
            availableRoom = jsonObject.getInt("avail_room_count");
            sequence = jsonObject.getInt("seq");
            detailRegion = jsonObject.getString("district_name");
            categoryCode = jsonObject.getString("category");

            JSONObject imageJSONObject = jsonObject.getJSONObject("img_path_main");

            Iterator<String> iterator = imageJSONObject.keys();
            while (iterator.hasNext())
            {
                String key = iterator.next();

                try
                {
                    JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);
                    this.imageUrl = imageUrl + key + pathJSONArray.getString(0);
                    break;
                } catch (JSONException e)
                {
                }
            }

            if (jsonObject.has("lat") == true)
            {
                latitude = jsonObject.getDouble("lat");
            }

            if (jsonObject.has("lng") == true)
            {
                longitude = jsonObject.getDouble("lng");
            }

            if (jsonObject.has("is_dailychoice") == true)
            {
                isDailyChoice = jsonObject.getBoolean("is_dailychoice");
            }

            if (jsonObject.has("sale_idx") == true)
            {
                saleIndex = jsonObject.getInt("sale_idx");
            }

            if (jsonObject.has("hotel_benefit") == true)
            {
                String dBenefit = jsonObject.getString("hotel_benefit");

                if (Util.isTextEmpty(dBenefit) == true || "null".equalsIgnoreCase(dBenefit) == true)
                {
                    isDBenefit = false;
                } else
                {
                    isDBenefit = true;
                }
            }

            if (jsonObject.has("rating_value") == true)
            {
                satisfaction = jsonObject.getInt("rating_value");
            }

            mHotelFilters = makeHotelFilters(jsonObject.getJSONArray("roomTypeList"));
        } catch (JSONException e)
        {
            ExLog.d(e.toString());

            return false;
        }

        return true;
    }

    public boolean isFiltered(int flag, int person)
    {
        if(mHotelFilters == null)
        {
            return false;
        }

        return mHotelFilters.isFiltered(flag, person);
    }

    public HotelFilters getFilters()
    {
        return mHotelFilters;
    }

    private HotelFilters makeHotelFilters(JSONArray jsonArray) throws JSONException
    {
        if (jsonArray == null || jsonArray.length() == 0)
        {
            return null;
        }

        int length = jsonArray.length();
        HotelFilters hotelFilters = new HotelFilters(length);

        for (int i = 0; i < length; i++)
        {
            hotelFilters.setHotelFilter(i, jsonArray.getJSONObject(i));
        }

        return hotelFilters;
    }

    public enum HotelGrade
    {
        special(R.string.grade_special, R.color.grade_special, R.drawable.bg_hotel_price_special1),
        special1(R.string.grade_special1, R.color.grade_special1, R.drawable.bg_hotel_price_special1),
        special2(R.string.grade_special2, R.color.grade_special2, R.drawable.bg_hotel_price_special2),
        //
        biz(R.string.grade_biz, R.color.grade_business, R.drawable.bg_hotel_price_business),
        hostel(R.string.grade_hostel, R.color.grade_business, R.drawable.bg_hotel_price_business),
        grade1(R.string.grade_1, R.color.grade_business, R.drawable.bg_hotel_price_business),
        grade2(R.string.grade_2, R.color.grade_business, R.drawable.bg_hotel_price_business),
        grade3(R.string.grade_3, R.color.grade_business, R.drawable.bg_hotel_price_business),
        //
        resort(R.string.grade_resort, R.color.grade_resort_pension_condo, R.drawable.bg_hotel_price_pension),
        pension(R.string.grade_pension, R.color.grade_resort_pension_condo, R.drawable.bg_hotel_price_pension),
        fullvilla(R.string.grade_fullvilla, R.color.grade_resort_pension_condo, R.drawable.bg_hotel_price_pension),
        condo(R.string.grade_condo, R.color.grade_resort_pension_condo, R.drawable.bg_hotel_price_pension),
        //
        boutique(R.string.grade_boutique, R.color.grade_boutique, R.drawable.bg_hotel_price_boutique),
        design(R.string.grade_design, R.color.grade_design, R.drawable.bg_hotel_price_design),
        //
        residence(R.string.grade_residence, R.color.grade_residence, R.drawable.bg_hotel_price_residence),
        //
        etc(R.string.grade_not_yet, R.color.grade_not_yet, R.drawable.bg_hotel_price_etc);

        private int mNameResId;
        private int mColorResId;
        private int mMarkerResId;

        private HotelGrade(int nameResId, int colorResId, int markerResId)
        {
            mNameResId = nameResId;
            mColorResId = colorResId;
            mMarkerResId = markerResId;
        }

        public String getName(Context context)
        {
            return context.getString(mNameResId);
        }

        public int getColorResId()
        {
            return mColorResId;
        }

        public int getMarkerResId()
        {
            return mMarkerResId;
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Hotel createFromParcel(Parcel in)
        {
            return new Hotel(in);
        }

        @Override
        public Hotel[] newArray(int size)
        {
            return new Hotel[size];
        }

    };
}
