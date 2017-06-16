package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.twoheart.dailyhotel.model.Stay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 6. 15..
 */
@JsonObject
public class StayListData
{
    @JsonField(name = "categories")
    public List<CategoryData> categoryDataList;

    @JsonField(name = "hotelSales")
    public List<StaySalesData> staySalesDataList;

    @JsonField(name = "hotelSalesCount")
    public int hotelSalesCount;

    @JsonField(name = "imgUrl")
    public String imageUrl;

    @JsonField(name = "searchMaxCount")
    public int searchMaxCount;

    @JsonField(name = "stays")
    public int stays;

    public List<Stay> getStayList()
    {
        ArrayList<Stay> stayList = new ArrayList<>();

        for (StaySalesData staySalesData : staySalesDataList)
        {
            Stay stay = staySalesData.getStay();

            if (stay == null)
            {
                continue;
            }

            stay.imageUrl = imageUrl + stay.imageUrl;

            stayList.add(stay);
        }

        return stayList;
    }
}
