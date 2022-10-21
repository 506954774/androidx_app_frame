package com.ilinklink.tg.mvp.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;


import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
//import com.ilinklink.tg.utils.BitmapUtils;
import com.ilinklink.tg.utils.Constants;
import com.ilinklink.tg.utils.PhoneUtils;
import com.ilinklink.tg.utils.ToastHelper;
import com.ilinklink.tg.widget.WifiView;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.qdong.communal.library.util.ToastUtil;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityLoginBinding;

import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.RequiresApi;

/**
 * LoginActivity
 * 登录界面
 * Created By:Chuck
 * Des:
 * on 2018/12/6 15:43
 */
public class LoginActivity extends BaseMvpActivity<ActivityLoginBinding> implements View.OnClickListener, LoginContract.LoginView{

    private LoginContract.LoginPresenter mLoginPresenter;
    private Bundle mBundle;
    private int mClickCount;
    private String uuID;


    //给父类存起来,父类destory时遍历释放资源
    @Override
    public ArrayList<BasePresenter> initPresenters() {
        ArrayList<BasePresenter> list = new ArrayList<>();
        mLoginPresenter = new LoginPresenterImpl(this);
        list.add((BasePresenter) mLoginPresenter);
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
        setContentView(R.layout.activity_login);
        mBundle = getIntent().getExtras();
        initView();
        initData();


    }

    private void initData() {
        DBHelper.getInstance(getApplication()).deleteAllLoginModel();//清除用户数据
        //mLoginPresenter.pollingTask(uuID);
    }

    //重置界面
    private void initView() {
        mViewBind.setClick(this);
        String account = SharedPreferencesUtil.getInstance(this).getString(Constants.ACCOUNT, "");
        if (!TextUtils.isEmpty(account)) {
            mViewBind.account.setText(account);
        }

       uuID = UUID.randomUUID().toString().replaceAll("-", "");
        Bitmap bitmap = null;
//        try {
//            bitmap = BitmapUtils.create2DCodeByLogo(uuID,136,136);//根据内容生成二维码
//            mViewBind.ivQrcode.setImageBitmap(bitmap);
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }


    }

    private boolean checkData() {
        if (TextUtils.isEmpty(mViewBind.account.getText().toString())) {
            ToastUtil.showCustomMessage(this, getString(R.string.login_phone_hint));
            return false;
        }
        if (TextUtils.isEmpty(mViewBind.pwd.getText().toString().trim())) {
            ToastUtil.showCustomMessage(this, getString(R.string.login_pwd_hint));
            return false;
        }

        if (!PhoneUtils.isMobile(mViewBind.account.getText().toString())) {
            ToastHelper.showCustomMessage(getString(R.string.account_login_phone_error));
            return false;
        }

        return true;
    }



    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.custom_tv_login://账户登录
                hideSoftInput(mViewBind.account);
                if (checkData()) {
                    showLoading(getString(R.string.login_logining));
                    mLoginPresenter.login(mViewBind.account.getText().toString(), mViewBind.pwd.getText().toString());
                }

                break;

            case R.id.custom_tv_register://注册
                //intent = new Intent(this, RegisterActivity.class);
                if (mBundle != null) {
                    intent.putExtras(mBundle);
                }
                startActivity(intent);
                break;
            case R.id.custom_tv_reset_pwd://忘记密码
                //intent = new Intent(this, FindPwdActivity.class);
                if (mBundle != null) {
                    intent.putExtras(mBundle);
                }
                startActivity(intent);
                break;
            case R.id.rl_swap_code:
                //扫码登录
                mViewBind.llPsw.setVisibility(View.GONE);
                mViewBind.llSwap.setVisibility(View.VISIBLE);
//                try {
//                    Bitmap bitmap = BitmapUtils.create2DCodeByLogo(uuID,440,440);
//                    mViewBind.ivSwapQrcode.setImageBitmap(bitmap);
//                } catch (WriterException e) {
//                    e.printStackTrace();
//                }


                break;
            case R.id.rl_by_account:
                //账号密码登录
                mViewBind.llPsw.setVisibility(View.VISIBLE);
                mViewBind.llSwap.setVisibility(View.GONE);

                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        if (mBundle != null) {
            jump2MainActivity();
        } else {
            finish();
        }
    }

    @Override
    public void loginSuccess() {
        mLoginPresenter.checkUserInfo();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void loginFailed(String msg) {
        dismissLoading();
        showToast(msg);
        mViewBind.pwd.setText("");//清空密码
        //mViewBind.account.setText("");//清空密码
    }

    @Override
    public void jumpNextActivity(int type) {
       // 1,主界面,2健康信息资料
        if(type == 1){
            jump2MainActivity();
        }else{
            jump2UserHealthyInfoActivity();
        }
    }


    private void jump2UserHealthyInfoActivity(){
        Intent intent = null;//new Intent(this, HealthyInfoActivity.class);
        //intent.putExtras(mBundle);
        startActivity(intent);
        finish();
    }

    private void jump2MainActivity() {

        Intent intent = null;//new Intent(this, MainActivity.class);
        //intent.putExtras(mBundle);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mLoginPresenter.pollingTask(uuID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LoginPresenterImpl loginPresenter = (LoginPresenterImpl) mLoginPresenter;
        loginPresenter.endPolling();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected WifiView.Type getCustomStatusBarType() {
        return WifiView.Type.NONE;
    }

}
