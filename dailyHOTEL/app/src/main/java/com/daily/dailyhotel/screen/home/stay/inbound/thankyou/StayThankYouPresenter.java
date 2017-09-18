package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;


import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
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
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.UserTracking;
import com.daily.dailyhotel.parcel.analytics.StayThankYouAnalyticsParam;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetSearchCuration;
import com.twoheart.dailyhotel.model.GourmetSearchParams;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayThankYouPresenter extends BaseExceptionPresenter<StayThankYouActivity, StayThankYouInterface> implements StayThankYouView.OnEventListener
{
    private StayThankYouAnalyticsInterface mAnalytics;

    private ProfileRemoteImpl mProfileRemoteImpl;
    private GourmetRemoteImpl mGourmetRemoteImpl;

    private String mAggregationId;
    private String mStayName;
    private String mImageUrl;
    private StayBookDateTime mStayBookDateTime;
    private String mRoomName;
    private boolean mOverseas;
    private boolean mWaitingForBooking;
    private double mLatitude;
    private double mLongitude;

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
        mGourmetRemoteImpl = new GourmetRemoteImpl(activity);

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

        mLatitude = intent.getDoubleExtra(StayThankYouActivity.INTENT_EXTRA_DATA_LATITUDE, 0d);
        mLongitude = intent.getDoubleExtra(StayThankYouActivity.INTENT_EXTRA_DATA_LONGITUDE, 0d);

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
        //        final boolean stampEnable = isStampEnabled();

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
            if (isStampEnabled() == true)
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

        if (getViewInterface() == null)
        {
            return;
        }

        Observable<List<Gourmet>> recommendObservable = Observable.defer(new Callable<ObservableSource<List<Gourmet>>>()
        {
            @Override
            public ObservableSource<List<Gourmet>> call() throws Exception
            {
                if (mLatitude == 0d || mLongitude == 0d)
                {
                    return Observable.just(new ArrayList<>());
                }

                GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();

                try
                {
                    String checkInTime = mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
                    gourmetBookingDay.setVisitDay(checkInTime);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                    return Observable.just(new ArrayList<>());
                }

                Location location = new Location((String) null);
                location.setLatitude(mLatitude);
                location.setLongitude(mLongitude);

                GourmetSearchCuration gourmetCuration = new GourmetSearchCuration();
                GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) gourmetCuration.getCurationOption();
                gourmetCurationOption.setSortType(Constants.SortType.DISTANCE);

                gourmetCuration.setGourmetBookingDay(gourmetBookingDay);
                gourmetCuration.setLocation(location);
                gourmetCuration.setCurationOption(gourmetCurationOption);
                gourmetCuration.setRadius(10d);

                GourmetSearchParams gourmetParams = (GourmetSearchParams) gourmetCuration.toPlaceParams(1, 10, true);
                return mGourmetRemoteImpl.getGourmetList(gourmetParams);
            }
        });

        addCompositeDisposable(Observable.zip(getViewInterface().getReceiptAnimation(), recommendObservable, new BiFunction<Boolean, List<Gourmet>, ArrayList<CarouselListItem>>()
        {
            @Override
            public ArrayList<CarouselListItem> apply(@io.reactivex.annotations.NonNull Boolean animationComplete, @io.reactivex.annotations.NonNull List<Gourmet> gourmetList) throws Exception
            {
                return convertCarouselListItemList(gourmetList);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<CarouselListItem>>()
        {
            @Override
            public void accept(ArrayList<CarouselListItem> carouselListItemList) throws Exception
            {
                getViewInterface().setRecommendGourmetData(carouselListItemList);

                startInformationAnimation();

                boolean hasData = !(carouselListItemList == null || carouselListItemList.size() == 0);

                //                    AnalyticsManager.getInstance(StayThankYouActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_DETAIL//
                //                        , AnalyticsManager.Action.GOURMET_RECOMMEND, hasData ? AnalyticsManager.Label.Y : AnalyticsManager.Label.N, null);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                ExLog.w(throwable.toString());
                getViewInterface().setRecommendGourmetData(null);

                startInformationAnimation();


                //                    AnalyticsManager.getInstance(StayReservationDetailActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_DETAIL//
                //                        , AnalyticsManager.Action.GOURMET_RECOMMEND, AnalyticsManager.Label.N, null);

                unLockAll();
            }
        }));

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

    private ArrayList<CarouselListItem> convertCarouselListItemList(List<Gourmet> list)
    {
        ArrayList<Gourmet> gourmetList = new ArrayList<>();
        ArrayList<CarouselListItem> carouselListItemList = new ArrayList<CarouselListItem>();

        if (list == null || list.size() == 0)
        {
            //            mRecommendGourmetList = gourmetList;
            return carouselListItemList;
        }

        for (Gourmet gourmet : list)
        {
            try
            {
                if (gourmet.isSoldOut == true)
                {
                    // sold out 업장 제외하기로 함
                    // ExLog.d(gourmet.name + " , " + gourmet.isSoldOut + " : " + gourmet.availableTicketNumbers);
                    continue;
                }

                gourmetList.add(gourmet);

                CarouselListItem item = new CarouselListItem(CarouselListItem.TYPE_GOURMET, gourmet);
                carouselListItemList.add(item);
            } catch (Exception e)
            {
                if (gourmet != null)
                {
                    ExLog.w(gourmet.index + " | " + gourmet.name + " :: " + e.getMessage());
                }
            }
        }

        //        mRecommendGourmetList = gourmetList;

        return carouselListItemList;
    }

    private void startInformationAnimation()
    {
        getViewInterface().startRecommendNStampAnimation(new Animator.AnimatorListener()
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
        }, isStampEnabled());
    }
}
