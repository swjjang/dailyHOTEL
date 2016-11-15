package com.twoheart.dailyhotel.place.layout;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.place.adapter.PlaceDetailImageViewPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyLineIndicator;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyPlaceDetailListView;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;

public abstract class PlaceDetailLayout extends BaseLayout
{
    public static final int STATUS_NONE = 0;
    public static final int STATUS_SELECT_PRODUCT = 1;
    public static final int STATUS_BOOKING = 2;
    public static final int STATUS_SOLD_OUT = 3;

    private static final int VIEW_COUNT = 4;

    protected PlaceDetail mPlaceDetail;
    protected DailyLoopViewPager mViewPager;
    protected DailyLineIndicator mDailyLineIndicator;
    protected View mMoreIconView;

    protected DailyPlaceDetailListView mListView;
    protected PlaceDetailImageViewPagerAdapter mImageAdapter;
    protected TextView mBookingTextView;
    protected TextView mSoldoutTextView;
    protected TextView mDescriptionTextView;
    protected View mPriceOptionLayout;
    protected RadioGroup mPriceRadioGroup;

    protected int mImageHeight;
    protected int mBookingStatus; // 예약 진행 상태로 객실 찾기, 없음, 예약 진행

    protected RecyclerView mProductTypeRecyclerView;
    protected View mProductTypeLayout;
    private View mBottomLayout;
    protected View mProductTypeBackgroundView;
    private Constants.ANIMATION_STATUS mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
    private Constants.ANIMATION_STATE mAnimationState = Constants.ANIMATION_STATE.END;
    private ObjectAnimator mObjectAnimator;
    private AlphaAnimation mAlphaAnimation;
    private AnimatorSet mWishPopupAnimatorSet;
    private int mStatusBarHeight;

    protected com.facebook.drawee.view.SimpleDraweeView mTransSimpleDraweeView;
    protected TextView mTransTotalGradeTextView, mTransPlaceNameTextView;
    protected View mTransGradientView;

    protected DailyTextView mWishButtonTextView;
    protected DailyTextView mWishPopupTextView;

    public enum WishPopupState
    {
        ADD,
        DELETE,
        GONE
    }

    public interface OnEventListener extends OnBaseEventListener
    {
        void showActionBar(boolean isAnimation);

        void hideActionBar(boolean isAnimation);

        void onClickImage(PlaceDetail placeDetail);

        void onSelectedImagePosition(int position);

        void onConciergeClick();

        void showProductInformationLayout();

        void hideProductInformationLayout();

        void showMap();

        void finish();

        void clipAddress(String address);

        void showNavigatorDialog();

        void onCalendarClick();

        void doBooking();

        void downloadCoupon();

        void onWishButtonClick();

        void releaseUiComponent();
    }

    protected abstract String getProductTypeTitle();

    protected abstract View getTitleLayout();

    public abstract void setBookingStatus(int status);

    public abstract void setSelectProduct(int index);

