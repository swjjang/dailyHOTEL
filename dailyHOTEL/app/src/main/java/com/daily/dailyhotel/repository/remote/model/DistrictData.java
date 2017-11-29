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
public class DistrictData
{
    @JsonField(name = "imgUrl")
    public String imgUrl;

    @JsonField(name = "regionProvince")
    public List<ProvinceData> regionProvince;

    @JsonField(name = "regionArea")
    public List<AreaData> regionArea;

    public DistrictData()
    {
    }

    public List<StayDistrict> getDistrictList()
    {
        List<StayDistrict> stayDistrictList = new ArrayList<>();

        if (regionProvince != null && regionProvince.size() > 0)
        {
            StayDistrict stayDistrict;

            for (ProvinceData provinceData : regionProvince)
            {
                // 해외 지역은 보여주지 않는다.
                if (provinceData.overseas == true)
                {
                    continue;
                }

                stayDistrict = provinceData.getDistrict();

                if (regionArea != null && regionArea.size() > 0)
                {
                    List<StayTown> stayTownList = new ArrayList<>();

                    for (AreaData areaData : regionArea)
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

        @JsonField(name = "categories")
        public List<LinkedHashMap<String, String>> categories;

        @JsonField(name = "overseas")
        public boolean overseas;

        public ProvinceData()
        {

        }

        public StayDistrict getDistrict()
        {
            StayDistrict stayDistrict = new StayDistrict();
            stayDistrict.index = index;
            stayDistrict.name = name;

            if (categories != null && categories.size() > 0)
            {
                List<Category> categoryList = new ArrayList<>();

                for(LinkedHashMap<String, String> linkedHashMap : categories)
                {
                    Iterator<Map.Entry<String, String>> iterator = linkedHashMap.entrySet().iterator();

                    while (iterator.hasNext())
                    {
                        Map.Entry<String, String> entry = iterator.next();

                        categoryList.add(new Category(entry.getKey(), entry.getValue()));
                    }
                }

                stayDistrict.setCategoryList(categoryList);
            }

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

        @JsonField(name = "categories")
        public List<LinkedHashMap<String, String>> categories;

        public AreaData()
        {

        }

        public StayTown getTown(StayDistrict stayDistrict)
        {
            StayTown stayTown = new StayTown();

            stayTown.index = index;
            stayTown.name = name;
            stayTown.setDistrict(stayDistrict);

            if (categories != null && categories.size() > 0)
            {
                List<Category> categoryList = new ArrayList<>();

                for(LinkedHashMap<String, String> linkedHashMap : categories)
                {
                    Iterator<Map.Entry<String, String>> iterator = linkedHashMap.entrySet().iterator();

                    while (iterator.hasNext())
                    {
                        Map.Entry<String, String> entry = iterator.next();

                        categoryList.add(new Category(entry.getKey(), entry.getValue()));
                    }
                }

                stayTown.setCategoryList(categoryList);
            }

            return stayTown;
        }
    }
}
