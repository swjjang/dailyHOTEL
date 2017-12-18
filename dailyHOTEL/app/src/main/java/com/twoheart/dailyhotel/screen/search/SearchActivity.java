package com.twoheart.dailyhotel.screen.search;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailySwitchCompat;
import com.daily.base.widget.DailyViewPager;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.screen.search.gourmet.GourmetSearchFragment;
import com.twoheart.dailyhotel.screen.search.stay.StaySearchFragment;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class SearchActivity extends BaseActivity implements View.OnClickListener
{
    private static final int SEARCH_TAB_COUNT = 2;
    private static final String INTENT_EXTRA_DATA_WORD = "word";
    private static final String INTENT_EXTRA_DATA_INDEX = "index";

    private SearchFragmentPagerAdapter mSearchFragmentPagerAdapter;
    DailyViewPager mViewPager;
    View mSearchView;
    View mTooltipLayout;
    PlaceType mPlaceType;

    StaySearchFragment mStaySearchFragment;
    GourmetSearchFragment mGourmetSearchFragment;
    private boolean mIsResizeMode;

    DailySwitchCompat mSwitchCompat;
    PublishSubject<Boolean> mSwitchChangedSubject;

    public static Intent newInstance(Context context, PlaceType placeType, PlaceBookingDay placeBookingDay, int campaignTagIndex)
    {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, placeBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_INDEX, campaignTagIndex);

        return intent;
    }

    public static Intent newInstance(Context context, PlaceType placeType, PlaceBookingDay placeBookingDay)
    {
        return newInstance(context, placeType, placeBookingDay, null);
    }

    public static Intent newInstance(Context context, PlaceType placeType, PlaceBookingDay placeBookingDay, String word)
    {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, placeBookingDay);

        if (DailyTextUtils.isTextEmpty(word) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_WORD, word);
        }
        return intent;
    }

    public static Intent newInstance(Context context, PlaceType placeType, String checkInDateTime, String checkOutDateTime)
    {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());

        try
        {
            StayBookingDay stayBookingDay = new StayBookingDay();
            stayBookingDay.setCheckInDay(checkInDateTime);
            stayBookingDay.setCheckOutDay(checkOutDateTime);

            intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        Intent intent = getIntent();

        if (intent == null)
        {
            return;
        }

        PlaceBookingDay placeBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

        if (placeBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        try
        {
            mPlaceType = PlaceType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));
        } catch (Exception e)
        {
            Util.restartApp(this);
            return;
        }

        String word = null;

        if (intent.hasExtra(INTENT_EXTRA_DATA_WORD) == true)
        {
            word = intent.getStringExtra(INTENT_EXTRA_DATA_WORD);
        }

        int index = intent.getIntExtra(INTENT_EXTRA_DATA_INDEX, -1);

        initLayout(placeBookingDay, mPlaceType, word, index);

        recordAnalyticsSearch(placeBookingDay, mPlaceType);
    }

    void setSoftInputMode(boolean isResizeMode)
    {
        if (mIsResizeMode == isResizeMode)
        {
            return;
        }

        mIsResizeMode = isResizeMode;

        if (isResizeMode == true)
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    private void initLayout(PlaceBookingDay placeBookingDay, PlaceType placeType, final String word, int index)
    {
        initToolbar(placeType);

        mTooltipLayout = findViewById(R.id.tooltipLayout);

        StayBookingDay stayBookingDay = null;
        GourmetBookingDay gourmetBookingDay = null;

        try
        {
            switch (placeType)
            {
                case HOTEL:
                {
                    if (DailyPreference.getInstance(this).isStaySearchObTooltip() == true)
                    {
                        DailyPreference.getInstance(this).setStaySearchObTooltip(false);
                        mTooltipLayout.setVisibility(View.VISIBLE);
                        mTooltipLayout.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                hideAnimationTooltip();
                            }
                        });
                    } else
                    {
                        mTooltipLayout.setVisibility(View.GONE);
                    }

                    stayBookingDay = (StayBookingDay) placeBookingDay;

                    gourmetBookingDay = new GourmetBookingDay();
                    gourmetBookingDay.setVisitDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT));
                    break;
                }

                case FNB:
                {
                    mTooltipLayout.setVisibility(View.GONE);

                    gourmetBookingDay = (GourmetBookingDay) placeBookingDay;

                    stayBookingDay = new StayBookingDay();
                    stayBookingDay.setCheckInDay(gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT));
                    stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), 1);
                    break;
                }
            }

            ArrayList<PlaceSearchFragment> fragmentList = new ArrayList<>();

            mStaySearchFragment = new StaySearchFragment();
            mStaySearchFragment.setStayBookingDay(stayBookingDay);
            mStaySearchFragment.setOnSearchFragmentListener(new PlaceSearchFragment.OnSearchFragmentListener()
            {
                @Override
                public void finish()
                {
                    SearchActivity.this.finish();
                }

                @Override
                public void finish(int resultCode)
                {
                    setResult(resultCode);
                    finish();
                }

                @Override
                public void onSearchEnabled(boolean enabled)
                {
                    mSearchView.setEnabled(enabled);
                }

                @Override
                public void onChangeAutoCompleteScrollView(boolean isShow)
                {
                    setSoftInputMode(isShow);
                }

                @Override
                public void onSwitchClick(String keyword)
                {
                    mSwitchCompat.setChecked(true);

                    mSwitchChangedSubject = PublishSubject.create();

                    addCompositeDisposable(mSwitchChangedSubject.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception
                        {
                            mGourmetSearchFragment.searchKeyword(keyword);
                        }
                    }));
                }
            });

            fragmentList.add(mStaySearchFragment);

            mGourmetSearchFragment = new GourmetSearchFragment();
            mGourmetSearchFragment.setGourmetBookingDay(gourmetBookingDay);
            mGourmetSearchFragment.setOnSearchFragmentListener(new PlaceSearchFragment.OnSearchFragmentListener()
            {
                @Override
                public void finish()
                {
                    SearchActivity.this.finish();
                }

                @Override
                public void finish(int resultCode)
                {
                    setResult(resultCode);
                    finish();
                }

                @Override
                public void onSearchEnabled(boolean enabled)
                {
                    mSearchView.setEnabled(enabled);
                }

                @Override
                public void onChangeAutoCompleteScrollView(boolean isShow)
                {
                    setSoftInputMode(isShow);
                }

                @Override
                public void onSwitchClick(String keyword)
                {
                    mSwitchCompat.setChecked(false);

                    mSwitchChangedSubject = PublishSubject.create();

                    addCompositeDisposable(mSwitchChangedSubject.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception
                        {
                            mStaySearchFragment.searchKeyword(keyword);
                        }
                    }));
                }
            });

            fragmentList.add(mGourmetSearchFragment);

            mSearchFragmentPagerAdapter = new SearchFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

            mViewPager = findViewById(R.id.viewPager);
            mViewPager.setOffscreenPageLimit(SEARCH_TAB_COUNT);
            mViewPager.setAdapter(mSearchFragmentPagerAdapter);
            mViewPager.setPagingEnabled(false);

            switch (placeType)
            {
                case HOTEL:
                    mViewPager.setCurrentItem(0);

                    if (DailyTextUtils.isTextEmpty(word) == false)
                    {
                        // Fragment 가 생성되기 전이라서 지연시간 추가
                        mViewPager.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mStaySearchFragment.setSearchWord(word);
                            }
                        }, 500);
                    } else if (index > 0)
                    {
                        // Fragment 가 생성되기 전이라서 지연시간 추가
                        mViewPager.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mStaySearchFragment.startCampaignTagList(index, null);
                            }
                        }, 500);
                    }
                    break;

                case FNB:
                    mViewPager.setCurrentItem(1);

                    if (DailyTextUtils.isTextEmpty(word) == false)
                    {
                        mViewPager.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mGourmetSearchFragment.setSearchWord(word);
                            }
                        }, 500);
                    } else if (index > 0)
                    {
                        mViewPager.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mGourmetSearchFragment.startCampaignTagList(index, null);
                            }
                        }, 500);
                    }
                    break;
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void initToolbar(PlaceType placeType)
    {
        View toolbar = findViewById(R.id.toolbar);
        View backView = toolbar.findViewById(R.id.backImageView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        initPlaceSwitch(toolbar, placeType);

        mSearchView = toolbar.findViewById(R.id.searchView);
        mSearchView.setOnClickListener(this);
        mSearchView.setEnabled(false);
    }

    private void initPlaceSwitch(View view, PlaceType placeType)
    {
        // 가운데 스위치
        mSwitchCompat = view.findViewById(R.id.placeSwitch);
        final View hotelTextView = view.findViewById(R.id.hotelTextView);
        final View gourmetTextView = view.findViewById(R.id.gourmetTextView);

        switch (placeType)
        {
            case HOTEL:
                mSwitchCompat.setChecked(false);
                break;

            case FNB:
                mSwitchCompat.setChecked(true);
                break;
        }

        final PlaceType startPlaceType = placeType;

        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                setSoftInputMode(false);

                mStaySearchFragment.onScrollingFragment(false);
                mGourmetSearchFragment.onScrollingFragment(false);

                mViewPager.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (isChecked == true)
                        {
                            mViewPager.setCurrentItem(1, false);

                            mPlaceType = PlaceType.FNB;

                            if (mStaySearchFragment != null)
                            {
                                mStaySearchFragment.hideSearchKeyboard();
                                mStaySearchFragment.hideAutoCompleteLayout();
                                mStaySearchFragment.setRecyclerViewPosition(0);
                            }

                            if (mGourmetSearchFragment != null)
                            {
                                mGourmetSearchFragment.resetSearchKeyword();
                                mGourmetSearchFragment.updateTermsOfLocationLayout();
                                //                        mGourmetSearchFragment.setRecyclerViewPosition(0);
                            }
                        } else
                        {
                            mViewPager.setCurrentItem(0, false);

                            mPlaceType = PlaceType.HOTEL;

                            if (mGourmetSearchFragment != null)
                            {
                                mGourmetSearchFragment.hideSearchKeyboard();
                                mGourmetSearchFragment.hideAutoCompleteLayout();
                                mGourmetSearchFragment.setRecyclerViewPosition(0);
                            }

                            if (mStaySearchFragment != null)
                            {
                                mStaySearchFragment.resetSearchKeyword();
                                mStaySearchFragment.updateTermsOfLocationLayout();
                                //                        mGourmetSearchFragment.setRecyclerViewPosition(0);
                            }
                        }

                        analyticsSwitchChanged(mPlaceType);
                    }
                }, 100);
            }
        });

        mSwitchCompat.setOnScrollListener(new DailySwitchCompat.OnScrollListener()
        {
            @Override
            public void onScrolled(int offset, int range)
            {
                if (offset == 0 || offset == range)
                {
                    mStaySearchFragment.onScrollingFragment(false);
                    mGourmetSearchFragment.onScrollingFragment(false);

                    if (mSwitchChangedSubject != null)
                    {
                        mSwitchChangedSubject.onNext(true);
                        mSwitchChangedSubject.onComplete();
                        mSwitchChangedSubject = null;
                    }
                } else
                {
                    mStaySearchFragment.onScrollingFragment(true);
                    mGourmetSearchFragment.onScrollingFragment(true);
                }

                if (range == 0)
                {
                    return;
                }

                float gourmetAlpha = 0.6f * offset / range;
                float hotelAlpha = 0.6f - gourmetAlpha;

                hotelTextView.setAlpha(0.4f + hotelAlpha);
                gourmetTextView.setAlpha(0.4f + gourmetAlpha);

                int pageOffset = (int) ((float) offset * mViewPager.getWidth() / range);

                switch (startPlaceType)
                {
                    case HOTEL:
                        mViewPager.setScrollX(pageOffset);
                        break;

                    case FNB:
                        mViewPager.setScrollX(pageOffset - mViewPager.getWidth());
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);

        AnalyticsManager.getInstance(SearchActivity.this).recordEvent(AnalyticsManager.Category.SEARCH_//
            , AnalyticsManager.Action.SEARCH_SCREEN, AnalyticsManager.Label.CLOSED, null);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.searchView:
                switch (mPlaceType)
                {
                    case HOTEL:
                        if (mStaySearchFragment.showCheckStayOutboundSearchDialog() == true//
                            || mStaySearchFragment.showCheckGourmetSearchDialog() == true)
                        {
                            return;
                        }

                        if (mStaySearchFragment.isDateChanged() == false)
                        {
                            mStaySearchFragment.startCalendar(true, SearchType.SEARCHES);
                            return;
                        }

                        mStaySearchFragment.startSearchResultActivity();
                        break;

                    case FNB:
                        if (mGourmetSearchFragment.isDateChanged() == false)
                        {
                            mGourmetSearchFragment.startCalendar(true, SearchType.SEARCHES);
                            return;
                        }

                        mGourmetSearchFragment.startSearchResultActivity();
                        break;
                }
                break;
        }
    }

    void analyticsSwitchChanged(PlaceType changedPlaceType)
    {
        String category = AnalyticsManager.Category.SEARCH_;
        String label = AnalyticsManager.ValueType.EMPTY;

        switch (changedPlaceType)
        {
            case HOTEL:
                label = AnalyticsManager.Label.SWITCHING_HOTEL;
                break;

            case FNB:
                label = AnalyticsManager.Label.SWITCHING_GOURMET;
                break;
        }

        AnalyticsManager.getInstance(SearchActivity.this).recordEvent(category, AnalyticsManager.Action.SEARCH_SCREEN, label, null);
    }

    private void recordAnalyticsSearch(PlaceBookingDay placeBookingDay, PlaceType placeType)
    {
        try
        {
            String placeValueType = null;

            Map<String, String> params = new HashMap<>();

            switch (placeType)
            {
                case HOTEL:
                    StayBookingDay stayBookingDay = (StayBookingDay) placeBookingDay;
                    params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                    placeValueType = AnalyticsManager.ValueType.STAY;
                    break;

                case FNB:
                    GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) placeBookingDay;
                    params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
                    placeValueType = AnalyticsManager.ValueType.GOURMET;
                    break;
            }
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, placeValueType);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, placeValueType);

            AnalyticsManager.getInstance(SearchActivity.this).recordScreen(this, AnalyticsManager.Screen.SEARCH_MAIN, null, params);
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
        }

    }

    void hideAnimationTooltip()
    {
        if (mTooltipLayout.getTag() != null)
        {
            return;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mTooltipLayout, "alpha", 1.0f, 0.0f);

        mTooltipLayout.setTag(objectAnimator);

        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(300);
        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                objectAnimator.removeAllListeners();
                objectAnimator.removeAllUpdateListeners();

                mTooltipLayout.setTag(null);
                mTooltipLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });

        objectAnimator.start();
    }
}