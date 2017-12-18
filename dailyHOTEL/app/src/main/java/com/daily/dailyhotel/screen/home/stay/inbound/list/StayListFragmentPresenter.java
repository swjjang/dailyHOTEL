package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseFragment;
import com.daily.base.BaseFragmentPresenter;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragmentPresenter extends BaseFragmentPresenter<StayListFragment, StayListFragmentView> implements StayListFragmentView.OnEventListener
{
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
        return getViewInterface().getContentView(inflater, R.layout.fragment_hotel_list, container);
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

    @NonNull
    @Override
    protected StayListFragmentView createInstanceViewInterface()
    {
        return new StayListFragmentView(this);
    }

    @Override
    public void constructorInitialize()
    {

    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {

    }

    @Override
    public void onBackClick()
    {

    }
}
