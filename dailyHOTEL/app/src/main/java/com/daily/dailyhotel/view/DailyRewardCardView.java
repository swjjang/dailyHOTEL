package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.util.FontManager;
import com.daily.base.widget.DailyImageView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewRewardCardDataBinding;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

public class DailyRewardCardView extends ConstraintLayout
{
    private DailyViewRewardCardDataBinding mViewDataBinding;

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

    public void setCampaignFreeCount(int count)
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
            , mViewDataBinding.nights8RewardImageView};

        final View[] campaignViews = {mViewDataBinding.nights1CampaignImageView//
            , mViewDataBinding.nights2CampaignImageView//
            , mViewDataBinding.nights3CampaignImageView//
            , mViewDataBinding.nights4CampaignImageView//
            , mViewDataBinding.nights5CampaignImageView//
            , mViewDataBinding.nights6CampaignImageView//
            , mViewDataBinding.nights7CampaignImageView//
            , mViewDataBinding.nights8CampaignImageView};

        final int dotViewsLength = dotImageViews.length;

        for (int i = 0; i < dotViewsLength; i++)
        {
            dotImageViews[i].setVisibility(i < count ? INVISIBLE : VISIBLE);
        }

        final int lineViewsLength = lineViews.length;

        for (int i = 0; i < lineViewsLength; i++)
        {
            if (i + 1 < count)
            {
                lineViews[i].setBackgroundResource(R.drawable.shape_line_cfaae37_dw2_dg2);
            } else
            {
                lineViews[i].setBackgroundResource(R.drawable.shape_line_ce7e7e7);
            }
        }

        final int rewardViewsLength = rewardViews.length;

        for (int i = 0; i < rewardViewsLength; i++)
        {
            rewardViews[i].setVisibility(GONE);
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
            , mViewDataBinding.nights8RewardImageView};

        final View[] campaignViews = {mViewDataBinding.nights1CampaignImageView//
            , mViewDataBinding.nights2CampaignImageView//
            , mViewDataBinding.nights3CampaignImageView//
            , mViewDataBinding.nights4CampaignImageView//
            , mViewDataBinding.nights5CampaignImageView//
            , mViewDataBinding.nights6CampaignImageView//
            , mViewDataBinding.nights7CampaignImageView//
            , mViewDataBinding.nights8CampaignImageView};

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

        for (int i = 0; i < lineViewsLength; i++)
        {
            if (i + 1 < count)
            {
                lineViews[i].setBackgroundResource(R.drawable.shape_line_cfaae37);
            } else
            {
                lineViews[i].setBackgroundResource(R.drawable.shape_line_ce7e7e7);
            }
        }

        final int rewardViewsLength = rewardViews.length;

        for (int i = 0; i < rewardViewsLength; i++)
        {
            rewardViews[i].setVisibility(i + 1 == count ? VISIBLE : GONE);
        }

        final int campaignViewsLength = campaignViews.length;

        for (int i = 0; i < campaignViewsLength; i++)
        {
            campaignViews[i].setVisibility(GONE);
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

    public void setWarningText(String text)
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

        // ^1박^ 화면에 다르게 보이도록 한다.
        int startIndex = text.indexOf('^');
        int endIndex = text.indexOf('^', startIndex + 1);

        text = text.replaceAll("\\^", "");

        if (startIndex >= 0 && endIndex >= 0)
        {
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(getContext()).getMediumTypeface()),//
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.default_text_ce9a230)), //
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mViewDataBinding.descriptionTextView.setText(spannableString);
        } else
        {
            mViewDataBinding.descriptionTextView.setText(text);
        }
    }
}
