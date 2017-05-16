package com.roundel.lazysteamhelper;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Krzysiek on 16/05/2017.
 */

public class NotificationReceiverService extends IntentService implements ServerDiscoveryThread.ServerDiscoveryListener
{
    private static final String TAG = NotificationReceiverService.class.getSimpleName();

    public static final String EXTRA_NOTIFICATION_BODY = "com.roundel.lazysteamhelper.extra.NOTIFICATION_BODY";
    public static final String EXTRA_NOTIFICATION_TITLE = "com.roundel.lazysteamhelper.extra.NOTIFICATION_TITLE";

    private static final Pattern REGEX_USERNAME = Pattern.compile("new steam login for (.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern REGEX_CODE = Pattern.compile("use code ([A-Z\\d]{5})", Pattern.CASE_INSENSITIVE);

    private String mCode;
    private String mUsername;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationReceiverService(String name)
    {
        super(name);
    }

    public NotificationReceiverService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        if(intent != null && intent.hasExtra(EXTRA_NOTIFICATION_BODY) && intent.hasExtra(EXTRA_NOTIFICATION_TITLE))
        {
            String body = intent.getStringExtra(EXTRA_NOTIFICATION_BODY);
            String title = intent.getStringExtra(EXTRA_NOTIFICATION_TITLE);

            Matcher codeMatcher = REGEX_CODE.matcher(body);
            Matcher usernameMatcher = REGEX_USERNAME.matcher(title);

            if(codeMatcher.matches() && usernameMatcher.matches() &&
                    codeMatcher.groupCount() == 1 && usernameMatcher.groupCount() == 1)
            {
                mCode = codeMatcher.group(1);
                mUsername = usernameMatcher.group(1);
                LogHelper.d(TAG, "Code: "+mCode+", Username: "+mUsername);
            }
            else
            {
                throw new IllegalArgumentException("Notification texts do not match the regex");
            }

            startDiscoveryThread();
        }
        else
        {
            throw new IllegalArgumentException(
                    "You must provide both extras: " + EXTRA_NOTIFICATION_TITLE + " and " + EXTRA_NOTIFICATION_BODY);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        LogHelper.d(TAG, "Service has been destroyed");
    }

    @Override
    public void onServerFound(LazyServer server)
    {
        Toast.makeText(NotificationReceiverService.this, "Server found", Toast.LENGTH_LONG).show();
        ServerSendingThread thread = new ServerSendingThread(server, mCode, mUsername);
        thread.start();
    }

    @Override
    public void onSocketClosed()
    {

    }

    @Override
    public void onSocketOpened()
    {

    }

    private void startDiscoveryThread()
    {
        ServerDiscoveryThread discoveryThread = new ServerDiscoveryThread();
        discoveryThread.setServerDiscoveryListener(this);
        discoveryThread.start();
    }
}
