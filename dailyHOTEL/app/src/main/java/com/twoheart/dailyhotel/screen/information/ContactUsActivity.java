package com.twoheart.dailyhotel.screen.information;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_us);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_call_service), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout()
    {
        View kakaoLayout = findViewById(R.id.kakaoLayout);
        View callLayout = findViewById(R.id.callLayout);
        View mailLayout = findViewById(R.id.mailLayout);

        kakaoLayout.setOnClickListener(this);
        callLayout.setOnClickListener(this);
        mailLayout.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        unLockUI();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.kakaoLayout:
                startKakaotalkConsult();
                break;

            case R.id.callLayout:
                showDailyCallDialog(new OnCallDialogListener()
                {
                    @Override
                    public void onShowDialog()
                    {

                    }

                    @Override
                    public void onPositiveButtonClick(View v)
                    {
                        AnalyticsManager.getInstance(ContactUsActivity.this).recordEvent(//
                            AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.MENU,//
                            AnalyticsManager.Label.CALL, null);
                    }

                    @Override
                    public void onNativeButtonClick(View v)
                    {
                        AnalyticsManager.getInstance(ContactUsActivity.this).recordEvent(//
                            AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.MENU,//
                            AnalyticsManager.Label.CANCEL, null);
                    }
                });
                break;

            case R.id.mailLayout:
                startEmail();
                break;
        }
    }

    private void startKakaotalkConsult()
    {
        Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/@%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94"));
        if (intent.resolveActivity(getPackageManager()) == null)
        {
            Util.installPackage(this, "com.kakao.talk");
        } else
        {
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SHAREKAKAO);
        }
    }

    public void startEmail()
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:help@dailyhotel.co.kr"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_text_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text_desc, DailyHotel.VERSION_CODE, Build.VERSION.RELEASE));
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(Intent.createChooser(intent, getString(R.string.mail_text_dialog_title)));


        //            BaseActivity baseActivity = (BaseActivity) getActivity();
        //            startActivityForResult(new Intent(baseActivity, FeedbackMailActivity.class), CODE_REQUEST_ACTIVITY_FEEDBACK);

        //                AnalyticsManager.getInstance(baseActivity).recordEvent(Screen.INFORMATION, Action.CLICK, Label.MAIL_CS, 0L);

    }
}
