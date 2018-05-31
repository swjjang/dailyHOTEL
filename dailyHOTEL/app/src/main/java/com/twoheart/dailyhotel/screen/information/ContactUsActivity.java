package com.twoheart.dailyhotel.screen.information;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

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
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.label_call_service);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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

        View homeButtonView = findViewById(R.id.homeButtonView);
        homeButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
                finish();
            }
        });
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
                startKakaoTalkConsult();

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.MENU_INQUIRY, null);
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
                            AnalyticsManager.Label.CANCEL_, null);
                    }

                    @Override
                    public void onDismissDialog()
                    {
                        // do nothing!
                    }
                });

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                    , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.MENU_INQUIRY, null);
                break;

            case R.id.mailLayout:
                startEmail();
                break;
        }
    }

    private void startKakaoTalkConsult()
    {
        startActivityForResult(HappyTalkCategoryDialog.newInstance(this//
            , HappyTalkCategoryDialog.CallScreen.SCREEN_CONTACT_US, 0, 0, null), Constants.CODE_REQUEST_ACTIVITY_HAPPY_TALK);
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
