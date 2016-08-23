package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayCuration;
import com.twoheart.dailyhotel.model.StayCurationOption;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;

public class StayListLayout extends PlaceListLayout
{
    protected StayListMapFragment mStayListMapFragment;
    protected StayCuration mStayCuration;

    public StayListLayout(Context context, OnEventListener eventListener)
    {
        super(context, eventListener);
    }

    @Override
    protected PlaceListAdapter getPlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        return new StayListAdapter(context, arrayList, mOnItemClickListener, mOnEventBannerItemClickListener);
    }

    public void setVisibility(FragmentManager fragmentManager, Constants.ViewType viewType, boolean isCurrentPage)
    {
        switch (viewType)
        {
            case LIST:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.GONE);
                mFilterEmptyView.setVisibility(View.GONE);

                if (mStayListMapFragment != null)
                {
                    mStayListMapFragment.resetMenuBarLayoutranslation();
                    fragmentManager.beginTransaction().remove(mStayListMapFragment).commitAllowingStateLoss();
                    mMapLayout.removeAllViews();
                    mStayListMapFragment = null;
                }

                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                break;

            case MAP:
                mEmptyView.setVisibility(View.GONE);
                mMapLayout.setVisibility(View.VISIBLE);
                mFilterEmptyView.setVisibility(View.GONE);

                if (isCurrentPage == true && mStayListMapFragment == null)
                {
                    mStayListMapFragment = new StayListMapFragment();
                    mStayListMapFragment.setBottomOptionLayout(mBottomOptionLayout);
                    fragmentManager.beginTransaction().add(mMapLayout.getId(), mStayListMapFragment).commitAllowingStateLoss();
                }

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                break;

            case GONE:
                StayCurationOption stayCurationOption = mStayCuration == null //
                    ? new StayCurationOption() //
                    : (StayCurationOption) mStayCuration.getCurationOption();

                if (stayCurationOption.isDefaultFilter() == true)
                {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mFilterEmptyView.setVisibility(View.GONE);
                } else
                {
                    mEmptyView.setVisibility(View.GONE);
                    mFilterEmptyView.setVisibility(View.VISIBLE);
                }

                mMapLayout.setVisibility(View.GONE);

                mSwipeRefreshLayout.setVisibility(View.INVISIBLE);

                AnalyticsManager.getInstance(mContext).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_EMPTY);
                break;
        }
    }

    public boolean isShowInformationAtMapView(Constants.ViewType viewType)
    {
        if (viewType == Constants.ViewType.MAP && mStayListMapFragment != null)
        {
            return mStayListMapFragment.isShowInformation();
        }

        return false;
    }

    @Override
    public PlaceListMapFragment getListMapFragment()
    {
        return mStayListMapFragment;
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

            // 리스트의 경우 Pagination 상황 고려
            List<PlaceViewItem> oldList = getList();

            int oldListSize = oldList == null ? 0 : oldList.size();
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

            String districtName = null;

            // 지역순(StayList의 경우 기본이 지역순) 일때 상위 섹션명을 가지고 가기위한 처리
            if (Constants.SortType.DEFAULT == sortType)
            {
                // 삭제 이벤트가 발생하였을수 있어서 재 검사
                int start = oldList == null ? 0 : oldList.size() - 1;
                int end = oldList == null ? 0 : oldList.size() - 5;
                end = end < 0 ? 0 : end;

                // 5번안에 검사 안끝나면 그냥 종료, 원래는 1번에 검사되어야 함
                for (int i = start; i >= end; i--)
                {
                    PlaceViewItem item = oldList.get(i);
                    if (item.mType == PlaceViewItem.TYPE_ENTRY)
                    {
                        Stay stay = item.getItem();
                        districtName = stay.districtName;
                        break;
                    } else if (item.mType == PlaceViewItem.TYPE_SECTION)
                    {
                        districtName = item.getItem();
                        break;
                    }
                }
            }

            if (list != null && list.size() > 0)
            {
                if (Util.isTextEmpty(districtName) == false)
                {
                    PlaceViewItem firstItem = list.get(0);
                    if (firstItem.mType == PlaceViewItem.TYPE_SECTION)
                    {
                        String firstDistrictName = firstItem.getItem();
                        if (districtName.equalsIgnoreCase(firstDistrictName))
                        {
                            list.remove(0);
                        }
                    }
                }

                mPlaceListAdapter.setSortType(sortType);
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
                if (oldListSize > 0)
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
                // 배너의 경우 리스트 타입이면서, 기존 데이터가 0일때 즉 첫 페이지일때, sortType은 default type 이면서 배너가 있을때만 최상단에 위치한다.
                if (oldListSize == 0)
                {
                    ((OnEventListener) mOnEventListener).onRecordAnalytics(viewType);

                    if (sortType == Constants.SortType.DEFAULT && isBannerVisibility() == true)
                    {
                        if (StayEventBannerManager.getInstance().getCount() > 0)
                        {
                            PlaceViewItem placeViewItem = new PlaceViewItem(PlaceViewItem.TYPE_EVENT_BANNER, //
                                StayEventBannerManager.getInstance().getList());
                            mPlaceListAdapter.add(0, placeViewItem);
                        }
                    }
                }

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

                mStayListMapFragment.setOnPlaceListMapFragment(new PlaceListMapFragment.OnPlaceListMapFragmentListener()
                {
                    @Override
                    public void onInformationClick(PlaceViewItem placeViewItem)
                    {
                        ((OnEventListener) mOnEventListener).onPlaceClick(placeViewItem);

                        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.HOTEL_MAP_DETAIL_VIEW_CLICKED, placeViewItem.<Place>getItem().name, null);
                    }
                });

                mStayListMapFragment.setPlaceViewItemList(list, true);

                ((OnEventListener) mOnEventListener).onRecordAnalytics(viewType);
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
        return mStayListMapFragment != null ? mStayListMapFragment.getPlaceViewItemListSize() : 0;
    }

    public void setStayCuration(StayCuration curation)
    {
        mStayCuration = curation;
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

    private View.OnClickListener mOnEventBannerItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Integer index = (Integer) view.getTag(view.getId());
            if (index != null)
            {
                EventBanner eventBanner = StayEventBannerManager.getInstance().getEventBanner(index);

                ((OnEventListener) mOnEventListener).onEventBannerClick(eventBanner);
            }
        }
    };
}
