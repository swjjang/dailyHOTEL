package com.twoheart.dailyhotel.screen.hotel.list;

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
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StayListFragment extends PlaceListFragment
{
    protected SaleTime mCheckInSaleTime;

    private ViewType mViewType;

    protected List<Stay> mStayList = new ArrayList<>();

    private StayListLayout mStayListLayout;
    private BaseActivity mBaseActivity;

    public interface OnStayListFragmentListener extends OnPlaceListFragmentListener
    {
        void onStayClick(PlaceViewItem placeViewItem, SaleTime checkInSaleTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mStayListLayout = new StayListLayout(getContext(), mEventListener);
        mStayListLayout.setBottomOptionLayout(mBottomOptionLayout);

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
            Util.restartApp(mBaseActivity);
            return;
        }

        int nights = StayCurationManager.getInstance().getNight();
        if (nights <= 0)
        {
            unLockUI();
            return;
        }

        Area area = null;

        if (province instanceof Area)
        {
            area = (Area) province;
        }

        StayCurationOption stayCurationOption = StayCurationManager.getInstance().getStayCurationOption();

        StayParams params = new StayParams();


        params.dateCheckIn = checkInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd");
        params.stays = nights;
        params.provinceIdx = province.getProvinceIndex();

        if (area != null)
        {
            params.areaIdx = area.index;
        }

        params.persons = stayCurationOption.person;
        params.category = StayCurationManager.getInstance().getCategory();
        params.bedType = stayCurationOption.getParamStringByBedTypes(); // curationOption에서 가져온 스트링
        params.luxury = stayCurationOption.getParamStingByAmenities(); // curationOption에서 가져온 스트링
        //        params.longitude = ;
        //        params.latitude = ;
        params.page = 1;
        params.limit = 20;
        params.setSortType(stayCurationOption.getSortType());
        params.details = true;

        DailyNetworkAPI.getInstance(mBaseActivity).requestHotelList(mNetworkTag, //
            province, checkInSaleTime, nights, mHotelListJsonResponseListener, mHotelListJsonResponseListener);

        DailyNetworkAPI.getInstance(mBaseActivity).requestStayList(mNetworkTag, params, mStayListJsonResponseListener);


    }

    public boolean hasSalesPlace()
    {
        return mStayListLayout.hasSalesPlace();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //        if (mViewType == ViewType.MAP)
        //        {
        //            if (mHotelMapFragment != null)
        //            {
        //                mHotelMapFragment.onActivityResult(requestCode, resultCode, data);
        //            }
        //        } else
        //        {
        //            if (mStayRecyclerView == null)
        //            {
        //                Util.restartApp(getContext());
        //                return;
        //            }
        //
        //            switch (requestCode)
        //            {
        //                case CODE_REQUEST_ACTIVITY_CALENDAR:
        //                {
        //                    if (resultCode == Activity.RESULT_OK && data != null)
        //                    {
        //                        mCheckInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
        //                        mCheckOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);
        //
        //                        mOnCommunicateListener.selectDay(mCheckInSaleTime, mCheckOutSaleTime, true);
        //                    } else
        //                    {
        //                        if (mStayRecyclerView.getVisibility() == View.VISIBLE && mStayRecyclerView.getAdapter() != null)
        //                        {
        //                            if (mStayRecyclerView.getAdapter().getItemCount() == 0)
        //                            {
        //                                fetchList();
        //                            }
        //                        }
        //                    }
        //                    break;
        //                }
        //            }
        //        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        //        if (mViewType == ViewType.MAP)
        //        {
        //            if (mHotelMapFragment != null)
        //            {
        //                mHotelMapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //            }
        //        }
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

    private ArrayList<PlaceViewItem> curationSorting(List<Stay> stayList, StayCurationOption stayCurationOption)
    {
        ArrayList<PlaceViewItem> hotelListViewItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return hotelListViewItemList;
        }

        final Location location = StayCurationManager.getInstance().getLocation();

        switch (stayCurationOption.getSortType())
        {
            case DEFAULT:
                return makeSectionStayList(stayList);

            case DISTANCE:
            {
                if (location == null)
                {
                    stayCurationOption.setSortType(SortType.DEFAULT);
                    DailyToast.showToast(getContext(), R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                    return makeSectionStayList(stayList);
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

    private DailyHotelJsonResponseListener mStayListJsonResponseListener = new DailyHotelJsonResponseListener()
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

                    if (dataJSONObject.has("hotelSales") == true)
                    {
                        hotelJSONArray = dataJSONObject.getJSONArray("hotelSales");
                    }

                    int length;

                    if (hotelJSONArray == null)
                    {
                        length = 0;
                    } else
                    {
                        length = hotelJSONArray.length();
                    }

                    mStayList.clear();

                    StayCurationOption stayCurationOption = StayCurationManager.getInstance().getStayCurationOption();

                    if (length == 0)
                    {
                        mStayListLayout.setList(getChildFragmentManager(), mViewType, null, stayCurationOption.getSortType());

                        setVisibility(ViewType.GONE, true);

                    } else
                    {
                        String imageUrl = dataJSONObject.getString("imgUrl");
                        int nights = dataJSONObject.getInt("stays");
                        int hotelSalesCount = dataJSONObject.getInt("hotelSalesCount");

                        ArrayList<Stay> stayList = makeStayList(hotelJSONArray, imageUrl, nights);

                        // 기본적으로 보관한다.
                        mStayList.addAll(stayList);

                        setScrollListTop(true);

                        mStayListLayout.setList(getChildFragmentManager(), mViewType, //
                            makeSectionStayList(stayList), stayCurationOption.getSortType());
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
                unLockUI();
                mStayListLayout.setSwipeRefreshing(false);
            }
        }

        private ArrayList<Stay> makeStayList(JSONArray jsonArray, String imageUrl, int nights) throws JSONException
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

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {

        }

        @Override
        public void onRefreshAll(boolean isShowProgress)
        {
            refreshList(isShowProgress);
        }

        @Override
        public void finish()
        {

        }
    };
}
