package com.daily.dailyhotel.repository.remote;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.SnsInterface;
import com.kakao.usermgmt.UserManagement;

public class KakaoRemoteImpl implements SnsInterface
{
    @Override
    public void initialize()
    {

    }

    @Override
    public void onRegister()
    {

    }

    @Override
    public void logOut()
    {
        try
        {
            UserManagement.getInstance().requestLogout(null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
