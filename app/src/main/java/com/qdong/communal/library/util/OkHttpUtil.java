package com.qdong.communal.library.util;


import android.util.Log;

import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.SdCardLogUtil;
import com.qdong.communal.library.module.VersionManager.FileUtils;
import com.qdong.communal.library.module.VersionManager.TaskEntity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OkHttpUtil
 *
 * @author LHD
 * @date 2016-06-28 10:26
 *
 */
public class OkHttpUtil {

	public static OkHttpClient httpClient;
	private static final MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("application/octet-stream");

	static {
		/***
		 * 超时设置
		 */
		httpClient = new OkHttpClient.Builder()
				.connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60*10, TimeUnit.SECONDS)
				.writeTimeout(60*10, TimeUnit.SECONDS).build();
		/*httpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(BicycleApplication.getContext()),
				CookiePolicy.ACCEPT_ORIGINAL_SERVER));*/
	}

	/**
	 * 使用GET请求数据
	 *
	 *            请求地址
	 * @return 成功：返回获取到的数据，失败：返回null
	 * @throws Exception
	 */
	public static String doGet(TaskEntity entity) throws Exception {
		Request request = new Request.Builder().url(entity.getUrl()).build();
		Call call = httpClient.newCall(request);
		entity.setCall(call);
		Response response = call.execute();
		if (response.isSuccessful())
			return response.body().string();
		return null;
	}

	/**
	 * 使用POST发送JSON数据到服务器
	 *
	 *            需要传递的数据
	 * @return 成功：返回获取到的数据，失败：返回null
	 * @throws Exception
	 */
	public static String doPost(TaskEntity entity) throws Exception {
		RequestBody post = RequestBody.create(JSONTYPE, entity.getRequest());
		Request request = new Request.Builder().url(entity.getUrl()).post(post).build();
		Call call = httpClient.newCall(request);
		entity.setCall(call);
		Response response = call.execute();
		if (response.isSuccessful())
			return response.body().string();
		return null;
	}





	/**
	 * 使用get请求获取数据
	 *
	 * @param URL
	 *            文件地址
	 */
	public static ResponseBody downFile(String URL) throws Exception {
		Request request = new Request.Builder().url(URL).build();
		Call call = httpClient.newCall(request);
		Response response = call.execute();
		if (response.isSuccessful())
			return response.body();
		return null;
	}


	public static String downloadFile(String url,String filePath)throws Exception{
		String tag="downLoadFaceImages";
		log(tag,"准备下载图片,url:"+url);
		ResponseBody body =OkHttpUtil.downFile(url);

		if (body == null) {
			Log.i("insertAndDownloadImage", "insertAndDownloadImage,下载失败,OkHttpUtil.downFile(url)=null,原始url:" + url);
			log(tag,"下载失败,OkHttpUtil.downFile(url)=null");
			return "";
		}

		File file=new File(filePath);

		log(tag,"filePath:"+filePath+",file.exists():"+file.exists());


		boolean dowaload=false;
		InputStream is=body.byteStream();

		if (is != null && file != null) {
			log(tag,"准备io操作");
			if (!file.getParentFile().exists()) {
				log(tag,"!file.getParentFile().exists(),准备mkdirs");
				file.getParentFile().mkdirs();
			}
			FileOutputStream os = new FileOutputStream(file);
			long fileSize=body.contentLength();

			log(tag,"fileSize:"+fileSize);

			dowaload= readData(is, os, fileSize);
		}
		else {
			log(tag,"下载失败:InputStream==null || file==null");
		}

		log(tag,"dowaload:"+dowaload);

		if (dowaload) {
			log(tag,"下载成功,绝对路径:"+file.getAbsoluteFile().getAbsolutePath());
			return file.getAbsoluteFile().getAbsolutePath();
		}
		return "";

	}

	/**
	 * 根据资源输入流创建本地文件并存储到文件中
	 *
	 * @param is
	 *            输入流
	 * @param file
	 *            文件对象
	 * @param
	 * @throws IOException
	 */
	public static boolean save(InputStream is, File file, long fileSize) throws IOException {
		if (is != null && file != null) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileOutputStream os = new FileOutputStream(file);
			return readData(is, os, fileSize);
		}
		return false;
	}

	/**
	 * 从指定资源输入流中往文件输出流中交换数据
	 *
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static boolean readData(InputStream is, OutputStream os, long fileSize) throws IOException {

		log("downLoadFaceImages","io操作,readData");
		boolean isLoad = false;
		// fileSize = 28835840;
		if (is != null && os != null) {
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int len = 0;
			long downloadFileSize = 0;
			byte[] data = new byte[1024];
			while ((len = bis.read(data)) != -1) {
				try {
					downloadFileSize += len;
					bos.write(data, 0, len);
					if (len % 1024 == 0) {
						// 计算下载进度比例
						double loadSize = downloadFileSize * 1.0;
						double count = loadSize / fileSize;
						int index = (int) (count * 100);

					}
					if (fileSize <= downloadFileSize) {
						isLoad = true;
					}
					bos.flush();
				} catch (Exception e) {
					log("downLoadFaceImages","下载失败:"+e.getMessage());
				}
			}

			// 关闭流操作
			is.close();
			os.close();
			bis.close();
			bos.close();
		}
		return isLoad;
	}

	private static String writeFile(ResponseBody body, String filePath ) throws Exception {
		if (body == null) {
			return "";
		}

		File file=new File(filePath);
		if (save(body.byteStream(), file, body.contentLength())) {
			return file.getAbsoluteFile().getAbsolutePath();
		}
		return "";
	}

	private static void log(String TAG,String message){
		SdCardLogUtil.logInSdCard(TAG,TAG+","+message);
		LogUtil.e(TAG,message);
	}
}
