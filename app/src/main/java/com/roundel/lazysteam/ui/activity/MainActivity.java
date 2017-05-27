package com.roundel.lazysteam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.roundel.lazysteam.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.button) Button mStartSetupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mStartSetupButton.setOnClickListener(this);

        /*Intent intent = new Intent(MainActivity.this, NotificationReceiverService.class);
        intent.putExtra(NotificationReceiverService.EXTRA_NOTIFICATION_TITLE, "new steam login for test");
        intent.putExtra(NotificationReceiverService.EXTRA_NOTIFICATION_BODY, "use code AAAAA");
        startService(intent);*/

        /*WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"TZ01_5_RPT\"";
        conf.preSharedKey="\"bsdzdu7205\"";

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int id = wifiManager.addNetwork(conf);
        Toast.makeText(this, Boolean.toString(wifiManager.enableNetwork(id, true)), Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.button:
            {
                startActivity(new Intent(MainActivity.this, ServerSetupActivity.class));
                break;
            }
        }
    }
}
