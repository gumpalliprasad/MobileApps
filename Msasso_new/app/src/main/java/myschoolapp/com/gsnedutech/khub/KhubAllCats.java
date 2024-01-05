package myschoolapp.com.gsnedutech.khub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.khub.models.KhubCategoryObj;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class KhubAllCats extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubAllCats.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;


    MyUtils utils = new MyUtils();
    @BindView(R.id.rv_categories)
    RecyclerView rvCategories;

    List<KhubCategoryObj> listCategories = new ArrayList<>();
    SharedPreferences sh_Pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_all_cats);
        ButterKnife.bind(this);
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

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

    void getCategories() {

        utils.showLoader(KhubAllCats.this);
        ApiClient client = new ApiClient();
        Request get = client.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.CATEGORIES, sh_Pref);

        utils.showLog(TAG, "url -" + AppUrls.KHUB_BASE_URL+AppUrls.CATEGORIES);
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
                utils.showLog(TAG, "response " + resp);

                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jar = jsonObject.getJSONArray("result");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<KhubCategoryObj>>() {
                            }.getType();
                            listCategories.clear();
                            listCategories.addAll(gson.fromJson(jar.toString(), type));
                            List<KhubCategoryObj> filteredList = new ArrayList<>();
                            for (int i = 0; i <listCategories.size() ; i++) {
                                if (listCategories.get(i).getIsActive())
                                    filteredList.add(listCategories.get(i));
                            }

                            listCategories.clear();
                            listCategories.addAll(filteredList);

                            if (listCategories.size() > 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (getResources().getString(R.string.screen).equalsIgnoreCase("Xlarge")){
                                            rvCategories.setLayoutManager(new GridLayoutManager(KhubAllCats.this, 4));
                                        }
                                        else {
                                            rvCategories.setLayoutManager(new GridLayoutManager(KhubAllCats.this, 4));
                                        }
                                        rvCategories.setAdapter(new CategoryAdapter(listCategories));
                                    }
                                });
                            }
                            else {
                                rvCategories.setVisibility(View.GONE);
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
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubAllCats.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getCategories();
            }
            isNetworkAvail = true;
        }
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

        List<KhubCategoryObj> listCat;

        public CategoryAdapter(List<KhubCategoryObj> listCat) {
            this.listCat = listCat;
        }

        @NonNull
        @Override
        public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CategoryAdapter.ViewHolder(LayoutInflater.from(KhubAllCats.this).inflate(R.layout.item_khub_cat, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvKhubCatName.setText(listCat.get(position).getKCatName());

            if (listCat.get(position).getKCatImage() != null && !listCat.get(position).getKCatImage().equalsIgnoreCase("")) {
                Picasso.with(KhubAllCats.this).load(listCat.get(position).getKCatImage()).placeholder(R.drawable.ic_khub_extreme)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivKhubCat);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listCat.get(position).getKCatName().equalsIgnoreCase("Brain power")){
                        Intent intent = new Intent(KhubAllCats.this, BrainPowerActivity.class);
                        intent.putExtra("id", listCat.get(position).getId());
                        intent.putExtra("title", listCat.get(position).getKCatName());
                        intent.putExtra("image", listCat.get(position).getKCatImage());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                    else {
                        Intent intent = new Intent(KhubAllCats.this, KhubCategoryDetail.class);
                        intent.putExtra("id", listCat.get(position).getId());
                        intent.putExtra("title", listCat.get(position).getKCatName());
                        intent.putExtra("image", listCat.get(position).getKCatImage());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return listCat.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvKhubCatName;
            ImageView ivKhubCat;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvKhubCatName = itemView.findViewById(R.id.tv_cat_name);
                ivKhubCat = itemView.findViewById(R.id.iv_cat_image);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }

}