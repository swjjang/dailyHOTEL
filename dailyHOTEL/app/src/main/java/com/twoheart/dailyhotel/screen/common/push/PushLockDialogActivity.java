package com.twoheart.dailyhotel.screen.common.push;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.WakeLock;

/**
 * 화면이 OFF 상태일때 GCM 메시지를 받는 경우 카카오톡 처럼 푸시 다이얼로그가 팝업됨.
 *
 * @author jangjunho
 */
public class PushLockDialogActivity extends Activity implements OnClickListener, Constants
{
    private String mLink;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Util.isOverAPI21() == true && Util.isOverAPI23() == false)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbar_background));
        }

        setContentView(R.layout.activity_push_lock_dialog_gcm);

        String title = getIntent().getStringExtra(NAME_INTENT_EXTRA_DATA_PUSH_TITLE);
        String message = getIntent().getStringExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG);
        int type = getIntent().getIntExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, -1);
        mLink = getIntent().getStringExtra(NAME_INTENT_EXTRA_DATA_PUSH_LINK);

        TextView messageTextView = (TextView) findViewById(R.id.messageTextView);

        switch (type)
        {
            case PUSH_TYPE_NOTICE:
            {
                // 공지 푸시
                message = message.replaceAll("\\\\n", "\n");

                messageTextView.setText(message);
                break;
            }

            case PUSH_TYPE_ACCOUNT_COMPLETE:
            {
                // 계좌이체 결제 완료 푸시
                String result = message;
                if (message.contains("]"))
                {
                    // [호텔이름 [조식 포함]] 예약되었습니다. 과 같은 경우 마지막 ] 다음에서 개행하여 보기 좋도록 표시
                    int index = message.lastIndexOf(']');
                    StringBuilder stringBuilder = new StringBuilder(message);
                    result = stringBuilder.replace(index, index + 1, "]\n").toString();
                }

                messageTextView.setText(result);
                break;
            }
        }

        if (Util.isTextEmpty(title) == true)
        {
            title = getString(R.string.dialog_notice2);
        }

        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        View positiveTextView = findViewById(R.id.positiveTextView);
        View negativeTextView = findViewById(R.id.negativeTextView);
        positiveTextView.setOnClickListener(this);
        negativeTextView.setOnClickListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        WakeLock.releaseWakeLock();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.positiveTextView:
                Intent intent = new Intent(this, LauncherActivity.class);

                if (Util.isTextEmpty(mLink) == false)
                {
                    intent.setData(Uri.parse(mLink));
                }

                startActivity(intent);
                finish();
                break;

            case R.id.negativeTextView:
                finish();
                break;
        }
    }
}
