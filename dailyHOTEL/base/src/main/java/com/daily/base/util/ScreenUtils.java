package com.daily.base.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class ScreenUtils
{
    public static int dpToPx(Context context, double dp)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int getScreenWidth(Context context)
    {
        if (context == null)
        {
            return 0;
        }

        try
        {
            return context.getResources().getDisplayMetrics().widthPixels;
        } catch (Exception e)
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    public static int getScreenHeight(Context context)
    {
        if (context == null)
        {
            return 0;
        }
        try
        {
            return context.getResources().getDisplayMetrics().heightPixels;
        } catch (Exception e)
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

    /**
     * 21:9로 표현된 높이
     *
     * @return
     */
    public static int getRatioHeightType21x9(int width)
    {
        if (width < 1)
        {
            return 0;
        }

        return width * 9 / 21;
    }

    /**
     * 16:9로 표현된 높이
     *
     * @return
     */
    public static int getRatioHeightType16x9(int width)
    {
        if (width < 1)
        {
            return 0;
        }

        return width * 9 / 16;
    }

    /**
     * 4:3으로 표현된 높이
     *
     * @return
     */
    public static int getRatioHeightType4x3(int width)
    {
        if (width < 1)
        {
            return 0;
        }

        return width * 3 / 4;
    }

    public static WindowManager.LayoutParams getDialogWidthLayoutParams(Activity activity, Dialog dialog)
    {
        if (dialog == null)
        {
            return null;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        if (isTabletDevice(activity) == false)
        {
            layoutParams.width = ScreenUtils.getScreenWidth(activity) * 13 / 15;
        } else
        {
            layoutParams.width = ScreenUtils.getScreenWidth(activity) * 10 / 15;
        }

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return layoutParams;
    }

    /**
     * 일단은 테블릿으로 정의하지만 실제로는 화면 크기이기 때문에 폰이 테블릿이 되는 경우도
     * 반대의 경우도 발생하나 스크린 사이즈로 결정되기 때문에 이상없을 것이라고 판단됨
     *
     * @param activity
     * @return
     */
    public static boolean isTabletDevice(Activity activity)
    {
        return checkTabletDeviceWithScreenSize(activity);
    }

    private static boolean checkTabletDeviceWithScreenSize(Activity activity)
    {
        boolean device_large = ((activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);

        if (device_large)
        {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT//
                || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH//
                || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM//
                || metrics.densityDpi == DisplayMetrics.DENSITY_TV//
                || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH)
            {
                return true;
            }
        }

        return false;
    }

    public static String getResolutionImageUrl(Context context, String defaultImageUrl, String lowResolutionImageUrl)
    {
        return ScreenUtils.getScreenWidth(context) < 1440 ? lowResolutionImageUrl : defaultImageUrl;
    }

    public static Bitmap takeScreenShot(@NonNull Activity activity)
    {
        if(activity == null)
        {
            return null;
        }

        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        Bitmap snapShot = view.getDrawingCache();

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Matrix matrix = new Matrix();
        matrix.setScale(0.4f, 0.4f);
        Bitmap copySnapShot = Bitmap.createBitmap(snapShot, 0, statusBarHeight//
            , ScreenUtils.getScreenWidth(activity), ScreenUtils.getScreenHeight(activity) - statusBarHeight, matrix, false);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return copySnapShot;
    }

    public static Bitmap fastblur(Context context, Bitmap snapShotBitmap, float radius)
    {
        if(context == null || snapShotBitmap == null)
        {
            return null;
        }

        //        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        final RenderScript renderScript = RenderScript.create(context);
        final Allocation inputAllocation = Allocation.createFromBitmap(renderScript, snapShotBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation outputAllocation = Allocation.createTyped(renderScript, inputAllocation.getType());
        final ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setRadius(radius /* e.g. 3.f */);
        scriptIntrinsicBlur.setInput(inputAllocation);
        scriptIntrinsicBlur.forEach(outputAllocation);

        outputAllocation.copyTo(snapShotBitmap);

        return snapShotBitmap;
    }
}