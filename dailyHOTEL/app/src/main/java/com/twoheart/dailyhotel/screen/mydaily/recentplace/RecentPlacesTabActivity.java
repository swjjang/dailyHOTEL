package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.daily.base.widget.DailyViewPager;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.local.model.RecentlyRealmObject;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.FacebookRemoteImpl;
import com.daily.dailyhotel.repository.remote.KakaoRemoteImpl;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;
import retrofit2.HttpException;

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

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    DailyViewPager mViewPager;
    private TabLayout mTabLayout;
    private View mEmptyView;

    private SourceType mSourceType;
    private PlaceType mPlaceType;

    private boolean mDontReloadAtOnResume;

    public enum SourceType
    {
        HOME,
        MYDAILY
    }

    /**
     *
     * @param context
     * @param sourceType 진입 화면
     * @param placeType 딥링크로 인한 화면 이동 처리를 위한 타입
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

        mCommonRemoteImpl = new CommonRemoteImpl(this);

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
                .subscribe(commonDateTime ->
                {
                    if (mFragmentList != null)
                    {
                        for (RecentPlacesListFragment fragment : mFragmentList)
                        {
                            fragment.setPlaceBookingDay(commonDateTime);
                        }
                    }

                    setTabLayout();
                }, throwable -> onHandleError(throwable)));
        }

        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        clearCompositeDisposable();

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

        mEmptyView = findViewById(R.id.emptyLayout);
        mViewPager = (DailyViewPager) findViewById(R.id.viewPager);

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
            if (isEmptyRecentStayPlace() == true && isEmptyRecentGourmetPlace() == true)
            {
                AnalyticsManager.getInstance(RecentPlacesTabActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_RECENT_VIEW_EMPTY, null);
            } else if (isEmptyRecentStayPlace() == true)
            {
                position = 1;
            }
        }

        mViewPager.removeAllViews();
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

    private boolean isEmptyRecentStayPlace()
    {
        RealmResults<RecentlyRealmObject> resultList = RecentlyPlaceUtil.getRecentlyTypeList( //
            RecentlyPlaceUtil.ServiceType.IB_STAY, RecentlyPlaceUtil.ServiceType.OB_STAY);
        return resultList == null || resultList.size() == 0;
    }

    private boolean isEmptyRecentGourmetPlace()
    {
        RealmResults<RecentlyRealmObject> resultList = RecentlyPlaceUtil.getRecentlyTypeList(RecentlyPlaceUtil.ServiceType.GOURMET);
        return resultList == null || resultList.size() == 0;
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
            {
                mDontReloadAtOnResume = true;

                setResult(resultCode);

                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    finish();
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
            RealmResults<RecentlyRealmObject> resultList = RecentlyPlaceUtil.getRecentlyTypeList((RecentlyPlaceUtil.ServiceType[]) null);

            if (resultList == null || resultList.size() == 0)
            {
                AnalyticsManager.getInstance(RecentPlacesTabActivity.this).recordScreen( //
                    RecentPlacesTabActivity.this, AnalyticsManager.Screen.MENU_RECENT_VIEW_EMPTY, null);
            }
        }
    };

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 기존의 BaseActivity에 있는 정보 가져오기
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void addCompositeDisposable(Disposable disposable)
    {
        if (disposable == null)
        {
            return;
        }

        mCompositeDisposable.add(disposable);
    }

    private void clearCompositeDisposable()
    {
        mCompositeDisposable.clear();
    }

    protected void onHandleError(Throwable throwable)
    {
        unLockUI();

        BaseActivity baseActivity = RecentPlacesTabActivity.this;

        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;

            baseActivity.showSimpleDialog(null, baseException.getMessage()//
                , getString(R.string.dialog_btn_text_confirm), null, null, null, null, dialogInterface -> RecentPlacesTabActivity.this.onBackPressed(), true);
        } else if (throwable instanceof HttpException)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
            {
                addCompositeDisposable(new ConfigLocalImpl(RecentPlacesTabActivity.this).clear().subscribe(object ->
                {
                    new FacebookRemoteImpl().logOut();
                    new KakaoRemoteImpl().logOut();

                    baseActivity.restartExpiredSession();
                }));
            } else
            {
                DailyToast.showToast(RecentPlacesTabActivity.this, getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);

                if (Constants.DEBUG == false)
                {
                    Crashlytics.log(httpException.response().raw().request().url().toString());
                    Crashlytics.logException(throwable);
                } else
                {
                    ExLog.e(httpException.response().raw().request().url().toString() + ", " + httpException.toString());
                }

                RecentPlacesTabActivity.this.finish();
            }
        } else
        {
            DailyToast.showToast(RecentPlacesTabActivity.this, getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);

            RecentPlacesTabActivity.this.finish();
        }
    }
}
