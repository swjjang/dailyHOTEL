package com.daily.dailyhotel.entity;

import android.util.SparseArray;

import java.util.List;
import java.util.Map;

public class StayOutboundDetail
{
    public int index;
    public String name;
    public String nameEng;
    public String address;
    public int grade;
    public String satisfaction;
    public double latitude;
    public double longitude;
    public Map<String, List<String>> details;

    List<StayOutboundDetailImage> mImageList;
    List<StayOutboundRoom> mRoomInformationList;
    SparseArray<String> mAmenitySparseArray;

    public StayOutboundDetail()
    {

    }
}
