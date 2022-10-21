package com.ilinklink.tg.mvp.selectsubject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.exam.BasePoseActivity2;
import com.ilinklink.tg.mvp.exam.ExamActivity2;

import com.ilinklink.tg.mvp.stuexamindex.StudentExamIndexActivity;

import com.ilinklink.tg.mvp.history.ExamListHistoryActivity;

import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseQuickAdapter2;
import com.qdong.communal.library.util.DensityUtil;
import com.spc.pose.demo.BR;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivitySelectSubjectBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * SelectSubjectActivity
 * 选择科目界面
 * Created By:Chuck
 * Des:
 * on 2018/12/6 15:43
 */
public class SelectSubjectActivity extends BaseMvpActivity<ActivitySelectSubjectBinding> implements View.OnClickListener {


    private ExamAdapter mExamAdapter;

    //给父类存起来,父类destory时遍历释放资源
    @Override
    public ArrayList<BasePresenter> initPresenters() {
        ArrayList<BasePresenter> list = new ArrayList<>();
        return list;
    }


    /**
     * @param :[]
     * @return type:boolean
     * @method name:isRelativeStatusBar
     * @des:设置状态栏类型,返回true,则沉浸,false则线性追加
     * @date 创建时间:2018/12/4
     * @author Chuck
     **/
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
        setContentView(R.layout.activity_select_subject);

        initView();
        initData();


    }

    private void initData() {

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        mViewBind.recyclerExams.setLayoutManager(layoutManager);
        SpaceItemDecoration itemDecoration=new SpaceItemDecoration(DensityUtil.dp2px(this,30),2);
        mViewBind.recyclerExams.addItemDecoration(itemDecoration);

        List<ExamInfo> examInfoList = DBHelper.getInstance(this).getExamInfoList();

        /**
         * 写死科目
         *
         */
        String [] names={"单杠引体向上","双杠臂屈伸","俯卧撑","仰卧起坐"};
        for(String name :names){
            ExamInfo s1=new ExamInfo();
            s1.setName(name);
            examInfoList.add(s1);
        }



        mExamAdapter = new ExamAdapter(examInfoList, R.layout.item_ztsb_course, BR.ExamInfo);

        mExamAdapter.setOnItemClickListener(new BaseQuickAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter2 adapter, View view, int position) {
                //ToastUtils.showShort(examInfoList.get(position).toString());

                ExamInfo mExam = examInfoList.get(position);
                if(mExam!=null){

                    Intent intent=new Intent(SelectSubjectActivity.this, ExamActivity2.class);

                    if("单杠引体向上".equals(mExam.getName())){
                        intent.putExtra(BasePoseActivity2.EXAM_NAME,mExam.getName());
                        startActivity(intent);
                    }
                    else if("双杠臂屈伸".equals(mExam.getName())){
                        intent.putExtra(BasePoseActivity2.EXAM_NAME,mExam.getName());
                        startActivity(intent);
                    }
                    else if("俯卧撑".equals(mExam.getName())){
                        intent.putExtra(BasePoseActivity2.EXAM_NAME,mExam.getName());
                        startActivity(intent);
                    }
                    else if("仰卧起坐".equals(mExam.getName())){
                        intent.putExtra(BasePoseActivity2.EXAM_NAME,mExam.getName());
                        startActivity(intent);
                    }
                    else  {
                        ToastUtils.showShort(getString(R.string.exam_not_supported));
                    }
                }


            }
        });

        mViewBind.recyclerExams.setAdapter(mExamAdapter);
    }

    //重置界面
    private void initView() {
        mViewBind.setClick(this);
    }




    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.btn_history:
                // 跳转考试历史记录
                startActivity(new Intent(this, ExamListHistoryActivity.class));
                break;

                //俯卧撑
            case R.id.tv_fuwocheng:
                //ToastUtils.showShort("tv_fuwocheng");

                //startActivity(new Intent(this, FuwochengActivity.class));

                break;
                //引体向上
            case R.id.tv_yintixiangshang:
                //ToastUtils.showShort("tv_yintixiangshang");

                //startActivity(new Intent(this, YintixiangshangActivity.class));

                break;
                //双杠
            case R.id.tv_shuanggangbiqushen:
                //ToastUtils.showShort("tv_shuanggangbiqushen");
                //startActivity(new Intent(this, ShuanggangActivity.class));


                break;
            case R.id.tv_enter_exam:
                //ToastUtils.showShort("tv_enter_exam");
                startActivity(new Intent(this, StudentExamIndexActivity.class));


                break;


        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
