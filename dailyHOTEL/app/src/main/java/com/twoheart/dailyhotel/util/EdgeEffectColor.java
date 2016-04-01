package com.twoheart.dailyhotel.util;

import android.annotation.TargetApi;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ScrollView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EdgeEffectColor
{
    protected EdgeEffectColor()
    {
    }

    private static final Class<ScrollView> CLASS_SCROLL_VIEW = ScrollView.class;
    private static final Field SCROLL_VIEW_FIELD_EDGE_GLOW_TOP;
    private static final Field SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM;

    private static final Class<NestedScrollView> CLASS_NESTED_SCROLL_VIEW = NestedScrollView.class;
    private static final Field NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_TOP;
    private static final Field NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM;
    private static final Method NESTED_SCROLL_VIEW_METHOD_ENSURE_GLOWS;

    private static final Class<RecyclerView> CLASS_RECYCLER_VIEW = RecyclerView.class;
    private static final Field RECYCLER_VIEW_FIELD_EDGE_GLOW_TOP;
    private static final Field RECYCLER_VIEW_FIELD_EDGE_GLOW_LEFT;
    private static final Field RECYCLER_VIEW_FIELD_EDGE_GLOW_RIGHT;
    private static final Field RECYCLER_VIEW_FIELD_EDGE_GLOW_BOTTOM;
    private static final Method RECYCLER_VIEW_METHOD_ENSURE_GLOW_TOP;
    private static final Method RECYCLER_VIEW_METHOD_ENSURE_GLOW_BOTTOM;

    private static final Class<AbsListView> CLASS_LIST_VIEW = AbsListView.class;
    private static final Field LIST_VIEW_FIELD_EDGE_GLOW_TOP;
    private static final Field LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM;

    private static final Field EDGE_GLOW_FIELD_EDGE;
    private static final Field EDGE_GLOW_FIELD_GLOW;

    private static final Field EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT;

    static
    {
        Field edgeGlowTop = null, edgeGlowBottom = null, edgeGlowLeft = null, edgeGlowRight = null;
        Method ensureGlowTop = null, ensureGlowBottom = null;

        for (Field f : CLASS_RECYCLER_VIEW.getDeclaredFields())
        {
            switch (f.getName())
            {
                case "mTopGlow":
                    f.setAccessible(true);
                    edgeGlowTop = f;
                    edgeGlowTop.setAccessible(true);
                    break;
                case "mBottomGlow":
                    f.setAccessible(true);
                    edgeGlowBottom = f;
                    edgeGlowBottom.setAccessible(true);
                    break;
                case "mLeftGlow":
                    f.setAccessible(true);
                    edgeGlowLeft = f;
                    edgeGlowLeft.setAccessible(true);
                    break;
                case "mRightGlow":
                    f.setAccessible(true);
                    edgeGlowRight = f;
                    edgeGlowRight.setAccessible(true);
                    break;
            }
        }

        for (Method m : CLASS_RECYCLER_VIEW.getDeclaredMethods())
        {
            switch (m.getName())
            {
                case "ensureTopGlow":
                    m.setAccessible(true);
                    ensureGlowTop = m;
                    ensureGlowTop.setAccessible(true);
                    break;
                case "ensureBottomGlow":
                    m.setAccessible(true);
                    ensureGlowBottom = m;
                    ensureGlowBottom.setAccessible(true);
                    break;
            }
        }

        RECYCLER_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
        RECYCLER_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;
        RECYCLER_VIEW_FIELD_EDGE_GLOW_LEFT = edgeGlowLeft;
        RECYCLER_VIEW_FIELD_EDGE_GLOW_RIGHT = edgeGlowRight;

        RECYCLER_VIEW_METHOD_ENSURE_GLOW_TOP = ensureGlowTop;
        RECYCLER_VIEW_METHOD_ENSURE_GLOW_BOTTOM = ensureGlowBottom;

        for (Field f : CLASS_NESTED_SCROLL_VIEW.getDeclaredFields())
        {
            switch (f.getName())
            {
                case "mEdgeGlowTop":
                    f.setAccessible(true);
                    edgeGlowTop = f;
                    edgeGlowTop.setAccessible(true);
                    break;
                case "mEdgeGlowBottom":
                    f.setAccessible(true);
                    edgeGlowBottom = f;
                    edgeGlowBottom.setAccessible(true);
                    break;
            }
        }

        NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
        NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;

        for (Field f : CLASS_SCROLL_VIEW.getDeclaredFields())
        {
            switch (f.getName())
            {
                case "mEdgeGlowTop":
                    f.setAccessible(true);
                    edgeGlowTop = f;
                    edgeGlowTop.setAccessible(true);
                    break;
                case "mEdgeGlowBottom":
                    f.setAccessible(true);
                    edgeGlowBottom = f;
                    edgeGlowBottom.setAccessible(true);
                    break;
            }
        }

        for (Method m : CLASS_NESTED_SCROLL_VIEW.getDeclaredMethods())
        {
            switch (m.getName())
            {
                case "ensureGlows":
                    m.setAccessible(true);
                    ensureGlowTop = m;
                    ensureGlowTop.setAccessible(true);
                    break;
            }
        }

        SCROLL_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
        SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;

        NESTED_SCROLL_VIEW_METHOD_ENSURE_GLOWS = ensureGlowTop;

        for (Field f : CLASS_LIST_VIEW.getDeclaredFields())
        {
            switch (f.getName())
            {
                case "mEdgeGlowTop":
                    f.setAccessible(true);
                    edgeGlowTop = f;
                    edgeGlowTop.setAccessible(true);
                    break;
                case "mEdgeGlowBottom":
                    f.setAccessible(true);
                    edgeGlowBottom = f;
                    edgeGlowBottom.setAccessible(true);
                    break;
            }
        }

        LIST_VIEW_FIELD_EDGE_GLOW_TOP = edgeGlowTop;
        LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM = edgeGlowBottom;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            Field edge = null, glow = null;

            Class cls = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            {
                try
                {
                    cls = Class.forName("android.widget.EdgeGlow");
                } catch (ClassNotFoundException e)
                {
                    ExLog.d(e.toString());
                }
            } else
            {
                cls = EdgeEffect.class;

            }

            if (cls != null)
            {
                for (Field f : cls.getDeclaredFields())
                {
                    switch (f.getName())
                    {
                        case "mEdge":
                            f.setAccessible(true);
                            edge = f;
                            break;
                        case "mGlow":
                            f.setAccessible(true);
                            glow = f;
                            break;
                    }
                }
            }

            EDGE_GLOW_FIELD_EDGE = edge;
            EDGE_GLOW_FIELD_GLOW = glow;
        } else
        {
            EDGE_GLOW_FIELD_EDGE = null;
            EDGE_GLOW_FIELD_GLOW = null;
        }

        Field efc = null;
        try
        {
            efc = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
            efc.setAccessible(true);
        } catch (NoSuchFieldException e)
        {
            ExLog.d(e.toString());
        }
        EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT = efc;
    }

    @IntDef({ALWAYS, PRE_HONEYCOMB, PRE_KITKAT, PRE_LOLLIPOP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EdgeGlowColorApi
    {
    }

    public static final int ALWAYS = 0;
    /**
     * Replace yellow glow in vanilla, blue glow on Samsung.
     */
    public static final int PRE_HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;
    /**
     * Replace Holo blue glow.
     */
    public static final int PRE_KITKAT = Build.VERSION_CODES.KITKAT;
    /**
     * Replace Holo grey glow.
     */
    public static final int PRE_LOLLIPOP = Build.VERSION_CODES.LOLLIPOP;

    public static void setEdgeGlowColor(AbsListView listView, @ColorInt int color, @EdgeGlowColorApi int when)
    {
        if (Build.VERSION.SDK_INT < when || when == ALWAYS)
        {
            setEdgeGlowColor(listView, color);
        }
    }

    public static void setEdgeGlowColor(AbsListView listView, @ColorInt int color)
    {
        try
        {
            Object object;
            object = LIST_VIEW_FIELD_EDGE_GLOW_TOP.get(listView);
            setEdgeGlowColor(object, color);
            object = LIST_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(listView);
            setEdgeGlowColor(object, color);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void setEdgeGlowColor(ScrollView scrollView, @ColorInt int color)
    {
        try
        {
            Object object;
            object = SCROLL_VIEW_FIELD_EDGE_GLOW_TOP.get(scrollView);
            setEdgeGlowColor(object, color);
            object = SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(scrollView);
            setEdgeGlowColor(object, color);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void setEdgeGlowColor(NestedScrollView scrollView, @ColorInt int color)
    {
        try
        {
            NESTED_SCROLL_VIEW_METHOD_ENSURE_GLOWS.invoke(scrollView);

            Object object;
            object = NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_TOP.get(scrollView);
            setEdgeGlowColor(object, color);
            object = NESTED_SCROLL_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(scrollView);
            setEdgeGlowColor(object, color);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static void setEdgeGlowColor(RecyclerView recyclerView, @ColorInt int color)
    {
        try
        {
            RECYCLER_VIEW_METHOD_ENSURE_GLOW_TOP.invoke(recyclerView);
            RECYCLER_VIEW_METHOD_ENSURE_GLOW_BOTTOM.invoke(recyclerView);

            Object object;
            object = RECYCLER_VIEW_FIELD_EDGE_GLOW_TOP.get(recyclerView);
            setEdgeGlowColor(object, color);
            object = RECYCLER_VIEW_FIELD_EDGE_GLOW_BOTTOM.get(recyclerView);
            setEdgeGlowColor(object, color);
            object = RECYCLER_VIEW_FIELD_EDGE_GLOW_LEFT.get(recyclerView);
            setEdgeGlowColor(object, color);
            object = RECYCLER_VIEW_FIELD_EDGE_GLOW_RIGHT.get(recyclerView);
            setEdgeGlowColor(object, color);
        } catch (Exception | NoClassDefFoundError e)
        {
            ExLog.d(e.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setEdgeGlowColor(Object edgeEffect, @ColorInt int color)
    {
        if (edgeEffect instanceof EdgeEffectCompat)
        {
            // EdgeEffectCompat
            try
            {
                edgeEffect = EDGE_EFFECT_COMPAT_FIELD_EDGE_EFFECT.get(edgeEffect);
            } catch (IllegalAccessException e)
            {
                ExLog.d(e.toString());
                return;
            }
        }

        if (edgeEffect == null)
        {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            // EdgeGlow below Android 4 then old EdgeEffect
            try
            {
                final Drawable mEdge = (Drawable) EDGE_GLOW_FIELD_EDGE.get(edgeEffect);
                final Drawable mGlow = (Drawable) EDGE_GLOW_FIELD_GLOW.get(edgeEffect);
                mEdge.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                mGlow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                mEdge.setCallback(null); // free up any references
                mGlow.setCallback(null); // free up any references
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        } else
        {
            // EdgeEffect
            ((EdgeEffect) edgeEffect).setColor(color);
        }
    }
}