package com.daily.dailyhotel.screen.home.stay.inbound.thankyou;

import android.animation.Animator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.animation.ThankYouScaleAnimator;
import com.daily.dailyhotel.animation.ThankYouScreenAnimator;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayPaymentThankYouDataBinding;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

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

        initToolbar(viewDataBinding);

        viewDataBinding.thankYouInformationView.setReservationTitle(R.string.label_booking_room_info);
        viewDataBinding.confirmView.setOnClickListener(this);

        viewDataBinding.thankYouInformationView.setVisibility(View.INVISIBLE);
        viewDataBinding.checkImageView.setVisibility(View.INVISIBLE);
        viewDataBinding.stampLayout.setVisibility(View.GONE);
        viewDataBinding.recommendGourmetLayout.setTitleText(R.string.label_booking_reservation_recommend_gourmet_title);
        viewDataBinding.recommendGourmetLayout.setVisibility(View.GONE);
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

        getViewDataBinding().thankYouInformationView.removeAllReservationInformation();
        getViewDataBinding().thankYouInformationView.addReservationInformation(getString(R.string.label_booking_place_name), stayName);
        getViewDataBinding().thankYouInformationView.addReservationInformation(getString(R.string.label_booking_room_type), roomType);
    }

    @Override
    public void startReceiptAnimation(Animator.AnimatorListener listener)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        ThankYouScreenAnimator animator;
        animator = new ThankYouScreenAnimator(getContext(), getViewDataBinding().checkImageView, getViewDataBinding().thankYouInformationView);
        animator.setListener(listener);
        animator.start();

//        StayThankYouScreenAnimator animator;
//
//        if (stampEnable == true)
//        {
//            animator = new StayThankYouScreenAnimator(getContext()//
//                , getViewDataBinding().checkImageView, getViewDataBinding().thankYouInformationView, getViewDataBinding().stampLayout);
//        } else
//        {
//            animator = new StayThankYouScreenAnimator(getContext()//
//                , getViewDataBinding().checkImageView, getViewDataBinding().thankYouInformationView, null);
//        }
//
//        animator.setListener(listener);
//        animator.start();
    }

    @Override
    public void startRecommendNStampAnimation(Animator.AnimatorListener listener, boolean stampEnable)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        boolean recommendGourmetEnable = getViewDataBinding().recommendGourmetLayout.hasData();
        if (recommendGourmetEnable == false && stampEnable == false)
        {
            return;
        }

        ThankYouScaleAnimator animator;

        animator = new ThankYouScaleAnimator(getContext() //
            , recommendGourmetEnable == true ? getViewDataBinding().recommendGourmetLayout : null //
            , stampEnable == true ? getViewDataBinding().stampLayout : null);

        animator.setListener(listener);
        animator.start();
    }


    @Override
    public Observable<Boolean> getReceiptAnimation()
    {
        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                startReceiptAnimation(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });
            }
        };

        observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
        return observable;
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
    public void setRecommendGourmetData(ArrayList<CarouselListItem> carouselListItemList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recommendGourmetLayout.setData(carouselListItemList);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                getEventListener().onConfirmClick();
                break;

            case R.id.stampLayout:
                getEventListener().onStampClick();
                break;
        }
    }

    private void initToolbar(ActivityStayPaymentThankYouDataBinding viewDataBinding)
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
