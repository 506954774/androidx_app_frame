package com.ilinklink.tg.mvp.initfacefeatrue;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.selectsubject.SelectSubjectActivity;
import com.ilinklink.tg.utils.CollectionUtils;
import com.qdong.communal.library.util.Constants;
import com.qdong.communal.library.util.LogUtil;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityInitFaceFeatrueBinding;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.CharsetUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassAgeGenderResult;
import mcv.facepass.types.FacePassConfig;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassFace;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageRotation;
import mcv.facepass.types.FacePassImageType;
import mcv.facepass.types.FacePassModel;
import mcv.facepass.types.FacePassPose;
import mcv.facepass.types.FacePassRCAttribute;
import mcv.facepass.types.FacePassRecognitionResult;
import mcv.facepass.types.FacePassRecognitionState;
import mcv.facepass.types.FacePassTrackOptions;
import megvii.testfacepass.FaceView;
import megvii.testfacepass.SettingActivity;
import megvii.testfacepass.SettingVar;
import megvii.testfacepass.adapter.FaceTokenAdapter;
import megvii.testfacepass.adapter.GroupNameAdapter;
import megvii.testfacepass.camera.CameraManager;
import megvii.testfacepass.camera.CameraPreview;
import megvii.testfacepass.camera.CameraPreviewData;
import megvii.testfacepass.camera.ComplexFrameHelper;
import megvii.testfacepass.utils.FileUtil;
/**
 *
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2022/10/16  13:51
 * Copyright : 2014-2020深圳令令科技有限公司-版权所有
 *
 *          实际的流程：
 *          一，先根据学生id和最后更新时间，判断是否需要下载，是则下载，文件命名规则：学生id+下划线+数据库里学生头像更新毫秒时间戳+jpg
 *
 *         二，遍历，添加到特征值：
 *                    1,遍历指定的sd卡路径下的图片
 *                    2，如果之前已经绑定了groupName，则continue
 *                    3，根据图片路径，生成bitmap，调用sdk拿到token
 *                    4，将此token绑定到group，并存入到sqlite数据库，使用desc这个字段，字段使用图片路径加英文逗号加token ： {图片路径}，[token]
 *
      * @des:
        *
        *           StudentInfo各个字段说明：
        *           StudentInfo是之前济南版本的考生实体类，
        *            private Long id;数据库行id，框架提供
        *            private String studentUUID; userId，系统内唯一标识
        *             private String name; 考生姓名
        *             private String imageUrl; 图片下载路径
        *             private String desc; 图片本地路径加英文逗号加token ： {图片路径}，[token]
        *              private Long updateTime;更新时间
        *               private Long imageDownloadTime;

 **/
public class InitFaceFeatrueAct3 extends BaseMvpActivity<ActivityInitFaceFeatrueBinding> implements CameraManager.CameraListener, View.OnClickListener {



    //给父类存起来,父类destory时遍历释放资源
    @Override
    public ArrayList<BasePresenter> initPresenters() {
        ArrayList<BasePresenter> list = new ArrayList<>();
        return list;
    }

    private enum FacePassSDKMode {
        MODE_ONLINE,
        MODE_OFFLINE
    }

    private enum FacePassCameraType{
        FACEPASS_SINGLECAM,
        FACEPASS_DUALCAM
    };

    private static FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;

    private static final String DEBUG_TAG = "FacePassDemo";

    // 需要客户根据自己需求配置
    private static final String authIP = "https://api-cn.faceplusplus.com";
    //private static final String apiKey = "fHrrO2VYQ8WQTujUuqsGixdBasnetD8J";
    private static final String apiKey = "7B18IrqUfpAdvmeP7MyhNOTEx4c8u0QT";
    //private static final String apiSecret = "9rwNIpyUclXsYRn3WRrzhKDVIl8MEb9O";
    private static final String apiSecret = "dO6TUdfpeRWnywYrrI6wcmaPDbdris9F";
    /* 根据需求配置单目 / 双目场景，默认单目 */
    private static FacePassCameraType CamType = FacePassCameraType.FACEPASS_SINGLECAM;

    /* 人脸识别Group */
    private static final String group_name = "facepass";

