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

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.HotelCurationOption;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StayListFragment extends PlaceListFragment
{
    protected SaleTime mCheckInSaleTime;
    protected SaleTime mCheckOutSaleTime;

    private List<EventBanner> mEventBannerList;

    private ViewType mViewType;
    protected boolean mScrollListTop;
    protected StayMainFragment.OnCommunicateListener mOnCommunicateListener;

    protected List<Stay> mStayList = new ArrayList<>();

    private StayListLayout mHotelCategoryListLayout;
    private BaseActivity mBaseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mHotelCategoryListLayout = new StayListLayout(getContext(), null);

        mViewType = ViewType.LIST;

        return mHotelCategoryListLayout.onCreateView(R.layout.fragment_hotel_list, container);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mViewType == ViewType.MAP)
        {
            if (mHotelMapFragment != null)
            {
                mHotelMapFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else
        {
            switch (requestCode)
            {
                case CODE_REQUEST_ACTIVITY_CALENDAR:
                {
                    if (resultCode == Activity.RESULT_OK && data != null)
                    {
                        mCheckInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
                        mCheckOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

                        mOnCommunicateListener.selectDay(mCheckInSaleTime, mCheckOutSaleTime, true);
                    } else
                    {
                        if (mHotelRecyclerView.getVisibility() == View.VISIBLE && mHotelRecyclerView.getAdapter() != null)
                        {
                            if (mHotelRecyclerView.getAdapter().getItemCount() == 0)
                            {
                                fetchList();
                            }
                        }
                    }
                    break;
                }
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (mViewType == ViewType.MAP)
        {
            if (mHotelMapFragment != null)
            {
                mHotelMapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void onRefreshComplete()
    {
        mOnCommunicateListener.refreshCompleted();

        mSwipeRefreshLayout.setRefreshing(false);

        if (mViewType == ViewType.MAP)
        {
            mSwipeRefreshLayout.setTag(mSwipeRefreshLayout.getId());
            mOnCommunicateListener.showFloatingActionButton();
        } else
        {
            Object objectTag = mSwipeRefreshLayout.getTag();

            if (objectTag == null)
            {
                mSwipeRefreshLayout.setTag(mSwipeRefreshLayout.getId());

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                animation.setDuration(300);
                animation.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        mSwipeRefreshLayout.setAnimation(null);
                        mOnCommunicateListener.showFloatingActionButton();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {

                    }
                });

                mSwipeRefreshLayout.startAnimation(animation);
            } else
            {
                mOnCommunicateListener.showFloatingActionButton();
            }
        }
    }

    public SaleTime getCheckInSaleTime()
    {
        return mCheckInSaleTime;
    }

    public SaleTime getCheckOutSaleTime()
    {
        return mCheckOutSaleTime;
    }

    public void setCheckInSaleTime(SaleTime saleTime)
    {
        mCheckInSaleTime = saleTime;
    }

    public void setCheckOutSaleTime(SaleTime saleTime)
    {
        mCheckOutSaleTime = saleTime;
    }

    public int getNights()
    {
        if (mCheckInSaleTime == null || mCheckOutSaleTime == null)
        {
            Util.restartApp(getContext());
            return 1;
        }

        return mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();
    }

    public void setOnCommunicateListener(StayMainFragment.OnCommunicateListener listener)
    {
        mOnCommunicateListener = listener;
    }

    public void refreshList(List<EventBanner> list)
    {
        mEventBannerList = list;

        fetchList();
    }

    protected void fetchList()
    {
        HotelCurationOption hotelCurationOption = mOnCommunicateListener.getCurationOption();
        fetchList(hotelCurationOption.getProvince(), mCheckInSaleTime, mCheckOutSaleTime);
    }

    protected void fetchList(Province province, SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (province == null || checkInSaleTime == null || checkOutSaleTime == null)
        {
            Util.restartApp(baseActivity);
            return;
        }

        lockUI();

        int nights = checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();

        if (nights <= 0)
        {
            unLockUI();
            return;
        }

        //        if (DEBUG == true && this instanceof HotelDaysListFragment)
        //        {
        //            baseActivity.showSimpleDialog(null, mSaleTime.toString() + "\n" + params, getString(R.string.dialog_btn_text_confirm), null);
        //        }

        DailyNetworkAPI.getInstance(baseActivity).requestHotelList(mNetworkTag, province, checkInSaleTime, nights, mHotelListJsonResponseListener, baseActivity);
    }

    public void setScrollListTop(boolean scrollListTop)
    {
        mScrollListTop = scrollListTop;
    }

    private ArrayList<PlaceViewItem> curationSorting(List<Stay> stayList, StayCurationOption stayCurationOption)
    {
        ArrayList<PlaceViewItem> hotelListViewItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return hotelListViewItemList;
        }

        final Location location = stayCurationOption.getLocation();

        switch (stayCurationOption.getSortType())
        {
            case DEFAULT:
                return makeSectionHotelList(stayList);

            case DISTANCE:
            {
                if (location == null)
                {
                    stayCurationOption.setSortType(SortType.DEFAULT);
                    DailyToast.showToast(getContext(), R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                    return makeSectionHotelList(stayList);
                }

                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Stay> comparator = new Comparator<Stay>()
                {
                    public int compare(Stay hotel1, Stay hotel2)
                    {
                        float[] results1 = new float[3];
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(), hotel1.latitude, hotel1.longitude, results1);
                        hotel1.distance = results1[0];

                        float[] results2 = new float[3];
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(), hotel2.latitude, hotel2.longitude, results2);
                        hotel2.distance = results2[0];

                        return Float.compare(results1[0], results2[0]);
                    }
                };

                if (stayList.size() == 1)
                {
                    Stay stay = stayList.get(0);

                    float[] results1 = new float[3];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), stay.latitude, stay.longitude, results1);
                    stay.distance = results1[0];
                } else
                {
                    Collections.sort(stayList, comparator);
                }
                break;
            }

            case LOW_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Stay> comparator = new Comparator<Stay>()
                {
                    public int compare(Stay hotel1, Stay hotel2)
                    {
                        return hotel1.averageDiscountPrice - hotel2.averageDiscountPrice;
                    }
                };

                Collections.sort(stayList, comparator);
                break;
            }

            case HIGH_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Stay> comparator = new Comparator<Stay>()
                {
                    public int compare(Stay hotel1, Stay hotel2)
                    {
                        return hotel2.averageDiscountPrice - hotel1.averageDiscountPrice;
                    }
                };

                Collections.sort(stayList, comparator);
                break;
            }

            case SATISFACTION:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Stay> comparator = new Comparator<Stay>()
                {
                    public int compare(Stay hotel1, Stay hotel2)
                    {
                        return hotel2.satisfaction - hotel1.satisfaction;
                    }
                };

                Collections.sort(stayList, comparator);
                break;
            }
        }

        for (Stay stay : stayList)
        {
            hotelListViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
        }

        return hotelListViewItemList;
    }

    private ArrayList<PlaceViewItem> makeSectionHotelList(List<Stay> stayList)
    {
        ArrayList<PlaceViewItem> hotelListViewItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return hotelListViewItemList;
        }

        String previousRegion = null;
        boolean hasDailyChoice = false;

        for (Stay stay : stayList)
        {
            String region = stay.detailRegion;

            if (Util.isTextEmpty(region) == true)
            {
                continue;
            }

            if (stay.isDailyChoice == true)
            {
                if (hasDailyChoice == false)
                {
                    hasDailyChoice = true;

                    PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getString(R.string.label_dailychoice));
                    hotelListViewItemList.add(section);
                }
            } else
            {
                if (Util.isTextEmpty(previousRegion) == true || region.equalsIgnoreCase(previousRegion) == false)
                {
                    previousRegion = region;

                    PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, region);
                    hotelListViewItemList.add(section);
                }
            }

            hotelListViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
        }

        return hotelListViewItemList;
    }

    public void curationList(ViewType viewType, Category category, StayCurationOption curationOption)
    {
        mScrollListTop = true;

        ArrayList<PlaceViewItem> placeViewItemList = curationList(mStayList, category, curationOption);
        setHotelListViewItemList(viewType, placeViewItemList, curationOption.getSortType());
    }

    private ArrayList<PlaceViewItem> curationList(List<Stay> list, Category category, StayCurationOption curationOption)
    {
        List<Stay> stayList = curationCategory(list, category);

        stayList = curationFiltering(stayList, curationOption);

        return curationSorting(stayList, curationOption);
    }

    private List<Stay> curationCategory(List<Stay> list, Category category)
    {
        List<Stay> filteredCategoryList = new ArrayList<>(list.size());

        if (category == null || Category.ALL.code.equalsIgnoreCase(category.code) == true)
        {
            filteredCategoryList.addAll(list);

            return filteredCategoryList;
        } else
        {
            for (Stay stay : list)
            {
                if (category.code.equalsIgnoreCase(stay.categoryCode) == true)
                {
                    filteredCategoryList.add(stay);
                }
            }
        }

        return filteredCategoryList;
    }

    private List<Stay> curationFiltering(List<Stay> list, StayCurationOption curationOption)
    {
        int size = list.size();
        Stay stay;

        for (int i = size - 1; i >= 0; i--)
        {
            stay = list.get(i);

            if (stay.isFiltered(curationOption) == false)
            {
                list.remove(i);
            }
        }

        return list;
    }

    private void setHotelListViewItemList(ViewType viewType, ArrayList<PlaceViewItem> hotelListViewItemList, SortType sortType)
    {
        if (mHotelAdapter == null)
        {
            Util.restartApp(getContext());
            return;
        }

        mHotelAdapter.clear();

        if (hotelListViewItemList == null || hotelListViewItemList.size() == 0)
        {
            mHotelAdapter.notifyDataSetChanged();

            setVisibility(ViewType.GONE, true);

            mOnCommunicateListener.expandedAppBar(true, true);
        } else
        {
            setVisibility(viewType, true);

            if (viewType == ViewType.MAP)
            {
                mHotelMapFragment.setOnCommunicateListener(mOnCommunicateListener);
                mHotelMapFragment.setHotelViewItemList(hotelListViewItemList, mCheckInSaleTime, mScrollListTop);

                AnalyticsManager.getInstance(getContext()).recordScreen(Screen.DAILYHOTEL_LIST_MAP);
            } else
            {
                AnalyticsManager.getInstance(getContext()).recordScreen(Screen.DAILYHOTEL_LIST);

                Map<String, String> parmas = new HashMap<>();
                HotelCurationOption hotelCurationOption = mOnCommunicateListener.getCurationOption();
                Province province = hotelCurationOption.getProvince();

                if (province instanceof Area)
                {
                    Area area = (Area) province;
                    parmas.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    parmas.put(AnalyticsManager.KeyType.DISTRICT, area.name);

                } else
                {
                    parmas.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                    parmas.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                }

                AnalyticsManager.getInstance(getContext()).recordScreen(Screen.DAILYHOTEL_LIST, parmas);
            }

            if (sortType == SortType.DEFAULT)
            {
                if (mEventBannerList != null && mEventBannerList.size() > 0)
                {
                    PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, mEventBannerList);
                    hotelListViewItemList.add(0, placeViewItem);
                }
            }

            mHotelAdapter.addAll(hotelListViewItemList, sortType);
            mHotelAdapter.notifyDataSetChanged();

            if (mScrollListTop == true)
            {
                mScrollListTop = false;
                mHotelRecyclerView.scrollToPosition(0);
            }
        }
    }
