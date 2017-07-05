package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutFinalcheckThirdPartyDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutPaymentAgreedialogThirdPartyDataBinding;
import com.twoheart.dailyhotel.widget.DailySignatureView;

/**
 * 신용카드 Final Check
 *
 * @author sheldon
 */
public class FinalCheckLayout extends FrameLayout
{
    private DailySignatureView mDailySignatureView;
    private ViewGroup mMessageLayout;

    public FinalCheckLayout(Context context)
    {
        super(context);

        initLayout(context);
    }

    public FinalCheckLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public FinalCheckLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    public FinalCheckLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_finalcheck, this, true);
        mMessageLayout = (ViewGroup) view.findViewById(R.id.messageLayout);
        mDailySignatureView = (DailySignatureView) view.findViewById(R.id.signatureView);
    }

    public void setMessages(int[] textResIds, String vendorName, boolean overseas)
    {
        if (textResIds == null)
        {
            return;
        }

        Context context = getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int length = textResIds.length;

        for (int i = 0; i < length; i++)
        {
            View messageRow = inflater.inflate(R.layout.row_payment_agreedialog, mMessageLayout, false);

            TextView messageTextView = (TextView) messageRow.findViewById(R.id.messageTextView);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(ScreenUtils.dpToPx(context, 5), 0, 0, ScreenUtils.dpToPx(context, 12));
            messageTextView.setLayoutParams(layoutParams);

            String message = context.getString(textResIds[i]);

            int startIndex = message.indexOf("<b>");

            if (startIndex >= 0)
            {
                message = message.replaceAll("<b>", "");

                int endIndex = message.indexOf("</b>");

                message = message.replaceAll("</b>", "");

                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message);

                spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dh_theme_color)), //
                    startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTextView.setText(spannableStringBuilder);
            } else
            {
                messageTextView.setText(message);
            }

            mMessageLayout.addView(messageRow);
        }

        // 제 3자 제공 내용 자세히 보기
        LayoutPaymentAgreedialogThirdPartyDataBinding viewDataBinding1 = DataBindingUtil.inflate(inflater, R.layout.layout_payment_agreedialog_third_party_data, mMessageLayout, true);

        vendorName = "업체명";

        String text = getContext().getString(R.string.message_payment_agreement_third_party_02, vendorName);
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.dh_theme_color)), text.indexOf(":") + 1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        LayoutFinalcheckThirdPartyDataBinding viewDataBinding2 = DataBindingUtil.inflate(inflater, R.layout.layout_finalcheck_third_party_data, mMessageLayout, true);
        viewDataBinding2.vendorTextView.setText(spannableString);

        if (overseas == true)
        {
            viewDataBinding2.offerInformationTextView.setText(R.string.message_payment_agreement_third_party_04_overseas);
        } else
        {
            viewDataBinding2.offerInformationTextView.setText(R.string.message_payment_agreement_third_party_04);
        }

        viewDataBinding1.getRoot().setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (viewDataBinding2.getRoot().getVisibility() == View.VISIBLE)
                {
                    viewDataBinding1.arrowImageView.setRotation(0f);
                    viewDataBinding2.getRoot().setVisibility(View.GONE);
                } else
                {
                    viewDataBinding1.arrowImageView.setRotation(180f);
                    viewDataBinding2.getRoot().setVisibility(View.VISIBLE);
                }
            }
        });

        viewDataBinding2.getRoot().setVisibility(View.GONE);
    }

    public DailySignatureView getDailySignatureView()
    {
        return mDailySignatureView;
    }

    public void setOnUserActionListener(DailySignatureView.OnUserActionListener listener)
    {
        if (mDailySignatureView != null)
        {
            mDailySignatureView.setOnUserActionListener(listener);
        }
    }
}