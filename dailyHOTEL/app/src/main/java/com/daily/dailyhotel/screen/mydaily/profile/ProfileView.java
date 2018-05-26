package com.daily.dailyhotel.screen.mydaily.profile;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.RadioGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.User;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityProfileDataBinding;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

public class ProfileView extends BaseDialogView<ProfileView.OnEventListener, ActivityProfileDataBinding> implements ProfileInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void startEditEmail();

        void startEditName(String name);

        void startEditPhone(String phoneNumber);

        void startEditPassword(String email);

        void startEditBirthday(String birthday);

        void doLogout();

        void doValidMonthChange(int month);

        void onLeaveDailyClick(User user);
    }

    public ProfileView(BaseActivity baseActivity, ProfileView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityProfileDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initLayout(viewDataBinding);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivityProfileDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
        viewDataBinding.toolbarView.setTitleText(R.string.actionbar_title_profile_activity);
    }

    private void initLayout(ActivityProfileDataBinding viewDataBinding)
    {
        viewDataBinding.nameLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                String name = getViewDataBinding().nameTextView.getText().toString();
                getEventListener().startEditName(name);
            }
        });

        viewDataBinding.phoneLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                String phone = getViewDataBinding().phoneTextView.getText().toString();
                getEventListener().startEditPhone(phone);
            }
        });

        viewDataBinding.birthdayLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                String birthday = (String) getViewDataBinding().birthdayTextView.getTag();

                getEventListener().startEditBirthday(DailyTextUtils.isTextEmpty(birthday) == false ? birthday : null);
            }
        });

        viewDataBinding.logoutView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().doLogout();
            }
        });

        // 개인정보 변경의 경우 리스너를 재 연결 해야 해서 따로 구현

        ClickableSpan clickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View view)
            {
                User user = (User) view.getTag();
                getEventListener().onLeaveDailyClick(user);
            }
        };

        String fullText = getString(R.string.label_leave_daily_full_text);
        String filterText = getString(R.string.label_leave_daily_linked_text);
        int start = fullText.indexOf(filterText);
        int end = start + filterText.length();

        SpannableString spannableString = new SpannableString(fullText);
        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.default_text_c929292)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewDataBinding.leaveDailyTextView.setText(spannableString);

        viewDataBinding.leaveDailyTextView.setClickable(true);
        viewDataBinding.leaveDailyTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void updateUserInformation(User user)
    {
        if (user == null)
        {
            return;
        }

        if (Constants.DAILY_USER.equalsIgnoreCase(user.userType) == true)
        {
            updateDailyUserInformation(user);
        } else
        {
            updateSocialUserInformation(user);
        }
    }

    @Override
    public void resetPrivacyValidDate()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int month = (int) getViewDataBinding().privacyValidDateRadioGroup.getTag();
        setPrivacyValidMonth(month);
    }

    private void updateDailyUserInformation(User user)
    {
        if (getViewDataBinding() == null || getContext() == null || user == null)
        {
            return;
        }

        // 이메일
        if (DailyTextUtils.isTextEmpty(user.email) == true)
        {
            getViewDataBinding().emailView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
        }

        getViewDataBinding().emailLayout.setOnClickListener(null);
        getViewDataBinding().emailTextView.setText(user.email);
        getViewDataBinding().emailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_daily_small, 0, 0, 0);
        getViewDataBinding().emailTextView.setCompoundDrawablePadding(ScreenUtils.dpToPx(getContext(), 3));

        // 이름
        if (DailyTextUtils.isTextEmpty(user.name) == true)
        {
            getViewDataBinding().nameView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
        }

        getViewDataBinding().nameTextView.setText(user.name);

        // 생일
        if (DailyTextUtils.isTextEmpty(user.birthday) == true)
        {
            getViewDataBinding().birthdayView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
            getViewDataBinding().birthdayTextView.setText(null);
        } else
        {
            try
            {
                getViewDataBinding().birthdayTextView.setText(DailyCalendar.convertDateFormatString(user.birthday, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
                getViewDataBinding().birthdayTextView.setTag(user.birthday);
            } catch (Exception e)
            {
                ExLog.d(e.toString());

                getViewDataBinding().birthdayTextView.setText(null);
            }
        }

        // 휴대폰
        if (DailyTextUtils.isTextEmpty(user.phone) == true)
        {
            getViewDataBinding().phoneView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
        }

        getViewDataBinding().phoneVerifyView.setVisibility(View.VISIBLE);
        getViewDataBinding().phoneTextView.setText(user.phone);

        String verifiedDate;
        try
        {
            verifiedDate = DailyCalendar.convertDateFormatString(user.phoneVerifiedAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
        } catch (Exception e)
        {
            verifiedDate = null;
        }

        // 인증 여부
        if (user.phoneVerified == true && DailyTextUtils.isTextEmpty(verifiedDate) == false)
        {
            getViewDataBinding().phoneVerifyView.setTextColor(getContext().getResources().getColor(R.color.search_hint_text));
            getViewDataBinding().phoneVerifyView.setText(getContext().getString(R.string.label_date_verification, verifiedDate.replaceAll("-", ".")));
        } else
        {
            getViewDataBinding().phoneVerifyView.setTextColor(getContext().getResources().getColor(R.color.dh_theme_color));
            getViewDataBinding().phoneVerifyView.setText(R.string.label_dont_verification);
        }

        // 페스워드
        getViewDataBinding().passwordLayout.setVisibility(View.VISIBLE);
        getViewDataBinding().passwordUnderLine.setVisibility(View.VISIBLE);
        getViewDataBinding().passwordLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().startEditPassword(user.email);
            }
        });

        setPrivacyValidMonth(user.dataRetentionInMonth);

        // 회원 탈퇴를 위한 user 정보 저장
        getViewDataBinding().leaveDailyTextView.setTag(user);
    }

    private void updateSocialUserInformation(User user)
    {
        if (getViewDataBinding() == null || getContext() == null || user == null)
        {
            return;
        }

        // 이메일
        if (DailyTextUtils.validEmail(user.email) == false)
        {
            getViewDataBinding().emailView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
            getViewDataBinding().emailTextView.setText(null);
            getViewDataBinding().emailLayout.setOnClickListener(v -> getEventListener().startEditEmail());
        } else
        {
            getViewDataBinding().emailTextView.setText(user.email);
            getViewDataBinding().emailLayout.setOnClickListener(null);

            if (Constants.FACEBOOK_USER.equalsIgnoreCase(user.userType) == true)
            {
                getViewDataBinding().emailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fb_small, 0, 0, 0);

            } else if (Constants.KAKAO_USER.equalsIgnoreCase(user.userType) == true)
            {
                getViewDataBinding().emailTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kakao_small, 0, 0, 0);
            }

            getViewDataBinding().emailTextView.setCompoundDrawablePadding(ScreenUtils.dpToPx(getContext(), 3));
        }

        // 이름
        if (DailyTextUtils.isTextEmpty(user.name) == true)
        {
            getViewDataBinding().nameView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
            getViewDataBinding().nameTextView.setText(null);
        } else
        {
            getViewDataBinding().nameTextView.setText(user.name);
        }

        // 생일
        if (DailyTextUtils.isTextEmpty(user.birthday) == true)
        {
            getViewDataBinding().birthdayView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
            getViewDataBinding().birthdayTextView.setText(null);
        } else
        {
            try
            {
                getViewDataBinding().birthdayTextView.setText(DailyCalendar.convertDateFormatString(user.birthday, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
                getViewDataBinding().birthdayTextView.setTag(user.birthday);
            } catch (Exception e)
            {
                ExLog.d(e.toString());

                getViewDataBinding().birthdayTextView.setText(null);
            }
        }

        // 휴대폰 번호
        // 휴대폰
        if (DailyTextUtils.isTextEmpty(user.phone) == true)
        {
            getViewDataBinding().phoneView.setTextColor(getContext().getResources().getColor(R.color.default_text_c4d4d4d));
        }

        getViewDataBinding().phoneTextView.setText(user.phone);
        getViewDataBinding().phoneVerifyView.setVisibility(View.GONE);

        // 패스워드
        getViewDataBinding().passwordLayout.setVisibility(View.GONE);
        getViewDataBinding().passwordUnderLine.setVisibility(View.GONE);

        setPrivacyValidMonth(user.dataRetentionInMonth);

        // 회원 탈퇴를 위한 user 정보 저장
        getViewDataBinding().leaveDailyTextView.setTag(user);
    }

    private void setPrivacyValidMonth(int month)
    {
        if (getViewDataBinding() == null)
        {
            getViewDataBinding().privacyValidDateRadioGroup.setOnCheckedChangeListener(null);
        }

        int buttonResId;
        switch (month)
        {
            case 60:
                buttonResId = R.id.yearRadioButton5;
                break;
            case 36:
                buttonResId = R.id.yearRadioButton3;
                break;
            case 12:
            default:
                buttonResId = R.id.yearRadioButton1;
                break;
        }

        getViewDataBinding().privacyValidDateRadioGroup.check(buttonResId);
        getViewDataBinding().privacyValidDateRadioGroup.setTag(month);
        getViewDataBinding().privacyValidDateRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId)
        {
            int month;
            switch (checkedId)
            {
                case R.id.yearRadioButton3:
                    month = 36;
                    break;

                case R.id.yearRadioButton5:
                    month = 60;
                    break;

                case R.id.yearRadioButton1:
                default:
                    month = 12;
                    break;
            }

            getEventListener().doValidMonthChange(month);
        }
    };
}
