package com.daily.dailyhotel.screen.mydaily.profile;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class ProfileView extends BaseView<ProfileView.OnEventListener> implements ProfileViewInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void startEditEmail();

        void startEditName(String name);

        void startEditPhone(String phoneNumber);

        void startEditPassword();

        void startEditBirthday(String birthday);

        void doLogOut();

        void doCodeCopy(String code);
    }

    public ProfileView(BaseActivity baseActivity, ProfileView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(View view)
    {


        initToolbar(view);

    }

    private void initToolbar(View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mActivity, toolbar);
        dailyToolbarLayout.initToolbar(mActivity.getString(R.string.actionbar_title_profile_activity)//
            , v -> mOnEventListener.finish());
    }

    @Override
    public void setText(String text)
    {

    }
}
