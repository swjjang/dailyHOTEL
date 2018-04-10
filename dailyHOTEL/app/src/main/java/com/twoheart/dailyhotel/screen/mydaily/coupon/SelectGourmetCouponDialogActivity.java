package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Coupons;
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.Response;

public class SelectGourmetCouponDialogActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_SELECT_COUPON = "selectCoupon";
    public static final String INTENT_EXTRA_GOURMET_INDEX = "gourmetIndex";
    public static final String INTENT_EXTRA_TICKET_INDEXES = "ticketIndexes";
    public static final String INTENT_EXTRA_VISIT_DAY = "visitDay";
    public static final String INTENT_EXTRA_GOURMET_NAME = "gourmetName";
    public static final String INTENT_EXTRA_TICKET_COUNTS = "ticketCounts";
    public static final String INTENT_EXTRA_MAX_COUPON_AMOUNT = "maxCouponAmount";

    SelectCouponDialogLayout mLayout;
    SelectGourmetCouponNetworkController mNetworkController;

    CouponRemoteImpl mCouponRemoteImpl;

    boolean mIsSetOk = false;

    int mGourmetIndex;
    int[] mTicketIndexes;
    int[] mTicketCounts;

    String mVisitDay;
    private String mGourmetName;
    String mCallByScreen;
    int mMaxCouponAmount;

    public static Intent newInstance(Context context, String visitDay, int gourmetIndex, String gourmetName//
        , int[] ticketIndexes, int[] ticketCounts)
    {
        Intent intent = new Intent(context, SelectGourmetCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_VISIT_DAY, visitDay);
        intent.putExtra(INTENT_EXTRA_GOURMET_INDEX, gourmetIndex);
        intent.putExtra(INTENT_EXTRA_GOURMET_NAME, gourmetName);
        intent.putExtra(INTENT_EXTRA_TICKET_INDEXES, ticketIndexes);
        intent.putExtra(INTENT_EXTRA_TICKET_COUNTS, ticketCounts);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE);

        return intent;
    }

    public static Intent newInstance(Context context, String visitDay, int gourmetIndex, String gourmetName)
    {
        Intent intent = new Intent(context, SelectGourmetCouponDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_VISIT_DAY, visitDay);
        intent.putExtra(INTENT_EXTRA_GOURMET_INDEX, gourmetIndex);
        intent.putExtra(INTENT_EXTRA_GOURMET_NAME, gourmetName);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, AnalyticsManager.Screen.DAILYGOURMET_DETAIL);

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

        mCallByScreen = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN);

        mCouponRemoteImpl = new CouponRemoteImpl();

        if (DailyTextUtils.isTextEmpty(mCallByScreen) == true)
        {
            Util.restartApp(this);
            return;
        }

        switch (mCallByScreen)
        {
            case AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE:
            {
                mVisitDay = intent.getStringExtra(INTENT_EXTRA_VISIT_DAY);
                mGourmetIndex = intent.getIntExtra(INTENT_EXTRA_GOURMET_INDEX, -1);
                mGourmetName = intent.getStringExtra(INTENT_EXTRA_GOURMET_NAME);
                mTicketIndexes = intent.getIntArrayExtra(INTENT_EXTRA_TICKET_INDEXES);
                mTicketCounts = intent.getIntArrayExtra(INTENT_EXTRA_TICKET_COUNTS);
                break;
            }

            case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
            {
                mVisitDay = intent.getStringExtra(INTENT_EXTRA_VISIT_DAY);
                mGourmetIndex = intent.getIntExtra(INTENT_EXTRA_GOURMET_INDEX, -1);
                mGourmetName = intent.getStringExtra(INTENT_EXTRA_GOURMET_NAME);
                break;
            }
        }

        mLayout = new SelectCouponDialogLayout(this, mOnEventListener);
        mNetworkController = new SelectGourmetCouponNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mLayout.onCreateView(R.layout.activity_select_coupon_dialog));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        lockUI();

        onRefresh();
    }

    @Override
    public void finish()
    {
        if (mIsSetOk == false)
        {
            recordCancelAnalytics();

            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_MAX_COUPON_AMOUNT, mMaxCouponAmount);
            SelectGourmetCouponDialogActivity.this.setResult(RESULT_OK, intent);
        }

        super.finish();

        overridePendingTransition(0, 0);
    }

    private void recordCancelAnalytics()
    {
        try
        {
            switch (mCallByScreen)
            {
                case AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE:
                {
                    if (mLayout.getCouponCount() == 0)
                    {
                        //                        // empty list
                        //                        String label = mCategoryCode + "-" + mHotelName + "-" + mRoomPrice;
                        //
                        //                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
                        //                            AnalyticsManager.Action.HOTEL_COUPON_NOT_FOUND, label, null);
                    } else
                    {
                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                            AnalyticsManager.Action.GOURMET_USING_COUPON_CANCEL_CLICKED, AnalyticsManager.Label.GOURMET_USING_COUPON_CANCEL, null);
                    }
                    break;
                }

                case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
                {
                    break;
                }
            }
        } catch (Exception e)
        {
            Crashlytics.logException(e);
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
            intent.putExtra(INTENT_EXTRA_MAX_COUPON_AMOUNT, mMaxCouponAmount);

            SelectGourmetCouponDialogActivity.this.setResult(RESULT_OK, intent);
            SelectGourmetCouponDialogActivity.this.finish();

            AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                AnalyticsManager.Action.GOURMET_COUPON_SELECTED, coupon.title, null);
        }

        @Override
        public void onCouponDownloadClick(Coupon coupon)
        {
            if (coupon == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            // 쿠폰 다운로드 시도!
            mNetworkController.requestDownloadCoupon(coupon);
        }

        @Override
        public void finish()
        {
            SelectGourmetCouponDialogActivity.this.finish();
        }
    };

    void onRefresh()
    {
        switch (mCallByScreen)
        {
            case AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE:
            {
                if (DailyTextUtils.isTextEmpty(mVisitDay) == true)
                {
                    Util.restartApp(this);
                    return;
                }

                addCompositeDisposable(mCouponRemoteImpl.getGourmetCouponListByPayment(mTicketIndexes, mTicketCounts).map(new Function<Coupons, List<Coupon>>()
                {
                    @Override
                    public List<Coupon> apply(Coupons coupons) throws Exception
                    {
                        mMaxCouponAmount = coupons.maxCouponAmount;

                        List<Coupon> couponList = new ArrayList<>();

                        for (com.daily.dailyhotel.entity.Coupon coupon : coupons.coupons)
                        {
                            couponList.add(new Coupon(coupon));
                        }

                        return couponList;
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Coupon>>()
                {
                    @Override
                    public void accept(List<Coupon> couponList) throws Exception
                    {
                        if (couponList.size() == 0)
                        {
                            mLayout.setVisibility(false);
                            showSimpleDialog(getString(R.string.label_booking_select_coupon), getString(R.string.message_select_coupon_empty), //
                                getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog)
                                    {
                                        finish();
                                    }
                                });

                            //                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this) //
                            //                            .recordScreen(AnalyticsManager.Screen.DAILY_GOURMET_UNAVAILABLE_COUPON_LIST);
                        } else
                        {
                            mLayout.setVisibility(true);
                            mLayout.setTitle(R.string.label_select_coupon);
                            mLayout.setTwoButtonLayout(true, R.string.dialog_btn_text_select, R.string.dialog_btn_text_cancel);

                            mLayout.setData(couponList, true);

                            AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this) //
                                .recordScreen(SelectGourmetCouponDialogActivity.this, AnalyticsManager.Screen.DAILY_GOURMET_AVAILABLE_COUPON_LIST, null);
                        }

                        unLockUI();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        onHandleError(throwable);
                        finish();
                    }
                }));

                break;
            }

            case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
            {
                if (DailyTextUtils.isTextEmpty(mVisitDay) == true)
                {
                    Util.restartApp(this);
                    return;
                }

                mNetworkController.requestCouponList(mGourmetIndex, mVisitDay);
                break;
            }
        }
    }

    // ///////////////////////////////////////////////////
    // NetworkController
    // ///////////////////////////////////////////////////

    private SelectGourmetCouponNetworkController.OnNetworkControllerListener mNetworkControllerListener = new SelectGourmetCouponNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCouponList(List<Coupon> list)
        {
            boolean isEmpty = (list == null || list.size() == 0);

            switch (mCallByScreen)
            {
                case AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE:
                {
                    if (isEmpty == true)
                    {
                        mLayout.setVisibility(false);
                        showSimpleDialog(getString(R.string.label_booking_select_coupon), getString(R.string.message_select_coupon_empty), //
                            getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    finish();
                                }
                            });

                        //                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this) //
                        //                            .recordScreen(AnalyticsManager.Screen.DAILY_GOURMET_UNAVAILABLE_COUPON_LIST);
                    } else
                    {
                        mLayout.setVisibility(true);
                        mLayout.setTitle(R.string.label_select_coupon);
                        mLayout.setTwoButtonLayout(true, R.string.dialog_btn_text_select, R.string.dialog_btn_text_cancel);

                        mLayout.setData(list, true);

                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this) //
                            .recordScreen(SelectGourmetCouponDialogActivity.this, AnalyticsManager.Screen.DAILY_GOURMET_AVAILABLE_COUPON_LIST, null);
                    }
                    break;
                }

                case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
                {
                    mLayout.setVisibility(true);

                    boolean hasDownloadCoupon = false;

                    if (isEmpty == false)
                    {
                        for (Coupon coupon : list)
                        {
                            if (coupon.isDownloaded == false)
                            {
                                hasDownloadCoupon = true;
                                break;
                            }
                        }
                    }

                    mLayout.setTitle(hasDownloadCoupon == true ? R.string.coupon_download_coupon : R.string.coupon_dont_download_coupon);
                    mLayout.setOneButtonLayout(true, R.string.dialog_btn_text_close);
                    mLayout.setData(list, false);
                    break;
                }
            }

            unLockUI();
        }

        @Override
        public void onDownloadCoupon(String couponCode)
        {
            lockUI();

            Coupon coupon = mLayout.getCoupon(couponCode);
            analyticsDownloadCoupon(coupon);

            onRefresh();
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            SelectGourmetCouponDialogActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            SelectGourmetCouponDialogActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            SelectGourmetCouponDialogActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            SelectGourmetCouponDialogActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            SelectGourmetCouponDialogActivity.this.onErrorResponse(call, response);
        }

        private void analyticsDownloadCoupon(Coupon coupon)
        {
            try
            {
                switch (mCallByScreen)
                {
                    case AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE:
                    {
                        Map<String, String> paramsMap = new HashMap<>();
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                        paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));
                        //                paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, Util.simpleDateFormat(new Date(), "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(new Date(), "yyyyMMddHHmm"));
                        //                paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, Util.simpleDateFormatISO8601toFormat(coupon.validTo, "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_FROM, "booking");
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_CODE, coupon.couponCode);

                        if (coupon.availableInGourmet == true && coupon.availableInStay == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.ALL);
                        } else if (coupon.availableInStay == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.STAY);
                        } else if (coupon.availableInGourmet == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.GOURMET);
                        }

                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                            , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "booking-" + coupon.title, paramsMap);

                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                            , AnalyticsManager.Action.GOURMET_COUPON_DOWNLOADED, coupon.title, null);
                        break;
                    }

                    case AnalyticsManager.Screen.DAILYGOURMET_DETAIL:
                    {
                        Map<String, String> paramsMap = new HashMap<>();
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                        paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));
                        //                paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, Util.simpleDateFormat(new Date(), "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(new Date(), "yyyyMMddHHmm"));
                        //                paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, Util.simpleDateFormatISO8601toFormat(coupon.validTo, "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"));
                        paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_FROM, "booking");
                        paramsMap.put(AnalyticsManager.KeyType.COUPON_CODE, coupon.couponCode);

                        if (coupon.availableInGourmet == true && coupon.availableInStay == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.ALL);
                        } else if (coupon.availableInStay == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.STAY);
                        } else if (coupon.availableInGourmet == true)
                        {
                            paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.GOURMET);
                        }

                        AnalyticsManager.getInstance(SelectGourmetCouponDialogActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                            , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "detail-" + coupon.title, paramsMap);
                        break;
                    }
                }
            } catch (ParseException e)
            {
                Crashlytics.log("Select Coupon::coupon.validTo: " + (coupon != null ? coupon.validTo : ""));
                ExLog.d(e.toString());
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    };
}
