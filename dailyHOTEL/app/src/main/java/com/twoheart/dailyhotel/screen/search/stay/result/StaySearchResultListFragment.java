package com.twoheart.dailyhotel.screen.search.stay.result;

import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StaySearchCuration;
import com.twoheart.dailyhotel.model.StaySearchParams;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.screen.hotel.list.StayListFragment;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class StaySearchResultListFragment extends StayListFragment
{
    boolean mResetCategory = true;
    boolean mIsDeepLink;
    SearchType mSearchType;

    public interface OnStaySearchResultListFragmentListener extends OnStayListFragmentListener
    {
        void onCategoryList(List<Category> categoryList);

        void onStayListCount(int count);
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
    public PlaceListLayout getPlaceListLayout()
    {
        if (mPlaceListLayout == null)
        {
            mPlaceListLayout = new StaySearchResultListLayout(mBaseActivity, mEventListener);
        }

        return mPlaceListLayout;
    }

    public void setSearchType(SearchType searchType)
    {
        mSearchType = searchType;
    }

    @Override
    public void setPlaceCuration(PlaceCuration curation)
    {
        if (mPlaceListLayout == null)
        {
            return;
        }

        super.setPlaceCuration(curation);

        ((StaySearchResultListLayout) mPlaceListLayout).setSearchType(mSearchType);
    }

    @Override
    protected void refreshList(boolean isShowProgress, int page)
    {
        // 더보기 시 unlock 걸지않음
        if (page <= 1)
        {
            lockUI(isShowProgress);

            if (isShowProgress == true)
            {
                // 새로 검색이 될경우에는 결과개수를 보여주는 부분은 안보이게 한다.
                ((StaySearchResultListLayout) mPlaceListLayout).updateResultCount(mViewType, -1, -1);
            }
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
        String abTestType = DailyRemoteConfigPreference.getInstance(getContext()).getKeyRemoteConfigStayRankTestType();

        ((StaySearchResultListNetworkController) mNetworkController).requestStaySearchList(params, abTestType);
    }

    @Override
    public void refreshList(boolean isShowProgress)
    {
        if (mViewType == null)
        {
            return;
        }

        mIsLoadMoreFlag = true;

        int size = mStayList.size();
        if (size == 0)
        {
            refreshList(isShowProgress, 1);
        } else
        {
            SortType sortType = mStayCuration.getCurationOption().getSortType();

            ArrayList<PlaceViewItem> placeViewItems = makePlaceList(mStayList, sortType, false);

            switch (mViewType)
            {
                case LIST:
                    mPlaceListLayout.addResultList(getChildFragmentManager(), mViewType, placeViewItems, sortType, mStayCuration.getStayBookingDay());
                    break;

                case MAP:
                    mPlaceListLayout.setList(getChildFragmentManager(), mViewType, placeViewItems, sortType, mStayCuration.getStayBookingDay());
                    break;

                default:
                    break;
            }
        }
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
            if (mResetCategory == true)
            {
                mResetCategory = false;

                if (page <= 1 && Category.ALL.code.equalsIgnoreCase(mStayCuration.getCategory().code) == true)
                {
                    ((OnStaySearchResultListFragmentListener) mOnPlaceListFragmentListener).onCategoryList(categoryList);
                    mOnPlaceListFragmentListener.onSearchCountUpdate(totalCount, maxCount);

                    Observable.just(totalCount).subscribe(new Consumer<Integer>()
                    {
                        @Override
                        public void accept(@NonNull Integer integer) throws Exception
                        {
                            int soldOutCount = 0;
                            for (Stay stay : list)
                            {
                                if (stay.availableRooms == 0)
                                {
                                    soldOutCount++;
                                }
                            }

                            switch (mSearchType)
                            {
                                case AUTOCOMPLETE:
                                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.AUTO_SEARCH_RESULT//
                                        , ((StaySearchCuration) mStayCuration).getKeyword().name, integer.toString(), soldOutCount, null);
                                    break;

                                case LOCATION:
                                    //                                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NEARBY_SEARCH_RESULT//
                                    //                                        , ((StaySearchCuration) mStayCuration).getKeyword().name, integer.toString(), soldOutCount, null);
                                    break;

                                case RECENTLY_KEYWORD:
                                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.RECENT_SEARCH_RESULT//
                                        , ((StaySearchCuration) mStayCuration).getKeyword().name, integer.toString(), soldOutCount, null);
                                    break;

                                case SEARCHES:
                                    AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.KEYWORD_SEARCH_RESULT//
                                        , ((StaySearchCuration) mStayCuration).getKeyword().name, integer.toString(), soldOutCount, null);
                                    break;
                            }
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception
                        {

                        }
                    });
                }

                ((OnStaySearchResultListFragmentListener) mOnPlaceListFragmentListener).onStayListCount(totalCount);
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
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StaySearchResultListFragment.this.onError(call, e, onlyReport);
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
        public void onErrorResponse(Call call, Response response)
        {
            StaySearchResultListFragment.this.onErrorResponse(call, response);
        }
    };
}
