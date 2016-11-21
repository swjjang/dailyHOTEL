package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HotelBookingDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
        mSelectReasonCancelTextView = (TextView) view.findViewById(R.id.selectReasonCancelView);
        mSelectReasonCancelTextView.setOnClickListener(this);

        initAccountLayout(view);

        mRequestRefundView = view.findViewById(R.id.requestRefundView);
        mRequestRefundView.setOnClickListener(this);
    }

    private void initAccountLayout(View view)
    {
        mCancelReasonEmptyView = view.findViewById(R.id.cancelReasonEmptyView);

        mRefundAccountLayout = view.findViewById(R.id.refundAccountLayout);

        mBankNameTextView = (TextView) view.findViewById(R.id.bankNameTextView);
        mBankNameTextView.setOnClickListener(this);

        mAccountNumberEditText = (EditText) view.findViewById(R.id.accountNumberEditText);
        mAccountNameEditText = (EditText) view.findViewById(R.id.accountNameEditText);

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

    public void setPlaceBookingDetail(HotelBookingDetail hotelBookingDetail)
    {
        View dateInformationLayout = mRootView.findViewById(R.id.dateInformationLayout);

        initTimeInformatonLayout(mContext, dateInformationLayout, hotelBookingDetail);

        // 예약 장소
        TextView hotelNameTextView = (TextView) mRootView.findViewById(R.id.hotelNameTextView);
        TextView roomTypeTextView = (TextView) mRootView.findViewById(R.id.roomTypeTextView);
        TextView addressTextView = (TextView) mRootView.findViewById(R.id.addressTextView);

        hotelNameTextView.setText(hotelBookingDetail.placeName);
        roomTypeTextView.setText(hotelBookingDetail.roomName);
        addressTextView.setText(hotelBookingDetail.address);

        initPaymentInformationLayout(mContext, mRootView, hotelBookingDetail);
    }

    private void initTimeInformatonLayout(Context context, View view, HotelBookingDetail bookingDetail)
    {
        if (context == null || view == null || bookingDetail == null)
        {
            return;
        }

        TextView checkinDayTextView = (TextView) view.findViewById(R.id.checkinDayTextView);
        TextView checkoutDayTextView = (TextView) view.findViewById(R.id.checkoutDayTextView);
        TextView nightsTextView = (TextView) view.findViewById(R.id.nightsTextView);

        try
        {
            String checkInDateFormat = DailyCalendar.convertDateFormatString(bookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH시");
            SpannableStringBuilder checkInSpannableStringBuilder = new SpannableStringBuilder(checkInDateFormat);
            checkInSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(context).getMediumTypeface()),//
                checkInDateFormat.length() - 3, checkInDateFormat.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            checkinDayTextView.setText(checkInSpannableStringBuilder);
        } catch (Exception e)
        {
            checkinDayTextView.setText(null);
        }

        try
        {
            String checkOutDateFormat = DailyCalendar.convertDateFormatString(bookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH시");
            SpannableStringBuilder checkOutSpannableStringBuilder = new SpannableStringBuilder(checkOutDateFormat);
            checkOutSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(context).getMediumTypeface()),//
                checkOutDateFormat.length() - 3, checkOutDateFormat.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            checkoutDayTextView.setText(checkOutSpannableStringBuilder);
        } catch (Exception e)
        {
            checkoutDayTextView.setText(null);
        }

        try
        {
            Date checkInDate = DailyCalendar.convertDate(bookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT);
            Date checkOutDate = DailyCalendar.convertDate(bookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT);

            int nights = (int) ((getCompareDate(checkOutDate.getTime()) - getCompareDate(checkInDate.getTime())) / SaleTime.MILLISECOND_IN_A_DAY);
            nightsTextView.setText(context.getString(R.string.label_nights, nights));
        } catch (Exception e)
        {
            nightsTextView.setText(null);
        }
    }

    private void initPaymentInformationLayout(Context context, View view, HotelBookingDetail hotelBookingDetail)
    {
        if (hotelBookingDetail == null)
        {
            return;
        }

        TextView paymentDateTextView = (TextView) view.findViewById(R.id.paymentDateTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);

        View bonusLayout = view.findViewById(R.id.bonusLayout);
        View couponLayout = view.findViewById(R.id.couponLayout);
        TextView bonusTextView = (TextView) view.findViewById(R.id.bonusTextView);
        TextView couponTextView = (TextView) view.findViewById(R.id.couponTextView);
        TextView totalPriceTextView = (TextView) view.findViewById(R.id.totalPriceTextView);

        try
        {
            paymentDateTextView.setText(DailyCalendar.convertDateFormatString(hotelBookingDetail.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        priceTextView.setText(Util.getPriceFormat(context, hotelBookingDetail.price, false));


        if (hotelBookingDetail.bonus > 0)
        {
            bonusLayout.setVisibility(View.VISIBLE);
            bonusTextView.setText("- " + Util.getPriceFormat(context, hotelBookingDetail.bonus, false));
        } else
        {
            bonusLayout.setVisibility(View.GONE);
        }

        if (hotelBookingDetail.coupon > 0)
        {
            couponLayout.setVisibility(View.VISIBLE);
            couponTextView.setText("- " + Util.getPriceFormat(context, hotelBookingDetail.coupon, false));
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        totalPriceTextView.setText(Util.getPriceFormat(context, hotelBookingDetail.paymentPrice, false));
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

    private long getCompareDate(long timeInMillis)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(timeInMillis);

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
