package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

public class EventBanner
{
    public int index;
    public long checkInTime;
    public int nights;
    public boolean mIsHotel;
    public String imageUrl;
    public String webLink;

    public EventBanner()
    {

    }

    public EventBanner(JSONObject jsonObject, String url) throws Exception
    {
        if (jsonObject.isNull("linkUrl") == false)
        {
            // 웹링크인 경우
            webLink = jsonObject.getString("linkUrl");
        } else
        {
            // 딥링크인 경우
            if (jsonObject.isNull("hotelIdx") == false)
            {
                mIsHotel = true;
                index = jsonObject.getInt("hotelIdx");
            } else if (jsonObject.isNull("fnbRestaurantIdx") == false)
            {
                mIsHotel = false;
                index = jsonObject.getInt("fnbRestaurantIdx");
            } else
            {
                throw new NullPointerException();
            }

            checkInTime = jsonObject.getLong("dateCheckIn");
            nights = jsonObject.getInt("nights");
        }

        imageUrl = url + jsonObject.getString("imagePath");
    }

    public boolean isDeepLink()
    {
        return Util.isTextEmpty(webLink) == true;
    }

    public boolean isHotel()
    {
        return mIsHotel;
    }

    //    public String getLink()
    //    {
    //        if(isDeepLink() == true)
    //        {
    //            // dailyhotel://dailyhotel.co.kr?view=hotel&idx=131&date=20151109&nights=1
    //            StringBuilder targetLink = new StringBuilder();
    //            targetLink.append("dailyhotel://dailyhotel.co.kr?view=");
    //
    //            // view
    //            if(isHotel() == true)
    //            {
    //                targetLink.append("hotel");
    //            } else
    //            {
    //                targetLink.append("gourmet");
    //            }
    //
    //            // index
    //            targetLink.append("&idx=");
    //            targetLink.append(mIndex);
    //
    //            // date
    //            targetLink.append("&date=");
    //
    //            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    //            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+9"));
    //
    //            targetLink.append(simpleDateFormat.format(new Date(mCheckInTime)));
    //
    //            // nights
    //            targetLink.append("&nights=");
    //            targetLink.append(mNights);
    //
    //            // 딥링크인 경우
    //            return targetLink.toString();
    //        } else
    //        {
    //            // 웹링크인 경우
    //            return mLink;
    //        }
    //    }
}
