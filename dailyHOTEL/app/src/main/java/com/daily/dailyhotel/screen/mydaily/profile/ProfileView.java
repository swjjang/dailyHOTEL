package com.daily.dailyhotel.screen.mydaily.profile;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityProfileDataBinding;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
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

        void onLogOutClick();

        void onCodeCopyClick(String code);
    }

    public ProfileView(BaseActivity baseActivity, ProfileView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }


    @Override
    protected void initLayout(View view, ViewDataBinding viewDataBinding)
    {
        if (view == null || viewDataBinding == null)
        {
            return;
        }

        ActivityProfileDataBinding activityProfileDataBinding = (ActivityProfileDataBinding) viewDataBinding;

        initToolbar(activityProfileDataBinding);

        // 이름
        activityProfileDataBinding.nameLayout.setOnClickListener(v -> mOnEventListener.startEditName(activityProfileDataBinding.nameTextView.getText().toString()));

        // 전화번호
        activityProfileDataBinding.phoneLayout.setOnClickListener(v -> mOnEventListener.startEditPhone(activityProfileDataBinding.phoneTextView.getText().toString()));

        // 생일
        activityProfileDataBinding.birthdayLayout.setOnClickListener(v ->
        {
            if (Util.isTextEmpty(activityProfileDataBinding.birthdayTextView.getText().toString()) == false)
            {
                mOnEventListener.startEditBirthday((String) activityProfileDataBinding.birthdayTextView.getTag());
            } else
            {
                mOnEventListener.startEditBirthday(null);
            }
        });

        // 로긍 아웃
        activityProfileDataBinding.logoutView.setOnClickListener(v -> mOnEventListener.onLogOutClick());

        // 코드 복사
        activityProfileDataBinding.codeCopyView.setOnClickListener(v -> mOnEventListener.onCodeCopyClick(activityProfileDataBinding.referralTextView.getText().toString()));
    }

    private void initToolbar(ActivityProfileDataBinding activityProfileDataBinding)
    {
        if (activityProfileDataBinding == null)
        {
            return;
        }

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mActivity, activityProfileDataBinding.toolbar);
        dailyToolbarLayout.initToolbar(mActivity.getString(R.string.actionbar_title_profile_activity)//
            , v -> mOnEventListener.finish());
    }

    public void updateUserInformation(String userType, String email, String name, String phone, String birthday, //
                                      String referralCode, boolean isVerified, boolean isPhoneVerified, String verifiedDate)
    {
        if (Constants.DAILY_USER.equalsIgnoreCase(userType) == true)
        {
            updateDailyUserInformation(email, name, phone, birthday, referralCode, isPhoneVerified, verifiedDate);
        } else
        {
            updateSocialUserInformation(userType, email, name, phone, birthday, referralCode);
        }
    }

    private void updateDailyUserInformation(String email, String name, String phone, String birthday, //
                                            String referralCode, boolean isPhoneVerified, String verifiedDate)
    {
        // 이메일
        if (Util.isTextEmpty(email) == true)
        {
            mEmailLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }

        mEmailLayout.setOnClickListener(null);
        mEmailTextView.setText(email);
        mEmailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_daily_small, 0, 0, 0);
        mEmailTextView.setCompoundDrawablePadding(Util.dpToPx(mContext, 3));

        // 이름
        if (Util.isTextEmpty(name) == true)
        {
            mNameLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }

        mNameTextView.setText(name);

        // 생일
        if (Util.isTextEmpty(birthday) == true)
        {
            mBirthdayLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
            mBirthdayTextView.setText(null);
        } else
        {
            try
            {
                mBirthdayTextView.setText(DailyCalendar.convertDateFormatString(birthday, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
                mBirthdayTextView.setTag(birthday);
            } catch (Exception e)
            {
                ExLog.d(e.toString());

                mBirthdayTextView.setText(null);
            }
        }

        // 휴대폰
        if (Util.isTextEmpty(phone) == true)
        {
            mPhoneLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }

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

        mReferralTextView.setText(referralCode);
    }

    private void updateSocialUserInformation(String userType, String email, String name, String phone, String birthday, String referralCode)
    {
        // 이메일
        if (Util.isTextEmpty(email) == true)
        {
            mEmailLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
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
        if (Util.isTextEmpty(name) == true)
        {
            mNameLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
            mNameTextView.setText(null);
        } else
        {
            mNameTextView.setText(name);
        }

        // 생일
        if (Util.isTextEmpty(birthday) == true)
        {
            mBirthdayLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
            mBirthdayTextView.setText(null);
        } else
        {
            try
            {
                mBirthdayTextView.setText(DailyCalendar.convertDateFormatString(birthday, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
                mBirthdayTextView.setTag(birthday);
            } catch (Exception e)
            {
                ExLog.d(e.toString());

                mBirthdayTextView.setText(null);
            }
        }

        // 휴대폰 번호
        // 휴대폰
        if (Util.isTextEmpty(phone) == true)
        {
            mPhoneLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }

        mPhoneTextView.setText(phone);
        mPhoneVerifyView.setVisibility(View.GONE);

        // 패스워드
        mPasswordLayout.setVisibility(View.GONE);
        mPasswordUnderLine.setVisibility(View.GONE);

        mReferralTextView.setText(referralCode);
    }
}
