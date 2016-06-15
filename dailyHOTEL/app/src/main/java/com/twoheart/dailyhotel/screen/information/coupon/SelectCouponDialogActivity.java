package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android_sam on 2016. 5. 26..
 */
public class SelectCouponDialogActivity extends BaseActivity
{

    public static final String INTENT_EXTRA_SELECT_COUPON = "selectCoupon";
    public static final String INTENT_EXTRA_HOTEL_IDX = "hotelIdx";
    public static final String INTENT_EXTRA_ROOM_IDX = "roomIdx";
    public static final String INTENT_EXTRA_CHECK_IN_DATE = "checkInDate";
    public static final String INTENT_EXTRA_CHECK_OUT_DATE = "checkOutDate";
    public static final String INTENT_EXTRA_CATEGORY_CODE = "categoryCode";
    public static final String INTENT_EXTRA_HOTEL_NAME = "hotelName";
    public static final String INTENT_EXTRA_ROOM_PRICE = "roomPrice";


    private SelectCouponDialogLayout mLayout;
    private SelectCouponNetworkController mNetworkController;

    private boolean mIsSetOk = false;

    private int mHotelIdx;
    private int mRoomIdx;

    private String mRoomPrice;
    private String mCheckInDate;
    private String mCheckOutDate;
    private String mCategoryCode;
    private String mHotelName;

    public static Intent newInstance(Context context, int hotelIdx, int roomIdx, String checkInDate, //
                                     String checkOutDate, String categoryCode, String hotelName, String roomPrice)
    {
        Intent intent = new Intent(context, SelectCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_HOTEL_IDX, hotelIdx);
        intent.putExtra(INTENT_EXTRA_ROOM_IDX, roomIdx);
        intent.putExtra(INTENT_EXTRA_CHECK_IN_DATE, checkInDate);
        intent.putExtra(INTENT_EXTRA_CHECK_OUT_DATE, checkOutDate);
        intent.putExtra(INTENT_EXTRA_CATEGORY_CODE, categoryCode);
        intent.putExtra(INTENT_EXTRA_HOTEL_NAME, hotelName);
        intent.putExtra(INTENT_EXTRA_ROOM_PRICE, roomPrice);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        if (intent == null)
        {
            finish();
            return;
        }

        mHotelIdx = intent.getIntExtra(INTENT_EXTRA_HOTEL_IDX, -1);
        mCheckInDate = intent.getStringExtra(INTENT_EXTRA_CHECK_IN_DATE);
        mCheckOutDate = intent.getStringExtra(INTENT_EXTRA_CHECK_OUT_DATE);

        mRoomIdx = intent.getIntExtra(INTENT_EXTRA_ROOM_IDX, -1);
        mCategoryCode = intent.getStringExtra(INTENT_EXTRA_CATEGORY_CODE);
        mHotelName = intent.getStringExtra(INTENT_EXTRA_HOTEL_NAME);
        mRoomPrice = intent.getStringExtra(INTENT_EXTRA_ROOM_PRICE);

        mLayout = new SelectCouponDialogLayout(this, mOnEventListener);
        mNetworkController = new SelectCouponNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mLayout.onCreateView(R.layout.activity_select_coupon_dialog));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        lockUI();

        mNetworkController.requestCouponList(mHotelIdx, mRoomIdx, mCheckInDate, mCheckOutDate);
    }

    @Override
    public void finish()
    {
        if (mIsSetOk == false)
        {
            recordCancelAnalytics();
        }

        super.finish();
        overridePendingTransition(0, 0);
    }

    private void recordCancelAnalytics()
    {
        if (mLayout.getCouponCount() == 0)
        {
            // empty list
            String label = mCategoryCode + "-" + mHotelName + "-" + mRoomPrice;

            AnalyticsManager.getInstance(SelectCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                AnalyticsManager.Action.HOTEL_COUPON_NOT_FOUND, label, null);
        } else
        {
            AnalyticsManager.getInstance(SelectCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                AnalyticsManager.Action.HOTEL_USING_COUPON_CANCEL_CLICKED, AnalyticsManager.Label.HOTEL_USING_COUPON_CANCEL, null);
        }
    }

    private SelectCouponDialogLayout.OnEventListener mOnEventListener = new SelectCouponDialogLayout.OnEventListener()
    {
        @Override
        public void setResult(Coupon coupon)
        {
            lockUI();

            mIsSetOk = true;

            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_SELECT_COUPON, coupon);

            SelectCouponDialogActivity.this.setResult(RESULT_OK, intent);
            SelectCouponDialogActivity.this.finish();

            AnalyticsManager.getInstance(SelectCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                AnalyticsManager.Action.HOTEL_COUPON_SELECTED, coupon.title, null);
        }

        @Override
        public void onCouponDownloadClick(Coupon coupon)
        {
            // 쿠폰 다운로드 시도!
            mNetworkController.requestDownloadCoupon(coupon);
        }

        @Override
        public void finish()
        {
            SelectCouponDialogActivity.this.finish();
        }
    };

    // ///////////////////////////////////////////////////
    // NetworkController
    // ///////////////////////////////////////////////////
    private SelectCouponNetworkController.OnNetworkControllerListener mNetworkControllerListener = new SelectCouponNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCouponList(List<Coupon> list)
        {
            mLayout.setData(list);

            if (list == null || list.size() == 0)
            {
                AnalyticsManager.getInstance(SelectCouponDialogActivity.this) //
                    .recordScreen(AnalyticsManager.Screen.DAILY_HOTEL_UNAVAILABLE_COUPON_LIST);
            } else
            {
                AnalyticsManager.getInstance(SelectCouponDialogActivity.this) //
                    .recordScreen(AnalyticsManager.Screen.DAILY_HOTEL_AVAILABLE_COUPON_LIST);
            }

            unLockUI();
        }

        @Override
        public void onDownloadCoupon(String userCouponCode)
        {
            lockUI();

            Coupon coupon = mLayout.getCoupon(userCouponCode);
            recordAnalytics(coupon);

            mNetworkController.requestCouponList(mHotelIdx, mRoomIdx, mCheckInDate, mCheckOutDate);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            SelectCouponDialogActivity.this.onErrorResponse(volleyError);
            finish();
        }

        @Override
        public void onError(Exception e)
        {
            SelectCouponDialogActivity.this.onError(e);
            finish();
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            SelectCouponDialogActivity.this.onErrorPopupMessage(msgCode, message);
            finish();
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            SelectCouponDialogActivity.this.onErrorToastMessage(message);
            finish();
        }

        private void recordAnalytics(Coupon coupon)
        {
            try
            {
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));
                paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, Util.simpleDateFormat(new Date(), "yyyyMMddHHmm"));
                paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, Util.simpleDateFormatISO8601toFormat(coupon.validTo, "yyyyMMddHHmm"));

                AnalyticsManager.getInstance(SelectCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                    , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "Booking-" + coupon.title, paramsMap);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };
}
