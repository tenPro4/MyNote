package com.example.mynote.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.example.mynote.App;

public class FixedHeightRecyclerView extends RecyclerView {

    public FixedHeightRecyclerView(@NonNull Context context) {
        super(context);
    }

    public FixedHeightRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedHeightRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = MeasureSpec.makeMeasureSpec(App.DEVICE_HEIGHT/2,MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec,heightSpec);
    }
}
