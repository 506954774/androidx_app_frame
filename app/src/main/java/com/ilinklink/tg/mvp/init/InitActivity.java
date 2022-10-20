package com.ilinklink.tg.mvp.init;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.ToastUtils;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.exam.ExamContract;
import com.ilinklink.tg.mvp.exam.ExamPresenterImpl;
import com.ilinklink.tg.mvp.initfacefeatrue.InitFaceFeatrueActivity;
import com.ilinklink.tg.mvp.selectsubject.SelectSubjectActivity;
import com.qdong.communal.library.util.ToastUtil;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityInitBinding;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;

/**
 * InitActivity
 * 初始化界面
 * 逻辑:
 *  *  *
 *
 *  获取全部考试信息,以及每个考试的考生集合  考试和考生,1对多
 *
 *
 *  子线程获取考生集合,下载到磁盘(耗时操作,会根据接口数据和sqlite的数据做对比,有更新则下载),如果此次有下载,则跳转到人脸特征值加载界面InitFaceFeatrueActivity
 *
 * Created By:Chuck
 * Des:
 * on 2018/12/6 15:43
 */
public class InitActivity extends BaseMvpActivity<ActivityInitBinding> implements View.OnClickListener, ExamContract
.ExamView{

    private ExamContract.ExamPresenter mPresenter;


    //给父类存起来,父类destory时遍历释放资源
    @Override
    public ArrayList<BasePresenter> initPresenters() {
        ArrayList<BasePresenter> list = new ArrayList<>();
        mPresenter = new ExamPresenterImpl(this);
        list.add((BasePresenter) mPresenter);
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

        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //设置布局,里面有埋点按钮,详细看布局文件
        setContentView(R.layout.activity_init);


        initView();
        initData();


    }

    private void initData() {

        mLoadingView.showLoading(getString(R.string.exam_info_loading));


        //获取数据,存入SQLite
        mPresenter.getExamInfoAndSave();

    }

    //重置界面
    private void initView() {
        mViewBind.setClick(this);
    }




    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_setting:
                ToastUtils.showShort("该功能尚未开启");

                break;
            case R.id.tv_go_2_select_subject:
                startActivity(new Intent( this, SelectSubjectActivity.class));
                //startActivity(new Intent( this, BaseFaceRGBPaymentActivity.class));
                //startActivity(new Intent( this, FaceRecognizeActivity.class));
                break;
            case R.id.tv_go_2_init_face_featrue:
                startActivity(new Intent( this, InitFaceFeatrueActivity.class));
                break;


        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void getExamInfoAndSaveOnCompleted(boolean successed, String message) {
        mLoadingView.dismiss();
        if(!TextUtils.isEmpty(message)){
            ToastUtil.showCustomMessageShort(this,message);
        }
        if(successed){
            ToastUtil.showCustomMessageShort(this,getString(R.string.exam_info_init_finished));

            /* String examId="19db45e7-fd0e-3c50-bc44-27cc7871504f";
            String studentId="6d25fac5-b71c-3eb7-8654-f55c3b87bd49";
            StudentExam studentExam = DBHelper.getInstance(this).getStudentExam(examId, studentId);

            LogUtil.e(mPresenter.getClass().getSimpleName(),"getStudentExam, :"+studentExam);*/



            mLoadingView.showLoading(getString(R.string.exam_info_student_image_loading));


            //下载图片到sd卡
            mPresenter.downLoadFaceImages();

        }
        else {
           // ToastUtil.showCustomMessage(this,getString(R.string.exam_info_no_data));
            finish();
        }
    }

    @Override
    public void compareFaceFeatrueOnCompleted(boolean isNeedUpdate, String message) {

    }

    @Override
    public void downLoadFaceFeatrueOnCompleted(boolean successed,String message,int count) {
        mLoadingView.dismiss();
        if(!TextUtils.isEmpty(message)){
            ToastUtil.showCustomMessageShort(this,message);
        }
        if(successed){
            if(count>0){

            }
        }
        else {

        }
         //前往百度人脸特征值录入的界面,特征值需要读取到内存里,否则人脸识别会失败
        startActivity(new Intent( this, InitFaceFeatrueActivity.class));
    }

    @Override
    public void uploadExamResultOnCompleted(boolean successed, String message) {

    }

    @Override
    public void initExamView() {

    }
}
