package com.twoheart.dailyhotel.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GourmetDetail extends PlaceDetail
{
    public GourmetDetail(int index)
    {
        super(index);
    }

    @Override
    public void setData(JSONObject jsonObject) throws Exception
    {
        grade = Place.Grade.valueOf(jsonObject.getString("grade"));
        name = jsonObject.getString("restaurant_name");
        address = jsonObject.getString("address");

        longitude = jsonObject.getDouble("longitude");
        latitude = jsonObject.getDouble("latitude");

        // Image Url
        JSONArray imageJsonArray = jsonObject.getJSONArray("img_url");
        int imageLength = imageJsonArray.length();

        mImageUrlList = new ArrayList<String>(imageLength);

        for (int i = 0; i < imageLength; i++)
        {
            mImageUrlList.add(imageJsonArray.getString(i));
        }

        // Detail
        JSONArray detailJSONArray = jsonObject.getJSONArray("detail");
        int detailLength = detailJSONArray.length();

        mInformationList = new ArrayList<DetailInformation>(detailLength);

        for (int i = 0; i < detailLength; i++)
        {
            mInformationList.add(new DetailInformation(detailJSONArray.getJSONObject(i)));
        }

        // Ticket Information
        JSONArray ticketInformationJSONArray = jsonObject.getJSONArray("ticket_info");
        int ticketInformationLength = ticketInformationJSONArray.length();

        mTicketInformationList = new ArrayList<TicketInformation>(ticketInformationLength);

        for (int i = 0; i < ticketInformationLength; i++)
        {
            mTicketInformationList.add(new TicketInformation(name, ticketInformationJSONArray.getJSONObject(i)));
        }
    }
}
