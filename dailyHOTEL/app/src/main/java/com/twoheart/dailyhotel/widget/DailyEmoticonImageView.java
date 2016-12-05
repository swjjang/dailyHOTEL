package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.facebook.keyframes.KeyframesDrawable;
import com.facebook.keyframes.KeyframesDrawableBuilder;
import com.facebook.keyframes.deserializers.KFImageDeserializer;
import com.facebook.keyframes.model.KFImage;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;

import java.io.IOException;
import java.io.InputStream;

public class DailyEmoticonImageView extends ImageView
{
    private KFImage mKfImage;
    private KeyframesDrawable mKeyFramesDrawable;

    public DailyEmoticonImageView(Context context)
    {
        super(context);
    }

    public DailyEmoticonImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DailyEmoticonImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public DailyEmoticonImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setJSONData(String fileName)
    {
        InputStream inputStream = null;
        try
        {
            inputStream = getResources().getAssets().open(fileName);
            KFImage kfImage = KFImageDeserializer.deserialize(inputStream);


        } catch (IOException e)
        {
            ExLog.e(e.toString());
        } finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                } catch (IOException ignored)
                {
                }
            }
        }
    }

    public void startAnimation()
    {
        if (mKeyFramesDrawable != null)
        {
            mKeyFramesDrawable.startAnimation();
        }
    }

    public void stopAnimation()
    {
        if (mKeyFramesDrawable != null)
        {
            mKeyFramesDrawable.stopAnimation();
        }
    }

    private void setKFImage(KFImage kfImage)
    {
        clearImage();
        mKfImage = kfImage;

        mKeyFramesDrawable = new KeyframesDrawableBuilder().withImage(mKfImage).withMaxFrameRate(60).build();

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setImageDrawable(mKeyFramesDrawable);
        setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void clearImage()
    {
        if (mKeyFramesDrawable == null)
        {
            return;
        }

        mKeyFramesDrawable.stopAnimation();
        mKeyFramesDrawable = null;
    }
}