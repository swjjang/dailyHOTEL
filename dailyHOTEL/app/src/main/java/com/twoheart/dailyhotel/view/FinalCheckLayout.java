package com.twoheart.dailyhotel.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.view.widget.DailySignatureView;

/**
 * 신용카드 Final Check
 *
 * @author sheldon
 */
public class FinalCheckLayout extends FrameLayout
{
    private DailySignatureView mDailySignatureView;

    public FinalCheckLayout(Context context, int[] textResIds)
    {
        super(context);

        initLayout(context, textResIds);
    }

    private void initLayout(Context context, int[] textResIds)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_finalcheck, this, true);
        ViewGroup messageLayout = (ViewGroup) view.findViewById(R.id.messageLayout);

        int length = textResIds.length;

        for (int i = 0; i < length; i++)
        {
            View messageRow = inflater.inflate(R.layout.row_payment_agreedialog, messageLayout, false);

            TextView messageTextView = (TextView) messageRow.findViewById(R.id.messageTextView);

            String message = context.getString(textResIds[i]);

            if (i == 0)
            {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message);

                int boldStartIndex = message.indexOf("예약");
                int boldLength = "예약 취소, 변경 및 환불이 불가".length();

                spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dialog_title_text)), //
                    boldStartIndex, boldStartIndex + boldLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                    boldStartIndex, boldStartIndex + boldLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTextView.setText(spannableStringBuilder);
            } else
            {
                messageTextView.setText(message);
            }

            messageLayout.addView(messageRow);
        }

        mDailySignatureView = (DailySignatureView) view.findViewById(R.id.signatureView);
    }

    public void setOnUserActionListener(DailySignatureView.OnUserActionListener listener)
    {
        if (mDailySignatureView != null)
        {
            mDailySignatureView.setOnUserActionListener(listener);
        }
    }
}