package com.daily.base;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.daily.base.util.VersionUtils;

public abstract class BaseActivity<T1 extends BasePresenter> extends AppCompatActivity
{
    public static final String INTENT_EXTRA_DATA_DEEPLINK = "deepLink";
    public static final String INTENT_EXTRA_DATA_ANALYTICS = "analytics";

    public static final int RESULT_CODE_REFRESH = -10000; // 종료시에 화면 리플래쉬를 요청한다.
    public static final int RESULT_CODE_ERROR = -10001; // 종료시에 에러를 알려준다
    public static final int RESULT_CODE_START_CALENDAR = -10002; // 종료시 캘린더를 요청한다.
    public static final int RESULT_CODE_START_AROUND_SEARCH = -10003; // 종료시 주변 검색을 요청한다.
    public static final int RESULT_CODE_BACK = -10004; // 한단계 뒤로 이동
    public static final int RESULT_CODE_DATA_CHANGED = -10005; // 데이터가 변동되었다.

    private T1 mPresenter;

    private boolean mScreenTouchEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (VersionUtils.isOverAPI21() == true && VersionUtils.isOverAPI23() == false)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.default_statusbar_background));
        }

        mPresenter = createInstancePresenter();

        if (mPresenter.onIntent(getIntent()) == false)
        {
            finish();
            return;
        }

        mPresenter.onPostCreate();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        if (mPresenter != null)
        {
            mPresenter.onNewIntent(intent);
        }
    }

    protected abstract @NonNull
    T1 createInstancePresenter();

    public @NonNull
    T1 getPresenter()
    {
        if (mPresenter == null)
        {
            mPresenter = createInstancePresenter();
        }

        return mPresenter;
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (mPresenter != null)
        {
            mPresenter.onStart();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mPresenter != null)
        {
            mPresenter.onResume();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (mScreenTouchEnabled == false)
        {
            return true;
        }

        try
        {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e)
        {
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        if (mPresenter != null)
        {
            mPresenter.onSaveInstanceState(outState);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        if (mPresenter != null)
        {
            mPresenter.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (mPresenter != null)
        {
            mPresenter.onPause();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mPresenter != null)
        {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void onBackPressed()
    {
        // 이미 종료했는데 onBacPressed()가 연속으로 호출 되는 경우가 있다.
        if (isFinishing() == true || (VersionUtils.isOverAPI17() == true && isDestroyed() == true))
        {
            return;
        }

        try
        {
            if (mPresenter != null)
            {
                if (mPresenter.onBackPressed() == false)
                {
                    super.onBackPressed();
                }
            } else
            {
                super.onBackPressed();
            }
        } catch (Exception e)
        {
            finish();
        }
    }

    @Override
    public void finish()
    {
        if (mPresenter != null)
        {
            mPresenter.onPreFinish();
        }

        super.finish();

        if (mPresenter != null)
        {
            mPresenter.onPostFinish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (mPresenter != null)
        {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setScreenTouchEnabled(boolean touchEnabled)
    {
        mScreenTouchEnabled = touchEnabled;
    }

    /**
     * DailyLock에서 사용하는 메소드
     */
    public void onLockProgressBackPressed()
    {
        // 이미 종료했는데 onBacPressed()가 연속으로 호출 되는 경우가 있다.
        if (isFinishing() == true || (VersionUtils.isOverAPI17() == true && isDestroyed() == true))
        {
            return;
        }

        onBackPressed();

        //        if (this instanceof PlaceDetailActivity == true)
        //        {
        //
        //        } else if (this instanceof MainActivity == false && isFinishing() == false)
        //        {
        //            finish();
        //        }
    }

    protected boolean equalsCallingActivity(Class className)
    {
        ComponentName callingActivity = getCallingActivity();

        if (callingActivity == null || className == null)
        {
            return false;
        }

        return callingActivity.getClassName().equalsIgnoreCase(className.getName());
    }
}
