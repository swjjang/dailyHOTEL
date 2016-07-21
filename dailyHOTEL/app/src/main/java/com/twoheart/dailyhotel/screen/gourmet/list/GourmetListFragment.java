package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetFilters;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GourmetListFragment extends PlaceListFragment
{
    protected BaseActivity mBaseActivity;

    protected List<Gourmet> mGourmetList = new ArrayList<>();
    private GourmetListLayout mGourmetListLayout;

    private GourmetCuration mGourmetCuration;

    public interface OnGourmetListFragmentListener extends OnPlaceListFragmentListener
    {
        void onGourmetClick(PlaceViewItem placeViewItem, SaleTime saleTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mGourmetListLayout = new GourmetListLayout(mBaseActivity, mOnEventListener);
        mGourmetListLayout.setBottomOptionLayout(mBottomOptionLayout);

        mViewType = ViewType.LIST;

        return mGourmetListLayout.onCreateView(R.layout.fragment_gourmet_list, container);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mViewType == ViewType.MAP)
        {
            PlaceListMapFragment placeListMapFragment = mGourmetListLayout.getListMapFragment();

            if (placeListMapFragment != null)
            {
                placeListMapFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void setPlaceCuration(PlaceCuration curation)
    {
        mGourmetCuration = (GourmetCuration) curation;
        mGourmetListLayout.setGourmetCuration(mGourmetCuration);
    }

    @Override
    public void clearList()
    {
        mGourmetListLayout.clearList();
    }

    @Override
    public void refreshList(boolean isShowProgress)
    {
        if (mGourmetCuration.getSaleTime() == null || mGourmetCuration.getProvince() == null)
        {
            Util.restartApp(mBaseActivity);
            return;
        }

        lockUI(isShowProgress);

        DailyNetworkAPI.getInstance(mBaseActivity).requestGourmetList(mNetworkTag, //
            mGourmetCuration.getProvince(), //
            mGourmetCuration.getSaleTime(), //
            mGourmetListJsonResponseListener, mBaseActivity);
    }

    public boolean hasSalesPlace()
    {
        return mGourmetListLayout.hasSalesPlace();
    }

    public void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
        mViewType = viewType;
        mGourmetListLayout.setVisibility(getChildFragmentManager(), viewType, isCurrentPage);

        mOnPlaceListFragmentListener.onShowMenuBar();
    }

    @Override
    public void setScrollListTop()
    {
        if (mGourmetListLayout == null)
        {
            return;
        }

        mGourmetListLayout.setScrollListTop();
    }

    private ArrayList<PlaceViewItem> curationSorting(List<Gourmet> gourmetList, GourmetCurationOption gourmetCurationOption)
    {
        ArrayList<PlaceViewItem> gourmetViewItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
        {
            return gourmetViewItemList;
        }

        final Location location = mGourmetCuration.getLocation();

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
                        int price1 = gourmet1.persons > 1 ? gourmet1.discountPrice / gourmet1.persons : gourmet1.discountPrice;
                        int price2 = gourmet2.persons > 1 ? gourmet2.discountPrice / gourmet2.persons : gourmet2.discountPrice;

                        return price1 - price2;
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
                        int price1 = gourmet1.persons > 1 ? gourmet1.discountPrice / gourmet1.persons : gourmet1.discountPrice;
                        int price2 = gourmet2.persons > 1 ? gourmet2.discountPrice / gourmet2.persons : gourmet2.discountPrice;

                        return price2 - price1;
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

    public void curationList(ViewType type, GourmetCurationOption curationOption)
    {
        ArrayList<PlaceViewItem> placeViewItemList = curationList(mGourmetList, curationOption);

        PlaceViewItem placeViewFooterItem = new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null);
        placeViewItemList.add(placeViewItemList.size(), placeViewFooterItem);

        mGourmetListLayout.setList(getChildFragmentManager(), type, placeViewItemList, curationOption.getSortType());
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetListLayout.OnEventListener mOnEventListener = new GourmetListLayout.OnEventListener()
    {
        @Override
        public void onPlaceClick(PlaceViewItem placeViewItem)
        {
            ((OnGourmetListFragmentListener) mOnPlaceListFragmentListener).onGourmetClick(placeViewItem, mGourmetCuration.getSaleTime());
        }

        @Override
        public void onEventBannerClick(EventBanner eventBanner)
        {
            mOnPlaceListFragmentListener.onEventBannerClick(eventBanner);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            mOnPlaceListFragmentListener.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            mOnPlaceListFragmentListener.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onRefreshAll(boolean isShowProgress)
        {
            setPlaceCuration(mGourmetCuration);
            refreshList(isShowProgress);

            mOnPlaceListFragmentListener.onShowMenuBar();
        }

        @Override
        public void onLoadMoreList()
        {
            // do nothing.
        }

        @Override
        public void onFilterClick()
        {
            mOnPlaceListFragmentListener.onFilterClick();
        }

        @Override
        public void finish()
        {
            mBaseActivity.finish();
        }
    };

    private DailyHotelJsonResponseListener mGourmetListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
                    JSONArray gourmetJSONArray = null;

                    if (dataJSONObject.has("gourmetSaleList") == true)
                    {
                        gourmetJSONArray = dataJSONObject.getJSONArray("gourmetSaleList");
                    }

                    int length;

                    if (gourmetJSONArray == null)
                    {
                        length = 0;
                    } else
                    {
                        length = gourmetJSONArray.length();
                    }

                    mGourmetList.clear();

                    GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

                    if (length == 0)
                    {
                        gourmetCurationOption.setFiltersList(null);

                        mGourmetListLayout.setList(getChildFragmentManager(), mViewType, null, gourmetCurationOption.getSortType());

                        setVisibility(ViewType.GONE, true);
                    } else
                    {
                        String imageUrl = dataJSONObject.getString("imgUrl");

                        ArrayList<Gourmet> gourmetList = makeGourmetList(gourmetJSONArray, imageUrl);
                        setFilterInformation(gourmetList, gourmetCurationOption);

                        // 기본적으로 보관한다.
                        mGourmetList.addAll(gourmetList);

                        ArrayList<PlaceViewItem> placeViewItemList = curationList(gourmetList, gourmetCurationOption);

                        PlaceViewItem placeViewFooterItem = new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null);
                        placeViewItemList.add(placeViewItemList.size(), placeViewFooterItem);

                        mGourmetListLayout.setList(getChildFragmentManager(), mViewType, placeViewItemList, gourmetCurationOption.getSortType());
                    }
                } else
                {
                    String message = response.getString("msg");

                    MainActivity mainActivity = (MainActivity) getActivity();

                    if (mainActivity != null && mainActivity.isFinishing() == false)
                    {
                        mainActivity.onRuntimeError("msgCode : " + msgCode + ", msg : " + message);
                    }
                }
            } catch (Exception e)
            {
                MainActivity mainActivity = (MainActivity) getActivity();

                if (mainActivity != null && mainActivity.isFinishing() == false)
                {
                    mainActivity.onRuntimeError(e.toString());
                }

                mainActivity.onError();
            } finally
            {
                unLockUI();
                mGourmetListLayout.setSwipeRefreshing(false);
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