//
//    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    //
//    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
//    {
//        @Override
//        public void onClick(View view)
//        {
//            BaseActivity baseActivity = (BaseActivity) getActivity();
//
//            if (baseActivity == null)
//            {
//                return;
//            }
//
//            int position = mHotelRecyclerView.getChildAdapterPosition(view);
//
//            if (position < 0)
//            {
//                fetchList();
//                return;
//            }
//
//            PlaceViewItem placeViewItem = mHotelAdapter.getItem(position);
//
//            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
//            {
//                mOnCommunicateListener.selectHotel(placeViewItem, mCheckInSaleTime);
//            }
//        }
//    };
//
//    private View.OnClickListener mOnEventBannerItemClickListener = new View.OnClickListener()
//    {
//        @Override
//        public void onClick(View view)
//        {
//            BaseActivity baseActivity = (BaseActivity) getActivity();
//
//            if (baseActivity == null)
//            {
//                return;
//            }
//
//            Integer index = (Integer) view.getTag(view.getId());
//
//            if (index != null)
//            {
//                EventBanner eventBanner = mEventBannerList.get(index);
//
//                mOnCommunicateListener.selectEventBanner(eventBanner);
//            }
//        }
//    };
//
//    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    // Listener
//    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    private DailyHotelJsonResponseListener mHotelListJsonResponseListener = new DailyHotelJsonResponseListener()
//    {
//        @Override
//        public void onErrorResponse(VolleyError volleyError)
//        {
//
//        }
//
//        @Override
//        public void onResponse(String url, JSONObject response)
//        {
//            BaseActivity baseActivity = (BaseActivity) getActivity();
//
//            if (baseActivity == null)
//            {
//                return;
//            }
//
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
//                    mStayList.clear();
//
//                    if (length == 0)
//                    {
//                        HotelCurationOption hotelCurationOption = mOnCommunicateListener.getCurationOption();
//                        hotelCurationOption.setFiltersList(null);
//
//                        mHotelAdapter.clear();
//                        mHotelAdapter.notifyDataSetChanged();
//
//                        setVisibility(ViewType.GONE, true);
//
//                        mOnCommunicateListener.expandedAppBar(true, true);
//                    } else
//                    {
//                        String imageUrl = dataJSONObject.getString("imgUrl");
//                        int nights = dataJSONObject.getInt("lengthStay");
//
//                        ArrayList<Stay> hotelList = makeHotelList(hotelJSONArray, imageUrl, nights);
//                        HotelCurationOption hotelCurationOption = mOnCommunicateListener.getCurationOption();
//                        setFilterInformation(hotelList, hotelCurationOption);
//
//                        // 기본적으로 보관한다.
//                        mStayList.addAll(hotelList);
//
//                        ArrayList<PlaceViewItem> placeViewItemList = curationList(hotelList, hotelCurationOption);
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
//        }
//
//        /**
//         * 미리 필터 정보를 저장하여 Curation시에 사용하도록 한다.(개수 정보 노출)
//         * @param hotelList
//         * @param curationOption
//         */
//        private void setFilterInformation(ArrayList<Stay> hotelList, HotelCurationOption curationOption)
//        {
//            // 필터 정보 넣기
//            ArrayList<HotelFilters> hotelFiltersList = new ArrayList<>(hotelList.size());
//
//            HotelFilters hotelFilters;
//
//            for (Stay hotel : hotelList)
//            {
//                hotelFilters = hotel.getFilters();
//
//                if (hotelFilters != null)
//                {
//                    hotelFiltersList.add(hotelFilters);
//                }
//            }
//
//            curationOption.setFiltersList(hotelFiltersList);
//        }
//
//        private ArrayList<Stay> makeHotelList(JSONArray jsonArray, String imageUrl, int nights) throws JSONException
//        {
//            if (jsonArray == null)
//            {
//                return new ArrayList<>();
//            }
//
//            int length = jsonArray.length();
//            ArrayList<Stay> hotelList = new ArrayList<>(length);
//            JSONObject jsonObject;
//            Stay hotel;
//
//            for (int i = 0; i < length; i++)
//            {
//                jsonObject = jsonArray.getJSONObject(i);
//
//                hotel = new Stay();
//
//                if (hotel.setHotel(jsonObject, imageUrl, nights) == true)
//                {
//                    hotelList.add(hotel); // 추가.
//                }
//            }
//
//            return hotelList;
//        }
//    };
}
