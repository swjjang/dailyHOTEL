package com.twoheart.dailyhotel.screen.hotel.payment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.model.StayPaymentInformation;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.activity.PlacePaymentActivity;
import com.twoheart.dailyhotel.screen.common.FinalCheckLayout;
import com.twoheart.dailyhotel.screen.information.coupon.SelectStayCouponDialogActivity;
import com.twoheart.dailyhotel.screen.information.creditcard.RegisterCreditCardActivity;
import com.twoheart.dailyhotel.screen.information.member.InputMobileNumberDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyScrollView;
import com.twoheart.dailyhotel.widget.DailySignatureView;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

@SuppressLint({"NewApi", "ResourceAsColor"})
public class HotelPaymentActivity extends PlacePaymentActivity
{
    private static final int DEFAULT_AVAILABLE_RESERVES = 20000;

    private HotelPaymentLayout mHotelPaymentLayout;
    //
    private boolean mIsChangedPrice; // 가격이 변경된 경우.
    private String mPlaceImageUrl;
    private boolean mIsUnderPrice;

    // 1 : 오후 6시 전 당일 예약, 2 : 오후 6시 후 당일 예약, 3: 새벽 3시 이후 - 오전 9시까지의 당일 예약
    // 10 : 오후 10시 전 사전 예약, 11 : 오후 10시 후 사전 예약 00시 전 12 : 00시 부터 오전 9시
    private int mPensionPopupMessageType;
    private String mWarningDialogMessage;
    private Province mProvince;
    private String mArea; // Analytics용 소지역

    // GA용 스크린 정의
    private String mScreenAnalytics;

