package com.twoheart.dailyhotel.place.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.RecentlyRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchLayout;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.DailyRecentSearches;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public abstract class PlaceSearchFragment extends BaseFragment
{
    protected BaseActivity mBaseActivity;
    protected boolean mIsScrolling;

    protected static final int REQUEST_ACTIVITY_SEARCH_RESULT = 10000;
    protected static final int REQUEST_ACTIVITY_CALENDAR = 10001;

    protected static final int REQUEST_CODE_STAY_CAMPAIGN_TAG_LIST = 10002;
    protected static final int REQUEST_CODE_GOURMET_CAMPAIGN_TAG_LIST = 10003;

    protected DailyRecentSearches mDailyRecentSearches;
    protected PlaceSearchLayout mPlaceSearchLayout;
    protected OnSearchFragmentListener mOnSearchFragmentListener;

    protected CommonRemoteImpl mCommonRemoteImpl;
    protected RecentlyRemoteImpl mRecentlyRemoteImpl;
    protected RecentlyLocalImpl mRecentlyLocalImpl;
    protected CampaignTagRemoteImpl mCampaignTagRemoteImpl;
    protected SuggestRemoteImpl mSuggestRemoteImpl;

    protected TodayDateTime mTodayDateTime;
    protected ArrayList<CampaignTag> mCampaignTagList;
    protected List<Keyword> mKeywordList;

    private boolean mIsDateChanged;
    DailyLocationFactory mDailyLocationFactory;

    protected abstract int getLayoutResourceId();

    protected abstract PlaceSearchLayout getPlaceSearchLayout(Context context);

    protected abstract String getRecentSearches();

    protected abstract void writeRecentSearches(String text);

    protected abstract void onSearch(Location location);

    public abstract void startSearchResultActivity();

    public abstract void startCalendar(boolean isAnimation, SearchType searchType);

    public abstract void startCampaignTagList(int index, String title);

    public abstract Constants.ServiceType getServiceType();

    public abstract void setTodayDateTime(TodayDateTime todayDateTime);

    public abstract void setSuggestsList(String keyword, ArrayList<? extends Keyword> list);

    public abstract void requestCampaignTagList();

    public interface OnSearchFragmentListener
    {
        void finish();

        void finish(int resultCode);

        void onSearchEnabled(boolean enabled);

        void onChangeAutoCompleteScrollView(boolean isShow);

        void onSwitchClick(String keyword);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mPlaceSearchLayout = getPlaceSearchLayout(mBaseActivity);
        mCommonRemoteImpl = new CommonRemoteImpl(mBaseActivity);
        mRecentlyRemoteImpl = new RecentlyRemoteImpl(mBaseActivity);
        mRecentlyLocalImpl = new RecentlyLocalImpl(mBaseActivity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(mBaseActivity);
        mSuggestRemoteImpl = new SuggestRemoteImpl(mBaseActivity);

        View view = mPlaceSearchLayout.onCreateView(getLayoutResourceId(), container);

        initContents();

        lockUI();
        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().map(new Function<CommonDateTime, TodayDateTime>()
        {
            @Override
            public TodayDateTime apply(@NonNull CommonDateTime commonDateTime) throws Exception
            {
                TodayDateTime todayDateTime = new TodayDateTime(commonDateTime.openDateTime //
                    , commonDateTime.closeDateTime, commonDateTime.currentDateTime //
                    , commonDateTime.dailyDateTime);

                return todayDateTime;
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<TodayDateTime>()
        {
            @Override
            public void accept(@NonNull TodayDateTime todayDateTime) throws Exception
            {
                mTodayDateTime = todayDateTime;
                setTodayDateTime(todayDateTime);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));

        return view;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mDailyLocationFactory != null)
        {
            mDailyLocationFactory.stopLocationMeasure();
        }
    }

    protected void initContents()
    {
        mDailyRecentSearches = new DailyRecentSearches(getRecentSearches());
    }

    public void setOnSearchFragmentListener(OnSearchFragmentListener listener)
    {
        mOnSearchFragmentListener = listener;
    }

    public void resetSearchKeyword()
    {
        if (mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.resetSearchKeyword();
    }

    public void showSearchKeyboard()
    {
        if (mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.showSearchKeyboard();
    }

    public void hideSearchKeyboard()
    {
        if (mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.hideSearchKeyboard();
    }

    public void searchKeyword(String keyword)
    {
        if (mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.searchKeyword(keyword);
    }

    public void hideAutoCompleteLayout()
    {
        if (mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.hideAutoCompleteLayout();
    }

    public void setSearchWord(String word)
    {
        if (mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.setSearchKeyword(word);
    }

    public void updateTermsOfLocationLayout()
    {
        if (mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.updateTermsOfLocationLayout();
    }

    public void onScrollingFragment(boolean scrolling)
    {
        mIsScrolling = scrolling;
    }

    public void setDateChanged(boolean isDateChanged)
    {
        mIsDateChanged = isDateChanged;
    }

    public boolean isDateChanged()
    {
        return mIsDateChanged;
    }

    public void setRecyclerViewPosition(int position)
    {
        if (mPlaceSearchLayout == null)
        {
            return;
        }

        mPlaceSearchLayout.setRecyclerViewPosition(position);
    }

    public boolean isShowAutoCompleteScrollView()
    {
        if (mPlaceSearchLayout == null)
        {
            return false;
        }

        return mPlaceSearchLayout.isShowAutoCompleteScrollView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case REQUEST_ACTIVITY_SEARCH_RESULT:
            {
                if (mOnSearchFragmentListener == null)
                {
                    Util.restartApp(mBaseActivity);
                    return;
                }

                if (data != null)
                {
                    Keyword keyword = data.getParcelableExtra(PlaceSearchResultActivity.INTENT_EXTRA_DATA_KEYWORD);

                    if (keyword != null)
                    {
                        mDailyRecentSearches.addString(new Keyword(keyword.icon, keyword.name));

                        writeRecentSearches(mDailyRecentSearches.toString());
                        mPlaceSearchLayout.setKeywordListData(mDailyRecentSearches.getList());
                    }
                }

                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    mOnSearchFragmentListener.finish(resultCode);
                } else if (resultCode == CODE_RESULT_ACTIVITY_HOME)
                {
                    mOnSearchFragmentListener.finish();
                } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    mOnSearchFragmentListener.finish(resultCode);
                } else
                {
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                mPlaceSearchLayout.updateTermsOfLocationLayout();

                checkLocationProvider();
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                mPlaceSearchLayout.updateTermsOfLocationLayout();

                if (resultCode == Activity.RESULT_OK)
                {
                    checkLocationProvider();
                } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    mBaseActivity.setResult(resultCode);
                    mBaseActivity.finish();
                }
                break;
            }

            case REQUEST_CODE_STAY_CAMPAIGN_TAG_LIST:
            case REQUEST_CODE_GOURMET_CAMPAIGN_TAG_LIST:
            {
                if (resultCode == Constants.CODE_RESULT_ACTIVITY_REFRESH)
                {
                    requestCampaignTagList();
                }
                break;
            }
        }
    }

    protected void checkLocationProvider()
    {
        lockUI();

        if (mDailyLocationFactory == null)
        {
            mDailyLocationFactory = new DailyLocationFactory(getContext());
        }

        mDailyLocationFactory.checkLocationMeasure(new DailyLocationFactory.OnCheckLocationListener()
        {
            @Override
            public void onRequirePermission()
            {
                unLockUI();

                Intent intent = PermissionManagerActivity.newInstance(mBaseActivity, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
            }

            @Override
            public void onFailed()
            {
                unLockUI();
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

                mBaseActivity.showSimpleDialog(getString(R.string.dialog_title_used_gps)//
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
                    }, null, false);
            }

            @Override
            public void onProviderEnabled()
            {
                unLockUI();

                onSearch(null);
            }
        });
    }

    protected void searchMyLocation()
    {
        lockUI();

        if (mDailyLocationFactory == null)
        {
            mDailyLocationFactory = new DailyLocationFactory(getContext());
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

                Intent intent = PermissionManagerActivity.newInstance(mBaseActivity, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
            }

            @Override
            public void onFailed()
            {
                unLockUI();
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

                mBaseActivity.showSimpleDialog(getString(R.string.dialog_title_used_gps)//
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
                    }, null, false);
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

                        if (location == null)
                        {
                            DailyToast.showToast(mBaseActivity, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                        } else
                        {
                            // 서버
                            onSearch(location);
                        }
                    }

                    @Override
                    public void onCheckSetting(ResolvableApiException exception)
                    {
                        unLockUI();

                        try
                        {
                            exception.startResolutionForResult(mBaseActivity, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                        }catch (Exception e)
                        {

                        }
                    }
                });
            }
        });
    }
}
