package com.daily.dailyhotel.screen.stay.outbound.detail;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundDataBinding;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundDetailDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class StayOutboundDetailView extends BaseView<StayOutboundDetailView.OnEventListener, ActivityStayOutboundDetailDataBinding> implements StayOutboundDetailViewInterface, View.OnClickListener
{
    private DailyToolbarLayout mDailyToolbarLayout;


    public interface OnEventListener extends OnBaseEventListener
    {
        void onImageClick();

        void onReviewClick();

        void onCalendarClick();

        void onDownloadCouponClick();

        void onMapClick();

        void onClipAddressClick(String address);

        void onNavigatorClick();

        void onWishClick();

        void onConciergeClick();

        void onBookingClick();
    }

    public StayOutboundDetailView(BaseActivity baseActivity, StayOutboundDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityStayOutboundDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }


    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }


    @Override
    public void onClick(View v)
    {

    }

    @Override
    public void showRoomList()
    {

    }

    @Override
    public void hideRoomList()
    {

    }
}
