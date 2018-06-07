package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewPaymentRefundDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetailInformationDataBinding;

import java.util.List;

public class DailyBookingRefundPolicyView extends ConstraintLayout
{
    private DailyViewPaymentRefundDataBinding mViewDataBinding;

    public DailyBookingRefundPolicyView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyBookingRefundPolicyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyBookingRefundPolicyView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_payment_refund_data, this, true);
    }

    public void setRefundPolicyList(List<String> refundPolicyList, boolean nrd)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (refundPolicyList == null || refundPolicyList.size() == 0)
        {
            mViewDataBinding.refundPolicyTitleLayout.setVisibility(GONE);
            mViewDataBinding.refundPolicyListLayout.setVisibility(GONE);
        } else
        {
            mViewDataBinding.refundPolicyTitleLayout.setVisibility(VISIBLE);
            mViewDataBinding.refundPolicyListLayout.setVisibility(VISIBLE);

            if (mViewDataBinding.refundPolicyListLayout.getChildCount() > 0)
            {
                mViewDataBinding.refundPolicyListLayout.removeAllViews();
            }

            int size = refundPolicyList.size();

            for (int i = 0; i < size; i++)
            {
                String comment = refundPolicyList.get(i);

                if (DailyTextUtils.isTextEmpty(comment) == true)
                {
                    continue;
                }

                LayoutStayOutboundDetailInformationDataBinding detailInformationDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
                    , R.layout.layout_stay_outbound_detail_information_data, mViewDataBinding.refundPolicyListLayout, true);

                if (nrd)
                {
                    comment = comment.replaceAll("(900034|B70038|b70038)", "EB2135");
                } else
                {
                    comment = comment.replaceAll("(900034|B70038|b70038)", "2C8DE6");
                }

                detailInformationDataBinding.textView.setText(Html.fromHtml(comment));

                if (i == size - 1)
                {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) detailInformationDataBinding.textView.getLayoutParams();
                    layoutParams.bottomMargin = 0;
                    detailInformationDataBinding.textView.setLayoutParams(layoutParams);
                }
            }
        }
    }
}
