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
    private TextView mEmailTextView, mNameTextView, mPhoneTextView, mPhoneVerifyView;
    private View mEmailLayout, mPasswordLayout;
    private View mPasswordUnderLine;

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
        mPhoneVerifyView = (TextView) view.findViewById(R.id.phoneVerifyView);
        mPasswordUnderLine = view.findViewById(R.id.passwordUnderLine);

        mEmailLayout = view.findViewById(R.id.emailLayout);

        View nameLayout = view.findViewById(R.id.nameLayout);
        nameLayout.setOnClickListener(this);

        View phoneLayout = view.findViewById(R.id.phoneLayout);
        phoneLayout.setOnClickListener(this);

        mPasswordLayout = view.findViewById(R.id.passwordLayout);

        View logoutView = view.findViewById(R.id.logoutView);
        logoutView.setOnClickListener(this);
    }

    public void updateUserInformation(String userType, String email, String name, String phone, boolean isVerified, boolean isPhoneVerified, String verifiedDate)
    {
        if (Constants.DAILY_USER.equalsIgnoreCase(userType) == true)
        {
            updateDailyUserInformation(email, name, phone, isPhoneVerified, verifiedDate);
        } else
        {
            updateSocialUserInformation(userType, email, name, phone);
        }
    }

    private void updateDailyUserInformation(String email, String name, String phone, boolean isPhoneVerified, String verifiedDate)
    {
        // 이메일
        mEmailLayout.setOnClickListener(null);
        mEmailTextView.setText(email);
        mEmailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_daily_small, 0, 0, 0);
        mEmailTextView.setCompoundDrawablePadding(Util.dpToPx(mContext, 3));

        // 이름
        mNameTextView.setText(name);

        // 휴대폰
        mPhoneVerifyView.setVisibility(View.VISIBLE);
        mPhoneTextView.setText(phone);

        // 인증 여부
        if (isPhoneVerified == true && Util.isTextEmpty(verifiedDate) == false)
        {
            mPhoneVerifyView.setTextColor(mContext.getResources().getColor(R.color.search_hint_text));
            mPhoneVerifyView.setText(mContext.getString(R.string.label_date_verification, verifiedDate.replaceAll("-", ".")));
        } else
        {
            mPhoneVerifyView.setTextColor(mContext.getResources().getColor(R.color.dh_theme_color));
            mPhoneVerifyView.setText(R.string.label_dont_verification);
        }

        // 페스워드
        mPasswordLayout.setVisibility(View.VISIBLE);
        mPasswordUnderLine.setVisibility(View.VISIBLE);
        mPasswordLayout.setOnClickListener(this);
    }

    private void updateSocialUserInformation(String userType, String email, String name, String phone)
    {
        // 이메일
        if (Util.isTextEmpty(email) == true)
        {
            mEmailTextView.setText(null);
            mEmailLayout.setOnClickListener(this);
        } else
        {
            mEmailTextView.setText(email);
            mEmailLayout.setOnClickListener(null);

            if (Constants.FACEBOOK_USER.equalsIgnoreCase(userType) == true)
            {
                mEmailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fb_small, 0, 0, 0);

            } else if (Constants.KAKAO_USER.equalsIgnoreCase(userType) == true)
            {
                mEmailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kakao_small, 0, 0, 0);
            }

            mEmailTextView.setCompoundDrawablePadding(Util.dpToPx(mContext, 3));
        }

        // 이름
        mNameTextView.setText(name);

        // 휴대폰 번호
        mPhoneTextView.setText(phone);
        mPhoneVerifyView.setVisibility(View.GONE);

        // 패스워드
        mPasswordLayout.setVisibility(View.GONE);
        mPasswordUnderLine.setVisibility(View.GONE);
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

            case R.id.logoutView:
                ((OnEventListener) mOnEventListener).doSignOut();
                break;
        }
    }
}
