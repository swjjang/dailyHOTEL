package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundRefundDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundRefund01DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundRefund02DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundRefund03DataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;

public class StayOutboundRefundView extends BaseDialogView<StayOutboundRefundView.OnEventListener, ActivityStayOutboundRefundDataBinding> implements StayOutboundRefundInterface
{
    private LayoutStayOutboundRefund01DataBinding mRefund01DataBinding;
    private LayoutStayOutboundRefund02DataBinding mRefund02DataBinding;
    private LayoutStayOutboundRefund03DataBinding mRefund03DataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onRefundClick();
    }

    public StayOutboundRefundView(BaseActivity baseActivity, StayOutboundRefundView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundRefundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        mRefund01DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_refund_01_data, viewDataBinding.scrollLayout, true);

        mRefund02DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_refund_02_data, viewDataBinding.scrollLayout, true);

        mRefund03DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_refund_03_data, viewDataBinding.scrollLayout, true);
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setBookingDate(SpannableString checkInDate, SpannableString checkOutDate, int nights)
    {
        if (getViewDataBinding() == null || mRefund01DataBinding == null)
        {
            return;
        }

        mRefund01DataBinding.checkInDayTextView.setText(checkInDate);
        mRefund01DataBinding.checkOutDayTextView.setText(checkInDate);
        mRefund01DataBinding.nightsTextView.setText(getString(R.string.label_nights, nights));
    }

    @Override
    public void setRefundDetail(StayOutboundRefundDetail stayOutboundRefundDetail)
    {
        setBookingInformation(getContext(), mRefund01DataBinding, stayOutboundRefundDetail);

        setPaymentInformation(getContext(), mRefund02DataBinding, stayOutboundRefundDetail);
    }

    private void setBookingInformation(Context context, LayoutStayOutboundRefund01DataBinding dataBinding, StayOutboundRefundDetail stayOutboundRefundDetail)
    {
        if (context == null || dataBinding == null || stayOutboundRefundDetail == null)
        {
            return;
        }

        dataBinding.hotelNameTextView.setText(stayOutboundRefundDetail.name);
        dataBinding.roomTypeTextView.setText(stayOutboundRefundDetail.roomName);
        dataBinding.addressTextView.setText(stayOutboundRefundDetail.address);
    }

    private void setPaymentInformation(Context context, LayoutStayOutboundRefund02DataBinding dataBinding, StayOutboundRefundDetail stayOutboundRefundDetail)
    {
        if (context == null || dataBinding == null || stayOutboundRefundDetail == null)
        {
            return;
        }

        try
        {
            dataBinding.paymentDateTextView.setText(DailyCalendar.convertDateFormatString(stayOutboundRefundDetail.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        dataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(context, stayOutboundRefundDetail.totalPrice, false));


        if (stayOutboundRefundDetail.bonus > 0)
        {
            dataBinding.bonusLayout.setVisibility(View.VISIBLE);
            dataBinding.bonusTextView.setText("- " + DailyTextUtils.getPriceFormat(context, stayOutboundRefundDetail.bonus, false));
        } else
        {
            dataBinding.bonusLayout.setVisibility(View.GONE);
        }

        dataBinding.totalPriceTextView.setText(DailyTextUtils.getPriceFormat(context, stayOutboundRefundDetail.paymentPrice, false));
    }
}
