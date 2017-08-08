package com.daily.dailyhotel.screen.home.campaigntag.stay;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.view.DailyCampainTagTitleView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityPlaceCampaignTagListDataBinding;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by iseung-won on 2017. 8. 4..
 */

public class StayCampaignTagListView extends BaseDialogView<StayCampaignTagListView.OnEventListener, ActivityPlaceCampaignTagListDataBinding> implements StayCampaignTagListInterface
{
    private StayCampaignListAdapter mRecyclerAdapter;

    public StayCampaignTagListView(BaseActivity activity, OnEventListener listener)
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

       viewDataBinding.campaignTitleLayout.setOnEventListener(new DailyCampainTagTitleView.OnEventListener()
        {
            @Override
            public void onCalendarClick()
            {
                getEventListener().onCalendarClick();
            }

            @Override
            public void onBackClick()
            {
                getEventListener().onBackClick();
            }
        });

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        if (mRecyclerAdapter == null)
        {
            mRecyclerAdapter = new StayCampaignListAdapter(getContext(), new ArrayList<>(), mOnClickListener);
        }

        if (DailyPreference.getInstance(getContext()).getTrueVRSupport() > 0)
        {
            mRecyclerAdapter.setTrueVREnabled(true);
        }

        if (Util.supportPreview(getContext()) == true)
        {
            mRecyclerAdapter.setOnLongClickListener(mOnLongClickListener);
        }

        viewDataBinding.recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().campaignTitleLayout.setTitleText(title);
    }

//    @Override
//    public void setResultCount(int resultCount)
//    {
//        if (getViewDataBinding() == null)
//        {
//            return;
//        }
//
//        getViewDataBinding().campaignTitleLayout.setResultCount(resultCount);
//    }

    @Override
    public void setData(ArrayList<PlaceViewItem> placeViewItemList, StayBookingDay stayBookingDay)
    {
        if (mRecyclerAdapter == null)
        {
            return;
        }

        if (getViewDataBinding() != null)
        {
            int resultCount = 0;
            for (PlaceViewItem placeViewItem : placeViewItemList)
            {
                if (PlaceViewItem.TYPE_ENTRY == placeViewItem.mType)
                {
                    resultCount++;
                }
            }

            getViewDataBinding().campaignTitleLayout.setResultCount(resultCount);
        }

        mRecyclerAdapter.setPlaceBookingDay(stayBookingDay);
        mRecyclerAdapter.setAll(placeViewItemList);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void setCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().campaignTitleLayout.setCalendarText(text);
    }

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCalendarClick();

        void onPlaceClick(View view, PlaceViewItem placeViewItem, int count);

        void onPlaceLongClick(View view, PlaceViewItem placeViewItem, int count);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (getViewDataBinding() == null)
            {
                return;
            }

            int position = getViewDataBinding().recyclerView.getChildAdapterPosition(v);
            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mRecyclerAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                if (getEventListener() == null)
                {
                    return;
                }

                getEventListener().onPlaceClick(v, placeViewItem, mRecyclerAdapter.getItemCount());
            }
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
                if (getEventListener() == null)
                {
                    return false;
                }

                getEventListener().onPlaceLongClick(v, placeViewItem, mRecyclerAdapter.getItemCount());
            }

            return true;
        }
    };

}
