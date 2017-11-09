package com.twoheart.dailyhotel.screen.home.category.region;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 4. 12..
 */

public class HomeCategoryRegionListNetworkController extends BaseNetworkController
{
    private static final int CHILD_GRID_COLUMN = 2;

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onRegionListResponse(List<RegionViewItem> regionViewList, List<RegionViewItem> subwayViewList);
    }

    public HomeCategoryRegionListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestRegionList(String categoryCode)
    {
        DailyMobileAPI.getInstance(mContext).requestStayCategoryRegionList(mNetworkTag, categoryCode, mCategoryRegionListCallback);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param provinceList       in
     * @param areaList
     * @param regionViewItemList out
     */
    protected void makeRegionViewItemList(List<Province> provinceList //
        , ArrayList<Area> areaList, List<RegionViewItem> regionViewItemList)
    {
        if (provinceList == null || areaList == null || regionViewItemList == null)
        {
            return;
        }

        // 국내
        List<RegionViewItem> regionViewList = getRegionViewList(provinceList, areaList);

        if (regionViewList != null)
        {
            regionViewItemList.clear();
            regionViewItemList.addAll(regionViewList);
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
                        totalArea.count = province.count;

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
     * @return
     * @throws JSONException
     */
    protected ArrayList<Province> makeProvinceList(JSONArray jsonArray) throws JSONException
    {
        ArrayList<Province> provinceList = new ArrayList<>();

        int length = jsonArray.length();

        for (int i = 0; i < length; i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            try
            {
                Province province = new Province(jsonObject, null); // imageUrl 의 경우 이제 사용하지 않음
                provinceList.add(province);
            } catch (JSONException e)
            {
                ExLog.d(e.toString());
            }
        }

        return provinceList;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mCategoryRegionListCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        JSONArray provinceArray = dataJSONObject.getJSONArray("provinceList");
                        ArrayList<Province> provinceList = makeProvinceList(provinceArray);

                        JSONArray areaJSONArray = dataJSONObject.getJSONArray("areaList");
                        ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                        List<RegionViewItem> domesticRegionViewList = new ArrayList<>();

                        makeRegionViewItemList(provinceList, areaList, domesticRegionViewList);

                        ((OnNetworkControllerListener) mOnNetworkControllerListener).onRegionListResponse(domesticRegionViewList, null);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }
                } catch (Exception e)
                {
                    String logMessage;
                    try
                    {
                        if (call == null)
                        {
                            logMessage = "mCategoryRegionListCallback , call is null";
                        } else if (call.request() == null)
                        {
                            logMessage = "mCategoryRegionListCallback , request is null";
                        } else if (call.request().url() == null)
                        {
                            logMessage = "mCategoryRegionListCallback , url is null";
                        } else
                        {
                            logMessage = "mCategoryRegionListCallback , " + call.request().url().toString();
                        }

                        JSONObject responseJSONObject = response.body();
                        logMessage += "\n" + responseJSONObject.toString();
                    } catch (Exception e1)
                    {
                        logMessage = "mCategoryRegionListCallback , " + e1.getMessage();
                    }

                    Crashlytics.log(logMessage);

                    mOnNetworkControllerListener.onError(e);
                }
            } else
            {
                mOnNetworkControllerListener.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            mOnNetworkControllerListener.onError(call, t, false);
        }
    };
}
