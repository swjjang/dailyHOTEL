package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 2. 15..
 */
@JsonObject
public class StayRegionData
{
    @JsonField(name = "provinceList")
    public List<ProvinceData> provinceList;

    @JsonField(name = "areaList")
    public List<AreaData> areaList;

    public StayRegionData()
    {
    }

    public List<StayAreaGroup> getAreaGroupList()
    {
        List<StayAreaGroup> areaGroupList = new ArrayList<>();

        if (provinceList != null && provinceList.size() > 0)
        {
            StayAreaGroup stayAreaGroup;

            for (ProvinceData provinceData : provinceList)
            {
                stayAreaGroup = provinceData.getAreaGroup();

                if (areaList != null && areaList.size() > 0)
                {
                    List<StayArea> areaList = new ArrayList<>();

                    for (AreaData areaData : this.areaList)
                    {
                        if (areaData.provinceIndex == provinceData.index)
                        {
                            areaList.add(areaData.getArea());
                        }
                    }

                    // 개수가 0보다 크면 전체 지역을 넣는다.
                    if (areaList.size() > 0)
                    {
                        StayArea stayArea = new StayArea(StayArea.ALL, stayAreaGroup.name);
                        stayArea.setCategoryList(stayAreaGroup.getCategoryList());
                        areaList.add(0, stayArea);
                    }

                    stayAreaGroup.setAreaList(areaList);
                }

                areaGroupList.add(stayAreaGroup);
            }
        }

        return areaGroupList;
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

        public StayAreaGroup getAreaGroup()
        {
            StayAreaGroup stayAreaGroup = new StayAreaGroup();
            stayAreaGroup.index = index;
            stayAreaGroup.name = name;

            return stayAreaGroup;
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

        public StayArea getArea()
        {
            return new StayArea(index, name);
        }
    }
}
