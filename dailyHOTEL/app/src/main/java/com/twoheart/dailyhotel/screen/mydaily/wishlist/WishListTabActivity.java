package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyViewPager;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.WishCount;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.WishRemoteImpl;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.daily.dailyhotel.view.DailyWishAnimationView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.reflect.Field;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class WishListTabActivity extends BaseActivity
{
    ArrayList<PlaceWishListFragment> mFragmentList;
    private WishListFragmentPageAdapter mPageAdapter;

    CommonRemoteImpl mCommonRemoteImpl;
    WishRemoteImpl mWishRemoteImpl;

    DailyViewPager mViewPager;
    private TabLayout mTabLayout;
    private View mLoginView;
    private View mBottomDescriptionView;
    DailyWishAnimationView mWishAnimationView;

    private SourceType mSourceType;
    private PlaceType mPlaceType;

    private boolean mDontReloadAtOnResume;

    public enum SourceType
    {
        HOME,
        MYDAILY
    }

    public static Intent newInstance(Context context, SourceType sourceType, PlaceType placeType)
    {
        if (sourceType == null)
        {
            return null;
        }

        Intent intent = new Intent(context, WishListTabActivity.class);

        if (placeType != null)
        {
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        }

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, sourceType.name());
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wishlist);

        mCommonRemoteImpl = new CommonRemoteImpl();
        mWishRemoteImpl = new WishRemoteImpl();

        initIntent(getIntent());

        initLayout();
    }

    @Override
    protected void onResume()
    {
        if (DailyHotel.isLogin() == false)
        {
            setLoginViewVisibility(View.VISIBLE);
            setBottomViewVisibility(View.GONE);
        } else
        {
            setLoginViewVisibility(View.GONE);
            setBottomViewVisibility(View.VISIBLE);

            if (mDontReloadAtOnResume == true)
            {
                mDontReloadAtOnResume = false;
            } else
            {
                lockUI();
                onRefresh();
            }
        }

        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        switch (mSourceType)
        {
            case HOME:
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                    AnalyticsManager.Action.WISHLIST_BACK_BUTTON_CLICK, AnalyticsManager.Label.HOME, null);
                break;

            case MYDAILY:
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                    AnalyticsManager.Action.WISHLIST_BACK_BUTTON_CLICK, AnalyticsManager.Label.MYDAILY, null);
                break;
        }
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

        if (DailyTextUtils.isTextEmpty(placeTypeName) == false)
        {
            try
            {
                mPlaceType = PlaceType.valueOf(placeTypeName);
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
            }
        }

        try
        {
            mSourceType = SourceType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_TYPE));
        } catch (Exception e)
        {
            Util.restartApp(this);
            return;
        }
    }

    private void initLayout()
    {
        initToolbar();
        initTabLayout();

        mLoginView = findViewById(R.id.loginLayout);
        View mLoginButtonView = findViewById(R.id.loginButtonView);
        mViewPager = findViewById(R.id.viewPager);
        mBottomDescriptionView = findViewById(R.id.bottomMessageTextView);
        mWishAnimationView = findViewById(R.id.wishAnimationView);

        mFragmentList = new ArrayList<>();

        StayWishListFragment stayWishListFragment = new StayWishListFragment();
        stayWishListFragment.setWishListFragmentListener(mWishListFragmentListener);

        mFragmentList.add(stayWishListFragment);

        GourmetWishListFragment gourmetWishListFragment = new GourmetWishListFragment();
        gourmetWishListFragment.setWishListFragmentListener(mWishListFragmentListener);

        mFragmentList.add(gourmetWishListFragment);

        mPageAdapter = new WishListFragmentPageAdapter(getSupportFragmentManager(), mFragmentList);

        mLoginButtonView.setOnClickListener(mLoginButtonClickListener);
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_wishList);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initTabLayout()
    {
        mTabLayout = findViewById(R.id.tabLayout);

        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_stay));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_gourmet));
        //        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTabLayout.getLayoutParams();
        layoutParams.topMargin = 1 - ScreenUtils.dpToPx(this, 1);

        mTabLayout.setLayoutParams(layoutParams);

        FontManager.apply(mTabLayout, FontManager.getInstance(this).getRegularTypeface());
    }

    void setTabLayout(int stayCount, int gourmetCount)
    {
        int position = 0;

        if (mFragmentList != null)
        {
            for (PlaceWishListFragment fragment : mFragmentList)
            {
                if (PlaceType.FNB.equals(fragment.getPlaceType()) == true)
                {
                    fragment.setWishListCount(gourmetCount);
                } else
                {
                    fragment.setWishListCount(stayCount);
                }
            }
        }

        if (mPlaceType != null)
        {
            // deep link
            if (PlaceType.FNB.equals(mPlaceType) == true)
            {
                position = 1;
            }

            // deep link 후 초기화
            mPlaceType = null;
        } else
        {
            if (stayCount == 0 && gourmetCount == 0)
            {
                AnalyticsManager.getInstance(WishListTabActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_WISHLIST_EMPTY, null);
            } else if (stayCount == 0)
            {
                position = 1;
            }
        }

        if (mViewPager.getChildCount() > 0)
        {
            mViewPager.removeAllViews();
        }

        mViewPager.setOffscreenPageLimit(1);

        TabLayout.Tab selectedTab = mTabLayout.getTabAt(position);

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

        if (selectedTab != null)
        {
            selectedTab.select();
        }

        mViewPager.setAdapter(mPageAdapter);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
        //        mViewPager.setCurrentItem(position);
    }

    private void setLoginViewVisibility(int visibility)
    {
        mLoginView.setVisibility(visibility);

        if (View.VISIBLE == visibility)
        {
            AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.MENU_WISHLIST_BEFORELOGIN, null);
        }
    }

    private void setBottomViewVisibility(int visibility)
    {
        if (mBottomDescriptionView != null)
        {
            mBottomDescriptionView.setVisibility(visibility);
        }
    }

    private void onRefresh()
    {
        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime(), mWishRemoteImpl.getWishCount()//
            , mWishRemoteImpl.getStayOutboundWishCount(this), new Function3<CommonDateTime, WishCount, Integer, WishCount>()
            {
                @Override
                public WishCount apply(CommonDateTime commonDateTime, WishCount wishCount, Integer wishStayOutboundCount) throws Exception
                {
                    if (mFragmentList != null)
                    {
                        TodayDateTime todayDateTime = commonDateTime.getTodayDateTime();

                        for (PlaceWishListFragment fragment : mFragmentList)
                        {
                            fragment.setPlaceBookingDay(todayDateTime);
                        }
                    }

                    wishCount.wishStayOutboundCount = wishStayOutboundCount;

                    return wishCount;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WishCount>()
        {
            @Override
            public void accept(WishCount wishCount) throws Exception
            {
                unLockUI();

                WishListTabActivity.this.setTabLayout(wishCount.wishStayCount + wishCount.wishStayOutboundCount, wishCount.wishGourmetCount);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
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
            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                mDontReloadAtOnResume = false;
                break;
            }

            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            {
                mDontReloadAtOnResume = true;

                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        finish();
                        break;

                    case CODE_RESULT_ACTIVITY_REFRESH:
                    {
                        boolean isChangeWishList = false;
                        if (data != null)
                        {
                            isChangeWishList = data.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_CHANGE_WISHLIST, false);
                        }

                        if (isChangeWishList == true)
                        {
                            PlaceType placeType;
                            if (requestCode == CODE_REQUEST_ACTIVITY_GOURMET_DETAIL)
                            {
                                placeType = PlaceType.FNB;
                            } else
                            {
                                placeType = PlaceType.HOTEL;
                            }

                            for (PlaceWishListFragment fragment : mFragmentList)
                            {
                                if (placeType.equals(fragment.getPlaceType()) == true)
                                {
                                    fragment.forceRefreshList();
                                }
                            }
                        }
                        break;
                    }

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                    {
                        PlaceType placeType;

                        if (requestCode == CODE_REQUEST_ACTIVITY_GOURMET_DETAIL)
                        {
                            placeType = PlaceType.FNB;
                        } else
                        {
                            placeType = PlaceType.HOTEL;
                        }

                        for (PlaceWishListFragment fragment : mFragmentList)
                        {
                            if (placeType.equals(fragment.getPlaceType()) == true)
                            {
                                fragment.forceRefreshList();
                            }
                        }
                        break;
                    }
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_PREVIEW:
                mDontReloadAtOnResume = true;

                mFragmentList.get(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void startLogin()
    {
        Intent intent = LoginActivity.newInstance(this, null);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_,//
            AnalyticsManager.Action.WISHLIST_LOGIN_CLICKED, null, null);
    }

    private final View.OnClickListener mLoginButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            startLogin();
        }
    };

    private final TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            AnalyticsManager.getInstance(WishListTabActivity.this).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.WISHLIST_TAB_CHANGE, //
                tab.getPosition() == 1 ? AnalyticsManager.ValueType.GOURMET : AnalyticsManager.ValueType.STAY, null);
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

    private final PlaceWishListFragment.OnWishListFragmentListener mWishListFragmentListener = new PlaceWishListFragment.OnWishListFragmentListener()
    {
        @Override
        public void onRemoveItemClick(PlaceType placeType, int position)
        {
            if (mFragmentList == null)
            {
                return;
            }

            if (mWishAnimationView != null)
            {
                Observable<Boolean> observable = mWishAnimationView.removeWishAnimation();

                if (observable != null)
                {
                    lockUI(false);

                    addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception
                        {
                            unLockUI();
                        }
                    }));
                }
            }

            int totalCount = 0;

            for (PlaceWishListFragment fragment : mFragmentList)
            {
                PlaceWishListLayout placeWishListLayout = fragment.getListLayout();
                if (placeWishListLayout != null)
                {
                    totalCount += placeWishListLayout.getRealItemCount();
                }
            }

            if (totalCount == 0)
            {
                AnalyticsManager.getInstance(WishListTabActivity.this).recordScreen(WishListTabActivity.this, AnalyticsManager.Screen.MENU_WISHLIST_EMPTY, null);
            }
        }
    };
}
