package com.daily.dailyhotel.entity;

import android.util.SparseArray;

import com.twoheart.dailyhotel.R;

import java.util.LinkedHashMap;
import java.util.List;

public class StayOutboundDetail
{
    public int index;
    public String name;
    public String nameEng;
    public String address;
    public float rating;
    public float tripAdvisorRating;
    public int tripAdvisorReviewCount;
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
        SAUNA(R.drawable.f_ic_facilities_16, 2135),
        POOL(R.drawable.f_ic_facilities_06, 24),
        KIDS_PLAY_ROOM(R.drawable.f_ic_facilities_17, 2186),
        FITNESS(R.drawable.f_ic_facilities_07, 2008),
        FRONT24(R.drawable.vector_f_ic_facilities_23, 2063),
        MORE(0, -1),
        NONE(0, 0);

        private int mImageResId;
        private int mIndex;

        Amenity(int imageResId, int index)
        {
            mImageResId = imageResId;
            mIndex = index;
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
