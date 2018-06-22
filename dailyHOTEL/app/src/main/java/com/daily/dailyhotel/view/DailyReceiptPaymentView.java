package com.daily.dailyhotel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutReceiptPaymentDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;

/**
 * Created by android_sam on 2017. 12. 14..
 */

public class DailyReceiptPaymentView extends LinearLayout
{
    LayoutReceiptPaymentDataBinding mDataBinding;

    public DailyReceiptPaymentView(Context context)
    {
        super(context);
        initLayout(context);
    }

    public DailyReceiptPaymentView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initLayout(context);
    }

    public DailyReceiptPaymentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DailyReceiptPaymentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_receipt_payment_data, this, true);
        setOrientation(VERTICAL);
    }

    public void setData(String paymentDate, String paymentType, int totalPrice, int bonusPrice, int couponPrice, int paymentPrice)
    {
        try
        {
            mDataBinding.paymentDateTextView.setText(paymentDate);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        // 결제수단
        if (DailyTextUtils.isTextEmpty(paymentType) == true)
        {
            mDataBinding.paymentTypeLayout.setVisibility(View.GONE);
        } else
        {
            mDataBinding.paymentTypeLayout.setVisibility(View.VISIBLE);
            mDataBinding.paymentTypeTextView.setText(paymentType);
        }

        // 총금액
        mDataBinding.totalPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), totalPrice, false));

        // 적립금 혹은 쿠폰 사용
        if (bonusPrice > 0 || couponPrice > 0)
        {
            if (bonusPrice < 0)
            {
                bonusPrice = 0;
            }

            if (couponPrice < 0)
            {
                couponPrice = 0;
            }

            mDataBinding.saleLayout.setVisibility(View.VISIBLE);
            mDataBinding.discountLayout.setVisibility(View.VISIBLE);

            mDataBinding.discountPriceTextView.setText( //
                "- " + DailyTextUtils.getPriceFormat(getContext(), bonusPrice + couponPrice, false));
        } else
        {
            mDataBinding.saleLayout.setVisibility(View.GONE);
            mDataBinding.discountLayout.setVisibility(View.GONE);
        }

        // 총 입금(실 결제) 금액
        mDataBinding.paymentPriceTextView.setText( //
            DailyTextUtils.getPriceFormat(getContext(), paymentPrice, false));
    }
}
