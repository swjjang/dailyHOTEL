package com.twoheart.dailyhotel.place.networkcontroller;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceRegionListNetworkController
{
    private static final int CHILD_GRID_COLUMN = 2;

    protected abstract void requestRegionList();

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onRegionListResponse(List<RegionViewItem> domesticList);
    }

    /**
     * @param domesticProvinceList in
     * @param areaList
     * @param domesticRegionList   out
     */
    protected void makeRegionViewItemList(List<Province> domesticProvinceList//
        , ArrayList<Area> areaList, List<RegionViewItem> domesticRegionList)
    {
        if (domesticProvinceList == null || areaList == null || domesticRegionList == null)
        {
            return;
        }

        // 국내
        List<RegionViewItem> regionViewList1 = getRegionViewList(domesticProvinceList, areaList);

        if (regionViewList1 != null)
        {
            domesticRegionList.clear();
            domesticRegionList.addAll(regionViewList1);
        }
    }

    private List<RegionViewItem> getRegionViewList(List<Province> provinceList, ArrayList<Area> areaList)
    {
        if (provinceList == null || areaList == null)
        {
            return null;
        }

        List<RegionViewItem> regionViewList = new ArrayList<>(provinceList.size());

        for (Province province : provinceList)
        {
            RegionViewItem item = new RegionViewItem();

            item.setProvince(province);

            int i = 0;
            Area[] areas = null;
            ArrayList<Area[]> areaArrayList = new ArrayList<>();

            for (Area area : areaList)
            {
                if (province.getProvinceIndex() == area.getProvinceIndex())
                {
                    if (areas == null)
                    {
                        areas = new Area[CHILD_GRID_COLUMN];
                    }

                    if (areaArrayList.size() == 0)
                    {
                        Area totalArea = new Area();

                        totalArea.index = -1;
                        totalArea.name = province.name + " 전체";
                        totalArea.setProvince(province);
                        totalArea.sequence = -1;
                        totalArea.isOverseas = province.isOverseas;
                        totalArea.setProvinceIndex(province.getProvinceIndex());

                        areas[i++] = totalArea;
                    }

                    area.isOverseas = province.isOverseas;
                    area.setProvince(province);

                    if (i > 0 && i % CHILD_GRID_COLUMN == 1)
                    {
                        areas[i] = area;
                        areaArrayList.add(areas);

                        i = 0;
                        areas = null;
                    } else
                    {
                        areas[i++] = area;
                    }
                }
            }

            if (areas != null)
            {
                areaArrayList.add(areas);
            }

            item.setAreaList(areaArrayList);
            regionViewList.add(item);
        }

        return regionViewList;
    }

    /**
     * @param jsonArray
     * @param imageUrl
     * @param domesticProvinceList out
     * @throws JSONException
     */
    protected void makeProvinceList(JSONArray jsonArray, String imageUrl, List<Province> domesticProvinceList) throws JSONException
    {
        if (domesticProvinceList == null)
        {
            return;
        }

        int length = jsonArray.length();

        for (int i = 0; i < length; i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            try
            {
                if (jsonObject.has("overseas") == true || jsonObject.getBoolean("overseas") == true)
                {
                    continue;
                }

                Province province = new Province(jsonObject, imageUrl);

                if (province.isOverseas == false)
                {
                    domesticProvinceList.add(province);
                }
            } catch (JSONException e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    protected ArrayList<Area> makeAreaList(JSONArray jsonArray)
    {
        ArrayList<Area> areaList = new ArrayList<>();

        try
        {
            int length = jsonArray.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                try
                {
                    if (jsonObject.has("overseas") == true || jsonObject.getBoolean("overseas") == true)
                    {
                        continue;
                    }

                    Area area = new Area(jsonObject);

                    areaList.add(area);
                } catch (JSONException e)
                {
                    ExLog.d(e.toString());
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return areaList;
    }
}