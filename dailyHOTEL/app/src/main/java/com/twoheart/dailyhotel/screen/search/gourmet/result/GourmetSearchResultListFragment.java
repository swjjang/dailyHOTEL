package com.twoheart.dailyhotel.screen.search.gourmet.result;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetSearchCuration;
import com.twoheart.dailyhotel.model.GourmetSearchParams;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListFragment;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GourmetSearchResultListFragment extends GourmetListFragment
{
    private boolean mIsDeepLink;
    private SearchType mSearchType;

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new GourmetSearchResultListNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.fragment_gourmet_search_result_list;
    }

    @Override
    protected PlaceListLayout getPlaceListLayout()
    {
        return new GourmetSearchResultListLayout(mBaseActivity, mEventListener);
    }

    public void setSearchType(SearchType searchType)
    {
        mSearchType = searchType;
    }

    @Override
    public void setPlaceCuration(PlaceCuration curation)
    {
        super.setPlaceCuration(curation);

        ((GourmetSearchResultListLayout) mPlaceListLayout).setSearchType(mSearchType);
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
                ((GourmetSearchResultListLayout) mPlaceListLayout).updateResultCount(mViewType, -1, -1);
            }
        }

        if (mGourmetCuration == null || mGourmetCuration.getCurationOption() == null//
            || mGourmetCuration.getCurationOption().getSortType() == null//
            || (mGourmetCuration.getCurationOption().getSortType() == SortType.DISTANCE && mGourmetCuration.getLocation() == null) //
            || (((GourmetSearchCuration) mGourmetCuration).getRadius() != 0d && mGourmetCuration.getLocation() == null))
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        GourmetSearchParams params = (GourmetSearchParams) mGourmetCuration.toPlaceParams(page, PAGENATION_LIST_SIZE, true);
        ((GourmetSearchResultListNetworkController) mNetworkController).requestGourmetSearchList(params);
    }

    @Override
    protected ArrayList<PlaceViewItem> makeSectionGourmetList(List<Gourmet> gourmetList, SortType sortType)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
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

        for (Gourmet gourmet : gourmetList)
        {
            gourmet.entryPosition = entryPosition;
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, gourmet));
            entryPosition++;
        }

        return placeViewItemList;
    }

    public void setIsDeepLink(boolean isDeepLink)
    {
        mIsDeepLink = isDeepLink;
    }

    private GourmetSearchResultListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new GourmetSearchResultListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetList(ArrayList<Gourmet> list, int page, int totalCount, int maxCount, HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap)
        {
            GourmetSearchResultListFragment.this.onGourmetList(list, page, totalCount, maxCount, categoryCodeMap, categorySequenceMap);

            if (mViewType == ViewType.MAP)
            {
                ((GourmetSearchResultListLayout) mPlaceListLayout).setMapMyLocation(mGourmetCuration.getLocation(), mIsDeepLink == false);
            }

            if (page <= 1)
            {
                ((GourmetSearchResultListLayout) mPlaceListLayout).updateResultCount(mViewType, totalCount, maxCount);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            GourmetSearchResultListFragment.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            GourmetSearchResultListFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            GourmetSearchResultListFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            GourmetSearchResultListFragment.this.onErrorToastMessage(message);
        }
    };
}
