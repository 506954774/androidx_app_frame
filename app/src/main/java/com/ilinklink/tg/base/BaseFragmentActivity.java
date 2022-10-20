package com.ilinklink.tg.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.ilinklink.tg.interfaces.OnSoftKeyBoardVisibleListener;
import com.ilinklink.tg.mvp.login.LoginActivity;
import com.ilinklink.tg.utils.Constants;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.Tools;
import com.qdong.communal.library.module.network.LinkLinkApi;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.module.network.RxHelper;
import com.qdong.communal.library.widget.custommask.CustomMaskLayerView;
import com.spc.pose.demo.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

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
 * 7,通过ViewDataBinding绑定布局 见:{@link BaseFragmentActivity#setContentView(int)},{@link BaseFragmentActivity#setContentView(View)}
 * 8,提供自动登录的HashMap 见:{@link BaseFragmentActivity#getAutoLoginParameterMap()}
 * 9,友盟,页面停留时间统计 {@link #onResume()},{@link #onPause()}
 * 10,提供一个集合,{@link #mSubscriptions} 放Subscription,在{@link #onDestroy()}里循环释放,每次子类有调用RxJava时就把
 * <p/>
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/7  11:53
 * Copyright : 全民智慧城市 版权所有
 **/
public abstract class BaseFragmentActivity<T extends ViewDataBinding> extends FragmentActivity {

    protected static String TAG="";

    //控制是否需要 沉浸式状态栏
    private boolean isTranslucentStatus = true;

    //沉浸式状态栏底色布局,api>=19时才会初始化
    protected View stateBar;

    //最外层的线性布局容器
    private LinearLayout ll_container;

    //最外层的相对布局容器
    private RelativeLayout rl_container;

    //是否使用公共的标题栏布局
    private boolean isTitleBar = true;

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

    public T getmViewBind() {
        return mViewBind;
    }

    /**接口api**/
    protected LinkLinkApi mApi;
    private CustomReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG=this.getClass().getSimpleName();
        ll_container = new LinearLayout(this);
        ll_container.setOrientation(LinearLayout.VERTICAL);

        //加这个布局,以同时适应追加的状态栏和沉浸式状态栏
        rl_container = new RelativeLayout(this);


        addOnSoftKeyBoardVisibleListener(this, getOnSoftKeyBoardVisibleListener());
        mApi= RetrofitAPIManager.provideClientApi(this);

        rigistFinishBR();

        setStateBarTextColorDefault();

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
            if(Constants.ACTION_FINISH_ALL.equals(intent.getAction())) {
                finish();
            }

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
                .statusBarDarkFont(true, 0.2f) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
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


    @Override
    public void setContentView(int layoutResID) {

        if(isRelativeStatusBar()){//叠加,沉浸式的状态栏
            View view = LayoutInflater.from(this).inflate(layoutResID, null);

            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);

            initTitleBar(activityParent);//把父类制造的title加进到子类里,title会在顶部

            mLoadingView= (CustomMaskLayerView) root.findViewById(R.id.loading_view);
            activityParent.addView(view,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            //ll_container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            rl_container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mViewBind = DataBindingUtil.bind(view);

            initStatusBar2();

            super.setContentView(rl_container);
        }
        else {//追加式的状态栏,垂直线性追加
            initStatusBar();
            initTitleBar();

            View view = LayoutInflater.from(this).inflate(layoutResID, null);

            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);
            mLoadingView= (CustomMaskLayerView) root.findViewById(R.id.loading_view);
            activityParent.addView(view,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            //ll_container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            ll_container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mViewBind = DataBindingUtil.bind(view);

            super.setContentView(ll_container);
        }


    }

    //此方法,是为了保证子类可以动态添加view,并能够使用dataBinding
    @Override
    public void setContentView(View view) {



        if(isRelativeStatusBar()){//叠加,沉浸式的状态栏

            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);

            initTitleBar(activityParent);//把父类制造的title加进到子类里,title会在顶部

            mLoadingView= (CustomMaskLayerView) root.findViewById(R.id.loading_view);
            activityParent.addView(view,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            //ll_container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            rl_container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mViewBind = DataBindingUtil.bind(view);

            initStatusBar2();

            super.setContentView(rl_container);
        }
        else {//追加式的状态栏,垂直线性追加
            initStatusBar();
            initTitleBar();


            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);
            mLoadingView= (CustomMaskLayerView) root.findViewById(R.id.loading_view);
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
                iv_my_left.setOnClickListener(new View.OnClickListener() {
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
        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_left_out);
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        try {
            // 必须在UI线程中调用
            if (!AppLoader.getInstance().isAppFront())
                Glide.get(this).clearMemory();
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

    protected void onResume() {
        super.onResume();
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void onPause() {
        super.onPause();
        try {

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
                        startActivity(new Intent(BaseFragmentActivity.this, LoginActivity.class));
                }
                else {
                    observer.onError(e);
                }
            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(isNeedLogin(linkLinkNetInfo.getErrorCode())){
                    //sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面
                    startActivity(new Intent(BaseFragmentActivity.this, LoginActivity.class));
                }
                else if(RxHelper.ERROR_SQUEEZE_OUT.equals(linkLinkNetInfo.getErrorCode())){//被挤下线
                    mLoadingView.dismiss();
                    Tools.quitApp(BaseFragmentActivity.this);
                }
                else{
                    observer.onNext(linkLinkNetInfo);
                }
            }
        };
        mSubscriptions.add(RxHelper.getInstance(BaseFragmentActivity.this).executeTaskAutoRetry(observable,mApi,ob, AppLoader.getInstance().getAutoLoginParameterMap()));
    }
    /**
     * @method name:executeTaskAutoRetry
     * @param :[observable, observer]
     * @return type:void
     * @date 创建时间:2017/5/13
     * @author Chuck
     **/
    public void executeTaskAutoRetry1(final Observable<LinkLinkNetInfo> observable, final Observer<LinkLinkNetInfo> observer){

        mSubscriptions.add(RxHelper.getInstance(BaseFragmentActivity.this).executeTaskAutoRetry(observable,mApi,observer, AppLoader.getInstance().getAutoLoginParameterMap()));
    }




}
