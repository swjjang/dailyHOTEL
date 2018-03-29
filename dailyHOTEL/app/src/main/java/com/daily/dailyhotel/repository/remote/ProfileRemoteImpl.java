package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.ProfileInterface;
import com.daily.dailyhotel.entity.LeaveInfo;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.daily.dailyhotel.entity.UserSimpleInformation;
import com.daily.dailyhotel.entity.UserTracking;
import com.daily.dailyhotel.repository.remote.model.LeaveInfoData;
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
            .subscribeOn(Schedulers.io()).map((userDataBaseDto) -> {
                User user;

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

        return mDailyMobileService.getUserBenefit(Crypto.getUrlDecoderEx(API)).subscribeOn(Schedulers.io()).map((userBenefitDataBaseDto) -> {
            UserBenefit userBenefit;

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
            .subscribeOn(Schedulers.io()).map(userInformationDataBaseDto -> {
                UserSimpleInformation userSimpleInformation;

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
            .subscribeOn(Schedulers.io()).map((BaseDto<UserTrackingData> userTrackingDataBaseDto) -> {
                UserTracking userTracking;

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
                    User user;

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

    @Override
    public Observable<Boolean> getCheckPassword(String password)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v5/users/check/password"//
            : "ODAkMjckNzMkMyQ3MSQ1OCQ0OSQ1NiQxMCQ0OCQ5NyQxMSQ1NSQ2NyQ0JDQ3JA==$NjZIJDN0QzNPDTFFREQzNjM3OEEyMzUH3QUQzNjQ5M0I5QzYBEQCkQL5QRTI4RWEE3UNDTA4ODM0QkM3OTThFVNkVGMTU0ZRjdFMA=E=$";

        return mDailyMobileService.getCheckPassword(Crypto.getUrlDecoderEx(API), password) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<Object>, Boolean>()
            {
                @Override
                public Boolean apply(BaseDto<Object> objectBaseDto) throws Exception
                {
                    boolean isSuccess;
                    if (objectBaseDto != null)
                    {
                        // 이 요청은 메세지 코드만 판단
                        if (objectBaseDto.msgCode == 100)
                        {
                            isSuccess = true;
                        } else
                        {
                            throw new BaseException(objectBaseDto.msgCode, objectBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return isSuccess;
                }
            });
    }

    @Override
    public Observable<LeaveInfo> getLeaveInfo()
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v6/users/leave/info"//
            : "NTkkMTQkNzIkNzUkNTkkODQkMjIkOTIkNDQkOTAkNiQ1MSQxNSQyOCQ5NCQ2NCQ=$NUFDREJQ5NkQ2RUSGU5RjU3MFEM3WNkJDRENEQTdGMDk1N0UQ1MkQW4NDg5MzQ2NWDCNYCQkIzN0YzRjCZEGMEYyOEIL0OUMDNGMQw==$";

        return mDailyMobileService.getLeaveInfo(Crypto.getUrlDecoderEx(API)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<LeaveInfoData>, LeaveInfo>()
            {
                @Override
                public LeaveInfo apply(BaseDto<LeaveInfoData> leaveInfoDataBaseDto) throws Exception
                {
                    LeaveInfo leaveInfo;

                    if (leaveInfoDataBaseDto != null)
                    {
                        if (leaveInfoDataBaseDto.data != null)
                        {
                            leaveInfo = leaveInfoDataBaseDto.data.getLeaveInfo();
                            leaveInfo.msg = leaveInfoDataBaseDto.msg;
                            leaveInfo.msgCode = leaveInfoDataBaseDto.msgCode;

                            if (leaveInfoDataBaseDto.msgCode != 100 && leaveInfoDataBaseDto.msgCode != 101)
                            {
                                throw new BaseException(leaveInfoDataBaseDto.msgCode, leaveInfoDataBaseDto.msg);
                            }
                        } else
                        {
                            throw new BaseException(leaveInfoDataBaseDto.msgCode, leaveInfoDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return leaveInfo;
                }
            });
    }

    @Override
    public Observable<Boolean> doUserLeaveDaily(int leaveReasonIdx)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v6/users/leave"//
            : "NjQkMjUkNDQkNjYkNzQkNDUkMjQkODIkNjIkNTIkNzkkOTEkNzUkMjQkMTAkMjEk$QzU3RkQxQzGIwNjE0ODE2KNEI0XRQJUVDQjIyMjc4NTlDQTcPX5QTQ4WMDUxMEI1NTUc0MEM1RLRjFXFQTFWg5QjcF2OTM0JNTQ0Mg==$";

        return mDailyMobileService.doUserLeaveDaily(Crypto.getUrlDecoderEx(API), leaveReasonIdx) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<Object>, Boolean>()
            {
                @Override
                public Boolean apply(BaseDto<Object> objectBaseDto) throws Exception
                {
                    boolean isSuccess;

                    if (objectBaseDto != null)
                    {
                        if (objectBaseDto.msgCode == 100)
                        {
                            isSuccess = true;
                        } else
                        {
                            throw new BaseException(objectBaseDto.msgCode, objectBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return isSuccess;
                }
            });
    }
}
