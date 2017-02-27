package com.twoheart.dailyhotel.network.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.model.DetailInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 2. 27..
 */
@JsonObject
public abstract class PlaceDetailParams
{
    @JsonField(name = "idx")
    public int index;

    @JsonField
    public String name;

    @JsonField
    public double latitude;

    @JsonField
    public double longitude;

    @JsonField
    public String address;

    // 직접 접근 금지
    // 주의 : Parcelable 후에 해당 값은 사라집니다.
    @JsonField
    public Map<String, List<ImageInformation>> imgPath;

    // 직접 접근 금지
    @JsonField
    public String grade;

    @JsonField
    public int ratingPersons;

    @JsonField
    public int ratingValue;

    @JsonField
    public boolean ratingShow;

    @JsonField
    public String category;

    @JsonField
    public String benefit;

    @JsonField
    public List<String> benefitContents;

    // 직접 접근 금지
    // 주의 : Parcelable 후에 해당 값은 사라집니다.
    @JsonField
    public List<Map<String, List<String>>> details;

    @JsonField
    public String imgUrl;

    @JsonField
    public int wishCount; // 위시리스트 카운트

    @JsonField
    public boolean myWish; // 위시리스트 클릭 상태

    protected ArrayList<ImageInformation> mImageList;
    protected ArrayList<DetailInformation> mDetailList;


    public List<ImageInformation> getImageList()
    {
        return mImageList;
    }

    public List<DetailInformation> getDetailList()
    {
        return mDetailList;
    }
}
