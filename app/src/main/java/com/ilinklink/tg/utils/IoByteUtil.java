package com.ilinklink.tg.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.util.Log;

import com.ilinklink.app.fw.BuildConfig;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * IoByteUtil
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2017/4/8  14:22
 * Copyright : 2014-2016 深圳趣动只能科技有限公司-版权所有
 **/
public class IoByteUtil {

    public static final int[] CRC_TABLE = {
            0X0000, 0X1021, 0X2042, 0X3063, 0X4084, 0X50A5, 0X60C6, 0X70E7,
            0X8108, 0X9129, 0XA14A, 0XB16B, 0XC18C, 0XD1AD, 0XE1CE, 0XF1EF,
            0X1231, 0X0210, 0X3273, 0X2252, 0X52B5, 0X4294, 0X72F7, 0X62D6,
            0X9339, 0X8318, 0XB37B, 0XA35A, 0XD3BD, 0XC39C, 0XF3FF, 0XE3DE,
            0X2462, 0X3443, 0X0420, 0X1401, 0X64E6, 0X74C7, 0X44A4, 0X5485,
            0XA56A, 0XB54B, 0X8528, 0X9509, 0XE5EE, 0XF5CF, 0XC5AC, 0XD58D,
            0X3653, 0X2672, 0X1611, 0X0630, 0X76D7, 0X66F6, 0X5695, 0X46B4,
            0XB75B, 0XA77A, 0X9719, 0X8738, 0XF7DF, 0XE7FE, 0XD79D, 0XC7BC,
            0X48C4, 0X58E5, 0X6886, 0X78A7, 0X0840, 0X1861, 0X2802, 0X3823,
            0XC9CC, 0XD9ED, 0XE98E, 0XF9AF, 0X8948, 0X9969, 0XA90A, 0XB92B,
            0X5AF5, 0X4AD4, 0X7AB7, 0X6A96, 0X1A71, 0X0A50, 0X3A33, 0X2A12,
            0XDBFD, 0XCBDC, 0XFBBF, 0XEB9E, 0X9B79, 0X8B58, 0XBB3B, 0XAB1A,
            0X6CA6, 0X7C87, 0X4CE4, 0X5CC5, 0X2C22, 0X3C03, 0X0C60, 0X1C41,
            0XEDAE, 0XFD8F, 0XCDEC, 0XDDCD, 0XAD2A, 0XBD0B, 0X8D68, 0X9D49,
            0X7E97, 0X6EB6, 0X5ED5, 0X4EF4, 0X3E13, 0X2E32, 0X1E51, 0X0E70,
            0XFF9F, 0XEFBE, 0XDFDD, 0XCFFC, 0XBF1B, 0XAF3A, 0X9F59, 0X8F78,
            0X9188, 0X81A9, 0XB1CA, 0XA1EB, 0XD10C, 0XC12D, 0XF14E, 0XE16F,
            0X1080, 0X00A1, 0X30C2, 0X20E3, 0X5004, 0X4025, 0X7046, 0X6067,
            0X83B9, 0X9398, 0XA3FB, 0XB3DA, 0XC33D, 0XD31C, 0XE37F, 0XF35E,
            0X02B1, 0X1290, 0X22F3, 0X32D2, 0X4235, 0X5214, 0X6277, 0X7256,
            0XB5EA, 0XA5CB, 0X95A8, 0X8589, 0XF56E, 0XE54F, 0XD52C, 0XC50D,
            0X34E2, 0X24C3, 0X14A0, 0X0481, 0X7466, 0X6447, 0X5424, 0X4405,
            0XA7DB, 0XB7FA, 0X8799, 0X97B8, 0XE75F, 0XF77E, 0XC71D, 0XD73C,
            0X26D3, 0X36F2, 0X0691, 0X16B0, 0X6657, 0X7676, 0X4615, 0X5634,
            0XD94C, 0XC96D, 0XF90E, 0XE92F, 0X99C8, 0X89E9, 0XB98A, 0XA9AB,
            0X5844, 0X4865, 0X7806, 0X6827, 0X18C0, 0X08E1, 0X3882, 0X28A3,
            0XCB7D, 0XDB5C, 0XEB3F, 0XFB1E, 0X8BF9, 0X9BD8, 0XABBB, 0XBB9A,
            0X4A75, 0X5A54, 0X6A37, 0X7A16, 0X0AF1, 0X1AD0, 0X2AB3, 0X3A92,
            0XFD2E, 0XED0F, 0XDD6C, 0XCD4D, 0XBDAA, 0XAD8B, 0X9DE8, 0X8DC9,
            0X7C26, 0X6C07, 0X5C64, 0X4C45, 0X3CA2, 0X2C83, 0X1CE0, 0X0CC1,
            0XEF1F, 0XFF3E, 0XCF5D, 0XDF7C, 0XAF9B, 0XBFBA, 0X8FD9, 0X9FF8,
            0X6E17, 0X7E36, 0X4E55, 0X5E74, 0X2E93, 0X3EB2, 0X0ED1, 0X1EF0
    };



    /**
     * @method name:calCRC16
     * @des:数组转为int
     * @param :[content]
     * @return type:int
     * @date 创建时间:2017/4/8
     * @author Chuck
     **/
    public static final int calCRC16(final byte[] content) {

        int length = content.length;
        int crc = 0;
        int i = 0;
        while (length-- > 0) {
            crc = CRC_TABLE[(int)((crc >> 8 ^ content[i++]) & 0xFF)] ^ (crc << 8);
        }
        return ~crc & 0xffff;
    }


