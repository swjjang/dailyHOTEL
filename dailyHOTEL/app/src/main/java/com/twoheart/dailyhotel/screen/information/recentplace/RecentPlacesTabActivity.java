package com.twoheart.dailyhotel.screen.information.recentplace;

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
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyViewPager;
import com.twoheart.dailyhotel.widget.FontManager;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 10..
 */

public class RecentPlacesTabActivity extends BaseActivity
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

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
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
            return;
        }

        setTabLayoutVisibility(View.VISIBLE);
        setEmptyViewVisibility(View.GONE);

        int position = 0;
        if (isEmptyRecentStayPlace() == true)
        {
            position = 1;
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

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }
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
            //            boolean isPagingEnabled;

            if (stayCount == 0 && gourmetCount == 0)
            {
                // 둘다 없을때
                setTabLayoutVisibility(View.GONE);
                setEmptyViewVisibility(View.VISIBLE);
                //                isPagingEnabled = false;
            } else
            {
                // 둘중에 하나만 있을때
                setTabLayoutVisibility(View.VISIBLE);
                setEmptyViewVisibility(View.GONE);
                //                isPagingEnabled = true;
            }

            //            mViewPager.setPagingEnabled(isPagingEnabled);
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
            RecentPlacesTabActivity.this.onErrorResponse(volleyError);
            finish();
        }

        @Override
        public void onError(Exception e)
        {
            RecentPlacesTabActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            RecentPlacesTabActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            RecentPlacesTabActivity.this.onErrorToastMessage(message);
            finish();
        }
    };
}
