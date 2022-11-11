package com.ilinklink.tg.demo.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonElement;

import com.ilinklink.tg.base.CustomBaseRefreshableFragment2;
import com.ilinklink.tg.entity.RecommendCourse;

import com.ilinklink.tg.utils.Json;
import com.ilinklink.tg.utils.ToastHelper;
import com.qdong.communal.library.module.BaseRefreshableListFragment.RefreshMode;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseQuickAdapter2;
import com.qdong.communal.library.module.network.LinkLinkApi;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.ilinklink.app.fw.BR;
import com.ilinklink.app.fw.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;

/**
 * FileName: CollectCourseListFragment
 * <p>
 * Author: WuJH
 * <p>
 * Date: 2020/10/14 18:24
 * <p>
 * Description:
 */
public class CollectCourseListFragment extends CustomBaseRefreshableFragment2{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setParentBackoundColor(R.color.cor_F4F4F4);
        return view;
    }

    @Override
    protected void onInitDataResult(boolean isSuccessfuly, LinkLinkNetInfo info) {
        loadingView.dismiss();
        fackData();
    }

    @Override
    protected void fackData() {
        List<RecommendCourse.CourseListBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            RecommendCourse.CourseListBean bean=new RecommendCourse.CourseListBean();
            bean.setCourseId(String.valueOf(i+1));
            bean.setCourseNo(String.valueOf(i+1));
            bean.setCourseName("课题"+String.valueOf(i+1));
            bean.setCourseCover("https://pics4.baidu.com/feed/0e2442a7d933c8957dd7ebda7f1839fa830200ff.png?token=71c83ea57631c1e7a30d1cf5719a8fba");
            bean.setCollectCount(i+1);
            bean.setCompleteCount(i+1);
            bean.setTrainDegree("25");
            bean.setTrainPlace("大的");
            bean.setTrainTarget("肌肉");
            bean.setTrainDuration("253");
            bean.setTrainId(String.valueOf(i+1));
            bean.setTrainScore(String.valueOf(i+1));
            bean.setCompleteTime("2022-01-"+String.valueOf(i+1));
            bean.setCourseRemove(0);
            bean.setCourseOnSale(0);
            list.add(bean);
        }
        setAdapterData(list);
    }

    @Override
    public Observable<LinkLinkNetInfo> callApi(LinkLinkApi api, int currentPage, int pageSize) {

        Map<String, Object> map=new HashMap<>();
        map.put("platform",1);
        map.put("pageIndex",currentPage);
        map.put("pageSize",20);
        return mApi.getCollectList(RetrofitAPIManager.ACCESSTOKEN,map);
    }

    @Override
    public List resolveData(RefreshMode refreshMode, JsonElement jsonStr) throws JSONException {
        List<RecommendCourse.CourseListBean> list = new ArrayList<>();
        RecommendCourse recommendCourse = Json.fromJson(jsonStr,RecommendCourse.class);
        if(recommendCourse != null && recommendCourse.getCount()>0){
            list.addAll(recommendCourse.getCourseList());
           //return list;
        }
        return list;
    }

    @Override
    public BaseQuickAdapter2 initAdapter() {

        CollectCourseAdapter collectCourseAdapter = new CollectCourseAdapter(new ArrayList<RecommendCourse.CourseListBean>(), R.layout.item_collect_course, BR.ListBean);

        collectCourseAdapter.setOnCourseClickListener(new CollectCourseAdapter.OnCourseClickListener() {
            @Override
            public void onCourseTrainClickListener(RecommendCourse.CourseListBean courseBean) {
                trainCourse(courseBean.getCourseId());
            }

            @Override
            public void onCourseFavorClickListener(RecommendCourse.CourseListBean courseBean) {
                cancelCollectCourse(courseBean.getCourseId());
            }
        });

        return collectCourseAdapter;
    }

    /**
     * 取消收藏
     * @param courseId
     */
    private void cancelCollectCourse(String courseId) {

        executeTaskAutoRetry(mApi.collect(RetrofitAPIManager.ACCESSTOKEN, courseId), new Observer<LinkLinkNetInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(linkLinkNetInfo.isSuccess()){
                    refreshData();
                }else{
                    ToastHelper.showCustomMessage(linkLinkNetInfo.getMessage());
                }
            }
        });
    }

    /**
     * 训练课程
     * @param courseId
     */
    private void trainCourse(String courseId) {


        ToastUtils.showShort("trainCourse,"+courseId);

    }

}
