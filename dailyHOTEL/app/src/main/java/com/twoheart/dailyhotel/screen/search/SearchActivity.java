package com.twoheart.dailyhotel.screen.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.screen.search.gourmet.GourmetSearchFragment;
import com.twoheart.dailyhotel.screen.search.stay.StaySearchFragment;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailySwitchCompat;
import com.twoheart.dailyhotel.widget.DailyViewPager;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity implements View.OnClickListener
{
    private static final int SEARCH_TAB_COUNT = 2;

    private SearchFragmentPagerAdapter mSearchFragmentPagerAdapter;
    private DailyViewPager mViewPager;
    private View mSearchView;
    private PlaceType mPlaceType;

    private StaySearchFragment mStaySearchFragment;
    private GourmetSearchFragment mGourmetSearchFragment;

    private SaleTime mSaleTime;
    private int mNights;

    public static Intent newInstance(Context context, PlaceType placeType, SaleTime saleTime, int nights)
    {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);
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

        mPlaceType = PlaceType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));

        initLayout(mPlaceType);

    }

    private void initLayout(PlaceType placeType)
    {
        initToolbar(placeType);

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
                break;

            case FNB:
                mViewPager.setCurrentItem(1);
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
        final ImageView hotelSwitchView = (ImageView) view.findViewById(R.id.hotelSwitch);
        final ImageView gourmetSwitchView = (ImageView) view.findViewById(R.id.gourmetSwitch);

        switch (placeType)
        {
            case HOTEL:
                switchCompat.setChecked(false);
                break;

            case FNB:
                switchCompat.setChecked(true);
                break;
        }

        final PlaceType startPlactType = placeType;

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
                    }
                }

                analyticsSwitchChanged(startPlactType, mPlaceType);
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

                hotelSwitchView.setAlpha(0.4f + hotelAlpha);
                gourmetSwitchView.setAlpha(0.4f + gourmetAlpha);

                int pageOffset = (int) ((float) offset * mViewPager.getWidth() / range);

                switch (startPlactType)
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

    private void analyticsSwitchChanged(PlaceType startPlaceType, PlaceType changedPlaceType)
    {
        String category = AnalyticsManager.ValueType.EMPTY;
        String label = AnalyticsManager.ValueType.EMPTY;

        switch (startPlaceType)
        {
            case HOTEL:
                category = AnalyticsManager.Category.HOTEL_SEARCH;
                break;

            case FNB:
                category = AnalyticsManager.Category.GOURMET_SEARCH;
                break;
        }

        switch (changedPlaceType)
        {
            case HOTEL:
                label = "Hotel";
                break;

            case FNB:
                label = "Gourmet";
                break;
        }

        AnalyticsManager.getInstance(SearchActivity.this).recordEvent(category, AnalyticsManager.Action.SWITCHING, label, null);
    }
}