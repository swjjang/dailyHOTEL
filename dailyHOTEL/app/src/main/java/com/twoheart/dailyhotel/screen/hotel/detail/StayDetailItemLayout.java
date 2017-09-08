package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 6. 5..
 */

public class StayDetailItemLayout extends LinearLayout
{
    private static final int GRID_COLUMN_COUNT = 5;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private StayDetailLayout.OnEventListener mEventListener;
    private View.OnTouchListener mEmptyViewOnTouchListener;

    private StayDetail mStayDetail;
    private StayBookingDay mStayBookingDay;
    private PlaceReviewScores mPlaceReviewScores;

    private View mHotelTitleLayout;

    public StayDetailItemLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout();
    }

    public StayDetailItemLayout(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout();
    }

    public StayDetailItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout();
    }

    public StayDetailItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        initLayout();
    }

    public void initLayout()
    {
        setOrientation(LinearLayout.VERTICAL);

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnEventListener(StayDetailLayout.OnEventListener onEventListener)
    {
        mEventListener = onEventListener;
    }

    public void setEmptyViewOnTouchListener(View.OnTouchListener emptyViewOnTouchListener)
    {
        mEmptyViewOnTouchListener = emptyViewOnTouchListener;
    }

    public void setData(StayBookingDay stayBookingDay, StayDetail stayDetail, PlaceReviewScores placeReviewScores)
    {
        mStayBookingDay = stayBookingDay;
        mStayDetail = stayDetail;
        mPlaceReviewScores = placeReviewScores;

        setView();
    }

    private void setView()
    {
        removeAllViews();

        //        StayDetailParams stayDetailParams = mStayDetail.getStayDetailParams();

        // 빈화면
        View emptyView = getEmptyView(mLayoutInflater, mEmptyViewOnTouchListener);
        if (emptyView != null)
        {
            addView(emptyView);
        }

        // 호텔 등급과 이름.
        View titleView = getTitleView(mLayoutInflater, mStayDetail, mEventListener);
        if (titleView != null)
        {
            addView(titleView);
        }

        // 주소 및 맵
        View addressView = getAddressView(mLayoutInflater, mStayDetail, mEventListener);
        if (addressView != null)
        {
            addView(addressView);
        }

        // 편의시설
        View amenitiesView = getAmenitiesView(mLayoutInflater, mStayDetail);
        if (amenitiesView != null)
        {
            addView(amenitiesView);
        }

        // D Benefit
        View benefitView = getBenefitView(mLayoutInflater, mStayDetail);
        if (benefitView != null)
        {
            addView(benefitView);
        }

        // 정보 영역
        View informationView = getInformationView(mLayoutInflater, mStayDetail);
        if (informationView != null)
        {
            addView(informationView);
        }

        // 문의하기
        View conciergeView = getConciergeView(mLayoutInflater, mEventListener);
        if (conciergeView != null)
        {
            addView(conciergeView);
        }
    }

    public View getTitleLayout()
    {
        return mHotelTitleLayout;
    }

    /**
     * 이미지 영역 빈화면
     *
     * @param layoutInflater
     * @return
     */
    private View getEmptyView(LayoutInflater layoutInflater, View.OnTouchListener onTouchListener)
    {
        View view = layoutInflater.inflate(R.layout.list_row_detail01, this, false);

        View emptyView = view.findViewById(R.id.imageEmptyHeight);
        emptyView.getLayoutParams().height = PlaceDetailLayout.getImageLayoutHeight(mContext);

        emptyView.setClickable(true);
        emptyView.setOnTouchListener(onTouchListener);

        return view;
    }

    /**
     * 호텔 등급 및 이름
     *
     * @param layoutInflater
     * @param stayDetail
     * @param onEventListener
     * @return
     */
    private View getTitleView(LayoutInflater layoutInflater, StayDetail stayDetail, StayDetailLayout.OnEventListener onEventListener)
    {
        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        View view = layoutInflater.inflate(R.layout.list_row_stay_detail02, this, false);

        mHotelTitleLayout = view.findViewById(R.id.hotelTitleLayout);

        // 등급
        TextView hotelGradeTextView = (TextView) mHotelTitleLayout.findViewById(R.id.hotelGradeTextView);
        hotelGradeTextView.setVisibility(View.VISIBLE);

        hotelGradeTextView.setText(stayDetailParams.getGrade().getName(mContext));
        hotelGradeTextView.setBackgroundResource(stayDetailParams.getGrade().getColorResId());

        // 호텔명
        TextView hotelNameTextView = (TextView) mHotelTitleLayout.findViewById(R.id.hotelNameTextView);
        hotelNameTextView.setText(stayDetailParams.name);

        // 만족도
        TextView satisfactionView = (TextView) mHotelTitleLayout.findViewById(R.id.satisfactionView);

        if (stayDetailParams.ratingShow == false)
        {
            satisfactionView.setVisibility(View.GONE);
        } else
        {
            satisfactionView.setVisibility(View.VISIBLE);
            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
            satisfactionView.setText(mContext.getString(R.string.label_stay_detail_satisfaction, //
                stayDetailParams.ratingValue, decimalFormat.format(stayDetailParams.ratingPersons)));
        }

        // 리뷰
        TextView trueReviewTextView = (TextView) mHotelTitleLayout.findViewById(R.id.trueReviewTextView);

        if (mPlaceReviewScores == null)
        {
            trueReviewTextView.setVisibility(View.GONE);
        } else
        {
            setTrueReviewCount(mPlaceReviewScores.reviewScoreTotalCount);
        }

        // 할인 쿠폰
        View couponLayout = view.findViewById(R.id.couponLayout);

        if (stayDetail.hasCoupon == true)
        {
            couponLayout.setVisibility(View.VISIBLE);

            View downloadCouponLayout = couponLayout.findViewById(R.id.downloadCouponLayout);

            downloadCouponLayout.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onEventListener.onDownloadCouponClick();
                }
            });
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        // Stamp
        View stampLayout = view.findViewById(R.id.stampLayout);
        View stampClickLayout = stampLayout.findViewById(R.id.stampClickLayout);

        if (stayDetailParams.isOverseas == false && DailyRemoteConfigPreference.getInstance(mContext).isRemoteConfigStampEnabled() == true)
        {
            // 테블릿 높이 수정 필요한지 확인
            stampLayout.setVisibility(View.VISIBLE);

            TextView stampMessage1TextView = (TextView) stampLayout.findViewById(R.id.stampMessage1TextView);
            TextView stampMessage2TextView = (TextView) stampLayout.findViewById(R.id.stampMessage2TextView);
            TextView stampMessage3TextView = (TextView) stampLayout.findViewById(R.id.stampMessage3TextView);

            String message1 = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigStampStayDetailMessage1();
            String message2 = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigStampStayDetailMessage2();

            boolean message3Enabled = DailyRemoteConfigPreference.getInstance(mContext).isRemoteConfigStampStayDetailMessage3Enabled();

            stampMessage1TextView.setText(message1);
            stampMessage2TextView.setText(message2);

            if (message3Enabled == true)
            {
                String message3 = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigStampStayDetailMessage3();

                SpannableString spannableString3 = new SpannableString(message3);
                spannableString3.setSpan(new UnderlineSpan(), 0, spannableString3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                stampMessage3TextView.setVisibility(View.VISIBLE);
                stampMessage3TextView.setText(spannableString3);

                stampClickLayout.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onEventListener.onStampClick();
                    }
                });
            } else
            {
                stampMessage3TextView.setVisibility(View.GONE);
                stampClickLayout.setOnClickListener(null);
            }
        } else
        {
            stampLayout.setVisibility(View.GONE);
        }

        // 날짜
        View dateInformationLayout = view.findViewById(R.id.dateInformationLayout);
        TextView checkInDayTextView = (TextView) dateInformationLayout.findViewById(R.id.checkinDayTextView);
        TextView checkOutDayTextView = (TextView) dateInformationLayout.findViewById(R.id.checkoutDayTextView);
        TextView nightsTextView = (TextView) dateInformationLayout.findViewById(R.id.nightsTextView);

        // 체크인체크아웃 날짜
        checkInDayTextView.setText(mStayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)"));
        checkOutDayTextView.setText(mStayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)"));

        try
        {
            nightsTextView.setText(mContext.getString(R.string.label_nights, mStayBookingDay.getNights()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        dateInformationLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener == null)
                {
                    return;
                }

                onEventListener.onCalendarClick();
            }
        });

        return view;
    }

    public void setTrueReviewCount(int count)
    {
        if (mHotelTitleLayout == null)
        {
            return;
        }

        TextView trueReviewTextView = (TextView) mHotelTitleLayout.findViewById(R.id.trueReviewTextView);

        if (count == 0)
        {
            trueReviewTextView.setVisibility(View.GONE);
        } else
        {
            trueReviewTextView.setVisibility(View.VISIBLE);
            trueReviewTextView.setText(mContext.getString(R.string.label_detail_view_review_go, count));
            trueReviewTextView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mEventListener == null)
                    {
                        return;
                    }

                    mEventListener.onReviewClick();
                }
            });
        }
    }

    /**
     * 호텔 주소 및 맵
     *
     * @param layoutInflater
     * @param stayDetail
     * @return
     */
    private View getAddressView(LayoutInflater layoutInflater, StayDetail stayDetail, StayDetailLayout.OnEventListener onEventListener)
    {
        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        final View view = layoutInflater.inflate(R.layout.list_row_detail03, this, false);

        view.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        // 주소지
        final TextView hotelAddressTextView = (TextView) view.findViewById(R.id.detailAddressTextView);

        final String address = stayDetailParams.address;
        hotelAddressTextView.setText(address);

        View clipAddress = view.findViewById(R.id.copyAddressView);
        clipAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener == null)
                {
                    return;
                }

                onEventListener.clipAddress(address);
            }
        });

        //길찾기
        View navigatorView = view.findViewById(R.id.navigatorView);
        navigatorView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener == null)
                {
                    return;
                }

                onEventListener.showNavigatorDialog();
            }
        });

        ImageView mapImageView = (ImageView) view.findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener != null)
                {
                    onEventListener.showMap();
                }
            }
        });

        return view;
    }

    /**
     * 편의시설
     *
     * @param layoutInflater
     * @param stayDetail
     * @return
     */
    private View getAmenitiesView(LayoutInflater layoutInflater, StayDetail stayDetail)
    {
        View view = layoutInflater.inflate(R.layout.list_row_detail_pictogram, this, false);
        if (view == null || stayDetail == null)
        {
            return view;
        }

        List<StayDetail.Pictogram> pictogramList = mStayDetail.getPictogramList();
        if (pictogramList == null || pictogramList.size() == 0)
        {
            return null;
        }

        android.support.v7.widget.GridLayout gridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.amenitiesGridLayout);
        gridLayout.removeAllViews();

        boolean isSingleLine = pictogramList.size() <= GRID_COLUMN_COUNT;

        for (StayDetail.Pictogram pictogram : pictogramList)
        {
            gridLayout.addView(getGridLayoutItemView(mContext, pictogram, isSingleLine));
        }

        int columnCount = pictogramList.size() % GRID_COLUMN_COUNT;

        if (columnCount != 0)
        {
            int addEmptyViewCount = GRID_COLUMN_COUNT - columnCount;
            for (int i = 0; i < addEmptyViewCount; i++)
            {
                gridLayout.addView(getGridLayoutItemView(mContext, StayDetail.Pictogram.NONE, isSingleLine));
            }
        }

        return view;
    }

    private DailyTextView getGridLayoutItemView(Context context, StayDetail.Pictogram pictogram, boolean isSingleLine)
    {
        DailyTextView dailyTextView = new DailyTextView(mContext);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(mContext.getResources().getColorStateList(R.color.default_text_c323232));
        dailyTextView.setText(pictogram.getName(context));
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, pictogram.getImageResId(), 0, 0);
        dailyTextView.setDrawableVectorTint(R.color.default_background_c454545);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        if (isSingleLine == true)
        {
            dailyTextView.setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 15));
        } else
        {
            dailyTextView.setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 2));
        }

        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    /**
     * 호텔 Benefit
     *
     * @param layoutInflater
     * @param stayDetail
     * @return
     */
    private View getBenefitView(LayoutInflater layoutInflater, StayDetail stayDetail)
    {
        View view = layoutInflater.inflate(R.layout.list_row_detail_benefit, this, false);

        if (view == null || stayDetail == null)
        {
            return null;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();
        final String benefit = stayDetailParams.benefit;

        if (DailyTextUtils.isTextEmpty(benefit) == true)
        {
            // benefit 이 없으면 상단 라인으로 대체 하기때문에 비어있으면 리턴
            return null;
        }

        final TextView benefitTitleTextView = (TextView) view.findViewById(R.id.benefitTitleTextView);
        final LinearLayout benefitMessagesLayout = (LinearLayout) view.findViewById(R.id.benefitMessagesLayout);

        benefitTitleTextView.setText(benefit);

        List<String> benefitList = stayDetail.getBenefitList();

        if (benefitList != null)
        {
            benefitMessagesLayout.removeAllViews();

            for (String information : benefitList)
            {
                ViewGroup childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail_benefit_text, benefitMessagesLayout, false);
                TextView textView = (TextView) childGroup.findViewById(R.id.textView);
                textView.setText(information);

                benefitMessagesLayout.addView(childGroup);
            }
        }

        return view;
    }

    /**
     * 정보
     *
     * @param layoutInflater
     * @param stayDetail
     * @return
     */
    private View getInformationView(LayoutInflater layoutInflater, StayDetail stayDetail)
    {
        if (layoutInflater == null || stayDetail == null || stayDetail.getProductList() == null)
        {
            return null;
        }

        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail04, this, false);
        if (viewGroup == null)
        {
            return null;
        }

        boolean hasNRD = false;
        for (StayProduct stayProduct : stayDetail.getProductList())
        {
            if (stayProduct.isNRD == true)
            {
                hasNRD = true;
                break;
            }
        }

        List<DetailInformation> detailInformationList = stayDetail.getDetailList();

        if (detailInformationList != null)
        {
            viewGroup.removeAllViews();
            boolean hasRefundPolicy = false;

            ViewGroup childViewGroup = null;
            for (DetailInformation information : detailInformationList)
            {
                childViewGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                makeInformationLayout(layoutInflater, childViewGroup, information, hasNRD, false);

                viewGroup.addView(childViewGroup);

                if (hasNRD == true && mContext.getString(R.string.label_detail_cancellation_refund_policy).equalsIgnoreCase(information.title) == true)
                {
                    hasRefundPolicy = true;
                }
            }

            // 서버에서 타이틀이 취소및 환불 규정이 없는 경우가 발생하는 경우가 있어서 관련 내용 처리
            if (hasNRD == true && hasRefundPolicy == false)
            {
                childViewGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                DetailInformation detailInformation = new DetailInformation(mContext.getString(R.string.label_detail_cancellation_refund_policy), null);

                makeInformationLayout(layoutInflater, childViewGroup, detailInformation, hasNRD, false);

                viewGroup.addView(childViewGroup);
            }

            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            if (stayDetailParams != null && stayDetailParams.reservationWaiting == true)
            {
                // 대기 예약 안내 추가
                childViewGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                List<String> contentList = new ArrayList<>();
                contentList.add(mContext.getString(R.string.message_stay_waiting_reservation_guide));
                DetailInformation detailInformation = new DetailInformation(mContext.getString(R.string.label_detail_waiting_reservation_guide), contentList);

                makeInformationLayout(layoutInflater, childViewGroup, detailInformation, hasNRD, true);

                viewGroup.addView(childViewGroup);

                View pensionOnlyLayout = layoutInflater.inflate(R.layout.list_row_detail_pension_only, viewGroup, false);
                viewGroup.addView(pensionOnlyLayout);

                if (childViewGroup != null)
                {
                    View lastContentView = childViewGroup.findViewById(R.id.contentsList);
                    if (lastContentView != null)
                    {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lastContentView.getLayoutParams();
                        layoutParams.bottomMargin = layoutParams.bottomMargin - ScreenUtils.dpToPx(mContext, 14d);
                        lastContentView.setLayoutParams(layoutParams);
                    }
                }
            } else
            {
                View lastContentView = childViewGroup.findViewById(R.id.contentsList);

                if (lastContentView != null)
                {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lastContentView.getLayoutParams();
                    layoutParams.bottomMargin = ScreenUtils.dpToPx(mContext, 20);
                    lastContentView.setLayoutParams(layoutParams);
                }
            }
        }

        return viewGroup;
    }

    private void makeInformationLayout(LayoutInflater layoutInflater, ViewGroup viewGroup, DetailInformation information, boolean hasNRD, boolean htmlCode)
    {
        if (layoutInflater == null || viewGroup == null || information == null)
        {
            return;
        }

        LinearLayout contentsLayout = (LinearLayout) viewGroup.findViewById(R.id.contentsList);
        contentsLayout.removeAllViews();

        TextView titleTextView = (TextView) viewGroup.findViewById(R.id.titleTextView);
        titleTextView.setText(information.title);

        boolean isRefundPolicy = false;

        if (hasNRD == true && mContext.getString(R.string.label_detail_cancellation_refund_policy).equalsIgnoreCase(information.title) == true)
        {
            isRefundPolicy = true;
        }

        List<String> contentsList = information.getContentsList();

        if (contentsList != null)
        {
            int size = contentsList.size();

            for (int i = 0; i < size; i++)
            {
                String contentText = contentsList.get(i);

                if (DailyTextUtils.isTextEmpty(contentText) == true)
                {
                    continue;
                }

                View textLayout = layoutInflater.inflate(R.layout.list_row_detail_text, contentsLayout, false);
                TextView textView = (TextView) textLayout.findViewById(R.id.textView);

                if (htmlCode == true)
                {
                    textView.setText(Html.fromHtml(contentText));
                } else
                {
                    textView.setText(contentText);
                }

                if (i == size - 1 && isRefundPolicy == false)
                {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    layoutParams.bottomMargin = 0;
                    textView.setLayoutParams(layoutParams);
                }

                contentsLayout.addView(textLayout);
            }
        }

        if (isRefundPolicy == true)
        {
            View textLayout = layoutInflater.inflate(R.layout.list_row_detail_text, contentsLayout, false);
            TextView textView = (TextView) textLayout.findViewById(R.id.textView);
            textView.setText(R.string.message_stay_detail_nrd);
            textView.setTextColor(mContext.getResources().getColor(R.color.default_text_cb70038));

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.bottomMargin = 0;
            textView.setLayoutParams(layoutParams);

            contentsLayout.addView(textLayout);
        }
    }

    /**
     * 문의하기
     *
     * @return
     */
    private View getConciergeView(LayoutInflater layoutInflater, StayDetailLayout.OnEventListener onEventListener)
    {
        View view = layoutInflater.inflate(R.layout.list_row_detail07, this, false);
        if (view == null)
        {
            return null;
        }

        view.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        TextView conciergeTimeTextView = (TextView) view.findViewById(R.id.conciergeTimeTextView);

        String[] hour = DailyPreference.getInstance(mContext).getOperationTime().split("\\,");

        String startHour = hour[0];
        String endHour = hour[1];

        String[] lunchTimes = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigOperationLunchTime().split("\\,");
        String startLunchTime = lunchTimes[0];
        String endLunchTime = lunchTimes[1];

        conciergeTimeTextView.setText(mContext.getString(R.string.message_consult02, startHour, endHour, startLunchTime, endLunchTime));

        View conciergeLayout = view.findViewById(R.id.conciergeLayout);
        conciergeLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener != null)
                {
                    onEventListener.onConciergeClick();
                }
            }
        });

        return view;
    }
}
