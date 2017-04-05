package com.twoheart.dailyhotel.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.model.DetailInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 2. 27..
 */
@JsonObject
public abstract class PlaceDetailParams<E> implements Parcelable
{
    @JsonField(name = "idx")
    public int index;

    @JsonField(name = "name")
    public String name;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "address")
    public String address;

    // 직접 접근 금지
    // 주의 : Parcelable 후에 해당 값은 사라집니다.
    @JsonField(name = "imgPath")
    public Map<String, List<ImageInformation>> imgPath;

    // 직접 접근 금지
    @JsonField(name = "grade")
    public String grade;

    @JsonField(name = "ratingPersons")
    public int ratingPersons;

    @JsonField(name = "ratingValue")
    public int ratingValue;

    @JsonField(name = "ratingShow")
    public boolean ratingShow;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "benefit")
    public String benefit;

    @JsonField(name = "benefitContents")
    public List<String> benefitContents;

    // 직접 접근 금지
    // 주의 : Parcelable 후에 해당 값은 사라집니다.
    @JsonField(name = "details")
    public List<Map<String, List<String>>> details;

    @JsonField(name = "imgUrl")
    public String imgUrl;

    @JsonField(name = "wishCount")
    public int wishCount; // 위시리스트 카운트

    @JsonField(name = "myWish")
    public boolean myWish; // 위시리스트 클릭 상태

    @JsonIgnore
    protected ArrayList<ImageInformation> mImageList;

    @JsonIgnore
    protected ArrayList<DetailInformation> mDetailList;

    public abstract List<E> getProductList();

    public List<ImageInformation> getImageList()
    {
        return mImageList;
    }

    public List<DetailInformation> getDetailList()
    {
        return mDetailList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(index);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address);
        dest.writeString(grade);
        dest.writeInt(ratingPersons);
        dest.writeInt(ratingValue);
        dest.writeInt(ratingShow == true ? 1 : 0);
        dest.writeString(category);
        dest.writeString(benefit);
        dest.writeStringList(benefitContents);
        dest.writeString(imgUrl);
        dest.writeInt(wishCount); // 위시리스트 카운트
        dest.writeInt(myWish == true ? 1 : 0);
        dest.writeTypedList(mDetailList);
        dest.writeTypedList(mImageList);
    }

    protected void readFromParcel(Parcel in)
    {
        index = in.readInt();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
        grade = in.readString();
        ratingPersons = in.readInt();
        ratingValue = in.readInt();
        ratingShow = in.readInt() == 1;
        category = in.readString();
        benefit = in.readString();
        benefitContents = in.createStringArrayList();
        imgUrl = in.readString();
        wishCount = in.readInt();
        myWish = in.readInt() == 1;
        mDetailList = in.createTypedArrayList(DetailInformation.CREATOR);
        mImageList = in.createTypedArrayList(ImageInformation.CREATOR);
    }
}
