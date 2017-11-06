package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

public class ProfileLayout extends BaseLayout implements OnClickListener
{
    private TextView mEmailTextView, mNameTextView, mPhoneTextView, mBirthdayTextView, mPhoneVerifyView;
    private TextView mEmailLabelTextView, mNameLabelTextView, mPhoneLabelTextView, mBirthdayLabelTextView, mReferralTextView;
    private View mEmailLayout, mPasswordLayout;
    private View mPasswordUnderLine;

    public interface OnEventListener extends OnBaseEventListener
    {
        void startEditEmail();

        void startEditName(String name);

        void startEditPhone(String phoneNumber);

        void startEditPassword();

        void startEditBirthday(String birthday);

        void doSignOut();

        void doCodeCopy(String code);

        void doValidDateChange(int year);
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
        DailyToolbarView dailyToolbarView = (DailyToolbarView) view.findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_profile_activity);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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
        mEmailLayout = view.findViewById(R.id.emailLayout);
        mEmailLabelTextView = (TextView) mEmailLayout.findViewById(R.id.emailView);
        mEmailTextView = (TextView) mEmailLayout.findViewById(R.id.emailTextView);

        View nameLayout = view.findViewById(R.id.nameLayout);
        nameLayout.setOnClickListener(this);

        mNameLabelTextView = (TextView) nameLayout.findViewById(R.id.nameView);
        mNameTextView = (TextView) nameLayout.findViewById(R.id.nameTextView);

        View phoneLayout = view.findViewById(R.id.phoneLayout);
        phoneLayout.setOnClickListener(this);

        mPhoneLabelTextView = (TextView) phoneLayout.findViewById(R.id.phoneView);
        mPhoneTextView = (TextView) phoneLayout.findViewById(R.id.phoneTextView);
        mPhoneVerifyView = (TextView) phoneLayout.findViewById(R.id.phoneVerifyView);

        View birthdayLayout = view.findViewById(R.id.birthdayLayout);
        birthdayLayout.setOnClickListener(this);

        mBirthdayLabelTextView = (TextView) birthdayLayout.findViewById(R.id.birthdayView);
        mBirthdayTextView = (TextView) birthdayLayout.findViewById(R.id.birthdayTextView);

        mPasswordLayout = view.findViewById(R.id.passwordLayout);

        mPasswordUnderLine = view.findViewById(R.id.passwordUnderLine);

        mReferralTextView = (TextView) view.findViewById(R.id.referralTextView);

        View logoutView = view.findViewById(R.id.logoutView);
        logoutView.setOnClickListener(this);

        View codeCopyView = view.findViewById(R.id.codeCopyView);
        codeCopyView.setOnClickListener(this);
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
        if (DailyTextUtils.isTextEmpty(email) == true)
        {
            mEmailLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }

        mEmailLayout.setOnClickListener(null);
        mEmailTextView.setText(email);
        mEmailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_daily_small, 0, 0, 0);
        mEmailTextView.setCompoundDrawablePadding(ScreenUtils.dpToPx(mContext, 3));

        // 이름
        if (DailyTextUtils.isTextEmpty(name) == true)
        {
            mNameLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }

        mNameTextView.setText(name);

        // 생일
        if (DailyTextUtils.isTextEmpty(birthday) == true)
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
        if (DailyTextUtils.isTextEmpty(phone) == true)
        {
            mPhoneLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
        }

        mPhoneVerifyView.setVisibility(View.VISIBLE);
        mPhoneTextView.setText(phone);

        // 인증 여부
        if (isPhoneVerified == true && DailyTextUtils.isTextEmpty(verifiedDate) == false)
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
        if (DailyTextUtils.isTextEmpty(email) == true)
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

            mEmailTextView.setCompoundDrawablePadding(ScreenUtils.dpToPx(mContext, 3));
        }

        // 이름
        if (DailyTextUtils.isTextEmpty(name) == true)
        {
            mNameLabelTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
            mNameTextView.setText(null);
        } else
        {
            mNameTextView.setText(name);
        }

        // 생일
        if (DailyTextUtils.isTextEmpty(birthday) == true)
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
        if (DailyTextUtils.isTextEmpty(phone) == true)
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.emailLayout:
                ((OnEventListener) mOnEventListener).startEditEmail();
                break;

            case R.id.nameLayout:
                ((OnEventListener) mOnEventListener).startEditName(mNameTextView.getText().toString());
                break;

            case R.id.birthdayLayout:
                if (DailyTextUtils.isTextEmpty(mBirthdayTextView.getText().toString()) == false)
                {
                    ((OnEventListener) mOnEventListener).startEditBirthday((String) mBirthdayTextView.getTag());
                } else
                {
                    ((OnEventListener) mOnEventListener).startEditBirthday(null);
                }
                break;

            case R.id.phoneLayout:
                ((OnEventListener) mOnEventListener).startEditPhone(mPhoneTextView.getText().toString());
                break;

            case R.id.passwordLayout:
                ((OnEventListener) mOnEventListener).startEditPassword();
                break;

            case R.id.logoutView:
                ((OnEventListener) mOnEventListener).doSignOut();
                break;

            case R.id.codeCopyView:
                ((OnEventListener) mOnEventListener).doCodeCopy(mReferralTextView.getText().toString());
                break;
        }
    }
}
