package com.daily.dailyhotel.screen.home.gourmet.thankyou;

import android.animation.Animator;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.animation.ThankYouScreenAnimator;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetPaymentThankYouDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

public class GourmetThankYouView extends BaseDialogView<GourmetThankYouView.OnEventListener, ActivityGourmetPaymentThankYouDataBinding> implements GourmetThankYouInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onConfirmClick();
    }

    public GourmetThankYouView(BaseActivity baseActivity, GourmetThankYouView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetPaymentThankYouDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.thankYouInformationView.setReservationTitle(R.string.label_booking_ticket_info);
        viewDataBinding.confirmView.setOnClickListener(this);

        viewDataBinding.thankYouInformationView.setVisibility(View.INVISIBLE);
        viewDataBinding.checkImageView.setVisibility(View.INVISIBLE);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.scrollLayout, getColor(R.color.transparent));
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

    @Override
    public void setUserName(String userName)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(userName) == false)
        {
            SpannableString spannableString = new SpannableString(getString(R.string.message_completed_payment_format, userName));
            spannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                0, userName.length(),//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getViewDataBinding().thankYouInformationView.setMessageText(spannableString);
        } else
        {
            getViewDataBinding().thankYouInformationView.setMessageText(R.string.message_completed_payment_default);
        }
    }

    @Override
    public void setImageUrl(String imageUrl)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        Util.requestImageResize(getContext(), getViewDataBinding().simpleDraweeView, imageUrl);
    }

    @Override
    public void setBooking(String visitDate, String visitTime, GourmetCart gourmetCart)
    {
        if (getViewDataBinding() == null || gourmetCart == null)
        {
            return;
        }

        getViewDataBinding().thankYouInformationView.setDate1Text(getString(R.string.label_visit_day), visitDate);
        getViewDataBinding().thankYouInformationView.setDate2Text(getString(R.string.label_booking_select_ticket_time), visitTime);
        getViewDataBinding().thankYouInformationView.setCenterNightsVisible(false);

        getViewDataBinding().thankYouInformationView.removeAllReservationInformation();
        getViewDataBinding().thankYouInformationView.addReservationInformation(getString(R.string.label_booking_place_name), gourmetCart.gourmetName);
        getViewDataBinding().thankYouInformationView.addReservationInformation(getString(R.string.frag_booking_tab_ticket_type), null);

//        if (productCount > 0)
//        {
//            getViewDataBinding().thankYouInformationView.addReservationInformation(getString(R.string.label_product_count), getString(R.string.label_booking_count, productCount));
//        }

        getViewDataBinding().thankYouInformationView.setProductVisible(false);
    }

    @Override
    public void startAnimation(Animator.AnimatorListener listener)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        ThankYouScreenAnimator animator = new ThankYouScreenAnimator(getContext()//
            , getViewDataBinding().checkImageView, getViewDataBinding().thankYouInformationView);

        animator.setListener(listener);
        animator.start();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                getEventListener().onConfirmClick();
                break;
        }
    }

    private void initToolbar(ActivityGourmetPaymentThankYouDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackVisible(false);
        viewDataBinding.toolbarView.clearMenuItem();
        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.CLOSE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });
    }
}
