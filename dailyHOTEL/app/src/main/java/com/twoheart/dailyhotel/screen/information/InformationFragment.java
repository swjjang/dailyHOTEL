package com.twoheart.dailyhotel.screen.information;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.AboutActivity;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.BonusActivity;
import com.twoheart.dailyhotel.activity.CreditCardListActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.ProfileActivity;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.screen.eventlist.EventListActivity;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class InformationFragment extends BaseFragment implements Constants, OnClickListener
{
    private View mProfileLayout, mCreditcardLayout;
    private String mCSoperatingTimeMessage;
    private DailyToolbarLayout mDailyToolbarLayout;

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

        // 프로필
        setSigninLayout(false);

        TextView pushTextView = (TextView) view.findViewById(R.id.pushTextView);

        if (DailyPreference.getInstance(baseActivity).isAllowPush() == true)
        {
            pushTextView.setText(R.string.label_on);
        } else
        {
            pushTextView.setText(R.string.label_off);
        }

        pushTextView.setOnClickListener(this);

        initSnsLayout(view);
        initBusinessLayout(baseActivity, view);

        TextView versionTextView = (TextView) view.findViewById(R.id.versionTextView);
        versionTextView.setText(getString(R.string.label_version, DailyHotel.VERSION));

        return view;
    }

    private void initToolbar(BaseActivity baseActivity, View view)
    {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(baseActivity, toolbar);
        mDailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_setting_frag), false, false);
    }

    private void initSnsLayout(View view)
    {
        View viewFacebook = view.findViewById(R.id.facebookLinkView);
        View viewInstagram = view.findViewById(R.id.instagramLinkView);
        View viewNaver = view.findViewById(R.id.naverLinkView);

        viewFacebook.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                try
                {
                    intent.setData(Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/Dailyhotel.Korea"));
                    startActivity(intent);
                } catch (Exception e)
                {
                    intent.setData(Uri.parse("https://www.facebook.com/dailyhotel"));
                    startActivity(intent);
                }
            }
        });

        viewInstagram.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                try
                {
                    intent.setData(Uri.parse("instagram://user?username=dailyhotel"));
                    startActivity(intent);
                } catch (Exception e)
                {
                    intent.setData(Uri.parse("https://www.instagram.com/dailyhotel"));
                    startActivity(intent);
                }
            }
        });

        viewNaver.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://blog.naver.com/dailyhotels"));
                startActivity(intent);
            }
        });
    }

    private void initBusinessLayout(BaseActivity baseActivity, View view)
    {
        TextView business1TextView = (TextView) view.findViewById(R.id.business1TextView);
        TextView business2TextView = (TextView) view.findViewById(R.id.business2TextView);

        business1TextView.setText(getString(R.string.frag_about_business_license01//
            , DailyPreference.getInstance(baseActivity).getCompanyCEO()//
            , DailyPreference.getInstance(baseActivity).getCompanyBizRegNumber()));
        business2TextView.setText(getString(R.string.frag_about_business_license02//
            , DailyPreference.getInstance(baseActivity).getCompanyItcRegNumber()//
            , DailyPreference.getInstance(baseActivity).getCompanyPhoneNumber()));
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
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.INFORMATION);
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        lockUI();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, baseActivity);
    }

    @Override
    public void onClick(View v)
    {
        if (isLockUiComponent() == true)
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

                    AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.LOGIN, 0L);
                } else
                {
                    startActivity(new Intent(baseActivity, ProfileActivity.class));
                    baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                    AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.PROFILE, 0L);
                }
                break;
            }

            case R.id.creditcardLayout:
            {
                startActivity(new Intent(baseActivity, CreditCardListActivity.class));
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.CREDITCARD, 0L);
                break;
            }

            case R.id.bonusLayout:
            {
                startActivity(new Intent(baseActivity, BonusActivity.class));
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.BOUNS, 0L);
                break;
            }

            case R.id.eventLayout:
            {
                startActivity(new Intent(baseActivity, EventListActivity.class));
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.EVENT, 0L);
                break;
            }

            case R.id.callLayout:
            {
                showCallDialog(baseActivity);

                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.CALL_CS, 0L);
                break;
            }

            case R.id.mailLayout:
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:help@dailyhotel.co.kr"));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_text_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text_desc));
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(Intent.createChooser(intent, getString(R.string.mail_text_dialog_title)));

                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.MAIL_CS, 0L);
                break;
            }

            case R.id.aboutLayout:
            {
                startActivity(new Intent(baseActivity, AboutActivity.class));
                baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.ABOUT, 0L);
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

            default:
                releaseUiComponent();
                break;
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
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString())));
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

        if (Util.isTextEmpty(mCSoperatingTimeMessage) == true)
        {
            mCSoperatingTimeMessage = getString(R.string.dialog_msg_call);
        }

        baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), mCSoperatingTimeMessage, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {
        @Override
        public void onResponse(String url, String response)
        {
            String result = null;

            if (Util.isTextEmpty(response) == false)
            {
                result = response.trim();
            }

            if ("alive".equalsIgnoreCase(result) == true)
            {
                setSigninLayout(true);
            } else
            {
                setSigninLayout(false);
            }

            BaseActivity baseActivity = (BaseActivity) getActivity();
            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, baseActivity);
        }
    };

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                mCSoperatingTimeMessage = getString(R.string.dialog_message_cs_operating_time //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("openDateTime")))) //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("closeDateTime")))));
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
