package com.roundel.lazysteam.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roundel.lazysteam.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ServerViewActivityFragment extends Fragment
{


    public ServerViewActivityFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_server_view, container, false);
    }
}
