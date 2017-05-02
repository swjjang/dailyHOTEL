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
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
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
import com.twoheart.dailyhotel.screen.hotel.filter.StayDetailCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.payment.HotelPaymentActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.SelectStayCouponDialogActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.screen.mydaily.wishlist.WishListTabActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
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

import retrofit2.Call;
import retrofit2.Response;

public class StayDetailActivity extends PlaceDetailActivity
{
    StayProduct mSelectedStayProduct;
    private boolean mCheckPrice;

    /**
     * 리스트에서 호출
     *
     * @param context
     * @param stayBookingDay
     * @param province
     * @param stay
     * @param listCount
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, Province province, Stay stay//
        , int listCount, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, stay.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, stay.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, stay.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, stay.discountPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, stay.entryPosition);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, listCount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, stay.isDailyChoice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GRADE, stay.getGrade().name());

        String[] area = stay.addressSummary.split("\\||l|ㅣ|I");

        intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, area[0].trim());

        String isShowOriginalPrice;
        if (stay.price <= 0 || stay.price <= stay.discountPrice)
        {
            isShowOriginalPrice = "N";
        } else
        {
            isShowOriginalPrice = "Y";
        }

        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE, isShowOriginalPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

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
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, int stayIndex//
        , int roomIndex, boolean isShowCalendar, boolean isShowVR, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, stayIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ROOMINDEX, roomIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, isShowCalendar);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_VR_FLAG, isShowVR);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

        return intent;
    }

    /**
     * 검색 결과에서 호출
     *
     * @param context
     * @param stayBookingDay
     * @param stay
     * @param listCount
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, Stay stay, int listCount, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, stay.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, stay.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, stay.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, stay.discountPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, stay.entryPosition);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, listCount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, stay.isDailyChoice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GRADE, stay.getGrade().name());

        String isShowOriginalPrice;
        if (stay.price <= 0 || stay.price <= stay.discountPrice)
        {
            isShowOriginalPrice = "N";
        } else
        {
            isShowOriginalPrice = "Y";
        }

        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE, isShowOriginalPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

        return intent;
    }

    /**
     * 홈에서 호출
     *
     * @param context
     * @param stayBookingDay
     * @param homePlace
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, HomePlace homePlace, boolean isUsedMultiTransition)
    {
        if (stayBookingDay == null || homePlace == null)
        {
            return null;
        }

        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, homePlace.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, homePlace.title);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, homePlace.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);

        if (homePlace.prices != null && homePlace.prices.discountPrice > 0)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, homePlace.prices.discountPrice);
        } else
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, SKIP_CHECK_DISCOUNT_PRICE_VALUE);
        }

        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GRADE, homePlace.details.stayGrade.name());

        String isShowOriginalPrice = "N";

        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE, isShowOriginalPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

        return intent;
    }

    /**
     * 추천 목록에서 호출
     *
     * @param context
     * @param stayBookingDay
     * @param recommendationStay
     * @param listCount
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, RecommendationStay recommendationStay//
        , int listCount, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, recommendationStay.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, recommendationStay.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, recommendationStay.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, recommendationStay.discount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, recommendationStay.entryPosition);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, listCount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, recommendationStay.isDailyChoice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GRADE, recommendationStay.grade);

        String isShowOriginalPrice;
        if (recommendationStay.price <= 0 || recommendationStay.price <= recommendationStay.discount)
        {
            isShowOriginalPrice = "N";
        } else
        {
            isShowOriginalPrice = "Y";
        }

        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE, isShowOriginalPrice);
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

        mPlaceBookingDay = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        mIsShowCalendar = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        mIsShowVR = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_VR_FLAG, false);

        if (mPlaceBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        mPlaceDetail = createPlaceDetail(intent);

        // 최근 본 업장 저장
        RecentPlaces recentPlaces = new RecentPlaces(this);
        recentPlaces.add(Constants.PlaceType.HOTEL, mPlaceDetail.index);
        recentPlaces.savePreference();

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_TYPE) == true)
        {
            mIsDeepLink = true;
            mDontReloadAtOnResume = false;
            mIsTransitionEnd = true;
            mIsUsedMultiTransition = false;

            mProductDetailIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ROOMINDEX, 0);

            initLayout(null, null, null, false);
        } else
        {
            mIsDeepLink = false;

            String placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME);
            mDefaultImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);

            if (placeName == null)
            {
                Util.restartApp(this);
                return;
            }

            mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
            mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);
            mViewPrice = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, 0);
            Stay.Grade grade = Stay.Grade.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_GRADE));

            boolean isFromMap = intent.hasExtra(NAME_INTENT_EXTRA_DATA_FROM_MAP) == true;

            mIsUsedMultiTransition = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, false);

            if (mIsUsedMultiTransition == true)
            {
                initTransition();
            } else
            {
                mIsTransitionEnd = true;
            }

            initLayout(placeName, mDefaultImageUrl, grade, isFromMap);
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
                    mPlaceDetailLayout.setTransImageVisibility(false);
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

    private void initLayout(String placeName, String imageUrl, Stay.Grade grade, boolean isFromMap)
    {
        setContentView(mPlaceDetailLayout.onCreateView(R.layout.activity_placedetail));

        if (mIsDeepLink == false && mIsUsedMultiTransition == true)
        {
            initTransLayout(placeName, imageUrl, grade, isFromMap);
        } else
        {
            mPlaceDetailLayout.setDefaultImage(imageUrl);
        }

        mPlaceDetailLayout.setStatusBarHeight(this);
        mPlaceDetailLayout.setIsUsedMultiTransitions(mIsUsedMultiTransition);

        setLockUICancelable(true);
        initToolbar(placeName);

        mOnEventListener.hideActionBar(false);
    }

    private void initTransLayout(String placeName, String imageUrl, Stay.Grade grade, boolean isFromMap)
    {
        if (DailyTextUtils.isTextEmpty(placeName, imageUrl) == true)
        {
            return;
        }

        mPlaceDetailLayout.setTransImageView(imageUrl);
        ((StayDetailLayout) mPlaceDetailLayout).setTitleText(grade, placeName);

        if (isFromMap == true)
        {
            mPlaceDetailLayout.setTransBottomGradientBackground(R.color.black_a28);
        }
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
        int entryPosition = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        String isShowOriginalPrice = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE);
        int listCount = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        boolean isDailyChoice = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        return new StayDetail(stayIndex, entryPosition, isShowOriginalPrice, listCount, isDailyChoice);
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

            if (stayDetail == null)
            {
                return;
            }

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
        if (placeBookingDay == null || placeDetail == null)
        {
            return;
        }

        StayBookingDay stayBookingDay = (StayBookingDay) placeBookingDay;
        StayDetail stayDetail = (StayDetail) placeDetail;

        try
        {
            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            String name = DailyUserPreference.getInstance(StayDetailActivity.this).getName();

            if (DailyTextUtils.isTextEmpty(name) == true)
            {
                name = getString(R.string.label_friend) + "가";
            } else
            {
                name += "님이";
            }

            int nights = stayBookingDay.getNights();

            String message = getString(R.string.message_detail_stay_share_sms//
                , name, stayDetailParams.name//
                , stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)")//
                , stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)")//
                , nights, nights + 1 //
                , stayDetailParams.address);

            Util.sendSms(this, message);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        recordAnalyticsShared(stayDetail, AnalyticsManager.ValueType.MESSAGE);
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

            if (mProvince instanceof Area)
            {
                Area area = (Area) mProvince;
                params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
            } else
            {
                params.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
            }

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

        //        stayProduct.categoryCode = stayDetailParams.categoryCode;
        boolean isBenefit = DailyTextUtils.isTextEmpty(stayDetailParams.benefit) == false;
        //        stayProduct.nights = stayDetailParams.nights;

        Intent intent = HotelPaymentActivity.newInstance(StayDetailActivity.this, stayProduct//
            , stayBookingDay, imageUrl, stayDetail.index, isBenefit //
            , mProvince, mArea, stayDetail.isShowOriginalPrice, stayDetail.entryPosition //
            , stayDetail.isDailyChoice, stayDetailParams.ratingValue //
            , stayDetailParams.getGrade().name(), stayDetailParams.address //
            , stayDetailParams.isOverseas, stayDetailParams.name, stayDetailParams.category);

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

            mPlaceDetail = new StayDetail(mPlaceDetail.index, mPlaceDetail.entryPosition, //
                mPlaceDetail.isShowOriginalPrice, mPlaceDetail.listCount, mPlaceDetail.isDailyChoice);

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
        mOnEventListener.doBooking();
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
                , stayBookingDay, stayDetailParams.category, stayDetailParams.name);
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

        if (mIsDeepLink == true)
        {
            // 딥링크로 진입한 경우에는 카테고리 코드를 알수가 없다. - 2017.04.28 알 수 없음으로 안보내기로 함 아이폰도 안보내고 있음.
//            if (DailyTextUtils.isTextEmpty(stayDetailParams.category) == true)
//            {
//                stayDetailParams.category = stayDetailParams.getGrade().name();
//            }

            mDailyToolbarLayout.setToolbarTitle(stayDetailParams.name);
        }

        if (mPlaceDetailLayout != null)
        {
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
                mPlaceDetailLayout.hideWishButton();
            }
        }

        if (DailyPreference.getInstance(this).getTrueVRSupport() > 0)
        {
            if(mTrueVRParamsList != null && mTrueVRParamsList.size() > 0)
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
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
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

    void startCalendar(TodayDateTime todayDateTime, StayBookingDay stayBookingDay, int placeIndex, boolean isAnimation, boolean isSingleDay)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = StayDetailCalendarActivity.newInstance(StayDetailActivity.this, todayDateTime, stayBookingDay//
            , placeIndex, AnalyticsManager.ValueType.DETAIL, true, isAnimation, isSingleDay);
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

        lockUiComponent();

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        boolean isExpectSelected = !stayDetailParams.myWish;
        int wishCount = isExpectSelected == true ? stayDetailParams.wishCount + 1 : stayDetailParams.wishCount - 1;
        mPlaceDetailLayout.setWishButtonCount(wishCount);
        mPlaceDetailLayout.setWishButtonSelected(isExpectSelected);

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
                        startActivityForResult(TrueVRActivity.newInstance(StayDetailActivity.this, mTrueVRParamsList//
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
            startActivityForResult(TrueVRActivity.newInstance(StayDetailActivity.this, mTrueVRParamsList//
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
                params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, stayDetailParams.category);
                params.put(AnalyticsManager.KeyType.CATEGORY, stayDetailParams.category);
            }

            if (mProvince == null)
            {
                params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.AREA, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                if (mProvince instanceof Area)
                {
                    Area area = (Area) mProvince;
                    params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                } else
                {
                    params.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                }

                params.put(AnalyticsManager.KeyType.AREA, DailyTextUtils.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(mViewPrice));
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, stayBookingDay.getCheckInDay("yyyyMMdd"));

            String listIndex = stayDetail.entryPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(stayDetail.entryPosition);

            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);

            String placeCount = stayDetail.listCount == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(stayDetail.listCount);

            params.put(AnalyticsManager.KeyType.PLACE_COUNT, placeCount);

            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetailParams.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, stayDetail.isShowOriginalPrice);
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, stayDetail.isDailyChoice ? "y" : "n");
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
            params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, stayDetailParams.category);

            if (mProvince == null)
            {
                params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.AREA, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                if (mProvince instanceof Area)
                {
                    Area area = (Area) mProvince;
                    params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                } else
                {
                    params.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                }

                params.put(AnalyticsManager.KeyType.AREA, DailyTextUtils.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

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
            mDailyToolbarLayout.setToolbarVisibility(true, isAnimation);
        }

        @Override
        public void hideActionBar(boolean isAnimation)
        {
            mDailyToolbarLayout.setToolbarVisibility(false, isAnimation);
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
                mPlaceDetailLayout.hideWishButtonAnimation();
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
                    mPlaceDetailLayout.showWishButtonAnimation();
                } else
                {
                    ((StayDetailLayout) mPlaceDetailLayout).hideProductInformationLayout();
                    mPlaceDetailLayout.showWishButton();
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
            titleTextView.setText(DailyPreference.getInstance(StayDetailActivity.this).getRemoteConfigStampStayDetailPopupTitle());

            // 메시지
            TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
            messageTextView.setText(DailyPreference.getInstance(StayDetailActivity.this).getRemoteConfigStampStayDetailPopupMessage());

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

            StayDetail stayDetail = (StayDetail) mPlaceDetail;
            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                Action.HOTEL_DETAIL_ADDRESS_COPY_CLICKED, stayDetailParams.name, null);
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
                , null);
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

            startCalendar(mTodayDateTime, (StayBookingDay) mPlaceBookingDay, stayDetail.index, true, stayDetailParams.isSingleStay);
        }

        @Override
        public void doBooking()
        {
            doBooking(mSelectedStayProduct);
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
        public void onWishClick()
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

        @Override
        public void releaseUiComponent()
        {
            StayDetailActivity.this.releaseUiComponent();
        }

        @Override
        public void onTrueVRTooltipClick()
        {
            if (mPlaceDetailLayout != null && mPlaceDetailLayout.isTrueVRTooltipVisibility() == true)
            {
                mPlaceDetailLayout.hideAnimationTooltip();
                DailyPreference.getInstance(StayDetailActivity.this).setTrueVRViewTooltip(false);
            }
        }
    };


    private StayDetailNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StayDetailNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCommonDateTime(TodayDateTime todayDateTime)
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
                    unLockUI();
                    startCalendar(mTodayDateTime, stayBookingDay, mPlaceDetail.index, false, false);
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
                mPlaceDetailLayout.setWishButtonCount(wishCount);
                mPlaceDetailLayout.setWishButtonSelected(true);
                mPlaceDetailLayout.setUpdateWishPopup(PlaceDetailLayout.WishPopupState.ADD);

                try
                {
                    Map<String, String> params = new HashMap<>();
                    params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                    params.put(AnalyticsManager.KeyType.NAME, stayDetailParams.name);
                    params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mViewPrice));
                    params.put(AnalyticsManager.KeyType.COUNTRY, stayDetailParams.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                    params.put(AnalyticsManager.KeyType.CATEGORY, stayDetailParams.category);

                    if (mProvince == null)
                    {
                        params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
                        params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                        params.put(AnalyticsManager.KeyType.AREA, AnalyticsManager.ValueType.EMPTY);
                    } else
                    {
                        if (mProvince instanceof Area)
                        {
                            Area area = (Area) mProvince;
                            params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                            params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                        } else
                        {
                            params.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
                            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                        }

                        params.put(AnalyticsManager.KeyType.AREA, DailyTextUtils.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
                    }

                    params.put(AnalyticsManager.KeyType.GRADE, stayDetailParams.getGrade().name());
                    params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mPlaceDetail.index));
                    params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetailParams.ratingValue));

                    String listIndex = mPlaceDetail.entryPosition == -1 //
                        ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mPlaceDetail.entryPosition);

                    params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
                    params.put(AnalyticsManager.KeyType.DAILYCHOICE, mPlaceDetail.isDailyChoice ? "y" : "n");
                    params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(stayDetailParams.benefit) ? "no" : "yes");

                    int nights = stayBookingDay.getNights();

                    params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
                    params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mPlaceDetail.isShowOriginalPrice);

                    AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(//
                        AnalyticsManager.Category.NAVIGATION_,//
                        Action.WISHLIST_ON, stayDetailParams.name, params);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            } else
            {
                mPlaceDetailLayout.setWishButtonCount(stayDetailParams.wishCount);
                mPlaceDetailLayout.setWishButtonSelected(stayDetailParams.myWish);

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
                mPlaceDetailLayout.setWishButtonCount(wishCount);
                mPlaceDetailLayout.setWishButtonSelected(false);
                mPlaceDetailLayout.setUpdateWishPopup(PlaceDetailLayout.WishPopupState.DELETE);

                try
                {
                    Map<String, String> params = new HashMap<>();
                    params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                    params.put(AnalyticsManager.KeyType.NAME, stayDetailParams.name);
                    params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mViewPrice));
                    params.put(AnalyticsManager.KeyType.COUNTRY, stayDetailParams.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                    params.put(AnalyticsManager.KeyType.CATEGORY, stayDetailParams.category);

                    if (mProvince == null)
                    {
                        params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
                        params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                        params.put(AnalyticsManager.KeyType.AREA, AnalyticsManager.ValueType.EMPTY);
                    } else
                    {
                        if (mProvince instanceof Area)
                        {
                            Area area = (Area) mProvince;
                            params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                            params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                        } else
                        {
                            params.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
                            params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                        }

                        params.put(AnalyticsManager.KeyType.AREA, DailyTextUtils.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
                    }

                    params.put(AnalyticsManager.KeyType.GRADE, stayDetailParams.getGrade().name());
                    params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mPlaceDetail.index));
                    params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetailParams.ratingValue));

                    String listIndex = mPlaceDetail.entryPosition == -1 //
                        ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mPlaceDetail.entryPosition);

                    params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
                    params.put(AnalyticsManager.KeyType.DAILYCHOICE, mPlaceDetail.isDailyChoice ? "y" : "n");
                    params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(stayDetailParams.benefit) ? "no" : "yes");

                    int nights = stayBookingDay.getNights();

                    params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
                    params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mPlaceDetail.isShowOriginalPrice);

                    AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(//
                        AnalyticsManager.Category.NAVIGATION_,//
                        Action.WISHLIST_OFF, stayDetailParams.name, params);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
            } else
            {
                mPlaceDetailLayout.setWishButtonCount(stayDetailParams.wishCount);
                mPlaceDetailLayout.setWishButtonSelected(stayDetailParams.myWish);

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
                            StayDetailActivity.this.onErrorPopupMessage(msgCode, message, null);
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
                    StayDetailActivity.this.onErrorPopupMessage(msgCode, message, null);
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
