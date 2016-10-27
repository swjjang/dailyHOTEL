package com.twoheart.dailyhotel.screen.information.wishlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.recentplace.RecentGourmetListFragment;
import com.twoheart.dailyhotel.screen.information.recentplace.RecentPlacesFragmentPagerAdapter;
import com.twoheart.dailyhotel.screen.information.recentplace.RecentPlacesListFragment;
import com.twoheart.dailyhotel.screen.information.recentplace.RecentPlacesNetworkController;
import com.twoheart.dailyhotel.screen.information.recentplace.RecentStayListFragment;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyViewPager;
import com.twoheart.dailyhotel.widget.FontManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by android_sam on 2016. 10. 27..
 */

public class WishListTabActivity extends BaseActivity
{
    private RecentPlaces mRecentStayPlaces;
    private RecentPlaces mRecentGourmetPlaces;
    private ArrayList<RecentPlacesListFragment> mFragmentList;

    private RecentStayListFragment mRecentStayListFragment;
    private RecentGourmetListFragment mRecentGourmetListFragment;

    private RecentPlacesFragmentPagerAdapter mPageAdapter;

    private RecentPlacesNetworkController mNetworkController;

    private DailyViewPager mViewPager;
    private TabLayout mTabLayout;
    private View mEmptyView;

    private PlaceType mPlaceType;

