package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;

import android.animation.Animator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Pair;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.animation.StayThankYouScreenAnimator;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayPaymentThankYouDataBinding;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;
import java.util.List;

public class StayThankYouView extends BaseDialogView<StayThankYouView.OnEventListener, ActivityStayPaymentThankYouDataBinding> implements StayThankYouInterface, View.OnClickListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onConfirmClick();

        void onStampClick();
    }

    public StayThankYouView(BaseActivity baseActivity, StayThankYouView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayPaymentThankYouDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.thankYouInformationView.setReservationTitle(R.string.label_booking_room_info);
        viewDataBinding.closeView.setOnClickListener(this);
        viewDataBinding.confirmView.setOnClickListener(this);

        viewDataBinding.thankYouInformationView.setVisibility(View.INVISIBLE);
        viewDataBinding.checkImageView.setVisibility(View.INVISIBLE);
        viewDataBinding.stampLayout.setVisibility(View.GONE);
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
    public void setBooking(SpannableString checkInDate, SpannableString checkOutDate, int nights, String stayName, String roomType)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().thankYouInformationView.setDate1Text(getString(R.string.act_booking_chkin), checkInDate);
        getViewDataBinding().thankYouInformationView.setDate2Text(getString(R.string.act_booking_chkout), checkOutDate);

        getViewDataBinding().thankYouInformationView.setCenterNightsVisible(true);
        getViewDataBinding().thankYouInformationView.setCenterNightsText(getString(R.string.label_nights, nights));

        List<Pair<CharSequence, CharSequence>> reservationInformationList = new ArrayList<>();

        reservationInformationList.add(new Pair(getString(R.string.label_booking_place_name), stayName));
        reservationInformationList.add(new Pair(getString(R.string.label_booking_room_type), roomType));

        getViewDataBinding().thankYouInformationView.setReservationInformation(reservationInformationList);
    }

    @Override
    public void startAnimation(Animator.AnimatorListener listener, boolean stampEnable)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        StayThankYouScreenAnimator animator;

        if (stampEnable == true)
        {
            animator = new StayThankYouScreenAnimator(getContext()//
                , getViewDataBinding().checkImageView, getViewDataBinding().thankYouInformationView, getViewDataBinding().stampLayout);
        } else
        {
            animator = new StayThankYouScreenAnimator(getContext()//
                , getViewDataBinding().checkImageView, getViewDataBinding().thankYouInformationView, null);
        }

        animator.setListener(listener);
        animator.start();
    }

    @Override
    public void setNoticeVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().thankYouInformationView.setNoticeVisible(visible);
    }

    @Override
    public void setNoticeText(String notice)
    {
        getViewDataBinding().thankYouInformationView.setNoticeText(notice);
    }

    @Override
    public void setStampVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stampLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setStampMessages(String message1, String message2, String message3)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().message1TextView.setText(message1);
        getViewDataBinding().message2TextView.setText(message2);

        // SpannableString 자체가 null을 허용하지 않
        if (DailyTextUtils.isTextEmpty(message3) == false)
        {
            SpannableString spannableString = new SpannableString(message3);
            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getViewDataBinding().message3TextView.setText(spannableString);
        } else
        {
            getViewDataBinding().message3TextView.setText(null);
        }
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

            case R.id.stampLayout:
                getEventListener().onStampClick();
                break;
        }
    }
}
