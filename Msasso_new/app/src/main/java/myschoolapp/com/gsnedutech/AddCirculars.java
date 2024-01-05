package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class AddCirculars extends AppCompatActivity {

    private static final String TAG = "SriRam -" + AddCirculars.class.getName();

    MyUtils utils = new MyUtils();

    @BindView(R.id.cv_attach_file)
    CardView cvAttachFile;

    @BindView(R.id.tv_img_name)
    TextView tvImgName;

    @BindView(R.id.overlay)
    ImageView ivOverlay;

    @BindView(R.id.ll_image)
    LinearLayout linearLayout;

    Uri imgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_circulars);
        ButterKnife.bind(this);
        init();

        cvAttachFile.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 1);
        });
    }

    void init(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        linearLayout.setVisibility(View.VISIBLE);
        Log.v(TAG,"result "+resultCode);

        if (resultCode != 0){
            imgUri = data.getData();
            String uriString = imgUri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(imgUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }
            tvImgName.setText(displayName);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}