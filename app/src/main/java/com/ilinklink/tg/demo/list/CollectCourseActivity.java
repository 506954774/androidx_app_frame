package com.ilinklink.tg.demo.list;

import android.os.Build;
import android.os.Bundle;

import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.base.BaseSimpleListActivty;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.widget.WifiView;
import com.qdong.communal.library.util.LogUtil;
import com.spc.pose.demo.R;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class CollectCourseActivity extends BaseSimpleListActivty {




    @Override
    protected WifiView.Type getCustomStatusBarType() {
        return WifiView.Type.NONE;
    }

    @Override
    public Fragment getmFragment() {
        return new CollectCourseListFragment();
    }

    @Override
    public void initTtitleView() {
        setTitleText(getString(R.string.user_collect));
    }

    @Override
    protected boolean isRelativeStatusBar() {
        return false;
    }


}
