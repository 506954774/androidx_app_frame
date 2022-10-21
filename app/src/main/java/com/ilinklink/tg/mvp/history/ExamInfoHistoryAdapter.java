package com.ilinklink.tg.mvp.history;

import android.text.Html;

import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.StudentExamRecord;
import com.ilinklink.tg.entity.SubjectExamResult;
import com.ilinklink.tg.utils.Json;
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
        SubjectExamResult result = Json.fromJson(item.getSubResultJson(), SubjectExamResult.class);

        String pushUpsScore = result.getPushUpsScore();// 俯卧撑
        String count = "0";
        String res = "0";
        if (pushUpsScore != null && !pushUpsScore.equals("")) {
            count = pushUpsScore.split(",")[0];
            res = pushUpsScore.split(",")[1];
        }
        String str = count;
        if (res.equals("0")) {
            str += "<font color='#ff0000'>（不合格）</font>";
        } else {
            str += "<font color='#00ff00'>（合格）</font>";
        }

        String sithUpsScore = result.getSithUpsScore();// 俯卧撑
        count = "0";
        res = "0";
        if (sithUpsScore != null && !sithUpsScore.equals("")) {
            count = sithUpsScore.split(",")[0];
            res = sithUpsScore.split(",")[1];
        }
        String str2 = count;
        if (res.equals("0")) {
            str2 += "<font color='#ff0000'>（不合格）</font>";
        } else {
            str2 += "<font color='#00ff00'>（合格）</font>";
        }



        String pullUpsScore = result.getPullUpsScore();// 引体向上
        count = "0";
        res = "0";
        if (pullUpsScore != null && !pullUpsScore.equals("")) {
            count = pullUpsScore.split(",")[0];
            res = pullUpsScore.split(",")[1];
        }
        String str3 = count;
        if (res.equals("0")) {
            str3 += "<font color='#ff0000'>（不合格）</font>";
        } else {
            str3 += "<font color='#00ff00'>（合格）</font>";
        }


        String parallelBarsScore = result.getParallelBarsScore();// 引体向上
        count = "0";
        res = "0";
        if (parallelBarsScore != null && !parallelBarsScore.equals("")) {
            count = parallelBarsScore.split(",")[0];
            res = parallelBarsScore.split(",")[1];
        }
        String str4 = count;
        if (res.equals("0")) {
            str4 += "<font color='#ff0000'>（不合格）</font>";
        } else {
            str4 += "<font color='#00ff00'>（合格）</font>";
        }



        viewBind.tvSource1.setText(Html.fromHtml(str));
        viewBind.tvSource2.setText(Html.fromHtml(str2));
        viewBind.tvSource3.setText(Html.fromHtml(str3));
        viewBind.tvSource4.setText(Html.fromHtml(str4));

    }

}
