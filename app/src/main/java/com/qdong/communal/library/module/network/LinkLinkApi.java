package com.qdong.communal.library.module.network;

import com.ilinklink.tg.entity.ZtsbAuthResponse;
import com.qdong.communal.library.util.Constants;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * QDongApi
 * 趣动api接口,Header没有用注解写,header添加的逻辑在RetrofitAPIManager里
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/6/28  11:19
 * Copyright : 2014-2016 深圳趣动智能科技有限公司-版权所有
 */
public interface LinkLinkApi {

    /********************************************************************************
     * 设备端模块
     *
     */
    //获取clientKey
    @GET(Constants.TOKEN_SERVICE + "/client/register")
    Observable<LinkLinkNetInfo> getClientKey(@Query("appId") String appId);

    //刷新token
    @POST(Constants.USER_SERVICE + "api/auth/jwt/refresh")
    Call<LinkLinkNetInfo> refreshToken(@Body Map<String, String> map);

    /********************************************************************************
     * 公用模块
     */

    //查询app最新版本
    @POST(Constants.APP_SERVICE + "api/config/appVersionControll/getVersion")
    Observable<LinkLinkNetInfo> findLatestVersion(@Body Map<String, Object> map);


    //tfs多文件上传
    @Multipart
    @POST(Constants.FILE_SERVICE + "/file/upload")
    Observable<LinkLinkNetInfo> uploadMultipleFile1(@PartMap Map<String, RequestBody> params);


    //tfs多文件上传
    @Multipart
    @POST(Constants.FILE_SERVICE + "/file/upload/files")
    //Observable<LinkLinkNetInfo> uploadMultipleFile(@Part MultipartBody.Part file);
    Observable<LinkLinkNetInfo> uploadMultipleFile(@PartMap Map<String, RequestBody> params);

    //tfs单文件上传
    @Multipart
    @POST(Constants.FILE_SERVICE + "/file/upload")
    Observable<LinkLinkNetInfo> uploadSingleFile(@Part MultipartBody.Part file);


    //加载网络图片
    @GET(Constants.USER_SERVICE + "/client/register")
    Observable<ResponseBody> getPicUrl(@Url String url);

    /**
     * 上传一张图片
     *
     * @param description
     * @param imgs
     * @return
     */
    @Multipart
    @POST(Constants.FILE_SERVICE + "/file/upload")
    Call<String> uploadImage(@Part("fileName") String description,
                             @Part("file\"; filename=\"image.png\"") RequestBody imgs);

    //根据参数类型查询常用参数值 参数类型 1-目标 2-部位 3-品类 4-难度
    @GET(Constants.COURSE_SERVICE + "kinect/user/param/value/common")
    Observable<LinkLinkNetInfo> queryParams(@Query("token") String token ,@Query("paramType") int paramType);


    //根据参数类型查询常用参数值 参数类型 1-目标 2-部位 3-品类 4-难度
    @GET(Constants.COURSE_SERVICE + "kinect/user/area/all")
    Observable<LinkLinkNetInfo> queryArea();

    /*******************************************************************************************************
     * 登录注册模块
     */
    //用户登陆
    @POST(Constants.USER_SERVICE + "kinect/user/login")
    Observable<LinkLinkNetInfo> userLogin(@Body Map<String, Object> map);

    //登陆,用Call是为了同步调用
    @POST(Constants.USER_SERVICE + "kinect/user/login")
    Call<LinkLinkNetInfo> login(@Body Map<String, String> map);

    //发送验证码-通用
    @GET(Constants.USER_SERVICE + "kinect/user/sms/send")
    Observable<LinkLinkNetInfo> sendSmsCode(@QueryMap Map<String, Object> map);

    //获取图形验证码
    @GET(Constants.USER_SERVICE + "kinect/user/verify/code")
    Observable<ResponseBody> getPicCode();

    //获取图形验证码
    @GET(Constants.USER_SERVICE + "kinect/user/verify/code")
    Call<ResponseBody> getPicCode2();

    @POST(Constants.USER_SERVICE + "kinect/user/register")
    Observable<LinkLinkNetInfo> regist(@Body Map<String, Object> map);

    //发送验证码-通用
    @GET(Constants.USER_SERVICE + "kinect/user/token-uuid")
    Observable<LinkLinkNetInfo> queryToken(@Query("uuid") String uuid);

    //发送验证码-通用
    @POST(Constants.USER_SERVICE + "kinect/user/login/pwd/update")
    Observable<LinkLinkNetInfo> updatePassword(@Body Map<String, Object> map);

    //退出登录
    @GET(Constants.USER_SERVICE + "kinect/user/logout")
    Observable<LinkLinkNetInfo> logout();

    /*******************************************************************************************************
     * 用户模块
     */

    //获取用户数据
    @GET(Constants.USER_SERVICE + "kinect/user/detail")
    Observable<LinkLinkNetInfo> queryUserInfo();

    //信息修改(示例昵称修改)
    @POST(Constants.USER_SERVICE + "kinect/user/info/update")
    Observable<LinkLinkNetInfo> updateUserInfo(@Body Map<String, Object> map);


    //信息修改(示例昵称修改)
    @GET(Constants.USER_SERVICE + "kinect/user/train/data")
    Observable<LinkLinkNetInfo> queryTrainData();

    /**
     * ****************************************************************************
     * 我的模块
     * ****************************************************************************
     */
    //3100-【获取用户全部信息】
    @POST(Constants.USER_SERVICE + "api/app/web/aucmUser/getCurrentById")
    Observable<LinkLinkNetInfo> getCurrentById(@Body Map<String, Object> map);

