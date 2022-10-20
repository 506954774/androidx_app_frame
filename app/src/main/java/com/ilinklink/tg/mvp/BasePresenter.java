package com.ilinklink.tg.mvp;

import android.graphics.Bitmap;

import com.ilinklink.tg.utils.LogUtil;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.module.network.RxHelper;

import rx.Subscription;

/**
 * BasePresenter
 * 控制器的基类,继承M是为了获取网络请求相关功能
 * Created By:Chuck
 * Des:
 * on 2018/12/7 12:52
 */
public class BasePresenter<T extends BaseView> extends BaseModel {

    protected T mView;//泛型,baseView的子类

    protected String TAG="";//日志标签

    public BasePresenter(T mView) {
        this.mView=mView;
        this.mActivity = mView.getActivityContext();
        mApi= RetrofitAPIManager.provideClientApi(this.mActivity);

        TAG=getClass().getSimpleName();
    }

    /**
     * @method name:onDestory
     * @des:释放资源
     * @param :[]
     * @return type:void
     * @date 创建时间:2018/12/7
     * @author Chuck
     **/
    public void onDestory(){
        try {
            for(Subscription s:mSubscriptions){
                LogUtil.e("Subscription",s);
                RxHelper.getInstance(this.mActivity).unsubscribe(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //是否压缩,对于宽高大于200的,压缩
    protected boolean needCompress(Bitmap bitmap) {
        try {
            return bitmap.getWidth()>200|| bitmap.getHeight()>200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface SmsCode {
        String SMS_TYPE_0001 = "0001"; // 注册
        String SMS_TYPE_0002 = "0002"; // 手机号找回密码
        String SMS_TYPE_0003 = "0003"; // 身份证号找回密
        String SMS_TYPE_0004 = "0004"; // 银行卡绑定

        String SMS_TYPE_0007 = "0007"; // 第三方注册短信验证码

        String SMS_TYPE_1001 = "1001"; // 修改手机号码
    }
}
