package com.daily.dailyhotel.screen.booking.detail.stay.refund;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.entity.StayBookingDetail;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayAutoRefundDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.Date;

public class StayAutoRefundView extends BaseDialogView<StayAutoRefundView.OnEventListener, ActivityStayAutoRefundDataBinding> implements StayAutoRefundInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void showSelectCancelDialog();

        void showSelectBankListDialog();

        void onAccountTextWatcher(int length);

        void onRefundClick();
    }

    public StayAutoRefundView(BaseActivity baseActivity, StayAutoRefundView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayAutoRefundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initLayout(viewDataBinding);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivityStayAutoRefundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setTitleText(R.string.label_request_free_refund);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private void initLayout(ActivityStayAutoRefundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.requestRefundView.setOnClickListener(this);

        // data, payment, refund 순
        viewDataBinding.refundDataBinding.selectReasonCancelView.setOnClickListener(this);
        viewDataBinding.refundDataBinding.bankNameTextView.setOnClickListener(this);
        viewDataBinding.refundDataBinding.accountNumberEditText.addTextChangedListener(new TextWatcher()
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
                getEventListener().onAccountTextWatcher(s.length());
            }
        });

        viewDataBinding.refundDataBinding.accountNameEditText.addTextChangedListener(new TextWatcher()
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
                getEventListener().onAccountTextWatcher(s.length());
            }
        });
    }

    private void setTimeInformation(StayBookingDetail stayBookingDetail)
    {
        if (getContext() == null || getViewDataBinding() == null || stayBookingDetail == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";
        final String TIME_FORMAT = "HH:mm";

        try
        {
            String checkInTime = DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, TIME_FORMAT);
            String checkInDate = DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT);

            SpannableStringBuilder checkInDateSpannableString = new SpannableStringBuilder(checkInDate + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                checkInDate.length(), checkInDate.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewDataBinding().dateDataBinding.checkInDayTextView.setText(checkInDateSpannableString);
        } catch (Exception e)
        {
            getViewDataBinding().dateDataBinding.checkInDayTextView.setText(null);
        }

        try
        {
            String checkOutTime = DailyCalendar.convertDateFormatString(stayBookingDetail.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, TIME_FORMAT);
            String checkOutDate = DailyCalendar.convertDateFormatString(stayBookingDetail.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT);

            SpannableStringBuilder checkOutDateSpannableString = new SpannableStringBuilder(checkOutDate + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                checkOutDate.length(), checkOutDate.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewDataBinding().dateDataBinding.checkOutDayTextView.setText(checkOutDateSpannableString);
        } catch (Exception e)
        {
            getViewDataBinding().dateDataBinding.checkOutDayTextView.setText(null);
        }

        try
        {
            Date checkInDate = DailyCalendar.convertDate(stayBookingDetail.checkInDateTime, DailyCalendar.ISO_8601_FORMAT);
            Date checkOutDate = DailyCalendar.convertDate(stayBookingDetail.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT);

            int nights = (int) ((DailyCalendar.clearTField(checkOutDate.getTime()) - DailyCalendar.clearTField(checkInDate.getTime())) / DailyCalendar.DAY_MILLISECOND);
            getViewDataBinding().dateDataBinding.nightsTextView.setText(getString(R.string.label_nights, nights));
        } catch (Exception e)
        {
            getViewDataBinding().dateDataBinding.nightsTextView.setText(null);
        }
    }

    private void setPlaceInformation(StayBookingDetail stayBookingDetail)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        // 예약 장소
        getViewDataBinding().dateDataBinding.hotelNameTextView.setText(stayBookingDetail.stayName);
        getViewDataBinding().dateDataBinding.roomTypeTextView.setText(stayBookingDetail.roomName);
        getViewDataBinding().dateDataBinding.addressTextView.setText(stayBookingDetail.stayAddress);
    }

    private void setPaymentInformation(StayBookingDetail stayBookingDetail)
    {
        if (stayBookingDetail == null || getViewDataBinding() == null)
        {
            return;
        }

        try
        {
            getViewDataBinding().paymentDataBinding.paymentDateTextView.setText(DailyCalendar.convertDateFormatString(stayBookingDetail.paymentDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewDataBinding().paymentDataBinding.priceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), stayBookingDetail.discountTotal, false));

        if (stayBookingDetail.bonusAmount > 0)
        {
            getViewDataBinding().paymentDataBinding.bonusLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().paymentDataBinding.bonusTextView.setText("- " + DailyTextUtils.getPriceFormat(getContext(), stayBookingDetail.bonusAmount, false));
        } else
        {
            getViewDataBinding().paymentDataBinding.bonusLayout.setVisibility(View.GONE);
        }

        if (stayBookingDetail.couponAmount > 0)
        {
            getViewDataBinding().paymentDataBinding.couponLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().paymentDataBinding.couponTextView.setText("- " + DailyTextUtils.getPriceFormat(getContext(), stayBookingDetail.couponAmount, false));
        } else
        {
            getViewDataBinding().paymentDataBinding.couponLayout.setVisibility(View.GONE);
        }

        getViewDataBinding().paymentDataBinding.totalPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), stayBookingDetail.priceTotal, false));
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.selectReasonCancelView:
                getEventListener().showSelectCancelDialog();
                break;

            case R.id.bankNameTextView:
                getEventListener().showSelectBankListDialog();
                break;

            case R.id.requestRefundView:
                getEventListener().onRefundClick();
                break;
        }
    }

    @Override
    public void setPlaceBookingDetail(StayBookingDetail stayBookingDetail)
    {
        setTimeInformation(stayBookingDetail);
        setPlaceInformation(stayBookingDetail);
        setPaymentInformation(stayBookingDetail);
    }


    @Override
    public void setCancelReasonText(String reason)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().refundDataBinding.selectReasonCancelView.setText(reason);
    }

    @Override
    public String getCancelReasonText()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return getViewDataBinding().refundDataBinding.selectReasonCancelView.getText().toString();
    }

    @Override
    public void setBankText(String bankName)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(bankName) == true)
        {
            bankName = getString(R.string.label_select_bank);
        }

        getViewDataBinding().refundDataBinding.bankNameTextView.setText(bankName);
    }

    @Override
    public void setAccountLayoutVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int refundAccountLayoutVisible = getViewDataBinding().refundDataBinding.refundAccountLayout.getVisibility();

        if (visible == true && refundAccountLayoutVisible != View.VISIBLE)
        {
            getViewDataBinding().refundDataBinding.refundAccountLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().refundDataBinding.cancelReasonEmptyView.setVisibility(View.GONE);
        } else if (visible == false && refundAccountLayoutVisible != View.GONE)
        {
            getViewDataBinding().refundDataBinding.refundAccountLayout.setVisibility(View.GONE);
            getViewDataBinding().refundDataBinding.cancelReasonEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setRefundButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().requestRefundView.setEnabled(enabled);
    }

    @Override
    public String getAccountNumber()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return getViewDataBinding().refundDataBinding.accountNumberEditText.getText().toString();
    }

    @Override
    public String getAccountName()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return getViewDataBinding().refundDataBinding.accountNameEditText.getText().toString();
    }
}
