package com.daily.dailyhotel.screen.booking.detail.wait;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.Booking;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;
import com.twoheart.dailyhotel.databinding.DialogConciergeDataBinding;

public class PaymentWaitView extends BaseDialogView<PaymentWaitView.OnEventListener, ActivityCopyDataBinding> implements PaymentWaitInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onConciergeClick();

        void onConciergeFaqClick(Booking.PlaceType placeType);

        void onConciergeHappyTalkClick(Booking.PlaceType placeType);

        void onConciergeCallClick(Booking.PlaceType placeType);
    }

    public PaymentWaitView(BaseActivity baseActivity, PaymentWaitView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityCopyDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
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

    private void initToolbar(ActivityCopyDataBinding viewDataBinding)
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
}
