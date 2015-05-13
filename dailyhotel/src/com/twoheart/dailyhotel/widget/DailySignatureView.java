package com.twoheart.dailyhotel.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DailySignatureView extends View
{
	private static final int CONFIRM_RAIO_PERCENT_OF_SIGNATURE = 20; // 전체 화면의 25%

	private Path mPath;
	private Paint mPaint;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private ArrayList<Point> mArrayList;
	private RectF mRectF;

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

		mArrayList = new ArrayList<Point>(5);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(5f);
		mPaint.setColor(Color.BLACK);

		mPath = new Path();
	}

	public boolean isSignatureChecked()
	{
		if (mBitmap == null)
		{
			return false;
		}

		int bitmapDimenions = mBitmap.getWidth() * mBitmap.getHeight();
		int signatureDimensions = (int) (mRectF.width() * mRectF.height());

		return 100 * signatureDimensions / bitmapDimenions > CONFIRM_RAIO_PERCENT_OF_SIGNATURE;
	}

	public void clearSignature()
	{
		mRectF = new RectF();

		if (mCanvas != null)
		{
			mCanvas.drawColor(Color.WHITE);
		}

		if (mArrayList != null)
		{
			mArrayList.clear();
		}

		if (mPath != null)
		{
			mPath.reset();
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);

		if (mBitmap == null)
		{
			mBitmap = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
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
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				float x = event.getX();
				float y = event.getY();

				mArrayList.clear();
				mArrayList.add(new Point(x, y));

				if (mRectF.isEmpty() == true)
				{
					mRectF.left = x;
					mRectF.top = y;
					mRectF.right = x;
					mRectF.bottom = y;
				}
				break;
			}

			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_UP:
			{
				float x = event.getX();
				float y = event.getY();

				mArrayList.add(new Point(x, y));
				mRectF.union(x, y);

				if (mArrayList.size() == 5)
				{
					mPath.reset();

					Point point0 = mArrayList.get(0);
					Point point1 = mArrayList.get(1);
					Point point2 = mArrayList.get(2);
					Point point3 = mArrayList.get(3);
					Point point4 = mArrayList.get(4);

					Point point234Mid = getTringleCenter(point2, point3, point4);
					//					Point point24Mid = new Point((point2.x + point4.x) / 2, (point2.y + point4.y) / 2);

					mPath.moveTo(point0.x, point0.y);
					mPath.cubicTo(point1.x, point1.y, point2.x, point2.y, point234Mid.x, point234Mid.y);

					mArrayList.clear();
					mArrayList.add(point234Mid);
					mArrayList.add(point4);

					mCanvas.drawPath(mPath, mPaint);

					invalidate();
				}

				mCanvas.drawRect(mRectF, mPaint);
				break;
			}

			default:
				break;
		}

		return true;
	}

	private Point getTringleCenter(Point p1, Point p2, Point p3)
	{
		return new Point((p1.x + p2.x + p3.x) / 3, (p1.y + p2.y + p3.y) / 3);
	}

	private class Point
	{
		public float x;
		public float y;

		public Point(float x, float y)
		{
			this.x = x;
			this.y = y;
		}
	}
}
