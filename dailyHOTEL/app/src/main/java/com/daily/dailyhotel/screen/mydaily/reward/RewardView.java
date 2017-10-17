package com.daily.dailyhotel.screen.mydaily.reward;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityRewardDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class RewardView extends BaseDialogView<RewardView.OnEventListener, ActivityRewardDataBinding> implements RewardInterface
{
    private AnimatorSet mIssueCouponAnimatorSet;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onLoginClick();

        void onIssueCouponClick();
    }

    public RewardView(BaseActivity baseActivity, RewardView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityRewardDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.loginTextView.setOnClickListener(v -> getEventListener().onLoginClick());
        viewDataBinding.issueCouponLayout.setOnClickListener(v -> getEventListener().onIssueCouponClick());
        viewDataBinding.issueCouponLayout.setTranslationY(ScreenUtils.dpToPx(getContext(), 192));

        viewDataBinding.issueCouponClickView.setOnClickListener(v -> getEventListener().onIssueCouponClick());
        viewDataBinding.issueCouponClickView.setTranslationY(ScreenUtils.dpToPx(getContext(), 192));

        viewDataBinding.issueCouponArrowImageView.setTranslationY(ScreenUtils.dpToPx(getContext(), 192));

        SpannableString spannableString1 = new SpannableString(getString(R.string.label_reward_reward_guide));
        spannableString1.setSpan(new UnderlineSpan(), 0, spannableString1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewDataBinding.guideLinkTextView.setText(spannableString1);

        SpannableString spannableString2 = new SpannableString(getString(R.string.label_reward_notification_on_setting));
        spannableString2.setSpan(new UnderlineSpan(), 0, spannableString2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewDataBinding.guideNotificationLinkTextView.setText(spannableString2);

        viewDataBinding.issueCouponLayout.setVisibility(View.GONE);
        viewDataBinding.issueCouponArrowImageView.setVisibility(View.GONE);
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
    public void setLoginVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().loginTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTitleMessage(String message)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stickerTitleTextView.setText(message);
    }

    @Override
    public void setDescriptionMessage(String message)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stickerDescriptionTextView.setText(message);
    }

    @Override
    public void setSticker(int nights, boolean visible, boolean enabled)
    {
        final int MIN_NIGHTS = 1;
        final int MAX_NIGHTS = 9;

        if (getViewDataBinding() == null || nights < MIN_NIGHTS || nights > MAX_NIGHTS)
        {
            return;
        }

        int resourceId = visible ? R.drawable.r_ic_l_47_shadow : R.drawable.r_ic_l_47_placeholder;
        float alpha = enabled ? 1.0f : 0.5f;

        final View[] views = {getViewDataBinding().sticker1nightsTextView//
            , getViewDataBinding().sticker2nightsTextView//
            , getViewDataBinding().sticker3nightsTextView//
            , getViewDataBinding().sticker4nightsTextView//
            , getViewDataBinding().sticker5nightsTextView//
            , getViewDataBinding().sticker6nightsTextView//
            , getViewDataBinding().sticker7nightsTextView//
            , getViewDataBinding().sticker8nightsTextView//
            , getViewDataBinding().sticker9nightsTextView};

        views[nights - 1].setBackgroundResource(resourceId);
        views[nights - 1].setAlpha(alpha);
    }

    @Override
    public void setIssueCouponCount(int count)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (count < 2)
        {
            getViewDataBinding().issueCouponCountTextView.setVisibility(View.GONE);
        } else
        {
            getViewDataBinding().issueCouponCountTextView.setVisibility(View.VISIBLE);
            getViewDataBinding().issueCouponCountTextView.setText(Integer.toString(count));
        }

    }

    @Override
    public void setRewardHistoryEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().rewardHistoryTextView.setEnabled(false);
    }

    @Override
    public void setGuideTitleMessage(String message)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().guideTitleTextView.setText(message);
    }

    @Override
    public void setGuideDescriptionMessage(String message)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().guideDescriptionTextView.setText(message);
    }

    @Override
    public void setNotificationVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.GONE;

        getViewDataBinding().guideUnderLineView.setVisibility(flag);
        getViewDataBinding().guideNotificationTextView.setVisibility(flag);
        getViewDataBinding().guideNotificationLinkTextView.setVisibility(flag);
    }

    @Override
    public void setIssueCouponVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.GONE;

        getViewDataBinding().issueCouponLayout.setVisibility(flag);
        getViewDataBinding().issueCouponClickView.setVisibility(flag);

        getViewDataBinding().stickerLayout.setPadding(0, 0, 0, visible ? ScreenUtils.dpToPx(getContext(), 60) : 0);
    }

    @Override
    public void setIssueCouponEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().issueCouponLayout.setBackgroundResource(enabled ? R.drawable.r_btn_coupon_on : R.drawable.r_btn_coupon_off);

        if (enabled == true && getViewDataBinding().issueCouponLayout.getVisibility() == View.VISIBLE)
        {
            getViewDataBinding().issueCouponArrowImageView.setVisibility(View.VISIBLE);

            getViewDataBinding().issueCouponClickView.setEnabled(true);
        } else
        {
            getViewDataBinding().issueCouponArrowImageView.setVisibility(View.GONE);

            getViewDataBinding().issueCouponClickView.setEnabled(false);
        }
    }

    @Override
    public Observable<Boolean> openIssueCouponAnimation()
    {
        if (getViewDataBinding() == null || mIssueCouponAnimatorSet != null)
        {
            return null;
        }

        ObjectAnimator issueCouponObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponLayout, View.TRANSLATION_Y//
            , getViewDataBinding().issueCouponLayout.getTranslationY(), 0.0f);

        ObjectAnimator issueCouponArrowObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponArrowImageView, View.TRANSLATION_Y//
            , getViewDataBinding().issueCouponArrowImageView.getTranslationY(), 0.0f);

        ObjectAnimator issueCouponClickObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponClickView, View.TRANSLATION_Y//
            , getViewDataBinding().issueCouponClickView.getTranslationY(), 0.0f);

        mIssueCouponAnimatorSet.playTogether(issueCouponObjectAnimator, issueCouponArrowObjectAnimator, issueCouponClickObjectAnimator);
        mIssueCouponAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mIssueCouponAnimatorSet.setDuration(200);

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                mIssueCouponAnimatorSet.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (mIssueCouponAnimatorSet != null)
                        {
                            mIssueCouponAnimatorSet.removeAllListeners();
                            mIssueCouponAnimatorSet = null;
                        }

                        getViewDataBinding().issueCouponArrowImageView.setRotation(0.0f);
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

                mIssueCouponAnimatorSet.start();
            }
        };

        return observable;
    }

    @Override
    public Observable<Boolean> closeIssueCouponAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        ObjectAnimator issueCouponObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponLayout, View.TRANSLATION_Y//
            , 0.0f, ScreenUtils.dpToPx(getContext(), 192));

        ObjectAnimator issueCouponArrowObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponArrowImageView, View.TRANSLATION_Y//
            , 0.0f, ScreenUtils.dpToPx(getContext(), 192));

        ObjectAnimator issueCouponClickObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponClickView, View.TRANSLATION_Y//
            , 0.0f, ScreenUtils.dpToPx(getContext(), 192));

        mIssueCouponAnimatorSet.playTogether(issueCouponObjectAnimator, issueCouponArrowObjectAnimator, issueCouponClickObjectAnimator);

        mIssueCouponAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mIssueCouponAnimatorSet.setDuration(200);

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                mIssueCouponAnimatorSet.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (mIssueCouponAnimatorSet != null)
                        {
                            mIssueCouponAnimatorSet.removeAllListeners();
                            mIssueCouponAnimatorSet = null;
                        }

                        getViewDataBinding().issueCouponArrowImageView.setRotation(180.0f);
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

                mIssueCouponAnimatorSet.start();
            }
        };

        return observable;
    }

    @Override
    public boolean isOpenedIssueCoupon()
    {
        return getViewDataBinding().issueCouponLayout.getTranslationY() == 0;
    }
}
