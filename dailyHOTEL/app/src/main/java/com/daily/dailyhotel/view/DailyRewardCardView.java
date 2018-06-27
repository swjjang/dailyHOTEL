package com.daily.dailyhotel.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.FontManager;
import com.daily.base.widget.DailyImageView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewRewardCardDataBinding;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.ArrayList;
import java.util.List;

public class DailyRewardCardView extends ConstraintLayout
{
    private DailyViewRewardCardDataBinding mViewDataBinding;

    AnimatorSet mStickerAnimatorSet;

    public DailyRewardCardView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyRewardCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyRewardCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_reward_card_data, this, true);

        setBackgroundResource(R.drawable.r_minibox);
    }


    public void setGuideVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.guideImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnGuideClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.guideImageView.setOnClickListener(listener);
    }

    public void setOptionText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.optionTextView.setText(text);
    }

    public void setOptionVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.optionTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnOptionClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.optionTextView.setOnClickListener(listener);
    }

    public void setCampaignFreeStickerCount(int count)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        final DailyImageView[] dotImageViews = {mViewDataBinding.nights1ImageView//
            , mViewDataBinding.nights2ImageView//
            , mViewDataBinding.nights3ImageView//
            , mViewDataBinding.nights4ImageView//
            , mViewDataBinding.nights5ImageView//
            , mViewDataBinding.nights6ImageView//
            , mViewDataBinding.nights7ImageView//
            , mViewDataBinding.nights8ImageView//
            , mViewDataBinding.nights9ImageView};

        final View[] lineViews = {mViewDataBinding.nights2LineView//
            , mViewDataBinding.nights3LineView//
            , mViewDataBinding.nights4LineView//
            , mViewDataBinding.nights5LineView//
            , mViewDataBinding.nights6LineView//
            , mViewDataBinding.nights7LineView//
            , mViewDataBinding.nights8LineView//
            , mViewDataBinding.nights9LineView};

        final View[] rewardViews = {mViewDataBinding.nights1RewardImageView//
            , mViewDataBinding.nights2RewardImageView//
            , mViewDataBinding.nights3RewardImageView//
            , mViewDataBinding.nights4RewardImageView//
            , mViewDataBinding.nights5RewardImageView//
            , mViewDataBinding.nights6RewardImageView//
            , mViewDataBinding.nights7RewardImageView//
            , mViewDataBinding.nights8RewardImageView//
            , mViewDataBinding.nights9RewardImageView};

        final View[] campaignViews = {mViewDataBinding.nights1CampaignImageView//
            , mViewDataBinding.nights2CampaignImageView//
            , mViewDataBinding.nights3CampaignImageView//
            , mViewDataBinding.nights4CampaignImageView//
            , mViewDataBinding.nights5CampaignImageView//
            , mViewDataBinding.nights6CampaignImageView//
            , mViewDataBinding.nights7CampaignImageView//
            , mViewDataBinding.nights8CampaignImageView//
            , mViewDataBinding.nights9CampaignImageView};

        final int dotViewsLength = dotImageViews.length;

        for (int i = 0; i < dotViewsLength; i++)
        {
            dotImageViews[i].setVisibility(i < count ? INVISIBLE : VISIBLE);
        }

        final int lineViewsLength = lineViews.length;

        ConstraintLayout.LayoutParams lineLayoutParams;

        for (int i = 0; i < lineViewsLength; i++)
        {
            lineLayoutParams = (ConstraintLayout.LayoutParams) lineViews[i].getLayoutParams();

            if (i + 1 < count)
            {
                lineLayoutParams.leftToLeft = -1;
                lineLayoutParams.leftToRight = campaignViews[i].getId();
                lineLayoutParams.rightToRight = -1;
                lineLayoutParams.rightToLeft = campaignViews[i + 1].getId();

                lineViews[i].setBackgroundResource(R.drawable.shape_line_cfaae37_dw2_dg2);
            } else if (i + 1 == count)
            {
                lineLayoutParams.leftToLeft = -1;
                lineLayoutParams.leftToRight = campaignViews[i].getId();
                lineLayoutParams.rightToRight = dotImageViews[i + 1].getId();
                lineLayoutParams.rightToLeft = -1;

                lineViews[i].setBackgroundResource(R.drawable.shape_line_ce7e7e7);
            } else
            {
                lineLayoutParams.leftToLeft = dotImageViews[i].getId();
                lineLayoutParams.leftToRight = -1;
                lineLayoutParams.rightToRight = dotImageViews[i + 1].getId();
                lineLayoutParams.rightToLeft = -1;

                lineViews[i].setBackgroundResource(R.drawable.shape_line_ce7e7e7);
            }

            lineViews[i].setLayoutParams(lineLayoutParams);
        }

        final int rewardViewsLength = rewardViews.length;

        for (View rewardView : rewardViews)
        {
            rewardView.setVisibility(GONE);
        }

        final int campaignViewsLength = campaignViews.length;

        for (int i = 0; i < campaignViewsLength; i++)
        {
            campaignViews[i].setVisibility(i < count ? VISIBLE : GONE);
        }
    }

    public void setStickerCount(int count)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        final DailyImageView[] dotImageViews = {mViewDataBinding.nights1ImageView//
            , mViewDataBinding.nights2ImageView//
            , mViewDataBinding.nights3ImageView//
            , mViewDataBinding.nights4ImageView//
            , mViewDataBinding.nights5ImageView//
            , mViewDataBinding.nights6ImageView//
            , mViewDataBinding.nights7ImageView//
            , mViewDataBinding.nights8ImageView//
            , mViewDataBinding.nights9ImageView};

        final View[] lineViews = {mViewDataBinding.nights2LineView//
            , mViewDataBinding.nights3LineView//
            , mViewDataBinding.nights4LineView//
            , mViewDataBinding.nights5LineView//
            , mViewDataBinding.nights6LineView//
            , mViewDataBinding.nights7LineView//
            , mViewDataBinding.nights8LineView//
            , mViewDataBinding.nights9LineView};

        final View[] rewardViews = {mViewDataBinding.nights1RewardImageView//
            , mViewDataBinding.nights2RewardImageView//
            , mViewDataBinding.nights3RewardImageView//
            , mViewDataBinding.nights4RewardImageView//
            , mViewDataBinding.nights5RewardImageView//
            , mViewDataBinding.nights6RewardImageView//
            , mViewDataBinding.nights7RewardImageView//
            , mViewDataBinding.nights8RewardImageView//
            , mViewDataBinding.nights9RewardImageView};

        final View[] campaignViews = {mViewDataBinding.nights1CampaignImageView//
            , mViewDataBinding.nights2CampaignImageView//
            , mViewDataBinding.nights3CampaignImageView//
            , mViewDataBinding.nights4CampaignImageView//
            , mViewDataBinding.nights5CampaignImageView//
            , mViewDataBinding.nights6CampaignImageView//
            , mViewDataBinding.nights7CampaignImageView//
            , mViewDataBinding.nights8CampaignImageView//
            , mViewDataBinding.nights9CampaignImageView};

        final int dotViewsLength = dotImageViews.length;

        for (int i = 0; i < dotViewsLength; i++)
        {
            dotImageViews[i].setVisibility(VISIBLE);

            if (i < count)
            {
                dotImageViews[i].setVectorImageResource(R.drawable.vector_ic_reward_circle_active);
            } else
            {
                dotImageViews[i].setVectorImageResource(R.drawable.vector_ic_reward_circle_normal);
            }
        }

        final int lineViewsLength = lineViews.length;

        ConstraintLayout.LayoutParams lineLayoutParams;

        for (int i = 0; i < lineViewsLength; i++)
        {
            lineLayoutParams = (ConstraintLayout.LayoutParams) lineViews[i].getLayoutParams();

            lineLayoutParams.leftToLeft = dotImageViews[i].getId();
            lineLayoutParams.leftToRight = -1;
            lineLayoutParams.rightToRight = dotImageViews[i + 1].getId();
            lineLayoutParams.rightToLeft = -1;

            if (i + 1 < count)
            {
                lineViews[i].setBackgroundResource(R.drawable.shape_line_cfaae37);
            } else
            {
                lineViews[i].setBackgroundResource(R.drawable.shape_line_ce7e7e7);
            }

            lineViews[i].setLayoutParams(lineLayoutParams);
        }

        final int rewardViewsLength = rewardViews.length;

        for (int i = 0; i < rewardViewsLength; i++)
        {
            rewardViews[i].setVisibility(i + 1 == count ? VISIBLE : GONE);
        }

        final int campaignViewsLength = campaignViews.length;

        for (View campaignView : campaignViews)
        {
            campaignView.setVisibility(GONE);
        }
    }

    public void setRewardTitleText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.rewardTitleTextView.setText(text);
    }

    public void setWarningText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.warningTextView.setText(text);
    }

    public void setWarningVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.warningTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setDescriptionText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(text) == true)
        {
            mViewDataBinding.descriptionTextView.setText(text);
            return;
        }

        // ^1박^ 화면에 다르게 보이도록 한다.
        int startIndex = text.indexOf('^');
        int endIndex = text.indexOf('^', startIndex + 1);

        text = text.replaceAll("\\^", "");

        if (startIndex >= 0 && endIndex >= 0)
        {
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.default_text_cf4a426)), //
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mViewDataBinding.descriptionTextView.setText(spannableString);
        } else
        {
            mViewDataBinding.descriptionTextView.setText(text);
        }
    }

    public void setDescriptionText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.descriptionTextView.setText(text);
    }

    public void setDescriptionVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.descriptionTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setWarningTextColor(boolean warningTextColor) {
        if (mViewDataBinding == null) {
            return;
        }

        if (getContext() == null || getContext().getResources() == null) {
            return;
        }

        int color = getContext().getResources().getColor(warningTextColor ? R.color.default_text_ceb2135 : R.color.default_text_c929292);

        mViewDataBinding.warningTextView.setTextColor(color);
        mViewDataBinding.descriptionTextView.setTextColor(color);
    }

    public void startCampaignStickerAnimation()
    {
        if (mStickerAnimatorSet != null)
        {
            return;
        }

        final View[] lineViews = {mViewDataBinding.nights2LineView//
            , mViewDataBinding.nights3LineView//
            , mViewDataBinding.nights4LineView//
            , mViewDataBinding.nights5LineView//
            , mViewDataBinding.nights6LineView//
            , mViewDataBinding.nights7LineView//
            , mViewDataBinding.nights8LineView//
            , mViewDataBinding.nights9LineView};

        final View[] campaignViews = {mViewDataBinding.nights1CampaignImageView//
            , mViewDataBinding.nights2CampaignImageView//
            , mViewDataBinding.nights3CampaignImageView//
            , mViewDataBinding.nights4CampaignImageView//
            , mViewDataBinding.nights5CampaignImageView//
            , mViewDataBinding.nights6CampaignImageView//
            , mViewDataBinding.nights7CampaignImageView//
            , mViewDataBinding.nights8CampaignImageView//
            , mViewDataBinding.nights9CampaignImageView};

        int campaignCount = 0;

        final int campaignViewsLength = campaignViews.length;

        for (View campaignView : campaignViews)
        {
            if (campaignView.getVisibility() == VISIBLE)
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

        final int MS_PER_FRAME = 166;

        for (int i = 0; i < campaignCount; i++)
        {
            // 1 set
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(campaignViews[i], View.ALPHA, 1.0f, 1.0f);
            objectAnimator1.setDuration(MS_PER_FRAME * 5);

            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(campaignViews[i], View.ALPHA, 1.0f, 0.5f, 0.5f);
            objectAnimator2.setStartDelay(MS_PER_FRAME * 5);
            objectAnimator2.setDuration(MS_PER_FRAME * 4);

            ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(campaignViews[i], View.ALPHA, 0.5f, 1.0f);
            objectAnimator3.setStartDelay(MS_PER_FRAME * 9);
            objectAnimator3.setDuration(MS_PER_FRAME);

            animatorList.add(objectAnimator1);
            animatorList.add(objectAnimator2);
            animatorList.add(objectAnimator3);

            if (i < campaignCount - 1)
            {
                // 1 set
                ObjectAnimator objectAnimator10 = ObjectAnimator.ofFloat(lineViews[i], View.ALPHA, 1.0f, 1.0f);
                objectAnimator10.setDuration(MS_PER_FRAME * 5);

                ObjectAnimator objectAnimator11 = ObjectAnimator.ofFloat(lineViews[i], View.ALPHA, 1.0f, 0.5f, 0.5f);
                objectAnimator11.setStartDelay(MS_PER_FRAME * 5);
                objectAnimator11.setDuration(MS_PER_FRAME * 4);

                ObjectAnimator objectAnimator12 = ObjectAnimator.ofFloat(lineViews[i], View.ALPHA, 0.5f, 1.0f);
                objectAnimator12.setStartDelay(MS_PER_FRAME * 9);
                objectAnimator12.setDuration(MS_PER_FRAME);

                animatorList.add(objectAnimator10);
                animatorList.add(objectAnimator11);
                animatorList.add(objectAnimator12);
            }
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

    public void stopCampaignStickerAnimation()
    {
        if (mStickerAnimatorSet == null)
        {
            return;
        }

        mStickerAnimatorSet.cancel();
        mStickerAnimatorSet = null;
    }
}
