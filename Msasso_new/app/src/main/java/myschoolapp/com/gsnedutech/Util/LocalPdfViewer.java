package myschoolapp.com.gsnedutech.Util;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.R;

public class LocalPdfViewer extends AppCompatActivity implements View.OnClickListener, OnPageChangeListener, OnLoadCompleteListener {

    @BindView(R.id.pdfview)
    PDFView pdfView;

    @BindView(R.id.prev)
    ImageView prev;

    @BindView(R.id.next)
    ImageView next;

    @BindView(R.id.tv_pagenumbers)
    TextView tvTotalPages;

    File pdf;

    MyUtils utils = new MyUtils();

    int pageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_pdf_viewer);
        ButterKnife.bind(this);

        ButterKnife.bind(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        init();

    }

    void init(){
        pdf = new File(getIntent().getStringExtra("path"));
        prev.setOnClickListener(this);
        next.setOnClickListener(this);

        loadPdf(pdf);

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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}