package com.twoheart.dailyhotel.screen.search.stay.result;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayParams;
import com.twoheart.dailyhotel.screen.hotel.list.StayListFragment;
import com.twoheart.dailyhotel.screen.hotel.list.StayListLayout;
import com.twoheart.dailyhotel.screen.hotel.list.StayListNetworkController;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StaySearchResultListFragment extends StayListFragment
{
    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.fragment_stay_search_result_list;
    }

    @Override
    protected StayListLayout getStayListLayout()
    {
        return new StaySearchResultListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected StayListNetworkController getStayListNetworkController()
    {
        return new StayListNetworkController(mBaseActivity, mNetworkTag, onNetworkControllerListener);
    }

    @Override
    protected void refreshList(boolean isShowProgress, int page)
    {
        // 더보기 시 uilock 걸지않음
        if (page <= 1)
        {
            lockUI(isShowProgress);
        }

        int nights = mStayCuration.getNights();
        if (nights <= 0)
        {
            unLockUI();
            return;
        }

        mPageIndex = page;

        if (mStayCuration == null || mStayCuration.getCurationOption() == null//
            || mStayCuration.getCurationOption().getSortType() == null//
            || (mStayCuration.getCurationOption().getSortType() == SortType.DISTANCE && mStayCuration.getLocation() == null))
        {
            unLockUI();
            Util.restartApp(mBaseActivity); // 제거 할 것인지 고민 필요.
            return;
        }

        StayParams params = (StayParams) mStayCuration.toPlaceParams(page, PAGENATION_LIST_SIZE, true);
        mNetworkController.requestStayList(params);
    }

    @Override
    protected ArrayList<PlaceViewItem> makeSectionStayList(List<Stay> stayList, SortType sortType)
    {
        ArrayList<PlaceViewItem> stayViewItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return stayViewItemList;
        }

        for (Stay stay : stayList)
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
        }

        if (Constants.PAGENATION_LIST_SIZE > stayList.size())
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
        } else
        {
            stayViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_LOADING_VIEW, null));
        }

        return stayViewItemList;
    }

    private StayListNetworkController.OnNetworkControllerListener onNetworkControllerListener = new StayListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayList(ArrayList<Stay> list, int page)
        {
            StaySearchResultListFragment.this.onStayList(list, page);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            StaySearchResultListFragment.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            StaySearchResultListFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StaySearchResultListFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StaySearchResultListFragment.this.onErrorToastMessage(message);
        }
    };
}
