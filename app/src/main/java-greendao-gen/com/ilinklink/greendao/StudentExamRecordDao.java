package com.ilinklink.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.ilinklink.greendao.StudentExamRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table STUDENT_EXAM_RECORD.
*/
public class StudentExamRecordDao extends AbstractDao<StudentExamRecord, Long> {

    public static final String TABLENAME = "STUDENT_EXAM_RECORD";

    /**
     * Properties of entity StudentExamRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property StudentExamRecordId = new Property(1, String.class, "studentExamRecordId", false, "STUDENT_EXAM_RECORD_ID");
        public final static Property ExamRecordId = new Property(2, String.class, "examRecordId", false, "EXAM_RECORD_ID");
        public final static Property StudentUUID = new Property(3, String.class, "studentUUID", false, "STUDENT_UUID");
        public final static Property StudentName = new Property(4, String.class, "studentName", false, "STUDENT_NAME");
        public final static Property ExamTime = new Property(5, String.class, "examTime", false, "EXAM_TIME");
        public final static Property SubResultJson = new Property(6, String.class, "subResultJson", false, "SUB_RESULT_JSON");
        public final static Property Desc = new Property(7, String.class, "desc", false, "DESC");
        public final static Property ReservedColumn = new Property(8, String.class, "reservedColumn", false, "RESERVED_COLUMN");
        public final static Property ReservedColumn2 = new Property(9, String.class, "reservedColumn2", false, "RESERVED_COLUMN2");
    };


    public StudentExamRecordDao(DaoConfig config) {
        super(config);
    }
    
    public StudentExamRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'STUDENT_EXAM_RECORD' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'STUDENT_EXAM_RECORD_ID' TEXT UNIQUE ," + // 1: studentExamRecordId
                "'EXAM_RECORD_ID' TEXT," + // 2: examRecordId
                "'STUDENT_UUID' TEXT," + // 3: studentUUID
                "'STUDENT_NAME' TEXT," + // 4: studentName
                "'EXAM_TIME' TEXT," + // 5: examTime
                "'SUB_RESULT_JSON' TEXT," + // 6: subResultJson
                "'DESC' TEXT," + // 7: desc
                "'RESERVED_COLUMN' TEXT," + // 8: reservedColumn
                "'RESERVED_COLUMN2' TEXT);"); // 9: reservedColumn2
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'STUDENT_EXAM_RECORD'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, StudentExamRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String studentExamRecordId = entity.getStudentExamRecordId();
        if (studentExamRecordId != null) {
            stmt.bindString(2, studentExamRecordId);
        }
 
        String examRecordId = entity.getExamRecordId();
        if (examRecordId != null) {
            stmt.bindString(3, examRecordId);
        }
 
        String studentUUID = entity.getStudentUUID();
        if (studentUUID != null) {
            stmt.bindString(4, studentUUID);
        }
 
        String studentName = entity.getStudentName();
        if (studentName != null) {
            stmt.bindString(5, studentName);
        }
 
        String examTime = entity.getExamTime();
        if (examTime != null) {
            stmt.bindString(6, examTime);
        }
 
        String subResultJson = entity.getSubResultJson();
        if (subResultJson != null) {
            stmt.bindString(7, subResultJson);
        }
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(8, desc);
        }
 
        String reservedColumn = entity.getReservedColumn();
        if (reservedColumn != null) {
            stmt.bindString(9, reservedColumn);
        }
 
        String reservedColumn2 = entity.getReservedColumn2();
        if (reservedColumn2 != null) {
            stmt.bindString(10, reservedColumn2);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public StudentExamRecord readEntity(Cursor cursor, int offset) {
        StudentExamRecord entity = new StudentExamRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // studentExamRecordId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // examRecordId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // studentUUID
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // studentName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // examTime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // subResultJson
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // desc
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // reservedColumn
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // reservedColumn2
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, StudentExamRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStudentExamRecordId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setExamRecordId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setStudentUUID(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStudentName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setExamTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSubResultJson(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDesc(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setReservedColumn(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setReservedColumn2(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(StudentExamRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(StudentExamRecord entity) {
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