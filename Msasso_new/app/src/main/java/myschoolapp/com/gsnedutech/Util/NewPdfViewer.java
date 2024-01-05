package myschoolapp.com.gsnedutech.Util;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import myschoolapp.com.gsnedutech.R;

public class NewPdfViewer extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + NewPdfViewer.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.pdfview)
    PDFView pdfView;

    @BindView(R.id.prev)
    ImageView prev;

    @BindView(R.id.next)
    ImageView next;

    @BindView(R.id.tv_pagenumbers)
    TextView tvTotalPages;

    String url;
    int pageNumber;

    MyUtils utils = new MyUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pdf_viewer);


        ButterKnife.bind(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        showProgress();
        init();

        url = getIntent().getStringExtra("url");

    }

    public void showProgress() {

        utils.showLoader(this);
    }

    public void dismissDialog() {
        utils.dismissDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, NewPdfViewer.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                new DownloadPdfFile().execute();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    public void init(){
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prev:
                if(pageNumber>0){
                    pdfView.jumpTo(pageNumber-1);
                }
                break;
            case R.id.next:
                if(pageNumber<pdfView.getPageCount())
                    pdfView.jumpTo(pageNumber+1);
                break;
        }
    }

    public class DownloadPdfFile extends AsyncTask<String, Void, File> {

        MyUtils utils = new MyUtils();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            utils.showLoader(NewPdfViewer.this);
        }

        @Override
        protected File doInBackground(String... strings) {
            File jsonResp = null;
            int count;

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

//            utils.showLog(TAG, "QuesList URL - " + new AppUrls().GetCoursePracticeQueestions +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&contentType=" + contentType + "&topicId=" + topicId + "&studentId=" + sObj.getStudentId());

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
//                jsonResp = response.body().string();

                InputStream in = response.body().byteStream();

                // Output stream to write file

                File root = new File(getFilesDir(),"temp");
                if (!root.exists()) {
                    root.mkdir();
                }

                File pdffile = new File(root,"currentpdf.pdf");
                OutputStream output = new FileOutputStream(pdffile);
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = in.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                in.close();
                jsonResp = pdffile;
//                utils.showLog(TAG, "QuesList response - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
                utils.dismissDialog();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(File responce) {
            super.onPostExecute(responce);

            if (responce!=null && responce.exists()){
                loadPdf(responce);
            }
            utils.dismissDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                }
            },2000);

        }
    }


    public void loadPdf(File file){
        pdfView.fromFile(file)
                .defaultPage(0)
                .swipeHorizontal(true)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .onPageChange(this)
                .onLoad(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        tvTotalPages.setText((page+1)+"/"+pageCount);
        if (pageCount>0) {
            if (page == 0) {
                prev.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            }
            else if (page+1 == pageCount) {
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.INVISIBLE);
            }
            else {
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        if (nbPages>0){
            next.setVisibility(View.VISIBLE);
        }
        else {
            next.setVisibility(View.GONE);
        }
        prev.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}