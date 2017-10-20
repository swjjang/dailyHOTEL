package com.twoheart.dailyhotel.place.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceListFragment extends BaseFragment implements Constants
{
    protected int mPlaceCount;
    protected int mLoadMorePageIndex;
    protected boolean mIsLoadMoreFlag = true; // 더보기 호출시 마지막 아이템이 성공이고 호텔 SaleList 또는 카운트가 0일때 false, refresh 시 true;

    protected ViewType mViewType;

    protected View mBottomOptionLayout; // 애니매이션 때문에 어쩔수 없음.

    protected BaseActivity mBaseActivity;

    protected PlaceListLayout mPlaceListLayout;

    protected BaseNetworkController mNetworkController;

    protected OnPlaceListFragmentListener mOnPlaceListFragmentListener;

    // onPlaceClick 부분이 있는데 이부분은 고메와 호텔은 서로 상속받아서 사용한다.
    public interface OnPlaceListFragmentListener
    {
        //        void onEventBannerClick(EventBanner eventBanner);

        // 왜 onActivityCreated 했을까?
        // http://blog.saltfactory.net/android/implement-layout-using-with-fragment.html
        void onActivityCreated(PlaceListFragment placeListFragment);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onShowMenuBar();

        void onBottomOptionVisible(boolean visible);

        void onUpdateFilterEnabled(boolean isShowFilterEnabled);

        void onUpdateViewTypeEnabled(boolean isShowViewTypeEnabled);

        void onFilterClick();

        void onShowActivityEmptyView(boolean isShow);

        void onRecordAnalytics(Constants.ViewType viewType);

        void onSearchCountUpdate(int searchCount, int searchMaxCount);
    }

    protected abstract int getLayoutResourceId();

    protected abstract BaseNetworkController getNetworkController();

    protected abstract void refreshList(boolean isShowProgress, int page);

    protected abstract void onChangedWish(int position);

    public abstract PlaceListLayout getPlaceListLayout();

    public abstract void setPlaceCuration(PlaceCuration curation);

    public abstract boolean isDefaultFilter();

    public void setPlaceOnListFragmentListener(OnPlaceListFragmentListener listener)
    {
        mOnPlaceListFragmentListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();
        mViewType = ViewType.LIST;
        mLoadMorePageIndex = 1;
        mIsLoadMoreFlag = true;

        mPlaceListLayout = getPlaceListLayout();
        mPlaceListLayout.setBottomOptionLayout(mBottomOptionLayout);

        mNetworkController = getNetworkController();

        return mPlaceListLayout.onCreateView(getLayoutResourceId(), container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (mOnPlaceListFragmentListener != null)
        {
            mOnPlaceListFragmentListener.onActivityCreated(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    onChangedWish(data.getIntExtra(WishDialogActivity.INTENT_EXTRA_DATA_POSITION, -1));
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

    public void clearList()
    {
        mPlaceCount = 0;

        if (mPlaceListLayout != null)
        {
            mPlaceListLayout.clearList();
        }
    }

    public void refreshList(boolean isShowProgress)
    {
        if (mViewType == null)
        {
            return;
        }

        mIsLoadMoreFlag = true;

        switch (mViewType)
        {
            case LIST:
                int size = mPlaceListLayout.getItemCount();
                if (size == 0)
                {
                    refreshList(isShowProgress, 1);
                }
                break;

            case MAP:
                refreshList(isShowProgress, 0);
                break;

            default:
                break;
        }
    }

    public void addList(boolean isShowProgress)
    {
        if (mIsLoadMoreFlag == true)
        {
            refreshList(isShowProgress, mLoadMorePageIndex + 1);
        }
    }

    public void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
        mViewType = viewType;
        mPlaceListLayout.setVisibility(getChildFragmentManager(), viewType, isCurrentPage);

        mOnPlaceListFragmentListener.onShowMenuBar();
    }

    public void setScrollListTop()
    {
        if (mPlaceListLayout == null)
        {
            return;
        }

        mPlaceListLayout.setScrollListTop();
    }

    public boolean hasSalesPlace()
    {
        if (mPlaceListLayout == null)
        {
            return false;
        }

        return mPlaceListLayout.hasSalesPlace();
    }

    public void setBottomOptionLayout(View view)
    {
        mBottomOptionLayout = view;
    }

    public ViewType getViewType()
    {
        return this.mViewType;
    }

    public void setViewType(ViewType viewType)
    {
        this.mViewType = viewType;
    }

    public int getPlaceCount()
    {
        return mPlaceCount;
    }

    protected ArrayList<PlaceViewItem> makePlaceList(List<? extends Place> placeList, SortType sortType, boolean hasSection)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();

        if (placeList == null || placeList.size() == 0)
        {
            return placeViewItemList;
        }

        String previousRegion = null;
        boolean hasDailyChoice = false;

        int entryPosition = 1;

        if (mPlaceListLayout != null)
        {
            ArrayList<PlaceViewItem> oldList = new ArrayList<>(mPlaceListLayout.getList());

            int oldListSize = oldList.size();
            if (oldListSize > 0)
            {
                int start = oldList.size() - 1;
                int end = oldListSize - 5;
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

        if (hasSection == true)
        {
            for (Place place : placeList)
            {
                // 지역순에만 section 존재함
                if (SortType.DEFAULT == sortType)
                {
                    String region = place.districtName;

                    if (DailyTextUtils.isTextEmpty(region) == true)
                    {
                        continue;
                    }

                    if (place.isDailyChoice == true)
                    {
                        if (hasDailyChoice == false)
                        {
                            hasDailyChoice = true;

                            PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, mBaseActivity.getResources().getString(R.string.label_dailychoice));
                            placeViewItemList.add(section);
                        }
                    } else
                    {
                        if (DailyTextUtils.isTextEmpty(previousRegion) == true || region.equalsIgnoreCase(previousRegion) == false)
                        {
                            previousRegion = region;

                            PlaceViewItem section = new PlaceViewItem(PlaceViewItem.TYPE_SECTION, region);
                            placeViewItemList.add(section);
                        }
                    }
                }

                place.entryPosition = entryPosition;
                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
                entryPosition++;
            }
        } else
        {
            for (Place place : placeList)
            {
                place.entryPosition = entryPosition;
                placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
                entryPosition++;
            }
        }

        return placeViewItemList;
    }
}
