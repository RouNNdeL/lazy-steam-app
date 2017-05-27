package com.roundel.lazysteam.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.roundel.lazysteam.LazyServer;
import com.roundel.lazysteam.R;
import com.roundel.lazysteam.ui.fragment.ServerScanFragment;

public class ServerSetupActivity extends AppCompatActivity implements ServerScanFragment.OnFragmentInteractionListener
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
        //TODO: Connect to the selected server using the ServerSetupThread
    }
}
