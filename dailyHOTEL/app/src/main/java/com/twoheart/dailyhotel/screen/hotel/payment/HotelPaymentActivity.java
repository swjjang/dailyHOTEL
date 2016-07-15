package com.twoheart.dailyhotel.screen.hotel.payment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.HotelPaymentInformation;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.activity.PlacePaymentActivity;
import com.twoheart.dailyhotel.screen.common.FinalCheckLayout;
import com.twoheart.dailyhotel.screen.information.coupon.SelectCouponDialogActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyScrollView;
import com.twoheart.dailyhotel.widget.DailySignatureView;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@SuppressLint({"NewApi", "ResourceAsColor"})
public class HotelPaymentActivity extends PlacePaymentActivity implements OnClickListener
{
    private static final int DEFAULT_AVAILABLE_RESERVES = 20000;

    private TextView mCheckinDayTextView, mCheckoutDayTextView;
    private TextView mPriceTextView, mDiscountPriceTextView, mFinalPaymentTextView;
    private EditText mReservationName, mReservationPhone, mReservationEmail;
    private EditText mMemoEditText;
    private Drawable[] mEditTextBackground;

    // 할인 정보
    private ImageView mBonusRadioButton;
    private View mDiscountBonusLayout;
    private View mUsedBonusLayout;
    private TextView mUsedBonusTextView;
    private TextView mBonusTextView;

    private ImageView mCouponRadioButton;
    private View mDiscountCouponLayout;
    private View mUsedCouponLayout;
    private TextView mUsedCouponTextView;

    // 결제 수단 선택
    private View mSimpleCardLayout;
    private ImageView mSimpleCardImageView;
    private TextView mSimpleCardTextView;
    private View mCardLayout;
    private View mPhoneLayout;
    private View mTransferLayout;

    private View mDisableSimpleCardView;
    private View mDisableCardView;

    private View mCardManagerLayout;
    private TextView mCardManagerTextView;

    //
    private boolean mIsChangedPrice; // 가격이 변경된 경우.
    private String mPlaceImageUrl;
    private boolean mIsEditMode;
    private boolean mIsUnderPrice;

    // 1 : 오후 6시 전 당일 예약, 2 : 오후 6시 후 당일 예약, 3: 새벽 3시 이후 - 오전 9시까지의 당일 예약
    // 10 : 오후 10시 전 사전 예약, 11 : 오후 10시 후 사전 예약 00시 전 12 : 00시 부터 오전 9시
    private int mPensionPopupMessageType;
    private String mWarningDialogMessage;
    private Province mProvince;
    private String mArea; // Analytics용 소지역

