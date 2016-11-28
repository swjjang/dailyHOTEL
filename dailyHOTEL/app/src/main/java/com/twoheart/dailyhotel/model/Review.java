package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 11. 25..
 */

public class Review
{
    public int reserveIdx = -1;
    public ReviewItem reviewItem;
    public ArrayList<ReviewScoreTypes> mReviewScoreTypeList;
    public ArrayList<UseCategoryTypes> mUseCategoryTypeList;

    public class ReviewItem
    {
        public ReviewItem(String baseImagePath, int itemIdx, String itemImagePath, String itemName, //
                          Constants.PlaceType placeType, String useEndDate, String useStartDate)
        {
            this.baseImagePath = baseImagePath;
            this.itemIdx = itemIdx;
            this.itemImagePath = itemImagePath;
            this.itemName = itemName;
            this.placeType = placeType;
            this.useEndDate = useEndDate;
            this.useStartDate = useStartDate;
        }

        public String baseImagePath;
        public int itemIdx;
        public String itemImagePath;
        public String itemName;
        public Constants.PlaceType placeType; // serviceType
        public String useEndDate;
        public String useStartDate;
    }


    public class ReviewScoreTypes
    {
        public ReviewScoreTypes(String code, String description)
        {
            this.code = code;
            this.description = description;
        }

        public String code;
        public String description;
    }

    public class UseCategoryTypes
    {
        public UseCategoryTypes(String code, String description)
        {
            this.code = code;
            this.description = description;
        }

        public String code;
        public String description;
    }

    public void clear()
    {
        this.reserveIdx = -1;
        this.reviewItem = null;
        this.mReviewScoreTypeList = null;
        this.mUseCategoryTypeList = null;
    }

    public void setData(JSONObject jsonObject) throws JSONException
    {
        if (jsonObject == null)
        {
            return;
        }

        reserveIdx = jsonObject.getInt("reserveIdx");

        if (jsonObject.has("reviewItem") == true && jsonObject.isNull("reviewItem") == false)
        {
            JSONObject reviewItemJsonObject = jsonObject.getJSONObject("reviewItem");

            String baseImagePath = reviewItemJsonObject.getString("baseImagePath");
            int itemIdx = reviewItemJsonObject.getInt("itemIdx");
            String itemImagePath = reviewItemJsonObject.getString("itemImagePath");
            String itemName = reviewItemJsonObject.getString("itemName");

            String serviceTypeString = reviewItemJsonObject.getString("serviceTypeString");
            Constants.PlaceType placeType = null;
            if (Util.isTextEmpty(serviceTypeString) == false)
            {
                if (Constants.PlaceType.HOTEL.name().equalsIgnoreCase(serviceTypeString) == true)
                {
                    placeType = Constants.PlaceType.HOTEL;
                } else if (Constants.PlaceType.FNB.name().equalsIgnoreCase(serviceTypeString) == true)
                {
                    placeType = Constants.PlaceType.FNB;
                } else
                {
                    ExLog.d("unKnown service type");
                }
            } else
            {
                ExLog.d("serviceTypeString is null");
            }

            String useEndDate = reviewItemJsonObject.getString("useEndDate");
            String useStartDate = reviewItemJsonObject.getString("useStartDate");

            this.reviewItem = new ReviewItem(baseImagePath, itemIdx, itemImagePath, itemName, //
                placeType, useEndDate, useStartDate);
        }

        if (jsonObject.has("reviewScoreTypes") == true && jsonObject.isNull("reviewScoreTypes") == false)
        {
            JSONArray reviewScoreTypeArray = jsonObject.getJSONArray("reviewScoreTypes");

            int scoreLength = reviewScoreTypeArray.length();
            if (scoreLength > 0)
            {
                this.mReviewScoreTypeList = new ArrayList<>();

                for (int i = 0; i < scoreLength; i++)
                {
                    JSONObject reviewScoreTypeObject = reviewScoreTypeArray.getJSONObject(i);

                    String code = reviewScoreTypeObject.getString("code");
                    String description = reviewScoreTypeObject.getString("description");
                    this.mReviewScoreTypeList.add(new ReviewScoreTypes(code, description));
                }
            }
        }

        if (jsonObject.has("mUseCategoryTypeList") == true && jsonObject.isNull("mUseCategoryTypeList") == false)
        {
            JSONArray useCategoryTypeArray = jsonObject.getJSONArray("mUseCategoryTypeList");

            int categoryLength = useCategoryTypeArray.length();
            if (categoryLength > 0)
            {
                this.mUseCategoryTypeList = new ArrayList<>();

                for (int i = 0; i < categoryLength; i++)
                {
                    JSONObject useCategoryTypeObject = useCategoryTypeArray.getJSONObject(i);

                    String code = useCategoryTypeObject.getString("code");
                    String description = useCategoryTypeObject.getString("description");
                    this.mUseCategoryTypeList.add(new UseCategoryTypes(code, description));
                }
            }
        }

    }
}
