package com.daily.dailyhotel.screen.home.gourmet.detail.review;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityTrueReviewDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

public class TrueReviewView extends BaseDialogView<TrueReviewView.OnEventListener, ActivityTrueReviewDataBinding> implements TrueReviewInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onTermsClick();

        void onTopClick();

        void onScroll(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);
    }

    public TrueReviewView(BaseActivity baseActivity, TrueReviewView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityTrueReviewDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (recyclerView.isComputingLayout() == true)
                {
                    return;
                }

                getEventListener().onScroll(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (recyclerView.isComputingLayout() == true)
                {
                    return;
                }

                getEventListener().onScrollStateChanged(recyclerView, newState);
            }
        });

        viewDataBinding.topButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onTopClick();
            }
        });
    }

    @Override
    public void setToolbarTitle(String title)
    {

    }
}
