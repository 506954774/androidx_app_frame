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
import com.ilinklink.app.fw.R;
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
 * activity??????,??????????????????:
 * <p/>
 * 1,?????????????????????????????????,??????????????????????????????,????????????/???????????????
 * 2,?????????????????????????????????
 * 3,baseActivity????????????????????????title,?????????????????????????????????????????????????????????,????????????????????????(???setContentView????????????setIsTitleBar(false)?????? )
 * 4,onTrimMemory?????????Glide??????
 * 5,????????????loadingView,{@link #mLoadingView}?????????????????????,???titile??????.???????????????
 * 6,??????????????????getOnSoftKeyBoardVisibleListener,????????????????????????????????????,????????????
 * 7,??????ViewDataBinding???????????? ???:{@link BaseActivity#setContentView(int)},{@link BaseActivity#setContentView(View)}
 * 8,?????????????????????HashMap ???:{@link BaseActivity#getAutoLoginParameterMap()}
 * 9,??????,???????????????????????? {@link #onResume()},{@link #onPause()}
 * 10,??????????????????,{@link #mSubscriptions} ???Subscription,???{@link #onDestroy()}???????????????,?????????????????????RxJava?????????
 * <p/>
 * ?????????:  Chuck
 * ???????????? Chuck
 * ??????/????????????: 2016/7/7  11:53
 * Copyright : ?????????????????? ????????????
 **/
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    protected static String TAG="";

    //?????????????????? ??????????????????
    private boolean isTranslucentStatus = true;

    //??????????????????????????????,api>=19??????????????????
    protected View stateBar;

    //??????????????????????????????
    protected View CostomStateBar;

    //??????????????????????????????
    private LinearLayout ll_container;

    //??????????????????????????????
    private RelativeLayout rl_container;

    //????????????????????????????????????
    private boolean isTitleBar = true;


    //???????????????,?????????wifi?????????????????????????????????
    private WifiView.Type mType=WifiView.Type.WHITE;

    //title??????????????????
    private View titleBarView;

    //????????????
    private ImageView iv_my_left;

    //????????????
    private ImageView iv_right;

    //??????
    private TextView tv_my_title;

    //????????????
    private TextView commen_bar_tv_right;

    protected T mViewBind;

    protected CustomMaskLayerView mLoadingView;//loadingView
    protected CustomMaskLayerView2 mLoadingView2;//loadingView2: ????????????
    private PermissionCallBack callBack;

    public T getmViewBind() {
        return mViewBind;
    }

    /**??????api**/
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

        //???????????????,??????????????????????????????????????????????????????
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
             * ????????????????????????,??????????????????????????????.??????:wifi,??????
             */
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            /**???????????????View?????????????????????????????????????????????????????????????????????,??????????????????**/
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
     * @des:????????????
     * @param :[]
     * @return type:void
     * @date ????????????:2016/10/25
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

    /***??????????????????????????????,?????????"?????????"???????????? by:Chuck 2017/09/22********start*********************/
    protected boolean mIsStarted;//??????activiity??????????????????

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


    //???????????????,???????????????
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


                //YPANDROID2-939   ??????finish,???????????????????????????,??????????????????????????????
                finish();

            }
            else if(Constants.ACTION_NOTIFYCATION_CLICKED.equals(intent.getAction())){//???????????????
                Bundle bundle=intent.getExtras();

                if(mIsStarted){//?????????????????????
                    // TODO: 2019/3/7 ??????:
                    // 1,??????????????????????????????,finish??????
                    //2,????????????????????????,?????????
                    //ToastHelper.showCustomMessage(bundle);

                    handleJPushBundle(bundle);

                }

            }
        }
    }

    /**
     * @method name:handleJPushBundle
     * @des:???????????????????????????,??????id,type,?????????????????????
     * @param :[bundle]
     * @return type:void
     * @date ????????????:2019/3/7
     * @author Chuck
     **/
    protected void handleJPushBundle(Bundle bundle) {
        //1,????????????????????????????????????,???finish,????????????
        //2,??????????????????????????????,???????????????
        LogUtil.i(TAG,"??????:"+bundle);

        try {
            String content = bundle.getString("JPushInterface.EXTRA_EXTRA");
            LogUtil.e(TAG, "[JPushReceiver] EXTRA_ALERT: " + content);
            if (!TextUtils.isEmpty(content)) {
                JSONObject json = new JSONObject(content);
                if (TextUtils.isEmpty(json.toString()) || ("{}").equals(json.toString())) {
                    LogUtil.e(TAG, "EXTRA_EXTRA ??????!");
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
     * ???????????????????????????,???????????????
     */
    public void setStateBarTextColorDefault(){
        //?????????
        ImmersionBar.with(this)
                //.statusBarDarkFont(true, 0.2f) //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                .statusBarColor(R.color.transparent)
                .statusBarDarkFont(true)
                .statusBarColorTransform(R.color.transparent)
                .init();
    }

    /**
     * ????????????????????????????????????
     */
    public void setStateBarTextColor2White(){
        //?????????
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .init();
    }

    /**
     * ???????????????????????????
     */
    public void setStateBarTextColor(int color){
        //?????????
        ImmersionBar.with(this)
                .statusBarColor(color)
                .init();
    }




    /**
     * ?????????????????????????????????
     */
    public void setStateBarBackgroundColor(int color) {
        if (stateBar != null) {
            stateBar.setBackgroundColor(color);
        }
    }

    /**
     * ???????????????????????????alpha
     */
    public void setStateBarBackgroundAlpha(float alpha) {
        if (stateBar != null) {
            stateBar.setAlpha(alpha);
        }
    }

    /**
     * ??????????????????????????????
     */
    public void setTitleBarBackgroundColor(int color) {
        if (titleBarView != null) {
            titleBarView.setBackgroundColor(color);
        }
    }

    /**
     * ??????????????????
     */
    public void setTitleText(String title) {
        if (tv_my_title != null) {
            tv_my_title.setText(title);
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    public void setStateBarVisibility(int visibility) {
        if (stateBar != null) {
            stateBar.setVisibility(visibility);
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void setStateBarHeight(int height) {
        if (stateBar != null) {
            ViewGroup.LayoutParams layoutParams = stateBar.getLayoutParams();
            layoutParams.height = height;
            stateBar.setLayoutParams(layoutParams);
        }
    }


    /**
     * ???????????????????????????????????????
     */
    public boolean isTranslucentStatus() {
        return isTranslucentStatus;
    }

    /**
     * ??????title??????????????????
     */
    public boolean isTitleBar() {
        return isTitleBar;
    }

    /**
     * ???????????????????????????title???,?????????true
     */
    public void setIsTitleBar(boolean isTitleBar) {
        this.isTitleBar = isTitleBar;
    }


    /**
     * ??????title????????????textview
     */
    public TextView getTv_title() {
        return tv_my_title;
    }


    /**
     * ??????title??????????????????icon
     */
    public ImageView getLeftImageView() {
        return iv_my_left;
    }

    /**
     * ??????title????????????imageview
     */
    public ImageView getRightImageView() {
        return iv_right;
    }

    /**
     * ??????title????????????TextView
     */
    public TextView getRightTextView() {
        return commen_bar_tv_right;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setContentView(int layoutResID) {
        mType=getCustomStatusBarType();
        if(isRelativeStatusBar()){//??????,?????????????????????
            View view = LayoutInflater.from(this).inflate(layoutResID, null);

            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);

            initTitleBar(activityParent);//??????????????????title??????????????????,title????????????

            /***
             * ????????????????????????????????????????????????(????????????)
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
        else {//?????????????????????,??????????????????
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

    //?????????,???????????????????????????????????????view,???????????????dataBinding
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setContentView(View view) {

        mType=getCustomStatusBarType();
        if(isRelativeStatusBar()){//??????,?????????????????????

            View root = LayoutInflater.from(this).inflate(R.layout.base_activity_base_root, null);
            LinearLayout activityParent= (LinearLayout) root.findViewById(R.id.ll_activity_root);

            initTitleBar(activityParent);//??????????????????title??????????????????,title????????????

            /***
             * ????????????????????????????????????????????????(????????????)
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
        else {//?????????????????????,??????????????????
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
     * @des:??????????????????
     * @param :[]
     * @return type:void
     * @date ????????????:2018/12/5
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
     * @des:??????????????????,??????????????????
     * @param :[activityParent]
     * @return type:void
     * @date ????????????:2018/12/5
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
     * @des:??????true,?????????????????????????????????,false???????????????????????????
     * @param :[]
     * @return type:boolean
     * @date ????????????:2018/12/4
     * @author Chuck
     **/
    protected boolean isRelativeStatusBar(){
        return false;
    }
    /**
     * @method name:isRelativeStatusBar
     * @des:?????????????????????????????????, NONE:????????? WHITE:?????? BLACK:??????
     * @param :[]
     * @return type:boolean
     * @date ????????????:2018/12/4
     * @author Chuck
     **/
    protected WifiView.Type getCustomStatusBarType(){
        return WifiView.Type.NONE;
    }

    /**
     * @method name:initStatusBar
     * @des:??????????????????
     * @param :[]
     * @return type:void
     * @date ????????????:2018/12/4
     * @author Chuck
     **/
    private void initStatusBar() {

        try {
            if (isTranslucentStatus && Build.VERSION.SDK_INT >= 19) {
                //???????????????
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //?????????stateBar
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
     * @des:??????????????????????????????(?????????????????????)
     * @param :[]
     * @return type:void
     * @date ????????????:2018/12/5
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
     * @des:??????????????????????????????(?????????????????????)
     * @param :[activityParent:????????????????????????? ????????????????????????contentView?????????????????????view,????????????content???root view]
     * @return type:void
     * @date ????????????:2018/12/5
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

        //?????????
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
                .observeOn(AndroidSchedulers.mainThread())//??????????????????????????????,???????????????????????????
                .subscribe(observer));


    }


    /**
     * @method name:initStatusBar2
     * @des:??????????????????,????????????
     * @param :[]
     * @return type:void
     * @date ????????????:2018/12/4
     * @author Chuck
     **/
    private void initStatusBar2() {

        try {
            if (isTranslucentStatus && Build.VERSION.SDK_INT >= 19) {
                //???????????????
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //?????????stateBar
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppLoader.getStateBarHeight());
                stateBar = new View(this);
                stateBar.setBackgroundColor(getStatusBarColor());
                rl_container.addView(stateBar, params);//??????,?????????rl_container

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
     * @des:????????????????????????,????????????????????????
     * @date ????????????:2016/7/7
     * @author Chuck
     **/
    protected int getStatusBarColor() {
        return Color.parseColor("#ffffffff");
    }


    /**
     * **********************************************************************************************************
     * ?????????????????????
     **/
    private boolean sLastVisiable;//????????????????????????????????????  true???????????????

    /**
     * @param :[activity, listener]
     * @return type:void
     *
     * @method name:addOnSoftKeyBoardVisibleListener
     * @des:???????????????????????????????????????
     * @date ????????????:2016/7/7
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
     * @des:???????????????????????????????????????????????????,????????????
     * @date ????????????:2016/7/11
     * @author Chuck
     **/
    protected OnSoftKeyBoardVisibleListener getOnSoftKeyBoardVisibleListener() {
        return null;
    }


    /**
     * @method name:showSoftInput
     * @des:?????????????????????
     * @param :[editText]
     * @return type:void
     * @date ????????????:2016/9/3
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
     * ????????????
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
     * @des:?????????????????????????????? true????????????
     * @date ????????????:2016/7/7
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
     * @des:??????????????????????????? ?????? ????????????????????????????????????,??????????????????????????????
     * @date ????????????:2016/7/7
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
        // ??????1???MainActivity?????????????????????2???SecondActivity????????????
        //overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_left_out);
        overridePendingTransition(R.anim.base_display, R.anim.base_slide_right_out);
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        try {
            // ?????????UI???????????????
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
            //MobclickAgent.onResume(this);       //??????????????????



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
        ImmersionBar.with(this).destroy(); //??????????????????????????????????????????
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
     * @des:??????loadingview
     * @param :[]
     * @return type:com.qdong.communal.library.widget.CustomMaskLayerView.CustomMaskLayerView
     * @date ????????????:2016/10/24
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
     * @date ????????????:2017/5/13
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
                LogUtil.i(TAG,"??????????????????:"+e.getMessage());

                if(RxHelper.THROWABLE_MESSAGE_AUTO_LOGIN_FAILED.equals(e.getMessage())){//??????????????????,???????????????
                        //sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//????????????,finish???????????????
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                }
                else {
                    observer.onError(e);
                }
            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(isNeedLogin(linkLinkNetInfo.getErrorCode())){
                    //sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//????????????,finish???????????????
                    startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                }
                else if(RxHelper.ERROR_SQUEEZE_OUT.equals(linkLinkNetInfo.getErrorCode())){//????????????
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
     * @date ????????????:2017/5/13
     * @author Chuck
     **/
    public void executeTaskAutoRetry1(final Observable<LinkLinkNetInfo> observable, final Observer<LinkLinkNetInfo> observer){

        mSubscriptions.add(RxHelper.getInstance(BaseActivity.this).executeTaskAutoRetry(observable,mApi,observer, AppLoader.getInstance().getAutoLoginParameterMap()));
    }


    /**
     * ??????????????????
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
                    //Toast.makeText(this,"???????????????",Toast.LENGTH_LONG).show();
                    grant=false;
                    break;
                }
            }
            //Toast.makeText(this,grant?"??????":"??????",Toast.LENGTH_LONG).show();
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

    //???????????????,???????????????frament??????
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
                        if(commit == true){//true:??????  false :??????
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
                        if(commit == true){//true:??????  false :??????
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
     * @des:?????????????????????,???????????????.??????????????????,?????????????????????????????????.????????????,????????????????????????
     * ??????,??????onRequestPermissionsResult???,??????super?????????.
     * @date ????????????:2018/12/21
     * @author Chuck
     **/
    protected void onCameraGranted(){

    }


    /**
     * @method name:checkLocationPermission
     * @des:??????????????????,?????????,???????????????????????????????????????
     * @param :[callBack :??????, requestCodeGoToSetting:????????????????????????code,?????????onActivityResult????????????????????????]
     * @return type:void
     * @date ????????????:2019/4/25
     * @author Chuck
     **/
    protected void checkLocationPermission(PermissionCallBack2 callBack, int requestCodeGoToSetting) {

        // BUGFIX: 2019/12/20  BY:Chuck http://10.32.156.110:8080/browse/YPANDROID2-1466
        if (!BhUtils.isLocationEnabled(this)) {
            //?????????????????????0.3??????????????????????????????????????????????????????????????????????????????
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
                                    //???????????????????????????
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent,requestCodeGoToSetting);

                                } else {
                                    //???????????????,??????????????????
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

        if(isPermissionOK){//???????????????????????????
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
     * @des:???????????????????????????
     * @param :[]
     * @return type:void
     * @date ????????????:2019/4/25
     * @author Chuck
     **/
    private void showSettingDialog(PermissionCallBack2 callBack,int code) {

        CallBack callBack1 = new CallBack() {
            @Override
            public void callBack(Object object) {
                if (object !=null && object instanceof Boolean){
                    boolean commit= (boolean) object;
                    if(commit){//true:??????  false :??????
                        //ToastHelper.showCustomMessage("delete");
                        CommonUtil.jump2PermissionPager(BaseActivity.this,code);
                    }else{
                        if(callBack!=null){//?????????
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
     * @des:????????????????????????????????????????????????
     * @date ????????????:2019/3/26
     * @author jibinghao
     **/
    protected void showDeniedDialog(boolean isLocationService,int requestCode,CallBack callBack) {

        //?????????????????????0.3??????????????????????????????????????????????????????????????????????????????
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                DialogConstom mDialog = new DialogConstom(BaseActivity.this, false, true, getString(R.string.location_permission_dialog_title), getString(R.string.location_permission_dialog_content), 14, getString(R.string.location_permission_dialog_confirm_btn), getString(R.string.location_permission_dialog_cancel_btn), new CallBack() {
                    @Override
                    public void callBack(Object object) {
                        if (object != null && object instanceof Boolean) {
                            boolean commit = (boolean) object;
                            if (commit) {
                                if (isLocationService) {//????????????????????????????????????,??????????????????,??????
                                    CommonUtil.jump2PermissionPager(BaseActivity.this,requestCode);
                                } else {//?????????,??????????????????????????????,??????????????????
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent,requestCode);
                                }
                                //???????????????????????????

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
