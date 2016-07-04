package com.twoheart.dailyhotel.screen.hotel.list;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StayListFragment extends PlaceListFragment
{
    protected SaleTime mCheckInSaleTime;

    private ViewType mViewType;

    protected List<Stay> mStayList = new ArrayList<>();

    private StayListLayout mStayListLayout;
    private BaseActivity mBaseActivity;
    private StayListNetworkController mNetworkController;

    public interface OnStayListFragmentListener extends OnPlaceListFragmentListener
    {
        void onStayClick(PlaceViewItem placeViewItem, SaleTime checkInSaleTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mStayListLayout = new StayListLayout(mBaseActivity, mEventListener);
        mStayListLayout.setBottomOptionLayout(mBottomOptionLayout);

        mNetworkController = new StayListNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);

        mViewType = ViewType.LIST;

        return mStayListLayout.onCreateView(R.layout.fragment_hotel_list, container);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////           Ovrride method    start   /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void refreshList(boolean isShowProgress)
    {
        lockUI();

        SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime();

        Province province = StayCurationManager.getInstance().getProvince();

        if (province == null || checkInSaleTime == null)
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        int nights = StayCurationManager.getInstance().getNight();
        if (nights <= 0)
        {
            unLockUI();
            return;
        }

        mNetworkController.requestStayList(StayCurationManager.getInstance().getStayParams(1, 20, true));
    }

    public boolean hasSalesPlace()
    {
        return mStayListLayout.hasSalesPlace();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
    }

    @Override
    public void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
        mViewType = viewType;
        mStayListLayout.setVisibility(getChildFragmentManager(), viewType, isCurrentPage);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////           Ovrride method     end    /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    public void setScrollListTop(boolean scrollListTop)
    {
        mStayListLayout.setScrollListTop(scrollListTop);
    }

    private ArrayList<PlaceViewItem> makeSectionStayList(List<Stay> stayList)
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
            String region = stay.districtName;

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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mHotelListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            StayListFragment.this.onErrorResponse(volleyError);
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
                    JSONArray hotelJSONArray = null;

                    if (dataJSONObject.has("hotelSaleList") == true)
                    {
                        hotelJSONArray = dataJSONObject.getJSONArray("hotelSaleList");
                    }

                    mStayList.clear();

                    StayCurationOption stayCurationOption = StayCurationManager.getInstance().getStayCurationOption();

                    stayCurationOption.setFiltersListByJson(hotelJSONArray);

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
                unLockUI();
            }
        }
    };

    StayListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StayListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayList(ArrayList<Stay> list, int page, int hotelSaleCount)
        {
            if (isFinishing() == true)
            {
                unLockUI();
                return;
            }

            mStayList.clear();

            StayCurationOption stayCurationOption = StayCurationManager.getInstance().getStayCurationOption();

            if (list == null || list.size() == 0)
            {
                mStayListLayout.setList(getChildFragmentManager(), mViewType, null, stayCurationOption.getSortType());
                setVisibility(ViewType.GONE, true);
                return;
            }

            // 기본적으로 보관한다.
            mStayList.addAll(list);

            setScrollListTop(true);

            mStayListLayout.setList(getChildFragmentManager(), mViewType, //
                makeSectionStayList(list), stayCurationOption.getSortType());

            unLockUI();
            mStayListLayout.setSwipeRefreshing(false);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onError(Exception e)
        {

        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {

        }

        @Override
        public void onErrorToastMessage(String message)
        {

        }
    };

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////   Listener   //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    private StayListLayout.OnEventListener mEventListener = new StayListLayout.OnEventListener()
    {
        @Override
        public void onPlaceClick(PlaceViewItem placeViewItem)
        {
            SaleTime checkInSaleTime = StayCurationManager.getInstance().getCheckInSaleTime();
            ((OnStayListFragmentListener) mOnPlaceListFragmentListener).onStayClick(placeViewItem, checkInSaleTime);
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
            refreshList(isShowProgress);
        }

        @Override
        public void finish()
        {
            if (mBaseActivity != null)
            {
                mBaseActivity.finish();
            }
        }
    };
}
