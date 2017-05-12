package com.daily.dailyhotel.screen.stay.outbound.detail;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundDetailDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail01DataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail02DataBinding;
import com.twoheart.dailyhotel.widget.AlphaTransition;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.TextTransition;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class StayOutboundDetailView extends BaseView<StayOutboundDetailView.OnEventListener, ActivityStayOutboundDetailDataBinding>//
    implements StayOutboundDetailViewInterface, View.OnClickListener
{
    private DailyToolbarLayout mDailyToolbarLayout;


    public interface OnEventListener extends OnBaseEventListener
    {
        void onImageClick();

        void onReviewClick();

        void onCalendarClick();

        void onDownloadCouponClick();

        void onMapClick();

        void onClipAddressClick(String address);

        void onNavigatorClick();

        void onWishClick();

        void onConciergeClick();

        void onBookingClick();
    }

    public StayOutboundDetailView(BaseActivity baseActivity, StayOutboundDetailView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityStayOutboundDetailDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }


    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }


    @Override
    public void onClick(View v)
    {

    }

    @Override
    public void showRoomList()
    {

    }

    @Override
    public void hideRoomList()
    {

    }

    @Override
    public void setStayDetail(StayBookDateTime stayBookDateTime, StayOutboundDetail stayOutboundDetail)
    {
        if(getViewDataBinding() == null || stayBookDateTime == null || stayOutboundDetail == null)
        {
            return;
        }

        getViewDataBinding().scrollLayout.removeAllViews();

        // 이미지 상단에 빈화면 넣기
        LayoutStayOutboundDetail01DataBinding detail01DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_detail_01_data, getViewDataBinding().scrollLayout, true);


        detail01DataBinding.imageEmptyView.getLayoutParams().height = getImageLayoutHeight(getContext());
        detail01DataBinding.imageEmptyView.setClickable(true);
        detail01DataBinding.imageEmptyView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return false;
            }
        });

        LayoutStayOutboundDetail02DataBinding detail02DataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.layout_stay_outbound_detail_02_data, getViewDataBinding().scrollLayout, true);

        setTitleView(detail02DataBinding, stayBookDateTime, stayOutboundDetail);
