package com.daily.dailyhotel.screen.mydaily.profile.leave;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.LeaveInfo;

public interface LeaveDailyInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void setLeaveInfo(LeaveInfo leaveInfo);

        void setRewardInfo(String titleText, String optionText, int rewardCount, String descriptionText);

        void setRewardLayoutVisible(boolean visible);

        void setLeaveReasonText(String text);

        boolean isAgreeChecked();

        void setLeaveButtonEnabled(boolean enabled);
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onLeaveReasonClick();

        void onLeaveButtonClick();

        void onAgreeCheckedChanged(boolean checked);

        void onRewardGuideClick();

        void onRewardClick();
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