    /* 程序所需权限 ：相机 文件存储 网络访问 */
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
    private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
    private String[] Permission = new String[]{PERMISSION_CAMERA, PERMISSION_WRITE_STORAGE, PERMISSION_READ_STORAGE, PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE};


    /* SDK 实例对象 */
    FacePassHandler mFacePassHandler;



    private boolean isLocalGroupExist = false;



    public class RecognizeData {
        public byte[] message;
        public FacePassTrackOptions[] trackOpt;

        public RecognizeData(byte[] message) {
            this.message = message;
            this.trackOpt = null;
        }

        public RecognizeData(byte[] message, FacePassTrackOptions[] opt) {
            this.message = message;
            this.trackOpt = opt;
        }
    }

    ArrayBlockingQueue<RecognizeData> mRecognizeDataQueue;
    ArrayBlockingQueue<CameraPreviewData> mFeedFrameQueue;


    /*图片缓存*/
    private FaceImageCache mImageCache;

    private Handler mAndroidHandler;


    /**
     * @param :[]
     * @return type:boolean
     * @method name:isRelativeStatusBar
     * @des:设置状态栏类型,返回true,则沉浸,false则线性追加
     * @date 创建时间:2018/12/4
     * @author Chuck
     **/
    @Override
    protected boolean isRelativeStatusBar() {
        return true;
    }

    @Override
    protected int getStatusBarColor() {
        return Color.parseColor("#00000000");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setIsTitleBar(false);

        super.onCreate(savedInstanceState);

        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //设置布局,里面有埋点按钮,详细看布局文件
        setContentView(R.layout.activity_init_face_featrue);

        mImageCache = new FaceImageCache();
        mRecognizeDataQueue = new ArrayBlockingQueue<RecognizeData>(5);
        mFeedFrameQueue = new ArrayBlockingQueue<CameraPreviewData>(1);
        initAndroidHandler();

        /* 初始化界面 */
        initView2();
        /* 申请程序所需权限 */
        if (!hasPermission()) {
            requestPermission();
        } else {
            initFacePassSDK();
        }

        initFaceHandler();



    }

    public static final String TAG="initFaceFeatrue";

    /**
     * @method name:
     * @des:  1,遍历指定的sd卡路径下的图片
     *         2，如果之前已经绑定了groupName，则continue
     *          3，根据图片路径，生成bitmap，调用sdk拿到token
     *           4，将此token绑定到group，并存入到sqlite数据库，使用desc这个字段，字段使用图片路径加英文逗号加token ： {图片路径}，[token]
     *
     *           StudentInfo各个字段说明：
     *           StudentInfo是之前济南版本的考生实体类，
     *            private Long id;数据库行id，框架提供
     *            private String studentUUID; userId，系统内唯一标识
     *             private String name; 考生姓名
     *             private String imageUrl; 图片下载路径
     *             private String desc; 图片路径加英文逗号加token ： {图片路径}，[token]
     *              private Long updateTime;更新时间
     *               private Long imageDownloadTime;

     *

     *
     * @param :
     * @return type:
     * @date 创建时间:2022/10/16
     * @author Chuck
     **/