//
//
//                // 호텔 등급과 이름.
//                if (mDetailViews[1] == null)
//                {
//                    mDetailViews[1] = layoutInflater.inflate(R.layout.list_row_stay_detail02, parent, false);
//                }
//
//                getTitleView(mDetailViews[1], mStayDetail);
//                linearLayout.addView(mDetailViews[1]);
//
//                // 주소 및 맵
//                if (mDetailViews[2] == null)
//                {
//                    mDetailViews[2] = layoutInflater.inflate(R.layout.list_row_detail03, parent, false);
//                }
//
//                getAddressView(mDetailViews[2], mStayDetail);
//                linearLayout.addView(mDetailViews[2]);
//
//                List<StayDetail.Pictogram> pictogramList = mStayDetail.getPictogramList();
//
//                if (pictogramList != null && pictogramList.size() > 0)
//                {
//                    if (mDetailViews[3] == null)
//                    {
//                        mDetailViews[3] = layoutInflater.inflate(R.layout.list_row_detail_pictogram, parent, false);
//                    }
//
//                    getAmenitiesView(mDetailViews[3], mStayDetail);
//                    linearLayout.addView(mDetailViews[3]);
//                }
//
//                // D Benefit
//                if (DailyTextUtils.isTextEmpty(stayDetailParams.benefit) == false)
//                {
//                    if (mDetailViews[4] == null)
//                    {
//                        mDetailViews[4] = layoutInflater.inflate(R.layout.list_row_detail_benefit, parent, false);
//                    }
//
//                    getDetailBenefitView(layoutInflater, mDetailViews[4], mStayDetail);
//                    linearLayout.addView(mDetailViews[4]);
//                } else
//                {
//                    // 베네핏이 없으면 정보화면의 상단 라인으로 대체한다.
//                    View view = new View(mContext);
//                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(mContext, 1));
//                    view.setLayoutParams(layoutParams);
//                    view.setBackgroundResource(R.color.default_line_cf0f0f0);
//                    linearLayout.addView(view);
//                }
//
//                // 정보 화면
//                if (mDetailViews[5] == null)
//                {
//                    mDetailViews[5] = layoutInflater.inflate(R.layout.list_row_detail04, parent, false);
//                }
//
//                getInformationView(layoutInflater, (ViewGroup) mDetailViews[5], mStayDetail);
//                linearLayout.addView(mDetailViews[5]);
//
//                // 카카오톡 문의
//                if (mDetailViews[6] == null)
//                {
//                    mDetailViews[6] = layoutInflater.inflate(R.layout.list_row_detail07, parent, false);
//                }
//
//                getConciergeView(mDetailViews[6]);
//                linearLayout.addView(mDetailViews[6]);
    }


    @TargetApi(value = 21)
    @Override
    public Observable<Boolean> getSharedElementTransition()
    {
        TransitionSet inTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
        Transition inTextTransition = new TextTransition(getColor(R.color.white), getColor(R.color.default_text_c323232)//
            , 17, 18, new LinearInterpolator());
        inTextTransition.addTarget(getString(R.string.transition_place_name));
        inTransitionSet.addTransition(inTextTransition);

        Transition inBottomAlphaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
        inBottomAlphaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
        inTransitionSet.addTransition(inBottomAlphaTransition);

        Transition inTopAlphaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
        inTopAlphaTransition.addTarget(getString(R.string.transition_gradient_top_view));
        inTransitionSet.addTransition(inTopAlphaTransition);

        getWindow().setSharedElementEnterTransition(inTransitionSet);

        TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
        Transition outTextTransition = new TextTransition(getColor(R.color.default_text_c323232), getColor(R.color.white)//
            , 18, 17, new LinearInterpolator());
        outTextTransition.addTarget(getString(R.string.transition_place_name));
        outTransitionSet.addTransition(outTextTransition);

        Transition outBottomAlphaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
        outBottomAlphaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
        outTransitionSet.addTransition(outBottomAlphaTransition);

        Transition outTopAlphaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
        outTopAlphaTransition.addTarget(getString(R.string.transition_gradient_top_view));
        outTransitionSet.addTransition(outTopAlphaTransition);

        outTransitionSet.setDuration(200);

        getWindow().setSharedElementReturnTransition(outTransitionSet);

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener()
                {
                    @Override
                    public void onTransitionStart(Transition transition)
                    {
                    }

                    @Override
                    public void onTransitionEnd(Transition transition)
                    {
                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onTransitionCancel(Transition transition)
                    {
                    }

                    @Override
                    public void onTransitionPause(Transition transition)
                    {
                    }

                    @Override
                    public void onTransitionResume(Transition transition)
                    {
                    }
                });
            }
        };

        return observable;
    }

    public static int getImageLayoutHeight(Context context)
    {
        return ScreenUtils.getRatioHeightType4x3(ScreenUtils.getScreenWidth(context));
    }

    /**
     * 호텔 등급 및 이름
     *
     * @param viewDataBinding
     * @param stayBookDateTime
     * @param stayOutboundDetail
     */
    private void setTitleView(LayoutStayOutboundDetail02DataBinding viewDataBinding, StayBookDateTime stayBookDateTime, StayOutboundDetail stayOutboundDetail)
    {
        if(viewDataBinding == null || stayBookDateTime == null || stayOutboundDetail == null)
        {
            return;
        }

        // 등급
        viewDataBinding.gradeTextView.setVisibility(View.VISIBLE);
        viewDataBinding.gradeTextView.setText(getString(R.string.label_stayoutbound_detail_grade, stayOutboundDetail.grade));
        viewDataBinding.gradeTextView.setBackgroundResource(R.color.default_background_c929292);

        // 호텔명
        viewDataBinding.nameTextView.setText(stayOutboundDetail.name);

        // 만족도
//        if (DailyTextUtils.isTextEmpty(stayOutboundDetail.ratingValue) == true)
//        {
//            viewDataBinding.satisfactionView.setVisibility(View.GONE);
//        } else
//        {
//            viewDataBinding.satisfactionView.setVisibility(View.VISIBLE);
//            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
//            satisfactionView.setText(mContext.getString(R.string.label_stay_detail_satisfaction, //
//                stayDetailParams.ratingValue, decimalFormat.format(stayDetailParams.ratingPersons)));
//        }
//
//        // 리뷰
//        TextView trueReviewTextView = (TextView) mHotelTitleLayout.findViewById(R.id.trueReviewTextView);
//
//        if (mPlaceReviewScores == null)
//        {
//            trueReviewTextView.setVisibility(View.GONE);
//        } else
//        {
//            setTrueReviewCount(mPlaceReviewScores.reviewScoreTotalCount);
//        }
//
//        // 할인 쿠폰
//        View couponLayout = view.findViewById(R.id.couponLayout);
//
//        if (stayDetail.hasCoupon == true)
//        {
//            couponLayout.setVisibility(View.VISIBLE);
//
//            View downloadCouponLayout = couponLayout.findViewById(R.id.downloadCouponLayout);
//
//            downloadCouponLayout.setOnClickListener(new OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    mOnEventListener.onDownloadCouponClick();
//                }
//            });
//        } else
//        {
//            couponLayout.setVisibility(View.GONE);
//        }
//
//        // Stamp
//        View stampLayout = view.findViewById(R.id.stampLayout);
//        View stampClickLayout = stampLayout.findViewById(R.id.stampClickLayout);
//
//        if (DailyPreference.getInstance(mContext).isRemoteConfigStampEnabled() == true)
//        {
//            // 테블릿 높이 수정 필요한지 확인
//            stampLayout.setVisibility(View.VISIBLE);
//
//            TextView stampMessage1TextView = (TextView) stampLayout.findViewById(R.id.stampMessage1TextView);
//            TextView stampMessage2TextView = (TextView) stampLayout.findViewById(R.id.stampMessage2TextView);
//            TextView stampMessage3TextView = (TextView) stampLayout.findViewById(R.id.stampMessage3TextView);
//
//            String message1 = DailyPreference.getInstance(mContext).getRemoteConfigStampStayDetailMessage1();
//            String message2 = DailyPreference.getInstance(mContext).getRemoteConfigStampStayDetailMessage2();
//
//            boolean message3Enabled = DailyPreference.getInstance(mContext).isRemoteConfigStampStayDetailMessage3Enabled();
//
//            stampMessage1TextView.setText(message1);
//            stampMessage2TextView.setText(message2);
//
//            if (message3Enabled == true)
//            {
//                String message3 = DailyPreference.getInstance(mContext).getRemoteConfigStampStayDetailMessage3();
//
//                SpannableString spannableString3 = new SpannableString(message3);
//                spannableString3.setSpan(new UnderlineSpan(), 0, spannableString3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                stampMessage3TextView.setVisibility(View.VISIBLE);
//                stampMessage3TextView.setText(spannableString3);
//
//                stampClickLayout.setOnClickListener(new OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        mOnEventListener.onStampClick();
//                    }
//                });
//            } else
//            {
//                stampMessage3TextView.setVisibility(View.GONE);
//                stampClickLayout.setOnClickListener(null);
//            }
//        } else
//        {
//            stampLayout.setVisibility(View.GONE);
//        }
//
//        // 날짜
//        View dateInformationLayout = view.findViewById(R.id.dateInformationLayout);
//        TextView checkInDayTextView = (TextView) dateInformationLayout.findViewById(R.id.checkinDayTextView);
//        TextView checkOutDayTextView = (TextView) dateInformationLayout.findViewById(R.id.checkoutDayTextView);
//        TextView nightsTextView = (TextView) dateInformationLayout.findViewById(R.id.nightsTextView);
//
//        // 체크인체크아웃 날짜
//        checkInDayTextView.setText(mStayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)"));
//        checkOutDayTextView.setText(mStayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)"));
//
//        try
//        {
//            nightsTextView.setText(mContext.getString(R.string.label_nights, mStayBookingDay.getNights()));
//        } catch (Exception e)
//        {
//            ExLog.e(e.toString());
//        }
//
//        dateInformationLayout.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if (mOnEventListener == null)
//                {
//                    return;
//                }
//
//                mOnEventListener.onCalendarClick();
//            }
//        });
    }

//    public void setTrueReviewCount(int count)
//    {
//        if (mHotelTitleLayout == null)
//        {
//            return;
//        }
//
//        TextView trueReviewTextView = (TextView) mHotelTitleLayout.findViewById(R.id.trueReviewTextView);
//
//        if (count == 0)
//        {
//            trueReviewTextView.setVisibility(View.GONE);
//        } else
//        {
//            trueReviewTextView.setVisibility(View.VISIBLE);
//            trueReviewTextView.setText(mContext.getString(R.string.label_detail_view_review_go, count));
//            trueReviewTextView.setOnClickListener(new OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    mOnEventListener.onReviewClick();
//                }
//            });
//        }
//    }
}
