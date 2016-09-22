package com.twoheart.dailyhotel.screen.hotel.payment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.HotelPaymentInformation;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.TimeZone;

public class HotelPaymentLayout extends BaseLayout implements View.OnClickListener, View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener
{
    private View mBookingLayout;
    private TextView mCheckinDayTextView, mCheckoutDayTextView, mNightsTextView;
    private TextView mBookingAmountTextView;
    private TextView mPriceTextView, mDiscountPriceTextView, mFinalPaymentTextView;
    private TextView mUserNameTextView, mUserPhoneTextView, mUserEmailTextView;
    private EditText mGuestNameEditText, mGuestPhoneEditText, mGuestEmailEditText;
    private TextView mPlaceNameTextView, mRoomTypeTextView;
    //    private EditText mMemoEditText;
    private View mUserLayout;
    private View mGuestFrameLayout, mGuestLinearLayout;

    private TextView mGuestNameHintEditText;
    private TextView mGuideNameMemo;
    private CheckBox mGuestCheckBox;

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

    //
    private View mRefundPolicyLayout;
    //
    private int mAnimationValue;
    private ValueAnimator mValueAnimator;
    private boolean mIsAnimationCancel;

    public interface OnEventListener extends OnBaseEventListener
    {
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
        initRefundPolicy(view);

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

    /**
     * 추후에 게스트입력 정보와 유저 입력 정보를 분리하도록 할때 User, Guest를 구분하도록 한다.
     *
     * @param view
     */
    private void initGuestInformation(View view)
    {
        mUserLayout = view.findViewById(R.id.userLayout);

        mUserNameTextView = (TextView) mUserLayout.findViewById(R.id.userNameTextView);
        mUserPhoneTextView = (TextView) mUserLayout.findViewById(R.id.userPhoneTextView);
        mUserEmailTextView = (TextView) mUserLayout.findViewById(R.id.userEmailTextView);

        mGuestFrameLayout = view.findViewById(R.id.guestFrameLayout);
        mGuestLinearLayout = mGuestFrameLayout.findViewById(R.id.guestLinearLayout);

        mGuideNameMemo = (TextView) mGuestLinearLayout.findViewById(R.id.guideNameMemoView);

        mGuestNameEditText = (EditText) mGuestLinearLayout.findViewById(R.id.guestNameEditText);
        mGuestPhoneEditText = (EditText) mGuestLinearLayout.findViewById(R.id.guestPhoneEditText);
        mGuestEmailEditText = (EditText) mGuestLinearLayout.findViewById(R.id.guestEmailEditText);

        mGuestPhoneEditText.setCursorVisible(false);

        mGuestNameEditText.setOnFocusChangeListener(this);
        mGuestPhoneEditText.setOnFocusChangeListener(this);
        mGuestEmailEditText.setOnFocusChangeListener(this);

        mGuestNameHintEditText = (TextView) mGuestLinearLayout.findViewById(R.id.guestNameHintEditText);
        mGuestNameHintEditText.setEnabled(false);
        mGuestNameHintEditText.setClickable(false);
        mGuestNameHintEditText.setVisibility(View.GONE);

        View fakeMobileEditView = mGuestLinearLayout.findViewById(R.id.fakeMobileEditView);
        fakeMobileEditView.setFocusable(true);
        fakeMobileEditView.setOnClickListener(this);

        mGuestCheckBox = (CheckBox) view.findViewById(R.id.guestCheckBox);
        mGuestCheckBox.setOnCheckedChangeListener(this);
    }

    private void initBookingMemo(View view)
    {
        //        mMemoEditText = (EditText) view.findViewById(R.id.memoEditText);
    }

