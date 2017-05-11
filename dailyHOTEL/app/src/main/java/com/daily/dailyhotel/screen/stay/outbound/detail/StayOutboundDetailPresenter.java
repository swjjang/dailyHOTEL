package com.daily.dailyhotel.screen.stay.outbound.detail;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.animation.LinearInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.widget.AlphaTransition;
import com.twoheart.dailyhotel.widget.TextTransition;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundDetailPresenter extends BaseExceptionPresenter<StayOutboundDetailActivity, StayOutboundDetailViewInterface> implements StayOutboundDetailView.OnEventListener
{
    private StayOutboundDetailAnalyticsInterface mAnalytics;

    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;

    private int mStayIndex;
    private StayBookDateTime mStayBookDateTime;
    private Persons mPersons;

    private boolean mIsUsedMultiTransition;

    public interface StayOutboundDetailAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundDetailPresenter(@NonNull StayOutboundDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundDetailViewInterface createInstanceViewInterface()
    {
        return new StayOutboundDetailView(getActivity(), this);
    }

    @Override
    public void initialize(StayOutboundDetailActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_detail_data);

        setAnalytics(new StayStayOutboundDetailAnalyticsImpl());

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);

        mPersons = new Persons(Persons.DEFAULT_PERSONS, null);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundDetailAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
            mIsUsedMultiTransition = false;
        } else
        {
            mIsUsedMultiTransition = intent.getBooleanExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_MULTITRANSITION, false);

            mStayIndex = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_STAY_INDEX, -1);

            if (mStayIndex == -1)
            {
                return false;
            }

            String checkInDateTime = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHECKIN);
            String checkOutDateTime = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHECKOUT);

            try
            {
                mStayBookDateTime = new StayBookDateTime();
                mStayBookDateTime.setCheckInDateTime(checkInDateTime);
                mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                return false;
            }

            mPersons.numberOfAdults = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            mPersons.setChildList(intent.getStringArrayListExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHILD_LIST));
        }

        return true;
    }

    @Override
    public void onIntentAfter()
    {
        Observable.zip(getSharedElementTransition(), mStayOutboundRemoteImpl.getStayOutBoundDetail(mStayIndex, mStayBookDateTime, mPersons), new BiFunction<Boolean, StayOutboundDetail, StayOutboundDetail>()
        {
            @Override
            public StayOutboundDetail apply(Boolean aBoolean, StayOutboundDetail stayOutboundDetail) throws Exception
            {
                return stayOutboundDetail;
            }
        }).subscribe(new Consumer<StayOutboundDetail>()
        {
            @Override
            public void accept(StayOutboundDetail stayOutboundDetail) throws Exception
            {
                if (stayOutboundDetail == null)
                {
                    return;
                }

                // 화면 업데이트.
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {

                onHandleError(throwable);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public void onFinish()
    {
        super.onFinish();

        if (mIsUsedMultiTransition == false)
        {
            getActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
        }
    }

    @Override
    protected void onRefresh()
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(true);


    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onImageClick()
    {

    }

    @Override
    public void onReviewClick()
    {

    }

    @Override
    public void onCalendarClick()
    {

    }

    @Override
    public void onDownloadCouponClick()
    {

    }

    @Override
    public void onMapClick()
    {

    }

    @Override
    public void onClipAddressClick(String address)
    {

    }

    @Override
    public void onNavigatorClick()
    {

    }

    @Override
    public void onWishClick()
    {

    }

    @Override
    public void onConciergeClick()
    {

    }

    @Override
    public void onBookingClick()
    {

    }

    @TargetApi(value = 21)
    private Observable<Boolean> getSharedElementTransition()
    {
        Observable<Boolean> observable;

        if (mIsUsedMultiTransition == false)
        {
            TransitionSet inTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            Transition inTextTransition = new TextTransition(getActivity().getResources().getColor(R.color.white), getActivity().getResources().getColor(R.color.default_text_c323232)//
                , 17, 18, new LinearInterpolator());
            inTextTransition.addTarget(getString(R.string.transition_place_name));
            inTransitionSet.addTransition(inTextTransition);

            Transition inBottomAlphaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
            inBottomAlphaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
            inTransitionSet.addTransition(inBottomAlphaTransition);

            Transition inTopAlphaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
            inTopAlphaTransition.addTarget(getString(R.string.transition_gradient_top_view));
            inTransitionSet.addTransition(inTopAlphaTransition);

            getActivity().getWindow().setSharedElementEnterTransition(inTransitionSet);

            TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            Transition outTextTransition = new TextTransition(getActivity().getResources().getColor(R.color.default_text_c323232), getActivity().getResources().getColor(R.color.white)//
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

            getActivity().getWindow().setSharedElementReturnTransition(outTransitionSet);

            observable = new Observable<Boolean>()
            {
                @Override
                protected void subscribeActual(Observer<? super Boolean> observer)
                {
                    getActivity().getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener()
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
        } else
        {
            observable = new Observable<Boolean>()
            {
                @Override
                protected void subscribeActual(Observer<? super Boolean> observer)
                {
                    observer.onNext(true);
                    observer.onComplete();
                }
            };
        }

        return observable;
    }

}
