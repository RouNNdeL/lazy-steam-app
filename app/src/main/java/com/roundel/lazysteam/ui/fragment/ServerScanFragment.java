package com.roundel.lazysteam.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.roundel.lazysteam.LazyServer;
import com.roundel.lazysteam.R;
import com.roundel.lazysteam.adapter.LazyServerAdapter;
import com.roundel.lazysteam.net.ServerDiscoveryThread;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Transition;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServerScanFragment extends Fragment implements ServerDiscoveryThread.ServerDiscoveryListener, LazyServerAdapter.ServerActionsListener
{

    private static final int MAX_REFRESH_COUNT = 5;
    private final DialogInterface.OnClickListener mOnManualConnectionButtonClickListener = (dialog, which) ->
    {
        //TODO: Add a manual connection dialog
    };
    @BindView(R.id.setup_scan_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.setup_scan_progress) ProgressBar mProgressBar;
    @BindView(R.id.setup_scan_progress_parent) FrameLayout mProgressContainer;
    @BindView(R.id.setup_scan_card) CardView mCardView;
    @BindView(R.id.setup_scan_help) LinearLayout mHelpLayout;
    private OnFragmentInteractionListener mListener;
    private LazyServerAdapter mAdapter;
    private ArrayList<LazyServer> mServerList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private ServerDiscoveryThread mDiscoveryThread;
    private int mRefreshCount;
    private final DialogInterface.OnClickListener mOnTryAgainButtonClickListener = (dialog, which) ->
    {
        dialog.dismiss();
        forceRefresh();
    };
    private final View.OnClickListener mOnHelpClickedListener = v -> showHelpDialog();

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
        ConstraintLayout root = (ConstraintLayout) inflater.inflate(R.layout.setup_scan_new, container, false);

        ButterKnife.bind(this, root);

        mAdapter = new LazyServerAdapter(mServerList, false);
        mAdapter.setActionsListener(this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mHelpLayout.setOnClickListener(mOnHelpClickedListener);

        //mSwipeRefreshLayout.setOnRefreshListener(this);

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
    public void onServerFound(LazyServer server)
    {
        if(getActivity() != null)
            getActivity().runOnUiThread(() ->
                    addServer(server, true));

    }

    @Override
    public void onSocketClosed()
    {
        new Handler(Looper.getMainLooper()).postDelayed(this::refresh, 500);
    }

    @Override
    public void onSocketOpened()
    {

    }

    @Override
    public void onDetailsClick(int position)
    {
        //There is no details icon in the setup layout
    }

    @Override
    public void onItemClick(int position)
    {
        if(mListener != null)
            mListener.onServerSelected(mServerList.get(position));
    }

    /**
     * Adds a server to the {@link #mServerList list} if it doesn't already contain it. Make sure to
     * call this method on the UI Thread, as it calls {@link RecyclerView.Adapter#notifyDataSetChanged()
     * notifyDataSetChanged()} which will throw a {@link android.view.ViewRootImpl.CalledFromWrongThreadException
     * CalledFromWrongThreadException} if it isn't called from the UI thread.
     *
     * @param server  a server to be added
     * @param animate whether to run a transition when adding a element
     */
    @SuppressWarnings("JavadocReference")
    private void addServer(LazyServer server, boolean animate)
    {
        if(animate)
        {
            TransitionManager.beginDelayedTransition(mRecyclerView);
        }
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
    }

    private void showHelpDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Can't see your PC?")
                .setMessage("Please make sure you are connected to the same network" +
                        " as the PC you are trying to connect to," +
                        " and that the PC app is up and running." +
                        " You can either manually enter the port and ip address displayed by" +
                        " the PC app, or try the automatic detection again.")
                .setPositiveButton("Try again", mOnTryAgainButtonClickListener)
                .setNeutralButton("Manual connection", mOnManualConnectionButtonClickListener);
        builder.create().show();
    }

    private void forceRefresh()
    {
        mRefreshCount = 0;
        refresh();
    }

    private void refresh()
    {
        if(mRefreshCount < MAX_REFRESH_COUNT)
        {
            if(mDiscoveryThread == null || !mDiscoveryThread.isAlive())
            {
                mDiscoveryThread = new ServerDiscoveryThread();
                mDiscoveryThread.setServerDiscoveryListener(this);
                mDiscoveryThread.start();

                mRefreshCount++;

                showProgressBar(true);
            }
        }
        else
        {
            showProgressBar(false);
        }
    }

    private void showProgressBar(boolean show)
    {
        showProgressBar(show, true);
    }

    private void showProgressBar(boolean show, boolean animate)
    {
        if(animate)
        {
            Transition transition = new Fade();
            transition.setDuration(150);
            com.transitionseverywhere.TransitionManager.
                    beginDelayedTransition(mCardView);
        }
        if(show && mProgressContainer.getVisibility() == View.INVISIBLE)
        {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
        else if(!show && mProgressContainer.getVisibility() == View.VISIBLE)
        {
            mProgressContainer.setVisibility(View.INVISIBLE);
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
