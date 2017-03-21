package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.DailyPreference;

public class StayCuration extends PlaceCuration
{
    protected StayBookingDay mStayBookingTime;
    protected Category mCategory;
    protected StayCurationOption mStayCurationOption;

    public StayCuration()
    {
        mStayCurationOption = new StayCurationOption();
        mStayBookingTime = new StayBookingDay();

        clear();
    }

    public StayCuration(Parcel in)
    {
        readFromParcel(in);
    }

    public void setCheckInDay(long millis)
    {
        if (mStayBookingTime == null)
        {
            mStayBookingTime = new StayBookingDay();
        }

        mStayBookingTime.setCheckInTime(millis);
    }

    public void setCheckInDay(String dateString)
    {
        if (mStayBookingTime == null)
        {
            mStayBookingTime = new StayBookingDay();
        }

        mStayBookingTime.setCheckInTime(dateString);
    }

    public String getCheckInDay(String format)
    {
        if (mStayBookingTime == null)
        {
            return null;
        }

        return mStayBookingTime.getCheckInTime(format);
    }

    public void setCheckOutDay(String dateString) throws Exception
    {
        if (mStayBookingTime == null)
        {
            mStayBookingTime = new StayBookingDay();
        }

        mStayBookingTime.setCheckOutTime(dateString);
    }

    public void setCheckOutDay(String dateString, int afterDay) throws Exception
    {
        if (mStayBookingTime == null)
        {
            mStayBookingTime = new StayBookingDay();
        }

        mStayBookingTime.setCheckOutTime(dateString, afterDay);
    }

    public String getCheckOutDay(String format)
    {
        if (mStayBookingTime == null)
        {
            return null;
        }

        return mStayBookingTime.getCheckOutTime(format);
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

    @Override
    public void clear()
    {
        mCategory = Category.ALL;

        mStayCurationOption.clear();

        mStayBookingTime = null;
        mProvince = null;
        mLocation = null;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(mProvince, flags);
        dest.writeParcelable(mLocation, flags);
        dest.writeParcelable(mStayBookingTime, flags);
        dest.writeParcelable(mCategory, flags);
        dest.writeParcelable(mStayCurationOption, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        mProvince = in.readParcelable(Province.class.getClassLoader());
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mStayBookingTime = in.readParcelable(StayBookingDay.class.getClassLoader());
        mCategory = in.readParcelable(Category.class.getClassLoader());
        mStayCurationOption = in.readParcelable(StayCurationOption.class.getClassLoader());
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
