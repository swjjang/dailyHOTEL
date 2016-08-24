package com.twoheart.dailyhotel.screen.gourmet.region;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceRegionListNetworkController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GourmetRegionListNetworkController extends PlaceRegionListNetworkController
{
    private BaseActivity mBaseActivity;

    private OnNetworkControllerListener mOnNetworkControllerListener;

    public GourmetRegionListNetworkController(BaseActivity baseActivity, OnNetworkControllerListener listener)
    {
        if (baseActivity == null || listener == null)
        {
            throw new NullPointerException("baseActivity == null || listener == null");
        }

        mBaseActivity = baseActivity;
        mOnNetworkControllerListener = listener;
    }

    @Override
    public void requestRegionList()
    {
        DailyNetworkAPI.getInstance(mBaseActivity).requestGourmetRegionList(mBaseActivity.getNetworkTag(), mGourmetRegionListJsonResponseListener);
    }

    private DailyHotelJsonResponseListener mGourmetRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mOnNetworkControllerListener.onErrorResponse(volleyError);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
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

                    mOnNetworkControllerListener.onRegionListResponse(domesticRegionViewList, globalRegionViewList);
                } else
                {
                    String message = response.getString("msg");

                    mOnNetworkControllerListener.onErrorPopupMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                mOnNetworkControllerListener.onError(e);
            }
        }
    };
}