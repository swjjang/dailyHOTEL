package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityChangeServerDialogDataBinding;
import com.twoheart.dailyhotel.place.base.BaseActivity;

public class ChangeServerUrlDialog extends BaseActivity
{
    ActivityChangeServerDialogDataBinding mVieDataBinding;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, ChangeServerUrlDialog.class);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mVieDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_server_dialog_data);

        mVieDataBinding.prodTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ChangeServerUrlDialog.this, LauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fprod-mobileapi.dailyhotel.kr%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fprod-silo.dailyhotel.me%2F"));
                startActivity(intent);
            }
        });

        mVieDataBinding.stageTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ChangeServerUrlDialog.this, LauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fstage-mobileapi.dailyhotel.me%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fstage-silo.dailyhotel.me%2F"));
                startActivity(intent);
            }
        });

        mVieDataBinding.devTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ChangeServerUrlDialog.this, LauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fdev-mobileapi.dailyhotel.me%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fdev-silo.dailyhotel.me%2F"));
                startActivity(intent);
            }
        });

        mVieDataBinding.alphaServerTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ChangeServerUrlDialog.this, LauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fdev-alpha-mobileapi.dailyhotel.me%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fdev-silo.dailyhotel.me%2F"));
                startActivity(intent);
            }
        });

        mVieDataBinding.canaryServerTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ChangeServerUrlDialog.this, LauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fbeta-mobileapi.dailyhotel.me%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fbeta-silo.dailyhotel.me%2F"));
                intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=http%3A%2F%2F10.0.1.159:8090%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fdev-silo.dailyhotel.me%2F"));
                startActivity(intent);
            }
        });
    }
}