package com.twoheart.dailyhotel.model;

import android.content.Context;

import com.twoheart.dailyhotel.R;

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
    public boolean hasCoupon;

    public ArrayList<Pictogram> mPictogramList;

    public StayDetail(int hotelIndex, int nights, int entryIndex, String isShowOriginalPrice, int listCount)
    {
        this.index = hotelIndex;
        this.nights = nights;
        this.entryPosition = entryIndex;
        this.isShowOriginalPrice = isShowOriginalPrice;
        this.listCount = listCount;
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

        name = jsonObject.getString("name");
        address = jsonObject.getString("address");

        longitude = jsonObject.getDouble("longitude");
        latitude = jsonObject.getDouble("latitude");
        isOverseas = jsonObject.getBoolean("overseas");

        boolean ratingShow = jsonObject.getBoolean("ratingShow");

        if(ratingShow == true)
        {
            ratingValue = jsonObject.getInt("ratingValue");
            rate = jsonObject.getInt("rate");
        }

        // Pictrogram
        mPictogramList = new ArrayList<>();

        // 주차
        if(jsonObject.getBoolean("parking") == true)
        {
            mPictogramList.add(Pictogram.parking);
        }

        // 주차금지
        if(jsonObject.getBoolean("noParking") == true)
        {
            mPictogramList.add(Pictogram.noParking);
        }

        // 수영장
        if(jsonObject.getBoolean("pool") == true)
        {
            mPictogramList.add(Pictogram.pool);
        }

        // 피트니스
        if(jsonObject.getBoolean("fitness") == true)
        {
            mPictogramList.add(Pictogram.fitness);
        }

        // 애완동물
        if(jsonObject.getBoolean("pet") == true)
        {
            mPictogramList.add(Pictogram.pet);
        }

        // 바베큐
        if(jsonObject.getBoolean("sharedBbq") == true)
        {
            mPictogramList.add(Pictogram.sharedBbq);
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

        if (jsonObject.has("benefit") == true)
        {
            benefit = jsonObject.getString("benefit");
        }

        // Detail
        JSONArray detailJSONArray = jsonObject.getJSONArray("detail");
        int detailLength = detailJSONArray.length();

        mInformationList = new ArrayList<>(detailLength);

        for (int i = 0; i < detailLength; i++)
        {
            mInformationList.add(new DetailInformation(detailJSONArray.getJSONObject(i)));
        }

        // Room Sale Info
        JSONArray saleRoomJSONArray = jsonObject.getJSONArray("rooms");
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

    public enum Pictogram
    {
        parking(R.string.label_parking, R.drawable.selector_filter_amenities_parking_button),
        noParking(R.string.label_unabled_parking, R.drawable.selector_filter_amenities_parking_button),
        pool(R.string.label_pool, R.drawable.selector_filter_amenities_pool_button),
        fitness(R.string.label_fitness, R.drawable.selector_filter_amenities_fitness_button),
        pet(R.string.label_allowed_pet, R.drawable.selector_filter_amenities_fitness_button),
        sharedBbq(R.string.label_allowed_barbecue, R.drawable.selector_filter_amenities_fitness_button);

        private int mNameResId;
        private int mImageResId;

        Pictogram(int nameResId, int imageResId)
        {
            mNameResId = nameResId;
            mImageResId = imageResId;
        }

        public String getName(Context context)
        {
            return context.getString(mNameResId);
        }

        public int getImageResId()
        {
            return mImageResId;
        }
    }
}