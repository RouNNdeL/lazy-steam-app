package com.roundel.lazysteam.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roundel.lazysteam.LazyServer;
import com.roundel.lazysteam.R;
import com.roundel.lazysteam.adapter.LazyServerAdapter;
import com.roundel.lazysteam.net.ServerDiscoveryThread;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServerScanFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ServerDiscoveryThread.ServerDiscoveryListener, LazyServerAdapter.ServerActionsListener
{

    @BindView(R.id.setup_scan_swiperefresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.setup_scan_recycler) RecyclerView mRecyclerView;
    private OnFragmentInteractionListener mListener;
    private LazyServerAdapter mAdapter;
    private ArrayList<LazyServer> mServerList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private ServerDiscoveryThread mDiscoveryThread;

    public ServerScanFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ServerScanFragment.
     */
    public static ServerScanFragment newInstance()
    {
        ServerScanFragment fragment = new ServerScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ConstraintLayout root = (ConstraintLayout) inflater.inflate(R.layout.setup_scan, container, false);

        ButterKnife.bind(this, root);

        mAdapter = new LazyServerAdapter(mServerList, false);
        mAdapter.setActionsListener(this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        refresh();

        return root;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh()
    {
        refresh();
    }

    @Override
    public void onServerFound(LazyServer server)
    {
        if(getActivity() != null)
            getActivity().runOnUiThread(() ->
            {
                TransitionManager.beginDelayedTransition(mRecyclerView);
                boolean contains = false;
                for(int i = 0; i < mServerList.size(); i++)
                {
                    final LazyServer lazyServer = mServerList.get(i);
                    if(!server.equals(lazyServer))
                    {
                        if(Objects.equals(server.getHost(), lazyServer.getHost()))
                        {
                            mServerList.remove(i);
                            mServerList.add(i, server);
                            mAdapter.notifyItemChanged(i);
                            contains = true;
                            break;
                        }
                        else
                        {
                            contains = false;
                            break;
                        }
                    }
                    else
                    {
                        contains = true;
                    }
                }
                if(!contains)
                {
                    mServerList.add(server);
                    mAdapter.notifyItemInserted(mServerList.size() - 1);
                }
            });
    }

    @Override
    public void onSocketClosed()
    {
        if(getActivity() != null)
            getActivity().runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public void onSocketOpened()
    {
        if(getActivity() != null)
            getActivity().runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(true));
    }

    @Override
    public void onDetailsClick(int position)
    {
        //There is no details icon in the setup icon
    }

    @Override
    public void onItemClick(int position)
    {
        if(mListener != null)
            mListener.onServerSelected(mServerList.get(position));
    }

    private void refresh()
    {
        if(mDiscoveryThread == null || !mDiscoveryThread.isAlive())
        {
            mDiscoveryThread = new ServerDiscoveryThread();
            mDiscoveryThread.setServerDiscoveryListener(this);
            mDiscoveryThread.setDiscoveryTimeout(2500);
            mDiscoveryThread.start();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener
    {
        void onServerSelected(LazyServer server);
    }
}
