package myschoolapp.com.gsnedutech.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import myschoolapp.com.gsnedutech.R;


public class CustomWebview extends WebView {
    public CustomWebview(Context context) {
        super(context);
        initView(context);
    }

    public CustomWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView(Context context){

        this.getSettings().setJavaScriptEnabled(true) ;
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setUseWideViewPort(true);
        this.setWebChromeClient(new WebChromeClient());
        this.setWebViewClient(new WebViewClient());
        this.getSettings().setDefaultFontSize(30);
        this.getSettings().setTextSize(WebSettings.TextSize.LARGER);
        //for disable zoom
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        //zoom
        this.getSettings().setDisplayZoomControls(false);
        //enable zoom
//        this.getSettings().setBuiltInZoomControls(true);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.setBackgroundColor(Color.TRANSPARENT);
        if (getResources().getString(R.string.screen).equalsIgnoreCase("Xlarge")){
            this.getSettings().setDefaultFontSize(18);
        }
        this.scrollTo(0, 0);
        this.setOnLongClickListener(v -> {
            // For final release of your app, comment the toast notification
            Toast.makeText(context, "Long Click Disabled", Toast.LENGTH_SHORT).show();
            return true;
        });

    }

    public void setText(String getqName) {

        getqName = getqName.replaceAll("<span.*?>","");
        getqName = getqName.replaceAll("</span.*?>","");
        getqName = getqName.replaceAll("&#39;","'");
        getqName = getqName.replaceAll("(?s)<!--.*?-->","");
        this.loadData(getqName, "text/html; charset=utf-8", "utf-8");
    }
}
