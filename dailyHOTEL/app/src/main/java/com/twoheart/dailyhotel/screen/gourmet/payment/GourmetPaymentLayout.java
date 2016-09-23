package com.twoheart.dailyhotel.screen.gourmet.payment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckedTextView;
import android.widget.EditText;
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

public class GourmetPaymentLayout extends BaseLayout implements View.OnClickListener, View.OnFocusChangeListener
{
    private ScrollView mScrollView;
    //
    private View mBookingLayout;
    private TextView mTicketTypeTextView, mTicketDateTextView, mTicketCountTextView, mTicketTimeTextView;
    private TextView mBookingAmountTextView;
    private TextView mPlaceNameTextView;
    private TextView mUserNameTextView, mUserPhoneTextView, mUserEmailTextView;
    private EditText mGuestNameEditText, mGuestPhoneEditText, mGuestEmailEditText;
    //    private EditText mMemoEditText;
    private View mUserLayout;
    private View mGuestFrameLayout, mGuestLinearLayout;
    private CheckedTextView mGuestCheckBox;

    private TextView mPriceTextView, mFinalPaymentTextView;
    private View mTicketCountMinusButton, mTicketCountPlusButton;

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

    private TextView mCardManagerTextView;
    private TextView mGuidePaymentMemoView;
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

        void startCreditCardManager();

        void changedPaymentType(PlacePaymentInformation.PaymentType paymentType);

        void doPayment();

        void showInputMobileNumberDialog(String mobileNumber);

        void showCallDialog();
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
        EdgeEffectColor.setEdgeGlowColor(mScrollView, view.getResources().getColor(R.color.default_over_scroll_edge));

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

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_call, -1);
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

        mGuestCheckBox = (CheckedTextView) view.findViewById(R.id.guestCheckBox);
        mGuestCheckBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mGuestCheckBox.setChecked(!mGuestCheckBox.isChecked());

                onCheckedChanged(mGuestCheckBox, mGuestCheckBox.isChecked());
            }
        });
    }

    private void initBookingMemo(View view)
    {
        //        mMemoEditText = (EditText) view.findViewById(R.id.memoEditText);
    }

    private void initPaymentInformation(View view)
    {
        mBookingAmountTextView = (TextView) view.findViewById(R.id.bookingAmountTextView);
        mPriceTextView = (TextView) view.findViewById(R.id.originalPriceTextView);
        mFinalPaymentTextView = (TextView) view.findViewById(R.id.totalPaymentPriceTextView);
    }

    private void initPaymentTypeInformation(View view)
    {
        mSimpleCardLayout = view.findViewById(R.id.simpleCardLayout);
        mSimpleCardTextView = (TextView) mSimpleCardLayout.findViewById(R.id.simpleCardTextView);
        mDisableSimpleCardView = view.findViewById(R.id.disableSimpleCardView);

        View cardManagerLayout = view.findViewById(R.id.cardManagerLayout);
        mCardManagerTextView = (TextView) cardManagerLayout.findViewById(R.id.cardManagerTextView);

        mCardLayout = view.findViewById(R.id.cardLayout);
        mDisableCardView = mCardLayout.findViewById(R.id.disableCardView);

        mPhoneLayout = view.findViewById(R.id.phoneLayout);
        mDisablePhoneView = mPhoneLayout.findViewById(R.id.disablePhoneView);

        mTransferLayout = view.findViewById(R.id.transferLayout);
        mDisableTransferView = mTransferLayout.findViewById(R.id.disableTransferView);

        cardManagerLayout.setOnClickListener(this);
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
        mTicketDateTextView.setText(gourmetPaymentInformation.checkInTime);

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

    public void setPaymentInformation(GourmetPaymentInformation gourmetPaymentInformation, CreditCard selectedCreditCard)
    {
        if (gourmetPaymentInformation == null)
        {
            return;
        }

        setPaymentInformation(gourmetPaymentInformation.getPaymentToPay());

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

    public void setPaymentInformation(int price)
    {
        // 결제금액
        String priceFormat = Util.getPriceFormat(mContext, price, false);

        mPriceTextView.setText(priceFormat);
        mFinalPaymentTextView.setText(priceFormat);
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
            mBookingAmountTextView.setText(mContext.getString(R.string.act_booking_price)//
                + mContext.getString(R.string.label_booking_gourmet_count, count));
        } else
        {
            mBookingAmountTextView.setText(mContext.getString(R.string.act_booking_price));
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

    public void requestUserInformationFocus(Constants.UserInformationType type)
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

    public void onCheckedChanged(CheckedTextView checkedTextView, final boolean isChecked)
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
