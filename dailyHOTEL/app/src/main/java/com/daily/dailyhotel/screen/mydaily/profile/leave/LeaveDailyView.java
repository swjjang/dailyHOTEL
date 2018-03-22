package com.daily.dailyhotel.screen.mydaily.profile.leave;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.LeaveInfo;
import com.daily.dailyhotel.view.DailyRewardCardView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityLeaveDailyDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutLeaveDailyPolicyInformationDataBinding;

import java.util.List;

public class LeaveDailyView extends BaseDialogView<LeaveDailyInterface.OnEventListener, ActivityLeaveDailyDataBinding> implements LeaveDailyInterface.ViewInterface
{
    public LeaveDailyView(BaseActivity baseActivity, LeaveDailyInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityLeaveDailyDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initLayout(viewDataBinding);
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

    private void initToolbar(ActivityLeaveDailyDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setTitleText(R.string.label_leave_daily);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private void initLayout(ActivityLeaveDailyDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        setRewardLayoutVisible(false);
        setLeaveReasonVisible(false);
        setAgreementButtonVisible(false);
        setLeaveButtonEnabled(false);

        viewDataBinding.leaveReasonTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (getEventListener() == null || isLeaveReasonVisible() == false)
                {
                    return;
                }

                getEventListener().onLeaveReasonClick();
            }
        });

        viewDataBinding.doLeaveView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (getEventListener() == null)
                {
                    return;
                }

                if (isLeaveButtonEnabled() == false)
                {
                    String reasonText = getViewDataBinding().leaveReasonTextView.getText().toString();
                    if (DailyTextUtils.isTextEmpty(reasonText))
                    {
                        DailyToast.showToast(getContext(), R.string.message_leave_daily_reason_unselected, Toast.LENGTH_SHORT);
                    } else if (isAgreeChecked() == false)
                    {
                        DailyToast.showToast(getContext(), R.string.message_leave_daily_agreement_unchecked, Toast.LENGTH_SHORT);
                    }
                    return;
                }

                getEventListener().onLeaveButtonClick();
            }
        });

        viewDataBinding.agreementCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if (getEventListener() == null)
                {
                    return;
                }

                getEventListener().onAgreeCheckedChanged(checked);
            }
        });
    }

    @Override
    public void setRewardLayoutVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().rewardLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLeaveReasonText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().leaveReasonTextView.setText(text);
    }

    @Override
    public boolean isAgreeChecked()
    {
        if (getViewDataBinding() == null)
        {
            return false;
        }

        return getViewDataBinding().agreementCheckBox.isChecked();
    }

    private void setLeaveReasonVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().leaveReasonLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    boolean isLeaveReasonVisible()
    {
        if (getViewDataBinding() == null)
        {
            return false;
        }

        return getViewDataBinding().leaveReasonLayout.getVisibility() == View.VISIBLE;
    }

    private void setAgreementButtonVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().leaveAgreementLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLeaveButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().doLeaveView.setTag(enabled);

        if (enabled == false)
        {
            getViewDataBinding().doLeaveView.setBackgroundResource(R.drawable.shape_button_disable);
            getViewDataBinding().doLeaveView.setTextColor(getColor(R.color.default_text_cc5c5c5));
        } else
        {
            getViewDataBinding().doLeaveView.setBackgroundResource(R.drawable.selector_button_default_background);
            getViewDataBinding().doLeaveView.setTextColor(getColorStateList(R.drawable.selector_button_default_text_color));
        }
    }

    boolean isLeaveButtonEnabled()
    {
        if (getViewDataBinding() == null)
        {
            return false;
        }

        return (boolean) getViewDataBinding().doLeaveView.getTag();
    }

    private void setLeavePolicyList(List<String> policyList)
    {
        if (getContext() == null || getViewDataBinding() == null || policyList == null || policyList.size() == 0)
        {
            return;
        }

        getViewDataBinding().leavePolicyInformationLayout.removeAllViews();

        LayoutLeaveDailyPolicyInformationDataBinding dataBinding = null;

        for (String policy : policyList)
        {
            dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()) //
                , R.layout.layout_leave_daily_policy_information_data //
                , getViewDataBinding().leavePolicyInformationLayout //
                , true);

            dataBinding.textView.setText(policy);
        }

        if (dataBinding != null)
        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dataBinding.textView.getLayoutParams();
            layoutParams.bottomMargin = 0;
            dataBinding.textView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void setLeaveInfo(LeaveInfo leaveInfo)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (leaveInfo == null)
        {
            return;
        }

        getViewDataBinding().messageTextView.setText(leaveInfo.msg);
        setLeavePolicyList(leaveInfo.policyList);

        boolean withDrawable = leaveInfo.msgCode == 100;
        boolean hasReasonList = leaveInfo.leaveReasonList != null && leaveInfo.leaveReasonList.size() > 0;

        setLeaveReasonVisible(withDrawable && hasReasonList);
        setAgreementButtonVisible(withDrawable);
    }

    @Override
    public void setRewardInfo(String titleText, String optionText, int rewardCount, String descriptionText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DailyRewardCardView rewardCardView = getViewDataBinding().rewardCardView;

        rewardCardView.setGuideVisible(true);
        rewardCardView.setOnGuideClickListener(v ->
        {
            if (getEventListener() == null)
            {
                return;
            }

            getEventListener().onRewardGuideClick();
        });

        if (DailyTextUtils.isTextEmpty(optionText) == false)
        {
            rewardCardView.setOptionVisible(true);
            rewardCardView.setOptionText(optionText);
            rewardCardView.setOnClickListener(v ->
            {
                if (getEventListener() == null)
                {
                    return;
                }

                getEventListener().onRewardClick();
            });
        } else
        {
            rewardCardView.setOptionVisible(false);
        }

        rewardCardView.setRewardTitleText(titleText);
        rewardCardView.setDescriptionText(descriptionText);

        rewardCardView.setStickerCount(rewardCount);
    }
}
