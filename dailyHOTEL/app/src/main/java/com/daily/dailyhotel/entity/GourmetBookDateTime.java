package com.daily.dailyhotel.entity;

public class GourmetBookDateTime extends PlaceBookDateTime
{
    public GourmetBookDateTime()
    {
    }

    public GourmetBookDateTime(String visitDateTime) throws Exception
    {
        setVisitDateTime(visitDateTime);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setVisitDateTime(String dateTime) throws Exception
    {
        setTimeInString(dateTime);
    }

    /**
     * @param dateTime ISO-8601
     */
    public void setVisitDateTime(String dateTime, int afterDay) throws Exception
    {
        setTimeInString(dateTime, afterDay);
    }

    public String getVisitDateTime(String format)
    {
        return getString(format);
    }
}
