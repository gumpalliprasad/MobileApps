package myschoolapp.com.gsnedutech.khub;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Flip.FlipObj;
import myschoolapp.com.gsnedutech.Flip.FlipView;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class KhubFlipActivity extends AppCompatActivity implements FlipView.OnPositionChangeListener {

    private static final String TAG = KhubFlipActivity.class.getName();

    int pos;
    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_count)
    TextView tvCount;

    ArrayList<FlipObj> flipObjArrayList = new ArrayList<FlipObj>();
    @BindView(R.id.main_flip_view)
    FlipView mainFlipView;

    String contentType = "", moduleContentId = "", moduleId = "";
    SharedPreferences sh_Pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_flip);
        ButterKnife.bind(this);

        init();
        mainFlipView.addOnPositionChangeListener(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        pos = Integer.parseInt(getIntent().getStringExtra("position"));

        moduleId = getIntent().getStringExtra("moduleId");
        moduleContentId = getIntent().getStringExtra("moduleContentId");
        contentType = getIntent().getStringExtra("contentType");
        ((TextView)findViewById(R.id.tv_title)).setText(getIntent().getStringExtra("name"));

        if (NetworkConnectivity.isConnected(this)) {
            getContent();
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }
    }

    void getContent() {

        utils.showLoader(KhubFlipActivity.this);

        ApiClient client = new ApiClient();
        String URL = AppUrls.KHUB_BASE_URL+AppUrls.Content+"contentType="+contentType+"&moduleContentId="+moduleContentId+"&moduleId="+moduleId;

        Request get = client.getRequest(URL, sh_Pref);
        utils.showLog(TAG, "URL - " + URL);
        client.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();

                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                } else {
                    try {
                        JSONObject object = new JSONObject(resp);

                        if (object.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("result");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FlipObj>>() {
                            }.getType();
                            flipObjArrayList.clear();
                            flipObjArrayList.addAll(gson.fromJson(jsonArray.toString(), type));

                            Log.v(TAG, "flipObjArrayList - " + flipObjArrayList.size());
                            if (flipObjArrayList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (flipObjArrayList.size() > 0) {
                                            mainFlipView.setAdapter(new FlipAdapter(flipObjArrayList));
                                            mainFlipView.scrollToPosition(0);
                                        }
                                    }
                                });
                            }
                            else {
                                runOnUiThread(() -> new AlertDialog.Builder(KhubFlipActivity.this)
                                        .setTitle(getString(R.string.app_name))
                                        .setMessage("No Data To Load")
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                            dialog.dismiss();
                                            finish();
                                        })
                                        .setCancelable(false)
                                        .show());
                            }



                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });

            }
        });

    }

    @Override
    public void onPositionChange(FlipView flipView, int position) {
        tvCount.setText(""+(position+1)+"/"+flipObjArrayList.size());
    }


    public class FlipAdapter extends RecyclerView.Adapter<FlipAdapter.ViewHolder> {

        ArrayList<FlipObj> flipObjList;

        public FlipAdapter(ArrayList<FlipObj> flipObjArrayList) {
            this.flipObjList = flipObjArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_flip, parent, false));
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.tvTitle.setText(""+flipObjList.get(position).getTitle());

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                holder.tvDecp.setText(Html.fromHtml(flipObjList.get(position).getDesc(), Html.FROM_HTML_MODE_COMPACT));
//            } else {
                holder.tvDecp.setText(Html.fromHtml(""+flipObjList.get(position).getDesc()));
//            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.tvDecp.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            }
            if (flipObjList.get(position).getHyperText().isEmpty()){
                holder.tvref.setVisibility(View.INVISIBLE);
            }
            else {
                holder.tvref.setVisibility(View.VISIBLE);
            }

            holder.tvDecp.setOnClickListener(v -> {
                Intent details = new Intent(KhubFlipActivity.this, KhubFlipDetailed.class);
                details.putExtra("flip", flipObjList.get(position));
                startActivity(details);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            });
            holder.tvref.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""+flipObjList.get(position).getHyperText()));
                    startActivity(browserIntent);
                }
            });

            if (flipObjList.get(position).getPath() != null && !flipObjList.get(position).getPath().isEmpty()&&
            !flipObjList.get(position).getPath().equalsIgnoreCase("")) {
                holder.imgFlip.setVisibility(View.VISIBLE);
                Picasso.with(KhubFlipActivity.this).load(flipObjList.get(position).getPath()).placeholder(R.drawable.user_default)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imgFlip);
            }else{
                holder.imgFlip.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return flipObjList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDecp, tvref;
            ImageView imgFlip;

            public ViewHolder(View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tv_title);
                tvDecp = itemView.findViewById(R.id.tv_descp);
                tvref = itemView.findViewById(R.id.tv_ref);
                imgFlip = itemView.findViewById(R.id.img_flip);
            }
        }

    }

    void postContentActivity(){
        utils.showLoader(this);
        ApiClient client  = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        try {

            float pr = (((float) (mainFlipView.getPosition()+1)/ flipObjArrayList.size())*100);


            jsonObject.put("studentId", getIntent().getStringExtra("studentId"));
            jsonObject.put("courseId", getIntent().getStringExtra("courseId"));
            jsonObject.put("moduleId", moduleId);
            jsonObject.put("moduleContentId", moduleContentId);
            jsonObject.put("contentType", contentType);
            jsonObject.put("progress", pr);
            jsonObject.put("activityData",array );

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = client.postRequest(AppUrls.KHUB_BASE_URL+AppUrls.ContentActivity,body, sh_Pref);
        utils.showLog(TAG, "url "+ AppUrls.KHUB_BASE_URL+AppUrls.ContentActivity);
        utils.showLog(TAG, "body -"+ jsonObject.toString());
        client.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String res = response.body().string();
                    utils.showLog(TAG, "Resp - "+ res);
                    try {
                        JSONObject ParentjObject = new JSONObject(res);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            runOnUiThread(() -> {
                                finish();
                            });

                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
        postContentActivity();
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }
}