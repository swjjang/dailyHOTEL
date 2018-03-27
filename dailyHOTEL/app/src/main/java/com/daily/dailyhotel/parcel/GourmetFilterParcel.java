package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.GourmetFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GourmetFilterParcel implements Parcelable
{
    private GourmetFilter mFilter;

    public GourmetFilterParcel(@NonNull GourmetFilter filter)
    {
        if (filter == null)
        {
            throw new NullPointerException("stayFilter == null");
        }

        mFilter = filter;
    }

    public GourmetFilterParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public GourmetFilter getFilter()
    {
        return mFilter;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeSerializable(mFilter.getCategoryFilterMap());

        LinkedHashMap<String, GourmetFilter.Category> categoryMap = mFilter.getCategoryMap();
        List<GourmetFilter.Category> categoryList = new ArrayList<>(categoryMap.values());
        List<CategoryParcel> categoryParcelList = new ArrayList<>();

        for (GourmetFilter.Category category : categoryList)
        {
            categoryParcelList.add(new CategoryParcel(category));
        }

        dest.writeTypedList(categoryParcelList);
        dest.writeInt(mFilter.flagTimeFilter);
        dest.writeInt(mFilter.flagAmenitiesFilters);
        dest.writeString(mFilter.sortType == null ? null : mFilter.sortType.name());
        dest.writeString(mFilter.defaultSortType == null ? null : mFilter.defaultSortType.name());
    }

    private void readFromParcel(Parcel in)
    {
        mFilter = new GourmetFilter().reset();

        mFilter.getCategoryFilterMap().putAll((HashMap) in.readSerializable());

        List<CategoryParcel> categoryParcelList = in.createTypedArrayList(CategoryParcel.CREATOR);
        LinkedHashMap<String, GourmetFilter.Category> categoryMap = new LinkedHashMap<>();

        for (CategoryParcel categoryParcel : categoryParcelList)
        {
            GourmetFilter.Category category = categoryParcel.getCategory();
            categoryMap.put(category.name, category);
        }

        mFilter.setCategoryMap(categoryMap);

        mFilter.flagTimeFilter = in.readInt();
        mFilter.flagAmenitiesFilters = in.readInt();

        String sortType = in.readString();

        if (DailyTextUtils.isTextEmpty(sortType) == false)
        {
            mFilter.sortType = GourmetFilter.SortType.valueOf(sortType);
        }

        String defaultSortType = in.readString();

        if (DailyTextUtils.isTextEmpty(defaultSortType) == false)
        {
            mFilter.defaultSortType = GourmetFilter.SortType.valueOf(defaultSortType);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public GourmetFilterParcel createFromParcel(Parcel in)
        {
            return new GourmetFilterParcel(in);
        }

        @Override
        public GourmetFilterParcel[] newArray(int size)
        {
            return new GourmetFilterParcel[size];
        }

    };

    private static class CategoryParcel implements Parcelable
    {
        private GourmetFilter.Category mCategory;

        public CategoryParcel(@NonNull GourmetFilter.Category category)
        {
            if (category == null)
            {
                throw new NullPointerException("stayFilter == null");
            }

            mCategory = category;
        }

        public CategoryParcel(Parcel in)
        {
            readFromParcel(in);
        }

        public GourmetFilter.Category getCategory()
        {
            return mCategory;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        private void readFromParcel(Parcel in)
        {
            mCategory = new GourmetFilter.Category();

            mCategory.name = in.readString();
            mCategory.code = in.readInt();
            mCategory.sequence = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(mCategory.name);
            dest.writeInt(mCategory.code);
            dest.writeInt(mCategory.sequence);
        }

        public static final Creator CREATOR = new Creator()
        {
            public CategoryParcel createFromParcel(Parcel in)
            {
                return new CategoryParcel(in);
            }

            @Override
            public CategoryParcel[] newArray(int size)
            {
                return new CategoryParcel[size];
            }
        };
    }
}
