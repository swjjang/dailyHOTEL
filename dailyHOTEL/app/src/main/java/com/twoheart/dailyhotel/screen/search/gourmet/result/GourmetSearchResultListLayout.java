package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetListMapFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GourmetSearchResultListLayout extends PlaceListLayout
{
    private GourmetListMapFragment mGourmetListMapFragment;

    private GourmetCuration mGourmetCuration;

    public GourmetSearchResultListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected PlaceListAdapter getPlacetListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new GourmetSearchResultListAdapter(mContext, new ArrayList<PlaceViewItem>(), mOnItemClickListener);
    }

    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage)
    {
        boolean isShowActivityEmptyView = false;

        switch (viewType)
        {
            case LIST:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);

                if (mGourmetListMapFragment != null)
                {
                    mGourmetListMapFragment.resetMenuBarLayoutranslation();
                    fragmentManager.beginTransaction().remove(mGourmetListMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mGourmetListMapFragment = null;
                }

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);

                if (isCurrentPage == true && mGourmetListMapFragment == null)
                {
                    mGourmetListMapFragment = new GourmetListMapFragment();
                    mGourmetListMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                    fragmentManager.beginTransaction().add(mMapLayout.getId(), mGourmetListMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                GourmetCurationOption GourmetCurationOption = mGourmetCuration == null //
                    ? new GourmetCurationOption() //
                    : (GourmetCurationOption) mGourmetCuration.getCurationOption();

                if (GourmetCurationOption.isDefaultFilter() == true)
                {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mFilterEmptyView.setVisibility(View.GONE);

                    isShowActivityEmptyView = true;
                } else
                {
                    mEmptyView.setVisibility(View.GONE);
                    mFilterEmptyView.setVisibility(View.VISIBLE);
                }

                mMapLayout.setVisibility(View.GONE);

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;
        }

        ((OnEventListener) mOnEventListener).onShowActivityEmptyView(isShowActivityEmptyView);
    }

    public boolean isShowInformationAtMapView(Constants.ViewType viewType)
    {
        if (viewType == Constants.ViewType.MAP && mGourmetListMapFragment != null)
        {
            return mGourmetListMapFragment.isShowInformation();
        }

        return false;
    }

    @Override
    public PlaceListMapFragment getListMapFragment()
    {
        return mGourmetListMapFragment;
    }

    public List<PlaceViewItem> getList()
    {
        if (mPlaceListAdapter == null)
        {
            return null;
        }

        return mPlaceListAdapter.getAll();
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

            int oldListSize = oldList.size();
            if (oldListSize > 0)
            {
                PlaceViewItem placeViewItem = oldList.get(oldListSize - 1);

                // 기존 리스트가 존재 할 때 마지막 아이템이 footer 일 경우 아이템 제거
                switch (placeViewItem.mType)
                {
                    case PlaceViewItem.TYPE_FOOTER_VIEW:
                    case PlaceViewItem.TYPE_LOADING_VIEW:
                        getList().remove(placeViewItem); // 실제 삭제
                        oldList.remove(placeViewItem); // 비교 리스트 삭제
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

    @Override
    public void setList(FragmentManager fragmentManager, Constants.ViewType viewType, ArrayList<PlaceViewItem> list, Constants.SortType sortType)
    {
        mIsLoading = false;

        if (mPlaceListAdapter == null)
        {
            Util.restartApp(mContext);
            return;
        }

        // 지도의 경우 무조건 전체 데이터를 가져옴으로 clear 후 진행되야 함
        if (viewType == Constants.ViewType.MAP)
        {
            clearList();

            if (list == null || list.size() == 0)
            {
                mPlaceListAdapter.notifyDataSetChanged();
                setVisibility(fragmentManager, Constants.ViewType.GONE, true);

            } else
            {
                setVisibility(fragmentManager, viewType, true);

                mGourmetListMapFragment.setOnPlaceListMapFragment(new PlaceListMapFragment.OnPlaceListMapFragmentListener()
                {
                    @Override
                    public void onInformationClick(PlaceViewItem placeViewItem)
                    {
                        ((OnEventListener) mOnEventListener).onPlaceClick(placeViewItem);
                    }
                });

                mGourmetListMapFragment.setPlaceViewItemList(list, true);
            }
        } else
        {
        }
    }

    public boolean hasSalesPlace()
    {
        return hasSalesPlace(mPlaceListAdapter.getAll());
    }

    protected boolean hasSalesPlace(List<PlaceViewItem> list)
    {
        if (list == null || list.size() == 0)
        {
            return false;
        }

        boolean hasPlace = false;

        for (PlaceViewItem placeViewItem : list)
        {
            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY//
                && placeViewItem.<Stay>getItem().isSoldOut == false)
            {
                hasPlace = true;
                break;
            }
        }

        return hasPlace;
    }

    public int getMapItemSize()
    {
        return mGourmetListMapFragment != null ? mGourmetListMapFragment.getPlaceViewItemListSize() : 0;
    }

    public void setGourmetCuration(GourmetCuration gourmetCuration)
    {
        mGourmetCuration = gourmetCuration;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Listener
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = mPlaceRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(null);
                return;
            }

            PlaceViewItem placeViewItem = mPlaceListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(placeViewItem);
            }
        }
    };
}
