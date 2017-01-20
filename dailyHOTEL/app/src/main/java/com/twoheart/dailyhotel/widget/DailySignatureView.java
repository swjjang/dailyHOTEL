package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public class DailySignatureView extends View
{
    private static final int CONFIRM_RATIO_PERCENT_OF_SIGNATURE = 5; // 전체 화면의 xx%
    private static final int B_CURVE_COUNT_OF_POINT = 5;

    private Path mPath;
    Paint mPaint;
    private Bitmap mBitmap;
    Canvas mCanvas;
    private ArrayList<Point> mArrayList;
    private RectF mRectF;
    private Rect mDstRect;
    OnUserActionListener mOnUserActionListener;

    int mTouchAction;
    boolean mIsSignatureChecked;

    public DailySignatureView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailySignatureView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailySignatureView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        initLayout(context);
    }

    public DailySignatureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mRectF = new RectF();
        mDstRect = new Rect();

        mArrayList = new ArrayList<>(100);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5f);
        mPaint.setColor(Color.BLACK);

        mPath = new Path();

        mIsSignatureChecked = false;
    }

    public void setOnUserActionListener(DailySignatureView.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    boolean isSignatureChecked()
    {
        if (mBitmap == null)
        {
            return false;
        }

        int bitmapDimensions = mBitmap.getWidth() * mBitmap.getHeight();
        int signatureDimensions = (int) (mRectF.width() * mRectF.height());

        return 100 * signatureDimensions / bitmapDimensions > CONFIRM_RATIO_PERCENT_OF_SIGNATURE;
    }

    private void clearCanvas(Canvas canvas)
    {
        if (canvas == null)
        {
            return;
        }

        canvas.drawColor(getContext().getResources().getColor(R.color.white));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        if (mBitmap == null)
        {
            try
            {
                mBitmap = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.RGB_565);
                mCanvas = new Canvas(mBitmap);
                clearCanvas(mCanvas);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.payment_sign_hint);

                if (Util.getLCDWidth(getContext()) < 720)
                {
                    int dstLeft = (mBitmap.getWidth() - bitmap.getWidth()) / 2;
                    int dstTop = mBitmap.getHeight() - Util.dpToPx(getContext(), 10) - bitmap.getHeight();

                    mDstRect.set(dstLeft, dstTop, dstLeft + bitmap.getWidth(), dstTop + bitmap.getHeight());
                } else
                {
                    int dstLeft = (mBitmap.getWidth() - bitmap.getWidth()) / 2;
                    int dstTop = mBitmap.getHeight() - Util.dpToPx(getContext(), 15) - bitmap.getHeight();
                    mDstRect.set(dstLeft, dstTop, dstLeft + bitmap.getWidth(), dstTop + bitmap.getHeight());
                }

                mCanvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), mDstRect, new Paint());

                bitmap.recycle();
                bitmap = null;
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mBitmap != null)
        {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEventCompat.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                mTouchAction = MotionEvent.ACTION_DOWN;

                float x = event.getX();
                float y = event.getY();

                mArrayList.add(new Point(x, y));

                if (mRectF.isEmpty() == true)
                {
                    mRectF.left = x;
                    mRectF.top = y;
                    mRectF.right = x;
                    mRectF.bottom = y;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    new BCurveTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, mArrayList);
                } else
                {
                    new BCurveTask().execute(mArrayList);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                mTouchAction = MotionEvent.ACTION_MOVE;

                float x = event.getX();
                float y = event.getY();

                mArrayList.add(new Point(x, y));
                mRectF.union(x, y);

                if (mIsSignatureChecked == false && isSignatureChecked() == true)
                {
                    mIsSignatureChecked = true;

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.onConfirmSignature();
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            {
                mTouchAction = MotionEvent.ACTION_UP;

                if (mIsSignatureChecked == false && isSignatureChecked() == true)
                {
                    mIsSignatureChecked = true;

                    if (mOnUserActionListener != null)
                    {
                        mOnUserActionListener.onConfirmSignature();
                    }
                }
                break;
            }

            default:
                break;
        }

        return true;
    }

    void drawBCurve(Point p1, Point p2, Point p3, Point p4)
    {
        if (p1 == null || p2 == null || p3 == null || p4 == null)
        {
            return;
        }

        mPath.reset();
        mPath.moveTo(p1.x, p1.y);
        mPath.cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);

        mCanvas.drawPath(mPath, mPaint);
    }

    void drawBCurve(Point p1, Point p2, Point p3)
    {
        mPath.reset();
        mPath.moveTo(p1.x, p1.y);
        mPath.quadTo(p2.x, p2.y, p3.x, p3.y);

        mCanvas.drawPath(mPath, mPaint);
    }

    Point getTriangleCenter(Point p1, Point p2, Point p3)
    {
        if (p1 == null || p2 == null || p3 == null)
        {
            return null;
        }

        return new Point((p1.x + p2.x + p3.x) / 3, (p1.y + p2.y + p3.y) / 3);
    }

    public interface OnUserActionListener
    {
        void onConfirmSignature();
    }

    private static class Point
    {
        public float x;
        public float y;

        public Point(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
    }

    public class BCurveTask extends AsyncTask<ArrayList<Point>, Integer, ArrayList<Point>>
    {
        @SafeVarargs
        @Override
        protected final ArrayList<Point> doInBackground(ArrayList<Point>... params)
        {
            ArrayList<Point> arrayList = params[0];

            if (arrayList == null || arrayList.size() == 0)
            {
                return null;
            }

            while (mTouchAction != MotionEvent.ACTION_UP || arrayList.size() >= B_CURVE_COUNT_OF_POINT)
            {
                if (arrayList.size() >= B_CURVE_COUNT_OF_POINT)
                {
                    try
                    {
                        Point point0 = arrayList.get(0);
                        Point point1 = arrayList.get(1);
                        Point point2 = arrayList.get(2);
                        Point point3 = arrayList.get(3);
                        Point point4 = arrayList.get(4);

                        Point point234Mid = getTriangleCenter(point2, point3, point4);

                        if (point234Mid != null)
                        {
                            drawBCurve(point0, point1, point2, point234Mid);

                            arrayList.remove(0);
                            arrayList.remove(0);
                            arrayList.remove(0);
                            arrayList.remove(0);
                            arrayList.add(0, point234Mid);

                            publishProgress(arrayList.size());
                        }
                    } catch (Exception e)
                    {
                        break;
                    }
                }

                Thread.yield();
            }

            return arrayList;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            invalidate();
        }

        @Override
        protected void onPostExecute(ArrayList<Point> result)
        {
            if (result == null)
            {
                return;
            }

            int size = result.size();

            if (size == 0)
            {
                return;
            }

            switch (size)
            {
                case 4:
                {
                    Point point0 = result.remove(0);
                    Point point1 = result.remove(0);
                    Point point2 = result.remove(0);
                    Point point3 = result.remove(0);

                    drawBCurve(point0, point1, point2, point3);
                    break;
                }

                case 3:
                {
                    Point point0 = result.remove(0);
                    Point point1 = result.remove(0);
                    Point point2 = result.remove(0);

                    drawBCurve(point0, point1, point2);
                    break;
                }

                case 2:
                {
                    Point point0 = result.remove(0);
                    Point point1 = result.remove(0);

                    mCanvas.drawLine(point0.x, point0.y, point1.x, point1.y, mPaint);
                    break;
                }

                default:
                {
                    Point point0 = result.remove(0);
                    mCanvas.drawPoint(point0.x, point0.y, mPaint);
                    break;
                }
            }

            invalidate();

            if (mIsSignatureChecked == false && isSignatureChecked() == true)
            {
                mIsSignatureChecked = true;

                if (mOnUserActionListener != null)
                {
                    mOnUserActionListener.onConfirmSignature();
                }
            }
        }
    }
}
