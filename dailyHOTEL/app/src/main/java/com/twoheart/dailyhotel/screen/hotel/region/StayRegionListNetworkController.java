package com.twoheart.dailyhotel.screen.hotel.region;

import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceRegionListNetworkController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StayRegionListNetworkController extends PlaceRegionListNetworkController
{
    private BaseActivity mBaseActivity;

    OnNetworkControllerListener mOnNetworkControllerListener;

    public StayRegionListNetworkController(BaseActivity baseActivity, OnNetworkControllerListener listener)
    {
        if (baseActivity == null || listener == null)
        {
            throw new NullPointerException("baseActivity == null || listener == null");
        }

        mBaseActivity = baseActivity;
        mOnNetworkControllerListener = listener;
    }

    public void requestRegionList()
    {
        DailyMobileAPI.getInstance(mBaseActivity).requestStayRegionList(mBaseActivity.getNetworkTag(), mHotelRegionListCallback);
    }

    private retrofit2.Callback mHotelRegionListCallback = new retrofit2.Callback<JSONObject>()
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

                        String imageUrl = dataJSONObject.getString("imgUrl");

                        List<Province> domesticProvinceList = new ArrayList<>();
                        List<Province> globalProvinceList = new ArrayList<>();

                        JSONArray provinceArray = dataJSONObject.getJSONArray("regionProvince");
                        makeProvinceList(provinceArray, imageUrl, domesticProvinceList, globalProvinceList);

                        JSONArray areaJSONArray = dataJSONObject.getJSONArray("regionArea");
                        ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                        List<RegionViewItem> domesticRegionViewList = new ArrayList<>();
                        List<RegionViewItem> globalRegionViewList = new ArrayList<>();

                        makeRegionViewItemList(domesticProvinceList, globalProvinceList, areaList, domesticRegionViewList, globalRegionViewList);

                        mOnNetworkControllerListener.onRegionListResponse(domesticRegionViewList, globalRegionViewList);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                    }
                } catch (Exception e)
                {
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
            mOnNetworkControllerListener.onError(t);
        }
    };
}