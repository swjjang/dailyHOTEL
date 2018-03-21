package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayCategory;
import com.daily.dailyhotel.entity.Stays;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 6. 15..
 */
@JsonObject
public class StaysData
{
    @JsonField(name = "categories")
    public List<StayCategoryData> stayCategoryDataList;

    @JsonField(name = "hotelSales")
    public List<StayData> stayDataList;

    @JsonField(name = "hotelSalesCount")
    public int hotelSalesCount;

    @JsonField(name = "imgUrl")
    public String imageUrl;

    @JsonField(name = "searchMaxCount")
    public int searchMaxCount;

    @JsonField(name = "stays")
    public int stays;

    @JsonField(name = "configurations")
    public ConfigurationsData configurations;

    public Stays getStays()
    {
        Stays stays = new Stays();

        stays.totalCount = hotelSalesCount;
        stays.searchMaxCount = searchMaxCount;

        if (stayCategoryDataList != null || stayCategoryDataList.size() > 0)
        {
            List<StayCategory> stayCategoryList = new ArrayList<>();

            for (StayCategoryData stayCategoryData : stayCategoryDataList)
            {
                if (stayCategoryData.count > 0)
                {
                    stayCategoryList.add(stayCategoryData.getStayCategory());
                }
            }

            stays.setStayCategoryList(stayCategoryList);
        }

        if (stayDataList != null || stayDataList.size() > 0)
        {
            List<Stay> stayList = new ArrayList<>();

            for (StayData stayData : stayDataList)
            {
                stayList.add(stayData.getStay(imageUrl));
            }

            stays.setStayList(stayList);
        }

        if (configurations != null)
        {
            stays.activeReward = configurations.activeReward;
        }

        return stays;
    }

    @JsonObject
    static class StayCategoryData
    {
        @JsonField(name = "alias")
        public String alias;

        @JsonField(name = "count")
        public int count;

        @JsonField(name = "name")
        public String name;

        public StayCategory getStayCategory()
        {
            StayCategory stayCategory = new StayCategory();

            stayCategory.code = alias;
            stayCategory.name = name;
            stayCategory.count = count;

            return stayCategory;
        }
    }
}
