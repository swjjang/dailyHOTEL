package com.twoheart.dailyhotel.screen.gourmet.payment;

import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
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
    private View mBookingLayout;
    private TextView mTicketTypeTextView, mTicketDateTextView, mTicketCountTextView, mTicketTimeTextView;
    private TextView mBookingAmountTextView;
    private TextView mPlaceNameTextView;
    private EditText mUserNameEditText, mUserPhoneEditText, mUserEmailEditText;
    private EditText mMemoEditText;
    private ScrollView mScrollLayout;

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

    public interface OnEventListener extends OnBaseEventListener
    {
        void selectTicketTime(String selectedTime);

        void editUserInformation();

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

        mScrollLayout = (ScrollView) view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(mScrollLayout, view.getResources().getColor(R.color.default_over_scroll_edge));

        mBookingLayout = mScrollLayout.findViewById(R.id.bookingLayout);

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
        mUserNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
        mUserPhoneEditText = (EditText) view.findViewById(R.id.userPhoneEditText);
        mUserEmailEditText = (EditText) view.findViewById(R.id.userEmailEditText);

        mUserPhoneEditText.setCursorVisible(false);

        mUserNameEditText.setOnFocusChangeListener(this);
        mUserPhoneEditText.setOnFocusChangeListener(this);
        mUserEmailEditText.setOnFocusChangeListener(this);

        View fakeMobileEditView = view.findViewById(R.id.fakeMobileEditView);
        fakeMobileEditView.setFocusable(true);
        fakeMobileEditView.setOnClickListener(this);
    }

    private void initBookingMemo(View view)
    {
        mMemoEditText = (EditText) view.findViewById(R.id.memoEditText);
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

    protected void setUserInformation(GourmetPaymentInformation gourmetPaymentInformation)
    {
        if (gourmetPaymentInformation == null)
        {
            return;
        }

        Guest guest = gourmetPaymentInformation.getGuest();

        if (guest == null)
        {
            return;
        }

        // 예약자
        mUserNameEditText.setText(guest.name);

        // 연락처
        mUserPhoneEditText.setText(Util.addHippenMobileNumber(mContext, guest.phone));

        // 이메일
        mUserEmailEditText.setText(guest.email);

        // 사용자 요청사항
        mMemoEditText.setText(guest.message);
    }

    public void setUserPhoneInformation(String mobileNumber)
    {
        if (mUserPhoneEditText == null)
        {
            return;
        }

        mUserPhoneEditText.setText(Util.addHippenMobileNumber(mContext, mobileNumber));
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
        Guest guest = new Guest();

        guest.name = mUserNameEditText.getText().toString().trim();
        guest.phone = mUserPhoneEditText.getText().toString().trim();
        guest.email = mUserEmailEditText.getText().toString().trim();
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
                mUserNameEditText.requestFocus();
                break;

            case PHONE:
                mUserPhoneEditText.requestFocus();
                break;

            case EMAIL:
                mUserEmailEditText.requestFocus();
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
        if (mScrollLayout == null)
        {
            return;
        }

        mScrollLayout.scrollTo(0, 0);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        ((OnEventListener) mOnEventListener).editUserInformation();

        switch (v.getId())
        {
            case R.id.userPhoneEditText:
                if (hasFocus == true)
                {
                    ((OnEventListener) mOnEventListener).showInputMobileNumberDialog(mUserPhoneEditText.getText().toString());
                } else
                {
                    mUserPhoneEditText.setSelected(false);
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
                if (mUserPhoneEditText.isSelected() == true)
                {
                    ((OnEventListener) mOnEventListener).showInputMobileNumberDialog(mUserPhoneEditText.getText().toString());
                } else
                {
                    mUserPhoneEditText.requestFocus();
                    mUserPhoneEditText.setSelected(true);
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
}
