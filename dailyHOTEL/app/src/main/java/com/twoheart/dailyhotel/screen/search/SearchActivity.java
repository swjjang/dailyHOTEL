package com.twoheart.dailyhotel.screen.search;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.screen.search.gourmet.GourmetSearchFragment;
import com.twoheart.dailyhotel.screen.search.stay.StaySearchFragment;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailySwitchCompat;
import com.twoheart.dailyhotel.widget.DailyViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends BaseActivity implements View.OnClickListener
{
    private static final int SEARCH_TAB_COUNT = 2;
    private static final String INTENT_EXTRA_DATA_WORD = "word";

    private SearchFragmentPagerAdapter mSearchFragmentPagerAdapter;
    DailyViewPager mViewPager;
    View mSearchView, mTooltipLayout;
    PlaceType mPlaceType;

    StaySearchFragment mStaySearchFragment;
    GourmetSearchFragment mGourmetSearchFragment;

    private SaleTime mSaleTime;
    private int mNights;

    public static Intent newInstance(Context context, PlaceType placeType, SaleTime saleTime, int nights)
    {
        return newInstance(context, placeType, saleTime, nights, null);
    }

    public static Intent newInstance(Context context, PlaceType placeType, SaleTime saleTime, int nights, String word)
    {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);

        if (Util.isTextEmpty(word) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_WORD, word);
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

        mSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        mNights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 1);

        if (mSaleTime == null)
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

        initLayout(mPlaceType, word);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        recordAnalyticsSearch(mSaleTime, mNights, mPlaceType);
    }

    private void initLayout(PlaceType placeType, final String word)
    {
        initToolbar(placeType);

        mTooltipLayout = findViewById(R.id.tooltipLayout);

        switch (placeType)
        {
            case HOTEL:
            {
                if (DailyPreference.getInstance(this).isViewSearchTooltip() == true)
                {
                    mTooltipLayout.setVisibility(View.GONE);
                } else
                {
                    DailyPreference.getInstance(this).setIsViewSearchTooltip(true);
                    mTooltipLayout.setVisibility(View.VISIBLE);
                    mTooltipLayout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            hideAnimationTooltip();
                        }
                    });

                    // 10초 후에 터치가 없으면 자동으로 사라짐.(기획서상 10초이지만 실제 보이기까지 여분의 시간을 넣음)
                    mTooltipLayout.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (mTooltipLayout.getVisibility() != View.GONE)
                            {
                                hideAnimationTooltip();
                            }
                        }
                    }, 10000);
                }
                break;
            }

            case FNB:
                mTooltipLayout.setVisibility(View.GONE);
                break;
        }

        ArrayList<PlaceSearchFragment> fragmentList = new ArrayList<>();

        SaleTime checkInSaleTime = mSaleTime;
        SaleTime checkOutSaleTime = checkInSaleTime.getClone(checkInSaleTime.getOffsetDailyDay() + mNights);

        mStaySearchFragment = new StaySearchFragment();
        mStaySearchFragment.setSaleTime(checkInSaleTime, checkOutSaleTime);
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
        });

        fragmentList.add(mStaySearchFragment);

        mGourmetSearchFragment = new GourmetSearchFragment();
        mGourmetSearchFragment.setSaleTime(mSaleTime);
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
        });

        fragmentList.add(mGourmetSearchFragment);

        mSearchFragmentPagerAdapter = new SearchFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        mViewPager = (DailyViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(SEARCH_TAB_COUNT);
        mViewPager.setAdapter(mSearchFragmentPagerAdapter);
        mViewPager.setPagingEnabled(false);

        switch (placeType)
        {
            case HOTEL:
                mViewPager.setCurrentItem(0);

                if (Util.isTextEmpty(word) == false)
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
                }
                break;

            case FNB:
                mViewPager.setCurrentItem(1);

                if (Util.isTextEmpty(word) == false)
                {
                    mViewPager.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mGourmetSearchFragment.setSearchWord(word);
                        }
                    }, 500);
                }
                break;
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
        DailySwitchCompat switchCompat = (DailySwitchCompat) view.findViewById(R.id.placeSwitch);
        final View hotelTextView = view.findViewById(R.id.hotelTextView);
        final View gourmetTextView = view.findViewById(R.id.gourmetTextView);

        switch (placeType)
        {
            case HOTEL:
                switchCompat.setChecked(false);
                break;

            case FNB:
                switchCompat.setChecked(true);
                break;
        }

        final PlaceType startPlaceType = placeType;

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                mStaySearchFragment.onScrollingFragment(false);
                mGourmetSearchFragment.onScrollingFragment(false);

                if (isChecked == true)
                {
                    mPlaceType = PlaceType.FNB;

                    if (mStaySearchFragment != null)
                    {
                        mStaySearchFragment.clearSearchKeywordFocus();
                    }

                    if (mGourmetSearchFragment != null)
                    {
                        mGourmetSearchFragment.resetSearchKeyword();
                        mGourmetSearchFragment.showSearchKeyboard();
                        mGourmetSearchFragment.updateTermsOfLocationLayout();
                    }
                } else
                {
                    mPlaceType = PlaceType.HOTEL;

                    if (mGourmetSearchFragment != null)
                    {
                        mGourmetSearchFragment.clearSearchKeywordFocus();
                    }

                    if (mStaySearchFragment != null)
                    {
                        mStaySearchFragment.resetSearchKeyword();
                        mStaySearchFragment.showSearchKeyboard();
                        mStaySearchFragment.updateTermsOfLocationLayout();
                    }
                }

                analyticsSwitchChanged(mPlaceType);
            }
        });

        switchCompat.setOnScrollListener(new DailySwitchCompat.OnScrollListener()
        {
            @Override
            public void onScrolled(int offset, int range)
            {
                if (offset == 0 || offset == range)
                {
                    mStaySearchFragment.onScrollingFragment(false);
                    mGourmetSearchFragment.onScrollingFragment(false);
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
                        mStaySearchFragment.startSearchResultActivity();
                        break;

                    case FNB:
                        mGourmetSearchFragment.startSearchResultActivity();
                        break;
                }
                break;
        }
    }

    void hideAnimationTooltip()
    {
        if (mTooltipLayout.getTag() != null)
        {
            return;
        }

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mTooltipLayout, "alpha", 1.0f, 0.0f);

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

    private void recordAnalyticsSearch(SaleTime saleTime, int nights, PlaceType placeType)
    {
        try
        {
            SaleTime checkInSaleTime = saleTime;
            SaleTime checkOutSaleTime = checkInSaleTime.getClone(checkInSaleTime.getOffsetDailyDay() + nights);

            String placeValueType = null;

            Map<String, String> params = new HashMap<>();

            switch (placeType)
            {
                case HOTEL:
                    params.put(AnalyticsManager.KeyType.CHECK_IN, checkInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
                    placeValueType = AnalyticsManager.ValueType.HOTEL;
                    break;

                case FNB:
                    params.put(AnalyticsManager.KeyType.CHECK_IN, checkInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
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
}