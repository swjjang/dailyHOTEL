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
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.TimeZone;

public class GourmetPaymentLayout extends BaseLayout implements View.OnClickListener, View.OnFocusChangeListener
{
    private TextView mTicketTypeTextView, mTicketDateTextView, mTicketCountTextView, mTicketTimeTextView;
    private TextView mPlaceNameTextView;
    private EditText mUserNameEditText, mUserPhoneEditText, mUserEmailEditText;
    private EditText mMemoEditText;
    private ScrollView mScrollLayout;
    private View mBookingLayout;

    private TextView mPriceTextView, mFinalPaymentTextView;
    private View mTicketCountMinusButton, mTicketCountPlusButton;

    private DailyToolbarLayout mDailyToolbarLayout;

    // 결제 수단 선택
    private View mSimpleCardLayout;
    private TextView mSimpleCardTextView;
    private View mCardLayout;
    private View mPhoneLayout;
    private View mTransferLayout;

    private TextView mCardManagerTextView;

    private View mDisableSimpleCardView;
    private View mDisableCardView;
    private View mDisablePhoneView;
    private View mDisableTransferView;

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

    private void initUserInformationLayout(View view)
    {
        View fakeMobileEditView = view.findViewById(R.id.fakeMobileEditView);

        // 예약자
        mUserNameEditText = (EditText) view.findViewById(R.id.userNameEditText);

        // 연락처
        mUserPhoneEditText = (EditText) view.findViewById(R.id.userPhoneEditText);

        // 이메일
        mUserEmailEditText = (EditText) view.findViewById(R.id.userEmailEditText);

        mUserPhoneEditText.setCursorVisible(false);

        mUserNameEditText.setOnFocusChangeListener(this);
        mUserPhoneEditText.setOnFocusChangeListener(this);
        mUserEmailEditText.setOnFocusChangeListener(this);

        fakeMobileEditView.setFocusable(true);
        fakeMobileEditView.setOnClickListener(this);
    }

    private void initBookingMemo(View view)
    {
        mMemoEditText = (EditText) view.findViewById(R.id.memoEditText);
    }

    private void initPaymentInformation(View view)
    {
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

        boolean isSimpleCardPaymentEnabled = DailyPreference.getInstance(mContext).isGourmetSimpleCardPaymentEnabled();
        boolean isCardPaymentEnabled = DailyPreference.getInstance(mContext).isGourmetCardPaymentEnabled();
        boolean isPhonePaymentEnabled = DailyPreference.getInstance(mContext).isGourmetPhonePaymentEnabled();
        boolean isVirtualPaymentEnabled = DailyPreference.getInstance(mContext).isGourmetVirtualPaymentEnabled();

        TextView guidePaymentMemoView = (TextView) view.findViewById(R.id.guidePaymentMemoView);
        StringBuilder guideMemo = new StringBuilder();

        if (isSimpleCardPaymentEnabled == false)
        {
            guideMemo.append(mContext.getString(R.string.label_simple_payment));
            guideMemo.append(", ");
        }

        if (isCardPaymentEnabled == false)
        {
            guideMemo.append(mContext.getString(R.string.label_card_payment));
            guideMemo.append(", ");
        }

        if (isPhonePaymentEnabled == false)
        {
            guideMemo.append(mContext.getString(R.string.act_booking_pay_mobile));
            guideMemo.append(", ");
        }

        if (isVirtualPaymentEnabled == false)
        {
            guideMemo.append(mContext.getString(R.string.act_booking_pay_account));
            guideMemo.append(", ");
        }

        if (guideMemo.length() > 0)
        {
            guideMemo.setLength(guideMemo.length() - 2);

            guidePaymentMemoView.setText(mContext.getString(R.string.message_dont_support_payment_type, guideMemo.toString()));
            guidePaymentMemoView.setVisibility(View.VISIBLE);
        } else
        {
            guidePaymentMemoView.setVisibility(View.GONE);
        }

        setPaymentTypeEnabled(mDisableSimpleCardView, DailyPreference.getInstance(mContext).isGourmetSimpleCardPaymentEnabled());
        setPaymentTypeEnabled(mDisableCardView, DailyPreference.getInstance(mContext).isGourmetCardPaymentEnabled());
        setPaymentTypeEnabled(mDisablePhoneView, DailyPreference.getInstance(mContext).isGourmetPhonePaymentEnabled());
        setPaymentTypeEnabled(mDisableTransferView, DailyPreference.getInstance(mContext).isGourmetVirtualPaymentEnabled());

        if (isSimpleCardPaymentEnabled == true)
        {
            ((OnEventListener) mOnEventListener).changedPaymentType(PlacePaymentInformation.PaymentType.EASY_CARD);
        } else if (isCardPaymentEnabled == true)
        {
            ((OnEventListener) mOnEventListener).changedPaymentType(PlacePaymentInformation.PaymentType.CARD);
        } else if (isPhonePaymentEnabled == true)
        {
            ((OnEventListener) mOnEventListener).changedPaymentType(PlacePaymentInformation.PaymentType.PHONE_PAY);
        } else if (isVirtualPaymentEnabled == true)
        {
            ((OnEventListener) mOnEventListener).changedPaymentType(PlacePaymentInformation.PaymentType.VBANK);
        }
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
            //            Calendar calendarTime = DailyCalendar.getInstance();
            //            calendarTime.setTimeZone(TimeZone.getTimeZone("GMT"));
            //            calendarTime.setTimeInMillis(gourmetPaymentInformation.ticketTime);
            //
            //            SimpleDateFormat formatDay = new SimpleDateFormat("HH:mm", Locale.KOREA);
            //            formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));
            //
            //            // 방문시간
            //            mTicketTimeTextView.setText(formatDay.format(calendarTime.getTime()));
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

    /**
     * @param mobileNumber
     */
    public void setUserPhoneInformation(String mobileNumber)
    {
        mUserPhoneEditText.setText(Util.addHippenMobileNumber(mContext, mobileNumber));
    }

    public void setPaymentInformation(GourmetPaymentInformation gourmetPaymentInformation, CreditCard selectedCreditCard)
    {
        if (gourmetPaymentInformation == null)
        {
            return;
        }

        setPaymentInformation(gourmetPaymentInformation);

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

    public void setPaymentInformation(GourmetPaymentInformation gourmetPaymentInformation)
    {
        if (gourmetPaymentInformation == null)
        {
            return;
        }

        int payPrice = gourmetPaymentInformation.getPaymentToPay();

        // 결제금액
        String price = Util.getPriceFormat(mContext, payPrice, false);

        mPriceTextView.setText(price);
        mFinalPaymentTextView.setText(price);

        // 30만원 한도 핸드폰 결제 금지
        if (payPrice >= 300000)
        {
            setPaymentTypeEnabled(mDisablePhoneView, false);
        } else
        {
            if (DailyPreference.getInstance(mContext).isGourmetPhonePaymentEnabled() == true)
            {
                setPaymentTypeEnabled(mDisablePhoneView, true);
            }
        }
    }

    public void setTicketCount(int count)
    {
        if (mTicketCountTextView == null)
        {
            return;
        }

        mTicketCountTextView.setText(mContext.getString(R.string.label_booking_count, count));
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

        //        Calendar calendarTime = DailyCalendar.getInstance();
        //        calendarTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        //        calendarTime.setTimeInMillis(time);
        //
        //        SimpleDateFormat formatDay = new SimpleDateFormat("HH:mm", Locale.KOREA);
        //        formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));
        //
        //        mTicketTimeTextView.setText(formatDay.format(calendarTime.getTime()));
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
