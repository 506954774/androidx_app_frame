package com.ilinklink.tg.mvp.history;

import android.text.Html;

import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.StudentExamRecord;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.BaseViewHolder2;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.CustomSingleQuickAdapter;
import com.spc.pose.demo.databinding.ItemExamInfoHistoryBinding;
import com.spc.pose.demo.databinding.ItemExamListHistoryBinding;

import java.util.List;

public class ExamInfoHistoryAdapter extends CustomSingleQuickAdapter<StudentExamRecord, BaseViewHolder2, ItemExamInfoHistoryBinding> {

    public ExamInfoHistoryAdapter(List<StudentExamRecord> data, int layoutId, int BR_id) {
        super(data, layoutId, BR_id);
    }

    @Override
    protected void convert(BaseViewHolder2 helper, StudentExamRecord item, ItemExamInfoHistoryBinding viewBind, int position) {
        viewBind.tvName.setText(item.getStudentName());

        viewBind.examNo.setText(item.getReservedColumn()); // 考试编号
        viewBind.examTime.setText(item.getExamTime());


        String str="32<font color='#ff0000'>（不合格）</font>";
        String str2="69<font color='#00ff00'>（合格）</font>";

        viewBind.tvSource1.setText(Html.fromHtml(str));
        viewBind.tvSource2.setText(Html.fromHtml(str));
        viewBind.tvSource2.setText(Html.fromHtml(str2));
        viewBind.tvSource3.setText(Html.fromHtml(str));
        viewBind.tvSource4.setText(Html.fromHtml(str2));

    }

}
