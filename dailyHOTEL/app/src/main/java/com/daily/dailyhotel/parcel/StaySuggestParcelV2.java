package com.daily.dailyhotel.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StaySuggestV2;

/**
 * Created by android_sam on 2018. 3. 12..
 */

public class StaySuggestParcelV2 implements Parcelable
{
    private StaySuggestV2 mStaySuggest;

    public StaySuggestParcelV2(@NonNull StaySuggestV2 staySuggest)
    {
        if (staySuggest == null)
        {
            throw new NullPointerException("staySuggest == null");
        }

        mStaySuggest = staySuggest;
    }

    public StaySuggestParcelV2(Parcel in)
    {
        readFromParcel(in);
    }

    public StaySuggestV2 getSuggest()
    {
        return mStaySuggest;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mStaySuggest.menuType);

        StaySuggestV2.SuggestItem item = mStaySuggest.suggestItem;
        if (item == null)
        {
            return;
        }

        if (item instanceof StaySuggestV2.Station)
        {
            StaySuggestV2.Station station = (StaySuggestV2.Station) item;
            dest.writeSerializable(station);
        } else if (item instanceof StaySuggestV2.Stay)
        {
            StaySuggestV2.Stay stay = (StaySuggestV2.Stay) item;
            dest.writeSerializable(stay);
        } else if (item instanceof StaySuggestV2.Province)
        {
            StaySuggestV2.Province province = (StaySuggestV2.Province) item;
            dest.writeSerializable(province);
        } else if (item instanceof StaySuggestV2.Direct)
        {
            StaySuggestV2.Direct direct = (StaySuggestV2.Direct) item;
            dest.writeSerializable(direct);
        } else if (item instanceof StaySuggestV2.Location)
        {
            StaySuggestV2.Location location = (StaySuggestV2.Location) item;
            dest.writeSerializable(location);
        } else if (item instanceof StaySuggestV2.CampaignTag)
        {
            StaySuggestV2.CampaignTag campaignTag = (StaySuggestV2.CampaignTag) item;
            dest.writeSerializable(campaignTag);
        } else
        {
            dest.writeSerializable(item);
            ExLog.e("sam : writeToParcel error suggestItem name : " + item.name);
        }
    }

    private void readFromParcel(Parcel in)
    {
        mStaySuggest = new StaySuggestV2();

        mStaySuggest.menuType = in.readInt();

        mStaySuggest.suggestItem = (StaySuggestV2.SuggestItem) in.readSerializable();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public StaySuggestParcelV2 createFromParcel(Parcel in)
        {
            return new StaySuggestParcelV2(in);
        }

        @Override
        public StaySuggestParcelV2[] newArray(int size)
        {
            return new StaySuggestParcelV2[size];
        }

    };
}
