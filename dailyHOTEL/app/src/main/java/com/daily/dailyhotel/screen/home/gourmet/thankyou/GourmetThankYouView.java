package com.daily.dailyhotel.screen.home.gourmet.thankyou;

import android.animation.Animator;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Pair;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.animation.ThankYouScreenAnimator;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPaymentThankYouDataBinding;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;
import java.util.List;

public class GourmetThankYouView extends BaseDialogView<GourmetThankYouView.OnEventListener, ActivityStayOutboundPaymentThankYouDataBinding> implements GourmetThankYouInterface, View.OnClickListener
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
    protected void setContentView(final ActivityStayOutboundPaymentThankYouDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.thankYouInformationView.setReservationTitle(R.string.label_booking_ticket_info);
        viewDataBinding.closeView.setOnClickListener(this);
        viewDataBinding.confirmView.setOnClickListener(this);

        viewDataBinding.thankYouInformationView.setVisibility(View.INVISIBLE);
        viewDataBinding.checkImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setToolbarTitle(String title)
    {
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
    public void setBooking(String visitDate, String visitTime, String gourmetName, String productType, int productCount)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().thankYouInformationView.setDate1Text(getString(R.string.label_visit_day), visitDate);
        getViewDataBinding().thankYouInformationView.setDate2Text(getString(R.string.label_booking_select_ticket_time), visitTime);
        getViewDataBinding().thankYouInformationView.setCenterNightsVisible(false);

        List<Pair<CharSequence, CharSequence>> reservationInformationList = new ArrayList<>();

        reservationInformationList.add(new Pair(getString(R.string.label_booking_place_name), gourmetName));
        reservationInformationList.add(new Pair(getString(R.string.frag_booking_tab_ticket_type), productType));

        if (productCount > 0)
        {
            reservationInformationList.add(new Pair(getString(R.string.label_product_count), productCount));
        }

        getViewDataBinding().thankYouInformationView.setReservationInformation(reservationInformationList);
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
            case R.id.closeView:
                getEventListener().onBackClick();
                break;

            case R.id.confirmView:
                getEventListener().onConfirmClick();
                break;
        }
    }
}
