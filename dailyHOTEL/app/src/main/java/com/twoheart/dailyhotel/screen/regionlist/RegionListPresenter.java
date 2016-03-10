package com.twoheart.dailyhotel.screen.regionlist;

import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegionListPresenter
{
    private static final int CHILD_GRID_COLUMN = 2;
    private BaseActivity mBaseActivity;

    private RegionListActivity.OnResponsePresenterListener mListener;

    public RegionListPresenter(BaseActivity baseActivity, RegionListActivity.OnResponsePresenterListener listener)
    {
        if (baseActivity == null || listener == null)
        {
            throw new NullPointerException("baseActivity == null || listener == null");
        }

        mBaseActivity = baseActivity;
        mListener = listener;
    }

    public void requestHotelRegionList()
    {
        DailyNetworkAPI.getInstance().requestHotelRegionList(mBaseActivity.getNetworkTag(), mHotelRegionListJsonResponseListener, mBaseActivity);
    }

    public void requestGourmetRegionList()
    {
        DailyNetworkAPI.getInstance().requestGourmetRegionList(mBaseActivity.getNetworkTag(), mGourmetRegionListJsonResponseListener, mBaseActivity);
    }

    /**
     * @param domesticProvinceList in
     * @param globalProvinceList   in
     * @param areaList
     * @param domesticRegionList   out
     * @param globalRegionList     out
     */
    private void makeRegionViewItemList(List<Province> domesticProvinceList, List<Province> globalProvinceList//
        , ArrayList<Area> areaList, List<RegionViewItem> domesticRegionList, List<RegionViewItem> globalRegionList)
    {
        if (domesticProvinceList == null || globalProvinceList == null || areaList == null || domesticRegionList == null || globalRegionList == null)
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

        // 해외
        List<RegionViewItem> regionViewList2 = getRegionViewList(globalProvinceList, areaList);

        if (regionViewList2 != null)
        {
            globalRegionList.clear();
            globalRegionList.addAll(regionViewList2);
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
                        totalArea.tag = totalArea.name;
                        totalArea.isOverseas = province.isOverseas;
                        totalArea.setProvinceIndex(province.getProvinceIndex());

                        areas[i++] = totalArea;
                    }

                    area.isOverseas = province.isOverseas;
                    area.setProvince(province);

                    if (i != 0 && i % CHILD_GRID_COLUMN == 1)
                    {
                        areas[i++] = area;
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
     * @param globalProvinceList   out
     * @throws JSONException
     */
    private void makeProvinceList(JSONArray jsonArray, String imageUrl, List<Province> domesticProvinceList, List<Province> globalProvinceList) throws JSONException
    {
        if (domesticProvinceList == null || globalProvinceList == null)
        {
            return;
        }

        int length = jsonArray.length();

        for (int i = 0; i < length; i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            try
            {
                Province province = new Province(jsonObject, imageUrl);

                if (province.isOverseas == true)
                {
                    globalProvinceList.add(province);
                } else
                {
                    domesticProvinceList.add(province);
                }
            } catch (JSONException e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    private ArrayList<Area> makeAreaList(JSONArray jsonArray)
    {
        ArrayList<Area> areaList = new ArrayList<Area>();

        try
        {
            int length = jsonArray.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                try
                {
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

    private DailyHotelJsonResponseListener mHotelRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msgCode");

                if (msg_code == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    String imageUrl = dataJSONObject.getString("imgUrl");

                    List<Province> domesticProvinceList = new ArrayList<>();
                    List<Province> globalProvinceList = new ArrayList<>();

                    JSONArray provinceArray = dataJSONObject.getJSONArray("province");
                    makeProvinceList(provinceArray, imageUrl, domesticProvinceList, globalProvinceList);

                    JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
                    ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                    List<RegionViewItem> domesticRegionViewList = new ArrayList<>();
                    List<RegionViewItem> globalRegionViewList = new ArrayList<>();

                    makeRegionViewItemList(domesticProvinceList, globalProvinceList, areaList, domesticRegionViewList, globalRegionViewList);

                    mListener.onRegionListResponse(domesticRegionViewList, globalRegionViewList);
                } else
                {
                    String message = response.getString("msg");

                    mListener.onInternalError(message);
                }
            } catch (Exception e)
            {
                mListener.onInternalError();
            }
        }
    };

    private DailyHotelJsonResponseListener mGourmetRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msgCode");

                if (msg_code == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    String imageUrl = dataJSONObject.getString("imgUrl");

                    List<Province> domesticProvinceList = new ArrayList<>();
                    List<Province> globalProvinceList = new ArrayList<>();

                    JSONArray provinceArray = dataJSONObject.getJSONArray("province");
                    makeProvinceList(provinceArray, imageUrl, domesticProvinceList, globalProvinceList);

                    JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
                    ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                    List<RegionViewItem> domesticRegionViewList = new ArrayList<>();
                    List<RegionViewItem> globalRegionViewList = new ArrayList<>();

                    makeRegionViewItemList(domesticProvinceList, globalProvinceList, areaList, domesticRegionViewList, globalRegionViewList);

                    mListener.onRegionListResponse(domesticRegionViewList, globalRegionViewList);
                } else
                {
                    String message = response.getString("msg");

                    mListener.onInternalError(message);
                }
            } catch (Exception e)
            {
                mListener.onInternalError();
            }
        }
    };
}