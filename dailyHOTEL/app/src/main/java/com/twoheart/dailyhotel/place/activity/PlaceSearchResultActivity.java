package com.twoheart.dailyhotel.place.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

public abstract class PlaceSearchResultActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_DATA_KEYWORD = "keyword";
    public static final String INTENT_EXTRA_DATA_LOCATION = "location";
    public static final String INTENT_EXTRA_DATA_SEARCHTYPE = "searchType";
    public static final String INTENT_EXTRA_DATA_INPUTTEXT = "inputText";
    public static final String INTENT_EXTRA_DATA_LATLNG = "latlng";
    public static final String INTENT_EXTRA_DATA_RADIUS = "radius";
    public static final String INTENT_EXTRA_DATA_IS_DEEPLINK = "isDeepLink";
    public static final String INTENT_EXTRA_DATA_CALL_BY_SCREEN = "callByScreen";

    protected static final double DEFAULT_SEARCH_RADIUS = 10d;

    protected ViewType mViewType = ViewType.LIST;

    protected boolean mIsFixedLocation;
    protected boolean mIsDeepLink;
    protected String mCallByScreen;

    protected int mSearchCount;
    protected int mSearchMaxCount;

    protected PlaceViewItem mPlaceViewItemByLongPress;
    protected int mListCountByLongPress;
    protected View mViewByLongPress;

    protected TodayDateTime mTodayDateTime;

    protected PlaceSearchResultLayout mPlaceSearchResultLayout;

    private DailyLocationFactory mDailyLocationFactory;

    public enum ScreenType
    {
        NONE,
        EMPTY,
        SEARCH_LOCATION,
        LIST
    }

    protected abstract PlaceSearchResultLayout getPlaceSearchResultLayout(Context context);

    protected abstract void onCalendarActivityResult(int resultCode, Intent data);

    protected abstract void onCurationActivityResult(int resultCode, Intent data);

    protected abstract void onLocationFailed();

    protected abstract void onLocationProviderDisabled();

    protected abstract void onLocationChanged(Location location);

    protected abstract void initIntent(Intent intent);

    protected abstract void initLayout();

    protected abstract Keyword getKeyword();

    protected abstract PlaceCuration getPlaceCuration();

    protected abstract void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount);

    protected abstract void requestAnalyticsByCanceled();

    protected abstract void changeViewType();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        initIntent(getIntent());

        mPlaceSearchResultLayout = getPlaceSearchResultLayout(this);

        setContentView(mPlaceSearchResultLayout.onCreateView(R.layout.activity_search_result));

        initLayout();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mPlaceSearchResultLayout != null && mPlaceSearchResultLayout.getBlurVisibility() == true)
        {
            mPlaceSearchResultLayout.setBlurVisibility(this, false);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    public void onBackPressed()
    {
        if (mViewType == ViewType.MAP)
        {
            try
            {
                if (mPlaceSearchResultLayout.getCurrentPlaceListFragment().getPlaceListLayout().getListMapFragment().isShowPlaceInformation() == true)
                {
                    mPlaceSearchResultLayout.getCurrentPlaceListFragment().getPlaceListLayout().getListMapFragment().clickMap();
                } else
                {
                    changeViewType();
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());

                changeViewType();
            }

            return;
        }

        finish(RESULT_CANCELED);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mDailyLocationFactory != null)
        {
            mDailyLocationFactory.stopLocationMeasure();
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    protected void finish(int resultCode)
    {
        if (mPlaceSearchResultLayout != null && mPlaceSearchResultLayout.isEmptyLayout() == false)
        {
            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, getKeyword());
            setResult(resultCode, intent);
        } else
        {
            setResult(resultCode);
        }

        if (resultCode == RESULT_CANCELED || resultCode == CODE_RESULT_ACTIVITY_GO_SEARCH)
        {
            requestAnalyticsByCanceled();
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                onCalendarActivityResult(resultCode, data);
                break;
            }

            case CODE_REQUEST_ACTIVITY_STAYCURATION:
            {
                onCurationActivityResult(resultCode, data);
                break;
            }

            case CODE_REQUEST_ACTIVITY_GOURMETCURATION:
            {
                onCurationActivityResult(resultCode, data);
                break;
            }

            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                if (mViewType == ViewType.MAP)
                {
                    PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();
                    placeListFragment.onActivityResult(requestCode, resultCode, data);
                } else
                {
                    searchMyLocation();
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (mViewType == ViewType.MAP)
                {
                    switch (resultCode)
                    {
                        case CODE_RESULT_ACTIVITY_GO_HOME:
                            finish(resultCode);
                            break;

                        default:
                            PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();
                            placeListFragment.onActivityResult(requestCode, resultCode, data);
                            break;
                    }
                } else
                {
                    switch (resultCode)
                    {
                        case Activity.RESULT_OK:
                            searchMyLocation();
                            break;

                        case CODE_RESULT_ACTIVITY_GO_HOME:
                            finish(resultCode);
                            break;

                        default:
                            onLocationFailed();
                            break;
                    }
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case CODE_REQUEST_ACTIVITY_SEARCH_RESULT:
            {
                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    finish(resultCode);
                } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    finish(resultCode);
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_PREVIEW:
                if (resultCode == Activity.RESULT_OK)
                {
                    Observable.create(new ObservableOnSubscribe<Object>()
                    {
                        @Override
                        public void subscribe(ObservableEmitter<Object> e) throws Exception
                        {
                            onPlaceDetailClickByLongPress(mViewByLongPress, mPlaceViewItemByLongPress, mListCountByLongPress);
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                }
                break;
        }
    }

    protected void refreshCurrentFragment(boolean isClearList)
    {
        if (isFinishing() == true)
        {
            return;
        }

        if (isClearList == true)
        {
            for (PlaceListFragment placeListFragment : mPlaceSearchResultLayout.getPlaceListFragment())
            {
                // 메인의 클리어 리스트의 경우 타화면에 영향을 줌으로 전체 리스트 데이터를 클리어함
                placeListFragment.clearList();
                // 해당 리스트의 viewType이 gone일 수 있음, 해당 경우 메인의 viewType을 따름
                placeListFragment.setViewType(mViewType);
            }
        }

        PlaceListFragment currentListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();
        if (currentListFragment != null)
        {
            currentListFragment.setPlaceCuration(getPlaceCuration());
            currentListFragment.refreshList(true);
        }
    }

    protected void searchMyLocation()
    {
        if (isFinishing() || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        if (mIsFixedLocation == true)
        {
            PlaceCuration placeCuration = getPlaceCuration();

            if (placeCuration != null)
            {
                onLocationChanged(placeCuration.getLocation());
            }
        } else
        {
            lockUI(false);

            mPlaceSearchResultLayout.clearCategoryTab();
            mPlaceSearchResultLayout.setCategoryTabLayoutVisibility(View.INVISIBLE);
            mPlaceSearchResultLayout.setScreenVisible(ScreenType.SEARCH_LOCATION);

            if (mDailyLocationFactory == null)
            {
                mDailyLocationFactory = new DailyLocationFactory(this);
            }

            if (mDailyLocationFactory.measuringLocation() == true)
            {
                return;
            }

            mDailyLocationFactory.checkLocationMeasure(new DailyLocationFactory.OnCheckLocationListener()
            {
                @Override
                public void onRequirePermission()
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    Intent intent = PermissionManagerActivity.newInstance(PlaceSearchResultActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                    startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
                }

                @Override
                public void onFailed()
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    onLocationFailed();
                }

                @Override
                public void onProviderDisabled()
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                    mDailyLocationFactory.stopLocationMeasure();

                    showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                        , getString(R.string.dialog_msg_used_gps)//
                        , getString(R.string.dialog_btn_text_dosetting)//
                        , getString(R.string.dialog_btn_text_cancel)//
                        , new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                            }
                        }, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                onLocationProviderDisabled();
                            }
                        }, false);
                }

                @Override
                public void onProviderEnabled()
                {
                    mDailyLocationFactory.startLocationMeasure(null, new DailyLocationFactory.OnLocationListener()
                    {
                        @Override
                        public void onFailed()
                        {
                            unLockUI();

                            if (isFinishing() == true)
                            {
                                return;
                            }

                            onLocationFailed();
                        }

                        @Override
                        public void onAlreadyRun()
                        {

                        }

                        @Override
                        public void onLocationChanged(Location location)
                        {
                            unLockUI();

                            if (isFinishing() == true)
                            {
                                return;
                            }

                            mDailyLocationFactory.stopLocationMeasure();

                            PlaceSearchResultActivity.this.onLocationChanged(location);
                        }
                    });
                }
            });
        }
    }

    protected void setScrollListTop()
    {
        if (isFinishing() == true)
        {
            return;
        }

        PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();
        if (placeListFragment != null)
        {
            placeListFragment.setScrollListTop();
        }
    }

    protected void recordEventSearchResultByRecentKeyword(Keyword keyword, boolean isEmpty, Map<String, String> params)
    {
        String action = (isEmpty == true) ? AnalyticsManager.Action.RECENT_KEYWORD_NOT_FOUND : AnalyticsManager.Action.RECENT_KEYWORD;
        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.RECENT);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, keyword.name);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, keyword.name);

        AnalyticsManager.getInstance(PlaceSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
            , action, keyword.name, params);
    }

    protected void recordEventSearchResultByKeyword(Keyword keyword, boolean isEmpty, Map<String, String> params)
    {
        String action = (isEmpty == true) ? AnalyticsManager.Action.KEYWORD_NOT_FOUND : AnalyticsManager.Action.KEYWORD;

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.DIRECT);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, keyword.name);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, keyword.name);

        AnalyticsManager.getInstance(PlaceSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
            , action, keyword.name, params);
    }

    protected void recordEventSearchResultByAutoSearch(Keyword keyword, String inputText, boolean isEmpty, Map<String, String> params)
    {
        String category = (isEmpty == true) ? AnalyticsManager.Category.AUTO_SEARCH_NOT_FOUND : AnalyticsManager.Category.AUTO_SEARCH;

        params.put(AnalyticsManager.KeyType.SEARCH_PATH, AnalyticsManager.ValueType.AUTO);
        params.put(AnalyticsManager.KeyType.SEARCH_WORD, inputText);
        params.put(AnalyticsManager.KeyType.SEARCH_RESULT, keyword.name);

        AnalyticsManager.getInstance(PlaceSearchResultActivity.this).recordEvent(category//
            , keyword.name, inputText, params);
    }
}
