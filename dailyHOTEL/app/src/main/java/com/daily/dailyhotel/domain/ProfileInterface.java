package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.LeaveInfo;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.entity.UserTracking;

import java.util.Map;

import io.reactivex.Observable;

public interface ProfileInterface
{
    Observable<User> getProfile();

    Observable<UserBenefit> getBenefit();

    Observable<UserSimpleInformation> getUserSimpleInformation();

    Observable<UserTracking> getTracking();

    Observable<User> updateUserInformation(Map<String, String> params);

    Observable<Boolean> getCheckPassword(String password);

    Observable<LeaveInfo> getLeaveInfo();

    Observable<Boolean> doUserLeaveDaily(int leaveReasonIdx);
}
