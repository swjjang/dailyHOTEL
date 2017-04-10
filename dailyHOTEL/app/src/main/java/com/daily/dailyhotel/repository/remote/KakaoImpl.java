package com.daily.dailyhotel.repository.remote;

import com.daily.dailyhotel.domain.SnsInterface;
import com.kakao.usermgmt.UserManagement;
import com.twoheart.dailyhotel.util.ExLog;

public class KakaoImpl implements SnsInterface
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
            UserManagement.requestLogout(null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
