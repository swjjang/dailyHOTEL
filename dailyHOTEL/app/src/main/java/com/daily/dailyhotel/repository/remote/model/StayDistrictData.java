package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.StayDistrict;
import com.daily.dailyhotel.entity.StayTown;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 2. 15..
 */
@JsonObject
public class StayDistrictData
{
    @JsonField(name = "imgUrl")
    public String imgUrl;

    @JsonField(name = "provinceList")
    public List<ProvinceData> provinceList;

    @JsonField(name = "areaList")
    public List<AreaData> areaList;

    public StayDistrictData()
    {
    }

    public List<StayDistrict> getDistrictList()
    {
        List<StayDistrict> stayDistrictList = new ArrayList<>();

        if (provinceList != null && provinceList.size() > 0)
        {
            StayDistrict stayDistrict;

            for (ProvinceData provinceData : provinceList)
            {
                stayDistrict = provinceData.getDistrict();

                if (areaList != null && areaList.size() > 0)
                {
                    List<StayTown> stayTownList = new ArrayList<>();

                    for (AreaData areaData : areaList)
                    {
                        if (areaData.provinceIndex == provinceData.index)
                        {
                            stayTownList.add(areaData.getTown(stayDistrict));
                        }
                    }

                    // 개수가 0보다 크면 전체 지역을 넣는다.
                    if (stayTownList.size() > 0)
                    {
                        stayTownList.add(0, new StayTown(stayDistrict));
                    }

                    stayDistrict.setTownList(stayTownList);
                }

                stayDistrictList.add(stayDistrict);
            }
        }

        return stayDistrictList;
    }

    @JsonObject
    static class ProvinceData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        @JsonField(name = "nameEng")
        public String nameEng;

        @JsonField(name = "sequence")
        public int sequence;

        @JsonField(name = "imagePath")
        public String imagePath;

        public ProvinceData()
        {

        }

        public StayDistrict getDistrict()
        {
            StayDistrict stayDistrict = new StayDistrict();
            stayDistrict.index = index;
            stayDistrict.name = name;

            return stayDistrict;
        }
    }

    @JsonObject
    static class AreaData
    {
        @JsonField(name = "idx")
        public int index;

        @JsonField(name = "name")
        public String name;

        @JsonField(name = "sequence")
        public int sequence;

        @JsonField(name = "provinceIdx")
        public int provinceIndex;

        public AreaData()
        {

        }

        public StayTown getTown(StayDistrict stayDistrict)
        {
            StayTown stayTown = new StayTown();

            stayTown.index = index;
            stayTown.name = name;
            stayTown.setDistrict(stayDistrict);

            return stayTown;
        }
    }
}
