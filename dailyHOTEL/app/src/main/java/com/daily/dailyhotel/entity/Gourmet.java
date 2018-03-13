package com.daily.dailyhotel.entity;

import android.content.Context;
import android.os.Parcel;
import android.util.SparseArray;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
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

public class Gourmet
{
    public int index;
    public String imageUrl;
    public String name;
    public int price;
    public int discountPrice;
    public String addressSummary;
    public double latitude;
    public double longitude;
    public boolean isDailyChoice;
    public boolean isSoldOut;
    public int satisfaction;
    public String districtName;
    public int entryPosition;
    public boolean truevr;
    public String stickerUrl;
    public int stickerIndex;

    // 신규 추가
    public int reviewCount;
    public int discountRate;
    public boolean newItem;
    public boolean myWish;
    public String couponDiscountText;

    public String dBenefitText;
    public double distance;
    public int categoryCode;
    public String category;
    public String subCategory;
    public int persons;

    public Grade grade;

    public boolean soldOut;
    public String regionName;

    public String createdWishDateTime; // ISO-8601 위시 등록 시간

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
