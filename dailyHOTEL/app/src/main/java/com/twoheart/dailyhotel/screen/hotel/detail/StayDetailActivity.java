package com.twoheart.dailyhotel.screen.hotel.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.model.RoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.place.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceDetailNetworkController;
import com.twoheart.dailyhotel.screen.common.ImageDetailListActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayDetailCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.payment.HotelPaymentActivity;
import com.twoheart.dailyhotel.screen.information.coupon.SelectCouponDialogActivity;
import com.twoheart.dailyhotel.screen.information.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.AlphaTransition;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.TextTransition;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class StayDetailActivity extends PlaceDetailActivity
{
    private RoomInformation mSelectedRoomInformation;
    private boolean mCheckPrice;

    /**
     * 리스트에서 호출
     *
     * @param context
     * @param saleTime
     * @param province
     * @param stay
     * @param listCount
     * @return
     */
    public static Intent newInstance(Context context, SaleTime saleTime, Province province, Stay stay, int listCount)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, stay.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, stay.nights);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, stay.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, stay.imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, stay.categoryCode);
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

        return intent;
    }


    /**
     * 딥링크로 호출
     *
     * @param context
     * @param saleTime
     * @param nights
     * @param staytIndex
     * @param isShowCalendar
     * @return
     */
    public static Intent newInstance(Context context, SaleTime saleTime, int nights, int staytIndex, int roomIndex, boolean isShowCalendar)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, staytIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ROOMINDEX, roomIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, isShowCalendar);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        return intent;
    }

    /**
     * 검색 결과에서 호출
     *
     * @param context
     * @param saleTime
     * @param stay
     * @param listCount
     * @return
     */
    public static Intent newInstance(Context context, SaleTime saleTime, Stay stay, int listCount)
    {
        Intent intent = new Intent(context, StayDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, stay.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, stay.nights);
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

        mSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        boolean isShowCalendar = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_CALENDAR_FLAG, false);

        mPlaceDetail = createPlaceDetail(intent);

        // 최근 본 업장 저장
        String preferenceRecentPlaces = DailyPreference.getInstance(this).getStayRecentPlaces();
        RecentPlaces recentPlaces = new RecentPlaces(preferenceRecentPlaces);
        recentPlaces.add(mPlaceDetail.index);
        DailyPreference.getInstance(this).setStayRecentPlaces(recentPlaces.toString());

        if (mSaleTime == null || mPlaceDetail == null)
        {
            Util.restartApp(this);
            return;
        }

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_TYPE) == true)
        {
            mIsDeepLink = true;
            mDontReloadAtOnResume = false;

            mOpenTicketIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ROOMINDEX, 0);

            initLayout(null, null, null, false);

            if (isShowCalendar == true)
            {
                startCalendar(mSaleTime, ((StayDetail) mPlaceDetail).nights, mPlaceDetail.index, false);
            }
        } else
        {
            mIsDeepLink = false;

            String placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME);
            mDefaultImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);
            ((StayDetail) mPlaceDetail).categoryCode = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CATEGORY);

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

            initTransition();
            initLayout(placeName, mDefaultImageUrl, grade, isFromMap);

            if (isShowCalendar == true)
            {
                startCalendar(mSaleTime, ((StayDetail) mPlaceDetail).nights, mPlaceDetail.index, true);
            }
        }
    }

    private void initTransition()
    {
        if (Util.isUsedMutilTransition() == true)
        {
            mDontReloadAtOnResume = true;

            TransitionSet intransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            Transition inTextTransition = new TextTransition(getResources().getColor(R.color.white), getResources().getColor(R.color.default_text_c323232)//
                , 17, 18, new LinearInterpolator());
            inTextTransition.addTarget(getString(R.string.transition_place_name));
            intransitionSet.addTransition(inTextTransition);

            Transition inBottomAlhpaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
            inBottomAlhpaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
            intransitionSet.addTransition(inBottomAlhpaTransition);

            Transition inTopAlhpaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
            inTopAlhpaTransition.addTarget(getString(R.string.transition_gradient_top_view));
            intransitionSet.addTransition(inTopAlhpaTransition);

            getWindow().setSharedElementEnterTransition(intransitionSet);

            TransitionSet outTransitionSet = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP);
            Transition outTextTransition = new TextTransition(getResources().getColor(R.color.default_text_c323232), getResources().getColor(R.color.white)//
                , 18, 17, new LinearInterpolator());
            outTextTransition.addTarget(getString(R.string.transition_place_name));
            outTransitionSet.addTransition(outTextTransition);

            Transition outBottomAlhpaTransition = new AlphaTransition(0.0f, 1.0f, new LinearInterpolator());
            outBottomAlhpaTransition.addTarget(getString(R.string.transition_gradient_bottom_view));
            outTransitionSet.addTransition(outBottomAlhpaTransition);

            Transition outTopAlhpaTransition = new AlphaTransition(1.0f, 0.0f, new LinearInterpolator());
            outTopAlhpaTransition.addTarget(getString(R.string.transition_gradient_top_view));
            outTransitionSet.addTransition(outTopAlhpaTransition);

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
                    lockUI();
                    mPlaceDetailNetworkController.requestCommonDatetime();
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
    }

    private void initLayout(String placeName, String imageUrl, Stay.Grade grade, boolean isFromMap)
    {
        setContentView(mPlaceDetailLayout.onCreateView(R.layout.activity_placedetail));

        if (mIsDeepLink == false && Util.isUsedMutilTransition() == true)
        {
            ininTransLayout(placeName, imageUrl, grade, isFromMap);
        } else
        {
            mPlaceDetailLayout.setDefaultImage(imageUrl);
        }

        mPlaceDetailLayout.setStatusBarHeight(this);

        setLockUICancelable(true);
        initToolbar(placeName);

        mOnEventListener.hideActionBar(false);
    }

    private void ininTransLayout(String placeName, String imageUrl, Stay.Grade grade, boolean isFromMap)
    {
        if (Util.isTextEmpty(placeName, imageUrl) == true && grade != null)
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
        int nights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 0);
        int entryPosition = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        String isShowOriginalPrice = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE);
        int listCount = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_LIST_COUNT, -1);
        boolean isDailyChoice = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        return new StayDetail(stayIndex, nights, entryPosition, isShowOriginalPrice, listCount, isDailyChoice);
    }

    @Override
    protected void shareKakao(PlaceDetail placeDetail, String imageUrl)
    {
        String name = DailyPreference.getInstance(StayDetailActivity.this).getUserName();

        if (Util.isTextEmpty(name) == true)
        {
            name = getString(R.string.label_friend) + "가";
        } else
        {
            name += "님이";
        }

        StayDetail stayDetail = (StayDetail) placeDetail;

        KakaoLinkManager.newInstance(StayDetailActivity.this).shareHotel(name, stayDetail.name, stayDetail.address//
            , stayDetail.index//
            , mDefaultImageUrl//
            , mSaleTime, stayDetail.nights);

        SaleTime checkOutSaleTime = mSaleTime.getClone(mSaleTime.getOffsetDailyDay() + stayDetail.nights);

        HashMap<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.NAME, stayDetail.name);
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayDetail.index));
        params.put(AnalyticsManager.KeyType.CHECK_IN, mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
        params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
        params.put(AnalyticsManager.KeyType.CURRENT_TIME, DailyCalendar.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));

        AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , Action.SOCIAL_SHARE_CLICKED, stayDetail.name, params);
    }

    private void processBooking(SaleTime saleTime, StayDetail stayDetail, RoomInformation roomInformation)
    {
        if (saleTime == null || stayDetail == null || roomInformation == null)
        {
            return;
        }

        String imageUrl = null;
        ArrayList<ImageInformation> mImageInformationList = stayDetail.getImageInformationList();

        if (mImageInformationList != null && mImageInformationList.size() > 0)
        {
            imageUrl = mImageInformationList.get(0).url;
        }

        roomInformation.categoryCode = stayDetail.categoryCode;

        Intent intent = HotelPaymentActivity.newInstance(StayDetailActivity.this, roomInformation//
            , saleTime, imageUrl, stayDetail.index, !Util.isTextEmpty(stayDetail.benefit) //
            , mProvince, mArea, stayDetail.isShowOriginalPrice, stayDetail.entryPosition //
            , stayDetail.isDailyChoice, stayDetail.ratingValue);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
            SaleTime checkOutSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);

            if (checkInSaleTime == null || checkOutSaleTime == null)
            {
                return;
            }

            lockUI();

            mSaleTime = checkInSaleTime;

            int nights = checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();
            mPlaceDetail = new StayDetail(mPlaceDetail.index, nights, mPlaceDetail.entryPosition, //
                mPlaceDetail.isShowOriginalPrice, mPlaceDetail.listCount, mPlaceDetail.isDailyChoice);

            ((StayDetailNetworkController) mPlaceDetailNetworkController).requestHasCoupon(mPlaceDetail.index, mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), nights);
        }
    }

    @Override
    protected void hideProductInformationLayout()
    {
        mOnEventListener.hideProductInformationLayout();
    }

    @Override
    protected void doBooking()
    {
        mOnEventListener.doBooking();
    }

    @Override
    protected void downloadCoupon()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, Action.HOTEL_COUPON_DOWNLOAD, mPlaceDetail.name, null);

        if (DailyHotel.isLogin() == false)
        {
            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_detail_please_login), //
                getString(R.string.dialog_btn_login_for_benefit), getString(R.string.dialog_btn_text_close), //
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, Action.COUPON_LOGIN, AnalyticsManager.Label.LOGIN, null);

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
            Intent intent = SelectCouponDialogActivity.newInstance(this, mPlaceDetail.index, mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), //
                ((StayDetail) mPlaceDetail).nights, ((StayDetail) mPlaceDetail).categoryCode, mPlaceDetail.name);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_DOWNLOAD_COUPON);
        }
    }

    private void startCalendar(SaleTime saleTime, int nights, int placeIndex, boolean isAnimation)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = StayDetailCalendarActivity.newInstance(StayDetailActivity.this, saleTime, //
            nights, placeIndex, AnalyticsManager.ValueType.DETAIL, true, isAnimation);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.DETAIL, null);
    }

    private void recordAnalyticsStayDetail(String screen, SaleTime saleTime, StayDetail stayDetail)
    {
        if (stayDetail == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, stayDetail.name);
            params.put(AnalyticsManager.KeyType.GRADE, stayDetail.grade.getName(StayDetailActivity.this)); // 14
            params.put(AnalyticsManager.KeyType.DBENEFIT, Util.isTextEmpty(stayDetail.benefit) ? "no" : "yes"); // 3

            if (stayDetail.getSaleRoomList() == null || stayDetail.getSaleRoomList().size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(stayDetail.getSaleRoomList().get(0).averageDiscount));
            }

            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(stayDetail.nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayDetail.index)); // 15

            SaleTime checkOutSaleTime = saleTime.getClone(saleTime.getOffsetDailyDay() + stayDetail.nights);

            params.put(AnalyticsManager.KeyType.CHECK_IN, saleTime.getDayOfDaysDateFormat("yyyy-MM-dd")); // 1
            params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd")); // 2

            params.put(AnalyticsManager.KeyType.ADDRESS, stayDetail.address);

            if (Util.isTextEmpty(stayDetail.categoryCode) == true) //
            {
                params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, stayDetail.categoryCode);
                params.put(AnalyticsManager.KeyType.CATEGORY, stayDetail.categoryCode);
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

                params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(mViewPrice));
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, Long.toString(saleTime.getDayOfDaysDate().getTime()));

            String listIndex = stayDetail.entryPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(stayDetail.entryPosition);

            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);

            String placeCount = stayDetail.listCount == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(stayDetail.listCount);

            params.put(AnalyticsManager.KeyType.PLACE_COUNT, placeCount);

            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetail.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, stayDetail.isShowOriginalPrice);
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, stayDetail.isDailyChoice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayDetail.nights));

            AnalyticsManager.getInstance(StayDetailActivity.this).recordScreen(screen, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private Map<String, String> recordAnalyticsBooking(SaleTime saleTime, StayDetail stayDetail, RoomInformation roomInformation)
    {
        if (saleTime == null || stayDetail == null || roomInformation == null)
        {
            return null;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, stayDetail.name);
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(stayDetail.nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayDetail.index));
            params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, stayDetail.categoryCode);

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

            params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.toString(roomInformation.averageDiscount));
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, Long.toString(saleTime.getDayOfDaysDate().getTime()));

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

    private StayDetailLayout.OnEventListener mOnEventListener = new StayDetailLayout.OnEventListener()
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

            ArrayList<ImageInformation> imageInformationArrayList = placeDetail.getImageInformationList();
            if (imageInformationArrayList.size() == 0)
            {
                return;
            }

            lockUiComponent();

            Intent intent = ImageDetailListActivity.newInstance(StayDetailActivity.this, placeDetail.name, imageInformationArrayList, mCurrentImage);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_IMAGELIST);
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
                mPlaceDetailLayout.showAnimationProductInformationLayout();
            }

            if (Util.isOverAPI21() == true)
            {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.textView_textColor_shadow_soldout));
            }

            releaseUiComponent();

            recordAnalyticsStayDetail(Screen.DAILYHOTEL_DETAIL_ROOMTYPE, mSaleTime, (StayDetail) mPlaceDetail);
            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.ROOM_TYPE_CLICKED, mPlaceDetail.name, null);
        }

        @Override
        public void hideProductInformationLayout()
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            if (mPlaceDetailLayout != null)
            {
                mPlaceDetailLayout.hideAnimationProductInformationLayout();
            }

            if (Util.isOverAPI21() == true)
            {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.white));
            }

            releaseUiComponent();
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

                Intent intent = ZoomMapActivity.newInstance(StayDetailActivity.this//
                    , ZoomMapActivity.SourceType.HOTEL, mPlaceDetail.name, mPlaceDetail.address//
                    , mPlaceDetail.latitude, mPlaceDetail.longitude, mPlaceDetail.isOverseas);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_ZOOMMAP);

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                    Action.HOTEL_DETAIL_MAP_CLICKED, mPlaceDetail.name, null);
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
            Util.clipText(StayDetailActivity.this, address);

            DailyToast.showToast(StayDetailActivity.this, R.string.message_detail_copy_address, Toast.LENGTH_SHORT);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                Action.HOTEL_DETAIL_ADDRESS_COPY_CLICKED, mPlaceDetail.name, null);
        }

        @Override
        public void showNavigatorDialog()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Util.showShareMapDialog(StayDetailActivity.this, mPlaceDetail.name//
                , mPlaceDetail.latitude, mPlaceDetail.longitude, mPlaceDetail.isOverseas//
                , AnalyticsManager.Category.HOTEL_BOOKINGS//
                , AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED//
                , null);
        }

        @Override
        public void onCalendarClick()
        {
            startCalendar(mSaleTime, ((StayDetail) mPlaceDetail).nights, mPlaceDetail.index, true);
        }

        @Override
        public void doBooking()
        {
            doBooking(mSelectedRoomInformation);
        }

        @Override
        public void downloadCoupon()
        {
            StayDetailActivity.this.downloadCoupon();
        }

        @Override
        public void doBooking(RoomInformation roomInformation)
        {
            if (roomInformation == null)
            {
                finish();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mSelectedRoomInformation = roomInformation;

            if (DailyHotel.isLogin() == false)
            {
                startLoginActivity(Screen.DAILYHOTEL_DETAIL);
            } else
            {
                lockUI();
                mPlaceDetailNetworkController.requestProfile();
            }

            String label = String.format("%s-%s", mPlaceDetail.name, mSelectedRoomInformation.roomName);
            AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.BOOKING_CLICKED, label, recordAnalyticsBooking(mSaleTime, (StayDetail) mPlaceDetail, roomInformation));
        }

        @Override
        public void onChangedViewPrice(int type)
        {
            ((StayDetailLayout) mPlaceDetailLayout).setChangedViewPrice(type);
        }

        @Override
        public void setWishList(boolean isAdded, int placeIndex)
        {
            if (isAdded == true)
            {
                mPlaceDetailNetworkController.requestAddWishList(PlaceType.HOTEL, placeIndex);
            } else
            {
                mPlaceDetailNetworkController.requestRemoveWishList(PlaceType.HOTEL, placeIndex);
            }
        }

        @Override
        public void onWishListButtonClick()
        {
            if (DailyHotel.isLogin() == false)
            {
                DailyToast.showToast(StayDetailActivity.this, R.string.toast_msg_please_login, Toast.LENGTH_LONG);

                Intent intent = LoginActivity.newInstance(StayDetailActivity.this, Screen.DAILYHOTEL_DETAIL);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN_BY_DETAIL_WISHLIST);
            } else
            {
                if (isLockUiComponent() == true || isFinishing() == true)
                {
                    return;
                }

                lockUiComponent();

                mPlaceDetailLayout.startWishListButtonClick();
            }
        }

        @Override
        public void releaseUiComponent()
        {
            StayDetailActivity.this.releaseUiComponent();
        }
    };


    private StayDetailNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StayDetailNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCommonDateTime(long currentDateTime, long dailyDateTime)
        {
            try
            {
                if (mIsDeepLink == true)
                {
                    mSaleTime.setCurrentTime(currentDateTime);
                    long shareDailyTime = mSaleTime.getDayOfDaysDate().getTime();

                    int shareDailyDay = Integer.parseInt(DailyCalendar.format(shareDailyTime, "yyyyMMdd", TimeZone.getTimeZone("GMT")));
                    int todayDailyDay = Integer.parseInt(DailyCalendar.format(dailyDateTime, "yyyyMMdd", TimeZone.getTimeZone("GMT")));

                    // 지난 날의 호텔인 경우.
                    if (shareDailyDay < todayDailyDay)
                    {
                        DailyToast.showToast(StayDetailActivity.this, R.string.toast_msg_dont_past_hotelinfo, Toast.LENGTH_LONG);
                        finish();
                        return;
                    }
                }

                ((StayDetailNetworkController) mPlaceDetailNetworkController).requestHasCoupon(mPlaceDetail.index,//
                    mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), ((StayDetail) mPlaceDetail).nights);
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
                    moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER);
                } else
                {
                    // 기존에 인증이 되었는데 인증이 해지되었다.
                    if (isVerified == true && isPhoneVerified == false)
                    {
                        moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER);
                    } else
                    {
                        processBooking(mSaleTime, (StayDetail) mPlaceDetail, mSelectedRoomInformation);
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
                    moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.WRONG_PHONENUMBER);
                } else
                {
                    processBooking(mSaleTime, (StayDetail) mPlaceDetail, mSelectedRoomInformation);
                }
            }
        }

        @Override
        public void onStaytDetailInformation(JSONObject dataJSONObject)
        {
            try
            {
                mPlaceDetail.setData(dataJSONObject);

                if (mIsDeepLink == true)
                {
                    // 딥링크로 진입한 경우에는 카테고리 코드를 알수가 없다.
                    StayDetail stayDetail = (StayDetail) mPlaceDetail;
                    stayDetail.categoryCode = stayDetail.grade.getName(StayDetailActivity.this);

                    mDailyToolbarLayout.setToolbarText(mPlaceDetail.name);
                }

                if (mPlaceDetailLayout != null)
                {
                    ((StayDetailLayout) mPlaceDetailLayout).setDetail(mSaleTime, (StayDetail) mPlaceDetail, mCurrentImage);
                }

                if (mCheckPrice == false)
                {
                    mCheckPrice = true;
                    checkStayRoom(mIsDeepLink, (StayDetail) mPlaceDetail, mViewPrice);
                }

                mIsDeepLink = false;

                recordAnalyticsStayDetail(Screen.DAILYHOTEL_DETAIL, mSaleTime, (StayDetail) mPlaceDetail);
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
            ((StayDetail) mPlaceDetail).hasCoupon = hasCoupon;

            ((StayDetailNetworkController) mPlaceDetailNetworkController).requestStayDetailInformation(mPlaceDetail.index,//
                mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), ((StayDetail) mPlaceDetail).nights);
        }

        @Override
        public void onAddWishList(boolean isSuccess, String message)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);

            if (isSuccess == true)
            {
                mPlaceDetail.myWish = true;
                mPlaceDetailLayout.setWishListButtonCount(++mPlaceDetail.wishCount);
                mPlaceDetailLayout.setWishListButtonSelected(true);
                mPlaceDetailLayout.setWishListPopup(PlaceDetailLayout.WishListPopupState.ADD);

                AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(//
                    AnalyticsManager.Category.NAVIGATION,//
                    Action.WISHLIST_ON, mPlaceDetail.name, null);
            } else
            {
                mPlaceDetailLayout.setWishListButtonCount(mPlaceDetail.wishCount);
                mPlaceDetailLayout.setWishListButtonSelected(false);

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
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);

            if (isSuccess == true)
            {
                mPlaceDetail.myWish = false;
                mPlaceDetailLayout.setWishListButtonCount(--mPlaceDetail.wishCount);
                mPlaceDetailLayout.setWishListButtonSelected(false);
                mPlaceDetailLayout.setWishListPopup(PlaceDetailLayout.WishListPopupState.DELETE);

                AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(//
                    AnalyticsManager.Category.NAVIGATION,//
                    Action.WISHLIST_OFF, mPlaceDetail.name, null);
            } else
            {
                mPlaceDetailLayout.setWishListButtonCount(mPlaceDetail.wishCount);
                mPlaceDetailLayout.setWishListButtonSelected(true);

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
        public void onErrorResponse(VolleyError volleyError)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            StayDetailActivity.this.onErrorResponse(volleyError);
            finish();
        }

        @Override
        public void onError(Exception e)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            StayDetailActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);

            // 판매 마감시
            if (msgCode == 5)
            {
                StayDetailActivity.this.onErrorPopupMessage(msgCode, message, null);
            } else
            {
                StayDetailActivity.this.onErrorPopupMessage(msgCode, message);
            }
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
            StayDetailActivity.this.onErrorToastMessage(message);
            finish();
        }

        private void checkStayRoom(boolean isDeepLink, StayDetail stayDetail, int listViewPrice)
        {
            // 판매 완료 혹은 가격이 변동되었는지 조사한다
            ArrayList<RoomInformation> saleRoomList = stayDetail.getSaleRoomList();

            if (saleRoomList == null || saleRoomList.size() == 0)
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

                if (isDeepLink == true)
                {
                    AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                        Action.SOLDOUT_DEEPLINK, stayDetail.name, null);
                } else
                {
                    AnalyticsManager.getInstance(StayDetailActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                        Action.SOLDOUT, stayDetail.name, null);
                }
            } else
            {
                if (isDeepLink == false)
                {
                    boolean hasPrice = false;

                    for (RoomInformation roomInformation : saleRoomList)
                    {
                        if (listViewPrice == roomInformation.averageDiscount)
                        {
                            hasPrice = true;
                            break;
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
                            Action.SOLDOUT_CHANGEPRICE, stayDetail.name, null);
                    }
                }
            }
        }
    };
}
