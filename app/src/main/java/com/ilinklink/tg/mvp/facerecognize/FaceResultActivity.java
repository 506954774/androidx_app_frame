package com.ilinklink.tg.mvp.facerecognize;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.exam.BasePoseActivity2;
import com.ilinklink.tg.mvp.exam.ExamActivity2;
import com.ilinklink.tg.mvp.stuexamindex.ExamSubjectAdapter;
import com.ilinklink.tg.mvp.stuexamindex.SpaceItemDecoration;
import com.ilinklink.tg.mvp.stuexamindex.StudentExamIndexActivity;
import com.ilinklink.tg.utils.CollectionUtils;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseQuickAdapter2;
import com.qdong.communal.library.util.BitmapUtil;
import com.qdong.communal.library.util.Constants;
import com.qdong.communal.library.util.DensityUtil;
import com.spc.pose.demo.BR;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityFaceresultBinding;
import com.spc.pose.demo.databinding.ActivityStudentExamIndexBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
  人脸识别成功之后，人员信息展示界面
 * Created By:Chuck
 * Des:
 * on 2018/12/6 15:43
 */
public class FaceResultActivity extends BaseMvpActivity<ActivityFaceresultBinding> implements View.OnClickListener {

    public static final String TAG="FaceResultActivity";

    private ExamSubjectAdapter mExamAdapter;

    private StudentInfo mStudentInfo;

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
        setContentView(R.layout.activity_faceresult);

        initData();

        initView();


    }

    private void initData() {

        List<StudentInfo> allStudents = DBHelper.getInstance(this).getAllStudents();
        if(!CollectionUtils.isNullOrEmpty(allStudents)){
            mStudentInfo=allStudents.get(new Random().nextInt(allStudents.size()));
        }

    }

    //重置界面
    private void initView() {
        mViewBind.setClick(this);

      /*  BitmapUtil.loadPic(this,"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fgss0.baidu.com%2F7Po3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2Fd8f9d72a6059252d5322e19e359b033b5bb5b977.jpg&refer=http%3A%2F%2Fgss0.baidu.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1668950495&t=227b884563ef2261c782846225dced9b", R.mipmap.album_placehold_image, R.mipmap.album_placehold_image, mViewBind.ivHead, 50);
*/

        if(mStudentInfo!=null){
            mViewBind.tvStuName.setText("姓名："+mStudentInfo.getName());
            mViewBind.tvStuSn.setText("编号："+mStudentInfo.getDesc());

            // Constants.FACE_IMAGES_PATH+File.separator+imagesPaths[i]

            BitmapUtil.loadPic(this,  mStudentInfo.getImageSdCardPath(), R.mipmap.album_placehold_image, R.mipmap.album_placehold_image, mViewBind.ivHead, 50);

            FaceRecognizeResult.getInstance().setStudentId(mStudentInfo.getStudentUUID());
        }

    }




    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.tv_return:
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
            case R.id.tv_verify_infomation:
               // ToastUtils.showShort("tv_enter_exam");
                //startActivity(new Intent(this, ShuanggangActivity.class));

                startActivity(new Intent(this, StudentExamIndexActivity.class));
                finish();


                break;


        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
