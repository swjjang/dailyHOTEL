package com.twoheart.dailyhotel.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyPreference;

public class GuideActivity extends BaseActivity implements View.OnClickListener
{
    private int mGuideStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);

        mGuideStatus = 0;

        ImageView imageView = (ImageView) findViewById(R.id.frontImageView);
        imageView.setOnClickListener(this);
    }

    @Override
    protected void onDestroy()
    {
        DailyPreference.getInstance(this).setShowGuide(true);

        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        //        switch (mGuideStatus)
        //        {
        //            case 0:
        //            {
        //                mGuideStatus = 1;
        //
        //                ImageView imageView = (ImageView) findViewById(R.id.backImageView);
        //                imageView.setImageResource(R.drawable.guide01);
        //
        //                Glide.with(this).load(R.drawable.guide02).crossFade().into((ImageView) v);
        //                break;
        //            }
        //
        //            case 1:
        //            {
        //                mGuideStatus = 2;
        //
        //                ImageView imageView = (ImageView) findViewById(R.id.backImageView);
        //                imageView.setImageResource(R.drawable.guide02);
        //
        //                Glide.with(this).load(R.drawable.guide03).crossFade().into((ImageView) v);
        //                break;
        //            }
        //
        //            default:
        //                finish();
        //                break;
        //        }
    }
}
