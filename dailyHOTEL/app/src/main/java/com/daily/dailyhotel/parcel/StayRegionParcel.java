package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayRegion;

import java.util.ArrayList;
import java.util.List;

public class StayRegionParcel implements Parcelable
{
    private StayRegion mRegion;

    public StayRegionParcel(@NonNull StayRegion region)
    {
        if (region == null)
        {
            throw new NullPointerException("region == null");
        }

        mRegion = region;
    }

    public StayRegionParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StayRegion getRegion()
    {
        return mRegion;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        StayAreaGroup areaGroup = mRegion.getAreaGroup();
        StayArea area = mRegion.getArea();

        if (areaGroup == null)
        {
            dest.writeInt(0);
            dest.writeString(null);
            dest.writeInt(0);
        } else
        {
            dest.writeInt(areaGroup.index);
            dest.writeString(areaGroup.name);

            writeCategoryToParcel(dest, areaGroup.getCategoryList());
        }

        if (area == null)
        {
            dest.writeInt(0);
            dest.writeString(null);
            dest.writeInt(0);
        } else
        {
            dest.writeInt(area.index);
            dest.writeString(area.name);

            writeCategoryToParcel(dest, area.getCategoryList());
        }
    }

    private void writeCategoryToParcel(Parcel dest, List<Category> categoryList)
    {
        if (categoryList != null && categoryList.size() > 0)
        {
            dest.writeInt(categoryList.size());

            for (Category category : categoryList)
            {
                dest.writeString(category.code);
                dest.writeString(category.name);
            }
        } else
        {
            dest.writeInt(0);
        }
    }

    private void readFromParcel(Parcel in)
    {
        StayAreaGroup areaGroup;

        int areaGroupIndex = in.readInt();
        String areaGroupName = in.readString();

        if (areaGroupIndex == 0 && areaGroupName == null)
        {
            areaGroup = null;
        } else
        {
            areaGroup = new StayAreaGroup();
            areaGroup.index = areaGroupIndex;
            areaGroup.name = areaGroupName;
            areaGroup.setCategoryList(readCategoryFromParcel(in));
        }

        StayArea area;

        int areaIndex = in.readInt();
        String areaName = in.readString();

        if (areaIndex == 0 && areaName == null)
        {
            area = null;
        } else
        {
            area = new StayArea();
            area.index = areaIndex;
            area.name = areaName;
            area.setCategoryList(readCategoryFromParcel(in));
        }

        mRegion = new StayRegion(areaGroup, area);
    }

    private List<Category> readCategoryFromParcel(Parcel in)
    {
        int categorySize = in.readInt();

        if (categorySize > 0)
        {
            List<Category> categoryList = new ArrayList<>();

            for (int i = 0; i < categorySize; i++)
            {
                categoryList.add(new Category(in.readString(), in.readString()));
            }

            return categoryList;
        }

        return null;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayRegionParcel createFromParcel(Parcel in)
        {
            return new StayRegionParcel(in);
        }

        @Override
        public StayRegionParcel[] newArray(int size)
        {
            return new StayRegionParcel[size];
        }

    };
}
