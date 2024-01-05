package myschoolapp.com.gsnedutech.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.ResourcesFiles;
import myschoolapp.com.gsnedutech.Models.SubChapterTopic;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import myschoolapp.com.gsnedutech.Util.YoutubeActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ResourcesListActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;
    @BindView(R.id.rv_resources)
    RecyclerView rvResources;
    List<ResourcesFiles> listFiles = new ArrayList<>();
    MyUtils utils = new MyUtils();
    SubChapterTopic mTopic = new SubChapterTopic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_deatils);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        if (getIntent().hasExtra(AppConst.TOPIC))
            mTopic = (SubChapterTopic) getIntent().getSerializableExtra(AppConst.TOPIC);
        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, ResourcesListActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                init();
                getStudentResources(mTopic);
            }
            isNetworkAvail = true;
        }
    }

    class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ResourcesListActivity.this).inflate(R.layout.resource_items, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String filePath = listFiles.get(position).getFilePath();
            holder.tvFileName.setText(getFileName(filePath.substring(0, filePath.lastIndexOf("."))));

            holder.itemView.setOnClickListener(view -> {
                if (listFiles.get(position).getFilePath().contains("pdf")) {
                    Intent intent = new Intent(ResourcesListActivity.this, NewPdfViewer.class);
                    intent.putExtra("url", listFiles.get(position).getFilePath());
                    startActivity(intent);
                } else if (listFiles.get(position).getFilePath().contains("mp4")) {
                    Intent intent = new Intent(ResourcesListActivity.this, PlayerActivity.class);
                    intent.putExtra("url", listFiles.get(position).getFilePath());
                    startActivity(intent);
                } else if (listFiles.get(position).getFilePath().contains("youtube")) {
                    Intent intent = new Intent(ResourcesListActivity.this, YoutubeActivity.class);

                    String s[] = listFiles.get(position).getFilePath().split("/");

                    intent.putExtra("videoItem", s[s.length - 1]);
                    startActivity(intent);
                } else if (listFiles.get(position).getFilePath().contains("jpg") || listFiles.get(position).getFilePath().contains("png")) {
                    Intent intent = new Intent(ResourcesListActivity.this, ImageDisp.class);
                    intent.putExtra("path", listFiles.get(position).getFilePath());
                    startActivity(intent);
                } else if (listFiles.get(position).getFilePath().contains("doc") || listFiles.get(position).getFilePath().equalsIgnoreCase("docx") || listFiles.get(position).getFilePath().equalsIgnoreCase("ppt") || listFiles.get(position).getFilePath().equalsIgnoreCase("pptx") | listFiles.get(position).getFilePath().equalsIgnoreCase("xls") || listFiles.get(position).getFilePath().equalsIgnoreCase("xlsx")) {
                    Intent intent = new Intent(ResourcesListActivity.this, PdfWebViewer.class);
                    intent.putExtra("url", listFiles.get(position).getFilePath());
                    startActivity(intent);
                } else {
                    Toast.makeText(ResourcesListActivity.this, "Cannot open this type of file!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return listFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFileName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
            }
        }
    }

    private String getFileName(String filePath) {
        if (filePath.length() > 0 && filePath.contains("/")) {
            String s[] = filePath.split("/");
            return s[s.length - 1];
        }
        return "";
    }

    void getStudentResources(SubChapterTopic mTopic) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetTextbookFilesByTopic + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")
                        + "&topicId=" + mTopic.getTopicId() +
                        "&topicCCMapId=" + mTopic.getTopicCCMapId()
                        + "&contentOwner=CEDZ")
                .build();

        utils.showLog("TAG", "url -" + AppUrls.GetTextbookFilesByTopic + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")
                + "&topicId=" + mTopic.getTopicId() +
                "&topicCCMapId=" + mTopic.getTopicCCMapId() + "&contentOwner=CEDZ");

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        //error from server
                    } else {
                        String resp = responseBody.string();
                        utils.showLog("TAG", "response- " + resp);
                        JSONObject parentObject = new JSONObject(resp);
                        if (parentObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = parentObject.getJSONArray("files");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ResourcesFiles>>() {
                            }.getType();
                            listFiles.clear();
                            listFiles.addAll(gson.fromJson(jsonArr.toString(), type));
                            utils.showLog("TAG", "size - " + listFiles.size());

                            runOnUiThread(() -> {
                                if (listFiles.size() > 1) {
                                    rvResources.setLayoutManager(new LinearLayoutManager(ResourcesListActivity.this, RecyclerView.VERTICAL, false));
                                    rvResources.setAdapter(new ResourcesAdapter());
                                    rvResources.addItemDecoration(new DividerItemDecoration(rvResources.getContext(), DividerItemDecoration.VERTICAL));
                                } else if (listFiles.size() > 0) {
                                    Intent intent = new Intent(ResourcesListActivity.this, NewPdfViewer.class);
                                    intent.putExtra("url", listFiles.get(0).getFilePath());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
