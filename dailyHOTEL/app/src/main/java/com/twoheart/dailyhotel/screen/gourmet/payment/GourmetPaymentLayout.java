package com.twoheart.dailyhotel.screen.gourmet.payment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
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
import com.twoheart.dailyhotel.model.GourmetPaymentInformation;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.TimeZone;

public class GourmetPaymentLayout extends BaseLayout implements View.OnClickListener, View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener
{
    private ScrollView mScrollView;
    //
    private View mBookingLayout;
    private TextView mTicketTypeTextView, mTicketDateTextView, mTicketCountTextView, mTicketTimeTextView;
    private TextView mAmountNightsTextView;
    private TextView mPriceTextView, mDiscountPriceTextView, mFinalPaymentTextView;
    private TextView mPlaceNameTextView;
    private TextView mUserNameTextView, mUserPhoneTextView, mUserEmailTextView;
    private EditText mGuestNameEditText, mGuestPhoneEditText, mGuestEmailEditText;
    //    private EditText mMemoEditText;
    private View mUserLayout;
    private View mGuestFrameLayout, mGuestLinearLayout;
    private CheckBox mGuestCheckBox;

    private View mTicketCountMinusButton, mTicketCountPlusButton;

    private DailyToolbarLayout mDailyToolbarLayout;

    // 할인 정보
    private ImageView mCouponRadioButton;
    private View mDiscountCouponLayout;
    private View mUsedCouponLayout;
    private TextView mUsedCouponTextView;

    // 결제 수단 선택
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
    private int mAnimationValue;
    private ValueAnimator mValueAnimator;
    private boolean mIsAnimationCancel;
    //
    private Rect mGuestFrameLayoutRect = new Rect();
    private int mScrollMoveHeight;

    public interface OnEventListener extends OnBaseEventListener
    {
        void selectTicketTime(String selectedTime);

        void plusTicketCount();

        void minusTicketCount();

        void startCreditCardManager(boolean isRegister);

        void changedPaymentType(PlacePaymentInformation.PaymentType paymentType);

        void doPayment();

        void showInputMobileNumberDialog(String mobileNumber);

        void showCallDialog();

        void onCouponClick(boolean isRadioLayout);
    }

