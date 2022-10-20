package com.ilinklink.tg.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;

import com.ilinklink.tg.communal.AppLoader;

import com.ilinklink.tg.dialog.DialogConstom;
import com.ilinklink.tg.interfaces.CallBack;
import com.ilinklink.tg.interfaces.OnSoftKeyBoardVisibleListener;
import com.ilinklink.tg.interfaces.PermissionCallBack;
import com.ilinklink.tg.interfaces.PermissionCallBack2;
import com.ilinklink.tg.mvp.login.LoginActivity;

import com.ilinklink.tg.utils.BhUtils;
import com.ilinklink.tg.utils.CommonUtil;
import com.ilinklink.tg.utils.Constants;
import com.ilinklink.tg.utils.LogUtil;

import com.ilinklink.tg.utils.Tools;
import com.ilinklink.tg.widget.WifiView;
import com.qdong.communal.library.module.network.LinkLinkApi;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.module.network.RxHelper;
import com.qdong.communal.library.util.DateFormatUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.qdong.communal.library.widget.custommask.CustomMaskLayerView;
import com.qdong.communal.library.widget.custommask.CustomMaskLayerView2;
import com.spc.pose.demo.R;
import com.tbruyelle.rxpermissions2.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.ilinklink.tg.utils.PermissionPageUtil.REQUEST_PERMISSION_CODE;


