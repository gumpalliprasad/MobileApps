package myschoolapp.com.gsnedutech.Util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.R;

public class VideoWebViewer extends AppCompatActivity {

    String videoLink;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_web_viewer);
        ButterKnife.bind(this);

        tvTitle.setText(getIntent().getStringExtra("name"));

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadWebsite(getIntent().getStringExtra("videoItem"));

    }

    private void loadWebsite(String url) {
        videoLink = url;

        MyUtils utils = new MyUtils();

        webView.setWebViewClient(new Browser_home());
        WebSettings webSettings = webView.getSettings();
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(false);
        webView.setWebChromeClient(new MyChrome());


        ConnectivityManager cm = (ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            webView.loadUrl(url);
        } else {
            webView.setVisibility(View.GONE);
        }

    }

    class Browser_home extends WebViewClient {

        Browser_home() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Toast.makeText(VideoWebViewer.this, "Not Permitted", Toast.LENGTH_LONG).show();
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(VideoWebViewer.this.getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) VideoWebViewer.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            VideoWebViewer.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            VideoWebViewer.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = VideoWebViewer.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = VideoWebViewer.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) VideoWebViewer.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            VideoWebViewer.this.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            final Handler handler = new Handler();
            VideoWebViewer.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


}
