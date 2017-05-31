package com.roundel.lazysteam.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roundel.lazysteam.LazyServer;
import com.roundel.lazysteam.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Krzysiek on 27/05/2017.
 */

public class LazyServerAdapter extends RecyclerView.Adapter<LazyServerAdapter.ViewHolder>
{
    private ArrayList<LazyServer> mServerList;
    /**
     * When supplying a custom layout res, make sure
     * that the TextViews have ids:
     * {@link R.id#server_name}, {@link R.id#server_host}
     * and the ImageView is a:
     * {@link R.id#server_details_icon}
     */
    @LayoutRes private int mLayoutRes;
    private RecyclerView mRecyclerView;
    private ServerActionsListener mActionsListener;

    private final View.OnClickListener mOnItemClickListener = v ->
    {
        if(mActionsListener != null && mRecyclerView != null)
        {
            int position = mRecyclerView.getChildLayoutPosition(v);
            mActionsListener.onItemClick(position);
        }
    };

    private final View.OnClickListener mOnDetailsClickListener = v ->
    {
        if(mActionsListener != null && mRecyclerView != null)
        {
            int position = mRecyclerView.getChildLayoutPosition((View) v.getParent());
            mActionsListener.onDetailsClick(position);
        }
    };

    public LazyServerAdapter(ArrayList<LazyServer> servers, @LayoutRes int layoutRes)
    {
        this.mServerList = servers;
        this.mLayoutRes = layoutRes;
    }

    public LazyServerAdapter(ArrayList<LazyServer> servers, boolean withDetails)
    {
        this.mServerList = servers;
        this.mLayoutRes = withDetails ?
                R.layout.list_server_details_icon : R.layout.list_server_simple;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutRes, parent, false);
        View detailsIcon = view.findViewById(R.id.server_details_icon);

        view.setOnClickListener(mOnItemClickListener);
        if(detailsIcon != null)
            detailsIcon.setOnClickListener(mOnDetailsClickListener);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final LazyServer server = mServerList.get(position);
        final View itemView = holder.itemView;
        ImageView icon = (ImageView) itemView.findViewById(R.id.server_icon);
        TextView name = (TextView) itemView.findViewById(R.id.server_name);
        TextView host = (TextView) itemView.findViewById(R.id.server_host);

        //TODO: Add support for custom icons
        /*if(icon != null)
            icon.setImageDrawable();*/
        if(name != null)
            name.setText(server.getName());
        if(host != null)
            host.setText(String.format(Locale.getDefault(),
                    "%s:%d", server.getHost(), server.getPort()
            ));
        if(icon != null)
        {
            /*if(server.getIcon() != null)
                icon.setImageDrawable(server.getIcon());
            else*/
            icon.setImageDrawable(mRecyclerView.getContext().getDrawable(R.drawable.ic_desktop_windows_black_24dp));
        }

    }

    @Override
    public int getItemCount()
    {
        return mServerList.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView)
    {
        super.onDetachedFromRecyclerView(recyclerView);
        this.mRecyclerView = null;
    }

    public void setActionsListener(ServerActionsListener actionsListener)
    {
        mActionsListener = actionsListener;
    }

    public interface ServerActionsListener
    {
        void onDetailsClick(int position);

        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
