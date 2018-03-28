package com.daily.dailyhotel.screen.home.search.stay.outbound.suggest;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.GoogleAddress;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StayOutboundSuggestParcel;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.local.SuggestLocalImpl;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.GoogleAddressRemoteImpl;
import com.daily.dailyhotel.repository.remote.RecentlyRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.screen.home.search.stay.inbound.suggest.SearchStaySuggestActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayOutboundSuggestPresenter //
    extends BaseExceptionPresenter<SearchStayOutboundSuggestActivity, SearchStayOutboundSuggestInterface> //
    implements SearchStayOutboundSuggestView.OnEventListener
{
    SearchStayOutboundSuggestAnalyticsInterface mAnalytics;
    private SuggestRemoteImpl mSuggestRemoteImpl;
    SuggestLocalImpl mSuggestLocalImpl;
    RecentlyRemoteImpl mRecentlyRemoteImpl;
    private RecentlyLocalImpl mRecentlyLocalImpl;
    GoogleAddressRemoteImpl mGoogleAddressRemoteImpl;
    private Disposable mSuggestDisposable;

    private List<StayOutboundSuggest> mPopularAreaList;
    private List<StayOutboundSuggest> mRecentlySuggestList;
    private List<StayOutboundSuggest> mSuggestList;
    StayOutboundSuggest mLocationSuggest;
    String mKeyword;

    DailyLocationExFactory mDailyLocationExFactory;

    public interface SearchStayOutboundSuggestAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onSearchSuggestList(Activity activity, String keyword, boolean hasStayOutboundSuggestList);

        void onDeleteRecentlySearch(Activity activity, String keyword);

        void onVoiceSearchClick(Activity activity);

        void onLocationSearchNoAddressClick(Activity activity);

        void onDeleteRecentlyStayOutbound(Activity activity);

        void onScreen(Activity activity);
    }

    public SearchStayOutboundSuggestPresenter(@NonNull SearchStayOutboundSuggestActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SearchStayOutboundSuggestInterface createInstanceViewInterface()
    {
        return new SearchStayOutboundSuggestView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SearchStayOutboundSuggestActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_search_suggest_data);

        setAnalytics(new SearchStayOutboundSuggestAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mSuggestLocalImpl = new SuggestLocalImpl(activity);
        mRecentlyRemoteImpl = new RecentlyRemoteImpl(activity);
        mRecentlyLocalImpl = new RecentlyLocalImpl(activity);
        mGoogleAddressRemoteImpl = new GoogleAddressRemoteImpl(activity);

        boolean isAgreeLocation = DailyPreference.getInstance(activity).isAgreeTermsOfLocation();

        mLocationSuggest = new StayOutboundSuggest(0, null);
        mLocationSuggest.display = isAgreeLocation ? getString(R.string.label_search_nearby_empty_address) : getString(R.string.label_search_nearby_description);
        mLocationSuggest.displayText = isAgreeLocation ? getString(R.string.label_search_nearby_empty_address) : getString(R.string.label_search_nearby_description);
        mLocationSuggest.categoryKey = StayOutboundSuggest.CATEGORY_LOCATION;
        mLocationSuggest.menuType = StayOutboundSuggest.MENU_TYPE_LOCATION;

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchStayOutboundSuggestAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mKeyword = intent.getStringExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        if (getViewInterface() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mKeyword) == false)
        {
            getViewInterface().setKeywordEditText(mKeyword);
            //            return;
        }

        String searchKeywordEditHint = DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigSearchStayOutboundSuggestHint();
        if (DailyTextUtils.isTextEmpty(searchKeywordEditHint) == false)
        {
            getViewInterface().setKeywordEditHint(searchKeywordEditHint);
        }

        setCheckVoiceSearchEnabled();

        getViewInterface().showKeyboard();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        try
        {
            mAnalytics.onScreen(getActivity());
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case SearchStayOutboundSuggestActivity.REQUEST_CODE_SPEECH_INPUT:
            {
                if (resultCode == Activity.RESULT_OK && null != data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mKeyword = result.get(0);
                    getViewInterface().setKeywordEditText(mKeyword);
                }
                break;
            }

            case SearchStayOutboundSuggestActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        startSearchMyLocation(true);
                        break;

                    default:
                        // 권한 설정을 하지 않았을때 아무것도 하지 않음
                        break;
                }
                break;
            }

            case SearchStayOutboundSuggestActivity.REQUEST_CODE_SETTING_LOCATION:
                startSearchMyLocation(true);
                break;
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        // 최근 검색어, 인기 지역 , 최근 본 업장 순
        addCompositeDisposable(Observable.zip( //
            mSuggestLocalImpl.getRecentlyStayOutboundSuggestList(SearchStayOutboundSuggestActivity.RECENTLY_PLACE_MAX_REQUEST_COUNT) //
            , mSuggestRemoteImpl.getPopularRegionSuggestsByStayOutbound() //
            , mRecentlyLocalImpl.getRecentlyTypeList(Constants.ServiceType.OB_STAY) //
            , new Function3<List<StayOutboundSuggest>, List<StayOutboundSuggest>, ArrayList<RecentlyDbPlace>, List<StayOutboundSuggest>>()
            {

                @Override
                public List<StayOutboundSuggest> apply(List<StayOutboundSuggest> stayOutboundRecentlySuggestList //
                    , List<StayOutboundSuggest> stayOutboundPopularList //
                    , ArrayList<RecentlyDbPlace> recentlyDbPlaceList) throws Exception
                {
                    if (stayOutboundPopularList != null && stayOutboundPopularList.size() > 0)
                    {
                        StayOutboundSuggest stayOutboundSuggest = new StayOutboundSuggest(0, getString(R.string.label_search_suggest_popular_area));
                        stayOutboundSuggest.menuType = StayOutboundSuggest.MENU_TYPE_POPULAR_AREA;
                        stayOutboundPopularList.add(0, stayOutboundSuggest);
                    }

                    // 인기지역
                    setPopularAreaList(stayOutboundPopularList);

                    // 최근 본업장, 최근 검색어
                    List<StayOutboundSuggest> mergeList = getRecentlySuggestList(recentlyDbPlaceList, stayOutboundRecentlySuggestList);
                    setRecentlySuggestList(mergeList);

                    return mergeList;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutboundSuggest>>()
        {
            @Override
            public void accept(List<StayOutboundSuggest> stayOutboundSuggestList) throws Exception
            {
                notifyDataSetChanged();

                startSearchMyLocation(false);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                notifyDataSetChanged();

                startSearchMyLocation(false);

                unLockAll();
            }
        }));
    }

    void setPopularAreaList(List<StayOutboundSuggest> popularAreaList)
    {
        mPopularAreaList = popularAreaList;
    }

    List<StayOutboundSuggest> getRecentlySuggestList(ArrayList<RecentlyDbPlace> recentlyDbPlaceList, List<StayOutboundSuggest> recentlySuggestList)
    {
        List<StayOutboundSuggest> mergeList = new ArrayList<>();

        if (recentlySuggestList != null && recentlySuggestList.size() > 0)
        {
            StayOutboundSuggest stayOutboundSuggest = new StayOutboundSuggest(0, getString(R.string.label_search_suggest_recently_search));
            stayOutboundSuggest.menuType = StayOutboundSuggest.MENU_TYPE_RECENTLY_SEARCH;
            mergeList.add(stayOutboundSuggest);

            mergeList.addAll(recentlySuggestList);
        }

        // 최근 본 업장
        if (recentlyDbPlaceList != null && recentlyDbPlaceList.size() > 0)
        {
            StayOutboundSuggest headerSuggest = new StayOutboundSuggest(0, getString(R.string.label_recently_stay));
            headerSuggest.menuType = StayOutboundSuggest.MENU_TYPE_RECENTLY_STAY;
            mergeList.add(headerSuggest);

            int maxSize = Math.min(10, recentlyDbPlaceList.size());

            for (int i = 0; i < maxSize; i++)
            {
                RecentlyDbPlace recentlyPlace = recentlyDbPlaceList.get(i);

                StayOutboundSuggest suggest = new StayOutboundSuggest(recentlyPlace.index, recentlyPlace.name);
                suggest.categoryKey = StayOutboundSuggest.CATEGORY_HOTEL;
                suggest.country = recentlyPlace.regionName;
                suggest.menuType = StayOutboundSuggest.MENU_TYPE_RECENTLY_STAY;

                mergeList.add(suggest);
            }
        }

        return mergeList;
    }

    void setRecentlySuggestList(List<StayOutboundSuggest> recentlySuggestList)
    {
        mRecentlySuggestList = recentlySuggestList;
    }

    void setSuggestList(List<StayOutboundSuggest> suggestList)
    {
        if (suggestList != null && suggestList.size() > 0)
        {
            for (StayOutboundSuggest suggest : suggestList)
            {
                suggest.menuType = StayOutboundSuggest.MENU_TYPE_SUGGEST;
            }
        }

        mSuggestList = suggestList;
    }

    void notifyDataSetChanged()
    {
        if (DailyTextUtils.isTextEmpty(mKeyword) == false)
        {
            if (mSuggestList == null || mSuggestList.size() == 0)
            {
                getViewInterface().setSuggests(mSuggestList);
                getViewInterface().setEmptySuggestsVisible(true);
                return;
            }

            getViewInterface().setSuggests(mSuggestList);
            getViewInterface().setEmptySuggestsVisible(false);
            return;
        }

        // 추천 검색어의 경우 검색어가 있을때만 작동 해야 함
        setSuggestList(null);
        getViewInterface().setEmptySuggestsVisible(false);

        if (mRecentlySuggestList != null && mRecentlySuggestList.size() > 0)
        {
            getViewInterface().setRecentlySuggests(mLocationSuggest, mRecentlySuggestList);
            return;
        }

        getViewInterface().setPopularAreaSuggests(mLocationSuggest, mPopularAreaList);
    }

    @Override
    public void onBackClick()
    {
        addCompositeDisposable(Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (getViewInterface() != null)
                {
                    getViewInterface().hideKeyboard();
                }

                return Observable.just(true);
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                getActivity().onBackPressed();
            }
        }));
    }

    @Override
    public void onSearchSuggest(String keyword)
    {
        //        clearCompositeDisposable();
        removeCompositeDisposable(mSuggestDisposable);

        mKeyword = keyword;

        getViewInterface().setEmptySuggestsVisible(false);
        getViewInterface().setProgressBarVisible(true);

        if (DailyTextUtils.isTextEmpty(keyword) == true)
        {
            setSuggestList(null);
            notifyDataSetChanged();

            getViewInterface().setProgressBarVisible(false);
            unLockAll();
        } else
        {
            mSuggestDisposable = mSuggestRemoteImpl.getSuggestsByStayOutbound(keyword)//
                .delaySubscription(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<List<StayOutboundSuggest>>()
                {
                    @Override
                    public void accept(List<StayOutboundSuggest> stayOutboundSuggests) throws Exception
                    {
                        setSuggestList(stayOutboundSuggests);
                        notifyDataSetChanged();

                        getViewInterface().setProgressBarVisible(false);
                        unLockAll();

                        try
                        {
                            boolean hasStayOutboundSuggestList = stayOutboundSuggests != null && stayOutboundSuggests.size() > 0;
                            mAnalytics.onSearchSuggestList(getActivity(), keyword, hasStayOutboundSuggestList);
                        } catch (Exception e)
                        {
                            ExLog.d(e.getMessage());
                        }
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        setSuggestList(null);
                        notifyDataSetChanged();

                        getViewInterface().setProgressBarVisible(false);
                        unLockAll();

                        try
                        {
                            mAnalytics.onSearchSuggestList(getActivity(), keyword, false);
                        } catch (Exception e)
                        {
                            ExLog.d(e.getMessage());
                        }
                    }
                });

            addCompositeDisposable(mSuggestDisposable);
        }
    }

    @Override
    public void onSuggestClick(StayOutboundSuggest stayOutboundSuggest)
    {
        if (stayOutboundSuggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mSuggestLocalImpl.addStayOutboundSuggestDb(stayOutboundSuggest, mKeyword) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    try
                    {

                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    startFinishAction(stayOutboundSuggest, mKeyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_AUTO);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    try
                    {

                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    startFinishAction(stayOutboundSuggest, mKeyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_AUTO);
                }
            }));
    }

    @Override
    public void onRecentlySuggestClick(StayOutboundSuggest stayOutboundSuggest)
    {
        if (stayOutboundSuggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mSuggestLocalImpl.getRecentlyStayOutboundSuggestKeyword(stayOutboundSuggest.id) //
            .flatMap(new Function<String, ObservableSource<String>>()
            {
                @Override
                public ObservableSource<String> apply(String keyword) throws Exception
                {
                    return mSuggestLocalImpl.addStayOutboundSuggestDb(stayOutboundSuggest, keyword).map(new Function<Boolean, String>()
                    {
                        @Override
                        public String apply(Boolean aBoolean) throws Exception
                        {
                            return keyword;
                        }
                    });
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(String keyword) throws Exception
                {
                    try
                    {

                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    startFinishAction(stayOutboundSuggest, keyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECENT);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    try
                    {

                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    startFinishAction(stayOutboundSuggest, "", AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECENT);
                }
            }));
    }

    @Override
    public void onPopularSuggestClick(StayOutboundSuggest stayOutboundSuggest)
    {
        if (stayOutboundSuggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mSuggestLocalImpl.addStayOutboundSuggestDb(stayOutboundSuggest, mKeyword) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    startFinishAction(stayOutboundSuggest, mKeyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    startFinishAction(stayOutboundSuggest, mKeyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND);
                }
            }));
    }

    void startFinishAction(StayOutboundSuggest stayOutboundSuggest, String keyword, String analyticsClickType)
    {
        Intent intent = new Intent();
        intent.putExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_SUGGEST, new StayOutboundSuggestParcel(stayOutboundSuggest));
        intent.putExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_KEYWORD, keyword);
        intent.putExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_CLICK_TYPE, DailyTextUtils.isTextEmpty(analyticsClickType) ? "" : analyticsClickType);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    void startFinishAction(StaySuggest staySuggest, StayOutboundSuggest stayOutboundSuggest, String keyword)
    {
        Intent intent = new Intent();
        intent.putExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(staySuggest));
        intent.putExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_KEYWORD, keyword);

        if (stayOutboundSuggest != null)
        {
            intent.putExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_SUGGEST_2, new StayOutboundSuggestParcel(stayOutboundSuggest));
        }

        setResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY, intent);
        finish();
    }

    @Override
    public void onDeleteRecentlySuggest(int position, StayOutboundSuggest stayOutboundSuggest)
    {
        if (getViewInterface() == null || stayOutboundSuggest == null || position < 0)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        getViewInterface().removeRecentlyItem(position);

        if (getViewInterface().getRecentlySuggestAllEntryCount() == 0)
        {
            setRecentlySuggestList(null);
            notifyDataSetChanged();
        }

        if (StayOutboundSuggest.MENU_TYPE_RECENTLY_STAY == stayOutboundSuggest.menuType)
        {
            addCompositeDisposable(mRecentlyLocalImpl.deleteRecentlyItem(Constants.ServiceType.OB_STAY, (int) stayOutboundSuggest.id) //
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        unLockAll();
                    }
                }));

            try
            {
                mAnalytics.onDeleteRecentlyStayOutbound(getActivity());
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
            }
        } else
        {
            addCompositeDisposable(mSuggestLocalImpl.deleteRecentlyStayOutboundSuggest(stayOutboundSuggest.id) //
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        unLockAll();
                    }
                }));

            try
            {
                mAnalytics.onDeleteRecentlySearch(getActivity(), stayOutboundSuggest.display);
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
            }
        }
    }

    @Override
    public void onVoiceSearchClick()
    {
        if (getViewInterface() == null || isVoiceSearchEnabled() == false)
        {
            return;
        }

        getViewInterface().hideKeyboard();

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.label_search_suggest_voice_search_title));

        try
        {
            startActivityForResult(intent, SearchStaySuggestActivity.REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a)
        {
            DailyToast.showToast(getActivity(), R.string.message_search_suggest_voice_search_error, DailyToast.LENGTH_SHORT);
            getViewInterface().setVoiceSearchEnabled(false);
        }

        try
        {
            mAnalytics.onVoiceSearchClick(getActivity());
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
        }
    }

    @Override
    public void setCheckVoiceSearchEnabled()
    {
        if (getViewInterface() == null)
        {
            return;
        }

        getViewInterface().setVoiceSearchEnabled(isVoiceSearchEnabled());
    }

    @Override
    public void onNearbyClick(StayOutboundSuggest stayOutboundSuggest)
    {
        startSearchMyLocation(true);
    }

    private boolean isVoiceSearchEnabled()
    {
        if (getActivity() == null)
        {
            return false;
        }

        List<ResolveInfo> activities = getActivity().getPackageManager() //
            .queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        return activities.size() > 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void startSearchMyLocation(boolean isUserClick)
    {
        Observable<Location> observable = searchMyLocation(isUserClick);

        if (observable == null)
        {
            ExLog.e("sam - observable is null");
            return;
        }

        if (isUserClick == true)
        {
            screenLock(true);
        }

        addCompositeDisposable(observable.subscribe(new Consumer<Location>()
        {
            @Override
            public void accept(Location location) throws Exception
            {
                //                mLocationSuggest.display = null;
                mLocationSuggest.display = getString(R.string.label_search_nearby_empty_address);
                mLocationSuggest.displayText = getString(R.string.label_search_nearby_empty_address);
                mLocationSuggest.latitude = location.getLatitude();
                mLocationSuggest.longitude = location.getLongitude();

                addCompositeDisposable(mGoogleAddressRemoteImpl.getLocationAddress(location.getLatitude(), location.getLongitude()) //
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GoogleAddress>()
                    {
                        @Override
                        public void accept(GoogleAddress address) throws Exception
                        {
                            mLocationSuggest.display = address.address;
                            mLocationSuggest.displayText = address.address;
                            mLocationSuggest.city = address.shortAddress;

                            getViewInterface().setNearbyStaySuggest(mLocationSuggest);

                            if (isUserClick == false)
                            {
                                return;
                            }

                            if ("KR".equalsIgnoreCase(address.shortCountry))
                            {
                                StaySuggest.Location itemLocation = new StaySuggest.Location();
                                itemLocation.address = address.address;
                                itemLocation.name = address.shortAddress;
                                itemLocation.latitude = mLocationSuggest.latitude;
                                itemLocation.longitude = mLocationSuggest.longitude;

                                StaySuggest staySuggest = new StaySuggest(StaySuggest.MenuType.LOCATION, itemLocation);

                                unLockAll();

                                getViewInterface().setSuggest(itemLocation.address);
                                startFinishAction(staySuggest, mLocationSuggest, mKeyword);
                            } else
                            {
                                addCompositeDisposable(mSuggestLocalImpl.addStayOutboundSuggestDb(mLocationSuggest, mKeyword) //
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                                    {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception
                                        {
                                            unLockAll();

                                            getViewInterface().setSuggest(mLocationSuggest.display);
                                            startFinishAction(mLocationSuggest, mKeyword, null);
                                        }
                                    }, new Consumer<Throwable>()
                                    {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception
                                        {
                                            unLockAll();

                                            getViewInterface().setSuggest(mLocationSuggest.display);
                                            startFinishAction(mLocationSuggest, mKeyword, null);
                                        }
                                    }));
                            }
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(Throwable throwable) throws Exception
                        {
                            getViewInterface().setNearbyStaySuggest(mLocationSuggest);

                            if (isUserClick == false)
                            {
                                return;
                            }

                            unLockAll();

                            getViewInterface().setSuggest(mLocationSuggest.displayText);

                            try
                            {
                                mAnalytics.onLocationSearchNoAddressClick(getActivity());
                            } catch (Exception e)
                            {
                                ExLog.d(e.getMessage());
                            }

                            startFinishAction(mLocationSuggest, mKeyword, null);
                        }
                    }));
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                String displayName = null;

                if (throwable instanceof PermissionException)
                {
                    displayName = getString(R.string.label_search_nearby_description);
                }

                mLocationSuggest.display = displayName;
                mLocationSuggest.displayText = displayName;

                getViewInterface().setNearbyStaySuggest(mLocationSuggest);

                if (isUserClick == false)
                {
                    return;
                }

                unLockAll();
            }
        }));
    }

    private Observable<Location> searchMyLocation(boolean isUserClick)
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory(getActivity());
        }

        if (mDailyLocationExFactory.measuringLocation() == true)
        {
            // 이미 동작 하고 있음
            return null;
        }

        return new Observable<Location>()
        {
            @Override
            protected void subscribeActual(Observer<? super Location> observer)
            {
                mDailyLocationExFactory.checkLocationMeasure(new DailyLocationExFactory.OnCheckLocationListener()
                {
                    @Override
                    public void onRequirePermission()
                    {
                        observer.onError(new PermissionException());
                    }

                    @Override
                    public void onFailed()
                    {
                        observer.onError(new Exception());
                    }

                    @Override
                    public void onProviderEnabled()
                    {
                        mDailyLocationExFactory.startLocationMeasure(new DailyLocationExFactory.OnLocationListener()
                        {
                            @Override
                            public void onFailed()
                            {
                                observer.onError(new Exception());
                            }

                            @Override
                            public void onAlreadyRun()
                            {
                                observer.onError(new DuplicateRunException());
                            }

                            @Override
                            public void onLocationChanged(Location location)
                            {
                                mDailyLocationExFactory.stopLocationMeasure();

                                if (location == null)
                                {
                                    observer.onError(new NullPointerException());
                                } else
                                {
                                    observer.onNext(location);
                                    observer.onComplete();
                                }
                            }

                            @Override
                            public void onCheckSetting(ResolvableApiException exception)
                            {
                                observer.onError(exception);
                            }
                        });
                    }

                    @Override
                    public void onProviderDisabled()
                    {
                        observer.onError(new ProviderException());
                    }
                });
            }
        }.doOnError(throwable ->
        {
            if (isUserClick == false)
            {
                // 화면 진입 시 처리 임
                return;
            }

            unLockAll();


            if (throwable instanceof PermissionException)
            {
                Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, SearchStaySuggestActivity.REQUEST_CODE_PERMISSION_MANAGER);
            } else if (throwable instanceof ProviderException)
            {
                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                View.OnClickListener positiveListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, SearchStaySuggestActivity.REQUEST_CODE_SETTING_LOCATION);
                    }
                };

                View.OnClickListener negativeListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                    }
                };

                DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                    }
                };

                getViewInterface().showSimpleDialog(//
                    getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), //
                    getString(R.string.dialog_btn_text_dosetting), //
                    getString(R.string.dialog_btn_text_cancel), //
                    positiveListener, negativeListener, cancelListener, null, true);
            } else if (throwable instanceof DuplicateRunException)
            {

            } else if (throwable instanceof ResolvableApiException)
            {
                try
                {
                    ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), SearchStaySuggestActivity.REQUEST_CODE_SETTING_LOCATION);
                } catch (Exception e)
                {
                    getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            } else
            {
                getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
            }
        });
    }
}
