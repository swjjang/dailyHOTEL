package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;


import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.UserTracking;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayThankYouAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.booking.detail.map.GourmetBookingDetailMapActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetSearchCuration;
import com.twoheart.dailyhotel.model.GourmetSearchParams;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayThankYouPresenter extends BaseExceptionPresenter<StayThankYouActivity, StayThankYouInterface> implements StayThankYouView.OnEventListener
{
    StayThankYouAnalyticsInterface mAnalytics;

    private ProfileRemoteImpl mProfileRemoteImpl;
    GourmetRemoteImpl mGourmetRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private String mAggregationId;
    private String mStayName;
    private String mImageUrl;
    StayBookDateTime mStayBookDateTime;
    private String mRoomName;
    private boolean mOverseas;
    private boolean mWaitingForBooking;
    double mLatitude;
    double mLongitude;
    CommonDateTime mCommonDateTime;
    View mViewByLongPress;
    Gourmet mGourmetByLongPress;

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

        void onEventRecommendGourmetVisible(Activity activity, boolean hasData);

        void onEventRecommendGourmetViewAllClick(Activity activity);

        void onEventRecommendGourmetItemClick(Activity activity, double distance, int placeIndex);

        void onEventOrderComplete(Activity activity);
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
        mCommonRemoteImpl = new CommonRemoteImpl(activity);

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
        mAnalytics.onEventOrderComplete(getActivity());

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (Util.supportPreview(getActivity()) == true)
        {
            if (getViewInterface().isBlurVisible() == true)
            {
                getViewInterface().setBlurVisible(getActivity(), false);
            }
        }

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
        switch (requestCode)
        {
            case StayThankYouActivity.REQUEST_CODE_PREVIEW:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    Observable.create(new ObservableOnSubscribe<Object>()
                    {
                        @Override
                        public void subscribe(ObservableEmitter<Object> e) throws Exception
                        {
                            startGourmetDetail(mViewByLongPress, mGourmetByLongPress, mCommonDateTime, mStayBookDateTime);
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                }
                break;
            }
        }

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
                return mGourmetRemoteImpl.getList(gourmetParams);
            }
        });

        addCompositeDisposable(Observable.zip(getViewInterface().getReceiptAnimation() //
            , mCommonRemoteImpl.getCommonDateTime(), recommendObservable //
            , new Function3<Boolean, CommonDateTime, List<Gourmet>, ArrayList<CarouselListItem>>()
            {
                @Override
                public ArrayList<CarouselListItem> apply(@io.reactivex.annotations.NonNull Boolean animationComplete //
                    , @io.reactivex.annotations.NonNull CommonDateTime commonDateTime //
                    , @io.reactivex.annotations.NonNull List<Gourmet> gourmetList) throws Exception
                {
                    mCommonDateTime = commonDateTime;
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

                unLockAll();

                mAnalytics.onEventRecommendGourmetVisible(getActivity(), hasData);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                ExLog.w(throwable.toString());
                getViewInterface().setRecommendGourmetData(null);

                startInformationAnimation();

                unLockAll();

                mAnalytics.onEventRecommendGourmetVisible(getActivity(), false);
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

    @Override
    public void onRecommendGourmetViewAllClick()
    {
        if (lock() == true)
        {
            return;
        }

        if (getViewInterface() == null || mStayBookDateTime == null || mCommonDateTime == null)
        {
            return;
        }

        try
        {
            String title = getActivity().getResources().getString(R.string.label_home_view_all);

            String visitDay = mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
            long currentDateTime = DailyCalendar.convertStringToDate(mCommonDateTime.currentDateTime).getTime();
            long checkInDateTime = DailyCalendar.convertStringToDate(visitDay).getTime();

            if (currentDateTime > checkInDateTime)
            {
                visitDay = mCommonDateTime.dailyDateTime;
            }

            GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
            gourmetBookingDay.setVisitDay(visitDay);

            ArrayList<Gourmet> gourmetList = new ArrayList<>();

            ArrayList<CarouselListItem> carouselListItemList = getViewInterface().getRecommendGourmetData();
            if (carouselListItemList != null && carouselListItemList.size() > 0)
            {
                for (CarouselListItem item : carouselListItemList)
                {
                    Gourmet gourmet = item.getItem();
                    if (gourmet != null)
                    {
                        gourmetList.add(gourmet);
                    }
                }
            }

            Location location = new Location((String) null);
            location.setLatitude(mLatitude);
            location.setLongitude(mLongitude);

            Intent intent = GourmetBookingDetailMapActivity.newInstance( //
                getActivity(), title, gourmetBookingDay, gourmetList, location, mStayName, true);

            getActivity().startActivityForResult(intent, StayThankYouActivity.REQUEST_CODE_RECOMMEND_MAP);

            mAnalytics.onEventRecommendGourmetViewAllClick(getActivity());

        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onRecommendGourmetItemClick(View view)
    {
        if (lock() == true)
        {
            return;
        }

        if (view == null)
        {
            return;
        }

        CarouselListItem item = (CarouselListItem) view.getTag();
        if (item == null)
        {
            return;
        }

        Gourmet gourmet = item.getItem();
        if (gourmet == null)
        {
            return;
        }

        startGourmetDetail(view, gourmet, mCommonDateTime, mStayBookDateTime);

        mAnalytics.onEventRecommendGourmetItemClick(getActivity(), gourmet.distance, gourmet.index);
    }

    @Override
    public void onRecommendGourmetItemLongClick(View view)
    {
        if (lock() == true)
        {
            return;
        }

        if (view == null || getViewInterface() == null || mCommonDateTime == null)
        {
            return;
        }

        CarouselListItem item = (CarouselListItem) view.getTag();
        if (item == null)
        {
            return;
        }

        Gourmet gourmet = item.getItem();
        if (gourmet == null)
        {
            return;
        }

        try
        {
            mViewByLongPress = view;
            mGourmetByLongPress = gourmet;

            getViewInterface().setBlurVisible(getActivity(), true);

            String visitDay = mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
            long currentDateTime = DailyCalendar.convertStringToDate(mCommonDateTime.currentDateTime).getTime();
            long checkInDateTime = DailyCalendar.convertStringToDate(visitDay).getTime();

            if (currentDateTime > checkInDateTime)
            {
                visitDay = mCommonDateTime.dailyDateTime;
            }

            GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
            gourmetBookingDay.setVisitDay(visitDay);

            Intent intent = GourmetPreviewActivity.newInstance(getActivity(), gourmetBookingDay, gourmet);

            startActivityForResult(intent, StayThankYouActivity.REQUEST_CODE_PREVIEW);
        } catch (Exception e)
        {
            unLockAll();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void startGourmetDetail(View view, Gourmet gourmet, CommonDateTime commonDateTime, StayBookDateTime stayBookDateTime)
    {
        if (view == null || gourmet == null || stayBookDateTime == null || commonDateTime == null)
        {
            return;
        }

        try
        {
            String visitDay = stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
            long currentDateTime = DailyCalendar.convertStringToDate(commonDateTime.currentDateTime).getTime();
            long checkInDateTime = DailyCalendar.convertStringToDate(visitDay).getTime();

            if (currentDateTime > checkInDateTime)
            {
                visitDay = commonDateTime.dailyDateTime;
            }

            // --> 추후에 정리되면 메소드로 수정
            GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
            analyticsParam.price = gourmet.price;
            analyticsParam.discountPrice = gourmet.discountPrice;
            analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
            analyticsParam.setProvince(null);
            analyticsParam.entryPosition = gourmet.entryPosition;
            analyticsParam.totalListCount = -1;
            analyticsParam.isDailyChoice = gourmet.isDailyChoice;
            analyticsParam.setAddressAreaName(gourmet.addressSummary);

            // <-- 추후에 정리되면 메소드로 수정

            if (Util.isUsedMultiTransition() == true)
            {
                getActivity().setExitSharedElementCallback(new SharedElementCallback()
                {
                    @Override
                    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                    {
                        super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

                        for (View view : sharedElements)
                        {
                            if (view instanceof SimpleDraweeView)
                            {
                                view.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                });

                Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                    , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                    , visitDay, gourmet.category, gourmet.isSoldOut, false, false, true//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                    , analyticsParam);

                if (intent == null)
                {
                    Util.restartApp(getActivity());
                    return;
                }

                View simpleDraweeView = view.findViewById(R.id.contentImageView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientBottomView);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity()//
                    , android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)) //
                    , android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)) //
                    , android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                getActivity().startActivityForResult(intent, StayThankYouActivity.REQUEST_CODE_DETAIL, options.toBundle());
            } else
            {
                Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                    , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                    , visitDay, gourmet.category, gourmet.isSoldOut, false, false, false//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                    , analyticsParam);

                if (intent == null)
                {
                    Util.restartApp(getActivity());
                    return;
                }

                getActivity().startActivityForResult(intent, StayThankYouActivity.REQUEST_CODE_DETAIL);

                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
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

    ArrayList<CarouselListItem> convertCarouselListItemList(List<Gourmet> gourmetList)
    {
        ArrayList<CarouselListItem> carouselListItemList = new ArrayList<>();

        if (gourmetList == null || gourmetList.size() == 0)
        {
            return carouselListItemList;
        }

        for (Gourmet gourmet : gourmetList)
        {
            try
            {
                if (gourmet.isSoldOut == true)
                {
                    // sold out 업장 제외하기로 함
                    // ExLog.d(gourmet.name + " , " + gourmet.isSoldOut + " : " + gourmet.availableTicketNumbers);
                    continue;
                }

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

        return carouselListItemList;
    }

    void startInformationAnimation()
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
