package com.daily.dailyhotel.screen.home.stay.outbound.search.suggest;


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
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StayOutboundSuggestParcel;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.local.SuggestLocalImpl;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.GoogleAddressRemoteImpl;
import com.daily.dailyhotel.repository.remote.RecentlyRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.screen.home.search.stay.inbound.suggest.SearchStaySuggestActivity;
import com.daily.dailyhotel.storage.database.DailyDb;
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
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundSearchSuggestPresenter //
    extends BaseExceptionPresenter<StayOutboundSearchSuggestActivity, StayOutboundSearchSuggestViewInterface> //
    implements StayOutboundSearchSuggestView.OnEventListener
{
    private StayOutboundSearchSuggestAnalyticsInterface mAnalytics;
    private SuggestRemoteImpl mSuggestRemoteImpl;
    private SuggestLocalImpl mSuggestLocalImpl;
    private RecentlyRemoteImpl mRecentlyRemoteImpl;
    private RecentlyLocalImpl mRecentlyLocalImpl;
    private GoogleAddressRemoteImpl mGoogleAddressRemoteImpl;

    private String mKeyword;

    private DailyLocationExFactory mDailyLocationExFactory;

    public interface StayOutboundSearchSuggestAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventSuggestEmpty(Activity activity, String keyword);

        void onEventCloseClick(Activity activity);

        void onEventDeleteAllRecentlySuggestClick(Activity activity);

        void onEventSuggestClick(Activity activity, String suggestDisplayName, String keyword);

        void onEventRecentlySuggestClick(Activity activity, String suggestDisplayName, String keyword);

        void onEventPopularSuggestClick(Activity activity, String suggestDisplayName);
    }

    public StayOutboundSearchSuggestPresenter(@NonNull StayOutboundSearchSuggestActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundSearchSuggestViewInterface createInstanceViewInterface()
    {
        return new StayOutboundSearchSuggestView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundSearchSuggestActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_search_suggest_data);

        setAnalytics(new StayOutboundSearchSuggestAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mSuggestLocalImpl = new SuggestLocalImpl(activity);
        mRecentlyRemoteImpl = new RecentlyRemoteImpl(activity);
        mRecentlyLocalImpl = new RecentlyLocalImpl(activity);
        mGoogleAddressRemoteImpl = new GoogleAddressRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundSearchSuggestAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mKeyword = intent.getStringExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

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
        mAnalytics.onEventCloseClick(getActivity());

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
            case StayOutboundSearchSuggestActivity.REQUEST_CODE_SPEECH_INPUT:
            {
                if (resultCode == Activity.RESULT_OK && null != data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mKeyword = result.get(0);
                    getViewInterface().setKeywordEditText(mKeyword);
                }
                break;
            }

            case StayOutboundSearchSuggestActivity.REQUEST_CODE_PERMISSION_MANAGER:
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

            case StayOutboundSearchSuggestActivity.REQUEST_CODE_SETTING_LOCATION:
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

        Observable<StayOutbounds> obObservable = mRecentlyLocalImpl.getTargetIndices(Constants.ServiceType.OB_STAY, DailyDb.MAX_RECENT_PLACE_COUNT) //
            .observeOn(Schedulers.io()).flatMap(new Function<String, ObservableSource<StayOutbounds>>()
            {
                @Override
                public ObservableSource<StayOutbounds> apply(@io.reactivex.annotations.NonNull String targetIndices) throws Exception
                {
                    return mRecentlyRemoteImpl.getStayOutboundRecentlyList(targetIndices, DailyDb.MAX_RECENT_PLACE_COUNT);
                }
            });

        addCompositeDisposable(Observable.zip(obObservable //
            , mSuggestLocalImpl.getRecentlyStayOutboundSuggestList(), new BiFunction<StayOutbounds, List<StayOutboundSuggest>, List<StayOutboundSuggest>>()
            {
                @Override
                public List<StayOutboundSuggest> apply(StayOutbounds stayOutbounds, List<StayOutboundSuggest> stayOutboundSuggestList) throws Exception
                {
                    List<StayOutbound> stayOutboundList = stayOutbounds.getStayOutbound();
                    List<StayOutboundSuggest> mergeList = new ArrayList<>();

                    if (stayOutboundList != null && stayOutboundList.size() > 0)
                    {
                        StayOutboundSuggest stayOutboundSuggest = new StayOutboundSuggest(0, getString(R.string.label_recently_stay));
                        stayOutboundSuggest.menuType = StayOutboundSuggest.MENU_TYPE_RECENTLY_STAY;
                        mergeList.add(stayOutboundSuggest);

                        for (StayOutbound stayOutbound : stayOutboundList)
                        {
                            stayOutboundSuggest = new StayOutboundSuggest();
                            stayOutboundSuggest.id = stayOutbound.index;
                            stayOutboundSuggest.name = stayOutbound.name;
                            stayOutboundSuggest.city = stayOutbound.city;
                            //                            stayOutboundSuggest.country = stayOutbound.country;
                            //                            stayOutboundSuggest.countryCode = stayOutbound.countryCode;
                            stayOutboundSuggest.categoryKey = StayOutboundSuggest.CATEGORY_HOTEL;
                            stayOutboundSuggest.display = stayOutbound.name;
                            stayOutboundSuggest.latitude = stayOutbound.latitude;
                            stayOutboundSuggest.longitude = stayOutbound.longitude;
                            stayOutboundSuggest.menuType = StayOutboundSuggest.MENU_TYPE_RECENTLY_STAY;

                            mergeList.add(stayOutboundSuggest);
                        }
                    }

                    if (stayOutboundSuggestList != null && stayOutboundSuggestList.size() > 0)
                    {
                        StayOutboundSuggest stayOutboundSuggest = new StayOutboundSuggest(0, getString(R.string.label_search_suggest_recently_search));
                        stayOutboundSuggest.menuType = StayOutboundSuggest.MENU_TYPE_RECENTLY_SEARCH;
                        mergeList.add(stayOutboundSuggest);

                        mergeList.addAll(stayOutboundSuggestList);
                    }

                    return mergeList;
                }
            }).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<List<StayOutboundSuggest>, ObservableSource<List<StayOutboundSuggest>>>()
        {
            @Override
            public ObservableSource<List<StayOutboundSuggest>> apply(List<StayOutboundSuggest> stayOutboundSuggestList) throws Exception
            {
                boolean visible = stayOutboundSuggestList != null && stayOutboundSuggestList.size() > 0;
                getViewInterface().setRecentlySuggests(stayOutboundSuggestList);
                getViewInterface().setRecentlySuggestsVisible(visible);
                getViewInterface().setPopularSuggestsVisible(visible == false);

                return visible == false ? mSuggestRemoteImpl.getPopularRegionSuggestsByStayOutbound() : Observable.just(new ArrayList<>());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutboundSuggest>>()
        {
            @Override
            public void accept(List<StayOutboundSuggest> stayOutboundSuggestList) throws Exception
            {
                getViewInterface().setPopularAreaSuggests(stayOutboundSuggestList);

                startSearchMyLocation(false);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().setRecentlySuggests(null);
                getViewInterface().setRecentlySuggestsVisible(false);
                getViewInterface().setPopularSuggestsVisible(true);

                startSearchMyLocation(false);

                unLockAll();
            }
        }));
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
        clearCompositeDisposable();

        mKeyword = keyword;

        getViewInterface().setEmptySuggestsVisible(false);
        getViewInterface().setProgressBarVisible(true);

        if (DailyTextUtils.isTextEmpty(keyword) == true)
        {
            onSuggestList(null);
        } else
        {
            addCompositeDisposable(mSuggestRemoteImpl.getSuggestsByStayOutbound(keyword)//
                .delaySubscription(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<List<StayOutboundSuggest>>()
                {
                    @Override
                    public void accept(List<StayOutboundSuggest> stayOutboundSuggests) throws Exception
                    {
                        StayOutboundSearchSuggestPresenter.this.onSuggestList(stayOutboundSuggests);
                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        StayOutboundSearchSuggestPresenter.this.onSuggestList(null);
                        unLockAll();
                    }
                }));
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

        try
        {
            mAnalytics.onEventSuggestClick(getActivity(), stayOutboundSuggest.display, mKeyword);
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
        }

        getViewInterface().setKeywordEditText(stayOutboundSuggest.display);
        startFinishAction(stayOutboundSuggest, mKeyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_AUTO);
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
                        mAnalytics.onEventRecentlySuggestClick(getActivity(), stayOutboundSuggest.display, keyword);
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
                        mAnalytics.onEventRecentlySuggestClick(getActivity(), stayOutboundSuggest.display, "");
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

        addCompositeDisposable(mSuggestLocalImpl.addStayOutboundSuggestDb(stayOutboundSuggest, mKeyword).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                try
                {
                    mAnalytics.onEventPopularSuggestClick(getActivity(), stayOutboundSuggest.display);
                } catch (Exception e)
                {
                    ExLog.d(e.getMessage());
                }

                startFinishAction(stayOutboundSuggest, mKeyword, AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                try
                {
                    mAnalytics.onEventPopularSuggestClick(getActivity(), stayOutboundSuggest.display);
                } catch (Exception e)
                {
                    ExLog.d(e.getMessage());
                }

                startFinishAction(stayOutboundSuggest, "", AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND);
            }
        }));
    }

    void startFinishAction(StayOutboundSuggest stayOutboundSuggest, String keyword, String analyticsClickType)
    {
        Intent intent = new Intent();
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_SUGGEST, new StayOutboundSuggestParcel(stayOutboundSuggest));
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_KEYWORD, keyword);
        intent.putExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_CLICK_TYPE, DailyTextUtils.isTextEmpty(analyticsClickType) ? "" : analyticsClickType);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDeleteAllRecentlySuggest(boolean skipLocked)
    {
        if (skipLocked == false && lock() == true)
        {
            return;
        }

        getViewInterface().setRecentlySuggests(null);
        getViewInterface().setRecentlySuggestsVisible(false);
        getViewInterface().setPopularSuggestsVisible(true);

        addCompositeDisposable(Observable.zip(mRecentlyLocalImpl.clearRecentlyItems(Constants.ServiceType.OB_STAY) //
            , mSuggestLocalImpl.deleteAllRecentlyStayOutboundSuggest(), new BiFunction<Boolean, Boolean, Boolean>()
            {
                @Override
                public Boolean apply(Boolean aBoolean, Boolean aBoolean2) throws Exception
                {
                    return true;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                mAnalytics.onEventDeleteAllRecentlySuggestClick(getActivity());

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                mAnalytics.onEventDeleteAllRecentlySuggestClick(getActivity());

                unLockAll();
            }
        }));
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

        if (getViewInterface().getRecentlySuggestEntryCount() == 0)
        {
            onDeleteAllRecentlySuggest(true);
            return;
        }

        if (StayOutboundSuggest.MENU_TYPE_RECENTLY_STAY == stayOutboundSuggest.menuType)
        {
            addCompositeDisposable(mRecentlyLocalImpl.deleteRecentlyItem(Constants.ServiceType.OB_STAY, (int) stayOutboundSuggest.id) //
                .flatMap(new Function<Boolean, ObservableSource<ArrayList<RecentlyDbPlace>>>()
                {
                    @Override
                    public ObservableSource<ArrayList<RecentlyDbPlace>> apply(Boolean aBoolean) throws Exception
                    {
                        return mRecentlyLocalImpl.getRecentlyTypeList(Constants.ServiceType.OB_STAY);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<RecentlyDbPlace>>()
                {
                    @Override
                    public void accept(ArrayList<RecentlyDbPlace> recentlyDbPlaces) throws Exception
                    {
                        if (recentlyDbPlaces.size() == 0)
                        {
                            getViewInterface().removeRecentlySection(StaySuggest.MENU_TYPE_RECENTLY_STAY);
                        }

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
        } else
        {
            addCompositeDisposable(mSuggestLocalImpl.deleteRecentlyStayOutboundSuggest(stayOutboundSuggest.id) //
                .flatMap(new Function<Boolean, ObservableSource<List<StayOutboundSuggest>>>()
                {
                    @Override
                    public ObservableSource<List<StayOutboundSuggest>> apply(Boolean aBoolean) throws Exception
                    {
                        return mSuggestLocalImpl.getRecentlyStayOutboundSuggestList();
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutboundSuggest>>()
                {
                    @Override
                    public void accept(List<StayOutboundSuggest> stayOutboundSuggestList) throws Exception
                    {
                        if (stayOutboundSuggestList.size() == 0)
                        {
                            getViewInterface().removeRecentlySection(StaySuggest.MENU_TYPE_RECENTLY_SEARCH);
                        }

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

    void onSuggestList(List<StayOutboundSuggest> stayOutboundSuggestList)
    {
        getViewInterface().setProgressBarVisible(false);

        if (stayOutboundSuggestList == null || stayOutboundSuggestList.size() == 0)
        {
            getViewInterface().setSuggestsVisible(false);

            boolean isShowEmpty = DailyTextUtils.isTextEmpty(mKeyword) == false;
            getViewInterface().setEmptySuggestsVisible(isShowEmpty);

            mAnalytics.onEventSuggestEmpty(getActivity(), mKeyword);
        } else
        {
            getViewInterface().setSuggestsVisible(true);
            getViewInterface().setEmptySuggestsVisible(false);
        }

        getViewInterface().setSuggests(stayOutboundSuggestList);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void startSearchMyLocation(boolean isUserClick)
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
                StayOutboundSuggest locationStayOutboundSuggest = new StayOutboundSuggest(0, null);
                locationStayOutboundSuggest.categoryKey = StayOutboundSuggest.CATEGORY_LOCATION;
                locationStayOutboundSuggest.menuType = StayOutboundSuggest.MENU_TYPE_LOCATION;
                locationStayOutboundSuggest.latitude = location.getLatitude();
                locationStayOutboundSuggest.longitude = location.getLongitude();

                addCompositeDisposable(mGoogleAddressRemoteImpl.getLocationAddress(location.getLatitude(), location.getLongitude()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String address) throws Exception
                    {
                        locationStayOutboundSuggest.display = address;

                        getViewInterface().setNearbyStaySuggest(true, locationStayOutboundSuggest);

                        if (isUserClick == false)
                        {
                            return;
                        }

                        unLockAll();

                        getViewInterface().setKeywordEditText(locationStayOutboundSuggest.display);
                        startFinishAction(locationStayOutboundSuggest, mKeyword, null);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        getViewInterface().setNearbyStaySuggest(true, locationStayOutboundSuggest);

                        if (isUserClick == false)
                        {
                            return;
                        }

                        unLockAll();

                        locationStayOutboundSuggest.display = getString(R.string.label_search_nearby_empty_address);

                        getViewInterface().setKeywordEditText(locationStayOutboundSuggest.display);
                        startFinishAction(locationStayOutboundSuggest, mKeyword, null);
                    }
                }));

            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                boolean isAgreePermission = true;
                String displayName = null;

                if (throwable instanceof PermissionException)
                {
                    displayName = getString(R.string.label_search_nearby_description);
                    isAgreePermission = false;
                }

                StayOutboundSuggest locationStayOutboundSuggest = new StayOutboundSuggest(0, null);
                locationStayOutboundSuggest.categoryKey = StayOutboundSuggest.CATEGORY_LOCATION;
                locationStayOutboundSuggest.menuType = StayOutboundSuggest.MENU_TYPE_LOCATION;
                locationStayOutboundSuggest.display = displayName;

                getViewInterface().setNearbyStaySuggest(isAgreePermission, locationStayOutboundSuggest);

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
                                //                                unLockAll();

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
            //            unLockAll();
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
