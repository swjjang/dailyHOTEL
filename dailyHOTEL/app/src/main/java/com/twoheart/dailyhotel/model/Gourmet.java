package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.GourmetWishDetails;
import com.twoheart.dailyhotel.network.model.GourmetWishItem;
import com.twoheart.dailyhotel.network.model.Prices;
import com.twoheart.dailyhotel.network.model.Sticker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public int minimumOrderQuantity;
    public boolean expired;
    public String regionName;

    public Grade grade;
    public String closeTime;
    public String endEatingTime;
    public String lastOrderTime;
    public String menuBenefit;
    private List<String> menuDetail;
    public String menuSummary;
    public String needToKnow;
    public String openTime;
    public int pricePerPerson;
    public String startEatingTime;
    public int ticketIdx;

    public Gourmet()
    {
        super();
    }

    public Gourmet(Parcel in)
    {
        readFromParcel(in);
    }

    public void setMenuDetail(List<String> menuDetail)
    {
        this.menuDetail = menuDetail;
    }

    public List<String> getMenuDetail()
    {
        return this.menuDetail;
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
        dest.writeString(category);
        dest.writeInt(categoryCode);
        dest.writeInt(categorySequence);
        dest.writeString(subCategory);
        dest.writeDouble(distance);
        dest.writeInt(availableTicketNumbers);
        dest.writeInt(minimumOrderQuantity);
        dest.writeInt(expired ? 1 : 0);
        dest.writeString(regionName);
        dest.writeSerializable(grade);
        dest.writeString(closeTime);
        dest.writeString(endEatingTime);
        dest.writeString(lastOrderTime);
        dest.writeString(menuBenefit);
        dest.writeStringList(menuDetail);
        dest.writeString(menuSummary);
        dest.writeString(needToKnow);
        dest.writeString(openTime);
        dest.writeInt(pricePerPerson);
        dest.writeString(startEatingTime);
        dest.writeInt(ticketIdx);
    }

    protected void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);

        dBenefitText = in.readString();
        persons = in.readInt();
        category = in.readString();
        categoryCode = in.readInt();
        categorySequence = in.readInt();
        subCategory = in.readString();
        distance = in.readDouble();
        availableTicketNumbers = in.readInt();
        minimumOrderQuantity = in.readInt();
        expired = in.readInt() == 1;
        regionName = in.readString();
        grade = (Grade) in.readSerializable();
        closeTime = in.readString();
        endEatingTime = in.readString();
        lastOrderTime = in.readString();
        menuBenefit = in.readString();
        in.readStringList(menuDetail == null ? new ArrayList<String>() : menuDetail);
        menuSummary = in.readString();
        needToKnow = in.readString();
        openTime = in.readString();
        pricePerPerson = in.readInt();
        startEatingTime = in.readString();
        ticketIdx = in.readInt();
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

            // 인트라넷에서 값을 잘못 넣는 경우가 있다.
            if (DailyTextUtils.isTextEmpty(addressSummary) == false)
            {
                if (addressSummary.indexOf('|') >= 0)
                {
                    addressSummary = addressSummary.replace(" | ", "ㅣ");
                } else if (addressSummary.indexOf('l') >= 0)
                {
                    addressSummary = addressSummary.replace(" l ", "ㅣ");
                }
            }

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
            regionName = jsonObject.getString("regionName");

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

            if (jsonObject.has("truevr") == true)
            {
                truevr = jsonObject.getBoolean("truevr");
            }

            if (jsonObject.has("stickerIdx") == true && jsonObject.isNull("stickerIdx") == false)
            {
                stickerIndex = jsonObject.getInt("stickerIdx");

                if (stringSparseArray != null && stringSparseArray.size() > 0)
                {
                    stickerUrl = stringSparseArray.get(stickerIndex);
                }
            }

            if (jsonObject.has("availableTicketNumbers") == true)
            {
                availableTicketNumbers = jsonObject.getInt("availableTicketNumbers");
            } else
            {
                availableTicketNumbers = -1;
            }

            if (jsonObject.has("minimumOrderQuantity") == true)
            {
                minimumOrderQuantity = jsonObject.getInt("minimumOrderQuantity");
            } else
            {
                minimumOrderQuantity = -1;
            }

            if (jsonObject.has("isExpired") == true)
            {
                expired = jsonObject.getBoolean("isExpired");
            } else
            {
                expired = false;
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

            reviewCount = jsonObject.getInt("reviewCount");
            discountRate = jsonObject.getInt("discountRate");
            newItem = jsonObject.getBoolean("newItem");
            myWish = jsonObject.getBoolean("myWish");
            couponDiscountText = jsonObject.getString("couponDiscountText");
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

            persons = gourmetWishDetails != null ? gourmetWishDetails.persons : 0;
            category = gourmetWishDetails != null ? gourmetWishDetails.category : "";
            subCategory = gourmetWishDetails != null ? gourmetWishDetails.subCategory : "";
            satisfaction = gourmetWishItem.rating;
            truevr = gourmetWishDetails != null && gourmetWishDetails.truevr;

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

            reviewCount = gourmetWishItem.reviewCount;
            newItem = gourmetWishItem.newItem;
            myWish = gourmetWishItem.myWish;
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
        gourmet(R.string.grade_not_yet, R.color.dh_theme_color, R.drawable.bg_hotel_price_off);

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
