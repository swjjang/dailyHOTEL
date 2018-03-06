package com.daily.dailyhotel.entity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.model.Province;

import org.json.JSONObject;

public class PreferenceRegion
{
    private static final String JSON_KEY_REGION_NAME = "regionName";
    private static final String JSON_KEY_AREA_TYPE = "areaType";
    private static final String JSON_KEY_OVERSEAS = "overseas";
    private static final String JSON_KEY_AREA_GROUP_NAME = "areaGroupName";
    private static final String JSON_KEY_AREA_NAME = "areaName";

    public enum AreaType
    {
        AREA,
        SUBWAY_AREA,
    }

    public String regionName;
    public AreaType areaType;
    public boolean overseas;
    public String areaGroupName;
    public String areaName;

    public PreferenceRegion(AreaType areaType)
    {
        this.areaType = areaType;
    }

    public PreferenceRegion(Province province) throws Exception
    {
        if (province == null)
        {
            throw new NullPointerException("province == null");
        }

        areaType = AreaType.AREA;

        if (province instanceof com.twoheart.dailyhotel.model.Area)
        {
            com.twoheart.dailyhotel.model.Area area = (com.twoheart.dailyhotel.model.Area) province;

            regionName = areaGroupName = area.getProvince().name;
            areaName = area.name;
            overseas = area.getProvince().isOverseas;
        } else
        {
            regionName = areaGroupName = province.name;
            areaName = "";
            overseas = province.isOverseas;
        }
    }

    public PreferenceRegion(JSONObject jsonObject) throws Exception
    {
        if (jsonObject == null)
        {
            throw new NullPointerException("jsonObject == null");
        }

        regionName = jsonObject.getString(JSON_KEY_REGION_NAME);
        areaType = AreaType.valueOf(jsonObject.getString(JSON_KEY_AREA_TYPE));
        overseas = jsonObject.getBoolean(JSON_KEY_OVERSEAS);
        areaGroupName = jsonObject.getString(JSON_KEY_AREA_GROUP_NAME);
        areaName = jsonObject.getString(JSON_KEY_AREA_NAME);
    }

    public JSONObject toJSONObject()
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put(JSON_KEY_REGION_NAME, DailyTextUtils.isTextEmpty(regionName) ? "" : regionName);
            jsonObject.put(JSON_KEY_AREA_TYPE, areaType.name());
            jsonObject.put(JSON_KEY_OVERSEAS, overseas);
            jsonObject.put(JSON_KEY_AREA_GROUP_NAME, DailyTextUtils.isTextEmpty(areaGroupName) ? "" : areaGroupName);
            jsonObject.put(JSON_KEY_AREA_NAME, DailyTextUtils.isTextEmpty(areaName) ? "" : areaName);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return null;
        }

        return jsonObject;
    }

    public boolean equalsArea(Province province)
    {
        if (province == null)
        {
            return false;
        }

        if (province instanceof com.twoheart.dailyhotel.model.Area)
        {
            return province.name.equalsIgnoreCase(areaName);
        } else
        {
            return province.name.equalsIgnoreCase(areaGroupName);
        }
    }
}
