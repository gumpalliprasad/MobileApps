package myschoolapp.com.gsnedutech.khub;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.text.LineBreaker;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import myschoolapp.com.gsnedutech.Models.StudentObj;
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

public class KhubFlipActivityNew extends AppCompatActivity implements View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubFlipActivityNew.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    int pos;
    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_count)
    TextView tvCount;

    @BindView(R.id.img_flip)
    ImageView imgFlip;

    @BindView(R.id.tv_descp)
    TextView tvDesc;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.ll_prev)
    LinearLayout llPrev;
    @BindView(R.id.ll_next)
    LinearLayout llNext;

    @BindView(R.id.tv_ref)
    TextView tvRef;

    @BindView(R.id.tv_next)
    TextView tvNext;

    int qNum = 0;

    ArrayList<FlipObj> flipObjArrayList = new ArrayList<FlipObj>();


    String contentType = "", moduleContentId = "", moduleId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_flip_new);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        pos = Integer.parseInt(getIntent().getStringExtra("position"));

        moduleId = getIntent().getStringExtra("moduleId");
        moduleContentId = getIntent().getStringExtra("moduleContentId");
        contentType = getIntent().getStringExtra("contentType");
        ((TextView)findViewById(R.id.tv_title)).setText(getIntent().getStringExtra("name"));


        llPrev.setOnClickListener(this);
        llNext.setOnClickListener(this);
    }

    void getContent() {

        utils.showLoader(KhubFlipActivityNew.this);

        ApiClient client = new ApiClient();
        String URL = AppUrls.KHUB_BASE_URL+AppUrls.Content+"contentType="+contentType+"&moduleContentId="+moduleContentId+"&moduleId="+moduleId+"&studentId="+sObj.getStudentId()+"&schemaName="+sh_Pref.getString("schema","");

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
                                            try {
                                                if (object.has("activityData") && object.getJSONArray("activityData").length()>0){
                                                    JSONArray arr = object.getJSONArray("activityData");
                                                    JSONArray jsonArray1 = arr.getJSONObject(0).getJSONArray("activityData");
                                                    qNum = jsonArray1.getJSONObject(0).getInt("positionId");
                                                    if (qNum == flipObjArrayList.size()-1){
                                                        showFinishAlert("finish");
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            loadText(qNum);
                                        }
                                    }
                                });
                            }
                            else {
                                runOnUiThread(() -> new AlertDialog.Builder(KhubFlipActivityNew.this)
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_prev:
                if (--qNum<0){
                    qNum = 0;
                }
                loadText(qNum);
                break;
            case R.id.ll_next:
                if (!tvNext.getText().toString().equals("Finish")) {
                    if (++qNum >= flipObjArrayList.size()) {
                        qNum = flipObjArrayList.size() - 1;
                    }
                    loadText(qNum);
                }
                else {
                    onBackPressed();
                }
                break;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubFlipActivityNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                if (flipObjArrayList.size() == 0)
                    getContent();
            }
            isNetworkAvail = true;
        }
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
                Intent details = new Intent(KhubFlipActivityNew.this, KhubFlipDetailed.class);
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
                Picasso.with(KhubFlipActivityNew.this).load(flipObjList.get(position).getPath()).placeholder(R.drawable.user_default)
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

    private void loadText(int i) {

        llPrev.setVisibility(View.VISIBLE);
        llNext.setVisibility(View.VISIBLE);
        tvNext.setText("Next Page");

        if(i == 0)
        {
            llPrev.setVisibility(View.INVISIBLE);
        }
        if (i== flipObjArrayList.size()-1){
            tvNext.setText("Finish");
        }
        tvCount.setText(""+(i+1)+"/"+flipObjArrayList.size());

        String mainString = flipObjArrayList.get(i).getDesc();
        tvTitle.setText(""+flipObjArrayList.get(i).getTitle());

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                holder.tvDecp.setText(Html.fromHtml(flipObjList.get(position).getDesc(), Html.FROM_HTML_MODE_COMPACT));
//            } else {
        tvDesc.setText(Html.fromHtml(""+flipObjArrayList.get(i).getDesc()));
//            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvDesc.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        if (flipObjArrayList.get(i).getPath() != null && !flipObjArrayList.get(i).getPath().isEmpty()&&
                !flipObjArrayList.get(i).getPath().equalsIgnoreCase("")) {
            imgFlip.setVisibility(View.VISIBLE);
            Picasso.with(KhubFlipActivityNew.this).load(flipObjArrayList.get(i).getPath()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(imgFlip);
        }else{
            imgFlip.setVisibility(View.GONE);
        }

        if (flipObjArrayList.get(i).getHyperText().isEmpty()){
            tvRef.setVisibility(View.INVISIBLE);
        }
        else {
            tvRef.setVisibility(View.VISIBLE);
        }

        tvRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""+flipObjArrayList.get(i).getHyperText()));
                startActivity(browserIntent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    void postContentActivity(String refresh){
        utils.showLoader(this);
        ApiClient client  = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        try {

            float pr = (((float) (qNum+1)/ flipObjArrayList.size())*100);


            jsonObject.put("studentId", getIntent().getStringExtra("studentId"));
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("courseId", getIntent().getStringExtra("courseId"));
            jsonObject.put("moduleId", moduleId);
            jsonObject.put("moduleContentId", moduleContentId);
            jsonObject.put("contentType", contentType);
            if (refresh.isEmpty()){
                jsonObject.put("progress", pr);
                JSONObject data = new JSONObject();
                data.put("positionId", qNum);
                data.put("moduleDataId", flipObjArrayList.get(qNum).getId());
                array.put(data);
                jsonObject.put("activityData",array );
            }
            else {
                jsonObject.put("progress", 0);
                jsonObject.put("activityData",new JSONArray() );
            }


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
                            if (refresh.isEmpty())runOnUiThread(() -> finish());

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

    void showFinishAlert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(KhubFlipActivityNew.this);
        builder1.setMessage("You have finished this Activity. Do you want to start Again?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        recreate();
                        postContentActivity("refresh");
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onBackPressed() {
        postContentActivity("");
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }
}