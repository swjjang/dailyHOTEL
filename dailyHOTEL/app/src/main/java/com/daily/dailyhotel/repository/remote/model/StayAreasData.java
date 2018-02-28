package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 2. 15..
 */
@JsonObject
public class StayAreasData
{
    @JsonField(name = "regionProvince")
    public List<ProvinceData> regionProvince;

    @JsonField(name = "regionArea")
    public List<AreaData> regionArea;

    public StayAreasData()
    {
    }

    public List<StayAreaGroup> getAreaGroupList()
    {
        List<StayAreaGroup> areaGroupList = new ArrayList<>();

        if (regionProvince != null && regionProvince.size() > 0)
        {
            StayAreaGroup stayAreaGroup;

            for (ProvinceData provinceData : regionProvince)
            {
                // 해외 지역은 보여주지 않는다.
                if (provinceData.overseas == true)
                {
                    continue;
                }

                stayAreaGroup = provinceData.getAreaGroup();

                if (regionArea != null && regionArea.size() > 0)
                {
                    List<StayArea> areaList = new ArrayList<>();

                    for (AreaData areaData : regionArea)
                    {
                        if (areaData.provinceIndex == provinceData.index)
                        {
                            areaList.add(areaData.getArea());
                        }
                    }

                    // 개수가 0보다 크면 전체 지역을 넣는다.
                    if (areaList.size() > 0)
                    {
                        StayArea stayArea = new StayArea(stayAreaGroup);
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

        @JsonField(name = "categories")
        public List<LinkedHashMap<String, String>> categories;

        @JsonField(name = "overseas")
        public boolean overseas;

        public ProvinceData()
        {

        }

        public StayAreaGroup getAreaGroup()
        {
            StayAreaGroup stayAreaGroup = new StayAreaGroup();
            stayAreaGroup.index = index;
            stayAreaGroup.name = name;

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

            stayAreaGroup.setCategoryList(categoryList);

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

        @JsonField(name = "categories")
        public List<LinkedHashMap<String, String>> categories;

        public AreaData()
        {

        }

        public StayArea getArea()
        {
            StayArea stayArea = new StayArea();

            stayArea.index = index;
            stayArea.name = name;

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

            stayArea.setCategoryList(categoryList);

            return stayArea;
        }
    }
}
