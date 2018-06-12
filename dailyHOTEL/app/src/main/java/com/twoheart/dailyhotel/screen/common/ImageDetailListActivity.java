package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyPlaceDetailListView;

import java.util.ArrayList;
import java.util.List;

public class ImageDetailListActivity extends BaseActivity implements Constants
{
    private static final String INTENT_EXTRA_DATA_PLACETYPE = "placeType";
    private static final String INTENT_EXTRA_DATA_TITLE = "title";

    DailyPlaceDetailListView mListView;
    View mAlphaView;
    float mY;
    boolean mIsMoved, mIsTop, mIsBottom;
    VelocityTracker mVelocityTracker;
    DailyToolbarView mToolbarView;

    PlaceType mPlaceType;

    public static Intent newInstance(Context context, Constants.PlaceType placeType, String title, List<ImageInformation> arrayList, int position)
    {
        Intent intent = new Intent(context, ImageDetailListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST, (ArrayList) arrayList);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, position);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail_list);

        int position;
        ArrayList<ImageInformation> arrayList;
        String title;

        Intent intent = getIntent();

        if (intent == null)
        {
            return;

        } else
        {
            try
            {
                mPlaceType = Constants.PlaceType.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_PLACETYPE));
            } catch (Exception e)
            {
                Util.restartApp(this);
                return;
            }

            title = intent.getStringExtra(INTENT_EXTRA_DATA_TITLE);
            arrayList = intent.getParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST);
            position = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, 0);
        }

        if (arrayList == null || arrayList.size() == 0)
        {
            finish();
            return;
        }

        initToolbar(title);
        initLayout(arrayList, position);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (mPlaceType == null)
        {
            Util.restartApp(this);
            return;
        }

        switch (mPlaceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_HOTELIMAGEVIEW, null);
                break;

            case FNB:
                AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYGOURMET_GOURMETIMAGEVIEW, null);
                break;
        }
    }

    private void initToolbar(String title)
    {
        mToolbarView = findViewById(R.id.toolbarView);
        mToolbarView.setTitleText(title);
        mToolbarView.setBackVisible(false);

        mToolbarView.clearMenuItem();
        mToolbarView.addMenuItem(DailyToolbarView.MenuItem.CLOSE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (mPlaceType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(ImageDetailListActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                            AnalyticsManager.Action.HOTEL_IMAGE_CLOSED, AnalyticsManager.Label.CLOSE_, null);
                        break;

                    case FNB:
                        AnalyticsManager.getInstance(ImageDetailListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                            AnalyticsManager.Action.GOURMET_IMAGE_CLOSED, AnalyticsManager.Label.CLOSE_, null);
                        break;
                }

                finish();
            }
        });
    }

    private void initLayout(ArrayList<ImageInformation> arrayList, final int position)
    {
        mListView = findViewById(R.id.listView);
        View translationView = findViewById(R.id.translationView);
        translationView.setClickable(true);
        mAlphaView = findViewById(R.id.alphaView);

        ImageDetailListAdapter adapter = new ImageDetailListAdapter(this, 0, arrayList);
        mListView.setAdapter(adapter);
        mListView.post(new Runnable()
        {
            @Override
            public void run()
            {
                mListView.setSelection(position);
            }
        });
        mListView.setClickable(false);

        translationView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction() & MotionEvent.ACTION_MASK;

                if (mVelocityTracker == null)
                {
                    mVelocityTracker = VelocityTracker.obtain();
                }

                mVelocityTracker.addMovement(event);

                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mY = event.getY();
                        mAlphaView.setAlpha(1.0f);
                        mAlphaView.setBackgroundResource(R.color.white);

                        final int firstChildIndex = 0;
                        final int lastChildIndex = mListView.getChildCount() - 1;
                        final int TITLE_BAR_SIZE = mListView.getTop();

                        if (mListView.getChildAt(firstChildIndex).getTop() == mListView.getTop() - TITLE_BAR_SIZE)
                        {
                            Integer topPosition = (Integer) mListView.getChildAt(firstChildIndex).getTag();

                            if (topPosition != null && topPosition == 0)
                            {
                                mIsTop = true;
                            }
                        }

                        if (mListView.getChildAt(lastChildIndex).getBottom() <= mListView.getBottom() - TITLE_BAR_SIZE)
                        {
                            Integer bottomPosition = (Integer) mListView.getChildAt(lastChildIndex).getTag();

                            if (bottomPosition != null && bottomPosition == mListView.getCount() - 1)
                            {
                                mIsBottom = true;
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                    {
                        float y = event.getY() - mY;

                        if (mIsMoved == true)
                        {
                            // 사진 개수가 화면보다 작다.
                            if (mIsTop == true && mIsBottom == true)
                            {
                                scrollListEffect(y);
                            } else if ((mIsTop == true && y < 0) || (mIsBottom == true && y > 0))
                            {
                                mIsMoved = false;
                            } else
                            {
                                scrollListEffect(y);
                            }
                        } else
                        {
                            if ((mIsTop == true && y > 0))
                            {
                                if (mListView.getGlowTopScaleY() == 0.0f)
                                {
                                    mIsMoved = true;
                                }
                            }

                            if (mIsBottom == true && y < 0)
                            {
                                mIsMoved = true;
                            }

                            if (mIsMoved == true)
                            {
                                event.setAction(MotionEvent.ACTION_UP);
                                mListView.onTouchEvent(event);

                                scrollListEffect(y);
                            } else
                            {
                                if (y != 0)
                                {
                                    mIsTop = false;
                                    mIsBottom = false;
                                }
                            }
                        }
                        break;
                    }

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                    {
                        mY = 0;

                        if (mIsMoved == true || mListView.getTranslationY() != 0.0f)
                        {
                            mVelocityTracker.computeCurrentVelocity(1);
                            float yVelocity = Math.abs(mVelocityTracker.getYVelocity());

                            if (yVelocity > 5.0f || Math.abs(mListView.getTranslationY()) > (ScreenUtils.getScreenHeight(ImageDetailListActivity.this) / 4))
                            {
                                switch (mPlaceType)
                                {
                                    case HOTEL:
                                        AnalyticsManager.getInstance(ImageDetailListActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                                            AnalyticsManager.Action.HOTEL_IMAGE_CLOSED, AnalyticsManager.Label.SWIPE, null);
                                        break;

                                    case FNB:
                                        AnalyticsManager.getInstance(ImageDetailListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                                            AnalyticsManager.Action.GOURMET_IMAGE_CLOSED, AnalyticsManager.Label.SWIPE, null);
                                        break;
                                }

                                finish();
                                return false;
                            }
                        }

                        mListView.setTranslationY(0);
                        mToolbarView.setTranslationY(0);
                        mAlphaView.setAlpha(1.0f);
                        mToolbarView.setAlpha(1.0f);

                        if (mVelocityTracker != null)
                        {
                            mVelocityTracker.recycle();
                            mVelocityTracker = null;
                        }

                        mIsTop = false;
                        mIsBottom = false;
                        mIsMoved = false;
                        break;
                    }
                }

                if (mIsMoved == false)
                {
                    mListView.onTouchEvent(event);
                }

                return false;
            }
        });
    }

    void scrollListEffect(float y)
    {
        mListView.setTranslationY(y);
        mToolbarView.setTranslationY(y);

        mAlphaView.setBackgroundResource(R.color.black);
        mAlphaView.setAlpha(1.0f - Math.abs(y * 1.5f) / ScreenUtils.getScreenHeight(ImageDetailListActivity.this));
        //        mToolbarView.setAlpha(1.0f - Math.abs(y * 20) / ScreenUtils.getScreenHeight(ImageDetailListActivity.this));
    }

    @Override
    public void finish()
    {
        super.finish();

        if (mIsMoved == true)
        {
            overridePendingTransition(R.anim.hold, R.anim.fade_out);
        } else
        {
            overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        switch (mPlaceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
                    AnalyticsManager.Action.HOTEL_IMAGE_CLOSED, AnalyticsManager.Label.BACK, null);
                break;

            case FNB:
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                    AnalyticsManager.Action.GOURMET_IMAGE_CLOSED, AnalyticsManager.Label.BACK, null);
                break;
        }
    }

    private class ImageDetailListAdapter extends ArrayAdapter<ImageInformation>
    {
        public ImageDetailListAdapter(Context context, int resourceId, List<ImageInformation> list)
        {
            super(context, resourceId, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view;

            Uri imageUri = DailyTextUtils.isTextEmpty(getItem(position).getImageUrl()) == true ? null : Uri.parse(getItem(position).getImageUrl());
            String description = getItem(position).description;

            if (convertView == null)
            {
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.list_row_image, parent, false);
            } else
            {
                view = convertView;
            }

            view.setTag(position);

            TextView textView = view.findViewById(R.id.descriptionTextView);
            final com.facebook.drawee.view.SimpleDraweeView imageView = view.findViewById(R.id.imageView);

            if (DailyTextUtils.isTextEmpty(description) == false)
            {
                textView.setVisibility(View.VISIBLE);
                textView.setText(description);
            } else
            {
                textView.setVisibility(View.INVISIBLE);
            }

            DraweeController controller;
            BaseControllerListener<ImageInfo> baseControllerListener = new BaseControllerListener<ImageInfo>()
            {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
                {
                    if (imageInfo == null)
                    {
                        return;
                    }

                    setImageViewHeight(imageView, imageInfo.getWidth(), imageInfo.getHeight());
                }
            };

            if (ScreenUtils.getScreenWidth(getContext()) >= 720)
            {
                controller = Fresco.newDraweeControllerBuilder()//
                    .setControllerListener(baseControllerListener)//
                    .setUri(imageUri).build();
            } else
            {
                final int resizeWidth = 360, resizeHeight = 240;

                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(imageUri)//
                    .setResizeOptions(new ResizeOptions(resizeWidth, resizeHeight))//
                    .build();

                controller = Fresco.newDraweeControllerBuilder()//
                    .setOldController(imageView.getController())//
                    .setImageRequest(imageRequest)//
                    .setControllerListener(baseControllerListener).build();
            }

            imageView.setController(controller);
            imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            return view;
        }

        void setImageViewHeight(ImageView imageView, int width, int height)
        {
            RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) imageView.getLayoutParams();

            float scale = (float) ScreenUtils.getScreenWidth(getContext()) / width;
            int viewHeight = (int) (scale * height);

            if (layoutParams == null)
            {
                layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, viewHeight);
            } else
            {
                layoutParams.height = viewHeight;
            }

            imageView.setLayoutParams(layoutParams);
        }

        @Override
        public boolean areAllItemsEnabled()
        {
            return true;
        }
    }
}