    public GourmetPaymentLayout(Context context, OnEventListener mOnEventListener)
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
        mDailyToolbarLayout.initToolbar(null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_help, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(new View.OnClickListener()
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
        mTicketTypeTextView = (TextView) view.findViewById(R.id.ticketTypeTextView);
        mTicketDateTextView = (TextView) view.findViewById(R.id.ticketDateTextView);

        // 방문 시간
        mTicketTimeTextView = (TextView) view.findViewById(R.id.ticketTimeTextView);
        mTicketTimeTextView.setOnClickListener(this);

        View ticketTimeLayout = view.findViewById(R.id.ticketTimeLayout);
        ticketTimeLayout.setOnClickListener(this);

        mPlaceNameTextView = (TextView) view.findViewById(R.id.placeNameTextView);

        // 수량
        mTicketCountTextView = (TextView) view.findViewById(R.id.ticketCountTextView);

        mTicketCountMinusButton = view.findViewById(R.id.ticketCountMinus);
        mTicketCountPlusButton = view.findViewById(R.id.ticketCountPlus);

        setTicketCountMinusButtonEnabled(false);
        setTicketCountPlusButtonEnabled(true);

        mTicketCountMinusButton.setOnClickListener(this);
        mTicketCountPlusButton.setOnClickListener(this);

        // 예약자 정보
        initUserInformationLayout(view);
    }

    /**
     * 추후에 게스트입력 정보와 유저 입력 정보를 분리하도록 할때 User, Guest를 구분하도록 한다.
     *
     * @param view
     */
    private void initUserInformationLayout(View view)
    {
        mUserLayout = view.findViewById(R.id.userLayout);

        mUserNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        mUserPhoneTextView = (TextView) view.findViewById(R.id.userPhoneTextView);
        mUserEmailTextView = (TextView) view.findViewById(R.id.userEmailTextView);

        mGuestFrameLayout = view.findViewById(R.id.guestFrameLayout);
        mGuestLinearLayout = mGuestFrameLayout.findViewById(R.id.guestLinearLayout);

        mGuestNameEditText = (EditText) mGuestLinearLayout.findViewById(R.id.guestNameEditText);
        mGuestPhoneEditText = (EditText) mGuestLinearLayout.findViewById(R.id.guestPhoneEditText);
        mGuestEmailEditText = (EditText) mGuestLinearLayout.findViewById(R.id.guestEmailEditText);

        mGuestPhoneEditText.setCursorVisible(false);

        mGuestNameEditText.setOnFocusChangeListener(this);
        mGuestPhoneEditText.setOnFocusChangeListener(this);
        mGuestEmailEditText.setOnFocusChangeListener(this);

        View fakeMobileEditView = view.findViewById(R.id.fakeMobileEditView);
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
        mAmountNightsTextView = (TextView) view.findViewById(R.id.amountNightsTextView);
        mPriceTextView = (TextView) view.findViewById(R.id.originalPriceTextView);
        mDiscountPriceTextView = (TextView) view.findViewById(R.id.discountPriceTextView);
        mFinalPaymentTextView = (TextView) view.findViewById(R.id.totalPaymentPriceTextView);

        mDiscountPriceTextView.setText(Util.getPriceFormat(mContext, 0, false));

        initDiscountInformation(view);
    }

    private void initDiscountInformation(View view)
    {
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

            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });
        }
    }

    public void setTicketInformation(GourmetPaymentInformation gourmetPaymentInformation)
    {
        if (gourmetPaymentInformation == null)
        {
            return;
        }

        TicketInformation ticketInformation = gourmetPaymentInformation.getTicketInformation();

        if (ticketInformation == null)
        {
            return;
        }

        // 타입
        mTicketTypeTextView.setText(ticketInformation.name);

        // 날짜
        mTicketDateTextView.setText(gourmetPaymentInformation.dateTime);

        //
        mPlaceNameTextView.setText(gourmetPaymentInformation.getTicketInformation().placeName);

        if (gourmetPaymentInformation.ticketTime != 0)
        {
            mTicketTimeTextView.setText(DailyCalendar.format(gourmetPaymentInformation.ticketTime, "HH:mm", TimeZone.getTimeZone("GMT")));
        }

        // 수량
        mTicketCountTextView.setText(mContext.getString(R.string.label_booking_count, gourmetPaymentInformation.ticketCount));
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

            mGuestNameEditText.setHint(R.string.label_booking_input_name);
            mGuestPhoneEditText.setHint(R.string.label_booking_input_phone);
            mGuestEmailEditText.setHint(R.string.label_booking_input_email);
        } else
        {
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

    public void setPaymentInformation(GourmetPaymentInformation gourmetPaymentInformation, CreditCard creditCard)
    {
        if (gourmetPaymentInformation == null)
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

    public void setPaymentInformation(PlacePaymentInformation.DiscountType discountType, int originalPrice, int discountPrice, int payPrice)
    {
        // 결제금액
        mPriceTextView.setText(Util.getPriceFormat(mContext, originalPrice, false));

        switch (discountType)
        {
            case BONUS:
            {
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

    public void setTicketCount(int count)
    {
        if (mTicketCountTextView == null)
        {
            return;
        }

        mTicketCountTextView.setText(mContext.getString(R.string.label_booking_count, count));

        if (count > 1)
        {
            mAmountNightsTextView.setText(mContext.getString(R.string.label_booking_gourmet_count, count));
            mAmountNightsTextView.setVisibility(View.VISIBLE);
        } else
        {
            mAmountNightsTextView.setText(mContext.getString(R.string.act_booking_price));
            mAmountNightsTextView.setVisibility(View.GONE);
        }
    }

    public void setTicketCountMinusButtonEnabled(boolean isEnabled)
    {
        setButtonVisibility(mTicketCountMinusButton, isEnabled);
    }

    public void setTicketCountPlusButtonEnabled(boolean isEnabled)
    {
        setButtonVisibility(mTicketCountPlusButton, isEnabled);
    }

    //    public void setTicketTimeMinusButtonEnabled(boolean isEnabled)
    //    {
    //        setButtonVisibility(mTicketTimeMinusButton, isEnabled);
    //    }
    //
    //    public void setTicketTimePlusButtonEnabled(boolean isEnabled)
    //    {
    //        setButtonVisibility(mTicketTimePlusButton, isEnabled);
    //    }

    private void setButtonVisibility(View view, boolean isEnabled)
    {
        if (view == null)
        {
            return;
        }

        view.setEnabled(isEnabled);
    }

    public void setTicketTime(long time)
    {
        if (mTicketTimeTextView == null)
        {
            return;
        }

        mTicketTimeTextView.setText(DailyCalendar.format(time, "HH:mm", TimeZone.getTimeZone("GMT")));
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

        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.PAYMENT_TYPE_ITEM_CLICKED, paymentType.getName(), null);
    }

    public void scrollTop()
    {
        if (mScrollView == null)
        {
            return;
        }

        mScrollView.scrollTo(0, 0);
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
    public void onClick(View v)
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

            case R.id.ticketCountMinus:
                ((OnEventListener) mOnEventListener).minusTicketCount();
                break;

            case R.id.ticketCountPlus:
                ((OnEventListener) mOnEventListener).plusTicketCount();
                break;

            case R.id.ticketTimeLayout:
                mTicketTimeTextView.performClick();
                break;

            case R.id.ticketTimeTextView:
                ((OnEventListener) mOnEventListener).selectTicketTime(mTicketTimeTextView.getText().toString());
                break;

            case R.id.usedCouponLayout:
                ((OnEventListener) mOnEventListener).onCouponClick(false);
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
