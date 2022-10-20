package com.qdong.communal.library.module.BaseRefreshableListFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseQuickAdapter2;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.LoadMoreView2;
import com.qdong.communal.library.module.network.LinkLinkApi;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.module.network.RxHelper;
import com.qdong.communal.library.util.Constants;
import com.qdong.communal.library.util.DensityUtil;
import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.util.NetworkUtil;
import com.qdong.communal.library.widget.custommask.CustomMaskLayerView;
import com.qdong.communal.library.widget.RefreshRecyclerView.manager.RecyclerMode;
import com.qdong.communal.library.widget.refresh.CGBHeader;
import com.spc.pose.demo.R;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import linklink.com.scrollview_within_recyclerview.base.CustomBaseFragment2;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * BaseRefreshableListFragment
 * 类的描述:
 * 可刷新列表类的Fragment的基类,网络请求事件使用retrofit和RxJava实现.泛型是列表数据模型.适用场景:以get/post请求获取数据的可下拉刷新,上拉加载的界面.数据载体控件为RecyclerView,为了使子类能获取悬浮功能,所以此类继承了CustomBaseFragment2,详情见:https://github.com/506954774/ScrollviewWithinRecyclerviewAndFloatView
 * <p/>
 * 4个抽象方法,分别是:
 * {@link #getBaseUrl()}:   子类提供请求的url,注意,是baseurl
 * {@link #callApi(LinkLinkApi, int, int)}: 子类通过retrofit框架里面的方法调用接口,返回一个Observable接口实例
 * {@link #refreshData()}:  子类解析数据,
 * {@link #initAdapter()} :子类提供适配器,
 * <p/>
 * 子类还可以重写
 * {@link #getRecyclerViewItemAnimator()} :设置item插入,删除动画,默认是系统提供的
 * {@link #getRecyclerViewItemDecoration()}:设置item分割线,默认为横向的黑线
 * {@link #getRecyclerViewHeadView()}:设置recyclerView的headview,因为系统的recyclerView是不提供headview的,
 * {@link #getRecyclerViewFootView()} :设置footview
 * {@link #getRecyclerViewPullMode()} :设置刷新开关:NONE,BOTH,TOP,BOTTOM. 父类默认是BOTH
 * {@link #getRecyclerViewLayoutManager()}:设置layoutManager, 父类默认是垂直的list
 * {@link #resetAutoCancelLoadMore()}:当界面数据源的size小于或等于pageSize时,此方法的返回值决定了此时会不会触发"上拉加载更多"的事件
 * {@link #getFragmentTitleView()}:子类在listview的上面布置view
 * {@link #onInitDataResult(boolean, LinkLinkNetInfo)}:首次加载的结果回调,
 * {@link #onRefreshDataResult(boolean, LinkLinkNetInfo)}:刷新的结果回调,
 * {@link #onLoadMoreDataResult(boolean, LinkLinkNetInfo)}:加载更多的结果回调,
 * {@link #isShowLoadingFirstTime()} :首次加载时,展不展示loading画面
 * {@link #isAutoRefreshFirstTime()} :首次加载时,是否自动请求
 * {@link #onInitNoNetWork()} :首次加载就没有网络
 * <p/>
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/6/20  18:24
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public abstract class BaseRefreshableListFragment2<T> extends CustomBaseFragment2 {

    /***************************************
     * Constant
     ****************************************/
    protected static String TAG = "BaseRefreshableListFragment";
    protected static final int REQUEST_TYPR_FIRST_TIME_LOAD = 0; // 初始化数据的加载
    protected static final int REQUEST_TYPR_REFRESH = 0X258; // 是下拉刷新
    protected static final int REQUEST_TYPR_LOAD_MORE = 0X369; // 是上拉加载
    protected static final int DEFAULT_FIRST_PAGE_NUM = 1;
    protected static final int DEFAULT_MAX_PAGE_SIZE = 20;


    /******************************************
     * Widget
     *******************************************/
    protected PtrFrameLayout mPtrFrame;
    protected RecyclerView rv_list;
    protected LinearLayout mLlListContainer;
    protected View mContentView;
    protected LinearLayout mLlTitle;
    protected LinearLayout mLlBottom;
    protected LinearLayout mLlFloatContainer;//提供悬浮覆盖的可能
    protected LinearLayout mLlFloatBottomContainer;//提供底部悬浮覆盖的可能
    protected CustomMaskLayerView loadingView;//loadingView
    private LoadMoreView2 mRecyclerViewLoadMoreView;



    private MyHandler<T> mHandler = new MyHandler(this);
    protected List<T> mListData = new ArrayList<T>();
    protected int mCurrentPage = DEFAULT_FIRST_PAGE_NUM; // 默认当前第1页
    protected int mPageSize = DEFAULT_MAX_PAGE_SIZE;
    protected int mLastDataSize = 0;//上一次解析到的数据集合
    protected BaseQuickAdapter2 mAdapter;//子类提供
    private boolean mAutoCancelLoadMore;//此布尔值决定了:当数据size小于PageSize时要不要自动屏蔽上拉事件.默认不屏蔽,重写方法来设置.
    private int mCurrentRequestType;//当前的请求类型(首次加载,刷新,加载更多)
    protected Bundle savedInstanceState;

    /************************************************
     * RetroFit RxJava
     **********************************************/
    private CustomObserver mObserver;//观察者
    protected LinkLinkApi mApi;//服务器接口,使用注解
    private Subscription subscription;//订阅,在界面销毁时要注意调用unSubscrib
    protected boolean mIsLoading = false;//正在发网络请求
    protected RecyclerMode mRecyclerMode;//界面刷新类型
    private boolean mIsFirstTime=true;//是否首次刷新
    protected View mRecyclerViewHeadView;//提取为成员变量
    protected View mRecyclerViewEmptyView;
    protected View mRecyclerViewErrorView;
    private boolean firstTimeLoadMore=true;//首次loadMore

    public LinkLinkApi getmApi() {
        return mApi;
    }

    /**
     * @param :[pageSize]
     * @return type:void
     * @method name:setPageSize
     * @des:设置请求页面最大值
     * @date 创建时间:2016/5/10
     * @author Chuck
     **/
    public void setPageSize(int pageSize) {
        this.mPageSize = pageSize;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;


        if (mContentView == null) {
            //mContentView = View.inflate(getActivity(), R.layout.fragment_base_list, null);

            mContentView = inflater.inflate(R.layout.fragment_base_list2, container, false);

            init(mContentView);
            //firstTimeLoad();
        } else {
            // 不为null时，需要把自身从父布局中移除，因为ViewPager会再次添加
            ViewParent parent = mContentView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(mContentView);
            }
        }
        return mContentView;
    }

    /**
     * @param :[view]
     * @return type:void
     * @method name:initView
     * @des:初始化
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    private void init(View view) {

        initRetroFit_RxJava();
        initView(view);


    }

    /**
     * @param :[view]
     * @return type:void
     * @method name:initView
     * @des:初始化控件
     * @date 创建时间:2016/6/25
     * @author Chuck
     **/
    private void initView(View view) {

        mAutoCancelLoadMore = resetAutoCancelLoadMore();


        loadingView = (CustomMaskLayerView) view.findViewById(R.id.loading_view);
        loadingView.setTransparentMode(CustomMaskLayerView.STYLE_TRANSPARENT_ON);

        //默认隐藏
        loadingView.dismiss();



        mPtrFrame= (PtrFrameLayout) view.findViewById(R.id.material_style_ptr_frame);
        rv_list= (RecyclerView) view.findViewById(R.id.rv_list);
        mLlFloatContainer = (LinearLayout) view.findViewById(R.id.ll_head_container);
        mLlListContainer = (LinearLayout) view.findViewById(R.id.ll_list_container);
        mLlTitle = (LinearLayout) view.findViewById(R.id.ll_title);
        mLlBottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
        mLlFloatBottomContainer = (LinearLayout) view.findViewById(R.id.ll_bottom_container);
        View title=getFragmentTitleView();
        View bottom=getFragmentBottomView();
        if (title != null) {
            mLlTitle.addView(title);
        }
        if(bottom != null){
            mLlBottom.addView(bottom);
        }
        setPageSize(resetPageSize());//子类可以重写

        /****集成益拍原来的刷新动画 by:Chuck  start ***********************/

        //纯本地数据,则不执行刷新动画
        if(getRecyclerViewPullMode()!=RecyclerMode.NONE){
            final CGBHeader header = new CGBHeader(getContext());
            header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
            header.setPadding(0, DensityUtil.dp2px(getActivity(), 5), 0, DensityUtil.dp2px(getActivity(), 10));
            mPtrFrame.setHeaderView(header);
            mPtrFrame.addPtrUIHandler(header);
            mPtrFrame.disableWhenHorizontalMove(true);
            mPtrFrame.setPtrHandler(new PtrHandler() {
                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    if(mIsFirstTime){
                        firstTimeLoad();
                        mIsFirstTime=false;
                    }
                    else{
                        pullDownAction();
                    }
                }

                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, rv_list, header);
                }
            });

            //如果需要一开始就自动加载,则200毫秒后加载
            if(isAutoRefreshFirstTime()){
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 防止 Activity 重建 导致 秘制空指针
                        if (mPtrFrame != null) {
                            mPtrFrame.autoRefresh();
                        }
                    }
                }, 200);
            }
        }
        /****集成益拍原来的刷新动画    end************************/

        if (getRecyclerViewItemDecoration() != null) {
            rv_list.addItemDecoration(getRecyclerViewItemDecoration());//设置item的分割线
        }
        if (getRecyclerViewItemAnimator() != null) {
            rv_list.setItemAnimator(getRecyclerViewItemAnimator());//设置item的动画
        }


        mAdapter = initAdapter();//初始化adapter

        mAdapter.setHeaderAndEmpty(whenEmptyShowHeaderView());

        mAdapter.setHeaderFooterEmpty(whenEmptyShowHeaderView(),whenEmptyShowFootView());

        //抽出为成员变量,子类直接使用
        mRecyclerViewHeadView=getRecyclerViewHeadView();
        if (mRecyclerViewHeadView != null) {
            mAdapter.addHeaderView(mRecyclerViewHeadView);//加头
        }



        View RecyclerViewFootView=getRecyclerViewFootView();
        if (RecyclerViewFootView != null) {
            mAdapter.addFooterView(RecyclerViewFootView);//加尾
        }

        //底部悬浮的bottomView
        View RecyclerViewFloatBottomView=getRecyclerViewFloatBottomView();

        if (RecyclerViewFloatBottomView != null) {

            LogUtil.e(TAG,"准备给悬浮bottom添加布局");
            mLlFloatBottomContainer.addView(RecyclerViewFloatBottomView);
            LogUtil.e(TAG,"添加完成");

            ViewTreeObserver viewTreeObserver = mLlFloatBottomContainer.getViewTreeObserver();
            if (viewTreeObserver != null) {//监听第一个的全局layout事件，来设置当前的mCurrentPosition，显示对应的tab
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        LogUtil.e("TabWithoutViewPager","OnGlobalLayoutListener()");

                        mLlFloatBottomContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);//只需要监听一次，之后通过listener回调即可

                        int height=mLlFloatBottomContainer.getHeight();

                        LogUtil.e(TAG, "动态设置margin:" + height);

                        if(height>0){
                            mLlListContainer.setPadding(0,0,0,height);
                        }
                    }
                });
            }
        }


        //setLoadMoreMode();


        rv_list.setLayoutManager(getRecyclerViewLayoutManager());//加布局管理器

        setmRecyclerMode(getRecyclerViewPullMode());//刷新模式



        // TODO: 2017/9/9 上拉,下拉,这里后续可以拓展,某些界面如果不允许下拉或上拉,则加变量去控制 by:Chuck
        if (getRecyclerViewPullMode() == RecyclerMode.BOTH) {

        } else if (getRecyclerViewPullMode() == RecyclerMode.TOP) {

        } else if (getRecyclerViewPullMode() == RecyclerMode.BOTTOM) {

        }

        //mAdapter.setHeaderAndEmpty(true);


        rv_list.setAdapter(mAdapter);

    }

    public void setEmptyView() {
        //局部变量先存,避免连续调用两次
        if(mRecyclerViewEmptyView==null){
            mRecyclerViewEmptyView=getRecyclerViewEmptyView();
        }
        if (mRecyclerViewEmptyView!= null) {
            mAdapter.setEmptyView(mRecyclerViewEmptyView);//加空布局
        }
    }

    public void setErrorView() {
        //局部变量先存,避免连续调用两次
        if(mRecyclerViewErrorView==null){
            mRecyclerViewErrorView=getRecyclerViewErrorView();
        }
        if (mRecyclerViewErrorView!= null) {
            mAdapter.setEmptyView(mRecyclerViewErrorView);//加空布局
        }
    }

    private void setLoadMoreMode() {
        //局部变量先存,避免连续调用两次

        if(mRecyclerViewLoadMoreView==null){
            mRecyclerViewLoadMoreView=getRecyclerViewLoadMoreView();
        }
        if (mRecyclerViewLoadMoreView!= null&&(getRecyclerViewPullMode() == RecyclerMode.BOTH||getRecyclerViewPullMode() == RecyclerMode.BOTTOM)) {
            mAdapter.setLoadMoreView(mRecyclerViewLoadMoreView);//加载更多
            mAdapter.setEnableLoadMore(true);
            BaseQuickAdapter2.RequestLoadMoreListener listener=new BaseQuickAdapter2.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!enableLoadmoreDirectly()&&mLastDataSize<mPageSize){
                                getRecyclerViewLoadMoreView().setLoadMoreEndGone(false);
                                mAdapter.loadMoreEnd(true);//没有更多
                                mPtrFrame.refreshComplete();
                            }
                            else{
                                pullUpAction();
                            }
                        }
                    },1000);//转一秒再刷


                }
            };
            mAdapter.setOnLoadMoreListener(listener);
        }


    }


    public void setmRecyclerMode(RecyclerMode mRecyclerMode) {
        this.mRecyclerMode = mRecyclerMode;
    }

    protected void continueSearch(String s){

    }


    /**
     * @method name:onAuthFailed
     * @des:重试登录失败
     * @param :[]
     * @return type:void
     * @date 创建时间:2019/1/23
     * @author Chuck
     **/
    protected void onAuthFailed(){

    }

    private void sendBroadcast(String errorCode) {
        Intent intent=new Intent(Constants.ACTION_FINISH_ALL_CASE_AUTH);
        //intent.putExtra(Constants.INTENT_KEY_ERRORCODE,errorCode);
        getActivity().sendBroadcast(intent);//发出广播,finish掉所有界面
    }

    /**
     * CustommObserver
     * 自定义的观察者,处理网络请求,结果封装在netInfo里,请求类型取他的actionType 有三个:首次加载,刷新,加载更多
     * 责任人:  Chuck
     * 修改人： Chuck
     * 创建/修改时间: 2016/6/25  22:11
     * Copyright : 趣动智能科技有限公司-版权所有
     **/
    class CustomObserver implements Observer<LinkLinkNetInfo> {
        @Override
        public void onCompleted() {
            mIsLoading = false;//当前没有在发请求
            LogUtil.e("RxJava", "onCompleted(),线程id:" + Thread.currentThread().getId());
            //Toast.makeText(getActivity(),"onCompleted(),线程id:"+Thread.currentThread().getId(), Toast.LENGTH_SHORT).show();
            onComplete();
        }

        @Override
        public void onError(Throwable e) {
            LogUtil.e("RxJava", "onError(),线程id:" + Thread.currentThread().getId() + ",异常:" + e.getMessage());

            //被其他用户挤掉,重新登陆时,如果出错,会走onError
            if( Constants.SESSION_ERROR_PUSH.equals(e.getMessage())|| Constants.SESSION_ERROR_PUSH2.equals(e.getMessage())){
                sendBroadcast(e.getMessage());
            }
            //token过期,重试无效,需要重新登录
            else if( RxHelper.THROWABLE_MESSAGE_AUTO_LOGIN_FAILED.equals(e.getMessage())){
                //sendBroadcast(e.getMessage());
                mPtrFrame.refreshComplete();
                onAuthFailed();
            }
            else{//其他的普通错误
                //ToastUtils.showShortToast(getActivity(),getString(R.string.common_net_error));
                //ToastUtil.showCustomMessage(getActivity(),getString(R.string.order_list_network_error));
                setErrorView();
                mIsLoading = false;//当前没有在发请求
                //Toast.makeText(getActivity(), "onError(),线程id:"+Thread.currentThread().getId()+",异常:"+ e.toString(), Toast.LENGTH_SHORT).show();

                //loadingView.showError();
                mPtrFrame.refreshComplete();

                switch (mCurrentRequestType) {
                    case REQUEST_TYPR_FIRST_TIME_LOAD://首次加载数据
                        onInitDataResult(false,null);
                        break;
                    case REQUEST_TYPR_REFRESH://刷新
                        onRefreshDataResult(false,null);
                        break;
                    case REQUEST_TYPR_LOAD_MORE://加载更多
                        mCurrentPage--;
                        onLoadMoreDataResult(false,null);
                        break;
                }
            }
        }

        @Override
        public void onNext(LinkLinkNetInfo info) {

            //用户被挤掉
           /* if( Constants.SESSION_ERROR_PUSH.equals(info.getErrorCode())|| Constants.SESSION_ERROR_PUSH2.equals(info.getErrorCode())){
                sendBroadcast(info.getErrorCode());//发出广播,finish掉所有界面
                return;
            }*/

            mLastDataSize=0;
            mIsLoading = false;//当前没有在发请求
            LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info:" + info.toString());
            //Toast.makeText(getActivity(), "onNext(),线程id:"+Thread.currentThread().getId()+",info:"+ info.toString(), Toast.LENGTH_SHORT).show();

            if (info == null) {
                //loadingView.dismiss();
                mPtrFrame.refreshComplete();
            } else {

                LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.getActionType():" + info.getActionType());
                LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.isSuccess():" + info.isSuccess());
                LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.getResult()!=null:" + (info.getData() != null));
                LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.getResult():" + info.getData());

                switch (info.getActionType()) {
                    case REQUEST_TYPR_FIRST_TIME_LOAD://首次加载数据

                       // loadingView.dismiss();
                        if (info.isSuccess()) {
                            if (info.getData() != null) {
                                try {


                                    List<T> list = resolveData(RefreshMode.FIRST_TIME_LOAD,info.getData());

                                    LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.getResult().toString:" + info.getData().toString());

                                    if (list == null || list.size() == 0) {
                                       // loadingView.showNoContent();
                                        setEmptyView();

                                        mListData.clear();
                                        mListData = list;
                                        mAdapter.setNewData(mListData);
                                        onInitDataResult(true,info);
                                    } else {
                                        //强制开启,或者只有满了,才开启加载更多
                                        if(enableLoadmoreDirectly()||(list!=null&&list.size()>=mPageSize)){
                                            setLoadMoreMode();
                                        }
                                        mLastDataSize=list.size();
                                        mListData.clear();
                                        mListData = list;
                                        mAdapter.setNewData(mListData);
                                        onInitDataResult(true,info);
                                    }

                                    mPtrFrame.refreshComplete();
                                } catch (Exception e) {
                                   // loadingView.showError();
                                    mPtrFrame.refreshComplete();

                                    //没有数据,则清空
                                    setEmptyView();

                                    mLastDataSize=0;
                                    mListData.clear();
                                    mAdapter.notifyDataSetChanged();
                                    onInitDataResult(true,info);
                                    e.printStackTrace();
                                }
                            } else {//result返回为空
                               // loadingView.showNoContent();
                                setEmptyView();

                                mListData.clear();
                                mAdapter.notifyDataSetChanged();
                                mPtrFrame.refreshComplete();
                                onInitDataResult(true,info);
                            }
                        } else {
                           // loadingView.showError();
                            mPtrFrame.refreshComplete();
                            onInitDataResult(false,info);
                        }

                        break;
                    case REQUEST_TYPR_REFRESH://刷新

                       // loadingView.dismiss();
                        if (info.isSuccess()) {
                            if (info.getData() != null) {
                                try {

                                    List<T> list = resolveData(RefreshMode.REFRESH,info.getData());
                                    mListData.clear();
                                    mListData = list;
                                    mLastDataSize=list.size();
                                    if (mListData.size() == 0) {//如果刷新成功后没有数据了,则显示没有内容视图
                                        //loadingView.showNoContent();
                                        setEmptyView();

                                    }

                                    //只有满了,才开启加载更多
                                    if(list!=null&&list.size()>=mPageSize){
                                        //setLoadMoreMode();
                                    }

                                    mAdapter.setNewData(mListData);
                                    mPtrFrame.refreshComplete();
                                } catch (Exception e) {
                                    //loadingView.showError();
                                    mPtrFrame.refreshComplete();

                                    //没有数据,则清空
                                    setEmptyView();

                                    mLastDataSize=0;
                                    mListData.clear();
                                    mAdapter.notifyDataSetChanged();
                                    e.printStackTrace();
                                }
                            } else {//result为空
                                setEmptyView();

                                mListData.clear();
                                mAdapter.setNewData(mListData);
                                mPtrFrame.refreshComplete();
                                //loadingView.showNoContent();
                            }


                            onRefreshDataResult(true,info);
                        } else {
                            mPtrFrame.refreshComplete();
                            //loadingView.showError();
                            onRefreshDataResult(false,info);
                        }

                        break;
                    case REQUEST_TYPR_LOAD_MORE://加载更多

                        //loadingView.dismiss();
                        if (info.isSuccess()) {
                            if (info.getData() != null ) {
                                try {
                                    List<T> list = resolveData(RefreshMode.LOAD_MORE,info.getData());
                                    if (list == null || list.size() == 0) {
                                        mAdapter.loadMoreEnd();//没有更多
                                        mPtrFrame.refreshComplete();
                                        mCurrentPage--;

                                    } else {
                                        mLastDataSize=list.size();
                                        mListData.addAll(list);
                                        mAdapter.setNewData(mListData);
                                        mAdapter.loadMoreComplete();
                                        mPtrFrame.refreshComplete();
                                    }

                                } catch (Exception e) {
                                    mAdapter.loadMoreEnd();//没有更多
                                    mAdapter.loadMoreComplete();
                                    mPtrFrame.refreshComplete();
                                    mCurrentPage--;
                                    e.printStackTrace();
                                }
                            } else {
                                mAdapter.loadMoreEnd();//没有更多
                                mAdapter.loadMoreComplete();
                                mPtrFrame.refreshComplete();
                                mCurrentPage--;
                            }

                            onLoadMoreDataResult(true,info);
                        } else {
                            mAdapter.loadMoreEnd();//没有更多
                            mAdapter.loadMoreComplete();
                            mPtrFrame.refreshComplete();
                            onLoadMoreDataResult(false,info);
                            //loadingView.showError();
                            mCurrentPage--;
                        }

                        break;

                    default:
                        break;
                }
            }

        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:initRetroFit_RxJava
     * @des:请求框架
     * @date 创建时间:2016/6/25
     * @author Chuck
     **/
    private void initRetroFit_RxJava() {

      /*  mObserver=new mObserver<NetInfo>(){
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(NetInfo info) {
                Toast.makeText(getActivity(),info.toString(),Toast.LENGTH_LONG).show();

            }
        };*/

        mObserver = new CustomObserver();


        if (TextUtils.isEmpty(getBaseUrl())) {
            mApi = RetrofitAPIManager.provideClientApi(getActivity());//定义出来,因为在使用自动登录获取最新token时要用到
        } else {
            mApi = RetrofitAPIManager.provideClientApi(getActivity(), getBaseUrl());//定义出来,因为在使用自动登录获取最新token时要用到
        }

    }

    /**
     * @param :[]
     * @return type:void
     * @method name:pullUpAction
     * @des:上拉
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    private void pullUpAction() {

        if (NetworkUtil.checkNetWorkWithToast(getActivity())) {//有网
            loadingMoreData(); // 上拉加载更多
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrame.refreshComplete();
                }
            }, 1000);
        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:pullDownAction
     * @des:下拉
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    private void pullDownAction() {



        if (NetworkUtil.checkNetWorkWithToast(getActivity())) {//有网
            refreshData(); // 下拉刷新
        } else {

            setErrorView();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrame.refreshComplete();
                }
            }, 1000);

        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:firstTimeLoad
     * @des:首次加载数据,这个无需参数,或者参数是默认值.
     * @date 创建时间:2016/4/20
     * @author Chuck
     **/
    public void firstTimeLoad() {
        // 判断是否有网络,没有网络的话显示网络异常
        if (!NetworkUtil.hasNetWork(getActivity())) {
            //loadingView.showError();
            onInitNoNetWork();
        } else {
            if (!mIsLoading) {
                mCurrentPage = DEFAULT_FIRST_PAGE_NUM;//页码回归
                getDataFromServer(isShowLoadingFirstTime(), mCurrentPage, REQUEST_TYPR_FIRST_TIME_LOAD);
                mIsLoading = true;
            }
        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:firstTimeLoad
     * @des:刷新
     * @date 创建时间:2016/4/20
     * @author Chuck
     **/
    public void refresh() {
        // 判断是否有网络,没有网络的话显示网络异常
        if (!NetworkUtil.hasNetWork(getActivity())) {
            //loadingView.showError();
            onInitNoNetWork();
        } else {
            if (!mIsLoading) {
                mCurrentPage = DEFAULT_FIRST_PAGE_NUM;//页码回归
                getDataFromServer(true, mCurrentPage, REQUEST_TYPR_REFRESH);
                mIsLoading = true;
            }
        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:onInitNoNetWork
     * @des:当首次进来就没有网络,会调用此方法
     * @date 创建时间:2016/9/10
     * @author Chuck
     **/
    protected void onInitNoNetWork() {
        mPtrFrame.refreshComplete();
        setErrorView();
    }

    /**
     * @param :[]
     * @return type:boolean
     * @method name:isShowLoadingFirstTime
     * @des:首次加载时是否展示loading画面
     * @date 创建时间:2016/8/24
     * @author Chuck
     **/
    protected boolean isShowLoadingFirstTime() {
        return true;
    }

    /**
     * @param :[]
     * @return type:boolean
     * @method name:isAutoRefreshFirstTime
     * @des:初始化时是否自动加载数据
     * @date 创建时间:2016/8/24
     * @author Chuck
     **/
    protected boolean isAutoRefreshFirstTime() {
        return true;
    }

    /**
     * @return type:void
     * @des: 下拉刷新, 这个没有公开, 是因为某些界面是改变了某些参数再刷新的,
     * 此时要在子fragment里定义方法并接受接口参数(比如某种查询条件),供activity调用.因此这里不做公开
     * @date 创建时间：2015-8-10
     * @author Chuck
     */
    protected void refreshData() {
        refreshData(false);
    }

    /**
     * @return type:void
     * @des: 下拉刷新, 这个没有公开, 是因为某些界面是改变了某些参数再刷新的,
     * 此时要在子fragment里定义方法并接受接口参数(比如某种查询条件),供activity调用.因此这里不做公开
     * @date 创建时间：2015-8-10
     * @author Chuck
     */
    protected void refreshData(boolean showLoading) {
        if (!mIsLoading) {
            mCurrentPage = DEFAULT_FIRST_PAGE_NUM;//页码回归
            getDataFromServer(showLoading, mCurrentPage, REQUEST_TYPR_REFRESH);
            mIsLoading = true;
        }
    }

    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getFragmentTitleView
     * @des:子类listview上面的view
     * @date 创建时间:2016/7/6
     * @author Chuck
     **/
    protected View getFragmentTitleView() {
        return null;
    }
    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getFragmentBottomView
     * @des:子类listview下面的view
     * @date 创建时间:2016/7/6
     * @author Chuck
     **/
    protected View getFragmentBottomView() {
        return null;
    }

    /**
     * @return type:void
     * @des: 上拉加载
     * @date 创建时间：2015-8-10
     */
    protected void loadingMoreData() {
        // TODO: 2016/6/21 因为界面可能有手动删除的业务,故通过子类来设置,默认是不加这个判断的
        if (mAutoCancelLoadMore) {//数据不足pageSize时是否屏蔽上拉事件
            if (!enableLoadmoreDirectly()&&mLastDataSize < mPageSize) {//没有强制开启加载更多,而且数据不足size,则不触发加载更多的接口
                //mRefreshRecyclerView.onNoMoreData();
                mAdapter.loadMoreEnd();
                return;
            }
        }

        if (!mIsLoading) {//某一时间只允许一个请求
            mCurrentPage++;//页码加1
            getDataFromServer(false, mCurrentPage, REQUEST_TYPR_LOAD_MORE);
            mIsLoading = true;
        }

    }

    /**
     * @method name:enableLoadmoreDirectly
     * @des:返回值决定了:是否 不加任何判断,直接开启加载更多的功能.默认不开启
     * @param :[]
     * @return type:boolean
     * @date 创建时间:2019/2/28
     * @author Chuck
     **/
    protected boolean enableLoadmoreDirectly(){
        return false;
    }

    /**
     * @param :[]
     * @return type:android.support.v7.widget.RecyclerView
     * @method name:getRecyclerView
     * @des:子类获取recyclerview
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerView getRecyclerView() {
        return rv_list;
    }


    /**
     * @param :[isShowLoadingView, page, requestType]
     * @return type:void
     * @method name:getDataFromServer
     * @des:发请求
     * @date 创建时间:2016/5/10
     * @author Chuck
     **/
    private void getDataFromServer(boolean isShowLoadingView, int page, final int requestType) {

       /* if (isShowLoadingView) { // 是否显示加载框
            if (loadingView != null) {
                loadingView.showLoading();
            }
        } else {
            if (loadingView != null) {
                loadingView.dismiss();
            }
        }*/

        mCurrentRequestType = requestType;

        subscription = callApi(mApi, mCurrentPage, mPageSize)//子类实现
                .subscribeOn(Schedulers.io())//指定被观察者的执行线程
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程,因为如果正常反回了,我要将事件直接交给观察者,观察者通常在主线程
                .flatMap(RxHelper.getInstance(getActivity()).judgeSessionExpired(mObserver, requestType))//判断session是否过期,如果过期了,会抛出异常到事件流里
                .observeOn(Schedulers.io())//切换到子线程,执行retryWhen,里面有网络请求
                .retryWhen(RxHelper.getInstance(getActivity()).judgeRetry(mApi, getAutoLoginParameterMap()))//触发retry
                .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程

               /* .doOnNext(new Action1<QDongNetInfo>() {//在子线程将请求来的数据改动,此处封装的是请求类型:首次加载/刷新/加载
                    @Override
                    public void call(QDongNetInfo netInfo) {
                        try {
                            netInfo.setActionType(requestType);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })*/

                .subscribe(mObserver);//订阅


    }


    public List<T> getmListData() {
        return mListData;
    }

    public void setmListData(List<T> mListData) {
        this.mListData = mListData;
    }


    /**
     * MyHandler
     * 继承httpHandler,泛型是列表数据模型
     * 责任人:  Chuck
     * 修改人： Chuck
     * 创建/修改时间: 2016/5/10  11:14
     * Copyright : 2014-2015 深圳掌通宝科技有限公司-版权所有
     **/
    private static class MyHandler<F> extends Handler {
        WeakReference<BaseRefreshableListFragment2> mActivityReference;

        public MyHandler(BaseRefreshableListFragment2 fragment) {
            mActivityReference = new WeakReference<BaseRefreshableListFragment2>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }


    /**
     * @param :
     * @return type:
     * @method name: setAdapterData
     * @des: 设置适配器数据
     * @date 创建时间：2016/2/19 13:40
     * @author hujie
     */
    protected void setAdapterData(List<T> data) {
        if (mAdapter != null) {
            mAdapter.setNewData(data);
        }
    }

    /**
     * @param :[data]
     * @return type:void
     * @method name:addData
     * @des:adapter添加数据
     * @date 创建时间:2016/6/17
     * @author Chuck
     **/
    protected void addDatas(List<T> data) {
        if (mAdapter != null) {
            mAdapter.addData(data);
        }
    }

    /**
     * @param :[data]
     * @return type:void
     * @method name:addData
     * @des:adapter添加数据
     * @date 创建时间:2016/6/17
     * @author Chuck
     **/
    protected void addData(T data) {
        if (mAdapter != null) {
            mAdapter.addData(data);
        }
    }

    /**
     * @param :[isAuto]
     * @return type:void
     * @method name:resetAutoCancelLoadMore
     * @des:重写这个来决定,集合size小于pageSize时是否屏蔽上拉,父类默认不屏蔽
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected boolean resetAutoCancelLoadMore() {
        return false;
    }

    /**
     * @param :[]
     * @return type:android.support.v7.widget.RecyclerView.ItemAnimator
     * @method name:getItemAnimator
     * @des:子类可以重写,以设置item的插入,删除动画
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerView.ItemAnimator getRecyclerViewItemAnimator() {
        return new DefaultItemAnimator();
    }


    /**
     * @param :[]
     * @return type:android.support.v7.widget.RecyclerView.ItemDecoration
     * @method name:getItemDecoration
     * @des:子类可以重写,以设置item分割线
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerView.ItemDecoration getRecyclerViewItemDecoration() {
        return new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
    }

    /**
     * @param :[]
     * @return type:android.support.v7.widget.RecyclerView.LayoutManager
     * @method name:getRecyclerViewLayoutManager
     * @des:子类可以重写,以修改recyclerView的布局类型,是垂直list,还是水平list,或者gridview
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }


    /**
     * @param :[]
     * @return type:com.qdong.communal.library.widget.RefreshRecyclerView.manager.RecyclerMode
     * @method name:getRecyclerPullMode
     * @des:设置滑动模式: NONE, BOTH, TOP, BOTTOM
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerMode getRecyclerViewPullMode() {
        return RecyclerMode.BOTH;
    }


    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getHeadView
     * @des:为recyclerView添加headview
     * @date 创建时间:2016/6/20
     * @author Chuck
     **/
    protected View getRecyclerViewHeadView() {
        return null;
    }

    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getRecyclerViewEmptyView()
     * @des:为recyclerView添加空布局
     * @date 创建时间:2016/6/20
     * @author Chuck
     **/
    protected View getRecyclerViewEmptyView() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.recyclerview_empty_view2,null);
        TextView textView = view.findViewById(R.id.tv_empty);
        textView.setText(getEmptyContent());
        return view;
    }

    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getRecyclerViewEmptyView()
     * @des:为recyclerView添加空布局
     * @date 创建时间:2016/6/20
     * @author Chuck
     **/
    protected View getRecyclerViewErrorView() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.recyclerview_error_view,null);
        return view;
    }

    /**
     * 显示emptyView的文本
     * @return
     */
    protected String getEmptyContent() {

        return "";
    }

    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getFootView
     * @des:为recyclerView提供footview
     * @date 创建时间:2016/6/20
     * @author Chuck
     **/
    protected View getRecyclerViewFootView() {
        return null;
    }


    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getRecyclerViewFloatBottomView
     * @des:为recyclerView提供悬浮的bottomview
     * @date 创建时间:2016/6/20
     * @author Chuck
     **/
    protected View getRecyclerViewFloatBottomView() {
        return null;
    }
    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getRecyclerViewPullMode
     * @des:为recyclerView提供加载更多的布局
     * @date 创建时间:2016/6/20
     * @author Chuck
     **/
    protected LoadMoreView2 getRecyclerViewLoadMoreView() {
        return null;
    }


    /**
     * @param :[]
     * @return type:void
     * @method name:unSubscribe
     * @des:注销监听,在fragment onDestroy记得调用
     * @date 创建时间:2016/6/25
     * @author Chuck
     **/
    protected void unSubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    /**
     * 重写此方法来给fragment来处理初始化结果
     *
     * @param isSuccessfuly 首次加载数据是否成功
     * @param info 服务器返回值,可能为空
     */
    protected void onInitDataResult(boolean isSuccessfuly,LinkLinkNetInfo info) {
    }

    /**
     * 重写此方法来给fragment来处理刷新结果
     *
     * @param isSuccessfuly 刷新数据是否成功
     * * @param info 服务器返回值,可能为空

     */
    protected void onRefreshDataResult(boolean isSuccessfuly,LinkLinkNetInfo info) {
    }

    /**
     * 重写此方法来给fragment来处理加载更多的结果
     *
     * @param isSuccessfuly 加载更多数据是否成功
     *                           * @param info 服务器返回值,可能为空

     */
    protected void onLoadMoreDataResult(boolean isSuccessfuly,LinkLinkNetInfo info) {
    }

    /**
     * 重写此方法来给fragment重置单页数据最大值
     *
     * 重置单页数据最大值
     */
    protected int resetPageSize() {
        return mPageSize;
    }


    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            unSubscribe();



            for (Subscription s : mSubscriptions) {
                LogUtil.e("Subscription", s);
                if (s != null && !s.isUnsubscribed()) {
                    s.unsubscribe();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 调用API,子类自己实现
     *
     * @param api
     * @param currentPage
     * @param pageSize
     */
    public abstract Observable<LinkLinkNetInfo> callApi(LinkLinkApi api, int currentPage, int pageSize);

    /***
     * 实现此方法提供请求的baseUrl
     *
     * @return
     */
    public abstract String getBaseUrl();

    /**
     * 实现此方法解析数据,参数refreshMode表示当前是首次加载,刷新,或加载更多
     */
    public abstract List<T> resolveData(RefreshMode refreshMode, JsonElement jsonStr) throws JSONException;


    /**
     * 实现此方法初始化适配器
     */
    public abstract BaseQuickAdapter2 initAdapter();


    /**
     * 子类必须提供自动登录所需的参数map
     */
    public abstract HashMap<String, String> getAutoLoginParameterMap();


    protected ArrayList<Subscription> mSubscriptions = new ArrayList<>();

    public ArrayList<Subscription> getmSubscriptions() {
        return mSubscriptions;
    }

    /**
     * 操作完成，子类可重写此方法
     */
    protected void onComplete() {
    }

    /**
     * @method name:whenEmptyShowHeaderView
     * @des:当没有数据时,是否展示headview
     * @param :[]
     * @return type:boolean
     * @date 创建时间:2018/6/7
     * @author Chuck
     **/
    protected boolean whenEmptyShowHeaderView() {
        return true;
    }

    /**
     * @method name:whenEmptyShowFootView
     * @des:当没有数据时,是否展示footview
     * @param :[]
     * @return type:boolean
     * @date 创建时间:2018/6/7
     * @author Chuck
     **/
    protected boolean whenEmptyShowFootView() {
        return true;
    }

    /**
     * @method name:isHeadviewAtTopNow
     * @des:为了使子类能够使用 'com.linklink.views:ScrollviewWithinRecyclerviewAndFloatView:1.0.5'里的功能,所以,顶层父类继承了
     * @param :[]
     * @return type:boolean
     * @date 创建时间:2018/12/4
     * @author Chuck
     **/
    public   boolean isHeadviewAtTopNow(){
        return false;
    }

    //滑到指定位置
    public void setSelection(int index){
        rv_list.scrollToPosition(index);
    }

    //设置背景颜色
    public void setParentBackoundColor(int color){
        mPtrFrame.setBackgroundColor(getContext().getResources().getColor(color));
    }
}
