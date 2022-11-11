package com.qdong.communal.library.room.demo;

/**
 * TableDatebase
 * Created By:Chuck
 * Des:
 * on 2022/9/18 18:42
 */


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


//将数据库 关联 之前的表 数据库信息
@Database(entities = {Table.class},version = 1,exportSchema = false)
public abstract class TableDatebase extends RoomDatabase {
    //用户只需要操作dao，我们必须暴露dao
    public abstract Table_dao getTable_dao();



    //单例模式 返回DB
    private static TableDatebase INSTANCE;
    public static synchronized TableDatebase getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),TableDatebase.class,"TABLE_DATABASE")

                    .build();
        }
        return INSTANCE;
    }


}


