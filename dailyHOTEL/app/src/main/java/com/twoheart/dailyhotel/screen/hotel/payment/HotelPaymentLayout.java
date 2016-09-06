package com.twoheart.dailyhotel.screen.hotel.payment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MotionEventCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.HotelPaymentInformation;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.RoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.screen.common.FinalCheckLayout;
import com.twoheart.dailyhotel.screen.information.coupon.SelectCouponDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
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
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.DailyScrollView;
import com.twoheart.dailyhotel.widget.DailySignatureView;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class HotelPaymentLayout extends BaseLayout implements View.OnClickListener, View.OnFocusChangeListener
{
    private View mBookingLayout;
    private TextView mCheckinDayTextView, mCheckoutDayTextView, mNightsTextView;
    private TextView mPriceTextView, mDiscountPriceTextView, mFinalPaymentTextView;
    private EditText mReservationName, mReservationPhone, mReservationEmail;
    private TextView mPlaceNameTextView, mRoomTypeTextView;
    private EditText mMemoEditText;
    private TextView mGuestNameHintEditText;
    private TextView mGuideNameMemo;

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

    private DailyToolbarLayout mDailyToolbarLayout;

    // 결제 수단 선택
    private View mSimpleCardLayout;
    private TextView mSimpleCardTextView;
    private View mCardLayout;
    private View mPhoneLayout;
    private View mTransferLayout;

    private View mDisableSimpleCardView;
    private View mDisableCardView;
    private View mDisablePhoneView;
    private View mDisableTransferView;

    private View mCardManagerLayout;
    private TextView mCardManagerTextView;
    private TextView mGuidePaymentMemoView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void editUserInformation();

        void startCreditCardManager();

        void changedPaymentType(PlacePaymentInformation.PaymentType paymentType);

        void doPayment();

        void showInputMobileNumberDialog(String mobileNumber);

        void showCallDialog();

        void onBonusClick(boolean isRadioLayout);

        void onCouponClick(boolean isRadioLayout);
    }

    public HotelPaymentLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mBookingLayout = scrollView.findViewById(R.id.bookingLayout);

        initReservationInformation(view);
        initBookingMemo(view);
        initPaymentInformation(view);
        initPaymentTypeInformation(view);

        // 결제하기
        View doPaymentView = view.findViewById(R.id.doPaymentView);
        doPaymentView.setOnClickListener(this);
    }

    private void initToolbar(View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        mDailyToolbarLayout.initToolbar(null, new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_call, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).showCallDialog();
            }
        });
    }

    public void setToolbarTitle(String title)
    {
        mDailyToolbarLayout.setToolbarText(title);
    }

    private void initReservationInformation(View view)
    {
        View dateInformationLayout = view.findViewById(R.id.dateInformationLayout);

        mCheckinDayTextView = (TextView) dateInformationLayout.findViewById(R.id.checkinDayTextView);
        mCheckoutDayTextView = (TextView) dateInformationLayout.findViewById(R.id.checkoutDayTextView);
        mNightsTextView = (TextView) dateInformationLayout.findViewById(R.id.nightsTextView);

        mPlaceNameTextView = (TextView) view.findViewById(R.id.placeNameTextView);
        mRoomTypeTextView = (TextView) view.findViewById(R.id.roomTypeTextView);

        // 투숙객 정보
        initGuestInformation(view);
    }

    private void initGuestInformation(View view)
    {
        mReservationName = (EditText) view.findViewById(R.id.guestNameEditText);
        mReservationPhone = (EditText) view.findViewById(R.id.guestPhoneEditText);
        mReservationEmail = (EditText) view.findViewById(R.id.guestEmailEditText);

        mReservationName.setOnFocusChangeListener(this);
        mReservationPhone.setOnFocusChangeListener(this);
        mReservationEmail.setOnFocusChangeListener(this);

        mGuestNameHintEditText = (TextView) view.findViewById(R.id.guestNameHintEditText);
        mGuestNameHintEditText.setEnabled(false);
        mGuestNameHintEditText.setClickable(false);
        mGuestNameHintEditText.setVisibility(View.GONE);

        // 전화번호.
        mReservationPhone.setCursorVisible(false);

        View fakeMobileEditView = view.findViewById(R.id.fakeMobileEditView);
        fakeMobileEditView.setFocusable(true);
        fakeMobileEditView.setOnClickListener(this);

        mGuideNameMemo = (TextView)view.findViewById(R.id.guideNameMemoView);
    }

    private void initBookingMemo(View view)
    {
        mMemoEditText = (EditText) view.findViewById(R.id.memoEditText);
    }

    private void initPaymentInformation(View view)
    {
        mPriceTextView = (TextView) view.findViewById(R.id.originalPriceTextView);
        mDiscountPriceTextView = (TextView) view.findViewById(R.id.discountPriceTextView);
        mFinalPaymentTextView = (TextView) view.findViewById(R.id.totalPaymentPriceTextView);

        mDiscountPriceTextView.setText(Util.getPriceFormat(mContext, 0, false));

        initDiscountInformation(view);
    }

    private void initDiscountInformation(View view)
    {
        mBonusRadioButton = (ImageView) view.findViewById(R.id.bonusRadioButton);
        mDiscountBonusLayout = view.findViewById(R.id.bonusLayout);
        mUsedBonusLayout = view.findViewById(R.id.usedBonusLayout);
        mUsedBonusTextView = (TextView) view.findViewById(R.id.usedBonusTextView);
        mBonusTextView = (TextView) view.findViewById(R.id.bonusTextView);

        mDiscountBonusLayout.setOnClickListener(this);

        mCouponRadioButton = (ImageView) view.findViewById(R.id.couponRadioButton);
        mDiscountCouponLayout = view.findViewById(R.id.couponLayout);
        mUsedCouponLayout = view.findViewById(R.id.usedCouponLayout);
        mUsedCouponTextView = (TextView) view.findViewById(R.id.usedCouponTextView);

        mDiscountCouponLayout.setOnClickListener(this);
    }

    private void initPaymentTypeInformation(View view)
    {
        mSimpleCardLayout = view.findViewById(R.id.simpleCardLayout);
        mSimpleCardTextView = (TextView) mSimpleCardLayout.findViewById(R.id.simpleCardTextView);
        mDisableSimpleCardView = view.findViewById(R.id.disableSimpleCardView);
        mCardManagerLayout = view.findViewById(R.id.cardManagerLayout);
        mCardManagerTextView = (TextView) mCardManagerLayout.findViewById(R.id.cardManagerTextView);

        mCardLayout = view.findViewById(R.id.cardLayout);
        mDisableCardView = mCardLayout.findViewById(R.id.disableCardView);

        mPhoneLayout = view.findViewById(R.id.phoneLayout);
        mDisablePhoneView = mPhoneLayout.findViewById(R.id.disablePhoneView);
        mTransferLayout = view.findViewById(R.id.transferLayout);
        mDisableTransferView = mTransferLayout.findViewById(R.id.disableTransferView);

        mCardManagerLayout.setOnClickListener(this);
        mSimpleCardLayout.setOnClickListener(this);
        mCardLayout.setOnClickListener(this);
        mPhoneLayout.setOnClickListener(this);
        mTransferLayout.setOnClickListener(this);

        mGuidePaymentMemoView = (TextView) view.findViewById(R.id.guidePaymentMemoView);
    }

    public void setPaymentMemoTextView(String text, boolean visible)
    {
        if (mGuidePaymentMemoView == null)
        {
            return;
        }

        if (Util.isTextEmpty(text) == true || visible == false)
        {
            mGuidePaymentMemoView.setVisibility(View.GONE);
            return;
        }

        mGuidePaymentMemoView.setVisibility(View.VISIBLE);
        mGuidePaymentMemoView.setText(text);
    }

    public void setPaymentTypeEnabled(PlacePaymentInformation.PaymentType paymentType, boolean enabled)
    {
        switch (paymentType)
        {
            case EASY_CARD:
                setPaymentTypeEnabled(mDisableSimpleCardView, enabled);
                break;

            case CARD:
                setPaymentTypeEnabled(mDisableCardView, enabled);
                break;

            case PHONE_PAY:
                setPaymentTypeEnabled(mDisablePhoneView, enabled);
                break;

            case VBANK:
                setPaymentTypeEnabled(mDisableTransferView, enabled);
                break;
        }
    }

    public void setReservationInformation(HotelPaymentInformation hotelPaymentInformation, long checkInDate, long checkOutDate, int nights)
    {
        // 예약 장소
        mPlaceNameTextView.setText(hotelPaymentInformation.getSaleRoomInformation().hotelName);

        // 객실 타입
        mRoomTypeTextView.setText(hotelPaymentInformation.getSaleRoomInformation().roomName);

        String checkInDateFormat = DailyCalendar.format(checkInDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT"));
        SpannableStringBuilder checkInSpannableStringBuilder = new SpannableStringBuilder(checkInDateFormat);
        checkInSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getMediumTypeface()),//
            checkInDateFormat.length() - 3, checkInDateFormat.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mCheckinDayTextView.setText(checkInSpannableStringBuilder);

        String checkOutDateFormat = DailyCalendar.format(checkOutDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT"));
        SpannableStringBuilder checkOutSpannableStringBuilder = new SpannableStringBuilder(checkOutDateFormat);
        checkOutSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getMediumTypeface()),//
            checkOutDateFormat.length() - 3, checkOutDateFormat.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mCheckoutDayTextView.setText(checkOutSpannableStringBuilder);

        mNightsTextView.setText(mContext.getString(R.string.label_nights, nights));
    }


    private void setPaymentTypeEnabled(View view, boolean enabled)
    {
        if (view == null)
        {
            return;
        }

        if (enabled == true)
        {
            view.setOnClickListener(null);
            view.setVisibility(View.GONE);
        } else
        {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });
        }
    }

    protected void setGuestInformation(HotelPaymentInformation hotelPaymentInformation)
    {
        if (hotelPaymentInformation == null)
        {
            return;
        }

        Guest guest = hotelPaymentInformation.getGuest();

        if (guest == null)
        {
            return;
        }

        // 예약자
        mReservationName.setText(guest.name);

        // 연락처
        mReservationPhone.setText(Util.addHippenMobileNumber(mContext, guest.phone));

        // 이메일
        mReservationEmail.setText(guest.email);

        // 사용자 요청사항
        mMemoEditText.setText(guest.message);


        if (hotelPaymentInformation.getSaleRoomInformation().isOverseas == true)
        {
            mGuestNameHintEditText.setVisibility(View.VISIBLE);
            mGuestNameHintEditText.setText(R.string.message_guide_name_hint);

            // 회원 가입시 이름 필터 적용.
            StringFilter stringFilter = new StringFilter(mContext);
            InputFilter[] allowAlphanumericName = new InputFilter[2];
            allowAlphanumericName[0] = stringFilter.allowAlphanumericName;
            allowAlphanumericName[1] = new InputFilter.LengthFilter(20);

            mReservationName.setFilters(allowAlphanumericName);
            mReservationName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | mReservationName.getInputType());

            mReservationPhone.setText(Util.addHippenMobileNumber(mContext, guest.phone));
            mReservationEmail.setText(guest.email);

            mReservationName.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {

                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    if (s == null || s.length() == 0)
                    {
                        mGuestNameHintEditText.setVisibility(View.VISIBLE);
                    } else
                    {
                        mGuestNameHintEditText.setVisibility(View.GONE);
                    }
                }
            });

            mGuideNameMemo.setVisibility(View.VISIBLE);

            if (Util.getLCDWidth(mContext) > 480)
            {
                mGuideNameMemo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_payment_name, 0, 0, 0);
                mGuideNameMemo.setCompoundDrawablePadding(Util.dpToPx(mContext, 4));
            }
        } else
        {
            mGuideNameMemo.setVisibility(View.GONE);
        }
    }

    public void setUserNameInformation(String name)
    {
        if (mReservationName == null)
        {
            return;
        }

        mReservationName.setText(name);
    }

    public void setUserPhoneInformation(String mobileNumber)
    {
        if (mReservationPhone == null)
        {
            return;
        }

        mReservationPhone.setText(Util.addHippenMobileNumber(mContext, mobileNumber));
    }

    public void setPaymentInformation(PlacePaymentInformation.DiscountType discountType, int originalPrice, int discountPrice, int payPrice)
    {
        mPriceTextView.setText(Util.getPriceFormat(mContext, originalPrice, false));

        switch (discountType)
        {
            case BONUS:
            {
                if (discountPrice == 0)
                {
                    mUsedBonusTextView.setText(R.string.label_booking_used_bonus);

                    setBonusEnabled(false);
                } else
                {
                    String priceFormat = Util.getPriceFormat(mContext, discountPrice, false);

                    mUsedBonusTextView.setText(priceFormat);
                    mDiscountPriceTextView.setText("- " + priceFormat);
                }
                break;
            }

            case COUPON:
            {
                if (discountPrice == 0)
                {
                    mUsedCouponTextView.setText(R.string.label_booking_select_coupon);
                } else
                {
                    String priceFormat = Util.getPriceFormat(mContext, discountPrice, false);

                    mUsedCouponTextView.setText(priceFormat);
                    mDiscountPriceTextView.setText("- " + priceFormat);
                }
                break;
            }

            default:
            {
                mDiscountPriceTextView.setText(Util.getPriceFormat(mContext, 0, false));
                break;
            }
        }

        mFinalPaymentTextView.setText(Util.getPriceFormat(mContext, payPrice, false));
    }

    public void setPaymentInformation(PlacePaymentInformation paymentInformation, CreditCard selectedCreditCard)
    {
        if (selectedCreditCard == null)
        {
            mCardManagerTextView.setText(R.string.label_register_card);
            mSimpleCardTextView.setText(R.string.label_booking_easypayment);
        } else
        {
            mCardManagerTextView.setText(R.string.label_manager);

            final String cardName = selectedCreditCard.name.replace("카드", "");
            final String cardNumber = selectedCreditCard.number;

            mSimpleCardTextView.setText(String.format("%s %s", cardName, cardNumber));
            mSimpleCardTextView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Layout layout = mSimpleCardTextView.getLayout();

                    if (layout == null || Util.isTextEmpty(cardName) == true)
                    {
                        return;
                    }

                    int lineCount = mSimpleCardTextView.getLineCount();
                    if (lineCount > 1)
                    {
                        mSimpleCardTextView.setText(String.format("%s\n%s", cardName, cardNumber));
                    }
                }
            });
        }
    }

    public Guest getGuest()
    {
        Guest guest = new Guest();

        guest.name = mReservationName.getText().toString().trim();
        guest.phone = mReservationPhone.getText().toString().trim();
        guest.email = mReservationEmail.getText().toString().trim();
        guest.message = mMemoEditText.getText().toString().trim();

        return guest;
    }

    public String getMemoEditText()
    {
        return mMemoEditText.getText().toString().trim();
    }

    public void requestUserInformationFocus(Constants.UserInformationType type)
    {
        switch (type)
        {
            case NAME:
                mReservationName.requestFocus();
                break;

            case PHONE:
                mReservationPhone.requestFocus();
                break;

            case EMAIL:
                mReservationEmail.requestFocus();
                break;
        }
    }

    public void clearFocus()
    {
        if (mBookingLayout == null)
        {
            return;
        }

        mBookingLayout.requestFocus();
    }

    public void checkPaymentType(PlacePaymentInformation.PaymentType paymentType)
    {
        if (paymentType == null)
        {
            ((View) mSimpleCardLayout.getParent()).setSelected(false);
            mSimpleCardLayout.setSelected(false);
            mCardLayout.setSelected(false);
            mPhoneLayout.setSelected(false);
            mTransferLayout.setSelected(false);
            return;
        }

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

        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , Action.PAYMENT_TYPE_ITEM_CLICKED, paymentType.getName(), null);
    }


    /**
     * 내가 보유한 적립금 작게 나오는 부분(보유: x원)
     *
     * @param bonus
     */
    public void setBonusTextView(int bonus)
    {
        String priceFormat = Util.getPriceFormat(mContext, bonus, false);
        String text = mContext.getString(R.string.label_booking_own_bonus, priceFormat);

        if (bonus > 0)
        {
            int startIndex = text.indexOf(priceFormat);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);

            spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_text_c323232)), //
                startIndex, text.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mBonusTextView.setText(spannableStringBuilder);
        } else
        {
            mBonusTextView.setText(text);
        }
    }

    public boolean setBonusSelected(boolean isSelected)
    {
        //selected가 true enabled가 false일수는 없다.
        if (mDiscountBonusLayout.isEnabled() == false)
        {
            return false;
        }

        if (isSelected == true)
        {
            mBonusRadioButton.setSelected(true);
            mDiscountBonusLayout.setSelected(true);
            mDiscountBonusLayout.setOnClickListener(null);

            mUsedBonusLayout.setOnClickListener(this);
            mUsedBonusLayout.setSelected(true);
        } else
        {
            mBonusRadioButton.setSelected(false);
            mDiscountBonusLayout.setSelected(false);
            mDiscountBonusLayout.setOnClickListener(this);

            mUsedBonusTextView.setText(R.string.label_booking_used_bonus);

            mUsedBonusLayout.setOnClickListener(this);
            mUsedBonusLayout.setSelected(false);
        }

        return true;
    }

    /**
     * selected가 true enabled가 false일수는 없다.
     *
     * @param isEnabled
     */
    public void setBonusEnabled(boolean isEnabled)
    {
        mBonusRadioButton.setEnabled(isEnabled);
        mDiscountBonusLayout.setEnabled(isEnabled);
        mUsedBonusLayout.setEnabled(isEnabled);
    }

    public void setCouponSelected(boolean isSelected)
    {
        if (isSelected == true)
        {
            mCouponRadioButton.setSelected(true);
            mDiscountCouponLayout.setSelected(true);
            mDiscountCouponLayout.setOnClickListener(null);

            mUsedCouponLayout.setOnClickListener(this);
            mUsedCouponLayout.setSelected(true);
        } else
        {
            mCouponRadioButton.setSelected(false);
            mDiscountCouponLayout.setSelected(false);
            mDiscountCouponLayout.setOnClickListener(this);

            mUsedCouponTextView.setText(R.string.label_booking_select_coupon);

            mUsedCouponLayout.setOnClickListener(this);
            mUsedCouponLayout.setSelected(false);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        ((OnEventListener) mOnEventListener).editUserInformation();

        switch (v.getId())
        {
            case R.id.guestPhoneEditText:
                if (hasFocus == true)
                {
                    ((OnEventListener) mOnEventListener).showInputMobileNumberDialog(mReservationPhone.getText().toString());
                } else
                {
                    mReservationPhone.setSelected(false);
                }
                break;
        }
    }

    @Override
    public void onClick(final View v)
    {
        switch (v.getId())
        {
            case R.id.fakeMobileEditView:
            {
                if (mReservationPhone.isSelected() == true)
                {
                    ((OnEventListener) mOnEventListener).showInputMobileNumberDialog(mReservationPhone.getText().toString());
                } else
                {
                    mReservationPhone.requestFocus();
                    mReservationPhone.setSelected(true);
                }
                break;
            }

            case R.id.usedBonusLayout:
                ((OnEventListener) mOnEventListener).onBonusClick(false);
                break;

            case R.id.usedCouponLayout:
                ((OnEventListener) mOnEventListener).onCouponClick(false);
                break;

            case R.id.bonusLayout:
                ((OnEventListener) mOnEventListener).onBonusClick(true);
                break;

            case R.id.couponLayout:
                ((OnEventListener) mOnEventListener).onCouponClick(true);
                break;

            case R.id.simpleCardLayout:
                ((OnEventListener) mOnEventListener).changedPaymentType(PlacePaymentInformation.PaymentType.EASY_CARD);
                break;

            case R.id.cardLayout:
                ((OnEventListener) mOnEventListener).changedPaymentType(PlacePaymentInformation.PaymentType.CARD);
                break;

            case R.id.phoneLayout:
                ((OnEventListener) mOnEventListener).changedPaymentType(PlacePaymentInformation.PaymentType.PHONE_PAY);
                break;

            case R.id.transferLayout:
                ((OnEventListener) mOnEventListener).changedPaymentType(PlacePaymentInformation.PaymentType.VBANK);
                break;

            case R.id.doPaymentView:
                ((OnEventListener) mOnEventListener).doPayment();
                break;

            case R.id.cardManagerLayout:
                ((OnEventListener) mOnEventListener).startCreditCardManager();
                break;
        }
    }
}
