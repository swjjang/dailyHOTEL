package com.daily.dailyhotel.screen.mydaily.profile;

import com.daily.base.BaseDialogViewInterface;

public interface ProfileViewInterface extends BaseDialogViewInterface
{
    void setEmail(String userType, String email);

    void setName(String name);

    void setBirthday(String birthday);

    void setPhoneNumber(String phoneNumber);

    void setPhoneNumberVerifiedVisible(boolean visible);

    void setPhoneNumberVerified(boolean isPhoneVerified, String verifiedDate);

    void setPasswordVisible(boolean visible);

    void setReferralCode(String referralCode);
}
