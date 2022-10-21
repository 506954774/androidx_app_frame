package com.ilinklink.tg.mvp.history;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.exam.BasePoseActivity2;
import com.ilinklink.tg.mvp.exam.ExamActivity2;
import com.ilinklink.tg.mvp.selectsubject.ExamAdapter;
import com.ilinklink.tg.mvp.selectsubject.SelectSubjectActivity;
import com.ilinklink.tg.mvp.selectsubject.SpaceItemDecoration;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseQuickAdapter2;
import com.qdong.communal.library.util.DensityUtil;
import com.spc.pose.demo.BR;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityExamListHistoryBinding;
import com.spc.pose.demo.databinding.ActivitySelectSubjectBinding;

import java.util.ArrayList;

/**
 * 考核的历史列表
 */
public class ExamListHistoryActivity extends BaseMvpActivity<ActivityExamListHistoryBinding> implements View.OnClickListener {
    @Override
    protected boolean isRelativeStatusBar() {
        return true;
    }

    @Override
    protected int getStatusBarColor() {
        return Color.parseColor("#00000000");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setIsTitleBar(false);

        super.onCreate(savedInstanceState);

        //设置布局,里面有埋点按钮,详细看布局文件
        setContentView(R.layout.activity_exam_list_history);

        initView();
        initData();


    }

    private void initData() {
    }

    private void initView() {
        mViewBind.setClick(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mViewBind.recyclerExamsList.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
        }
    }

    @Override
    public ArrayList<BasePresenter> initPresenters() {
        ArrayList<BasePresenter> list = new ArrayList<>();
        return list;
    }
}
