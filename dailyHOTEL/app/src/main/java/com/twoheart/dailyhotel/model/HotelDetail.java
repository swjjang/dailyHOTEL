package com.twoheart.dailyhotel.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class HotelDetail
{
    public int hotelIndex;
    public int nights;
    public Stay.Grade grade;
    public String hotelName;
    public String address;
    public double latitude;
    public double longitude;
    public boolean isOverseas; // 0 : 국내 , 1 : 해외
    public String hotelBenefit;
    public String satisfaction;
    private ArrayList<ImageInformation> mImageInformationList;
    private ArrayList<DetailInformation> mInformationList;
    private ArrayList<DetailInformation> mMoreInformationList;
    private ArrayList<SaleRoomInformation> mSaleRoomList;
    //
    public String categoryCode;

    public HotelDetail(int hotelIndex, int nights)
    {
        this.hotelIndex = hotelIndex;
        this.nights = nights;
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

        hotelName = jsonObject.getString("hotelName");
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
            hotelBenefit = jsonObject.getString("hotelBenefit");
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

        if (detailMoreLength == 0)
        {
            mMoreInformationList = null;
        } else
        {
            mMoreInformationList = new ArrayList<>(detailMoreLength);

            for (int i = 0; i < detailMoreLength; i++)
            {
                mMoreInformationList.add(new DetailInformation(detailMoreJSONArray.getJSONObject(i)));
            }
        }

        // Room Sale Info
        JSONArray saleRoomJSONArray = jsonObject.getJSONArray("hotelRoomDetail");
        int saleRoomLength = saleRoomJSONArray.length();

        mSaleRoomList = new ArrayList<>(saleRoomLength);

        for (int i = 0; i < saleRoomLength; i++)
        {
            SaleRoomInformation saleRoomInformation = new SaleRoomInformation(hotelName, saleRoomJSONArray.getJSONObject(i), isOverseas, nights);
            saleRoomInformation.grade = grade;
            saleRoomInformation.address = address;
            mSaleRoomList.add(saleRoomInformation);
        }
    }

    public ArrayList<ImageInformation> getImageInformationList()
    {
        return mImageInformationList;
    }

    public ArrayList<SaleRoomInformation> getSaleRoomList()
    {
        return mSaleRoomList;
    }

    public ArrayList<DetailInformation> getInformation()
    {
        return mInformationList;
    }

    public ArrayList<DetailInformation> getMoreInformation()
    {
        return mMoreInformationList;
    }
}
