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

import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.dialog.DialogUploadSuccess;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.selectsubject.SpaceItemDecoration;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseQuickAdapter2;
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
        List<ExamRecord> examInfoList = DBHelper.getInstance(this).getExamRecordList();
        Log.e("TAG", "长度是" + examInfoList.size());
        for (ExamRecord examInfo : examInfoList) {
            Log.e("TAG", examInfo + "");
        }
        mAdapter = new ExamListHistoryAdapter(examInfoList, R.layout.item_exam_list_history, BR.ExamInfo);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter2 adapter, View view, int position) {
                Intent intent = new Intent(ExamListHistoryActivity.this, ExamInfoHistoryActivity.class);
                intent.putExtra("title", examInfoList.get(position).getName());
                intent.putExtra(ExamInfoHistoryActivity.KEY_EXAM_ID, examInfoList.get(position).getExamRecordId());
                ExamListHistoryActivity.this.startActivity(intent);
            }
        });
        mViewBind.recyclerExamsList.setAdapter(mAdapter);

    }

    private void initView() {
        mViewBind.setClick(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mViewBind.recyclerExamsList.setLayoutManager(layoutManager);
        mViewBind.recyclerExamsList.addItemDecoration(new SpaceItemDecoration(20, 1));

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
        }
        if (view.getId() == R.id.btn_upload) {
            // todo  上传数据
            DialogUploadSuccess dialogUploadSuccess = new DialogUploadSuccess(this);
            dialogUploadSuccess.show();
        }
    }

    @Override
    public ArrayList<BasePresenter> initPresenters() {
        ArrayList<BasePresenter> list = new ArrayList<>();
        return list;
    }
}
