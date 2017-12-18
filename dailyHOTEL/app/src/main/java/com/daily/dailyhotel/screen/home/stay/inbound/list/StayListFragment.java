package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.base.BaseFragment;
import com.daily.base.BaseFragmentPresenter;
import com.daily.dailyhotel.entity.ObjectItem;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragment extends BaseFragment<StayListFragmentPresenter>
{
    public interface OnStayListFragmentListener
    {
        // 왜 onActivityCreated 했을까?
        // http://blog.saltfactory.net/android/implement-layout-using-with-fragment.html
        void onActivityCreated(StayListFragment stayListFragment);

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

    @NonNull
    @Override
    protected StayListFragmentPresenter createInstancePresenter()
    {
        return new StayListFragmentPresenter();
    }
}
