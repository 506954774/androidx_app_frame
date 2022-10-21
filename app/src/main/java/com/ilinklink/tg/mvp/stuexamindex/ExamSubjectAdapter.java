package com.ilinklink.tg.mvp.stuexamindex;

import android.view.View;

import com.ilinklink.greendao.ExamInfo;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseViewHolder2;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.CustomSingleQuickAdapter;
import com.spc.pose.demo.databinding.ItemExamSubItemBinding;
import com.spc.pose.demo.databinding.ItemZtsbCourseBinding;

import java.util.List;

/**
 * @des:  考试科目适配器
 * @date 创建时间:2022/5/22
 * @author Chuck
 **/

public class ExamSubjectAdapter extends CustomSingleQuickAdapter<ExamInfo, BaseViewHolder2, ItemExamSubItemBinding> {


    public ExamSubjectAdapter(List<ExamInfo> data, int layoutId, int BR_id) {
        super(data, layoutId, BR_id);
    }

    @Override
    protected void convert(BaseViewHolder2 helper, ExamInfo item, ItemExamSubItemBinding viewBind, int position) {

        //BitmapUtil.loadPhoto(AppLoader.getInstance(),item.getCourseCover(),viewBind.ivCover);

        viewBind.tvName.setText(item.getName());

        if(item.getDesc()==null){
            viewBind.ivFinished.setVisibility(View.INVISIBLE);
        }
        else {
            viewBind.ivFinished.setVisibility(View.VISIBLE);
        }

    }



}
