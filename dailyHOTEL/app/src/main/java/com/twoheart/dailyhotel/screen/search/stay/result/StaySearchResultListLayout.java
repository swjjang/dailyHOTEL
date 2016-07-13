package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.screen.hotel.list.StayListLayout;
import com.twoheart.dailyhotel.screen.hotel.list.StayListMapFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StaySearchResultListLayout extends StayListLayout
{
    public StaySearchResultListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    public void addResultList(FragmentManager fragmentManager, Constants.ViewType viewType, //
                              ArrayList<PlaceViewItem> list, Constants.SortType sortType)
    {
        mIsLoading = false;

        if (mPlaceListAdapter == null)
        {
            Util.restartApp(mContext);
            return;
        }

        if (viewType == Constants.ViewType.LIST)
        {
            setVisibility(fragmentManager, viewType, true);

            // 리스트의 경우 Pagenation 상황 고려
            List<PlaceViewItem> oldList = getList();

            if (oldList != null && oldList.size() > 0)
            {
                PlaceViewItem placeViewItem = oldList.get(oldList.size() - 1);

                // 기존 리스트가 존재 할 때 마지막 아이템이 footer 일 경우 아이템 제거
                switch (placeViewItem.mType)
                {
                    case PlaceViewItem.TYPE_FOOTER_VIEW:
                    case PlaceViewItem.TYPE_LOADING_VIEW:
                        oldList.remove(placeViewItem); // 실제 삭제
                        break;
                }
            }

            if (list != null && list.size() > 0)
            {
                mPlaceListAdapter.addAll(list);

                if (list.size() < Constants.PAGENATION_LIST_SIZE)
                {
                    mPlaceListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, true));
                } else
                {
                    mPlaceListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_LOADING_VIEW, null));
                }
            } else
            {
                // 요청 온 데이터가 empty 일때 기존 리스트가 있으면 라스트 footer 재 생성
                if (oldList.size() > 0)
                {
                    mPlaceListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, true));
                }
            }

            int size = getItemCount();
            if (size == 0)
            {
                mPlaceListAdapter.notifyDataSetChanged();
                setVisibility(fragmentManager, Constants.ViewType.GONE, true);
            } else
            {
                mPlaceListAdapter.setSortType(sortType);
                mPlaceListAdapter.notifyDataSetChanged();
            }
        } else
        {

        }
    }
}
