package com.daily.dailyhotel.screen.home.gourmet.detail.menus;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailPresenter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetMenusDataBinding;
import com.twoheart.dailyhotel.databinding.DialogGourmetTimePickerDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class GourmetMenusView extends BaseDialogView<GourmetMenusView.OnEventListener, ActivityGourmetMenusDataBinding>//
    implements GourmetMenusInterface, View.OnClickListener
{
    GourmetMenusAdapter mGourmetMenusAdapter;
    GourmetCartMenusAdapter mGourmetCartMenusAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCloseClick();

        void onGuideClick();

        void onBookingClick();

        void onScrolled(int position, boolean real);

        void onMoreImageClick(int position);

        void onOperationTimeClick();

        void onVisitTimeClick(String time);

        void onVisitTimeClick(String time, int menuIndex);

        void onMenuOderCountPlusClick(int position);

        void onMenuOderCountMinusClick(int position);

        void onOpenCartMenusClick();

        void onCloseCartMenusClick();

        void onDeleteCartMenuClick(int menuIndex);

        void onCartMenuMinusClick(int menuIndex);

        void onCartMenuPlusClick(int menuIndex);
    }

    public GourmetMenusView(BaseActivity baseActivity, GourmetMenusView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetMenusDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.operationTimeTextView.setOnClickListener(this);
        viewDataBinding.arrowImageView.setOnClickListener(this);
        viewDataBinding.operationTimesBackgroundView.setOnClickListener(this);
        viewDataBinding.closeImageView.setOnClickListener(this);
        viewDataBinding.cartMenusLayout.setOnClickListener(this);
        viewDataBinding.cartMenusBackgroundView.setOnClickListener(this);
        viewDataBinding.cartBookingLayout.setOnClickListener(this);

        viewDataBinding.recyclerView.setLayoutManager(new ZoomCenterLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(viewDataBinding.recyclerView);

        viewDataBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(viewDataBinding.recyclerView.getLayoutManager());
                getEventListener().onScrolled(viewDataBinding.recyclerView.getChildAdapterPosition(view), true);
            }
        });

        viewDataBinding.cartMenusArrowImageView.setOnClickListener(this);
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.cartMenusRecyclerView, getColor(R.color.default_over_scroll_edge));
        viewDataBinding.cartMenusRecyclerView.setItemAnimator(new CartItemAnimator());

        viewDataBinding.guideLayout.setOnClickListener(this);
        viewDataBinding.guideLayout.setVisibility(View.GONE);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().operationTimeTextView.setText(title);
    }

    @Override
    public void setMenuIndicator(int position, int totalCount)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().menuIndicatorTextView.setText(String.format(Locale.KOREA, "%d / %d", position, totalCount));
    }

    @Override
    public void setGourmetMenus(List<GourmetMenu> gourmetMenuList, int position)
    {
        if (getViewDataBinding() == null || gourmetMenuList == null || gourmetMenuList.size() == 0)
        {
            return;
        }

        if (mGourmetMenusAdapter == null)
        {
            mGourmetMenusAdapter = new GourmetMenusAdapter(getContext());
        }

        mGourmetMenusAdapter.clear();
        mGourmetMenusAdapter.addAll(gourmetMenuList);

        mGourmetMenusAdapter.setOnEventListener(new GourmetMenusAdapter.OnEventListener()
        {
            @Override
            public void onMoreImageClick(int index)
            {
                getEventListener().onMoreImageClick(index);
            }

            @Override
            public void onOderCountPlusClick(int position)
            {
                getEventListener().onMenuOderCountPlusClick(position);
            }

            @Override
            public void onOderCountMinusClick(int position)
            {
                getEventListener().onMenuOderCountMinusClick(position);
            }

            @Override
            public void onBackClick()
            {

            }
        });

        getViewDataBinding().recyclerView.setAdapter(mGourmetMenusAdapter);
        getViewDataBinding().recyclerView.post(new Runnable()
        {
            @Override
            public void run()
            {
                ((LinearLayoutManager) (getViewDataBinding().recyclerView.getLayoutManager()))//
                    .scrollToPositionWithOffset(position, (int) mGourmetMenusAdapter.getMenuMargin());
            }
        });
    }

    @Override
    public void setGuideVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().guideLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public Observable<Boolean> hideGuideAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().guideLayout, "alpha", 1.0f, 0.0f);

        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(300);

        return new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                objectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animator)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator)
                    {
                        objectAnimator.removeAllListeners();

                        getViewDataBinding().guideLayout.setVisibility(View.GONE);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator)
                    {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator)
                    {
                    }
                });

                objectAnimator.start();
            }
        };
    }

    @Override
    public void setOperationTimes(List<String> operationTimeList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onVisitTimeClick((String) v.getTag());
            }
        };

        getViewDataBinding().operationTimesGridLayout.removeAllViews();

        DailyTextView fullTimeTextView = new DailyTextView(getContext());
        fullTimeTextView.setText(R.string.label_all);
        fullTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        fullTimeTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cffffff));
        fullTimeTextView.setBackgroundResource(R.drawable.selector_gourmet_time_background_drawable);
        fullTimeTextView.setTag(GourmetDetailPresenter.FULL_TIME);
        fullTimeTextView.setGravity(Gravity.CENTER);
        fullTimeTextView.setOnClickListener(onClickListener);

        GridLayout.LayoutParams fullTimeLayoutParams = new GridLayout.LayoutParams();
        fullTimeLayoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
        fullTimeLayoutParams.height = ScreenUtils.dpToPx(getContext(), 30);
        fullTimeLayoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
        fullTimeLayoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
        fullTimeLayoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 10);
        fullTimeLayoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        getViewDataBinding().operationTimesGridLayout.addView(fullTimeTextView, fullTimeLayoutParams);

        for (String time : operationTimeList)
        {
            try
            {
                // 24시 이후 값은 00:00로 보이도록 한다.
                DailyTextView dailyTextView = new DailyTextView(getContext());
                dailyTextView.setText(DailyCalendar.convertDateFormatString(time, DailyCalendar.ISO_8601_FORMAT, "HH:mm"));
                dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                dailyTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cffffff));
                dailyTextView.setBackgroundResource(R.drawable.selector_gourmet_time_background_drawable);
                dailyTextView.setTag(time);
                dailyTextView.setGravity(Gravity.CENTER);
                dailyTextView.setOnClickListener(onClickListener);

                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
                layoutParams.height = ScreenUtils.dpToPx(getContext(), 30);
                layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
                layoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
                layoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 10);
                layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

                getViewDataBinding().operationTimesGridLayout.addView(dailyTextView, layoutParams);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        // 빈공간 채우기
        int columnCount = getViewDataBinding().operationTimesGridLayout.getColumnCount();
        int size = (operationTimeList.size() + 1) % columnCount;
        size = size > 0 ? columnCount - size : 0;

        for (int i = 0; i < size; i++)
        {
            DailyTextView dailyTextView = new DailyTextView(getContext());
            dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
            layoutParams.height = 1;
            layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.bottomMargin = ScreenUtils.dpToPx(getContext(), 10);
            layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

            getViewDataBinding().operationTimesGridLayout.addView(dailyTextView, layoutParams);
        }

        getViewDataBinding().operationTimesScrollView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setVisitTime(String time)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int size = getViewDataBinding().operationTimesGridLayout.getChildCount();

        for (int i = 0; i < size; i++)
        {
            View childView = getViewDataBinding().operationTimesGridLayout.getChildAt(i);
            String timeTag = (String) childView.getTag();

            if (timeTag != null)
            {
                if (time.equalsIgnoreCase(timeTag) == true)
                {
                    childView.setSelected(true);
                } else
                {
                    childView.setSelected(false);
                }
            }
        }
    }

    @Override
    public Observable<Boolean> openOperationTimes(String selectedTimes)
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        getViewDataBinding().operationTimesLayout.bringToFront();
        getViewDataBinding().getRoot().requestLayout();

        int childCount = getViewDataBinding().operationTimesGridLayout.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            String time = (String) getViewDataBinding().operationTimesGridLayout.getChildAt(i).getTag();

            if (time != null && time.equalsIgnoreCase(selectedTimes) == true)
            {
                getViewDataBinding().operationTimesGridLayout.getChildAt(i).setSelected(true);
            } else
            {
                getViewDataBinding().operationTimesGridLayout.getChildAt(i).setSelected(false);
            }
        }

        ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().operationTimesGridLayout//
            , View.TRANSLATION_Y, -getViewDataBinding().operationTimesGridLayout.getHeight(), 0);

        transObjectAnimator.setDuration(200);
        transObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        transObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                float value = (float) animation.getAnimatedValue();
                float vector = 1.0f - (value / getViewDataBinding().operationTimesGridLayout.getHeight());
                //                float rotation = 180.0f * vector;

                //                getViewDataBinding().arrowImageView.setRotation(rotation);
                getViewDataBinding().operationTimesBackgroundView.setAlpha(vector);
            }
        });

        return new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                transObjectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        getViewDataBinding().operationTimesScrollView.setVisibility(View.VISIBLE);
                        getViewDataBinding().operationTimesBackgroundView.setAlpha(0.0f);
                        getViewDataBinding().operationTimesBackgroundView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        transObjectAnimator.removeAllUpdateListeners();
                        transObjectAnimator.removeAllListeners();

                        getViewDataBinding().arrowImageView.setRotation(180);
                        getViewDataBinding().operationTimesGridLayout.setTranslationY(0.0f);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });

                transObjectAnimator.start();
            }
        };
    }

    @Override
    public Observable<Boolean> closeOperationTimes()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().operationTimesGridLayout//
            , View.TRANSLATION_Y, 0, -getViewDataBinding().operationTimesGridLayout.getHeight());

        transObjectAnimator.setDuration(200);
        transObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        transObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                float value = (float) animation.getAnimatedValue();
                float vector = 1.0f - Math.abs(value / getViewDataBinding().operationTimesGridLayout.getHeight());
                //                float rotation = 180.0f * vector;

                //                getViewDataBinding().arrowImageView.setRotation(rotation);
                getViewDataBinding().operationTimesBackgroundView.setAlpha(vector);
            }
        });

        return new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                transObjectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        transObjectAnimator.removeAllUpdateListeners();
                        transObjectAnimator.removeAllListeners();

                        getViewDataBinding().arrowImageView.setRotation(0);
                        getViewDataBinding().operationTimesGridLayout.setTranslationY(-getViewDataBinding().operationTimesGridLayout.getHeight());
                        getViewDataBinding().operationTimesScrollView.setVisibility(View.INVISIBLE);
                        getViewDataBinding().operationTimesBackgroundView.setVisibility(View.GONE);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });

                transObjectAnimator.start();
            }
        };
    }

    @Override
    public void setMenuOrderCount(int menuIndex, int menuOrderCount, int minimumOrderQuantity, int maximumOrderQuantity, int saleOrderQuantity)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int position = mGourmetMenusAdapter.getPosition(menuIndex);

        getViewDataBinding().recyclerView.post(() -> mGourmetMenusAdapter.setMenuOrderCount(getViewDataBinding().recyclerView.findViewHolderForAdapterPosition(position), position, menuOrderCount, minimumOrderQuantity, maximumOrderQuantity, saleOrderQuantity));
    }

    @Override
    public void showTimePickerDialog(List<String> operationTimeList, int menuIndex)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        DialogGourmetTimePickerDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_gourmet_time_picker_data, null, false);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int size = dataBinding.timeGridLayout.getChildCount();

                for (int i = 0; i < size; i++)
                {
                    dataBinding.timeGridLayout.getChildAt(i).setSelected(v == dataBinding.timeGridLayout.getChildAt(i));
                }

                dataBinding.positiveTextView.setEnabled(true);
            }
        };

        for (String time : operationTimeList)
        {
            try
            {
                DailyTextView dailyTextView = new DailyTextView(getContext());
                dailyTextView.setText(DailyCalendar.convertDateFormatString(time, DailyCalendar.ISO_8601_FORMAT, "HH:mm"));
                dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                dailyTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cffffff));
                dailyTextView.setBackgroundResource(R.drawable.selector_gourmet_time_background_drawable);
                dailyTextView.setTag(time);
                dailyTextView.setGravity(Gravity.CENTER);
                dailyTextView.setOnClickListener(onClickListener);

                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
                layoutParams.height = ScreenUtils.dpToPx(getContext(), 30);
                layoutParams.topMargin = ScreenUtils.dpToPx(getContext(), 10);
                layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
                layoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
                layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

                dataBinding.timeGridLayout.addView(dailyTextView, layoutParams);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        // 빈공간 채우기
        int columnCount = dataBinding.timeGridLayout.getColumnCount();
        int size = operationTimeList.size() % columnCount;
        size = size > 0 ? columnCount - size : 0;

        for (int i = 0; i < size; i++)
        {
            DailyTextView dailyTextView = new DailyTextView(getContext());
            dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            dailyTextView.setTextColor(getColorStateList(R.drawable.selector_text_color_c323232_cffffff));
            dailyTextView.setGravity(Gravity.CENTER);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = ScreenUtils.dpToPx(getContext(), 56);
            layoutParams.height = ScreenUtils.dpToPx(getContext(), 30);
            layoutParams.topMargin = ScreenUtils.dpToPx(getContext(), 10);
            layoutParams.leftMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.rightMargin = ScreenUtils.dpToPx(getContext(), 5);
            layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

            dataBinding.timeGridLayout.addView(dailyTextView, layoutParams);
        }

        dataBinding.negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        dataBinding.positiveTextView.setEnabled(false);
        dataBinding.positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int size = dataBinding.timeGridLayout.getChildCount();
                String time = GourmetDetailPresenter.FULL_TIME;

                for (int i = 0; i < size; i++)
                {
                    if (dataBinding.timeGridLayout.getChildAt(i).isSelected() == true)
                    {
                        time = (String) dataBinding.timeGridLayout.getChildAt(i).getTag();
                        break;
                    }
                }

                getEventListener().onVisitTimeClick(time, menuIndex);

                hideSimpleDialog();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, null, true);
    }

    @Override
    public void setSummeryCart(String text, int totalCount, int totalPrice)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().cartBookingTextView.setText(text);
        getViewDataBinding().cartBookingTotalCountTextView.setText(Integer.toString(totalCount));

        // 하단 종합 내용
        getViewDataBinding().cartMenusCountTextView.setText(getString(R.string.label_gourmet_product_detail_count_tag, totalCount));
        getViewDataBinding().cartMenusTotalPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), totalPrice, false));
    }

    @Override
    public void setCartVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if ((visible == true && getViewDataBinding().cartBookingLayout.getVisibility() == View.VISIBLE) //
            || (visible == false && getViewDataBinding().cartBookingLayout.getVisibility() == View.GONE))
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.GONE;

        if (visible == false)
        {
            getViewDataBinding().cartMenusLayout.setVisibility(View.GONE);
            getViewDataBinding().cartMenusLayout.setTranslationY(0.0f);
            getViewDataBinding().cartMenusArrowImageView.setRotation(180.0f);
            getViewDataBinding().cartMenusArrowImageView.setTranslationY(0.0f);
            getViewDataBinding().cartMenusTopBackgroundView.setTranslationY(0.0f);
        }

        getViewDataBinding().cartMenusTopBackgroundView.setVisibility(flag);
        getViewDataBinding().cartMenusArrowImageView.setVisibility(flag);
        getViewDataBinding().cartBookingLayout.setVisibility(flag);
    }

    @Override
    public void setGourmetCart(GourmetCart gourmetCart)
    {
        if (getViewDataBinding() == null || gourmetCart == null || gourmetCart.getMenuCount() == 0)
        {
            return;
        }

        // 방문시간
        try
        {
            String visitTimeText = getString(R.string.label_gourmet_product_detail_cart_visit_time//
                , DailyCalendar.convertDateFormatString(gourmetCart.visitTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"));
            SpannableString spannableString = new SpannableString(visitTimeText);

            int startIndex = visitTimeText.indexOf(' ');
            spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.dh_theme_color)), //
                startIndex, visitTimeText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewDataBinding().cartMenusVisitTimeTextView.setText(spannableString);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        if (mGourmetCartMenusAdapter == null)
        {
            mGourmetCartMenusAdapter = new GourmetCartMenusAdapter(getContext());
        }

        mGourmetCartMenusAdapter.clear();
        mGourmetCartMenusAdapter.addAll(gourmetCart.getMenuList());

        mGourmetCartMenusAdapter.setOnEventListener(new GourmetCartMenusAdapter.OnEventListener()
        {
            @Override
            public void onDeleteClick(int menuIndex)
            {
                getEventListener().onDeleteCartMenuClick(menuIndex);
            }

            @Override
            public void onMenuCountPlusClick(int menuIndex)
            {
                getEventListener().onCartMenuPlusClick(menuIndex);
            }

            @Override
            public void onMenuCountMinusClick(int menuIndex)
            {
                getEventListener().onCartMenuMinusClick(menuIndex);
            }

            @Override
            public void onBackClick()
            {

            }
        });

        // 하단 종합 내용
        getViewDataBinding().cartMenusCountTextView.setText(getString(R.string.label_gourmet_product_detail_count_tag, gourmetCart.getTotalCount()));
        getViewDataBinding().cartMenusTotalPriceTextView.setText(DailyTextUtils.getPriceFormat(getContext(), gourmetCart.getTotalPrice(), false));

        //
        final int ITEM_HEIGHT = ScreenUtils.dpToPx(getContext(), 88);
        final int VIEW_COUNT = 3;

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) getViewDataBinding().cartMenusRecyclerViewLayout.getLayoutParams();

        if (gourmetCart.getMenuCount() < VIEW_COUNT)
        {
            layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        } else
        {
            layoutParams.height = ITEM_HEIGHT * 2 + ITEM_HEIGHT / 2;
        }

        getViewDataBinding().cartMenusRecyclerView.setAdapter(mGourmetCartMenusAdapter);
        getViewDataBinding().cartMenusRecyclerViewLayout.setLayoutParams(layoutParams);
    }

    @Override
    public void setGourmetCartMenu(int menuIndex, int menuOrderCount, int minimumOrderQuantity, int maximumOrderQuantity, int saleOrderQuantity)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int position = mGourmetCartMenusAdapter.getPosition(menuIndex);

        getViewDataBinding().recyclerView.post(() -> mGourmetCartMenusAdapter.setMenuOrderCount(getViewDataBinding().cartMenusRecyclerView.findViewHolderForAdapterPosition(position), position, menuOrderCount, minimumOrderQuantity, maximumOrderQuantity, saleOrderQuantity));
    }

    @Override
    public Observable<Boolean> openCartMenus(int gourmetMenuCount)
    {
        if (getViewDataBinding() == null || gourmetMenuCount == 0)
        {
            return null;
        }

        getViewDataBinding().cartMenuBookingLayout.bringToFront();
        getViewDataBinding().getRoot().requestLayout();

        //
        final int ITEM_HEIGHT = ScreenUtils.dpToPx(getContext(), 88) + 1;
        final int VIEW_COUNT = 3;
        final int height;

        if (gourmetMenuCount < VIEW_COUNT)
        {
            height = ScreenUtils.dpToPx(getContext(), 42) + gourmetMenuCount * ITEM_HEIGHT + ScreenUtils.dpToPx(getContext(), 36);
        } else
        {
            height = ScreenUtils.dpToPx(getContext(), 42) + ITEM_HEIGHT * 2 + ITEM_HEIGHT / 2 + +ScreenUtils.dpToPx(getContext(), 36);
        }

        getViewDataBinding().cartMenusLayout.setTranslationY(height);
        getViewDataBinding().cartMenusBackgroundView.setAlpha(0.0f);

        ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().cartMenusLayout//
            , View.TRANSLATION_Y, height, 0);

        transObjectAnimator.setDuration(200);
        transObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        transObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                float value = (float) animation.getAnimatedValue();
                float vector = value - height;

                getViewDataBinding().cartMenusTopBackgroundView.setTranslationY(value);
                getViewDataBinding().cartMenusArrowImageView.setTranslationY(value);
                getViewDataBinding().cartMenusBackgroundView.setAlpha(Math.abs(vector) / height);
            }
        });

        return new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                transObjectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        getViewDataBinding().cartMenusLayout.setVisibility(View.VISIBLE);
                        getViewDataBinding().cartMenusBackgroundView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (transObjectAnimator != null)
                        {
                            transObjectAnimator.removeAllListeners();
                            transObjectAnimator.removeAllUpdateListeners();
                        }

                        getViewDataBinding().cartMenusArrowImageView.setRotation(0.0f);
                        getViewDataBinding().cartMenusBackgroundView.setEnabled(true);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });

                transObjectAnimator.start();
            }
        };
    }

    @Override
    public Observable<Boolean> closeCartMenus(int gourmetMenuCount)
    {
        if (getViewDataBinding() == null || getViewDataBinding().cartMenusLayout.getVisibility() != View.VISIBLE)
        {
            return null;
        }

        final int height = getViewDataBinding().cartMenusLayout.getHeight();

        getViewDataBinding().cartMenusBackgroundView.setEnabled(false);

        ObjectAnimator transObjectAnimator = ObjectAnimator.ofFloat(getViewDataBinding().cartMenusLayout//
            , View.TRANSLATION_Y, 0, height);

        transObjectAnimator.setDuration(400);
        transObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        transObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                float value = (float) animation.getAnimatedValue();
                float vector = value - height;

                getViewDataBinding().cartMenusTopBackgroundView.setTranslationY(value);
                getViewDataBinding().cartMenusArrowImageView.setTranslationY(value);
                getViewDataBinding().cartMenusBackgroundView.setAlpha(Math.abs(vector) / height);
            }
        });

        return new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                transObjectAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (transObjectAnimator != null)
                        {
                            transObjectAnimator.removeAllListeners();
                            transObjectAnimator.removeAllUpdateListeners();
                        }

                        getViewDataBinding().cartMenusLayout.setVisibility(View.INVISIBLE);
                        getViewDataBinding().cartMenusLayout.setTranslationY(0.0f);
                        getViewDataBinding().cartMenusArrowImageView.setRotation(180.0f);
                        getViewDataBinding().cartMenusBackgroundView.setVisibility(View.GONE);

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });

                transObjectAnimator.start();
            }
        };
    }

    @Override
    public void notifyGourmetMenusChanged()
    {
        if (getViewDataBinding() == null || mGourmetMenusAdapter == null)
        {
            return;
        }

        mGourmetMenusAdapter.notifyDataSetChanged();
    }

    @Override
    public Observable<Boolean> removeGourmetCartMenu(int menuIndex)
    {
        if (getViewDataBinding() == null || mGourmetMenusAdapter == null)
        {
            return null;
        }

        int position = mGourmetCartMenusAdapter.getPosition(menuIndex);
        RecyclerView.ViewHolder viewHolder = getViewDataBinding().cartMenusRecyclerView.findViewHolderForAdapterPosition(position);

        int gourmetMenuCount = mGourmetCartMenusAdapter.getItemCount() - 1;

        final int ITEM_HEIGHT = ScreenUtils.dpToPx(getContext(), 88) + 1;
        final int VIEW_COUNT = 3;

        ValueAnimator valueAnimator01;

        if (gourmetMenuCount < VIEW_COUNT)
        {
            int height = gourmetMenuCount * ITEM_HEIGHT;

            valueAnimator01 = ValueAnimator.ofInt(getViewDataBinding().cartMenusRecyclerViewLayout.getHeight(), height);
            valueAnimator01.setDuration(300);
            valueAnimator01.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator)
                {
                    if (valueAnimator == null)
                    {
                        return;
                    }

                    int value = (int) valueAnimator.getAnimatedValue();

                    getViewDataBinding().cartMenusRecyclerViewLayout.getLayoutParams().height = value;
                    getViewDataBinding().cartMenusRecyclerViewLayout.setLayoutParams(getViewDataBinding().cartMenusRecyclerViewLayout.getLayoutParams());
                }
            });

            valueAnimator01.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {

                }

                @Override
                public void onAnimationEnd(Animator animator)
                {
                    valueAnimator01.removeAllUpdateListeners();
                    valueAnimator01.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animator)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animator)
                {

                }
            });
        } else
        {
            valueAnimator01 = null;
        }

        ValueAnimator valueAnimator02 = ValueAnimator.ofInt(viewHolder.itemView.getHeight(), 0);
        valueAnimator02.setDuration(300);
        valueAnimator02.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                if (valueAnimator == null)
                {
                    return;
                }

                int value = (int) valueAnimator.getAnimatedValue();

                viewHolder.itemView.getLayoutParams().height = value;
                viewHolder.itemView.setLayoutParams(viewHolder.itemView.getLayoutParams());
            }
        });

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                valueAnimator02.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animator)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator)
                    {
                        valueAnimator02.removeAllUpdateListeners();
                        valueAnimator02.removeAllListeners();

                        mGourmetCartMenusAdapter.remove(position);

                        viewHolder.itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        viewHolder.itemView.setLayoutParams(viewHolder.itemView.getLayoutParams());

                        mGourmetCartMenusAdapter.notifyDataSetChanged();

                        observer.onNext(true);
                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator)
                    {

                    }
                });

                if (valueAnimator01 == null)
                {
                    valueAnimator02.start();
                } else
                {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(valueAnimator01, valueAnimator02);
                    animatorSet.start();
                }
            }
        };

        return observable.subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.operationTimeTextView:
            case R.id.arrowImageView:
            case R.id.operationTimesBackgroundView:
                getEventListener().onOperationTimeClick();
                break;

            case R.id.closeImageView:
                getEventListener().onCloseClick();
                break;

            case R.id.guideLayout:
                getEventListener().onGuideClick();
                break;

            case R.id.cartMenusArrowImageView:
                if (getViewDataBinding().cartMenusLayout.getVisibility() == View.VISIBLE)
                {
                    getEventListener().onCloseCartMenusClick();
                } else
                {
                    getEventListener().onOpenCartMenusClick();
                }
                break;

            case R.id.cartMenusBackgroundView:
                getEventListener().onCloseCartMenusClick();
                break;

            case R.id.cartBookingLayout:
                if (getViewDataBinding().cartMenusLayout.getVisibility() == View.VISIBLE)
                {
                    getEventListener().onBookingClick();
                } else
                {
                    getEventListener().onOpenCartMenusClick();
                }
                break;
        }
    }

    class ZoomCenterLayoutManager extends LinearLayoutManager
    {
        private static final float MIN_SCALE = 0.90f;
        private static final float AMOUNT = 1.0f - MIN_SCALE; // 1.0f - AMOUNT = MIN_SCALE
        private static final float DISTANCE = 0.75f;

        public ZoomCenterLayoutManager(Context context)
        {
            super(context);

            initialize(context);
        }

        public ZoomCenterLayoutManager(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);

            initialize(context);
        }

        public ZoomCenterLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
        {
            super(context, attrs, defStyleAttr, defStyleRes);

            initialize(context);
        }

        private void initialize(Context context)
        {
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state)
        {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            final float midpoint = getWidth() / 2.f;
            final float d1 = DISTANCE * midpoint;
            final float s0 = 1.f;
            //            final float s1 = 1.f - AMOUNT;
            int childCount = getChildCount();

            for (int i = 0; i < childCount; i++)
            {
                View childView = getChildAt(i);
                float childMidpoint = (getDecoratedRight(childView) + getDecoratedLeft(childView)) / 2.f;
                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 - AMOUNT * d / d1;
                float vectorValue = (1.0f - scale) / AMOUNT;

                View blurView = (View) childView.getTag(R.id.blurView);

                if (blurView != null)
                {
                    blurView.setAlpha(vectorValue);
                }
            }

            return scrolled;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position)
        {
            super.smoothScrollToPosition(recyclerView, state, position);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
        {
            super.onLayoutChildren(recycler, state);

            scrollHorizontallyBy(0, recycler, state);
        }
    }


    class CartItemAnimator extends DefaultItemAnimator
    {
        @Override
        public boolean animateRemove(RecyclerView.ViewHolder holder)
        {
            super.animateRemove(holder);

            final View view = holder.itemView;

            int gourmetMenuCount = mGourmetCartMenusAdapter.getItemCount();

            final int ITEM_HEIGHT = ScreenUtils.dpToPx(getContext(), 88) + 1;
            final int VIEW_COUNT = 3;

            if (gourmetMenuCount < VIEW_COUNT)
            {
                int height = gourmetMenuCount * ITEM_HEIGHT;

                ValueAnimator valueAnimator01 = ValueAnimator.ofInt(getViewDataBinding().cartMenusRecyclerViewLayout.getHeight(), height);
                //                valueAnimator01.setDuration(getRemoveDuration());
                valueAnimator01.setStartDelay(getRemoveDuration());
                valueAnimator01.setDuration(getMoveDuration());
                valueAnimator01.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator)
                    {
                        if (valueAnimator == null)
                        {
                            return;
                        }

                        int value = (int) valueAnimator.getAnimatedValue();

                        getViewDataBinding().cartMenusRecyclerViewLayout.getLayoutParams().height = value;
                        getViewDataBinding().cartMenusRecyclerViewLayout.setLayoutParams(getViewDataBinding().cartMenusRecyclerViewLayout.getLayoutParams());
                    }
                });

                valueAnimator01.start();

                //                ValueAnimator valueAnimator02 = ValueAnimator.ofInt(view.getHeight(), 0);
                //                valueAnimator02.setDuration(getRemoveDuration());
                //                //                valueAnimator.setStartDelay(getRemoveDuration());
                //                //                valueAnimator.setDuration(getMoveDuration());
                //                valueAnimator02.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                //                {
                //                    @Override
                //                    public void onAnimationUpdate(ValueAnimator valueAnimator)
                //                    {
                //                        if (valueAnimator == null)
                //                        {
                //                            return;
                //                        }
                //
                //                        int value = (int) valueAnimator.getAnimatedValue();
                //
                //                        view.getLayoutParams().height = value;
                //                        view.setLayoutParams(view.getLayoutParams());
                //                    }
                //                });
                //
                //                AnimatorSet animatorSet = new AnimatorSet();
                //                animatorSet.playTogether(valueAnimator01, valueAnimator02);
                //                animatorSet.start();
            }

            return true;
        }
    }
}
