package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;


import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.UserTracking;
import com.daily.dailyhotel.parcel.analytics.StayThankYouAnalyticsParam;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayThankYouPresenter extends BaseExceptionPresenter<StayThankYouActivity, StayThankYouInterface> implements StayThankYouView.OnEventListener
{
    private StayThankYouAnalyticsInterface mAnalytics;

    private ProfileRemoteImpl mProfileRemoteImpl;

    private String mAggregationId;
    private String mStayName;
    private String mImageUrl;
    private StayBookDateTime mStayBookDateTime;
    private String mRoomName;
    private boolean mOverseas;
    private boolean mWaitingForBooking;

    public interface StayThankYouAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(StayThankYouAnalyticsParam analyticsParam);

        StayThankYouAnalyticsParam getAnalyticsParam();

        void onScreen(Activity activity);

        void onEventPayment(Activity activity);

        void onEventTracking(Activity activity, UserTracking userTracking);

        void onEventConfirmClick(Activity activity);

        void onEventStampClick(Activity activity);

        void onEventBackClick(Activity activity);
    }

    public StayThankYouPresenter(@NonNull StayThankYouActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayThankYouInterface createInstanceViewInterface()
    {
        return new StayThankYouView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayThankYouActivity activity)
    {
        lock();

        setContentView(R.layout.activity_stay_payment_thank_you_data);

        setAnalytics(new StayThankYouAnalyticsImpl());

        mProfileRemoteImpl = new ProfileRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayThankYouAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mOverseas = intent.getBooleanExtra(StayThankYouActivity.INTENT_EXTRA_DATA_OVERSEAS, false);
        mStayName = intent.getStringExtra(StayThankYouActivity.INTENT_EXTRA_DATA_STAY_NAME);
        mImageUrl = intent.getStringExtra(StayThankYouActivity.INTENT_EXTRA_DATA_IMAGE_URL);

        String checkInDateTime = intent.getStringExtra(StayThankYouActivity.INTENT_EXTRA_DATA_CHECK_IN);
        String checkOutDateTime = intent.getStringExtra(StayThankYouActivity.INTENT_EXTRA_DATA_CHECK_OUT);

        setStayBookDateTime(checkInDateTime, checkOutDateTime);

        mRoomName = intent.getStringExtra(StayThankYouActivity.INTENT_EXTRA_DATA_ROOM_NAME);
        mAggregationId = intent.getStringExtra(StayThankYouActivity.INTENT_EXTRA_DATA_AGGREGATION_ID);
        mWaitingForBooking = intent.getBooleanExtra(StayThankYouActivity.INTENT_EXTRA_DATA_WAITING_FOR_BOOKING, false);

        mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));

        mAnalytics.onEventPayment(getActivity());

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.label_completed_payment));
        getViewInterface().setImageUrl(mImageUrl);

        String name = DailyUserPreference.getInstance(getActivity()).getName();
        getViewInterface().setUserName(name);

        final String DATE_FORMAT = "yyyy.M.d (EEE) HH시";
        final boolean stampEnable = isStampEnabled();

        try
        {
            String checkInDate = mStayBookDateTime.getCheckInDateTime(DATE_FORMAT);
            String checkOutDate = mStayBookDateTime.getCheckOutDateTime(DATE_FORMAT);

            SpannableString checkInSpannableString = new SpannableString(checkInDate);
            checkInSpannableString.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                checkInDate.length() - 3, checkInDate.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString checkOutSpannableString = new SpannableString(checkOutDate);
            checkOutSpannableString.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                checkOutDate.length() - 3, checkOutDate.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewInterface().setBooking(checkInSpannableString, checkOutSpannableString, mStayBookDateTime.getNights(), mStayName, mRoomName);

            // 예약 대기 표시
            if (mWaitingForBooking == true)
            {
                getViewInterface().setNoticeVisible(true);
                getViewInterface().setNoticeText(getString(R.string.label_reservation_wait_message));
            } else
            {
                getViewInterface().setNoticeVisible(false);
            }

            // 스탬프를 보여주어야 하는 경우
            if (stampEnable == true)
            {
                getViewInterface().setStampMessages(DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigStampStayThankYouMessage1()//
                    , DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigStampStayThankYouMessage2()//
                    , DailyRemoteConfigPreference.getInstance(getActivity()).getRemoteConfigStampStayThankYouMessage3());
            } else
            {
                getViewInterface().setStampVisible(false);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().startAnimation(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                unLockAll();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        }, stampEnable);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mAnalytics.onScreen(getActivity());

        if (isRefresh() == true)
        {
            onRefresh(true);
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
    public boolean onBackPressed()
    {
        if (isLock() == true)
        {
            return true;
        }

        mAnalytics.onEventBackClick(getActivity());

        startActivity(DailyInternalDeepLink.getStayBookingDetailScreenLink(getActivity(), mAggregationId));

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
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);

        addCompositeDisposable(mProfileRemoteImpl.getTracking().subscribe(new Consumer<UserTracking>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull UserTracking userTracking) throws Exception
            {
                mAnalytics.onEventTracking(getActivity(), userTracking);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onConfirmClick()
    {
        if (isLock() == true)
        {
            return;
        }

        mAnalytics.onEventConfirmClick(getActivity());

        startActivity(DailyInternalDeepLink.getStayBookingDetailScreenLink(getActivity(), mAggregationId));

        finish();
    }

    @Override
    public void onStampClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivity(DailyInternalDeepLink.getStampScreenLink(getActivity()));

        mAnalytics.onEventStampClick(getActivity());

        finish();
    }

    private void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private boolean isStampEnabled()
    {
        return DailyRemoteConfigPreference.getInstance(getActivity()).isRemoteConfigStampEnabled() && mOverseas == false;
    }
}
