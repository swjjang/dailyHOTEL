/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * PaymentWaitActivity (입금대기 화면)
 * <p>
 * 계좌이체 결제 선택 후 입금대기 상태 화면
 * 가상계좌 정보를 보여주는 화면이다.
 */
package com.twoheart.dailyhotel.screen.booking.detail;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.WaitingDeposit;
import com.daily.dailyhotel.parcel.BookingParcel;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class PaymentWaitActivity extends BaseActivity
{
    private BookingRemoteImpl mBookingRemoteImpl;

    TextView mAccountTextView;
    private TextView mDailyTextView;
    private TextView mPriceTextView, mBonusTextView, mCouponTextView, mTotalPriceTextView;
    private TextView mDeadlineTextView;
    private ViewGroup mGuide1Layout;
    private View mBonusLayout, mCouponLayout;

    Booking mBooking;

    public static Intent newInstance(Context context, Booking booking)
    {
        Intent intent = new Intent(context, PaymentWaitActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING, new BookingParcel(booking));

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        BookingParcel bookingParcel = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_BOOKING);

        if (bookingParcel == null)
        {
            Util.restartApp(this);
            return;
        }

        mBookingRemoteImpl = new BookingRemoteImpl(this);

        mBooking = bookingParcel.getBooking();

        setContentView(R.layout.activity_payment_wait_data);

        initToolbar();
        initLayout(mBooking);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        switch (mBooking.placeType)
        {
            case STAY:
            {
                HashMap<String, String> params = new HashMap();
                params.put(AnalyticsManager.KeyType.PLACE_TYPE, "stay");

                AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_TEMPORARY_ACCOUNT, null, params);
                break;
            }

            case GOURMET:
            {
                HashMap<String, String> params = new HashMap();
                params.put(AnalyticsManager.KeyType.PLACE_TYPE, "gourmet");

                AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.BOOKINGDETAIL_MYBOOKINGINFO_TEMPORARY_ACCOUNT, null, params);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_FAQ:
            {
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(CODE_RESULT_ACTIVITY_GO_HOME);
                    finish();
                }
                break;
            }
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);

        dailyToolbarView.setTitleText(R.string.actionbar_title_payment_wait_activity);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        dailyToolbarView.addMenuItem(DailyToolbarView.MenuItem.HELP, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showCallDialog(mBooking.placeType);

                switch (mBooking.placeType)
                {
                    case STAY:
                        AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.STAY_DEPOSIT_WAITING, null);
                        break;

                    case GOURMET:
                        AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.GOURMET_DEPOSIT_WAITING, null);
                        break;
                }
            }
        });
    }

    private void initLayout(Booking booking)
    {
        if (booking == null)
        {
            return;
        }

        ScrollView scrollLayout = findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollLayout, getResources().getColor(R.color.default_over_scroll_edge));

        TextView placeNameTextView = findViewById(R.id.paymentWaitHotelNameView);
        mAccountTextView = findViewById(R.id.accountNumberView);
        mDailyTextView = findViewById(R.id.accountHolderView);
        mPriceTextView = findViewById(R.id.priceTextView);
        mBonusTextView = findViewById(R.id.bonusTextView);
        mCouponTextView = findViewById(R.id.couponTextView);
        mTotalPriceTextView = findViewById(R.id.totalPriceTextView);
        mDeadlineTextView = findViewById(R.id.waitingDeadlineView);
        mGuide1Layout = findViewById(R.id.guide1Layout);

        mBonusLayout = findViewById(R.id.bonusLayout);
        mCouponLayout = findViewById(R.id.couponLayout);

        View view = findViewById(R.id.editLinearLayout);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mAccountTextView == null)
                {
                    return;
                }

                DailyTextUtils.clipText(PaymentWaitActivity.this, (String) mAccountTextView.getTag());

                DailyToast.showToast(PaymentWaitActivity.this, R.string.message_detail_copy_account_number, Toast.LENGTH_SHORT);
            }
        });

        placeNameTextView.setText(booking.placeName);

        lockUI();

        if (DailyTextUtils.isTextEmpty(booking.aggregationId) == true)
        {
            switch (booking.placeType)
            {
                case STAY:
                    DailyMobileAPI.getInstance(this).requestDepositWaitDetailInformation(mNetworkTag, booking.tid, mHotelReservationCallback);
                    break;

                case GOURMET:
                    DailyMobileAPI.getInstance(this).requestGourmetAccountInformation(mNetworkTag, booking.tid, mFnBReservationCallback);
                    break;
            }
        } else
        {
            addCompositeDisposable(mBookingRemoteImpl.getWaitingDeposit(booking.aggregationId).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WaitingDeposit>()
            {
                @Override
                public void accept(@NonNull WaitingDeposit waitingDeposit) throws Exception
                {
                    unLockUI();

                    setWaitingDeposit(waitingDeposit);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {

                    Intent intent = new Intent();
                    intent.putExtra("msg", throwable.getMessage());
                    setResult(CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT, intent);
                    finish();
                    //                    onHandleError(throwable);
                }
            }));
        }
    }

    void showCallDialog(final Booking.PlaceType placeType)
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_contact_us_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 버튼
        View contactUs01Layout = dialogView.findViewById(R.id.contactUs01Layout);
        View contactUs02Layout = dialogView.findViewById(R.id.contactUs02Layout);
        contactUs02Layout.setVisibility(View.GONE);

        DailyTextView contactUs01TextView = contactUs01Layout.findViewById(R.id.contactUs01TextView);
        contactUs01TextView.setText(R.string.frag_faqs);
        contactUs01TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_05_faq, 0, 0, 0);

        contactUs01Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startFAQ();

                switch (placeType)
                {
                    case STAY:
                        AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.STAY_DEPOSIT_WAITING, null);
                        break;

                    case GOURMET:
                        AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.GOURMET_DEPOSIT_WAITING, null);
                        break;
                }
            }
        });

        View kakaoDailyView = dialogView.findViewById(R.id.kakaoDailyView);
        View callDailyView = dialogView.findViewById(R.id.callDailyView);

        kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startKakao();

                switch (placeType)
                {
                    case STAY:
                        AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.STAY_DEPOSIT_WAITING, null);
                        break;

                    case GOURMET:
                        AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.GOURMET_DEPOSIT_WAITING, null);
                        break;
                }
            }
        });

        callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startCall();

                switch (placeType)
                {
                    case STAY:
                        AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.STAY_DEPOSIT_WAITING, null);
                        break;

                    case GOURMET:
                        AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                            , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.GOURMET_DEPOSIT_WAITING, null);
                        break;
                }
            }
        });

        View closeView = dialogView.findViewById(R.id.closeView);
        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockUI();
            }
        });

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void setGuideText(ViewGroup viewGroups, String[] guides, boolean isImportant)
    {
        if (guides == null)
        {
            return;
        }

        for (String guide : guides)
        {
            View textLayout = LayoutInflater.from(this).inflate(R.layout.list_row_payment_wait_guide_data, viewGroups, false);
            TextView textView = textLayout.findViewById(R.id.textView);

            String guideText = guide.replace("\n", " ").trim();

            if (guideText.endsWith(".") == false)
            {
                guideText += ".";
            }

            textView.setText(guideText);

            if (isImportant == true)
            {
                textView.setTextColor(getResources().getColor(R.color.dh_theme_color));
            }

            viewGroups.addView(textLayout);
        }
    }

    void setHotelReservationData(JSONObject jsonObject) throws JSONException, ParseException
    {
        JSONObject reservationJSONObject = jsonObject.getJSONObject("reservation");
        String accountNumber = reservationJSONObject.getString("vactNum");
        mAccountTextView.setText(reservationJSONObject.getString("bankName") + ", " + accountNumber);
        mAccountTextView.setTag(accountNumber);

        mDailyTextView.setText(reservationJSONObject.getString("vactName"));

        // 입금기한
        //        String validToDate = Util.simpleDateFormatISO8601toFormat(reservationJSONObject.getString("validTo"), "yyyy년 MM월 dd일 HH시 mm분 까지");
        String validToDate = DailyCalendar.convertDateFormatString(reservationJSONObject.getString("validTo"), DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일 HH시 mm분 까지");
        mDeadlineTextView.setText(validToDate);

        // 결재 금액 정보
        mPriceTextView.setText(DailyTextUtils.getPriceFormat(this, reservationJSONObject.getInt("price"), false));

        int bonus = reservationJSONObject.getInt("bonus");

        if (bonus > 0)
        {
            mBonusLayout.setVisibility(View.VISIBLE);
            mBonusTextView.setText("- " + DailyTextUtils.getPriceFormat(this, bonus, false));
        } else
        {
            mBonusLayout.setVisibility(View.GONE);
        }

        int coupon = reservationJSONObject.getInt("couponAmount");

        if (coupon > 0)
        {
            mCouponLayout.setVisibility(View.VISIBLE);
            mCouponTextView.setText("- " + DailyTextUtils.getPriceFormat(this, coupon, false));
        } else
        {
            mCouponLayout.setVisibility(View.GONE);
        }

        int paymentPrice = reservationJSONObject.getInt("amt");

        mTotalPriceTextView.setText(DailyTextUtils.getPriceFormat(this, paymentPrice, false));

        // 확인 사항
        String msg1 = jsonObject.getString("msg1");
        setGuideText(mGuide1Layout, msg1.split("\\."), false);

        String msg2 = getString(R.string.message__wait_payment03);
        setGuideText(mGuide1Layout, msg2.split("\\."), true);
    }

    void setWaitingDeposit(WaitingDeposit waitingDeposit) throws Exception
    {
        if (waitingDeposit == null)
        {
            return;
        }

        mAccountTextView.setText(waitingDeposit.bankName + ", " + waitingDeposit.accountNumber);
        mAccountTextView.setTag(waitingDeposit.accountNumber);

        mDailyTextView.setText(waitingDeposit.accountHolder);

        // 입금기한
        String validToDate = DailyCalendar.convertDateFormatString(waitingDeposit.expiredAt, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일 HH시 mm분 까지");
        mDeadlineTextView.setText(validToDate);

        // 결재 금액 정보
        mPriceTextView.setText(DailyTextUtils.getPriceFormat(this, waitingDeposit.totalPrice, false));

        if (waitingDeposit.bonusAmount > 0)
        {
            mBonusLayout.setVisibility(View.VISIBLE);
            mBonusTextView.setText("- " + DailyTextUtils.getPriceFormat(this, waitingDeposit.bonusAmount, false));
        } else
        {
            mBonusLayout.setVisibility(View.GONE);
        }

        if (waitingDeposit.couponAmount > 0)
        {
            mCouponLayout.setVisibility(View.VISIBLE);
            mCouponTextView.setText("- " + DailyTextUtils.getPriceFormat(this, waitingDeposit.couponAmount, false));
        } else
        {
            mCouponLayout.setVisibility(View.GONE);
        }

        mTotalPriceTextView.setText(DailyTextUtils.getPriceFormat(this, waitingDeposit.depositWaitingAmount, false));

        // 확인 사항
        if (waitingDeposit.getMessage1List() != null)
        {
            String[] messages1 = waitingDeposit.getMessage1List().toArray(new String[waitingDeposit.getMessage1List().size()]);
            setGuideText(mGuide1Layout, messages1, false);
        }

        if (waitingDeposit.getMessage2List() != null)
        {
            String[] messages2 = waitingDeposit.getMessage2List().toArray(new String[waitingDeposit.getMessage2List().size()]);
            setGuideText(mGuide1Layout, messages2, true);
        }
    }

    void setGourmetReservationData(JSONObject jsonObject) throws JSONException
    {
        String accountNumber = jsonObject.getString("account_num");
        mAccountTextView.setText(jsonObject.getString("bank_name") + ", " + accountNumber);
        mAccountTextView.setTag(accountNumber);

        mDailyTextView.setText(jsonObject.getString("name"));

        String[] dateSlice = jsonObject.getString("date").split("/");
        String[] timeSlice = jsonObject.getString("time").split(":");

        String date = String.format(Locale.KOREA, "%s년 %s월 %s일", dateSlice[0], dateSlice[1], dateSlice[2]);

        mDeadlineTextView.setText(String.format(Locale.KOREA, "%s %s시 %s분 까지", date, timeSlice[0], timeSlice[1]));

        int coupon = jsonObject.getInt("coupon_amount");

        if (coupon > 0)
        {
            mCouponLayout.setVisibility(View.VISIBLE);
            mCouponTextView.setText("- " + DailyTextUtils.getPriceFormat(this, coupon, false));
        } else
        {
            mCouponLayout.setVisibility(View.GONE);
        }

        mPriceTextView.setText(DailyTextUtils.getPriceFormat(this, jsonObject.getInt("price"), false));

        int paymentPrice = jsonObject.getInt("amt");
        mTotalPriceTextView.setText(DailyTextUtils.getPriceFormat(this, paymentPrice, false));

        String msg1 = jsonObject.getString("msg1");
        setGuideText(mGuide1Layout, msg1.split("\\."), false);

        String msg2 = getString(R.string.message__wait_payment03);
        setGuideText(mGuide1Layout, msg2.split("\\."), true);
    }

    void startFAQ()
    {
        startActivityForResult(new Intent(this, FAQActivity.class), CODE_REQUEST_ACTIVITY_FAQ);
    }

    void startCall()
    {
        if (isFinishing() == true)
        {
            return;
        }

        showDailyCallDialog(new OnCallDialogListener()
        {
            @Override
            public void onShowDialog()
            {
                AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(//
                    AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.DEPOSIT_WAITING,//
                    AnalyticsManager.Label.CLICK, null);
            }

            @Override
            public void onPositiveButtonClick(View v)
            {
                AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(//
                    AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.DEPOSIT_WAITING,//
                    AnalyticsManager.Label.CALL, null);
            }

            @Override
            public void onNativeButtonClick(View v)
            {
                AnalyticsManager.getInstance(PaymentWaitActivity.this).recordEvent(//
                    AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.DEPOSIT_WAITING,//
                    AnalyticsManager.Label.CANCEL_, null);
            }

            @Override
            public void onDismissDialog()
            {
                // do nothing!
            }
        });
    }

    void startKakao()
    {
        try
        {
            getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED,//
                AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);

            switch (mBooking.placeType)
            {
                case STAY:
                    startActivityForResult(HappyTalkCategoryDialog.newInstance(this//
                        , HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_PAYMENT_WAIT, 0//
                        , mBooking.reservationIndex, mBooking.placeName), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
                    break;

                case GOURMET:
                    startActivityForResult(HappyTalkCategoryDialog.newInstance(this//
                        , HappyTalkCategoryDialog.CallScreen.SCREEN_GOURMET_PAYMENT_WAIT, 0//
                        , mBooking.reservationIndex, mBooking.placeName), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
                    break;
            }
        } catch (Exception e)
        {
            showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(PaymentWaitActivity.this, "com.kakao.talk");
                    }
                }, null);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mHotelReservationCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        setHotelReservationData(dataJSONObject);
                    } else
                    {
                        Intent intent = new Intent();
                        intent.putExtra("msg", responseJSONObject.getString("msg"));
                        setResult(CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT, intent);
                        finish();
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                    finish();
                } finally
                {
                    unLockUI();
                }
            } else
            {
                PaymentWaitActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            PaymentWaitActivity.this.onError(t);
        }
    };

    private retrofit2.Callback mFnBReservationCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");

                    if (msgCode == 0)
                    {
                        JSONObject jsonObject = responseJSONObject.getJSONObject("data");

                        setGourmetReservationData(jsonObject);
                    } else
                    {
                        Intent intent = new Intent();
                        intent.putExtra("msg", responseJSONObject.getString("msg"));
                        setResult(CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT, intent);
                        finish();
                    }
                } catch (Exception e)
                {
                    onError(e);
                    finish();
                } finally
                {
                    unLockUI();
                }
            } else
            {
                PaymentWaitActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            PaymentWaitActivity.this.onError(t);
        }
    };
}
