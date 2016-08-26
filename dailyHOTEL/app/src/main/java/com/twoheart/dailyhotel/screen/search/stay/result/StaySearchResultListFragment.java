package com.twoheart.dailyhotel.screen.search.stay.result;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceCuration;
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
    private boolean mIsDeepLink;
    private SearchType mSearchType;

    public interface OnStaySearchResultListFragmentListener extends OnStayListFragmentListener
    {
        void onCategoryList(List<Category> categoryList);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new StaySearchResultListNetworkController(mBaseActivity, mNetworkTag, onNetworkControllerListener);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.fragment_stay_search_result_list;
    }

    @Override
    protected StayListLayout getPlaceListLayout()
    {
        return new StaySearchResultListLayout(mBaseActivity, mEventListener);
    }

    public void setSearchType(SearchType searchType)
    {
        mSearchType = searchType;
    }

    @Override
    public void setPlaceCuration(PlaceCuration curation)
    {
        super.setPlaceCuration(curation);

        ((StaySearchResultListLayout) mPlaceListLayout).setSearchType(mSearchType);
    }

    @Override
    protected void refreshList(boolean isShowProgress, int page)
    {
        // 더보기 시 uilock 걸지않음
        if (page <= 1)
        {
            lockUI(isShowProgress);

            if (isShowProgress == true)
            {
                // 새로 검색이 될경우에는 결과개수를 보여주는 부분은 안보이게 한다.
                ((StaySearchResultListLayout) mPlaceListLayout).updateResultCount(mViewType, -1, -1);
            }
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
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return placeViewItemList;
        }

        int entryPosition = 1;

        if (mPlaceListLayout != null)
        {
            ArrayList<PlaceViewItem> oldList = new ArrayList<>(mPlaceListLayout.getList());

            int oldListSize = oldList == null ? 0 : oldList.size();
            if (oldListSize > 0)
            {
                int start = oldList == null ? 0 : oldList.size() - 1;
                int end = oldList == null ? 0 : oldListSize - 5;
                end = end < 0 ? 0 : end;

                // 5번안에 검사 안끝나면 그냥 종료, 원래는 1번에 검사되어야 함
                for (int i = start; i >= end; i--)
                {
                    PlaceViewItem item = oldList.get(i);
                    if (item.mType == PlaceViewItem.TYPE_ENTRY)
                    {
                        Place place = item.getItem();
                        entryPosition = place.entryPosition + 1;
                        break;
                    }
                }
            }
        }

        for (Stay stay : stayList)
        {
            stay.entryPosition = entryPosition;
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
            entryPosition++;
        }

        return placeViewItemList;
    }

    public void setIsDeepLink(boolean isDeepLink)
    {
        mIsDeepLink = isDeepLink;
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

                if (page <= 1 && Category.ALL.code.equalsIgnoreCase(mStayCuration.getCategory().code) == true)
                {
                    ((OnStaySearchResultListFragmentListener) mOnPlaceListFragmentListener).onCategoryList(categoryList);
                }
            }

            StaySearchResultListFragment.this.onStayList(list, page);

            if (mViewType == ViewType.MAP)
            {
                ((StaySearchResultListLayout) mPlaceListLayout).setMapMyLocation(mStayCuration.getLocation(), mIsDeepLink == false);
            }

            if (page <= 1)
            {
                ((StaySearchResultListLayout) mPlaceListLayout).updateResultCount(mViewType, totalCount, maxCount);
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
