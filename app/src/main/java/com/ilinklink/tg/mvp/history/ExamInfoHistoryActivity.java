package com.ilinklink.tg.mvp.history;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.StudentExamRecord;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.selectsubject.SpaceItemDecoration;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseQuickAdapter2;
import com.qdong.communal.library.util.DensityUtil;
import com.spc.pose.demo.BR;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityExamInfoHistoryBinding;
import com.spc.pose.demo.databinding.ActivityExamListHistoryBinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 考核的历史列表
 */
public class ExamInfoHistoryActivity extends BaseMvpActivity<ActivityExamInfoHistoryBinding> implements View.OnClickListener {
    public static final String KEY_EXAM_ID = "examID";
    private ExamInfoHistoryAdapter mAdapter;
    private String examID;


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
        setContentView(R.layout.activity_exam_info_history);

        examID = getIntent().getStringExtra(KEY_EXAM_ID);
        initView();
        initData();


    }

    private void initData() {
        List<StudentExamRecord> examInfoList = new LinkedList<>();
//        Log.e("TAG","长度是"+examInfoList.size());
//        for (ExamRecord examInfo : examInfoList) {
//            Log.e("TAG",examInfo+"");
//        }
        StudentExamRecord studentExamRecord = new StudentExamRecord();
        StudentExamRecord studentExamRecor2 = new StudentExamRecord();

        studentExamRecord.setStudentName("名字1");
        studentExamRecor2.setStudentName("名字2");
        studentExamRecord.setExamTime("2022-2-2-22 23:22:22");
        studentExamRecor2.setExamTime("2022-4-2-22 23:22:22");
        studentExamRecord.setReservedColumn("dgasjdga1@!#!@#12");
        studentExamRecor2.setReservedColumn("2342423423@!#!@#12");

        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecord);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);
        examInfoList.add(studentExamRecor2);


        mAdapter = new ExamInfoHistoryAdapter(examInfoList, R.layout.item_exam_info_history, BR.studentExamRecord);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter2.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter2 adapter, View view, int position) {
                showToast("  " + position);
            }
        });
        mViewBind.recyclerExamsList.setAdapter(mAdapter);
    }

    private void initView() {

        String title = getIntent().getStringExtra("title");
        mViewBind.title.setText(title);
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
