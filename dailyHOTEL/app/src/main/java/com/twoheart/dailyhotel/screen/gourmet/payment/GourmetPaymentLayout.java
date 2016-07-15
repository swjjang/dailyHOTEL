package com.twoheart.dailyhotel.screen.gourmet.payment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.TimeZone;

public class GourmetPaymentLayout extends BaseLayout implements View.OnClickListener
{
    private TextView mTicketTypeTextView, mTicketDateTextView, mTicketCountTextView, mTicketTimeTextView;
    private EditText mUserNameEditText, mUserPhoneEditText, mUserEmailEditText;
    private EditText mMemoEditText;
    private Drawable[] mEditTextBackgrounds;
    private View mFakeMobileEditView;
    private ScrollView mScrollLayout;

    private TextView mPriceTextView, mFinalPaymentTextView;
    private View mTicketCountMinusButton, mTicketCountPlusButton;

    private DailyToolbarLayout mDailyToolbarLayout;

    // 결제 수단 선택
    private View mSimpleCardLayout;
    private ImageView mSimpleCardImageView;
    private TextView mSimpleCardTextView;
    private View mCardLayout;
    private View mPhoneLayout;
    private View mTransferLayout;

    private View mCardManagerLayout;
    private TextView mCardManagerTextView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void selectTicketTime(String selectedTime);

        void plusTicketCount();

        void minusTicketCount();

        void editUserInformation();

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

        initTicketInformationLayout(view);
        initUserInformationLayout(view);
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

    private void initTicketInformationLayout(View view)
    {
        mTicketTypeTextView = (TextView) view.findViewById(R.id.ticketTypeTextView);
        mTicketDateTextView = (TextView) view.findViewById(R.id.ticketDateTextView);

        // 방문 시간
        mTicketTimeTextView = (TextView) view.findViewById(R.id.ticketTimeTextView);
        mTicketTimeTextView.setText(R.string.label_booking_select);
        mTicketTimeTextView.setOnClickListener(this);

        View ticketTimeTab = view.findViewById(R.id.ticketTimeTab);
        ticketTimeTab.setOnClickListener(this);

        // 수량
        mTicketCountTextView = (TextView) view.findViewById(R.id.ticketCountTextView);

        mTicketCountMinusButton = view.findViewById(R.id.ticketCountMinus);
        mTicketCountPlusButton = view.findViewById(R.id.ticketCountPlus);

        setTicketCountMinusButtonEnabled(false);
        setTicketCountPlusButtonEnabled(true);

        mTicketCountMinusButton.setOnClickListener(this);
        mTicketCountPlusButton.setOnClickListener(this);
    }

