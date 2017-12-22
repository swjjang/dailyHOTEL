package com.daily.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface BaseFragmentInterface
{
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onDestroy();

    void onActivityCreated(Bundle savedInstanceState);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
