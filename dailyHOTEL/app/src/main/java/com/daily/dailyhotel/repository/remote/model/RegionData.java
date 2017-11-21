package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.Province;
import com.daily.dailyhotel.entity.Region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2017. 2. 15..
 */
@JsonObject
public class RegionData
{
    @JsonField(name = "imgUrl")
    public String imgUrl;

    @JsonField(name = "regionProvince")
    public List<ProvinceData> regionProvince;

    @JsonField(name = "regionArea")
    public List<AreaData> regionArea;

    public RegionData()
    {
    }

    public Region getRegion()
    {
        Region region = new Region();

        if (regionProvince != null && regionProvince.size() > 0)
        {
            List<Province> provinceList = new ArrayList<>();

            for (ProvinceData provinceData : regionProvince)
            {
                provinceList.add(provinceData.getProvince());
            }

            region.setProvinceList(provinceList);
        }

        if (regionProvince != null && regionProvince.size() > 0 && regionArea != null && regionArea.size() > 0)
        {
            List<Area> areaList = new ArrayList<>();
            Area area;

            for (AreaData areaData : regionArea)
            {
                area = areaData.getArea(region.getProvinceList());

                if (area != null)
                {
                    areaList.add(area);
                }
            }

            region.setAreaList(areaList);
        }

        return region;
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
        public LinkedHashMap<String, String> categories;

        @JsonField(name = "overseas")
        public boolean overseas;

        public ProvinceData()
        {

        }

        public Province getProvince()
        {
            Province province = new Province();
            province.index = index;
            province.name = name;
            province.nameEng = nameEng;
            province.sequence = sequence;

            if (categories != null && categories.size() > 0)
            {
                List<Category> categoryList = new ArrayList<>();

                Iterator<Map.Entry<String, String>> iterator = categories.entrySet().iterator();

                while (iterator.hasNext())
                {
                    Map.Entry<String, String> entry = iterator.next();

                    categoryList.add(new Category(entry.getKey(), entry.getValue()));
                }

                province.setCategoryList(categoryList);
            }

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

        @JsonField(name = "sequence")
        public int sequence;

        @JsonField(name = "provinceIdx")
        public int provinceIndex;

        @JsonField(name = "categories")
        public LinkedHashMap<String, String> categories;

        public AreaData()
        {

        }

        public Area getArea(List<Province> provinceList)
        {
            if (provinceList == null || provinceList.size() == 0)
            {
                return null;
            }

            Area area = new Area();

            for (Province province : provinceList)
            {
                if (provinceIndex == province.index)
                {
                    area.setProvince(province);
                    break;
                }
            }

            if (area.getProvince() == null)
            {
                return null;
            }

            area.index = index;
            area.name = name;
            area.sequence = sequence;

            if (categories != null && categories.size() > 0)
            {
                List<Category> categoryList = new ArrayList<>();

                Iterator<Map.Entry<String, String>> iterator = categories.entrySet().iterator();

                while (iterator.hasNext())
                {
                    Map.Entry<String, String> entry = iterator.next();

                    categoryList.add(new Category(entry.getKey(), entry.getValue()));
                }

                area.setCategoryList(categoryList);
            }

            return area;
        }
    }
}
