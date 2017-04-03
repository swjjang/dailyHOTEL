package com.daily.dailyhotel.screen.mydaily.profile;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.domain.ProfileInterface;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.network.model.UserData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class ProfileNetworkImpl implements ProfileInterface
{
    private Context mContext;

    public ProfileNetworkImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Flowable<User> getProfile()
    {
        return DailyMobileAPI.getInstance(mContext).getUserProfile().map(new Function<BaseDto<UserData>, User>()
        {
            @Override
            public User apply(BaseDto<UserData> userBaseDto) throws Exception
            {
                User user = null;

                if (userBaseDto != null)
                {
                    if (userBaseDto.msgCode == 100 && userBaseDto.data != null)
                    {
                        user = userBaseDto.data.getUser();
                    }
                }

                return user;
            }
        }).s;
    }
}
