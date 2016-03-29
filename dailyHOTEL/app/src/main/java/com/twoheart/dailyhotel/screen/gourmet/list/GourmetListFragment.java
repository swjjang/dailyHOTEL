package com.twoheart.dailyhotel.screen.gourmet.list;

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
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetFilters;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.PinnedSectionRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GourmetListFragment extends BaseFragment implements Constants
{
    private static final int APPBARLAYOUT_DRAG_DISTANCE = 200;

    protected PinnedSectionRecyclerView mGourmetRecyclerView;
    protected GourmetListAdapter mGourmetAdapter;
    protected SaleTime mSaleTime;

    private View mEmptyView;
    private ViewGroup mMapLayout;
    private GourmetMapFragment mGourmetMapFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<EventBanner> mEventBannerList;

    private ViewType mViewType;
    protected boolean mScrollListTop;
    protected GourmetMainFragment.OnCommunicateListener mOnCommunicateListener;

    private int mDownDistance;
    private int mUpDistance;

    protected List<Gourmet> mGourmetList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gourmet_list, container, false);

        mGourmetRecyclerView = (PinnedSectionRecyclerView) view.findViewById(R.id.recycleView);
        mGourmetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGourmetRecyclerView.setTag("GourmetListFragment");

        BaseActivity baseActivity = (BaseActivity) getActivity();

        mGourmetAdapter = new GourmetListAdapter(baseActivity, new ArrayList<PlaceViewItem>(), mOnItemClickListener, mOnEventBannerItemClickListener);
        mGourmetRecyclerView.setAdapter(mGourmetAdapter);
        mGourmetRecyclerView.setOnScrollListener(mOnScrollListener);

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

        mGourmetRecyclerView.setShadowVisible(false);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mViewType == ViewType.MAP)
        {
            if (mGourmetMapFragment != null)
            {
                mGourmetMapFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (mViewType == ViewType.MAP)
        {
            if (mGourmetMapFragment != null)
            {
                mGourmetMapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

                if (mGourmetMapFragment != null)
                {
                    getChildFragmentManager().beginTransaction().remove(mGourmetMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mGourmetMapFragment = null;
                }

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mViewType = ViewType.MAP;

                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);

                if (isCurrentPage == true && mGourmetMapFragment == null)
                {
                    mGourmetMapFragment = new GourmetMapFragment();
                    getChildFragmentManager().beginTransaction().add(mMapLayout.getId(), mGourmetMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.DAILYGOURMET_LIST_EMPTY, null);

                mEmptyView.setVisibility(View.VISIBLE);
                mMapLayout.setVisibility(View.GONE);

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;
        }
    }

    protected SaleTime getSelectedSaleTime()
    {
        return mSaleTime;
    }

    protected SaleTime getSaleTime()
    {
        return mSaleTime;
    }

    public void setSaleTime(SaleTime saleTime)
    {
        mSaleTime = saleTime;
    }

    public void setOnCommunicateListener(GourmetMainFragment.OnCommunicateListener listener)
    {
        mOnCommunicateListener = listener;
    }

    public boolean isShowInformationAtMapView()
    {
        if (mViewType == ViewType.MAP && mGourmetMapFragment != null)
        {
            return mGourmetMapFragment.isShowInformation();
        }

        return false;
    }

    public void refreshList()
    {
        DailyNetworkAPI.getInstance().requestEventBannerList(mNetworkTag, "gourmet", mEventBannerListJsonResponseListener, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                fetchList();
            }
        });
    }

    public void fetchList()
    {
        GourmetCurationOption gourmetCurationOption = mOnCommunicateListener.getCurationOption();
        fetchList(gourmetCurationOption.getProvince(), getSelectedSaleTime(), null);
    }

    public void fetchList(Province province, SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (province == null || checkInSaleTime == null)
        {
            Util.restartApp(baseActivity);
            return;
        }

        lockUI();

        //        if (DEBUG == true && this instanceof GourmetDaysListFragment)
        //        {
        //            baseActivity.showSimpleDialog(null, mSaleTime.toString() + "\n" + params, getString(R.string.dialog_btn_text_confirm), null);
        //        }

        DailyNetworkAPI.getInstance().requestGourmetList(mNetworkTag, province, checkInSaleTime, mGourmetListJsonResponseListener, baseActivity);
    }

    public void setScrollListTop(boolean scrollListTop)
    {
        mScrollListTop = scrollListTop;
    }

    private ArrayList<PlaceViewItem> curationSorting(List<Gourmet> gourmetList, GourmetCurationOption gourmetCurationOption)
    {
        ArrayList<PlaceViewItem> gourmetViewItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
        {
            return gourmetViewItemList;
        }

        final Location location = gourmetCurationOption.getLocation();

        switch (gourmetCurationOption.getSortType())
        {
            case DEFAULT:
                return makeSectionList(gourmetList);

            case DISTANCE:
            {
                if (location == null)
                {
                    gourmetCurationOption.setSortType(SortType.DEFAULT);
                    DailyToast.showToast(getContext(), R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                    return makeSectionList(gourmetList);
                }

                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Gourmet> comparator = new Comparator<Gourmet>()
                {
                    public int compare(Gourmet gourmet1, Gourmet gourmet2)
                    {
                        float[] results1 = new float[3];
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(), gourmet1.latitude, gourmet1.longitude, results1);
                        gourmet1.distance = results1[0];

                        float[] results2 = new float[3];
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(), gourmet2.latitude, gourmet2.longitude, results2);
                        gourmet2.distance = results2[0];

                        return Float.compare(results1[0], results2[0]);
                    }
                };

                if (gourmetList.size() == 1)
                {
                    Gourmet gourmet = gourmetList.get(0);

                    float[] results1 = new float[3];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), gourmet.latitude, gourmet.longitude, results1);
                    gourmet.distance = results1[0];
                } else
                {
                    Collections.sort(gourmetList, comparator);
                }
                break;
            }

            case LOW_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Gourmet> comparator = new Comparator<Gourmet>()
                {
                    public int compare(Gourmet gourmet1, Gourmet gourmet2)
                    {
                        return gourmet1.discountPrice - gourmet2.discountPrice;
                    }
                };

                Collections.sort(gourmetList, comparator);
                break;
            }

            case HIGH_PRICE:
            {
                // 중복된 위치에 있는 호텔들은 위해서 소팅한다.
                Comparator<Gourmet> comparator = new Comparator<Gourmet>()
                {
                    public int compare(Gourmet gourmet1, Gourmet gourmet2)
                    {
                        return gourmet2.discountPrice - gourmet1.discountPrice;
                    }
                };

                Collections.sort(gourmetList, comparator);
                break;
            }
        }

        for (Gourmet gourmet : gourmetList)
        {
            gourmetViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
        }

        return gourmetViewItemList;
    }

    private ArrayList<PlaceViewItem> makeSectionList(List<Gourmet> gourmetList)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
        {
            return placeViewItemList;
        }

        String previousRegion = null;
        boolean hasDailyChoice = false;

        for (Gourmet gourmet : gourmetList)
        {
            String region = gourmet.districtName;

            if (Util.isTextEmpty(region) == true)
            {
                continue;
            }

            if (gourmet.isDailyChoice == true)
            {
                if (hasDailyChoice == false)
                {
                    hasDailyChoice = true;

                    PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, getString(R.string.label_dailychoice));
                    placeViewItemList.add(section);
                }
            } else
            {
                if (Util.isTextEmpty(previousRegion) == true || region.equalsIgnoreCase(previousRegion) == false)
                {
                    previousRegion = region;

                    PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, region);
                    placeViewItemList.add(section);
                }
            }

            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
        }

        return placeViewItemList;
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

    public void curationList(ViewType type, GourmetCurationOption curationOption)
    {
        mScrollListTop = true;

        ArrayList<PlaceViewItem> placeViewItemList = curationList(mGourmetList, curationOption);
        setGourmetListViewItemList(type, placeViewItemList, curationOption.getSortType());
    }

    private ArrayList<PlaceViewItem> curationList(List<Gourmet> list, GourmetCurationOption curationOption)
    {
        List<Gourmet> gourmetList = curationCategory(list, curationOption.getFilterMap());

        gourmetList = curationFiltering(gourmetList, curationOption);

        return curationSorting(gourmetList, curationOption);
    }

    private List<Gourmet> curationCategory(List<Gourmet> list, Map<String, Integer> categoryMap)
    {
        List<Gourmet> filteredCategoryList = new ArrayList<>(list.size());

        if (categoryMap == null || categoryMap.size() == 0)
        {
            filteredCategoryList.addAll(list);

            return filteredCategoryList;
        } else
        {
            for (Gourmet gourmet : list)
            {
                if (categoryMap.containsKey(gourmet.category) == true)
                {
                    filteredCategoryList.add(gourmet);
                }
            }
        }

        return filteredCategoryList;
    }

    private List<Gourmet> curationFiltering(List<Gourmet> list, GourmetCurationOption curationOption)
    {
        int size = list.size();
        Gourmet gourmet;

        for (int i = size - 1; i >= 0; i--)
        {
            gourmet = list.get(i);

            if (gourmet.isFiltered(curationOption) == false)
            {
                list.remove(i);
            }
        }

        return list;
    }

    private void setGourmetListViewItemList(ViewType viewType, ArrayList<PlaceViewItem> gourmetListViewItemList, SortType sortType)
    {
        mGourmetAdapter.clear();

        if (gourmetListViewItemList == null || gourmetListViewItemList.size() == 0)
        {
            mGourmetAdapter.notifyDataSetChanged();

            setVisibility(ViewType.GONE, true);

            mOnCommunicateListener.expandedAppBar(true, true);
        } else
        {
            setVisibility(viewType, true);

            if (viewType == ViewType.MAP)
            {
                if (hasSalesPlace(gourmetListViewItemList) == false)
                {
                    unLockUI();

                    BaseActivity baseActivity = (BaseActivity) getActivity();

                    if (baseActivity == null)
                    {
                        return;
                    }

                    DailyToast.showToast(baseActivity, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);

                    mOnCommunicateListener.toggleViewType();
                    return;
                }

                mGourmetMapFragment.setOnCommunicateListener(mOnCommunicateListener);
                mGourmetMapFragment.setPlaceViewItemList(gourmetListViewItemList, getSelectedSaleTime(), mScrollListTop);

                AnalyticsManager.getInstance(getContext()).recordScreen(Screen.DAILYGOURMET_LIST_MAP, null);
            } else
            {
                AnalyticsManager.getInstance(getContext()).recordScreen(Screen.DAILYGOURMET_LIST, null);
            }

            if (sortType == SortType.DEFAULT)
            {
                if (mEventBannerList != null && mEventBannerList.size() > 0)
                {
                    PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, mEventBannerList);
                    gourmetListViewItemList.add(0, placeViewItem);
                }
            }

            mGourmetAdapter.addAll(gourmetListViewItemList, sortType);
            mGourmetAdapter.notifyDataSetChanged();

            if (mScrollListTop == true)
            {
                mScrollListTop = false;
                mGourmetRecyclerView.scrollToPosition(0);
            }
        }
    }

    public boolean hasSalesPlace()
    {
        return hasSalesPlace(mGourmetAdapter.getAll());
    }

    private boolean hasSalesPlace(List<PlaceViewItem> gourmetListViewItemList)
    {
        boolean hasPlace = false;

        List<PlaceViewItem> arrayList = gourmetListViewItemList;

        if (arrayList != null)
        {
            for (PlaceViewItem placeViewItem : arrayList)
            {
                if (placeViewItem.getType() == PlaceViewItem.TYPE_ENTRY//
                    && placeViewItem.<Gourmet>getItem().isSoldOut == false)
                {
                    hasPlace = true;
                    break;
                }
            }
        }

        return hasPlace;
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

            int position = mGourmetRecyclerView.getChildAdapterPosition(view);

            if (position < 0)
            {
                refreshList();
                return;
            }

            PlaceViewItem gourmetViewItem = mGourmetAdapter.getItem(position);

            if (gourmetViewItem.getType() == PlaceViewItem.TYPE_ENTRY)
            {
                mOnCommunicateListener.selectPlace(gourmetViewItem, getSelectedSaleTime());
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
                EventBanner eventBanner = mEventBannerList.get(index.intValue());

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

    private DailyHotelJsonResponseListener mGourmetListJsonResponseListener = new DailyHotelJsonResponseListener()
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
                    JSONArray gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSaleList");

                    int length;

                    if (gourmetJSONArray == null)
                    {
                        length = 0;
                    } else
                    {
                        length = gourmetJSONArray.length();
                    }

                    mGourmetList.clear();

                    if (length == 0)
                    {
                        GourmetCurationOption gourmetCurationOption = mOnCommunicateListener.getCurationOption();
                        gourmetCurationOption.setFiltersList(null);

                        mGourmetAdapter.clear();
                        mGourmetAdapter.notifyDataSetChanged();

                        setVisibility(ViewType.GONE, true);

                        mOnCommunicateListener.expandedAppBar(true, true);
                    } else
                    {
                        ArrayList<Gourmet> gourmetList = makeGourmetList(gourmetJSONArray, imageUrl);
                        GourmetCurationOption gourmetCurationOption = mOnCommunicateListener.getCurationOption();
                        setFilterInformation(gourmetList, gourmetCurationOption);

                        // 기본적으로 보관한다.
                        mGourmetList.addAll(gourmetList);

                        ArrayList<PlaceViewItem> placeViewItemList = curationList(gourmetList, gourmetCurationOption);

                        setGourmetListViewItemList(mViewType, placeViewItemList, gourmetCurationOption.getSortType());
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

        private void setFilterInformation(ArrayList<Gourmet> gourmetList, GourmetCurationOption curationOption)
        {
            HashMap<String, Integer> categoryCodeMap = new HashMap<>(12);
            HashMap<String, Integer> categorySequenceMap = new HashMap<>(12);

            ArrayList<GourmetFilters> gourmetFiltersList = new ArrayList<>(gourmetList.size());

            GourmetFilters gourmetFilters;

            // 필터 정보 넣기
            for (Gourmet gourmet : gourmetList)
            {
                gourmetFilters = gourmet.getFilters();

                if (gourmetFilters != null)
                {
                    gourmetFiltersList.add(gourmetFilters);
                }

                categoryCodeMap.put(gourmet.category, gourmet.categoryCode);
                categorySequenceMap.put(gourmet.category, gourmet.categorySequence);
            }

            curationOption.setFiltersList(gourmetFiltersList);
            curationOption.setCategoryCoderMap(categoryCodeMap);
            curationOption.setCategorySequenceMap(categorySequenceMap);
        }

        private ArrayList<Gourmet> makeGourmetList(JSONArray jsonArray, String imageUrl) throws JSONException
        {
            if (jsonArray == null)
            {
                return new ArrayList<>();
            }

            int length = jsonArray.length();
            ArrayList<Gourmet> gourmetList = new ArrayList<>(length);
            JSONObject jsonObject;
            Gourmet gouremt;

            for (int i = 0; i < length; i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                gouremt = new Gourmet();

                if (gouremt.setData(jsonObject, imageUrl) == true)
                {
                    gourmetList.add(gouremt); // 추가.
                }
            }

            return gourmetList;
        }
    };
}
