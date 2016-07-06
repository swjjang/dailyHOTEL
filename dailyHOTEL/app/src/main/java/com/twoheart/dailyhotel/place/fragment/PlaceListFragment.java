/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * StayListFragment (호텔 목록 화면)
 * <p>
 * 어플리케이션의 가장 주가 되는 화면으로서 호텔들의 목록을 보여주는 화면이다.
 * 호텔 리스트는 따로 커스텀되어 구성되어 있으며, 액션바의 네비게이션을 이용
 * 하여 큰 지역을 분리하고 리스트뷰 헤더를 이용하여 세부 지역을 나누어 표시
 * 한다. 리스트뷰의 맨 첫 아이템은 이벤트 참여하기 버튼이 있으며, 이 버튼은
 * 서버의 이벤트 API에 따라 NEW 아이콘을 붙여주기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.place.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.util.Constants;

public abstract class PlaceListFragment extends BaseFragment implements Constants
{
    protected OnPlaceListFragmentListener mOnPlaceListFragmentListener;
    protected View mBottomOptionLayout; // 애니매이션 때문에 어쩔수 없음.

    // onPlaceClick 부분이 있는데 이부분은 고메와 호텔은 서로 상속받아서 사용한다.
    public interface OnPlaceListFragmentListener
    {
        void onEventBannerClick(EventBanner eventBanner);

        void onActivityCreated(PlaceListFragment placeListFragment);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onShowMenuBar();
    }

    public abstract void clearList();

    public abstract void refreshList(boolean isShowProgress);

    public abstract void setVisibility(ViewType viewType, boolean isCurrentPage);

    public abstract void setScrollListTop();

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
}
