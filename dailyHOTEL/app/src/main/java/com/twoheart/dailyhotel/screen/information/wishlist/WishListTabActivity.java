package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyViewPager;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by android_sam on 2016. 10. 27..
 */

public class WishListTabActivity extends BaseActivity
{
    private boolean mDontReloadAtOnResume;

    private WishListFragmentPagerAdapter mPageAdapter;

    private WishListTabNetworkController mNetworkController;

    private View mNeedLoginView;
    private View mNeedLoginButtonView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private PlaceType mPlaceType;

    public static Intent newInstance(Context context, PlaceType placeType)
    {
        Intent intent = new Intent(context, WishListTabActivity.class);

        if (placeType != null)
        {
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wishlist);

        mNetworkController = new WishListTabNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        initIntent(getIntent());

        initLayout();
    }

    @Override
    protected void onResume()
    {
        if (DailyHotel.isLogin() == false)
        {
            lockUI();
            setNeedLoginViewVisibility(View.VISIBLE);
        } else
        {
            setNeedLoginViewVisibility(View.GONE);

            if (mDontReloadAtOnResume == true)
            {
                mDontReloadAtOnResume = false;
            } else
            {
                lockUI();
                mNetworkController.requestCommonDateTime();
            }
        }

        super.onResume();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    private void initIntent(Intent intent) {
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

        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_hotel).setTag(PlaceType.HOTEL));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_fnb).setTag(PlaceType.FNB));
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTabLayout.getLayoutParams();
        layoutParams.topMargin = 1 - Util.dpToPx(this, 1);

        mTabLayout.setLayoutParams(layoutParams);

        FontManager.apply(mTabLayout, FontManager.getInstance(this).getRegularTypeface());
    }

    private void initLayout() {
        initToolbar();
        initTabLayout();

        mNeedLoginView = findViewById(R.id.loginLayout);
        mNeedLoginButtonView = findViewById(R.id.loginButtonView);
        mViewPager = (DailyViewPager) findViewById(R.id.viewPager);

        mNeedLoginButtonView.setOnClickListener(mLoginButtonClickListener);

        ArrayList<PlaceWishListFragment> fragmentList = new ArrayList<>();

        StayWishListFragment stayWishListFragment = new StayWishListFragment();
        stayWishListFragment.setWishListListFragmentListener(mWishListFragmentListener);
        stayWishListFragment.setPlaceType(PlaceType.HOTEL);

        fragmentList.add(stayWishListFragment);

        GourmetWishListFragment gourmetWishListFragment = new GourmetWishListFragment();
        gourmetWishListFragment.setWishListListFragmentListener(mWishListFragmentListener);
        gourmetWishListFragment.setPlaceType(PlaceType.FNB);

        fragmentList.add(gourmetWishListFragment);

        mPageAdapter = new WishListFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
    }

    private void setTabLayout(int stayWishListCount, int gourmetWishListCount)
    {
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
            if (stayWishListCount == 0 && gourmetWishListCount == 0)
            {
//                AnalyticsManager.getInstance(WishListTabActivity.this).recordScreen(AnalyticsManager.Screen.MENU_RECENT_VIEW_EMPTY);
            } else if (stayWishListCount == 0)
            {
                position = 1;
            }
        }

        mViewPager.removeAllViews();
        mViewPager.setOffscreenPageLimit(1);

        mViewPager.setAdapter(mPageAdapter);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mViewPager.setCurrentItem(position);

        String placeTypeString = position == 1 ? AnalyticsManager.ValueType.GOURMET : AnalyticsManager.ValueType.STAY;

        HashMap<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.PLACE_TYPE, placeTypeString);
        params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, placeTypeString);

//        AnalyticsManager.getInstance(WishListTabActivity.this).recordScreen(AnalyticsManager.Screen.MENU_RECENT_VIEW, params);
    }

    private void setNeedLoginViewVisibility(int visibility)
    {
        mNeedLoginView.setVisibility(View.VISIBLE == visibility ? View.VISIBLE : View.GONE);
    }

    private View.OnClickListener mLoginButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
//            Intent intent = LoginActivity.newInstance(WishListTabActivity.this, Screen.DAILYHOTEL_DETAIL);
            Intent intent = LoginActivity.newInstance(WishListTabActivity.this);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN_BY_WISHLIST);
        }
    };

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

//            AnalyticsManager.getInstance(WishListTabActivity.this).recordEvent(//
//                AnalyticsManager.Category.NAVIGATION, //
//                AnalyticsManager.Action.RECENT_VIEW_TAB_CHANGE, //
//                tab.getPosition() == 1 ? AnalyticsManager.ValueType.GOURMET : AnalyticsManager.ValueType.HOTEL, null);
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

    private PlaceWishListFragment.OnWishListFragmentListener mWishListFragmentListener = new PlaceWishListFragment.OnWishListFragmentListener()
    {
        @Override
        public void onDeleteItemClick(PlaceType placeType, int position)
        {
//            if (PlaceType.FNB.equals(placeType) == true)
//            {
//                mRecentGourmetPlaces = recentPlaces;
//            }
//
//            if (PlaceType.HOTEL.equals(placeType) == true)
//            {
//                mRecentStayPlaces = recentPlaces;
//            }
//
//            int stayCount = mRecentStayPlaces.size();
//            int gourmetCount = mRecentGourmetPlaces.size();

//            if (stayCount == 0 && gourmetCount == 0)
//            {
//                AnalyticsManager.getInstance(RecentPlacesTabActivity.this).recordScreen(AnalyticsManager.Screen.MENU_RECENT_VIEW_EMPTY);
//            }
        }
    };

    private WishListTabNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new WishListTabNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCommonDateTime(long currentDateTime, long dailyDateTime)
        {
            SaleTime saleTime = new SaleTime();
            saleTime.setCurrentTime(currentDateTime);
            saleTime.setDailyTime(dailyDateTime);
            saleTime.setOffsetDailyDay(0);
            //
            //            if (mFragmentList != null)
            //            {
            //                for (RecentPlacesListFragment fragment : mFragmentList)
            //                {
            //                    fragment.setSaleTime(saleTime);
            //                }
            //            }
            //
            //            setTabLayout();

            mNetworkController.requestWishListCount();

        }

        @Override
        public void onWishListCount(int stayWishListCount, int gourmetWishListCount)
        {
            setTabLayout(stayWishListCount, gourmetWishListCount);
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