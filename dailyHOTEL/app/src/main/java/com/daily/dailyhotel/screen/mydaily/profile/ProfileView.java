package com.daily.dailyhotel.screen.mydaily.profile;

import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityProfileDataBinding;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

public class ProfileView extends BaseDialogView<ProfileView.OnEventListener, ActivityProfileDataBinding> implements ProfileViewInterface
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
    protected void setContentView(ActivityProfileDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        // 이름
        viewDataBinding.nameLayout.setOnClickListener(v -> getEventListener().startEditName(viewDataBinding.nameTextView.getText().toString()));

        // 전화번호
        viewDataBinding.phoneLayout.setOnClickListener(v -> getEventListener().startEditPhone(viewDataBinding.phoneTextView.getText().toString()));

        // 생일
        viewDataBinding.birthdayLayout.setOnClickListener(v ->
        {
            if (DailyTextUtils.isTextEmpty(viewDataBinding.birthdayTextView.getText().toString()) == false)
            {
                getEventListener().startEditBirthday((String) viewDataBinding.birthdayTextView.getTag());
            } else
            {
                getEventListener().startEditBirthday(null);
            }
        });

        // 로긍 아웃
        viewDataBinding.logoutView.setOnClickListener(v -> getEventListener().onLogOutClick());

        // 코드 복사
        viewDataBinding.codeCopyView.setOnClickListener(v -> getEventListener().onCodeCopyClick(viewDataBinding.referralTextView.getText().toString()));
    }

    @Override
    public void setToolbarTitle(String title)
    {

    }

    private void initToolbar(ActivityProfileDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setTitleText(R.string.actionbar_title_profile_activity);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    @Override
    public void setEmail(String userType, String email)
    {
        ActivityProfileDataBinding viewDataBinding = getViewDataBinding();

        if (viewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(userType) == false)
        {
            userType = Constants.DAILY_USER;
        }

        if (DailyTextUtils.isTextEmpty(email) == true)
        {
            viewDataBinding.emailView.setTextColor(getColor(R.color.default_text_c323232));
        }

        viewDataBinding.emailTextView.setText(email);

        switch (userType)
        {
            case Constants.FACEBOOK_USER:
                if (DailyTextUtils.isTextEmpty(email) == true)
                {
                    viewDataBinding.emailTextView.setOnClickListener(v -> getEventListener().startEditEmail());
                }

                viewDataBinding.emailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fb_small, 0, 0, 0);
                break;

            case Constants.KAKAO_USER:
                if (DailyTextUtils.isTextEmpty(email) == true)
                {
                    viewDataBinding.emailTextView.setOnClickListener(v -> getEventListener().startEditEmail());
                }

                viewDataBinding.emailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kakao_small, 0, 0, 0);
                break;

            case Constants.DAILY_USER:
            default:
                viewDataBinding.emailTextView.setOnClickListener(null);
                viewDataBinding.emailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_daily_small, 0, 0, 0);
                break;
        }

        viewDataBinding.emailTextView.setCompoundDrawablePadding(ScreenUtils.dpToPx(getContext(), 3));
    }

    @Override
    public void setName(String name)
    {
        ActivityProfileDataBinding viewDataBinding = getViewDataBinding();

        if (viewDataBinding == null)
        {
            return;
        }

        // 이름
        if (DailyTextUtils.isTextEmpty(name) == true)
        {
            viewDataBinding.nameView.setTextColor(getColor(R.color.default_text_c323232));
        }

        viewDataBinding.nameTextView.setText(name);
    }

    @Override
    public void setBirthday(String birthday)
    {
        ActivityProfileDataBinding viewDataBinding = getViewDataBinding();

        if (viewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(birthday) == true)
        {
            viewDataBinding.birthdayView.setTextColor(getColor(R.color.default_text_c323232));
        } else
        {
            try
            {
                viewDataBinding.birthdayTextView.setText(DailyCalendar.convertDateFormatString(birthday, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
                viewDataBinding.birthdayTextView.setTag(birthday);
            } catch (Exception e)
            {
                ExLog.d(e.toString());

                viewDataBinding.birthdayTextView.setText(null);
            }
        }
    }

    @Override
    public void setPhoneNumber(String phoneNumber)
    {
        ActivityProfileDataBinding viewDataBinding = getViewDataBinding();

        if (viewDataBinding == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(phoneNumber) == true)
        {
            viewDataBinding.phoneView.setTextColor(getColor(R.color.default_text_c323232));
        }

        viewDataBinding.phoneTextView.setText(phoneNumber);
    }

    @Override
    public void setPhoneNumberVerifiedVisible(boolean visible)
    {
        ActivityProfileDataBinding viewDataBinding = getViewDataBinding();

        if (viewDataBinding == null)
        {
            return;
        }

        if (visible == true)
        {
            viewDataBinding.phoneVerifyView.setVisibility(View.VISIBLE);
        } else
        {
            viewDataBinding.phoneVerifyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPhoneNumberVerified(boolean isPhoneVerified, String verifiedDate)
    {
        ActivityProfileDataBinding viewDataBinding = getViewDataBinding();

        if (viewDataBinding == null)
        {
            return;
        }

        // 인증 여부
        if (isPhoneVerified == true && DailyTextUtils.isTextEmpty(verifiedDate) == false)
        {
            viewDataBinding.phoneVerifyView.setTextColor(getColor(R.color.search_hint_text));
            viewDataBinding.phoneVerifyView.setText(getString(R.string.label_date_verification, verifiedDate.replaceAll("-", ".")));
        } else
        {
            viewDataBinding.phoneVerifyView.setTextColor(getColor(R.color.dh_theme_color));
            viewDataBinding.phoneVerifyView.setText(R.string.label_dont_verification);
        }
    }

    @Override
    public void setPasswordVisible(boolean visible)
    {
        ActivityProfileDataBinding viewDataBinding = getViewDataBinding();

        if (viewDataBinding == null)
        {
            return;
        }

        if (visible == true)
        {
            viewDataBinding.passwordLayout.setVisibility(View.VISIBLE);
            viewDataBinding.passwordUnderLine.setVisibility(View.VISIBLE);
            viewDataBinding.passwordLayout.setOnClickListener(v -> getEventListener().startEditPassword());
        } else
        {
            viewDataBinding.passwordLayout.setVisibility(View.GONE);
            viewDataBinding.passwordUnderLine.setVisibility(View.GONE);
        }
    }

    @Override
    public void setReferralCode(String referralCode)
    {
        ActivityProfileDataBinding viewDataBinding = getViewDataBinding();

        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.referralTextView.setText(referralCode);
    }
}
