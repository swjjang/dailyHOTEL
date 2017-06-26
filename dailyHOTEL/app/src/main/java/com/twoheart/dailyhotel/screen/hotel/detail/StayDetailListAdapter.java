package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;

import java.text.DecimalFormat;
import java.util.List;

public class StayDetailListAdapter extends BaseAdapter
{
    private static final int NUMBER_OF_ROWSLIST = 7;

    private static final int GRID_COLUMN_COUNT = 5;

    private StayDetail mStayDetail;
    private StayBookingDay mStayBookingDay;
    private PlaceReviewScores mPlaceReviewScores;
    private Context mContext;
    private View[] mDetailViews;
    private View mHotelTitleLayout;

    StayDetailLayout.OnEventListener mOnEventListener;
    private View.OnTouchListener mEmptyViewOnTouchListener;

    public StayDetailListAdapter(Context context, StayBookingDay stayBookingDay, StayDetail stayDetail//
        , PlaceReviewScores placeReviewScores//
        , StayDetailLayout.OnEventListener onEventListener//
        , View.OnTouchListener emptyViewOnTouchListener)
    {
        mDetailViews = new View[NUMBER_OF_ROWSLIST];

        mContext = context;
        setData(stayBookingDay, stayDetail, placeReviewScores);

        mOnEventListener = onEventListener;
        mEmptyViewOnTouchListener = emptyViewOnTouchListener;
    }

