package com.twoheart.dailyhotel.screen.gourmet.list;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceCurationOption;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetListFragment extends PlaceListFragment
{
    protected GourmetCuration mGourmetCuration;
    private GourmetListLayout mGourmetListLayout;

    public interface OnGourmetListFragmentListener extends OnPlaceListFragmentListener
    {
        void onGourmetClick(View view, PlaceViewItem placeViewItem, int listCount);

        void onGourmetLongClick(View view, PlaceViewItem placeViewItem, int listCount);

        void onGourmetCategoryFilter(int page, HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap);

        void onRegionClick();

        void onCalendarClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case BaseActivity.RESULT_CODE_ERROR:
                        if (data != null)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false));
                        }
                        break;
                }
                break;

            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            {
                if (resultCode == com.daily.base.BaseActivity.RESULT_CODE_REFRESH && data != null)
                {
                    if (data.hasExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                    {
                        onChangedWish(mWishPosition, data.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH, false));
                    }
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_PREVIEW:
                if (resultCode == Constants.CODE_RESULT_ACTIVITY_REFRESH)
                {
                    if (data != null && data.hasExtra(GourmetPreviewActivity.INTENT_EXTRA_DATA_WISH) == true)
                    {
                        onChangedWish(mWishPosition, data.getBooleanExtra(GourmetPreviewActivity.INTENT_EXTRA_DATA_WISH, false));
                    }
                }
                break;

            default:
                if (mViewType == ViewType.MAP)
                {
                    PlaceListMapFragment placeListMapFragment = mPlaceListLayout.getListMapFragment();

                    if (placeListMapFragment != null)
                    {
                        placeListMapFragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;
        }
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new GourmetListNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);
    }

    @Override
    public PlaceListLayout getPlaceListLayout()
    {
        if (mGourmetListLayout == null)
        {
            mGourmetListLayout = new GourmetListLayout(mBaseActivity, mEventListener);
        }

        return mGourmetListLayout;
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.fragment_gourmet_list;
    }

    @Override
    public void setPlaceCuration(PlaceCuration curation)
    {
        if (mPlaceListLayout == null)
        {
            return;
        }

        mGourmetCuration = (GourmetCuration) curation;
        ((GourmetListLayout) mPlaceListLayout).setGourmetCuration(mGourmetCuration);
    }

    @Override
    protected void refreshList(boolean isShowProgress, int page)
    {
        // 더보기 시 unlock 걸지않음
        if (page <= 1)
        {
            lockUI(isShowProgress);
        }

        GourmetBookingDay gourmetBookingDay = mGourmetCuration.getGourmetBookingDay();
        Province province = mGourmetCuration.getProvince();

        if (province == null || gourmetBookingDay == null)
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        if (mGourmetCuration == null || mGourmetCuration.getCurationOption() == null//
            || mGourmetCuration.getCurationOption().getSortType() == null//
            || (mGourmetCuration.getCurationOption().getSortType() == SortType.DISTANCE && mGourmetCuration.getLocation() == null))
        {
            unLockUI();
            Util.restartApp(mBaseActivity);
            return;
        }

        GourmetParams params = (GourmetParams) mGourmetCuration.toPlaceParams(page, PAGENATION_LIST_SIZE, true);
        ((GourmetListNetworkController) mNetworkController).requestGourmetList(params);
    }

    @Override
    protected void onChangedWish(int position, boolean wish)
    {
        if (position < 0)
        {
            return;
        }

        PlaceViewItem placeViewItem = mPlaceListLayout.getItem(position);

        if (placeViewItem == null)
        {
            return;
        }

        Gourmet gourmet = placeViewItem.getItem();

        if (gourmet.myWish != wish)
        {
            gourmet.myWish = wish;
            mPlaceListLayout.notifyWishChanged(position, wish);
        }
    }

    protected void onGourmetList(List<Gourmet> list, int page, int totalCount, int maxCount, //
                                 HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap, boolean hasSection)
    {
        if (isFinishing() == true)
        {
            unLockUI();
            return;
        }

        // 페이지가 전체데이터 이거나 첫페이지 이면 스크롤 탑
        if (page <= 1)
        {
            mPlaceCount = 0;
            mPlaceListLayout.clearList();

            if (mGourmetCuration.getCurationOption().isDefaultFilter() == true)
            {
                ((OnGourmetListFragmentListener) mOnPlaceListFragmentListener).onGourmetCategoryFilter(page, categoryCodeMap, categorySequenceMap);
            }
        }

        int listSize = list == null ? 0 : list.size();
        if (listSize > 0)
        {
            mLoadMorePageIndex = page;
            mIsLoadMoreFlag = true;
        } else
        {
            mIsLoadMoreFlag = false;
        }

        mPlaceCount += listSize;

        SortType sortType = mGourmetCuration.getCurationOption().getSortType();

        ArrayList<PlaceViewItem> placeViewItems = makePlaceList(list, sortType, hasSection);

        switch (mViewType)
        {
            case LIST:
            {
                mPlaceListLayout.addResultList(getChildFragmentManager(), mViewType, placeViewItems, sortType, mGourmetCuration.getGourmetBookingDay());

                int size = mPlaceListLayout.getItemCount();

                if (size == 0)
                {
                    setVisibility(ViewType.GONE, true);
                }

                mEventListener.onShowActivityEmptyView(size == 0);
                break;
            }

            case MAP:
            {
                mPlaceListLayout.setList(getChildFragmentManager(), mViewType, placeViewItems, sortType, mGourmetCuration.getGourmetBookingDay());

                int mapSize = mPlaceListLayout.getMapItemSize();
                if (mapSize == 0)
                {
                    setVisibility(ViewType.GONE, true);
                }

                mEventListener.onShowActivityEmptyView(mapSize == 0);
                break;
            }

            default:
                break;
        }

        unLockUI();
        mPlaceListLayout.setSwipeRefreshing(false);
    }

    @Override
    public boolean isDefaultFilter()
    {
        if (mGourmetCuration == null)
        {
            return true;
        }

        PlaceCurationOption placeCurationOption = mGourmetCuration.getCurationOption();
        if (placeCurationOption == null)
        {
            return true;
        }

        return placeCurationOption.isDefaultFilter();
    }

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////   Listener   //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    protected GourmetListLayout.OnEventListener mEventListener = new GourmetListLayout.OnEventListener()
    {
        @Override
        public void onPlaceClick(int position, View view, PlaceViewItem placeViewItem)
        {
            mWishPosition = position;

            ((OnGourmetListFragmentListener) mOnPlaceListFragmentListener).onGourmetClick(view, placeViewItem, getPlaceCount());
        }

        @Override
        public void onPlaceLongClick(int position, View view, PlaceViewItem placeViewItem)
        {
            mWishPosition = position;

            ((OnGourmetListFragmentListener) mOnPlaceListFragmentListener).onGourmetLongClick(view, placeViewItem, getPlaceCount());
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            mOnPlaceListFragmentListener.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            mOnPlaceListFragmentListener.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onRefreshAll(boolean isShowProgress)
        {
            refreshList(isShowProgress, 1);

            mOnPlaceListFragmentListener.onShowMenuBar();
        }

        @Override
        public void onLoadMoreList()
        {
            addList(false);
        }

        @Override
        public void onFilterClick()
        {
            mOnPlaceListFragmentListener.onFilterClick();
        }

        @Override
        public void onUpdateFilterEnabled(boolean isEnabled)
        {
            mOnPlaceListFragmentListener.onUpdateFilterEnabled(isEnabled);
        }

        @Override
        public void onBottomOptionVisible(boolean visible)
        {
            mOnPlaceListFragmentListener.onBottomOptionVisible(visible);
        }

        @Override
        public void onUpdateViewTypeEnabled(boolean isEnabled)
        {
            mOnPlaceListFragmentListener.onUpdateViewTypeEnabled(isEnabled);
        }

        @Override
        public void onShowActivityEmptyView(boolean isShow)
        {
            mOnPlaceListFragmentListener.onShowActivityEmptyView(isShow);
        }

        @Override
        public void onRecordAnalytics(ViewType viewType)
        {
            mOnPlaceListFragmentListener.onRecordAnalytics(viewType);
        }

        @Override
        public void onShowCallDialog()
        {
            startActivity(CallDialogActivity.newInstance(getActivity()));
        }

        @Override
        public void onRegionClick()
        {
            ((OnGourmetListFragmentListener) mOnPlaceListFragmentListener).onRegionClick();
        }

        @Override
        public void onCalendarClick()
        {
            ((OnGourmetListFragmentListener) mOnPlaceListFragmentListener).onCalendarClick();
        }

        @Override
        public void onWishClick(int position, PlaceViewItem placeViewItem)
        {
            if (placeViewItem == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Gourmet gourmet = placeViewItem.getItem();

            mWishPosition = position;

            if (DailyHotel.isLogin() == true)
            {
                mPlaceListLayout.notifyWishChanged(position, !gourmet.myWish);
            }

            mBaseActivity.startActivityForResult(WishDialogActivity.newInstance(mBaseActivity, ServiceType.GOURMET//
                , gourmet.index, !gourmet.myWish, position, AnalyticsManager.Screen.DAILYGOURMET_LIST), Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG);

            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.WISH_GOURMET, !gourmet.myWish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
        }

        @Override
        public void finish()
        {
            if (mBaseActivity != null)
            {
                mBaseActivity.finish();
            }
        }
    };

    private GourmetListNetworkController.OnNetworkControllerListener mNetworkControllerListener = new GourmetListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetList(ArrayList<Gourmet> list, int page, int totalCount, int maxCount, HashMap<String, Integer> categoryCodeMap, HashMap<String, Integer> categorySequenceMap)
        {
            GourmetListFragment.this.onGourmetList(list, page, totalCount, maxCount, categoryCodeMap, categorySequenceMap, true);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            if (mPlaceListLayout.isRefreshing() == true)
            {
                mPlaceListLayout.setSwipeRefreshing(false);
            }

            GourmetListFragment.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            if (mPlaceListLayout.isRefreshing() == true)
            {
                mPlaceListLayout.setSwipeRefreshing(false);
            }

            GourmetListFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            if (mPlaceListLayout.isRefreshing() == true)
            {
                mPlaceListLayout.setSwipeRefreshing(false);
            }

            GourmetListFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            if (mPlaceListLayout.isRefreshing() == true)
            {
                mPlaceListLayout.setSwipeRefreshing(false);
            }

            GourmetListFragment.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            if (mPlaceListLayout.isRefreshing() == true)
            {
                mPlaceListLayout.setSwipeRefreshing(false);
            }

            GourmetListFragment.this.onErrorResponse(call, response);
        }
    };
}
