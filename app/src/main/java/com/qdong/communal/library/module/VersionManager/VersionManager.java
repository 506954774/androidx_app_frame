package com.qdong.communal.library.module.VersionManager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.ilinklink.app.fw.R;

import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.util.Constants;
import com.qdong.communal.library.util.DateFormatUtil;
import com.qdong.communal.library.util.JsonUtil;
import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.qdong.communal.library.util.ToastUtil;

import org.json.JSONException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 版本管理
 *
 * @Time 2015-12-17
 * @author LHD
 *
 */
public class VersionManager {

	//private static final String HOST_URL ="http://tryingdo.cn/" ;
	private static final String HOST_URL = Constants.SERVER_URL;

	public static final String DOWNLOAD_START_PROGRESS="0";
	public static final int DOWNLOAD_PROGRESS_MAX=100-1;//实际只能到99,到不了100
	private static final String MAIN_APPLICATION_ID = "com.qmzhhchsh.yp";//主app的id

	private UpgradeEntity upgradeEntity;
	private Activity activity;
	private FileLoader loader;
	private boolean isLoading;
	private NotificationCompat.Builder builder;
	private long lastTime = 0;
	private String className;

	private boolean mIsShowingDialog;//是否正展示着升级的dialog
	private Subscription mSubscription;
	private VersionCheckerCallBack mVersionCheckerCallBack;
	private boolean isAuto;

	public VersionManager(Activity activity, VersionCheckerCallBack mVersionCheckerCallBack) {
		this.activity = activity;
		this.mVersionCheckerCallBack = mVersionCheckerCallBack;
	}





	/** Bugfix-0412-20160413-LHD-START */
	// 修改界面标记设置方式
	public void setTag(String name) {
		this.className = name;
	}
	/** Bugfix-0412-20160413-LHD-END */