    public void setData(StayBookingDay stayBookingDay, StayDetail stayDetail, PlaceReviewScores placeReviewScores)
    {
        mStayBookingDay = stayBookingDay;
        mStayDetail = stayDetail;
        mPlaceReviewScores = placeReviewScores;

        if (mDetailViews != null)
        {
            for (int i = 0; i < NUMBER_OF_ROWSLIST; i++)
            {
                mDetailViews[i] = null;
            }
        }
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCount()
    {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout linearLayout;

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
        {
            linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        } else
        {
            linearLayout = (LinearLayout) convertView;
        }

        linearLayout.removeAllViews();

        StayDetailParams stayDetailParams = mStayDetail.getStayDetailParams();

        // 빈화면
        if (mDetailViews[0] == null)
        {
            mDetailViews[0] = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);
            getDetail00View(mDetailViews[0]);
        }

        linearLayout.addView(mDetailViews[0]);

        // 호텔 등급과 이름.
        if (mDetailViews[1] == null)
        {
            mDetailViews[1] = layoutInflater.inflate(R.layout.list_row_stay_detail02, parent, false);
            getTitleView(mDetailViews[1], mStayDetail);
        }

        linearLayout.addView(mDetailViews[1]);

        // 주소 및 맵
        if (mDetailViews[2] == null)
        {
            mDetailViews[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
            getAddressView(mDetailViews[2], mStayDetail);
        }

        linearLayout.addView(mDetailViews[2]);

        List<StayDetail.Pictogram> pictogramList = mStayDetail.getPictogramList();

        if (pictogramList != null && pictogramList.size() > 0)
        {
            if (mDetailViews[3] == null)
            {
                mDetailViews[3] = layoutInflater.inflate(R.layout.list_row_detail_pictogram, parent, false);
                getAmenitiesView(mDetailViews[3], mStayDetail);
            }

            linearLayout.addView(mDetailViews[3]);
        }

        // D Benefit
        if (DailyTextUtils.isTextEmpty(stayDetailParams.benefit) == false)
        {
            if (mDetailViews[4] == null)
            {
                mDetailViews[4] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
                getDetailBenefitView(layoutInflater, mDetailViews[4], mStayDetail);
            }

            linearLayout.addView(mDetailViews[4]);
        } else
        {
            // 베네핏이 없으면 정보화면의 상단 라인으로 대체한다.
            View view = new View(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(mContext, 1));
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(R.color.default_line_cf0f0f0);
            linearLayout.addView(view);
        }

        // 정보 화면
        if (mDetailViews[5] == null)
        {
            mDetailViews[5] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
            getInformationView(layoutInflater, (ViewGroup) mDetailViews[5], mStayDetail);
        }

        linearLayout.addView(mDetailViews[5]);

        // 카카오톡 문의
        if (mDetailViews[6] == null)
        {
            mDetailViews[6] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
            getConciergeView(mDetailViews[6]);
        }

        linearLayout.addView(mDetailViews[6]);

        return linearLayout;
    }

    public View getTitleLayout()
    {
        return mHotelTitleLayout;
    }

    /**
     * 빈화면
     *
     * @param view
     * @return
     */
    private View getDetail00View(View view)
    {
        View emptyView = view.findViewById(R.id.imageEmptyHeight);
        emptyView.getLayoutParams().height = PlaceDetailLayout.getImageLayoutHeight(mContext);

        emptyView.setClickable(true);
        emptyView.setOnTouchListener(mEmptyViewOnTouchListener);

        return view;
    }

    /**
     * 호텔 등급 및 이름
     *
     * @param view
     * @param stayDetail
     * @return
     */
    private View getTitleView(View view, StayDetail stayDetail)
    {
        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

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

        if (stayDetailParams.ratingValue == 0)
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
                    mOnEventListener.onDownloadCouponClick();
                }
            });
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        // Stamp
        View stampLayout = view.findViewById(R.id.stampLayout);
        View stampClickLayout = stampLayout.findViewById(R.id.stampClickLayout);

        if (DailyRemoteConfigPreference.getInstance(mContext).isRemoteConfigStampEnabled() == true)
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
                        mOnEventListener.onStampClick();
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
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.onCalendarClick();
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
                    mOnEventListener.onReviewClick();
                }
            });
        }
    }

    /**
     * 호텔 주소 및 맵
     *
     * @param view
     * @param stayDetail
     * @return
     */
    private View getAddressView(final View view, StayDetail stayDetail)
    {
        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

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
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.clipAddress(address);
            }
        });

        //길찾기
        View navigatorView = view.findViewById(R.id.navigatorView);
        navigatorView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener == null)
                {
                    return;
                }

                mOnEventListener.showNavigatorDialog();
            }
        });

        ImageView mapImageView = (ImageView) view.findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener != null)
                {
                    mOnEventListener.showMap();
                }
            }
        });

        return view;
    }

    /**
     * 편의시설
     *
     * @param view
     * @return
     */
    private View getAmenitiesView(View view, StayDetail stayDetail)
    {
        if (view == null || stayDetail == null)
        {
            return view;
        }

        android.support.v7.widget.GridLayout gridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.amenitiesGridLayout);
        gridLayout.removeAllViews();

        List<StayDetail.Pictogram> pictogramList = stayDetail.getPictogramList();

        boolean isSingleLine = pictogramList == null || pictogramList.size() <= GRID_COLUMN_COUNT;

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
     * @param view
     * @return
     */
    private View getDetailBenefitView(LayoutInflater layoutInflater, View view, StayDetail stayDetail)
    {
        if (view == null || stayDetail == null)
        {
            return view;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        final TextView benefitTitleTextView = (TextView) view.findViewById(R.id.benefitTitleTextView);
        final LinearLayout benefitMessagesLayout = (LinearLayout) view.findViewById(R.id.benefitMessagesLayout);

        final String benefit = stayDetailParams.benefit;
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
     * @param viewGroup
     * @param stayDetail
     * @return
     */
    private View getInformationView(LayoutInflater layoutInflater, ViewGroup viewGroup, StayDetail stayDetail)
    {
        if (layoutInflater == null || viewGroup == null || stayDetail == null || stayDetail.getProductList() == null)
        {
            return viewGroup;
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

            ViewGroup childGroup = null;
            for (DetailInformation information : detailInformationList)
            {
                childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                makeInformationLayout(layoutInflater, childGroup, information, hasNRD);

                viewGroup.addView(childGroup);

                if (hasNRD == true && mContext.getString(R.string.label_detail_cancellation_refund_policy).equalsIgnoreCase(information.title) == true)
                {
                    hasRefundPolicy = true;
                }
            }

            // 서버에서 타이틀이 취소및 환불 규정이 없는 경우가 발생하는 경우가 있어서 관련 내용 처리
            if (hasNRD == true && hasRefundPolicy == false)
            {
                childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                DetailInformation detailInformation = new DetailInformation(mContext.getString(R.string.label_detail_cancellation_refund_policy), null);

                makeInformationLayout(layoutInflater, childGroup, detailInformation, hasNRD);

                viewGroup.addView(childGroup);
            }

            String gradeString = stayDetail.getStayDetailParams().getGrade().name();

            if (Stay.Grade.fullvilla.name().equalsIgnoreCase(gradeString) == true //
                || Stay.Grade.pension.name().equalsIgnoreCase(gradeString) == true)
            {
                View pensionOnlyLayout = layoutInflater.inflate(R.layout.list_row_detail_pension_only, viewGroup, false);
                viewGroup.addView(pensionOnlyLayout);

                if (childGroup != null)
                {
                    View lastContentView = childGroup.findViewById(R.id.contentsList);
                    if (lastContentView != null)
                    {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lastContentView.getLayoutParams();
                        layoutParams.bottomMargin = layoutParams.bottomMargin - ScreenUtils.dpToPx(mContext, 14d);
                        lastContentView.setLayoutParams(layoutParams);
                    }
                }

            }
        }

        return viewGroup;
    }

    /**
     * 문의하기
     *
     * @return
     */
    private View getConciergeView(View view)
    {
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
                if (mOnEventListener != null)
                {
                    mOnEventListener.onConciergeClick();
                }
            }
        });

        return view;
    }

    private void makeInformationLayout(LayoutInflater layoutInflater, ViewGroup viewGroup, DetailInformation information, boolean hasNRD)
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

                View textLayout = layoutInflater.inflate(R.layout.list_row_detail_text, null, false);
                TextView textView = (TextView) textLayout.findViewById(R.id.textView);
                textView.setText(contentText);

                contentsLayout.addView(textLayout);
            }
        }

        if (isRefundPolicy == true)
        {
            View textLayout = layoutInflater.inflate(R.layout.list_row_detail_text, null, false);
            TextView textView = (TextView) textLayout.findViewById(R.id.textView);
            textView.setText(R.string.message_stay_detail_nrd);
            textView.setTypeface(FontManager.getInstance(mContext).getMediumTypeface());
            textView.setTextColor(mContext.getResources().getColor(R.color.default_text_cb70038));

            contentsLayout.addView(textLayout);
        }
    }
}