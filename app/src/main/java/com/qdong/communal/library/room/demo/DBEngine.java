package com.qdong.communal.library.room.demo;

/**
 * DBEngine
 * Created By:Chuck
 * Des:
 * on 2022/9/18 18:42
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class DBEngine {

    private Table_dao table_dao;

    public DBEngine(Context context){
        TableDatebase tableDatebase = TableDatebase.getInstance(context);
        table_dao = tableDatebase.getTable_dao();

    }

    //增删改查

    //增 insert

    public void inserTable(Table... tables){
        new InsertAsyncTask(table_dao).execute(tables);
    }

    //修改 update
    public void updateTable(Table... tables){
        new UpdateAsyncTask(table_dao).execute(tables);
    }

    //删除 delete
    public void deleteTable(Table... tables){
        new DeleteAsyncTask(table_dao).execute(tables);
    }

    //删除 全部删除 deleteAll
    public void deleteallTable(){
        new DeleteAllTable(table_dao).execute();
    }

    //查询全部
    public void queryAllTable(){
        new QueryAllTable(table_dao).execute();
    }

    //查询 条件
    public void queryTable(Table... tables){
        new QueryTable(table_dao).execute(tables);
    }
    //如果我们想完数据库 默认异步操作 ================== 异步操作
    //AsyncTask的异步线程的一个抽象类 具体理解可以看：https://www.jianshu.com/p/ee1342fcf5e7
    public class InsertAsyncTask extends AsyncTask<Table,Void,Void>{

        private Table_dao dao;
        public InsertAsyncTask(Table_dao table_dao){
            dao = table_dao;
        }
        @Override
        protected Void doInBackground(Table... tables) {
            dao.insertTable(tables);
            return null;
        }
    }
    //修改 update
    public class UpdateAsyncTask extends AsyncTask<Table,Void,Void>{
        private Table_dao dao;
        public UpdateAsyncTask(Table_dao table_dao){
            dao = table_dao;
        }
        @Override
        protected Void doInBackground(Table... tables) {
            dao.updateTable(tables);
            return null;
        }
    }
    //删除根据条件
    public class DeleteAsyncTask extends AsyncTask<Table,Void,Void>{
        private Table_dao dao;
        public DeleteAsyncTask(Table_dao table_dao){
            dao = table_dao;
        }
        //既然传入了table说明是条件删除
        @Override
        protected Void doInBackground(Table... tables) {
            dao.deleteTable(tables);
            return null;
        }
    }
    //全部删除
    public class DeleteAllTable extends AsyncTask<Void,Void,Void>{
        private Table_dao dao;
        public DeleteAllTable(Table_dao table_dao){
            dao = table_dao;
        }
        //全部删除
        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteallTable();
            return null;
        }
    }
    //全部查询
    public static class QueryAllTable extends AsyncTask<Void,Void,Void>{
        private Table_dao dao;
        public QueryAllTable(Table_dao table_dao){
            dao = table_dao;
        }
        //全部查询
        @Override
        protected Void doInBackground(Void... voids) {
            List<Table> allTable = dao.getAllTable();
            //遍历所有结果
            for (Table table : allTable){
                Log.e("leo", "doInBackground:全部查询 每一项 查询 " +table.toString());
            }

            return null;
        }
    }
    //条件查询
    public static class QueryTable extends AsyncTask<Table,Void,Void>{
        private Table_dao dao;
        public QueryTable(Table_dao table_dao){
            dao = table_dao;
        }
        //条件查询
        @Override
        protected Void doInBackground(Table... tables) {
            try {
                //可以通过下面这种方式获得主键，从而查询到相应的信息
                Table table = dao.getTable(tables[0].getId());
                //显示结果

                Log.e("leo", "doInBackground:全部查询 每一项 查询 " +table.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}

