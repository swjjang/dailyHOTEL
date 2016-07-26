package com.twoheart.dailyhotel.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class StayDetail extends PlaceDetail
{
    public int nights;
    public Stay.Grade grade;
    private ArrayList<RoomInformation> mSaleRoomList;
    //
    public String categoryCode;

    public StayDetail(int hotelIndex, int nights, int entryIndex, String isShowOriginalPrice)
    {
        this.index = hotelIndex;
        this.nights = nights;
        this.entryPosition = entryIndex;
        this.isShowOriginalPrice = isShowOriginalPrice;
    }

    public void setData(JSONObject jsonObject) throws Exception
    {
        try
        {
            grade = Stay.Grade.valueOf(jsonObject.getString("grade"));
        } catch (Exception e)
        {
            grade = Stay.Grade.etc;
        }

        name = jsonObject.getString("hotelName");
        address = jsonObject.getString("address");

        longitude = jsonObject.getDouble("longitude");
        latitude = jsonObject.getDouble("latitude");

        isOverseas = jsonObject.getBoolean("isOverseas");

        if (jsonObject.has("rating") == true)
        {
            satisfaction = jsonObject.getString("rating");
        }

        // Image Url
        String imageUrl = jsonObject.getString("imgUrl");
        JSONObject pahtUrlJSONObject = jsonObject.getJSONObject("imgPath");

        Iterator<String> iterator = pahtUrlJSONObject.keys();
        while (iterator.hasNext())
        {
            String key = iterator.next();

            try
            {
                JSONArray pathJSONArray = pahtUrlJSONObject.getJSONArray(key);

                int length = pathJSONArray.length();
                mImageInformationList = new ArrayList<>(pathJSONArray.length());

                for (int i = 0; i < length; i++)
                {
                    JSONObject imageInformationJSONObject = pathJSONArray.getJSONObject(i);

                    String description = imageInformationJSONObject.getString("description");
                    String imageFullUrl = imageUrl + key + imageInformationJSONObject.getString("name");
                    mImageInformationList.add(new ImageInformation(imageFullUrl, description));
                }
                break;
            } catch (JSONException e)
            {
            }
        }

        if (jsonObject.has("hotelBenefit") == true)
        {
            benefit = jsonObject.getString("hotelBenefit");
        }

        // Detail
        JSONArray detailJSONArray = jsonObject.getJSONArray("detail");
        int detailLength = detailJSONArray.length();

        mInformationList = new ArrayList<>(detailLength);

        for (int i = 0; i < detailLength; i++)
        {
            mInformationList.add(new DetailInformation(detailJSONArray.getJSONObject(i)));
        }

        // Detail Info
        JSONArray detailMoreJSONArray = jsonObject.getJSONArray("detailMore");
        int detailMoreLength = detailMoreJSONArray.length();

        if (detailMoreLength != 0)
        {
            for (int i = 0; i < detailMoreLength; i++)
            {
                mInformationList.add(new DetailInformation(detailMoreJSONArray.getJSONObject(i)));
            }
        }

        // Room Sale Info
        JSONArray saleRoomJSONArray = jsonObject.getJSONArray("hotelRoomDetail");
        int saleRoomLength = saleRoomJSONArray.length();

        mSaleRoomList = new ArrayList<>(saleRoomLength);

        for (int i = 0; i < saleRoomLength; i++)
        {
            RoomInformation roomInformation = new RoomInformation(name, saleRoomJSONArray.getJSONObject(i), isOverseas, nights);
            roomInformation.grade = grade;
            roomInformation.address = address;
            mSaleRoomList.add(roomInformation);
        }
    }

    public ArrayList<RoomInformation> getSaleRoomList()
    {
        return mSaleRoomList;
    }
}
