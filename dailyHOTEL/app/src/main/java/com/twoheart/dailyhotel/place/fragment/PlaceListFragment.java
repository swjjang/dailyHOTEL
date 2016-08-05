package com.twoheart.dailyhotel.place.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.util.Constants;

public abstract class PlaceListFragment extends BaseFragment implements Constants
{
    protected OnPlaceListFragmentListener mOnPlaceListFragmentListener;
    protected View mBottomOptionLayout; // 애니매이션 때문에 어쩔수 없음.
    protected ViewType mViewType;

    // onPlaceClick 부분이 있는데 이부분은 고메와 호텔은 서로 상속받아서 사용한다.
    public interface OnPlaceListFragmentListener
    {
        void onEventBannerClick(EventBanner eventBanner);

        void onActivityCreated(PlaceListFragment placeListFragment);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onShowMenuBar();

        void onFilterClick();

        void onShowActivityEmptyView(boolean isShow);

        void onRecordAnalytics(Constants.ViewType viewType);
    }

    public abstract void setPlaceCuration(PlaceCuration curation);

    public abstract void clearList();

    public abstract void refreshList(boolean isShowProgress);

    public abstract void setVisibility(ViewType viewType, boolean isCurrentPage);

    public abstract void setScrollListTop();

    public abstract int getPlaceCount();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (mOnPlaceListFragmentListener != null)
        {
            mOnPlaceListFragmentListener.onActivityCreated(this);
        }
    }

    public void setPlaceOnListFragmentListener(OnPlaceListFragmentListener listener)
    {
        mOnPlaceListFragmentListener = listener;
    }

    public void setBottomOptionLayout(View view)
    {
        mBottomOptionLayout = view;
    }

    public void setViewType(ViewType viewType)
    {
        this.mViewType = viewType;
    }
}
