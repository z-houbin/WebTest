package z.houbin.webtest;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Random;

public class WebService extends Service {
    private boolean firstLoad = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadWeb(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    private void loadWeb(final Context mContext) {
        View v = View.inflate(mContext, R.layout.activity_web, null);
        // 窗体的布局样式
        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
        // 设置窗体显示类型――TYPE_SYSTEM_ALERT(系统提示)
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        // 设置窗体焦点及触摸：
        // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 设置显示的模式
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        // 设置对齐的方法
        mLayoutParams.gravity = Gravity.TOP | Gravity.START;
        // 设置窗体宽度和高度
        mLayoutParams.width = -1;
        mLayoutParams.height = -1;
        // 设置窗体显示的位置，否则在屏幕中心显示
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        //获取程序顶层界面
        Activity topActivity = ActivityUtils.getTopActivity(mContext);
        Log.e("TopActivity", String.valueOf(topActivity));
        if (topActivity == null) {
            return;
        }

        //添加网页到界面
        ViewGroup group = (ViewGroup) topActivity.getWindow().getDecorView();
        group = (ViewGroup) ((ViewGroup) group.getChildAt(0)).getChildAt(1);
        group.addView(v, 0, mLayoutParams);

        final WebView web = v.findViewById(R.id.web);
        //透明不可见
        web.setAlpha(0);
        WebSettings webSettings = web.getSettings();
        //activity_web 设置待优化
        webSettings.setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient() {
        });

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("onPageFinished ", url);

                Toast.makeText(mContext, "onPageFinished " + url, Toast.LENGTH_SHORT).show();
                if(firstLoad){
                    firstLoad = false;
                    clickLoop(web);
                }
            }
        });
        firstLoad = true;
        web.loadUrl("http://www.cctv.com/");
    }

    //模拟点击
    public void click(View v, Point p) {
        Log.i("Click", "start");
        if (v != null) {
            MotionEvent actionDown = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, p.x, p.y, 0);
            v.dispatchTouchEvent(actionDown);

            MotionEvent actionUp = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, p.x, p.y, 0);
            v.dispatchTouchEvent(actionUp);

            actionDown.recycle();
            actionUp.recycle();

            Log.i("Click", "finish");

            clickLoop(v);
        }
    }

    //循环随机点击
    private void clickLoop(final View v) {
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int x = random.nextInt(1000);
                int y = random.nextInt(1000);
                click(v, new Point(x, y));
            }
        }, 4000);
    }
}