    //【获取用户全部信息】31000-【3100复刻】
    @POST(Constants.USER_SERVICE + "api/app/web/aucmUser/getCurrentByIdCopy")
    Observable<LinkLinkNetInfo> getCurrentByIdCopy(@Body Map<String, Object> map);

    //【API用户主表】个人资料修改(示例昵称修改)
    @POST(Constants.USER_SERVICE + "api/app/wap/appUser/saveOrUpdate")
    Observable<LinkLinkNetInfo> saveOrUpdate(@Body Map<String, Object> map);



    /**
     * ****************************************************************************
     * 课程模块
     * ****************************************************************************
     */
    //推荐课程列表
    @POST(Constants.COURSE_SERVICE + "kinect/user/course/recommend/list")
    Observable<LinkLinkNetInfo> getRecommendList(@Query("token") String token,@Body Map<String, Object> map);

    //课程详情
    @GET(Constants.COURSE_SERVICE + "kinect/user/course/detail")
    Observable<LinkLinkNetInfo> getCourseDetails(@Query("token") String token ,@Query("courseId") String courseId);

    //收藏/取消收藏课程
    @GET(Constants.COURSE_SERVICE + "kinect/user/course/collect")
    Observable<LinkLinkNetInfo> collect(@Query("token") String token ,@Query("courseId") String courseId);

    //课程列表
    @POST(Constants.COURSE_SERVICE + "kinect/user/course/list")
    Observable<LinkLinkNetInfo> courseList(@Query("token") String token ,@Body Map<String, Object> map);

    //练习课程列表
    @POST(Constants.COURSE_SERVICE + "kinect/user/course/train/list")
    Observable<LinkLinkNetInfo> getPracticeList(@Query("token") String token,@Body Map<String, Object> map);

    //收藏课程列表
    @POST(Constants.COURSE_SERVICE + "kinect/user/course/collect/list")
    Observable<LinkLinkNetInfo> getCollectList(@Query("token") String token,@Body Map<String, Object> map);

    //开始训练
    @POST(Constants.COURSE_SERVICE + "kinect/user/train/course/start")
    Observable<LinkLinkNetInfo> courseStart(@Query("token") String token ,@Body Map<String, Object> map);

    //结束训练
    @POST(Constants.COURSE_SERVICE + "kinect/user/train/course/end")
    Observable<LinkLinkNetInfo> courseEnd(@Query("token") String token ,@Body Map<String, Object> map);


    //设备端向后台上传分数
    @POST(Constants.COURSE_SERVICE + "kinect/user/train/score/upload")
    Call<LinkLinkNetInfo> scoreUpload(@Query("token") String token ,@Body Map<String, Object> map);


    //训练分数详情
    @GET(Constants.COURSE_SERVICE + "kinect/user/train/course/data")
    Observable<LinkLinkNetInfo> scoreData(@Query("token") String token ,@Query("trainId") String trainId);

    /**
     * ****************************************************************************
     * 第三方服务
     * ****************************************************************************
     */
    //百度图片算法识别
    @FormUrlEncoded
    @POST(Constants.COURSE_SERVICE + "frame/upload.do")
    Call<LinkLinkNetInfo> pictureRecognize(  @FieldMap Map<String, Object> map);

    //百度图片算法识别分数查询
    @POST(Constants.COURSE_SERVICE + "frame/match/query.do")
    Call<LinkLinkNetInfo> pictureScoreQuery( @Body Map<String, Object> map);

    //百度图片算法识别分数查询
    @FormUrlEncoded
    @POST(Constants.COURSE_SERVICE + "frame/match/score.do")
    Observable<LinkLinkNetInfo> trainScoreQuery( @FieldMap Map<String, Object> map);



    /**************************************************************************************************************************************************
     * ******************************************************************
     * 姿态识别  start
     */

    //登录
    @POST(Constants.COURSE_SERVICE + "user/login")
    Observable<LinkLinkNetInfo> auth(@Body Map<String, Object> map);

    //获取最近一次的考试信息
    @GET(Constants.COURSE_SERVICE + "posture_recognition/exam_emp")
    Observable<LinkLinkNetInfo> getExamInfo();

    //获取最近一次的考试信息
    @GET(Constants.COURSE_SERVICE + "posture_recognition/exam_emp")
    Call<LinkLinkNetInfo> getExamInfo2();

    //提交考试成绩
    @POST(Constants.COURSE_SERVICE + "posture_recognition/result_input")
    Observable<LinkLinkNetInfo> commitExamResult(@Body Map<String, Object> map);

    /********************************************************************************
     * 姿态识别  end
     */



    /**************************************************************************************************************************************************
     * ******************************************************************
     * 新兵体态识别系统  start
     */

    //https://174y9539y5.zicp.fun/mgr-backend/app/examination/down
    //拉取考试信息
    @GET(Constants.ILINK_APP_SERVICE + "examination/down")
    Observable<LinkLinkNetInfo> getExamInfoList();


    //https://174y9539y5.zicp.fun/mgr-backend/app/person/down
    //拉取考生信息
    @GET(Constants.ILINK_APP_SERVICE + "person/down")
    Observable<LinkLinkNetInfo> getStuInfoList();


    //http://112.74.87.88:10089/mgr-backend/app/examData/upload
    //提交考试成绩
    @POST(Constants.COURSE_SERVICE + "examData/upload222")
    Observable<LinkLinkNetInfo> uploadExamResult(@Body String jsonArray);


    /********************************************************************************
     * 新兵体态识别系统  end
     */


}
