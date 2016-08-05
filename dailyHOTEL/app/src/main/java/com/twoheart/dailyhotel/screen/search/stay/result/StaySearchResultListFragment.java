package com.twoheart.dailyhotel.screen.search.stay.result;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StaySearchCuration;
import com.twoheart.dailyhotel.model.StaySearchParams;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.hotel.list.StayListFragment;
import com.twoheart.dailyhotel.screen.hotel.list.StayListLayout;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StaySearchResultListFragment extends StayListFragment
{
    private boolean mIsOptimizeCategory;
    private int mResultTotalCount;
    private int mResultMaxCount;

    public interface OnStaySearchResultListFragmentListener extends OnStayListFragmentListener
    {
        void onResultListCount(int count, int maxCount);

        void onCategoryList(List<Category> categoryList);
    }

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
    protected BaseNetworkController getStayListNetworkController()
    {
        return new StaySearchResultListNetworkController(mBaseActivity, mNetworkTag, onNetworkControllerListener);
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

        if (mStayCuration == null || mStayCuration.getCurationOption() == null//
            || mStayCuration.getCurationOption().getSortType() == null//
            || (mStayCuration.getCurationOption().getSortType() == SortType.DISTANCE && mStayCuration.getLocation() == null) //
            || (((StaySearchCuration) mStayCuration).getRadius() != 0d && mStayCuration.getLocation() == null))
        {
            unLockUI();
            Util.restartApp(mBaseActivity); // 제거 할 것인지 고민 필요.
            return;
        }

        StaySearchParams params = (StaySearchParams) mStayCuration.toPlaceParams(page, PAGENATION_LIST_SIZE, true);
        ((StaySearchResultListNetworkController) mNetworkController).requestStaySearchList(params);
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

        return stayViewItemList;
    }

    public int getResultTotalCount()
    {
        return mResultTotalCount;
    }

    public int getResultMaxCount()
    {
        return mResultMaxCount;
    }

    private StaySearchResultListNetworkController.OnNetworkControllerListener onNetworkControllerListener = new StaySearchResultListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayList(ArrayList<Stay> list, int page, int totalCount, int maxCount, List<Category> categoryList)
        {
            // 첫페이지 호출시에 카테고리 목록 조절
            if (mIsOptimizeCategory == false)
            {
                mIsOptimizeCategory = true;

                if (page == 1 && Category.ALL.code.equalsIgnoreCase(mStayCuration.getCategory().code) == true)
                {
                    ((OnStaySearchResultListFragmentListener) mOnPlaceListFragmentListener).onCategoryList(categoryList);
                }
            }

            StaySearchResultListFragment.this.onStayList(list, page);

            if (page <= 1)
            {
                mResultTotalCount = totalCount;
                mResultMaxCount = maxCount;
                ((OnStaySearchResultListFragmentListener) mOnPlaceListFragmentListener).onResultListCount(totalCount, maxCount);
            }
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
