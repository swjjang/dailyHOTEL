package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class ProfileLayout extends BaseLayout implements OnClickListener
{
    private TextView mEmailEditText, mNameEditText, mPhoneEditText;

    public interface OnEventListener extends OnBaseEventListener
    {
        void showEditName();

        void showEditPhone();

        void showEditPassword();

        void doSignOut();
    }

    public ProfileLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);
        initLayoutForm(view);
    }

    private void initToolbar(View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_profile_activity), new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    private void initLayoutForm(View view)
    {
        mEmailEditText = (TextView) view.findViewById(R.id.emailTextView);
        mNameEditText = (TextView) view.findViewById(R.id.nameTextView);
        mPhoneEditText = (TextView) view.findViewById(R.id.phoneEditText);

        View nameLayout = view.findViewById(R.id.nameLayout);
        nameLayout.setOnClickListener(this);

        View phoneLayout = view.findViewById(R.id.phoneLayout);
        phoneLayout.setOnClickListener(this);

        View passwordLayout = view.findViewById(R.id.passwordLayout);
        passwordLayout.setOnClickListener(this);

        View logoutView = view.findViewById(R.id.signOutView);
        logoutView.setOnClickListener(this);
    }

    public void updateUserInformation(String sns, String email, String name, String phone)
    {
        mEmailEditText.setText(email);

        if(Util.isTextEmpty(sns) == true)
        {
            mEmailEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else
        {

        }

        mNameEditText.setText(name);
        mPhoneEditText.setText(phone);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.nameLayout:
                ((OnEventListener) mOnEventListener).showEditName();
                break;

            case R.id.phoneLayout:
                ((OnEventListener) mOnEventListener).showEditPhone();
                break;

            case R.id.passwordLayout:
                ((OnEventListener) mOnEventListener).showEditPassword();
                break;

            case R.id.signOutView:
                ((OnEventListener) mOnEventListener).doSignOut();
                break;
        }
    }
}
