package com.ilinklink.tg.mvp.initfacefeatrue;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.ToastUtils;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.mvp.BasePresenter;
import com.qdong.communal.library.util.LogUtil;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityInitFaceFeatrueBinding;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;

/**
 * InitFaceFeatrueActivity
 * 初始化人脸特征值
 * Created By:Chuck
 * Des:
 * on 2018/12/6 15:43
 */
public class InitFaceFeatrueActivity extends BaseMvpActivity<ActivityInitFaceFeatrueBinding> implements View.OnClickListener  {

    private volatile boolean mImporting;   // 导入状态，是否正在导入


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

        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //设置布局,里面有埋点按钮,详细看布局文件
        setContentView(R.layout.activity_init_face_featrue);

        initView();


        initBaiduFaceModel();



        mViewBind.ivBack.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        },1500);
    }





    private void initData() {

        LogUtil.i("initfacefeatrue","initData");

        //
        if (!mImporting) {

            mImporting = true;
            // 开始导入

        }

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
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onBackPressed() {

    }



    public void showProgressView() {

    }

    /**
     * 正在导入，实时更新导入状态
     */

    public void onImporting(final int totalCount, final int successCount, final int failureCount,
                            final float progress) {

        LogUtil.i("initfacefeatrue","onImporting,progress:"+progress);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // mBtnDialogClose.setEnabled(false);    // 设置进度条“关闭”不可点击
                mViewBind.progressbarHint.setProgress((int) (progress * 100));

                mViewBind.tvFeatrueTotal.setText("数据总数：" + totalCount);
                mViewBind.tvFeatrueSucessed.setText("导入成功：" + successCount);
                mViewBind.tvFeatrueFailed.setText("导入失败：" + failureCount);

            }
        });
    }

    /**
     * 导入结束，显示导入结果
     */

    public void endImport(final int totalCount, final int successCount, final int failureCount) {
        LogUtil.i("initfacefeatrue","endImport,totalCount:"+totalCount+",successCount:"+successCount+",failureCount:"+failureCount);


        // 数据变化，更新内存
//        FaceSDKManager.getInstance().initDatabases();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mViewBind.progressbarHint.setProgress((int) (1 * 100));


                mViewBind.tvFeatrueTotal.setText("数据总数：" + totalCount);
                mViewBind.tvFeatrueSucessed.setText("导入成功：" + successCount);
                mViewBind.tvFeatrueFailed.setText("导入失败：" + failureCount);

                // 设置数据库状态

                mImporting = false;

                mViewBind.ivBack.setVisibility(View.VISIBLE);
            }
        });
    }


    public void showToastMessage(String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放

//        ImportFileManager.getInstance().release();
    }

}