    /**
     * @method name:unsignedShort2Bytes
     * @des:获取检验和,是无符号的两个字节的byte数组
     * @param :[src]
     * @return type:byte[]
     * @date 创建时间:2017/4/8
     * @author Chuck
     **/
    public static final byte[] unsignedShort2Bytes(int src) {
        byte[] dest = new byte[]{(byte)(255 & src >> 8), (byte)(255 & src)};
        return dest;
    }



    public static byte[] cutBytesByIndex(int index,byte[] content,int max){
        byte[] result=null;

        int PACKET_CONTENT_MAX=max>0?max:16;

        try {
            if(content!=null&&index>=0&&index<=content.length-1){
                int upSize = content.length;
                int count = upSize / PACKET_CONTENT_MAX;
                count += upSize % PACKET_CONTENT_MAX == 0 ? 0 : 1;
                int len;
                if (upSize > index * PACKET_CONTENT_MAX) {
                    len = PACKET_CONTENT_MAX;
                } else {
                    len = upSize - (index - 1) * PACKET_CONTENT_MAX;
                }
                byte[] con = new byte[len];

                System.arraycopy(content, (index - 1) * PACKET_CONTENT_MAX, con, 0, len);
                result=con;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return result;
        }
    }



    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }

    /**
     * the traditional io way
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    /**
     * NIO way
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray2(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
                // System.out.println("reading");
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray3(String filename) throws IOException {

        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(filename, "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size()).load();
            //System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                // System.out.println("remain");
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveYUV2Bitmap(byte[] data,int width, int height ) {

        ByteArrayOutputStream stream = null;
        FileOutputStream fileOutputStream=null;
        try {
            //HEIGHT_DEF, WIDTH_DEF
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width,height ,null);

            if (yuvImage != null) {
                stream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, width,height ), 100, stream);
                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());


                String filePath = Constants.QD_PERSONAL_DIR;
                String fileName = System.currentTimeMillis() + "";
                FileOutputStream fos = null;// 文件输出流
                // 判断是否有sdcard
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    File file = new File(filePath);
                    // 判断文件是否存在
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    // 判断是否有同名文件
                    File file2 = new File(filePath, fileName + ".jpg");
                    if (file2.exists()) {
                        file2 = new File(filePath, fileName + "_2" + ".jpg");
                    }
                    fileOutputStream = new FileOutputStream(file2);
                }

                try {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveYUV2Bitmap(byte[] data,int width, int height ,String path) {

        ByteArrayOutputStream stream = null;
        FileOutputStream fileOutputStream=null;
        try {
            //HEIGHT_DEF, WIDTH_DEF
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width,height ,null);

            if (yuvImage != null) {
                stream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, width,height ), 100, stream);
                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());



                FileOutputStream fos = null;// 文件输出流
                // 判断是否有sdcard
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    // 判断是否有同名文件
                    File file2 = new File(path);
                    if (file2.exists()) {
                        file2 = new File(path.replace(".jpg","_2.jpg"));
                    }
                    fileOutputStream = new FileOutputStream(file2);
                }

                try {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static byte[] yuv2JpgBytes(byte[] data,int width, int height  ) {

        ByteArrayOutputStream stream = null;
        byte[] result=null;
        try {
            //HEIGHT_DEF, WIDTH_DEF
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width,height ,null);

            if (yuvImage != null) {
                stream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, width,height ), 100, stream);
                result= stream.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }

    public static byte[] readFile(File file) {
        // 需要读取的文件，参数是文件的路径名加文件名
        if (file.isFile()) {
            // 以字节流方法读取文件

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                // 设置一个，每次 装载信息的容器
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                // 开始读取数据
                int len = 0;// 每次读取到的数据的长度
                while ((len = fis.read(buffer)) != -1) {// len值为-1时，表示没有数据了
                    // append方法往sb对象里面添加数据
                    outputStream.write(buffer, 0, len);
                }
                // 输出字符串
                return outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件不存在！");
        }
        return null;
    }

    public static void saveBytesToFile(byte[] vArr, String gcodeFile) {
        try {
            OutputStream out = new FileOutputStream(gcodeFile);
            InputStream is = new ByteArrayInputStream(vArr);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            is.close();
            out.close();
        }catch (Exception e){

        }
    }

    public static String getYuvFilePath(String trainId) {
        String path = null;
        try {
            path = Environment.getExternalStorageDirectory().getCanonicalPath() + "/"
                    +  BuildConfig.FILE_ROOT_NAME + "/YUV/"+trainId+"/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getCrashFilePath: " + path);
        return path;
    }

    public static void writeToFile(String mulu,String content){

        try {
            //byte[] bs = toHexString(YUV).getBytes();
            String now = DateFormatuUtil.getDate("yyyy-MM-dd HH:mm:ss");

            String CRASH_REPORTER_EXTENSION =now+ ".txt";

            String filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/"
                    +  BuildConfig.FILE_ROOT_NAME + "/log/" + mulu + CRASH_REPORTER_EXTENSION;
            FileWriter fwriter = new FileWriter(filePath, true);
            fwriter.write(content);
            fwriter.write("\r\n");
            fwriter.flush();
            fwriter.close();
        }catch (Exception e){

        }

    }
}

