package com.twoheart.dailyhotel.screen.information;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class GuideActivity extends BaseActivity implements View.OnClickListener
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, GuideActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_dailyhotel_guide), new View.OnClickListener()
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
        View aboutLayout = findViewById(R.id.aboutLayout);
        View trueReviewLayout = findViewById(R.id.trueReviewLayout);

        aboutLayout.setOnClickListener(this);
        trueReviewLayout.setOnClickListener(this);

        initSnsLayout();

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

    private void initSnsLayout()
    {
        View facebookView = findViewById(R.id.facebookLinkView);
        View instagramView = findViewById(R.id.instagramLinkView);
        View naverView = findViewById(R.id.naverLinkView);
        View youtubeLinkView = findViewById(R.id.youtubeLinkView);

        facebookView.setOnClickListener(this);
        instagramView.setOnClickListener(this);
        naverView.setOnClickListener(this);
        youtubeLinkView.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        unLockUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case Constants.CODE_REQUEST_ACTIVITY_ABOUT:
            case Constants.CODE_REQUEST_ACTIVITY_ABOUT_TRUEREVIEW:
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    public void startFacebook()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        try
        {
            intent.setData(Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/Dailyhotel.Korea"));
            startActivity(intent);
        } catch (Exception e)
        {
            try
            {
                intent.setData(Uri.parse("http://www.facebook.com/dailyhotel"));
                startActivity(intent);
            } catch (ActivityNotFoundException e1)
            {
                ExLog.d(e.toString());
            }
        }
    }

    public void startInstagram()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        try
        {
            intent.setData(Uri.parse("instagram://user?username=dailyhotel_korea"));
            startActivity(intent);
        } catch (Exception e)
        {
            try
            {
                intent.setData(Uri.parse("http://www.instagram.com/dailyhotel_korea"));
                startActivity(intent);
            } catch (ActivityNotFoundException e1)
            {
            }
        }
    }

    public void startNaverBlog()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        try
        {
            intent.setData(Uri.parse("naversearchapp://inappbrowser?url=http%3A%2F%2Fblog.naver.com%2Fdailyhotel&target=new&version=6"));
            startActivity(intent);
        } catch (Exception e)
        {
            try
            {
                intent.setData(Uri.parse("http://blog.naver.com/dailyhotel"));
                startActivity(intent);
            } catch (ActivityNotFoundException e1)
            {
            }
        }
    }

    public void startYouTube()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        try
        {
            intent.setData(Uri.parse("youtube://channel/UCNJASbBThd0TFo3qLgl1wuw"));
            startActivity(intent);
        } catch (Exception e)
        {
            try
            {
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCNJASbBThd0TFo3qLgl1wuw"));
                startActivity(intent);
            } catch (ActivityNotFoundException e1)
            {
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.aboutLayout:
                startActivityForResult(AboutActivity.newInstance(this), Constants.CODE_REQUEST_ACTIVITY_ABOUT);
                break;

            case R.id.trueReviewLayout:
                startActivityForResult(AboutTrueReviewActivity.newInstance(this), Constants.CODE_REQUEST_ACTIVITY_ABOUT_TRUEREVIEW);
                break;

            case R.id.facebookLinkView:
                startFacebook();
                break;

            case R.id.instagramLinkView:
                startInstagram();
                break;

            case R.id.naverLinkView:
                startNaverBlog();
                break;

            case R.id.youtubeLinkView:
                startYouTube();
                break;
        }
    }
}
