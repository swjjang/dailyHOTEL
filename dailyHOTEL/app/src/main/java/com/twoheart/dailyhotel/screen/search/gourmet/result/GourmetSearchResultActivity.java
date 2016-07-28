package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCurationActivity;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GourmetSearchResultActivity extends PlaceSearchResultActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_LOCATION = "location";
    private static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searchType";
    private static final String INTENT_EXTRA_DATA_INPUTTEXT = "inputText";

    private String mInputText;
    private String mAddress;

    private Keyword mKeyword;
    private SearchType mSearchType;
    private GourmetCuration mGourmetCuration;

    private GourmetSearchResultNetworkController mNetworkController;

    public static Intent newInstance(Context context, SaleTime saleTime, String inputText, Keyword keyword, SearchType searchType)
    {
        Intent intent = new Intent(context, GourmetSearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, keyword);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType.name());
        intent.putExtra(INTENT_EXTRA_DATA_INPUTTEXT, inputText);

        return intent;
    }

    public static Intent newInstance(Context context, SaleTime saleTime, Keyword keyword, SearchType searchType)
    {
        return newInstance(context, saleTime, null, keyword, searchType);
    }

    public static Intent newInstance(Context context, SaleTime saleTime, String text)
    {
        return newInstance(context, saleTime, null, new Keyword(0, text), SearchType.SEARCHES);
    }

    public static Intent newInstance(Context context, SaleTime saleTime, Location location)
    {
        Intent intent = new Intent(context, GourmetSearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_LOCATION, location);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, SearchType.LOCATION.name());

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        lockUI();

        if (mSearchType == SearchType.LOCATION)
        {
            mNetworkController.requestAddress(mGourmetCuration.getLocation());
        }
    }

    @Override
    protected PlaceSearchResultLayout getPlaceSearchResultLayout(Context context)
    {
        return new GourmetSearchResultLayout(this, mOnEventListener);
    }

    @Override
    protected void onCalendarActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            SaleTime saleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

            if (saleTime == null)
            {
                return;
            }

            mGourmetCuration.setSaleTime(saleTime);

            ((GourmetSearchResultLayout) mPlaceSearchResultLayout).setCalendarText(saleTime);

            refreshCurrentFragment(true);
        }
    }

    @Override
    protected void onCurationActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            PlaceCuration placeCuration = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);

            if ((placeCuration instanceof GourmetCuration) == false)
            {
                return;
            }

            GourmetCuration changedGourmetCuration = (GourmetCuration) placeCuration;
            GourmetCurationOption changedGourmetCurationOption = (GourmetCurationOption) changedGourmetCuration.getCurationOption();

            mGourmetCuration.setCurationOption(changedGourmetCurationOption);
            mPlaceSearchResultLayout.setOptionFilterEnabled(changedGourmetCurationOption.isDefaultFilter() == false);

            if (changedGourmetCurationOption.getSortType() == SortType.DISTANCE)
            {
                mGourmetCuration.setLocation(changedGourmetCuration.getLocation());

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
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        gourmetCurationOption.setSortType(SortType.DEFAULT);
        mPlaceSearchResultLayout.setOptionFilterEnabled(gourmetCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationProviderDisabled()
    {
        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) mGourmetCuration.getCurationOption();

        gourmetCurationOption.setSortType(SortType.DEFAULT);
        mPlaceSearchResultLayout.setOptionFilterEnabled(gourmetCurationOption.isDefaultFilter() == false);

        refreshCurrentFragment(true);
    }

    @Override
    protected void onLocationChanged(Location location)
    {
        if (location == null)
        {
            mGourmetCuration.getCurationOption().setSortType(SortType.DEFAULT);
            refreshCurrentFragment(true);
        } else
        {
            mGourmetCuration.setLocation(location);

            // 만약 sort type이 거리가 아니라면 다른 곳에서 변경 작업이 일어났음으로 갱신하지 않음
            if (mGourmetCuration.getCurationOption().getSortType() == SortType.DISTANCE)
            {
                refreshCurrentFragment(true);
            }
        }
    }

    @Override
    protected void initIntent(Intent intent)
    {
        SaleTime saleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);
        Location location = null;

        if (intent.hasExtra(INTENT_EXTRA_DATA_KEYWORD) == true)
        {
            mKeyword = intent.getParcelableExtra(INTENT_EXTRA_DATA_KEYWORD);
        } else if (intent.hasExtra(INTENT_EXTRA_DATA_LOCATION) == true)
        {
            location = intent.getParcelableExtra(INTENT_EXTRA_DATA_LOCATION);
        }

        mSearchType = SearchType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_SEARCHTYPE));
        mInputText = intent.getStringExtra(INTENT_EXTRA_DATA_INPUTTEXT);

        if (saleTime == null)
        {
            finish();
        }

        mGourmetCuration = new GourmetCuration();

        // ---> 테스트를 위한 임시 코드
        Province province = mGourmetCuration.getProvince();
        if (province == null)
        {
            province = new Province();
            province.index = 5;
            province.name = "서울";
            province.isOverseas = false;
        }

        mGourmetCuration.setProvince(province);
        // <----

        mGourmetCuration.setSaleTime(saleTime);

        if (mSearchType == SearchType.LOCATION)
        {
            mGourmetCuration.getCurationOption().setSortType(SortType.DISTANCE);
            mGourmetCuration.setLocation(location);
        } else
        {

        }
    }

    @Override
    protected void initLayout()
    {
        if (mGourmetCuration == null || mGourmetCuration.getSaleTime() == null)
        {
            finish();
            return;
        }

        if (mSearchType == SearchType.LOCATION)
        {
            mPlaceSearchResultLayout.setToolbarTitle("");
        } else
        {
            mPlaceSearchResultLayout.setToolbarTitle(mKeyword.name);
        }

        ((GourmetSearchResultLayout) mPlaceSearchResultLayout).setCalendarText(mGourmetCuration.getSaleTime());

        mNetworkController = new GourmetSearchResultNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        mPlaceSearchResultLayout.setCategoryTabLayout(getSupportFragmentManager(), new ArrayList<Category>(), null, mOnGourmetListFragmentListener);
    }

    @Override
    protected Keyword getKeyword()
    {
        return mKeyword;
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mGourmetCuration;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void recordScreenSearchResult(String screen)
    {
        if (AnalyticsManager.Screen.SEARCH_RESULT.equalsIgnoreCase(screen) == false //
            && AnalyticsManager.Screen.SEARCH_RESULT_EMPTY.equalsIgnoreCase(screen) == false)
        {
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.CHECK_IN, mGourmetCuration.getSaleTime().getDayOfDaysDateFormat("yyyy-MM-dd"));

        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
        params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, AnalyticsManager.ValueType.GOURMET);

        Province province = mGourmetCuration.getProvince();
        if (province instanceof Area)
        {
            Area area = (Area) province;
            params.put(AnalyticsManager.KeyType.COUNTRY, area.getProvince().isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
            params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
        } else if (province != null)
        {
            params.put(AnalyticsManager.KeyType.COUNTRY, province.isOverseas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, province.name);
            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
        }

        if (AnalyticsManager.Screen.SEARCH_RESULT.equalsIgnoreCase(screen) == true)
        {
            PlaceListFragment gourmetSearchResultListFragment = mPlaceSearchResultLayout.getPlaceListFragment().get(0);
            int placeCount = gourmetSearchResultListFragment.getPlaceCount();
            params.put(AnalyticsManager.KeyType.PLACE_COUNT, Integer.toString(placeCount));
        }

        AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordScreen(screen, params);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnEventListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlaceSearchResultLayout.OnEventListener mOnEventListener = new PlaceSearchResultLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            // 사용하지 않음
        }

        @Override
        public void onCategoryTabSelected(TabLayout.Tab tab)
        {

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

            Intent intent = GourmetCalendarActivity.newInstance(GourmetSearchResultActivity.this, //
                mGourmetCuration.getSaleTime(), AnalyticsManager.ValueType.SEARCH_RESULT, null, true, true);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED,//
                AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
        }

        @Override
        public void onViewTypeClick()
        {

        }

        @Override
        public void onFilterClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = GourmetCurationActivity.newInstance(GourmetSearchResultActivity.this, mViewType, mGourmetCuration);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAYCURATION);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent( //
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.GOURMET_SORT_FILTER_BUTTON_CLICKED,//
                AnalyticsManager.Label.SEARCH_RESULT_VIEW, null);
        }

        @Override
        public void finish(int resultCode)
        {
            GourmetSearchResultActivity.this.finish(resultCode);

            if (resultCode == Constants.CODE_RESULT_ACTIVITY_HOME)
            {
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                    , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CANCEL, null);
            } else
            {
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                    , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.BACK_BUTTON, null);
            }
        }

        @Override
        public void research(int resultCode)
        {
            GourmetSearchResultActivity.this.finish(resultCode);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.SEARCH_AGAIN, null);
        }

        @Override
        public void onShowCallDialog()
        {
            showCallDialog();

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
                , AnalyticsManager.Action.SEARCH_RESULT_VIEW, AnalyticsManager.Label.CALL, null);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetSearchResultListFragment.OnGourmetSearchResultListFragmentListener mOnGourmetListFragmentListener = new GourmetSearchResultListFragment.OnGourmetSearchResultListFragmentListener()
    {
        @Override
        public void onGourmetClick(PlaceViewItem placeViewItem)
        {
            if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            Gourmet gourmet = placeViewItem.getItem();

            Intent intent = GourmetDetailActivity.newInstance(GourmetSearchResultActivity.this,//
                mGourmetCuration.getSaleTime(), gourmet);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
        }

        @Override
        public void onResultListCount(int count)
        {
            if (mPlaceSearchResultLayout == null)
            {
                return;
            }

            mPlaceSearchResultLayout.updateResultCount(count);
        }

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
                currentPlaceListFragment.setPlaceCuration(mGourmetCuration);
                currentPlaceListFragment.refreshList(true);
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
                        GourmetListAdapter stayListAdapter = (GourmetListAdapter) recyclerView.getAdapter();

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

        @Override
        public void onFilterClick()
        {
            if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Province province = mGourmetCuration.getProvince();

            if (province == null)
            {
                releaseUiComponent();
                return;
            }

            Intent intent = GourmetSearchResultCurationActivity.newInstance(GourmetSearchResultActivity.this, mViewType, mGourmetCuration);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMETCURATION);

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
        }

        @Override
        public void onShowActivityEmptyView(boolean isShow)
        {
            if (mPlaceSearchResultLayout == null)
            {
                return;
            }

            if (isShow == true)
            {
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.GONE);
                mPlaceSearchResultLayout.showEmptyLayout();

                recordScreenSearchResult(AnalyticsManager.Screen.SEARCH_RESULT_EMPTY);
            } else
            {
                mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.VISIBLE);
                mPlaceSearchResultLayout.showListLayout();

                recordScreenSearchResult(AnalyticsManager.Screen.SEARCH_RESULT);
            }

            if (mSearchType == SearchType.LOCATION)
            {
                recordEventSearchResultByLocation(mAddress, isShow);
            } else if (mSearchType == SearchType.RECENT)
            {
                recordEventSearchResultByRecentKeyword(mKeyword, isShow);
            } else if (mSearchType == SearchType.AUTOCOMPLETE)
            {
                recordEventSearchResultByAutoSearch(mKeyword, mInputText, isShow);
            } else
            {
                recordEventSearchResultByKeyword(mKeyword, isShow);

                // 기존 AppBoy 이벤트
                PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getPlaceListFragment().get(0);
                int placeCount = placeListFragment.getPlaceCount();

                String action = isShow == true ? AnalyticsManager.Action.GOURMET_KEYWORD_SEARCH_NOT_FOUND : AnalyticsManager.Action.GOURMET_KEYWORD_SEARCH_CLICKED;
                String label = isShow == true ? String.format("%s-%s", mKeyword.name, getSearchDate()) : String.format("%s-%d-%s", mKeyword.name, placeCount, getSearchDate());

                Map<String, String> eventParams = new HashMap<>();
                eventParams.put(AnalyticsManager.KeyType.KEYWORD, mKeyword.name);
                eventParams.put(AnalyticsManager.KeyType.NUM_OF_SEARCH_RESULTS_RETURNED, Integer.toString(placeCount));
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                    , action, label, eventParams);
            }
        }

        private String getSearchDate()
        {
            String checkInDate = mGourmetCuration.getSaleTime().getDayOfDaysDateFormat("yyMMdd");

            return String.format("%s-%s", checkInDate, DailyCalendar.format(new Date(), "yyMMddHHmm"));
        }
    };

    private GourmetSearchResultNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetSearchResultNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onResponseAddress(String address)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mAddress = address;

            mPlaceSearchResultLayout.setToolbarTitle(address);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();
            GourmetSearchResultActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            GourmetSearchResultActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            GourmetSearchResultActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            GourmetSearchResultActivity.this.onErrorToastMessage(message);
        }
    };
}