package com.roundel.lazysteam.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.roundel.lazysteam.R;

/*
 * Created by Krzysiek on 31/05/2017.
 */

public class MaxDimenRecyclerView extends RecyclerView
{
    @Px private int mMaxHeight;
    @Px private int mMaxWidth;

    public MaxDimenRecyclerView(Context context)
    {
        this(context, null);
    }

    public MaxDimenRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MaxDimenRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MaxDimenRecyclerView, defStyle, 0);
        try
        {
            mMaxHeight = array.getDimensionPixelSize(R.styleable.MaxDimenRecyclerView_maxHeight, -1);
            mMaxWidth = array.getDimensionPixelSize(R.styleable.MaxDimenRecyclerView_maxWidth, -1);
        }
        finally
        {
            array.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec)
    {
        if(mMaxHeight != -1)
        {
            heightSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }
        if(mMaxWidth != -1)
        {
            widthSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthSpec, heightSpec);
    }
}
