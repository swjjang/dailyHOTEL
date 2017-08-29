package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.view.carousel.DailyCarouselLayout;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.layout.PlaceReservationDetailLayout;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;
import java.util.Date;

public class StayReservationDetailLayout extends PlaceReservationDetailLayout
{
    private View mRefundPolicyLayout;
    private View mDefaultRefundPolicyLayout, mWaitRefundPolicyLayout;
    private View mRecommendGourmetButtonView;
    private View mRecommendGourmetItemLayout;
    private DailyCarouselLayout mRecommendGourmetCarouselLayout;
    private ObjectAnimator mRecommendGourmetButtonAnimator;

    public interface OnEventListener extends PlaceReservationDetailLayout.OnEventListener
    {
        void onRecommendListItemViewAllClick();

        void onRecommendListItemClick(View view);

        void onRecommendListItemLongClick(View view);
    }

    public StayReservationDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);

        mRecommendGourmetButtonView = view.findViewById(R.id.recommendGourmetButtonView);

        mRecommendGourmetButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mScrollLayout.smoothScrollTo(0, mRecommendGourmetItemLayout.getBottom());
            }
        });

        mRecommendGourmetItemLayout = view.findViewById(R.id.recommendGourmetLayout);

        mRecommendGourmetCarouselLayout = (DailyCarouselLayout) view.findViewById(R.id.recommendGourmetCarouselLayout);

        mRecommendGourmetCarouselLayout.setTitleText(R.string.label_booking_reservation_recommend_gourmet_title);

        mRecommendGourmetCarouselLayout.setCarouselListener(new DailyCarouselLayout.OnCarouselListener()
        {
            @Override
            public void onViewAllClick()
            {
                ((StayReservationDetailLayout.OnEventListener) mOnEventListener).onRecommendListItemViewAllClick();
            }

            @Override
            public void onItemClick(View view)
            {
                ((StayReservationDetailLayout.OnEventListener) mOnEventListener).onRecommendListItemClick(view);
            }

            @Override
            public void onItemLongClick(View view)
            {
                ((StayReservationDetailLayout.OnEventListener) mOnEventListener).onRecommendListItemLongClick(view);
            }
        });
    }

    @Override
    protected void initPlaceInformationLayout(Context context, View view, TodayDateTime todayDateTime, PlaceBookingDetail placeBookingDetail)
    {
        if (context == null || view == null || placeBookingDetail == null)
        {
            return;
        }

        StayBookingDetail stayBookingDetail = (StayBookingDetail) placeBookingDetail;

        // 3일전 부터 몇일 남음 필요.
        View remainedDayLayout = view.findViewById(R.id.remainedDayLayout);
        TextView remainedDayTextView = (TextView) view.findViewById(R.id.remainedDayTextView);

        if (stayBookingDetail.readyForRefund == true)
        {
            remainedDayLayout.setVisibility(View.GONE);
        } else
        {
            try
            {
                String remainedDayText;

                Date checkInDate = DailyCalendar.convertStringToDate(stayBookingDetail.checkInDate);
                Date currentDate = DailyCalendar.convertStringToDate(todayDateTime.currentDateTime);

                int dayOfDays = (int) ((DailyCalendar.clearTField(checkInDate.getTime()) - DailyCalendar.clearTField(currentDate.getTime())) / DailyCalendar.DAY_MILLISECOND);
                if (dayOfDays < 0 || dayOfDays > 3)
                {
                    remainedDayText = null;
                } else if (dayOfDays > 0)
                {
                    // 하루이상 남음
                    remainedDayText = context.getString(R.string.frag_booking_duedate_formet_stay, dayOfDays);
                } else
                {
                    // 당일
                    remainedDayText = context.getString(R.string.frag_booking_today_type_stay);
                }

                if (DailyTextUtils.isTextEmpty(remainedDayText) == true)
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
        }

        // 예약 장소
        TextView hotelNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
        TextView roomTypeTextView = (TextView) view.findViewById(R.id.roomTypeTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);

        hotelNameTextView.setText(stayBookingDetail.placeName);
        roomTypeTextView.setText(stayBookingDetail.roomName);
        addressTextView.setText(stayBookingDetail.address);
    }

    @Override
    protected void initTimeInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail)
    {
        if (context == null || view == null || placeBookingDetail == null)
        {
            return;
        }

        StayBookingDetail stayBookingDetail = (StayBookingDetail) placeBookingDetail;

        TextView checkInDayTextView = (TextView) view.findViewById(R.id.checkinDayTextView);
        TextView checkOutDayTextView = (TextView) view.findViewById(R.id.checkoutDayTextView);
        TextView nightsTextView = (TextView) view.findViewById(R.id.nightsTextView);

        try
        {
            String checkInDateFormat = DailyCalendar.convertDateFormatString(stayBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH시");
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
            String checkOutDateFormat = DailyCalendar.convertDateFormatString(stayBookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH시");
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
            // 날짜로 계산한다. 서버에 체크인시간, 체크아웃시간이 잘못 기록되어있는 경우 발생해서 예외 처리 추가
            String[] checkInDates = stayBookingDetail.checkInDate.split("T");
            String[] checkOutDates = stayBookingDetail.checkOutDate.split("T");

            Date checkInDate = DailyCalendar.convertDate(checkInDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);
            Date checkOutDate = DailyCalendar.convertDate(checkOutDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);

            int nights = (int) ((DailyCalendar.clearTField(checkOutDate.getTime()) - DailyCalendar.clearTField(checkInDate.getTime())) / DailyCalendar.DAY_MILLISECOND);
            nightsTextView.setText(context.getString(R.string.label_nights, nights));
        } catch (Exception e)
        {
            nightsTextView.setText(null);
        }
    }

    @Override
    protected void initGuestInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail)
    {
        if (context == null || view == null || placeBookingDetail == null)
        {
            return;
        }

        StayBookingDetail stayBookingDetail = (StayBookingDetail) placeBookingDetail;

        TextView guestNameTextView = (TextView) view.findViewById(R.id.guestNameTextView);
        TextView guestPhoneTextView = (TextView) view.findViewById(R.id.guestPhoneTextView);
        TextView guestEmailTextView = (TextView) view.findViewById(R.id.guestEmailTextView);

        guestNameTextView.setText(stayBookingDetail.guestName);
        guestPhoneTextView.setText(Util.addHyphenMobileNumber(mContext, stayBookingDetail.guestPhone));
        guestEmailTextView.setText(stayBookingDetail.guestEmail);

        View visitTypeLayout = view.findViewById(R.id.visitTypeLayout);
        View guideVisitMemoLayout = view.findViewById(R.id.guideVisitMemoLayout);

        TextView visitTypeTitleTextView = (TextView) view.findViewById(R.id.visitTypeTitleTextView);
        TextView visitTypeTextView = (TextView) view.findViewById(R.id.visitTypeTextView);
        TextView guideVisitMemoView = (TextView) view.findViewById(R.id.guideVisitMemoView);

        switch (stayBookingDetail.visitType)
        {
            case StayBookingDetail.VISIT_TYPE_CAR:
                visitTypeLayout.setVisibility(View.VISIBLE);

                visitTypeTitleTextView.setText(R.string.label_how_to_visit);
                visitTypeTextView.setText(R.string.label_visit_car);

                guideVisitMemoLayout.setVisibility(View.VISIBLE);
                guideVisitMemoView.setText(R.string.message_visit_car_memo);
                break;

            case StayBookingDetail.VISIT_TYPE_NO_PARKING:
                visitTypeLayout.setVisibility(View.VISIBLE);

                visitTypeTitleTextView.setText(R.string.label_parking_information);
                visitTypeTextView.setText(R.string.label_no_parking);

                guideVisitMemoLayout.setVisibility(View.VISIBLE);
                guideVisitMemoView.setText(R.string.message_visit_no_parking_memo);
                break;

            case StayBookingDetail.VISIT_TYPE_WALKING:
                visitTypeLayout.setVisibility(View.VISIBLE);

                visitTypeTitleTextView.setText(R.string.label_how_to_visit);
                visitTypeTextView.setText(R.string.label_visit_walk);

                guideVisitMemoLayout.setVisibility(View.GONE);
                break;

            default:
                visitTypeLayout.setVisibility(View.GONE);
                guideVisitMemoLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void initPaymentInformationLayout(Context context, View view, PlaceBookingDetail placeBookingDetail)
    {
        if (context == null || view == null || placeBookingDetail == null)
        {
            return;
        }

        StayBookingDetail stayBookingDetail = (StayBookingDetail) placeBookingDetail;

        TextView paymentDateTextView = (TextView) view.findViewById(R.id.paymentDateTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);

        View bonusLayout = view.findViewById(R.id.bonusLayout);
        View couponLayout = view.findViewById(R.id.couponLayout);
        TextView bonusTextView = (TextView) view.findViewById(R.id.bonusTextView);
        TextView couponTextView = (TextView) view.findViewById(R.id.couponTextView);
        TextView totalPriceTextView = (TextView) view.findViewById(R.id.totalPriceTextView);

        try
        {
            //            paymentDateTextView.setText(Util.simpleDateFormatISO8601toFormat(bookingDetail.paymentDate, "yyyy.MM.dd"));
            paymentDateTextView.setText(DailyCalendar.convertDateFormatString(stayBookingDetail.paymentDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        priceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayBookingDetail.price, false));


        if (stayBookingDetail.bonus > 0)
        {
            bonusLayout.setVisibility(View.VISIBLE);
            bonusTextView.setText("- " + DailyTextUtils.getPriceFormat(mContext, stayBookingDetail.bonus, false));
        } else
        {
            bonusLayout.setVisibility(View.GONE);
        }

        if (stayBookingDetail.coupon > 0)
        {
            couponLayout.setVisibility(View.VISIBLE);
            couponTextView.setText("- " + DailyTextUtils.getPriceFormat(mContext, stayBookingDetail.coupon, false));
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        totalPriceTextView.setText(DailyTextUtils.getPriceFormat(mContext, stayBookingDetail.paymentPrice, false));

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
        if (context == null || view == null || placeBookingDetail == null)
        {
            return;
        }

        StayBookingDetail stayBookingDetail = (StayBookingDetail) placeBookingDetail;

        mRefundPolicyLayout = view.findViewById(R.id.refundPolicyLayout);
        mDefaultRefundPolicyLayout = view.findViewById(R.id.defaultRefundPolicyLayout);
        mWaitRefundPolicyLayout = view.findViewById(R.id.waitRefundPolicyLayout);

        updateRefundPolicyLayout(stayBookingDetail);
    }

    public void updateRefundPolicyLayout(StayBookingDetail stayBookingDetail)
    {
        TextView refundPolicyTextView = (TextView) mRefundPolicyLayout.findViewById(R.id.refundPolicyTextView);

        if (DailyTextUtils.isTextEmpty(stayBookingDetail.mRefundComment) == false)
        {
            String comment = stayBookingDetail.mRefundComment.replaceAll("900034", "B70038");
            refundPolicyTextView.setText(Html.fromHtml(comment));
        }

        View refundButtonLayout = mRefundPolicyLayout.findViewById(R.id.refundButtonLayout);
        TextView buttonTextView = (TextView) refundButtonLayout.findViewById(R.id.buttonTextView);

        // 정책을 보여주지 않을 경우
        if (stayBookingDetail.isVisibleRefundPolicy == false)
        {
            setRefundLayoutVisible(false);
        } else
        {
            setRefundLayoutVisible(true);

            switch (getRefundPolicyStatus(stayBookingDetail))
            {
                case StayBookingDetail.STATUS_NO_CHARGE_REFUND:
                {
                    mDefaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mWaitRefundPolicyLayout.setVisibility(View.GONE);

                    refundButtonLayout.setOnClickListener(this);
                    buttonTextView.setText(R.string.label_request_free_refund);
                    break;
                }

                case StayBookingDetail.STATUS_WAIT_REFUND:
                {
                    mDefaultRefundPolicyLayout.setVisibility(View.GONE);
                    mWaitRefundPolicyLayout.setVisibility(View.VISIBLE);

                    TextView waitRefundPolicyTextView = (TextView) mWaitRefundPolicyLayout.findViewById(R.id.waitRefundPolicyTextView);
                    waitRefundPolicyTextView.setText(Html.fromHtml(mContext.getString(R.string.message_please_wait_refund01)));

                    refundButtonLayout.setOnClickListener(this);
                    buttonTextView.setText(R.string.label_contact_refund);
                    break;
                }

                case StayBookingDetail.STATUS_SURCHARGE_REFUND:
                {
                    mDefaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mWaitRefundPolicyLayout.setVisibility(View.GONE);

                    refundButtonLayout.setOnClickListener(this);
                    buttonTextView.setText(R.string.label_contact_refund);
                    break;
                }

                default:
                {
                    mDefaultRefundPolicyLayout.setVisibility(View.VISIBLE);
                    mWaitRefundPolicyLayout.setVisibility(View.GONE);
                    refundButtonLayout.setOnClickListener(null);
                    refundButtonLayout.setVisibility(View.GONE);
                    break;
                }
            }
        }
    }

    private String getRefundPolicyStatus(StayBookingDetail stayBookingDetail)
    {
        // 환불 대기 상태
        if (stayBookingDetail.readyForRefund == true)
        {
            return StayBookingDetail.STATUS_WAIT_REFUND;
        } else
        {
            if (DailyTextUtils.isTextEmpty(stayBookingDetail.refundPolicy) == false)
            {
                return stayBookingDetail.refundPolicy;
            } else
            {
                return StayBookingDetail.STATUS_SURCHARGE_REFUND;
            }
        }
    }

    public void setRefundLayoutVisible(boolean visible)
    {
        if (mRefundPolicyLayout == null)
        {
            return;
        }

        if (visible == true)
        {
            mRefundPolicyLayout.setVisibility(View.VISIBLE);
        } else
        {
            mRefundPolicyLayout.setVisibility(View.GONE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  고메  추천  ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setRecommendGourmetLayoutVisible(boolean isVisible)
    {
        if (mRecommendGourmetItemLayout == null)
        {
            return;
        }

        mRecommendGourmetItemLayout.setVisibility(isVisible == true ? View.VISIBLE : View.GONE);

        if (mRecommendGourmetButtonView == null)
        {
            return;
        }

        setRecommendGourmetButtonAnimation(isVisible);
    }

    public ArrayList<CarouselListItem> getRecommendGourmetData()
    {
        if (mRecommendGourmetCarouselLayout == null)
        {
            return null;
        }

        return mRecommendGourmetCarouselLayout.getData();
    }

    public void setRecommendGourmetData(ArrayList<CarouselListItem> list)
    {
        if (mRecommendGourmetCarouselLayout == null)
        {
            return;
        }

        mRecommendGourmetCarouselLayout.setData(list);
        setRecommendGourmetLayoutVisible(list != null && list.size() > 0);
    }

    public void setRecommendGourmetButtonAnimation(boolean isVisible)
    {
        if (mRecommendGourmetButtonView == null)
        {
            return;
        }

        boolean isOldVisible = mRecommendGourmetButtonView.getVisibility() == View.VISIBLE;
        if (isOldVisible == isVisible)
        {
            return;
        }

        if (mRecommendGourmetButtonAnimator != null)
        {
            mRecommendGourmetButtonAnimator.cancel();
            mRecommendGourmetButtonAnimator = null;
        }

        if (isVisible == true)
        {
            mRecommendGourmetButtonView.setVisibility(View.VISIBLE);

            float transY = ScreenUtils.dpToPx(mContext, 7d);

            mRecommendGourmetButtonAnimator = ObjectAnimator.ofFloat(mRecommendGourmetButtonView, "translationY", 0.0f, transY, 0.0f);
            mRecommendGourmetButtonAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mRecommendGourmetButtonAnimator.setDuration(1600);
            mRecommendGourmetButtonAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            mRecommendGourmetButtonAnimator.start();
        } else
        {
            mRecommendGourmetButtonView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
    {
        if (mRecommendGourmetItemLayout == null)
        {
            return;
        }

        if (isExpandedMap() == true)
        {
            return;
        }

        if (mRecommendGourmetCarouselLayout.hasData() == false //
            || View.VISIBLE != mRecommendGourmetItemLayout.getVisibility())
        {
            return;
        }

        int expectedY = mRecommendGourmetItemLayout.getTop() - ScreenUtils.getScreenHeight(mContext) //
            + mContext.getResources().getDimensionPixelOffset(R.dimen.toolbar_height) + ScreenUtils.dpToPx(mContext, 75d);

        //        ExLog.d("expectedY : " + expectedY + " , t : " + t);

        if (expectedY <= t)
        {
            setRecommendGourmetButtonAnimation(false);
        } else
        {
            setRecommendGourmetButtonAnimation(true);
        }
    }
}
