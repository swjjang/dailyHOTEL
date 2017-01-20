package com.twoheart.dailyhotel.screen.hotel.payment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.model.StayPaymentInformation;
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
    ScrollView mScrollView;
    //
    private View mBookingLayout;
    private TextView mCheckinDayTextView, mCheckoutDayTextView, mNightsTextView;
    private TextView mAmountNightsTextView;
    private TextView mPriceTextView, mDiscountPriceTextView, mFinalPaymentTextView;
    private TextView mUserNameTextView, mUserPhoneTextView, mUserEmailTextView;
    EditText mGuestNameEditText, mGuestPhoneEditText, mGuestEmailEditText;
    private TextView mPlaceNameTextView, mRoomTypeTextView;
    //    private EditText mMemoEditText;
    private View mUserLayout;
    View mGuestFrameLayout, mGuestLinearLayout;
    //
    TextView mGuestNameHintEditText;
    private TextView mGuideNameMemo;
    private CheckBox mGuestCheckBox;

    private View mHowToVisitLayout;
    private View mVisitWalkView, mVisitCarView, mNoParkingView;
    private TextView mHowToVisitTextView, mGuideVisitMemoView;
    private View mGuideVisitMemoLayout;

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
    private View mFreePaymentView;
    private View paymentTypeInformationLayout;

    private View mSimpleCardLayout;
    private TextView mSimpleCardNumberTextView;
    private TextView mSimpleCardLogoTextView;
    private View mCardLayout;
    private View mPhoneLayout;
    private View mTransferLayout;

    private View mDisableSimpleCardView;
    private View mDisableCardView;
    private View mDisablePhoneView;
    private View mDisableTransferView;

    private View mCardManagerLayout;
    private TextView mGuidePaymentMemoView;

    private View mEmptySimpleCardLayout;
    private View mSelectedSimpleCardLayout;
    //
    private View mRefundPolicyLayout;
    //
    int mAnimationValue;
    private ValueAnimator mValueAnimator;
    boolean mIsAnimationCancel;
    //
    Rect mGuestFrameLayoutRect = new Rect();
    int mScrollMoveHeight;

    public interface OnEventListener extends OnBaseEventListener
    {
        void startCreditCardManager(boolean isRegister);

        void changedPaymentType(PlacePaymentInformation.PaymentType paymentType);

        void doPayment();

        void showInputMobileNumberDialog(String mobileNumber);

        void showCallDialog();

        void onBonusClick(boolean isRadioLayout);

        void onCouponClick(boolean isRadioLayout);

        void onVisitType(boolean isWalking);
    }

    public HotelPaymentLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);

        mScrollView = (ScrollView) view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(mScrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mBookingLayout = mScrollView.findViewById(R.id.bookingLayout);

        initReservationInformation(view);
        initVisitType(view);
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

    /**
     * 방문 타입.
     *
     * @param view
     */
    private void initVisitType(View view)
    {
        mHowToVisitLayout = view.findViewById(R.id.howToVisitLayout);
        mHowToVisitLayout.setVisibility(View.GONE);

        mHowToVisitTextView = (TextView) mHowToVisitLayout.findViewById(R.id.howToVisitTextView);
        mVisitWalkView = mHowToVisitLayout.findViewById(R.id.visitWalkView);
        mVisitCarView = mHowToVisitLayout.findViewById(R.id.visitCarView);
        mNoParkingView = mHowToVisitLayout.findViewById(R.id.noParkingView);

        mGuideVisitMemoLayout = mHowToVisitLayout.findViewById(R.id.guideVisitMemoLayout);
        mGuideVisitMemoView = (TextView) mHowToVisitLayout.findViewById(R.id.guideVisitMemoView);

        mVisitWalkView.setOnClickListener(this);
        mVisitCarView.setOnClickListener(this);
    }

    private void initBookingMemo(View view)
    {
        //        mMemoEditText = (EditText) view.findViewById(R.id.memoEditText);
    }

    private void initPaymentInformation(View view)
    {
        paymentTypeInformationLayout = view.findViewById(R.id.paymentTypeInformationLayout);

        mAmountNightsTextView = (TextView) view.findViewById(R.id.amountNightsTextView);
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
        mDisableSimpleCardView = view.findViewById(R.id.disableSimpleCardView);

        mCardManagerLayout = view.findViewById(R.id.cardManagerLayout);

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

        View simpleCreditCardLayout = view.findViewById(R.id.simpleCreditCardLayout);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) simpleCreditCardLayout.getLayoutParams();

        if (layoutParams == null)
        {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(Util.dpToPx(mContext, 15), Util.dpToPx(mContext, 15), Util.dpToPx(mContext, 15), Util.dpToPx(mContext, 15));
        }

        layoutParams.height = (Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 60)) * 9 / 16;
        simpleCreditCardLayout.setLayoutParams(layoutParams);

        mEmptySimpleCardLayout = simpleCreditCardLayout.findViewById(R.id.emptySimpleCardLayout);
        mSelectedSimpleCardLayout = simpleCreditCardLayout.findViewById(R.id.selectedSimpleCardLayout);
        mSimpleCardLogoTextView = (TextView) mSelectedSimpleCardLayout.findViewById(R.id.logoTextView);
        mSimpleCardNumberTextView = (TextView) mSelectedSimpleCardLayout.findViewById(R.id.numberTextView);

        mEmptySimpleCardLayout.setVisibility(View.VISIBLE);
        mSelectedSimpleCardLayout.setVisibility(View.GONE);

        mEmptySimpleCardLayout.setOnClickListener(this);
        mSelectedSimpleCardLayout.setOnClickListener(this);

        mGuidePaymentMemoView = (TextView) view.findViewById(R.id.guidePaymentMemoView);
        mFreePaymentView = view.findViewById(R.id.freePaymentView);
    }

    private void initRefundPolicy(View view)
    {
        mRefundPolicyLayout = view.findViewById(R.id.refundPolicyLayout);
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

    private void setPaymentTypeEnabled(final View view, boolean enabled)
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
            view.post(new Runnable()
            {
                @Override
                public void run()
                {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = ((View) view.getParent()).getHeight();
                    view.setLayoutParams(layoutParams);
                }
            });

            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });
        }
    }

    public void setReservationInformation(StayPaymentInformation stayPaymentInformation)
    {
        // 예약 장소
        mPlaceNameTextView.setText(stayPaymentInformation.getSaleRoomInformation().hotelName);

        // 객실 타입
        mRoomTypeTextView.setText(stayPaymentInformation.getSaleRoomInformation().roomName);

        String checkInDateFormat = DailyCalendar.format(stayPaymentInformation.checkInDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT"));
        SpannableStringBuilder checkInSpannableStringBuilder = new SpannableStringBuilder(checkInDateFormat);
        checkInSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getMediumTypeface()),//
            checkInDateFormat.length() - 3, checkInDateFormat.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mCheckinDayTextView.setText(checkInSpannableStringBuilder);

        String checkOutDateFormat = DailyCalendar.format(stayPaymentInformation.checkOutDate, "yyyy.M.d (EEE) HH시", TimeZone.getTimeZone("GMT"));
        SpannableStringBuilder checkOutSpannableStringBuilder = new SpannableStringBuilder(checkOutDateFormat);
        checkOutSpannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getMediumTypeface()),//
            checkOutDateFormat.length() - 3, checkOutDateFormat.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mCheckoutDayTextView.setText(checkOutSpannableStringBuilder);

        mNightsTextView.setText(mContext.getString(R.string.label_nights, stayPaymentInformation.nights));
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
            mUserPhoneTextView.setText(Util.addHyphenMobileNumber(mContext, user.getPhone()));

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
                mGuestPhoneEditText.setText(Util.addHyphenMobileNumber(mContext, guest.phone));
                mGuestEmailEditText.setText(guest.email);
            }

            mGuideNameMemo.setVisibility(View.GONE);

            mGuestNameEditText.setHint(R.string.label_booking_input_name);
            mGuestPhoneEditText.setHint(R.string.label_booking_input_phone);
            mGuestEmailEditText.setHint(R.string.label_booking_input_email);
        } else
        {
            ViewGroup.LayoutParams frameLayoutParams = mGuestFrameLayout.getLayoutParams();
            frameLayoutParams.height = Util.dpToPx(mContext, 164) + Util.dpToPx(mContext, 36);
            mGuestFrameLayout.setLayoutParams(frameLayoutParams);

            ViewGroup.LayoutParams linearLayoutParams = mGuestLinearLayout.getLayoutParams();
            linearLayoutParams.height = frameLayoutParams.height;
            mGuestLinearLayout.setLayoutParams(linearLayoutParams);

            mGuestNameHintEditText.setVisibility(View.VISIBLE);
            mGuestNameHintEditText.setText(R.string.message_guide_name_hint);
            mGuestPhoneEditText.setHint(R.string.label_booking_input_phone);
            mGuestEmailEditText.setHint(R.string.label_booking_input_email);

            // 회원 가입시 이름 필터 적용.
            StringFilter stringFilter = new StringFilter(mContext);
            InputFilter[] allowAlphanumericName = new InputFilter[2];
            allowAlphanumericName[0] = stringFilter.allowAlphanumericName;
            allowAlphanumericName[1] = new InputFilter.LengthFilter(20);

            mGuestNameEditText.setFilters(allowAlphanumericName);
            mGuestNameEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | mGuestNameEditText.getInputType());

            mGuestPhoneEditText.setText(Util.addHyphenMobileNumber(mContext, guest.phone));
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

        mGuestPhoneEditText.setText(Util.addHyphenMobileNumber(mContext, mobileNumber));
    }

    public void setVisitTypeInformation(StayPaymentInformation stayPaymentInformation)
    {
        switch (stayPaymentInformation.visitType)
        {
            case StayPaymentInformation.VISIT_TYPE_PARKING:
                mHowToVisitLayout.setVisibility(View.VISIBLE);

                mHowToVisitTextView.setText(R.string.label_how_to_visit);
                mVisitCarView.setVisibility(View.VISIBLE);
                mVisitWalkView.setVisibility(View.VISIBLE);
                mNoParkingView.setVisibility(View.GONE);

                mGuideVisitMemoView.setText(R.string.message_visit_car_memo);

                // 디폴트로 도보가 기본이다.
                if (stayPaymentInformation.isVisitWalking == true)
                {
                    mVisitWalkView.performClick();
                } else
                {
                    mVisitCarView.performClick();
                }
                break;

            case StayPaymentInformation.VISIT_TYPE_NO_PARKING:
                mHowToVisitLayout.setVisibility(View.VISIBLE);

                mHowToVisitTextView.setText(R.string.label_parking_information);
                mVisitCarView.setVisibility(View.GONE);
                mVisitWalkView.setVisibility(View.GONE);
                mNoParkingView.setVisibility(View.VISIBLE);

                mGuideVisitMemoView.setText(R.string.message_visit_no_parking_memo);
                break;

            default:
                mHowToVisitLayout.setVisibility(View.GONE);
                break;
        }
    }

    public void setPaymentInformation(StayPaymentInformation stayPaymentInformation, CreditCard creditCard)
    {
        if (stayPaymentInformation == null)
        {
            return;
        }

        if (creditCard == null)
        {
            mCardManagerLayout.setVisibility(View.GONE);
            mEmptySimpleCardLayout.setVisibility(View.VISIBLE);
            mSelectedSimpleCardLayout.setVisibility(View.GONE);
        } else
        {
            mCardManagerLayout.setVisibility(View.VISIBLE);
            mEmptySimpleCardLayout.setVisibility(View.GONE);
            mSelectedSimpleCardLayout.setVisibility(View.VISIBLE);

            setSimpleCardLayout(creditCard);
        }
    }

    private void setSimpleCardLayout(CreditCard creditCard)
    {
        if (creditCard == null)
        {
            return;
        }

        mSimpleCardLogoTextView.setText(creditCard.name);
        mSimpleCardNumberTextView.setText(creditCard.number);
    }

    public void setPaymentInformation(PlacePaymentInformation.DiscountType discountType, int originalPrice, int discountPrice, int payPrice, int nights)
    {
        if (nights > 1)
        {
            mAmountNightsTextView.setText(mContext.getString(R.string.label_booking_hotel_nights, nights));
            mAmountNightsTextView.setVisibility(View.VISIBLE);
        } else
        {
            mAmountNightsTextView.setVisibility(View.GONE);
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

        // 다음 버전에서 진행.
        //        if (payPrice == 0)
        //        {
        //            paymentTypeInformationLayout.setVisibility(View.GONE);
        //            mFreePaymentView.setVisibility(View.VISIBLE);
        //        } else
        {
            paymentTypeInformationLayout.setVisibility(View.VISIBLE);
            mFreePaymentView.setVisibility(View.GONE);
        }
    }

    public void setRefundPolicyText(String text)
    {
        TextView refundPolicyTextView = (TextView) mRefundPolicyLayout.findViewById(R.id.refundPolicyTextView);

        // 기본 디폴트 색상이 바뀌었음.
        String comment = text.replaceAll("900034", "B70038");

        refundPolicyTextView.setText(Html.fromHtml(comment));
    }

    public void setRefundPolicyVisible(boolean visible)
    {
        if (mRefundPolicyLayout == null)
        {
            return;
        }

        mRefundPolicyLayout.setVisibility(visible == true ? View.VISIBLE : View.GONE);
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
                ((OnEventListener) mOnEventListener).startCreditCardManager(false);
                break;

            case R.id.emptySimpleCardLayout:
                ((OnEventListener) mOnEventListener).startCreditCardManager(true);
                break;

            case R.id.selectedSimpleCardLayout:
                ((OnEventListener) mOnEventListener).changedPaymentType(PlacePaymentInformation.PaymentType.EASY_CARD);
                break;

            case R.id.visitWalkView:
                mGuideVisitMemoLayout.setVisibility(View.GONE);

                mVisitWalkView.setSelected(true);
                mVisitCarView.setSelected(false);

                ((OnEventListener) mOnEventListener).onVisitType(true);
                break;

            case R.id.visitCarView:
                mGuideVisitMemoLayout.setVisibility(View.VISIBLE);

                mVisitWalkView.setSelected(false);
                mVisitCarView.setSelected(true);

                ((OnEventListener) mOnEventListener).onVisitType(false);
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

        final int dp164 = Util.dpToPx(mContext, 164);
        final int height = Util.getLCDHeight(mContext);
        mScrollMoveHeight = -1;

        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (Integer) animation.getAnimatedValue();

                mAnimationValue = value;

                ViewGroup.LayoutParams layoutParams = mGuestFrameLayout.getLayoutParams();
                layoutParams.height = dp164 * value / 100;

                float prevTranslationY = mGuestLinearLayout.getTranslationY();

                mGuestLinearLayout.setTranslationY(layoutParams.height - dp164);
                mGuestFrameLayout.setLayoutParams(layoutParams);

                if (isChecked == true)
                {
                    mGuestFrameLayout.getGlobalVisibleRect(mGuestFrameLayoutRect);

                    if (mScrollMoveHeight < 0)
                    {
                        mScrollMoveHeight = (mGuestFrameLayoutRect.top + dp164 - height) / 2;
                    }

                    if (mScrollMoveHeight >= 0 && mGuestFrameLayoutRect.top + dp164 > (height - mScrollMoveHeight))
                    {
                        mScrollView.scrollBy(0, (int) (mGuestLinearLayout.getTranslationY() - prevTranslationY));
                    }
                } else
                {
                    mScrollMoveHeight = -1;
                }
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
