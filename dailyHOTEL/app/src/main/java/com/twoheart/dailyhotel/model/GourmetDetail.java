package com.twoheart.dailyhotel.model;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class GourmetDetail extends PlaceDetail
{
    public Gourmet.Grade grade;
    public String category;
    private ArrayList<Pictogram> mPictogramList;

    protected ArrayList<TicketInformation> mTicketInformationList;

    public GourmetDetail(int index, int entryPosition, String isShowOriginalPrice, int listCount)
    {
        this.index = index;
        this.entryPosition = entryPosition;
        this.isShowOriginalPrice = isShowOriginalPrice;
        this.listCount = listCount;
    }

    @Override
    public void setData(JSONObject jsonObject) throws Exception
    {
        grade = Gourmet.Grade.gourmet;

        if (Util.isTextEmpty(category) == true)
        {
            category = jsonObject.getString("category");
        }

        name = jsonObject.getString("restaurant_name");
        address = jsonObject.getString("address");

        longitude = jsonObject.getDouble("longitude");
        latitude = jsonObject.getDouble("latitude");

        boolean ratingShow = jsonObject.getBoolean("ratingShow");

        if(ratingShow == true)
        {
            ratingValue = jsonObject.getInt("ratingValue");
            rate = jsonObject.getInt("rate");
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

        // TODO : pictogram
        mPictogramList = new ArrayList<>();
        mPictogramList.add(Pictogram.parking);

    }

    public ArrayList<TicketInformation> getTicketInformation()
    {
        return mTicketInformationList;
    }

    public ArrayList<Pictogram> getPictogramList()
    {
        return mPictogramList;
    }

    public enum Pictogram
    {
        parking(R.string.label_parking, R.string.code_parking, R.drawable.ic_detail_facilities_01_parking, GourmetFilter.Amenities.FLAG_PARKING),
        valetAvailable(R.string.label_valet_available, R.string.code_valet_available, R.drawable.ic_detail_facilities_06_valet, GourmetFilter.Amenities.FLAG_PARKING),
        privateRoom(R.string.label_private_room, R.string.code_private_room, R.drawable.ic_detail_facilities_07_private, GourmetFilter.Amenities.FLAG_PARKING),
        groupBooking(R.string.label_group_booking, R.string.code_group_booking, R.drawable.ic_detail_facilities_08_group, GourmetFilter.Amenities.FLAG_PARKING),
        babySeat(R.string.label_baby_seat, R.string.code_baby_seat, R.drawable.ic_detail_facilities_09_babyseat, GourmetFilter.Amenities.FLAG_PARKING),
        corkage(R.string.label_corkage, R.string.code_corkage, R.drawable.ic_detail_facilities_10_corkage, GourmetFilter.Amenities.FLAG_PARKING),
        none(0, 0, 0, 0);

        private int nameResId;
        private int codeResId;
        private int imageResId;
        private int flag;

        Pictogram(int nameResId, int codeResId, int imageResId, int flag)
        {
            this.nameResId = nameResId;
            this.codeResId = codeResId;
            this.imageResId = imageResId;
            this.flag = flag;
        }

        public String getName(Context context)
        {
            if (nameResId <= 0)
            {
                return "";
            }
            return context.getString(nameResId);
        }

        public String getCode(Context context)
        {
            if (codeResId <= 0)
            {
                return "";
            }
            return context.getString(codeResId);
        }

        public int getImageResId()
        {
            return imageResId;
        }

        public int getFlag()
        {
            return flag;
        }

    }
}
