package com.twoheart.dailyhotel.activity;

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
import com.twoheart.dailyhotel.view.widget.FontManager;

/**
 * 화면이 OFF 상태일때 GCM 메시지를 받는 경우 카카오톡 처럼 푸시 다이얼로그가 팝업됨.
 *
 * @author jangjunho
 */
public class PushLockDialogActivity extends Activity implements OnClickListener, Constants
{
    private TextView mPositiveView;
    private TextView mNegativeView;

    private String mLink;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_push_lock_dialog_gcm);

        String message = getIntent().getStringExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG);
        int type = getIntent().getIntExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, -1);
        mLink = getIntent().getStringExtra(NAME_INTENT_EXTRA_DATA_PUSH_LINK);

        TextView messageTextView = (TextView) findViewById(R.id.messageTextView);

        switch (type)
        {
            case PUSH_TYPE_NOTICE:
            {
                // 공지 푸시
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
                    StringBuffer sb = new StringBuffer(message);
                    result = sb.replace(index, index + 1, "]\n").toString();
                }

                messageTextView.setText(result);
                break;
            }
        }

        messageTextView.setTypeface(FontManager.getInstance(this).getMediumTypeface());

        mPositiveView = (TextView) findViewById(R.id.positiveTextView);
        mNegativeView = (TextView) findViewById(R.id.negativeTextView);

        mPositiveView.setOnClickListener(this);
        mNegativeView.setOnClickListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        WakeLock.releaseWakeLock();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == mPositiveView.getId())
        {
            if (Util.isTextEmpty(mLink) == false)
            {
                Intent intent = new Intent(this, LauncherActivity.class);
                intent.setData(Uri.parse(mLink));

                startActivity(intent);
            }

            finish();
        } else if (v.getId() == mNegativeView.getId())
        {
            finish();
        }
    }

}
