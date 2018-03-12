package com.daily.dailyhotel.screen.home.campaigntag.gourmet;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.base.BaseBlurView;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.view.DailySearchToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityPlaceCampaignTagListDataBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 8. 4..
 */

public class GourmetCampaignTagListView //
    extends BaseBlurView<GourmetCampaignTagListView.OnEventListener, ActivityPlaceCampaignTagListDataBinding> //
    implements GourmetCampaignTagListInterface
{
    GourmetCampaignListAdapter mRecyclerAdapter;

    public GourmetCampaignTagListView(BaseActivity activity, OnEventListener listener)
    {
        super(activity, listener);
    }

    @Override
    protected void setContentView(ActivityPlaceCampaignTagListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initLayout(viewDataBinding);
    }

    private void initLayout(ActivityPlaceCampaignTagListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setTitleImageResource(R.drawable.search_ic_01_date);
        viewDataBinding.toolbarView.setOnToolbarListener(new DailySearchToolbarView.OnToolbarListener()
        {
            @Override
            public void onTitleClick()
            {
                getEventListener().onCalendarClick();
            }

            @Override
            public void onBackClick()
            {
                getEventListener().onBackClick();
            }

            @Override
            public void onSelectedRadiusPosition(int position)
            {

            }
        });

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        if (mRecyclerAdapter == null)
        {
            mRecyclerAdapter = new GourmetCampaignListAdapter(getContext(), new ArrayList<>(), mOnEventListener);
        }

        if (DailyPreference.getInstance(getContext()).getTrueVRSupport() > 0)
        {
            mRecyclerAdapter.setTrueVREnabled(true);
        }

        if (Util.supportPreview(getContext()) == true)
        {
            mRecyclerAdapter.setOnLongClickListener(mOnLongClickListener);
        }

        mRecyclerAdapter.setOnWishClickListener(mOnWishClickListener);

        viewDataBinding.recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public void setData(ArrayList<PlaceViewItem> placeViewItemList, GourmetBookDateTime gourmetBookDateTime)
    {
        if (mRecyclerAdapter == null)
        {
            return;
        }

        if (getViewDataBinding() != null)
        {
            int resultCount = 0;
            if (placeViewItemList != null && placeViewItemList.size() > 0)
            {
                for (PlaceViewItem placeViewItem : placeViewItemList)
                {
                    if (PlaceViewItem.TYPE_ENTRY == placeViewItem.mType)
                    {
                        resultCount++;
                    }
                }
            }

            setResultCount(resultCount);
        }

        mRecyclerAdapter.setAll(placeViewItemList);

        // 리스트를 최상단으로 다시 올린다.
        getViewDataBinding().recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void setCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setSubTitleText(text);
    }

    @Override
    public void setListScrollTop()
    {
        if (getViewDataBinding().recyclerView == null || getViewDataBinding().recyclerView.getChildCount() == 0)
        {
            return;
        }

        getViewDataBinding().recyclerView.scrollToPosition(0);
    }

    @Override
    public PlaceViewItem getItem(int position)
    {
        if (getViewDataBinding() == null || mRecyclerAdapter == null)
        {
            return null;
        }

        return mRecyclerAdapter.getItem(position);
    }

    @Override
    public void notifyWishChanged(int position, boolean wish)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                GourmetCampaignListAdapter.GourmetViewHolder gourmetViewHolder = (GourmetCampaignListAdapter.GourmetViewHolder) getViewDataBinding().recyclerView.findViewHolderForAdapterPosition(position);

                if (gourmetViewHolder != null)
                {
                    gourmetViewHolder.gourmetCardView.setWish(wish);
                }
            }
        });
    }

    private void setResultCount(int count)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (count > 0)
        {
            getViewDataBinding().resultCountTextView.setText(getString(R.string.label_searchresult_resultcount, count));
            getViewDataBinding().resultCountTextView.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().resultCountTextView.setVisibility(View.GONE);
        }
    }

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCalendarClick();

        void onResearchClick();

        void onCallClick();

        void onPlaceClick(int position, View view, PlaceViewItem placeViewItem, int count);

        void onPlaceLongClick(int position, View view, PlaceViewItem placeViewItem, int count);

        void onWishClick(int position, PlaceViewItem placeViewItem);
    }

    private GourmetCampaignListAdapter.OnEventListener mOnEventListener = new GourmetCampaignListAdapter.OnEventListener()
    {
        @Override
        public void onItemClick(View view)
        {
            if (getViewDataBinding() == null)
            {
                return;
            }

            int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mRecyclerAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                getEventListener().onPlaceClick(position, view, placeViewItem, mRecyclerAdapter.getItemCount());
            }
        }

        @Override
        public void onEmptyChangeDateClick()
        {
            getEventListener().onCalendarClick();
        }

        @Override
        public void onEmptyResearchClick()
        {
            getEventListener().onResearchClick();
        }

        @Override
        public void onEmptyCallClick()
        {
            getEventListener().onCallClick();
        }
    };

    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            if (getViewDataBinding() == null)
            {
                return false;
            }

            int position = getViewDataBinding().recyclerView.getChildAdapterPosition(v);
            if (position < 0)
            {
                return false;
            }

            PlaceViewItem placeViewItem = mRecyclerAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                getEventListener().onPlaceLongClick(position, v, placeViewItem, mRecyclerAdapter.getItemCount());
            }

            return true;
        }
    };

    protected View.OnClickListener mOnWishClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (getViewDataBinding() == null)
            {
                return;
            }

            int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mRecyclerAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                getEventListener().onWishClick(position, placeViewItem);
            }
        }
    };
}
