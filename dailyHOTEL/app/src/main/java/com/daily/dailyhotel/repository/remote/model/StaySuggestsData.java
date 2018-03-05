package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

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
    public List<RegionData> regionList;

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

        @JsonField(name = "availableRooms")
        public int availableRooms;

        @JsonField(name = "region")
        public RegionData region;
    }

    @JsonObject
    static class RegionData
    {
        @JsonField(name = "province")
        public List<ProvinceData> provinceList;

        @JsonField(name = "area")
        public List<AreaData> areaList;
    }

    @JsonObject
    static class ProvinceData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public int name;
    }

    @JsonObject
    static class AreaData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public int name;

        @JsonField(name = "province")
        public ProvinceData province;
    }
}
