package com.ilinklink.tg.mvp.history;

import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.tg.utils.DateFormatuUtil;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseViewHolder2;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.CustomSingleQuickAdapter;
import com.spc.pose.demo.databinding.ItemExamListHistoryBinding;

import java.util.List;

public class ExamListHistoryAdapter extends CustomSingleQuickAdapter<ExamInfo, BaseViewHolder2, ItemExamListHistoryBinding> {

    public ExamListHistoryAdapter(List<ExamInfo> data, int layoutId, int BR_id) {
        super(data, layoutId, BR_id);
    }

    @Override
    protected void convert(BaseViewHolder2 helper, ExamInfo item, ItemExamListHistoryBinding viewBind, int position) {
        viewBind.tvName.setText(item.getName());
        viewBind.examTime.setText(DateFormatuUtil.getDate("yyyy-MM-dd HH:mm:ss", item.getStartTimeMs()));
    }

}
