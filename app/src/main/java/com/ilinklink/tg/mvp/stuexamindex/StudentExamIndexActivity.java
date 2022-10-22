package com.ilinklink.tg.mvp.stuexamindex;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.StudentExamRecord;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.dto.QueryStudentExamRecordDto;
import com.ilinklink.tg.entity.SubjectExamResult;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.exam.BasePoseActivity2;
import com.ilinklink.tg.mvp.exam.ExamActivity2;
import com.ilinklink.tg.mvp.facerecognize.FaceRecognizeResult;
import com.ilinklink.tg.utils.CollectionUtils;
import com.ilinklink.tg.utils.Json;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseQuickAdapter2;
import com.qdong.communal.library.util.DensityUtil;
import com.spc.pose.demo.BR;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityStudentExamIndexBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * SelectSubjectActivity
 * 考生考试首页，科目选择
 * Created By:Chuck
 * Des:
 * on 2018/12/6 15:43
 */
public class StudentExamIndexActivity extends BaseMvpActivity<ActivityStudentExamIndexBinding> implements View.OnClickListener {

    public static final String TAG="StudentExamIndexActivity";

    private ExamSubjectAdapter mExamAdapter;

    private StudentInfo mStudentInfo;

    private ExamRecord mExamRecord;
    private int GO_TO_EXAM=110;

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
        setContentView(R.layout.activity_student_exam_index);

        initData();
        initView();


    }

    private void initData() {

        mStudentInfo=DBHelper.getInstance(this).getStudentInfo(FaceRecognizeResult.getInstance().getStudentId());



        /**
         * 此次考试的数据
         */
        List<ExamRecord> examRecordList = DBHelper.getInstance(this).getExamRecordList();
        Log.i(TAG,"examRecordList:"+examRecordList);

        if(CollectionUtils.isNullOrEmpty(examRecordList)){
            ExamRecord examRecord=new ExamRecord();
            examRecord.setExamRecordId("1");
            examRecord.setExamUUID("1");
            examRecord.setName("十月份大比武");
            examRecord.setExamTime("2022-10-21 17:00");
            DBHelper.getInstance(this).saveExamRecord(examRecord);

            mExamRecord=examRecord;
        }
        else {
            mExamRecord=examRecordList.get(examRecordList.size()-1);
        }


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        mViewBind.recyclerExams.setLayoutManager(layoutManager);
        SpaceItemDecoration itemDecoration=new SpaceItemDecoration(DensityUtil.dp2px(this,30),2);
        mViewBind.recyclerExams.addItemDecoration(itemDecoration);


    }

    //重置界面
    private void initView() {
        mViewBind.setClick(this);

        mViewBind.tvEnterExam.setText(getString(R.string.exit_exam));

        if(mStudentInfo!=null){
            mViewBind.ivStuName.setText("考生："+mStudentInfo.getName());
        }

        resetAdapter();

    }


    private void resetAdapter(){


        List<ExamInfo> examInfoList=new ArrayList<>();

        if(mStudentInfo!=null){

            if(mExamRecord!=null){

                QueryStudentExamRecordDto dto=new QueryStudentExamRecordDto();
                dto.setExamRecordId("1");
                dto.setStudentUUID(mStudentInfo.getStudentUUID());
                List<StudentExamRecord> studentRecord = DBHelper.getInstance(this).getStudentRecord(dto);

                if(!CollectionUtils.isNullOrEmpty(studentRecord)){

                    StudentExamRecord studentExamRecord = studentRecord.get(studentRecord.size()-1);

                    if(studentExamRecord!=null){
                        SubjectExamResult result= Json.fromJson(studentExamRecord.getSubResultJson(),SubjectExamResult.class);
                        if(result!=null){
                            /**
                             * 写死科目
                             *
                             */
                            String [] names={"单杠引体向上","双杠臂屈伸","俯卧撑","仰卧起坐"};
                            for(String name :names){
                                ExamInfo s1=new ExamInfo();
                                s1.setName(name);

                                //用desc存分数
                                if(name.equals("单杠引体向上")){
                                    s1.setDesc(result.getPullUpsScore());
                                }
                                //用desc存分数
                                if(name.equals("双杠臂屈伸")){
                                    s1.setDesc(result.getParallelBarsScore());
                                }
                                //用desc存分数
                                if(name.equals("俯卧撑")){
                                    s1.setDesc(result.getPushUpsScore());
                                }
                                //用desc存分数
                                if(name.equals("仰卧起坐")){
                                    s1.setDesc(result.getSithUpsScore());
                                }

                                examInfoList.add(s1);
                            }
                        }
                        else {
                            /**
                             * 写死科目
                             *
                             */
                            initList(examInfoList);

                        }
                    }
                    else {
                        /**
                         * 写死科目
                         *
                         */
                        initList(examInfoList);

                    }
                }

                else {
                    /**
                     * 写死科目
                     *
                     */
                    initList(examInfoList);

                }


            }
        }
        else {
            /**
             * 写死科目
             *
             */
            initList(examInfoList);

        }






        mExamAdapter = new ExamSubjectAdapter(examInfoList, R.layout.item_exam_sub_item, BR.ExamSubIndex);

        mExamAdapter.setOnItemClickListener(new BaseQuickAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter2 adapter, View view, int position) {
                //ToastUtils.showShort(examInfoList.get(position).toString());

                ExamInfo mExam = examInfoList.get(position);
                if(mExam!=null){

                    Intent intent=new Intent(StudentExamIndexActivity.this, ExamActivity2.class);
                    //EXAM_DATA

                    StudentExamRecord studentExamRecord=new StudentExamRecord();

                    studentExamRecord.setStudentExamRecordId(UUID.randomUUID().toString());
                    studentExamRecord.setExamRecordId(mExamRecord.getExamRecordId());

                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    studentExamRecord.setExamTime(simpleDateFormat.format(new Date()));

                    studentExamRecord.setStudentName(mStudentInfo.getName());
                    studentExamRecord.setStudentUUID(mStudentInfo.getStudentUUID());
                    // TODO: 2022/10/21 此处应该加字段，最好不用用desc作为学生编号
                    studentExamRecord.setReservedColumn(mStudentInfo.getDesc());

                    intent.putExtra(BasePoseActivity2.EXAM_DATA,Json.toJson(studentExamRecord));


                    if("单杠引体向上".equals(mExam.getName())){
                        intent.putExtra(BasePoseActivity2.EXAM_NAME,mExam.getName());
                        startActivityForResult(intent,GO_TO_EXAM);
                    }
                    else if("双杠臂屈伸".equals(mExam.getName())){
                        intent.putExtra(BasePoseActivity2.EXAM_NAME,mExam.getName());
                        startActivityForResult(intent,GO_TO_EXAM);
                    }
                    else if("俯卧撑".equals(mExam.getName())){
                        intent.putExtra(BasePoseActivity2.EXAM_NAME,mExam.getName());
                        startActivityForResult(intent,GO_TO_EXAM);
                    }
                    else if("仰卧起坐".equals(mExam.getName())){
                        intent.putExtra(BasePoseActivity2.EXAM_NAME,mExam.getName());
                        startActivityForResult(intent,GO_TO_EXAM);
                    }
                    else  {
                        ToastUtils.showShort(getString(R.string.exam_not_supported));
                    }
                }


            }
        });

        mViewBind.recyclerExams.setAdapter(mExamAdapter);
    }


    private void initList(List<ExamInfo> examInfoList){
        examInfoList.clear();
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
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.iv_back:
                finish();
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
                //startActivity(new Intent(this, ShuanggangActivity.class));
                finish();

                break;


        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        resetAdapter();
    }

}
