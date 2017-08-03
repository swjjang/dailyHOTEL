package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingPaymentTypeDataBinding;

import java.util.ArrayList;

public class DailyBookingPaymentTypeView extends ConstraintLayout implements View.OnClickListener
{
    private DailyViewBookingPaymentTypeDataBinding mViewDataBinding;

    private PaymentType mPaymentType;

    private OnPaymentTypeClickListener mOnPaymentTypeClickListener;

    public enum PaymentType
    {
        EASY_CARD,
        CARD,
        PHONE,
        VBANK,
        FREE,
    }

    public interface OnPaymentTypeClickListener
    {
        void onEasyCardManagerClick();

        void onRegisterEasyCardClick();

        void onPaymentTypeClick(PaymentType paymentType);
    }

    public DailyBookingPaymentTypeView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyBookingPaymentTypeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyBookingPaymentTypeView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_booking_payment_type_data, this, true);

        setEasyCard(null, null);

        mViewDataBinding.cardManagerLayout.setOnClickListener(this);
        mViewDataBinding.simpleCardHeaderLayout.setOnClickListener(this);
        mViewDataBinding.emptySimpleCardLayout.setOnClickListener(this);
        mViewDataBinding.selectedSimpleCardLayout.setOnClickListener(this);
        mViewDataBinding.cardLayout.setOnClickListener(this);
        mViewDataBinding.phoneLayout.setOnClickListener(this);
        mViewDataBinding.transferLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if (view == null)
        {
            return;
        }

        switch (view.getId())
        {
            case R.id.cardManagerLayout:
                if (mOnPaymentTypeClickListener != null)
                {
                    mOnPaymentTypeClickListener.onEasyCardManagerClick();
                }
                break;

            case R.id.emptySimpleCardLayout:
                if (mOnPaymentTypeClickListener != null)
                {
                    mOnPaymentTypeClickListener.onRegisterEasyCardClick();
                }
                break;

            case R.id.simpleCardHeaderLayout:
            case R.id.selectedSimpleCardLayout:
                if (mOnPaymentTypeClickListener != null)
                {
                    mOnPaymentTypeClickListener.onPaymentTypeClick(PaymentType.EASY_CARD);
                }
                break;

            case R.id.cardLayout:
                if (mOnPaymentTypeClickListener != null)
                {
                    mOnPaymentTypeClickListener.onPaymentTypeClick(PaymentType.CARD);
                }
                break;

            case R.id.phoneLayout:
                if (mOnPaymentTypeClickListener != null)
                {
                    mOnPaymentTypeClickListener.onPaymentTypeClick(PaymentType.PHONE);
                }
                break;

            case R.id.transferLayout:
                if (mOnPaymentTypeClickListener != null)
                {
                    mOnPaymentTypeClickListener.onPaymentTypeClick(PaymentType.VBANK);
                }
                break;
        }
    }

    public void setOnPaymentTypeClickListener(OnPaymentTypeClickListener listener)
    {
        mOnPaymentTypeClickListener = listener;
    }

    public void setPaymentType(PaymentType paymentType)
    {
        if (mViewDataBinding == null || paymentType == null)
        {
            return;
        }

        mPaymentType = paymentType;

        switch (paymentType)
        {
            case EASY_CARD:
            {
                mViewDataBinding.simpleCardLayout.setSelected(true);
                mViewDataBinding.cardLayout.setSelected(false);
                mViewDataBinding.phoneLayout.setSelected(false);
                mViewDataBinding.transferLayout.setSelected(false);
                break;
            }

            case CARD:
            {
                mViewDataBinding.simpleCardLayout.setSelected(false);
                mViewDataBinding.cardLayout.setSelected(true);
                mViewDataBinding.phoneLayout.setSelected(false);
                mViewDataBinding.transferLayout.setSelected(false);
                break;
            }

            case PHONE:
            {
                mViewDataBinding.simpleCardLayout.setSelected(false);
                mViewDataBinding.cardLayout.setSelected(false);
                mViewDataBinding.phoneLayout.setSelected(true);
                mViewDataBinding.transferLayout.setSelected(false);
                break;
            }

            case VBANK:
            {
                mViewDataBinding.simpleCardLayout.setSelected(false);
                mViewDataBinding.cardLayout.setSelected(false);
                mViewDataBinding.phoneLayout.setSelected(false);
                mViewDataBinding.transferLayout.setSelected(true);
                break;
            }

            case FREE:
            {
                mViewDataBinding.simpleCardLayout.setSelected(false);
                mViewDataBinding.cardLayout.setSelected(false);
                mViewDataBinding.phoneLayout.setSelected(false);
                mViewDataBinding.transferLayout.setSelected(false);

                setPaymentTypeVisible(PaymentType.FREE, true);
                break;
            }
        }
    }

    public void setPaymentTypeEnable(PaymentType paymentType, boolean enable)
    {
        if (mViewDataBinding == null || paymentType == null)
        {
            return;
        }

        switch (paymentType)
        {
            case EASY_CARD:
                setPaymentTypeEnabled(mViewDataBinding.disableSimpleCardView, enable);
                break;

            case CARD:
                setPaymentTypeEnabled(mViewDataBinding.disableCardView, enable);
                break;

            case PHONE:
                setPaymentTypeEnabled(mViewDataBinding.disablePhoneView, enable);
                break;

            case VBANK:
                setPaymentTypeEnabled(mViewDataBinding.disableTransferView, enable);
                break;

            case FREE:
                break;
        }
    }

    public void setPaymentTypeVisible(PaymentType paymentType, boolean visible)
    {
        if (mViewDataBinding == null || paymentType == null)
        {
            return;
        }

        switch (paymentType)
        {
            case EASY_CARD:
                mViewDataBinding.simpleCardLayout.setVisibility(visible ? VISIBLE : GONE);
                break;

            case CARD:
                mViewDataBinding.cardLayout.setVisibility(visible ? VISIBLE : GONE);
                break;

            case PHONE:
                mViewDataBinding.phoneLayout.setVisibility(visible ? VISIBLE : GONE);
                break;

            case VBANK:
                mViewDataBinding.transferLayout.setVisibility(visible ? VISIBLE : GONE);
                break;

            case FREE:
                if (visible == true)
                {
                    mViewDataBinding.paymentGuideView.setVisibility(GONE);
                    mViewDataBinding.paymentTypeInformationLayout.setVisibility(GONE);
                    mViewDataBinding.freePaymentView.setVisibility(VISIBLE);
                } else
                {
                    mViewDataBinding.paymentTypeInformationLayout.setVisibility(VISIBLE);
                    mViewDataBinding.freePaymentView.setVisibility(GONE);
                }
                break;
        }

        if (paymentType != PaymentType.FREE)
        {
            ArrayList<View> paymentTypeViewList = new ArrayList<>();

            if (mViewDataBinding.simpleCardLayout.getVisibility() == VISIBLE)
            {
                paymentTypeViewList.add(mViewDataBinding.simpleCardLayout);
            }

            if (mViewDataBinding.cardLayout.getVisibility() == VISIBLE)
            {
                paymentTypeViewList.add(mViewDataBinding.cardLayout);
            }

            if (mViewDataBinding.phoneLayout.getVisibility() == VISIBLE)
            {
                paymentTypeViewList.add(mViewDataBinding.phoneLayout);
            }

            if (mViewDataBinding.transferLayout.getVisibility() == VISIBLE)
            {
                paymentTypeViewList.add(mViewDataBinding.transferLayout);
            }

            if (paymentTypeViewList.size() == 0)
            {
                return;
            }

            final int DP_1 = ScreenUtils.dpToPx(getContext(), 1);

            // 결제 타입이 1가지만 된경우
            if (paymentTypeViewList.size() == 1)
            {
                View view = paymentTypeViewList.get(0);
                ((LinearLayout.LayoutParams) view.getLayoutParams()).setMargins(DP_1, DP_1, DP_1, DP_1);
                view.setBackgroundResource(R.drawable.selector_select_paymnettype_top_bottom_background);
            } else
            {
                int size = paymentTypeViewList.size();
                View view;

                for (int i = 0; i < size; i++)
                {
                    view = paymentTypeViewList.get(i);

                    if (i == 0)
                    {
                        ((LinearLayout.LayoutParams) view.getLayoutParams()).setMargins(DP_1, DP_1, DP_1, 0);
                        view.setBackgroundResource(R.drawable.selector_select_paymnettype_top_background);
                    } else if (i == size - 1)
                    {
                        ((LinearLayout.LayoutParams) view.getLayoutParams()).setMargins(DP_1, 0, DP_1, DP_1);
                        view.setBackgroundResource(R.drawable.selector_select_paymnettype_bottom_background);
                    } else
                    {
                        ((LinearLayout.LayoutParams) view.getLayoutParams()).setMargins(DP_1, 0, DP_1, 0);
                        view.setBackgroundResource(R.drawable.selector_select_paymnettype_background);
                    }
                }
            }
        }
    }

    public void setGuidePaymentTypeVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.paymentGuideView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setGuidePaymentType(String text)
    {
        if (mViewDataBinding == null || DailyTextUtils.isTextEmpty(text) == true)
        {
            return;
        }

        mViewDataBinding.paymentGuideView.setText(text);
    }

    public void setEasyCard(String cardName, String cardNumber)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(cardName, cardNumber) == true)
        {
            mViewDataBinding.cardManagerLayout.setVisibility(GONE);
            mViewDataBinding.emptySimpleCardLayout.setVisibility(VISIBLE);
            mViewDataBinding.selectedSimpleCardLayout.setVisibility(GONE);
        } else
        {
            mViewDataBinding.cardManagerLayout.setVisibility(VISIBLE);
            mViewDataBinding.emptySimpleCardLayout.setVisibility(GONE);
            mViewDataBinding.selectedSimpleCardLayout.setVisibility(VISIBLE);

            mViewDataBinding.logoTextView.setText(cardName);
            mViewDataBinding.numberTextView.setText(cardNumber);
        }
    }

    private void setPaymentTypeEnabled(final View view, boolean enabled)
    {
        if (view == null)
        {
            return;
        }

        if (enabled == true)
        {
            view.setOnClickListener(null);
            view.setVisibility(View.GONE);
        } else
        {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });
        }
    }
}
