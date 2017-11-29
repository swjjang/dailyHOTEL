package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.daily.dailyhotel.entity.StayTown;
import com.daily.dailyhotel.parcel.StayTownParcel;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.model.time.StayBookingDay;

public class StayCuration extends PlaceCuration
{
    protected StayBookingDay mStayBookingDay;
    protected Category mCategory;
    protected StayCurationOption mStayCurationOption;

    protected StayTown mTown;

    public StayCuration()
    {
        mStayCurationOption = new StayCurationOption();

        clear();
    }

    public StayCuration(Parcel in)
    {
        readFromParcel(in);
    }

    public StayBookingDay getStayBookingDay()
    {
        return mStayBookingDay;
    }

    public void setStayBookingDay(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
            return;
        }

        mStayBookingDay = stayBookingDay;
    }

    @Override
    public PlaceCurationOption getCurationOption()
    {
        return mStayCurationOption;
    }

    @Override
    public void setCurationOption(PlaceCurationOption placeCurationOption)
    {
        if (mStayCurationOption == null)
        {
            mStayCurationOption = new StayCurationOption();
        }

        mStayCurationOption.setCurationOption((StayCurationOption) placeCurationOption);
    }

    @Override
    public PlaceParams toPlaceParams(int page, int limit, boolean isDetails)
    {
        StayParams stayParams = new StayParams(this);
        stayParams.setPageInformation(page, limit, isDetails);

        return stayParams;
    }

    public Category getCategory()
    {
        if (mCategory == null)
        {
            mCategory = Category.ALL;
        }

        return mCategory;
    }

    public void setCategory(Category category)
    {
        setCategory(null, category);
    }

    public void setCategory(Context context, Category category)
    {
        if (category == null)
        {
            category = Category.ALL;
        }

        mCategory = category;

        if (context != null)
        {
            DailyPreference.getInstance(context).setStayCategory(category.name, category.code);
        }
    }

    public void setTown(StayTown stayTown)
    {
        mTown = stayTown;
    }

    public StayTown getTown()
    {
        return mTown;
    }

    @Override
    public void clear()
    {
        super.clear();

        mCategory = Category.ALL;

        mStayCurationOption.clear();

        mStayBookingDay = null;
        mTown = null;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mLocation, flags);

        dest.writeParcelable(mStayBookingDay, flags);
        dest.writeParcelable(mCategory, flags);
        dest.writeParcelable(mStayCurationOption, flags);

        if (mTown == null)
        {
            dest.writeParcelable(null, flags);
        } else
        {
            dest.writeParcelable(new StayTownParcel(mTown), flags);
        }
    }

    protected void readFromParcel(Parcel in)
    {
        mLocation = in.readParcelable(Location.class.getClassLoader());

        mStayBookingDay = in.readParcelable(StayBookingDay.class.getClassLoader());
        mCategory = in.readParcelable(Category.class.getClassLoader());
        mStayCurationOption = in.readParcelable(StayCurationOption.class.getClassLoader());

        StayTownParcel stayTownParcel = in.readParcelable(StayTownParcel.class.getClassLoader());

        if (stayTownParcel != null)
        {
            mTown = stayTownParcel.getStayTown();
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StayCuration createFromParcel(Parcel in)
        {
            return new StayCuration(in);
        }

        @Override
        public StayCuration[] newArray(int size)
        {
            return new StayCuration[size];
        }
    };
}
