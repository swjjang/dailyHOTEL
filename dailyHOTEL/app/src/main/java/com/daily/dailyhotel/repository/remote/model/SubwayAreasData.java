package com.daily.dailyhotel.repository.remote.model;

import android.util.Pair;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.StaySubwayArea;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonObject
public class SubwayAreasData
{
    @JsonField(name = "idx")
    public int index;

    @JsonField(name = "region")
    public String name;

    @JsonField(name = "lines")
    public List<LineData> lineList;

    public SubwayAreasData()
    {
    }

    public Pair<Area, List<StaySubwayAreaGroup>> getAreaGroup()
    {
        Area area = new Area();
        area.index = index;
        area.name = name;

        List<StaySubwayAreaGroup> subwayAreaGroupList = new ArrayList<>();

        for (LineData lineData : lineList)
        {
            subwayAreaGroupList.add(lineData.getStaySubwayAreaGroup(area));
        }

        return new Pair(area, subwayAreaGroupList);
    }

    @JsonObject
    static class LineData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "line")
        public String name;

        @JsonField(name = "stations")
        public List<StationData> stationList;

        StaySubwayAreaGroup getStaySubwayAreaGroup(Area region)
        {
            StaySubwayAreaGroup subwayAreaGroup = new StaySubwayAreaGroup(region);
            subwayAreaGroup.index = index;
            subwayAreaGroup.name = name;

            List<StaySubwayArea> areaList = new ArrayList<>();

            for (StationData stationData : stationList)
            {
                areaList.add(stationData.getStaySubwayArea());
            }

            subwayAreaGroup.setAreaList(areaList);

            return subwayAreaGroup;
        }
    }

    @JsonObject
    static class StationData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "station")
        public String name;

        @JsonField(name = "categories")
        public List<LinkedHashMap<String, String>> categories;

        StaySubwayArea getStaySubwayArea()
        {
            StaySubwayArea area = new StaySubwayArea(index, name);

            List<Category> categoryList = new ArrayList<>();

            if (categories != null && categories.size() > 0)
            {
                for (LinkedHashMap<String, String> linkedHashMap : categories)
                {
                    Iterator<Map.Entry<String, String>> iterator = linkedHashMap.entrySet().iterator();

                    while (iterator.hasNext())
                    {
                        Map.Entry<String, String> entry = iterator.next();

                        categoryList.add(new Category(entry.getKey(), entry.getValue()));
                    }
                }
            } else
            {
                categoryList.add(Category.ALL);
            }

            return area;
        }
    }
}
