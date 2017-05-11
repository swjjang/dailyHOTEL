package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.DailyPreference;

import java.text.DecimalFormat;
import java.util.List;

public class GourmetDetailListAdapter extends BaseAdapter
{
    private static final int NUMBER_OF_ROWSLIST = 7;

    private static final int GRID_COLUMN_COUNT = 5;

    private GourmetDetail mGourmetDetail;
    private GourmetBookingDay mGourmetBookingDay;
    private PlaceReviewScores mPlaceReviewScores;
    private Context mContext;
    private View[] mDetailViews;
    protected View mGourmetTitleLayout;

    GourmetDetailLayout.OnEventListener mOnEventListener;
    private View.OnTouchListener mEmptyViewOnTouchListener;

    public GourmetDetailListAdapter(Context context, GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail //
        , PlaceReviewScores placeReviewScores//
        , GourmetDetailLayout.OnEventListener onEventListener//
        , View.OnTouchListener emptyViewOnTouchListener)
    {
        mContext = context;
        setData(gourmetBookingDay, gourmetDetail, placeReviewScores);
        mDetailViews = new View[NUMBER_OF_ROWSLIST];

        mOnEventListener = onEventListener;
        mEmptyViewOnTouchListener = emptyViewOnTouchListener;
    }

    public void setData(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail, PlaceReviewScores placeReviewScores)
    {
        mGourmetBookingDay = gourmetBookingDay;
        mGourmetDetail = gourmetDetail;
        mPlaceReviewScores = placeReviewScores;
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

    public View getTitleLayout()
    {
        return mGourmetTitleLayout;
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

        GourmetDetailParams gourmetDetailParams = mGourmetDetail.getGourmetDetailParmas();

        // 빈화면
        if (mDetailViews[0] == null)
        {
            mDetailViews[0] = layoutInflater.inflate(R.layout.list_row_detail01, parent, false);
            getEmptyView(mDetailViews[0]);
        }

        linearLayout.addView(mDetailViews[0]);

        // 레스토랑 등급과 이름.
        if (mDetailViews[1] == null)
        {
            mDetailViews[1] = layoutInflater.inflate(R.layout.list_row_gourmet_detail02, parent, false);
            getTitleView(mDetailViews[1], mGourmetDetail);
        }

        linearLayout.addView(mDetailViews[1]);

        // 주소 및 맵
        if (mDetailViews[2] == null)
        {
            mDetailViews[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
            getAddressView(mDetailViews[2], mGourmetDetail);
        }

        linearLayout.addView(mDetailViews[2]);

        List<GourmetDetail.Pictogram> pictogramList = mGourmetDetail.getPictogramList();

        if (pictogramList != null && pictogramList.size() > 0)
        {
            if (mDetailViews[3] == null)
            {
                mDetailViews[3] = layoutInflater.inflate(R.layout.list_row_detail_pictogram, parent, false);
                getAmenitiesView(mDetailViews[3], mGourmetDetail);
            }

            linearLayout.addView(mDetailViews[3]);
        }

        if (DailyTextUtils.isTextEmpty(gourmetDetailParams.benefit) == false)
        {
            // D Benefit
            if (mDetailViews[4] == null)
            {
                mDetailViews[4] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
                getBenefitView(layoutInflater, mDetailViews[4], mGourmetDetail);
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

        // 정보
        if (mDetailViews[5] == null)
        {
            mDetailViews[5] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
            getInformationView(layoutInflater, (ViewGroup) mDetailViews[5], mGourmetDetail);
        }

        linearLayout.addView(mDetailViews[5]);

        // 문의 하기
        if (mDetailViews[6] == null)
        {
            mDetailViews[6] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
            getConciergeView(mDetailViews[6]);
        }

        linearLayout.addView(mDetailViews[6]);

        return linearLayout;
    }

    /**
     * 빈화면
     *
     * @param view
     * @return
     */
    private View getEmptyView(View view)
    {
        View emptyView = view.findViewById(R.id.imageEmptyHeight);
        emptyView.getLayoutParams().height = PlaceDetailLayout.getImageLayoutHeight(mContext);

        emptyView.setClickable(true);
        emptyView.setOnTouchListener(mEmptyViewOnTouchListener);

        return view;
    }

    /**
     * 등급 및 이름
     *
     * @param view
     * @return
     */
    private View getTitleView(View view, final GourmetDetail gourmetDetail)
    {
        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        mGourmetTitleLayout = view.findViewById(R.id.gourmetTitleLayout);

        // 등급
        TextView gradeTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.gourmetGradeTextView);

        if (DailyTextUtils.isTextEmpty(gourmetDetailParams.category) == true)
        {
            gradeTextView.setVisibility(View.GONE);
        } else
        {
            gradeTextView.setVisibility(View.VISIBLE);
            gradeTextView.setText(gourmetDetailParams.category);
        }

        // 소분류 등급
        TextView subGradeTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.gourmetSubGradeTextView);

        if (DailyTextUtils.isTextEmpty(gourmetDetailParams.categorySub) == true)
        {
            subGradeTextView.setVisibility(View.GONE);
        } else
        {
            subGradeTextView.setVisibility(View.VISIBLE);
            subGradeTextView.setText(gourmetDetailParams.categorySub);
        }

        // 호텔명
        TextView placeNameTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.gourmetNameTextView);
        placeNameTextView.setText(gourmetDetailParams.name);

        // 만족도
        TextView satisfactionView = (TextView) mGourmetTitleLayout.findViewById(R.id.satisfactionView);

        if (gourmetDetailParams.ratingShow == false)
        {
            satisfactionView.setVisibility(View.GONE);
        } else
        {
            satisfactionView.setVisibility(View.VISIBLE);
            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
            satisfactionView.setText(mContext.getString(R.string.label_gourmet_detail_satisfaction, //
                gourmetDetailParams.ratingValue, decimalFormat.format(gourmetDetailParams.ratingPersons)));
        }

        // 리뷰
        TextView trueReviewTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.trueReviewTextView);

        if (mPlaceReviewScores == null)
        {
            trueReviewTextView.setVisibility(View.GONE);
        } else
        {
            setTrueReviewCount(mPlaceReviewScores.reviewScoreTotalCount);
        }

        // 할인 쿠폰
        View couponLayout = view.findViewById(R.id.couponLayout);

        if (gourmetDetail.hasCoupon == true)
        {
            couponLayout.setVisibility(View.VISIBLE);

            View downloadCouponLayout = couponLayout.findViewById(R.id.downloadCouponLayout);

            downloadCouponLayout.setOnClickListener(new View.OnClickListener()
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

        // 날짜
        TextView dayTextView = (TextView) view.findViewById(R.id.dayTextView);

        dayTextView.setText(mGourmetBookingDay.getVisitDay("yyyy.MM.dd(EEE)"));

        View dateInformationLayout = view.findViewById(R.id.dateInformationLayout);
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
        if (mGourmetTitleLayout == null)
        {
            return;
        }

        TextView trueReviewTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.trueReviewTextView);

        if (count == 0)
        {
            trueReviewTextView.setVisibility(View.GONE);
        } else
        {
            trueReviewTextView.setVisibility(View.VISIBLE);
            trueReviewTextView.setText(mContext.getString(R.string.label_detail_view_review_go, count));
            trueReviewTextView.setOnClickListener(new View.OnClickListener()
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
     * 주소 및 맵
     *
     * @param view
     * @param gourmetDetail
     * @return
     */
    private View getAddressView(final View view, GourmetDetail gourmetDetail)
    {
        view.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        // 주소지
        final TextView hotelAddressTextView = (TextView) view.findViewById(R.id.detailAddressTextView);

        final String address = gourmetDetailParams.address;
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
        navigatorView.setOnClickListener(new View.OnClickListener()
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
        mapImageView.setOnClickListener(new View.OnClickListener()
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
    private View getAmenitiesView(View view, GourmetDetail gourmetDetail)
    {
        if (view == null || gourmetDetail == null)
        {
            return view;
        }

        android.support.v7.widget.GridLayout gridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.amenitiesGridLayout);
        gridLayout.removeAllViews();

        List<GourmetDetail.Pictogram> pictogramList = gourmetDetail.getPictogramList();

        boolean isSingleLine = pictogramList == null || pictogramList.size() <= GRID_COLUMN_COUNT;

        for (GourmetDetail.Pictogram pictogram : pictogramList)
        {
            gridLayout.addView(getGridLayoutItemView(mContext, pictogram, isSingleLine));
        }

        int columnCount = pictogramList.size() % GRID_COLUMN_COUNT;

        if (columnCount != 0)
        {
            int addEmptyViewCount = GRID_COLUMN_COUNT - columnCount;
            for (int i = 0; i < addEmptyViewCount; i++)
            {
                gridLayout.addView(getGridLayoutItemView(mContext, GourmetDetail.Pictogram.none, isSingleLine));
            }
        }
        return view;
    }

    protected DailyTextView getGridLayoutItemView(Context context, GourmetDetail.Pictogram pictogram, boolean isSingleLine)
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
     * 고메 Benefit
     *
     * @param view
     * @return
     */
    private View getBenefitView(LayoutInflater layoutInflater, View view, GourmetDetail gourmetDetail)
    {
        if (view == null || gourmetDetail == null)
        {
            return view;
        }

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        final TextView benefitTitleTextView = (TextView) view.findViewById(R.id.benefitTitleTextView);
        final LinearLayout benefitMessagesLayout = (LinearLayout) view.findViewById(R.id.benefitMessagesLayout);

        final String benefit = gourmetDetailParams.benefit;
        benefitTitleTextView.setText(benefit);

        List<String> mBenefitInformation = gourmetDetail.getBenefitList();

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
     * @return
     */
    private View getInformationView(LayoutInflater layoutInflater, ViewGroup viewGroup, PlaceDetail placeDetail)
    {
        if (layoutInflater == null || viewGroup == null || placeDetail == null)
        {
            return viewGroup;
        }

        List<DetailInformation> detailInformationList = placeDetail.getDetailList();

        if (detailInformationList != null)
        {
            viewGroup.removeAllViews();

            for (DetailInformation detailInformation : detailInformationList)
            {
                ViewGroup childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                makeInformationLayout(layoutInflater, childGroup, detailInformation);

                viewGroup.addView(childGroup);
            }
        }

        return viewGroup;
    }

    /**
     * 문의 상담
     *
     * @param view
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

        String[] lunchTimes = DailyPreference.getInstance(mContext).getRemoteConfigOperationLunchTime().split("\\,");
        String startLunchTime = lunchTimes[0];
        String endLunchTime = lunchTimes[1];

        conciergeTimeTextView.setText(mContext.getString(R.string.message_consult02, startHour, endHour, startLunchTime, endLunchTime));

        View conciergeLayout = view.findViewById(R.id.conciergeLayout);
        conciergeLayout.setOnClickListener(new View.OnClickListener()
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
    }
}