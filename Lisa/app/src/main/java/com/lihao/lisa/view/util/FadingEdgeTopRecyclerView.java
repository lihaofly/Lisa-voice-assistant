package com.lihao.lisa.view.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class FadingEdgeTopRecyclerView extends RecyclerView {
    public FadingEdgeTopRecyclerView(Context context) {
        super(context);
    }

    public FadingEdgeTopRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadingEdgeTopRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        return super.getTopFadingEdgeStrength();
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 0f;
    }
}
