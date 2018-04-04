package com.daily.dailyhotel.view.shimmer;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;

public class ShimmerViewHelper
{

    public interface AnimationSetupCallback
    {
        void onSetupAnimation(View target);
    }

    private static final int DEFAULT_REFLECTION_COLOR = 0xFFE7E7E7;
    private static final int DEFAULT_PRIMARY_COLOR = 0xFFF8F8F9;

    private View view;
    private Paint paint;

    // center position of the gradient
    private float gradientX;

    // shader applied on the text view
    // only null until the first global layout
    private LinearGradient linearGradient;

    // shader's local matrix
    // never null
    private Matrix linearGradientMatrix;

    private int primaryColor;

    // shimmer reflection color
    private int reflectionColor;

    // true when animating
    private boolean isShimmering;

    // true after first global layout
    private boolean isSetUp;

    // callback called after first global layout
    private AnimationSetupCallback callback;

    public ShimmerViewHelper(View view, AttributeSet attributeSet)
    {
        this.view = view;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);

        init(attributeSet);
    }

    public float getGradientX()
    {
        return gradientX;
    }

    public void setGradientX(float gradientX)
    {
        this.gradientX = gradientX;
        view.invalidate();
    }

    public boolean isShimmering()
    {
        return isShimmering;
    }

    public void setShimmering(boolean isShimmering)
    {
        this.isShimmering = isShimmering;
    }

    public boolean isSetUp()
    {
        return isSetUp;
    }

    public void setAnimationSetupCallback(AnimationSetupCallback callback)
    {
        this.callback = callback;
    }

    public int getPrimaryColor()
    {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor)
    {
        this.primaryColor = primaryColor;
        if (isSetUp)
        {
            resetLinearGradient();
        }
    }

    public int getReflectionColor()
    {
        return reflectionColor;
    }

    public void setReflectionColor(int reflectionColor)
    {
        this.reflectionColor = reflectionColor;
        if (isSetUp)
        {
            resetLinearGradient();
        }
    }

    private void init(AttributeSet attributeSet)
    {

        primaryColor = DEFAULT_PRIMARY_COLOR;
        reflectionColor = DEFAULT_REFLECTION_COLOR;

        if (attributeSet != null)
        {
            TypedArray a = view.getContext().obtainStyledAttributes(attributeSet, R.styleable.ShimmerView, 0, 0);
            if (a != null)
            {
                try
                {
                    primaryColor = a.getColor(R.styleable.ShimmerView_primaryColor, DEFAULT_PRIMARY_COLOR);
                    reflectionColor = a.getColor(R.styleable.ShimmerView_reflectionColor, DEFAULT_REFLECTION_COLOR);
                } catch (Exception e)
                {
                    ExLog.e("sam : Error while creating the view:" + e.toString());
                } finally
                {
                    a.recycle();
                }
            }
        }

        linearGradientMatrix = new Matrix();
    }

    private void resetLinearGradient()
    {


        // our gradient is a simple linear gradient from textColor to reflectionColor. its axis is at the center
        // when it's outside of the view, the outer color (textColor) will be repeated (Shader.TileMode.CLAMP)
        // initially, the linear gradient is positioned on the left side of the view
        linearGradient = new LinearGradient(-view.getWidth(), 0, 0, 0, new int[]{primaryColor, reflectionColor, primaryColor,}, new float[]{0, 0.5f, 1}, Shader.TileMode.CLAMP);

        paint.setShader(linearGradient);
    }

    protected void onSizeChanged()
    {

        resetLinearGradient();

        if (!isSetUp)
        {
            isSetUp = true;

            if (callback != null)
            {
                callback.onSetupAnimation(view);
            }
        }
    }

    /**
     * content of the wrapping view's onDraw(Canvas)
     * MUST BE CALLED BEFORE SUPER STATEMENT
     */
    public void onDraw(Canvas canvas)
    {

        // only draw the shader gradient over the text while animating
        if (isShimmering)
        {

            // first onDraw() when shimmering
            if (paint.getShader() == null)
            {
                paint.setShader(linearGradient);
            }

            // translate the shader local matrix
            linearGradientMatrix.setTranslate(2 * gradientX, 0);

            // this is required in order to invalidate the shader's position
            linearGradient.setLocalMatrix(linearGradientMatrix);
            canvas.drawPaint(paint);

        } else
        {
            // we're not animating, remove the shader from the paint
            paint.setShader(null);
            paint.setColor(DEFAULT_PRIMARY_COLOR);
            canvas.drawPaint(paint);
        }
    }
}


