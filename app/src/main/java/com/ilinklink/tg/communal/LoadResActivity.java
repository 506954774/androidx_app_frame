package com.ilinklink.tg.communal;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.ilinklink.app.fw.R;

import androidx.multidex.MultiDex;


/**
 * LoadResActivity 这个界面是在另外一个进程里面的
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/1/23  10:14
 * Copyright : 2014-2015 深圳掌通宝科技有限公司-版权所有
 **/
public class LoadResActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.e("loadDex", "LoadResActivity onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super .onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.left2right_in, R.anim.left2right_out);
        setContentView(R.layout.base_layout_load);
        new LoadDexTask().execute();

        overridePendingTransition(R.anim.base_display, R.anim.base_fade);//透明度渐增
    }
    class LoadDexTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                MultiDex.install(getApplication());
                Log.d("loadDex", "install finish");

              /*  SharedPreferences sp = LoadResActivity.this.getSharedPreferences(
                        PackageUtil.getPackageInfo(LoadResActivity.this).versionName, MODE_MULTI_PROCESS);
                sp.edit().putString(AppLoader.KEY_DEX2_SHA1,get2thDexSHA1(context)).commit();*/

                ((AppLoader) getApplication()).installFinish(getApplication());
            } catch (Exception e) {
                Log.e("loadDex" , e.getLocalizedMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            Log.d( "loadDex", "get install finish");
            finish();
            System.exit( 0);
        }
    }
    @Override
    public void onBackPressed() {
        //cannot backpress
    }

/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
             //do something
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {

            //do something
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {

         //这里操作是没有返回结果的
        }

        return super.onKeyDown(keyCode, event);
    }*/
}