    public static Intent newInstance(Context context, RoomInformation roomInformation//
        , SaleTime checkInSaleTime, String imageUrl, int hotelIndex, boolean isDBenefit //
        , Province province, String area, String isShowOriginalPrice, int entryPosition //
        , boolean isDailyChoice, int ratingValue)
    {
        Intent intent = new Intent(context, HotelPaymentActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALEROOMINFORMATION, roomInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkInSaleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotelIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DBENEFIT, isDBenefit);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, area);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE, isShowOriginalPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, entryPosition);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, isDailyChoice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_RATING_VALUE, ratingValue);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mHotelPaymentLayout = new HotelPaymentLayout(this, mOnEventListener);

        setContentView(mHotelPaymentLayout.onCreateView(R.layout.activity_booking));

        Intent intent = getIntent();

        if (intent == null || initIntent(intent) == false)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            finish();
            return;
        }

        mIsChangedPrice = false;
        mWarningDialogMessage = null;

        mHotelPaymentLayout.setToolbarTitle(getString(R.string.actionbar_title_payment_activity));

        setAvailableDefaultPaymentType();
    }

    private boolean initIntent(Intent intent)
    {
        mPaymentInformation = new StayPaymentInformation();
        StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;

        stayPaymentInformation.setSaleRoomInformation((RoomInformation) intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALEROOMINFORMATION));
        mCheckInSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        mPlaceImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_URL);
        stayPaymentInformation.placeIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
        stayPaymentInformation.isDBenefit = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_DBENEFIT, false);
        mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
        mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);
        stayPaymentInformation.ratingValue = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_RATING_VALUE, -1);
        stayPaymentInformation.isShowOriginalPrice = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IS_SHOW_ORIGINALPRICE);
        stayPaymentInformation.entryPosition = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_ENTRY_INDEX, -1);
        stayPaymentInformation.isDailyChoice = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_IS_DAILYCHOICE, false);

        if (stayPaymentInformation.getSaleRoomInformation() == null)
        {
            return false;
        } else
        {
            return true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            View view = getCurrentFocus();
            if (view instanceof EditText)
            {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);

                if (outRect.contains((int) event.getRawX(), (int) event.getRawY()) == false)
                {
                    mHotelPaymentLayout.clearFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void requestUserInformationForPayment()
    {
        DailyMobileAPI.getInstance(this).requestUserInformationForPayment(mNetworkTag, mUserInformationCallback);
    }

    @Override
    protected void requestEasyPayment(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime)
    {
        if (paymentInformation == null || checkInSaleTime == null || mSelectedCreditCard == null)
        {
            Util.restartApp(this);
            return;
        }

        lockUI();

        Guest guest = mHotelPaymentLayout.getGuest();
        Customer customer = paymentInformation.getCustomer();

        if (guest == null)
        {
            guest = new Guest();
            guest.name = customer.getName();
            guest.phone = customer.getPhone();
            guest.email = customer.getEmail();
        }

        StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) paymentInformation;
        RoomInformation roomInformation = stayPaymentInformation.getSaleRoomInformation();

        Map<String, String> params = new HashMap<>();
        params.put("room_idx", String.valueOf(roomInformation.roomIndex));
        params.put("checkin_date", checkInSaleTime.getDayOfDaysDateFormat("yyyyMMdd"));
        params.put("nights", String.valueOf(roomInformation.nights));
        params.put("billkey", mSelectedCreditCard.billingkey);

        switch (paymentInformation.discountType)
        {
            case BONUS:
                String bonus = String.valueOf(paymentInformation.bonus);
                params.put("bonus", bonus);
                break;

            case COUPON:
                Coupon coupon = paymentInformation.getCoupon();
                params.put("user_coupon_code", coupon.userCouponCode);
                break;
        }

        params.put("guest_name", guest.name);
        params.put("guest_phone", guest.phone.replace("-", ""));
        params.put("guest_email", guest.email);
        params.put("guest_msg", "");

        // 주차/도보
        if (StayPaymentInformation.VISIT_TYPE_PARKING.equalsIgnoreCase(stayPaymentInformation.visitType) == true)
        {
            params.put("arrival_transportation", stayPaymentInformation.isVisitWalking == true ? "WALKING" : "CAR");
        } else if (StayPaymentInformation.VISIT_TYPE_NO_PARKING.equalsIgnoreCase(stayPaymentInformation.visitType) == true)
        {
            params.put("arrival_transportation", "NO_PARKING");
        }

        if (DEBUG == false)
        {
            if (customer == null)
            {
                Crashlytics.log("HotelPaymentActivity::requestEasyPayment :: customer is null");
            } else if (Util.isTextEmpty(customer.getName()) == true)
            {
                Crashlytics.log("HotelPaymentActivity::requestEasyPayment :: name=" //
                    + customer.getName() + " , userIndex=" + customer.getUserIdx() + " , user_email=" + customer.getEmail());
            }
        }

        DailyMobileAPI.getInstance(this).requestStayPayment(mNetworkTag, params, mPaymentEasyCreditCardCallback);
    }

    @Override
    protected void requestFreePayment(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime)
    {
        if (paymentInformation == null || checkInSaleTime == null)
        {
            Util.restartApp(this);
            return;
        }

        lockUI();

        Guest guest = mHotelPaymentLayout.getGuest();
        Customer customer = paymentInformation.getCustomer();

        if (guest == null)
        {
            guest = new Guest();
            guest.name = customer.getName();
            guest.phone = customer.getPhone();
            guest.email = customer.getEmail();
        }

        StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) paymentInformation;
        RoomInformation roomInformation = stayPaymentInformation.getSaleRoomInformation();

        Map<String, String> params = new HashMap<>();
        params.put("room_idx", String.valueOf(roomInformation.roomIndex));
        params.put("checkin_date", checkInSaleTime.getDayOfDaysDateFormat("yyyyMMdd"));
        params.put("nights", String.valueOf(roomInformation.nights));
        params.put("billkey", mSelectedCreditCard.billingkey);

        switch (paymentInformation.discountType)
        {
            case BONUS:
                String bonus = String.valueOf(paymentInformation.bonus);
                params.put("bonus", bonus);
                break;

            case COUPON:
                Coupon coupon = paymentInformation.getCoupon();
                params.put("user_coupon_code", coupon.userCouponCode);
                break;
        }

        params.put("guest_name", guest.name);
        params.put("guest_phone", guest.phone.replace("-", ""));
        params.put("guest_email", guest.email);
        params.put("guest_msg", "");

        // 주차/도보
        if (StayPaymentInformation.VISIT_TYPE_PARKING.equalsIgnoreCase(stayPaymentInformation.visitType) == true)
        {
            params.put("arrival_transportation", stayPaymentInformation.isVisitWalking == true ? "WALKING" : "CAR");
        } else if (StayPaymentInformation.VISIT_TYPE_NO_PARKING.equalsIgnoreCase(stayPaymentInformation.visitType) == true)
        {
            params.put("arrival_transportation", "NO_PARKING");
        }

        if (DEBUG == false)
        {
            if (customer == null)
            {
                Crashlytics.log("HotelPaymentActivity::requestEasyPayment :: customer is null");
            } else if (Util.isTextEmpty(customer.getName()) == true)
            {
                Crashlytics.log("HotelPaymentActivity::requestEasyPayment :: name=" //
                    + customer.getName() + " , userIndex=" + customer.getUserIdx() + " , user_email=" + customer.getEmail());
            }
        }

        //        DailyMobileAPI.getInstance(this).requestStayFreePayment(mNetworkTag, params, mPaymentEasyCreditCardCallback);
    }

    @Override
    protected void requestPlacePaymentInformation(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime)
    {
        RoomInformation roomInformation = ((StayPaymentInformation) paymentInformation).getSaleRoomInformation();

        // 호텔 디테일 정보 재 요청
        DailyMobileAPI.getInstance(this).requestStayPaymentInformation(mNetworkTag//
            , roomInformation.roomIndex//
            , checkInSaleTime.getDayOfDaysDateFormat("yyyyMMdd")//
            , roomInformation.nights, mHotelPaymentInformationCallback);
    }

    @Override
    protected void setSimpleCardInformation(PlacePaymentInformation paymentInformation, CreditCard selectedCreditCard)
    {
        mHotelPaymentLayout.setPaymentInformation((StayPaymentInformation) paymentInformation, selectedCreditCard);
    }

    @Override
    protected void setGuestInformation(String phoneNumber)
    {
        mHotelPaymentLayout.setGuestPhoneInformation(phoneNumber);
    }

    @Override
    protected void changedPaymentType(PlacePaymentInformation.PaymentType paymentType, CreditCard creditCard)
    {
        mSelectedCreditCard = creditCard;

        if (paymentType == PlacePaymentInformation.PaymentType.EASY_CARD &&//
            creditCard != null && Util.isTextEmpty(creditCard.billingkey) == false)
        {
            DailyPreference.getInstance(this).setSelectedSimpleCard(Crypto.urlEncrypt(creditCard.billingkey));
        }

        mOnEventListener.changedPaymentType(paymentType);
    }

    @Override
    protected boolean isChangedPrice()
    {
        return mIsChangedPrice;
    }

    @Override
    protected boolean hasWarningMessage()
    {
        return (Util.isTextEmpty(mWarningDialogMessage) == false);
    }

    @Override
    protected void showWarningMessageDialog()
    {
        showSimpleDialog(getString(R.string.dialog_notice2), mWarningDialogMessage, getString(R.string.dialog_btn_text_confirm), null, new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                mWarningDialogMessage = "";
            }
        });
    }

    @Override
    protected void showChangedPriceDialog()
    {
        unLockUI();

        setBonusSelected(false);
        setCouponSelected(false);
        setPaymentInformation((StayPaymentInformation) mPaymentInformation);

        showChangedValueDialog(R.string.message_stay_detail_changed_price, new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                mDontReload = false;
                mIsChangedPrice = false;
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
            }
        });

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, //
            Action.SOLDOUT_CHANGEPRICE, ((StayPaymentInformation) mPaymentInformation).getSaleRoomInformation().hotelName, null);
    }

    @Override
    protected void showStopOnSaleDialog()
    {
        showChangedValueDialog(R.string.dialog_msg_stay_stop_onsale, null);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.POPUP_BOXES, //
            Action.SOLDOUT, ((StayPaymentInformation) mPaymentInformation).getSaleRoomInformation().hotelName, null);
    }

    @Override
    protected void showChangedBonusDialog()
    {
        unLockUI();

        setBonusSelected(false);
        setCouponSelected(false);
        setPaymentInformation((StayPaymentInformation) mPaymentInformation);

        super.showChangedBonusDialog();
    }

    @Override
    protected void showPaymentWeb(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime)
    {
        Intent intent = new Intent(this, HotelPaymentWebActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PAYMENTINFORMATION, paymentInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkInSaleTime);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PAYMENT);
    }

    @Override
    protected void showPaymentThankyou(PlacePaymentInformation paymentInformation, String imageUrl)
    {
        if (paymentInformation.paymentType == PlacePaymentInformation.PaymentType.EASY_CARD)
        {
            try
            {
                DailyPreference.getInstance(this).setSelectedSimpleCard(Crypto.urlEncrypt(mSelectedCreditCard.billingkey));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) paymentInformation;
        RoomInformation roomInformation = stayPaymentInformation.getSaleRoomInformation();

        String discountType = Label.FULL_PAYMENT;

        switch (paymentInformation.discountType)
        {
            case BONUS:
                if (paymentInformation.bonus != 0)
                {
                    discountType = Label.PAYMENTWITH_CREDIT;
                }
                break;

            case COUPON:
                discountType = Label.PAYMENTWITH_COUPON;
                break;
        }

        String placeName = roomInformation.hotelName;
        String placeType = roomInformation.roomName;
        String checkInDate = DailyCalendar.format(stayPaymentInformation.checkInDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT"));
        String checkOutDate = DailyCalendar.format(stayPaymentInformation.checkOutDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT"));
        int nights = stayPaymentInformation.nights;
        String userName = stayPaymentInformation.getCustomer() == null ? "" : stayPaymentInformation.getCustomer().getName();
        String userIndex = stayPaymentInformation.getCustomer() == null ? "" : stayPaymentInformation.getCustomer().getUserIdx();

        if (Util.isTextEmpty(userName) == true)
        {
            try
            {
                String message = "Empty UserName :: placeIndex:" + stayPaymentInformation.placeIndex //
                    + ",roomIndex:" + roomInformation.roomIndex + ",checkIn:" + checkInDate//
                    + ",checkOut:" + checkOutDate + ",placeName:" + placeName + ",payType:" + paymentInformation.paymentType//
                    + ",userIndex:" + userIndex;
                Crashlytics.logException(new NullPointerException(message));
            } catch (Exception e)
            {
            }
        }

        Map<String, String> params = getMapPaymentInformation((StayPaymentInformation) paymentInformation);

        Intent intent = HotelPaymentThankyouActivity.newInstance(this, imageUrl, placeName, placeType, //
            userName, checkInDate, checkOutDate, nights, paymentInformation.paymentType.getName(), discountType, params);

        startActivityForResult(intent, REQUEST_CODE_PAYMETRESULT_ACTIVITY);
    }

    @Override
    protected Dialog getEasyPaymentConfirmDialog()
    {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

        int[] messageResIds;
        if (mPensionPopupMessageType != 0)
        {
            messageResIds = pensionPaymentDialogMessage(mPensionPopupMessageType, PlacePaymentInformation.PaymentType.EASY_CARD);
        } else
        {
            messageResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                , R.string.dialog_msg_hotel_payment_message14//
                , R.string.dialog_msg_hotel_payment_message02//
                , R.string.dialog_msg_hotel_payment_message03//
                , R.string.dialog_msg_hotel_payment_message07};
        }


        final FinalCheckLayout finalCheckLayout = new FinalCheckLayout(HotelPaymentActivity.this);
        finalCheckLayout.setMessages(messageResIds);

        final TextView agreeSignatureTextView = (TextView) finalCheckLayout.findViewById(R.id.agreeSignatureTextView);
        final View confirmTextView = finalCheckLayout.findViewById(R.id.confirmTextView);

        confirmTextView.setEnabled(false);

        // 화면이 작은 곳에서 스크롤 뷰가 들어가면서 발생하는 이슈
        final DailyScrollView scrollLayout = (DailyScrollView) finalCheckLayout.findViewById(R.id.scrollLayout);

        View dailySignatureView = finalCheckLayout.getDailySignatureView();

        dailySignatureView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction() & MotionEventCompat.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        scrollLayout.setScrollingEnabled(false);
                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    {
                        scrollLayout.setScrollingEnabled(true);
                        break;
                    }
                }

                return false;
            }
        });

        finalCheckLayout.setOnUserActionListener(new DailySignatureView.OnUserActionListener()
        {
            @Override
            public void onConfirmSignature()
            {
                AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(500);
                animation.setFillBefore(true);
                animation.setFillAfter(true);

                animation.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        agreeSignatureTextView.setAnimation(null);
                        agreeSignatureTextView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                    }
                });

                agreeSignatureTextView.startAnimation(animation);

                //                TransitionDrawable transition = (TransitionDrawable) confirmTextView.getBackground();
                //                transition.startTransition(500);

                confirmTextView.setEnabled(true);
                confirmTextView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        synchronized (HotelPaymentActivity.this)
                        {
                            if (isLockUiComponent() == true)
                            {
                                return;
                            }

                            dialog.dismiss();

                            lockUI();

                            // 1. 세션이 살아있는지 검사 시작.
                            DailyMobileAPI.getInstance(HotelPaymentActivity.this).requestUserInformationForPayment(mNetworkTag, mUserInformationFinalCheckCallback);
                        }
                    }
                });
            }
        });

        AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.START_PAYMENT, mPaymentInformation.paymentType.getName(), null);

        dialog.setContentView(finalCheckLayout);

        return dialog;
    }

    @Override
    protected Dialog getPaymentConfirmDialog(PlacePaymentInformation.PaymentType paymentType)
    {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.fragment_dialog_confirm_payment, null);
        ViewGroup messageLayout = (ViewGroup) view.findViewById(R.id.messageLayout);

        int[] textResIds;


        if (mPensionPopupMessageType != 0)
        {
            textResIds = pensionPaymentDialogMessage(mPensionPopupMessageType, paymentType);
        } else
        {
            switch (paymentType)
            {
                // 신용카드 일반 결제
                case CARD:
                    textResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                        , R.string.dialog_msg_hotel_payment_message14//
                        , R.string.dialog_msg_hotel_payment_message02//
                        , R.string.dialog_msg_hotel_payment_message03//
                        , R.string.dialog_msg_hotel_payment_message06};
                    break;

                // 핸드폰 결제
                case PHONE_PAY:
                    textResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                        , R.string.dialog_msg_hotel_payment_message14//
                        , R.string.dialog_msg_hotel_payment_message02//
                        , R.string.dialog_msg_hotel_payment_message03//
                        , R.string.dialog_msg_hotel_payment_message06};
                    break;

                // 계좌 이체
                case VBANK:
                    textResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                        , R.string.dialog_msg_hotel_payment_message14//
                        , R.string.dialog_msg_hotel_payment_message02//
                        , R.string.dialog_msg_hotel_payment_message03//
                        , R.string.dialog_msg_hotel_payment_message05//
                        , R.string.dialog_msg_hotel_payment_message06};
                    break;

                default:
                    textResIds = null;
                    break;
            }
        }

        if (textResIds == null)
        {
            return null;
        }

        makeDialogMessages(messageLayout, textResIds);

        View confirmTextView = view.findViewById(R.id.confirmTextView);

        OnClickListener buttonOnClickListener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

                synchronized (HotelPaymentActivity.this)
                {
                    if (isLockUiComponent() == true)
                    {
                        return;
                    }

                    lockUI();

                    // 1. 세션이 살아있는지 검사 시작.
                    DailyMobileAPI.getInstance(HotelPaymentActivity.this).requestUserInformationForPayment(mNetworkTag, mUserInformationFinalCheckCallback);

                    AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                        , AnalyticsManager.Action.START_PAYMENT, mPaymentInformation.paymentType.getName(), null);
                }
            }
        };

        confirmTextView.setOnClickListener(buttonOnClickListener);

        dialog.setContentView(view);

        return dialog;
    }

    @Override
    protected void onActivityPaymentResult(int requestCode, int resultCode, Intent intent)
    {
        String title = getString(R.string.dialog_title_payment);
        String msg;
        String posTitle = getString(R.string.dialog_btn_text_confirm);
        OnClickListener posListener = null;

        if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_PAYMENTINFORMATION) == true)
        {
            Customer customer = mPaymentInformation.getCustomer();

            if (customer == null || Util.isTextEmpty(customer.getName(), customer.getUserIdx()) == true //
                || ((StayPaymentInformation) mPaymentInformation).checkInDate == 0//
                || ((StayPaymentInformation) mPaymentInformation).checkOutDate == 0)
            {
                if (DEBUG == false)
                {
                    Crashlytics.log("HotelPaymentActivity - onActivityPaymentResult : Clear mPaymentInformation");
                }

                mPaymentInformation = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PAYMENTINFORMATION);
            }
        }

        switch (resultCode)
        {
            // 결제가 성공한 경우 GA와 믹스패널에 등록
            case CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE:
            case CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS:
                recordAnalyticsPaymentComplete(mPaymentInformation);

                showPaymentThankyou(mPaymentInformation, mPlaceImageUrl);
                return;

            case CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT:
                msg = getString(R.string.act_toast_payment_soldout);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE:
                title = getString(R.string.dialog_notice2);
                msg = getString(R.string.act_toast_payment_not_available);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
                msg = getString(R.string.act_toast_payment_network_error);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
                restartExpiredSession();
                return;

            case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE:
                msg = getString(R.string.act_toast_payment_invalid_date);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
                msg = getString(R.string.act_toast_payment_fail);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_CANCELED:
                msg = getString(R.string.act_toast_payment_canceled);

                posListener = new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                    }
                };
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                /**
                 * 가상계좌선택시 해당 가상계좌 정보를 보기위해 화면 스택을 쌓으면서 들어가야함. 이를 위한 정보를 셋팅.
                 * 예약 리스트 프래그먼트에서 찾아 들어가기 위해서 필요함. 들어간 후에는 다시 프리퍼런스를 초기화해줌.
                 * 플로우) 예약 액티비티 => 호텔탭 액티비티 => 메인액티비티 => 예약 리스트 프래그먼트 => 예약
                 * 리스트 갱신 후 최상단 아이템 인텐트
                 */
                DailyPreference.getInstance(HotelPaymentActivity.this).setVirtualAccountReadyFlag(CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY);

                msg = getString(R.string.dialog_msg_issuing_account);

                posListener = new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setResult(CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY);
                        finish();
                    }
                };
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_TIME_ERROR:
                msg = getString(R.string.act_toast_payment_account_time_error);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_DUPLICATE:
                msg = getString(R.string.act_toast_payment_account_duplicate_type_hotel);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                msg = getString(R.string.act_toast_payment_account_timeover);
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_UNKNOW_ERROR:
                if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_MESSAGE) == true)
                {
                    msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_MESSAGE);
                } else
                {
                    msg = getString(R.string.act_toast_payment_fail);
                }
                break;

            case CODE_RESULT_ACTIVITY_PAYMENT_PRECHECK:
            {
                if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_MESSAGE))
                {
                    String result = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_MESSAGE);
                    String[] message = result.split("\\^");

                    try
                    {
                        int msgCode = Integer.parseInt(message[0]);
                        msg = message[1];

                        // 5	판매 마감시
                        // 1000	결제 성공시
                        // 1001	결제가 취소되었을시 ( 입금전 )
                        // 1002	예약 실패시 ( 입금 후 )
                        // 1003	입금 대기중 일시
                        // 1004	모든 객실이 판매되었을시
                        // 1005	마지막 상품을 다른 고객님이 결제중 일시
                        // 1006	이니시스 에러 메시지 일시
                        switch (msgCode)
                        {
                            case 5:
                            {
                                posListener = new OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        setResult(CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER);
                                        finish();
                                    }
                                };
                                break;
                            }

                            case 300:
                            case 302:
                            case 303:
                            case 304:
                            case 1010:
                            {
                                // 쿠폰 취소
                                mPaymentInformation.setCoupon(null);
                                setCouponSelected(false);
                                setBonusSelected(false);

                                posListener = new OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                    }
                                };
                                break;
                            }

                            case 1000:
                            {
                                recordAnalyticsPaymentComplete(mPaymentInformation);

                                showPaymentThankyou(mPaymentInformation, mPlaceImageUrl);
                                return;
                            }

                            case 1001:
                            case 1002:
                            case 1003:
                                posListener = new OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                    }
                                };
                                break;

                            case 1004:
                            case 1005:
                            case 1006:
                            default:
                                break;
                        }
                    } catch (Exception e)
                    {
                        msg = getString(R.string.act_toast_payment_fail);
                    }
                } else
                {
                    msg = getString(R.string.act_toast_payment_fail);
                }
                break;
            }

            default:
                return;
        }

        if (posListener == null)
        {
            posListener = new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    finish();
                }
            };
        }

        showSimpleDialog(title, msg, posTitle, null, posListener, null, false);
    }

    @Override
    protected void recordAnalyticsAgreeTermDialog(PlacePaymentInformation paymentInformation)
    {
        AnalyticsManager.getInstance(this).recordScreen(Screen.DAILYHOTEL_PAYMENT_AGREEMENT_POPUP//
            , getMapPaymentInformation((StayPaymentInformation) paymentInformation));
    }

    @Override
    protected void setCoupon(final Coupon coupon)
    {
        final StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;

        int originalPrice = stayPaymentInformation.getSaleRoomInformation().totalDiscount;

        if (coupon.amount > originalPrice)
        {
            String difference = Util.getPriceFormat(this, (coupon.amount - originalPrice), false);

            showSimpleDialog(null, getString(R.string.message_over_coupon_price, difference), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    stayPaymentInformation.setCoupon(coupon);
                    setCouponSelected(true);
                }
            }, new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    stayPaymentInformation.setCoupon(null);
                    setCouponSelected(false);
                }
            }, new DialogInterface.OnCancelListener()
            {

                @Override
                public void onCancel(DialogInterface dialog)
                {
                    stayPaymentInformation.setCoupon(null);
                    setCouponSelected(false);
                }
            }, null, true);

        } else
        {
            // 호텔 결제 정보에 쿠폰 가격 넣고 텍스트 업데이트 필요
            stayPaymentInformation.setCoupon(coupon);
            setCouponSelected(true);
        }
    }

    @Override
    protected void setCancelCoupon()
    {
        // 쿠폰 삭제 - 쿠폰 선택 팝업에서 Cancel 시 처리
        mPaymentInformation.setCoupon(null);
        setCouponSelected(false);
    }

    private void setAvailableDefaultPaymentType()
    {
        boolean isSimpleCardPaymentEnabled = DailyPreference.getInstance(this).isRemoteConfigStaySimpleCardPaymentEnabled();
        boolean isCardPaymentEnabled = DailyPreference.getInstance(this).isRemoteConfigStayCardPaymentEnabled();
        boolean isPhonePaymentEnabled = DailyPreference.getInstance(this).isRemoteConfigStayPhonePaymentEnabled();
        boolean isVirtualPaymentEnabled = DailyPreference.getInstance(this).isRemoteConfigStayVirtualPaymentEnabled();

        StringBuilder guideMemo = new StringBuilder();

        if (isSimpleCardPaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.label_simple_payment));
            guideMemo.append(", ");
        }

        if (isCardPaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.label_card_payment));
            guideMemo.append(", ");
        }

        if (isPhonePaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.act_booking_pay_mobile));
            guideMemo.append(", ");
        }

        if (isVirtualPaymentEnabled == false)
        {
            guideMemo.append(getString(R.string.act_booking_pay_account));
            guideMemo.append(", ");
        }

        if (guideMemo.length() > 0)
        {
            guideMemo.setLength(guideMemo.length() - 2);

            mHotelPaymentLayout.setPaymentMemoTextView(getString(R.string.message_dont_support_payment_type, guideMemo.toString()), true);
        } else
        {
            mHotelPaymentLayout.setPaymentMemoTextView(null, false);
        }

        mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.EASY_CARD, isSimpleCardPaymentEnabled);
        mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.CARD, isCardPaymentEnabled);
        mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.PHONE_PAY, isPhonePaymentEnabled);
        mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.VBANK, isVirtualPaymentEnabled);

        if (isSimpleCardPaymentEnabled == true)
        {
            changedPaymentType(PlacePaymentInformation.PaymentType.EASY_CARD, mSelectedCreditCard);
        } else if (isCardPaymentEnabled == true)
        {
            changedPaymentType(PlacePaymentInformation.PaymentType.CARD, mSelectedCreditCard);
        } else if (isPhonePaymentEnabled == true)
        {
            changedPaymentType(PlacePaymentInformation.PaymentType.PHONE_PAY, mSelectedCreditCard);
        } else if (isVirtualPaymentEnabled == true)
        {
            changedPaymentType(PlacePaymentInformation.PaymentType.VBANK, mSelectedCreditCard);
        }
    }

    @Override
    protected void processAgreeTermDialog()
    {
        super.processAgreeTermDialog();

        StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;

        String label = String.format("%s-%s", stayPaymentInformation.getSaleRoomInformation().hotelName, stayPaymentInformation.getSaleRoomInformation().roomName);
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , Action.PAYMENT_CLICKED, label, null);
    }


    private void recordAnalyticsPaymentComplete(PlacePaymentInformation paymentInformation)
    {
        try
        {
            String strDate = DailyCalendar.format(new Date(), "yyyyMMddHHmmss");
            String userIndex = paymentInformation.getCustomer().getUserIdx();
            String transId = strDate + '_' + userIndex;

            Map<String, String> params = getMapPaymentInformation((StayPaymentInformation) paymentInformation);

            AnalyticsManager.getInstance(getApplicationContext()).purchaseCompleteHotel(transId, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    protected void recordAnalyticsPayment(PlacePaymentInformation paymentInformation)
    {
        if (paymentInformation == null || Util.isTextEmpty(mScreenAnalytics) == true)
        {
            return;
        }

        StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, stayPaymentInformation.getSaleRoomInformation().hotelName);
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(stayPaymentInformation.getSaleRoomInformation().averageDiscount));
            params.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(stayPaymentInformation.getSaleRoomInformation().totalDiscount));
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(stayPaymentInformation.getSaleRoomInformation().nights));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayPaymentInformation.getSaleRoomInformation().nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayPaymentInformation.placeIndex));

            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + stayPaymentInformation.getSaleRoomInformation().nights);

            params.put(AnalyticsManager.KeyType.CHECK_IN, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.TICKET_NAME, stayPaymentInformation.getSaleRoomInformation().roomName);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(stayPaymentInformation.getSaleRoomInformation().roomIndex));
            params.put(AnalyticsManager.KeyType.GRADE, stayPaymentInformation.getSaleRoomInformation().grade.getName(HotelPaymentActivity.this));
            params.put(AnalyticsManager.KeyType.DBENEFIT, stayPaymentInformation.isDBenefit ? "yes" : "no");
            params.put(AnalyticsManager.KeyType.ADDRESS, stayPaymentInformation.getSaleRoomInformation().address);
            params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, stayPaymentInformation.getSaleRoomInformation().categoryCode);
            params.put(AnalyticsManager.KeyType.CATEGORY, stayPaymentInformation.getSaleRoomInformation().categoryCode);
            params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, mSelectedCreditCard != null ? "y" : "n");
            params.put(AnalyticsManager.KeyType.NRD, stayPaymentInformation.getSaleRoomInformation().isNRD ? "y" : "n");
            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayPaymentInformation.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, stayPaymentInformation.isShowOriginalPrice);
            params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(stayPaymentInformation.entryPosition));
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, stayPaymentInformation.isDailyChoice ? "y" : "n");

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

            AnalyticsManager.getInstance(HotelPaymentActivity.this).recordScreen(mScreenAnalytics, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private Map<String, String> getMapPaymentInformation(StayPaymentInformation stayPaymentInformation)
    {
        if (stayPaymentInformation == null)
        {
            return null;
        }

        Map<String, String> params = new HashMap<>();

        try
        {
            RoomInformation roomInformation = stayPaymentInformation.getSaleRoomInformation();

            params.put(AnalyticsManager.KeyType.NAME, roomInformation.hotelName);
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayPaymentInformation.placeIndex));
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(roomInformation.averageDiscount));
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(roomInformation.nights));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(roomInformation.nights));
            params.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(roomInformation.totalDiscount));
            params.put(AnalyticsManager.KeyType.TICKET_NAME, roomInformation.roomName);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(roomInformation.roomIndex));
            params.put(AnalyticsManager.KeyType.GRADE, stayPaymentInformation.getSaleRoomInformation().grade.getName(this));
            params.put(AnalyticsManager.KeyType.DBENEFIT, stayPaymentInformation.isDBenefit ? "yes" : "no");

            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + stayPaymentInformation.getSaleRoomInformation().nights);

            params.put(AnalyticsManager.KeyType.CHECK_IN, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

            params.put(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD, mSelectedCreditCard != null ? "y" : "n");
            params.put(AnalyticsManager.KeyType.NRD, stayPaymentInformation.getSaleRoomInformation().isNRD ? "y" : "n");
            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayPaymentInformation.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, stayPaymentInformation.isShowOriginalPrice);
            params.put(AnalyticsManager.KeyType.LIST_INDEX, Integer.toString(stayPaymentInformation.entryPosition));
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, stayPaymentInformation.isDailyChoice ? "y" : "n");

            switch (stayPaymentInformation.discountType)
            {
                case BONUS:
                {
                    int payPrice = roomInformation.totalDiscount - stayPaymentInformation.bonus;
                    int bonus;

                    if (payPrice <= 0)
                    {
                        payPrice = 0;
                        bonus = roomInformation.totalDiscount;
                    } else
                    {
                        bonus = stayPaymentInformation.bonus;
                    }

                    params.put(AnalyticsManager.KeyType.USED_BOUNS, Integer.toString(bonus));
                    params.put(AnalyticsManager.KeyType.COUPON_REDEEM, "false");
                    params.put(AnalyticsManager.KeyType.COUPON_NAME, "");
                    params.put(AnalyticsManager.KeyType.COUPON_CODE, "");
                    params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(payPrice));
                    break;
                }

                case COUPON:
                {
                    Coupon coupon = stayPaymentInformation.getCoupon();
                    int payPrice = roomInformation.totalDiscount - coupon.amount;

                    if (payPrice < 0)
                    {
                        payPrice = 0;
                    }

                    params.put(AnalyticsManager.KeyType.USED_BOUNS, "0");
                    params.put(AnalyticsManager.KeyType.COUPON_REDEEM, "true");
                    params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(payPrice));
                    params.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                    params.put(AnalyticsManager.KeyType.COUPON_CODE, coupon.couponCode);
                    params.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                    params.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));

                    String expireDate = DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm");
                    params.put(AnalyticsManager.KeyType.EXPIRATION_DATE, expireDate);
                    break;
                }

                default:
                {
                    params.put(AnalyticsManager.KeyType.USED_BOUNS, "0");
                    params.put(AnalyticsManager.KeyType.COUPON_REDEEM, "false");
                    params.put(AnalyticsManager.KeyType.COUPON_NAME, "");
                    params.put(AnalyticsManager.KeyType.COUPON_CODE, "");
                    params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(roomInformation.totalDiscount));
                    break;
                }
            }

            params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, stayPaymentInformation.paymentType.getName());
            params.put(AnalyticsManager.KeyType.ADDRESS, stayPaymentInformation.getSaleRoomInformation().address);
            params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, stayPaymentInformation.getSaleRoomInformation().categoryCode);
            params.put(AnalyticsManager.KeyType.CATEGORY, stayPaymentInformation.getSaleRoomInformation().categoryCode);

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

            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, Long.toString(mCheckInSaleTime.getDayOfDaysDate().getTime()));
            params.put(AnalyticsManager.KeyType.CHECK_OUT_DATE, Long.toString(checkOutSaleTime.getDayOfDaysDate().getTime()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return params;
    }

    private void setPaymentInformation(StayPaymentInformation stayPaymentInformation)
    {
        if (stayPaymentInformation == null)
        {
            return;
        }

        int nights = stayPaymentInformation.getSaleRoomInformation().nights;
        int originalPrice = stayPaymentInformation.getSaleRoomInformation().totalDiscount;
        int payPrice = originalPrice;

        switch (stayPaymentInformation.discountType)
        {
            case BONUS:
            {
                stayPaymentInformation.setCoupon(null);

                int discountPrice = stayPaymentInformation.bonus;

                if (discountPrice > 0)
                {
                    setBonusEnabled(true);

                    if (discountPrice < originalPrice)
                    {
                        payPrice = originalPrice - discountPrice;
                    } else
                    {
                        payPrice = 0;
                        discountPrice = originalPrice;
                    }
                }

                mHotelPaymentLayout.setPaymentInformation(PlacePaymentInformation.DiscountType.BONUS, originalPrice, discountPrice, payPrice, nights);
                break;
            }

            case COUPON:
            {
                Coupon coupon = stayPaymentInformation.getCoupon();

                if (coupon == null)
                {
                    mHotelPaymentLayout.setPaymentInformation(PlacePaymentInformation.DiscountType.COUPON, originalPrice, 0, payPrice, nights);
                } else
                {
                    int discountPrice = coupon.amount;

                    if (discountPrice < originalPrice)
                    {
                        payPrice = originalPrice - discountPrice;
                    } else
                    {
                        payPrice = 0;
                        discountPrice = originalPrice;
                    }

                    mHotelPaymentLayout.setPaymentInformation(PlacePaymentInformation.DiscountType.COUPON, originalPrice, discountPrice, payPrice, nights);
                }
                break;
            }

            default:
                if (stayPaymentInformation.bonus <= 0)
                {
                    setBonusEnabled(false);
                }

                mHotelPaymentLayout.setPaymentInformation(stayPaymentInformation.discountType, originalPrice, 0, payPrice, nights);
                break;
        }

        if (payPrice == 0)
        {
            stayPaymentInformation.isFree = true;
        } else
        {
            stayPaymentInformation.isFree = false;
        }

        mHotelPaymentLayout.setBonusTextView(stayPaymentInformation.bonus);

        // 1000원 미만 결제시에 간편/일반 결제 불가 - 쿠폰 또는 적립금 전체 사용이 아닌경우 조건 추가
        if (payPrice > 0 && payPrice < 1000)
        {
            mIsUnderPrice = true;

            mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.EASY_CARD, false);
            mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.CARD, false);

            mOnEventListener.changedPaymentType(PlacePaymentInformation.PaymentType.PHONE_PAY);
        } else
        {
            if (DailyPreference.getInstance(this).isRemoteConfigStaySimpleCardPaymentEnabled() == true)
            {
                mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.EASY_CARD, true);
            }

            if (DailyPreference.getInstance(this).isRemoteConfigStayCardPaymentEnabled() == true)
            {
                mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.CARD, true);
            }

            // 50만원 한도 핸드폰 결제 금지
            if (payPrice >= PHONE_PAYMENT_LIMIT)
            {
                if (mPaymentInformation.paymentType == PlacePaymentInformation.PaymentType.PHONE_PAY)
                {
                    mOnEventListener.changedPaymentType(getAvailableDefaultPaymentType());
                }

                mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.PHONE_PAY, false);
            } else
            {
                if (DailyPreference.getInstance(this).isRemoteConfigStayPhonePaymentEnabled() == true)
                {
                    mHotelPaymentLayout.setPaymentTypeEnabled(PlacePaymentInformation.PaymentType.PHONE_PAY, true);
                }
            }

            // 1000원 이하였다가 되돌아 오는 경우 한번 간편결제로 바꾸어준다.
            if (mIsUnderPrice == true)
            {
                mIsUnderPrice = false;

                mOnEventListener.changedPaymentType(getAvailableDefaultPaymentType());
            } else
            {
                mOnEventListener.changedPaymentType(mPaymentInformation.paymentType);
            }
        }
    }

    private PlacePaymentInformation.PaymentType getAvailableDefaultPaymentType()
    {
        if (DailyPreference.getInstance(this).isRemoteConfigStaySimpleCardPaymentEnabled() == true &&//
            mHotelPaymentLayout.isPaymentTypeEnabled(PlacePaymentInformation.PaymentType.EASY_CARD) == true)
        {
            return PlacePaymentInformation.PaymentType.EASY_CARD;
        } else if (DailyPreference.getInstance(this).isRemoteConfigStayCardPaymentEnabled() == true &&//
            mHotelPaymentLayout.isPaymentTypeEnabled(PlacePaymentInformation.PaymentType.CARD) == true)
        {
            return PlacePaymentInformation.PaymentType.CARD;
        } else if (DailyPreference.getInstance(this).isRemoteConfigStayPhonePaymentEnabled() == true &&//
            mHotelPaymentLayout.isPaymentTypeEnabled(PlacePaymentInformation.PaymentType.PHONE_PAY) == true)
        {
            return PlacePaymentInformation.PaymentType.PHONE_PAY;
        } else if (DailyPreference.getInstance(this).isRemoteConfigStayVirtualPaymentEnabled() == true &&//
            mHotelPaymentLayout.isPaymentTypeEnabled(PlacePaymentInformation.PaymentType.VBANK) == true)
        {
            return PlacePaymentInformation.PaymentType.VBANK;
        } else
        {
            return null;
        }
    }

    /**
     * selected가 true enabled가 false일수는 없다.
     *
     * @param isEnabled
     */
    private void setBonusEnabled(boolean isEnabled)
    {
        mHotelPaymentLayout.setBonusEnabled(isEnabled);

        if (isEnabled == true)
        {
        } else
        {
            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.NONE;
        }
    }

    private void setBonusSelected(boolean isSelected)
    {
        if (mHotelPaymentLayout.setBonusSelected(isSelected) == false)
        {
            return;
        }

        if (isSelected == true)
        {
            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.BONUS;

            AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.USING_CREDIT_CLICKED, Integer.toString(mPaymentInformation.bonus), null);
        } else
        {
            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.NONE;

            AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.USING_CREDIT_CANCEL_CLICKED, Integer.toString(mPaymentInformation.bonus), null);
        }

        setPaymentInformation((StayPaymentInformation) mPaymentInformation);
    }

    private void setCouponSelected(boolean isSelected)
    {
        mHotelPaymentLayout.setCouponSelected(isSelected);

        if (isSelected == true)
        {
            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.COUPON;
        } else
        {
            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.NONE;
        }

        setPaymentInformation((StayPaymentInformation) mPaymentInformation);
    }

    private void startCouponPopup(StayPaymentInformation stayPaymentInformation)
    {
        RoomInformation roomInformation = stayPaymentInformation.getSaleRoomInformation();

        int hotelIdx = stayPaymentInformation.placeIndex;
        int roomIdx = roomInformation.roomIndex;
        String checkInDate = stayPaymentInformation.checkInDateFormat;
        String checkOutDate = stayPaymentInformation.checkOutDateFormat;

        String categoryCode = roomInformation.categoryCode;
        String hotelName = roomInformation.hotelName;
        String roomPrice = Integer.toString(roomInformation.averageDiscount);

        Intent intent = SelectStayCouponDialogActivity.newInstance(HotelPaymentActivity.this, hotelIdx, //
            roomIdx, checkInDate, checkOutDate, categoryCode, hotelName, roomPrice);
        startActivityForResult(intent, REQUEST_CODE_COUPONPOPUP_ACTIVITY);

        AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
            Action.HOTEL_USING_COUPON_CLICKED, Label.HOTEL_USING_COUPON_CLICKED, null);
    }

    private void startCancelBonusPopup(View.OnClickListener positiveListener)
    {
        showSimpleDialog(null, getString(R.string.message_booking_cancel_bonus), getString(R.string.dialog_btn_text_yes), //
            getString(R.string.dialog_btn_text_no), positiveListener, null);
    }

    private void startCancelCouponPopup(View.OnClickListener positiveListener)
    {
        showSimpleDialog(null, getString(R.string.message_booking_cancel_coupon), getString(R.string.dialog_btn_text_yes), //
            getString(R.string.dialog_btn_text_no), positiveListener, null);
    }

    private int[] pensionPaymentDialogMessage(int messageType, PlacePaymentInformation.PaymentType paymentType)
    {
        if (paymentType == null)
        {
            return null;
        }

        int[] messageList;
        if (PlacePaymentInformation.PaymentType.VBANK == paymentType)
        {
            messageList = new int[6];
        } else
        {
            messageList = new int[5];
        }

        messageList[0] = R.string.dialog_msg_hotel_payment_message01;
        messageList[1] = R.string.dialog_msg_hotel_payment_message14;

        switch (messageType)
        {
            case 1:
            case 2:
                messageList[2] = R.string.dialog_msg_hotel_payment_message09;
                break;

            case 10:
                messageList[2] = R.string.dialog_msg_hotel_payment_message10;
                break;

            case 3:
                messageList[2] = R.string.dialog_msg_hotel_payment_message11;
                break;

            case 11:
            case 12:
                messageList[2] = R.string.dialog_msg_hotel_payment_message12;
                break;

            default:
                break;
        }

        messageList[3] = R.string.dialog_msg_hotel_payment_message03;

        switch (paymentType)
        {
            case EASY_CARD:
                messageList[4] = R.string.dialog_msg_hotel_payment_message07;
                break;

            case VBANK:
                messageList[4] = R.string.dialog_msg_hotel_payment_message05;
                messageList[5] = R.string.dialog_msg_hotel_payment_message06;
                break;

            default:
                messageList[4] = R.string.dialog_msg_hotel_payment_message06;
                break;
        }

        return messageList;
    }


    private void setReservationInformation(long checkInDate, long checkOutDate, int nights)
    {
        StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;

        stayPaymentInformation.checkInDate = checkInDate;
        stayPaymentInformation.checkOutDate = checkOutDate;
        stayPaymentInformation.nights = nights;

        mHotelPaymentLayout.setReservationInformation(stayPaymentInformation);

        // Check In
        Calendar calendarCheckin = DailyCalendar.getInstance();
        calendarCheckin.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendarCheckin.setTimeInMillis(checkInDate);

        // CheckOut
        Calendar calendarCheckout = DailyCalendar.getInstance();
        calendarCheckout.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendarCheckout.setTimeInMillis(checkOutDate);

        calendarCheckin.setTimeInMillis(calendarCheckin.getTimeInMillis() - 3600 * 1000 * 9);
        calendarCheckout.setTimeInMillis(calendarCheckout.getTimeInMillis() - 3600 * 1000 * 9);

        stayPaymentInformation.checkInDateFormat = DailyCalendar.format(calendarCheckin.getTime(), DailyCalendar.ISO_8601_FORMAT);
        stayPaymentInformation.checkOutDateFormat = DailyCalendar.format(calendarCheckout.getTime(), DailyCalendar.ISO_8601_FORMAT);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // User ActionListener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private HotelPaymentLayout.OnEventListener mOnEventListener = new HotelPaymentLayout.OnEventListener()
    {
        @Override
        public void startCreditCardManager(boolean isRegister)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mPaymentInformation.setGuest(mHotelPaymentLayout.getGuest());

            if (isRegister == true)
            {
                AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                    , AnalyticsManager.Action.EDIT_BUTTON_CLICKED, AnalyticsManager.Label.PAYMENT_CARD_REGISTRATION, null);

                Intent intent = new Intent(HotelPaymentActivity.this, RegisterCreditCardActivity.class);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD);
            } else
            {
                startCreditCardList();
            }
        }

        @Override
        public void changedPaymentType(PlacePaymentInformation.PaymentType paymentType)
        {
            mPaymentInformation.paymentType = paymentType;
            mHotelPaymentLayout.checkPaymentType(paymentType);
        }

        @Override
        public void doPayment()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;

            Guest guest = mHotelPaymentLayout.getGuest();

            if (guest != null)
            {
                if (Util.isTextEmpty(guest.name) == true)
                {
                    releaseUiComponent();

                    mHotelPaymentLayout.requestGuestInformationFocus(Constants.UserInformationType.NAME);

                    if (stayPaymentInformation.getSaleRoomInformation().isOverseas == true)
                    {
                        DailyToast.showToast(HotelPaymentActivity.this, R.string.toast_msg_please_input_guest_typeoverseas, Toast.LENGTH_SHORT);
                    } else
                    {
                        DailyToast.showToast(HotelPaymentActivity.this, R.string.toast_msg_please_input_guest, Toast.LENGTH_SHORT);
                    }
                    return;
                } else if (Util.isTextEmpty(guest.phone) == true)
                {
                    releaseUiComponent();

                    mHotelPaymentLayout.requestGuestInformationFocus(Constants.UserInformationType.PHONE);

                    DailyToast.showToast(HotelPaymentActivity.this, R.string.toast_msg_please_input_contact, Toast.LENGTH_SHORT);
                    return;
                } else if (Util.isTextEmpty(guest.email) == true)
                {
                    releaseUiComponent();

                    mHotelPaymentLayout.requestGuestInformationFocus(UserInformationType.EMAIL);

                    DailyToast.showToast(HotelPaymentActivity.this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
                    return;
                } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(guest.email).matches() == false)
                {
                    releaseUiComponent();

                    mHotelPaymentLayout.requestGuestInformationFocus(Constants.UserInformationType.EMAIL);

                    DailyToast.showToast(HotelPaymentActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }

                if (stayPaymentInformation.getSaleRoomInformation().isOverseas == true)
                {
                    DailyPreference.getInstance(HotelPaymentActivity.this).setOverseasUserInformation(guest.name, guest.phone, guest.email);
                }
            }

            stayPaymentInformation.setGuest(guest);

            //호텔 가격이 xx 이하인 이벤트 호텔에서는 적립금 사용을 못하게 막음.
            if (stayPaymentInformation.discountType == PlacePaymentInformation.DiscountType.BONUS //
                && (stayPaymentInformation.getSaleRoomInformation().totalDiscount <= DEFAULT_AVAILABLE_RESERVES) //
                && stayPaymentInformation.bonus != 0)
            {
                setBonusSelected(false);

                String msg = getString(R.string.dialog_btn_payment_no_reserve, Util.getPriceFormat(HotelPaymentActivity.this, DEFAULT_AVAILABLE_RESERVES, false));

                showSimpleDialog(getString(R.string.dialog_notice2), msg, getString(R.string.dialog_btn_text_confirm), null);

                releaseUiComponent();
            } else
            {
                Stay.Grade hotelGrade = stayPaymentInformation.getSaleRoomInformation().grade;
                if (Stay.Grade.pension == hotelGrade | Stay.Grade.fullvilla == hotelGrade)
                {
                    lockUI();

                    DailyMobileAPI.getInstance(HotelPaymentActivity.this).requestCommonDateTime(mNetworkTag, mMessageDateTimeCallback);
                } else
                {
                    processAgreeTermDialog();
                }
            }
        }

        @Override
        public void showInputMobileNumberDialog(String mobileNumber)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mPaymentInformation.setGuest(mHotelPaymentLayout.getGuest());

            Intent intent = InputMobileNumberDialogActivity.newInstance(HotelPaymentActivity.this, mobileNumber);
            startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY);
        }

        @Override
        public void showCallDialog()
        {
            HotelPaymentActivity.this.showCallDialog();
        }

        @Override
        public void onBonusClick(boolean isRadioLayout)
        {
            switch (mPaymentInformation.discountType)
            {
                case BONUS:
                {
                    if (isRadioLayout == true)
                    {
                        setBonusSelected(true);
                    } else
                    {
                        // 적립금 삭제
                        startCancelBonusPopup(new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                setBonusSelected(false);
                            }
                        });
                    }
                    break;
                }

                case COUPON:
                {
                    // 쿠폰 기 선택 상태 일때 쿠폰 선택 취소 팝업 생성 필요함 (">" 아이콘 이므로)
                    startCancelCouponPopup(new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mPaymentInformation.setCoupon(null);
                            setCouponSelected(false);
                            setBonusSelected(true);
                        }
                    });
                    break;
                }

                default:
                {
                    // 아무것도 선택 되지 않은 상태 일때 bonusLayout 과 동일한 처리
                    setBonusSelected(true);
                    break;
                }
            }
        }

        @Override
        public void onCouponClick(boolean isRadioLayout)
        {
            switch (mPaymentInformation.discountType)
            {
                case BONUS:
                {
                    // 적립금 기 선택 상태 일때 적립금 선택 취소 팝업 생성 필요함 (">" 아이콘 이므로)
                    startCancelBonusPopup(new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setBonusSelected(false);
                            setCouponSelected(true);
                            startCouponPopup((StayPaymentInformation) mPaymentInformation);
                        }
                    });
                    break;
                }

                case COUPON:
                {
                    if (isRadioLayout == true)
                    {
                        setCouponSelected(true);
                        startCouponPopup((StayPaymentInformation) mPaymentInformation);
                    } else
                    {
                        // 쿠폰 삭제
                        startCancelCouponPopup(new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mPaymentInformation.setCoupon(null);
                                setCouponSelected(false);
                            }
                        });
                    }
                    break;
                }

                default:
                {
                    // 아무것도 선택 되지 않은 상태 일때 couponLayout 과 동일한 처리
                    setCouponSelected(true);
                    startCouponPopup((StayPaymentInformation) mPaymentInformation);
                    break;
                }
            }
        }

        @Override
        public void onVisitType(boolean isWalking)
        {
            ((StayPaymentInformation) mPaymentInformation).isVisitWalking = isWalking;

            AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                Action.WAYTOVISIT_SELECTED, isWalking == true ? Label.WALK : Label.CAR, null);
        }

        @Override
        public void finish()
        {
            HotelPaymentActivity.this.finish();
        }
    };


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mUserInformationCallback = new retrofit2.Callback<JSONObject>()
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

                    if (msgCode != 0)
                    {
                        if (responseJSONObject.has("msg") == true)
                        {
                            String msg = responseJSONObject.getString("msg");

                            DailyToast.showToast(HotelPaymentActivity.this, msg, Toast.LENGTH_SHORT);
                            setResult(CODE_RESULT_ACTIVITY_REFRESH);
                            finish();
                            return;
                        } else
                        {
                            HotelPaymentActivity.this.onError();
                            return;
                        }
                    }

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                    String name = dataJSONObject.getString("user_name");
                    String phone = dataJSONObject.getString("user_phone");
                    String email = dataJSONObject.getString("user_email");
                    String userIndex = dataJSONObject.getString("user_idx");
                    int bonus = dataJSONObject.getInt("user_bonus");

                    if (bonus < 0)
                    {
                        bonus = 0;
                    }

                    StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;
                    stayPaymentInformation.bonus = bonus;

                    setPaymentInformation(stayPaymentInformation);

                    Customer buyer = new Customer();
                    buyer.setEmail(email);
                    buyer.setName(name);
                    buyer.setPhone(phone);
                    buyer.setUserIdx(userIndex);

                    stayPaymentInformation.setCustomer(buyer);

                    Guest guest = stayPaymentInformation.getGuest();

                    // 해외 호텔인 경우.
                    boolean isOverseas = stayPaymentInformation.getSaleRoomInformation().isOverseas;

                    if (isOverseas == true)
                    {
                        if (guest == null)
                        {
                            guest = mHotelPaymentLayout.getGuest();
                        }

                        if (guest == null)
                        {
                            guest = new Guest();
                        }

                        String overseasName = DailyPreference.getInstance(HotelPaymentActivity.this).getOverseasName();
                        String overseasPhone = DailyPreference.getInstance(HotelPaymentActivity.this).getOverseasPhone();
                        String overseasEmail = DailyPreference.getInstance(HotelPaymentActivity.this).getOverseasEmail();

                        guest.name = overseasName;

                        if (Util.isTextEmpty(guest.phone) == true)
                        {
                            if (Util.isTextEmpty(overseasPhone) == false)
                            {
                                guest.phone = overseasPhone;
                            } else
                            {
                                guest.phone = phone;
                            }
                        }

                        if (Util.isTextEmpty(guest.email) == true)
                        {
                            if (Util.isTextEmpty(overseasEmail) == false)
                            {
                                guest.email = overseasEmail;
                            } else
                            {
                                guest.email = email;
                            }
                        }

                        if (Util.isNameCharacter(overseasName) == false)
                        {
                            guest.name = "";
                            mHotelPaymentLayout.requestGuestInformationFocus(UserInformationType.NAME);
                        }

                        stayPaymentInformation.setGuest(guest);
                    }

                    mHotelPaymentLayout.setUserInformation(buyer, isOverseas);
                    mHotelPaymentLayout.setGuestInformation(guest, isOverseas);

                    // 2. 화면 정보 얻기
                    DailyMobileAPI.getInstance(HotelPaymentActivity.this).requestStayPaymentInformation(mNetworkTag//
                        , stayPaymentInformation.getSaleRoomInformation().roomIndex//
                        , mCheckInSaleTime.getDayOfDaysDateFormat("yyyyMMdd")//
                        , stayPaymentInformation.getSaleRoomInformation().nights, mHotelPaymentInformationCallback);
                } catch (Exception e)
                {
                    onError(e);
                }
            } else
            {
                HotelPaymentActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            HotelPaymentActivity.this.onError(t);
        }
    };

    private retrofit2.Callback mHotelPaymentInformationCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;

                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    // 0	성공
                    // 4	데이터가 없을시
                    // 5	판매 마감시
                    // 6	현재 시간부터 날짜 바뀌기 전시간(새벽 3시
                    // 7    3시부터 9시까지
                    switch (msgCode)
                    {
                        case 6:
                        case 7:
                            if (mWarningDialogMessage == null && responseJSONObject.has("msg") == true)
                            {
                                mWarningDialogMessage = responseJSONObject.getString("msg");
                            }
                        case 0:
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            long checkInDate = dataJSONObject.getLong("check_in_date");
                            long checkOutDate = dataJSONObject.getLong("check_out_date");
                            int discount = dataJSONObject.getInt("discount_total");
                            boolean isOnSale = dataJSONObject.getBoolean("on_sale");
                            int availableRooms = dataJSONObject.getInt("available_rooms");
                            boolean isNRD = false;

                            boolean noParking = false;
                            boolean parking = false;
                            boolean provideTransportation = false;

                            if (dataJSONObject.has("no_parking") == true && dataJSONObject.has("parking") == true//
                                && dataJSONObject.has("provide_transportation") == true)
                            {
                                noParking = dataJSONObject.getBoolean("no_parking");
                                parking = dataJSONObject.getBoolean("parking");
                                provideTransportation = dataJSONObject.getBoolean("provide_transportation");
                            }

                            if (dataJSONObject.has("refund_type") == true && RoomInformation.NRD.equalsIgnoreCase(dataJSONObject.getString("refund_type")) == true)
                            {
                                isNRD = true;
                            }

                            RoomInformation roomInformation = stayPaymentInformation.getSaleRoomInformation();

                            roomInformation.isNRD = isNRD;

                            // 가격이 변동 되었다.
                            if (roomInformation.totalDiscount != discount)
                            {
                                mIsChangedPrice = true;
                            }

                            roomInformation.totalDiscount = discount;

                            if (DEBUG == false && (checkInDate == 0 || checkOutDate == 0))
                            {
                                Crashlytics.log(responseJSONObject.toString());
                                Crashlytics.logException(new RuntimeException(call.request().url().toString()));

                                Util.restartExitApp(HotelPaymentActivity.this);
                                return;
                            }

                            setReservationInformation(checkInDate, checkOutDate, roomInformation.nights);

                            if (provideTransportation == true)
                            {
                                if (noParking == true)
                                {
                                    stayPaymentInformation.visitType = StayPaymentInformation.VISIT_TYPE_NO_PARKING;
                                } else if (parking == true)
                                {
                                    stayPaymentInformation.visitType = StayPaymentInformation.VISIT_TYPE_PARKING;
                                } else
                                {
                                    stayPaymentInformation.visitType = StayPaymentInformation.VISIT_TYPE_NONE;
                                }
                            } else
                            {
                                stayPaymentInformation.visitType = StayPaymentInformation.VISIT_TYPE_NONE;
                            }

                            mHotelPaymentLayout.setVisitTypeInformation(stayPaymentInformation);

                            // 판매 중지 상품으로 호텔 리스트로 복귀 시킨다.
                            if (isOnSale == false || availableRooms == 0)
                            {
                                mWarningDialogMessage = null;

                                if (isFinishing() == true)
                                {
                                    return;
                                }

                                showStopOnSaleDialog();
                            } else
                            {
                                // 취소 및 환불 규정
                                DailyMobileAPI.getInstance(HotelPaymentActivity.this).requestPolicyRefund(mNetworkTag//
                                    , stayPaymentInformation.placeIndex, roomInformation.roomIndex//
                                    , stayPaymentInformation.checkInDateFormat, stayPaymentInformation.checkOutDateFormat//
                                    , mPolicyRefundCallback);
                            }
                            break;
                        }

                        case 5:
                        {
                            if (responseJSONObject.has("msg") == true)
                            {
                                String msg = responseJSONObject.getString("msg");

                                showSimpleDialog(getString(R.string.dialog_notice2), msg, getString(R.string.dialog_btn_text_confirm), null, new OnDismissListener()
                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog)
                                    {
                                        setResult(CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER);
                                        finish();
                                    }
                                });
                            } else
                            {
                                throw new NullPointerException("response == null");
                            }
                            break;
                        }

                        case 4:
                        default:
                            if (responseJSONObject.has("msg") == true)
                            {
                                String msg = responseJSONObject.getString("msg");

                                DailyToast.showToast(HotelPaymentActivity.this, msg, Toast.LENGTH_SHORT);
                                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                                finish();
                            } else
                            {
                                throw new NullPointerException("response == null");
                            }
                            break;
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    unLockUI();
                    onError(e);

                    setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    finish();
                }
            } else
            {
                HotelPaymentActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            HotelPaymentActivity.this.onError(t);
        }
    };

    private retrofit2.Callback mPaymentEasyCreditCardCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                hideProgressDialog();

                try
                {
                    JSONObject responseJSONObject = response.body();

                    // 해당 화면은 메시지를 넣지 않는다.
                    int msgCode = responseJSONObject.getInt("msgCode");
                    String message = responseJSONObject.getString("msg");

                    Intent intent = new Intent();
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PAYMENTINFORMATION, mPaymentInformation);
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_MESSAGE, String.format("%d^%s", msgCode, message));

                    onActivityPaymentResult(CODE_REQUEST_ACTIVITY_PAYMENT, CODE_RESULT_ACTIVITY_PAYMENT_PRECHECK, intent);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    onError(e);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                HotelPaymentActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            HotelPaymentActivity.this.onError(t);
        }
    };

    private retrofit2.Callback mFinalCheckPayCallback = new retrofit2.Callback<JSONObject>()
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

                    switch (msgCode)
                    {
                        case 6:
                        case 7:
                        case 0:
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            //                        long checkInDate = jsonData.getLong("check_in_date");
                            //                        long checkOutDate = jsonData.getLong("check_out_date");
                            int discount = dataJSONObject.getInt("discount_total");
                            boolean isOnSale = dataJSONObject.getBoolean("on_sale");
                            int availableRooms = dataJSONObject.getInt("available_rooms");

                            StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;

                            RoomInformation roomInformation = stayPaymentInformation.getSaleRoomInformation();

                            // 가격이 변동 되었다.
                            if (roomInformation.totalDiscount != discount)
                            {
                                mIsChangedPrice = true;
                            }

                            roomInformation.totalDiscount = discount;

                            // 판매 중지 상품으로 호텔 리스트로 복귀 시킨다.
                            if (isOnSale == false || availableRooms == 0)
                            {
                                showStopOnSaleDialog();
                            } else if (isChangedPrice() == true)
                            {
                                mIsChangedPrice = false;

                                // 현재 있는 팝업을 없애도록 한다.
                                if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
                                {
                                    mFinalCheckDialog.cancel();
                                    mFinalCheckDialog = null;
                                }

                                showChangedPriceDialog();
                            } else
                            {
                                processPayment(stayPaymentInformation, mCheckInSaleTime);
                            }
                            break;
                        }

                        case 5:
                        {
                            unLockUI();

                            if (responseJSONObject.has("msg") == true)
                            {
                                String msg = responseJSONObject.getString("msg");

                                showSimpleDialog(getString(R.string.dialog_notice2), msg, getString(R.string.dialog_btn_text_confirm), null, new OnDismissListener()
                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog)
                                    {
                                        setResult(CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER);
                                        finish();
                                    }
                                });
                            } else
                            {
                                throw new NullPointerException("response == null");
                            }
                            break;
                        }

                        case 4:
                        default:
                        {
                            unLockUI();

                            if (responseJSONObject.has("msg") == true)
                            {
                                String msg = responseJSONObject.getString("msg");

                                DailyToast.showToast(HotelPaymentActivity.this, msg, Toast.LENGTH_SHORT);
                                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                                finish();
                            } else
                            {
                                throw new NullPointerException("response == null");
                            }
                            break;
                        }
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    onError(e);
                    setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    finish();
                }
            } else
            {
                HotelPaymentActivity.this.onErrorResponse(call, response);
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                finish();
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);

            HotelPaymentActivity.this.onError(t);
        }
    };

    private retrofit2.Callback mUserInformationFinalCheckCallback = new retrofit2.Callback<JSONObject>()
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

                    if (msgCode != 0)
                    {
                        if (responseJSONObject.has("msg") == true)
                        {
                            String msg = responseJSONObject.getString("msg");

                            DailyToast.showToast(HotelPaymentActivity.this, msg, Toast.LENGTH_SHORT);
                            setResult(CODE_RESULT_ACTIVITY_REFRESH);
                            finish();
                            return;
                        } else
                        {
                            HotelPaymentActivity.this.onError();
                            return;
                        }
                    }

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                    int bonus = dataJSONObject.getInt("user_bonus");

                    if (bonus < 0)
                    {
                        bonus = 0;
                    }

                    StayPaymentInformation stayPaymentInformation = (StayPaymentInformation) mPaymentInformation;

                    if (stayPaymentInformation.discountType == PlacePaymentInformation.DiscountType.BONUS //
                        && bonus != stayPaymentInformation.bonus)
                    {
                        stayPaymentInformation.bonus = bonus;
                        showChangedBonusDialog();
                        return;
                    }

                    // 2. 마지막 가격 및 기타 이상이 없는지 검사
                    DailyMobileAPI.getInstance(HotelPaymentActivity.this).requestStayPaymentInformation(mNetworkTag//
                        , stayPaymentInformation.getSaleRoomInformation().roomIndex//
                        , mCheckInSaleTime.getDayOfDaysDateFormat("yyyyMMdd")//
                        , stayPaymentInformation.getSaleRoomInformation().nights, mFinalCheckPayCallback);
                } catch (Exception e)
                {
                    onError(e);
                }
            } else
            {
                HotelPaymentActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            HotelPaymentActivity.this.onError(t);
        }
    };

    private retrofit2.Callback mMessageDateTimeCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                        long currentDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("currentDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long openDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("openDateTime"), DailyCalendar.ISO_8601_FORMAT);
                        long closeDateTime = DailyCalendar.getTimeGMT9(dataJSONObject.getString("closeDateTime"), DailyCalendar.ISO_8601_FORMAT);

                        int openHour = Integer.parseInt(DailyCalendar.format(openDateTime, "HH", TimeZone.getTimeZone("GMT")));
                        int closeHour = Integer.parseInt(DailyCalendar.format(closeDateTime, "HH", TimeZone.getTimeZone("GMT")));
                        int currentHour = Integer.parseInt(DailyCalendar.format(currentDateTime, "HH", TimeZone.getTimeZone("GMT")));

                        // 당일인지 아닌지
                        if (mCheckInSaleTime.getOffsetDailyDay() == 0)
                        {
                            if (currentHour >= openHour && currentHour < 18)
                            {
                                mPensionPopupMessageType = 1;
                            } else if (currentHour >= 18 || currentHour < closeHour)
                            {
                                mPensionPopupMessageType = 2;
                            } else
                            {
                                mPensionPopupMessageType = 3;
                            }
                        } else
                        {
                            if (currentHour >= openHour && currentHour < 22)
                            {
                                mPensionPopupMessageType = 10;
                            } else if (currentHour >= 22)
                            {
                                mPensionPopupMessageType = 11;
                            } else
                            {
                                mPensionPopupMessageType = 12;
                            }
                        }

                        processAgreeTermDialog();
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        onErrorPopupMessage(msgCode, message);

                        setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    }
                } catch (Exception e)
                {
                    onError(e);
                    setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    finish();
                } finally
                {
                    unLockUI();
                }
            } else
            {
                HotelPaymentActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            HotelPaymentActivity.this.onError(t);
        }
    };

    private retrofit2.Callback mPolicyRefundCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    switch (msgCode)
                    {
                        case 100:
                        {
                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            String comment = dataJSONObject.getString("comment");
                            String refundPolicy = dataJSONObject.getString("refundPolicy");

                            if (StayBookingDetail.STATUS_NONE.equalsIgnoreCase(refundPolicy) == true)
                            {
                                mHotelPaymentLayout.setRefundPolicyVisible(false);
                            } else
                            {
                                mHotelPaymentLayout.setRefundPolicyText(comment);
                            }

                            // Analytics
                            if (Util.isTextEmpty(refundPolicy) == false)
                            {
                                switch (refundPolicy)
                                {
                                    case StayBookingDetail.STATUS_NO_CHARGE_REFUND:
                                        mScreenAnalytics = Screen.DAILYHOTEL_BOOKINGINITIALISE_CANCELABLE;
                                        break;

                                    case StayBookingDetail.STATUS_SURCHARGE_REFUND:
                                        mScreenAnalytics = Screen.DAILYHOTEL_BOOKINGINITIALISE_CANCELLATIONFEE;
                                        break;

                                    default:
                                        mScreenAnalytics = Screen.DAILYHOTEL_BOOKINGINITIALISE_NOREFUNDS;
                                        break;
                                }
                            } else
                            {
                                mScreenAnalytics = Screen.DAILYHOTEL_BOOKINGINITIALISE_NOREFUNDS;
                            }
                            break;
                        }

                        default:
                            // 에러가 발생하더라도 결제는 가능하도록 수정
                            mHotelPaymentLayout.setRefundPolicyVisible(false);

                            mScreenAnalytics = Screen.DAILYHOTEL_BOOKINGINITIALISE_NOREFUNDS;
                            break;
                    }

                    // 3. 간편결제 credit card 요청
                    DailyMobileAPI.getInstance(HotelPaymentActivity.this).requestUserBillingCardList(mNetworkTag, mUserCreditCardListCallback);

                } catch (Exception e)
                {
                    onError(e);
                    setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    finish();
                } finally
                {
                    unLockUI();
                }
            } else
            {
                HotelPaymentActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            HotelPaymentActivity.this.onError(t);
        }
    };
}
