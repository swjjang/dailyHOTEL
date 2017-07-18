package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.entity.GourmetMenuImage;
import com.daily.dailyhotel.repository.local.model.AnalyticsParam;
import com.daily.dailyhotel.screen.home.gourmet.detail.menus.GourmetMenusActivity;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.ProductImageInformation;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.network.model.TrueVRParams;
import com.twoheart.dailyhotel.place.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.ImageDetailListActivity;
import com.twoheart.dailyhotel.screen.common.TrueVRActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
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
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
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
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class GourmetDetailActivity extends PlaceDetailActivity
{
    int mSelectedTicketIndex;
    private boolean mFirstCheckPrice;
    private boolean mRefreshCheckPrice;
    boolean mIsListSoldOut; // 리스트에서 솔드아웃인지 체크한다.

    /**
     * 리스트, 검색 결과, 위시리스트, 홈, 추천 목록 에서 호출
     *
     * @param context
     * @param gourmetBookingDay
     * @param index
     * @param name
     * @param imageUrl
     * @param category
     * @param isSoldOut
     * @param analyticsParam
     * @param isUsedMultiTransition
     * @return
     */
    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, int index //
        , String name, String imageUrl, String category, boolean isSoldOut //
        , AnalyticsParam analyticsParam, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, category);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SOLDOUT, isSoldOut);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ANALYTICS_PARAM, analyticsParam);
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
    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, int gourmetIndex//
        , boolean isShowCalendar, boolean isShowVR, boolean isUsedMultiTransition)
    {
        Intent intent = new Intent(context, GourmetDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmetIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, isShowCalendar);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_VR_FLAG, isShowVR);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        AnalyticsParam analyticsParam = new AnalyticsParam();
        analyticsParam.placeIndex = gourmetIndex;
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

        mPlaceBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        mIsShowCalendar = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);
        mIsShowVR = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_VR_FLAG, false);

        if (mPlaceBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        mPlaceDetail = createPlaceDetail(intent);

        String placeName = null;
        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_PLACENAME) == true)
        {
            placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACENAME);
        }

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL) == true)
        {
            mDefaultImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);
        }

        // 최근 본 업장 저장
        RecentlyPlaceUtil.addRecentlyItemAsync(RecentlyPlaceUtil.ServiceType.GOURMET //
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

            initLayout(null, null, false);
        } else
        {
            mIsDeepLink = false;

            if (placeName == null)
            {
                Util.restartApp(this);
                return;
            }

            //            mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
            //            mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);
            mViewPrice = mAnalyticsParam != null ? mAnalyticsParam.discountPrice : 0;
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

        // VR 여부 판단
        //        mPlaceDetailNetworkController.requestHasVRList(PlaceType.FNB, mPlaceDetail.index, "RESTAURANT");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_GOURMET_PRODUCT_DETAIL:
                switch (resultCode)
                {
                    // 결제 하기 선택
                    case RESULT_OK:
                        mDontReloadAtOnResume = true;

                        // 결재하기 호출
                        if (data != null)
                        {
                            int index = data.getIntExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_INDEX, -1);

                            if (index >= 0)
                            {
                                onReservation(index);
                            }
                        }
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

        mPlaceDetailLayout.setStatusBarHeight(this);
        mPlaceDetailLayout.setIsUsedMultiTransitions(mIsUsedMultiTransition);

        if (mIsDeepLink == false && mIsUsedMultiTransition == true)
        {
            initTransLayout(placeName, imageUrl, isFromMap);
        } else
        {
            mPlaceDetailLayout.setDefaultImage(imageUrl);
        }

        setLockUICancelable(true);
        initToolbar(placeName);

        mOnEventListener.hideActionBar(false);
    }

    private void initTransLayout(String placeName, String imageUrl, boolean isFromMap)
    {
        if (DailyTextUtils.isTextEmpty(imageUrl) == true)
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
        //        int entryIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        //        String isShowOriginalPrice = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE);
        //        int listCount = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        //        boolean isDailyChoice = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        return new GourmetDetail(index);
    }

    @Override
    protected void requestCommonDateTimeNSoldOutList(int placeIndex)
    {
        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime() //
            , mPlaceDetailCalendarImpl.getGourmetUnavailableDates(placeIndex, GourmetCalendarActivity.DAYCOUNT_OF_MAX, false) //
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
            }).subscribe(new Consumer<TodayDateTime>()
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
                startCalendar(mTodayDateTime, gourmetBookingDay, mPlaceDetail.index, mSoldOutList, false);
                return;
            }

            ((GourmetDetailNetworkController) mPlaceDetailNetworkController).requestHasCoupon(mPlaceDetail.index,//
                gourmetBookingDay.getVisitDay("yyyy-MM-dd"));

            mPlaceDetailNetworkController.requestPlaceReviewScores(PlaceType.FNB, mPlaceDetail.index);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
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

            String name = DailyUserPreference.getInstance(GourmetDetailActivity.this).getName();

            if (DailyTextUtils.isTextEmpty(name) == true)
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
        } catch (Exception e)
        {
            showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(GourmetDetailActivity.this, "com.kakao.talk");
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

        GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) placeBookingDay;
        GourmetDetailParams gourmetDetailParams = ((GourmetDetail) placeDetail).getGourmetDetailParmas();

        try
        {
            String name = DailyUserPreference.getInstance(GourmetDetailActivity.this).getName();

            if (DailyTextUtils.isTextEmpty(name) == true)
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
        try
        {
            // 카카오톡 패키지 설치 여부
            getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            GourmetDetailParams gourmetDetailParams = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas();

            startActivityForResult(HappyTalkCategoryDialog.newInstance(this//
                , HappyTalkCategoryDialog.CallScreen.SCREEN_GOURMET_DETAIL//
                , gourmetDetailParams.index, 0, gourmetDetailParams.name)//
                , Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
        } catch (Exception e)
        {
            showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(GourmetDetailActivity.this, "com.kakao.talk");
                    }
                }, null);
        }


        //        try
        //        {
        //            startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/%40%EB%8D%B0%EC%9D%BC%EB%A6%AC%EA%B3%A0%EB%A9%94")));
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

        boolean isBenefit = DailyTextUtils.isTextEmpty(gourmetDetailParams.benefit) == false;

        Intent intent = GourmetPaymentActivity.newInstance(GourmetDetailActivity.this, gourmetDetailParams.name, gourmetProduct//
            , gourmetBookingDay, imageUrl, gourmetDetailParams.category, gourmetDetail.index, isBenefit //
            , gourmetDetailParams.ratingValue, mAnalyticsParam);

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

            mPlaceDetail = new GourmetDetail(mPlaceDetail.index);

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
                        AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, AnalyticsManager.Action.COUPON_LOGIN, AnalyticsManager.Label.LOGIN_, null);

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

        RecentlyPlaceUtil.addRecentlyItemAsync(RecentlyPlaceUtil.ServiceType.GOURMET //
            , gourmetDetail.index, gourmetDetailParams.name, null, gourmetDetailParams.imgUrl, false);

        if (mPlaceDetailLayout != null)
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            if (gourmetBookingDay == null || gourmetDetail == null || gourmetDetail.getGourmetDetailParmas() == null)
            {
                setWishTextView(false, 0);
            } else
            {
                setWishTextView(gourmetDetailParams.myWish, gourmetDetailParams.wishCount);
            }

            ((GourmetDetailLayout) mPlaceDetailLayout).setDetail(gourmetBookingDay, gourmetDetail//
                , mPlaceReviewScores, mCurrentImage, displayMetrics.densityDpi);
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

    void startCalendar(TodayDateTime todayDateTime, GourmetBookingDay gourmetBookingDay, int placeIndex, ArrayList<Integer> soldoutList, boolean isAnimation)
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
            todayDateTime, gourmetBookingDay, placeIndex, callByScreen, soldoutList, true, isAnimation);
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
        setWishTextView(isExpectSelected, wishCount);

        if (isExpectSelected == true)
        {
            mPlaceDetailNetworkController.requestAddWishList(placeType, gourmetDetail.index);
        } else
        {
            mPlaceDetailNetworkController.requestRemoveWishList(placeType, gourmetDetail.index);
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
                        startActivityForResult(TrueVRActivity.newInstance(GourmetDetailActivity.this, mPlaceDetail.index, mTrueVRParamsList//
                            , PlaceType.FNB, ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas().category), CODE_REQUEST_ACTIVITY_TRUEVIEW);
                    }
                }, null, null, new OnCheckDialogStateListener()
                {
                    @Override
                    public void onState(View view, boolean checked)
                    {
                        unLockUI();
                        DailyPreference.getInstance(GourmetDetailActivity.this).setTrueVRCheckDataGuide(checked);
                    }
                }, true);
        } else
        {
            startActivityForResult(TrueVRActivity.newInstance(GourmetDetailActivity.this, mPlaceDetail.index, mTrueVRParamsList//
                , PlaceType.FNB, ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas().category), CODE_REQUEST_ACTIVITY_TRUEVIEW);
        }

        try
        {
            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                AnalyticsManager.Action.TRUE_VR_CLICK, Integer.toString(((GourmetDetail) mPlaceDetail).getGourmetDetailParmas().index), null);
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
            DailyToast.showToast(GourmetDetailActivity.this, R.string.toast_msg_please_login, Toast.LENGTH_LONG);

            Intent intent = LoginActivity.newInstance(GourmetDetailActivity.this, AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN_BY_DETAIL_WISHLIST);
        } else
        {
            GourmetDetailActivity.this.onWishButtonClick(PlaceType.FNB, (GourmetDetail) mPlaceDetail);
        }
    }

    public void onReservation(int index)
    {
        if (mPlaceDetail == null)
        {
            return;
        }

        GourmetProduct gourmetProduct = ((GourmetDetail) mPlaceDetail).getProduct(index);

        if (gourmetProduct == null)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            finish();
            return;
        }

        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        mSelectedTicketIndex = index;

        if (DailyHotel.isLogin() == false)
        {
            startLoginActivity(AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
        } else
        {
            lockUI();
            mPlaceDetailNetworkController.requestProfile();
        }

        GourmetDetailParams gourmetDetailParams = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas();

        if (gourmetDetailParams != null)
        {
            String label = String.format(Locale.KOREA, "%s-%s", gourmetDetailParams.name, gourmetProduct.ticketName);
            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.BOOKING_CLICKED, label, recordAnalyticsBooking((GourmetBookingDay) mPlaceBookingDay, ((GourmetDetail) mPlaceDetail), gourmetProduct));
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
            params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(gourmetDetailParams.benefit) ? "no" : "yes");

            if (gourmetDetail.getProductList() == null || gourmetDetail.getProductList().size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(gourmetDetail.getProduct(0).discountPrice));
            }

            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetDetail.index));
            params.put(AnalyticsManager.KeyType.DATE, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

            params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(mViewPrice));
            params.put(AnalyticsManager.KeyType.VISIT_DATE, gourmetBookingDay.getVisitDay("yyyyMMdd"));

            String listIndex = mAnalyticsParam.entryPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);

            String placeCount = mAnalyticsParam.totalListCount == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.totalListCount);

            params.put(AnalyticsManager.KeyType.PLACE_COUNT, placeCount);

            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(gourmetDetailParams.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPriceYn);
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, "1");
            params.put(AnalyticsManager.KeyType.NRD, gourmetDetailParams.sticker != null ? "y" : "n");

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

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

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

            ((GourmetDetailLayout) mPlaceDetailLayout).scrollProduct();

            unLockUI();
        }

        @Override
        public void onProductClick(int index)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            GourmetDetail gourmetDetail = (GourmetDetail) mPlaceDetail;

            // 메뉴 목록 보여주기
            Observable.just(gourmetDetail.getProductList()).subscribeOn(Schedulers.io()).map(new Function<List<GourmetProduct>, List<GourmetMenu>>()
            {
                @Override
                public List<GourmetMenu> apply(@NonNull List<GourmetProduct> gourmetProductList) throws Exception
                {
                    List<GourmetMenu> gourmetMenuList = new ArrayList<>(gourmetProductList.size());

                    for (GourmetProduct gourmetProduct : gourmetProductList)
                    {
                        GourmetMenu gourmetMenu = new GourmetMenu();
                        gourmetMenu.index = gourmetProduct.index;
                        gourmetMenu.saleIdx = gourmetProduct.saleIdx;
                        gourmetMenu.ticketName = gourmetProduct.ticketName;
                        gourmetMenu.price = gourmetProduct.price;
                        gourmetMenu.discountPrice = gourmetProduct.discountPrice;
                        gourmetMenu.menuBenefit = gourmetProduct.menuBenefit;
                        gourmetMenu.needToKnow = gourmetProduct.needToKnow;
                        gourmetMenu.openTime = gourmetProduct.openTime;
                        gourmetMenu.closeTime = gourmetProduct.closeTime;
                        gourmetMenu.lastOrderTime = gourmetProduct.lastOrderTime;
                        gourmetMenu.menuSummary = gourmetProduct.menuSummary;
                        gourmetMenu.reserveCondition = gourmetProduct.reserveCondition;

                        List<GourmetMenuImage> gourmetMenuImageList = new ArrayList<>();
                        for (ProductImageInformation productImageInformation : gourmetProduct.getImageList())
                        {
                            GourmetMenuImage gourmetMenuImage = new GourmetMenuImage();
                            gourmetMenuImage.url = productImageInformation.imageUrl;
                            gourmetMenuImage.caption = productImageInformation.imageDescription;

                            gourmetMenuImageList.add(gourmetMenuImage);
                        }

                        gourmetMenu.setImageList(gourmetMenuImageList);
                        gourmetMenu.setMenuDetailList(gourmetProduct.getMenuDetailList());

                        gourmetMenuList.add(gourmetMenu);
                    }

                    return gourmetMenuList;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<GourmetMenu>>()
            {
                @Override
                public void accept(@NonNull List<GourmetMenu> gourmetMenuList) throws Exception
                {
                    startActivityForResult(GourmetMenusActivity.newInstance(GourmetDetailActivity.this, gourmetMenuList, index)//
                        , CODE_REQUEST_ACTIVITY_GOURMET_PRODUCT_DETAIL);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    ExLog.d(throwable.toString());
                    unLockUI();
                }
            });
        }

        @Override
        public void onReviewClick()
        {
            if (mPlaceDetail == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            String category = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas().category;

            startActivityForResult(GourmetReviewActivity.newInstance(GourmetDetailActivity.this//
                , mPlaceDetail.index, category, mPlaceReviewScores), Constants.CODE_REQUEST_ACTIVITY_PLACE_REVIEW);

            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.TRUE_REVIEW_CLICK, AnalyticsManager.Label.GOURMET, null);
        }

        @Override
        public void onMoreProductListClick()
        {
            if (mPlaceDetail == null || mPlaceDetailLayout == null)
            {
                return;
            }

            if (((GourmetDetailLayout) mPlaceDetailLayout).isOpenedProductMoreList() == true)
            {
                ((GourmetDetailLayout) mPlaceDetailLayout).closeMoreProductList();
            } else
            {
                ((GourmetDetailLayout) mPlaceDetailLayout).openMoreProductList();
            }
        }

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

            showCallDialog(PlaceType.FNB);

            AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.GOURMET_DETAIL, null);
        }

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

            DailyTextUtils.clipText(GourmetDetailActivity.this, address);

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
            startCalendar(mTodayDateTime, (GourmetBookingDay) mPlaceBookingDay, mPlaceDetail.index, mSoldOutList, true);
        }

        @Override
        public void releaseUiComponent()
        {
            GourmetDetailActivity.this.releaseUiComponent();
        }

        @Override
        public void onTrueVRTooltipClick()
        {
            if (mPlaceDetailLayout != null && mPlaceDetailLayout.isTrueVRTooltipVisibility() == true)
            {
                mPlaceDetailLayout.hideAnimationTooltip();
                DailyPreference.getInstance(GourmetDetailActivity.this).setTrueVRViewTooltip(false);
            }
        }
    };

    private GourmetDetailNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetDetailNetworkController.OnNetworkControllerListener()
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
                        processBooking((GourmetBookingDay) mPlaceBookingDay, (GourmetDetail) mPlaceDetail, mSelectedTicketIndex);
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

                // analyticsParam 갱신용 - 딥링크시 고메 index 빼고는 알 수 없음 - 상세에서 받아온 정보로 갱신
                mAnalyticsParam.placeName = gourmetDetailParams.name;
                mAnalyticsParam.gradeCode = gourmetDetailParams.getGrade().name();
                mAnalyticsParam.gradeName = gourmetDetailParams.getGrade().getName(GourmetDetailActivity.this);
                //                mAnalyticsParam. // TODO : 여기 추가로 들어간... 카테고리 어찌 할지

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
                setWishTextView(true, wishCount);
                mPlaceDetailLayout.setUpdateWishPopup(PlaceDetailLayout.WishPopupState.ADD);

                try
                {
                    Map<String, String> params = new HashMap<>();
                    params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
                    params.put(AnalyticsManager.KeyType.NAME, gourmetDetailParams.name);
                    params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mViewPrice));
                    params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                    params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetailParams.category);

                    params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
                    params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
                    params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

                    params.put(AnalyticsManager.KeyType.GRADE, gourmetDetailParams.getGrade().name());
                    params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mPlaceDetail.index));
                    params.put(AnalyticsManager.KeyType.RATING, Integer.toString(gourmetDetailParams.ratingValue));

                    String listIndex = mAnalyticsParam.entryPosition == -1 //
                        ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

                    params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
                    params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
                    params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(gourmetDetailParams.benefit) ? "no" : "yes");

                    params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
                    params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, "1");
                    params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPriceYn);


                    AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(//
                        AnalyticsManager.Category.NAVIGATION_,//
                        AnalyticsManager.Action.WISHLIST_ON, gourmetDetailParams.name, params);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            } else
            {
                setWishTextView(gourmetDetailParams.myWish, gourmetDetailParams.wishCount);

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

            GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;
            GourmetDetailParams gourmetDetailParams = ((GourmetDetail) mPlaceDetail).getGourmetDetailParmas();

            if (isSuccess == true)
            {
                gourmetDetailParams.myWish = false;
                int wishCount = --gourmetDetailParams.wishCount;
                setWishTextView(false, wishCount);
                mPlaceDetailLayout.setUpdateWishPopup(PlaceDetailLayout.WishPopupState.DELETE);

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
                params.put(AnalyticsManager.KeyType.NAME, gourmetDetailParams.name);
                params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mViewPrice));
                params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
                params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetailParams.category);

                params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
                params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
                params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

                params.put(AnalyticsManager.KeyType.GRADE, gourmetDetailParams.getGrade().name());
                params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(mPlaceDetail.index));
                params.put(AnalyticsManager.KeyType.RATING, Integer.toString(gourmetDetailParams.ratingValue));

                String listIndex = mAnalyticsParam.entryPosition == -1 //
                    ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

                params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
                params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
                params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(gourmetDetailParams.benefit) ? "no" : "yes");

                params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
                params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, "1");
                params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.showOriginalPriceYn);


                AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(//
                    AnalyticsManager.Category.NAVIGATION_,//
                    AnalyticsManager.Action.WISHLIST_OFF, gourmetDetailParams.name, params);
            } else
            {
                setWishTextView(gourmetDetailParams.myWish, gourmetDetailParams.wishCount);

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
            if (mIsListSoldOut == false)
            {
                setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            }

            GourmetDetailActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            if (mIsListSoldOut == false)
            {
                setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            }

            GourmetDetailActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(final int msgCode, final String message)
        {
            if (mIsListSoldOut == false)
            {
                setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            }

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
                            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_sold_out)//
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
                            GourmetDetailActivity.this.onErrorPopupMessage(msgCode, message);
                        }
                    }
                };
            } else
            {
                // 판매 마감시
                if (msgCode == 5)
                {
                    showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_sold_out)//
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
                    GourmetDetailActivity.this.onErrorPopupMessage(msgCode, message);
                }
            }
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            if (mIsListSoldOut == false)
            {
                setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            }

            GourmetDetailActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(final Call call, final Response response)
        {
            if (mIsListSoldOut == false)
            {
                setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            }

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
