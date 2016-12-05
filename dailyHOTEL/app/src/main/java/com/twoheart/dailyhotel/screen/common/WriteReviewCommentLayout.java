package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyEditText;

import static android.R.attr.visible;

/**
 * Created by android_sam on 2016. 12. 5..
 */

public class WriteReviewCommentLayout extends BaseLayout
{
    private View mCompleteView;
    private TextView mToolbarTitleView;
    private View mBodyTitleLayout;
    private DailyEditText mEditTextView;
    private TextView mBottomTextCountView;
    private View mBottomLayout;
    private ScrollView mScrollView;

    private boolean mIsShowAnimationStart;
    private boolean mIsHideAnimationStart;
    private AlphaAnimation mAlphaAnimation;

    private static final int DEFAULT_TEXT_COUNT = 10;
    private static final int TOOLBAR_TITLE_ANIMATION_DURATION = 200;


    public interface OnEventListener extends OnBaseEventListener
    {
        void onCompleteClick(String text);

        void onBackClick(String text);
    }

    public WriteReviewCommentLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        mToolbarTitleView = (TextView) view.findViewById(R.id.titleTextView);
        mCompleteView = view.findViewById(R.id.completeTextView);
        mBodyTitleLayout = view.findViewById(R.id.bodyTitleLayout);
        mBottomLayout = view.findViewById(R.id.textCountLayout);
        mBottomTextCountView = (TextView) view.findViewById(R.id.textCountView);
        mEditTextView = (DailyEditText) view.findViewById(R.id.writeReviewEditText);
        mScrollView = (ScrollView) view.findViewById(R.id.scrollView);

        View backView = view.findViewById(R.id.backImageView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onBackClick(mEditTextView.getText().toString());
            }
        });

        setToolbarTitleVisibility(false, false);

        mCompleteView.setEnabled(false);
        mBottomLayout.setVisibility(View.GONE);

        mEditTextView.addTextChangedListener(mEditTextWatcher);

        mScrollView.setOnScrollChangeListener(mScrollChangedListener);
    }

    public void setData(String text)
    {
        updateCompleteLayout(text);
        int textCount = Util.isTextEmpty(text) == true ? 0 : text.length();
        updateTextCountLayout(textCount);
    }

    private void updateCompleteLayout(String text)
    {
        if (Util.isTextEmpty(text) == true)
        {
            text = "";
        }

        int remainder = DEFAULT_TEXT_COUNT - text.length();
        mCompleteView.setEnabled(remainder <= 0 ? true : false);
        mCompleteView.setOnClickListener(remainder <= 0 ? new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String text = mEditTextView == null ? "" : mEditTextView.getText().toString();
                ((OnEventListener) mOnEventListener).onCompleteClick(text);
                ExLog.d("complete click : " + text);
            }
        } : null);
    }

    private void updateTextCountLayout(int count)
    {
        int remainder = DEFAULT_TEXT_COUNT - count;
        if (remainder <= 0)
        {
            mBottomLayout.setVisibility(View.GONE);
        } else
        {
            mBottomLayout.setVisibility(View.VISIBLE);

            String text = mContext.getString(R.string.label_write_review_comment_count_format, remainder);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);

            int length = Integer.toString(remainder).length();

            stringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_text_c323232)), //
                0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mBottomTextCountView.setText(stringBuilder);
        }
    }

    public void setToolbarTitleVisibility(boolean visible, boolean isAnimation)
    {
        if (mToolbarTitleView == null)
        {
            return;
        }

        if (visible == true)
        {
            if (mToolbarTitleView.getVisibility() != View.VISIBLE)
            {
                if (isAnimation == true && mIsShowAnimationStart == false)
                {
                    mIsHideAnimationStart = false;
                    showTitleViewAnimation();
                } else
                {
                    mToolbarTitleView.setVisibility(View.VISIBLE);

                    mIsShowAnimationStart = false;
                    mIsHideAnimationStart = false;
                }
            }
        } else
        {
            if (mToolbarTitleView.getVisibility() == View.VISIBLE)
            {
                if (isAnimation == true && mIsHideAnimationStart == false)
                {
                    mIsShowAnimationStart = false;
                    hideTitleViewAnimation();
                } else
                {
                    mToolbarTitleView.setVisibility(View.GONE);

                    mIsShowAnimationStart = false;
                    mIsHideAnimationStart = false;
                }
            }
        }
    }

    private void showTitleViewAnimation()
    {
        if (mToolbarTitleView == null)
        {
            return;
        }

        if (mIsShowAnimationStart == true)
        {
            return;
        } else
        {
            mIsShowAnimationStart = true;
        }

        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setDuration(TOOLBAR_TITLE_ANIMATION_DURATION);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                mToolbarTitleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mIsShowAnimationStart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        mToolbarTitleView.startAnimation(mAlphaAnimation);
    }

    private void hideTitleViewAnimation()
    {
        if (mToolbarTitleView == null)
        {
            return;
        }

        if (mIsHideAnimationStart == true)
        {
            return;
        } else
        {
            mIsHideAnimationStart = true;
        }

        if (mAlphaAnimation != null)
        {
            if (mAlphaAnimation.hasEnded() == false)
            {
                mAlphaAnimation.cancel();
            }

            mAlphaAnimation = null;
        }

        mAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimation.setDuration(TOOLBAR_TITLE_ANIMATION_DURATION);
        mAlphaAnimation.setFillBefore(true);
        mAlphaAnimation.setFillAfter(true);

        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mToolbarTitleView.setVisibility(View.GONE);
                mIsHideAnimationStart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }
        });

        mToolbarTitleView.startAnimation(mAlphaAnimation);
    }

    private TextWatcher mEditTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            updateTextCountLayout((s == null || s.length() == 0) ? 0 : s.length());
            updateCompleteLayout((s == null || s.length() == 0) ? "" : s.toString());
        }
    };

    private ScrollView.OnScrollChangeListener mScrollChangedListener = new ScrollView.OnScrollChangeListener()
    {
        private int mBodyTitleBottom = 0;

        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
        {
            if (v != null)
            {
                if (mBodyTitleBottom == 0)
                {
                    int bottom = mBodyTitleLayout.getBottom();
                    if (bottom > 0)
                    {
                        mBodyTitleBottom = bottom;
                    }
                }

                setToolbarTitleVisibility((mBodyTitleBottom <= scrollY ? true : false), true);

                if (Constants.DEBUG == true)
                {
                    ExLog.d("visible : " + visible + " , scrollY : " + scrollY + " , mBodyTitleBottom : " + mBodyTitleBottom);
                }
            }
        }
    };
}
