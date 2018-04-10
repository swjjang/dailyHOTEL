package com.twoheart.dailyhotel.screen.mydaily.recentplace;

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
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.reflect.Field;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by android_sam on 2016. 10. 10..
 */

public class RecentPlacesTabActivity extends BaseActivity
{
    ArrayList<RecentPlacesListFragment> mFragmentList;

    private RecentStayListFragment mRecentStayListFragment;
    private RecentGourmetListFragment mRecentGourmetListFragment;

    private RecentPlacesFragmentPagerAdapter mPageAdapter;

    private CommonRemoteImpl mCommonRemoteImpl;
    RecentlyLocalImpl mRecentlyLocalImpl;

    DailyViewPager mViewPager;
    private TabLayout mTabLayout;

    private SourceType mSourceType;
    private PlaceType mPlaceType;

    private boolean mDontReloadAtOnResume;

    public enum SourceType
    {
        HOME,
        MYDAILY
    }

    /**
     * @param context
     * @param sourceType 진입 화면
     * @param placeType  딥링크로 인한 화면 이동 처리를 위한 타입
     * @return
     */
    public static Intent newInstance(Context context, SourceType sourceType, PlaceType placeType)
    {
        if (sourceType == null)
        {
            return null;
        }

        Intent intent = new Intent(context, RecentPlacesTabActivity.class);

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

        setContentView(R.layout.activity_recent_places);

        mCommonRemoteImpl = new CommonRemoteImpl();
        mRecentlyLocalImpl = new RecentlyLocalImpl();

        initIntent(getIntent());

        initLayout();
    }

