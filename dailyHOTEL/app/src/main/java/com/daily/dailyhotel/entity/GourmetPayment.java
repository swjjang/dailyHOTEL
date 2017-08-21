package com.daily.dailyhotel.entity;

import java.util.List;

public class GourmetPayment
{
    public boolean soldOut;
    public String visitDate;
    public int totalPrice; // 결재할 총금액
    public String businessName;
    public int minMenuCount;
    public int maxMenuCount;
    private List<String> mVisitDateTimeList;

    public GourmetPayment()
    {

    }

    public void setVisitTimeList(List<String> dateTimeList)
    {
        mVisitDateTimeList = dateTimeList;
    }

    public List<String> getVisitDateTimeList()
    {
        return mVisitDateTimeList;
    }

    public int getVisitTimeListSize()
    {
        if (mVisitDateTimeList == null)
        {
            return 0;
        }

        return mVisitDateTimeList.size();
    }

    public String getVisitDateTime(int index)
    {
        if (mVisitDateTimeList == null || mVisitDateTimeList.size() <= index)
        {
            return null;
        }

        return mVisitDateTimeList.get(index);
    }
}