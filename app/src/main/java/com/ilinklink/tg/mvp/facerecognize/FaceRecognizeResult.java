package com.ilinklink.tg.mvp.facerecognize;

/**
 * FaceRecognizeResult
 * Created By:Chuck
 * Des:
 * on 2022/5/15 14:56
 */
public class FaceRecognizeResult {

    private static final FaceRecognizeResult ourInstance = new FaceRecognizeResult();

    public static FaceRecognizeResult getInstance() {
        return ourInstance;
    }

    private FaceRecognizeResult() {
    }

    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
