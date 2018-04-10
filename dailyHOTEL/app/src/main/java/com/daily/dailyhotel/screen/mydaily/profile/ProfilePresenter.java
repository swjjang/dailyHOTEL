package com.daily.dailyhotel.screen.mydaily.profile;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.mydaily.profile.leave.LeaveDailyActivity;
import com.daily.dailyhotel.screen.mydaily.profile.password.CheckPasswordActivity;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfileBirthdayActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfileEmailActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfileNameActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePasswordActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;

import java.util.Collections;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ProfilePresenter extends BaseExceptionPresenter<ProfileActivity, ProfileInterface> implements ProfileView.OnEventListener
{
    ProfileAnalyticsInterface mAnalytics;

    ProfileRemoteImpl mProfileRemoteImpl;
    private ConfigLocalImpl mConfigLocalImpl;

    private DailyDeepLink mDailyDeepLink;

    public interface ProfileAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onScreenLogout(Activity activity);

        void clearUserInformation(Activity activity);

        void setExceedBonus(Activity activity, boolean isExceedBonus);

        void onEventPrivacyValidMonth(Activity activity, int month);

        void onEventMemberLeaveClick(Activity activity);
    }

    public ProfilePresenter(@NonNull ProfileActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected ProfileInterface createInstanceViewInterface()
    {
        return new ProfileView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(ProfileActivity activity)
    {
        setContentView(R.layout.activity_profile_data);

        mAnalytics = new ProfileAnalyticsImpl();

        mConfigLocalImpl = new ConfigLocalImpl();
        mProfileRemoteImpl = new ProfileRemoteImpl();

        //        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (intent.hasExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK) == false)
        {
            return true;
        }

        try
        {
            mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK)));
        } catch (Exception e)
        {
            mDailyDeepLink = null;
            return false;
        }

        if (mDailyDeepLink != null)
        {
            if (mDailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                if (externalDeepLink.isProfileBirthdayView() == true)
                {
                    if (DailyHotel.isLogin() == true)
                    {
                        startEditBirthday(DailyUserPreference.getInstance(getActivity()).getBirthday());
                    } else
                    {
                        startEditBirthday(null);
                    }
                }
            }

            mDailyDeepLink.clear();
            mDailyDeepLink = null;
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (DailyHotel.isLogin() == false)
        {
            setRefresh(false);
            showLoginDialog();
        } else
        {
            setRefresh(true);
        }

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        mAnalytics.onScreen(getActivity());
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (DailyHotel.isLogin() == false)
        {
            setRefresh(false);
            showLoginDialog();
        } else
        {
            setRefresh(true);
        }

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case ProfileActivity.REQUEST_CODE_EDIT_PROFILE:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    screenLock(true);
                    setRefresh(true);
                }
                break;
            }

            case ProfileActivity.REQUEST_CODE_EDIT_PROFILE_BIRTHDAY:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    screenLock(true);

                    setRefresh(true);
                } else
                {
                    finish();
                }
                break;
            }

            case ProfileActivity.REQUEST_CODE_LOGIN:
            {
                if (resultCode != Activity.RESULT_OK)
                {
                    finish();
                }
                break;
            }

            case ProfileActivity.REQUEST_CODE_CHECK_PASSWORD:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    Intent intent = LeaveDailyActivity.newInstance(getActivity());
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(mProfileRemoteImpl.getProfile().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>()
        {
            @Override
            public void accept(User user) throws Exception
            {
                getViewInterface().updateUserInformation(user);

                if (user.verified == true)
                {
                    if (user.phoneVerified == true)
                    {
                        DailyPreference.getInstance(getActivity()).setVerification(true);
                    } else
                    {
                        // 인증 후 인증이 해지된 경우
                        if (DailyPreference.getInstance(getActivity()).isVerification() == true)
                        {
                            getViewInterface().showSimpleDialog(null, getString(R.string.message_invalid_verification), getString(R.string.dialog_btn_text_confirm), null);
                        }

                        DailyPreference.getInstance(getActivity()).setVerification(false);
                    }
                }

                addCompositeDisposable(mProfileRemoteImpl.getBenefit().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<UserBenefit>()
                {
                    @Override
                    public void accept(UserBenefit userBenefit) throws Exception
                    {
                        mAnalytics.setExceedBonus(getActivity(), userBenefit.exceedLimitedBonus);

                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        onHandleError(throwable);
                    }
                }));
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        //        getActivity().onBackPressed();
        finish();
    }

    private void showLoginDialog()
    {
        if (lock() == true)
        {
            return;
        }

        // 로그인 필요
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lock() == true)
                {
                    return;
                }

                startLogin();
            }
        };

        View.OnClickListener negativeListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        };

        String title = getActivity().getResources().getString(R.string.dialog_notice2);
        String message = getActivity().getResources().getString(R.string.dialog_message_profile_login);
        String positive = getActivity().getResources().getString(R.string.dialog_btn_text_yes);
        String negative = getActivity().getResources().getString(R.string.dialog_btn_text_no);

        getViewInterface().showSimpleDialog(title, message, positive, negative, positiveListener, negativeListener, new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish();
            }
        }, null, true);
    }

    void startLogin()
    {
        Intent intent = LoginActivity.newInstance(getActivity());
        startActivityForResult(intent, ProfileActivity.REQUEST_CODE_LOGIN);
    }

    @Override
    public void startEditEmail()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = EditProfileEmailActivity.newInstance(getActivity());
        startActivityForResult(intent, ProfileActivity.REQUEST_CODE_EDIT_PROFILE);
    }

    @Override
    public void startEditName(String name)
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = EditProfileNameActivity.newInstance(getActivity(), name);
        startActivityForResult(intent, ProfileActivity.REQUEST_CODE_EDIT_PROFILE);
    }

    @Override
    public void startEditPhone(String phoneNumber)
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = EditProfilePhoneActivity.newInstance(getActivity(), EditProfilePhoneActivity.Type.EDIT_PROFILE, phoneNumber);
        startActivityForResult(intent, ProfileActivity.REQUEST_CODE_EDIT_PROFILE);
    }

    @Override
    public void startEditPassword(String email)
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = EditProfilePasswordActivity.newInstance(getActivity(), email);
        startActivityForResult(intent, ProfileActivity.REQUEST_CODE_EDIT_PROFILE);
    }

    @Override
    public void startEditBirthday(String birthday)
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = EditProfileBirthdayActivity.newInstance(getActivity(), birthday);
        startActivityForResult(intent, ProfileActivity.REQUEST_CODE_EDIT_PROFILE_BIRTHDAY);
    }

    @Override
    public void doLogout()
    {
        if (lock() == true)
        {
            return;
        }

        /**
         * 로그 아웃시 내부 저장한 유저정보 초기화
         */
        View.OnClickListener posListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DailyPreference.getInstance(getActivity()).clear();
                DailyUserPreference.getInstance(getActivity()).clear();

                // 임시 저장된 리뷰 전체 삭제
                DailyDb dailyDb = DailyDbHelper.getInstance().open(getActivity());
                dailyDb.deleteAllTempReview();
                DailyDbHelper.getInstance().close();

                try
                {
                    LoginManager.getInstance().logOut();
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                try
                {
                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback()
                    {
                        @Override
                        public void onCompleteLogout()
                        {

                        }
                    });
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                unLockAll();
                DailyToast.showToast(getActivity(), R.string.toast_msg_logouted, Toast.LENGTH_SHORT);

                // Adjust에서 로그아웃시 기존 정보를 보냄으로 이벤트 발생후 삭제 필요.
                mAnalytics.onScreenLogout(getActivity());
                mAnalytics.clearUserInformation(getActivity());

                finish();
            }
        };

        getViewInterface().showSimpleDialog(getString(R.string.act_profile_btn_logout), getString(R.string.dialog_msg_chk_wanna_login),//
            getString(R.string.dialog_btn_text_logout), getString(R.string.dialog_btn_text_cancel), posListener, null, null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    unLockAll();
                }
            }, false);
    }

    @Override
    public void doValidMonthChange(int month)
    {
        if (lock() == true)
        {
            return;
        }

        Map<String, String> params = Collections.singletonMap("dataRetentionInMonth", Integer.toString(month));

        addCompositeDisposable(mProfileRemoteImpl.updateUserInformation(params).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>()
        {
            @Override
            public void accept(User user) throws Exception
            {
                getViewInterface().updateUserInformation(user);

                DailyToast.showToast(getActivity(), R.string.message_change_privacy_valid_date, Toast.LENGTH_SHORT);

                unLockAll();

                mAnalytics.onEventPrivacyValidMonth(getActivity(), month);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().resetPrivacyValidDate();
                DailyToast.showToast(getActivity(), R.string.message_change_privacy_valid_date_fail, Toast.LENGTH_SHORT);

                unLockAll();
            }
        }));
    }

    @Override
    public void onLeaveDailyClick(User user)
    {
        if (getActivity() == null || user == null || DailyHotel.isLogin() == false)
        {
            return;
        }

        if (Constants.DAILY_USER.equalsIgnoreCase(user.userType))
        {
            Intent intent = CheckPasswordActivity.newInstance(getActivity());
            startActivityForResult(intent, ProfileActivity.REQUEST_CODE_CHECK_PASSWORD);
        } else
        {
            Intent intent = LeaveDailyActivity.newInstance(getActivity());
            startActivity(intent);
        }

        try
        {
            mAnalytics.onEventMemberLeaveClick(getActivity());
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
        }
    }
}
