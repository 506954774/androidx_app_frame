package com.ilinklink.tg.widget.webView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

import com.ilinklink.tg.utils.LogUtil;
import com.qdong.communal.library.jsbridge.BridgeHandler;
import com.qdong.communal.library.jsbridge.BridgeWebView;
import com.qdong.communal.library.jsbridge.CallBackFunction;
import com.qdong.communal.library.jsbridge.DefaultHandler;


/**
 * NoScrollBridgeWebView
 * Created By:Chuck
 * Des:
 * on 2019/7/29 09:38
 */
public class NoScrollBridgeWebView extends BridgeWebView {



    public static final String TAG="NoScrollBridgeWebView";

    public NoScrollBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollBridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public NoScrollBridgeWebView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public void init(String url) {

        LogUtil.i(TAG, "init,url:"+url);



        setDefaultHandler(new DefaultHandler());

        messageHandlers.clear();

        loadUrl(url);//加载h5


        //添加交互接口
        registerHandler("imgClick",//"imgClick" 是H5里的js定义的函数名,点击后,会调用原生的方法
                new BridgeHandler() {

                    @Override
                    public void handler(String data, CallBackFunction function) {
                        Log.i(TAG, "js交互,接口实例:" + this);


                        Log.i(TAG, "js交互,数据:" + data);//这个data就是从h5里拿到的数据,是一个图片集合,以及当前图片索引


                        try {




                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

    }

    public void initWithBlackStyle(String url) {

        LogUtil.i(TAG, "init,url:"+url);



        setDefaultHandler(new DefaultHandler());

        messageHandlers.clear();

        loadUrl(url);//加载h5


        //添加交互接口
        registerHandler("imgClick",//"imgClick" 是H5里的js定义的函数名,点击后,会调用原生的方法
                new BridgeHandler() {

                    @Override
                    public void handler(String data, CallBackFunction function) {
                        Log.i(TAG, "js交互,接口实例:" + this);


                        Log.i(TAG, "js交互,数据:" + data);//这个data就是从h5里拿到的数据,是一个图片集合,以及当前图片索引


                        try {




                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

    }

    /**
     * 修改webview样式(assets--css文件)
     *
     * @param webview
     * @param str
     */
    public  void getWebContent(WebView webview, String str) {
        String linkCss = "<link rel=\"stylesheet\" href=\"file:///android_asset/content.css\" type=\"text/css\">";

        String body = "<html><header>" + linkCss + "</header>" + str
                + "</body></html>";

        loadDataWithBaseURL(linkCss, body, "text/html", "UTF-8", null);
    }


}
