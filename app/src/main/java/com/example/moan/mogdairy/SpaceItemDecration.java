package com.example.moan.mogdairy;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecration extends RecyclerView.ItemDecoration {
    public int space;

    public SpaceItemDecration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        outRect.bottom = space;
    }
}
