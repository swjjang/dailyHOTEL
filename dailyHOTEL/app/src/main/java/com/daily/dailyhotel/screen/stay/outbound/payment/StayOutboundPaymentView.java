package com.daily.dailyhotel.screen.stay.outbound.payment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.User;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPaymentDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentBookingDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentButtonDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentDiscountDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentPayDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentRefundDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

public class StayOutboundPaymentView extends BaseView<StayOutboundPaymentView.OnEventListener, ActivityStayOutboundPaymentDataBinding> implements StayOutboundPaymentInterface
{
    LayoutStayOutboundPaymentBookingDataBinding mBookingDataBinding;
    LayoutStayOutboundPaymentDiscountDataBinding mDiscountDataBinding;
    LayoutStayOutboundPaymentPayDataBinding mPayDataBinding;
    LayoutStayOutboundPaymentRefundDataBinding mRefundDataBinding;
    LayoutStayOutboundPaymentButtonDataBinding mButtonDataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayOutboundPaymentView(BaseActivity baseActivity, StayOutboundPaymentView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundPaymentDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollView, getColor(R.color.default_over_scroll_edge));

        setBookingLayout(getContext(), viewDataBinding.scrollLayout);
        setDiscountLayout(getContext(), viewDataBinding.scrollLayout);
        setPaymentLayout(getContext(), viewDataBinding.scrollLayout);
        setRefundLayout(getContext(), viewDataBinding.scrollLayout);
        setPayButtonLayout(getContext(), viewDataBinding.scrollLayout);
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setBooking(String checkInDate, String checkOutDate, int nights, String stayName, String roomType)
    {

    }

    @Override
    public void setUser(User user, String firstName, String lastName, String phone, String email)
    {

    }

    @Override
    public void setPeople(People people)
    {

    }

    @Override
    public void setStayOutboundPayment(int bonus, StayOutboundPayment stayOutboundPayment, int nights)
    {

    }

    @Override
    public void setSimpleCard(Card card)
    {

    }

    private void setBookingLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mBookingDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_booking_data, viewGroup, true);


    }

    private void setDiscountLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mDiscountDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_discount_data, viewGroup, true);


    }

    private void setPaymentLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mPayDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_pay_data, viewGroup, true);


    }

    private void setRefundLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mRefundDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_refund_data, viewGroup, true);


    }

    private void setPayButtonLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mButtonDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_button_data, viewGroup, true);


    }
}
