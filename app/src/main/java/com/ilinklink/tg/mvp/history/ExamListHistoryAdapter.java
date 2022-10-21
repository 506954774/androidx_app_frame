package com.ilinklink.tg.mvp.history;

import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.tg.utils.DateFormatuUtil;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseViewHolder2;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.CustomSingleQuickAdapter;
import com.spc.pose.demo.databinding.ItemExamListHistoryBinding;

import java.util.List;

public class ExamListHistoryAdapter extends CustomSingleQuickAdapter<ExamRecord, BaseViewHolder2, ItemExamListHistoryBinding> {

    public ExamListHistoryAdapter(List<ExamRecord> data, int layoutId, int BR_id) {
        super(data, layoutId, BR_id);
    }

    @Override
    protected void convert(BaseViewHolder2 helper, ExamRecord item, ItemExamListHistoryBinding viewBind, int position) {
        viewBind.tvName.setText(item.getName());
        viewBind.examTime.setText(item.getExamTime());
    }

}
