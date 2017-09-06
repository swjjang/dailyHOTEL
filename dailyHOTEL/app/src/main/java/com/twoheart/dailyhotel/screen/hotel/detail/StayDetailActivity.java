package com.twoheart.dailyhotel.screen.hotel.detail;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam;
import com.daily.dailyhotel.repository.local.model.AnalyticsParam;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.home.stay.inbound.payment.StayPaymentActivity;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.network.model.TrueVRParams;
import com.twoheart.dailyhotel.place.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.ImageDetailListActivity;
import com.twoheart.dailyhotel.screen.common.TrueVRActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayDetailCalendarActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.SelectStayCouponDialogActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.screen.mydaily.wishlist.WishListTabActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.AlphaTransition;
import com.twoheart.dailyhotel.widget.TextTransition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class StayDetailActivity extends PlaceDetailActivity
{
    StayProduct mSelectedStayProduct;
    private boolean mCheckPrice;
    private boolean mOverseas;

    /**
     * 리스트, 검색 결과, 위시리스트, 최근 본 업장, 홈 , 추천모아보기 에서 호출
     *
     * @param context
     * @param stayBookingDay
     * @param index
     * @param name
     * @param imageUrl
     * @param analyticsParam
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, int index //
        , String name, String imageUrl, AnalyticsParam analyticsParam//
        , boolean isUsedMultiTransition, int gradientType)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ANALYTICS_PARAM, analyticsParam);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GRADIENT_TYPE, gradientType);

        return intent;
    }

    /**
     * 딥링크로 호출
     *
     * @param context
     * @param stayBookingDay
     * @param stayIndex
     * @param roomIndex
     * @param isShowCalendar
     * @param isShowVR
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, boolean overseas, int stayIndex//
        , int roomIndex, boolean isShowCalendar, boolean isShowVR, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ISOVERSEAS, overseas);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, stayIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ROOMINDEX, roomIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, isShowCalendar);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_VR_FLAG, isShowVR);
        //        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        //        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        //        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        AnalyticsParam analyticsParam = new AnalyticsParam();
        analyticsParam.placeIndex = stayIndex;
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ANALYTICS_PARAM, analyticsParam);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mOverseas = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_ISOVERSEAS, false);
        mPlaceBookingDay = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        mIsShowCalendar = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        mIsShowVR = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_VR_FLAG, false);

        if (mPlaceBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        mPlaceDetail = createPlaceDetail(intent);

        String placeName = null;
        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME) == true)
        {
            placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME);
        }

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL) == true)
        {
            mDefaultImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);
        }

        RecentlyPlaceUtil.addRecentlyItem(this, Constants.ServiceType.HOTEL //
            , mPlaceDetail.index, placeName, null, mDefaultImageUrl, true);

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_ANALYTICS_PARAM) == true)
        {
            mAnalyticsParam = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_ANALYTICS_PARAM);
        }

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_TYPE) == true)
        {
            mIsDeepLink = true;
            mDontReloadAtOnResume = false;
            mIsTransitionEnd = true;
            mIsUsedMultiTransition = false;

            mProductDetailIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ROOMINDEX, 0);

            initLayout(null, null, null, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);
        } else
        {
            mIsDeepLink = false;

            if (placeName == null)
            {
                Util.restartApp(this);
                return;
            }

            mViewPrice = mAnalyticsParam != null ? mAnalyticsParam.discountPrice : 0;
            Stay.Grade grade = mAnalyticsParam != null ? Stay.Grade.valueOf(mAnalyticsParam.gradeCode) : Stay.Grade.etc;

            int gradientType = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_GRADIENT_TYPE, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);

            mIsUsedMultiTransition = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, false);

            if (mIsUsedMultiTransition == true)
            {
                initTransition();
            } else
            {
                mIsTransitionEnd = true;
            }

            initLayout(placeName, mDefaultImageUrl, grade, gradientType);
        }

        // VR 여부 판단
        mPlaceDetailNetworkController.requestHasVRList(PlaceType.HOTEL, mPlaceDetail.index, "HOTEL");
    }

    @Override
    protected void onResume()
    {
        if (mPlaceDetailLayout != null)
        {
            hideProductInformationLayout(false);
        }

        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        if (mPlaceDetailLayout != null)
        {
            switch (mPlaceDetailLayout.getBookingStatus())
            {
                case StayDetailLayout.STATUS_BOOKING:
                case StayDetailLayout.STATUS_NONE:
                    hideProductInformationLayout(true);
                    return;
            }
        }

        super.onBackPressed();
    }

    @TargetApi(value = 21)
    private void initTransition()
    {
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
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener()
            {
                @Override
                public void onTransitionStart(Transition transition)
                {

                }

                @Override
                public void onTransitionEnd(Transition transition)
                {
                    mPlaceDetailLayout.setTransVisibility(View.INVISIBLE);
                    mPlaceDetailLayout.setDefaultImage(mDefaultImageUrl);

                    // 딥링크가 아닌 경우에는 시간을 요청할 필요는 없다. 어떻게 할지 고민중
                    mIsTransitionEnd = true;

                    if (mInitializeStatus == STATUS_INITIALIZE_DATA)
                    {
                        mHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                updateDetailInformationLayout((StayBookingDay) mPlaceBookingDay, (StayDetail) mPlaceDetail);
                            }
                        });
                    } else
                    {
                        // 애니메이션이 끝났으나 아직 데이터가 로드 되지 않은 경우에는 프로그래스 바를 그리도록 한다.
                        lockUI();
                    }
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
        } else
        {
            mIsTransitionEnd = true;
        }
    }

    private void initLayout(String placeName, String imageUrl, Stay.Grade grade, int gradientType)
    {
        setContentView(mPlaceDetailLayout.onCreateView(R.layout.activity_placedetail));

        mPlaceDetailLayout.setStatusBarHeight(this);
        mPlaceDetailLayout.setIsUsedMultiTransitions(mIsUsedMultiTransition, gradientType);

        if (mIsDeepLink == false && mIsUsedMultiTransition == true)
        {
            initTransLayout(placeName, imageUrl, grade);
        } else
        {
            mPlaceDetailLayout.setDefaultImage(imageUrl);
        }

        setLockUICancelable(true);
        initToolbar(placeName);

        mOnEventListener.hideActionBar(false);
    }

    private void initTransLayout(String placeName, String imageUrl, Stay.Grade grade)
    {
        if (DailyTextUtils.isTextEmpty(placeName, imageUrl) == true)
        {
            return;
        }

        mPlaceDetailLayout.setTransImageView(imageUrl);
        ((StayDetailLayout) mPlaceDetailLayout).setTitleText(grade, placeName);
    }

    @Override
    protected PlaceDetailLayout getDetailLayout(Context context)
    {
        return new StayDetailLayout(context, mOnEventListener);
    }

    @Override
    protected PlaceDetailNetworkController getNetworkController(Context context)
    {
        return new StayDetailNetworkController(context, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected PlaceDetail createPlaceDetail(Intent intent)
    {
        if (intent == null)
        {
            return null;
        }

        int stayIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
        //        int entryPosition = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        //        String isShowOriginalPrice = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE);
        //        int listCount = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        //        boolean isDailyChoice = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        return new StayDetail(stayIndex);
    }

    @Override
    protected void requestCommonDateTimeNSoldOutList(int placeIndex)
    {
        int dayCount = StayDetailCalendarActivity.DEFAULT_OVERSEAS_CALENDAR_DAY_OF_MAX_COUNT;

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime().observeOn(Schedulers.io()) //
            , mPlaceDetailCalendarImpl.getStayUnavailableCheckInDates(mPlaceDetail.index, dayCount, false).observeOn(Schedulers.io()) //
            , new BiFunction<CommonDateTime, List<String>, TodayDateTime>()
            {
                @Override
                public TodayDateTime apply(@NonNull CommonDateTime commonDateTime, @NonNull List<String> soldOutList) throws Exception
                {
                    if (mSoldOutList == null)
                    {
                        mSoldOutList = new ArrayList<>();
                    }

                    mSoldOutList.clear();

                    for (String dayString : soldOutList)
                    {
                        int soldOutDay = Integer.parseInt(DailyCalendar.convertDateFormatString(dayString, "yyyy-MM-dd", "yyyyMMdd"));
                        mSoldOutList.add(soldOutDay);
                    }

                    TodayDateTime todayDateTime = new TodayDateTime();
                    todayDateTime.setToday(commonDateTime.openDateTime, commonDateTime.closeDateTime //
                        , commonDateTime.currentDateTime, commonDateTime.dailyDateTime);

                    return todayDateTime;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<TodayDateTime>()
        {
            @Override
            public void accept(@NonNull TodayDateTime todayDateTime) throws Exception
            {
                setCommonDateTime(todayDateTime);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    @Override
    protected void setCommonDateTime(TodayDateTime todayDateTime)
    {
        mTodayDateTime = todayDateTime;

        try
        {
            // 체크인 시간이 설정되어 있지 않는 경우 기본값을 넣어준다.
            if (mPlaceBookingDay == null)
            {
                mPlaceBookingDay = new StayBookingDay();
                StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;

                stayBookingDay.setCheckInDay(mTodayDateTime.dailyDateTime);
                stayBookingDay.setCheckOutDay(mTodayDateTime.dailyDateTime, 1);
            } else
            {
                StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;

                // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                // 체크인 날짜 체크

                // 날짜로 비교해야 한다.
                Calendar todayCalendar = DailyCalendar.getInstance(mTodayDateTime.dailyDateTime, true);
                Calendar checkInCalendar = DailyCalendar.getInstance(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), true);
                Calendar checkOutCalendar = DailyCalendar.getInstance(stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT), true);

                // 하루가 지나서 체크인 날짜가 전날짜 인 경우
                if (todayCalendar.getTimeInMillis() > checkInCalendar.getTimeInMillis())
                {
                    stayBookingDay.setCheckInDay(mTodayDateTime.dailyDateTime);

                    checkInCalendar = DailyCalendar.getInstance(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), true);
                }

                // 체크인 날짜가 체크 아웃 날짜와 같거나 큰경우.
                if (checkInCalendar.getTimeInMillis() >= checkOutCalendar.getTimeInMillis())
                {
                    stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), 1);
                }
            }

            StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;

            if (mIsShowCalendar == true)
            {
                boolean overseas = mOverseas;

                if (mPlaceDetail != null && ((StayDetail) mPlaceDetail).getStayDetailParams() != null)
                {
                    overseas = ((StayDetail) mPlaceDetail).getStayDetailParams().isOverseas;
                }

                unLockUI();
                startCalendar(mTodayDateTime, stayBookingDay, overseas, mPlaceDetail.index, mSoldOutList, false, false);
                return;
            }

            ((StayDetailNetworkController) mPlaceDetailNetworkController).requestHasCoupon(mPlaceDetail.index,//
                stayBookingDay.getCheckInDay("yyyy-MM-dd"), stayBookingDay.getNights());

            mPlaceDetailNetworkController.requestPlaceReviewScores(PlaceType.HOTEL, mPlaceDetail.index);
        } catch (Exception e)
        {
            onError(e);
            unLockUI();

            finish();
        }
    }

    @Override
    protected void shareKakao(String imageUrl, PlaceBookingDay placeBookingDay, PlaceDetail placeDetail)
    {
        if (placeBookingDay == null || placeDetail == null)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            String name = DailyUserPreference.getInstance(StayDetailActivity.this).getName();

            if (DailyTextUtils.isTextEmpty(name) == true)
            {
                name = getString(R.string.label_friend) + "가";
            } else
            {
                name += "님이";
            }

            StayDetail stayDetail = (StayDetail) placeDetail;

            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();
            if (stayDetailParams == null)
            {
                return;
            }

            KakaoLinkManager.newInstance(StayDetailActivity.this).shareStay(name//
                , stayDetailParams.name//
                , stayDetailParams.address//
                , stayDetail.index//
                , imageUrl//
                , (StayBookingDay) placeBookingDay);

            recordAnalyticsShared(stayDetail, AnalyticsManager.ValueType.KAKAO);
        } catch (Exception e)
        {
            showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(StayDetailActivity.this, "com.kakao.talk");
                    }
                }, null);
        }
    }

    @Override
    protected void shareSMS(PlaceBookingDay placeBookingDay, PlaceDetail placeDetail)
    {
        if (placeBookingDay == null || placeDetail == null || isLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        try
        {
            StayBookingDay stayBookingDay = (StayBookingDay) placeBookingDay;
            int nights = stayBookingDay.getNights();

            String longUrl = String.format(Locale.KOREA, "https://mobile.dailyhotel.co.kr/stay/%d?dateCheckIn=%s&stays=%d"//
                , placeDetail.index, stayBookingDay.getCheckInDay("yyyy-MM-dd"), nights);

            StayDetail stayDetail = (StayDetail) placeDetail;

            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            String name = DailyUserPreference.getInstance(StayDetailActivity.this).getName();

            if (DailyTextUtils.isTextEmpty(name) == true)
            {
                name = getString(R.string.label_friend) + "가";
            } else
            {
                name += "님이";
            }

            final String message = getString(R.string.message_detail_stay_share_sms//
                , name, stayDetailParams.name//
                , stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)")//
                , stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)")//
                , nights, nights + 1 //
                , stayDetailParams.address);

            CommonRemoteImpl commonRemote = new CommonRemoteImpl(StayDetailActivity.this);

            addCompositeDisposable(commonRemote.getShortUrl(longUrl).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(@NonNull String shortUrl) throws Exception
                {
                    unLockUI();

                    Util.sendSms(StayDetailActivity.this, message + shortUrl);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    unLockUI();

                    Util.sendSms(StayDetailActivity.this, message + "https://mobile.dailyhotel.co.kr/stay/" + placeDetail.index);
                }
            }));
        } catch (Exception e)
        {
            unLockUI();

            ExLog.d(e.toString());
        }

        recordAnalyticsShared((StayDetail) placeDetail, AnalyticsManager.ValueType.MESSAGE);
    }

    private void recordAnalyticsShared(StayDetail stayDetail, String label)
    {
        try
        {
            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            HashMap<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.SERVICE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.COUNTRY, stayDetailParams.isOverseas//
                ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());

            if (DailyHotel.isLogin() == true)
            {
                params.put(AnalyticsManager.KeyType.USER_TYPE, AnalyticsManager.ValueType.MEMBER);

                switch (DailyUserPreference.getInstance(this).getType())
                {
                    case Constants.DAILY_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.EMAIL);
                        break;

                    case Constants.KAKAO_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.KAKAO);
                        break;

                    case Constants.FACEBOOK_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.FACEBOOK);
                        break;

                    default:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
                        break;
                }
            } else
            {
                params.put(AnalyticsManager.KeyType.USER_TYPE, AnalyticsManager.ValueType.GUEST);
                params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
            }

            params.put(AnalyticsManager.KeyType.PUSH_NOTIFICATION, DailyUserPreference.getInstance(this).isBenefitAlarm() ? "on" : "off");
            params.put(AnalyticsManager.KeyType.SHARE_METHOD, label);
            params.put(AnalyticsManager.KeyType.VENDOR_ID, Integer.toString(stayDetail.index));
            params.put(AnalyticsManager.KeyType.VENDOR_NAME, stayDetailParams.name);

            AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
                , Action.STAY_ITEM_SHARE, label, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    protected void startKakao()
    {
        try
        {
            // 카카오톡 패키지 설치 여부
            getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            StayDetailParams stayDetailParams = ((StayDetail) mPlaceDetail).getStayDetailParams();

            startActivityForResult(HappyTalkCategoryDialog.newInstance(this//
                , HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_DETAIL//
                , stayDetailParams.index, 0, stayDetailParams.name), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
        } catch (Exception e)
        {
            showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(StayDetailActivity.this, "com.kakao.talk");
                    }
                }, null);
        }

        //        try
        //        {
        //            startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/@%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94")));
        //        } catch (ActivityNotFoundException e)
        //        {
        //            try
        //            {
        //                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_GOOGLE_KAKAOTALK)));
        //            } catch (ActivityNotFoundException e1)
        //            {
        //                Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
        //                marketLaunch.setData(Uri.parse(URL_STORE_GOOGLE_KAKAOTALK_WEB));
        //                startActivity(marketLaunch);
        //            }
        //        }
    }

    void processBooking(StayBookingDay stayBookingDay, StayDetail stayDetail, StayProduct stayProduct)
    {
        if (stayBookingDay == null || stayDetail == null || stayProduct == null)
        {
            return;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        if (stayDetailParams == null)
        {
            return;
        }

        String imageUrl = null;
        List<ImageInformation> imageInformationList = stayDetail.getImageList();

        if (imageInformationList != null && imageInformationList.size() > 0)
        {
            imageUrl = imageInformationList.get(0).getImageUrl();
        }

        StayPaymentAnalyticsParam stayPaymentAnalyticsParam = new StayPaymentAnalyticsParam();

        stayPaymentAnalyticsParam.nrd = stayProduct.isNRD;
        stayPaymentAnalyticsParam.showOriginalPrice = mAnalyticsParam.showOriginalPriceYn;
        stayPaymentAnalyticsParam.rankingPosition = mAnalyticsParam.entryPosition;
        stayPaymentAnalyticsParam.totalListCount = mAnalyticsParam.totalListCount;
        stayPaymentAnalyticsParam.ratingValue = stayDetailParams.ratingValue;
        stayPaymentAnalyticsParam.benefit = DailyTextUtils.isTextEmpty(stayDetailParams.benefit) == false;
        stayPaymentAnalyticsParam.averageDiscount = stayProduct.averageDiscount;
        stayPaymentAnalyticsParam.address = stayDetailParams.address;
        stayPaymentAnalyticsParam.dailyChoice = mAnalyticsParam.isDailyChoice;
        stayPaymentAnalyticsParam.province = mAnalyticsParam.getProvince();
        stayPaymentAnalyticsParam.addressAreaName = mAnalyticsParam.getAddressAreaName();

        Intent intent = StayPaymentActivity.newInstance(StayDetailActivity.this, stayDetailParams.index//
            , stayDetailParams.name, imageUrl, stayProduct.roomIndex, stayProduct.totalDiscount, stayProduct.roomName//
            , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
            , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
            , stayDetailParams.isOverseas, stayDetailParams.category, stayDetailParams.getGrade(), stayPaymentAnalyticsParam);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            hideSimpleDialog();

            StayBookingDay stayBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

            if (stayBookingDay == null)
            {
                return;
            }

            lockUI();

            mPlaceBookingDay = stayBookingDay;

            mPlaceDetail = new StayDetail(mPlaceDetail.index);

            try
            {
                ((StayDetailNetworkController) mPlaceDetailNetworkController).requestHasCoupon(mPlaceDetail.index//
                    , stayBookingDay.getCheckInDay("yyyy-MM-dd"), stayBookingDay.getNights());
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }
    }

    protected void hideProductInformationLayout(boolean isAnimation)
    {
        mOnEventListener.hideProductInformationLayout(isAnimation);
    }

    @Override
    protected void doBooking()
    {
        mOnEventListener.doBooking(mSelectedStayProduct);
    }

    @Override
    protected void downloadCoupon(PlaceBookingDay placeBookingDay, PlaceDetail placeDetail)
    {
        if (placeBookingDay == null || placeDetail == null)
        {
            return;
        }

        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        StayBookingDay stayBookingDay = (StayBookingDay) placeBookingDay;
        StayDetailParams stayDetailParams = ((StayDetail) placeDetail).getStayDetailParams();

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, Action.HOTEL_COUPON_DOWNLOAD, stayDetailParams.name, null);

        if (DailyHotel.isLogin() == false)
        {
            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_detail_please_login), //
                getString(R.string.dialog_btn_login_for_benefit), getString(R.string.dialog_btn_text_close), //
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, Action.COUPON_LOGIN, AnalyticsManager.Label.LOGIN_, null);

                        Intent intent = LoginActivity.newInstance(StayDetailActivity.this, Screen.DAILYHOTEL_DETAIL);
                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN_BY_COUPON);
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, Action.COUPON_LOGIN, AnalyticsManager.Label.CLOSED, null);
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, Action.COUPON_LOGIN, AnalyticsManager.Label.CLOSED, null);
                    }
                }, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        releaseUiComponent();
                    }
                }, true);
        } else
        {
            Intent intent = SelectStayCouponDialogActivity.newInstance(this, mPlaceDetail.index//
                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                , stayDetailParams.category, stayDetailParams.name);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_DOWNLOAD_COUPON);
        }
    }

    @Override
    protected void recordAnalyticsShareClicked()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SHARE,//
            AnalyticsManager.Action.ITEM_SHARE, AnalyticsManager.Label.STAY, null);
    }

    void updateDetailInformationLayout(StayBookingDay stayBookingDay, StayDetail stayDetail)
    {
        switch (mInitializeStatus)
        {
            case STATUS_INITIALIZE_DATA:
                mInitializeStatus = STATUS_INITIALIZE_LAYOUT;
                break;

            case STATUS_INITIALIZE_COMPLETE:
                break;

            default:
                return;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        RecentlyPlaceUtil.addRecentlyItem(this, Constants.ServiceType.HOTEL //
            , stayDetail.index, stayDetailParams.name, null, stayDetailParams.imgUrl, false);

        if (mPlaceDetailLayout != null)
        {
            if (stayDetail == null || stayDetail.getStayDetailParams() == null)
            {
                setWishTextView(false, 0);
            } else
            {
                setWishTextView(stayDetail.getStayDetailParams().myWish, stayDetail.getStayDetailParams().wishCount);
            }

            ((StayDetailLayout) mPlaceDetailLayout).setDetail(stayBookingDay, stayDetail, mPlaceReviewScores, mCurrentImage);
        }

        if (mCheckPrice == false)
        {
            mCheckPrice = true;
            checkStayRoom(mIsDeepLink, stayDetail, mViewPrice);
        }

        // 딥링크로 메뉴 오픈 요청
        if (mIsDeepLink == true && mProductDetailIndex > 0 && stayDetail.getProductList().size() > 0)
        {
            if (mPlaceDetailLayout != null)
            {
                ((StayDetailLayout) mPlaceDetailLayout).showProductInformationLayout(mProductDetailIndex);
            }
        }

        if (DailyPreference.getInstance(this).getTrueVRSupport() > 0)
        {
            if (mTrueVRParamsList != null && mTrueVRParamsList.size() > 0)
            {
                showTrueViewMenu();

                if (mIsShowVR == true)
                {
                    unLockUI();
                    onTrueViewClick();
                }
            } else
            {
                hideTrueViewMenu();

                if (mIsShowVR == true)
                {
                    unLockUI();
                    showSimpleDialog(null, getString(R.string.message_truevr_not_support), getString(R.string.dialog_btn_text_confirm), null);
                }
            }
        } else
        {
            hideTrueViewMenu();

            if (mIsShowVR == true)
            {
                unLockUI();
                showSimpleDialog(null, getString(R.string.message_truevr_not_support_hardware), getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        mProductDetailIndex = 0;
        mIsDeepLink = false;
        mIsShowVR = false;
        mInitializeStatus = STATUS_INITIALIZE_COMPLETE;
    }

    private void checkStayRoom(boolean isDeepLink, StayDetail stayDetail, int listViewPrice)
    {
        // 판매 완료 혹은 가격이 변동되었는지 조사한다
        List<StayProduct> stayProductList = stayDetail.getProductList();
        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        if (stayProductList == null || stayProductList.size() == 0)
        {
            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_sold_out)//
                , getString(R.string.label_changing_date), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mOnEventListener.onCalendarClick();
                    }
                }, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
                    }
                });

            if (stayDetailParams != null)
            {
                if (isDeepLink == true)
                {
                    AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                        Action.SOLDOUT_DEEPLINK, stayDetailParams.name, null);
                } else
                {
                    AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                        Action.SOLDOUT, stayDetailParams.name, null);
                }
            }
        } else
        {
            if (isDeepLink == false)
            {
                boolean hasPrice = false;

                if (listViewPrice == SKIP_CHECK_DISCOUNT_PRICE_VALUE)
                {
                    // 홈 가격 정보 제거로 인한 처리 추가
                    hasPrice = true;
                } else
                {
                    for (StayProduct stayProduct : stayProductList)
                    {
                        if (listViewPrice == stayProduct.averageDiscount)
                        {
                            hasPrice = true;
                            break;
                        }
                    }
                }

                if (hasPrice == false)
                {
                    setResultCode(CODE_RESULT_ACTIVITY_REFRESH);

                    showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                mOnEventListener.showProductInformationLayout();
                            }
                        });

                    AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                        Action.SOLDOUT_CHANGEPRICE, stayDetailParams.name, null);
                }
            }
        }
    }

    void startCalendar(TodayDateTime todayDateTime, StayBookingDay stayBookingDay, boolean overseas, int placeIndex//
        , List<Integer> soldOutList, boolean isAnimation, boolean isSingleDay)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        int dayCount = overseas == false //
            ? StayCalendarActivity.DEFAULT_DOMESTIC_CALENDAR_DAY_OF_MAX_COUNT //
            : StayDetailCalendarActivity.DEFAULT_OVERSEAS_CALENDAR_DAY_OF_MAX_COUNT;

        Intent intent = StayDetailCalendarActivity.newInstance(StayDetailActivity.this, todayDateTime //
            , stayBookingDay, dayCount, placeIndex, AnalyticsManager.ValueType.DETAIL //
            , (ArrayList)soldOutList, true, isAnimation, isSingleDay);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.DETAIL, null);
    }

    void onWishButtonClick(PlaceType placeType, StayDetail stayDetail)
    {
        if (isLockUiComponent() == true || isFinishing() == true || stayDetail == null)
        {
            return;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        if (stayDetailParams == null)
        {
            return;
        }

        lockUiComponent();

        boolean isExpectSelected = !stayDetailParams.myWish;
        int wishCount = isExpectSelected == true ? stayDetailParams.wishCount + 1 : stayDetailParams.wishCount - 1;
        setWishTextView(isExpectSelected, wishCount);

        if (isExpectSelected == true)
        {
            mPlaceDetailNetworkController.requestAddWishList(placeType, stayDetail.index);
        } else
        {
            mPlaceDetailNetworkController.requestRemoveWishList(placeType, stayDetail.index);
        }
    }

    @Override
    public void onTrueViewClick()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        if (DailyPreference.getInstance(this).isTrueVRCheckDataGuide() == false)
        {
            showSimpleDialogType02(null, getString(R.string.message_stay_used_data_guide), getString(R.string.dialog_btn_do_continue)//
                , getString(R.string.dialog_btn_text_close), new OnCheckDialogStateListener()
                {
                    @Override
                    public void onState(View view, boolean checked)
                    {
                        startActivityForResult(TrueVRActivity.newInstance(StayDetailActivity.this, mPlaceDetail.index, mTrueVRParamsList//
                            , PlaceType.HOTEL, ((StayDetail) mPlaceDetail).getStayDetailParams().category), CODE_REQUEST_ACTIVITY_TRUEVIEW);
                    }
                }, null, null, new OnCheckDialogStateListener()
                {
                    @Override
                    public void onState(View view, boolean checked)
                    {
                        unLockUI();
                        DailyPreference.getInstance(StayDetailActivity.this).setTrueVRCheckDataGuide(checked);
                    }
                }, true);
        } else
        {
            startActivityForResult(TrueVRActivity.newInstance(StayDetailActivity.this, mPlaceDetail.index, mTrueVRParamsList//
                , PlaceType.HOTEL, ((StayDetail) mPlaceDetail).getStayDetailParams().category), CODE_REQUEST_ACTIVITY_TRUEVIEW);
        }

        try
        {
            AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                AnalyticsManager.Action.TRUE_VR_CLICK, Integer.toString(((StayDetail) mPlaceDetail).getStayDetailParams().index), null);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected void onWishClick()
    {
        if (DailyHotel.isLogin() == false)
        {
            DailyToast.showToast(StayDetailActivity.this, R.string.toast_msg_please_login, Toast.LENGTH_LONG);

            Intent intent = LoginActivity.newInstance(StayDetailActivity.this, Screen.DAILYHOTEL_DETAIL);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN_BY_DETAIL_WISHLIST);
        } else
        {
            StayDetailActivity.this.onWishButtonClick(PlaceType.HOTEL, (StayDetail) mPlaceDetail);
        }
    }

    void recordAnalyticsStayDetail(String screen, StayBookingDay stayBookingDay, StayDetail stayDetail)
    {
        if (stayDetail == null)
        {
            return;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();
        if (stayDetailParams == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, stayDetailParams.name);
            params.put(AnalyticsManager.KeyType.GRADE, stayDetailParams.getGrade().getName(StayDetailActivity.this)); // 14
            params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(stayDetailParams.benefit) ? "no" : "yes"); // 3

            if (stayDetail.getProductList() == null || stayDetail.getProductList().size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(stayDetail.getProductList().get(0).averageDiscount));
            }

            int nights = stayBookingDay.getNights();

            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayDetail.index)); // 15

            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd")); // 1
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd")); // 2

            params.put(AnalyticsManager.KeyType.ADDRESS, stayDetailParams.address);

            if (DailyTextUtils.isTextEmpty(stayDetailParams.category) == true) //
            {
                params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                params.put(AnalyticsManager.KeyType.CATEGORY, stayDetailParams.category);
            }

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

            params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(mViewPrice));
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, stayBookingDay.getCheckInDay("yyyyMMdd"));

            String listIndex = mAnalyticsParam.entryPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);

            String placeCount = mAnalyticsParam.totalListCount == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.totalListCount);

            params.put(AnalyticsManager.KeyType.PLACE_COUNT, placeCount);

            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetailParams.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPriceYn);
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));

            AnalyticsManager.getInstance(StayDetailActivity.this).recordScreen(this, screen, null, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    Map<String, String> recordAnalyticsBooking(StayBookingDay stayBookingDay, StayDetail stayDetail, StayProduct stayProduct)
    {
        if (stayBookingDay == null || stayDetail == null || stayProduct == null)
        {
            return null;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        try
        {
            int nights = stayBookingDay.getNights();

            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, stayDetailParams.name);
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayDetail.index));
            params.put(AnalyticsManager.KeyType.CATEGORY, stayDetailParams.category);

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

            params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.toString(stayProduct.averageDiscount));
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, stayBookingDay.getCheckInDay("yyyyMMdd"));

            return params;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    StayDetailLayout.OnEventListener mOnEventListener = new StayDetailLayout.OnEventListener()
    {
        @Override
        public void showActionBar(boolean isAnimation)
        {
            if (isAnimation == true)
            {
                mToolbarView.showAnimation();
            } else
            {
                mToolbarView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void hideActionBar(boolean isAnimation)
        {
            if (isAnimation == true)
            {
                mToolbarView.hideAnimation();
            } else
            {
                mToolbarView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClickImage(PlaceDetail placeDetail)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            List<ImageInformation> imageInformationList = placeDetail.getImageList();
            if (imageInformationList.size() == 0)
            {
                return;
            }

            StayDetailParams stayDetailParams = ((StayDetail) placeDetail).getStayDetailParams();
            if (stayDetailParams == null)
            {
                return;
            }

            lockUiComponent();

            Intent intent = ImageDetailListActivity.newInstance(StayDetailActivity.this, PlaceType.HOTEL, stayDetailParams.name, imageInformationList, mCurrentImage);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_IMAGELIST);

            AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                AnalyticsManager.Action.HOTEL_IMAGE_CLICKED, stayDetailParams.name, null);
        }

        @Override
        public void onSelectedImagePosition(int position)
        {
            mCurrentImage = position;
        }

        @Override
        public void onConciergeClick()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            showCallDialog(PlaceType.HOTEL);

            AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.STAY_DETAIL, null);
        }

        @Override
        public void showProductInformationLayout()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            if (mPlaceDetailLayout != null)
            {
                ((StayDetailLayout) mPlaceDetailLayout).showAnimationProductInformationLayout();
            }

            releaseUiComponent();

            StayDetailParams stayDetailParams = ((StayDetail) mPlaceDetail).getStayDetailParams();

            recordAnalyticsStayDetail(Screen.DAILYHOTEL_DETAIL_ROOMTYPE, (StayBookingDay) mPlaceBookingDay, (StayDetail) mPlaceDetail);
            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.ROOM_TYPE_CLICKED, stayDetailParams.name, null);
        }

        @Override
        public void hideProductInformationLayout(boolean isAnimation)
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            if (mPlaceDetailLayout != null)
            {
                if (isAnimation == true)
                {
                    ((StayDetailLayout) mPlaceDetailLayout).hideAnimationProductInformationLayout();
                } else
                {
                    ((StayDetailLayout) mPlaceDetailLayout).hideProductInformationLayout();
                }
            }

            releaseUiComponent();
        }

        @Override
        public void onStampClick()
        {
            if (isFinishing())
            {
                return;
            }

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = layoutInflater.inflate(R.layout.view_dialog_stamp_layout, null, false);

            final Dialog dialog = new Dialog(StayDetailActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            // 상단
            TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
            titleTextView.setText(DailyRemoteConfigPreference.getInstance(StayDetailActivity.this).getRemoteConfigStampStayDetailPopupTitle());

            // 메시지
            TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
            messageTextView.setText(DailyRemoteConfigPreference.getInstance(StayDetailActivity.this).getRemoteConfigStampStayDetailPopupMessage());

            View confirmTextView = dialogView.findViewById(R.id.confirmTextView);
            confirmTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                }
            });

            try
            {
                dialog.setContentView(dialogView);

                WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(StayDetailActivity.this, dialog);

                dialog.show();

                dialog.getWindow().setAttributes(layoutParams);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                AnalyticsManager.Action.STAMP_DETAIL_CLICK, AnalyticsManager.Label.STAY_DETAIL_VIEW, null);
        }

        @Override
        public void onReviewClick()
        {
            if (mPlaceDetail == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            String category = ((StayDetail) mPlaceDetail).getStayDetailParams().category;

            startActivityForResult(StayReviewActivity.newInstance(StayDetailActivity.this//
                , mPlaceDetail.index, category, mPlaceReviewScores), Constants.CODE_REQUEST_ACTIVITY_PLACE_REVIEW);

            AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.TRUE_REVIEW_CLICK, AnalyticsManager.Label.STAY, null);
        }

        @Override
        public void showMap()
        {
            if (Util.isInstallGooglePlayService(StayDetailActivity.this) == true)
            {
                if (lockUiComponentAndIsLockUiComponent() == true || isFinishing() == true)
                {
                    return;
                }

                StayDetail stayDetail = (StayDetail) mPlaceDetail;
                StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

                Intent intent = ZoomMapActivity.newInstance(StayDetailActivity.this//
                    , ZoomMapActivity.SourceType.HOTEL, stayDetailParams.name, stayDetailParams.address//
                    , stayDetailParams.latitude, stayDetailParams.longitude, stayDetailParams.isOverseas);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_ZOOMMAP);

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                    Action.HOTEL_DETAIL_MAP_CLICKED, stayDetailParams.name, null);
            } else
            {
                Util.installGooglePlayService(StayDetailActivity.this);
            }
        }

        @Override
        public void finish()
        {
            StayDetailActivity.this.finish();
        }

        @Override
        public void clipAddress(String address)
        {
            DailyTextUtils.clipText(StayDetailActivity.this, address);

            DailyToast.showToast(StayDetailActivity.this, R.string.message_detail_copy_address, Toast.LENGTH_SHORT);

            try
            {
                StayDetail stayDetail = (StayDetail) mPlaceDetail;
                StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                    Action.HOTEL_DETAIL_ADDRESS_COPY_CLICKED, stayDetailParams.name, null);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        @Override
        public void showNavigatorDialog()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            StayDetail stayDetail = (StayDetail) mPlaceDetail;
            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            Util.showShareMapDialog(StayDetailActivity.this, stayDetailParams.name//
                , stayDetailParams.latitude, stayDetailParams.longitude, stayDetailParams.isOverseas//
                , AnalyticsManager.Category.HOTEL_BOOKINGS//
                , AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED//
                , null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        unLockUI();
                    }
                });
        }

        @Override
        public void onCalendarClick()
        {
            StayDetail stayDetail = (StayDetail) mPlaceDetail;

            if (stayDetail == null)
            {
                return;
            }

            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            if (stayDetailParams == null)
            {
                return;
            }

            startCalendar(mTodayDateTime, (StayBookingDay) mPlaceBookingDay, stayDetailParams.isOverseas //
                , stayDetail.index, mSoldOutList, true, stayDetailParams.isSingleStay);
        }

        @Override
        public void onDownloadCouponClick()
        {
            StayDetailActivity.this.downloadCoupon(mPlaceBookingDay, mPlaceDetail);
        }

        @Override
        public void doBooking(StayProduct stayProduct)
        {
            if (stayProduct == null)
            {
                finish();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mSelectedStayProduct = stayProduct;

            if (DailyHotel.isLogin() == false)
            {
                startLoginActivity(Screen.DAILYHOTEL_DETAIL);
            } else
            {
                lockUI();
                mPlaceDetailNetworkController.requestProfile();
            }

            StayDetail stayDetail = (StayDetail) mPlaceDetail;
            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            String label = String.format(Locale.KOREA, "%s-%s", stayDetailParams.name, mSelectedStayProduct.roomName);
            AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.BOOKING_CLICKED, label, recordAnalyticsBooking((StayBookingDay) mPlaceBookingDay, (StayDetail) mPlaceDetail, stayProduct));
        }

        @Override
        public void onChangedViewPrice(int type)
        {
            ((StayDetailLayout) mPlaceDetailLayout).setChangedViewPrice(type);
        }

        @Override
        public void releaseUiComponent()
        {
            StayDetailActivity.this.releaseUiComponent();
        }

        @Override
        public void onWishTooltipClick()
        {
            if (mPlaceDetailLayout != null && mPlaceDetailLayout.isWishTooltipVisibility() == true)
            {
                mPlaceDetailLayout.hideAnimationTooltip();
                DailyPreference.getInstance(StayDetailActivity.this).setWishTooltip(false);
            }
        }

        @Override
        public void onTrueVRClick()
        {
            StayDetailActivity.this.onTrueViewClick();
        }
    };


    private StayDetailNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StayDetailNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onUserProfile(Customer user, String birthday, boolean isDailyUser, boolean isVerified, boolean isPhoneVerified)
        {
            if (isDailyUser == true)
            {
                if (Util.isValidatePhoneNumber(user.getPhone()) == false)
                {
                    moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER, user.getPhone());
                } else
                {
                    // 기존에 인증이 되었는데 인증이 해지되었다.
                    if (isVerified == true && isPhoneVerified == false)
                    {
                        moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER, user.getPhone());
                    } else
                    {
                        processBooking((StayBookingDay) mPlaceBookingDay, (StayDetail) mPlaceDetail, mSelectedStayProduct);
                    }
                }
            } else
            {
                // 입력된 정보가 부족해.
                if (DailyTextUtils.isTextEmpty(user.getEmail(), user.getPhone(), user.getName()) == true)
                {
                    moveToAddSocialUserInformation(user, birthday);
                } else if (Util.isValidatePhoneNumber(user.getPhone()) == false)
                {
                    moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.WRONG_PHONENUMBER, user.getPhone());
                } else
                {
                    processBooking((StayBookingDay) mPlaceBookingDay, (StayDetail) mPlaceDetail, mSelectedStayProduct);
                }
            }
        }

        @Override
        public void onStayDetailInformation(StayDetailParams stayDetailParams)
        {
            try
            {
                StayDetail stayDetail = (StayDetail) mPlaceDetail;

                stayDetail.setStayDetailParams(stayDetailParams);

                // analyticsParam 갱신용 - 딥링크시 호텔 index 빼고는 알 수 없음 - 상세에서 받아온 정보로 갱신
                mAnalyticsParam.placeName = stayDetailParams.name;
                mAnalyticsParam.gradeCode = stayDetailParams.getGrade().name();
                mAnalyticsParam.gradeName = stayDetailParams.getGrade().getName(StayDetailActivity.this);

                if (mInitializeStatus == STATUS_INITIALIZE_NONE)
                {
                    mInitializeStatus = STATUS_INITIALIZE_DATA;
                }

                if (mIsTransitionEnd == true)
                {
                    updateDetailInformationLayout((StayBookingDay) mPlaceBookingDay, stayDetail);
                }

                recordAnalyticsStayDetail(Screen.DAILYHOTEL_DETAIL, (StayBookingDay) mPlaceBookingDay, stayDetail);
            } catch (Exception e)
            {
                DailyToast.showToast(StayDetailActivity.this, R.string.act_base_network_connect, Toast.LENGTH_LONG);
                finish();
            } finally
            {
                unLockUI();
            }
        }

        @Override
        public void onHasCoupon(boolean hasCoupon)
        {
            StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;
            StayDetail stayDetail = (StayDetail) mPlaceDetail;

            stayDetail.hasCoupon = hasCoupon;

            try
            {
                ((StayDetailNetworkController) mPlaceDetailNetworkController).requestStayDetailInformation(stayDetail.index,//
                    stayBookingDay.getCheckInDay("yyyy-MM-dd"), stayBookingDay.getNights());
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        @Override
        public void onAddWishList(boolean isSuccess, String message)
        {
            if (isSameCallingActivity(WishListTabActivity.class.getName()) == true)
            {
                if (mResultIntent == null)
                {
                    mResultIntent = new Intent();
                    mResultIntent.putExtra(NAME_INTENT_EXTRA_DATA_IS_CHANGE_WISHLIST, true);
                }

                setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            }

            StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;
            StayDetail stayDetail = (StayDetail) mPlaceDetail;
            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            if (isSuccess == true)
            {
                stayDetailParams.myWish = true;
                int wishCount = ++stayDetailParams.wishCount;
                setWishTextView(true, wishCount);
                mPlaceDetailLayout.setUpdateWishPopup(PlaceDetailLayout.WishPopupState.ADD);

                try
                {
                    Map<String, String> params = new HashMap<>();
                    params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                    params.put(AnalyticsManager.KeyType.NAME, stayDetailParams.name);
                    params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mViewPrice));
                    params.put(AnalyticsManager.KeyType.COUNTRY, stayDetailParams.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                    params.put(AnalyticsManager.KeyType.CATEGORY, stayDetailParams.category);

                    params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
                    params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
                    params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

                    params.put(AnalyticsManager.KeyType.GRADE, stayDetailParams.getGrade().name());
                    params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mPlaceDetail.index));
                    params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetailParams.ratingValue));

                    String listIndex = mAnalyticsParam.entryPosition == -1 //
                        ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

                    params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
                    params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
                    params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(stayDetailParams.benefit) ? "no" : "yes");

                    int nights = stayBookingDay.getNights();

                    params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
                    params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPriceYn);

                    AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(//
                        AnalyticsManager.Category.NAVIGATION_,//
                        Action.WISHLIST_ON, stayDetailParams.name, params);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            } else
            {
                setWishTextView(stayDetailParams.myWish, stayDetailParams.wishCount);

                if (DailyTextUtils.isTextEmpty(message) == true)
                {
                    message = "";
                }

                releaseUiComponent();

                showSimpleDialog(getResources().getString(R.string.dialog_notice2), message//
                    , getResources().getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        @Override
        public void onRemoveWishList(boolean isSuccess, String message)
        {
            if (isSameCallingActivity(WishListTabActivity.class.getName()) == true)
            {
                if (mResultIntent == null)
                {
                    mResultIntent = new Intent();
                    mResultIntent.putExtra(NAME_INTENT_EXTRA_DATA_IS_CHANGE_WISHLIST, true);
                }

                setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            }

            StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;
            StayDetail stayDetail = (StayDetail) mPlaceDetail;
            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            if (isSuccess == true)
            {
                stayDetailParams.myWish = false;
                int wishCount = --stayDetailParams.wishCount;
                setWishTextView(false, wishCount);
                mPlaceDetailLayout.setUpdateWishPopup(PlaceDetailLayout.WishPopupState.DELETE);

                try
                {
                    Map<String, String> params = new HashMap<>();
                    params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                    params.put(AnalyticsManager.KeyType.NAME, stayDetailParams.name);
                    params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mViewPrice));
                    params.put(AnalyticsManager.KeyType.COUNTRY, stayDetailParams.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                    params.put(AnalyticsManager.KeyType.CATEGORY, stayDetailParams.category);

                    params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
                    params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
                    params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

                    params.put(AnalyticsManager.KeyType.GRADE, stayDetailParams.getGrade().name());
                    params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mPlaceDetail.index));
                    params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetailParams.ratingValue));

                    String listIndex = mAnalyticsParam.entryPosition == -1 //
                        ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

                    params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
                    params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
                    params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(stayDetailParams.benefit) ? "no" : "yes");

                    int nights = stayBookingDay.getNights();

                    params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
                    params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPriceYn);

                    AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(//
                        AnalyticsManager.Category.NAVIGATION_,//
                        Action.WISHLIST_OFF, stayDetailParams.name, params);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
            } else
            {
                setWishTextView(stayDetailParams.myWish, stayDetailParams.wishCount);

                if (DailyTextUtils.isTextEmpty(message) == true)
                {
                    message = "";
                }

                releaseUiComponent();

                showSimpleDialog(getResources().getString(R.string.dialog_notice2), message//
                    , getResources().getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        @Override
        public void onPlaceReviewScores(PlaceReviewScores placeReviewScores)
        {
            if (placeReviewScores == null)
            {
                return;
            }

            mPlaceReviewScores = placeReviewScores;

            mPlaceDetailLayout.setTrueReviewCount(mPlaceReviewScores.reviewScoreTotalCount);
        }

        @Override
        public void onHasVRList(ArrayList<TrueVRParams> trueVRParamsList)
        {
            mTrueVRParamsList = trueVRParamsList;
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            StayDetailActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            StayDetailActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(final int msgCode, final String message)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);

            if (mIsUsedMultiTransition == true && mIsTransitionEnd == false)
            {
                mTransitionEndRunnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mTransitionEndRunnable = null;

                        // 판매 마감시
                        if (msgCode == 5)
                        {
                            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_sold_out)//
                                , getString(R.string.label_changing_date), new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        mOnEventListener.onCalendarClick();
                                    }
                                });
                        } else
                        {
                            StayDetailActivity.this.onErrorPopupMessage(msgCode, message);
                        }
                    }
                };
            } else
            {
                // 판매 마감시
                if (msgCode == 5)
                {
                    showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_sold_out)//
                        , getString(R.string.label_changing_date), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mOnEventListener.onCalendarClick();
                            }
                        });
                } else
                {
                    StayDetailActivity.this.onErrorPopupMessage(msgCode, message);
                }
            }
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            StayDetailActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(final Call call, final Response response)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);

            if (mIsUsedMultiTransition == true && mIsTransitionEnd == false)
            {
                mTransitionEndRunnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mTransitionEndRunnable = null;
                        StayDetailActivity.this.onErrorResponse(call, response);
                        finish();
                    }
                };
            } else
            {
                StayDetailActivity.this.onErrorResponse(call, response);
                finish();
            }
        }
    };
}
