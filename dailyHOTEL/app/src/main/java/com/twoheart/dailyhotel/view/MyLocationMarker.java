package com.twoheart.dailyhotel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.twoheart.dailyhotel.util.Util;

public class MyLocationMarker
{
    private Context mContext;
    private Bitmap mBitmap;

    public MyLocationMarker(Context context)
    {
        mContext = context;

        mBitmap = Bitmap.createBitmap(Util.dpToPx(mContext, 15), Util.dpToPx(mContext, 15), Bitmap.Config.ARGB_8888);
    }

    public BitmapDescriptor makeIcon()
    {
        Canvas canvas = new Canvas(mBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float) mBitmap.getWidth() / 2, (float) mBitmap.getHeight() / 2, (float) mBitmap.getWidth() / 2, paint);

        paint.setColor(0xff4285f4);
        canvas.drawCircle((float) mBitmap.getWidth() / 2, (float) mBitmap.getHeight() / 2, (float) mBitmap.getWidth() / 2 - 4f, paint);

        return BitmapDescriptorFactory.fromBitmap(mBitmap);
    }
}
