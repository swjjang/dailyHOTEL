package com.twoheart.dailyhotel.screen.search.stay.result;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StaySearchCuration;
import com.twoheart.dailyhotel.model.StaySearchParams;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.hotel.list.StayListFragment;
import com.twoheart.dailyhotel.screen.hotel.list.StayListLayout;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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
                    mOnPlaceListFragmentListener.onSearchCountUpdate(totalCount, maxCount);
                }
            }

            StaySearchResultListFragment.this.onStayList(list, page, false);

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
        public void onError(Throwable e)
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

        @Override
        public void onErrorResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            StaySearchResultListFragment.this.onErrorResponse(call, response);
        }
    };
}
