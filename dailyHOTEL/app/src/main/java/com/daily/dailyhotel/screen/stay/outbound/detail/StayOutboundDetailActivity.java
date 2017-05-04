package com.daily.dailyhotel.screen.stay.outbound.detail;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.animation.LinearInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.widget.AlphaTransition;
import com.twoheart.dailyhotel.widget.TextTransition;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundDetailActivity extends BaseActivity<StayOutboundDetailPresenter>
{
    static final String INTENT_EXTRA_DATA_STAY_INDEX = "stayIndex";
    static final String INTENT_EXTRA_DATA_CHECKIN = "checkIn";
    static final String INTENT_EXTRA_DATA_CHECKOUT = "checkOut";
    static final String INTENT_EXTRA_DATA_NUMBER_OF_ADULTS = "numberOfAdults";
    static final String INTENT_EXTRA_DATA_CHILD_LIST = "childList";

    private boolean mIsUsedMultiTransition;

    /**
     * @param stayIndex
     * @param context
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     * @param numberOfAdults
     * @param childList
     * @return
     */
    public static Intent newInstance(Context context, int stayIndex, String checkInDateTime, String checkOutDateTime, int numberOfAdults, ArrayList<String> childList)
    {
        Intent intent = new Intent(context, StayOutboundDetailActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKIN, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECKOUT, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, numberOfAdults);
        intent.putExtra(INTENT_EXTRA_DATA_CHILD_LIST, childList);
        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, StayOutboundDetailActivity.class);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getPresenter().startAsync(getSharedElementTransition());
    }

    @NonNull
    @Override
    protected StayOutboundDetailPresenter createInstancePresenter()
    {
        return new StayOutboundDetailPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        if (mIsUsedMultiTransition == false)
        {
            overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
        }
    }

    @TargetApi(value = 21)
    private Observable<Boolean> getSharedElementTransition()
    {
        Observable<Boolean> observable;

        if (mIsUsedMultiTransition == true)
        {
            TransitionSet inTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            Transition inTextTransition = new TextTransition(getResources().getColor(R.color.white), getResources().getColor(R.color.default_text_c323232)//
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
            Transition outTextTransition = new TextTransition(getResources().getColor(R.color.default_text_c323232), getResources().getColor(R.color.white)//
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

            observable = new Observable<Boolean>()
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
