package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.District;
import com.daily.dailyhotel.entity.StayTown;

import java.util.ArrayList;
import java.util.List;

public class StayTownParcel implements Parcelable
{
    private StayTown mStayTown;

    public StayTownParcel(@NonNull StayTown stayTown)
    {
        if (stayTown == null)
        {
            throw new NullPointerException("stayTown == null");
        }

        mStayTown = stayTown;
    }

    public StayTownParcel(Parcel in)
    {
        readFromParcel(in);
    }

    public StayTown getStayTown()
    {
        return mStayTown;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mStayTown.index);
        dest.writeString(mStayTown.name);

        List<Category> categoryList = mStayTown.getCategoryList();

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

        District district = mStayTown.getDistrict();

        if (district != null)
        {
            dest.writeInt(district.index);
            dest.writeString(district.name);
        } else
        {
            dest.writeInt(0);
            dest.writeString(null);
        }
    }

    private void readFromParcel(Parcel in)
    {
        mStayTown = new StayTown();

        mStayTown.index = in.readInt();
        mStayTown.name = in.readString();

        int categorySize = in.readInt();

        if (categorySize > 0)
        {
            List<Category> categoryList = new ArrayList<>();

            for (int i = 0; i < categorySize; i++)
            {
                categoryList.add(new Category(in.readString(), in.readString()));
            }
        }

        District district = new District();
        district.index = in.readInt();
        district.name = in.readString();

        mStayTown.setDistrict(district);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public StayTownParcel createFromParcel(Parcel in)
        {
            return new StayTownParcel(in);
        }

        @Override
        public StayTownParcel[] newArray(int size)
        {
            return new StayTownParcel[size];
        }

    };
}
