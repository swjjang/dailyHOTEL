package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.ProfileInterface;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.twoheart.dailyhotel.network.DailyMobileAPI;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ProfileRemoteImpl implements ProfileInterface
{
    private Context mContext;

    public ProfileRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<User> getProfile()
    {
        //        return DailyMobileAPI.getInstance(mContext).getUserProfile().map(new Function<BaseDto<UserData>, User>()
        return DailyMobileAPI.getInstance(mContext).getUserProfile().map((userDataBaseDto) ->
        {
            User user = null;

            if (userDataBaseDto != null)
            {
                if (userDataBaseDto.msgCode == 100 && userDataBaseDto.data != null)
                {
                    user = userDataBaseDto.data.getUser();
                } else
                {
                    throw new BaseException(userDataBaseDto.msgCode, userDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return user;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<UserBenefit> getBenefit()
    {
        return DailyMobileAPI.getInstance(mContext).getUserBenefit().map((userBenefitDataBaseDto) ->
        {
            UserBenefit userBenefit = null;

            if (userBenefitDataBaseDto != null)
            {
                if (userBenefitDataBaseDto.msgCode == 100 && userBenefitDataBaseDto.data != null)
                {
                    userBenefit = userBenefitDataBaseDto.data.getUserBenefit();
                } else
                {
                    throw new BaseException(userBenefitDataBaseDto.msgCode, userBenefitDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return userBenefit;
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
