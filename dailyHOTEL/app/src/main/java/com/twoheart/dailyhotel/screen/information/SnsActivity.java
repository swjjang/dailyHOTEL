package com.twoheart.dailyhotel.screen.information;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class SnsActivity extends BaseActivity implements View.OnClickListener
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, SnsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sns);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.label_dailyh_sns);
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
        findViewById(R.id.facebookLinkView).setOnClickListener(this);
        findViewById(R.id.naverLinkView).setOnClickListener(this);
        findViewById(R.id.naverBlogLinkView).setOnClickListener(this);
        findViewById(R.id.youtubeLinkView).setOnClickListener(this);
        findViewById(R.id.stayInstagramLinkView).setOnClickListener(this);
        findViewById(R.id.gourmetInstagramLinkView).setOnClickListener(this);
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

    private void startFacebook()
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

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.FACEBOOK_CLICK, null, null);
    }

    private void startStayInstagram()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        try
        {
            intent.setData(Uri.parse("instagram://user?username=dailyhotel_official"));
            startActivity(intent);
        } catch (Exception e)
        {
            try
            {
                intent.setData(Uri.parse("https://www.instagram.com/dailyhotel_official/"));
                startActivity(intent);
            } catch (ActivityNotFoundException e1)
            {
            }
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.INSTAGRAM_CLICK, null, null);
    }

    private void startGourmetInstagram()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        try
        {
            intent.setData(Uri.parse("instagram://user?username=dailygourmet_official"));
            startActivity(intent);
        } catch (Exception e)
        {
            try
            {
                intent.setData(Uri.parse("https://www.instagram.com/dailygourmet_official/"));
                startActivity(intent);
            } catch (ActivityNotFoundException e1)
            {
            }
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.INSTAGRAM_CLICK, null, null);
    }

    private void startNaverPost()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://post.naver.com/dailyhotel")));
        } catch (ActivityNotFoundException e)
        {
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.BLOG_CLICK, null, null);
    }

    private void startNaverBlog()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://blog.naver.com/dailyhotel")));
        } catch (ActivityNotFoundException e)
        {
        }

        //        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
        //            , AnalyticsManager.Action.BLOG_CLICK, null, null);
    }

    private void startYouTube()
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

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.YOUTUBE_CLICK, null, null);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.facebookLinkView:
                startFacebook();
                break;

            case R.id.naverLinkView:
                startNaverPost();
                break;

            case R.id.youtubeLinkView:
                startYouTube();
                break;

            case R.id.naverBlogLinkView:
                startNaverBlog();
                break;

            case R.id.stayInstagramLinkView:
                startStayInstagram();
                break;

            case R.id.gourmetInstagramLinkView:
                startGourmetInstagram();
                break;
        }
    }
}
