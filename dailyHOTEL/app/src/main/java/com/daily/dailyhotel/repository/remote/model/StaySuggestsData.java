package com.daily.dailyhotel.repository.remote.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StaySuggest;
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
    public List<AreaGroupData> areaGroupList;

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

        public StaySuggest.Station getStation()
        {
            StaySuggest.Station station = new StaySuggest.Station();
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
        public AreaGroupData areaGroup;

        public StaySuggest.Stay getStay()
        {
            StaySuggest.Stay stay = new StaySuggest.Stay();
            stay.index = index;
            stay.name = name;
            stay.discountAvg = discountAvg;
            stay.available = available;
            stay.areaGroup = areaGroup == null ? null : areaGroup.getAreaGroup();

            return stay;
        }
    }

    @JsonObject
    static class AreaGroupData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        @JsonField(name = "area")
        public AreaData area;

        public StaySuggest.AreaGroup getAreaGroup()
        {
            StaySuggest.AreaGroup areaGroup = new StaySuggest.AreaGroup();
            areaGroup.index = index;
            areaGroup.name = name;
            areaGroup.area = area == null ? null : area.getArea();

            return areaGroup;
        }
    }

    @JsonObject
    static class AreaData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        public StaySuggest.Area getArea()
        {
            StaySuggest.Area area = new StaySuggest.Area();
            area.index = index;
            area.name = name;

            return area;
        }
    }

    public List<StaySuggest> getSuggestList(Context context)
    {
        List<StaySuggest> list = new ArrayList<>();

        if (context == null)
        {
            return list;
        }

        if (stayList != null && stayList.size() > 0)
        {
            list.add(new StaySuggest(StaySuggest.MenuType.SUGGEST, new StaySuggest.Section(context.getString(R.string.label_search_suggest_type_stay))));

            for (StayData stayData : stayList)
            {
                StaySuggest.Stay stay = stayData.getStay();

                list.add(new StaySuggest(StaySuggest.MenuType.SUGGEST, stay));
            }
        }

        if (areaGroupList != null && areaGroupList.size() > 0)
        {
            list.add(new StaySuggest(StaySuggest.MenuType.SUGGEST, new StaySuggest.Section(context.getString(R.string.label_search_suggest_type_region))));

            for (AreaGroupData areaGroupData : areaGroupList)
            {
                StaySuggest.AreaGroup areaGroup = areaGroupData.getAreaGroup();

                list.add(new StaySuggest(StaySuggest.MenuType.SUGGEST, areaGroup));
            }
        }

        if (stationList != null && stationList.size() > 0)
        {
            list.add(new StaySuggest(StaySuggest.MenuType.SUGGEST, new StaySuggest.Section(context.getString(R.string.label_search_suggest_type_station))));

            for (StationData stationData : stationList)
            {
                StaySuggest.Station station = stationData.getStation();

                list.add(new StaySuggest(StaySuggest.MenuType.SUGGEST, station));
            }
        }

        return list;
    }

}