    private void initUserInformationLayout(View view)
    {
        mFakeMobileEditView = view.findViewById(R.id.fakeMobileEditView);

        // 예약자
        mUserNameEditText = (EditText) view.findViewById(R.id.userNameEditText);

        // 연락처
        mUserPhoneEditText = (EditText) view.findViewById(R.id.userPhoneEditText);

        // 이메일
        mUserEmailEditText = (EditText) view.findViewById(R.id.userEmailEditText);

        mEditTextBackgrounds = new Drawable[3];
        mEditTextBackgrounds[0] = mUserNameEditText.getBackground();
        mEditTextBackgrounds[1] = mUserPhoneEditText.getBackground();
        mEditTextBackgrounds[2] = mUserEmailEditText.getBackground();

        mUserNameEditText.setBackgroundResource(0);
        mUserPhoneEditText.setBackgroundResource(0);
        mUserEmailEditText.setBackgroundResource(0);

        mUserNameEditText.setEnabled(false);
        mUserPhoneEditText.setEnabled(false);
        mUserEmailEditText.setEnabled(false);

        mUserPhoneEditText.setCursorVisible(false);

        // 수정
        View editLayout = view.findViewById(R.id.editLinearLayout);
        editLayout.setOnClickListener(this);
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
        mSimpleCardImageView = (ImageView) mSimpleCardLayout.findViewById(R.id.simpleCardImageView);
        mSimpleCardTextView = (TextView) mSimpleCardLayout.findViewById(R.id.simpleCardTextView);
        mCardManagerLayout = view.findViewById(R.id.cardManagerLayout);
        mCardManagerTextView = (TextView) mCardManagerLayout.findViewById(R.id.cardManagerTextView);

        mCardLayout = view.findViewById(R.id.cardLayout);
        mPhoneLayout = view.findViewById(R.id.phoneLayout);
        mTransferLayout = view.findViewById(R.id.transferLayout);

        mCardManagerLayout.setOnClickListener(this);
        mSimpleCardLayout.setOnClickListener(this);
        mCardLayout.setOnClickListener(this);
        mPhoneLayout.setOnClickListener(this);
        mTransferLayout.setOnClickListener(this);
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
            mSimpleCardImageView.setImageResource(R.drawable.selector_simplecard_plus_button);
            mSimpleCardTextView.setText(R.string.label_booking_easypayment);
        } else
        {
            mCardManagerTextView.setText(R.string.label_manager);
            mSimpleCardImageView.setImageResource(R.drawable.selector_simplecard_button);
            mSimpleCardTextView.setText(String.format("%s %s", selectedCreditCard.name.replace("카드", ""), selectedCreditCard.number));
        }
    }

    public void setPaymentInformation(GourmetPaymentInformation gourmetPaymentInformation)
    {
        if (gourmetPaymentInformation == null)
        {
            return;
        }

        // 결제금액
        String price = Util.getPriceFormat(mContext, gourmetPaymentInformation.getPaymentToPay(), false);

        mPriceTextView.setText(price);
        mFinalPaymentTextView.setText(price);
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

    public void enabledEditUserInformation()
    {
        if (mUserNameEditText != null && mUserNameEditText.isEnabled() == false)
        {
            mUserNameEditText.setEnabled(true);

            // 회원 가입시 이름 필터 적용.
            StringFilter stringFilter = new StringFilter(mContext);
            InputFilter[] allowAlphanumericHangul = new InputFilter[2];
            allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;
            allowAlphanumericHangul[1] = new InputFilter.LengthFilter(20);

            mUserNameEditText.setFilters(allowAlphanumericHangul);
            mUserNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);

            if (Util.isOverAPI16() == true)
            {
                mUserNameEditText.setBackground(mEditTextBackgrounds[0]);
            } else
            {
                mUserNameEditText.setBackgroundDrawable(mEditTextBackgrounds[0]);
            }
        }

        if (mUserPhoneEditText != null && mUserPhoneEditText.isEnabled() == false)
        {
            mUserPhoneEditText.setEnabled(true);

            if (Util.isOverAPI16() == true)
            {
                mUserPhoneEditText.setBackground(mEditTextBackgrounds[1]);
            } else
            {
                mUserPhoneEditText.setBackgroundDrawable(mEditTextBackgrounds[1]);
            }

            mUserPhoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (hasFocus == true)
                    {
                        ((OnEventListener) mOnEventListener).showInputMobileNumberDialog(mUserPhoneEditText.getText().toString());
                    } else
                    {
                        mUserPhoneEditText.setSelected(false);
                    }
                }
            });

            mFakeMobileEditView.setFocusable(true);
            mFakeMobileEditView.setOnClickListener(this);
        }

        if (mUserEmailEditText != null && mUserEmailEditText.isEnabled() == false)
        {
            mUserEmailEditText.setEnabled(true);

            if (Util.isOverAPI16() == true)
            {
                mUserEmailEditText.setBackground(mEditTextBackgrounds[2]);
            } else
            {
                mUserEmailEditText.setBackgroundDrawable(mEditTextBackgrounds[2]);
            }

            mUserEmailEditText.setOnEditorActionListener(new OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
                {
                    if (actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        textView.clearFocus();

                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        return true;
                    } else
                    {
                        return false;
                    }
                }
            });
        }
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

            case R.id.editLinearLayout:
                ((OnEventListener) mOnEventListener).editUserInformation();
                break;

            case R.id.ticketCountMinus:
                ((OnEventListener) mOnEventListener).minusTicketCount();
                break;

            case R.id.ticketCountPlus:
                ((OnEventListener) mOnEventListener).plusTicketCount();
                break;

            case R.id.ticketTimeTab:
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
