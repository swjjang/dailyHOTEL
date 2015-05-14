package com.twoheart.dailyhotel.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class DailySignatureView extends View
{
	private static final int CONFIRM_RAIO_PERCENT_OF_SIGNATURE = 15; // 전체 화면의 25%
	private static final int B_CURVE_COUNT_OF_POINT = 5;

	private Path mPath;
	private Paint mPaint;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private ArrayList<Point> mArrayList;
	private RectF mRectF;
	private OnUserActionListener mOnUserActionListener;

	private int mTouchAction;
	private boolean mIsSignatureChecked;

	public interface OnUserActionListener
	{
		public void onConfirmSignature();
	};

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

		mArrayList = new ArrayList<Point>(100);

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

	private boolean isSignatureChecked()
	{
		if (mBitmap == null)
		{
			return false;
		}

		int bitmapDimenions = mBitmap.getWidth() * mBitmap.getHeight();
		int signatureDimensions = (int) (mRectF.width() * mRectF.height());

		return 100 * signatureDimensions / bitmapDimenions > CONFIRM_RAIO_PERCENT_OF_SIGNATURE;
	}

	private void clearCanvas(Canvas canvas)
	{
		if (canvas == null)
		{
			return;
		}

		canvas.drawColor(getContext().getResources().getColor(R.color.white_a50));

		Paint paint = new Paint();
		paint.setStrokeWidth(Util.dpToPx(getContext(), 1));
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getResources().getColor(R.color.dh_theme_color));
		canvas.drawRect(0, 0, canvas.getWidth() - paint.getStrokeWidth(), canvas.getHeight() - paint.getStrokeWidth(), paint);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);

		if (mBitmap == null)
		{
			mBitmap = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);

			clearCanvas(mCanvas);
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

				new BCurveTask().execute(mArrayList);
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

	private void drawBCurve(Point p1, Point p2, Point p3, Point p4)
	{
		mPath.reset();
		mPath.moveTo(p1.x, p1.y);
		mPath.cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);

		mCanvas.drawPath(mPath, mPaint);
	}

	private void drawBCurve(Point p1, Point p2, Point p3)
	{
		mPath.reset();
		mPath.moveTo(p1.x, p1.y);
		mPath.quadTo(p2.x, p2.y, p3.x, p3.y);

		mCanvas.drawPath(mPath, mPaint);
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

	public class BCurveTask extends
			AsyncTask<ArrayList<Point>, Integer, ArrayList<Point>>
	{

		@Override
		protected ArrayList<Point> doInBackground(ArrayList<Point>... params)
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
					Point point0 = arrayList.remove(0);
					Point point1 = arrayList.remove(0);
					Point point2 = arrayList.remove(0);
					Point point3 = arrayList.remove(0);
					Point point4 = arrayList.remove(0);

					Point point234Mid = getTringleCenter(point2, point3, point4);

					drawBCurve(point0, point1, point2, point234Mid);

					arrayList.add(0, point4);
					arrayList.add(0, point234Mid);

					publishProgress(arrayList.size());
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