	/**
	 * 请求服务器数据
	 */
	public void checkVersion(boolean isAuto) {

		this.isAuto=isAuto;
		if(mVersionCheckerCallBack !=null){
			if(!isAuto){
				mVersionCheckerCallBack.showLoadingView();
			}
		}

		Observer<LinkLinkNetInfo> ob =new Observer<LinkLinkNetInfo>() {
			@Override
			public void onCompleted() {
				try {
					mSubscription.unsubscribe();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable e) {

				LogUtil.e("VersionManager","e:"+e.getMessage());

				if(mVersionCheckerCallBack !=null){
						mVersionCheckerCallBack.dismissLoadingView();
					mVersionCheckerCallBack.onError();
				}
			}

			@Override
			public void onNext(LinkLinkNetInfo qDongNetInfo) {
				LogUtil.e("VersionManager","qDongNetInfo:"+qDongNetInfo.toString());

				//YiPaiUpgradeEntity


				mVersionCheckerCallBack.dismissLoadingView();
				if(qDongNetInfo.isSuccess()&&qDongNetInfo.getData()!=null){

					try {
						//是个集合
						List<YiPaiUpgradeEntity> list = JsonUtil.toList(qDongNetInfo.getData(),YiPaiUpgradeEntity.class);


						if(list!=null&&list.size()>0){
							YiPaiUpgradeEntity bean =list.get(0);
							if(bean!=null){
								upgradeEntity=new UpgradeEntity();
								upgradeEntity.setUploadUrl(bean.getDownloadurl());
								upgradeEntity.setVersionName(bean.getVersion());
								upgradeEntity.setDescription(bean.getUpdatamessage());
								//upgradeEntity.setIsForceUpgrade("1".equals(bean.getNeedupdate())?1:0);

								if(getVersionName(activity).equals(bean.getVersion())){
									upgradeEntity=null;//版本一样,则无视
								}
								else if(getVersionName(activity).compareTo(bean.getVersion())>0){
									upgradeEntity=null;//版本比服务器还高,则无视
								}
								else {//版本比服务器小,而且强制升级
									//
                                     if(getVersionName(activity).compareTo(bean.getVersion())<0&&bean.getNeedupdate().equals("1")){
                                                   upgradeEntity.setIsForceUpgrade(1);//强制升级
                                                   upgradeEntity.setIsEnd(1);//立马就得升级
									 }
								}
							}
                        }

						//upgradeEntity = JsonUtil.fromJson(qDongNetInfo.getResult(),UpgradeEntity.class);
						upgradeVersionPrompt(upgradeEntity);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else {
					if(mVersionCheckerCallBack !=null){
						mVersionCheckerCallBack.onError();
					}
				}
			}
		};

		Map<String, Object> map=new HashMap<>();
		map.put("idf","A-yipai20");
		mSubscription= RetrofitAPIManager.provideClientApi(activity,HOST_URL).findLatestVersion(map)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(ob);




		//PreferenceUtil.put(activity, "check_Version", System.currentTimeMillis());
		SharedPreferencesUtil.getInstance(activity).putLong("check_Version", System.currentTimeMillis());
	}

	/**
	 * 自动检测版本
	 */
	public void autoCheck() {
		//long time = PreferenceUtil.getLong(activity, "check_Version");
		long time = SharedPreferencesUtil.getInstance(activity).getLong("check_Version", 0);

		long now = System.currentTimeMillis();
		//long limit=86400000;
		long limit=0;
		if (now - time > limit) { //每次进来都检测
			checkVersion(true);
		}
	}

	/**
	 * 获取应用版本码
	 *
	 * @return 版本号
	 */
	public static String getVersionName(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

			LogUtil.i("VersionManager","getVersionName:"+info.versionName);

			return info.versionName;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 版本提示
	 */
	private void upgradeVersionPrompt(final UpgradeEntity entity) {
		if (entity != null) {

			/**升级弹出框正在展示**/
			setmIsShowingDialog(true);

			final CustomPromptDialog dialog = new CustomPromptDialog(activity);

			dialog.setOnDissmissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialogInterface) {
					/**把事件给调用者**/
					if(mVersionCheckerCallBack !=null&&(entity.getIsForceUpgrade()==1&&entity.getIsEnd()==1)==false){
						mVersionCheckerCallBack.onDismissed();
					}
				}
			});

			/** Bugfix-0412-20160413-LHD-START */
			// 去除Dialog时，版本显示Vv中的V


			/***是否强制升级**/
			String IsForceUpgrade="";
			if(entity.getIsForceUpgrade()==1){

				if(entity.getIsEnd()==1){//时间到
					IsForceUpgrade="\n\n"+activity.getString(R.string.check_version_hint1);
				}
				else{
					IsForceUpgrade="\n\n"+activity.getString(R.string.check_version_time_hint1)
							+ DateFormatUtil.getDate(DateFormatUtil.YMD, entity.getForceUpgradeTime())
							+activity.getString(R.string.check_version_time_hint2);
				}

				dialog.setPromptText(activity.getString(R.string.check_version_force_update), entity.getDescription()+IsForceUpgrade, activity.getString(R.string.lib_commit));
			}
			else{
				dialog.setPromptText(activity.getString(R.string.check_version_update) + entity.getVersionName(), entity.getDescription()+IsForceUpgrade, activity.getString(R.string.lib_commit));
			}



			/** Bugfix-0412-20160413-LHD-END */
			dialog.getConfirm().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					/**升级弹出框消除**/
					ToastUtil.showCustomMessage(activity,activity.getString(R.string.check_version_updating));
					setmIsShowingDialog(false);
					dialog.dialogDismiss();
					if (isLoading) {
						//Tools.showShort(activity, "已在下载中");
						ToastUtil.showCustomMessage(activity,activity.getString(R.string.check_version_updating));
						return;
					}
					download(entity);
				}
			});

