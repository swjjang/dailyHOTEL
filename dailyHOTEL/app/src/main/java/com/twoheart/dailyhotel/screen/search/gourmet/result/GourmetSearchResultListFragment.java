package com.twoheart.dailyhotel.screen.search.gourmet.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceListLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GourmetSearchResultListFragment extends PlaceListFragment
{
    private BaseActivity mBaseActivity;
    private PinnedSectionRecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsLoading;

    private GourmetSearchResultListAdapter mListAdapter;

    private GourmetCuration mPlaceCuration;

    public interface OnGourmetSearchResultListFragmentListener extends OnPlaceListFragmentListener
    {
        void onRefreshAll(boolean isShowProgress);

        void onItemClick(PlaceViewItem placeViewItem);

        void onLoadMoreList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_gourmet_search_result_list, container, false);

        initLayout(view);

        return view;
    }

    private void initLayout(View view)
    {
        mRecyclerView = (PinnedSectionRecyclerView) view.findViewById(R.id.recycleView);

        mLayoutManager = new LinearLayoutManager(mBaseActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, getResources().getColor(R.color.default_over_scroll_edge));

        mListAdapter = new GourmetSearchResultListAdapter(mBaseActivity, new ArrayList<PlaceViewItem>(), mOnItemClickListener);
        mRecyclerView.setAdapter(mListAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                ((OnGourmetSearchResultListFragmentListener) mOnPlaceListFragmentListener).onRefreshAll(false);
            }
        });

        // edgeglow을 보이게 하기 위해서
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                // SwipeRefreshLayout
                if (dy <= 0)
                {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItem == 0)
                    {
                        mSwipeRefreshLayout.setEnabled(true);
                    } else
                    {
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                } else
                {
                    if (mIsLoading == false)
                    {
                        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                        int itemCount = mLayoutManager.getItemCount();
                        if (itemCount > 0)
                        {
                            if ((itemCount - 1) <= (lastVisibleItemPosition + PlaceListLayout.LOAD_MORE_POSITION_GAP))
                            {
                                mIsLoading = true;
                                ((OnGourmetSearchResultListFragmentListener) mOnPlaceListFragmentListener).onLoadMoreList();
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
            }
        });

        mRecyclerView.setShadowVisible(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    }

    public void setSortType(Constants.SortType sortType)
    {
        if (mListAdapter == null)
        {
            return;
        }

        mListAdapter.setSortType(sortType);
    }

    @Override
    public void setPlaceCuration(PlaceCuration curation)
    {
        mPlaceCuration = (GourmetCuration) curation;
    }

    @Override
    public void clearList()
    {
        if (mListAdapter != null)
        {
            mListAdapter.clear();
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void refreshList(boolean isShowProgress)
    {
    }

    public void addSearchResultList(ArrayList<PlaceViewItem> placeViewItemList)
    {
        if (placeViewItemList == null)
        {
            return;
        }

        // 리스트의 경우 Pagenation 상황 고려
        List<PlaceViewItem> oldList = mListAdapter.getAll();

        if (oldList != null)
        {
            int size = oldList.size();

            if (size > 0)
            {
                PlaceViewItem placeViewItem = oldList.get(size - 1);

                // 기존 리스트가 존재 할 때 마지막 아이템이 footer 일 경우 아이템 제거
                switch (placeViewItem.mType)
                {
                    case PlaceViewItem.TYPE_FOOTER_VIEW:
                    case PlaceViewItem.TYPE_LOADING_VIEW:
                        oldList.remove(placeViewItem); // 실제 삭제
                        break;
                }
            }
        }

        if (placeViewItemList != null && placeViewItemList.size() > 0)
        {
            mListAdapter.addAll(placeViewItemList);

            if (placeViewItemList.size() < PAGENATION_LIST_SIZE)
            {
                mListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, true));
            } else
            {
                mListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_LOADING_VIEW, null));
            }
        } else
        {
            // 요청 온 데이터가 empty 일때 기존 리스트가 있으면 라스트 footer 재 생성
            if (oldList.size() > 0)
            {
                mListAdapter.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, true));
            }
        }

        mIsLoading = false;
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setVisibility(ViewType viewType, boolean isCurrentPage)
    {
    }

    @Override
    public void setScrollListTop()
    {
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int position = mRecyclerView.getChildAdapterPosition(v);

            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListAdapter.getItem(position);

            if (placeViewItem.mType != PlaceViewItem.TYPE_ENTRY)
            {
                return;
            }

            ((OnGourmetSearchResultListFragmentListener) mOnPlaceListFragmentListener).onItemClick(placeViewItem);
        }
    };
}
