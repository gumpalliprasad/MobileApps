package myschoolapp.com.gsnedutech.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import myschoolapp.com.gsnedutech.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageDisp extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_disp);


        final ImageView iv = findViewById(R.id.iv);
        String url = "";
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        Picasso.with(ImageDisp.this).load(getIntent().getStringExtra("path")).placeholder(R.drawable.progress_animation)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(iv, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                PhotoViewAttacher photoAttacher;
                photoAttacher= new PhotoViewAttacher(iv);
                photoAttacher.update();
            }

            @Override
            public void onError() {
                Toast.makeText(ImageDisp.this,"Oops there was a problem!",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}