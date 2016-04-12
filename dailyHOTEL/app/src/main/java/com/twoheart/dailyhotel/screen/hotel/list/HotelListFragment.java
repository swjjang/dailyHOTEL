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

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelCurationOption;
import com.twoheart.dailyhotel.model.HotelFilters;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HotelListFragment extends BaseFragment implements Constants
{
    private static final int APPBARLAYOUT_DRAG_DISTANCE = 200;

    protected PinnedSectionRecyclerView mHotelRecyclerView;
    protected HotelListAdapter mHotelAdapter;

    protected SaleTime mCheckInSaleTime;
    protected SaleTime mCheckOutSaleTime;

    private View mEmptyView;
    private ViewGroup mMapLayout;
    private HotelMapFragment mHotelMapFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<EventBanner> mEventBannerList;

    private ViewType mViewType;
    protected boolean mScrollListTop;
    protected HotelMainFragment.OnCommunicateListener mOnCommunicateListener;

    private int mDownDistance;
    private int mUpDistance;

    protected List<Hotel> mHotelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hotel_list, container, false);

        mHotelRecyclerView = (PinnedSectionRecyclerView) view.findViewById(R.id.recycleView);
        mHotelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHotelRecyclerView.setTag("HotelListFragment");
        EdgeEffectColor.setEdgeGlowColor(mHotelRecyclerView, getResources().getColor(R.color.over_scroll_edge));

        BaseActivity baseActivity = (BaseActivity) getActivity();

        mHotelAdapter = new HotelListAdapter(baseActivity, new ArrayList<PlaceViewItem>(), mOnItemClickListener, mOnEventBannerItemClickListener);
        mHotelRecyclerView.setAdapter(mHotelAdapter);
        mHotelRecyclerView.setOnScrollListener(mOnScrollListener);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                mOnCommunicateListener.showAppBarLayout();
                mOnCommunicateListener.expandedAppBar(true, true);
                mOnCommunicateListener.refreshAll(false);
            }
        });

        mEmptyView = view.findViewById(R.id.emptyView);

        mMapLayout = (ViewGroup) view.findViewById(R.id.mapLayout);

        mViewType = ViewType.LIST;

        setVisibility(mViewType, true);

        mHotelRecyclerView.setShadowVisible(false);

        return view;
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

    public boolean canScrollUp()
    {
        if (mSwipeRefreshLayout != null)
        {
            return mSwipeRefreshLayout.canChildScrollUp();
        }

        return true;
    }

    public void onPageSelected()
    {
    }

    public void onPageUnSelected()
    {
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

    protected void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
        switch (viewType)
        {
            case LIST:
                mViewType = ViewType.LIST;

                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);

                if (mHotelMapFragment != null)
                {
                    getChildFragmentManager().beginTransaction().remove(mHotelMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mHotelMapFragment = null;
                }

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mViewType = ViewType.MAP;

                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);

                if (isCurrentPage == true && mHotelMapFragment == null)
                {
                    mHotelMapFragment = new HotelMapFragment();
                    getChildFragmentManager().beginTransaction().add(mMapLayout.getId(), mHotelMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.DAILYHOTEL_LIST_EMPTY, null);

                mEmptyView.setVisibility(View.VISIBLE);
                mMapLayout.setVisibility(View.GONE);

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;
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
        return mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();
    }

    public void setOnCommunicateListener(HotelMainFragment.OnCommunicateListener listener)
    {
        mOnCommunicateListener = listener;
    }

    public boolean isShowInformationAtMapView()
    {
        if (mViewType == ViewType.MAP && mHotelMapFragment != null)
        {
            return mHotelMapFragment.isShowInformation();
        }

        return false;
    }

    public void refreshList()
    {
        DailyNetworkAPI.getInstance().requestEventBannerList(mNetworkTag, "hotel", mEventBannerListJsonResponseListener, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                fetchList();
            }
        });
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

        DailyNetworkAPI.getInstance().requestHotelList(mNetworkTag, province, checkInSaleTime, nights, mHotelListJsonResponseListener, baseActivity);
    }

    public void setScrollListTop(boolean scrollListTop)
    {
        mScrollListTop = scrollListTop;
    }

    private ArrayList<PlaceViewItem> curationSorting(List<Hotel> hotelList, HotelCurationOption hotelCurationOption)
    {
        ArrayList<PlaceViewItem> hotelListViewItemList = new ArrayList<>();

        if (hotelList == null || hotelList.size() == 0)
        {
            return hotelListViewItemList;
        }

        final Location location = hotelCurationOption.getLocation();

        switch (hotelCurationOption.getSortType())
        {
            case DEFAULT:
                return makeSectionHotelList(hotelList);

            case DISTANCE:
            {
                if (location == null)
                {
                    hotelCurationOption.setSortType(SortType.DEFAULT);
                    DailyToast.showToast(getContext(), R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                    return makeSectionHotelList(hotelList);
                }

                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Hotel> comparator = new Comparator<Hotel>()
                {
                    public int compare(Hotel hotel1, Hotel hotel2)
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

                if (hotelList.size() == 1)
                {
                    Hotel hotel = hotelList.get(0);

                    float[] results1 = new float[3];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), hotel.latitude, hotel.longitude, results1);
                    hotel.distance = results1[0];
                } else
                {
                    Collections.sort(hotelList, comparator);
                }
                break;
            }

            case LOW_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Hotel> comparator = new Comparator<Hotel>()
                {
                    public int compare(Hotel hotel1, Hotel hotel2)
                    {
                        return hotel1.averageDiscountPrice - hotel2.averageDiscountPrice;
                    }
                };

                Collections.sort(hotelList, comparator);
                break;
            }

            case HIGH_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Hotel> comparator = new Comparator<Hotel>()
                {
                    public int compare(Hotel hotel1, Hotel hotel2)
                    {
                        return hotel2.averageDiscountPrice - hotel1.averageDiscountPrice;
                    }
                };

                Collections.sort(hotelList, comparator);
                break;
            }

            case SATISFACTION:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Hotel> comparator = new Comparator<Hotel>()
                {
                    public int compare(Hotel hotel1, Hotel hotel2)
                    {
                        return hotel2.satisfaction - hotel1.satisfaction;
                    }
                };

                Collections.sort(hotelList, comparator);
                break;
            }
        }

        for (Hotel hotel : hotelList)
        {
            hotelListViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, hotel));
        }

        return hotelListViewItemList;
    }

    private ArrayList<PlaceViewItem> makeSectionHotelList(List<Hotel> hotelList)
    {
        ArrayList<PlaceViewItem> hotelListViewItemList = new ArrayList<>();

        if (hotelList == null || hotelList.size() == 0)
        {
            return hotelListViewItemList;
        }

        String previousRegion = null;
        boolean hasDailyChoice = false;

        for (Hotel hotel : hotelList)
        {
            String region = hotel.detailRegion;

            if (Util.isTextEmpty(region) == true)
            {
                continue;
            }

            if (hotel.isDailyChoice == true)
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

            hotelListViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, hotel));
        }

        return hotelListViewItemList;
    }

    public void resetScrollDistance(boolean isUpDistance)
    {
        if (isUpDistance == true)
        {
            mDownDistance = 1;
            mUpDistance = 0;
        } else
        {
            mUpDistance = -1;
            mDownDistance = 0;
        }
    }

    public void curationList(ViewType viewType, HotelCurationOption curationOption)
    {
        mScrollListTop = true;

        ArrayList<PlaceViewItem> placeViewItemList = curationList(mHotelList, curationOption);
        setHotelListViewItemList(viewType, placeViewItemList, curationOption.getSortType());
    }

    private ArrayList<PlaceViewItem> curationList(List<Hotel> list, HotelCurationOption curationOption)
    {
        List<Hotel> hotelList = curationCategory(list, curationOption.getCategory());

        hotelList = curationFiltering(hotelList, curationOption);

        return curationSorting(hotelList, curationOption);
    }

    private List<Hotel> curationCategory(List<Hotel> list, Category category)
    {
        List<Hotel> filteredCategoryList = new ArrayList<>(list.size());

        if (category == null || Category.ALL.code.equalsIgnoreCase(category.code) == true)
        {
            filteredCategoryList.addAll(list);

            return filteredCategoryList;
        } else
        {
            for (Hotel hotel : list)
            {
                if (category.code.equalsIgnoreCase(hotel.categoryCode) == true)
                {
                    filteredCategoryList.add(hotel);
                }
            }
        }

        return filteredCategoryList;
    }

    private List<Hotel> curationFiltering(List<Hotel> list, HotelCurationOption curationOption)
    {
        int size = list.size();
        Hotel hotel;

        for (int i = size - 1; i >= 0; i--)
        {
            hotel = list.get(i);

            if (hotel.isFiltered(curationOption) == false)
            {
                list.remove(i);
            }
        }

        return list;
    }

    private void setHotelListViewItemList(ViewType viewType, ArrayList<PlaceViewItem> hotelListViewItemList, SortType sortType)
    {
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

                AnalyticsManager.getInstance(getContext()).recordScreen(Screen.DAILYHOTEL_LIST_MAP, null);
            } else
            {
                AnalyticsManager.getInstance(getContext()).recordScreen(Screen.DAILYHOTEL_LIST, null);
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            super.onScrolled(recyclerView, dx, dy);

            if (dy < 0)
            {
                if (mDownDistance == 1)
                {
                    return;
                }

                mDownDistance += dy;

                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (-mDownDistance >= Util.dpToPx(baseActivity, APPBARLAYOUT_DRAG_DISTANCE))
                {
                    mUpDistance = 0;
                    mDownDistance = 1;
                    mOnCommunicateListener.showAppBarLayout();
                }
            } else if (dy > 0)
            {
                if (mUpDistance == -1)
                {
                    return;
                }

                mUpDistance += dy;

                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (mUpDistance >= Util.dpToPx(baseActivity, APPBARLAYOUT_DRAG_DISTANCE))
                {
                    mDownDistance = 0;
                    mUpDistance = -1;
                    mOnCommunicateListener.hideAppBarLayout();
                    mOnCommunicateListener.expandedAppBar(false, true);
                }
            }
        }
    };

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            int position = mHotelRecyclerView.getChildAdapterPosition(view);

            if (position < 0)
            {
                refreshList();
                return;
            }

            PlaceViewItem placeViewItem = mHotelAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                mOnCommunicateListener.selectHotel(placeViewItem, mCheckInSaleTime);
            }
        }
    };

    private View.OnClickListener mOnEventBannerItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            Integer index = (Integer) view.getTag(view.getId());

            if (index != null)
            {
                EventBanner eventBanner = mEventBannerList.get(index);

                mOnCommunicateListener.selectEventBanner(eventBanner);
            }
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mEventBannerListJsonResponseListener = new DailyHotelJsonResponseListener()
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

                    String baseUrl = dataJSONObject.getString("imgUrl");

                    JSONArray jsonArray = dataJSONObject.getJSONArray("eventBanner");

                    if (mEventBannerList == null)
                    {
                        mEventBannerList = new ArrayList<>();
                    }

                    mEventBannerList.clear();

                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++)
                    {
                        try
                        {
                            EventBanner eventBanner = new EventBanner(jsonArray.getJSONObject(i), baseUrl);
                            mEventBannerList.add(eventBanner);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            } finally
            {
                fetchList();
            }
        }
    };

    private DailyHotelJsonResponseListener mHotelListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");

                    String imageUrl = dataJSONObject.getString("imgUrl");
                    int nights = dataJSONObject.getInt("lengthStay");
                    JSONArray hotelJSONArray = dataJSONObject.getJSONArray("hotelSaleList");

                    int length;

                    if (hotelJSONArray == null)
                    {
                        length = 0;
                    } else
                    {
                        length = hotelJSONArray.length();
                    }

                    mHotelList.clear();

                    if (length == 0)
                    {
                        HotelCurationOption hotelCurationOption = mOnCommunicateListener.getCurationOption();
                        hotelCurationOption.setFiltersList(null);

                        mHotelAdapter.clear();
                        mHotelAdapter.notifyDataSetChanged();

                        setVisibility(ViewType.GONE, true);

                        mOnCommunicateListener.expandedAppBar(true, true);
                    } else
                    {
                        ArrayList<Hotel> hotelList = makeHotelList(hotelJSONArray, imageUrl, nights);
                        HotelCurationOption hotelCurationOption = mOnCommunicateListener.getCurationOption();
                        setFilterInformation(hotelList, hotelCurationOption);

                        // 기본적으로 보관한다.
                        mHotelList.addAll(hotelList);

                        ArrayList<PlaceViewItem> placeViewItemList = curationList(hotelList, hotelCurationOption);

                        setHotelListViewItemList(mViewType, placeViewItemList, hotelCurationOption.getSortType());
                    }

                    // 리스트 요청 완료후에 날짜 탭은 애니매이션을 진행하도록 한다.
                    onRefreshComplete();
                } else
                {
                    String message = response.getString("msg");
                    onErrorMessage(msgCode, message);
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        private void setFilterInformation(ArrayList<Hotel> hotelList, HotelCurationOption curationOption)
        {
            // 필터 정보 넣기
            ArrayList<HotelFilters> hotelFiltersList = new ArrayList<>(hotelList.size());

            HotelFilters hotelFilters;

            for (Hotel hotel : hotelList)
            {
                hotelFilters = hotel.getFilters();

                if (hotelFilters != null)
                {
                    hotelFiltersList.add(hotelFilters);
                }
            }

            curationOption.setFiltersList(hotelFiltersList);
        }

        private ArrayList<Hotel> makeHotelList(JSONArray jsonArray, String imageUrl, int nights) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            int length = jsonArray.length();
            ArrayList<Hotel> hotelList = new ArrayList<>(length);
            JSONObject jsonObject;
            Hotel hotel;

            for (int i = 0; i < length; i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                hotel = new Hotel();

                if (hotel.setHotel(jsonObject, imageUrl, nights) == true)
                {
                    hotelList.add(hotel); // 추가.
                }
            }

            return hotelList;
        }
    };
}