    public PlaceDetailLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mTransSimpleDraweeView = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.transImageView);
        mTransGradientView = view.findViewById(R.id.transGradientView);
        View transGradientTopView = view.findViewById(R.id.transGradientTopView);

        View transTitleLayout = view.findViewById(R.id.transTitleLayout);
        mTransTotalGradeTextView = (TextView) transTitleLayout.findViewById(R.id.transGradeTextView);
        mTransPlaceNameTextView = (TextView) transTitleLayout.findViewById(R.id.transNameTextView);

        if (Util.isUsedMutilTransition() == true)
        {
            setTransImageVisibility(true);
            transTitleLayout.setVisibility(View.VISIBLE);

            mTransSimpleDraweeView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.getLCDWidth(mContext)));
            mTransSimpleDraweeView.setTransitionName(mContext.getString(R.string.transition_place_image));

            mTransGradientView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.getLCDWidth(mContext)));
            mTransGradientView.setTransitionName(mContext.getString(R.string.transition_gradient_bottom_view));
            mTransGradientView.setBackground(makeShaderFactory());

            transGradientTopView.setTransitionName(mContext.getString(R.string.transition_gradient_top_view));
        } else
        {
            setTransImageVisibility(false);
            transTitleLayout.setVisibility(View.GONE);
        }

        mListView = (DailyPlaceDetailListView) view.findViewById(R.id.placeListView);
        mListView.setOnScrollListener(mOnScrollListener);

        // 이미지 ViewPage 넣기.
        mDailyLineIndicator = (DailyLineIndicator) view.findViewById(R.id.viewpagerIndicator);

        mViewPager = (DailyLoopViewPager) view.findViewById(R.id.defaulLoopViewPager);

        mImageAdapter = new PlaceDetailImageViewPagerAdapter(mContext);
        mViewPager.setAdapter(mImageAdapter);

        mDailyLineIndicator.setViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        mDailyLineIndicator.setmOnPageChangeListener(mOnPageChangeListener);

        mImageHeight = Util.getLCDWidth(mContext);
        ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
        layoutParams.height = mImageHeight;
        mViewPager.setLayoutParams(layoutParams);

        mMoreIconView = view.findViewById(R.id.moreIconView);

        mDescriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);

        mProductTypeLayout = view.findViewById(R.id.productTypeLayout);

        TextView productTypeTextView = (TextView) mProductTypeLayout.findViewById(R.id.productTypeTextView);

        productTypeTextView.setText(getProductTypeTitle());
        productTypeTextView.setClickable(true);

        mPriceOptionLayout = mProductTypeLayout.findViewById(R.id.priceOptionLayout);
        mPriceRadioGroup = (RadioGroup) mPriceOptionLayout.findViewById(R.id.priceRadioGroup);

        mPriceOptionLayout.setVisibility(View.GONE);

        mProductTypeRecyclerView = (RecyclerView) mProductTypeLayout.findViewById(R.id.productTypeRecyclerView);
        mProductTypeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        EdgeEffectColor.setEdgeGlowColor(mProductTypeRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
        mProductTypeLayout.setVisibility(View.INVISIBLE);

        mBottomLayout = view.findViewById(R.id.bottomLayout);

        mProductTypeBackgroundView = view.findViewById(R.id.productTypeBackgroundView);
        mProductTypeBackgroundView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).hideProductInformationLayout();
            }
        });

        mBookingTextView = (TextView) mBottomLayout.findViewById(R.id.bookingTextView);
        mSoldoutTextView = (TextView) mBottomLayout.findViewById(R.id.soldoutTextView);

        mWishPopupTextView = (DailyTextView) view.findViewById(R.id.wishListPopupView);
        mWishButtonTextView = (DailyTextView) view.findViewById(R.id.wishListBottonView);
        mWishButtonTextView.setTag(false);
        mWishButtonTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onWishButtonClick();
            }
        });

        setBookingStatus(STATUS_NONE);
        hideProductInformationLayout();
        setUpdateWishPopup(WishPopupState.GONE);
    }

    private PaintDrawable makeShaderFactory()
    {
        // 그라디에이션 만들기.
        final int colors[] = {Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.01f, 0.02f, 0.17f, 0.38f};

        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setShape(new RectShape());

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
        {
            @Override
            public Shader resize(int width, int height)
            {
                return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
            }
        };

        paintDrawable.setShaderFactory(sf);

        return paintDrawable;
    }

    public void setListScrollTop()
    {
        if (mListView == null || mListView.getChildCount() == 0)
        {
            return;
        }

        mListView.smoothScrollBy(0, 0);
        mListView.setSelection(0);
    }

    public boolean isListScrollTop()
    {
        if (mListView == null || mListView.getChildCount() == 0)
        {
            return true;
        }

        View view = mListView.getChildAt(0);
        int scrollY = -view.getTop() + mListView.getFirstVisiblePosition() * view.getHeight();

        return scrollY == 0;
    }

    public void setTransImageVisibility(boolean isVisibility)
    {
        mTransSimpleDraweeView.setVisibility(isVisibility == true ? View.VISIBLE : View.INVISIBLE);
        mTransGradientView.setVisibility(isVisibility == true ? View.VISIBLE : View.INVISIBLE);
    }

    public void setTransBottomGradientBackground(int resId)
    {
        mTransGradientView.setBackgroundResource(resId);
    }

    public void setTransImageView(String url)
    {
        if (mTransSimpleDraweeView.getVisibility() == View.VISIBLE)
        {
            Util.requestImageResize(mContext, mTransSimpleDraweeView, url);
        }
    }

    public void setDefaultImage(String url)
    {
        if (Util.isTextEmpty(url) == true)
        {
            setLineIndicatorVisible(false);
            return;
        }

        setLineIndicatorVisible(true);

        if (mImageAdapter == null)
        {
            mImageAdapter = new PlaceDetailImageViewPagerAdapter(mContext);
        }

        ArrayList<ImageInformation> arrayList = new ArrayList<>();
        arrayList.add(new ImageInformation(url, null));

        mImageAdapter.setData(arrayList);
        mViewPager.setAdapter(mImageAdapter);

        mDailyLineIndicator.setViewPager(mViewPager);
    }

    public void setStatusBarHeight(Activity activity)
    {
        final Window window = activity.getWindow();

        mListView.post(new Runnable()
        {
            @Override
            public void run()
            {
                Rect rect = new Rect();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);

                mStatusBarHeight = rect.top;
            }
        });
    }

    public int getBookingStatus()
    {
        return mBookingStatus;
    }

    public int getCurrentImage()
    {
        if (mViewPager != null)
        {
            return mViewPager.getCurrentItem();
        }

        return 0;
    }

    public void setCurrentImage(int position)
    {
        if (mImageAdapter == null || mViewPager == null)
        {
            return;
        }

        mViewPager.setCurrentItem(position, true);
    }

    public int getTotalImage()
    {
        if (mImageAdapter != null)
        {
            return mImageAdapter.getCount();
        }

        return 0;
    }

    private void setProductInformationLayoutEnabled(boolean enabled)
    {
        mProductTypeLayout.setEnabled(enabled);
        mProductTypeRecyclerView.setEnabled(enabled);
        mProductTypeBackgroundView.setEnabled(enabled);
    }

    public void showProductInformationLayout(int index)
    {
        if (mProductTypeBackgroundView == null || mProductTypeLayout == null)
        {
            Util.restartApp(mContext);
            return;
        }

        if (mObjectAnimator != null)
        {
            if (mObjectAnimator.isRunning() == true)
            {
                mObjectAnimator.cancel();
                mObjectAnimator.removeAllListeners();
            }

            mObjectAnimator = null;
        }

        mProductTypeBackgroundView.setAnimation(null);
        mProductTypeLayout.setAnimation(null);

        mProductTypeBackgroundView.setVisibility(View.VISIBLE);

        mProductTypeLayout.setVisibility(View.VISIBLE);

        mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
        mAnimationState = Constants.ANIMATION_STATE.END;

        setProductInformationLayoutEnabled(true);

        setBookingStatus(STATUS_BOOKING);

        setSelectProduct(index);
    }

    public void hideProductInformationLayout()
    {
        if (mProductTypeBackgroundView == null || mProductTypeLayout == null)
        {
            Util.restartApp(mContext);
            return;
        }

        if (mObjectAnimator != null)
        {
            if (mObjectAnimator.isRunning() == true)
            {
                mObjectAnimator.cancel();
                mObjectAnimator.removeAllListeners();
            }

            mObjectAnimator = null;
        }

        mProductTypeBackgroundView.setAnimation(null);
        mProductTypeLayout.setAnimation(null);

        mProductTypeBackgroundView.setVisibility(View.GONE);

        if (Util.isOverAPI12() == true)
        {
            mProductTypeLayout.setVisibility(View.INVISIBLE);
        } else
        {
            mProductTypeLayout.setVisibility(View.GONE);
        }

        mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
        mAnimationState = Constants.ANIMATION_STATE.END;
    }

    public void showAnimationProductInformationLayout()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.SHOW)
        {
            return;
        }

        setBookingStatus(STATUS_NONE);

        if (Util.isOverAPI12() == true)
        {
            final float y = mBottomLayout.getTop();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            // 리스트 높이 + 아이콘 높이(실제 화면에 들어나지 않기 때문에 높이가 정확하지 않아서 내부 높이를 더함)
            int height = mProductTypeLayout.getHeight();
            mProductTypeLayout.setTranslationY(Util.dpToPx(mContext, height));

            mObjectAnimator = ObjectAnimator.ofFloat(mProductTypeLayout, "y", y, mBottomLayout.getTop() - height);
            mObjectAnimator.setDuration(300);

            mObjectAnimator.addListener(new AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    if (mProductTypeLayout.getVisibility() != View.VISIBLE)
                    {
                        mProductTypeLayout.setVisibility(View.VISIBLE);
                    }

                    mAnimationState = Constants.ANIMATION_STATE.START;
                    mAnimationStatus = Constants.ANIMATION_STATUS.SHOW;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;

                        setProductInformationLayoutEnabled(true);

                        setBookingStatus(STATUS_BOOKING);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = Constants.ANIMATION_STATE.CANCEL;
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            mObjectAnimator.start();
        } else
        {
            if (mProductTypeLayout != null && mProductTypeLayout.getVisibility() != View.VISIBLE)
            {
                mProductTypeLayout.setVisibility(View.VISIBLE);

                mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                mAnimationState = Constants.ANIMATION_STATE.END;

                setProductInformationLayoutEnabled(true);

                setBookingStatus(STATUS_BOOKING);
            }
        }

        showAnimationFadeOut();
    }

    public void hideAnimationProductInformationLayout()
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.HIDE)
        {
            return;
        }

        if (mPlaceDetail == null)
        {
            Util.restartApp(mContext);
            return;
        }

        setBookingStatus(STATUS_NONE);

        if (Util.isOverAPI12() == true)
        {
            final float y = mProductTypeLayout.getY();

            if (mObjectAnimator != null)
            {
                if (mObjectAnimator.isRunning() == true)
                {
                    mObjectAnimator.cancel();
                    mObjectAnimator.removeAllListeners();
                }

                mObjectAnimator = null;
            }

            mObjectAnimator = ObjectAnimator.ofFloat(mProductTypeLayout, "y", y, mBottomLayout.getTop());
            mObjectAnimator.setDuration(300);

            mObjectAnimator.addListener(new AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mAnimationState = Constants.ANIMATION_STATE.START;
                    mAnimationStatus = Constants.ANIMATION_STATUS.HIDE;

                    setProductInformationLayoutEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;

                        hideProductInformationLayout();

                        setBookingStatus(STATUS_SELECT_PRODUCT);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = Constants.ANIMATION_STATE.CANCEL;
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {
                }
            });

            mObjectAnimator.start();

            showAnimationFadeIn();
        } else
        {
            setProductInformationLayoutEnabled(false);

            mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
            mAnimationState = Constants.ANIMATION_STATE.END;

            hideProductInformationLayout();

            setBookingStatus(STATUS_SELECT_PRODUCT);
        }
    }

    /**
     * 점점 밝아짐.
     */
    private void showAnimationFadeIn()
    {
        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimation.setDuration(300);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        if (mProductTypeBackgroundView != null)
        {
            mProductTypeBackgroundView.startAnimation(mAlphaAnimation);
        }
    }

    /**
     * 점점 어두워짐.
     */
    private void showAnimationFadeOut()
    {
        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setDuration(300);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                if (mProductTypeBackgroundView.getVisibility() != View.VISIBLE)
                {
                    mProductTypeBackgroundView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        if (mProductTypeBackgroundView != null)
        {
            mProductTypeBackgroundView.startAnimation(mAlphaAnimation);
        }
    }

    public void setImageInformation(String description)
    {
        if (Util.isTextEmpty(description) == false)
        {
            mDescriptionTextView.setVisibility(View.VISIBLE);
            mDescriptionTextView.setText(description);
        } else
        {
            mDescriptionTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void setLineIndicatorVisible(boolean isShow)
    {
        mMoreIconView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        mDailyLineIndicator.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    public void setWishButtonSelected(boolean isSelected)
    {
        int imageResId = isSelected == true ? R.drawable.ic_heart_fill_s : R.drawable.ic_heart_stroke_s;
        mWishButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, imageResId, 0, 0);
        mWishButtonTextView.setTag(isSelected);
    }

    public void setWishButtonCount(int count)
    {
        String buttonText;
        if (count <= 0)
        {
            buttonText = mContext.getResources().getString(R.string.label_wishlist);
        } else if (count > 9999)
        {
            buttonText = mContext.getResources().getString(R.string.wishlist_count_over_10_thousand);
        } else
        {
            buttonText = Integer.toString(count);
        }

        mWishButtonTextView.setText(buttonText);
    }

    public void setUpdateWishPopup(final WishPopupState state)
    {
        if (WishPopupState.GONE == state)
        {
            mWishPopupTextView.setVisibility(View.GONE);

            if (mWishPopupAnimatorSet != null)
            {
                mWishPopupAnimatorSet.cancel();
                mWishPopupAnimatorSet.removeAllListeners();
                mWishPopupAnimatorSet = null;
            }
        } else
        {
            if (mWishPopupAnimatorSet != null && mWishPopupAnimatorSet.isRunning() == true)
            {
                ExLog.d("WishPopup is Already running");
                return;
            }

            if (WishPopupState.ADD == state)
            {
                mWishPopupTextView.setText(R.string.wishlist_detail_add_message);
                mWishPopupTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_fill_l, 0, 0);
                mWishPopupTextView.setBackgroundResource(R.drawable.shape_filloval_ccdb2453);
            } else
            {
                mWishPopupTextView.setText(R.string.wishlist_detail_delete_message);
                mWishPopupTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_stroke_l, 0, 0);
                mWishPopupTextView.setBackgroundResource(R.drawable.shape_filloval_75000000);
            }

            ObjectAnimator objectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(mWishPopupTextView //
                , PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1.2f, 1.0f) //
                , PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1.2f, 1.0f) //
                , PropertyValuesHolder.ofFloat("alpha", 0.5f, 1.0f, 1.0f) //
            );
            objectAnimator1.setInterpolator(new AccelerateInterpolator());
            objectAnimator1.setDuration(300);


            ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(mWishPopupTextView //
                , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.0f) //
                , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.0f) //
                , PropertyValuesHolder.ofFloat("alpha", 1.0f, 1.0f) //
            );
            objectAnimator2.setDuration(600);


            ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(mWishPopupTextView //
                , PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.7f) //
                , PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.7f) //
                , PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f) //
            );
            objectAnimator3.setDuration(200);

            mWishPopupAnimatorSet = new AnimatorSet();
            mWishPopupAnimatorSet.playSequentially(objectAnimator1, objectAnimator2, objectAnimator3);
            mWishPopupAnimatorSet.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mWishPopupTextView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mWishPopupTextView.setVisibility(View.INVISIBLE);
                    ((OnEventListener) mOnEventListener).releaseUiComponent();
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mWishPopupTextView.setVisibility(View.INVISIBLE);
                    ((OnEventListener) mOnEventListener).releaseUiComponent();
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            mWishPopupAnimatorSet.start();
        }
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
    {
        private int mScrollState = -1;
        private int mScrollPosition = -1;

        @Override
        public void onPageSelected(int position)
        {
            ((OnEventListener) mOnEventListener).onSelectedImagePosition(position);
            setImageInformation(mPlaceDetail.getImageInformationList().get(position).description);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            if (mScrollState == -1 || Float.compare(positionOffset, 0.0f) == 0 || positionOffsetPixels == 0)
            {
                return;
            }

            if (mScrollPosition == -1)
            {
                mScrollPosition = position;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            mScrollState = state;

            switch (state)
            {
                case ViewPager.SCROLL_STATE_IDLE:
                    mScrollPosition = -1;
                    break;

                case ViewPager.SCROLL_STATE_DRAGGING:
                    break;

                case ViewPager.SCROLL_STATE_SETTLING:
                    break;
            }
        }
    };

    private OnScrollListener mOnScrollListener = new OnScrollListener()
    {
        private Rect mTitleLayoutRect = new Rect();

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (view.getAdapter() == null || getBookingStatus() == STATUS_BOOKING)
            {
                return;
            }

            if (firstVisibleItem > 1)
            {
                ((OnEventListener) mOnEventListener).showActionBar(false);
                return;
            }

            if (mStatusBarHeight == 0)
            {
                return;
            }

            View titleLayout = getTitleLayout();

            if (titleLayout == null)
            {
                return;
            }

            titleLayout.getGlobalVisibleRect(mTitleLayoutRect);

            final int TOOLBAR_HEIGHT = mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height);

            if (mTitleLayoutRect.top == mTitleLayoutRect.right)
            {

            } else
            {
                if (mTitleLayoutRect.top <= mStatusBarHeight + TOOLBAR_HEIGHT)
                {
                    ((OnEventListener) mOnEventListener).showActionBar(true);
                } else
                {
                    ((OnEventListener) mOnEventListener).hideActionBar(true);
                }
            }
        }
    };

    protected View.OnTouchListener mEmptyViewOnTouchListener = new View.OnTouchListener()
    {
        private int mMoveState;
        private float mPrevX, mPrevY;

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction() & MotionEventCompat.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                {
                    mPrevX = event.getX();
                    mPrevY = event.getY();

                    mMoveState = 0;
                    mListView.setScrollEnabled(false);

                    try
                    {
                        mViewPager.onTouchEvent(event);
                    } catch (Exception e)
                    {
                    }
                    break;
                }

                case MotionEvent.ACTION_UP:
                {
                    int touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

                    int x = (int) (mPrevX - event.getX());
                    int y = (int) (mPrevY - event.getY());

                    int distance = (int) Math.sqrt(x * x + y * y);

                    if (distance < touchSlop)
                    {
                        ((OnEventListener) mOnEventListener).onClickImage(mPlaceDetail);

                        mMoveState = 0;

                        try
                        {
                            mViewPager.onTouchEvent(event);
                        } catch (Exception e)
                        {
                            event.setAction(MotionEvent.ACTION_CANCEL);
                            event.setLocation(mViewPager.getScrollX(), mViewPager.getScrollY());
                            mViewPager.onTouchEvent(event);
                        }

                        mListView.setScrollEnabled(true);
                        break;
                    }
                }
                case MotionEvent.ACTION_CANCEL:
                {
                    mMoveState = 0;

                    try
                    {
                        mViewPager.onTouchEvent(event);
                    } catch (Exception e)
                    {
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        event.setLocation(mViewPager.getScrollX(), mViewPager.getScrollY());
                        mViewPager.onTouchEvent(event);
                    }

                    mListView.setScrollEnabled(true);
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX();
                    float y = event.getY();

                    if (mMoveState == 0)
                    {
                        if (Math.abs(x - mPrevX) == Math.abs(y - mPrevY))
                        {

                        } else if (Math.abs(x - mPrevX) > Math.abs(y - mPrevY))
                        {
                            // x 축으로 이동한 경우.
                            mMoveState = 100;

                            try
                            {
                                mViewPager.onTouchEvent(event);
                            } catch (Exception e)
                            {
                                event.setAction(MotionEvent.ACTION_CANCEL);
                                event.setLocation(mViewPager.getScrollX(), mViewPager.getScrollY());
                                mViewPager.onTouchEvent(event);
                            }
                        } else
                        {
                            // y축으로 이동한 경우.
                            mMoveState = 10;
                            mListView.setScrollEnabled(true);
                            return true;
                        }
                    } else if (mMoveState == 100)
                    {
                        try
                        {
                            mViewPager.onTouchEvent(event);
                        } catch (Exception e)
                        {
                            event.setAction(MotionEvent.ACTION_CANCEL);
                            event.setLocation(mViewPager.getScrollX(), mViewPager.getScrollY());
                            mViewPager.onTouchEvent(event);
                        }
                    }
                    break;
                }
            }

            return false;
        }
    };
}