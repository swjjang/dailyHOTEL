package com.daily.dailyhotel.screen.mydaily.profile;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.User;

public interface ProfileInterface extends BaseDialogViewInterface
{
    void updateUserInformation(User user);

    void resetPrivacyValidDate();
}
