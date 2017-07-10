package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.GourmetWishDetails;
import com.twoheart.dailyhotel.network.model.GourmetWishItem;
import com.twoheart.dailyhotel.network.model.Prices;
import com.twoheart.dailyhotel.network.model.Sticker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Gourmet extends Place
{
    public String dBenefitText;
    public int persons;
    public String category;
    public int categoryCode;
    public int categorySequence;
    public String subCategory;
    public double distance;
    public int availableTicketNumbers;

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
    public int getGradeMarkerResId()
    {
        return grade.getMarkerResId();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);

        dest.writeString(dBenefitText);
        dest.writeInt(persons);
        dest.writeSerializable(grade);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        dBenefitText = in.readString();
        persons = in.readInt();
        grade = (Grade) in.readSerializable();
    }

    public boolean setData(JSONObject jsonObject, String imageUrl, SparseArray<String> stringSparseArray)
    {
        try
        {
            index = jsonObject.getInt("restaurantIdx");

            if (jsonObject.has("restaurantName") == true)
            {
                name = jsonObject.getString("restaurantName");
            } else if (jsonObject.has("name") == true)
            {
                name = jsonObject.getString("name");
            }

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

            if (jsonObject.has("categorySub") == true)
            {
                subCategory = jsonObject.getString("categorySub");
            }

            if (jsonObject.has("rating") == true)
            {
                satisfaction = jsonObject.getInt("rating");
            }

            if (jsonObject.has("distance") == true)
            {
                distance = jsonObject.getDouble("distance");
            }

            // 추후에 고메가 VR지원되면 보여지도록 한다.
            //            if (jsonObject.has("truevr") == true)
            //            {
            //                truevr = jsonObject.getBoolean("truevr");
            //            }


            if (jsonObject.has("stickerIdx") == true && jsonObject.isNull("stickerIdx") == false)
            {
                stickerIndex = jsonObject.getInt("stickerIdx");

                if (stringSparseArray != null && stringSparseArray.size() > 0)
                {
                    stickerUrl = stringSparseArray.get(stickerIndex);
                }
            }

            truevr = false;

            if (jsonObject.has("availableTicketNumbers") == true)
            {
                availableTicketNumbers = jsonObject.getInt("availableTicketNumbers");
            } else
            {
                availableTicketNumbers = -1;
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

            if (jsonObject.has("benefit") == true)
            {
                dBenefitText = jsonObject.getString("benefit");
            } else
            {
                dBenefitText = null;
            }
        } catch (JSONException e)
        {
            ExLog.d(e.toString());

            return false;
        }

        return true;
    }

    public boolean setData(GourmetWishItem gourmetWishItem, String imageUrl, boolean isLowResource)
    {
        try
        {
            index = gourmetWishItem.index;
            name = gourmetWishItem.title;

            Prices prices = gourmetWishItem.prices;

            price = prices == null ? 0 : prices.normalPrice;
            discountPrice = prices == null ? 0 : prices.discountPrice;

            addressSummary = gourmetWishItem.addrSummary;
            grade = Grade.gourmet;
            districtName = gourmetWishItem.regionName;

            GourmetWishDetails gourmetWishDetails = gourmetWishItem.getDetails();

            persons = gourmetWishDetails != null ? gourmetWishDetails.persons : 1;
            category = gourmetWishDetails != null ? gourmetWishDetails.category : "";
            subCategory = gourmetWishDetails != null ? gourmetWishDetails.subCategory : "";

            satisfaction = gourmetWishItem.rating;

            // 추후에 고메가 VR지원되면 보여지도록 한다.
            //            if (jsonObject.has("truevr") == true)
            //            {
            //                truevr = jsonObject.getBoolean("truevr");
            //            }
            truevr = false;

            Sticker sticker = gourmetWishDetails != null ? gourmetWishDetails.sticker : null;
            if (sticker != null)
            {
                stickerIndex = sticker.index;
                stickerUrl = isLowResource == false ? sticker.defaultImageUrl : sticker.lowResolutionImageUrl;
            }

            try
            {
                this.imageUrl = imageUrl + gourmetWishItem.imageUrl;
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            dBenefitText = null;
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            return false;
        }

        return true;
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
