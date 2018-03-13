package com.daily.dailyhotel.repository.remote.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2018. 3. 2..
 */
@JsonObject
public class StaySuggestsData
{
    @JsonField(name = "station")
    public List<StationData> stationList;

    @JsonField(name = "hotel")
    public List<StayData> stayList;

    @JsonField(name = "region")
    public List<ProvinceData> areaGroupList;

    @JsonObject
    static class StationData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "region")
        public String region;

        @JsonField(name = "line")
        public String line;

        @JsonField(name = "name")
        public String name;

        public StaySuggestV2.Station getStation()
        {
            StaySuggestV2.Station station = new StaySuggestV2.Station();
            station.index = index;
            station.region = region;
            station.line = line;
            station.name = name;

            return station;
        }
    }

    @JsonObject
    static class StayData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        @JsonField(name = "discountAvg")
        public int discountAvg;

        @JsonField(name = "available")
        public boolean available;

        @JsonField(name = "province")
        public ProvinceData province;

        public StaySuggestV2.Stay getStay()
        {
            StaySuggestV2.Stay stay = new StaySuggestV2.Stay();
            stay.index = index;
            stay.name = name;
            stay.discountAvg = discountAvg;
            stay.available = available;
            stay.province = province == null ? null : province.getProvince();

            return stay;
        }
    }

    @JsonObject
    static class ProvinceData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        @JsonField(name = "area")
        public AreaData area;

        public StaySuggestV2.Province getProvince()
        {
            StaySuggestV2.Province province = new StaySuggestV2.Province();
            province.index = index;
            province.name = name;
            province.area = area == null ? null : area.getArea();

            return province;
        }
    }

    @JsonObject
    static class AreaData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        public StaySuggestV2.Area getArea()
        {
            StaySuggestV2.Area area = new StaySuggestV2.Area();
            area.index = index;
            area.name = name;

            return area;
        }
    }

    public List<StaySuggestV2> getSuggestList(Context context)
    {
        List<StaySuggestV2> list = new ArrayList<>();

        if (context == null)
        {
            return list;
        }

        if (stayList != null && stayList.size() > 0)
        {
            list.add(new StaySuggestV2(StaySuggestV2.MenuType.SUGGEST, new StaySuggestV2.Section(context.getString(R.string.label_search_suggest_type_stay))));

            for (StayData stayData : stayList)
            {
                StaySuggestV2.Stay stay = stayData.getStay();

                list.add(new StaySuggestV2(StaySuggestV2.MenuType.SUGGEST, stay));
            }
        }

        if (areaGroupList != null && areaGroupList.size() > 0)
        {
            list.add(new StaySuggestV2(StaySuggestV2.MenuType.SUGGEST, new StaySuggestV2.Section(context.getString(R.string.label_search_suggest_type_region))));

            for (ProvinceData provinceData : areaGroupList)
            {
                StaySuggestV2.Province province = provinceData.getProvince();

                list.add(new StaySuggestV2(StaySuggestV2.MenuType.SUGGEST, province));
            }
        }

        if (stationList != null && stationList.size() > 0)
        {
            list.add(new StaySuggestV2(StaySuggestV2.MenuType.SUGGEST, new StaySuggestV2.Section(context.getString(R.string.label_search_suggest_type_station))));

            for (StationData stationData : stationList)
            {
                StaySuggestV2.Station station = stationData.getStation();

                list.add(new StaySuggestV2(StaySuggestV2.MenuType.SUGGEST, station));
            }
        }

        return list;
    }

}
