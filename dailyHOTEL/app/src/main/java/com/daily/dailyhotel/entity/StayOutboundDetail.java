package com.daily.dailyhotel.entity;

import android.content.Context;
import android.util.SparseArray;

import com.twoheart.dailyhotel.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StayOutboundDetail
{
    public int index;
    public String name;
    public String nameEng;
    public String address;
    public int grade;
    public String ratingValue;
    public double latitude;
    public double longitude;

    LinkedHashMap<String, List<String>> mInformationMap;
    List<StayOutboundDetailImage> mImageList;
    List<StayOutboundRoom> mRoomList;
    SparseArray<String> mAmenitySparseArray;

    public StayOutboundDetail()
    {

    }

    public void setInformationMap(LinkedHashMap<String, List<String>> informationMap)
    {
        mInformationMap = informationMap;
    }

    public LinkedHashMap<String, List<String>> getInformationMap()
    {
        return mInformationMap;
    }

    public void setImageList(List<StayOutboundDetailImage> imageList)
    {
        mImageList = imageList;
    }

    public List<StayOutboundDetailImage> getImageList()
    {
        return mImageList;
    }

    public void setRoomList(List<StayOutboundRoom> roomList)
    {
        mRoomList = roomList;
    }

    public List<StayOutboundRoom> getRoomList()
    {
        return mRoomList;
    }

    public void setAmenityList(SparseArray<String> amenitySparseArray)
    {
        mAmenitySparseArray = amenitySparseArray;
    }

    public SparseArray<String> getAmenityList()
    {
        return mAmenitySparseArray;
    }

    public enum Amenity
    {
        POOL(R.string.label_pool, R.drawable.f_ic_facilities_06, 24),
        FITNESS(R.string.label_fitness, R.drawable.f_ic_facilities_07, 2008),
        FRONT24(R.string.label_front24, R.drawable.f_ic_facilities_05, 2063),
        SAUNA(R.string.label_sauna, R.drawable.f_ic_facilities_16, 2135),
        KIDS_PLAY_ROOM(R.string.label_kids_play_room, R.drawable.f_ic_facilities_17, 2186),
        MORE(R.string.label_more, R.drawable.f_ic_facilities_15, -1),
        NONE(0, 0, 0);

        private int mNameResId;
        private int mImageResId;
        private int mIndex;

        Amenity(int nameResId, int imageResId, int index)
        {
            mNameResId = nameResId;
            mImageResId = imageResId;
        }

        public String getName(Context context)
        {
            if (mNameResId == 0)
            {
                return null;
            }

            return context.getString(mNameResId);
        }

        public int getImageResId()
        {
            return mImageResId;
        }

        public int getIndex()
        {
            return mIndex;
        }
    }
}
