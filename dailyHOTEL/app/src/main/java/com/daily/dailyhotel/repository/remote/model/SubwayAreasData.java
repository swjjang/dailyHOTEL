package com.daily.dailyhotel.repository.remote.model;

import android.util.Pair;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;

import java.util.ArrayList;
import java.util.List;

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
            subwayAreaGroupList.add(lineData.getStaySubwayAreaGroup());
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

        StaySubwayAreaGroup getStaySubwayAreaGroup()
        {
            StaySubwayAreaGroup subwayAreaGroup = new StaySubwayAreaGroup();
            subwayAreaGroup.index = index;
            subwayAreaGroup.name = name;

            List<Area> areaList = new ArrayList<>();

            for (StationData stationData : stationList)
            {
                areaList.add(stationData.getStation());
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

        Area getStation()
        {
            Area area = new Area();
            area.index = index;
            area.name = name;

            return area;
        }
    }
}