    @Override
    protected void onResume()
    {
        if (mDontReloadAtOnResume == true)
        {
            mDontReloadAtOnResume = false;

            if (mFragmentList != null && mFragmentList.size() != 0)
            {
                for (RecentPlacesListFragment fragment : mFragmentList)
                {
                    fragment.setDontReload(true);
                }
            }
        } else
        {
            lockUI();

            addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime() //
                .subscribe(commonDateTime -> {
                    if (mFragmentList != null)
                    {
                        for (RecentPlacesListFragment fragment : mFragmentList)
                        {
                            fragment.setPlaceBookingDay(commonDateTime);
                        }
                    }

                    setTabLayout();
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        RecentPlacesTabActivity.this.onHandleError(throwable);
                    }
                }));
        }

        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        switch (mSourceType)
        {
            case HOME:
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                    AnalyticsManager.Action.RECENTVIEW_BACK_BUTTON_CLICK, AnalyticsManager.Label.HOME, null);
                break;

            case MYDAILY:
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                    AnalyticsManager.Action.RECENTVIEW_BACK_BUTTON_CLICK, AnalyticsManager.Label.MYDAILY, null);
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

        mViewPager = findViewById(R.id.viewPager);

        mFragmentList = new ArrayList<>();

        mRecentStayListFragment = new RecentStayListFragment();
        mRecentStayListFragment.setRecentPlaceListFragmentListener(mRecentPlaceListFragmentListener);

        mFragmentList.add(mRecentStayListFragment);

        mRecentGourmetListFragment = new RecentGourmetListFragment();
        mRecentGourmetListFragment.setRecentPlaceListFragmentListener(mRecentPlaceListFragmentListener);

        mFragmentList.add(mRecentGourmetListFragment);

        mPageAdapter = new RecentPlacesFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.frag_recent_places);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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
        mTabLayout = findViewById(R.id.tabLayout);

        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_stay));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.label_gourmet));
        //        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTabLayout.getLayoutParams();
        layoutParams.topMargin = 1 - ScreenUtils.dpToPx(this, 1);

        mTabLayout.setLayoutParams(layoutParams);

        FontManager.apply(mTabLayout, FontManager.getInstance(this).getRegularTypeface());
    }

    void setTabLayout()
    {
        if (mPlaceType != null)
        {
            int position = 0;

            // deeplink type
            if (PlaceType.FNB.equals(mPlaceType) == true)
            {
                position = 1;
            }

            // deeplink 로 인한 처리 후 초기화
            mPlaceType = null;

            setTabLayout(position);
            return;
        }

        // mPlaceType == null 일때 - DeepLink 가 아닐때
        addCompositeDisposable(Observable.zip( //
            mRecentlyLocalImpl.getRecentlyTypeList(RecentPlacesTabActivity.this, Constants.ServiceType.HOTEL, Constants.ServiceType.OB_STAY) //
            , mRecentlyLocalImpl.getRecentlyTypeList(RecentPlacesTabActivity.this, Constants.ServiceType.GOURMET) //
            , new BiFunction<ArrayList<RecentlyDbPlace>, ArrayList<RecentlyDbPlace>, Integer>()
            {
                @Override
                public Integer apply(@NonNull ArrayList<RecentlyDbPlace> stayList, @NonNull ArrayList<RecentlyDbPlace> gourmetList) throws Exception
                {
                    int position = 0;

                    // 이전에 선택된 탭이 있는 경우
                    if (mViewPager != null && mViewPager.getChildCount() > 0)
                    {
                        position = mViewPager.getCurrentItem();
                    } else
                    {
                        boolean isEmptyStayList = stayList == null || stayList.size() == 0;
                        boolean isEmptyGourmetList = gourmetList == null || gourmetList.size() == 0;
                        if (isEmptyStayList == true && isEmptyGourmetList == true)
                        {
                            AnalyticsManager.getInstance(RecentPlacesTabActivity.this).recordScreen(RecentPlacesTabActivity.this, AnalyticsManager.Screen.MENU_RECENT_VIEW_EMPTY, null);
                        } else if (isEmptyStayList == true)
                        {
                            position = 1;
                        }
                    }

                    return position;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>()
        {
            @Override
            public void accept(Integer integer) throws Exception
            {
                setTabLayout(integer);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                setTabLayout(0);
            }
        }));
    }

    void setTabLayout(int position)
    {
        mViewPager.removeAllViews();
        mViewPager.setOffscreenPageLimit(1);

        mViewPager.setAdapter(mPageAdapter);
        mViewPager.clearOnPageChangeListeners();

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

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
        //        mViewPager.setCurrentItem(position);
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
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case RecentStayListFragment.REQUEST_CODE_DETAIL:
            {
                mDontReloadAtOnResume = true;

                switch (resultCode)
                {
                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:

                        if (data == null)
                        {
                            mDontReloadAtOnResume = false;
                        } else
                        {
                            if (data.hasExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH) == true//
                                || data.hasExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                            {
                                mFragmentList.get(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
                            } else
                            {
                                mDontReloadAtOnResume = false;
                            }
                        }
                        break;

                    case CODE_RESULT_ACTIVITY_REFRESH:
                    case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                        mDontReloadAtOnResume = false;
                        break;

                    default:
                        break;
                }

                break;
            }

            case CODE_REQUEST_ACTIVITY_PREVIEW:
                mDontReloadAtOnResume = true;

                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case com.daily.base.BaseActivity.RESULT_CODE_DATA_CHANGED:
                        mFragmentList.get(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        mDontReloadAtOnResume = false;
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_REFRESH:
                        if (data == null)
                        {
                            mDontReloadAtOnResume = false;
                        } else
                        {
                            mFragmentList.get(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
                        }
                        break;
                }
                break;

            case Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG:
                if (resultCode == com.daily.base.BaseActivity.RESULT_CODE_REFRESH)
                {
                    mDontReloadAtOnResume = false;
                } else
                {
                    mDontReloadAtOnResume = true;

                    mFragmentList.get(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
                }
                break;
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

            AnalyticsManager.getInstance(RecentPlacesTabActivity.this).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.RECENT_VIEW_TAB_CHANGE, //
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

    private RecentPlacesListFragment.OnRecentPlaceListFragmentListener mRecentPlaceListFragmentListener //
        = new RecentPlacesListFragment.OnRecentPlaceListFragmentListener()
    {
        @Override
        public void onDeleteItemClickAnalytics()
        {
            addCompositeDisposable(mRecentlyLocalImpl.getRecentlyTypeList(RecentPlacesTabActivity.this, (Constants.ServiceType[]) null) //
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<RecentlyDbPlace>>()
                {
                    @Override
                    public void accept(ArrayList<RecentlyDbPlace> recentlyDbPlaces) throws Exception
                    {
                        if (recentlyDbPlaces == null || recentlyDbPlaces.size() == 0)
                        {
                            AnalyticsManager.getInstance(RecentPlacesTabActivity.this).recordScreen( //
                                RecentPlacesTabActivity.this, AnalyticsManager.Screen.MENU_RECENT_VIEW_EMPTY, null);
                        }
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        ExLog.d(throwable.getMessage());
                    }
                }));
        }
    };
}
