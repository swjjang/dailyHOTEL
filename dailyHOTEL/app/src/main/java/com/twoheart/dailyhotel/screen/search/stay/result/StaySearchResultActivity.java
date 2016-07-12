package com.twoheart.dailyhotel.screen.search.stay.result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCurationOption;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.screen.hotel.detail.HotelDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCurationActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StaySearchResultActivity extends PlaceSearchResultActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_NIGHTS = "nights";
    private static final String INTENT_EXTRA_DATA_LOCATION = "location";
    private static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searchType";
    private static final String INTENT_EXTRA_DATA_INPUTTEXT = "inputText";

    private static final int COUNT_PER_TIMES = 30;

    public static final int SEARCHTYPE_SEARCHES = 0;
    public static final int SEARCHTYPE_AUTOCOMPLETE = 1;
    public static final int SEARCHTYPE_RECENT = 2;
    public static final int SEARCHTYPE_LOCATION = 3;

    private SaleTime mSaleTime;
    private int mNights;
    private Keyword mKeyword;
    private String mInputText;
    private Location mLocation;

    private int mOffset, mTotalCount;
    private int mSearchType;
    private StaySearchResultNetworkController mNetworkController;

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, String inputText, Keyword keyword, int searchType)
    {
        Intent intent = new Intent(context, StaySearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);
        intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, keyword);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType);
        intent.putExtra(INTENT_EXTRA_DATA_INPUTTEXT, inputText);

        return intent;
    }


    public static Intent newInstance(Context context, SaleTime saleTime, int nights, Keyword keyword, int searchType)
    {
        return newInstance(context, saleTime, nights, null, keyword, searchType);
    }

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, String text)
    {
        return newInstance(context, saleTime, nights, null, new Keyword(0, text), SEARCHTYPE_SEARCHES);
    }

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, Location location)
    {
        Intent intent = new Intent(context, StaySearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_NIGHTS, nights);
        intent.putExtra(INTENT_EXTRA_DATA_LOCATION, location);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, SEARCHTYPE_LOCATION);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        lockUI();

        if (mSearchType == SEARCHTYPE_LOCATION)
        {
            mNetworkController.requestCategoryList(mSaleTime, mNights, mLocation);
        } else
        {
            mNetworkController.requestCategoryList(mSaleTime, mNights, mKeyword.name);
        }
    }

    @Override
    protected PlaceSearchResultLayout getPlaceSearchResultLayout(Context context)
    {
        return new StaySearchResultLayout(context, mOnEventListener);
    }

    @Override
    protected void onCalendarActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
            SaleTime checkOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

            if (checkInSaleTime == null || checkOutSaleTime == null)
            {
                return;
            }

            StaySearchResultCurationManager.getInstance().setCheckInSaleTime(checkInSaleTime);
            StaySearchResultCurationManager.getInstance().setCheckOutSaleTime(checkOutSaleTime);

            setToolbarDateText(checkInSaleTime, checkOutSaleTime);

            refreshCurrentFragment(true);
        }
    }

    @Override
    protected void onCurationActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            PlaceCurationOption placeCurationOption = data.getParcelableExtra(StayCurationActivity.INTENT_EXTRA_DATA_CURATION_OPTIONS);
            Category category = data.getParcelableExtra(StayCurationActivity.INTENT_EXTRA_DATA_CATEGORY);
            Province province = data.getParcelableExtra(StayCurationActivity.INTENT_EXTRA_DATA_PROVINCE);

            if ((placeCurationOption instanceof StayCurationOption) == false)
            {
                return;
            }

            StayCurationOption changeCurationOption = (StayCurationOption) placeCurationOption;
            StayCurationOption stayCurationOption = StaySearchResultCurationManager.getInstance().getStayCurationOption();

            stayCurationOption.setCurationOption(changeCurationOption);

            StaySearchResultCurationManager.getInstance().setCategory(category);
            StaySearchResultCurationManager.getInstance().setProvince(province);

            mPlaceSearchResultLayout.setOptionFilterEnabled(StaySearchResultCurationManager.getInstance().getStayCurationOption().isDefaultFilter() == false);

            if (changeCurationOption.getSortType() == SortType.DISTANCE)
            {
                searchMyLocation();
            } else
            {
                refreshCurrentFragment(true);
            }
        }
    }

    @Override
    protected void onLocationFailed()
    {
        StaySearchResultCurationManager.getInstance().getStayCurationOption().setSortType(SortType.DEFAULT);
        mPlaceSearchResultLayout.setOptionFilterEnabled(StaySearchResultCurationManager.getInstance().getStayCurationOption().isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        StaySearchResultCurationManager.getInstance().getStayCurationOption().setSortType(SortType.DEFAULT);
        mPlaceSearchResultLayout.setOptionFilterEnabled(StaySearchResultCurationManager.getInstance().getStayCurationOption().isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationChanged(Location location)
    {
        if (location == null)
        {
            StaySearchResultCurationManager.getInstance().getStayCurationOption().setSortType(SortType.DEFAULT);
            refreshCurrentFragment(true);
        } else
        {
            StaySearchResultCurationManager.getInstance().setLocation(location);

            // 만약 sort type이 거리가 아니라면 다른 곳에서 변경 작업이 일어났음으로 갱신하지 않음
            if (StaySearchResultCurationManager.getInstance().getStayCurationOption().getSortType() == SortType.DISTANCE)
            {
                refreshCurrentFragment(true);
            }
        }
    }

    @Override
    protected void initIntent(Intent intent)
    {
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);
        mNights = intent.getIntExtra(INTENT_EXTRA_DATA_NIGHTS, 1);

        if (intent.hasExtra(INTENT_EXTRA_DATA_KEYWORD) == true)
        {
            mKeyword = intent.getParcelableExtra(INTENT_EXTRA_DATA_KEYWORD);
        } else if (intent.hasExtra(INTENT_EXTRA_DATA_LOCATION) == true)
        {
            mLocation = intent.getParcelableExtra(INTENT_EXTRA_DATA_LOCATION);
        }

        mSearchType = intent.getIntExtra(INTENT_EXTRA_DATA_SEARCHTYPE, SEARCHTYPE_SEARCHES);
        mInputText = intent.getStringExtra(INTENT_EXTRA_DATA_INPUTTEXT);
        mOffset = 0;

        if (mSaleTime == null)
        {
            finish();
        }
    }

    @Override
    protected void initLayout()
    {
        String checkInDate = mSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd");
        SaleTime checkOutSaleTime = mSaleTime.getClone(mSaleTime.getOffsetDailyDay() + mNights);
        String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd");

        if (mSearchType == SEARCHTYPE_LOCATION)
        {
            mPlaceSearchResultLayout.setToolbarTitle("");
            mPlaceSearchResultLayout.setCalendarText(String.format("%s - %s", checkInDate, checkOutDate));
        } else
        {
            mPlaceSearchResultLayout.setToolbarTitle(mKeyword.name);
            mPlaceSearchResultLayout.setCalendarText(String.format("%s - %s", checkInDate, checkOutDate));
        }

        mNetworkController = new StaySearchResultNetworkController(this, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected Keyword getKeyword()
    {
        return mKeyword;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mSaleTime == null)
        {
            Util.restartApp(this);
        }
    }

    protected void setToolbarDateText(SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        String checkInDay = checkInSaleTime.getDayOfDaysDateFormat("M.d(EEE)");
        String checkOutDay = checkOutSaleTime.getDayOfDaysDateFormat("M.d(EEE)");

        mPlaceSearchResultLayout.setCalendarText(String.format("%s-%s", checkInDay, checkOutDay));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceSearchResultLayout.OnEventListener mOnEventListener = new PlaceSearchResultLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            StaySearchResultActivity.this.finish();
        }

        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {
            Category category = (Category) tab.getTag();
            StaySearchResultCurationManager.getInstance().setCategory(category);

            mPlaceSearchResultLayout.setCurrentItem(tab.getPosition());
            mPlaceSearchResultLayout.showBottomLayout(false);

            refreshCurrentFragment(false);
        }

        @Override
        public void onCategoryTabUnselected(TabLayout.Tab tab)
        {

        }

        @Override
        public void onCategoryTabReselected(TabLayout.Tab tab)
        {

        }

        @Override
        public void onDateClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            SaleTime checkInSaleTime = StaySearchResultCurationManager.getInstance().getCheckInSaleTime();
            int nights = StaySearchResultCurationManager.getInstance().getNights();

            Intent intent = StayCalendarActivity.newInstance(StaySearchResultActivity.this, checkInSaleTime, nights, AnalyticsManager.ValueType.LIST, true, true);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
        }

        @Override
        public void onViewTypeClick()
        {
            if (isFinishing() == true || isLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            StaySearchResultListFragment currentFragment = (StaySearchResultListFragment) mPlaceSearchResultLayout.getCurrentPlaceListFragment();

            switch (mViewType)
            {
                case LIST:
                    // 고메 쪽에서 보여지는 메세지로 Stay의 경우도 동일한 처리가 필요해보여서 추가함
                    if (currentFragment.hasSalesPlace() == false)
                    {
                        unLockUI();

                        DailyToast.showToast(StaySearchResultActivity.this, R.string.toast_msg_solodout_area, Toast.LENGTH_SHORT);
                        return;
                    }

                    mViewType = ViewType.MAP;

                    AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHAGE_VIEW, AnalyticsManager.Label.HOTEL_MAP, null);
                    break;

                case MAP:
                {
                    mViewType = ViewType.LIST;

                    Map<String, String> params = new HashMap<>();
                    Province province = StaySearchResultCurationManager.getInstance().getProvince();

                    if (province == null)
                    {
                        Util.restartApp(StaySearchResultActivity.this);
                        return;
                    }

                    if (province instanceof Area)
                    {
                        Area area = (Area) province;
                        params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                        params.put(AnalyticsManager.KeyType.DISTRICT, area.name);

                    } else
                    {
                        params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                        params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                    }

                    AnalyticsManager.getInstance(StaySearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST, params);
                    AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CHAGE_VIEW, AnalyticsManager.Label.HOTEL_LIST, null);
                    break;
                }
            }

            mPlaceSearchResultLayout.setOptionViewTypeView(mViewType);

            // 현재 페이지 선택 상태를 Fragment에게 알려준다.
            for (PlaceListFragment placeListFragment : mPlaceSearchResultLayout.getPlaceListFragment())
            {
                boolean isCurrentFragment = (placeListFragment == currentFragment) ? true : false;
                placeListFragment.setVisibility(mViewType, isCurrentFragment);
            }

            refreshCurrentFragment(false);

            unLockUI();
        }

        @Override
        public void onFilterClick()
        {

            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Province province = StaySearchResultCurationManager.getInstance().getProvince();
            if (province == null)
            {
                releaseUiComponent();
                return;
            }

            Intent intent = StayCurationActivity.newInstance(StaySearchResultActivity.this, //
                province.isOverseas, mViewType, //
                StaySearchResultCurationManager.getInstance().getStayCurationOption(), //
                StaySearchResultCurationManager.getInstance().getCategory(), //
                StaySearchResultCurationManager.getInstance().getProvince());
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAYCURATION);

            String viewType = AnalyticsManager.Label.VIEWTYPE_LIST;

            switch (mViewType)
            {
                case LIST:
                    viewType = AnalyticsManager.Label.VIEWTYPE_LIST;
                    break;

                case MAP:
                    viewType = AnalyticsManager.Label.VIEWTYPE_MAP;
                    break;
            }

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.HOTEL_SORT_FILTER_BUTTON_CLICKED, viewType, null);
        }

        @Override
        public void finish(int resultCode)
        {
            StaySearchResultActivity.this.finish(resultCode);

            if (resultCode == Constants.CODE_RESULT_ACTIVITY_HOME)
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_SEARCH_RESULT_CANCELED, AnalyticsManager.Label.SEARCH_RESULT_CANCELED, null);
            } else
            {
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_SEARCH_BACK_BUTTON_CLICKED, AnalyticsManager.Label.RESULT_BACK_BUTTON_CLICKED, null);
            }
        }

        @Override
        public void research(int resultCode)
        {
            StaySearchResultActivity.this.finish(resultCode);

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.HOTEL_SEARCH_AGAIN_CLICKED, AnalyticsManager.Label.HOTEL_SEARCH_AGAIN_CLICKED, null);
        }

        @Override
        public void onItemClick(PlaceViewItem placeViewItem)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            Stay stay = placeViewItem.getItem();

            Intent intent = new Intent(StaySearchResultActivity.this, HotelDetailActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mSaleTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, stay.index);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, stay.nights);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, stay.name);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, stay.imageUrl);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
        }

        @Override
        public void onShowCallDialog()
        {
            showCallDialog();

            AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                , AnalyticsManager.Action.CALL_INQUIRY_CLICKED, AnalyticsManager.Label.CALL_KEYWORD_HOTEL, null);
        }

        @Override
        public void onLoadMoreList()
        {

        }

        @Override
        public void onShowProgressBar()
        {

        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private StaySearchResultNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StaySearchResultNetworkController.OnNetworkControllerListener()
    {
        private String mAddress;
        private int mSize = -100;

        private String getSearchDate()
        {
            String checkInDate = mSaleTime.getDayOfDaysDateFormat("yyMMdd");
            SaleTime checkOutSaleTime = mSaleTime.getClone(mSaleTime.getOffsetDailyDay() + mNights);
            String checkOutDate = checkOutSaleTime.getDayOfDaysDateFormat("yyMMdd");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm", Locale.KOREA);

            return String.format("%s-%s-%s", checkInDate, checkOutDate, simpleDateFormat.format(calendar.getTime()));
        }

        private void analyticsOnResponseSearchResultListForSearches(Keyword keyword, int totalCount)
        {
            String action;

            if (totalCount == 0)
            {
                String prefix = null;

                switch (mSearchType)
                {
                    case SEARCHTYPE_SEARCHES:
                        action = AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_NOT_FOUND;
                        break;

                    case SEARCHTYPE_AUTOCOMPLETE:
                        action = AnalyticsManager.Action.HOTEL_AUTOCOMPLETE_KEYWORD_NOT_FOUND;

                        if (keyword.price == 0)
                        {
                            prefix = String.format("지역-%s", mInputText);
                        } else
                        {
                            prefix = String.format("호텔-%s", mInputText);
                        }
                        break;

                    case SEARCHTYPE_RECENT:
                        action = AnalyticsManager.Action.HOTEL_RECENT_KEYWORD_NOT_FOUND;
                        break;

                    default:
                        action = AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_NOT_FOUND;
                        break;
                }

                String label;

                if (Util.isTextEmpty(prefix) == true)
                {
                    label = String.format("%s-%s", keyword.name, getSearchDate());
                } else
                {
                    label = String.format("%s-%s-%s", prefix, keyword.name, getSearchDate());
                }

                Map<String, String> eventParams = new HashMap<>();
                eventParams.put(AnalyticsManager.KeyType.KEYWORD, keyword.name);
                eventParams.put(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED, Integer.toString(totalCount));
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , action, label, eventParams);

                Map<String, String> screenParams = Collections.singletonMap(AnalyticsManager.KeyType.KEYWORD, keyword.name);
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT_EMPTY);
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT_EMPTY, screenParams);
            } else
            {
                String prefix = null;

                switch (mSearchType)
                {
                    case SEARCHTYPE_SEARCHES:
                        action = AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_CLICKED;
                        break;

                    case SEARCHTYPE_AUTOCOMPLETE:
                        action = AnalyticsManager.Action.HOTEL_AUTOCOMPLETED_KEYWORD_CLICKED;

                        if (keyword.price == 0)
                        {
                            prefix = String.format("지역-%s", mInputText);
                        } else
                        {
                            prefix = String.format("호텔-%s", mInputText);
                        }
                        break;

                    case SEARCHTYPE_RECENT:
                        action = AnalyticsManager.Action.HOTEL_RECENT_KEYWORD_SEARCH_CLICKED;
                        break;

                    default:
                        action = AnalyticsManager.Action.HOTEL_KEYWORD_SEARCH_CLICKED;
                        break;
                }

                String label;

                if (totalCount == -1)
                {
                    label = String.format("%s-Los-%s", keyword.name, getSearchDate());
                } else
                {
                    label = String.format("%s-%d-%s", keyword.name, totalCount, getSearchDate());
                }

                if (Util.isTextEmpty(prefix) == false)
                {
                    label = String.format("%s-%s", prefix, label);
                }

                Map<String, String> eventParams = new HashMap<>();
                eventParams.put(AnalyticsManager.KeyType.KEYWORD, keyword.name);
                eventParams.put(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED, Integer.toString(totalCount));
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , action, label, eventParams);

                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT);
            }
        }

        private void analyticsOnResponseSearchResultListForLocation()
        {
            if (Util.isTextEmpty(mAddress) == true || mSize == -100)
            {
                return;
            }

            if (mSize == 0)
            {
                String label = String.format("%s-%s", mAddress, getSearchDate());
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_AROUND_SEARCH_NOT_FOUND, label, null);

                Map<String, String> params = Collections.singletonMap(AnalyticsManager.KeyType.KEYWORD, mAddress);
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT_EMPTY);
                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT_EMPTY, params);
            } else
            {
                String label;

                if (mSize == -1)
                {
                    label = String.format("%s-Los-%s", mAddress, getSearchDate());
                } else
                {
                    label = String.format("%s-%d-%s", mAddress, mSize, getSearchDate());
                }

                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SEARCH//
                    , AnalyticsManager.Action.HOTEL_AROUND_SEARCH_CLICKED, label, null);

                AnalyticsManager.getInstance(StaySearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_SEARCH_RESULT);
            }
        }

        private void distanceBetween(Location location, ArrayList<PlaceViewItem> placeViewItemList)
        {
            ((StaySearchResultLayout) mPlaceSearchResultLayout).setSortType(SortType.DISTANCE);

            Stay stay;
            float[] results = new float[3];

            for (PlaceViewItem placeViewItem : placeViewItemList)
            {
                stay = placeViewItem.getItem();

                Location.distanceBetween(location.getLatitude(), location.getLongitude(), stay.latitude, stay.longitude, results);
                stay.distance = results[0];
            }
        }

        private void responseSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList)
        {
            mTotalCount = totalCount;

            if (totalCount == 0 || (mOffset == 0 && (placeViewItemList == null || placeViewItemList.size() == 0)))
            {
                mPlaceSearchResultLayout.showEmptyLayout();
            } else
            {
                if (placeViewItemList != null)
                {
                    int size = placeViewItemList.size();
                    if (size < COUNT_PER_TIMES)
                    {
                        mOffset = -1;
                    } else
                    {
                        mOffset += placeViewItemList.size();
                    }

                    // 위치 요청 타입인 경우에는 위치를 계산해 주어야 한다.
                    if (mLocation != null)
                    {
                        distanceBetween(mLocation, placeViewItemList);
                    }
                } else
                {
                    mOffset = -1;
                }

                mPlaceSearchResultLayout.showListLayout();
                ((StaySearchResultLayout) mPlaceSearchResultLayout).addSearchResultList(placeViewItemList);
            }

            mPlaceSearchResultLayout.updateResultCount(totalCount);
            unLockUI();
        }

        @Override
        public void onResponseSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (mOffset == 0)
            {
                // 연박인 경우 사이즈가 0이면 검색개수가 없음
                if (totalCount == -1)
                {
                    if (placeViewItemList == null || placeViewItemList.size() == 0)
                    {
                        analyticsOnResponseSearchResultListForSearches(mKeyword, 0);
                    } else
                    {
                        analyticsOnResponseSearchResultListForSearches(mKeyword, totalCount);
                    }
                } else
                {
                    analyticsOnResponseSearchResultListForSearches(mKeyword, totalCount);
                }
            }

            responseSearchResultList(totalCount, placeViewItemList);
        }

        @Override
        public void onResponseLocationSearchResultList(int totalCount, ArrayList<PlaceViewItem> placeViewItemList)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (mOffset == 0)
            {
                if (totalCount == -1)
                {
                    if (placeViewItemList == null || placeViewItemList.size() == 0)
                    {
                        mSize = 0;
                    } else
                    {
                        mSize = totalCount;
                    }
                } else
                {
                    mSize = totalCount;
                }

                analyticsOnResponseSearchResultListForLocation();
            }

            responseSearchResultList(totalCount, placeViewItemList);
        }

        @Override
        public void onResponseAddress(String address)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mAddress = address;

            mPlaceSearchResultLayout.setToolbarTitle(address);

            analyticsOnResponseSearchResultListForLocation();
        }

        @Override
        public void onResponseCategoryList(List<Category> list)
        {
            mPlaceSearchResultLayout.setCategoryTabLayout(getSupportFragmentManager(), list, null, new PlaceListFragment.OnPlaceListFragmentListener()
            {
                @Override
                public void onEventBannerClick(EventBanner eventBanner)
                {

                }

                @Override
                public void onActivityCreated(PlaceListFragment placeListFragment)
                {
                    if (mPlaceSearchResultLayout == null || placeListFragment == null)
                    {
                        return;
                    }

                    PlaceListFragment currentPlaceListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();

                    if (currentPlaceListFragment == placeListFragment)
                    {
                        currentPlaceListFragment.setVisibility(mViewType, true);
                        currentPlaceListFragment.refreshList(true);

                        // Analytics
                        Map<String, String> params = new HashMap<>();
                        Province province = StaySearchResultCurationManager.getInstance().getProvince();

                        if (province instanceof Area)
                        {
                            Area area = (Area) province;
                            params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                            params.put(AnalyticsManager.KeyType.DISTRICT, area.name);

                        } else
                        {
                            params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
                            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                        }

                        if (DailyHotel.isLogin() == false)
                        {
                            params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.GUEST);
                        } else
                        {
                            params.put(AnalyticsManager.KeyType.IS_SIGNED, AnalyticsManager.ValueType.MEMBER);
                        }
                    } else
                    {
                        placeListFragment.setVisibility(mViewType, false);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    mPlaceSearchResultLayout.calculationMenuBarLayoutTranslationY(dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                {
                    switch (newState)
                    {
                        case RecyclerView.SCROLL_STATE_IDLE:
                        {
                            mPlaceSearchResultLayout.animationMenuBarLayout();

                            //                    ExLog.d("offset : " + recyclerView.computeVerticalScrollOffset() + ", " + recyclerView.computeVerticalScrollExtent() + ", " + recyclerView.computeVerticalScrollRange());

                            if (recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent() >= recyclerView.computeVerticalScrollRange())
                            {
                                StayListAdapter stayListAdapter = (StayListAdapter) recyclerView.getAdapter();

                                if (stayListAdapter != null)
                                {
                                    int count = stayListAdapter.getItemCount();

                                    if (count == 0)
                                    {
                                    } else
                                    {
                                        PlaceViewItem placeViewItem = stayListAdapter.getItem(stayListAdapter.getItemCount() - 1);

                                        if (placeViewItem != null && placeViewItem.mType == PlaceViewItem.TYPE_FOOTER_VIEW)
                                        {
                                            mPlaceSearchResultLayout.showBottomLayout(false);
                                        }
                                    }
                                }
                            }
                            break;
                        }

                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            break;

                        case RecyclerView.SCROLL_STATE_SETTLING:
                            break;
                    }
                }

                @Override
                public void onShowMenuBar()
                {

                }
            });
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();
            StaySearchResultActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            StaySearchResultActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            StaySearchResultActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            StaySearchResultActivity.this.onErrorToastMessage(message);
        }
    };
}
