package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Gourmet extends Place
{
    public int persons;
    public String category;
    public int categoryCode;
    public int categorySequence;
    public float distance;

    private GourmetFilters mGourmetFilters;

    public int discountPrice;
    public Grade grade;

    public Gourmet()
    {
        super();
    }

    public Gourmet(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeInt(persons);
        dest.writeInt(discountPrice);
        dest.writeSerializable(grade);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        persons = in.readInt();
        discountPrice = in.readInt();
        grade = (Grade) in.readSerializable();
    }

    public boolean setData(JSONObject jsonObject, String imageUrl)
    {
        try
        {
            index = jsonObject.getInt("restaurantIdx");
            name = jsonObject.getString("restaurantName");

            price = jsonObject.getInt("price");
            discountPrice = jsonObject.getInt("discount");
            addressSummary = jsonObject.getString("addrSummary");
            grade = Grade.gourmet;
            districtName = jsonObject.getString("districtName");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            isDailyChoice = jsonObject.getBoolean("isDailychoice");
            isSoldOut = jsonObject.getBoolean("isSoldOut");
            persons = jsonObject.getInt("persons");
            category = jsonObject.getString("category");
            categoryCode = jsonObject.getInt("categoryCode");
            categorySequence = jsonObject.getInt("categorySeq");

            if (jsonObject.has("ratingValue") == true)
            {
                satisfaction = jsonObject.getInt("ratingValue");
            }

            JSONObject imageJSONObject = jsonObject.getJSONObject("imgPathMain");

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

            mGourmetFilters = makeGourmetFilters(category, jsonObject);
        } catch (JSONException e)
        {
            ExLog.d(e.toString());

            return false;
        }

        return true;
    }

    public boolean isFiltered(GourmetCurationOption curationOption)
    {
        if (mGourmetFilters == null)
        {
            return false;
        }

        return mGourmetFilters.isFiltered(curationOption);
    }

    public GourmetFilters getFilters()
    {
        return mGourmetFilters;
    }

    private GourmetFilters makeGourmetFilters(String category, JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return null;
        }

        JSONArray jsonArray = jsonObject.getJSONArray("restaurantTicketList");

        if (jsonArray == null || jsonArray.length() == 0)
        {
            return null;
        }

        int length = jsonArray.length();
        GourmetFilters gourmetFilters = new GourmetFilters(length);
        gourmetFilters.category = category;


        for (int i = 0; i < length; i++)
        {
            gourmetFilters.setGourmetFilter(i, jsonArray.getJSONObject(i));
        }

        setAmenitiesFlag(gourmetFilters, jsonObject);

        return gourmetFilters;
    }

    private void setAmenitiesFlag(GourmetFilters gourmetFilters, JSONObject jsonObject) throws JSONException
    {
        gourmetFilters.amenitiesFlag = GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_NONE;

        boolean parking = false;

        if (jsonObject.has("parking") == true)
        {
            parking = "Y".equalsIgnoreCase(jsonObject.getString("parking"));
        }

        if (parking == true)
        {
            gourmetFilters.amenitiesFlag |= GourmetFilters.FLAG_HOTEL_FILTER_AMENITIES_PARKING;
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Gourmet createFromParcel(Parcel in)
        {
            return new Gourmet(in);
        }

        @Override
        public Gourmet[] newArray(int size)
        {
            return new Gourmet[size];
        }
    };


    public enum Grade
    {
        gourmet(R.string.grade_not_yet, R.color.dh_theme_color, R.drawable.bg_hotel_price_900034);

        private int mNameResId;
        private int mColorResId;
        private int mMarkerResId;

        Grade(int nameResId, int colorResId, int markerResId)
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
}
