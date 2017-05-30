package com.daily.dailyhotel.screen.stay.outbound.payment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Card;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayOutboundPayment;
import com.daily.dailyhotel.entity.UserInformation;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPaymentDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentBookingDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentButtonDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentDiscountDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentPayDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundPaymentRefundDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;
import java.util.Locale;

public class StayOutboundPaymentView extends BaseView<StayOutboundPaymentView.OnEventListener, ActivityStayOutboundPaymentDataBinding>//
    implements StayOutboundPaymentInterface, View.OnClickListener
{
    private DailyToolbarLayout mDailyToolbarLayout;

    private LayoutStayOutboundPaymentBookingDataBinding mBookingDataBinding;
    private LayoutStayOutboundPaymentDiscountDataBinding mDiscountDataBinding;
    private LayoutStayOutboundPaymentPayDataBinding mPayDataBinding;
    private LayoutStayOutboundPaymentRefundDataBinding mRefundDataBinding;
    private LayoutStayOutboundPaymentButtonDataBinding mButtonDataBinding;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCallClick();
    }

    public StayOutboundPaymentView(BaseActivity baseActivity, StayOutboundPaymentView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundPaymentDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollView, getColor(R.color.default_over_scroll_edge));

        setBookingLayout(getContext(), viewDataBinding.scrollLayout);
        setDiscountLayout(getContext(), viewDataBinding.scrollLayout);
        setPaymentLayout(getContext(), viewDataBinding.scrollLayout);
        setRefundLayout(getContext(), viewDataBinding.scrollLayout);
        setPayButtonLayout(getContext(), viewDataBinding.scrollLayout);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }

    @Override
    public void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType)
    {
        if (getViewDataBinding() == null || mBookingDataBinding == null)
        {
            return;
        }

        mBookingDataBinding.checkInDayTextView.setText(checkInDate);
        mBookingDataBinding.checkOutDayTextView.setText(checkOutDate);
        mBookingDataBinding.nightsTextView.setText(getString(R.string.label_nights, nights));

        mBookingDataBinding.placeNameTextView.setText(stayName);
        mBookingDataBinding.roomTypeTextView.setText(roomType);
    }

    @Override
    public void setUserInformation(UserInformation userInformation, String firstName, String lastName, String phone, String email)
    {
        if (getViewDataBinding() == null || mBookingDataBinding == null)
        {
            return;
        }

        mBookingDataBinding.guestLastNameEditText.setText(lastName);
        mBookingDataBinding.guestFirstNameEditText.setText(firstName);

        if (DailyTextUtils.isTextEmpty(phone) == true)
        {
            mBookingDataBinding.guestPhoneEditText.setText(userInformation.phone);
        } else
        {
            mBookingDataBinding.guestPhoneEditText.setText(phone);
        }

        if (DailyTextUtils.isTextEmpty(email) == true)
        {
            mBookingDataBinding.guestEmailEditText.setText(userInformation.email);
        } else
        {
            mBookingDataBinding.guestEmailEditText.setText(email);
        }
    }

    @Override
    public void setPeople(People people)
    {
        if (getViewDataBinding() == null || mBookingDataBinding == null || people == null)
        {
            return;
        }

        mBookingDataBinding.guestPeopleEditText.setText(people.toString(getContext()));
    }

    @Override
    public void setStayOutboundPayment(int bonus, int nights, int totalPrice, int discountPrice, int paymentPrice, double taxPrice)
    {
        if (getViewDataBinding() == null || mDiscountDataBinding == null || nights == 0)
        {
            return;
        }

        setBonus(bonus);

        if (nights > 1)
        {
            mDiscountDataBinding.amountNightsTextView.setText(getString(R.string.label_booking_hotel_nights, nights));
            mDiscountDataBinding.amountNightsTextView.setVisibility(View.VISIBLE);
        } else
        {
            mDiscountDataBinding.amountNightsTextView.setVisibility(View.GONE);
        }

        mDiscountDataBinding.originalPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), totalPrice, false));
        mDiscountDataBinding.discountPriceTextView.setText("-" + DailyTextUtils.getPriceFormat(getContext(), discountPrice, false));
        mDiscountDataBinding.totalPaymentPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), paymentPrice, false));

        if (taxPrice > 0)
        {
            mDiscountDataBinding.additionalTaxMemoTextView.setVisibility(View.VISIBLE);
            mDiscountDataBinding.additionalTaxLayout.setVisibility(View.VISIBLE);
            mDiscountDataBinding.taxPriceTextView.setText(DailyTextUtils.getGlobalCurrency(Locale.US, taxPrice));
        } else
        {
            mDiscountDataBinding.additionalTaxMemoTextView.setVisibility(View.GONE);
            mDiscountDataBinding.additionalTaxLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setSimpleCard(Card card)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        if (card == null)
        {
            mPayDataBinding.cardManagerLayout.setVisibility(View.GONE);
            mPayDataBinding.emptySimpleCardLayout.setVisibility(View.VISIBLE);
            mPayDataBinding.selectedSimpleCardLayout.setVisibility(View.GONE);
        } else
        {
            mPayDataBinding.cardManagerLayout.setVisibility(View.VISIBLE);
            mPayDataBinding.emptySimpleCardLayout.setVisibility(View.GONE);
            mPayDataBinding.selectedSimpleCardLayout.setVisibility(View.VISIBLE);

            mPayDataBinding.logoTextView.setText(card.name);
            mPayDataBinding.simpleCardTextView.setText(card.number);
        }
    }

    @Override
    public void setRefundDescriptionList(List<String> refundDescriptionList)
    {
        if (refundDescriptionList == null || refundDescriptionList.size() == 0)
        {
            mRefundDataBinding.getRoot().setVisibility(View.GONE);
        } else
        {
            mRefundDataBinding.getRoot().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setMemoPaymentType(String memo)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(memo) == true)
        {
            mPayDataBinding.guidePaymentMemoTextView.setVisibility(View.GONE);
        } else
        {
            mPayDataBinding.guidePaymentMemoTextView.setVisibility(View.VISIBLE);
            mPayDataBinding.guidePaymentMemoTextView.setText(memo);
        }
    }


    @Override
    public void setPaymentTypeEnabled(StayOutboundPayment.PaymentType paymentType, boolean enabled)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        switch (paymentType)
        {
            case EASY_CARD:
                setPaymentTypeEnabled(mPayDataBinding.disableSimpleCardView, enabled);
                break;

            case CARD:
                setPaymentTypeEnabled(mPayDataBinding.disableCardView, enabled);
                break;

            case PHONE_PAY:
                setPaymentTypeEnabled(mPayDataBinding.disablePhoneView, enabled);
                break;
        }
    }

    @Override
    public void setPaymentType(StayOutboundPayment.PaymentType paymentType)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return;
        }

        if (paymentType == null)
        {
            ((View) mPayDataBinding.simpleCardLayout.getParent()).setSelected(false);
            mPayDataBinding.simpleCardLayout.setSelected(false);
            mPayDataBinding.cardLayout.setSelected(false);
            mPayDataBinding.phoneLayout.setSelected(false);
            return;
        }

        switch (paymentType)
        {
            case EASY_CARD:
            {
                ((View) mPayDataBinding.simpleCardLayout.getParent()).setSelected(true);
                mPayDataBinding.simpleCardLayout.setSelected(true);
                mPayDataBinding.cardLayout.setSelected(false);
                mPayDataBinding.phoneLayout.setSelected(false);
                break;
            }

            case CARD:
            {
                ((View) mPayDataBinding.simpleCardLayout.getParent()).setSelected(false);
                mPayDataBinding.simpleCardLayout.setSelected(false);
                mPayDataBinding.cardLayout.setSelected(true);
                mPayDataBinding.phoneLayout.setSelected(false);
                break;
            }

            case PHONE_PAY:
            {
                ((View) mPayDataBinding.simpleCardLayout.getParent()).setSelected(false);
                mPayDataBinding.simpleCardLayout.setSelected(false);
                mPayDataBinding.cardLayout.setSelected(false);
                mPayDataBinding.phoneLayout.setSelected(true);
                break;
            }
        }
    }


    @Override
    public void onClick(View v)
    {

    }


    private void initToolbar(ActivityStayOutboundPaymentDataBinding viewDataBinding)
    {
        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar.findViewById(R.id.toolbar));
        mDailyToolbarLayout.initToolbar(null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_call, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onCallClick();
            }
        });
    }

    private void setBookingLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mBookingDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_booking_data, viewGroup, true);


    }

    private void setDiscountLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mDiscountDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_discount_data, viewGroup, true);


    }

    private void setPaymentLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mPayDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_pay_data, viewGroup, true);


    }

    private void setRefundLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mRefundDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_refund_data, viewGroup, true);


    }

    private void setPayButtonLayout(Context context, ViewGroup viewGroup)
    {
        if (context == null)
        {
            return;
        }

        mButtonDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context)//
            , R.layout.layout_stay_outbound_payment_button_data, viewGroup, true);


    }

    private void setBonus(int bonus)
    {
        if (getViewDataBinding() == null || mDiscountDataBinding == null)
        {
            return;
        }

        String priceFormat = DailyTextUtils.getPriceFormat(getContext(), bonus, false);
        String text = getString(R.string.label_booking_own_bonus, priceFormat);

        if (bonus > 0)
        {
            int startIndex = text.indexOf(priceFormat);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);

            spannableStringBuilder.setSpan(new ForegroundColorSpan(getColor(R.color.default_text_c323232)), //
                startIndex, text.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mDiscountDataBinding.bonusTextView.setText(spannableStringBuilder);
        } else
        {
            mDiscountDataBinding.bonusTextView.setText(text);
        }
    }

    public boolean isPaymentTypeEnabled(StayOutboundPayment.PaymentType paymentType)
    {
        if (getViewDataBinding() == null || mPayDataBinding == null)
        {
            return false;
        }

        switch (paymentType)
        {
            case EASY_CARD:
                return mPayDataBinding.disableSimpleCardView.getVisibility() == View.VISIBLE;

            case CARD:
                return mPayDataBinding.disableCardView.getVisibility() == View.VISIBLE;

            case PHONE_PAY:
                return mPayDataBinding.disablePhoneView.getVisibility() == View.VISIBLE;
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
}
