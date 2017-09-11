package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.ProfileInterface;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.entity.UserTracking;
import com.daily.dailyhotel.repository.remote.model.UserTrackingData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

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

    @Override
    public Observable<UserSimpleInformation> getUserSimpleInformation()
    {
        return DailyMobileAPI.getInstance(mContext).getUserSimpleInformation().map(userInformationDataBaseDto ->
        {
            UserSimpleInformation userSimpleInformation = null;

            if (userInformationDataBaseDto != null)
            {
                if (userInformationDataBaseDto.msgCode == 100 && userInformationDataBaseDto.data != null)
                {
                    userSimpleInformation = userInformationDataBaseDto.data.getUserInformation();
                } else
                {
                    throw new BaseException(userInformationDataBaseDto.msgCode, userInformationDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return userSimpleInformation;
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<UserTracking> getTracking()
    {
        return DailyMobileAPI.getInstance(mContext).getUserTracking().map((BaseDto<UserTrackingData> userTrackingDataBaseDto) ->
        {
            UserTracking userTracking = null;

            if (userTrackingDataBaseDto != null)
            {
                if (userTrackingDataBaseDto.msgCode == 100 && userTrackingDataBaseDto.data != null)
                {
                    userTracking = userTrackingDataBaseDto.data.getUserTracking();
                } else
                {
                    throw new BaseException(userTrackingDataBaseDto.msgCode, userTrackingDataBaseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return userTracking;
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
