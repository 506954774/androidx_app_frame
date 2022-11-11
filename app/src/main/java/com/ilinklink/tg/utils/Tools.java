package com.ilinklink.tg.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.ilinklink.tg.communal.AppLoader;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.interfaces.CallBack;
import com.ilinklink.tg.mvp.login.LoginActivity;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.util.BitmapUtil;
import com.qdong.communal.library.util.DateFormatUtil;
import com.qdong.communal.library.util.DensityUtil;
import com.qdong.communal.library.util.FileUtils;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.qdong.communal.library.widget.CustomTagView.Tag;
import com.qdong.communal.library.widget.StarView4DataBinding.StarView4DataBinding;
import com.ilinklink.app.fw.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.qdong.communal.library.util.BitmapUtil.getSDcard;

/**
 * Tools
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/11/9  15:43
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class Tools {
    public static final String TAG = "Tools";

    private static final float RATIO_MIN = 0.75F;//瀑布流的高宽比的最小值
    private static final float RATIO_MAX = 1.33F;//高宽比最大值

    //原始的高宽,实际高
    public static int getHeight(int vWidth, int vHeight, int width) {
        LogUtil.e("Tools", "getHeight,vWidth:" + vWidth + ",vHeight:" + vHeight + ",width:" + width);
        if (vWidth <= 0 || vHeight <= 0 || width <= 0) {
            return 500;//参数错误的话,直接返回500像素
        }
        float k = 1.0f * vHeight / vWidth;//高宽比

        if (k > RATIO_MAX) {//设定最大值
            k = RATIO_MAX;//黄金比例
        }

        /****需求变更 2017/09/23*********/
        if (k < RATIO_MIN) {//设定最小值
            k = RATIO_MIN;//最少是方形的
        }

        int height = (int) (width * k);
        LogUtil.e("Tools", "计算获取的高度:" + height);
        return height;

    }
    //原始的高宽,实际高
    public static int getHeight2(int vWidth, int vHeight, int width) {
        LogUtil.e("Tools", "getHeight,vWidth:" + vWidth + ",vHeight:" + vHeight + ",width:" + width);
        if (vWidth <= 0 || vHeight <= 0 || width <= 0) {
            return 500;//参数错误的话,直接返回500像素
        }
        int height=0;

        if(vWidth>vHeight){
            height = width/16*9;
        }else {
            height = width/3*4;
        }



        LogUtil.e("Tools", "计算获取的高度:" + height);
        return height;

    }

    public static void setStars(StarView4DataBinding StarView4DataBinding, TextView tv, double score) {

        if (StarView4DataBinding != null && tv != null && score >= 0) {
            /**评分**/
            BigDecimal b = new BigDecimal(score);
            float f = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();//保留一位小数
            StarView4DataBinding.setDefaultChosen(f, "");
            tv.setText(f + "分");
        }
    }

    public static void loadHeadUrl(Context context, String tfs, ImageView iv, int round) {

        String url = null;
        if (tfs.startsWith("http")) {
            url = tfs;
        } else {
            url = com.qdong.communal.library.util.Constants.TFS_READ_URL + tfs;
        }

        //头像
        BitmapUtil.loadHead(
                context,
                url,
                 R.mipmap.album_placehold_image,
                 R.mipmap.album_placehold_image,

                round,
                iv);
    }

    public static void loadHeadUrl(Context context, String tfs, ImageView iv, int round, int holderIcon) {
        //头像
        BitmapUtil.loadHead(
                context,
                com.qdong.communal.library.util.Constants.TFS_READ_URL + tfs,
                holderIcon,
                holderIcon,
                round,
                iv);
    }

    public static void loadHeadPublicUrl(Context context, String tfs, ImageView iv, int round, int holderIcon) {
        //头像
        BitmapUtil.loadHead(
                context,
                tfs,
                holderIcon,
                holderIcon,
                round,
                iv);
    }


    /**
     * @param :[context, gif, iv]
     * @return type:void
     * @method name:loadGif
     * @des:加载gif
     * @date 创建时间:2019/1/17
     * @author Chuck
     **/
    public static void loadGif(Context context, String gif, ImageView iv) {
        try {
            Glide.with(context)
                    .load(gif)
                    .asGif()
                    .into(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param :[context, path, iv]
     * @return type:void
     * @method name:loadLocalPic
     * @des:加载本地图片
     * @date 创建时间:2019/1/17
     * @author Chuck
     **/
    public static void loadLocalPic(Context context, String path, ImageView iv) {

        if (TextUtils.isEmpty(path)) {
            return;
        }

        BitmapUtil.loadPic(
                context,
                path,
                 R.mipmap.album_placehold_image,

                 R.mipmap.album_placehold_image,

                iv);
    }


/**
     * @param :[context, path, iv]
     * @return type:void
     * @method name:loadLocalPic
     * @des:加载本地图片
     * @date 创建时间:2019/1/17
     * @author Chuck
     **/
    public static void loadRoundLocalPic(Context context, String path, ImageView iv,int round) {

        if (TextUtils.isEmpty(path)) {
            return;
        }

        BitmapUtil.loadPic(
                context,
                path,
                 R.mipmap.album_placehold_image,

                R.mipmap.album_placehold_image,

                iv,round);
    }

    /**
     * @param :[ll, tv, quantity, text:已经展示出来的点赞者的name]
     * @return type:void
     * @method name:setPraiseUserQuantity
     * @des:
     * @date 创建时间:2017/3/25
     * @author Chuck
     **/
    public static void setPraiseUserQuantity(LinearLayout ll, TextView tv, int quantity, String text) {


        if (tv != null && ll != null && !TextUtils.isEmpty(text)) {
            if (quantity == 0) {
                ll.setVisibility(View.GONE);
            } else {
                ll.setVisibility(View.VISIBLE);
                if (quantity <= 2) {
                    tv.setText("觉得很赞");
                } else {
                    tv.setText("等" + quantity + "人觉得很赞");
                }

            }
        }
    }


    /**
     * 判断字符串是否有效
     *
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        if (null == str || "".equals(str) || " ".equals(str) || "null".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否有效
     *
     * @return
     */
    public static boolean checkJsonNull(JsonElement jsonElement) {
        return jsonElement.isJsonNull();
    }

    //获取距离选择集合
    public static ArrayList<Tag> getDistanceSelections() {

        ArrayList<Tag> tags = new ArrayList<Tag>();

        String[] names = {"不限", "5公里", "10公里"};
        int[] ids = {0, 5000, 10 * 1000};

        for (int i = 0; i < names.length; i++) {
            Tag tag = new Tag();
            tag.setName(names[i]);
            tag.setId(ids[i]);
            if (tag.getId() == 0) {
                tag.setIsChecked(true);
            }
            tags.add(tag);
        }

        return tags;
    }

    //获取薪酬选择集合
    public static ArrayList<Tag> getSalarySelections() {

        ArrayList<Tag> tags = new ArrayList<Tag>();

        String[] names = {"不限", "5000元", "8000元"};
        int[] ids = {0, 5000, 8000};

        for (int i = 0; i < names.length; i++) {
            Tag tag = new Tag();
            tag.setName(names[i]);
            tag.setId(ids[i]);
            if (tag.getId() == 0) {
                tag.setIsChecked(true);
            }
            tags.add(tag);
        }

        return tags;
    }

    //拼接id
    public static String getIdsByTags(ArrayList<Tag> tags) {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; tags != null && i < tags.size(); i++) {
            Tag tag = tags.get(i);
            stringBuilder.append(tag.getId());
            if (i != tags.size() - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    //拼接name
    public static String getNamesByTags(ArrayList<Tag> tags) {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; tags != null && i < tags.size(); i++) {
            Tag tag = tags.get(i);
            stringBuilder.append(tag.getName());
            if (i != tags.size() - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * @param :[date:项目开始时间, quantitiesOfWeek:项目开始后的第几周的值再减一 ]
     * @return type:java.lang.String
     * @method name:getNewWeekOfYear
     * @des:考勤业务里,周考勤报表,界面上选取的是项目开始后的第几周,但是服务器需要的参数是自然周. 这个方法专门把项目开始后的第几周转换为自然周
     * 例如:要获取项目开始后的第1周对应的自然周,则第一参数传项目开始时间,第二个参数传0
     * @date 创建时间:2017/12/6
     * @author Chuck
     **/
    public static String getNewWeekOfYear(Date date, int quantitiesOfWeek) {
        if (date == null || quantitiesOfWeek < 0) {
            return "2018-1";//如果调用者传的参数不正确,则返回一个默认值,即2018年的第一周
        }

        //构造日历对象
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        //加天数
        cal.add(Calendar.DAY_OF_MONTH, quantitiesOfWeek * 7);

        //构造新的时间戳
        Date newDate = cal.getTime();

        //根据新的时间戳,获取自然周
        int newWeek = DateFormatUtil.getWeekOfYear(newDate);
        //拼接出数据,${年份}+"-"+${自然周}  ,格式:"2018-29"
        return DateFormatUtil.getDate(newDate, DateFormatUtil.Y) + DateFormatUtil.CONNECTOR + newWeek;
    }

    //切分,获取项目开始时间
    public static Date getDateBySplit(String workDate) {

        //workDate=2017.11.24-2018.11.23
        if (TextUtils.isEmpty(workDate) || !workDate.contains(DateFormatUtil.CONNECTOR)) {
            return null;
        }
        String time = workDate.split("-")[0];
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        try {
            return format.parse(time);
        } catch (ParseException e) {
            LogUtil.e("Tools", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param :[s]
     * @return type:java.lang.String
     * @method name:resetPath
     * @des: 避免待上传的图片出现相同的路径, 如果有, 则copy一份返回
     * @date 创建时间:2018/11/24
     * @author Chuck
     **/
    public static String resetPath(ArrayList<String> before, String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }

        if (before == null || before.size() == 0) {
            return s;
        }

        try {
            for (String b : before) {
                if (s.equals(b)) {//只要有相同的,则拷贝并返回
                    return FileUtils.copyFile(s);
                }
            }

            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public static String compressBitmapTo32BK(String srcPath) {
        int k = 32;//32kb
        String path = "";
        DisplayMetrics dm = new DisplayMetrics();
        float hh = DensityUtil.getDisplayHeight(AppLoader.getInstance());
        float ww = DensityUtil.getDisplayWidth(AppLoader.getInstance());
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        int size = 0;
        if (w <= ww && h <= hh) {
            size = 1;
        } else {
            double scale = w >= h ? w / ww : h / hh;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        bitmap = BitmapFactory.decodeFile(srcPath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        System.out.println(baos.toByteArray().length);
        while (baos.toByteArray().length > k * 1024) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 20;
            System.out.println(baos.toByteArray().length);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String filePath = getSDcard() + "/" + sdf.format(new Date()) + ".jpg";
            baos.writeTo(new FileOutputStream(filePath));
            path = filePath;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return path;
        }
    }

    public static String compressBitmapTo32BK(Bitmap bitmap) {
        int k = 32;//32kb
        String path = "";
        DisplayMetrics dm = new DisplayMetrics();
        float hh = DensityUtil.getDisplayHeight(AppLoader.getInstance());
        float ww = DensityUtil.getDisplayWidth(AppLoader.getInstance());
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inJustDecodeBounds = false;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int size = 0;
        if (w <= ww && h <= hh) {
            size = 1;
        } else {
            double scale = w >= h ? w / ww : h / hh;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        System.out.println(baos.toByteArray().length);
        while (baos.toByteArray().length > k * 1024 && quality > 0) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 20;
            //System.out.println(baos.toByteArray().length);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String filePath = getSDcard() + "/" + sdf.format(new Date()) + ".jpg";
            baos.writeTo(new FileOutputStream(filePath));
            path = filePath;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return path;
        }
    }

    /**
     * @param :[activity, big]
     * @return type:java.lang.String
     * @method name:compressBigImage
     * @des:全景图压缩
     * @date 创建时间:2019/1/19
     * @author Chuck
     **/
    public static String compressBigImage(Activity activity, String big) {

        try {
            // 设置参数
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
            //options.inPreferredConfig= Bitmap.Config.RGB_565;
            //options.inPreferredConfig = Bitmap.Config.RGB_565;//每个像素,16位,2个字节
            BitmapFactory.decodeFile(big, options);
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2

            //先获取
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mWidth = dm.widthPixels; // 当前分辨率 宽度 单位px
            int mHeight = dm.heightPixels; // 当前分辨率 高度 单位px
            float dpi = dm.densityDpi;//屏幕密度
            float density = dm.density;//密度
            LogUtil.i(TAG, "mWidth:" + mWidth + ",mHeight:" + mHeight + ",dpi:" + dpi + ",density:" + density);

            long memerySize = 10 * 1024 * 1024;//10M
            long memerySize2 = 20 * 1024 * 1024;//20M

            long size = memerySize;

            if (dpi > 320) {//
                size = memerySize2;
            }

            inSampleSize = (int) Math.sqrt(4 * height * width / size);


            LogUtil.i(TAG, "inSampleSize:" + inSampleSize); // 输出图像数据

            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
            options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
            //options.inPreferredConfig = Bitmap.Config.ALPHA_8;//每个像素,16位,1个字节

            Bitmap bm = BitmapFactory.decodeFile(big, options); // 解码文件
            LogUtil.i(TAG, "bitmap,size: " + bm.getByteCount() + " width: " + bm.getWidth() + " heigth:" + bm.getHeight()); // 输出图像数据

            return BitmapUtil.saveBmpToSdcard(bm, 100);
        } catch (Exception e) {
            LogUtil.e(TAG, "压缩异常:" + e.getMessage());
            return null;
        }
    }


    /**
     * @param :[width, height]
     * @return type:boolean
     * @method name:judgeBigImage
     * @des:是否属于大图
     * @date 创建时间:2019/1/21
     * @author Chuck
     **/
    public static boolean judgeBigImage(int width, int height,boolean toast) {
        boolean isBig = width > Constants.DEFAULT_THRESHOLD_WIDTH || height > Constants.DEFAULT_THRESHOLD_HEIGHT;
        if (isBig&&toast) {
            ToastHelper.showCustomMessage(AppLoader.getInstance().getString(R.string.photo_too_big_to_edit));
        }
        return isBig;
    }

    /**
     * @method name:judgeBigImage2
     * @des:判断是否是大图,4000像素就算大图
     * @param :[width, height, toast]
     * @return type:boolean
     * @date 创建时间:2019/8/8
     * @author Chuck
     **/
    public static boolean judgeBigImage2(int width, int height,boolean toast) {

        boolean isBig=false;
        if ( height <= 0 || width <= 0) {
            return false;
        } else {
            float ratio = 1.0f * height / width;

            if (height < THRESHOLD_SMALL && width < THRESHOLD_SMALL) {
                isBig = false;
            } else if (height >= THRESHOLD_BIG || (width > THRESHOLD_SMALL && ratio > THRESHOLD_RATIO_BIG)) {
                isBig=true;
            } else if (width >= THRESHOLD_BIG || (height > THRESHOLD_SMALL && ratio < THRESHOLD_RATIO_SMALL)) {
                isBig=true;
            }
        }

        if (isBig&&toast) {
            ToastHelper.showCustomMessage(AppLoader.getInstance().getString(R.string.photo_too_big_to_edit));
        }
        return isBig;
    }


    /**
     * @param :[imageUrl, height, width]
     * @return type:java.lang.String
     * @method name:getThumbnailByHeightAndWidth
     * @des:縮略圖拼接
     * @date 创建时间:2019/3/1
     * @author Chuck
     **/
    public static String getThumbnailByHeightAndWidth(String imageUrl, int height, int width) {

        if (TextUtils.isEmpty(imageUrl) || height <= 0 || width <= 0) {
            return null;
        } else {
            float ratio = 1.0f * height / width;

            if (height < THRESHOLD_SMALL && width < THRESHOLD_SMALL) {
                return imageUrl + _SMALL;
            } else if (height >= THRESHOLD_SMALL && width >= THRESHOLD_SMALL && height < THRESHOLD_BIG && width < THRESHOLD_BIG) {
                return imageUrl + _BIG;
            } else if (height >= THRESHOLD_BIG || (width > THRESHOLD_SMALL && ratio > THRESHOLD_RATIO_BIG)) {
                return imageUrl + _HEIGHT;
            } else if (width >= THRESHOLD_BIG || (height > THRESHOLD_SMALL && ratio < THRESHOLD_RATIO_SMALL)) {
                return imageUrl + _WIDTH;
            } else {
                return imageUrl + _BIG;
            }
        }
    }

    public static final int THRESHOLD_SMALL = 300;
    public static final int THRESHOLD_BIG = 4000;
    public static final float THRESHOLD_RATIO_SMALL = 0.075f;
    public static final float THRESHOLD_RATIO_BIG = 13f;
    public static final String _SMALL = "_small";
    public static final String _BIG = "_big";
    public static final String _HEIGHT = "_height";
    public static final String _WIDTH = "_width";


    /**
     * @param :[activity]
     * @return type:void
     * @method name:quitApp
     * @des:踢下线的功能
     * @date 创建时间:2019/4/29
     * @author Chuck
     **/
    public static void quitApp(Activity activity) {


        CallBack callBack1 = new CallBack() {
            @Override
            public void callBack(Object object) {
                if (object != null && object instanceof Boolean) {
                    boolean commit = (boolean) object;
                    if (commit) {//true:确认  false :取消
                        //ToastHelper.showCustomMessage("delete");
                        // 1.清空本地登录数据
                        //缓存token
                        SharedPreferencesUtil.getInstance(activity).removeKey(SharedPreferencesUtil.ACCESS_TOKEN);
                        RetrofitAPIManager.ACCESSTOKEN = "1";//token
                        //清空本地数据库:
                        DBHelper.getInstance(activity).deleteAllLoginModel();
                        // 2.退出融云(IM)

                        activity.sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        //activity.finish();
                    }
                }
            }
        };


    }

    public static String compressBitmapTo500BK(String srcPath) {
        int k = 500;//32kb
        String path = "";
        DisplayMetrics dm = new DisplayMetrics();
        float hh = DensityUtil.getDisplayHeight(AppLoader.getInstance());
        float ww = DensityUtil.getDisplayWidth(AppLoader.getInstance());
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap;
        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        int size = 0;
        if (w <= ww && h <= hh) {
            size = 1;
        } else {
            double scale = w >= h ? w / ww : h / hh;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        bitmap = BitmapFactory.decodeFile(srcPath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        System.out.println(baos.toByteArray().length);
        while (baos.toByteArray().length > k * 1024) {
            baos.reset();
            if (quality<=0){
                quality=1;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 20;
            System.out.println(baos.toByteArray().length);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String filePath = getSDcard() + "/" + sdf.format(new Date()) + ".jpg";
            baos.writeTo(new FileOutputStream(filePath));
            path = filePath;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return path;
        }
    }


    public static InputFilter[] getMoneyFormatFilter(){
        InputFilter lengthFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // source:当前输入的字符
                // start:输入字符的开始位置
                // end:输入字符的结束位置
                // dest：当前已显示的内容
                // dstart:当前光标开始位置
                // dent:当前光标结束位置
                //Log.e("", "source=" + source + ",start=" + start + ",end=" + end + ",dest=" + dest.toString() + ",dstart=" + dstart + ",dend=" + dend);
                if (dest.length() == 0 && source.equals(".")) {
                    return "0.";
                }
                String dValue = dest.toString();
                String[] splitArray = dValue.split("\\.");
                if (splitArray.length > 1) {
                    String dotValue = splitArray[1];
//                if (dotValue.length() == 2) {//输入框小数的位数是2的情况，整个输入框都不允许输入
//                    return "";
//                }
                    if (dotValue.length() == 2 && dest.length() - dstart < 3){ //输入框小数的位数是2的情况时小数位不可以输入，整数位可以正常输入
                        return "";
                    }
                }
                return null;
            }
        };
        return new InputFilter[]{lengthFilter};
    }

    public static boolean checkNull(String str){

        return TextUtils.isEmpty(str) || str.equals("null") || str.equals("nothing");

    }

    /**
     * @method name:objectToMap
     * @des:对象转为map
     * @param :[obj]
     * @return type:java.util.HashMap<java.lang.String,java.lang.Object>
     * @date 创建时间:2019/3/23
     * @author Chuck
     **/
    public static HashMap<String, Object> objectToMap(Object obj) throws Exception {
        if(obj == null){
            return null;
        }

        HashMap<String, Object> map = new HashMap<String, Object>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }

        return map;
    }

    /**
     * @param :[context, tfs, iv]
     * @return type:void
     * @method name:loadWebPic
     * @des:加载网络图片
     * @date 创建时间:2019/1/17
     * @author Chuck
     **/
    public static void loadWebPic(Context context, String tfs, ImageView iv) {

        if (TextUtils.isEmpty(tfs)) {
            return;
        }

        String url = null;
        if (tfs.startsWith("http")) {
            url = tfs;
        } else {//拼接域名
            //url=com.qdong.communal.library.util.Constants.TFS_READ_URL+tfs;
            url = com.qdong.communal.library.util.Constants.TFS_READ_URL+tfs;
        }

        BitmapUtil.loadPic(
                context,
                url,
                R.mipmap.album_placehold_image,

                R.mipmap.album_placehold_image,

                iv);
    }

    public static String getChineseNum(int index){
        String result="0";
        switch (index){
            case 0:
                result="一";
                break;
            case 1:
                result="二";
                break;
            case 2:
                result="三";
                break;
            case 3:
                result="四";
                break;
            case 4:
                result="五";
                break;
            case 5:
                result="六";
                break;
            case 6:
                result="七";
                break;
            case 7:
                result="八";
                break;
            case 8:
                result="九";
                break;
            case 9:
                result="十";
                break;
            case 10:
                result="十一";
                break;
            case 11:
                result="十二";
                break;
            case 12:
                result="十三";
                break;
            case 13:
                result="十四";
                break;
            case 14:
                result="十五";
                break;
            case 15:
                result="十六";
                break;
            case 16:
                result="十七";
                break;
            case 17:
                result="十八";
                break;
            case 18:
                result="十九";
                break;
            case 19:
                result="二十";
                break;
            case 20:
                result="二十一";
                break;
            case 21:
                result="二十二";
                break;
                default:
                    result=index+"";
                    break;
        }

        return result;
    }




    public static int parsePoseCount(List<String> poseClassification){
        //[down : 2 reps, up : 0.22 confidence]
        if(!CollectionUtils.isNullOrEmpty(poseClassification)){
            for (String data:poseClassification){
                if(data!=null&&data.contains("reps")){
                    String[] s = data.split(" ");
                    if(s.length>2){
                        return Integer.parseInt(s[2]);
                    }
                }
            }
        }
        return 0;
    }


    /**
     * 读取json配置文件
     * @param context
     * @param path
     * @return
     */
    public static String readLocalJson(Context context, String path) throws Exception{
        String jsonString="";

        String resultString="";


        InputStream inputStream=new FileInputStream(path);

        byte[] buffer=new byte[inputStream.available()];

        inputStream.read(buffer);

        resultString=new String(buffer,"GB2312");



        return resultString;

    }
}
