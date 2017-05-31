package com.roundel.lazysteam.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.roundel.lazysteam.LazyServer;
import com.roundel.lazysteam.R;
import com.roundel.lazysteam.net.ServerSetupThread;
import com.roundel.lazysteam.ui.fragment.ServerScanFragment;

public class ServerSetupActivity extends AppCompatActivity implements ServerScanFragment.OnFragmentInteractionListener, ServerSetupThread.SetupProgress
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setup);

        ServerScanFragment serverScanFragment = ServerScanFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.setup_container, serverScanFragment);
        transaction.commit();
    }


    @Override
    public void onServerSelected(LazyServer server)
    {
        ServerSetupThread setupThread = new ServerSetupThread(server, this);
        setupThread.start();
    }

    @Override
    public void onStart(LazyServer server)
    {
        runOnUiThread(() ->
                Toast.makeText(this, "onStart: " + server.toString(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onConnect(LazyServer server)
    {
        runOnUiThread(() ->
                Toast.makeText(this, "onConnect: " + server.toString(), Toast.LENGTH_LONG).show());
    }

    @Override
    public int onCodeRequest(LazyServer server)
    {
        runOnUiThread(() ->
                Toast.makeText(this, "onCodeRequest: " + server.toString(), Toast.LENGTH_LONG).show());
        return 1234;
    }

    @Override
    public void onSuccess(LazyServer external)
    {
        runOnUiThread(() ->
                Toast.makeText(this, "onSuccess: " + external.toString(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onFailure(Exception e)
    {
        runOnUiThread(() ->
                Toast.makeText(this, "onFailure: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
