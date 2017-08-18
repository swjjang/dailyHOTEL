package com.daily.dailyhotel.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by iseung-won on 2017. 8. 18..
 */

public class SearchCalendarReturnData implements Parcelable
{
    public Constants.SearchType searchType;
    public String inputText;
    public Keyword keyword;
    public Place place;
    public CampaignTag campaignTag;

    public SearchCalendarReturnData()
    {
    }

    public SearchCalendarReturnData(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(searchType.name());
        dest.writeString(inputText);
        dest.writeParcelable(keyword, flags);
        dest.writeParcelable(place, flags);
        dest.writeParcelable(campaignTag, flags);
    }

    protected void readFromParcel(Parcel in)
    {
        String type = in.readString();
        try
        {
            searchType = Constants.SearchType.valueOf(type);
        } catch (Exception e)
        {
            searchType = Constants.SearchType.SEARCHES;
        }

        inputText = in.readString();
        keyword = in.readParcelable(Keyword.class.getClassLoader());
        place = in.readParcelable(Place.class.getClassLoader());
        campaignTag = in.readParcelable(CampaignTag.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        @Override
        public SearchCalendarReturnData createFromParcel(Parcel source)
        {
            return new SearchCalendarReturnData(source);
        }

        @Override
        public SearchCalendarReturnData[] newArray(int size)
        {
            return new SearchCalendarReturnData[0];
        }
    };
}
