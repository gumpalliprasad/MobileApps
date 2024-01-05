package myschoolapp.com.gsnedutech.khub;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Flip.FlipObj;
import myschoolapp.com.gsnedutech.R;

public class KhubFlipDetailed extends AppCompatActivity {

    @BindView(R.id.img_flip)
    ImageView imgFlip;

    @BindView(R.id.tv_descp)
    TextView tvDesc;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_ref)
    TextView tvRef;

    FlipObj flipObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_flip_detailed);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    void init(){
        flipObj = (FlipObj) getIntent().getSerializableExtra("flip");

        tvTitle.setText(""+flipObj.getTitle());
        tvDesc.setText(Html.fromHtml(""+flipObj.getDesc()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvDesc.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        if (flipObj.getHyperText().isEmpty()){
            tvRef.setVisibility(View.INVISIBLE);
        }
        else {
            tvRef.setVisibility(View.VISIBLE);
        }
        tvRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""+flipObj.getHyperText()));
                startActivity(browserIntent);
            }
        });

        if (flipObj.getPath() != null && !flipObj.getPath().isEmpty()&&
                !flipObj.getPath().equalsIgnoreCase("")) {
            imgFlip.setVisibility(View.VISIBLE);
            Picasso.with(KhubFlipDetailed.this).load(flipObj.getPath()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(imgFlip);
        }else{
            imgFlip.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}