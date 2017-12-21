package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseFragmentExceptionPresenter;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragmentPresenter extends BaseFragmentExceptionPresenter<StayListFragment, StayListFragmentView> implements StayListFragmentView.OnEventListener
{
    private StayListFragmentAnalyticsInterface mAnalytics;

    StayRemoteImpl mStayRemoteImpl;

    StayTabPresenter.StayViewModel mStayViewModel;

    public interface StayListFragmentAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public interface OnStayListFragmentListener
    {
        // 왜 onActivityCreated 했을까?
        // http://blog.saltfactory.net/android/implement-layout-using-with-fragment.html
        void onActivityCreated(StayListFragmentPresenter stayListFragment);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onShowMenuBar();

        void onBottomOptionVisible(boolean visible);

        void onUpdateFilterEnabled(boolean isShowFilterEnabled);

        void onUpdateViewTypeEnabled(boolean isShowViewTypeEnabled);

        void onFilterClick();

        void onShowActivityEmptyView(boolean isShow);

        void onSearchCountUpdate(int searchCount, int searchMaxCount);

        void onStayClick(View view, ObjectItem objectItem, int listCount);

        void onStayLongClick(View view, ObjectItem objectItem, int listCount);

        void onRegionClick();

        void onCalendarClick();

        void onRecordAnalytics(Constants.ViewType viewType);
    }

    public StayListFragmentPresenter(@NonNull StayListFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return getViewInterface().getContentView(inflater, R.layout.fragment_stay_list_data, container);
    }

    @NonNull
    @Override
    protected StayListFragmentView createInstanceViewInterface()
    {
        return new StayListFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new StayListFragmentAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl(activity);

        initViewModel(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayListFragmentAnalyticsInterface) analytics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

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
    public void onBackClick()
    {

    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);


    }

    protected synchronized void onRefresh(boolean showProgress, int page)
    {
        //        if (mStayCuration == null)
        //        {
        //            unLockUI();
        //            Util.restartApp(mBaseActivity);
        //            return;
        //        }
        //
        //        // 더보기 시 unlock 걸지않음
        //        if (page <= 1)
        //        {
        //            lockUI(isShowProgress);
        //        }
        //
        //        StayBookingDay stayBookingDay = mStayCuration.getStayBookingDay();
        //        StayRegion region = mStayCuration.getRegion();
        //
        //        if (region == null || stayBookingDay == null)
        //        {
        //            unLockUI();
        //            Util.restartApp(mBaseActivity);
        //            return;
        //        }
        //
        //        if (mStayCuration == null || mStayCuration.getCurationOption() == null//
        //            || mStayCuration.getCurationOption().getSortType() == null//
        //            || (mStayCuration.getCurationOption().getSortType() == SortType.DISTANCE && mStayCuration.getLocation() == null))
        //        {
        //            unLockUI();
        //            Util.restartApp(mBaseActivity);
        //            return;
        //        }
        //
        //        StayParams params = (StayParams) mStayCuration.toPlaceParams(page, PAGENATION_LIST_SIZE, true);
        //        String abTestType = DailyRemoteConfigPreference.getInstance(getContext()).getKeyRemoteConfigStayRankTestType();
        //
        //        ((StayListNetworkController) mNetworkController).requestStayList(params, abTestType);
    }


    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mStayViewModel = ViewModelProviders.of(activity).get(StayTabPresenter.StayViewModel.class);

        ExLog.d("pinkred : " + mStayViewModel.stayRegion.getValue().getAreaName() + ", " + mStayViewModel.commonDateTime.getValue().dailyDateTime + ", " + mStayViewModel.stayBookDateTime.getValue().getNights() + ", " + mStayViewModel.category.getValue().name);
    }
}
