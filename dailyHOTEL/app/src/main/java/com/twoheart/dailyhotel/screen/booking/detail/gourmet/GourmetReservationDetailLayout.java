package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlaceReservationDetailLayout;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.Date;

public class GourmetReservationDetailLayout extends PlaceReservationDetailLayout
{
    public GourmetReservationDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initPlaceInformationLayout(Context context, View view, TodayDateTime todayDateTime, PlaceBookingDetail placeBookingDetail)
    {
        if (context == null || view == null || todayDateTime == null || placeBookingDetail == null)
        {
            return;
        }

        GourmetBookingDetail gourmetBookingDetail = (GourmetBookingDetail) placeBookingDetail;

        // 3일전 부터 몇일 남음 필요.
        View remainedDayLayout = view.findViewById(R.id.remainedDayLayout);
        TextView remainedDayTextView = (TextView) view.findViewById(R.id.remainedDayTextView);
        String remainedDayText;

        try
        {
            Date checkInDate = DailyCalendar.convertStringToDate(gourmetBookingDetail.reservationTime);
            Date currentDate = DailyCalendar.convertStringToDate(todayDateTime.currentDateTime);

            int dayOfDays = (int) ((DailyCalendar.clearTField(checkInDate.getTime()) - DailyCalendar.clearTField(currentDate.getTime())) / DailyCalendar.DAY_MILLISECOND);
            if (dayOfDays < 0 || dayOfDays > 3)
            {
                remainedDayText = null;
            } else if (dayOfDays > 0)
            {
                // 하루이상 남음
                remainedDayText = context.getString(R.string.frag_booking_duedate_formet, dayOfDays);
            } else
            {
                // 당일
                remainedDayText = context.getString(R.string.frag_booking_today_type_gourmet);
            }

            if (com.daily.base.util.TextUtils.isTextEmpty(remainedDayText) == true)
            {
                remainedDayLayout.setVisibility(View.GONE);
            } else
            {
                remainedDayLayout.setVisibility(View.VISIBLE);
                remainedDayTextView.setText(remainedDayText);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        TextView gourmetNameTextView = (TextView) view.findViewById(R.id.gourmetNameTextView);
        TextView ticketTypeTextView = (TextView) view.findViewById(R.id.ticketTypeTextView);
        TextView ticketCountTextView = (TextView) view.findViewById(R.id.ticketCountTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);

        gourmetNameTextView.setText(gourmetBookingDetail.placeName);
        ticketTypeTextView.setText(gourmetBookingDetail.ticketName);
        ticketCountTextView.setText(mContext.getString(R.string.label_booking_count, gourmetBookingDetail.ticketCount));
        addressTextView.setText(gourmetBookingDetail.address);
    }

    @Override
    protected void initTimeInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail)
    {
        if (context == null || view == null || placeBookingDetail == null)
        {
            return;
        }

        GourmetBookingDetail gourmetBookingDetail = (GourmetBookingDetail) placeBookingDetail;

        TextView ticketDateTextView = (TextView) view.findViewById(R.id.ticketDateTextView);
        TextView ticketTimeTextView = (TextView) view.findViewById(R.id.ticketTimeTextView);

        try
        {
            String ticketDateFormat = DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE)");
            ticketDateTextView.setText(ticketDateFormat);
        } catch (Exception e)
        {
            ticketDateTextView.setText(null);
        }

        try
        {
            String timeDateFormat = DailyCalendar.convertDateFormatString(gourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm");
            ticketTimeTextView.setText(timeDateFormat);
        } catch (Exception e)
        {
            ticketTimeTextView.setText(null);
        }
    }

    @Override
    protected void initGuestInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail)
    {
        if (context == null || view == null || placeBookingDetail == null)
        {
            return;
        }

        GourmetBookingDetail gourmetBookingDetail = (GourmetBookingDetail) placeBookingDetail;

        TextView guestNameTextView = (TextView) view.findViewById(R.id.guestNameTextView);
        TextView guestPhoneTextView = (TextView) view.findViewById(R.id.guestPhoneTextView);
        TextView guestEmailTextView = (TextView) view.findViewById(R.id.guestEmailTextView);

        guestNameTextView.setText(gourmetBookingDetail.guestName);
        guestPhoneTextView.setText(Util.addHyphenMobileNumber(mContext, gourmetBookingDetail.guestPhone));
        guestEmailTextView.setText(gourmetBookingDetail.guestEmail);
    }

    @Override
    protected void initPaymentInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail)
    {
        if (context == null || view == null || placeBookingDetail == null)
        {
            return;
        }

        GourmetBookingDetail gourmetBookingDetail = (GourmetBookingDetail) placeBookingDetail;

        TextView paymentDateTextView = (TextView) view.findViewById(R.id.paymentDateTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);

        View bonusLayout = view.findViewById(R.id.bonusLayout);
        View couponLayout = view.findViewById(R.id.couponLayout);
        bonusLayout.setVisibility(View.GONE);

        TextView couponTextView = (TextView) view.findViewById(R.id.couponTextView);
        TextView totalPriceTextView = (TextView) view.findViewById(R.id.totalPriceTextView);

        try
        {
            paymentDateTextView.setText(DailyCalendar.convertDateFormatString(gourmetBookingDetail.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        priceTextView.setText(com.daily.base.util.TextUtils.getPriceFormat(mContext, gourmetBookingDetail.price, false));

        if (gourmetBookingDetail.coupon > 0)
        {
            couponLayout.setVisibility(View.VISIBLE);
            couponTextView.setText("- " + com.daily.base.util.TextUtils.getPriceFormat(mContext, gourmetBookingDetail.coupon, false));
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        totalPriceTextView.setText(com.daily.base.util.TextUtils.getPriceFormat(mContext, gourmetBookingDetail.paymentPrice, false));

        // 영수증 발급
        View confirmView = view.findViewById(R.id.buttonLayout);
        confirmView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onIssuingReceiptClick();
            }
        });
    }

    @Override
    protected void initRefundPolicyLayout(Context context, View view, PlaceBookingDetail placeBookingDetail)
    {

    }
}
