/*
 * *
 *  * Created by SriRamaMurthy A on 11/10/19 3:11 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 11/10/19 3:11 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Util.AppUrls;

public class ImageDisplay extends AppCompatActivity {

    private static final String TAG = ImageDisplay.class.getName();

    @BindView(R.id.img)
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.v(TAG, "" + getIntent().getStringExtra("path"));
        Log.v(TAG, "" + new AppUrls().HomeWorkFileDownLoad + getIntent().getStringExtra("path"));

        Picasso.with(ImageDisplay.this).load(new AppUrls().HomeWorkFileDownLoad + getIntent().getStringExtra("path")).placeholder(R.drawable.progress_animation)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(img);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
