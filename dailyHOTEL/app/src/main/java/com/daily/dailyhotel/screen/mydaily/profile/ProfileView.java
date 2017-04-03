package com.daily.dailyhotel.screen.mydaily.profile;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;

public class ProfileView extends BaseView<ProfileView.OnEventListener> implements ProfileViewInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onClick(View view);
    }

    public ProfileView(BaseActivity baseActivity, ProfileView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(View view)
    {

    }

    @Override
    public void setText(String text)
    {

    }
}
