package com.daily.dailyhotel.screen.mydaily.reward.history.reward;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.RewardHistory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityRewardHistoryDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;

public class RewardHistoryView extends BaseDialogView<RewardHistoryView.OnEventListener, ActivityRewardHistoryDataBinding> implements RewardHistoryInterface
{
    RewardHistoryAdapter mRewardHistoryAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onViewReservationClick(RewardHistory rewardHistory);

        void onHomeClick();
    }

    public RewardHistoryView(BaseActivity baseActivity, RewardHistoryView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityRewardHistoryDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.homeImageView.setOnClickListener(v -> getEventListener().onHomeClick());
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
    public void setStickerValidityText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().validityTextView.setText(text);
    }

    @Override
    public void setRewardHistoryList(List<ObjectItem> list)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mRewardHistoryAdapter == null)
        {
            mRewardHistoryAdapter = new RewardHistoryAdapter(getContext());
            mRewardHistoryAdapter.setOnClickListener(new RewardHistoryAdapter.OnEventListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getViewDataBinding().recyclerView.getChildAdapterPosition(view);

                    if (position < 0)
                    {
                        return;
                    }

                    ObjectItem objectItem = mRewardHistoryAdapter.getItem(position);

                    if (objectItem != null && objectItem.mType == ObjectItem.TYPE_ENTRY)
                    {
                        getEventListener().onViewReservationClick(objectItem.getItem());
                    }
                }
            });
        }

        mRewardHistoryAdapter.setAll(list);
        getViewDataBinding().recyclerView.setAdapter(mRewardHistoryAdapter);
    }

    private void initToolbar(ActivityRewardHistoryDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
