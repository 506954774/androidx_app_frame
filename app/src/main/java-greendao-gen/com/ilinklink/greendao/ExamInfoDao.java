package com.ilinklink.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.ilinklink.greendao.ExamInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table EXAM_INFO.
*/
public class ExamInfoDao extends AbstractDao<ExamInfo, Long> {

    public static final String TABLENAME = "EXAM_INFO";

    /**
     * Properties of entity ExamInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ExamUUID = new Property(1, String.class, "examUUID", false, "EXAM_UUID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Desc = new Property(3, String.class, "desc", false, "DESC");
        public final static Property Remark = new Property(4, String.class, "remark", false, "REMARK");
        public final static Property DifficultyThreshold = new Property(5, Double.class, "difficultyThreshold", false, "DIFFICULTY_THRESHOLD");
        public final static Property LimitTime = new Property(6, Long.class, "limitTime", false, "LIMIT_TIME");
    };


    public ExamInfoDao(DaoConfig config) {
        super(config);
    }
    
    public ExamInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'EXAM_INFO' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'EXAM_UUID' TEXT UNIQUE ," + // 1: examUUID
                "'NAME' TEXT," + // 2: name
                "'DESC' TEXT," + // 3: desc
                "'REMARK' TEXT," + // 4: remark
                "'DIFFICULTY_THRESHOLD' REAL," + // 5: difficultyThreshold
                "'LIMIT_TIME' INTEGER);"); // 6: limitTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'EXAM_INFO'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ExamInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String examUUID = entity.getExamUUID();
        if (examUUID != null) {
            stmt.bindString(2, examUUID);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(4, desc);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(5, remark);
        }
 
        Double difficultyThreshold = entity.getDifficultyThreshold();
        if (difficultyThreshold != null) {
            stmt.bindDouble(6, difficultyThreshold);
        }
 
        Long limitTime = entity.getLimitTime();
        if (limitTime != null) {
            stmt.bindLong(7, limitTime);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ExamInfo readEntity(Cursor cursor, int offset) {
        ExamInfo entity = new ExamInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // examUUID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // desc
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // remark
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // difficultyThreshold
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6) // limitTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ExamInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setExamUUID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDesc(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRemark(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDifficultyThreshold(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setLimitTime(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ExamInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ExamInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
