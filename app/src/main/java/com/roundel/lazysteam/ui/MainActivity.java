package com.roundel.lazysteam.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.roundel.lazysteam.service.NotificationReceiverService;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(MainActivity.this, NotificationReceiverService.class);
        intent.putExtra(NotificationReceiverService.EXTRA_NOTIFICATION_TITLE, "new steam login for test");
        intent.putExtra(NotificationReceiverService.EXTRA_NOTIFICATION_BODY, "use code AAAAA");
        startService(intent);


    }
}
