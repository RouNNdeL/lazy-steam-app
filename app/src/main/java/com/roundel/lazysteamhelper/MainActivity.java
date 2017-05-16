package com.roundel.lazysteamhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(MainActivity.this, NotificationReceiverService.class);
        intent.putExtra(NotificationReceiverService.EXTRA_NOTIFICATION_TITLE, "new steam login for test");
        intent.putExtra(NotificationReceiverService.EXTRA_NOTIFICATION_BODY, "use code AAAAA");
        startService(intent);

        finish();
    }
}
