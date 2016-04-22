package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class ProfileLayout extends BaseLayout implements OnClickListener
{
    private TextView mEmailTextView, mNameTextView, mPhoneTextView;
    private View mEmailLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void showEditEmail();

        void showEditName(String name);

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
        mEmailTextView = (TextView) view.findViewById(R.id.emailTextView);
        mNameTextView = (TextView) view.findViewById(R.id.nameTextView);
        mPhoneTextView = (TextView) view.findViewById(R.id.phoneTextView);

        mEmailLayout = view.findViewById(R.id.emailLayout);

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
        View emailArrowImage = mEmailLayout.findViewById(R.id.emailArrowImage);

        if (Util.isTextEmpty(email) == true && Util.isTextEmpty(sns) == false)
        {
            mEmailLayout.setOnClickListener(this);
            emailArrowImage.setVisibility(View.VISIBLE);
        } else
        {
            mEmailLayout.setOnClickListener(null);
            emailArrowImage.setVisibility(View.INVISIBLE);
        }

        mEmailTextView.setText(email);

        if (Util.isTextEmpty(sns) == true)
        {
            mEmailTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else if (Constants.FACEBOOK_USER.equalsIgnoreCase(sns) == true)
        {
            mEmailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fb_small, 0, 0, 0);
            mEmailTextView.setCompoundDrawablePadding(Util.dpToPx(mContext, 3));
        } else if (Constants.KAKAO_USER.equalsIgnoreCase(sns) == true)
        {
            mEmailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kakao_small, 0, 0, 0);
            mEmailTextView.setCompoundDrawablePadding(Util.dpToPx(mContext, 3));
        }

        mNameTextView.setText(name);
        mPhoneTextView.setText(phone);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.emailLayout:
                ((OnEventListener) mOnEventListener).showEditEmail();
                break;

            case R.id.nameLayout:
                ((OnEventListener) mOnEventListener).showEditName(mNameTextView.getText().toString());
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
