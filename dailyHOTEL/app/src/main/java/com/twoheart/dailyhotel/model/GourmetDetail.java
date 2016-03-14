package com.twoheart.dailyhotel.model;

import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class GourmetDetail extends PlaceDetail
{
    public String category;

    public GourmetDetail(int index)
    {
        super(index);
    }

    @Override
    public void setData(JSONObject jsonObject) throws Exception
    {
        grade = Place.Grade.gourmet;

        if (Util.isTextEmpty(category) == true)
        {
            category = jsonObject.getString("category");
        }

        name = jsonObject.getString("restaurant_name");
        address = jsonObject.getString("address");

        longitude = jsonObject.getDouble("longitude");
        latitude = jsonObject.getDouble("latitude");

        if (jsonObject.has("rating") == true)
        {
            satisfaction = jsonObject.getString("rating");
        }

        // Image Url
        String imageUrl = jsonObject.getString("img_url");
        JSONObject pahtUrlJSONObject = jsonObject.getJSONObject("img_path");

        Iterator<String> iterator = pahtUrlJSONObject.keys();
        while (iterator.hasNext())
        {
            String key = iterator.next();

            try
            {
                JSONArray pathJSONArray = pahtUrlJSONObject.getJSONArray(key);

                int length = pathJSONArray.length();
                mImageInformationList = new ArrayList<ImageInformation>(pathJSONArray.length());

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

        // Detail
        JSONArray detailJSONArray = jsonObject.getJSONArray("detail");
        int detailLength = detailJSONArray.length();

        mInformationList = new ArrayList<>(detailLength);

        for (int i = 0; i < detailLength; i++)
        {
            mInformationList.add(new DetailInformation(detailJSONArray.getJSONObject(i)));
        }

        // Ticket Information
        JSONArray ticketInformationJSONArray = jsonObject.getJSONArray("ticket_info");
        int ticketInformationLength = ticketInformationJSONArray.length();

        mTicketInformationList = new ArrayList<>(ticketInformationLength);

        for (int i = 0; i < ticketInformationLength; i++)
        {
            mTicketInformationList.add(new TicketInformation(name, ticketInformationJSONArray.getJSONObject(i)));
        }
    }
}
