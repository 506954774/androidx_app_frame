package com.ilinklink.tg.mvp.history;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.selectsubject.SpaceItemDecoration;
import com.qdong.communal.library.util.DensityUtil;
import com.spc.pose.demo.BR;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityExamListHistoryBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 考核的历史列表
 */
public class ExamListHistoryActivity extends BaseMvpActivity<ActivityExamListHistoryBinding> implements View.OnClickListener {
    ExamListHistoryAdapter mAdapter;
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
        List<ExamInfo> examInfoList = DBHelper.getInstance(this).getExamInfoList();
        Log.e("TAG","长度是"+examInfoList.size());
        for (ExamInfo examInfo : examInfoList) {
            Log.e("TAG",examInfo+"");
        }
        ExamInfo examInfo = new ExamInfo();
        examInfo.setName("考核1");
        examInfo.setStartTimeMs(System.currentTimeMillis());
        examInfoList.add(examInfo);

        ExamInfo examInfo2 = new ExamInfo();
        examInfo2.setName("考核2");
        examInfo2.setStartTimeMs(System.currentTimeMillis()+10000);
        examInfoList.add(examInfo2);
        mAdapter = new ExamListHistoryAdapter(examInfoList, R.layout.item_exam_list_history, BR.ExamInfo);
        mViewBind.recyclerExamsList.setAdapter(mAdapter);
    }

    private void initView() {
        mViewBind.setClick(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mViewBind.recyclerExamsList.setLayoutManager(layoutManager);
        SpaceItemDecoration itemDecoration=new SpaceItemDecoration(DensityUtil.dp2px(this,30),2);
        mViewBind.recyclerExamsList.addItemDecoration(itemDecoration);

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