/**
 * BaseActivity
 * activity父类,统一处理以下:
 * <p/>
 * 1,保存软键盘的当前的状态,并给出一个方法给子类,让其开启/关闭软键盘
 * 2,沉浸式状态栏的颜色设置
 * 3,baseActivity提供了一个公用的title,包含一个标题文本和一个左上角的返回按钮,可以设置为不可见(在setContentView之前调用setIsTitleBar(false)即可 )
 * 4,onTrimMemory里释放Glide资源
 * 5,提供一个loadingView,{@link #mLoadingView}盖在布局最外层,在titile下面.默认为隐藏
 * 6,子类可以重写getOnSoftKeyBoardVisibleListener,提供接口来处理软键盘弹出,收起事件
 * 7,通过ViewDataBinding绑定布局 见:{@link BaseActivity#setContentView(int)},{@link BaseActivity#setContentView(View)}
 * 8,提供自动登录的HashMap 见:{@link BaseActivity#getAutoLoginParameterMap()}
 * 9,友盟,页面停留时间统计 {@link #onResume()},{@link #onPause()}
 * 10,提供一个集合,{@link #mSubscriptions} 放Subscription,在{@link #onDestroy()}里循环释放,每次子类有调用RxJava时就把
 * <p/>
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/7  11:53
 * Copyright : 全民智慧城市 版权所有
 **/
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    protected static String TAG="";

    //控制是否需要 沉浸式状态栏
    private boolean isTranslucentStatus = true;

    //沉浸式状态栏底色布局,api>=19时才会初始化
    protected View stateBar;

    //自定义状态栏底色布局
    protected View CostomStateBar;

    //最外层的线性布局容器
    private LinearLayout ll_container;

    //最外层的相对布局容器
    private RelativeLayout rl_container;

    //是否使用公共的标题栏布局
    private boolean isTitleBar = true;


    //状态栏颜色,决定了wifi图标的颜色和时间的颜色
    private WifiView.Type mType=WifiView.Type.WHITE;

    //title栏的布局容器
    private View titleBarView;

    //返回按钮
    private ImageView iv_my_left;

    //右边按钮
    private ImageView iv_right;

    //标题
    private TextView tv_my_title;

    //右边文字
    private TextView commen_bar_tv_right;

    protected T mViewBind;

    protected CustomMaskLayerView mLoadingView;//loadingView
    protected CustomMaskLayerView2 mLoadingView2;//loadingView2: 动画效果
    private PermissionCallBack callBack;

    public T getmViewBind() {
        return mViewBind;
    }

    /**接口api**/
    protected LinkLinkApi mApi;
    private CustomReceiver receiver;
    private Map<Integer, DialogConstom> dialogConstomMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TAG=this.getClass().getSimpleName();
        ll_container = new LinearLayout(this);
        ll_container.setOrientation(LinearLayout.VERTICAL);

        //加这个布局,以同时适应追加的状态栏和沉浸式状态栏
        rl_container = new RelativeLayout(this);


        addOnSoftKeyBoardVisibleListener(this, getOnSoftKeyBoardVisibleListener());
        mApi= RetrofitAPIManager.provideClientApi(this);

        rigistFinishBR();

        setStateBarTextColorDefault();

        try {
            String lastChooseTime= SharedPreferencesUtil.getInstance(this).getString(Constants.INITENT_KEY_LAST_CRASH_TIME,"");
            if(!TextUtils.isEmpty(lastChooseTime)&&System.currentTimeMillis()-Long.parseLong(lastChooseTime)<4000){
               LogUtil.i(TAG,"JUST CRASHED,SO FINISH ALL!");
                sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        if(getCustomStatusBarType()!=WifiView.Type.NONE){
            /**
             * 隐藏系统的状态栏,使用自己绘制的状态栏.包含:wifi,时间
             */
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setStateBarVisibility(View.GONE);
        }
        else {
            setStateBarVisibility(View.VISIBLE);
        }

    }

    public LinkLinkApi getmApi() {
        return mApi;
    }

    /**
     * @method name:rigistFinishBR
     * @des:结束界面
     * @param :[]
     * @return type:void
     * @date 创建时间:2016/10/25
     * @author Chuck
     **/
    private void rigistFinishBR() {
        receiver=new CustomReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Constants.ACTION_FINISH_ALL);
        filter.addAction(Constants.ACTION_ALARM);
        filter.addAction(Constants.ACTION_NOTIFYCATION_CLICKED);
        registerReceiver(receiver,filter);
    }

    /***重写两个生命周期方法,并处理"大网红"相关推送 by:Chuck 2017/09/22********start*********************/
    protected boolean mIsStarted;//当前activiity是否正在前台

    @Override
    protected void onStart() {
        super.onStart();
        mIsStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsStarted = false;
    }
    private DialogConstom mDialog;


    //广播接收者,弹框等业务
    class CustomReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            LogUtil.i(TAG,"CustomReceiver,onReceive");


            if(Constants.ACTION_FINISH_ALL.equals(intent.getAction())){
               /* if(BaseActivity.this instanceof MainActivity){
                    //finish();
                }else{
                    finish();
                }*/


                //YPANDROID2-939   直接finish,不再区分是否是首页,退出登录会重新进首页
                finish();

            }
            else if(Constants.ACTION_NOTIFYCATION_CLICKED.equals(intent.getAction())){//通知的点击
                Bundle bundle=intent.getExtras();

                if(mIsStarted){//当前界面才处理
                    // TODO: 2019/3/7 逻辑:
                    // 1,当前界面就是目的界面,finish再进
                    //2,当前不是目的界面,则跳转
                    //ToastHelper.showCustomMessage(bundle);

                    handleJPushBundle(bundle);

                }

            }
        }
    }

    /**
     * @method name:handleJPushBundle
     * @des:处理极光拿到的数据,根据id,type,跳到不同的界面
     * @param :[bundle]
     * @return type:void
     * @date 创建时间:2019/3/7
     * @author Chuck
     **/
    protected void handleJPushBundle(Bundle bundle) {
        //1,如果当前界面就是目的界面,则finish,然后再进
        //2,如果当前不是目的界面,则直接跳转
        LogUtil.i(TAG,"数据:"+bundle);

        try {
            String content = bundle.getString("JPushInterface.EXTRA_EXTRA");
            LogUtil.e(TAG, "[JPushReceiver] EXTRA_ALERT: " + content);
            if (!TextUtils.isEmpty(content)) {
                JSONObject json = new JSONObject(content);
                if (TextUtils.isEmpty(json.toString()) || ("{}").equals(json.toString())) {
                    LogUtil.e(TAG, "EXTRA_EXTRA 为空!");
                } else {
//                    PageRouting rout= Json.fromJson(json.get("data").toString(),PageRouting.class);
//                    LogUtil.e(TAG, "rout:"+rout);
//
//                    if(rout!=null&&rout.getTypeFlag()==0){//
//                        PageRoutingUtil.startActivity2(this,rout.getType(),rout.getTypeId());
//                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void setTitle(CharSequence title) {
        setTitleText(title==null?"":title.toString());
    }

    /**
     * 设置状态栏字体颜色,默认为黑色
     */
    public void setStateBarTextColorDefault(){
        //沉浸式
        ImmersionBar.with(this)
                //.statusBarDarkFont(true, 0.2f) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .statusBarColor(R.color.transparent)
                .statusBarDarkFont(true)
                .statusBarColorTransform(R.color.transparent)
                .init();
    }

    /**
     * 设置状态栏字体颜色为白色
     */
    public void setStateBarTextColor2White(){
        //沉浸式
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .init();
    }

    /**
     * 设置状态栏字体颜色
     */
    public void setStateBarTextColor(int color){
        //沉浸式
        ImmersionBar.with(this)
                .statusBarColor(color)
                .init();
    }




    /**
     * 设置沉浸式状态栏的背景
     */
    public void setStateBarBackgroundColor(int color) {
        if (stateBar != null) {
            stateBar.setBackgroundColor(color);
        }
    }

    /**
     * 设置沉浸式状态栏的alpha
     */
    public void setStateBarBackgroundAlpha(float alpha) {
        if (stateBar != null) {
            stateBar.setAlpha(alpha);
        }
    }

    /**
     * 设置标题栏的背景颜色
     */
    public void setTitleBarBackgroundColor(int color) {
        if (titleBarView != null) {
            titleBarView.setBackgroundColor(color);
        }
    }

    /**
     * 设置标题文字
     */
    public void setTitleText(String title) {
        if (tv_my_title != null) {
            tv_my_title.setText(title);
        }
    }

    /**
     * 设置沉浸式状态栏布局是否显示
     */
    public void setStateBarVisibility(int visibility) {
        if (stateBar != null) {
            stateBar.setVisibility(visibility);
        }
    }

    /**
     * 设置沉浸式状态栏的高度
     */
    public void setStateBarHeight(int height) {
        if (stateBar != null) {
            ViewGroup.LayoutParams layoutParams = stateBar.getLayoutParams();
            layoutParams.height = height;
            stateBar.setLayoutParams(layoutParams);
        }
    }


    /**
     * 获取沉浸式状态栏的启用状态
     */
    public boolean isTranslucentStatus() {
        return isTranslucentStatus;
    }

    /**
     * 获取title栏的启用状态
     */
    public boolean isTitleBar() {
        return isTitleBar;
    }

    /**
     * 设置是否使用公用的title栏,默认为true
     */
    public void setIsTitleBar(boolean isTitleBar) {
        this.isTitleBar = isTitleBar;
    }


    /**
     * 获取title栏中间的textview
     */
    public TextView getTv_title() {
        return tv_my_title;
    }


    /**
     * 获取title栏左边的返回icon
     */
    public ImageView getLeftImageView() {
        return iv_my_left;
    }

    /**
     * 获取title栏右边的imageview
     */
    public ImageView getRightImageView() {
        return iv_right;
    }

    /**
     * 获取title栏右边的TextView
     */
    public TextView getRightTextView() {
        return commen_bar_tv_right;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setContentView(int layoutResID) {
        mType=getCustomStatusBarType();
        if(isRelativeStatusBar()){//叠加,沉浸式的状态栏
            View view = LayoutInflater.from(this).inflate(layoutResID, null);

            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);

            initTitleBar(activityParent);//把父类制造的title加进到子类里,title会在顶部

            /***
             * 把父类制造的自定义状态栏放在顶部(相对布局)
             */
            initCustomStatusBar(activityParent);

            mLoadingView= (CustomMaskLayerView) root.findViewById(R.id.loading_view);
            mLoadingView2= (CustomMaskLayerView2) root.findViewById(R.id.loading_view2);
            activityParent.addView(view,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            //ll_container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            rl_container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mViewBind = DataBindingUtil.bind(view);

            initStatusBar2();

            super.setContentView(rl_container);
        }
        else {//追加式的状态栏,垂直线性追加
            initStatusBar();
            initCustomStatusBar();
            initTitleBar();

            View view = LayoutInflater.from(this).inflate(layoutResID, null);

            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);
            mLoadingView= (CustomMaskLayerView) root.findViewById(R.id.loading_view);
            mLoadingView2= (CustomMaskLayerView2) root.findViewById(R.id.loading_view2);
            activityParent.addView(view,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            //ll_container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            ll_container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mViewBind = DataBindingUtil.bind(view);

            super.setContentView(ll_container);
        }


    }

    //此方法,是为了保证子类可以动态添加view,并能够使用dataBinding
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setContentView(View view) {

        mType=getCustomStatusBarType();
        if(isRelativeStatusBar()){//叠加,沉浸式的状态栏

            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);

            initTitleBar(activityParent);//把父类制造的title加进到子类里,title会在顶部

            /***
             * 把父类制造的自定义状态栏放在顶部(相对布局)
             */
            initCustomStatusBar(activityParent);

            mLoadingView= (CustomMaskLayerView) root.findViewById(R.id.loading_view);
            mLoadingView2= (CustomMaskLayerView2) root.findViewById(R.id.loading_view2);
            activityParent.addView(view,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            //ll_container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            rl_container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mViewBind = DataBindingUtil.bind(view);

            initStatusBar2();

            super.setContentView(rl_container);
        }
        else {//追加式的状态栏,垂直线性追加
            initStatusBar();
            initCustomStatusBar();
            initTitleBar();


            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);
            mLoadingView= (CustomMaskLayerView) root.findViewById(R.id.loading_view);
            mLoadingView2= (CustomMaskLayerView2) root.findViewById(R.id.loading_view2);
            activityParent.addView(view,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            //ll_container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            ll_container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mViewBind = DataBindingUtil.bind(view);

            super.setContentView(ll_container);
        }
    }

    /**
     * @method name:initTitleBar
     * @des:初始化标题栏
     * @param :[]
     * @return type:void
     * @date 创建时间:2018/12/5
     * @author Chuck
     **/
    private void initTitleBar() {


        if (isTitleBar) {
            titleBarView = View.inflate(this, R.layout.base_common_title_bar, null);
            iv_my_left = (ImageView) titleBarView.findViewById(R.id.commen_bar_back);
            iv_right = (ImageView) titleBarView.findViewById(R.id.iv_right);
            tv_my_title = (TextView) titleBarView.findViewById(R.id.commen_bar_title);
            commen_bar_tv_right = (TextView) titleBarView.findViewById(R.id.commen_bar_tv_right);
            LinearLayout ll_back = titleBarView.findViewById(R.id.ll_back);
            ll_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelOffset(R.dimen.title_bar_height));
            ll_container.addView(titleBarView, params);
        }
    }


    /**
     * @method name:initTitleBar
     * @des:初始化标题栏,垂直线性布局
     * @param :[activityParent]
     * @return type:void
     * @date 创建时间:2018/12/5
     * @author Chuck
     **/
    private void initTitleBar(LinearLayout activityParent) {


        try {
            if (isTitleBar) {
                titleBarView = View.inflate(this, R.layout.base_common_title_bar, null);
                iv_my_left = (ImageView) titleBarView.findViewById(R.id.commen_bar_back);
                iv_right = (ImageView) titleBarView.findViewById(R.id.iv_right);
                tv_my_title = (TextView) titleBarView.findViewById(R.id.commen_bar_title);
                commen_bar_tv_right = (TextView) titleBarView.findViewById(R.id.commen_bar_tv_right);
                LinearLayout ll_back = titleBarView.findViewById(R.id.ll_back);
                ll_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelOffset(R.dimen.title_bar_height));
                activityParent.addView(titleBarView, params);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @method name:isRelativeStatusBar
     * @des:返回true,则状态栏是叠加在主界面,false则垂直线性追加界面
     * @param :[]
     * @return type:boolean
     * @date 创建时间:2018/12/4
     * @author Chuck
     **/
    protected boolean isRelativeStatusBar(){
        return false;
    }
    /**
     * @method name:isRelativeStatusBar
     * @des:返回自定义状态栏的显示, NONE:不展示 WHITE:白色 BLACK:黑色
     * @param :[]
     * @return type:boolean
     * @date 创建时间:2018/12/4
     * @author Chuck
     **/
    protected WifiView.Type getCustomStatusBarType(){
        return WifiView.Type.NONE;
    }

    /**
     * @method name:initStatusBar
     * @des:初始化状态栏
     * @param :[]
     * @return type:void
     * @date 创建时间:2018/12/4
     * @author Chuck
     **/
    private void initStatusBar() {

        try {
            if (isTranslucentStatus && Build.VERSION.SDK_INT >= 19) {
                //透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //初始化stateBar
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppLoader.getStateBarHeight());
                stateBar = new View(this);
                stateBar.setBackgroundColor(getStatusBarColor());
                ll_container.addView(stateBar, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * @method name:initCustomStatusBar
     * @des:初始化自定义的状态栏(线性布局下调用)
     * @param :[]
     * @return type:void
     * @date 创建时间:2018/12/5
     * @author Chuck
     **/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initCustomStatusBar() {
        if (mType !=WifiView.Type.NONE) {
            setCustomStatusBar();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    70);
            ll_container.addView(CostomStateBar, params);
        }
    }



    /**
     * @method name:initTitleBar
     * @des:初始化自定义的状态栏(相对布局下使用)
     * @param :[activityParent:为何是个线性布局? 此线性布局与子类contentView是同级别层次的view,并非子类content的root view]
     * @return type:void
     * @date 创建时间:2018/12/5
     * @author Chuck
     **/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initCustomStatusBar(LinearLayout activityParent) {
        try {
            if (mType !=WifiView.Type.NONE) {
                setCustomStatusBar();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        70);
                activityParent.addView(CostomStateBar, params);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setCustomStatusBar() {
        CostomStateBar = View.inflate(this, R.layout.base_status_bar, null);


        RelativeLayout rl_root = (RelativeLayout) CostomStateBar.findViewById(R.id.rl_root);
        int color=mType ==WifiView.Type.WHITE?getColor(R.color.cor_c_E8E8E8):getColor(R.color.black);
        rl_root.setBackgroundColor(color);

        TextView tvTime = (TextView) CostomStateBar.findViewById(R.id.commen_bar_title);
        tvTime.setTextColor(mType ==WifiView.Type.BLACK?getColor(R.color.white):getColor(R.color.black));

        WifiView wifiView = (WifiView) CostomStateBar.findViewById(R.id.wifiView);
        wifiView.setmType(mType);

        //计时器
        Action1<? super Long> observer = new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                try {
                    tvTime.setText(DateFormatUtil.getDate(new Date(),DateFormatUtil.HM));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        getmSubscriptions().add(Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程,最终来到主线程执行
                .subscribe(observer));


    }


    /**
     * @method name:initStatusBar2
     * @des:初始化状态栏,沉浸式的
     * @param :[]
     * @return type:void
     * @date 创建时间:2018/12/4
     * @author Chuck
     **/
    private void initStatusBar2() {

        try {
            if (isTranslucentStatus && Build.VERSION.SDK_INT >= 19) {
                //透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //初始化stateBar
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppLoader.getStateBarHeight());
                stateBar = new View(this);
                stateBar.setBackgroundColor(getStatusBarColor());
                rl_container.addView(stateBar, params);//注意,这里是rl_container

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param :[]
     * @return type:int
     *
     * @method name:getStatusBarColor
     * @des:子类可以重写这个,修改状态栏的颜色
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    protected int getStatusBarColor() {
        return Color.parseColor("#ffffffff");
    }


    /**
     * **********************************************************************************************************
     * 软键盘监听相关
     **/
    private boolean sLastVisiable;//标示此时键盘是否已经弹出  true为已经弹出

    /**
     * @param :[activity, listener]
     * @return type:void
     *
     * @method name:addOnSoftKeyBoardVisibleListener
     * @des:添加软键盘是否弹出的监听器
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    private void addOnSoftKeyBoardVisibleListener(Activity activity, final OnSoftKeyBoardVisibleListener listener) {

        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHight = rect.bottom - rect.top;
                int hight = decorView.getHeight();
                boolean visible = (double) displayHight / hight < 0.8;

                if (visible != sLastVisiable && listener != null) {
                    listener.onSoftKeyBoardVisible(visible);
                }
                sLastVisiable = visible;
            }
        });
    }




    /**
     * @param :[]
     * @return type:com.qdong.onemile.interfaces.OnSoftKeyBoardVisibleListener
     *
     * @method name:getOnSoftKeyBoardVisibleListener
     * @des:子类可以重写这个来获取软键盘的弹出,收起事件
     * @date 创建时间:2016/7/11
     * @author Chuck
     **/
    protected OnSoftKeyBoardVisibleListener getOnSoftKeyBoardVisibleListener() {
        return null;
    }


    /**
     * @method name:showSoftInput
     * @des:手动弹出软键盘
     * @param :[editText]
     * @return type:void
     * @date 创建时间:2016/9/3
     * @author Chuck
     **/
    protected void showSoftInput(final EditText editText) {

        try {
            if(editText!=null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isSoftInputShowwing()){
                           showOrHideInput(editText);
                        }
                    }
                },500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏键盘
     * @param editText
     */
    public void hideSoftInput(final EditText editText){

        try {
            if (editText != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param :[]
     * @return type:boolean
     *
     * @method name:isSoftInputShowwing
     * @des:软键盘此时是否弹出了 true为已弹出
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    protected boolean isSoftInputShowwing() {
        return sLastVisiable;
    }

    /**
     * @param :[show, view]
     * @return type:void
     *
     * @method name:showOrHideInput
     * @des:展示或者隐藏软件盘 切换 如果此时软键盘弹了就隐藏,如果此时隐藏了就弹出
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    protected void showOrHideInput(EditText view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                view.setFocusableInTouchMode(true);
                view.requestFocus();
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        // 参数1：MainActivity进场动画，参数2：SecondActivity出场动画
        //overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_left_out);
        overridePendingTransition(R.anim.base_display, R.anim.base_slide_right_out);
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        try {
            // 必须在UI线程中调用
            if (!AppLoader.getInstance().isAppFront()) {
                Glide.get(this).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected HashMap<String, String> getAutoLoginParameterMap() {
        return AppLoader.getInstance().getAutoLoginParameterMap();
    }


    protected boolean showActiveDialog(){
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //MobclickAgent.onResume(this);       //友盟统计时长



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void onPause() {
        super.onPause();
        try {
            //MobclickAgent.onPause(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   protected ArrayList<Subscription> mSubscriptions=new ArrayList<>();

    public ArrayList<Subscription> getmSubscriptions() {
        return mSubscriptions;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy(); //必须调用该方法，防止内存泄漏
        try {
            for(Subscription s:mSubscriptions){
                LogUtil.e("Subscription",s);
                RxHelper.getInstance(this).unsubscribe(s);
            }
            unregisterReceiver(receiver);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @method name:getmLoadingView
     * @des:获取loadingview
     * @param :[]
     * @return type:com.qdong.communal.library.widget.CustomMaskLayerView.CustomMaskLayerView
     * @date 创建时间:2016/10/24
     * @author Chuck
     **/
    public CustomMaskLayerView getmLoadingView() {
        return mLoadingView;
    }

    public CustomMaskLayerView2 getmLoadingView2() {
        return mLoadingView2;
    }

    /**
     * @method name:executeTaskAutoRetry
     * @param :[observable, observer]
     * @return type:void
     * @date 创建时间:2017/5/13
     * @author Chuck
     **/
    public void executeTaskAutoRetry(final Observable<LinkLinkNetInfo> observable, final Observer<LinkLinkNetInfo> observer){


        Observer<LinkLinkNetInfo> ob=new AbstractObserver () {


            @Override
            public void onCompleted() {
                observer.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG,"父类处理异常:"+e.getMessage());

                if(RxHelper.THROWABLE_MESSAGE_AUTO_LOGIN_FAILED.equals(e.getMessage())){//自动登陆失败,跳登录界面
                        //sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                }
                else {
                    observer.onError(e);
                }
            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(isNeedLogin(linkLinkNetInfo.getErrorCode())){
                    //sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面
                    startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                }
                else if(RxHelper.ERROR_SQUEEZE_OUT.equals(linkLinkNetInfo.getErrorCode())){//被挤下线
                     mLoadingView.dismiss();
                     Tools.quitApp(BaseActivity.this);
                }
                else{
                    observer.onNext(linkLinkNetInfo);
                }
            }
        };
        mSubscriptions.add(RxHelper.getInstance(BaseActivity.this).executeTaskAutoRetry(observable,mApi,ob, AppLoader.getInstance().getAutoLoginParameterMap()));
    }
    /**
     * @method name:executeTaskAutoRetry
     * @param :[observable, observer]
     * @return type:void
     * @date 创建时间:2017/5/13
     * @author Chuck
     **/
    public void executeTaskAutoRetry1(final Observable<LinkLinkNetInfo> observable, final Observer<LinkLinkNetInfo> observer){

        mSubscriptions.add(RxHelper.getInstance(BaseActivity.this).executeTaskAutoRetry(observable,mApi,observer, AppLoader.getInstance().getAutoLoginParameterMap()));
    }


    /**
     * 检验拍照权限
     */
    public void checkCameraPermissions(){

        if(callBack == null){
            callBack = generatePermissionCallBack();
        }



    }

    private PermissionCallBack generatePermissionCallBack() {

        PermissionCallBack callBack = new PermissionCallBack() {

            @Override
            public void permissionGranted(Permission permission) {


                        onCameraGranted();



            }

            @Override
            public void permissionShouldShowRequest(Permission permission) {
                String message = getString(R.string.permission_tips1);

                showMessageOKCancel(0,message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkCameraPermissions();
                    }
                });
            }

            @Override
            public void permissionNeverAgain(Permission permission) {

                String message = getString(R.string.permission_tips2);

                showMessageOKCancel(1,message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CommonUtil.jump2PermissionPager(BaseActivity.this);
                    }
                });
            }

            @Override
            public void complete() {

            }
        };

        return callBack;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISSION_CODE){
            //checkCameraPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /*if(requestCode== CameraActivity.TO_GRANT_AUTHORIZATION){
            boolean grant=true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // callPhone();
                    //Toast.makeText(this,"权限被拒绝",Toast.LENGTH_LONG).show();
                    grant=false;
                    break;
                }
            }
            //Toast.makeText(this,grant?"同意":"拒绝",Toast.LENGTH_LONG).show();
            if(grant){
                onCameraGranted();
            }
            else{
                checkPermission1();
            }
        }*/
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        boolean ret = true;
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            ret = false;
        }
        return ret;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission1() {
        boolean ret = true;

        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.CAMERA)) {
            permissionsNeeded.add( getString(R.string.camera_permission_camera) );
        }
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO)) {
            permissionsNeeded.add( getString(R.string.camera_permission_microphone) );
        }
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add( getString(R.string.camera_permission_sd_card) );
        }

        if (permissionsNeeded.size() > 0) {
            // Need Rationale
            String message = getString(R.string.camera_permission) + permissionsNeeded.get(0);
            for (int i = 1; i < permissionsNeeded.size(); i++) {
                message = message + ", " + permissionsNeeded.get(i);
            }
            // Check for Rationale Option

            showMessageOKCancel(0,message,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
//                                    CameraActivity.TO_GRANT_AUTHORIZATION);
                        }
                    });


            /*if (!shouldShowRequestPermissionRationale(permissionsList.get(0))) {
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        CameraActivity.TO_GRANT_AUTHORIZATION);
                            }
                        });
            }
            else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        CameraActivity.TO_GRANT_AUTHORIZATION);
            }*/
            ret = false;
        }

        return ret;
    }

    //重写此方法,避免崩溃时frament重叠
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void showMessageOKCancel(int type, String message, DialogInterface.OnClickListener okListener) {
       /* new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();*/
        DialogConstom mDialog = dialogConstomMap.get(type);

        if(mDialog == null){
            CallBack callBack=new CallBack() {
                @Override
                public void callBack(Object object) {
                    if (object !=null && object instanceof Boolean){
                        boolean commit= (boolean) object;
                        if(commit == true){//true:确认  false :取消
                            //ToastHelper.showCustomMessage("delete");
                            if(okListener!=null){
                                okListener.onClick(null,0);
                            }
                        }
                    }
                }
            };
            mDialog = new DialogConstom(
                    this,
                    false,
                    true,
                    this.getString(R.string.camera_permission_tips),
                    message,
                    //DensityUtil.dip2px(activity,9),
                    -1,
                    this.getString(R.string.camera_permission_setting),
                    this.getString(R.string.camera_permission_sd_cancel),
                    callBack);

            dialogConstomMap.put(type,mDialog);
        }

        if(!mDialog.isShowing()){
            mDialog.show();
        }
    }

    private void showNoticeDialog(int type,String message) {

        DialogConstom mDialog = dialogConstomMap.get(type);
        if(mDialog == null){
            CallBack callBack=new CallBack() {
                @Override
                public void callBack(Object object) {
                    if (object !=null && object instanceof Boolean){
                        boolean commit= (boolean) object;
                        if(commit == true){//true:确认  false :取消
                            //ToastHelper.showCustomMessage("delete");
                            CommonUtil.jump2PermissionPager(BaseActivity.this);
                        }
                    }
                }
            };
            mDialog = new DialogConstom(
                    this,
                    false,
                    false,
                    this.getString(R.string.camera_permission_tips),
                    message,
                    //DensityUtil.dip2px(activity,9),
                    -1,
                    this.getString(R.string.dialog_got_it),
                    "",
                    callBack);
        }

        if(!mDialog.isShowing()){
            mDialog.show();
        }

    }


    /**
     * @method
     * @des:跳转至拍照界面,参数自己填.子类重写这个,以实现拍照授权后的回调.重写之前,如果界面有额外的
     * 授权,重写onRequestPermissionsResult时,要把super给做了.
     * @date 创建时间:2018/12/21
     * @author Chuck
     **/
    protected void onCameraGranted(){

    }


    /**
     * @method name:checkLocationPermission
     * @des:定位动态授权,拒绝后,会弹框引导用户前往设置界面
     * @param :[callBack :回调, requestCodeGoToSetting:跳转到设置界面的code,子类在onActivityResult里可再作后续处理]
     * @return type:void
     * @date 创建时间:2019/4/25
     * @author Chuck
     **/
    protected void checkLocationPermission(PermissionCallBack2 callBack, int requestCodeGoToSetting) {

        // BUGFIX: 2019/12/20  BY:Chuck http://10.32.156.110:8080/browse/YPANDROID2-1466
        if (!BhUtils.isLocationEnabled(this)) {
            //这里需要延迟个0.3秒显示无权限的对话框，不然截图拿到是滑动到一半的过程
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //execute the task
                    DialogConstom mDialog = new DialogConstom(BaseActivity.this, false, true, getString(R.string.location_permission_dialog_title), getString(R.string.location_permission_dialog_content), 14, getString(R.string.location_permission_dialog_confirm_btn), getString(R.string.location_permission_dialog_cancel_btn), new CallBack() {
                        @Override
                        public void callBack(Object object) {
                            if (object != null && object instanceof Boolean) {
                                boolean commit = (boolean) object;
                                if (commit) {
                                    //进入设置去开启服务
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent,requestCodeGoToSetting);

                                } else {
                                    //点击了暂不,则什么也不做
                                }
                            }
                        }
                    });
                    mDialog.show();
                }
            }, 300);

            return;
        }


        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ;

        if(isPermissionOK){//低版本设备直接回调
            if(callBack!=null){
                callBack.permissionGranted(null);
            }
            return;
        }

        PermissionCallBack callBack1 = new PermissionCallBack() {
            @Override
            public void permissionGranted(Permission permission) {
                if(callBack!=null){
                    callBack.permissionGranted(permission);
                }
            }

            @Override
            public void permissionShouldShowRequest(Permission permission) {
                showSettingDialog(callBack,requestCodeGoToSetting);
            }

            @Override
            public void permissionNeverAgain(Permission permission) {
                showSettingDialog(callBack,requestCodeGoToSetting);
            }

            @Override
            public void complete() {

            }
        };


      /*  PermissionsUtil.requestPermission(this,callBack1, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);*/
    }

    /**
     * @method name:showSettingDialog
     * @des:弹框跳转到设置界面
     * @param :[]
     * @return type:void
     * @date 创建时间:2019/4/25
     * @author Chuck
     **/
    private void showSettingDialog(PermissionCallBack2 callBack,int code) {

        CallBack callBack1 = new CallBack() {
            @Override
            public void callBack(Object object) {
                if (object !=null && object instanceof Boolean){
                    boolean commit= (boolean) object;
                    if(commit){//true:确认  false :取消
                        //ToastHelper.showCustomMessage("delete");
                        CommonUtil.jump2PermissionPager(BaseActivity.this,code);
                    }else{
                        if(callBack!=null){//拒绝了
                            callBack.permissionRefused();
                        }
                    }
                }
            }
        };

        DialogConstom dialog = new DialogConstom(
                this,
                false,
                true,
                this.getString(R.string.dialog_location_hint),
                this.getString(R.string.location_srtting_hint),
                //DensityUtil.dip2px(activity,9),
                -1,
                this.getString(R.string.go_to_setting),
                this.getString(R.string.location_cancel),
                callBack1);


        dialog.show();
    }


    /**
     * @param :[]
     * @return type:void
     * @method name:showDeniedDialog
     * @des:没有定位权限显示权限被拒绝对话框
     * @date 创建时间:2019/3/26
     * @author jibinghao
     **/
    protected void showDeniedDialog(boolean isLocationService,int requestCode,CallBack callBack) {

        //这里需要延迟个0.3秒显示无权限的对话框，不然截图拿到是滑动到一半的过程
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                DialogConstom mDialog = new DialogConstom(BaseActivity.this, false, true, getString(R.string.location_permission_dialog_title), getString(R.string.location_permission_dialog_content), 14, getString(R.string.location_permission_dialog_confirm_btn), getString(R.string.location_permission_dialog_cancel_btn), new CallBack() {
                    @Override
                    public void callBack(Object object) {
                        if (object != null && object instanceof Boolean) {
                            boolean commit = (boolean) object;
                            if (commit) {
                                if (isLocationService) {//如果系统的定位服务开启了,则跳应用详情,授权
                                    CommonUtil.jump2PermissionPager(BaseActivity.this,requestCode);
                                } else {//没开启,则先前往系统设计界面,开启定位服务
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent,requestCode);
                                }
                                //进入设置去开启服务

                            } else {

                                if(callBack!=null){
                                    callBack.callBack(null);
                                }
                            }
                        }
                    }
                });
                mDialog.show();
            }
        }, 300);

    }


}
