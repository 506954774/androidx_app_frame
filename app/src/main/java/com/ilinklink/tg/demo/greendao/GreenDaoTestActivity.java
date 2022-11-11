package com.ilinklink.tg.demo.greendao;

import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.base.BaseSimpleListActivty;
import com.ilinklink.tg.demo.list.CollectCourseListFragment;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.widget.WifiView;
import com.qdong.communal.library.util.LogUtil;
import com.ilinklink.app.fw.R;

import java.util.List;

import androidx.fragment.app.Fragment;

public class GreenDaoTestActivity extends BaseSimpleListActivty {




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

    private void queryStudentList(){
        String studentUUID="15262592514";
        List<StudentInfo> studentInfoList = DBHelper.getInstance(this).getStudentInfoList(studentUUID);
        LogUtil.i("greenDao","getStudentInfoList,"+studentInfoList);

    }

    private void insertStudent(){


        try {
            String studentUUID="15262592514";
            StudentInfo student=new StudentInfo();
            student.setStudentUUID(studentUUID);
            student.setName("王五");
            student.setImageUrl("http://www.12333.jpg");
            student.setDesc("");
            student.setUpdateTime(System.currentTimeMillis());
            //student.setImageDownloadTime(0L);


            StudentInfo studentInfo = DBHelper.getInstance(this).getStudentInfo(studentUUID);
            if(studentInfo==null){
                DBHelper.getInstance(this).saveStudentInfo(student);

                LogUtil.i("greenDao","插入成功");
            }

            else {
                LogUtil.i("greenDao","更新");

                studentInfo.setDesc("更新again2");
                //studentInfo.setImageDownloadTime(new Long(10086));
                DBHelper.getInstance(this).saveStudentInfo(studentInfo);
            }



            studentInfo = DBHelper.getInstance(this).getStudentInfo(studentUUID);
            studentInfo.getId();

            LogUtil.i("greenDao","插入成功,studentInfo.getId():"+studentInfo.getId());


        } catch (Exception e) {
            LogUtil.i("greenDao","异常:"+e.getMessage());
        }

    }
}
