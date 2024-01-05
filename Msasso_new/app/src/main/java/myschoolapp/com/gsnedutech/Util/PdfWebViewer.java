package myschoolapp.com.gsnedutech.Util;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.R;

public class PdfWebViewer extends AppCompatActivity {

    private static final String TAG = "SriRam -" + PdfWebViewer.class.getName();
    MyUtils utils = new MyUtils();


    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    //    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        ButterKnife.bind(this);


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Set BackgroundDrawable


        utils.showLog(TAG, "url - " + getIntent().getStringExtra("url"));
//        startWebView(getIntent().getStringExtra("url"));

//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new AppWebViewClients(progressBar));

//        webView.loadUrl(getIntent().getStringExtra("url"));

        if (savedInstanceState != null)
            startWebView("https://docs.google.com/gview?embedded=true&url="+getIntent().getStringExtra("url"));
        else
            startWebView("https://docs.google.com/gview?embedded=true&url="+getIntent().getStringExtra("url"));
    }


    private void startWebView(String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUseWideViewPort(false);
        webView.clearCache(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    progressbar.setVisibility(View.GONE);
                }
            }

        });

//        For removing the share option
        webView.setWebViewClient(new WebViewClient() {

            //once the page is loaded get the html element by class or id and through javascript hide it.
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
                            }
        });
    }



    @Override
    protected void onDestroy() {
//        if (progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }

        if (progressbar.getVisibility() == View.VISIBLE) {
            progressbar.setVisibility(View.GONE);
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