    private void initFaceFeatrue(){
        new Thread(){
            @Override
            public void run() {
                //创建group


                boolean isSuccess = false;
                String groupName="facepass";
                try {
                    isSuccess = mFacePassHandler.createLocalGroup(groupName);
                } catch (FacePassException e) {
                    e.printStackTrace();
                }

                if(isLocalGroupExist){
                    isSuccess=true;
                }

                Log.i(TAG,"createLocalGroup,isSuccess："+isSuccess);
                Log.i(TAG,"createLocalGroup, groupName："+groupName);

                //选取照片

                if(isSuccess){

                    //Constants.FACE_IMAGES_PATH
                    String [] imagesPaths={"40.jpg","41.jpg","42.jpg","45.jpg"};

                    List<String> faceTokenList = new ArrayList<>();

                    try {
                        byte[][] faceTokens = mFacePassHandler.getLocalGroupInfo(groupName);
                        if (faceTokens != null && faceTokens.length > 0) {
                            for (int j = 0; j < faceTokens.length; j++) {
                                if (faceTokens[j].length > 0) {
                                    faceTokenList.add(new String(faceTokens[j]));
                                }
                            }
                        }

                        Log.i(TAG,"此groupName既有的token列表，faceTokenList:"+faceTokenList );

                    } catch (FacePassException e) {
                        e.printStackTrace();
                    }

                    int successCount=0;
                    int failureCount=0;
                    // TODO: 2022/10/16 此处应该做判断，本地磁盘的图片文件要有token关联。 根据文件绝对路径拿到token，然后判断此token是否已经被绑定过
                    for (int i = 0; i < imagesPaths.length; i++) {

                        boolean bindBefore=false;
                        StudentInfo old = DBHelper.getInstance(InitFaceFeatrueAct3.this).getStudentInfo(imagesPaths[i]);
                        if(old!=null){
                            if(old.getDesc()!=null){
                                String[] split = old.getDesc().split(",");
                                //需要判断，因为下载完成之后，是没有token的。
                                if(split.length==2){
                                    String token=old.getDesc().split(",")[1];
                                    if(!CollectionUtils.isNullOrEmpty(faceTokenList)){
                                        if(faceTokenList.contains(token)){
                                            bindBefore=true;
                                            successCount++;
                                            Log.i(TAG,"此照片已经被绑定过,path："+imagesPaths[i]);
                                        }
                                    }
                                }
                            }
                        }

                        if(bindBefore){
                            Log.i(TAG,"此照片已经被绑定过,循环将 continue" );

                            //界面绘制进度
                            onImporting(imagesPaths.length,  successCount,  failureCount, 1.0f*successCount/imagesPaths.length);


                            continue;
                        }

                        String imagePath= Constants.FACE_IMAGES_PATH+File.separator+imagesPaths[i];

                        Log.i(TAG,"imagePath:"+imagePath );

                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                        try {
                            FacePassAddFaceResult result = mFacePassHandler.addFace(bitmap);
                            Log.d("qujiaqi", "result:" + result
                                    + ",bl:" + result.blur
                                    + ",pp:" + result.pose.pitch
                                    + ",pr:" + result.pose.roll
                                    + ",py" + result.pose.yaw);
                            if (result != null) {
                                if (result.result == 0) {
                                    //toast("add face successfully！");
                                    Log.i(TAG,"add face successfully！,faceToken："+new String(result.faceToken));


                                    byte[] faceToken =result.faceToken;
                                    if (faceToken == null || faceToken.length == 0 || TextUtils.isEmpty(groupName)) {
                                        Log.i(TAG,"bindGroup,params error！" );
                                        failureCount++;

                                    }
                                    try {
                                        boolean b = mFacePassHandler.bindGroup(groupName, faceToken);
                                        String bindGroupResult = b ? "success " : "failed";
                                        Log.i(TAG,"bindGroupResult:  " + bindGroupResult);

                                        if(b){

                                            successCount++;

                                            StudentInfo student=new StudentInfo();
                                            student.setStudentUUID(imagesPaths[i]);
                                            student.setName(imagesPaths[i]);
                                            student.setImageUrl("");
                                            student.setDesc(imagesPaths[i]+","+new String(faceToken));
                                            student.setUpdateTime(System.currentTimeMillis());
                                            student.setImageDownloadTime(System.currentTimeMillis());

                                            DBHelper.getInstance(InitFaceFeatrueAct3.this).saveStudentInfo(student);

                                            successCount++;
                                            //界面绘制进度
                                            onImporting(imagesPaths.length,  successCount,  failureCount, 1.0f*successCount/imagesPaths.length);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e(TAG,"bindGroupResult,Exception:  " + e.getMessage());
                                        failureCount++;
                                    }


                                } else if (result.result == 1) {
                                   // toast("no face ！");
                                    Log.i(TAG,"add face failed,no face ！" );
                                    failureCount++;


                                } else {
                                    Log.i(TAG,"add face failed,quality problem！" );
                                    failureCount++;

                                    //toast("quality problem！");

                                }
                            }
                        } catch (FacePassException e) {
                            e.printStackTrace();
                            Log.i(TAG,"bindGroupResult,FacePassException:"+e.getMessage() );

                        }
                    }

                    //跳转到首页
                    mViewBind.ivBack.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(InitFaceFeatrueAct3.this, SelectSubjectActivity.class));
                            finish();
                        }
                    },2000);


                }

            }
        }.start();
    }

    private void initAndroidHandler() {

        mAndroidHandler = new Handler();

    }

    private void initFacePassSDK() {
        Context mContext = getApplicationContext();
        FacePassHandler.initSDK(mContext);
        FacePassHandler.authPrepare(mContext);
        FacePassHandler.getAuth(authIP, apiKey, apiSecret, true);
        Log.d("FacePassDemo", FacePassHandler.getVersion());

    }

    private void initFaceHandler() {

        new Thread() {
            @Override
            public void run() {
                while (true && !isFinishing()) {
                    while (FacePassHandler.isAvailable()) {
                        Log.d(DEBUG_TAG, "start to build FacePassHandler");
                        FacePassConfig config;
                        try {
                            /* 填入所需要的模型配置 */
                            config = new FacePassConfig();
                            config.poseBlurModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.pose_blur.arm.190630.bin");

                            config.livenessModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.CPU.rgb.G.bin");
                            if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
                                config.rgbIrLivenessModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.CPU.rgbir.G.bin");
                            }

                            config.searchModel = FacePassModel.initModel(getApplicationContext().getAssets(), "feat2.arm.K.v1.0_1core.bin");

                            config.detectModel = FacePassModel.initModel(getApplicationContext().getAssets(), "detector.arm.G.bin");
                            config.detectRectModel = FacePassModel.initModel(getApplicationContext().getAssets(), "detector_rect.arm.G.bin");
                            config.landmarkModel = FacePassModel.initModel(getApplicationContext().getAssets(), "pf.lmk.arm.E.bin");

                            config.rcAttributeModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.RC.arm.G.bin");
                            config.occlusionFilterModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.occlusion.arm.20201209.bin");
                            //config.smileModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.RC.arm.200815.bin");
                            //config.ageGenderModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.age_gender.arm.190630.bin");

                            /* 送识别阈值参数 */
                            config.rcAttributeAndOcclusionMode = 1;
                            config.searchThreshold = 65f;
                            config.livenessThreshold = 55f;
                            if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
                                config.livenessEnabled = false;
                                config.rgbIrLivenessEnabled = true;
                            } else {
                                config.livenessEnabled = true;
                                config.rgbIrLivenessEnabled = false;
                            }


                            config.poseThreshold = new FacePassPose(35f, 35f, 35f);
                            config.blurThreshold = 0.8f;
                            config.lowBrightnessThreshold = 30f;
                            config.highBrightnessThreshold = 210f;
                            config.brightnessSTDThreshold = 80f;
                            config.faceMinThreshold = 100;
                            config.retryCount = 10;
                            config.smileEnabled = false;
                            config.maxFaceEnabled = true;

                            config.fileRootPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

                            /* 创建SDK实例 */
                            mFacePassHandler = new FacePassHandler(config);

                            /* 入库阈值参数 */
                            FacePassConfig addFaceConfig = mFacePassHandler.getAddFaceConfig();
                            addFaceConfig.poseThreshold.pitch = 35f;
                            addFaceConfig.poseThreshold.roll = 35f;
                            addFaceConfig.poseThreshold.yaw = 35f;
                            addFaceConfig.blurThreshold = 0.7f;
                            addFaceConfig.lowBrightnessThreshold = 70f;
                            addFaceConfig.highBrightnessThreshold = 220f;
                            addFaceConfig.brightnessSTDThreshold = 60f;
                            addFaceConfig.faceMinThreshold = 100;
                            addFaceConfig.rcAttributeAndOcclusionMode = 2;
                            mFacePassHandler.setAddFaceConfig(addFaceConfig);

                            checkGroup();

                            initFaceFeatrue();
                        } catch (FacePassException e) {
                            e.printStackTrace();
                            Log.d(DEBUG_TAG, "FacePassHandler is null");
                            return;
                        }
                        return;
                    }
                    try {
                        /* 如果SDK初始化未完成则需等待 */
                        sleep(500);



                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onResume() {
        checkGroup();


        adaptFrameLayout();
        super.onResume();
    }


    private void checkGroup() {
        if (mFacePassHandler == null) {
            return;
        }
        try {
            String[] localGroups = mFacePassHandler.getLocalGroups();
            isLocalGroupExist = false;
            if (localGroups == null || localGroups.length == 0) {

                return;
            }
            for (String group : localGroups) {
                if (group_name.equals(group)) {
                    isLocalGroupExist = true;
                }
            }
            if (!isLocalGroupExist) {


            }
        } catch (FacePassException e) {
            e.printStackTrace();
        }
    }

    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
        if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
            ComplexFrameHelper.addRgbFrame(cameraPreviewData);
        } else {
            mFeedFrameQueue.offer(cameraPreviewData);
        }
    }



    int findidx(FacePassAgeGenderResult[] results, long trackId) {
        int result = -1;
        if (results == null) {
            return result;
        }
        for (int i = 0; i < results.length; ++i) {
            if (results[i].trackId == trackId) {
                return i;
            }
        }
        return result;
    }




    /* 判断程序是否有所需权限 android22以上需要自申请权限 */
    private boolean hasPermission() {
        Log.i("demo","hasPermission()");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Log.i("demo","Build.VERSION.SDK_INT >= Build.VERSION_CODES.M");


            return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /* 请求程序所需权限 */
    private void requestPermission() {
        Log.i("demo","requestPermission()");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Log.i("demo","Build.VERSION.SDK_INT >= Build.VERSION_CODES.M");


            requestPermissions(Permission, PERMISSIONS_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    granted = false;
            }
            if (!granted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    if (!shouldShowRequestPermissionRationale(PERMISSION_CAMERA)
                            || !shouldShowRequestPermissionRationale(PERMISSION_READ_STORAGE)
                            || !shouldShowRequestPermissionRationale(PERMISSION_WRITE_STORAGE)
                            || !shouldShowRequestPermissionRationale(PERMISSION_INTERNET)
                            || !shouldShowRequestPermissionRationale(PERMISSION_ACCESS_NETWORK_STATE)) {
                        Toast.makeText(getApplicationContext(), "需要开启摄像头网络文件存储权限", Toast.LENGTH_SHORT).show();
                    }
            } else {
                initFacePassSDK();
            }
        }
    }

    private void adaptFrameLayout() {
        SettingVar.isButtonInvisible = false;
        SettingVar.iscameraNeedConfig = false;
    }

    private void initToast() {
        SettingVar.isButtonInvisible = false;
    }

    private void initView2() {

    }


    @Override
    protected void onStop() {
        SettingVar.isButtonInvisible = false;
        mRecognizeDataQueue.clear();

        super.onStop();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onDestroy() {

        if (mAndroidHandler != null) {
            mAndroidHandler.removeCallbacksAndMessages(null);
        }

        if (mFacePassHandler != null) {
            mFacePassHandler.release();
        }
        super.onDestroy();
    }



    public void showToast(CharSequence text, int duration, boolean isSuccess, Bitmap bitmap) {

    }

    private static final int REQUEST_CODE_CHOOSE_PICK = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }





    private AlertDialog mFaceOperationDialog;


    private void toast(String msg) {
        Toast.makeText(InitFaceFeatrueAct3.this, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 根据facetoken下载图片缓存
     */
    private static class FaceImageCache implements ImageLoader.ImageCache {

        private static final int CACHE_SIZE = 6 * 1024 * 1024;

        LruCache<String, Bitmap> mCache;

        public FaceImageCache() {
            mCache = new LruCache<String, Bitmap>(CACHE_SIZE) {

                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }
    }


    /**
     * 正在导入，实时更新导入状态
     */

    public void onImporting(final int totalCount, final int successCount, final int failureCount,
                            final float progress) {

        LogUtil.i("initfacefeatrue","onImporting,progress:"+progress);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // mBtnDialogClose.setEnabled(false);    // 设置进度条“关闭”不可点击
                mViewBind.progressbarHint.setProgress((int) (progress * 100));

                mViewBind.tvFeatrueTotal.setText("数据总数：" + totalCount);
                mViewBind.tvFeatrueSucessed.setText("导入成功：" + successCount);
                mViewBind.tvFeatrueFailed.setText("导入失败：" + failureCount);

            }
        });
    }

}
