package com.qdong.communal.library.module.network;

import android.content.Context;

import android.text.TextUtils;

import com.qdong.communal.library.util.Constants;
import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * RetrofitAPIManager
 * http的管理类,为了避免通过注解去写header,使用此类统一管理
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/6/28  11:46
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class RetrofitAPIManager {

    private static final long DEFAULT_CONNECT_TIMEOUT = 100_000;//连接时间,100秒
    private static final long DEFAULT_WRITE_TIMEOUT =300_000 ;//写,300秒
    private static final long DEFAULT_READ_TIMEOUT = 300_000;//读,300秒
    public static  String JSESSIONID="1";
    public static  String CLIENT_KEY="1";//为何要设置为公开?外部可以直接改,不必给这个设置上下文
    public static  String ACCESSTOKEN="1";//为何要设置为公开?外部可以直接改,不必给这个设置上下文



    public static LinkLinkApi provideClientApi(Context context) {//初始化时,把sessionId取出来
        return provideClientApi(context,false);
    }

    public static LinkLinkApi provideClientApi(Context context, String url) {
        return provideClientApi(context, url,false);
    }

    public static LinkLinkApi provideFormClient(Context context, String url) {
        JSESSIONID= SharedPreferencesUtil.getInstance(context).getString(Constants.SESSION_ID,"-1");
        ACCESSTOKEN= SharedPreferencesUtil.getInstance(context).getString(SharedPreferencesUtil.ACCESS_TOKEN,"-1");
        CLIENT_KEY= SharedPreferencesUtil.getInstance(context).getString(SharedPreferencesUtil.CLIENT_KEY,"-1");
        Retrofit retrofit =   new Retrofit.Builder()
                .client(genericFormClient())
                .addConverterFactory(GsonConverterFactory.create())//json解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//调用适配器
                .baseUrl(TextUtils.isEmpty(url)?Constants.SERVER_URL:url)
                .build();
        return retrofit.create(LinkLinkApi.class);
    }

    /**
     * @method name:provideClientApi
     * @des:获取client
     * @param :[context, isFileUpload:是不是文件上传的任务]
     * @return type:com.qdong.communal.library.module.network.QDongApi
     * @date 创建时间:2016/8/25
     * @author Chuck
     **/
    public static LinkLinkApi provideClientApi(Context context, boolean isFileUpload) {//初始化时,把sessionId取出来

        JSESSIONID= SharedPreferencesUtil.getInstance(context).getString(Constants.SESSION_ID,"-1");
        ACCESSTOKEN= SharedPreferencesUtil.getInstance(context).getString(SharedPreferencesUtil.ACCESS_TOKEN,"-1");
        CLIENT_KEY= SharedPreferencesUtil.getInstance(context).getString(SharedPreferencesUtil.CLIENT_KEY,"-1");
        Retrofit retrofit = getRetrofit(Constants.SERVER_URL, isFileUpload);
        return retrofit.create(LinkLinkApi.class);
    }

    /**
     * @method name:provideClientApi
     * @des:获取client
     * @param :[context, url:baseUrl, isFileUpload:是否是文件上传]
     * @return type:com.qdong.communal.library.module.network.QDongApi
     * @date 创建时间:2016/8/25
     * @author Chuck
     **/
    public static LinkLinkApi provideClientApi(Context context, String url, boolean isFileUpload) {

        JSESSIONID= SharedPreferencesUtil.getInstance(context).getString(Constants.SESSION_ID,"-1");
        ACCESSTOKEN= SharedPreferencesUtil.getInstance(context).getString(SharedPreferencesUtil.ACCESS_TOKEN,"-1");
        CLIENT_KEY= SharedPreferencesUtil.getInstance(context).getString(SharedPreferencesUtil.CLIENT_KEY,"-1");
        Retrofit retrofit = getRetrofit(url, isFileUpload);
        return retrofit.create(LinkLinkApi.class);
    }

    @NonNull
    private static Retrofit getRetrofit(String url, boolean isFileUpload) {
        return new Retrofit.Builder()
                    .client(isFileUpload?genericFileUploadClient():genericClient())
                    .addConverterFactory(GsonConverterFactory.create())//json解析器
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//调用适配器
                    .baseUrl(TextUtils.isEmpty(url)?Constants.SERVER_URL:url)
                    .build();
    }


    /**
     * @method name:genericClient
     * @des:普通的post,get,非文件上传使用这个,重点是带上sessionId
     * @param :[]
     * @return type:okhttp3.OkHttpClient
     * @date 创建时间:2016/6/28
     * @author Chuck
     **/
    public static OkHttpClient genericClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtil.e("HttpRetrofit", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//这里可以选择拦截级别
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                    // 添加Query参数
                    HttpUrl httpUrl = chain.request()
                                      .url()
                                      .newBuilder()

                            //setQueryParamter:无则加,有则覆盖.add只能加,不能覆盖
                                      .setQueryParameter("token", ACCESSTOKEN)//这个值,是外部直接更新的(重新登陆后服务器返回的)
                                      .setQueryParameter("Authorization", "Bearer "+ACCESSTOKEN)//这个值,是外部直接更新的(重新登陆后服务器返回的)


                           // .setQueryParameter("clientKey", CLIENT_KEY)//这个值,是外部直接更新的(重新登陆后服务器返回的)

                            .build();



                        Request request = chain.request()
                                .newBuilder()
                                //.addHeader("Content-Type", "application/json; charset=UTF-8")
                                //.addHeader("Accept-Encoding", "gzip, deflate")
                                //.addHeader("Connection", "keep-alive")
                                //.addHeader("Accept", "*/*")
                                /***
                                 * bugFix:这里只留下sessionId即可,对于普通的get,post请求,别的都去掉
                                 * by:chuck 2016/09/18
                                 */
                                .addHeader("Cookie", "JSESSIONID=" + JSESSIONID)//加sessionId
                                .url(httpUrl)
                                .build();

                       LogUtil.e("RxJava", "Interceptor执行!线程id:" + Thread.currentThread().getId() + ",===========>request:"+chain.request().toString());




                        return chain.proceed(request);
                    }

                })
                .addInterceptor(loggingInterceptor)
                .build();

        return httpClient;
    }




    /**
     * @method name:genericClient
     * @des:文件上传的加头
     * @param :[]
     * @return type:okhttp3.OkHttpClient
     * @date 创建时间:2016/6/28
     * @author Chuck
     **/
    public static OkHttpClient genericFileUploadClient() {

        OkHttpClient httpClient = new OkHttpClient.Builder()

                /*****
                 * 此处加入超时时间,针对上传图片的请求
                 */
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)


                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        // 添加Query参数
                        HttpUrl httpUrl = chain.request()
                                .url()
                                .newBuilder()

                                //setQueryParamter:无则加,有则覆盖.add只能加,不能覆盖
                              .setQueryParameter("token", ACCESSTOKEN)//这个值,是外部直接更新的(重新登陆后服务器返回的)
                             //   .setQueryParameter("clientKey", CLIENT_KEY)//这个值,是外部直接更新的(重新登陆后服务器返回的)

                                .setQueryParameter("Authorization", "Bearer "+ACCESSTOKEN)//这个值,是外部直接更新的(重新登陆后服务器返回的)

                                .build();


                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/octet-stream; charset=UTF-8")
                                .addHeader("Accept-Encoding", "gzip")
                                .addHeader("Connection", "Keep-Alive")
                                .addHeader("Cookie", "JSESSIONID=" + JSESSIONID)//加sessionId
                                .url(httpUrl)
                                .build();

                       LogUtil.e("RxJava", "Interceptor执行!线程id:" + Thread.currentThread().getId() + ",===========>request:"+chain.request().toString());


                        return chain.proceed(request);
                    }

                })
                .build();

        return httpClient;
    }

    /**
     * @method name:genericFormClient
     * @des:form表达加头
     * @param :[]
     * @return type:okhttp3.OkHttpClient
     * @date 创建时间:2016/6/28
     * @author Chuck
     **/
    public static OkHttpClient genericFormClient() {

        OkHttpClient httpClient = new OkHttpClient.Builder()

                /*****
                 */
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)

                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {



                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .url(chain.request()
                                        .url())
                                .build();

                       LogUtil.e("RxJava", "Interceptor执行!线程id:" + Thread.currentThread().getId() + ",===========>request:"+chain.request().toString());


                        return chain.proceed(request);
                    }

                })
                .build();

        return httpClient;
    }

}
