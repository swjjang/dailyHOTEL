package com.daily.dailyhotel.screen.common.images;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Animatable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityImageListDataBinding;
import com.twoheart.dailyhotel.databinding.ListRowImageDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyPlaceDetailListView;

import java.io.IOException;
import java.util.List;

public class ImageListView extends BaseDialogView<ImageListView.OnEventListener, ActivityImageListDataBinding>//
    implements ImageListInterface, View.OnTouchListener
{
    private float mY;
    private boolean mIsMoved, mIsTop, mIsBottom;
    private VelocityTracker mVelocityTracker;

    private ImageDetailListAdapter mImageDetailListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onTouchMoving(boolean moving);
    }

    public ImageListView(BaseActivity baseActivity, ImageListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityImageListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.translationView.setClickable(true);
        viewDataBinding.translationView.setOnTouchListener(this);
        viewDataBinding.listView.setClickable(false);

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.listView, getColor(R.color.default_over_scroll_edge));
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (mVelocityTracker == null)
        {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);

        DailyPlaceDetailListView listView = getViewDataBinding().listView;

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mY = event.getY();
                getViewDataBinding().alphaView.setAlpha(1.0f);
                getViewDataBinding().alphaView.setBackgroundResource(R.color.white);

                final int firstChildIndex = 0;
                final int lastChildIndex = listView.getChildCount() - 1;
                final int TITLE_BAR_SIZE = listView.getTop();

                if (listView.getChildAt(firstChildIndex).getTop() == listView.getTop() - TITLE_BAR_SIZE)
                {
                    Integer topPosition = (Integer) listView.getChildAt(firstChildIndex).getTag();

                    if (topPosition != null && topPosition == 0)
                    {
                        mIsTop = true;
                    }
                } else if (listView.getChildAt(lastChildIndex).getBottom() == listView.getBottom() - TITLE_BAR_SIZE)
                {
                    Integer bottomPosition = (Integer) listView.getChildAt(lastChildIndex).getTag();

                    if (bottomPosition != null && bottomPosition == listView.getCount() - 1)
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
                    if ((mIsTop == true && y < 0) || (mIsBottom == true && y > 0))
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
                        if (listView.getGlowTopScaleY() == 0.0f)
                        {
                            mIsMoved = true;
                        }
                    } else if (mIsBottom == true && y < 0)
                    {
                        mIsMoved = true;
                    }

                    if (mIsMoved == true)
                    {
                        event.setAction(MotionEvent.ACTION_UP);
                        listView.onTouchEvent(event);

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

                if (mIsMoved == true || listView.getTranslationY() != 0.0f)
                {
                    mVelocityTracker.computeCurrentVelocity(1);
                    float yVelocity = Math.abs(mVelocityTracker.getYVelocity());

                    if (yVelocity > 5.0f || Math.abs(listView.getTranslationY()) > (ScreenUtils.getScreenHeight(getContext()) / 4))
                    {
                        getEventListener().onBackClick();
                        return false;
                    }
                }

                listView.setTranslationY(0);
                getViewDataBinding().alphaView.setAlpha(1.0f);
                getViewDataBinding().toolbarView.setTranslationY(0);
                getViewDataBinding().toolbarView.setAlpha(1.0f);

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
            listView.onTouchEvent(event);
        }

        getEventListener().onTouchMoving(mIsMoved);

        return false;
    }

    @Override
    public void setImageList(String imageUrl, List<DetailImageInformation> imageList, int position)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (mImageDetailListAdapter == null)
        {
            mImageDetailListAdapter = new ImageDetailListAdapter(getContext(), 0, imageUrl);
            getViewDataBinding().listView.setAdapter(mImageDetailListAdapter);
        }

        mImageDetailListAdapter.clear();
        mImageDetailListAdapter.addAll(imageList);
        getViewDataBinding().listView.setSelection(position);
        mImageDetailListAdapter.notifyDataSetChanged();
    }

    private void initToolbar(ActivityImageListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackImageResource(R.drawable.navibar_ic_x);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private void scrollListEffect(float y)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().listView.setTranslationY(y);
        getViewDataBinding().toolbarView.setTranslationY(y);

        getViewDataBinding().alphaView.setBackgroundResource(R.color.black);
        getViewDataBinding().alphaView.setAlpha(1.0f - Math.abs(y * 1.5f) / ScreenUtils.getScreenHeight(getContext()));
    }

    private class ImageDetailListAdapter extends ArrayAdapter<DetailImageInformation>
    {
        private Context mContext;
        String mImageUrl;

        public ImageDetailListAdapter(Context context, int resourceId, String imageUrl)
        {
            super(context, resourceId);

            mContext = context;
            mImageUrl = imageUrl;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ListRowImageDataBinding dataBinding;

            if (convertView == null)
            {
                dataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_image_data, parent, false);
            } else
            {
                dataBinding = DataBindingUtil.bind(convertView);
            }

            DetailImageInformation detailImageInformation = getItem(position);

            dataBinding.getRoot().setTag(position);

            if (DailyTextUtils.isTextEmpty(detailImageInformation.caption) == false)
            {
                dataBinding.descriptionTextView.setVisibility(View.VISIBLE);
                dataBinding.descriptionTextView.setText(detailImageInformation.caption);
            } else
            {
                dataBinding.descriptionTextView.setVisibility(View.INVISIBLE);
            }

            dataBinding.imageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            String url;
            ImageMap imageMap = detailImageInformation.getImageMap();
            if (ScreenUtils.getScreenWidth(mContext) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
            {
                if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
                {
                    url = imageMap.smallUrl;
                } else
                {
                    url = imageMap.bigUrl;
                }
            } else
            {
                if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
                {
                    url = imageMap.smallUrl;
                } else
                {
                    url = imageMap.mediumUrl;
                }
            }

            ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
            {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable)
                {
                    if (imageInfo == null)
                    {
                        return;
                    }

                    setImageViewHeight(dataBinding.imageView, imageInfo.getWidth(), imageInfo.getHeight());
                }

                @Override
                public void onFailure(String id, Throwable throwable)
                {
                    if (DailyTextUtils.isTextEmpty(url) == true)
                    {
                        return;
                    }

                    if (throwable instanceof IOException == true)
                    {
                        if (url.equalsIgnoreCase(imageMap.bigUrl) == true)
                        {
                            imageMap.bigUrl = null;
                        } else if (url.equalsIgnoreCase(imageMap.mediumUrl) == true)
                        {
                            imageMap.mediumUrl = null;
                        } else
                        {
                            // 작은 이미지를 로딩했지만 실패하는 경우.
                            return;
                        }

                        dataBinding.imageView.setImageURI(mImageUrl + imageMap.smallUrl);
                    }
                }
            };

            DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
                .setControllerListener(controllerListener).setUri(mImageUrl + url).build();

            dataBinding.imageView.setController(draweeController);

            return dataBinding.getRoot();
        }

        void setImageViewHeight(ImageView imageView, int width, int height)
        {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();

            float scale = (float) ScreenUtils.getScreenWidth(mContext) / width;
            int viewHeight = (int) (scale * height);

            if (layoutParams == null)
            {
                layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, viewHeight);
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