    public static Intent newInstance(Context context, SaleRoomInformation saleRoomInformation//
        , SaleTime checkInSaleTime, String imageUrl, int hotelIndex, boolean isDBenefit, Province province, String area)
    {
        Intent intent = new Intent(context, HotelPaymentActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALEROOMINFORMATION, saleRoomInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkInSaleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, imageUrl);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotelIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DBENEFIT, isDBenefit);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, area);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking);

        Intent intent = getIntent();

        if (intent == null)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            finish();
            return;
        }

        mPaymentInformation = new HotelPaymentInformation();
        HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

        hotelPaymentInformation.setSaleRoomInformation((SaleRoomInformation) intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALEROOMINFORMATION));
        mCheckInSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        mPlaceImageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_URL);
        hotelPaymentInformation.placeIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
        hotelPaymentInformation.isDBenefit = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_DBENEFIT, false);
        mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
        mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);

        if (hotelPaymentInformation.getSaleRoomInformation() == null)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            finish();
            return;
        }

        mIsChangedPrice = false;
        mWarningDialogMessage = null;

        initToolbar(hotelPaymentInformation.getSaleRoomInformation().hotelName);
        initLayout();
    }

    private void initToolbar(String title)
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(title, new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        dailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_call, -1);
        dailyToolbarLayout.setToolbarMenuClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                showCallDialog();
            }
        });
    }

    private void initLayout()
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollView, getResources().getColor(R.color.default_over_scroll_edge));

        initHotelInformation();
        initGuestInformation();
        initBookingMemo();
        initDiscountInformation();
        initPaymentInformation();
        initPaymentTypeInformation();

        // 결제하기
        View doPaymentView = findViewById(R.id.doPaymentView);
        doPaymentView.setOnClickListener(this);
    }

    private void initHotelInformation()
    {
        mCheckinDayTextView = (TextView) findViewById(R.id.checkinDayTextView);
        mCheckoutDayTextView = (TextView) findViewById(R.id.checkoutDayTextView);

        // 객실 타입
        TextView roomTypeTextView = (TextView) findViewById(R.id.roomTypeTextView);
        roomTypeTextView.setText(((HotelPaymentInformation) mPaymentInformation).getSaleRoomInformation().roomName);
    }

    private void initGuestInformation()
    {
        mReservationName = (EditText) findViewById(R.id.et_hotel_payment_reserver_name);
        mReservationPhone = (EditText) findViewById(R.id.et_hotel_payment_reserver_number);
        mReservationEmail = (EditText) findViewById(R.id.et_hotel_payment_reserver_email);

        mEditTextBackground = new Drawable[3];
        mEditTextBackground[0] = mReservationName.getBackground();
        mEditTextBackground[1] = mReservationPhone.getBackground();
        mEditTextBackground[2] = mReservationEmail.getBackground();

        mReservationName.setBackgroundResource(0);
        mReservationPhone.setBackgroundResource(0);
        mReservationEmail.setBackgroundResource(0);

        mReservationName.setEnabled(false);
        mReservationPhone.setEnabled(false);
        mReservationEmail.setEnabled(false);

        TextView guideNameMemo = (TextView) findViewById(R.id.guideNameMemoView);

        if (((HotelPaymentInformation) mPaymentInformation).getSaleRoomInformation().isOverseas == true)
        {
            guideNameMemo.setVisibility(View.VISIBLE);

            if (Util.getLCDWidth(this) > 480)
            {
                guideNameMemo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_payment_name, 0, 0, 0);
                guideNameMemo.setCompoundDrawablePadding(Util.dpToPx(this, 4));
            }
        } else
        {
            guideNameMemo.setVisibility(View.GONE);
        }

        // 수정.
        View editLinearLayout = findViewById(R.id.editLinearLayout);
        editLinearLayout.setOnClickListener(mOnEditInfoOnClickListener);
    }

    private void initBookingMemo()
    {
        mMemoEditText = (EditText) findViewById(R.id.memoEditText);
    }

    private void initDiscountInformation()
    {
        mBonusRadioButton = (ImageView) findViewById(R.id.bonusRadioButton);
        mDiscountBonusLayout = findViewById(R.id.bonusLayout);
        mUsedBonusLayout = findViewById(R.id.usedBonusLayout);
        mUsedBonusTextView = (TextView) findViewById(R.id.usedBonusTextView);
        mBonusTextView = (TextView) findViewById(R.id.bonusTextView);

        mDiscountBonusLayout.setOnClickListener(this);

        mCouponRadioButton = (ImageView) findViewById(R.id.couponRadioButton);
        mDiscountCouponLayout = findViewById(R.id.couponLayout);
        mUsedCouponLayout = findViewById(R.id.usedCouponLayout);
        mUsedCouponTextView = (TextView) findViewById(R.id.usedCouponTextView);

        mDiscountCouponLayout.setOnClickListener(this);
    }

    private void initPaymentInformation()
    {
        mPriceTextView = (TextView) findViewById(R.id.originalPriceTextView);
        mDiscountPriceTextView = (TextView) findViewById(R.id.discountPriceTextView);
        mFinalPaymentTextView = (TextView) findViewById(R.id.totalPaymentPriceTextView);

        mDiscountPriceTextView.setText(Util.getPriceFormat(this, 0, false));
    }

    private void initPaymentTypeInformation()
    {
        mSimpleCardLayout = findViewById(R.id.simpleCardLayout);
        mSimpleCardImageView = (ImageView) mSimpleCardLayout.findViewById(R.id.simpleCardImageView);
        mSimpleCardTextView = (TextView) mSimpleCardLayout.findViewById(R.id.simpleCardTextView);
        mDisableSimpleCardView = findViewById(R.id.disableSimpleCardView);
        mCardManagerLayout = findViewById(R.id.cardManagerLayout);
        mCardManagerTextView = (TextView) mCardManagerLayout.findViewById(R.id.cardManagerTextView);

        mCardLayout = findViewById(R.id.cardLayout);
        mDisableCardView = mCardLayout.findViewById(R.id.disableCardView);

        mPhoneLayout = findViewById(R.id.phoneLayout);
        mTransferLayout = findViewById(R.id.transferLayout);

        mCardManagerLayout.setOnClickListener(this);
        mSimpleCardLayout.setOnClickListener(this);
        mCardLayout.setOnClickListener(this);
        mPhoneLayout.setOnClickListener(this);
        mTransferLayout.setOnClickListener(this);

        // 기본이 간편카드 결제이다.
        changedPaymentType(PlacePaymentInformation.PaymentType.EASY_CARD, mSelectedCreditCard);
    }

    private void updatePaymentPrice(HotelPaymentInformation hotelPaymentInformation)
    {
        if (hotelPaymentInformation == null)
        {
            return;
        }

        int originalPrice = hotelPaymentInformation.getSaleRoomInformation().totalDiscount;
        int payPrice = originalPrice;

        mPriceTextView.setText(Util.getPriceFormat(this, originalPrice, false));

        if (hotelPaymentInformation.discountType == PlacePaymentInformation.DiscountType.BONUS)
        {
            hotelPaymentInformation.setCoupon(null);

            int discountPrice = hotelPaymentInformation.bonus;

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

                String priceFormat = Util.getPriceFormat(this, discountPrice, false);

                mUsedBonusTextView.setText(priceFormat);
                mDiscountPriceTextView.setText("- " + priceFormat);
            } else
            {
                mUsedBonusTextView.setText(R.string.label_booking_used_bonus);

                setBonusEnabled(false);
            }
        } else if (hotelPaymentInformation.discountType == PlacePaymentInformation.DiscountType.COUPON)
        {
            Coupon coupon = hotelPaymentInformation.getCoupon();

            if (coupon == null)
            {
                mUsedCouponTextView.setText(R.string.label_booking_select_coupon);
            } else
            {
                int discountPrice = coupon.amount;

                if (discountPrice < originalPrice)
                {
                    payPrice = originalPrice - coupon.amount;
                } else
                {
                    payPrice = 0;
                    discountPrice = originalPrice;
                }

                String priceFormat = Util.getPriceFormat(this, discountPrice, false);

                mUsedCouponTextView.setText(priceFormat);
                mDiscountPriceTextView.setText("- " + priceFormat);
            }
        } else
        {
            if (hotelPaymentInformation.bonus <= 0)
            {
                setBonusEnabled(false);
            }

            mDiscountPriceTextView.setText(Util.getPriceFormat(this, 0, false));
        }

        setBonusTextView(hotelPaymentInformation.bonus);
        mFinalPaymentTextView.setText(Util.getPriceFormat(this, payPrice, false));

        // 1000원 미만 결제시에 간편/일반 결제 불가 - 쿠폰 또는 적립금 전체 사용이 아닌경우 조건 추가
        if (payPrice > 0 && payPrice < 1000)
        {
            mIsUnderPrice = true;

            mDisableSimpleCardView.setVisibility(View.VISIBLE);
            mDisableCardView.setVisibility(View.VISIBLE);

            mDisableSimpleCardView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });

            mDisableCardView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });

            // 기본이 간편카드 결제이다.
            changedPaymentType(PlacePaymentInformation.PaymentType.PHONE_PAY, mSelectedCreditCard);
        } else
        {
            mDisableSimpleCardView.setOnClickListener(null);
            mDisableCardView.setOnClickListener(null);

            mDisableSimpleCardView.setVisibility(View.GONE);
            mDisableCardView.setVisibility(View.GONE);

            // 1000원 이하였다가 되돌아 오는 경우 한번 간편결제로 바꾸어준다.
            if (mIsUnderPrice == true)
            {
                mIsUnderPrice = false;

                // 기본이 간편카드 결제이다.
                changedPaymentType(PlacePaymentInformation.PaymentType.EASY_CARD, mSelectedCreditCard);
            } else
            {
                changedPaymentType(mPaymentInformation.paymentType, mSelectedCreditCard);
            }
        }
    }

    /**
     * 내가 보유한 적립금 작게 나오는 부분(보유: x원)
     *
     * @param bonus
     */
    private void setBonusTextView(int bonus)
    {
        String priceFormat = Util.getPriceFormat(this, bonus, false);
        String text = getString(R.string.label_booking_own_bonus, priceFormat);

        if (bonus > 0)
        {
            int startIndex = text.indexOf(priceFormat);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);

            spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.default_text_c323232)), //
                startIndex, text.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mBonusTextView.setText(spannableStringBuilder);
        } else
        {
            mBonusTextView.setText(text);
        }
    }

    private void setBonusSelected(boolean isSelected)
    {
        //selected가 true enabled가 false일수는 없다.
        if (mDiscountBonusLayout.isEnabled() == false)
        {
            return;
        }

        if (isSelected == true)
        {
            mBonusRadioButton.setSelected(true);
            mDiscountBonusLayout.setSelected(true);
            mDiscountBonusLayout.setOnClickListener(null);

            mUsedBonusLayout.setOnClickListener(this);
            mUsedBonusLayout.setSelected(true);

            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.BONUS;

            AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.USING_CREDIT_CLICKED, Integer.toString(mPaymentInformation.bonus), null);
        } else
        {
            mBonusRadioButton.setSelected(false);
            mDiscountBonusLayout.setSelected(false);
            mDiscountBonusLayout.setOnClickListener(this);

            mUsedBonusTextView.setText(R.string.label_booking_used_bonus);

            mUsedBonusLayout.setOnClickListener(this);
            mUsedBonusLayout.setSelected(false);

            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.NONE;

            AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
                , Action.USING_CREDIT_CANCEL_CLICKED, Integer.toString(mPaymentInformation.bonus), null);
        }

        updatePaymentPrice((HotelPaymentInformation) mPaymentInformation);
    }

    /**
     * selected가 true enabled가 false일수는 없다.
     *
     * @param isEnabled
     */
    private void setBonusEnabled(boolean isEnabled)
    {
        mBonusRadioButton.setEnabled(isEnabled);
        mDiscountBonusLayout.setEnabled(isEnabled);
        mUsedBonusLayout.setEnabled(isEnabled);

        if (isEnabled == true)
        {

        } else
        {
            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.NONE;
        }
    }

    private void setCouponSelected(boolean isSelected)
    {
        if (isSelected == true)
        {
            mCouponRadioButton.setSelected(true);
            mDiscountCouponLayout.setSelected(true);
            mDiscountCouponLayout.setOnClickListener(null);

            mUsedCouponLayout.setOnClickListener(this);
            mUsedCouponLayout.setSelected(true);

            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.COUPON;
        } else
        {
            mCouponRadioButton.setSelected(false);
            mDiscountCouponLayout.setSelected(false);
            mDiscountCouponLayout.setOnClickListener(this);

            mUsedCouponTextView.setText(R.string.label_booking_select_coupon);

            mUsedCouponLayout.setOnClickListener(this);
            mUsedCouponLayout.setSelected(false);

            mPaymentInformation.discountType = PlacePaymentInformation.DiscountType.NONE;
        }

        updatePaymentPrice((HotelPaymentInformation) mPaymentInformation);
    }

    private void startCouponPopup(HotelPaymentInformation hotelPaymentInformation)
    {
        SaleRoomInformation saleRoomInformation = hotelPaymentInformation.getSaleRoomInformation();

        int hotelIdx = hotelPaymentInformation.placeIndex;
        int roomIdx = saleRoomInformation.roomIndex;
        String checkInDate = hotelPaymentInformation.checkInDateFormat;
        String checkOutDate = hotelPaymentInformation.checkOutDateFormat;

        String categoryCode = saleRoomInformation.categoryCode;
        String hotelName = saleRoomInformation.hotelName;
        String roomPrice = Integer.toString(saleRoomInformation.averageDiscount);

        Intent intent = SelectCouponDialogActivity.newInstance(HotelPaymentActivity.this, hotelIdx, //
            roomIdx, checkInDate, checkOutDate, categoryCode, hotelName, roomPrice);
        startActivityForResult(intent, REQUEST_CODE_COUPONPOPUP_ACTIVITY);

        AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
            Action.HOTEL_USING_COUPON_CLICKED, Label.HOTEL_USING_COUPON_CLICKED, null);
    }

    private void startCancelBonusPopup(final HotelPaymentInformation hotelPaymentInformation, View.OnClickListener positiveListener)
    {
        showSimpleDialog(null, getString(R.string.message_booking_cancel_bonus), getString(R.string.dialog_btn_text_yes), //
            getString(R.string.dialog_btn_text_no), positiveListener, null);
    }

    private void startCancelCouponPopup(final HotelPaymentInformation hotelPaymentInformation, View.OnClickListener positiveListener)
    {
        showSimpleDialog(null, getString(R.string.message_booking_cancel_coupon), getString(R.string.dialog_btn_text_yes), //
            getString(R.string.dialog_btn_text_no), positiveListener, null);
    }

    @Override
    protected void requestUserInformationForPayment()
    {
        DailyNetworkAPI.getInstance(this).requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener, this);
    }

    @Override
    protected void requestEasyPayment(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime)
    {
        if (paymentInformation == null || checkInSaleTime == null)
        {
            return;
        }

        lockUI();

        Guest guest = paymentInformation.getGuest();

        if (mIsEditMode == true)
        {
            guest.name = mReservationName.getText().toString().trim();
            guest.phone = mReservationPhone.getText().toString().trim();
            guest.email = mReservationEmail.getText().toString().trim();
            guest.message = mMemoEditText.getText().toString().trim();
        }

        SaleRoomInformation saleRoomInformation = ((HotelPaymentInformation) paymentInformation).getSaleRoomInformation();

        Map<String, String> params = new HashMap<>();
        params.put("room_idx", String.valueOf(saleRoomInformation.roomIndex));
        params.put("checkin_date", checkInSaleTime.getDayOfDaysDateFormat("yyyyMMdd"));
        params.put("nights", String.valueOf(saleRoomInformation.nights));
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
        params.put("guest_msg", guest.message);

        //            if (DEBUG == true)
        //            {
        //                showSimpleDialog(null, params.toString(), getString(R.string.dialog_btn_text_confirm), null);
        //            }

        DailyNetworkAPI.getInstance(this).requestHotelPayment(mNetworkTag, params, mPaymentEasyCreditCardJsonResponseListener, this);
    }

    @Override
    protected void requestPlacePaymentInfomation(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime)
    {
        SaleRoomInformation saleRoomInformation = ((HotelPaymentInformation) paymentInformation).getSaleRoomInformation();

        // 호텔 디테일 정보 재 요청
        DailyNetworkAPI.getInstance(this).requestHotelPaymentInformation(mNetworkTag//
            , saleRoomInformation.roomIndex//
            , checkInSaleTime.getDayOfDaysDateFormat("yyyyMMdd")//
            , saleRoomInformation.nights, mHotelPaymentInformationJsonResponseListener, this);
    }

    @Override
    protected void updateSimpleCardInformation(PlacePaymentInformation paymentInformation, CreditCard selectedCreditCard)
    {
        if (selectedCreditCard == null)
        {
            mCardManagerTextView.setText(R.string.label_register_card);
            mSimpleCardImageView.setImageResource(R.drawable.selector_simplecard_plus_button);
            mSimpleCardTextView.setText(R.string.label_booking_easypayment);
        } else
        {
            mCardManagerTextView.setText(R.string.label_manager);
            mSimpleCardImageView.setImageResource(R.drawable.selector_simplecard_button);
            mSimpleCardTextView.setText(String.format("%s %s", selectedCreditCard.name.replace("카드", ""), selectedCreditCard.number));
        }
    }

    @Override
    protected void updateGuestInformation(String phoneNumber)
    {
        mPaymentInformation.getGuest().phone = phoneNumber;
        mReservationPhone.setText(phoneNumber);
    }

    @Override
    protected void changedPaymentType(PlacePaymentInformation.PaymentType paymentType, CreditCard creditCard)
    {
        mSelectedCreditCard = creditCard;
        mPaymentInformation.paymentType = paymentType;

        switch (paymentType)
        {
            case EASY_CARD:
            {
                ((View) mSimpleCardLayout.getParent()).setSelected(true);
                mSimpleCardLayout.setSelected(true);
                mCardLayout.setSelected(false);
                mPhoneLayout.setSelected(false);
                mTransferLayout.setSelected(false);
                break;
            }

            case CARD:
            {
                ((View) mSimpleCardLayout.getParent()).setSelected(false);
                mSimpleCardLayout.setSelected(false);
                mCardLayout.setSelected(true);
                mPhoneLayout.setSelected(false);
                mTransferLayout.setSelected(false);
                break;
            }

            case PHONE_PAY:
            {
                ((View) mSimpleCardLayout.getParent()).setSelected(false);
                mSimpleCardLayout.setSelected(false);
                mCardLayout.setSelected(false);
                mPhoneLayout.setSelected(true);
                mTransferLayout.setSelected(false);
                break;
            }

            case VBANK:
            {
                ((View) mSimpleCardLayout.getParent()).setSelected(false);
                mSimpleCardLayout.setSelected(false);
                mCardLayout.setSelected(false);
                mPhoneLayout.setSelected(false);
                mTransferLayout.setSelected(true);
                break;
            }
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.PAYMENT_TYPE_ITEM_CLICKED, paymentType.getName(), null);
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
        HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

        SaleRoomInformation saleRoomInformation = hotelPaymentInformation.getSaleRoomInformation();

        Intent intent = HotelPaymentThankyouActivity.newInstance(this, imageUrl, saleRoomInformation.hotelName//
            , saleRoomInformation.roomName, hotelPaymentInformation.checkInOutDate);

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

        int[] messageResIds = {R.string.dialog_msg_hotel_payment_message01//
            , R.string.dialog_msg_hotel_payment_message14//
            , R.string.dialog_msg_hotel_payment_message02//
            , R.string.dialog_msg_hotel_payment_message03//
            , R.string.dialog_msg_hotel_payment_message08//
            , R.string.dialog_msg_hotel_payment_message07};

        messageResIds = paymentDialogMessage(mPensionPopupMessageType, messageResIds);

        final FinalCheckLayout finalCheckLayout = new FinalCheckLayout(HotelPaymentActivity.this, messageResIds);
        final TextView agreeSinatureTextView = (TextView) finalCheckLayout.findViewById(R.id.agreeSinatureTextView);
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
                        agreeSinatureTextView.setAnimation(null);
                        agreeSinatureTextView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                    }
                });

                agreeSinatureTextView.startAnimation(animation);

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
                            DailyNetworkAPI.getInstance(HotelPaymentActivity.this).requestUserInformationForPayment(mNetworkTag, mUserInformationFinalCheckJsonResponseListener, HotelPaymentActivity.this);

                            AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                                , Action.PAYMENT_AGREEMENT_POPPEDUP, Label.AGREE, null);
                        }
                    }
                });
            }
        });

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

        switch (paymentType)
        {
            // 신용카드 일반 결제
            case CARD:
                textResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                    , R.string.dialog_msg_hotel_payment_message14//
                    , R.string.dialog_msg_hotel_payment_message02//
                    , R.string.dialog_msg_hotel_payment_message03//
                    , R.string.dialog_msg_hotel_payment_message06};

                textResIds = paymentDialogMessage(mPensionPopupMessageType, textResIds);
                break;

            // 핸드폰 결제
            case PHONE_PAY:
                textResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                    , R.string.dialog_msg_hotel_payment_message14//
                    , R.string.dialog_msg_hotel_payment_message02//
                    , R.string.dialog_msg_hotel_payment_message03//
                    , R.string.dialog_msg_hotel_payment_message04//
                    , R.string.dialog_msg_hotel_payment_message06};

                textResIds = paymentDialogMessage(mPensionPopupMessageType, textResIds);
                break;

            // 계좌 이체
            case VBANK:
                textResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                    , R.string.dialog_msg_hotel_payment_message14//
                    , R.string.dialog_msg_hotel_payment_message02//
                    , R.string.dialog_msg_hotel_payment_message03//
                    , R.string.dialog_msg_hotel_payment_message05//
                    , R.string.dialog_msg_hotel_payment_message06};

                textResIds = paymentDialogMessage(mPensionPopupMessageType, textResIds);
                break;

            default:
                return null;
        }

        int length = textResIds.length;

        for (int i = 0; i < length; i++)
        {
            View messageRow = LayoutInflater.from(this).inflate(R.layout.row_payment_agreedialog, messageLayout, false);

            TextView messageTextView = (TextView) messageRow.findViewById(R.id.messageTextView);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (i == length - 1)
            {
                layoutParams.setMargins(Util.dpToPx(this, 5), 0, 0, 0);
            } else
            {
                layoutParams.setMargins(Util.dpToPx(this, 5), 0, 0, Util.dpToPx(this, 10));
            }

            messageTextView.setLayoutParams(layoutParams);

            String message = getString(textResIds[i]);

            int startIndex = message.indexOf("<b>");

            if (startIndex >= 0)
            {
                message = message.replaceAll("<b>", "");

                int endIndex = message.indexOf("</b>");

                message = message.replaceAll("</b>", "");

                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message);

                spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dh_theme_color)), //
                    startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                    startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTextView.setText(spannableStringBuilder);
            } else
            {
                messageTextView.setText(message);
            }

            messageLayout.addView(messageRow);
        }

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
                    DailyNetworkAPI.getInstance(HotelPaymentActivity.this).requestUserInformationForPayment(mNetworkTag, mUserInformationFinalCheckJsonResponseListener, HotelPaymentActivity.this);

                    AnalyticsManager.getInstance(HotelPaymentActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                        , Action.PAYMENT_AGREEMENT_POPPEDUP, Label.AGREE, null);
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
                msg = getString(R.string.act_toast_payment_account_duplicate);
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
            , getMapPaymentInformation((HotelPaymentInformation) paymentInformation));
    }

    @Override
    public void onClick(final View v)
    {
        Guest guest = mPaymentInformation.getGuest();

        if (guest == null)
        {
            restartExpiredSession();
            return;
        }

        switch (v.getId())
        {
            case R.id.usedBonusLayout:
            {
                if (mPaymentInformation.discountType == PlacePaymentInformation.DiscountType.COUPON)
                {
                    // 쿠폰 기 선택 상태 일때 쿠폰 선택 취소 팝업 생성 필요함 (">" 아이콘 이므로)
                    startCancelCouponPopup((HotelPaymentInformation) mPaymentInformation, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mPaymentInformation.setCoupon(null);
                            setCouponSelected(false);
                            setBonusSelected(true);
                        }
                    });

                } else if (mPaymentInformation.discountType == PlacePaymentInformation.DiscountType.NONE)
                {
                    // 아무것도 선택 되지 않은 상태 일때 bonusLayout 과 동일한 처리
                    setBonusSelected(true);
                } else
                {
                    // 적립금 삭제
                    startCancelBonusPopup((HotelPaymentInformation) mPaymentInformation, new OnClickListener()
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

            case R.id.usedCouponLayout:
            {
                if (mPaymentInformation.discountType == PlacePaymentInformation.DiscountType.BONUS)
                {
                    // 적립금 기 선택 상태 일때 적립금 선택 취소 팝업 생성 필요함 (">" 아이콘 이므로)
                    startCancelBonusPopup((HotelPaymentInformation) mPaymentInformation, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setBonusSelected(false);
                            setCouponSelected(true);
                            startCouponPopup((HotelPaymentInformation) mPaymentInformation);
                        }
                    });
                } else if (mPaymentInformation.discountType == PlacePaymentInformation.DiscountType.NONE)
                {
                    // 아무것도 선택 되지 않은 상태 일때 couponLayout 과 동일한 처리
                    setCouponSelected(true);
                    startCouponPopup((HotelPaymentInformation) mPaymentInformation);
                } else
                {
                    // 쿠폰 삭제
                    startCancelCouponPopup((HotelPaymentInformation) mPaymentInformation, new OnClickListener()
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

            case R.id.bonusLayout:
            {
                if (mPaymentInformation.discountType == PlacePaymentInformation.DiscountType.COUPON)
                {
                    startCancelCouponPopup((HotelPaymentInformation) mPaymentInformation, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mPaymentInformation.setCoupon(null);
                            setCouponSelected(false);
                            setBonusSelected(true);
                        }
                    });

                } else
                {
                    setBonusSelected(true);
                }
                break;
            }

            case R.id.couponLayout:
            {
                if (mPaymentInformation.discountType == PlacePaymentInformation.DiscountType.BONUS)
                {
                    startCancelBonusPopup((HotelPaymentInformation) mPaymentInformation, new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setBonusSelected(false);
                            setCouponSelected(true);
                            startCouponPopup((HotelPaymentInformation) mPaymentInformation);
                        }
                    });

                } else
                {
                    setCouponSelected(true);
                    startCouponPopup((HotelPaymentInformation) mPaymentInformation);
                }
                break;
            }

            case R.id.simpleCardLayout:
            {
                if (mSelectedCreditCard == null)
                {
                    startCreditCardList();
                } else
                {
                    changedPaymentType(PlacePaymentInformation.PaymentType.EASY_CARD, mSelectedCreditCard);
                }
                break;
            }

            case R.id.cardLayout:
            {
                changedPaymentType(PlacePaymentInformation.PaymentType.CARD, mSelectedCreditCard);
                break;
            }

            case R.id.phoneLayout:
            {
                changedPaymentType(PlacePaymentInformation.PaymentType.PHONE_PAY, mSelectedCreditCard);
                break;
            }

            case R.id.transferLayout:
            {
                changedPaymentType(PlacePaymentInformation.PaymentType.VBANK, mSelectedCreditCard);
                break;
            }

            case R.id.doPaymentView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

                if (mIsEditMode == true)
                {
                    guest.name = mReservationName.getText().toString().trim();
                    guest.phone = mReservationPhone.getText().toString().trim();
                    guest.email = mReservationEmail.getText().toString().trim();
                    guest.message = mMemoEditText.getText().toString().trim();

                    releaseUiComponent();

                    if (Util.isTextEmpty(guest.name) == true)
                    {
                        mReservationName.requestFocus();

                        if (hotelPaymentInformation.getSaleRoomInformation().isOverseas == true)
                        {
                            DailyToast.showToast(this, R.string.toast_msg_please_input_guest_typeoverseas, Toast.LENGTH_SHORT);
                        } else
                        {
                            DailyToast.showToast(this, R.string.toast_msg_please_input_guest, Toast.LENGTH_SHORT);
                        }
                        return;
                    } else if (Util.isTextEmpty(guest.phone) == true)
                    {
                        mReservationPhone.requestFocus();
                        DailyToast.showToast(this, R.string.toast_msg_please_input_contact, Toast.LENGTH_SHORT);
                        return;
                    } else if (Util.isTextEmpty(guest.email) == true)
                    {
                        mReservationEmail.requestFocus();
                        DailyToast.showToast(this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
                        return;
                    } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(guest.email).matches() == false)
                    {
                        mReservationEmail.requestFocus();
                        DailyToast.showToast(HotelPaymentActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                        return;
                    }

                    if (hotelPaymentInformation.getSaleRoomInformation().isOverseas == true)
                    {
                        DailyPreference.getInstance(HotelPaymentActivity.this).setOverseasUserInformation(guest.name, guest.phone, guest.email);
                    }
                } else
                {
                    // 사용자 요청 메세지 추가
                    guest.message = mMemoEditText.getText().toString().trim();

                    hotelPaymentInformation.setGuest(guest);
                }

                //호텔 가격이 xx 이하인 이벤트 호텔에서는 적립금 사용을 못하게 막음.
                if (hotelPaymentInformation.discountType == PlacePaymentInformation.DiscountType.BONUS //
                    && (hotelPaymentInformation.getSaleRoomInformation().totalDiscount <= DEFAULT_AVAILABLE_RESERVES) //
                    && hotelPaymentInformation.bonus != 0)
                {
                    setBonusSelected(false);

                    String msg = getString(R.string.dialog_btn_payment_no_reserve, Util.getPriceFormat(this, DEFAULT_AVAILABLE_RESERVES, false));

                    showSimpleDialog(getString(R.string.dialog_notice2), msg, getString(R.string.dialog_btn_text_confirm), null);

                    releaseUiComponent();
                } else
                {
                    if (hotelPaymentInformation.paymentType == PlacePaymentInformation.PaymentType.VBANK//
                        && DailyPreference.getInstance(HotelPaymentActivity.this).getNotificationUid() < 0)
                    {
                        // 가상계좌 결제시 푸쉬를 받지 못하는 경우
                        String title = getString(R.string.dialog_notice2);
                        String positive = getString(R.string.dialog_btn_text_confirm);
                        String msg = getString(R.string.dialog_msg_none_gcmid);

                        showSimpleDialog(title, msg, positive, new OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

                                Stay.Grade hotelGrade = hotelPaymentInformation.getSaleRoomInformation().grade;
                                if (Stay.Grade.pension == hotelGrade || Stay.Grade.fullvilla == hotelGrade)
                                {
                                    lockUI();

                                    DailyNetworkAPI.getInstance(HotelPaymentActivity.this).requestCommonDatetime(mNetworkTag, mMessageDateTimeJsonResponseListener, HotelPaymentActivity.this);
                                } else
                                {
                                    processAgreeTermDialog();
                                }

                            }
                        }, new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                unLockUI();
                            }
                        });
                    } else
                    {
                        Stay.Grade hotelGrade = hotelPaymentInformation.getSaleRoomInformation().grade;
                        if (Stay.Grade.pension == hotelGrade | Stay.Grade.fullvilla == hotelGrade)
                        {
                            lockUI();

                            DailyNetworkAPI.getInstance(HotelPaymentActivity.this).requestCommonDatetime(mNetworkTag, mMessageDateTimeJsonResponseListener, HotelPaymentActivity.this);
                        } else
                        {
                            processAgreeTermDialog();
                        }
                    }
                }
                break;
            }

            case R.id.cardManagerLayout:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                if (mIsEditMode == true)
                {
                    guest.name = mReservationName.getText().toString();
                    guest.phone = mReservationPhone.getText().toString();
                    guest.email = mReservationEmail.getText().toString();
                    guest.message = mMemoEditText.getText().toString();
                }

                startCreditCardList();
                break;
            }
        }
    }

    @Override
    protected void processAgreeTermDialog()
    {
        super.processAgreeTermDialog();

        HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

        String label = String.format("%s-%s", hotelPaymentInformation.getSaleRoomInformation().hotelName, hotelPaymentInformation.getSaleRoomInformation().roomName);
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , Action.PAYMENT_CLICKED, label, null);
    }

    @Override
    protected void setCoupon(final Coupon coupon)
    {
        final HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

        int originalPrice = hotelPaymentInformation.getSaleRoomInformation().totalDiscount;

        if (coupon.amount > originalPrice)
        {
            String difference = Util.getPriceFormat(this, (coupon.amount - originalPrice), false);

            showSimpleDialog(null, getString(R.string.message_over_coupon_price, difference), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    hotelPaymentInformation.setCoupon(coupon);
                    setCouponSelected(true);
                }
            }, new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    hotelPaymentInformation.setCoupon(null);
                    setCouponSelected(false);
                }
            }, new DialogInterface.OnCancelListener()
            {

                @Override
                public void onCancel(DialogInterface dialog)
                {
                    hotelPaymentInformation.setCoupon(null);
                    setCouponSelected(false);
                }
            }, null, true);

        } else
        {
            // 호텔 결제 정보에 쿠폰 가격 넣고 텍스트 업데이트 필요
            hotelPaymentInformation.setCoupon(coupon);
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

    private int[] paymentDialogMessage(int messageType, int[] currentMessages)
    {
        int[] messages;
        int addMessagesCount = 2;
        int startPosition = 2;

        switch (mPensionPopupMessageType)
        {
            case 1:
            case 2:
            case 10:
                messages = new int[currentMessages.length + addMessagesCount];
                messages[0] = currentMessages[0];
                messages[1] = currentMessages[1];
                messages[2] = R.string.dialog_msg_hotel_payment_message09;
                messages[3] = R.string.dialog_msg_hotel_payment_message15;
                System.arraycopy(currentMessages, startPosition, messages, startPosition + addMessagesCount, currentMessages.length - startPosition);
                break;

            case 3:
            case 12:
                messages = new int[currentMessages.length + addMessagesCount];
                messages[0] = currentMessages[0];
                messages[1] = currentMessages[1];
                messages[2] = R.string.dialog_msg_hotel_payment_message11;
                messages[3] = R.string.dialog_msg_hotel_payment_message15;
                System.arraycopy(currentMessages, startPosition, messages, startPosition + addMessagesCount, currentMessages.length - startPosition);
                break;

            case 11:
                messages = new int[currentMessages.length + addMessagesCount];
                messages[0] = currentMessages[0];
                messages[1] = currentMessages[1];
                messages[2] = R.string.dialog_msg_hotel_payment_message12;
                messages[3] = R.string.dialog_msg_hotel_payment_message15;
                System.arraycopy(currentMessages, startPosition, messages, startPosition + addMessagesCount, currentMessages.length - startPosition);
                break;

            default:
                messages = currentMessages;
                break;
        }

        return messages;
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(Screen.DAILYHOTEL_PAYMENT);

        super.onStart();
    }


    private Map<String, String> getMapPaymentInformation(HotelPaymentInformation hotelPaymentInformation)
    {
        if (hotelPaymentInformation == null)
        {
            return null;
        }

        Map<String, String> params = new HashMap<>();

        try
        {
            SaleRoomInformation saleRoomInformation = hotelPaymentInformation.getSaleRoomInformation();

            params.put(AnalyticsManager.KeyType.NAME, saleRoomInformation.hotelName);
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(hotelPaymentInformation.placeIndex));
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(saleRoomInformation.averageDiscount));
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(saleRoomInformation.nights));
            params.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(saleRoomInformation.totalDiscount));
            params.put(AnalyticsManager.KeyType.TICKET_NAME, saleRoomInformation.roomName);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(saleRoomInformation.roomIndex));
            params.put(AnalyticsManager.KeyType.GRADE, hotelPaymentInformation.getSaleRoomInformation().grade.getName(this));
            params.put(AnalyticsManager.KeyType.DBENEFIT, hotelPaymentInformation.isDBenefit ? "yes" : "no");

            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + hotelPaymentInformation.getSaleRoomInformation().nights);

            params.put(AnalyticsManager.KeyType.CHECK_IN, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

            switch (hotelPaymentInformation.discountType)
            {
                case BONUS:
                {
                    int payPrice = saleRoomInformation.totalDiscount - hotelPaymentInformation.bonus;
                    int bonus;

                    if (payPrice <= 0)
                    {
                        payPrice = 0;
                        bonus = saleRoomInformation.totalDiscount;
                    } else
                    {
                        bonus = hotelPaymentInformation.bonus;
                    }

                    params.put(AnalyticsManager.KeyType.USED_BOUNS, Integer.toString(bonus));
                    params.put(AnalyticsManager.KeyType.COUPON_REDEEM, "false");
                    params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(payPrice));
                    break;
                }

                case COUPON:
                {
                    Coupon coupon = hotelPaymentInformation.getCoupon();
                    int payPrice = saleRoomInformation.totalDiscount - coupon.amount;

                    if (payPrice < 0)
                    {
                        payPrice = 0;
                    }

                    params.put(AnalyticsManager.KeyType.USED_BOUNS, "0");
                    params.put(AnalyticsManager.KeyType.COUPON_REDEEM, "true");
                    params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(payPrice));
                    params.put(AnalyticsManager.KeyType.COUPON_NAME, coupon.title);
                    params.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, coupon.availableItem);
                    params.put(AnalyticsManager.KeyType.PRICE_OFF, Integer.toString(coupon.amount));

                    //                    String expireDate = Util.simpleDateFormatISO8601toFormat(coupon.validTo, "yyyyMMddHHmm");
                    String expireDate = DailyCalendar.convertDateFormatString(coupon.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm");
                    params.put(AnalyticsManager.KeyType.EXPIRATION_DATE, expireDate);
                    break;
                }

                default:
                {
                    params.put(AnalyticsManager.KeyType.USED_BOUNS, "0");
                    params.put(AnalyticsManager.KeyType.COUPON_REDEEM, "false");
                    params.put(AnalyticsManager.KeyType.PAYMENT_PRICE, Integer.toString(saleRoomInformation.totalDiscount));
                    break;
                }
            }

            params.put(AnalyticsManager.KeyType.PAYMENT_TYPE, hotelPaymentInformation.paymentType.getName());
            params.put(AnalyticsManager.KeyType.ADDRESS, hotelPaymentInformation.getSaleRoomInformation().address);
            params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, hotelPaymentInformation.getSaleRoomInformation().categoryCode);

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
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
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

    private void recordAnalyticsPaymentComplete(PlacePaymentInformation paymentInformation)
    {
        try
        {
            //            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
            //            Date date = new Date();
            //            String strDate = dateFormat.format(date);
            String strDate = DailyCalendar.format(new Date(), "yyyyMMddHHmmss");

            String userIndex = paymentInformation.getCustomer().getUserIdx();
            String transId = strDate + '_' + userIndex;

            Map<String, String> params = getMapPaymentInformation((HotelPaymentInformation) paymentInformation);

            AnalyticsManager.getInstance(getApplicationContext()).purchaseCompleteHotel(transId, params);
            AnalyticsManager.getInstance(getApplicationContext()).recordScreen(Screen.DAILYHOTEL_PAYMENT_COMPLETE);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void recordAnalyticsPayment(PlacePaymentInformation paymentInformation)
    {
        if (paymentInformation == null)
        {
            return;
        }

        HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, hotelPaymentInformation.getSaleRoomInformation().hotelName);
            params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(hotelPaymentInformation.getSaleRoomInformation().averageDiscount));
            params.put(AnalyticsManager.KeyType.TOTAL_PRICE, Integer.toString(hotelPaymentInformation.getSaleRoomInformation().totalDiscount));
            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(hotelPaymentInformation.getSaleRoomInformation().nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(hotelPaymentInformation.placeIndex));

            SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay() + hotelPaymentInformation.getSaleRoomInformation().nights);

            params.put(AnalyticsManager.KeyType.CHECK_IN, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, checkOutSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.TICKET_NAME, hotelPaymentInformation.getSaleRoomInformation().roomName);
            params.put(AnalyticsManager.KeyType.TICKET_INDEX, Integer.toString(hotelPaymentInformation.getSaleRoomInformation().roomIndex));
            params.put(AnalyticsManager.KeyType.GRADE, hotelPaymentInformation.getSaleRoomInformation().grade.getName(HotelPaymentActivity.this));
            params.put(AnalyticsManager.KeyType.DBENEFIT, hotelPaymentInformation.isDBenefit ? "yes" : "no");
            params.put(AnalyticsManager.KeyType.ADDRESS, hotelPaymentInformation.getSaleRoomInformation().address);
            params.put(AnalyticsManager.KeyType.HOTEL_CATEGORY, hotelPaymentInformation.getSaleRoomInformation().categoryCode);

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
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                }

                params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            AnalyticsManager.getInstance(HotelPaymentActivity.this).recordScreen(Screen.DAILYHOTEL_PAYMENT, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UI Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnClickListener mOnEditInfoOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            mIsEditMode = true;
            view.setVisibility(View.INVISIBLE);

            // 이름.
            if (mReservationName.isEnabled() == false)
            {
                mReservationName.setEnabled(true);

                HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

                if (hotelPaymentInformation.getSaleRoomInformation().isOverseas == true)
                {
                    // 회원 가입시 이름 필터 적용.
                    StringFilter stringFilter = new StringFilter(HotelPaymentActivity.this);
                    InputFilter[] allowAlphanumericName = new InputFilter[2];
                    allowAlphanumericName[0] = stringFilter.allowAlphanumericName;
                    allowAlphanumericName[1] = new InputFilter.LengthFilter(20);

                    mReservationName.setFilters(allowAlphanumericName);
                    mReservationName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | mReservationName.getInputType());
                } else
                {
                    mReservationName.setEnabled(true);

                    // 회원 가입시 이름 필터 적용.
                    StringFilter stringFilter = new StringFilter(HotelPaymentActivity.this);
                    InputFilter[] allowAlphanumericHangul = new InputFilter[2];
                    allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;
                    allowAlphanumericHangul[1] = new InputFilter.LengthFilter(20);

                    mReservationName.setFilters(allowAlphanumericHangul);
                    mReservationName.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    mReservationName.setBackground(mEditTextBackground[0]);
                } else
                {
                    mReservationName.setBackgroundDrawable(mEditTextBackground[0]);
                }
            }

            // 전화번호.
            mReservationPhone.setEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mReservationPhone.setBackground(mEditTextBackground[1]);
            } else
            {
                mReservationPhone.setBackgroundDrawable(mEditTextBackground[1]);
            }

            mReservationPhone.setCursorVisible(false);
            mReservationPhone.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (isFinishing() == true)
                    {
                        return;
                    }

                    if (hasFocus == true)
                    {
                        startInputMobileNumberDialog(mReservationPhone.getText().toString());
                    } else
                    {
                        mReservationPhone.setSelected(false);
                    }
                }
            });

            View fakeMobileEditView = findViewById(R.id.fakeMobileEditView);

            fakeMobileEditView.setFocusable(true);
            fakeMobileEditView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mReservationPhone.isSelected() == true)
                    {
                        startInputMobileNumberDialog(mReservationPhone.getText().toString());
                    } else
                    {
                        mReservationPhone.requestFocus();
                        mReservationPhone.setSelected(true);
                    }
                }
            });

            // 이메일.

            mReservationEmail.setEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mReservationEmail.setBackground(mEditTextBackground[2]);
            } else
            {
                mReservationEmail.setBackgroundDrawable(mEditTextBackground[2]);
            }

            mReservationEmail.setOnEditorActionListener(new OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
                {
                    if (actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        textView.clearFocus();

                        if (getWindow() == null || getWindow().getDecorView() == null || getWindow().getDecorView().getWindowToken() == null)
                        {
                            return false;
                        }

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        return true;
                    } else
                    {
                        return false;
                    }
                }
            });
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        DailyToast.showToast(HotelPaymentActivity.this, msg, Toast.LENGTH_SHORT);
                        setResult(CODE_RESULT_ACTIVITY_REFRESH);
                        finish();
                        return;
                    } else
                    {
                        throw new NullPointerException("response == null");
                    }
                }

                JSONObject jsonData = response.getJSONObject("data");

                String name = jsonData.getString("user_name");
                String phone = jsonData.getString("user_phone");
                String email = jsonData.getString("user_email");
                String userIndex = jsonData.getString("user_idx");
                int bonus = jsonData.getInt("user_bonus");

                if (bonus < 0)
                {
                    bonus = 0;
                }

                HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;
                hotelPaymentInformation.bonus = bonus;

                updatePaymentPrice(hotelPaymentInformation);

                Customer buyer = new Customer();
                buyer.setEmail(email);
                buyer.setName(name);
                buyer.setPhone(phone);
                buyer.setUserIdx(userIndex);

                Guest guest = new Guest();
                guest.name = name;
                guest.phone = phone;
                guest.email = email;
                guest.message = mMemoEditText.getText().toString().trim();

                hotelPaymentInformation.setCustomer(buyer);
                hotelPaymentInformation.setGuest(guest);

                // 해외 호텔인 경우.
                if (hotelPaymentInformation.getSaleRoomInformation().isOverseas == true)
                {
                    String overseasName = DailyPreference.getInstance(HotelPaymentActivity.this).getOverseasName();
                    String overseasPhone = DailyPreference.getInstance(HotelPaymentActivity.this).getOverseasPhone();
                    String overseasEmail = DailyPreference.getInstance(HotelPaymentActivity.this).getOverseasEmail();

                    guest.name = overseasName;

                    if (Util.isTextEmpty(overseasPhone) == false)
                    {
                        guest.phone = overseasPhone;
                    }

                    if (Util.isTextEmpty(overseasEmail) == false)
                    {
                        guest.email = overseasEmail;
                    }

                    if (mIsEditMode == false)
                    {
                        if (Util.isNameCharacter(overseasName) == false)
                        {
                            mIsEditMode = true;

                            guest.name = "";
                            mReservationName.setText("");
                            mReservationName.setEnabled(true);
                            mReservationName.requestFocus();

                            // 회원 가입시 이름 필터 적용.
                            StringFilter stringFilter = new StringFilter(HotelPaymentActivity.this);
                            InputFilter[] allowAlphanumericName = new InputFilter[2];
                            allowAlphanumericName[0] = stringFilter.allowAlphanumericName;
                            allowAlphanumericName[1] = new InputFilter.LengthFilter(20);

                            mReservationName.setFilters(allowAlphanumericName);
                            mReservationName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | mReservationName.getInputType());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                mReservationName.setBackground(mEditTextBackground[0]);
                            } else
                            {
                                mReservationName.setBackgroundDrawable(mEditTextBackground[0]);
                            }
                        } else
                        {
                            mReservationName.setText(overseasName);
                        }

                        mReservationPhone.setText(Util.addHippenMobileNumber(HotelPaymentActivity.this, guest.phone));
                        mReservationEmail.setText(guest.email);
                    }
                } else
                {
                    if (mIsEditMode == false)
                    {
                        mReservationName.setText(guest.name);
                        mReservationPhone.setText(Util.addHippenMobileNumber(HotelPaymentActivity.this, guest.phone));
                        mReservationEmail.setText(guest.email);
                    }
                }

                // 2. 화면 정보 얻기
                DailyNetworkAPI.getInstance(HotelPaymentActivity.this).requestHotelPaymentInformation(mNetworkTag//
                    , hotelPaymentInformation.getSaleRoomInformation().roomIndex//
                    , mCheckInSaleTime.getDayOfDaysDateFormat("yyyyMMdd")//
                    , hotelPaymentInformation.getSaleRoomInformation().nights, mHotelPaymentInformationJsonResponseListener, HotelPaymentActivity.this);
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mHotelPaymentInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

                int msgCode = response.getInt("msgCode");

                // 0	성공
                // 4	데이터가 없을시
                // 5	판매 마감시
                // 6	현재 시간부터 날짜 바뀌기 전시간(새벽 3시
                // 7    3시부터 9시까지
                switch (msgCode)
                {
                    case 6:
                    case 7:
                        if (mWarningDialogMessage == null && response.has("msg") == true)
                        {
                            mWarningDialogMessage = response.getString("msg");
                        }
                    case 0:
                    {
                        JSONObject jsonData = response.getJSONObject("data");

                        long checkInDate = jsonData.getLong("check_in_date");
                        long checkOutDate = jsonData.getLong("check_out_date");
                        int discount = jsonData.getInt("discount_total");
                        boolean isOnSale = jsonData.getBoolean("on_sale");
                        int availableRooms = jsonData.getInt("available_rooms");

                        SaleRoomInformation saleRoomInformation = hotelPaymentInformation.getSaleRoomInformation();

                        // 가격이 변동 되었다.
                        if (saleRoomInformation.totalDiscount != discount)
                        {
                            mIsChangedPrice = true;
                        }

                        saleRoomInformation.totalDiscount = discount;

                        // Check In
                        Calendar calendarCheckin = DailyCalendar.getInstance();
                        calendarCheckin.setTimeZone(TimeZone.getTimeZone("GMT"));
                        calendarCheckin.setTimeInMillis(checkInDate);
                        //
                        //                        SimpleDateFormat formatDay = new SimpleDateFormat("yyyy.M.d (EEE) HH시", Locale.KOREA);
                        //                        formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));
                        //
                        //                        mCheckinDayTextView.setText(formatDay.format(calendarCheckin.getTime()));
                        mCheckinDayTextView.setText(DailyCalendar.format(checkInDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT")));

                        // CheckOut
                        Calendar calendarCheckout = DailyCalendar.getInstance();
                        calendarCheckout.setTimeZone(TimeZone.getTimeZone("GMT"));
                        calendarCheckout.setTimeInMillis(checkOutDate);
                        //
                        //                        mCheckoutDayTextView.setText(formatDay.format(calendarCheckout.getTime()));
                        mCheckoutDayTextView.setText(DailyCalendar.format(checkOutDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT")));

                        //                        SimpleDateFormat checkInOutFormat = new SimpleDateFormat("yyyy.M.d(EEE) HH시", Locale.KOREA);
                        //                        checkInOutFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                        if (Util.getLCDWidth(HotelPaymentActivity.this) >= 720)
                        {
                            //                            hotelPaymentInformation.checkInOutDate = String.format("%s - %s"//
                            //                                , checkInOutFormat.format(calendarCheckin.getTime())//
                            //                                , checkInOutFormat.format(calendarCheckout.getTime()));

                            hotelPaymentInformation.checkInOutDate = String.format("%s - %s"//
                                , DailyCalendar.format(checkInDate, "yyyy.M.d(EEE) HH시", TimeZone.getTimeZone("GMT"))//
                                , DailyCalendar.format(checkOutDate, "yyyy.M.d(EEE) HH시", TimeZone.getTimeZone("GMT")));

                        } else
                        {
                            //                            hotelPaymentInformation.checkInOutDate = String.format("%s%n- %s"//
                            //                                , checkInOutFormat.format(calendarCheckin.getTime())//
                            //                                , checkInOutFormat.format(calendarCheckout.getTime()));

                            hotelPaymentInformation.checkInOutDate = String.format("%s%n- %s"//
                                , DailyCalendar.format(checkInDate, "yyyy.M.d(EEE) HH시", TimeZone.getTimeZone("GMT"))//
                                , DailyCalendar.format(checkOutDate, "yyyy.M.d(EEE) HH시", TimeZone.getTimeZone("GMT")));
                        }

                        calendarCheckin.setTimeInMillis(calendarCheckin.getTimeInMillis() - 3600 * 1000 * 9);
                        calendarCheckout.setTimeInMillis(calendarCheckout.getTimeInMillis() - 3600 * 1000 * 9);

                        //                        hotelPaymentInformation.checkInDateFormat = Util.getISO8601String(calendarCheckin.getTime());
                        //                        hotelPaymentInformation.checkOutDateFormat = Util.getISO8601String(calendarCheckout.getTime());

                        hotelPaymentInformation.checkInDateFormat = DailyCalendar.format(calendarCheckin.getTime(), DailyCalendar.ISO_8601_FORMAT);
                        hotelPaymentInformation.checkOutDateFormat = DailyCalendar.format(calendarCheckout.getTime(), DailyCalendar.ISO_8601_FORMAT);

                        recordAnalyticsPayment(hotelPaymentInformation);

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
                            // 3. 간편결제 credit card 요청
                            DailyNetworkAPI.getInstance(HotelPaymentActivity.this).requestUserBillingCardList(mNetworkTag, mUserCreditCardListJsonResponseListener, HotelPaymentActivity.this);
                        }
                        break;
                    }

                    case 5:
                    {
                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

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
                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

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
        }
    };

    private DailyHotelJsonResponseListener mPaymentEasyCreditCardJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            hidePorgressDialog();

            try
            {
                // 해당 화면은 메시지를 넣지 않는다.
                int msgCode = response.getInt("msgCode");
                String message = response.getString("msg");

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
        }
    };

    private DailyHotelJsonResponseListener mFinalCheckPayJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                switch (msgCode)
                {
                    case 6:
                    case 7:
                    case 0:
                    {
                        JSONObject jsonData = response.getJSONObject("data");

                        //                        long checkInDate = jsonData.getLong("check_in_date");
                        //                        long checkOutDate = jsonData.getLong("check_out_date");
                        int discount = jsonData.getInt("discount_total");
                        boolean isOnSale = jsonData.getBoolean("on_sale");
                        int availableRooms = jsonData.getInt("available_rooms");

                        HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

                        SaleRoomInformation saleRoomInformation = hotelPaymentInformation.getSaleRoomInformation();

                        // 가격이 변동 되었다.
                        if (saleRoomInformation.totalDiscount != discount)
                        {
                            mIsChangedPrice = true;
                        }

                        saleRoomInformation.totalDiscount = discount;

                        // 판매 중지 상품으로 호텔 리스트로 복귀 시킨다.
                        if (isOnSale == false || availableRooms == 0)
                        {
                            showStopOnSaleDialog();
                        } else if (mIsChangedPrice == true)
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
                            processPayment(hotelPaymentInformation, mCheckInSaleTime);
                        }
                        break;
                    }

                    case 5:
                    {
                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

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
                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

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
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mUserInformationFinalCheckJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        DailyToast.showToast(HotelPaymentActivity.this, msg, Toast.LENGTH_SHORT);
                        setResult(CODE_RESULT_ACTIVITY_REFRESH);
                        finish();
                        return;
                    } else
                    {
                        throw new NullPointerException("response == null");
                    }
                }

                JSONObject jsonData = response.getJSONObject("data");

                int bonus = jsonData.getInt("user_bonus");

                if (bonus < 0)
                {
                    bonus = 0;
                }

                HotelPaymentInformation hotelPaymentInformation = (HotelPaymentInformation) mPaymentInformation;

                if (hotelPaymentInformation.discountType == PlacePaymentInformation.DiscountType.BONUS //
                    && bonus != hotelPaymentInformation.bonus)
                {
                    hotelPaymentInformation.bonus = bonus;
                    showChangedBonusDialog();
                    return;
                }

                // 2. 마지막 가격 및 기타 이상이 없는지 검사
                DailyNetworkAPI.getInstance(HotelPaymentActivity.this).requestHotelPaymentInformation(mNetworkTag//
                    , hotelPaymentInformation.getSaleRoomInformation().roomIndex//
                    , mCheckInSaleTime.getDayOfDaysDateFormat("yyyyMMdd")//
                    , hotelPaymentInformation.getSaleRoomInformation().nights, mFinalCheckPayJsonResponseListener, HotelPaymentActivity.this);
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mMessageDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            try
            {
                //                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                //                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                //
                //                int openHour = Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("openDateTime"))));
                //                int closeHour = Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("closeDateTime"))));
                //                int currentHour = Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("currentDateTime"))));

                int openHour = Integer.parseInt(DailyCalendar.format(response.getLong("openDateTime"), "HH", TimeZone.getTimeZone("GMT")));
                int closeHour = Integer.parseInt(DailyCalendar.format(response.getLong("closeDateTime"), "HH", TimeZone.getTimeZone("GMT")));
                int currentHour = Integer.parseInt(DailyCalendar.format(response.getLong("currentDateTime"), "HH", TimeZone.getTimeZone("GMT")));

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
            } catch (Exception e)
            {
                onError(e);
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                finish();
            } finally
            {
                unLockUI();
            }
        }
    };
}
