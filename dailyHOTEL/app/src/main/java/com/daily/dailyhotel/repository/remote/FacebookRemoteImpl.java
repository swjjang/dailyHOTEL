package com.daily.dailyhotel.repository.remote;

import com.daily.dailyhotel.domain.SnsInterface;
import com.facebook.login.LoginManager;
import com.daily.base.util.ExLog;

public class FacebookRemoteImpl implements SnsInterface
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
            LoginManager.getInstance().logOut();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
