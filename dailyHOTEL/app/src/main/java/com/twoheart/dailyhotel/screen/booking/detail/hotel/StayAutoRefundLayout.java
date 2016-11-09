package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bank;
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
import java.util.List;
import java.util.TimeZone;

public class StayAutoRefundLayout extends BaseLayout implements Constants, View.OnClickListener
{
    private TextView mSelectReasonCancelView, mBankNameTextView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void showSelectCancelDialog();

        void showSelectBankListDialog();

        void onClickRefund();
    }

    public StayAutoRefundLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mSelectReasonCancelView = (TextView) view.findViewById(R.id.selectReasonCancelView);
        mSelectReasonCancelView.setOnClickListener(this);

        mBankNameTextView = (TextView) view.findViewById(R.id.bankNameTextView);
        mBankNameTextView.setOnClickListener(this);

        View requestRefundView = view.findViewById(R.id.requestRefundView);
        requestRefundView.setOnClickListener(this);
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
        if (mSelectReasonCancelView == null)
        {
            return;
        }

        mSelectReasonCancelView.setText(reason);
    }

    public void setBankText(String bankName)
    {
        if (mBankNameTextView == null)
        {
            return;
        }

        mBankNameTextView.setText(bankName);
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

    private class BankListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<Bank> mList;
        private Context mContext;

        public BankListAdapter(Context context, List<Bank> list)
        {
            mContext = context;
            mList = list;
        }

        public Bank getItem(int position)
        {
            return mList.get(position);
        }

        @Override
        public int getItemCount()
        {
            return mList == null ? 0 : mList.size();
        }

        public void setData(List<Bank> list)
        {
            mList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_coupon, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
        {
            ((ItemViewHolder) holder).onBindViewHolder(position);
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder
        {
            TextView bankNameTextView;

            public ItemViewHolder(View view)
            {
                super(view);

                bankNameTextView = (TextView) view;
            }

            public void onBindViewHolder(final int position)
            {
                Bank bank = getItem(position);

                bankNameTextView.setText(bank.name);
            }
        }
    }
}
