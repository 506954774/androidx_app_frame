package com.qdong.communal.library.room.demo;

/**
 * Table_dao
 * Created By:Chuck
 * Des:
 * on 2022/9/18 18:39
 */


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

//留给用户使用
@Dao
public interface Table_dao {

    //增加
    @Insert
    void insertTable(Table... tables);

    //改写
    @Update
    void updateTable(Table... tables);

    //删除  单个--根据条件
    @Delete
    void deleteTable(Table... tables);

    //删除所有信息
    @Query("DELETE FROM `Table`")
    void deleteallTable();

    //查询 单个--根据条件
    //query后面跟的是SQL语句，里面可以设有变量方便编程
    @Query("SELECT * FROM `Table` WHERE id = :id")
    Table getTable(int id);

    //查询所有
    @Query("SELECT * FROM `Table` ORDER BY ID DESC")
    List<Table> getAllTable();



}


