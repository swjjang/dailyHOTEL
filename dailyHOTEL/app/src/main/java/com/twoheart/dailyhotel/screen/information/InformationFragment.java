package com.twoheart.dailyhotel.screen.information;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.event.EventListActivity;
import com.twoheart.dailyhotel.screen.information.bonus.BonusActivity;
import com.twoheart.dailyhotel.screen.information.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.screen.information.member.ProfileActivity;
import com.twoheart.dailyhotel.screen.information.terms.LocationTermsActivity;
import com.twoheart.dailyhotel.screen.information.terms.PrivacyActivity;
import com.twoheart.dailyhotel.screen.information.terms.ProtectChildTermsActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermActivity;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InformationFragment extends BaseFragment implements Constants, OnClickListener
{
    private View mProfileLayout, mCreditcardLayout;
    private View mNewEventIconView;
    private TextView mEnabledSMS, mEnabledPush;
    private BroadcastReceiver mNewEventBroadcastReceiver;
    private boolean mIsAttach;
    //    private View mInformationScrollView, mInformationLayout, mDailyInformationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        BaseActivity baseActivity = (BaseActivity) getActivity();

        initToolbar(baseActivity, view);

        mProfileLayout = view.findViewById(R.id.profileLayout);
        mCreditcardLayout = view.findViewById(R.id.creditcardLayout);
        View bonusLayout = view.findViewById(R.id.bonusLayout);
        View eventLayout = view.findViewById(R.id.eventLayout);
        View callLayout = view.findViewById(R.id.callLayout);
        View mailLayout = view.findViewById(R.id.mailLayout);
        View aboutLayout = view.findViewById(R.id.aboutLayout);

        mProfileLayout.setOnClickListener(this);
        mCreditcardLayout.setOnClickListener(this);
        bonusLayout.setOnClickListener(this);
        eventLayout.setOnClickListener(this);
        callLayout.setOnClickListener(this);
        mailLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);

        mNewEventIconView = eventLayout.findViewById(R.id.newIconView);

        TextView pushTextView = (TextView) view.findViewById(R.id.pushTextView);

        if (DailyPreference.getInstance(baseActivity).isAllowPush() == true)
        {
            pushTextView.setText(R.string.label_on);
        } else
        {
            pushTextView.setText(R.string.label_off);
        }

        pushTextView.setOnClickListener(this);

        mEnabledSMS = (TextView) view.findViewById(R.id.smsTextView);
        setEnabledSMS(baseActivity, DailyPreference.getInstance(baseActivity).isAllowSMS());
        mEnabledSMS.setOnClickListener(this);

        setSigninLayout(false);
        initSnsLayout(view);
        initBusinessLayout(baseActivity, view);
        initTermsLayout(baseActivity, view);

        TextView versionTextView = (TextView) view.findViewById(R.id.versionTextView);
        versionTextView.setText(getString(R.string.label_version, DailyHotel.VERSION));

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        mIsAttach = true;
    }

    private void initToolbar(BaseActivity baseActivity, View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(baseActivity, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_setting_frag), null, false);
    }

    private void initSnsLayout(View view)
    {
        View viewFacebook = view.findViewById(R.id.facebookLinkView);
        View viewInstagram = view.findViewById(R.id.instagramLinkView);
        View viewNaver = view.findViewById(R.id.naverLinkView);

        viewFacebook.setOnClickListener(this);
        viewInstagram.setOnClickListener(this);
        viewNaver.setOnClickListener(this);
    }

    private void initBusinessLayout(BaseActivity baseActivity, View view)
    {
        TextView business1TextView = (TextView) view.findViewById(R.id.business1TextView);
        TextView business2TextView = (TextView) view.findViewById(R.id.business2TextView);
        TextView business3TextView = (TextView) view.findViewById(R.id.business3TextView);

        business1TextView.setText(getString(R.string.frag_about_business_license01//
            , DailyPreference.getInstance(baseActivity).getCompanyCEO()//
            , DailyPreference.getInstance(baseActivity).getCompanyBizRegNumber()//
            , DailyPreference.getInstance(baseActivity).getCompanyPhoneNumber()));

        if (Util.getLCDWidth(baseActivity) < 720)
        {
            String text = DailyPreference.getInstance(baseActivity).getCompanyAddress() + '\n'//
                + getString(R.string.frag_about_business_license02//
                , DailyPreference.getInstance(baseActivity).getCompanyItcRegNumber());

            business2TextView.setText(text);
        } else
        {
            String text = DailyPreference.getInstance(baseActivity).getCompanyAddress() + " | "//
                + getString(R.string.frag_about_business_license02//
                , DailyPreference.getInstance(baseActivity).getCompanyItcRegNumber());

            business2TextView.setText(text);
        }

        business3TextView.setText(getString(R.string.frag_about_business_license03//
            , DailyPreference.getInstance(baseActivity).getCompanyPrivacyEmail()));
    }

    private void initTermsLayout(BaseActivity baseActivity, View view)
    {
        LinearLayout termsLayout = (LinearLayout) view.findViewById(R.id.termsLayout);

        if (Util.getLCDWidth(baseActivity) < 720)
        {
            termsLayout.setOrientation(LinearLayout.VERTICAL);
        }

        View termsView = view.findViewById(R.id.termsView);
        View personalView = view.findViewById(R.id.personalView);
        View locationTermsView = view.findViewById(R.id.locationTermsView);
        View protectChildTermsView = view.findViewById(R.id.protectChildTermsView);

        termsView.setOnClickListener(this);
        personalView.setOnClickListener(this);
        locationTermsView.setOnClickListener(this);
        protectChildTermsView.setOnClickListener(this);
    }

    private void setSigninLayout(boolean isSignin)
    {
        TextView profileTextView = (TextView) mProfileLayout.findViewById(R.id.profileTextView);

        mProfileLayout.setTag(isSignin);

        if (isSignin == true)
        {
            profileTextView.setText(R.string.frag_profile);
            mCreditcardLayout.setVisibility(View.VISIBLE);
        } else
        {
            profileTextView.setText(R.string.frag_login);
            mCreditcardLayout.setVisibility(View.GONE);
        }

        boolean enabled = DailyPreference.getInstance(getContext()).isAllowSMS();
        setEnabledSMS(getContext(), enabled);

        //        mDailyInformationView.post(new Runnable()
        //        {
        //            @Override
        //            public void run()
        //            {
        //                if (mInformationScrollView.getHeight() > mInformationLayout.getHeight())
        //                {
        //                    ViewGroup.LayoutParams layoutParams = mDailyInformationView.getLayoutParams();
        //
        //                    if (layoutParams != null)
        //                    {
        //                        layoutParams.height += mInformationScrollView.getHeight() - mInformationLayout.getHeight();
        //                        mDailyInformationView.setLayoutParams(layoutParams);
        //                    }
        //                }
        //
        //                mDailyInformationView.setVisibility(View.VISIBLE);
        //            }
        //        });
    }

    @Override
    public void onStart()
    {
        //        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.INFORMATION);

        super.onStart();

        if (DailyDeepLink.getInstance().isValidateLink() == true)
        {
            if (DailyDeepLink.getInstance().isEventView() == true)
            {
                startActivity(new Intent(getActivity(), EventListActivity.class));
            } else if (DailyDeepLink.getInstance().isBonusView() == true)
            {
                startActivity(new Intent(getActivity(), BonusActivity.class));
            }

            DailyDeepLink.getInstance().clear();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        unLockUI();

        if (Util.isTextEmpty(DailyPreference.getInstance(getActivity()).getAuthorization()) == true)
        {
            AnalyticsManager.getInstance(getContext()).recordScreen(Screen.INFORMATION_SIGNOUT, null);

            setSigninLayout(false);
        } else
        {
            AnalyticsManager.getInstance(getContext()).recordScreen(Screen.INFORMATION_SIGNIN, null);

            setSigninLayout(true);
        }

        registerReceiver();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        unregisterReceiver();
    }

    @Override
    public void onClick(View v)
    {
        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        lockUiComponent();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        switch (v.getId())
        {
            case R.id.profileLayout:
            {
                Boolean isSignin = (Boolean) mProfileLayout.getTag();

                if (isSignin == null || isSignin == false)
                {
                    baseActivity.startActivityForResult(new Intent(baseActivity, LoginActivity.class), CODE_REQUEST_ACTIVITY_LOGIN);
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                    AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.LOGIN_CLICKED, AnalyticsManager.Label.LOGIN_CLICKED, null);
                } else
                {
                    startActivity(new Intent(baseActivity, ProfileActivity.class));
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                    //                    AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.PROFILE, 0L);
                }
                break;
            }

            case R.id.creditcardLayout:
            {
                startActivity(new Intent(baseActivity, CreditCardListActivity.class));
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , Action.CARD_MANAGEMENT_CLICKED, AnalyticsManager.Label.CARD_MANAGEMENT_CLICKED, null);
                break;
            }

            case R.id.bonusLayout:
            {
                startActivity(new Intent(baseActivity, BonusActivity.class));
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , Action.CREDIT_MANAGEMENT_CLICKED, AnalyticsManager.Label.CREDIT_MANAGEMENT_CLICKED, null);
                break;
            }

            case R.id.eventLayout:
            {
                startActivity(new Intent(baseActivity, EventListActivity.class));
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , Action.EVENT_CLICKED, AnalyticsManager.Label.EVENT_CLICKED, null);
                break;
            }

            case R.id.callLayout:
            {
                showCallDialog(baseActivity);

                //                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.CALL_CS, 0L);
                break;
            }

            case R.id.mailLayout:
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:help@dailyhotel.co.kr"));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_text_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text_desc));
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(Intent.createChooser(intent, getString(R.string.mail_text_dialog_title)));

                //                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.MAIL_CS, 0L);
                break;
            }

            case R.id.aboutLayout:
            {
                startActivity(new Intent(baseActivity, AboutActivity.class));
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                //                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.ABOUT, 0L);
                break;
            }

            case R.id.pushTextView:
            {
                if (DailyPreference.getInstance(baseActivity).isAllowPush() == true)
                {
                    DailyPreference.getInstance(baseActivity).setAllowPush(false);
                    ((TextView) v).setText(R.string.label_off);
                } else
                {
                    DailyPreference.getInstance(baseActivity).setAllowPush(true);
                    ((TextView) v).setText(R.string.label_on);
                }
                releaseUiComponent();
                break;
            }

            case R.id.smsTextView:
            {
                lockUI();

                if (Constants.DAILY_USER.equalsIgnoreCase(DailyPreference.getInstance(baseActivity).getUserType()) == true)
                {
                    boolean enabled = DailyPreference.getInstance(baseActivity).isAllowSMS();

                    Map<String, String> params = Collections.singletonMap("is_text_enabled", enabled ? "false" : "true");

                    DailyNetworkAPI.getInstance().requestUserInformationUpdate(mNetworkTag, params, mDailyUserUpdateJsonResponseListener, this);
                } else
                {
                    DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInformationJsonResponseListener, this);
                }
                break;
            }

            case R.id.facebookLinkView:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                try
                {
                    intent.setData(Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/Dailyhotel.Korea"));
                    startActivity(intent);
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                } catch (Exception e)
                {
                    try
                    {
                        intent.setData(Uri.parse("http://www.facebook.com/dailyhotel"));
                        startActivity(intent);
                        baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                    } catch (ActivityNotFoundException e1)
                    {

                    }
                }
                break;
            }

            case R.id.instagramLinkView:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                try
                {
                    intent.setData(Uri.parse("instagram://user?username=dailyhotel"));
                    startActivity(intent);
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                } catch (Exception e)
                {
                    try
                    {
                        intent.setData(Uri.parse("http://www.instagram.com/dailyhotel"));
                        startActivity(intent);
                        baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                    } catch (ActivityNotFoundException e1)
                    {
                    }
                }
                break;
            }

            case R.id.naverLinkView:
            {
                try
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://blog.naver.com/dailyhotels"));
                    startActivity(intent);
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                } catch (ActivityNotFoundException e)
                {

                }
                break;
            }

            case R.id.termsView:
            {
                Intent intent = new Intent(baseActivity, TermActivity.class);
                startActivity(intent);
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            }

            case R.id.personalView:
            {
                Intent intent = new Intent(baseActivity, PrivacyActivity.class);
                startActivity(intent);
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            }

            case R.id.locationTermsView:
            {
                Intent intent = new Intent(baseActivity, LocationTermsActivity.class);
                startActivity(intent);
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            }

            case R.id.protectChildTermsView:
            {
                Intent intent = new Intent(baseActivity, ProtectChildTermsActivity.class);
                startActivity(intent);
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            }

            default:
                releaseUiComponent();
                break;
        }
    }

    private void updateNewIconView(Context context)
    {
        if (mNewEventIconView == null)
        {
            return;
        }

        if (DailyPreference.getInstance(context).hasNewEvent() == true)
        {
            mNewEventIconView.setVisibility(View.VISIBLE);
        } else
        {
            mNewEventIconView.setVisibility(View.INVISIBLE);
        }
    }

    private void setEnabledSMS(Context context, boolean enabled)
    {
        DailyPreference.getInstance(context).setAllowSMS(enabled);

        if (enabled == true)
        {
            mEnabledSMS.setText(R.string.label_on);
        } else
        {
            mEnabledSMS.setText(R.string.label_off);
        }
    }

    private void showCallDialog(final BaseActivity baseActivity)
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                if (Util.isTelephonyEnabled(baseActivity) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE_NUMBER_DAILYHOTEL)));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(baseActivity, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(baseActivity, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
            }
        };

        String operatingTimeMessage = DailyPreference.getInstance(baseActivity).getOperationTimeMessage(baseActivity);

        baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }

    private void registerReceiver()
    {
        if (mNewEventBroadcastReceiver != null)
        {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.BROADCAST_EVENT_UPDATE);

        mNewEventBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (context == null)
                {
                    return;
                }

                updateNewIconView(context);
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mNewEventBroadcastReceiver, intentFilter);
    }

    private void unregisterReceiver()
    {
        if (mNewEventBroadcastReceiver == null)
        {
            return;
        }

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mNewEventBroadcastReceiver);
        mNewEventBroadcastReceiver = null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDailyUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                boolean result = false;

                if (response.has("success") == true)
                {
                    result = response.getBoolean("success");
                }

                if (result == true)
                {
                    setEnabledSMS(getContext(), DailyPreference.getInstance(getContext()).isAllowSMS() ? false : true);
                } else
                {
                    DailyToast.showToast(getContext(), response.getString("msg"), Toast.LENGTH_LONG);
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                String userIndex = response.getString("idx");

                boolean enabled = DailyPreference.getInstance(getContext()).isAllowSMS();

                Map<String, String> params = new HashMap<>();
                params.put("user_idx", userIndex);
                params.put("is_text_enabled", enabled ? "false" : "true");

                DailyNetworkAPI.getInstance().requestUserUpdateInformationForSocial(mNetworkTag, params, mSocialUserUpdateJsonResponseListener, InformationFragment.this);
            } catch (Exception e)
            {
                onError(e);
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mSocialUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                JSONObject jsonObject = response.getJSONObject("data");

                boolean result = jsonObject.getBoolean("is_success");
                int msgCode = response.getInt("msg_code");

                if (result == true)
                {
                    setEnabledSMS(getContext(), DailyPreference.getInstance(getContext()).isAllowSMS() ? false : true);
                } else
                {
                    DailyToast.showToast(getContext(), response.getString("msg"), Toast.LENGTH_LONG);
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };
}
