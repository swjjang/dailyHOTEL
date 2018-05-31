package com.daily.base;

import android.content.Intent;
import android.os.Bundle;

public interface BaseActivityInterface
{
    boolean onIntent(Intent intent);

    void onNewIntent(Intent intent);

    void onStart();

    void onResume();

    void onPause();

    void onDestroy();

    void onPreFinish();

    void onPostFinish();

    boolean onBackPressed();

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
