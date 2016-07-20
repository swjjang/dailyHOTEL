package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GourmetSearchResultActivity extends PlaceSearchResultActivity
{
    private static final String INTENT_EXTRA_DATA_SALETIME = "saletime";
    private static final String INTENT_EXTRA_DATA_LOCATION = "location";
    private static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searcyType";
    private static final String INTENT_EXTRA_DATA_INPUTTEXT = "inputText";

    public static final int SEARCHTYPE_SEARCHES = 0;
    public static final int SEARCHTYPE_AUTOCOMPLETE = 1;
    public static final int SEARCHTYPE_RECENT = 2;
    public static final int SEARCHTYPE_LOCATION = 3;

    private SaleTime mSaleTime;
    private Keyword mKeyword;
    private String mInputText;
    private Location mLocation;

    private int mOffset, mTotalCount;
    private int mSearchType;
    private GourmetSearchResultNetworkController mNetworkController;

    private GourmetCuration mPlaceCuration;

    public static Intent newInstance(Context context, SaleTime saleTime, String inputText, Keyword keyword, int searchType)
    {
        Intent intent = new Intent(context, GourmetSearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, keyword);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, searchType);
        intent.putExtra(INTENT_EXTRA_DATA_INPUTTEXT, inputText);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, new GourmetCuration());

        return intent;
    }

    public static Intent newInstance(Context context, SaleTime saleTime, Keyword keyword, int searchType)
    {
        return newInstance(context, saleTime, null, keyword, searchType);
    }

    public static Intent newInstance(Context context, SaleTime saleTime, String text)
    {
        return newInstance(context, saleTime, null, new Keyword(0, text), SEARCHTYPE_SEARCHES);
    }

    public static Intent newInstance(Context context, SaleTime saleTime, Location location)
    {
        Intent intent = new Intent(context, GourmetSearchResultActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_LOCATION, location);
        intent.putExtra(INTENT_EXTRA_DATA_SEARCHTYPE, SEARCHTYPE_LOCATION);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION, new GourmetCuration());

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        lockUI();
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
            mSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

            ((GourmetSearchResultLayout) mPlaceSearchResultLayout).setCalendarText(mSaleTime);

            PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();

            if (placeListFragment != null)
            {
                placeListFragment.clearList();
                mOffset = 0;
                requestSearchResultList(mOffset);
            }
        }
    }

    @Override
    protected void onCurationActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    protected void onLocationFailed()
    {

    }

    @Override
    protected void onLocationProviderDisabled()
    {

    }

    @Override
    protected void onLocationChanged(Location location)
    {

    }

    @Override
    protected void initIntent(Intent intent)
    {
        mSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_SALETIME);

        if (intent.hasExtra(INTENT_EXTRA_DATA_KEYWORD) == true)
        {
            mKeyword = intent.getParcelableExtra(INTENT_EXTRA_DATA_KEYWORD);
        } else if (intent.hasExtra(INTENT_EXTRA_DATA_LOCATION) == true)
        {
            mLocation = intent.getParcelableExtra(INTENT_EXTRA_DATA_LOCATION);
        }

        mSearchType = intent.getIntExtra(INTENT_EXTRA_DATA_SEARCHTYPE, SEARCHTYPE_SEARCHES);
        mInputText = intent.getStringExtra(INTENT_EXTRA_DATA_INPUTTEXT);
        mPlaceCuration = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACECURATION);

        mOffset = 0;

        if (mSaleTime == null)
        {
            finish();
        }
    }

    @Override
    protected void initLayout()
    {
        if (mSaleTime == null)
        {
            finish();
            return;
        }

        if (mSearchType == SEARCHTYPE_LOCATION)
        {
            mPlaceSearchResultLayout.setToolbarTitle("");
        } else
        {
            mPlaceSearchResultLayout.setToolbarTitle(mKeyword.name);
        }

        ((GourmetSearchResultLayout) mPlaceSearchResultLayout).setCalendarText(mSaleTime);

        mNetworkController = new GourmetSearchResultNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        mPlaceSearchResultLayout.setCategoryTabLayout(getSupportFragmentManager(), new ArrayList<Category>(), null, new GourmetSearchResultListFragment.OnGourmetSearchResultListFragmentListener()
        {
            @Override
            public void onRefreshAll(boolean isShowProgress)
            {

            }

            @Override
            public void onItemClick(PlaceViewItem placeViewItem)
            {
                if (placeViewItem == null || placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
                {
                    return;
                }

                Gourmet gourmet = placeViewItem.getItem();

                Intent intent = new Intent(GourmetSearchResultActivity.this, GourmetDetailActivity.class);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mSaleTime);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmet.index);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, gourmet.name);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, gourmet.imageUrl);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, gourmet.category);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
            }

            @Override
            public void onLoadMoreList()
            {
                requestSearchResultList(mOffset);
            }

            @Override
            public void onEventBannerClick(EventBanner eventBanner)
            {

            }

            @Override
            public void onActivityCreated(PlaceListFragment placeListFragment)
            {
                // Fragment가 1개 뿐이다.
                requestSearchResultList(mOffset);
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
            public void onShowMenuBar()
            {

            }

            @Override
            public void onFilterClick()
            {

            }
        });
    }

    private void requestSearchResultList(int offset)
    {
        if ((offset > 0 && mOffset >= mTotalCount) || offset == -1)
        {
            return;
        }

        if (offset == 0)
        {
            lockUI();
        }

        if (mSearchType == SEARCHTYPE_LOCATION)
        {
            mNetworkController.requestSearchResultList(mSaleTime, mLocation, offset, PAGENATION_LIST_SIZE);
        } else
        {
            mNetworkController.requestSearchResultList(mSaleTime, mKeyword.name, offset, PAGENATION_LIST_SIZE);
        }
    }

    @Override
    protected Keyword getKeyword()
    {
        return mKeyword;
    }

    @Override
    protected PlaceCuration getPlaceCuration()
    {
        return mPlaceCuration;
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
            Intent intent = GourmetCalendarActivity.newInstance(GourmetSearchResultActivity.this, mSaleTime, AnalyticsManager.ValueType.SEARCH_RESULT, true, true);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);
        }

        @Override
        public void onViewTypeClick()
        {

        }

        @Override
        public void onFilterClick()
        {

        }

        @Override
        public void finish(int resultCode)
        {
            GourmetSearchResultActivity.this.finish(resultCode);

            if (resultCode == Constants.CODE_RESULT_ACTIVITY_HOME)
            {
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                    , AnalyticsManager.Action.GOURMET_SEARCH_RESULT_CANCELED, AnalyticsManager.Label.SEARCH_RESULT_CANCELED, null);
            } else
            {
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                    , AnalyticsManager.Action.GOURMET_SEARCH_BACK_BUTTON_CLICKED, AnalyticsManager.Label.RESULT_BACK_BUTTON_CLICKED, null);
            }
        }

        @Override
        public void research(int resultCode)
        {
            GourmetSearchResultActivity.this.finish(resultCode);

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                , AnalyticsManager.Action.GOURMET_SEARCH_AGAIN_CLICKED, AnalyticsManager.Label.GOURMET_SEARCH_AGAIN_CLICKED, null);
        }

        @Override
        public void onShowCallDialog()
        {
            showCallDialog();

            AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                , AnalyticsManager.Action.CALL_INQUIRY_CLICKED, AnalyticsManager.Label.CALL_KEYWORD_GOURMET, null);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // mOnNetworkControllerListener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private GourmetSearchResultNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetSearchResultNetworkController.OnNetworkControllerListener()
    {
        private String mAddress;
        private int mSize = -100;

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

        private String getSearchDate()
        {
            String checkInDate = mSaleTime.getDayOfDaysDateFormat("yyMMdd");
            //            Calendar calendar = Calendar.getInstance();
            //            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm", Locale.KOREA);
            //
            //            return String.format("%s-%s", checkInDate, simpleDateFormat.format(calendar.getTime()));
            return String.format("%s-%s", checkInDate, DailyCalendar.format(new Date(), "yyMMddHHmm"));
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
                        action = AnalyticsManager.Action.GOURMET_KEYWORD_SEARCH_NOT_FOUND;
                        break;

                    case SEARCHTYPE_AUTOCOMPLETE:
                        action = AnalyticsManager.Action.GOURMET_AUTOCOMPLETE_KEYWORD_NOT_FOUND;

                        if (keyword.price == 0)
                        {
                            prefix = String.format("지역-%s", mInputText);
                        } else
                        {
                            prefix = String.format("고메-%s", mInputText);
                        }
                        break;

                    case SEARCHTYPE_RECENT:
                        action = AnalyticsManager.Action.GOURMET_RECENT_KEYWORD_NOT_FOUND;
                        break;

                    default:
                        action = AnalyticsManager.Action.GOURMET_KEYWORD_SEARCH_NOT_FOUND;
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
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                    , action, label, eventParams);

                Map<String, String> screenParams = Collections.singletonMap(AnalyticsManager.KeyType.KEYWORD, keyword.name);
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_SEARCH_RESULT_EMPTY);
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_SEARCH_RESULT_EMPTY, screenParams);
            } else
            {
                String prefix = null;

                switch (mSearchType)
                {
                    case SEARCHTYPE_SEARCHES:
                        action = AnalyticsManager.Action.GOURMET_KEYWORD_SEARCH_CLICKED;
                        break;

                    case SEARCHTYPE_AUTOCOMPLETE:
                        action = AnalyticsManager.Action.GOURMET_AUTOCOMPLETED_KEYWORD_CLICKED;

                        if (keyword.price == 0)
                        {
                            prefix = String.format("지역-%s", mInputText);
                        } else
                        {
                            prefix = String.format("고메-%s", mInputText);
                        }
                        break;

                    case SEARCHTYPE_RECENT:
                        action = AnalyticsManager.Action.GOURMET_RECENT_KEYWORD_SEARCH_CLICKED;
                        break;

                    default:
                        action = AnalyticsManager.Action.GOURMET_KEYWORD_SEARCH_CLICKED;
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
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                    , action, label, eventParams);

                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_SEARCH_RESULT);
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
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                    , AnalyticsManager.Action.GOURMET_AROUND_SEARCH_NOT_FOUND, label, null);

                Map<String, String> params = Collections.singletonMap(AnalyticsManager.KeyType.KEYWORD, mAddress);
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_SEARCH_RESULT_EMPTY);
                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_SEARCH_RESULT_EMPTY, params);
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

                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SEARCH//
                    , AnalyticsManager.Action.GOURMET_AROUND_SEARCH_CLICKED, label, null);

                AnalyticsManager.getInstance(GourmetSearchResultActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_SEARCH_RESULT);
            }
        }

        private void distanceBetween(Location location, ArrayList<PlaceViewItem> placeViewItemList)
        {
            ((GourmetSearchResultLayout) mPlaceSearchResultLayout).setSortType(SortType.DISTANCE);

            Gourmet gourmet;
            float[] results = new float[3];

            for (PlaceViewItem placeViewItem : placeViewItemList)
            {
                gourmet = placeViewItem.getItem();

                Location.distanceBetween(location.getLatitude(), location.getLongitude(), gourmet.latitude, gourmet.longitude, results);
                gourmet.distance = results[0];
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
                    if (size < PAGENATION_LIST_SIZE)
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
                ((GourmetSearchResultLayout) mPlaceSearchResultLayout).addSearchResultList(placeViewItemList);
            }

            mPlaceSearchResultLayout.updateResultCount(totalCount);
            unLockUI();
        }
    };
}