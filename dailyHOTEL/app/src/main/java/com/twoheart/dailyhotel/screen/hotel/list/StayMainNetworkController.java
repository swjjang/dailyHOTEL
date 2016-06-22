package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;
import com.twoheart.dailyhotel.place.manager.PlaceEventBannerManager;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StayMainNetworkController extends PlaceMainNetworkController
{
    public StayMainNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestEventBanner()
    {
        DailyNetworkAPI.getInstance(mContext).requestEventBannerList(mNetworkTag, "hotel", mEventBannerListJsonResponseListener);
    }

    public void requestRegionList()
    {
        DailyNetworkAPI.getInstance(mContext).requestHotelRegionList(mNetworkTag, mRegionListJsonResponseListener, baseActivity);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // NetworkActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////


    private DailyHotelJsonResponseListener mRegionListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    JSONArray provinceArray = dataJSONObject.getJSONArray("province");
                    ArrayList<Province> provinceList = makeProvinceList(provinceArray);

                    JSONArray areaJSONArray = dataJSONObject.getJSONArray("area");
                    ArrayList<Area> areaList = makeAreaList(areaJSONArray);

                    Province selectedProvince = getProvince();

                    if (selectedProvince == null)
                    {
                        selectedProvince = searchLastRegion(baseActivity, provinceList, areaList);
                    }

                    // 여러가지 방식으로 지역을 검색했지만 찾지 못하는 경우.
                    if (selectedProvince == null)
                    {
                        selectedProvince = provinceList.get(0);
                    }

                    // 처음 시작시에는 지역이 Area로 저장된 경우 Province로 변경하기 위한 저장값.
                    boolean mIsProvinceSetting = DailyPreference.getInstance(baseActivity).isSettingRegion(Constants.PlaceType.HOTEL);
                    DailyPreference.getInstance(baseActivity).setSettingRegion(Constants.PlaceType.HOTEL, true);

                    // 마지막으로 지역이 Area로 되어있으면 Province로 바꾸어 준다.
                    if (mIsProvinceSetting == false && selectedProvince instanceof Area)
                    {
                        int provinceIndex = selectedProvince.getProvinceIndex();

                        for (Province province : provinceList)
                        {
                            if (province.getProvinceIndex() == provinceIndex)
                            {
                                selectedProvince = province;
                                break;
                            }
                        }
                    }

                    setProvince(selectedProvince);

                    int position = 0; // 마지막으로 선택한 카테고리로 시작하도로 한다.

                    mHotelMainLayout.setCategoryTabLayout(getChildFragmentManager(), position, getProvince().getCategoryList(), null);

                    if (DailyDeepLink.getInstance().isValidateLink() == true//
                        && processDeepLink(baseActivity, provinceList, areaList) == true)
                    {

                    } else
                    {
                        refreshCurrentFragment(getProvince());
                    }
                } else
                {
                    String message = response.getString("msg");
                    onErrorPopupMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
                unLockUI();
            }
        }

        private Province searchLastRegion(BaseActivity baseActivity, ArrayList<Province> provinceList, ArrayList<Area> areaList)
        {
            Province selectedProvince = null;

            // 마지막으로 선택한 지역을 가져온다.
            String regionName = DailyPreference.getInstance(baseActivity).getSelectedRegion(PlaceType.HOTEL);

            if (Util.isTextEmpty(regionName) == true)
            {
                selectedProvince = provinceList.get(0);
            }

            if (selectedProvince == null)
            {
                for (Province province : provinceList)
                {
                    if (province.name.equals(regionName) == true)
                    {
                        selectedProvince = province;
                        break;
                    }
                }

                if (selectedProvince == null)
                {
                    for (Area area : areaList)
                    {
                        if (area.name.equals(regionName) == true)
                        {
                            for (Province province : provinceList)
                            {
                                if (area.getProvinceIndex() == province.index)
                                {
                                    area.isOverseas = province.isOverseas;
                                    area.setProvince(province);
                                    break;
                                }
                            }

                            selectedProvince = area;
                            break;
                        }
                    }
                }
            }

            return selectedProvince;
        }

        private boolean processDeepLink(BaseActivity baseActivity, ArrayList<Province> provinceList, ArrayList<Area> areaList)
        {
            if (DailyDeepLink.getInstance().isHotelListView() == true)
            {
                unLockUI();
                deepLinkHotelList(provinceList, areaList);
                return true;
            } else
            {
                DailyDeepLink.getInstance().clear();
            }

            return false;
        }

        private ArrayList<Area> makeAreaList(JSONArray jsonArray) throws JSONException
        {
            ArrayList<Area> areaList = new ArrayList<>();

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

            return areaList;
        }

        private ArrayList<Province> makeProvinceList(JSONArray jsonArray)
        {
            ArrayList<Province> provinceList = new ArrayList<>();

            try
            {
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try
                    {
                        Province province = new Province(jsonObject, null);

                        provinceList.add(province);
                    } catch (JSONException e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return provinceList;
        }
    };

    private DailyHotelJsonResponseListener mEventBannerListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventBanner(null);
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            List<EventBanner> eventBannerList = PlaceEventBannerManager.makeEventBannerList(response);

            ((OnNetworkControllerListener) mOnNetworkControllerListener).onEventBanner(eventBannerList);
        }
    };
}
