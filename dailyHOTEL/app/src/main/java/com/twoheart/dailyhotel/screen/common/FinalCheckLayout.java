package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutFinalcheckThirdPartyBinding;
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

//            if (i == length - 1)
//            {
//                layoutParams.setMargins(ScreenUtils.dpToPx(context, 5), 0, 0, 0);
//            } else
            {
                layoutParams.setMargins(ScreenUtils.dpToPx(context, 5), 0, 0, ScreenUtils.dpToPx(context, 10));
            }

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
                spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                    startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTextView.setText(spannableStringBuilder);
            } else
            {
                messageTextView.setText(message);
            }

            mMessageLayout.addView(messageRow);
        }

        // 제 3자 제공 내용 자세히 보기
        DailyTextView dailyTextView = new DailyTextView(getContext());
        dailyTextView.setPadding(ScreenUtils.dpToPx(getContext(), 15), 0, 0, 0);
        dailyTextView.setText(R.string.label_payment_agreement_third_party);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        dailyTextView.setTextColor(getContext().getResources().getColor(R.color.default_text_c323232));
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.navibar_m_ic_v, 0);
        dailyTextView.setCompoundDrawablePadding(ScreenUtils.dpToPx(getContext(), 2));
        dailyTextView.setPaintFlags(dailyTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mMessageLayout.addView(dailyTextView, layoutParams);

        vendorName = "업체명";

        String text = getContext().getString(R.string.message_payment_agreement_third_party_02, vendorName);
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.dh_theme_color)), text.indexOf(":") + 1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        LayoutFinalcheckThirdPartyBinding viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.layout_finalcheck_third_party, mMessageLayout, true);
        viewDataBinding.vendorTextView.setText(spannableString);

        if (overseas == true)
        {
            viewDataBinding.offerInformationTextView.setText(R.string.message_payment_agreement_third_party_04_overseas);
        } else
        {
            viewDataBinding.offerInformationTextView.setText(R.string.message_payment_agreement_third_party_04);
        }

        dailyTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (viewDataBinding.getRoot().getVisibility() == View.VISIBLE)
                {
                    dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.navibar_m_ic_v, 0);
                    viewDataBinding.getRoot().setVisibility(View.GONE);
                } else
                {
                    dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.navibar_m_ic_v, 0);
                    viewDataBinding.getRoot().setVisibility(View.VISIBLE);
                }
            }
        });

        viewDataBinding.getRoot().setVisibility(View.GONE);
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