    private boolean mDontReloadAtOnResume; // TODO : 타 기능 구현 완료 후 처리 예정

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recent_places);

        mNetworkController = new RecentPlacesNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        String stayString = DailyPreference.getInstance(this).getStayRecentPlaces();
        mRecentStayPlaces = new RecentPlaces(stayString);

        String gourmetString = DailyPreference.getInstance(this).getGourmetRecentPlaces();
        mRecentGourmetPlaces = new RecentPlaces(gourmetString);

        initIntent(getIntent());

        initLayout();
    }

    @Override
    protected void onResume()
    {
        if (mDontReloadAtOnResume == true)
        {
            mDontReloadAtOnResume = false;
        } else
        {
            lockUI();
            mNetworkController.requestCommonDateTime();
        }

        super.onResume();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    private void initIntent(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        String placeTypeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE);

        if (Util.isTextEmpty(placeTypeName) == false)
        {
            try
            {
                mPlaceType = PlaceType.valueOf(placeTypeName);
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
            }
        }
    }

    private void initLayout()
    {
        initToolbar();
        initTabLayout();

        mFragmentList = new ArrayList<>();

        mRecentStayListFragment = new RecentStayListFragment();
        mRecentStayListFragment.setRecentPlaces(mRecentStayPlaces);
        mRecentStayListFragment.setRecentPlaceListFragmentListener(mRecentPlaceListFragmentListener);

        mFragmentList.add(mRecentStayListFragment);

        mRecentGourmetListFragment = new RecentGourmetListFragment();
        mRecentGourmetListFragment.setRecentPlaces(mRecentGourmetPlaces);
        mRecentGourmetListFragment.setRecentPlaceListFragmentListener(mRecentPlaceListFragmentListener);

        mFragmentList.add(mRecentGourmetListFragment);

        mPageAdapter = new RecentPlacesFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);

        //        setTabLayout(mFragmentList);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.frag_recent_places), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    protected void initTabLayout()
    {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_hotel));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_fnb));
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTabLayout.getLayoutParams();
        layoutParams.topMargin = 1 - Util.dpToPx(this, 1);

        mTabLayout.setLayoutParams(layoutParams);

        setTabLayoutVisibility(View.GONE);

        FontManager.apply(mTabLayout, FontManager.getInstance(this).getRegularTypeface());

        mEmptyView = findViewById(R.id.emptyLayout);
        mViewPager = (DailyViewPager) findViewById(R.id.viewPager);
    }

    private void setTabLayout(ArrayList<RecentPlacesListFragment> fragmentList)
    {
        if (fragmentList == null || fragmentList.size() == 0 //
            || (isEmptyRecentStayPlace() == true && isEmptyRecentGourmetPlace() == true))
        {
            mViewPager.removeAllViews();
            mViewPager.clearOnPageChangeListeners();

            setTabLayoutVisibility(View.GONE);
            setEmptyViewVisibility(View.VISIBLE);

            unLockUI();

            AnalyticsManager.getInstance(WishListTabActivity.this).recordScreen(AnalyticsManager.Screen.MENU_RECENT_VIEW_EMPTY);
            return;
        }

        setTabLayoutVisibility(View.VISIBLE);
        setEmptyViewVisibility(View.GONE);

        int position = 0;

        if (mPlaceType != null)
        {
            // deeplink type
            if (PlaceType.FNB.equals(mPlaceType) == true)
            {
                position = 1;
            }

            // deeplink 로 인한 처리 후 초기화
            mPlaceType = null;
        } else
        {
            if (isEmptyRecentStayPlace() == true)
            {
                position = 1;
            }
        }


        mViewPager.removeAllViews();
        mViewPager.setOffscreenPageLimit(1);


        Class reflectionClass = ViewPager.class;

        try
        {
            Field mCurItem = reflectionClass.getDeclaredField("mCurItem");
            mCurItem.setAccessible(true);
            mCurItem.setInt(mViewPager, position);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        mViewPager.setAdapter(mPageAdapter);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        String placeTypeString = position == 1 ? AnalyticsManager.ValueType.GOURMET : AnalyticsManager.ValueType.STAY;

        HashMap<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, placeTypeString);
        params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, placeTypeString);

        AnalyticsManager.getInstance(WishListTabActivity.this).recordScreen(AnalyticsManager.Screen.MENU_RECENT_VIEW, params);
    }

    private void setTabLayoutVisibility(int visibility)
    {
        if (mTabLayout == null)
        {
            return;
        }

        if (View.VISIBLE != visibility)
        {
            mTabLayout.setVisibility(View.GONE);
            mTabLayout.setOnTabSelectedListener(null);
        } else
        {
            mTabLayout.setVisibility(View.VISIBLE);
            mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
        }
    }

    private void setEmptyViewVisibility(int visibility)
    {
        if (mEmptyView == null)
        {
            return;
        }

        mEmptyView.setVisibility(visibility);
    }

    private boolean isEmptyRecentStayPlace()
    {
        return mRecentStayPlaces == null || mRecentStayPlaces.size() == 0;
    }

    private boolean isEmptyRecentGourmetPlace()
    {
        return mRecentGourmetPlaces == null || mRecentGourmetPlaces.size() == 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (isFinishing() == true)
        {
            return;
        }

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            {
                mDontReloadAtOnResume = true;

                setResult(resultCode);

                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    finish();
                }
                break;
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            AnalyticsManager.getInstance(WishListTabActivity.this).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.RECENT_VIEW_TAB_CHANGE, //
                tab.getPosition() == 1 ? AnalyticsManager.ValueType.GOURMET : AnalyticsManager.ValueType.HOTEL, null);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {

        }
    };

    private RecentPlacesListFragment.OnRecentPlaceListFragmentListener mRecentPlaceListFragmentListener = new RecentPlacesListFragment.OnRecentPlaceListFragmentListener()
    {
        @Override
        public void onDeleteItemClick(PlaceType placeType, RecentPlaces recentPlaces)
        {
            if (PlaceType.FNB.equals(placeType) == true)
            {
                mRecentGourmetPlaces = recentPlaces;
            }

            if (PlaceType.HOTEL.equals(placeType) == true)
            {
                mRecentStayPlaces = recentPlaces;
            }

            int stayCount = mRecentStayPlaces.size();
            int gourmetCount = mRecentGourmetPlaces.size();

            if (stayCount == 0 && gourmetCount == 0)
            {
                // 둘다 없을때
                setTabLayoutVisibility(View.GONE);
                setEmptyViewVisibility(View.VISIBLE);

                AnalyticsManager.getInstance(WishListTabActivity.this).recordScreen(AnalyticsManager.Screen.MENU_RECENT_VIEW_EMPTY);
            } else
            {
                // 둘중에 하나라도 있을때
                setTabLayoutVisibility(View.VISIBLE);
                setEmptyViewVisibility(View.GONE);
            }
        }
    };

    private RecentPlacesNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new RecentPlacesNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCommonDateTime(long currentDateTime, long dailyDateTime)
        {
            SaleTime saleTime = new SaleTime();
            saleTime.setCurrentTime(currentDateTime);
            saleTime.setDailyTime(dailyDateTime);
            saleTime.setOffsetDailyDay(0);

            if (mFragmentList != null)
            {
                for (RecentPlacesListFragment fragment : mFragmentList)
                {
                    fragment.setSaleTime(saleTime);
                }
            }

            setTabLayout(mFragmentList);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            WishListTabActivity.this.onErrorResponse(volleyError);
            finish();
        }

        @Override
        public void onError(Exception e)
        {
            WishListTabActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            WishListTabActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            WishListTabActivity.this.onErrorToastMessage(message);
            finish();
        }
    };
}