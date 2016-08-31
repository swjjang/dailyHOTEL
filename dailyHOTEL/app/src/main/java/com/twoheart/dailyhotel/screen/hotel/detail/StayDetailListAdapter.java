package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;
import java.util.List;

public class StayDetailListAdapter extends BaseAdapter
{
    private static final int NUMBER_OF_ROWSLIST = 7;

    private static final int GRID_COLUME_COUNT = 5;

    private StayDetail mStayDetail;
    private SaleTime mCheckInSaleTime;
    private Context mContext;
    private View[] mDeatilViews;
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
        mDeatilViews = new View[NUMBER_OF_ROWSLIST];
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
        if (mDeatilViews[0] == null)
        {
            mDeatilViews[0] = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);
        }

        getDetail00View(mDeatilViews[0]);
        linearLayout.addView(mDeatilViews[0]);

        // 호텔 등급과 이름.
        if (mDeatilViews[1] == null)
        {
            mDeatilViews[1] = layoutInflater.inflate(R.layout.list_row_stay_detail02, parent, false);
        }

        getTitleView(mDeatilViews[1], mStayDetail);
        linearLayout.addView(mDeatilViews[1]);

        // 주소 및 맵
        if (mDeatilViews[2] == null)
        {
            mDeatilViews[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
        }

        getAddressView(mDeatilViews[2], mStayDetail);
        linearLayout.addView(mDeatilViews[2]);

        ArrayList<StayDetail.Pictogram> list = mStayDetail.getPictogramList();

        if (list != null && list.size() > 0)
        {
            if (mDeatilViews[3] == null)
            {
                mDeatilViews[3] = layoutInflater.inflate(R.layout.list_row_detail_pictogram, parent, false);
            }

            getAmenitiesView(mDeatilViews[3], mStayDetail);
            linearLayout.addView(mDeatilViews[3]);
        }

        // D Benefit
        if (Util.isTextEmpty(mStayDetail.benefit) == false)
        {
            if (mDeatilViews[4] == null)
            {
                mDeatilViews[4] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
            }

            getDetailBenefitView(mDeatilViews[4], mStayDetail);
            linearLayout.addView(mDeatilViews[4]);
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
        if (mDeatilViews[5] == null)
        {
            mDeatilViews[5] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
        }

        getInformationView(layoutInflater, (ViewGroup) mDeatilViews[5], mStayDetail);
        linearLayout.addView(mDeatilViews[5]);

        // 카카오톡 문의
        if (mDeatilViews[6] == null)
        {
            mDeatilViews[6] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
        }

        getKakaoView(mDeatilViews[6]);
        linearLayout.addView(mDeatilViews[6]);

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
            satisfactionView.setText(mContext.getString(R.string.label_satisfaction, stayDetail.ratingValue, stayDetail.rate));
        }

        // 할인 쿠폰
        View couponLayout = view.findViewById(R.id.couponLayout);

        if (stayDetail.hasCoupon == true)
        {
            couponLayout.setVisibility(View.VISIBLE);

            View downloadCouponView = couponLayout.findViewById(R.id.downloadCouponView);

            downloadCouponView.setOnClickListener(new OnClickListener()
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

        com.facebook.drawee.view.SimpleDraweeView mapImageView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.mapImageView);

        double width = mImageHeight;
        double height = Util.dpToPx(mContext, 200);
        double ratio = height / width;

        if (width >= 640)
        {
            width = 640;
        }

        height = width * ratio;

        String size = String.format("%dx%d", (int) width * 4 / 5, (int) height * 4 / 5);
        String iconUrl = "http://s3.dailyhotel.kr/resources/images/_banner/googlemap_marker-1.png";
        String url = String.format("http://maps.googleapis.com/maps/api/staticmap?zoom=17&size=%s&markers=icon:%s|%s,%s&sensor=false&scale=2&format=png8&mobile=true&key=%s"//
            , size, iconUrl, mStayDetail.latitude, mStayDetail.longitude, DailyHotelRequest.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        mapImageView.setImageURI(Uri.parse(url));

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

        boolean isSingleLine = list == null || list.size() <= GRID_COLUME_COUNT ? true : false;

        for (StayDetail.Pictogram pictogram : list)
        {
            gridLayout.addView(getGridLayoutItemView(mContext, pictogram, isSingleLine));
        }

        int emptyCount = GRID_COLUME_COUNT - (list.size() % GRID_COLUME_COUNT);
        for (int i = 0; i < emptyCount; i++)
        {
            gridLayout.addView(getGridLayoutItemView(mContext, StayDetail.Pictogram.none, isSingleLine));
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
    private View getDetailBenefitView(View view, StayDetail stayDetail)
    {
        if (view == null || stayDetail == null)
        {
            return view;
        }

        final TextView benefitTextView = (TextView) view.findViewById(R.id.benefitTextView);

        final String benefit = stayDetail.benefit;

        benefitTextView.setText(benefit);
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

        ArrayList<DetailInformation> arrayList = stayDetail.getInformation();

        if (arrayList != null)
        {
            viewGroup.removeAllViews();

            for (DetailInformation information : arrayList)
            {
                ViewGroup childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                makeInformationLayout(layoutInflater, childGroup, information);

                viewGroup.addView(childGroup);
            }
        }

        return viewGroup;
    }

    /**
     * 카톡 실시간 상담
     *
     * @return
     */
    private View getKakaoView(View view)
    {
        if (view == null)
        {
            return null;
        }

        view.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        // 카톡 1:1 실시간 상담
        View consultKakaoView = view.findViewById(R.id.kakaoImageView);
        consultKakaoView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnEventListener != null)
                {
                    mOnEventListener.doKakaotalkConsult();
                }
            }
        });

        return view;
    }

    private void makeInformationLayout(LayoutInflater layoutInflater, ViewGroup viewGroup, DetailInformation information)
    {
        if (layoutInflater == null || viewGroup == null || information == null)
        {
            return;
        }

        LinearLayout contentsLayout = (LinearLayout) viewGroup.findViewById(R.id.contentsList);
        contentsLayout.removeAllViews();

        TextView titleTextView = (TextView) viewGroup.findViewById(R.id.titleTextView);
        titleTextView.setText(information.title);

        List<String> contentsList = information.getContentsList();

        if (contentsList != null)
        {
            int size = contentsList.size();

            for (int i = 0; i < size; i++)
            {
                View textLayout = layoutInflater.inflate(R.layout.list_row_detail_text, null, false);
                TextView textView = (TextView) textLayout.findViewById(R.id.textView);
                textView.setText(contentsList.get(i));

                contentsLayout.addView(textLayout);
            }
        }
    }
}