package com.daily.dailyhotel.screen.home.search.gourmet.suggest;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.parcel.GourmetSuggestParcel;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.GoogleAddressRemoteImpl;
import com.daily.dailyhotel.repository.remote.RecentlyRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.network.model.GourmetKeyword;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyRecentSearches;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public class SearchGourmetSuggestPresenter extends BaseExceptionPresenter<SearchGourmetSuggestActivity, SearchGourmetSuggestInterface> implements SearchGourmetSuggestView.OnEventListener
{
    private SearchGourmetSuggestAnalyticsInterface mAnalytics;

    private SuggestRemoteImpl mSuggestRemoteImpl;
    private RecentlyRemoteImpl mRecentlyRemoteImpl;
    private RecentlyLocalImpl mRecentlyLocalImpl;
    private GoogleAddressRemoteImpl mGoogleAddressRemoteImpl;

    private DailyRecentSearches mDailyRecentSearches;
    private GourmetBookDateTime mGourmetBookDateTime;
    private String mKeyword;

    private DailyLocationExFactory mDailyLocationExFactory;

    public interface SearchGourmetSuggestAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public SearchGourmetSuggestPresenter(@NonNull SearchGourmetSuggestActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SearchGourmetSuggestInterface createInstanceViewInterface()
    {
        return new SearchGourmetSuggestView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SearchGourmetSuggestActivity activity)
    {
        setContentView(R.layout.activity_search_gourmet_suggest_data);

        setAnalytics(new SearchGourmetSuggestAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mRecentlyRemoteImpl = new RecentlyRemoteImpl(activity);
        mRecentlyLocalImpl = new RecentlyLocalImpl(activity);
        mGoogleAddressRemoteImpl = new GoogleAddressRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchGourmetSuggestAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mKeyword = intent.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

        String visitDate = intent.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_VISIT_DATE);

        if (DailyTextUtils.isTextEmpty(visitDate) == true)
        {
            return false;
        }

        try
        {
            mGourmetBookDateTime = new GourmetBookDateTime();

            mGourmetBookDateTime.setVisitDateTime(visitDate);
        } catch (Exception e)
        {
            return false;
        }

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

        String searchKeywordEditHint = DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigSearchGourmetSuggestHint();
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

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
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
            case SearchGourmetSuggestActivity.REQUEST_CODE_SPEECH_INPUT:
            {
                if (resultCode == Activity.RESULT_OK && null != data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mKeyword = result.get(0);
                    getViewInterface().setKeywordEditText(mKeyword);
                }
                break;
            }

            case SearchGourmetSuggestActivity.REQUEST_CODE_PERMISSION_MANAGER:
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

            case SearchGourmetSuggestActivity.REQUEST_CODE_SETTING_LOCATION:
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

        Observable<ArrayList<RecentlyPlace>> ibObservable = mRecentlyLocalImpl.getRecentlyJSONObject(DailyDb.MAX_RECENT_PLACE_COUNT, Constants.ServiceType.GOURMET) //
            .observeOn(Schedulers.io()).flatMap(new Function<JSONObject, ObservableSource<ArrayList<RecentlyPlace>>>()
            {
                @Override
                public ObservableSource<ArrayList<RecentlyPlace>> apply(JSONObject jsonObject) throws Exception
                {
                    if (jsonObject == null || jsonObject.has("keys") == false)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    return mRecentlyRemoteImpl.getInboundRecentlyList(jsonObject);
                }
            });

        addCompositeDisposable(Observable.zip(ibObservable //
            , mRecentlyLocalImpl.getRecentlyIndexList(Constants.ServiceType.GOURMET) //
            , new BiFunction<ArrayList<RecentlyPlace>, ArrayList<Integer>, List<GourmetSuggest>>()
            {
                @Override
                public List<GourmetSuggest> apply(ArrayList<RecentlyPlace> gourmetList, ArrayList<Integer> expectedList) throws Exception
                {
                    if (expectedList != null && expectedList.size() > 0)
                    {
                        Collections.sort(gourmetList, new Comparator<RecentlyPlace>()
                        {
                            @Override
                            public int compare(RecentlyPlace o1, RecentlyPlace o2)
                            {
                                Integer position1 = expectedList.indexOf(o1.index);
                                Integer position2 = expectedList.indexOf(o2.index);

                                return position1.compareTo(position2);
                            }
                        });
                    }

                    mDailyRecentSearches = new DailyRecentSearches(DailyPreference.getInstance(getActivity()).getGourmetRecentSearches());
                    List<Keyword> keywordList = mDailyRecentSearches.getList();

                    ArrayList<GourmetSuggest> gourmetSuggestList = new ArrayList<>();

                    if (keywordList != null && keywordList.size() > 0)
                    {
                        gourmetSuggestList.add(new GourmetSuggest(GourmetSuggest.MENU_TYPE_RECENTLY_SEARCH //
                            , null, getString(R.string.label_search_suggest_recently_search)));

                        for (Keyword keyword : keywordList)
                        {
                            gourmetSuggestList.add(new GourmetSuggest(keyword));
                        }
                    }

                    if (gourmetList != null && gourmetList.size() > 0)
                    {
                        gourmetSuggestList.add(new GourmetSuggest(GourmetSuggest.MENU_TYPE_RECENTLY_GOURMET //
                            , null, getString(R.string.label_recently_gourmet)));

                        for (RecentlyPlace recentlyPlace : gourmetList)
                        {
                            gourmetSuggestList.add(new GourmetSuggest(recentlyPlace));
                        }
                    }

                    return gourmetSuggestList;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<GourmetSuggest>>()
        {
            @Override
            public void accept(List<GourmetSuggest> gourmetSuggests) throws Exception
            {
                boolean visible = gourmetSuggests != null && gourmetSuggests.size() > 0;
                getViewInterface().setRecentlySuggests(gourmetSuggests);
                getViewInterface().setRecentlySuggestVisible(visible);

                startSearchMyLocation(false);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().setRecentlySuggests(null);
                getViewInterface().setRecentlySuggestVisible(false);

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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onSearchSuggest(String keyword)
    {
        clearCompositeDisposable();

        if (mGourmetBookDateTime == null)
        {
            Util.restartApp(getActivity());
            return;
        }

        String visitDate;

        try
        {
            visitDate = mGourmetBookDateTime.getVisitDateTime("yyyy-MM-dd");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return;
        }

        mKeyword = keyword;

        getViewInterface().setProgressBarVisible(true);

        if (DailyTextUtils.isTextEmpty(keyword) == true)
        {
            onSuggestList(null);
        } else
        {
            addCompositeDisposable(mSuggestRemoteImpl.getSuggestsByGourmet(visitDate, keyword)//
                .delaySubscription(500, TimeUnit.MILLISECONDS).map(new Function<Pair<String, ArrayList<GourmetKeyword>>, List<GourmetSuggest>>()
                {
                    @Override
                    public List<GourmetSuggest> apply(Pair<String, ArrayList<GourmetKeyword>> stringArrayListPair) throws Exception
                    {
                        ArrayList<GourmetKeyword> keywordList = stringArrayListPair.second;
                        ArrayList<GourmetSuggest> gourmetSuggestList = new ArrayList<>();

                        if (keywordList == null || keywordList.size() == 0)
                        {
                            return gourmetSuggestList;
                        }

                        String oldCategoryKey = null;

                        for (GourmetKeyword gourmetKeyword : keywordList)
                        {
                            GourmetSuggest gourmetSuggest = new GourmetSuggest(gourmetKeyword);

                            if (DailyTextUtils.isTextEmpty(oldCategoryKey) || oldCategoryKey.equalsIgnoreCase(gourmetSuggest.categoryKey) == false)
                            {
                                int resId;
                                if (GourmetSuggest.CATEGORY_GOURMET.equalsIgnoreCase(gourmetSuggest.categoryKey))
                                {
                                    resId = R.string.label_search_suggest_type_gourmet;
                                    oldCategoryKey = gourmetSuggest.categoryKey;
                                } else
                                {
                                    resId = R.string.label_search_suggest_type_region;
                                    oldCategoryKey = GourmetSuggest.CATEGORY_REGION;
                                }

                                gourmetSuggestList.add(new GourmetSuggest(GourmetSuggest.MENU_TYPE_SUGGEST, null, getString(resId)));

                            }

                            gourmetSuggestList.add(gourmetSuggest);
                        }

                        return gourmetSuggestList;
                    }
                }).subscribe(new Consumer<List<GourmetSuggest>>()
                {
                    @Override
                    public void accept(List<GourmetSuggest> gourmetSuggestList) throws Exception
                    {
                        SearchGourmetSuggestPresenter.this.onSuggestList(gourmetSuggestList);

                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        SearchGourmetSuggestPresenter.this.onSuggestList(null);

                        unLockAll();
                    }
                }));
        }
    }

    @Override
    public void onSuggestClick(GourmetSuggest gourmetSuggest)
    {
        if (gourmetSuggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addRecentSearches(gourmetSuggest);

        getViewInterface().setKeywordEditText(gourmetSuggest.displayName);
        startFinishAction(gourmetSuggest, mKeyword, null);
    }

    @Override
    public void onRecentlySuggestClick(GourmetSuggest gourmetSuggest)
    {
        if (gourmetSuggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addRecentSearches(gourmetSuggest);

        getViewInterface().setKeywordEditText(gourmetSuggest.displayName);
        startFinishAction(gourmetSuggest, mKeyword, null);
    }

    private Keyword getKeyword(GourmetSuggest gourmetSuggest)
    {
        if (getActivity() == null || gourmetSuggest == null)
        {
            return null;
        }

        int icon = Keyword.DEFAULT_ICON;
        if (GourmetSuggest.CATEGORY_GOURMET.equalsIgnoreCase(gourmetSuggest.categoryKey))
        {
            icon = Keyword.GOURMET_ICON;
        }

        return new Keyword(icon, gourmetSuggest.displayName);
    }

    private void addRecentSearches(GourmetSuggest gourmetSuggest)
    {
        if (getActivity() == null || gourmetSuggest == null)
        {
            return;
        }

        Keyword keyword = getKeyword(gourmetSuggest);

        if (keyword == null)
        {
            return;
        }

        mDailyRecentSearches.addString(keyword);
        DailyPreference.getInstance(getActivity()).setGourmetRecentSearches(mDailyRecentSearches.toString());
    }

    private void startFinishAction(GourmetSuggest gourmetSuggest, String keyword, String analyticsClickType)
    {
        Intent intent = new Intent();
        intent.putExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcel(gourmetSuggest));
        intent.putExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_KEYWORD, keyword);

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
        getViewInterface().setRecentlySuggestVisible(false);

        Observable<Boolean> recentlySearchObservable = Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                mDailyRecentSearches.clear();
                DailyPreference.getInstance(getActivity()).setGourmetRecentSearches("");

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());

        addCompositeDisposable(Observable.zip(mRecentlyLocalImpl.clearRecentlyItems(Constants.ServiceType.GOURMET) //
            , recentlySearchObservable, new BiFunction<Boolean, Boolean, Boolean>()
            {
                @Override
                public Boolean apply(Boolean t1, Boolean t2) throws Exception
                {
                    return true;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
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
    }

    @Override
    public void onDeleteRecentlySuggest(int position, GourmetSuggest gourmetSuggest)
    {
        if (getViewInterface() == null || gourmetSuggest == null || position < 0)
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

        if (GourmetSuggest.MENU_TYPE_RECENTLY_GOURMET == gourmetSuggest.menuType)
        {
            addCompositeDisposable(mRecentlyLocalImpl.deleteRecentlyItem(Constants.ServiceType.GOURMET, gourmetSuggest.gourmetIndex) //
                .flatMap(new Function<Boolean, ObservableSource<ArrayList<RecentlyDbPlace>>>()
                {
                    @Override
                    public ObservableSource<ArrayList<RecentlyDbPlace>> apply(Boolean aBoolean) throws Exception
                    {
                        return mRecentlyLocalImpl.getRecentlyTypeList(Constants.ServiceType.GOURMET);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<RecentlyDbPlace>>()
                {
                    @Override
                    public void accept(ArrayList<RecentlyDbPlace> recentlyDbPlaces) throws Exception
                    {
                        if (recentlyDbPlaces.size() == 0)
                        {
                            getViewInterface().removeRecentlySection(GourmetSuggest.MENU_TYPE_RECENTLY_GOURMET);
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
            // 최근 검색어
            Keyword keyword = getKeyword(gourmetSuggest);
            if (keyword == null)
            {
                unLockAll();
                return;
            }

            mDailyRecentSearches.remove(keyword);
            if (mDailyRecentSearches.size() == 0)
            {
                getViewInterface().removeRecentlySection(GourmetSuggest.MENU_TYPE_RECENTLY_SEARCH);
            }

            DailyPreference.getInstance(getActivity()).setGourmetRecentSearches(mDailyRecentSearches.toString());

            unLockAll();
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
            startActivityForResult(intent, SearchGourmetSuggestActivity.REQUEST_CODE_SPEECH_INPUT);
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
    public void onNearbyClick(GourmetSuggest gourmetSuggest)
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

    private void onSuggestList(List<GourmetSuggest> gourmetSuggestList)
    {
        getViewInterface().setProgressBarVisible(false);

        boolean hasKeyword = DailyTextUtils.isTextEmpty(mKeyword) == false;
        getViewInterface().setSuggestsVisible(hasKeyword);

        getViewInterface().setSuggests(gourmetSuggestList);
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
                GourmetSuggest locationGourmetSuggest = new GourmetSuggest(GourmetSuggest.MENU_TYPE_LOCATION, GourmetSuggest.CATEGORY_LOCATION, null);
                locationGourmetSuggest.latitude = location.getLatitude();
                locationGourmetSuggest.longitude = location.getLongitude();

                addCompositeDisposable(mGoogleAddressRemoteImpl.getLocationAddress(location.getLatitude(), location.getLongitude()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String address) throws Exception
                    {
                        locationGourmetSuggest.displayName = address;

                        getViewInterface().setNearbyGourmetSuggest(true, locationGourmetSuggest);

                        if (isUserClick == false)
                        {
                            return;
                        }

                        unLockAll();

                        getViewInterface().setKeywordEditText(locationGourmetSuggest.displayName);
                        startFinishAction(locationGourmetSuggest, mKeyword, null);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        getViewInterface().setNearbyGourmetSuggest(true, locationGourmetSuggest);

                        if (isUserClick == false)
                        {
                            return;
                        }

                        unLockAll();

                        locationGourmetSuggest.displayName = getString(R.string.label_search_nearby_empty_address);

                        getViewInterface().setKeywordEditText(locationGourmetSuggest.displayName);
                        startFinishAction(locationGourmetSuggest, mKeyword, null);
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

                GourmetSuggest locationGourmetSuggest = new GourmetSuggest(GourmetSuggest.MENU_TYPE_LOCATION, GourmetSuggest.CATEGORY_LOCATION, displayName);

                getViewInterface().setNearbyGourmetSuggest(isAgreePermission, locationGourmetSuggest);

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
                startActivityForResult(intent, SearchGourmetSuggestActivity.REQUEST_CODE_PERMISSION_MANAGER);
            } else if (throwable instanceof ProviderException)
            {
                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                View.OnClickListener positiveListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, SearchGourmetSuggestActivity.REQUEST_CODE_SETTING_LOCATION);
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
                    ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), SearchGourmetSuggestActivity.REQUEST_CODE_SETTING_LOCATION);
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
