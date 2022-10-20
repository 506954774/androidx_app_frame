package com.ilinklink.tg.mvp.selectsubject;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @des:  间隔设置
 * @date 创建时间:2022/5/22
 * @author Chuck
 **/

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int itemCount;

    public SpaceItemDecoration(int space,int itemCount) {
        this.space = space;
        this.itemCount = itemCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = space;
        outRect.bottom = space;
        //因为每行都只有itemCount个，因此第一个都是itemCount的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) %itemCount==0) {
            outRect.left = 0;
        }
    }

}