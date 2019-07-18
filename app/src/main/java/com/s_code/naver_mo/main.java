package com.s_code.naver_mo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class main extends AppCompatActivity {

    private ProgressDialog progressBar;


    public final String TAG = "main_activity";
    private  String[] data_url;
    private String url = "";
    private String originae_url = url;
    private String url_push="";
    private String m_url="";
    JSONObject jsonobject;
    JSONObject jsonobject2;
    JSONObject jsonobject3;
    JSONObject jsonobject4;
    JSONArray jsonarray;
    JSONArray jsonarray2;
    private static final String TYPE_IMAGE = "image/*";
    private static final int INPUT_FILE_REQUEST_CODE = 1;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;


    //-------------------사용자설정------------------------/

    private int serverConectionTime = 10000;
    //getPhoneNumber(getApplicationContext()); 폰번호 읽기
//-------------------------------------------------------------------------------------------------
    private WebView mWebView;
    private View emailNewField;
    private LinearLayout wr_pop;
    private FrameLayout wr_main;
    private ImageView closebtn_popup;
    private LinearLayout wrap_d_list;
    private TimerTask mTask;
    private Timer mTimer;
    private Thread trd;
    private Intent intent;
    private String token;

    private String mov_json_url;

    private ImageView go_url;
    private EditText url_text;

    private  String[] list;
    private WebView newWebView;
    private int popup_flag;

    private File outputFile; //파일명까지 포함한 경로
    private File path;//디렉토리경로

    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"};
    private boolean hasPermissions(String[] permissions) {
        int res = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //퍼미션이 허가된 경우
        return true;
    }


    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        progressBar=new ProgressDialog(main.this);
        progressBar.setMessage("다운로드중");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(false);






        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateIconBadgeCount(this,0);


        wr_main = (FrameLayout) findViewById(R.id.main_wrap);
        url_text=findViewById(R.id.url_text);
        wrap_d_list=findViewById(R.id.wrap_d_list);
        go_url=findViewById(R.id.go_url);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(123456);

        Intent intent = getIntent(); // 값을 받아온다.
        String s = intent.getStringExtra("get_u");
        if(s!=null) {
            if (s.startsWith("http")) {
                url=s;
            }else{
                Toast.makeText(getApplicationContext(), "정상적인 접근이 아닙니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            //이미 사용자에게 퍼미션 허가를 받음.
        }

        go_url.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        mWebView.loadUrl(url_text.getText().toString());

                        wrap_d_list.removeAllViews();

                    }
                }
        );


        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색
            getWindow().setStatusBarColor(Color.parseColor("#a40b11"));
        }

        mWebView = (WebView) findViewById(R.id.webview);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setPluginState(WebSettings.PluginState. ON_DEMAND);

        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mWebView.getSettings().setDatabasePath("/data/data/" + mWebView.getContext().getPackageName() + "/databases/");
        }


        mWebView.loadUrl(url);
        url_text.setText(url);
        mWebView.addJavascriptInterface(new JavaScriptInterface(this), "App");
        mWebView.setWebViewClient(new ProxyWebViewClient());
        mWebView.setWebChromeClient(new ProxyWebChromeClient());

        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportMultipleWindows(true);

        String userAgent = mWebView.getSettings().getUserAgentString();

        mWebView.getSettings().setUserAgentString(userAgent+" hyapp");

        final Context myApp = this;
        mWebView.setDownloadListener(new DownloadListener() {



            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimeType);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file");
                    String fileName = contentDisposition.replace("inline; filename=", "");
                    fileName = fileName.replaceAll("\"", "");
                    request.setTitle(fileName);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                    if (ContextCompat.checkSelfPermission(main.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(main.this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {



                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(main.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        } else {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(main.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        }
                    }
                }
            }
        });


        Log.d("popup_n:: ","onstart");
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("popup_n", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
            {
                new AlertDialog.Builder(myApp)
                        .setTitle("메세지")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                return true;
            };

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityForResult(intent, 0);
                return true;
            }


            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.d(TAG, "popup_n: oncreat");


                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                emailNewField = inflater.inflate(R.layout.popup , null);
                newWebView = (WebView) emailNewField.findViewById(R.id.popup_webview);
                WebSettings settings = newWebView.getSettings();   // 웹뷰 세팅 및 세부세팅
                wr_pop=(LinearLayout)emailNewField.findViewById(R.id.wrap_pop);

                closebtn_popup = (ImageView) emailNewField.findViewById(R.id.popupclose);
                closebtn_popup.setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                newWebView.loadUrl("javascript:window.close();");
                                popup_flag=0;


                            }
                        }
                );




                settings.setJavaScriptEnabled(true);
                settings.setDisplayZoomControls(false);
                settings.setLoadWithOverviewMode(true);
                settings.setSupportZoom(true);
                settings.setBuiltInZoomControls(true);


                settings.setAllowFileAccess(true);
                settings.setAllowContentAccess(true);
                settings.setAllowFileAccessFromFileURLs(true);
                settings.setAllowUniversalAccessFromFileURLs(true);


                newWebView.setVerticalScrollBarEnabled(false);
                newWebView.setHorizontalScrollBarEnabled(false);
                settings.setPluginState(WebSettings.PluginState. ON_DEMAND);
                newWebView.setWebViewClient(new ProxyWebViewClient());
                newWebView.setWebChromeClient(new ProxyWebChromeClient());

                newWebView.setDownloadListener(new DownloadListener() {



                    @Override
                    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                        try {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setMimeType(mimeType);
                            request.addRequestHeader("User-Agent", userAgent);
                            request.setDescription("Downloading file");
                            String fileName = contentDisposition.replace("inline; filename=", "");
                            fileName = fileName.replaceAll("\"", "");
                            request.setTitle(fileName);
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            dm.enqueue(request);
                            Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                            if (ContextCompat.checkSelfPermission(main.this,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(main.this,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {



                                    Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                                    ActivityCompat.requestPermissions(main.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            110);
                                } else {
                                    Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                                    ActivityCompat.requestPermissions(main.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            110);
                                }
                            }
                        }
                    }
                });


                settings.setJavaScriptCanOpenWindowsAutomatically(true);
                settings.setSupportMultipleWindows(true);


                newWebView.setWebViewClient(new WebViewClient() {

                    @TargetApi(Build.VERSION_CODES.N)
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                        Log.d(TAG, "removeAllViews: loadurl2");
                        wrap_d_list.removeAllViews();
                        String url = request.getUrl().toString();
                        Log.d(TAG, "load_url: "+url);
                        if (url.startsWith("tel:")) {
                            Intent call_phone = new Intent(Intent.ACTION_DIAL);
                            call_phone.setData(Uri.parse(url));
                            startActivity(call_phone);
                        } else if (url.startsWith("sms:")) {
                            Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                            startActivity(i);
                        } else if (url.startsWith("intent:")) {
                            try {
                                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                                Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                                if (existPackage != null) {
                                    startActivity(intent);
                                } else {
                                    Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                                    marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                                    startActivity(marketIntent);
                                }
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            url_text.setText(url);
                            Log.d(TAG, "removeAllViews: loadurl2");
                            wrap_d_list.removeAllViews();
                            view.loadUrl(url);
                        }
                        return true;
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {

                        Log.d(TAG, "removeAllViews: loadurl2");
                        wrap_d_list.removeAllViews();

                        url_text.setText(url);
                        if (url.startsWith("tel:")) {
                            Intent call_phone = new Intent(Intent.ACTION_DIAL);
                            call_phone.setData(Uri.parse(url));
                            startActivity(call_phone);
                        } else if (url.startsWith("sms:")) {
                            Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                            startActivity(i);
                        } else if (url.startsWith("intent:")) {
                            try {
                                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                                Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                                if (existPackage != null) {
                                    startActivity(intent);
                                } else {
                                    Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                                    marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                                    startActivity(marketIntent);
                                }
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            url_text.setText(url);
                            Log.d(TAG, "removeAllViews: loadurl2");
                            wrap_d_list.removeAllViews();
                            view.loadUrl(url);
                        }
                        Log.d(TAG, "popup_n: loadurl2");
                        return true;
                    }



                });

                newWebView.setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onCloseWindow(WebView window) {

                        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
                        emailNewField.startAnimation(animation2);
                       Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.del_pop);
                       animation.setAnimationListener(new MyAnimationListener());
                        wr_pop.startAnimation(animation);

                    }

                    @Override

                    public void onReceivedTitle(WebView view, String title) {

                        super.onReceivedTitle(view, title);

                        if (!TextUtils.isEmpty(title)) {

                            TextView testh = (TextView) emailNewField.findViewById(R.id.textView2);
                            testh.setText(title);

                        }

                    }

                    @Override
                    public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
                    {
                        new AlertDialog.Builder(myApp)
                                .setTitle("메세지")
                                .setMessage(message)
                                .setPositiveButton(android.R.string.ok,
                                        new AlertDialog.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                result.confirm();
                                            }
                                        })
                                .setCancelable(false)
                                .create()
                                .show();

                        return true;
                    };

                    @Override
                    public boolean onShowFileChooser(WebView webView, ValueCallback filePathCallback, FileChooserParams fileChooserParams) {
                        mFilePathCallback = filePathCallback;

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");

                        startActivityForResult(intent, 0);
                        return true;
                    }

                });
                wr_pop.setVisibility(View.VISIBLE);

                wr_main.addView(emailNewField);
                Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
                emailNewField.startAnimation(animation2);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_pop);
                wr_pop.startAnimation(animation);

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);

                emailNewField.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;


                resultMsg.sendToTarget();
                popup_flag=1;
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("resultCode:: ", String.valueOf(resultCode));
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            }else{
                mFilePathCallback.onReceiveValue(new Uri[]{data.getData()});
            }
            mFilePathCallback = null;
        }else{
            mFilePathCallback.onReceiveValue(null);
        }
    }

    private final class MyAnimationListener implements Animation.AnimationListener {

        /**
         * 애니메이션이 끝날 때 자동 호출됨
         */
        public void onAnimationEnd(Animation animation) {

            emailNewField.setVisibility(View.GONE);

            wr_pop.removeAllViewsInLayout();
            wr_main.removeView(wr_pop);
            newWebView.destroy();

            popup_flag=0;
            Log.d(TAG, "popup_n: close");
        }

        /**
         * 애니메이션이 반복될 때 자동 호출됨
         */
        public void onAnimationRepeat(Animation animation) {
        }

        /**
         * 애니메이션이 시작할 때 자동 호출됨
         */
        public void onAnimationStart(Animation animation) {
        }
    }
    @Override
    //백버튼 클릭시 뒤로가기 추가 뒤로가기 더이상 없으면 종료
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(TAG, "removeAllViews: loadurl2");
        wrap_d_list.removeAllViews();
        if(popup_flag==1) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && newWebView.canGoBack()) {


                newWebView.goBack();
                return true;
            }
        }else {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {


                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }
    @Override
    //종료처리시 종료 할지 물어보기 추가    mWebView.loadUrl(url);
    public void onBackPressed()
    {
        finish();
    }




    private class ProxyWebViewClient extends WebViewClient {


        public void onLoadResource(WebView view, String url) {
            // do your stuff here

            Log.d("jisung2", url);
            if(url.startsWith("https://apis.naver.com/rmcnmv/rmcnmv/vod/play/v2.0/")){
                Toast.makeText(getApplicationContext(), "동영상 파일 찾음.", Toast.LENGTH_LONG).show();
                mov_json_url=url;

                new DownloadJSON().execute();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.d(TAG, "removeAllViews: loadurl2");
            wrap_d_list.removeAllViews();
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("kakaolink:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("sms:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }else if (url.startsWith("storylink:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("market:")) {
                Log.d("jisung_market", url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("ispmobile:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("kakaoplus:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("http://goto.kakao")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }   //
            else if (url.startsWith("http://kakaolink.com/")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }   //
            else if (url.startsWith("https://www.facebook.")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("intent://")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));


                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    webIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    if (webIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(webIntent);
                    }
                }


                return true;
            }
            else {
                return false;
            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            final String urlt = uri.toString();

            Log.d(TAG, "removeAllViews: loadurl2");
            wrap_d_list.removeAllViews();
            Log.d("jisung2", urlt);
            if (request.isRedirect()){
                Log.d("jisung4", urlt);
                if (urlt.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(urlt));
                    startActivity(intent);
                } else if (urlt.startsWith("kakaolink:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                } else if (urlt.startsWith("sms:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                } else if (urlt.startsWith("mailto:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                }else if (urlt.startsWith("storylink:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                } else if (urlt.startsWith("market:")) {
                    Log.d("jisung3", urlt);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                } else if (urlt.startsWith("ispmobile:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                } else if (urlt.startsWith("kakaoplus:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                } else if (urlt.startsWith("http://goto.kakao")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                }   //
                else if (urlt.startsWith("http://kakaolink.com/")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                }   //
                else if (urlt.startsWith("https://www.facebook.")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    startActivity(intent);
                }   //
                else if (urlt.startsWith("intent://")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW);
                        webIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                        if (webIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(webIntent);
                        }
                    }
                }   //
                else {
                    view.loadUrl(urlt);
                }
                return true;
            }
            Log.d("jisung4", urlt);
            if (urlt.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(urlt));
                startActivity(intent);
                return true;
            } else if (urlt.startsWith("kakaolink:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            } else if (urlt.startsWith("sms:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            } else if (urlt.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            }else if (urlt.startsWith("storylink:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            } else if (urlt.startsWith("market:")) {
                Log.d("jisung3", urlt);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            } else if (urlt.startsWith("ispmobile:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            } else if (urlt.startsWith("kakaoplus:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            } else if (urlt.startsWith("http://goto.kakao")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            }   //
            else if (urlt.startsWith("http://kakaolink.com/")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            }   //
            else if (urlt.startsWith("https://www.facebook.")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                startActivity(intent);
                return true;
            }else if (urlt.startsWith("intent://")) {    //2015-01-21 카카오톡 옐로아이디 1:1 채팅 관련하여 추가 작업
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlt));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    webIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    if (webIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(webIntent);
                    }
                }
                return true;
            }
            Log.d("jisung5", urlt);
            //a
            return false;
        }


    }



    private class ProxyWebChromeClient extends WebChromeClient {
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//            Toast.makeText(StreetAndroidActivity.this, message, 3000).show();
            result.confirm();
            return true;
        }
    }


    public class JavaScriptInterface {

        private Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public String getcook(String val){

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String userkey=pref.getString(val, "");
            return userkey;

        }

        @JavascriptInterface
        public void alert(String val, String val2)
        {
            new AlertDialog.Builder(mContext)
                    .setTitle(val)
                    .setMessage(val2)
                    .setPositiveButton("확인",null).show();
        }


        @JavascriptInterface
        public void savecook(String val, String userkey){

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(val, userkey);
            editor.commit();

        }

        @JavascriptInterface
        public void intopic(String val){



            //   getto_topic(val);

        }

        @JavascriptInterface
        public void outtopic(String val){



            //   getto_topic_out(val);

        }

        public String getlogin(){

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String userkey=pref.getString("userkey", "");
            return userkey;

        }


        public String getuuid(){

            return "uuid";

        }



        @JavascriptInterface
        public void callweb(String url){

            Intent i = new Intent(Intent.ACTION_VIEW);

            Uri u = Uri.parse(url);

            i.setData(u);

            startActivity(i);

        }


        @JavascriptInterface
        public void viewpopup(String url){

            Context context = getBaseContext();

        }




        @JavascriptInterface
        public void showToast(String toast) {

            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();

        }

    }






    @Override
    protected void onStart() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pause", "true");
        editor.commit();
        super.onStart();
        Log.d("TestAppActivity", "onStart");
    }
    @Override
    protected void onRestart() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pause", "true");
        editor.commit();
        updateIconBadgeCount(this,0);
        super.onRestart();
        Log.d("TestAppActivity", "onRestart");
    }

    @Override
    protected void onStop() {

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pause", "false");
        editor.commit();
        super.onStop();
        Log.d("TestAppActivity", "onStop");
    }

    @Override
    protected void onPostResume() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pause", "true");
        editor.commit();

        updateIconBadgeCount(this,0);
        super.onPostResume();
        Log.d("TestAppActivity", "onPostResume");
    }

    @Override
    protected void onPause() {

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pause", "false");
        editor.commit();
        super.onPause();
        Log.d("TestAppActivity", "onPause");
    }
    @Override
    protected void onResume() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pause", "true");
        editor.commit();
        updateIconBadgeCount(this,0);


        super.onResume();
        Log.d("TestAppActivity", "onResume");
    }

    @Override

    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        String s = intent.getStringExtra("get_u");
        if(s!=null) {
            if (s.startsWith("http")) {
                url=s;
                wrap_d_list.removeAllViews();
                mWebView.loadUrl(url_text.getText().toString());
            }else{
                Toast.makeText(getApplicationContext(), "정상적인 접근이 아닙니다.", Toast.LENGTH_LONG).show();
            }
        }

    }


    @Override
    protected void onDestroy() {

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pause", "false");
        editor.commit();
        super.onDestroy();
        Log.d("onPostCreate", "onDestroy");
    }





    public void updateIconBadgeCount(Context context, int count) {
        if(count==0) {
            SharedPreferences bpref = getSharedPreferences("bpref", MODE_PRIVATE);
            SharedPreferences.Editor editor = bpref.edit();
            editor.putString("b_count", "0");
            editor.commit();
        }
        Log.d("test", "updateIconBadgeCount");
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        // Component를 정의
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName(context));

        // 카운트를 넣어준다.
        intent.putExtra("badge_count", count);

        // Version이 3.1이상일 경우에는 Flags를 설정하여 준다.
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {

            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        // send
        sendBroadcast(intent);
    }

    private String getLauncherClassName(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(getPackageName());

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);
        if(resolveInfoList != null && resolveInfoList.size() > 0) {
            Log.d("test", "success");
            return resolveInfoList.get(0).activityInfo.name;

        }
        Log.d("test", "Fail");
        return "";
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }


    // DownloadJSON AsyncTask
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array

            if(mov_json_url.startsWith("https://apis.naver.com")) {
                jsonobject = JSONfunctions
                        .getJSONfromURL(mov_json_url);
                Log.d("jisung3","1");

                if (jsonobject == null) {
                    Log.d("jisung3","22");
                    return null;
                }
                try {
                    Log.d("jisung3","2");
                    // Locate the array name in JSON
                    jsonobject2 = jsonobject.getJSONObject("videos");
                  //  jsonobject3 = jsonobject2.getJSONObject("list");
                  //  jsonarray = jsonobject2.getJSONArray("source");


                 //   jsonobject3 = jsonobject2.getJSONObject("list");
                    jsonarray = jsonobject2.getJSONArray("list");


                    list = new String[jsonarray.length()];


                    Log.d("jisung3","3");

                    Log.d("jisung3", String.valueOf(jsonarray.length()));
                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonobject = jsonarray.getJSONObject(i);

                        jsonobject3=jsonobject.getJSONObject("encodingOption");



                        list[i] = jsonobject3.getString("name")+"|"+jsonobject.getString("source");
                    }

                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if(jsonarray!=null) {

                //  RecyclerAdapter.additem(RecyclerAdapter.getItemCount()-jsonarray.length());
                // mProgressDialog.dismiss();
                for (int i = 0; i < jsonarray.length(); i++) {

                    LayoutInflater inflater_n = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout pro_list_l = (LinearLayout) inflater_n.inflate(R.layout.file_list, null);
                    TextView user_name_v = (TextView) pro_list_l.findViewById(R.id.user_name);
                    Button button_down = (Button) pro_list_l.findViewById(R.id.button_down);

                    data_url = list[i].split("\\|");
                    user_name_v.setText(data_url[0]+"화질 영상 다운로드");



                    button_down.setOnClickListener(
                            new Button.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
                                    String user_point=preferences.getString("user_point", "");
                                    Log.d("user_pointuser_point", "err:"+user_point);
                                    int ia=Integer.parseInt(user_point);
                                    if(ia>29){

                                        ia=ia-30;
                                        user_point=String.valueOf(ia);
                                        SharedPreferences pref =getSharedPreferences("pref", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("user_point", user_point);
                                        editor.commit();


                                        m_url=data_url[1];
                                        final DownloadFilesTask downloadTask = new DownloadFilesTask(main.this);
                                        downloadTask.execute(m_url);


                                    }else{
                                        Toast.makeText(getApplicationContext(), "포인트가 부족합니다. 광고시청무료충전을 이용해보세요.", Toast.LENGTH_LONG).show();
                                    }



                                }
                            }
                    );
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                    pro_list_l.setLayoutParams(param);

                    pro_list_l.setGravity(Gravity.BOTTOM);

                    wrap_d_list.addView(pro_list_l);
                }



            }else{

            }
        }
    }



    private class DownloadFilesTask extends AsyncTask<String, String, Long> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadFilesTask(Context context) {
            this.context = context;
        }


        //파일 다운로드를 시작하기 전에 프로그레스바를 화면에 보여줍니다.
        @Override
        protected void onPreExecute() { //2
            super.onPreExecute();

            //사용자가 다운로드 중 파워 버튼을 누르더라도 CPU가 잠들지 않도록 해서
            //다시 파워버튼 누르면 그동안 다운로드가 진행되고 있게 됩니다.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            progressBar.show();
        }


        //파일 다운로드를 진행합니다.
        @Override
        protected Long doInBackground(String... string_url) { //3
            int count;
            long FileSize = -1;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection = null;

            try {
                URL url = new URL(string_url[0]);
                connection = url.openConnection();
                connection.connect();


                //파일 크기를 가져옴
                FileSize = connection.getContentLength();

                //URL 주소로부터 파일다운로드하기 위한 input stream
                input = new BufferedInputStream(url.openStream(), 8192);



                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                outputFile= new File(path, "naver_cafe_"+timeStamp+".mp4"); //파일명까지 포함함 경로의 File 객체 생성

                // SD카드에 저장하기 위한 Output stream
                output = new FileOutputStream(outputFile);


                byte data[] = new byte[1024];
                long downloadedSize = 0;
                while ((count = input.read(data)) != -1) {
                    //사용자가 BACK 버튼 누르면 취소가능
                    if (isCancelled()) {
                        input.close();
                        return Long.valueOf(-1);
                    }

                    downloadedSize += count;

                    if (FileSize > 0) {
                        float per = ((float)downloadedSize/FileSize) * 100;
                        String str = "Downloaded " + downloadedSize + "KB / " + FileSize + "KB (" + (int)per + "%)";
                        publishProgress("" + (int) ((downloadedSize * 100) / FileSize), str);

                    }

                    //파일에 데이터를 기록합니다.
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();

                // Close streams
                output.close();
                input.close();


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                mWakeLock.release();

            }
            return FileSize;
        }


        //다운로드 중 프로그레스바 업데이트
        @Override
        protected void onProgressUpdate(String... progress) { //4
            super.onProgressUpdate(progress);

            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(progress[0]));
            progressBar.setMessage(progress[1]);
        }

        //파일 다운로드 완료 후
        @Override
        protected void onPostExecute(Long size) { //5
            super.onPostExecute(size);

            progressBar.dismiss();

            if ( size > 0) {
                Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다. 파일 크기=" + size.toString(), Toast.LENGTH_LONG).show();

                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outputFile));
                sendBroadcast(mediaScanIntent);


            }
            else
                Toast.makeText(getApplicationContext(), "다운로드 에러", Toast.LENGTH_LONG).show();
        }

    }


}


