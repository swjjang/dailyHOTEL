package com.daily.dailyhotel.screen.booking.detail.wait;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.entity.WaitingDeposit;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityPaymentWaitDataBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowPaymentWaitGuideDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.text.ParseException;

public class PaymentWaitView extends BaseDialogView<PaymentWaitView.OnEventListener, ActivityPaymentWaitDataBinding> implements PaymentWaitInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onConciergeClick();

        void onConciergeFaqClick(Booking.PlaceType placeType);

        void onConciergeHappyTalkClick(Booking.PlaceType placeType);

        void onConciergeCallClick(Booking.PlaceType placeType);

        void onClipAccountNumberClick(String accountNumber);
    }

    public PaymentWaitView(BaseActivity baseActivity, PaymentWaitView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityPaymentWaitDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollLayout, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.editLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                String accountNumber = (String) getViewDataBinding().accountNumberView.getTag();
                getEventListener().onClipAccountNumberClick(accountNumber);
            }
        });

    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivityPaymentWaitDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setTitleText(R.string.actionbar_title_payment_wait_activity);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());

        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.HELP, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onConciergeClick();
            }
        });
    }

    @Override
    public void showConciergeDialog(Booking.PlaceType placeType, Dialog.OnDismissListener listener)
    {
        DialogConciergeDataBinding dataBinding = DataBindingUtil.inflate( //
            LayoutInflater.from(getContext()), R.layout.dialog_concierge_data, null, false);

        dataBinding.contactUs01TextView.setText(R.string.frag_faqs);
        dataBinding.contactUs01TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_05_faq, 0, 0, 0);
        dataBinding.contactUs01Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeFaqClick(placeType);
            }
        });

        dataBinding.contactUs02Layout.setVisibility(View.GONE);

        dataBinding.kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeHappyTalkClick(placeType);
            }
        });

        dataBinding.callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onConciergeCallClick(placeType);
            }
        });

        dataBinding.closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, listener, true);
    }

    @Override
    public void setPlaceName(String placeName)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().paymentWaitHotelNameView.setText(placeName);
    }

    @Override
    public void setWaitingDeposit(WaitingDeposit waitingDeposit)
    {
        if (getViewDataBinding() == null || waitingDeposit == null)
        {
            return;
        }

        getViewDataBinding().accountNumberView.setText(waitingDeposit.bankName + ", " + waitingDeposit.accountNumber);
        getViewDataBinding().accountNumberView.setTag(waitingDeposit.accountNumber);

        getViewDataBinding().accountHolderView.setText(waitingDeposit.accountHolder);

        // 입금기한
        String validToDate = null;
        try
        {
            validToDate = DailyCalendar.convertDateFormatString(waitingDeposit.expiredAt, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일 HH시 mm분 까지");
        } catch (ParseException e)
        {
            ExLog.d("expiredAt error : " + e.getMessage());
        }
        getViewDataBinding().waitingDeadlineView.setText(validToDate);

        // 결재 금액 정보
        getViewDataBinding().priceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), waitingDeposit.totalPrice, false));

        if (waitingDeposit.bonusAmount > 0)
        {
            getViewDataBinding().bonusLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().bonusTextView.setText("- " + DailyTextUtils.getPriceFormat(getContext(), waitingDeposit.bonusAmount, false));
        } else
        {
            getViewDataBinding().bonusLayout.setVisibility(View.GONE);
        }

        if (waitingDeposit.couponAmount > 0)
        {
            getViewDataBinding().couponLayout.setVisibility(View.VISIBLE);
            getViewDataBinding().couponTextView.setText("- " + DailyTextUtils.getPriceFormat(getContext(), waitingDeposit.couponAmount, false));
        } else
        {
            getViewDataBinding().couponLayout.setVisibility(View.GONE);
        }

        getViewDataBinding().totalPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), waitingDeposit.depositWaitingAmount, false));

        // 확인 사항
        if (waitingDeposit.getMessage1List() != null)
        {
            String[] messages1 = waitingDeposit.getMessage1List().toArray(new String[waitingDeposit.getMessage1List().size()]);
            setGuideText(messages1, R.color.default_text_c4d4d4d);
        }

        if (waitingDeposit.getMessage2List() != null)
        {
            String[] messages2 = waitingDeposit.getMessage2List().toArray(new String[waitingDeposit.getMessage2List().size()]);
            setGuideText(messages2, R.color.default_text_c2c8de6);
        }
    }

    private void setGuideText(String[] guides, int colorResourceId)
    {
        if (getViewDataBinding() == null || getContext() == null || guides == null)
        {
            return;
        }

        for (String guide : guides)
        {
            ListRowPaymentWaitGuideDataBinding dataBinding = DataBindingUtil.inflate( //
                LayoutInflater.from(getContext()), R.layout.list_row_payment_wait_guide_data //
                , getViewDataBinding().guide1Layout, false);

            String guideText = guide.replace("\n", " ").trim();

            if (guideText.endsWith(".") == false)
            {
                guideText += ".";
            }

            dataBinding.textView.setText(guideText);
            dataBinding.textView.setTextColor(getColor(colorResourceId));

            getViewDataBinding().guide1Layout.addView(dataBinding.getRoot());
        }
    }
}