package com.ilinklink.tg.demo.room;

/**
 * TypeOneActivity
 * Created By:Chuck
 * Des:
 * on 2022/6/6 22:20
 */

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;

import com.ilinklink.app.fw.R;
import com.qdong.communal.library.room.demo.DBEngine;
import com.qdong.communal.library.room.demo.Table;

import androidx.annotation.Nullable;

public class RoomTestActivity extends Activity implements View.OnClickListener, SensorEventListener {


    private DBEngine dbEngine;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        setContentView(R.layout.activity_cropimage);


        //获得db对象
        dbEngine = new DBEngine(this);


        insert(null);

        query(null);

    }

    @Override
    public void onResume() {
        //为方向传感器注册监听器
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        Intent intent;

    }


    private float mOffset=150.0f;

    @Override
    public void onSensorChanged(SensorEvent event) {

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 插入三个信息
     * @param view
     */
    public void insert(View view) {
        Table table_0 = new Table("语文","张三","109");
        Table table_1 = new Table("英语","李四","234");
        Table table_2 = new Table("数学","王五","123");
        dbEngine.inserTable(table_0,table_1,table_2);
    }

    /**
     * 修改 下标为2 的信息
     * @param view
     */
    public void update(View view) {
        Table table_0 = new Table("军事",null,"124");
        table_0.setId(2);
        dbEngine.updateTable(table_0);
    }

    /**
     * 删除主键为1 的信息
     * @param view
     */
    public void delete(View view) {
        Table table = new Table(null,null,null);
        table.setId(1);
        dbEngine.deleteTable(table);
    }

    /**
     * 搜索并显示所有信息
     * @param view
     */

    public void queryable(View view) {
        dbEngine.queryAllTable();
    }

    /**
     * 删除数据库所有信息
     * @param view
     */
    public void delete_all(View view) {
        dbEngine.deleteallTable();
    }
    /**
     * 根据特定条件显示信息
     * @param view
     */
    public void query(View view) {
        Table table = new Table(null,null,null);
        //设置主键为2
        table.setId(2);
        dbEngine.queryTable(table);
    }

}