			/***是否强制升级,且时间到了**/
			if(entity.getIsForceUpgrade()==1&&entity.getIsEnd()==1){
				dialog.getCancel().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						/**升级弹出框消除**/
						setmIsShowingDialog(false);
						dialog.dialogDismiss();

						//Tools.showShort(activity, "抱歉,当前版本过低,无法使用!");

						/**把事件给调用者**/
						if(mVersionCheckerCallBack !=null){
							mVersionCheckerCallBack.forceCloseApp();
						}
					}
				});
			}



		} else {
			//Tools.showShort(activity, "已是最新版本");
			/*if(!isAuto){
				ToastUtil.showCustomMessage(activity,activity.getString(R.string.check_version_is_the_very_latest));
			}*/

			if(mVersionCheckerCallBack!=null){
				mVersionCheckerCallBack.noNewVersion();
			}

		}
	}

	/**
	 * 下载文件
	 *
	 * @param entity
	 */
	private void download(UpgradeEntity entity) {

		//把下载进度给调用者,在actiivty里展示
		if(mVersionCheckerCallBack!=null){
			mVersionCheckerCallBack.onProgressUpdate(DOWNLOAD_START_PROGRESS);
		}

		isLoading = true;
		// 调取异步任务下载文件
		loader = new FileLoader(activity) {
			protected void onPostExecute(String result) {
				/** Bugfix-0412-20160412-yyh-START */
				// 增加文件写入错误版本处理
				if (result.equals("error") || "fail".equals(result)) {
					//loadError("下载失败");
					loadError(activity.getString(R.string.check_version_download_failed));
				} else {
					loadEnd(result);
				}
				/** Bugfix-0412-20160412-yyh-END */
				isLoading = false;
				exit();
				loader = null;
			}

			protected void onProgressUpdate(String... values) {
				updateProgress(Integer.valueOf(values[0]), values[1]);

				//把下载进度给调用者,在actiivty里展示
				if(mVersionCheckerCallBack!=null){
					mVersionCheckerCallBack.onProgressUpdate(values);
				}
			}
		};
		loader.execute(entity);// 执行下载
	}

	/**
	 * 初始化通知对象
	 */
	private void initNoti() {
		String title = activity.getString(R.string.app_name)+"_" + upgradeEntity.getVersionName();
		builder = new NotificationCompat.Builder(activity).setSmallIcon(R.mipmap.icon_logo)
				// 设置状态栏的小标题
				.setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_download))
				// 设置下拉列表里的图标
				.setWhen(System.currentTimeMillis()).setTicker(activity.getString(R.string.app_name)+"下载")// 设置状态栏的显示的信息
				.setContentTitle(title); // 设置下拉列表里的标题
	}

	/**
	 * 更新进度
	 *
	 * @param position
	 */
	private void updateProgress(int position, String text) {
		long nowTime = System.currentTimeMillis();
		if (nowTime - lastTime < 500)
			return;
		lastTime = nowTime;
		initNoti();
		// 1 得到通知管理器
		NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		builder.setProgress(100, position, false).setAutoCancel(false).setContentText(text);// 设置可以清除;
		Notification notification = builder.build();// API 16添加创建notification的方法
		notification.flags = Notification.FLAG_NO_CLEAR;
		manager.notify(1111, notification);
	}

	/**
	 * 取消通知对象,并安装程序
	 */
	private void loadEnd(String fileName) {
		NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(1111);
		/*Intent app = new Intent(Intent.ACTION_VIEW);
		app.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		app.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
		activity.startActivity(app);*/

		Log.i("DOWNLOAD:","fileName:"+fileName);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		//判断是否是AndroidN以及更高的版本
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			//Uri contentUri = FileProvider.getUriForFile(activity, MAIN_APPLICATION_ID + ".fileProvider", new File(fileName));
			Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".FileProvider", new File(fileName));
			intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
		} else {
			intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		activity.startActivity(intent);



	}

	/**
	 * 下载错误
	 */
	private void loadError(String text) {
		initNoti();
		Intent intent = new Intent(activity, activity.getClass());
		PendingIntent contentIntent = PendingIntent.getActivity(activity, 100, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);// FLAG_UPDATE_CURRENT显示最新的
		NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		builder.setContentText(text).setContentIntent(contentIntent).setAutoCancel(true);
		Notification notification = builder.build();// API 16添加创建notification的方法
		manager.notify(1111, notification);
	}

	public void exit() {
		if (loader != null) {
			loader.cancel(true);
			loader = null;
		}
		activity = null;
	}

	public boolean ismIsShowingDialog() {
		return mIsShowingDialog;
	}

	public void setmIsShowingDialog(boolean mIsShowingDialog) {
		this.mIsShowingDialog = mIsShowingDialog;
	}




}
