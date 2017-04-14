package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.R;

/**
 * Created by android_sam on 2017. 4. 11..
 */

public enum DailyCategoryType implements Parcelable
{
    // 기존 버튼 용 카테고리
    STAY_ALL(R.string.label_home_category_stay_all, 0, 0),
    // 기존 버튼 용 카테고리
    GOURMET_ALL(0, 0, 0),
    // 신규 홈 카테고리 버튼 용 카테고리
    STAY_HOTEL(R.string.label_home_category_hotel, R.string.code_home_category_hotel, 0),
    // 신규 홈 카테고리 버튼 용 카테고리
    STAY_BOUTIQUE(R.string.label_home_category_boutique, R.string.code_home_category_boutique, 0),
    // 신규 홈 카테고리 버튼 용 카테고리
    STAY_PENSION(R.string.label_home_category_pension, R.string.code_home_category_pension, 0),
    // 신규 홈 카테고리 버튼 용 카테고리
    STAY_RESORT(R.string.label_home_category_resort, R.string.code_home_category_resort, 0),
    // 신규 홈 카테고리 버튼 용 카테고리 - 홈 화면 전용
    STAY_AROUND_SEARCH(R.string.label_home_category_around_search, 0, 0),
    // 신규 홈 카테고리 빈 뷰 생성용 - 홈화면 전용
    NONE(0, 0, 0);

    private int mNameResId;
    private int mCodeResId;
    private int mImageResId;

    public int getNameResId()
    {
        return mNameResId;
    }

    public int getCodeResId()
    {
        return mCodeResId;
    }

    public int getImageResId()
    {
        return mImageResId;
    }

    DailyCategoryType(int nameResId, int codeResId, int imageResId)
    {
        mNameResId = nameResId;
        mCodeResId = codeResId;
        mImageResId = imageResId;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name());
    }

    public static final Parcelable.Creator<DailyCategoryType> CREATOR = new Creator<DailyCategoryType>()
    {
        @Override
        public DailyCategoryType createFromParcel(Parcel source)
        {
            return DailyCategoryType.valueOf(source.readString());
        }

        @Override
        public DailyCategoryType[] newArray(int size)
        {
            return new DailyCategoryType[size];
        }
    };
}
