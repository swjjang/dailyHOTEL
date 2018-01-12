package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.Review;
import com.daily.dailyhotel.entity.ReviewAnswerValue;
import com.daily.dailyhotel.entity.ReviewItem;
import com.daily.dailyhotel.entity.ReviewQuestionItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JsonObject
public class ReviewData
{
    @JsonField(name = "requiredCommentReview")
    public boolean requiredCommentReview;

    @JsonField(name = "reserveIdx")
    public int reserveIdx;

    @JsonField(name = "reviewItem")
    public ReviewItemData reviewItem;

    @JsonField(name = "reviewPickQuestions")
    public List<ReviewQuestionItemData> reviewPickQuestions;

    @JsonField(name = "reviewScoreQuestions")
    public List<ReviewQuestionItemData> reviewScoreQuestions;

    public ReviewData()
    {

    }

    public Review getReview()
    {
        Review review = new Review();

        review.requiredCommentReview = requiredCommentReview;
        review.reserveIdx = reserveIdx;
        review.setReviewItem(reviewItem.getReviewItem());

        List<ReviewQuestionItem> reviewPickQuestionList = new ArrayList<>();

        if (reviewPickQuestions != null && reviewPickQuestions.size() > 0)
        {
            for (ReviewQuestionItemData reviewQuestionItemData : reviewPickQuestions)
            {
                reviewPickQuestionList.add(reviewQuestionItemData.getReviewQuestionItem());
            }
        }

        review.setReviewPickQuestionList(reviewPickQuestionList);

        List<ReviewQuestionItem> reviewScoreQuestionList = new ArrayList<>();

        if (reviewScoreQuestions != null && reviewScoreQuestions.size() > 0)
        {
            for (ReviewQuestionItemData reviewQuestionItemData : reviewScoreQuestions)
            {
                reviewScoreQuestionList.add(reviewQuestionItemData.getReviewQuestionItem());
            }
        }

        review.setReviewScoreQuestionList(reviewScoreQuestionList);

        return review;
    }

    @JsonObject
    static class ReviewItemData
    {
        @JsonField(name = "baseImagePath")
        public String baseImagePath;

        @JsonField(name = "itemIdx")
        public int itemIdx;

        @JsonField(name = "itemImagePath")
        public String itemImagePath;

        @JsonField(name = "itemName")
        public String itemName;

        @JsonField(name = "serviceType")
        public String serviceType;

        @JsonField(name = "imageMap")
        public ImageMapData imageMap;

        @JsonField(name = "useEndDate")
        public String useEndDate;

        @JsonField(name = "useStartDate")
        public String useStartDate;

        public ReviewItem getReviewItem()
        {
            ReviewItem reviewItem = new ReviewItem();
            reviewItem.itemIdx = itemIdx;

            try
            {
                switch (serviceType)
                {
                    case "HOTEL":
                    case "GOURMET":
                        JSONObject imageJSONObject = new JSONObject(itemImagePath);

                        Iterator<String> iterator = imageJSONObject.keys();
                        if (iterator.hasNext() == true)
                        {
                            String key = iterator.next();

                            JSONArray pathJSONArray = imageJSONObject.getJSONArray(key);

                            ImageMap imageMap = new ImageMap();
                            imageMap.bigUrl = imageMap.mediumUrl = imageMap.smallUrl = baseImagePath + key + pathJSONArray.getString(0);
                            reviewItem.setImageMap(imageMap);
                        }
                        break;

                    case "OUTBOUND":
                        if (imageMap != null)
                        {
                            reviewItem.setImageMap(imageMap.getImageMap());
                        }
                        break;
                }


            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }

            reviewItem.itemName = itemName;
            reviewItem.serviceType = serviceType.toUpperCase();
            reviewItem.useEndDate = useEndDate;
            reviewItem.useStartDate = useStartDate;

            return reviewItem;
        }
    }

    @JsonObject
    static class ReviewQuestionItemData
    {
        @JsonField(name = "answerCode")
        public String answerCode;

        @JsonField(name = "description")
        public String description;

        @JsonField(name = "title")
        public String title;

        @JsonField(name = "answerValues")
        public List<ReviewAnswerValueData> answerValues;

        public ReviewQuestionItem getReviewQuestionItem()
        {
            ReviewQuestionItem reviewQuestionItem = new ReviewQuestionItem();

            reviewQuestionItem.answerCode = answerCode;
            reviewQuestionItem.description = description;
            reviewQuestionItem.title = title;

            List<ReviewAnswerValue> reviewAnswerValueList = new ArrayList<>();

            if (answerValues != null && answerValues.size() > 0)
            {
                for (ReviewAnswerValueData reviewAnswerValueData : answerValues)
                {
                    reviewAnswerValueList.add(reviewAnswerValueData.getReviewAnswerValue());
                }
            }

            reviewQuestionItem.setAnswerValueList(reviewAnswerValueList);

            return reviewQuestionItem;
        }
    }

    @JsonObject
    static class ReviewAnswerValueData
    {
        @JsonField(name = "code")
        public String code;

        @JsonField(name = "description")
        public String description;

        public ReviewAnswerValue getReviewAnswerValue()
        {
            ReviewAnswerValue reviewAnswerValue = new ReviewAnswerValue();
            reviewAnswerValue.code = code;
            reviewAnswerValue.description = description;

            return reviewAnswerValue;
        }
    }

    @JsonObject
    static class ImageMapData
    {
        @JsonField(name = "big")
        public String big;

        @JsonField(name = "medium")
        public String medium;

        @JsonField(name = "small")
        public String small;

        public ImageMap getImageMap()
        {
            ImageMap imageMap = new ImageMap();
            imageMap.bigUrl = big;
            imageMap.mediumUrl = medium;
            imageMap.smallUrl = small;

            return imageMap;
        }
    }
}
