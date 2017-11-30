package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.StayArea;
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
        Area areaGroup = mRegion.getAreaGroup();
        StayArea area = mRegion.getArea();

        if (areaGroup == null)
        {
            dest.writeInt(0);
            dest.writeString(null);
        } else
        {
            dest.writeInt(areaGroup.index);
            dest.writeString(areaGroup.name);
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

            List<Category> categoryList = area.getCategoryList();

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
    }

    private void readFromParcel(Parcel in)
    {
        Area areaGroup;

        int areaGroupIndex = in.readInt();
        String areaGroupName = in.readString();

        if (areaGroupIndex == 0 && areaGroupName == null)
        {
            areaGroup = null;
        } else
        {
            areaGroup = new Area();

            areaGroup.index = areaGroupIndex;
            areaGroup.name = areaGroupName;
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

            int categorySize = in.readInt();

            if (categorySize > 0)
            {
                List<Category> categoryList = new ArrayList<>();

                for (int i = 0; i < categorySize; i++)
                {
                    categoryList.add(new Category(in.readString(), in.readString()));
                }

                area.setCategoryList(categoryList);
            }
        }

        mRegion = new StayRegion(areaGroup, area);
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
