package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.screen.common.ImageDetailListActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetDetailCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.payment.GourmetPaymentActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.SelectGourmetCouponDialogActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.screen.mydaily.wishlist.WishListTabActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.AlphaTransition;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.TextTransition;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetDetailActivity extends PlaceDetailActivity
{
    int mSelectedTicketIndex;
    private boolean mFirstCheckPrice;
    private boolean mRefreshCheckPrice;
    private boolean mIsListSoldOut; // 리스테어서 솔드아웃인지 체크한다.

    /**
     * 리스트에서 호출
     *
     * @param context
     * @param gourmetBookingDay
     * @param province
     * @param gourmet
     * @param listCount
     * @return
     */
    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, Province province, Gourmet gourmet//
        , int listCount, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmet.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, gourmet.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, gourmet.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, gourmet.category);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SOLDOUT, gourmet.isSoldOut);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, gourmet.discountPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PRICE, gourmet.price);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, gourmet.entryPosition);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, listCount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, gourmet.isDailyChoice);

        String[] area = gourmet.addressSummary.split("\\||l|ㅣ|I");
        intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, area[0].trim());

        String isShowOriginalPrice;
        if (gourmet.price <= 0 || gourmet.price <= gourmet.discountPrice)
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
     * @param gourmetBookingDay
     * @param gourmetIndex
     * @param isShowCalendar
     * @return
     */
    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, int gourmetIndex, int productIndex//
        , boolean isShowCalendar, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmetIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PRODUCTINDEX, productIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, isShowCalendar);
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
     * @param gourmetBookingDay
     * @param gourmet
     * @param listCount
     * @return
     */
    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, Gourmet gourmet, int listCount, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmet.index);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, gourmet.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, gourmet.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, gourmet.category);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SOLDOUT, gourmet.isSoldOut);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, gourmet.discountPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PRICE, gourmet.price);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, gourmet.entryPosition);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, listCount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, gourmet.isDailyChoice);

        String isShowOriginalPrice;
        if (gourmet.price <= 0 || gourmet.price <= gourmet.discountPrice)
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
     * @param gourmetBookingDay
     * @param homePlace
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, HomePlace homePlace, boolean isUsedMultiTransition)
    {
        if (gourmetBookingDay == null || homePlace == null)
        {
            return null;
        }

        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, homePlace.index);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, homePlace.title);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, homePlace.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, homePlace.details.category);

        if (homePlace.prices != null && homePlace.prices.discountPrice > 0)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, homePlace.prices.discountPrice);
        } else
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, SKIP_CHECK_DISCOUNT_PRICE_VALUE);
        }

        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        String isShowOriginalPrice = "N";

        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE, isShowOriginalPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, isUsedMultiTransition);

        return intent;
    }

    /**
     * 추천 목록에서 호출
     *
     * @param context
     * @param gourmetBookingDay
     * @param recommendationGourmet
     * @param listCount
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, RecommendationGourmet recommendationGourmet//
        , int listCount, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, recommendationGourmet.index);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, recommendationGourmet.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, recommendationGourmet.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, recommendationGourmet.category);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SOLDOUT, recommendationGourmet.isSoldOut);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, recommendationGourmet.discount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PRICE, recommendationGourmet.price);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, recommendationGourmet.entryPosition);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, listCount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, recommendationGourmet.isDailyChoice);

        String isShowOriginalPrice;
        if (recommendationGourmet.price <= 0 || recommendationGourmet.price <= recommendationGourmet.discount)
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

        mPlaceBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        mIsShowCalendar = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);

        if (mPlaceBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        mPlaceDetail = createPlaceDetail(intent);

        // 최근 본 업장 저장
        RecentPlaces recentPlaces = new RecentPlaces(this);
        recentPlaces.add(PlaceType.FNB, mPlaceDetail.index);
        recentPlaces.savePreference();

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_TYPE) == true)
        {
            mIsDeepLink = true;
            mDontReloadAtOnResume = false;
            mIsTransitionEnd = true;

            mProductDetailIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PRODUCTINDEX, 0);

            initLayout(null, null, false);
        } else
        {
            mIsDeepLink = false;

            String placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACENAME);
            mDefaultImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);
            //            ((GourmetDetail) mPlaceDetail).category = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CATEGORY);

            if (placeName == null)
            {
                Util.restartApp(this);
                return;
            }

            mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
            mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);
            mViewPrice = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, 0);
            mIsListSoldOut = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_SOLDOUT, false);

            boolean isFromMap = intent.hasExtra(NAME_INTENT_EXTRA_DATA_FROM_MAP) == true;

            mIsUsedMultiTransition = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_USED_MULTITRANSITIOIN, false);

            if (mIsUsedMultiTransition == true)
            {
                initTransition();
            } else
            {
                mIsTransitionEnd = true;
            }

            initLayout(placeName, mDefaultImageUrl, isFromMap);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_GOURMET_PRODUCT_LIST:
                setResultCode(resultCode);

                switch (resultCode)
                {
                    case RESULT_OK:
                    case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                        finish();
                        break;

                    case CODE_RESULT_ACTIVITY_REFRESH:
                    case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                        mDontReloadAtOnResume = false;

                        mRefreshCheckPrice = true;
                        break;

                    default:
                        mDontReloadAtOnResume = true;
                        break;
                }
                break;

            case Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK:
                mDontReloadAtOnResume = true;
                break;
        }
    }

    @TargetApi(value = 21)
    private void initTransition()
    {
        if (mIsUsedMultiTransition == true)
        {
            TransitionSet inTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            Transition inNameTextTransition = new TextTransition(getResources().getColor(R.color.white), getResources().getColor(R.color.default_text_c323232)//
                , 17, 18, new LinearInterpolator());
            inNameTextTransition.addTarget(getString(R.string.transition_place_name));
            inTransitionSet.addTransition(inNameTextTransition);

            Transition inBottomAlphaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
            inBottomAlphaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
            inTransitionSet.addTransition(inBottomAlphaTransition);

            Transition inTopAlphaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
            inTopAlphaTransition.addTarget(getString(R.string.transition_gradient_top_view));
            inTransitionSet.addTransition(inTopAlphaTransition);

            getWindow().setSharedElementEnterTransition(inTransitionSet);

            TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            Transition outNameTextTransition = new TextTransition(getResources().getColor(R.color.default_text_c323232), getResources().getColor(R.color.white)//
                , 18, 17, new LinearInterpolator());
            outNameTextTransition.addTarget(getString(R.string.transition_place_name));
            outTransitionSet.addTransition(outNameTextTransition);

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
                                updateDetailInformationLayout((GourmetBookingDay) mPlaceBookingDay, (GourmetDetail) mPlaceDetail);
                            }
                        });

                        if (mTransitionEndRunnable != null)
                        {
                            mHandler.post(mTransitionEndRunnable);
                        }
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

    private void initLayout(String placeName, String imageUrl, boolean isFromMap)
    {
        setContentView(mPlaceDetailLayout.onCreateView(R.layout.activity_gourmet_detail));

        if (mIsDeepLink == false && mIsUsedMultiTransition == true)
        {
            initTransLayout(placeName, imageUrl, isFromMap);
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

    private void initTransLayout(String placeName, String imageUrl, boolean isFromMap)
    {
        if (Util.isTextEmpty(imageUrl) == true)
        {
            return;
        }

        mPlaceDetailLayout.setTransImageView(imageUrl);
        ((GourmetDetailLayout) mPlaceDetailLayout).setTitleText(placeName);

        if (isFromMap == true)
        {
            mPlaceDetailLayout.setTransBottomGradientBackground(R.color.black_a28);
        }
    }

    @Override
    protected PlaceDetailLayout getDetailLayout(Context context)
    {
        return new GourmetDetailLayout(context, mOnEventListener);
    }

    @Override
    protected PlaceDetailNetworkController getNetworkController(Context context)
    {
        return new GourmetDetailNetworkController(context, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected PlaceDetail createPlaceDetail(Intent intent)
    {
        if (intent == null)
        {
            return null;
        }

        int index = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, -1);
        int entryIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        String isShowOriginalPrice = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE);
        int listCount = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        boolean isDailyChoice = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        return new GourmetDetail(index, entryIndex, isShowOriginalPrice, listCount, isDailyChoice);
    }

    @Override
    protected void shareKakao(String imageUrl, PlaceBookingDay placeBookingDay, PlaceDetail placeDetail)
    {
        if (placeBookingDay == null || placeDetail == null)
        {
            return;
        }

        String name = DailyUserPreference.getInstance(GourmetDetailActivity.this).getName();

        if (Util.isTextEmpty(name) == true)
        {
            name = getString(R.string.label_friend) + "가";
        } else
        {
            name += "님이";
        }

        GourmetDetailParams gourmetDetailParams = ((GourmetDetail) placeDetail).getGourmetDetailParmas();

        if (gourmetDetailParams == null)
        {
            return;
        }

        KakaoLinkManager.newInstance(this).shareGourmet(name, gourmetDetailParams.name, gourmetDetailParams.address//
            , placeDetail.index //
            , imageUrl //
            , (GourmetBookingDay) placeBookingDay);

        recordAnalyticsShared(placeDetail, AnalyticsManager.ValueType.KAKAO);
    }

    @Override
    protected void shareSMS(PlaceBookingDay placeBookingDay, PlaceDetail placeDetail)
    {
        if (placeBookingDay == null || placeDetail == null)
        {
            return;
        }

        GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) placeBookingDay;
        GourmetDetailParams gourmetDetailParams = ((GourmetDetail) placeDetail).getGourmetDetailParmas();

        try
        {
            String name = DailyUserPreference.getInstance(GourmetDetailActivity.this).getName();

            if (Util.isTextEmpty(name) == true)
            {
                name = getString(R.string.label_friend) + "가";
            } else
            {
                name += "님이";
            }

            String message = getString(R.string.message_detail_gourmet_share_sms, //
                name, gourmetDetailParams.name, gourmetBookingDay.getVisitDay("yyyy.MM.dd (EEE)"),//
                gourmetDetailParams.address);

            Util.sendSms(this, message);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        recordAnalyticsShared(placeDetail, AnalyticsManager.ValueType.MESSAGE);
    }

    private void recordAnalyticsShared(PlaceDetail placeDetail, String label)
    {
        try
        {
            GourmetDetailParams gourmetDetailParams = ((GourmetDetail) placeDetail).getGourmetDetailParmas();

            HashMap<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.SERVICE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);

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
            params.put(AnalyticsManager.KeyType.VENDOR_ID, Integer.toString(placeDetail.index));
            params.put(AnalyticsManager.KeyType.VENDOR_NAME, gourmetDetailParams.name);

            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.SHARE//
                , AnalyticsManager.Action.GOURMET_ITEM_SHARE, label, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    protected void startKakao()
    {
        //        startActivityForResult(HappyTalkCategoryDialog.newInstance(this, HappyTalkCategoryDialog.CallScreen.SCREEN_GOURMET_DETAIL, ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas().index, 0), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);

        try
        {
            startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/%40%EB%8D%B0%EC%9D%BC%EB%A6%AC%EA%B3%A0%EB%A9%94")));
        } catch (ActivityNotFoundException e)
        {
            try
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_GOOGLE_KAKAOTALK)));
            } catch (ActivityNotFoundException e1)
            {
                Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                marketLaunch.setData(Uri.parse(URL_STORE_GOOGLE_KAKAOTALK_WEB));
                startActivity(marketLaunch);
            }
        }
    }

    protected void processBooking(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail, int ticketIndex)
    {
        if (gourmetBookingDay == null || gourmetDetail == null || ticketIndex < 0)
        {
            return;
        }

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();
        GourmetProduct gourmetProduct = gourmetDetail.getProduct(ticketIndex);

        if (gourmetProduct == null || gourmetDetailParams == null)
        {
            return;
        }

        String imageUrl = null;
        List<ImageInformation> imageInformationList = gourmetDetail.getImageList();

        if (imageInformationList != null && imageInformationList.size() > 0)
        {
            imageUrl = imageInformationList.get(0).getImageUrl();
        }

        boolean isBenefit = Util.isTextEmpty(gourmetDetailParams.benefit) == false;

        Intent intent = GourmetPaymentActivity.newInstance(GourmetDetailActivity.this, gourmetDetailParams.name, gourmetProduct//
            , gourmetBookingDay, imageUrl, gourmetDetailParams.category, gourmetDetail.index, isBenefit //
            , mProvince, mArea, gourmetDetail.isShowOriginalPrice, gourmetDetail.entryPosition //
            , gourmetDetail.isDailyChoice, gourmetDetailParams.ratingValue);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            hideSimpleDialog();

            GourmetBookingDay gourmetBookingDay = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

            if (gourmetBookingDay == null)
            {
                return;
            }

            lockUI();

            mPlaceBookingDay = gourmetBookingDay;

            mPlaceDetail = new GourmetDetail(mPlaceDetail.index, mPlaceDetail.entryPosition, //
                mPlaceDetail.isShowOriginalPrice, mPlaceDetail.listCount, mPlaceDetail.isDailyChoice);

            ((GourmetDetailNetworkController) mPlaceDetailNetworkController).requestHasCoupon(mPlaceDetail.index,//
                gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
        }
    }

    @Override
    protected void doBooking()
    {
        mOnEventListener.doBooking();
    }

    @Override
    protected void downloadCoupon(PlaceBookingDay placeBookingDay, PlaceDetail placeDetail)
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) placeBookingDay;
        GourmetDetailParams gourmetDetailParams = ((GourmetDetail) placeDetail).getGourmetDetailParmas();

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, AnalyticsManager.Action.GOURMET_COUPON_DOWNLOAD, gourmetDetailParams.name, null);

        if (DailyHotel.isLogin() == false)
        {
            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_detail_please_login), //
                getString(R.string.dialog_btn_login_for_benefit), getString(R.string.dialog_btn_text_close), //
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, AnalyticsManager.Action.COUPON_LOGIN, AnalyticsManager.Label.LOGIN, null);

                        Intent intent = LoginActivity.newInstance(GourmetDetailActivity.this, AnalyticsManager.Screen.DAILYHOTEL_DETAIL);
                        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN_BY_COUPON);
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, AnalyticsManager.Action.COUPON_LOGIN, AnalyticsManager.Label.CLOSED, null);
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, AnalyticsManager.Action.COUPON_LOGIN, AnalyticsManager.Label.CLOSED, null);
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
            Intent intent = SelectGourmetCouponDialogActivity.newInstance(this, mPlaceDetail.index, //
                gourmetBookingDay.getVisitDay("yyyy-MM-dd"), gourmetDetailParams.name);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_DOWNLOAD_COUPON);
        }
    }

    @Override
    protected void recordAnalyticsShareClicked()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.SHARE,//
            AnalyticsManager.Action.ITEM_SHARE, AnalyticsManager.Label.GOURMET, null);
    }

    void updateDetailInformationLayout(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail)
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

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        if (mIsDeepLink == true)
        {
            mDailyToolbarLayout.setToolbarText(gourmetDetailParams.name);
        }

        if (mPlaceDetailLayout != null)
        {
            ((GourmetDetailLayout) mPlaceDetailLayout).setDetail(gourmetBookingDay, gourmetDetail, mCurrentImage);
        }

        if (mFirstCheckPrice == false)
        {
            mFirstCheckPrice = true;
            checkGourmetTicket(mIsDeepLink, gourmetDetail, mViewPrice, mIsListSoldOut);
        } else if (mRefreshCheckPrice == true)
        {
            mRefreshCheckPrice = false;
            checkGourmetTicket(mIsDeepLink, gourmetDetail, SKIP_CHECK_DISCOUNT_PRICE_VALUE, mIsListSoldOut);
        }

        // 딥링크로 메뉴 오픈 요청
        if (mIsDeepLink == true && mProductDetailIndex > 0 && gourmetDetail.getProductList().size() > 0)
        {
            if (mPlaceDetailLayout != null)
            {
                Intent intent = GourmetProductListActivity.newInstance(GourmetDetailActivity.this, gourmetBookingDay, gourmetDetail, mProductDetailIndex, null, null);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_PRODUCT_LIST);
            }
        }

        mProductDetailIndex = 0;
        mIsDeepLink = false;
        mInitializeStatus = STATUS_INITIALIZE_COMPLETE;
    }

    private void checkGourmetTicket(boolean isDeepLink, GourmetDetail gourmetDetail, int listViewPrice, final boolean isListSoldOut)
    {
        // 판매 완료 혹은 가격이 변동되었는지 조사한다
        List<GourmetProduct> gourmetProductList = gourmetDetail.getProductList();
        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        if (gourmetProductList == null || gourmetProductList.size() == 0)
        {
            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_sold_out)//
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        if (isListSoldOut == false)
                        {
                            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
                        }
                    }
                });

            if (isDeepLink == true)
            {
                AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                    AnalyticsManager.Action.SOLDOUT_DEEPLINK, gourmetDetailParams.name, null);
            } else
            {
                AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                    AnalyticsManager.Action.SOLDOUT, gourmetDetailParams.name, null);
            }
        } else
        {
            if (isDeepLink == false)
            {
                boolean hasPrice = false;

                if (listViewPrice == SKIP_CHECK_DISCOUNT_PRICE_VALUE)
                {
                    hasPrice = true;
                } else
                {
                    for (GourmetProduct gourmetProduct : gourmetProductList)
                    {
                        if (listViewPrice == gourmetProduct.discountPrice)
                        {
                            hasPrice = true;
                            break;
                        }
                    }
                }

                if (hasPrice == false)
                {
                    setResultCode(CODE_RESULT_ACTIVITY_REFRESH);

                    showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null);

                    AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                        AnalyticsManager.Action.SOLDOUT_CHANGEPRICE, gourmetDetailParams.name, null);
                }
            }
        }
    }

    void startCalendar(TodayDateTime todayDateTime, GourmetBookingDay gourmetBookingDay, int placeIndex, boolean isAnimation)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        String callByScreen;
        if (mIsDeepLink == true)
        {
            callByScreen = AnalyticsManager.Label.EVENT;
        } else
        {
            callByScreen = AnalyticsManager.ValueType.DETAIL;
        }

        Intent intent = GourmetDetailCalendarActivity.newInstance(GourmetDetailActivity.this, //
            todayDateTime, gourmetBookingDay, placeIndex, callByScreen, true, isAnimation);
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.DETAIL, null);
    }

    void onWishButtonClick(PlaceType placeType, GourmetDetail gourmetDetail)
    {
        if (isLockUiComponent() == true || isFinishing() == true || gourmetDetail == null)
        {
            return;
        }

        lockUiComponent();

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        boolean isExpectSelected = !gourmetDetailParams.myWish;
        int wishCount = isExpectSelected == true ? gourmetDetailParams.wishCount + 1 : gourmetDetailParams.wishCount - 1;
        mPlaceDetailLayout.setWishButtonCount(wishCount);
        mPlaceDetailLayout.setWishButtonSelected(isExpectSelected);

        if (isExpectSelected == true)
        {
            mPlaceDetailNetworkController.requestAddWishList(placeType, gourmetDetail.index);
        } else
        {
            mPlaceDetailNetworkController.requestRemoveWishList(placeType, gourmetDetail.index);
        }
    }

    protected void recordAnalyticsGourmetDetail(String screen, GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail)
    {
        if (gourmetBookingDay == null || gourmetDetail == null)
        {
            return;
        }

        try
        {
            GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, gourmetDetailParams.name);
            params.put(AnalyticsManager.KeyType.GRADE, gourmetDetailParams.getGrade().name()); // 14
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetailParams.category);
            params.put(AnalyticsManager.KeyType.DBENEFIT, Util.isTextEmpty(gourmetDetailParams.benefit) ? "no" : "yes");

            if (gourmetDetail.getProductList() == null || gourmetDetail.getProductList().size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(gourmetDetail.getProduct(0).discountPrice));
            }

            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetDetail.index));
            params.put(AnalyticsManager.KeyType.DATE, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));

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

                params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(mViewPrice));
            params.put(AnalyticsManager.KeyType.VISIT_DATE, gourmetBookingDay.getVisitDay("yyyyMMdd"));

            String listIndex = gourmetDetail.entryPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(gourmetDetail.entryPosition);

            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);

            String placeCount = gourmetDetail.listCount == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(gourmetDetail.listCount);

            params.put(AnalyticsManager.KeyType.PLACE_COUNT, placeCount);

            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(gourmetDetailParams.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, gourmetDetail.isShowOriginalPrice);
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, gourmetDetail.isDailyChoice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, "1");

            AnalyticsManager.getInstance(this).recordScreen(this, screen, null, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    protected Map<String, String> recordAnalyticsBooking(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail, GourmetProduct gourmetProduct)
    {
        if (gourmetBookingDay == null || gourmetDetail == null || gourmetProduct == null)
        {
            return null;
        }

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        if (gourmetDetailParams == null)
        {
            return null;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, gourmetDetailParams.name);
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetailParams.category);

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

                params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.toString(gourmetProduct.discountPrice));
            params.put(AnalyticsManager.KeyType.VISIT_DATE, gourmetBookingDay.getVisitDay("yyyyMMdd"));

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

    GourmetDetailLayout.OnEventListener mOnEventListener = new GourmetDetailLayout.OnEventListener()
    {
        @Override
        public void onProductListClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            GourmetDetail gourmetDetail = (GourmetDetail) mPlaceDetail;
            GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

            Intent intent = GourmetProductListActivity.newInstance(GourmetDetailActivity.this, (GourmetBookingDay) mPlaceBookingDay, gourmetDetail, -1, mProvince, mArea);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_PRODUCT_LIST);

            recordAnalyticsGourmetDetail(AnalyticsManager.Screen.DAILYGOURMET_DETAIL_TICKETTYPE, (GourmetBookingDay) mPlaceBookingDay, (GourmetDetail) mPlaceDetail);
            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.TICKET_TYPE_CLICKED, gourmetDetailParams.name, null);
        }

        //        @Override
        //        public void onReservationClick(TicketInformation ticketInformation)
        //        {
        //            if (ticketInformation == null)
        //            {
        //                finish();
        //                return;
        //            }
        //
        //            if (lockUiComponentAndIsLockUiComponent() == true)
        //            {
        //                return;
        //            }
        //
        //            mSelectedTicketInformation = ticketInformation;
        //
        //            if (DailyHotel.isLogin() == false)
        //            {
        //                startLoginActivity(AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
        //            } else
        //            {
        //                lockUI();
        //                mPlaceDetailNetworkController.requestProfile();
        //            }
        //
        //            String label = String.format("%s-%s", mPlaceDetail.name, mSelectedTicketInformation.name);
        //            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
        //                , AnalyticsManager.Action.BOOKING_CLICKED, label, recordAnalyticsBooking(mSaleTime, (GourmetDetail) mPlaceDetail, mSelectedTicketInformation));
        //        }

        @Override
        public void doBooking()
        {
        }

        @Override
        public void onDownloadCouponClick()
        {
            GourmetDetailActivity.this.downloadCoupon(mPlaceBookingDay, mPlaceDetail);
        }

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

            lockUiComponent();

            GourmetDetailParams gourmetDetailParams = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas();

            Intent intent = ImageDetailListActivity.newInstance(GourmetDetailActivity.this, PlaceType.FNB, gourmetDetailParams.name, imageInformationList, mCurrentImage);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_IMAGELIST);

            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                AnalyticsManager.Action.GOURMET_IMAGE_CLICKED, gourmetDetailParams.name, null);
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

            showCallDialog();
        }

        //        @Override
        //        public void showProductInformationLayout()
        //        {
        //            if (isLockUiComponent() == true || isFinishing() == true)
        //            {
        //                return;
        //            }
        //
        //            lockUiComponent();
        //
        //            if (mPlaceDetailLayout != null)
        //            {
        //                mPlaceDetailLayout.showAnimationProductInformationLayout(0);
        //                mPlaceDetailLayout.hideWishButtonAnimation();
        //            }
        //
        //            if (Util.isOverAPI21() == true)
        //            {
        //                Window window = getWindow();
        //                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //                window.setStatusBarColor(getResources().getColor(R.color.textView_textColor_shadow_soldout));
        //            }
        //
        //            releaseUiComponent();
        //
        //            recordAnalyticsGourmetDetail(AnalyticsManager.Screen.DAILYGOURMET_DETAIL_TICKETTYPE, mSaleTime, (GourmetDetail) mPlaceDetail);
        //            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
        //                , AnalyticsManager.Action.TICKET_TYPE_CLICKED, mPlaceDetail.name, null);
        //        }
        //
        //        @Override
        //        public void hideProductInformationLayout(boolean isAnimation)
        //        {
        //            if (isLockUiComponent() == true || isFinishing() == true)
        //            {
        //                return;
        //            }
        //
        //            lockUiComponent();
        //
        //            if (mPlaceDetailLayout != null)
        //            {
        //                if (isAnimation == true)
        //                {
        //                    mPlaceDetailLayout.hideAnimationProductInformationLayout();
        //                    mPlaceDetailLayout.showWishButtonAnimation();
        //                } else
        //                {
        //                    mPlaceDetailLayout.hideProductInformationLayout();
        //                    mPlaceDetailLayout.showWishButton();
        //                }
        //            }
        //
        //            if (Util.isOverAPI21() == true)
        //            {
        //                Window window = getWindow();
        //                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //                window.setStatusBarColor(getResources().getColor(R.color.white));
        //            }
        //
        //            releaseUiComponent();
        //        }

        @Override
        public void showMap()
        {
            if (Util.isInstallGooglePlayService(GourmetDetailActivity.this) == true)
            {
                if (lockUiComponentAndIsLockUiComponent() == true || isFinishing() == true)
                {
                    return;
                }

                GourmetDetailParams gourmetDetailParams = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas();

                Intent intent = ZoomMapActivity.newInstance(GourmetDetailActivity.this//
                    , ZoomMapActivity.SourceType.GOURMET, gourmetDetailParams.name, gourmetDetailParams.address//
                    , gourmetDetailParams.latitude, gourmetDetailParams.longitude, false);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_ZOOMMAP);

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                    AnalyticsManager.Action.GOURMET_DETAIL_MAP_CLICKED, gourmetDetailParams.name, null);
            } else
            {
                Util.installGooglePlayService(GourmetDetailActivity.this);
            }
        }

        @Override
        public void finish()
        {
            GourmetDetailActivity.this.finish();
        }

        @Override
        public void clipAddress(String address)
        {
            GourmetDetailParams gourmetDetailParams = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas();

            Util.clipText(GourmetDetailActivity.this, address);

            DailyToast.showToast(GourmetDetailActivity.this, R.string.message_detail_copy_address, Toast.LENGTH_SHORT);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                AnalyticsManager.Action.GOURMET_DETAIL_ADDRESS_COPY_CLICKED, gourmetDetailParams.name, null);
        }

        @Override
        public void showNavigatorDialog()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            GourmetDetailParams gourmetDetailParams = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas();

            Util.showShareMapDialog(GourmetDetailActivity.this, gourmetDetailParams.name//
                , gourmetDetailParams.latitude, gourmetDetailParams.longitude, false//
                , AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.GOURMET_DETAIL_NAVIGATION_APP_CLICKED//
                , null);
        }

        @Override
        public void onCalendarClick()
        {
            startCalendar(mTodayDateTime, (GourmetBookingDay) mPlaceBookingDay, mPlaceDetail.index, true);
        }

        @Override
        public void onWishClick()
        {
            if (DailyHotel.isLogin() == false)
            {
                DailyToast.showToast(GourmetDetailActivity.this, R.string.toast_msg_please_login, Toast.LENGTH_LONG);

                Intent intent = LoginActivity.newInstance(GourmetDetailActivity.this, AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN_BY_DETAIL_WISHLIST);
            } else
            {
                GourmetDetailActivity.this.onWishButtonClick(PlaceType.FNB, (GourmetDetail) mPlaceDetail);
            }
        }

        @Override
        public void releaseUiComponent()
        {
            GourmetDetailActivity.this.releaseUiComponent();
        }
    };

    private GourmetDetailNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetDetailNetworkController.OnNetworkControllerListener()
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
                    mPlaceBookingDay = new GourmetBookingDay();
                    GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;

                    gourmetBookingDay.setVisitDay(mTodayDateTime.dailyDateTime);
                } else
                {
                    GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;

                    // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                    // 체크인 날짜 체크

                    // 날짜로 비교해야 한다.
                    Calendar todayCalendar = DailyCalendar.getInstance(mTodayDateTime.dailyDateTime, true);
                    Calendar visitCalendar = DailyCalendar.getInstance(gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT), true);

                    // 하루가 지나서 체크인 날짜가 전날짜 인 경우
                    if (todayCalendar.getTimeInMillis() > visitCalendar.getTimeInMillis())
                    {
                        gourmetBookingDay.setVisitDay(mTodayDateTime.dailyDateTime);
                    }
                }

                GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;

                if (mIsShowCalendar == true)
                {
                    unLockUI();
                    startCalendar(mTodayDateTime, gourmetBookingDay, mPlaceDetail.index, false);
                    return;
                }

                ((GourmetDetailNetworkController) mPlaceDetailNetworkController).requestHasCoupon(mPlaceDetail.index,//
                    gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
            } catch (Exception e)
            {
                ExLog.e(e.toString());
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
                        processBooking((GourmetBookingDay) mPlaceBookingDay, (GourmetDetail) mPlaceDetail, mSelectedTicketIndex);
                    }
                }
            } else
            {
                // 입력된 정보가 부족해.
                if (Util.isTextEmpty(user.getEmail(), user.getPhone(), user.getName()) == true)
                {
                    moveToAddSocialUserInformation(user, birthday);
                } else if (Util.isValidatePhoneNumber(user.getPhone()) == false)
                {
                    moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.WRONG_PHONENUMBER, user.getPhone());
                } else
                {
                    processBooking((GourmetBookingDay) mPlaceBookingDay, (GourmetDetail) mPlaceDetail, mSelectedTicketIndex);
                }
            }
        }

        @Override
        public void onGourmetDetailInformation(GourmetDetailParams gourmetDetailParams)
        {
            try
            {
                ((GourmetDetail) mPlaceDetail).setGourmetDetailParmas(gourmetDetailParams);

                if (mInitializeStatus == STATUS_INITIALIZE_NONE)
                {
                    mInitializeStatus = STATUS_INITIALIZE_DATA;
                }

                if (mIsTransitionEnd == true)
                {
                    updateDetailInformationLayout((GourmetBookingDay) mPlaceBookingDay, (GourmetDetail) mPlaceDetail);
                }

                recordAnalyticsGourmetDetail(AnalyticsManager.Screen.DAILYGOURMET_DETAIL, (GourmetBookingDay) mPlaceBookingDay, (GourmetDetail) mPlaceDetail);
            } catch (Exception e)
            {
                DailyToast.showToast(GourmetDetailActivity.this, R.string.act_base_network_connect, Toast.LENGTH_LONG);
                finish();
            } finally
            {
                unLockUI();
            }
        }

        @Override
        public void onHasCoupon(boolean hasCoupon)
        {
            ((GourmetDetail) mPlaceDetail).hasCoupon = hasCoupon;

            ((GourmetDetailNetworkController) mPlaceDetailNetworkController).requestGourmetDetailInformation(((GourmetBookingDay) mPlaceBookingDay).getVisitDay("yyyy-MM-dd"), mPlaceDetail.index);
        }

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

            GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;
            GourmetDetailParams gourmetDetailParams = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas();

            if (isSuccess == true)
            {
                gourmetDetailParams.myWish = true;
                int wishCount = ++gourmetDetailParams.wishCount;
                mPlaceDetailLayout.setWishButtonCount(wishCount);
                mPlaceDetailLayout.setWishButtonSelected(true);
                mPlaceDetailLayout.setUpdateWishPopup(PlaceDetailLayout.WishPopupState.ADD);

                try
                {
                    Map<String, String> params = new HashMap<>();
                    params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
                    params.put(AnalyticsManager.KeyType.NAME, gourmetDetailParams.name);
                    params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mViewPrice));
                    params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                    params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetailParams.category);

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

                        params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
                    }

                    params.put(AnalyticsManager.KeyType.GRADE, gourmetDetailParams.getGrade().name());
                    params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mPlaceDetail.index));
                    params.put(AnalyticsManager.KeyType.RATING, Integer.toString(gourmetDetailParams.ratingValue));

                    String listIndex = mPlaceDetail.entryPosition == -1 //
                        ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mPlaceDetail.entryPosition);

                    params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
                    params.put(AnalyticsManager.KeyType.DAILYCHOICE, mPlaceDetail.isDailyChoice ? "y" : "n");
                    params.put(AnalyticsManager.KeyType.DBENEFIT, Util.isTextEmpty(gourmetDetailParams.benefit) ? "no" : "yes");

                    params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, "1");
                    params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mPlaceDetail.isShowOriginalPrice);


                    AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(//
                        AnalyticsManager.Category.NAVIGATION_,//
                        AnalyticsManager.Action.WISHLIST_ON, gourmetDetailParams.name, params);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            } else
            {
                mPlaceDetailLayout.setWishButtonCount(gourmetDetailParams.wishCount);
                mPlaceDetailLayout.setWishButtonSelected(gourmetDetailParams.myWish);

                if (Util.isTextEmpty(message) == true)
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

            GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;
            GourmetDetailParams gourmetDetailParams = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas();

            if (isSuccess == true)
            {
                gourmetDetailParams.myWish = false;
                int wishCount = --gourmetDetailParams.wishCount;
                mPlaceDetailLayout.setWishButtonCount(wishCount);
                mPlaceDetailLayout.setWishButtonSelected(false);
                mPlaceDetailLayout.setUpdateWishPopup(PlaceDetailLayout.WishPopupState.DELETE);

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
                params.put(AnalyticsManager.KeyType.NAME, gourmetDetailParams.name);
                params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mViewPrice));
                params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetailParams.category);

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

                    params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
                }

                params.put(AnalyticsManager.KeyType.GRADE, gourmetDetailParams.getGrade().name());
                params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mPlaceDetail.index));
                params.put(AnalyticsManager.KeyType.RATING, Integer.toString(gourmetDetailParams.ratingValue));

                String listIndex = mPlaceDetail.entryPosition == -1 //
                    ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mPlaceDetail.entryPosition);

                params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
                params.put(AnalyticsManager.KeyType.DAILYCHOICE, mPlaceDetail.isDailyChoice ? "y" : "n");
                params.put(AnalyticsManager.KeyType.DBENEFIT, Util.isTextEmpty(gourmetDetailParams.benefit) ? "no" : "yes");

                params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
                params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, "1");
                params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mPlaceDetail.isShowOriginalPrice);


                AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(//
                    AnalyticsManager.Category.NAVIGATION_,//
                    AnalyticsManager.Action.WISHLIST_OFF, gourmetDetailParams.name, params);
            } else
            {
                mPlaceDetailLayout.setWishButtonCount(gourmetDetailParams.wishCount);
                mPlaceDetailLayout.setWishButtonSelected(gourmetDetailParams.myWish);

                if (Util.isTextEmpty(message) == true)
                {
                    message = "";
                }

                releaseUiComponent();

                showSimpleDialog(getResources().getString(R.string.dialog_notice2), message//
                    , getResources().getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        @Override
        public void onError(Throwable e)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            GourmetDetailActivity.this.onError(e);
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
                            GourmetDetailActivity.this.onErrorPopupMessage(msgCode, message, null);
                        } else
                        {
                            GourmetDetailActivity.this.onErrorPopupMessage(msgCode, message);
                        }
                    }
                };
            } else
            {
                // 판매 마감시
                if (msgCode == 5)
                {
                    GourmetDetailActivity.this.onErrorPopupMessage(msgCode, message, null);
                } else
                {
                    GourmetDetailActivity.this.onErrorPopupMessage(msgCode, message);
                }
            }
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            GourmetDetailActivity.this.onErrorToastMessage(message);
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
                        GourmetDetailActivity.this.onErrorResponse(call, response);
                        finish();
                    }
                };
            } else
            {
                GourmetDetailActivity.this.onErrorResponse(call, response);
                finish();
            }
        }
    };
}
