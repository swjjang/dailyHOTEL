package com.twoheart.dailyhotel.screen.hotel.region;

import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RegionViewItem;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.presenter.PlaceRegionListPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HotelRegionListPresenter extends PlaceRegionListPresenter
{
    private BaseActivity mBaseActivity;

    private OnResponsePresenterListener mListener;

    public HotelRegionListPresenter(BaseActivity baseActivity, OnResponsePresenterListener listener)
    {
        if (baseActivity == null || listener == null)
        {
            throw new NullPointerException("baseActivity == null || listener == null");
        }

        mBaseActivity = baseActivity;
        mListener = listener;
    }

    public void requestRegionList()
    {
        DailyNetworkAPI.getInstance().requestHotelRegionList(mBaseActivity.getNetworkTag(), mHotelRegionListJsonResponseListener, mBaseActivity);
    }

    private DailyHotelJsonResponseListener mHotelRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
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

                    mListener.onRegionListResponse(domesticRegionViewList, globalRegionViewList);
                } else
                {
                    String message = response.getString("msg");
                    mListener.onErrorMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                mListener.onError(e);
            }
        }
    };
}