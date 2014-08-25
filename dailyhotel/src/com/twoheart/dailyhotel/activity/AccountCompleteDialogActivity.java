package com.twoheart.dailyhotel.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
/**
 * 
 * @author jangjunho
 *
 */
public class AccountCompleteDialogActivity extends Activity implements OnClickListener{

	private TextView tvMsg;
	private ImageView btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_account_complete_dialog);

		String msg = getIntent().getStringExtra("msg");
		msg = msg.replace("]", "]\n");

		tvMsg = (TextView)findViewById(R.id.tv_account_complete_dialog_msg);
		tvMsg.setText(msg);

		btnClose = (ImageView)findViewById(R.id.iv_account_complete_dialog_close);
		btnClose.setOnClickListener(this);
	}
	//test

	@Override
	public void onClick(View v) { 
		android.util.Log.e("ACCOUNT_COMPLETE_SCREEN_ON_DIALOG", "closed");
		finish(); 
	}
}
