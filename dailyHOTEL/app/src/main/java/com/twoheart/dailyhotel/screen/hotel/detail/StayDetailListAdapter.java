package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.graphics.Typeface;
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

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.RoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.FontManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StayDetailListAdapter extends BaseAdapter
{
    private static final int NUMBER_OF_ROWSLIST = 7;

    private static final int GRID_COLUMN_COUNT = 5;

    private StayDetail mStayDetail;
    private SaleTime mCheckInSaleTime;
    private Context mContext;
    private View[] mDetailViews;
    private int mImageHeight;
    private View mHotelTitleLayout;

    private StayDetailLayout.OnEventListener mOnEventListener;
    private View.OnTouchListener mEmptyViewOnTouchListener;

    public StayDetailListAdapter(Context context, SaleTime saleTime, StayDetail stayDetail,//
                                 StayDetailLayout.OnEventListener onEventListener,//
                                 View.OnTouchListener emptyViewOnTouchListener)
    {
        mContext = context;
        mCheckInSaleTime = saleTime;
        mStayDetail = stayDetail;
        mDetailViews = new View[NUMBER_OF_ROWSLIST];
        mImageHeight = Util.getLCDWidth(mContext);

        mOnEventListener = onEventListener;
        mEmptyViewOnTouchListener = emptyViewOnTouchListener;
    }

    public void setData(StayDetail stayDetail, SaleTime checkInSaleTime)
    {
        mStayDetail = stayDetail;
        mCheckInSaleTime = checkInSaleTime;
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

        // 빈화면
        if (mDetailViews[0] == null)
        {
            mDetailViews[0] = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);
        }

        getDetail00View(mDetailViews[0]);
        linearLayout.addView(mDetailViews[0]);

        // 호텔 등급과 이름.
        if (mDetailViews[1] == null)
        {
            mDetailViews[1] = layoutInflater.inflate(R.layout.list_row_stay_detail02, parent, false);
        }

        getTitleView(mDetailViews[1], mStayDetail);
        linearLayout.addView(mDetailViews[1]);

        // 주소 및 맵
        if (mDetailViews[2] == null)
        {
            mDetailViews[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
        }

        getAddressView(mDetailViews[2], mStayDetail);
        linearLayout.addView(mDetailViews[2]);

        ArrayList<StayDetail.Pictogram> list = mStayDetail.getPictogramList();

        if (list != null && list.size() > 0)
        {
            if (mDetailViews[3] == null)
            {
                mDetailViews[3] = layoutInflater.inflate(R.layout.list_row_detail_pictogram, parent, false);
            }

            getAmenitiesView(mDetailViews[3], mStayDetail);
            linearLayout.addView(mDetailViews[3]);
        }

        // D Benefit
        if (Util.isTextEmpty(mStayDetail.benefit) == false)
        {
            if (mDetailViews[4] == null)
            {
                mDetailViews[4] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
            }

            getDetailBenefitView(layoutInflater, mDetailViews[4], mStayDetail);
            linearLayout.addView(mDetailViews[4]);
        } else
        {
            // 베네핏이 없으면 정보화면의 상단 라인으로 대체한다.
            View view = new View(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dpToPx(mContext, 1));
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(R.color.default_line_cf0f0f0);
            linearLayout.addView(view);
        }

        // 정보 화면
        if (mDetailViews[5] == null)
        {
            mDetailViews[5] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
        }

        getInformationView(layoutInflater, (ViewGroup) mDetailViews[5], mStayDetail);
        linearLayout.addView(mDetailViews[5]);

        // 카카오톡 문의
        if (mDetailViews[6] == null)
        {
            mDetailViews[6] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
        }

        getConciergeView(mDetailViews[6]);
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
        emptyView.getLayoutParams().height = mImageHeight;

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
        mHotelTitleLayout = view.findViewById(R.id.hotelTitleLayout);

        // 등급
        TextView hotelGradeTextView = (TextView) view.findViewById(R.id.hotelGradeTextView);
        hotelGradeTextView.setVisibility(View.VISIBLE);

        hotelGradeTextView.setText(stayDetail.grade.getName(mContext));
        hotelGradeTextView.setBackgroundResource(stayDetail.grade.getColorResId());

        // 호텔명
        TextView hotelNameTextView = (TextView) view.findViewById(R.id.hotelNameTextView);
        hotelNameTextView.setText(stayDetail.name);

        // 만족도
        TextView satisfactionView = (TextView) view.findViewById(R.id.satisfactionView);

        if (stayDetail.ratingValue == 0)
        {
            satisfactionView.setVisibility(View.GONE);
        } else
        {
            satisfactionView.setVisibility(View.VISIBLE);
            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
            satisfactionView.setText(mContext.getString(R.string.label_stay_detail_satisfaction, //
                stayDetail.ratingValue, decimalFormat.format(stayDetail.ratingPersons)));
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
                    mOnEventListener.downloadCoupon();
                }
            });
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        // 날짜
        View dateInformationLayout = view.findViewById(R.id.dateInformationLayout);
        TextView checkinDayTextView = (TextView) dateInformationLayout.findViewById(R.id.checkinDayTextView);
        TextView checkoutDayTextView = (TextView) dateInformationLayout.findViewById(R.id.checkoutDayTextView);
        TextView nightsTextView = (TextView) dateInformationLayout.findViewById(R.id.nightsTextView);

        // 체크인체크아웃 날짜
        final String checkInDate = mCheckInSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
        SaleTime checkOutSaletime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + mStayDetail.nights);
        String checkOutDate = checkOutSaletime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

        checkinDayTextView.setText(checkInDate);
        checkoutDayTextView.setText(checkOutDate);
        nightsTextView.setText(mContext.getString(R.string.label_nights, mStayDetail.nights));

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

    /**
     * 호텔 주소 및 맵
     *
     * @param view
     * @param stayDetail
     * @return
     */
    private View getAddressView(final View view, StayDetail stayDetail)
    {
        view.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        // 주소지
        final TextView hotelAddressTextView = (TextView) view.findViewById(R.id.detailAddressTextView);

        final String address = stayDetail.address;
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

        ArrayList<StayDetail.Pictogram> list = stayDetail.getPictogramList();

        boolean isSingleLine = list == null || list.size() <= GRID_COLUMN_COUNT ? true : false;

        for (StayDetail.Pictogram pictogram : list)
        {
            gridLayout.addView(getGridLayoutItemView(mContext, pictogram, isSingleLine));
        }

        int columnCount = list.size() % GRID_COLUMN_COUNT;

        if (columnCount != 0)
        {
            int addEmptyViewCount = GRID_COLUMN_COUNT - columnCount;
            for (int i = 0; i < addEmptyViewCount; i++)
            {
                gridLayout.addView(getGridLayoutItemView(mContext, StayDetail.Pictogram.none, isSingleLine));
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
            dailyTextView.setPadding(0, Util.dpToPx(context, 10), 0, Util.dpToPx(context, 15));
        } else
        {
            dailyTextView.setPadding(0, Util.dpToPx(context, 10), 0, Util.dpToPx(context, 2));
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

        final TextView benefitTitleTextView = (TextView) view.findViewById(R.id.benefitTitleTextView);
        final LinearLayout benefitMessagesLayout = (LinearLayout) view.findViewById(R.id.benefitMessagesLayout);

        final String benefit = stayDetail.benefit;
        benefitTitleTextView.setText(benefit);

        List<String> mBenefitInformation = stayDetail.getBenefitInformation();

        if (mBenefitInformation != null)
        {
            benefitMessagesLayout.removeAllViews();

            for (String information : mBenefitInformation)
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
        if (layoutInflater == null || viewGroup == null || stayDetail == null)
        {
            return viewGroup;
        }

        boolean hasNRD = false;
        for (RoomInformation roomInformation : stayDetail.getSaleRoomList())
        {
            if (roomInformation.isNRD == true)
            {
                hasNRD = true;
                break;
            }
        }

        ArrayList<DetailInformation> arrayList = stayDetail.getInformation();

        if (arrayList != null)
        {
            viewGroup.removeAllViews();
            boolean hasRefundPolicy = false;

            for (DetailInformation information : arrayList)
            {
                ViewGroup childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

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
                ViewGroup childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                DetailInformation detailInformation = new DetailInformation(mContext.getString(R.string.label_detail_cancellation_refund_policy), null);

                makeInformationLayout(layoutInflater, childGroup, detailInformation, hasNRD);

                viewGroup.addView(childGroup);
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

        conciergeTimeTextView.setText(mContext.getString(R.string.message_consult02, startHour, endHour));

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

                if (Util.isTextEmpty(contentText) == true)
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