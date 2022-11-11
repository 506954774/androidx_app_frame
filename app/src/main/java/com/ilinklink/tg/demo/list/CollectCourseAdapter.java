package com.ilinklink.tg.demo.list;

import android.view.View;

import com.ilinklink.tg.communal.AppLoader;
import com.ilinklink.tg.entity.RecommendCourse;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseViewHolder2;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.CustomSingleQuickAdapter;
import com.qdong.communal.library.util.BitmapUtil;
import com.ilinklink.app.fw.R;
import com.ilinklink.app.fw.databinding.ItemCollectCourseBinding;

import java.util.List;

/**
 * FileName: CourseAdapter
 * <p>
 * Author: WuJH
 * <p>
 * Date: 2020/7/9 16:04
 * <p>
 * Description:
 */
public class CollectCourseAdapter extends CustomSingleQuickAdapter<RecommendCourse.CourseListBean, BaseViewHolder2, ItemCollectCourseBinding> {

    private OnCourseClickListener onCourseClickListener;

    public CollectCourseAdapter(List<RecommendCourse.CourseListBean> data, int layoutId, int BR_id) {
        super(data, layoutId, BR_id);
    }

    @Override
    protected void convert(BaseViewHolder2 helper, RecommendCourse.CourseListBean item, ItemCollectCourseBinding viewBind, int position) {
        BitmapUtil.loadPhoto(AppLoader.getInstance(),item.getCourseCover(),viewBind.ivCover);
        viewBind.tvTitle.setText(item.getCourseName());
        viewBind.tvCount.setText(String.format(AppLoader.getInstance().getResources().getString(R.string.collect_count),item.getCollectCount()));
        viewBind.tvTarget.setText(String.format(AppLoader.getInstance().getResources().getString(R.string.collect_target),item.getTrainTarget()));
        viewBind.tvPosition.setText(String.format(AppLoader.getInstance().getResources().getString(R.string.collect_position),item.getTrainPlace()));
        viewBind.tvDifficulty.setText(String.format(AppLoader.getInstance().getResources().getString(R.string.collect_difficulty),item.getTrainDegree()));

        viewBind.customTvTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onCourseClickListener != null){
                    onCourseClickListener.onCourseTrainClickListener(item);
                }
            }
        });
        viewBind.customTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onCourseClickListener != null){
                    onCourseClickListener.onCourseFavorClickListener(item);
                }
            }
        });
    }



    public void setOnCourseClickListener(OnCourseClickListener onCourseClickListener){
        this.onCourseClickListener = onCourseClickListener;
    }


    public interface OnCourseClickListener{
        void onCourseTrainClickListener(RecommendCourse.CourseListBean courseBean);
        void onCourseFavorClickListener(RecommendCourse.CourseListBean courseBean);
    }
}
