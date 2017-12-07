package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.Date;

public class StayAutoRefundLayout extends BaseLayout implements Constants, View.OnClickListener
{
    private View mRefundAccountLayout, mRequestRefundView, mCancelReasonEmptyView;
    private TextView mSelectReasonCancelTextView, mBankNameTextView;
    private EditText mAccountNumberEditText, mAccountNameEditText;

    public interface OnEventListener extends OnBaseEventListener
    {
        void showSelectCancelDialog();

        void showSelectBankListDialog();

        void onAccountTextWatcher(int length);

        void onClickRefund();
    }

    public StayAutoRefundLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mSelectReasonCancelTextView = view.findViewById(R.id.selectReasonCancelView);
        mSelectReasonCancelTextView.setOnClickListener(this);

        initAccountLayout(view);

        mRequestRefundView = view.findViewById(R.id.requestRefundView);
        mRequestRefundView.setOnClickListener(this);
    }

    private void initAccountLayout(View view)
    {
        mCancelReasonEmptyView = view.findViewById(R.id.cancelReasonEmptyView);

        mRefundAccountLayout = view.findViewById(R.id.refundAccountLayout);

        mBankNameTextView = view.findViewById(R.id.bankNameTextView);
        mBankNameTextView.setOnClickListener(this);

        mAccountNumberEditText = view.findViewById(R.id.accountNumberEditText);
        mAccountNameEditText = view.findViewById(R.id.accountNameEditText);

        mAccountNumberEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                ((OnEventListener) mOnEventListener).onAccountTextWatcher(s.length());
            }
        });

        mAccountNameEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                ((OnEventListener) mOnEventListener).onAccountTextWatcher(s.length());
            }
        });
    }

    public void setPlaceBookingDetail(StayBookingDetail stayBookingDetail)
    {
        View dateInformationLayout = mRootView.findViewById(R.id.dateInformationLayout);

        initTimeInformationLayout(mContext, dateInformationLayout, stayBookingDetail);

        // 예약 장소
        TextView hotelNameTextView = mRootView.findViewById(R.id.hotelNameTextView);
        TextView roomTypeTextView = mRootView.findViewById(R.id.roomTypeTextView);
        TextView addressTextView = mRootView.findViewById(R.id.addressTextView);

        hotelNameTextView.setText(stayBookingDetail.placeName);
        roomTypeTextView.setText(stayBookingDetail.roomName);
        addressTextView.setText(stayBookingDetail.address);

        initPaymentInformationLayout(mContext, mRootView, stayBookingDetail);
    }

    private void initTimeInformationLayout(Context context, View view, StayBookingDetail bookingDetail)
    {
        if (context == null || view == null || bookingDetail == null)
        {
            return;
        }

        TextView checkInDayTextView = view.findViewById(R.id.checkinDayTextView);
        TextView checkOutDayTextView = view.findViewById(R.id.checkoutDayTextView);
        TextView nightsTextView = view.findViewById(R.id.nightsTextView);

        try
        {
            String checkInDateFormat = DailyCalendar.convertDateFormatString(bookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH시");
            SpannableStringBuilder checkInSpannableStringBuilder = new SpannableStringBuilder(checkInDateFormat);
            checkInSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(context).getMediumTypeface()),//
                checkInDateFormat.length() - 3, checkInDateFormat.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            checkInDayTextView.setText(checkInSpannableStringBuilder);
        } catch (Exception e)
        {
            checkInDayTextView.setText(null);
        }

        try
        {
            String checkOutDateFormat = DailyCalendar.convertDateFormatString(bookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH시");
            SpannableStringBuilder checkOutSpannableStringBuilder = new SpannableStringBuilder(checkOutDateFormat);
            checkOutSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(context).getMediumTypeface()),//
                checkOutDateFormat.length() - 3, checkOutDateFormat.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            checkOutDayTextView.setText(checkOutSpannableStringBuilder);
        } catch (Exception e)
        {
            checkOutDayTextView.setText(null);
        }

        try
        {
            Date checkInDate = DailyCalendar.convertDate(bookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT);
            Date checkOutDate = DailyCalendar.convertDate(bookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT);

            int nights = (int) ((DailyCalendar.clearTField(checkOutDate.getTime()) - DailyCalendar.clearTField(checkInDate.getTime())) / DailyCalendar.DAY_MILLISECOND);
            nightsTextView.setText(context.getString(R.string.label_nights, nights));
        } catch (Exception e)
        {
            nightsTextView.setText(null);
        }
    }

    private void initPaymentInformationLayout(Context context, View view, StayBookingDetail stayBookingDetail)
    {
        if (stayBookingDetail == null)
        {
            return;
        }

        TextView paymentDateTextView = view.findViewById(R.id.paymentDateTextView);
        TextView priceTextView = view.findViewById(R.id.priceTextView);

        View bonusLayout = view.findViewById(R.id.bonusLayout);
        View couponLayout = view.findViewById(R.id.couponLayout);
        TextView bonusTextView = view.findViewById(R.id.bonusTextView);
        TextView couponTextView = view.findViewById(R.id.couponTextView);
        TextView totalPriceTextView = view.findViewById(R.id.totalPriceTextView);

        try
        {
            paymentDateTextView.setText(DailyCalendar.convertDateFormatString(stayBookingDetail.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        priceTextView.setText(DailyTextUtils.getPriceFormat(context, stayBookingDetail.price, false));


        if (stayBookingDetail.bonus > 0)
        {
            bonusLayout.setVisibility(View.VISIBLE);
            bonusTextView.setText("- " + DailyTextUtils.getPriceFormat(context, stayBookingDetail.bonus, false));
        } else
        {
            bonusLayout.setVisibility(View.GONE);
        }

        if (stayBookingDetail.coupon > 0)
        {
            couponLayout.setVisibility(View.VISIBLE);
            couponTextView.setText("- " + DailyTextUtils.getPriceFormat(context, stayBookingDetail.coupon, false));
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        totalPriceTextView.setText(DailyTextUtils.getPriceFormat(context, stayBookingDetail.paymentPrice, false));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.selectReasonCancelView:
                ((OnEventListener) mOnEventListener).showSelectCancelDialog();
                break;

            case R.id.bankNameTextView:
                ((OnEventListener) mOnEventListener).showSelectBankListDialog();
                break;

            case R.id.requestRefundView:
                ((OnEventListener) mOnEventListener).onClickRefund();
                break;
        }
    }

    public void setCancelReasonText(String reason)
    {
        if (mSelectReasonCancelTextView == null)
        {
            return;
        }

        mSelectReasonCancelTextView.setText(reason);
    }

    public void setBankText(String bankName)
    {
        if (mBankNameTextView == null)
        {
            return;
        }

        mBankNameTextView.setText(bankName);
    }

    public void setAccountLayoutVisible(boolean visible)
    {
        if (mRefundAccountLayout == null)
        {
            return;
        }

        if (visible == true && mRefundAccountLayout.getVisibility() != View.VISIBLE)
        {
            mRefundAccountLayout.setVisibility(View.VISIBLE);
            mCancelReasonEmptyView.setVisibility(View.GONE);
        } else if (visible == false && mRefundAccountLayout.getVisibility() != View.GONE)
        {
            mRefundAccountLayout.setVisibility(View.GONE);
            mCancelReasonEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public void setRefundButtonEnabled(boolean enabled)
    {
        if (mRequestRefundView == null)
        {
            return;
        }

        mRequestRefundView.setEnabled(enabled);
    }

    public String getAccountNumber()
    {
        if (mAccountNumberEditText == null)
        {
            return null;
        }

        return mAccountNumberEditText.getText().toString();
    }

    public String getAccountName()
    {
        if (mAccountNameEditText == null)
        {
            return null;
        }

        return mAccountNameEditText.getText().toString();
    }
}
