/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelListFragment (호텔 목록 화면)
 * <p>
 * 어플리케이션의 가장 주가 되는 화면으로서 호텔들의 목록을 보여주는 화면이다.
 * 호텔 리스트는 따로 커스텀되어 구성되어 있으며, 액션바의 네비게이션을 이용
 * 하여 큰 지역을 분리하고 리스트뷰 헤더를 이용하여 세부 지역을 나누어 표시
 * 한다. 리스트뷰의 맨 첫 아이템은 이벤트 참여하기 버튼이 있으며, 이 버튼은
 * 서버의 이벤트 API에 따라 NEW 아이콘을 붙여주기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.HotelCurationOption;
import com.twoheart.dailyhotel.model.HotelFilters;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StayListNetworkController extends BaseNetworkController
{
    protected interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onDateTime();

        void onEventBanner();

        void onRegionList();
    }

    public StayListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public void requestStayList()
    {

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mHotelListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            //            try
            //            {
            //                int msgCode = response.getInt("msgCode");
            //
            //                if (msgCode == 100)
            //                {
            //                    JSONObject dataJSONObject = response.getJSONObject("data");
            //                    JSONArray hotelJSONArray = null;
            //
            //                    if (dataJSONObject.has("hotelSaleList") == true)
            //                    {
            //                        hotelJSONArray = dataJSONObject.getJSONArray("hotelSaleList");
            //                    }
            //
            //                    int length;
            //
            //                    if (hotelJSONArray == null)
            //                    {
            //                        length = 0;
            //                    } else
            //                    {
            //                        length = hotelJSONArray.length();
            //                    }
            //
            //                    mHotelList.clear();
            //
            //                    if (length == 0)
            //                    {
            //                        HotelCurationOption hotelCurationOption = mOnCommunicateListener.getCurationOption();
            //                        hotelCurationOption.setFiltersList(null);
            //
            //                        mStayListAdapter.clear();
            //                        mStayListAdapter.notifyDataSetChanged();
            //
            //                        setVisibility(Constants.ViewType.GONE, true);
            //
            //                        mOnCommunicateListener.expandedAppBar(true, true);
            //                    } else
            //                    {
            //                        String imageUrl = dataJSONObject.getString("imgUrl");
            //                        int nights = dataJSONObject.getInt("lengthStay");
            //
            //                        ArrayList<Stay> stayList = makeHotelList(hotelJSONArray, imageUrl, nights);
            //                        HotelCurationOption hotelCurationOption = mOnCommunicateListener.getCurationOption();
            //                        setFilterInformation(stayList, hotelCurationOption);
            //
            //                        // 기본적으로 보관한다.
            //                        mHotelList.addAll(stayList);
            //
            //                        ArrayList<PlaceViewItem> placeViewItemList = curationList(stayList, hotelCurationOption);
            //
            //                        setHotelListViewItemList(mViewType, placeViewItemList, hotelCurationOption.getSortType());
            //                    }
            //
            //                    // 리스트 요청 완료후에 날짜 탭은 애니매이션을 진행하도록 한다.
            //                    onRefreshComplete();
            //                } else
            //                {
            //                    String message = response.getString("msg");
            //                    onErrorPopupMessage(msgCode, message);
            //                }
            //            } catch (Exception e)
            //            {
            //                onError(e);
            //            } finally
            //            {
            //                unLockUI();
            //            }
        }

        /**
         * 미리 필터 정보를 저장하여 Curation시에 사용하도록 한다.(개수 정보 노출)
         * @param stayList
         * @param curationOption
         */
        private void setFilterInformation(ArrayList<Stay> stayList, HotelCurationOption curationOption)
        {
            // 필터 정보 넣기
            ArrayList<HotelFilters> hotelFiltersList = new ArrayList<>(stayList.size());

            HotelFilters hotelFilters;

            for (Stay stay : stayList)
            {
                hotelFilters = stay.getFilters();

                if (hotelFilters != null)
                {
                    hotelFiltersList.add(hotelFilters);
                }
            }

            curationOption.setFiltersList(hotelFiltersList);
        }

        private ArrayList<Stay> makeHotelList(JSONArray jsonArray, String imageUrl, int nights) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            int length = jsonArray.length();
            ArrayList<Stay> stayList = new ArrayList<>(length);
            JSONObject jsonObject;
            Stay stay;

            for (int i = 0; i < length; i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                stay = new Stay();

                if (stay.setStay(jsonObject, imageUrl, nights) == true)
                {
                    stayList.add(stay); // 추가.
                }
            }

            return stayList;
        }
    };
}
