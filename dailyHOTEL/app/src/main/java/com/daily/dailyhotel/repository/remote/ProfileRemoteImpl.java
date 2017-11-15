package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.ProfileInterface;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.entity.UserTracking;
import com.daily.dailyhotel.repository.remote.model.UserData;
import com.daily.dailyhotel.repository.remote.model.UserTrackingData;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ProfileRemoteImpl extends BaseRemoteImpl implements ProfileInterface
{
    public ProfileRemoteImpl(@NonNull Context context)
    {
        super(context);
    }

    @Override
    public Observable<User> getProfile()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/users/profile"//
            : "NzMkNTEkMzYkNTkkNzckNjQkMTQkMjkkNTIkNTkkODckOSQ5NyQ5JDg5JDEk$MRUY4NUFGMRYjU0MjNI0Q0YyNjYyMjdCKMEQ5M0U5MMEY5NDQyQjcwNFTEC5NTKRCQS0ZFNPEU3RjFCOEMwMWOURDQJHjBEQTI4NRQ==$";

        return mDailyMobileService.getUserProfile(Crypto.getUrlDecoderEx(API)) //
            .subscribeOn(Schedulers.io()).map((userDataBaseDto) ->
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
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/users/profile/benefit"//
            : "NDUkODAkMjkkMjEkMzMkMzMkMzEkODgkMzgkNzUkOTMkNzgkMjMkOTYkNTQkODck$N0M1N0ZCQzE4ODgxQ0Y2QWTTEzMjBCOCCTRBYDRTDE4RDhGMDkyMXzLY0NjcxNDM3NEVCMDE2QTc3RRjPdDREZFWODUT2RBjACG5Rg==$";

        return mDailyMobileService.getUserBenefit(Crypto.getUrlDecoderEx(API)).subscribeOn(Schedulers.io()).map((userBenefitDataBaseDto) ->
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
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/users/profile/simple"//
            : "MjkkMzQkNzMkNzAkNDEkNzMkMTIkNjMkNDUkOTAkMzYkODIkOTkkNDMkMTAkMTYk$MDE0NTQ0OTYZFVRkZM1OTMyQzgxRDY0MPTM1QSVjlCNDcRMzNAEVBM0Q5QjcyRjY0NUFJCNjRFQ0U0ONNTFVDEMUE1RUMwOLTBCNQ=Z=$";

        return mDailyMobileService.getUserSimpleInformation(Crypto.getUrlDecoderEx(API)) //
            .subscribeOn(Schedulers.io()).map(userInformationDataBaseDto ->
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
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/users/tracking"//
            : "MzkkMzEkNTIkNjUkNDckMzUkOTAkMTIkODEkNDEkNDEkNDckOTYkMTckNjEkMTAk$MjAxNkUyMTYk5QRDMzXQjk4RkYwOTRCMzMYwRkLRGMjHKlWBQPTdDMXDkxQkTNQBNzAzMDEyMjQgwMjg0M0VCMUNU2Qzk3OTNCOWQw==$";

        return mDailyMobileService.getUserTracking(Crypto.getUrlDecoderEx(API)) //
            .subscribeOn(Schedulers.io()).map((BaseDto<UserTrackingData> userTrackingDataBaseDto) ->
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

    @Override
    public Observable<User> updateUserInformation(Map<String, String> params)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/users/profile"//
            : "MzEkNTAkNzMkMzAkMzgkNDQkMTckMzIkNjMkOTIkNjAkNzMkOCQ1MiQ1JDM3JA==$RTZGMNDc1TMjhGQTA2QXzM3MTQ3MzY1OTTPVJMFNjJBXOUVGQXTY5NJjg2MUzg5NCDQ3WNDdGQUFFCRjdDOEVODODQ5MTk5MjcO0OA==$";

        return mDailyMobileService.updateUserInformation(Crypto.getUrlDecoderEx(API), params) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<UserData>, User>()
            {
                @Override
                public User apply(BaseDto<UserData> userDataBaseDto) throws Exception
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
                }
            });
    }
}