    private void initPaymentInformation(View view)
    {
        mBookingAmountTextView = (TextView) view.findViewById(R.id.bookingAmountTextView);
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

    private void initRefundPolicy(View view)
    {
        mRefundPolicyLayout = view.findViewById(R.id.refundPolicyLayout);
        mRefundPolicyLayout.setVisibility(View.GONE);
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

    public boolean isPaymentTypeEnabled(PlacePaymentInformation.PaymentType paymentType)
    {
        switch (paymentType)
        {
            case EASY_CARD:
                return mDisableSimpleCardView.getVisibility() == View.VISIBLE;

            case CARD:
                return mDisableCardView.getVisibility() == View.VISIBLE;

            case PHONE_PAY:
                return mDisablePhoneView.getVisibility() == View.VISIBLE;

            case VBANK:
                return mDisableTransferView.getVisibility() == View.VISIBLE;
        }

        return false;
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

    public void setReservationInformation(HotelPaymentInformation hotelPaymentInformation)
    {
        // 예약 장소
        mPlaceNameTextView.setText(hotelPaymentInformation.getSaleRoomInformation().hotelName);

        // 객실 타입
        mRoomTypeTextView.setText(hotelPaymentInformation.getSaleRoomInformation().roomName);

        String checkInDateFormat = DailyCalendar.format(hotelPaymentInformation.checkInDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT"));
        SpannableStringBuilder checkInSpannableStringBuilder = new SpannableStringBuilder(checkInDateFormat);
        checkInSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getMediumTypeface()),//
            checkInDateFormat.length() - 3, checkInDateFormat.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mCheckinDayTextView.setText(checkInSpannableStringBuilder);

        String checkOutDateFormat = DailyCalendar.format(hotelPaymentInformation.checkOutDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT"));
        SpannableStringBuilder checkOutSpannableStringBuilder = new SpannableStringBuilder(checkOutDateFormat);
        checkOutSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getMediumTypeface()),//
            checkOutDateFormat.length() - 3, checkOutDateFormat.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mCheckoutDayTextView.setText(checkOutSpannableStringBuilder);

        mNightsTextView.setText(mContext.getString(R.string.label_nights, hotelPaymentInformation.nights));
    }

    protected void setUserInformation(Customer user, boolean isOverseas)
    {
        if (user == null)
        {
            return;
        }

        // 국내인 경우
        if (isOverseas == false)
        {
            mUserLayout.setVisibility(View.VISIBLE);

            // 예약자
            mUserNameTextView.setText(user.getName());

            // 연락처
            mUserPhoneTextView.setText(Util.addHippenMobileNumber(mContext, user.getPhone()));

            // 이메일
            mUserEmailTextView.setText(user.getEmail());
        } else
        {
            mUserLayout.setVisibility(View.GONE);
        }


        // 사용자 요청사항
        //        mMemoEditText.setText(guest.message);
    }

    protected void setGuestInformation(Guest guest, boolean isOverseas)
    {
        if (isOverseas == false)
        {
            if (guest == null)
            {
                mGuestNameEditText.setText(null);
                mGuestPhoneEditText.setText(null);
                mGuestEmailEditText.setText(null);
            } else
            {
                mGuestNameEditText.setText(guest.name);
                mGuestPhoneEditText.setText(Util.addHippenMobileNumber(mContext, guest.phone));
                mGuestEmailEditText.setText(guest.email);
            }

            mGuideNameMemo.setVisibility(View.GONE);

            mGuestNameEditText.setHint(R.string.label_booking_input_name);
            mGuestPhoneEditText.setHint(R.string.label_booking_input_phone);
            mGuestEmailEditText.setHint(R.string.label_booking_input_email);
        } else
        {
            ViewGroup.LayoutParams framelayoutParams = mGuestFrameLayout.getLayoutParams();
            framelayoutParams.height = Util.dpToPx(mContext, 164) + Util.dpToPx(mContext, 36);
            mGuestFrameLayout.setLayoutParams(framelayoutParams);

            ViewGroup.LayoutParams linearlayoutParams = mGuestLinearLayout.getLayoutParams();
            linearlayoutParams.height = framelayoutParams.height;
            mGuestLinearLayout.setLayoutParams(linearlayoutParams);

            mGuestNameHintEditText.setVisibility(View.VISIBLE);
            mGuestNameHintEditText.setText(R.string.message_guide_name_hint);

            // 회원 가입시 이름 필터 적용.
            StringFilter stringFilter = new StringFilter(mContext);
            InputFilter[] allowAlphanumericName = new InputFilter[2];
            allowAlphanumericName[0] = stringFilter.allowAlphanumericName;
            allowAlphanumericName[1] = new InputFilter.LengthFilter(20);

            mGuestNameEditText.setFilters(allowAlphanumericName);
            mGuestNameEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | mGuestNameEditText.getInputType());

            mGuestPhoneEditText.setText(Util.addHippenMobileNumber(mContext, guest.phone));
            mGuestEmailEditText.setText(guest.email);

            mGuestNameEditText.addTextChangedListener(new TextWatcher()
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

            mGuestNameEditText.setText(guest.name);

            mGuideNameMemo.setVisibility(View.VISIBLE);

            if (Util.getLCDWidth(mContext) > 480)
            {
                mGuideNameMemo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_payment_notice, 0, 0, 0);
                mGuideNameMemo.setCompoundDrawablePadding(Util.dpToPx(mContext, 4));
            }
        }
    }

    public void setGuestPhoneInformation(String mobileNumber)
    {
        if (mGuestPhoneEditText == null)
        {
            return;
        }

        mGuestPhoneEditText.setText(Util.addHippenMobileNumber(mContext, mobileNumber));
    }

    public void setPaymentInformation(HotelPaymentInformation hotelPaymentInformation, CreditCard selectedCreditCard)
    {
        if (hotelPaymentInformation == null)
        {
            return;
        }

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

    public void setPaymentInformation(PlacePaymentInformation.DiscountType discountType, int originalPrice, int discountPrice, int payPrice, int nights)
    {
        if (nights > 1)
        {
            mBookingAmountTextView.setText(mContext.getString(R.string.act_booking_price)//
                + mContext.getString(R.string.label_booking_hotel_nights, nights));
        }

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

    public void setRefundPolicyVisibility(boolean visibility)
    {
        if (visibility == true)
        {
            mRefundPolicyLayout.setVisibility(View.VISIBLE);

            TextView refundPolicyTextView = (TextView) mRefundPolicyLayout.findViewById(R.id.refundPolicyTextView);

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(mContext.getString(R.string.message_booking_refund_product));
            spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.dh_theme_color)), //
                0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            refundPolicyTextView.setText(spannableStringBuilder);
        } else
        {
            mRefundPolicyLayout.setVisibility(View.GONE);
        }
    }

    public Guest getGuest()
    {
        // 국내인 경우에는 체크된 경우만 해외인 경우에는 무조건 반환
        if (mGuestCheckBox.isChecked() == true || mUserLayout.getVisibility() == View.GONE)
        {
            Guest guest = new Guest();

            guest.name = mGuestNameEditText.getText().toString().trim();
            guest.phone = mGuestPhoneEditText.getText().toString().trim();
            guest.email = mGuestEmailEditText.getText().toString().trim();
            //            guest.message = mMemoEditText.getText().toString().trim();

            return guest;
        } else
        {
            return null;
        }
    }

    //    public String getMemoEditText()
    //    {
    //        return mMemoEditText.getText().toString().trim();
    //    }

    public void requestGuestInformationFocus(Constants.UserInformationType type)
    {
        switch (type)
        {
            case NAME:
                mGuestNameEditText.requestFocus();
                break;

            case PHONE:
                mGuestPhoneEditText.requestFocus();
                break;

            case EMAIL:
                mGuestEmailEditText.requestFocus();
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
        switch (v.getId())
        {
            case R.id.guestPhoneEditText:
                if (hasFocus == true)
                {
                    ((OnEventListener) mOnEventListener).showInputMobileNumberDialog(mGuestPhoneEditText.getText().toString());
                } else
                {
                    mGuestPhoneEditText.setSelected(false);
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
                if (mGuestPhoneEditText.isSelected() == true)
                {
                    ((OnEventListener) mOnEventListener).showInputMobileNumberDialog(mGuestPhoneEditText.getText().toString());
                } else
                {
                    mGuestPhoneEditText.requestFocus();
                    mGuestPhoneEditText.setSelected(true);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked)
    {
        if (mValueAnimator != null)
        {
            if (mValueAnimator.isRunning() == true)
            {
                mValueAnimator.cancel();
            }

            mValueAnimator = null;
        }

        if (isChecked == true)
        {
            mValueAnimator = ValueAnimator.ofInt(mAnimationValue, 100);
        } else
        {
            mValueAnimator = ValueAnimator.ofInt(mAnimationValue, 0);
        }

        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (Integer) animation.getAnimatedValue();

                mAnimationValue = value;

                ViewGroup.LayoutParams layoutParams = mGuestFrameLayout.getLayoutParams();
                layoutParams.height = Util.dpToPx(mContext, 164) * value / 100;

                mGuestLinearLayout.setTranslationY(layoutParams.height - Util.dpToPx(mContext, 164));
                mGuestFrameLayout.setLayoutParams(layoutParams);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mGuestNameEditText.setEnabled(false);
                mGuestPhoneEditText.setEnabled(false);
                mGuestEmailEditText.setEnabled(false);

                mIsAnimationCancel = false;
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mIsAnimationCancel == false)
                {
                    mGuestNameEditText.setEnabled(true);
                    mGuestPhoneEditText.setEnabled(true);
                    mGuestEmailEditText.setEnabled(true);

                    if (isChecked == false)
                    {
                        setGuestInformation(null, false);
                    }
                }

                mIsAnimationCancel = false;
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mIsAnimationCancel = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.setDuration(300);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.start();
    }
}
