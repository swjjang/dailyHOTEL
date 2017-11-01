package com.daily.dailyhotel.screen.mydaily.reward;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityRewardDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class RewardView extends BaseDialogView<RewardView.OnEventListener, ActivityRewardDataBinding> implements RewardInterface
{
    private AnimatorSet mIssueCouponAnimatorSet;
    private AnimatorSet mIssueCouponShakeAnimatorSet;
    private AnimatorSet mStickerAnimatorSet;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onLoginClick();

        void onIssueCouponClick();

        void onHistoryClick();

        void onTermsClick();

        void onRewardGuideClick();

        void onNotificationClick();

        void onGoBookingClick();
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

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.nestedScrollView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.loginTextView.setOnClickListener(v -> getEventListener().onLoginClick());
        viewDataBinding.rewardHistoryTextView.setOnClickListener(v -> getEventListener().onHistoryClick());
        viewDataBinding.rewardTermsTextView.setOnClickListener(v -> getEventListener().onTermsClick());

        viewDataBinding.issueCouponLayout.setTranslationY(ScreenUtils.dpToPx(getContext(), 192));

        viewDataBinding.issueCouponClickView.setOnClickListener(v -> getEventListener().onIssueCouponClick());
        viewDataBinding.issueCouponClickView.setTranslationY(ScreenUtils.dpToPx(getContext(), 192));

        viewDataBinding.issueCouponArrowImageView.setTranslationY(ScreenUtils.dpToPx(getContext(), 192));

        SpannableString spannableString1 = new SpannableString(getString(R.string.label_reward_reward_guide));
        spannableString1.setSpan(new UnderlineSpan(), 0, spannableString1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewDataBinding.guideLinkTextView.setText(spannableString1);
        viewDataBinding.guideLinkTextView.setOnClickListener(v -> getEventListener().onRewardGuideClick());

        SpannableString spannableString2 = new SpannableString(getString(R.string.label_reward_notification_on_setting));
        spannableString2.setSpan(new UnderlineSpan(), 0, spannableString2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewDataBinding.guideNotificationLinkTextView.setText(spannableString2);
        viewDataBinding.guideNotificationLinkTextView.setOnClickListener(v -> getEventListener().onNotificationClick());

        viewDataBinding.issueCouponBackgroundView.setOnClickListener(v -> getEventListener().onIssueCouponClick());
        viewDataBinding.issueCouponBackgroundView.setVisibility(View.GONE);

        viewDataBinding.issueCouponLayout.setVisibility(View.GONE);
        viewDataBinding.issueCouponArrowImageView.setVisibility(View.GONE);

        viewDataBinding.goBookingTextView.setOnClickListener(v -> getEventListener().onGoBookingClick());
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
        if (getViewDataBinding() == null || DailyTextUtils.isTextEmpty(message) == true)
        {
            return;
        }

        // ^1박^ 화면에 다르게 보이도록 한다.
        int startIndex = message.indexOf('^');
        int endIndex = message.indexOf('^', startIndex + 1);

        message = message.replaceAll("\\^", "");

        if (startIndex >= 0 && endIndex >= 0)
        {
            SpannableString spannableString = new SpannableString(message);
            spannableString.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.default_text_ce9a230)), //
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewDataBinding().stickerDescriptionTextView.setText(spannableString);
        } else
        {
            getViewDataBinding().stickerDescriptionTextView.setText(message);
        }
    }

    @Override
    public void setCampaignFreeStickerCount(int count)
    {
        final int MAX_COUNT = 9;

        if (getViewDataBinding() == null || count > MAX_COUNT)
        {
            return;
        }

        final View[] views = {getViewDataBinding().sticker1nightsTextView//
            , getViewDataBinding().sticker2nightsTextView//
            , getViewDataBinding().sticker3nightsTextView//
            , getViewDataBinding().sticker4nightsTextView//
            , getViewDataBinding().sticker5nightsTextView//
            , getViewDataBinding().sticker6nightsTextView//
            , getViewDataBinding().sticker7nightsTextView//
            , getViewDataBinding().sticker8nightsTextView//
            , getViewDataBinding().sticker9nightsTextView};

        final View[] stickerViews = {getViewDataBinding().sticker1nightsImageView//
            , getViewDataBinding().sticker2nightsImageView//
            , getViewDataBinding().sticker3nightsImageView//
            , getViewDataBinding().sticker4nightsImageView//
            , getViewDataBinding().sticker5nightsImageView//
            , getViewDataBinding().sticker6nightsImageView//
            , getViewDataBinding().sticker7nightsImageView//
            , getViewDataBinding().sticker8nightsImageView//
            , getViewDataBinding().sticker9nightsImageView};

        for (int i = 0; i < MAX_COUNT; i++)
        {
            if (i < count)
            {
                views[i].setAlpha(0.0f);
                stickerViews[i].setAlpha(0.5f);
                stickerViews[i].setVisibility(View.VISIBLE);
            } else
            {
                views[i].setAlpha(1.0f);
                stickerViews[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void setStickerCount(int count)
    {
        final int MAX_COUNT = 9;

        if (getViewDataBinding() == null || count > MAX_COUNT)
        {
            return;
        }

        final View[] views = {getViewDataBinding().sticker1nightsTextView//
            , getViewDataBinding().sticker2nightsTextView//
            , getViewDataBinding().sticker3nightsTextView//
            , getViewDataBinding().sticker4nightsTextView//
            , getViewDataBinding().sticker5nightsTextView//
            , getViewDataBinding().sticker6nightsTextView//
            , getViewDataBinding().sticker7nightsTextView//
            , getViewDataBinding().sticker8nightsTextView//
            , getViewDataBinding().sticker9nightsTextView};

        final View[] stickerViews = {getViewDataBinding().sticker1nightsImageView//
            , getViewDataBinding().sticker2nightsImageView//
            , getViewDataBinding().sticker3nightsImageView//
            , getViewDataBinding().sticker4nightsImageView//
            , getViewDataBinding().sticker5nightsImageView//
            , getViewDataBinding().sticker6nightsImageView//
            , getViewDataBinding().sticker7nightsImageView//
            , getViewDataBinding().sticker8nightsImageView//
            , getViewDataBinding().sticker9nightsImageView};

        for (int i = 0; i < MAX_COUNT; i++)
        {
            if (i < count)
            {
                views[i].setAlpha(0.0f);
                stickerViews[i].setAlpha(1.0f);
                stickerViews[i].setVisibility(View.VISIBLE);
            } else
            {
                views[i].setAlpha(1.0f);
                stickerViews[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void startCampaignStickerAnimation()
    {
        if (getViewDataBinding() == null || mStickerAnimatorSet != null)
        {
            return;
        }

        final View[] stickerViews = {getViewDataBinding().sticker1nightsImageView//
            , getViewDataBinding().sticker2nightsImageView//
            , getViewDataBinding().sticker3nightsImageView//
            , getViewDataBinding().sticker4nightsImageView//
            , getViewDataBinding().sticker5nightsImageView//
            , getViewDataBinding().sticker6nightsImageView//
            , getViewDataBinding().sticker7nightsImageView//
            , getViewDataBinding().sticker8nightsImageView//
            , getViewDataBinding().sticker9nightsImageView};

        int campaignCount = 0;

        final int campaignViewsLength = stickerViews.length;

        for (int i = 0; i < campaignViewsLength; i++)
        {
            if (stickerViews[i].getVisibility() == View.VISIBLE)
            {
                campaignCount++;
            } else
            {
                break;
            }
        }

        if (campaignCount == 0)
        {
            return;
        }

        List<Animator> animatorList = new ArrayList<>();

        final int MS_PER_FRAME = 170;

        for (int i = 0; i < campaignCount; i++)
        {
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(stickerViews[i], View.ALPHA, 0.5f, 0.5f);
            objectAnimator1.setDuration(MS_PER_FRAME * 3);

            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(stickerViews[i], View.ALPHA, 0.5f, 1.0f, 1.0f, 0.5f);
            objectAnimator2.setStartDelay(MS_PER_FRAME * 3);
            objectAnimator2.setDuration(MS_PER_FRAME * 6);

            animatorList.add(objectAnimator1);
            animatorList.add(objectAnimator2);
        }

        mStickerAnimatorSet = new AnimatorSet();
        mStickerAnimatorSet.playTogether(animatorList);
        mStickerAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mStickerAnimatorSet.addListener(new Animator.AnimatorListener()
        {
            boolean canceled;

            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (canceled == false)
                {
                    mStickerAnimatorSet.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                canceled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mStickerAnimatorSet.start();
    }

    @Override
    public void stopCampaignStickerAnimation()
    {
        if (getViewDataBinding() == null || mStickerAnimatorSet == null)
        {
            return;
        }

        mStickerAnimatorSet.cancel();
        mStickerAnimatorSet = null;
    }

    @Override
    public void setStickerValidityVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stickerValidityTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setStickerValidityText(String message)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stickerValidityTextView.setText(message);
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

        getViewDataBinding().rewardHistoryTextView.setEnabled(enabled);
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

        if (enabled == true)
        {
            getViewDataBinding().issueCouponLayout.setBackgroundResource(R.drawable.r_btn_coupon_on);
            getViewDataBinding().issueCouponTitleTextView.setText(R.string.label_reward_issued_reward_coupon);
        } else
        {
            getViewDataBinding().issueCouponLayout.setBackgroundResource(R.drawable.r_btn_coupon_off);
            getViewDataBinding().issueCouponTitleTextView.setText(R.string.label_reward_to_bo_issued_reward_coupon);
        }

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

        mIssueCouponAnimatorSet = new AnimatorSet();
        mIssueCouponAnimatorSet.playTogether(issueCouponObjectAnimator, issueCouponArrowObjectAnimator, issueCouponClickObjectAnimator);
        mIssueCouponAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mIssueCouponAnimatorSet.setDuration(150);

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
                        getViewDataBinding().issueCouponBackgroundView.setVisibility(View.VISIBLE);

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

                mIssueCouponAnimatorSet.start();
            }
        };

        return observable;
    }

    @Override
    public Observable<Boolean> closeIssueCouponAnimation()
    {
        if (getViewDataBinding() == null || mIssueCouponAnimatorSet != null)
        {
            return null;
        }

        final int DP_192 = ScreenUtils.dpToPx(getContext(), 192);

        ObjectAnimator issueCouponObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponLayout, View.TRANSLATION_Y//
            , 0.0f, DP_192);

        ObjectAnimator issueCouponArrowObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponArrowImageView, View.TRANSLATION_Y//
            , 0.0f, DP_192);

        ObjectAnimator issueCouponClickObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponClickView, View.TRANSLATION_Y//
            , 0.0f, DP_192);

        mIssueCouponAnimatorSet = new AnimatorSet();
        mIssueCouponAnimatorSet.playTogether(issueCouponObjectAnimator, issueCouponArrowObjectAnimator, issueCouponClickObjectAnimator);
        mIssueCouponAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mIssueCouponAnimatorSet.setDuration(150);

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
                        getViewDataBinding().issueCouponBackgroundView.setVisibility(View.GONE);

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

                mIssueCouponAnimatorSet.start();
            }
        };

        return observable;
    }

    @Override
    public boolean isOpenedIssueCoupon()
    {
        if (getViewDataBinding() == null)
        {
            return false;
        }

        return getViewDataBinding().issueCouponLayout.getTranslationY() == 0.0f;
    }

    @Override
    public void setIssueCouponAnimation(boolean enabled)
    {
        if (getViewDataBinding() == null || getViewDataBinding().issueCouponLayout.getVisibility() != View.VISIBLE)
        {
            return;
        }

        if (enabled == true)
        {
            if (mIssueCouponShakeAnimatorSet != null)
            {
                return;
            }

            final int DP_6 = ScreenUtils.dpToPx(getContext(), 6);
            final int DP_192 = ScreenUtils.dpToPx(getContext(), 192);

            getViewDataBinding().issueCouponTitleUnderLineView.setVisibility(View.INVISIBLE);

            ObjectAnimator issueCouponObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponLayout, View.TRANSLATION_Y//
                , DP_192, DP_192 - DP_6, DP_192);

            issueCouponObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);

            ObjectAnimator issueCouponArrowObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponArrowImageView, View.TRANSLATION_Y//
                , DP_192, DP_192 - DP_6, DP_192);

            issueCouponArrowObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);

            ObjectAnimator issueCouponClickObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().issueCouponClickView, View.TRANSLATION_Y//
                , DP_192, DP_192 - DP_6, DP_192);

            issueCouponClickObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);

            mIssueCouponShakeAnimatorSet = new AnimatorSet();
            mIssueCouponShakeAnimatorSet.playTogether(issueCouponObjectAnimator, issueCouponArrowObjectAnimator, issueCouponClickObjectAnimator);
            mIssueCouponShakeAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            mIssueCouponShakeAnimatorSet.setDuration(1600);
            mIssueCouponShakeAnimatorSet.start();
        } else
        {
            if (mIssueCouponShakeAnimatorSet == null)
            {
                return;
            }

            mIssueCouponShakeAnimatorSet.cancel();
            mIssueCouponShakeAnimatorSet = null;

            getViewDataBinding().issueCouponTitleUnderLineView.setVisibility(View.VISIBLE);
        }
    }

    private void initToolbar(ActivityRewardDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }
}
