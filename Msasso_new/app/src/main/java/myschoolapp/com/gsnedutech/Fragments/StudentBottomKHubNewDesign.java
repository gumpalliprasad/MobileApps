package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentHome;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.CircularProgressBar;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.khub.BrainPowerActivity;
import myschoolapp.com.gsnedutech.khub.KhubAllCats;
import myschoolapp.com.gsnedutech.khub.KhubCategoryDetail;
import myschoolapp.com.gsnedutech.khub.KhubCourseInfoNew;
import myschoolapp.com.gsnedutech.khub.KhubMyCoursesActivity;
import myschoolapp.com.gsnedutech.khub.KhubScholarships;
import myschoolapp.com.gsnedutech.khub.models.KHubBanners;
import myschoolapp.com.gsnedutech.khub.models.KhubCategoryCourse;
import myschoolapp.com.gsnedutech.khub.models.KhubCategoryObj;
import myschoolapp.com.gsnedutech.khub.models.KhubMyCourses;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StudentBottomKHubNewDesign extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = StudentBottomKHubNewDesign.class.getName();

    CategoryAdapter adapter;

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (adapter!=null)
            adapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapter!=null)
            adapter.getFilter().filter(newText);
        return false;
    }

    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < listBanners.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    @BindView(R.id.sv_qbox_questions)
    SearchView svQboxQuestions;

    @BindView(R.id.rv_categories)
    RecyclerView rvCategories;
    @BindView(R.id.rv_myCourses)
    RecyclerView rvMyCourses;
    @BindView(R.id.rv_popular_courses)
    RecyclerView rvPopularCourses;
    @BindView(R.id.rv_trending)
    RecyclerView rvTrending;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.SliderDots)
    LinearLayout SliderDots;

    @BindView(R.id.tv_cat_showAll)
    TextView tvCatShowAll;
    @BindView(R.id.tv_mycos_showAll)
    TextView tvMyCosShowAll;

    private int dotscount;
    private ImageView[] dots;


    MyUtils utils = new MyUtils();

    List<KhubCategoryObj> listCategories = new ArrayList<>();
    List<KhubMyCourses> listMyCourses = new ArrayList<>();
    List<KHubBanners> listBanners = new ArrayList<>();
    List<KhubCategoryCourse> listPopular = new ArrayList<>();
    List<KhubCategoryCourse> listTrending = new ArrayList<>();



    Unbinder unbinder;

    Activity mActivity;

    View viewKhub;
    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";




    public StudentBottomKHubNewDesign() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewKhub = inflater.inflate(R.layout.fragment_student_bottom_k_hub_new, container, false);
        unbinder = ButterKnife.bind(this, viewKhub);
        init();
        return viewKhub;
    }


    void init() {
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        svQboxQuestions.setOnQueryTextListener(this);

        studentId = sObj.getStudentId();
        getCategories();

        tvCatShowAll.setOnClickListener(view -> {
            Intent catInt = new Intent(mActivity, KhubAllCats.class);
            startActivity(catInt);
            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });

        tvMyCosShowAll.setOnClickListener(view -> {
            Intent catInt = new Intent(mActivity, KhubMyCoursesActivity.class);
            startActivity(catInt);
            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });
        
        viewPager.setVisibility(View.GONE);
        SliderDots.setVisibility(View.GONE);
        viewKhub.findViewById(R.id.ll_trending).setVisibility(View.GONE);
        rvTrending.setVisibility(View.GONE);
        viewKhub.findViewById(R.id.ll_popular).setVisibility(View.GONE);
        rvPopularCourses.setVisibility(View.GONE);
        viewKhub.findViewById(R.id.ll_mycourses).setVisibility(View.GONE);
        rvMyCourses.setVisibility(View.GONE);
        tvCatShowAll.setVisibility(View.GONE);
        viewKhub.findViewById(R.id.ll_cat).setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        ((StudentHome) mActivity).fabMyDoubts.setVisibility(View.GONE);
        super.onResume();
        InputMethodManager inputManager = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(svQboxQuestions.getWindowToken(), 0);
//        getMyCourses();

    }

    void getBanners(){
        utils.showLoader(mActivity);
        ApiClient client = new ApiClient();
        Request get = client.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.KHUBBanners, sh_Pref);

        utils.showLog(TAG, "url -" + AppUrls.KHUB_BASE_URL+AppUrls.KHUBBanners);
        client.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getCategories();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG, "response " + resp);

                if (!response.isSuccessful()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getCategories();
                        }
                    });
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jar = jsonObject.getJSONArray("result");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<KHubBanners>>() {
                            }.getType();
                            listBanners.clear();
                            listBanners.addAll(gson.fromJson(jar.toString(), type));

                            if (listBanners.size() > 0) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setBanners();
                                    }
                                });
                            }
                            else {
                                mActivity.runOnUiThread(() -> {
                                    viewPager.setVisibility(View.GONE);
                                });
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getCategories();
                    }
                });
            }
        });
    }

    void setBanners(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mActivity);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(mActivity);
            dots[i].setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.non_active_dots));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            SliderDots.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.active_dots));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.non_active_dots));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.active_dots));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
    }

    void getMyCourses() {

//        utils.showLoader(mActivity);
        ApiClient client = new ApiClient();
        Request get = client.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.MyCourses+"studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""), sh_Pref);

        utils.showLog(TAG, "url -" + AppUrls.KHUB_BASE_URL+AppUrls.MyCourses+"studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""));
        client.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
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
                    mActivity.runOnUiThread(new Runnable() {
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
                            Type type = new TypeToken<List<KhubMyCourses>>() {
                            }.getType();
                            listMyCourses.clear();
                            listMyCourses.addAll(gson.fromJson(jar.toString(), type));

                            if (listMyCourses.size() > 0) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewKhub.findViewById(R.id.ll_mycourses).setVisibility(View.VISIBLE);
                                        rvMyCourses.setVisibility(View.VISIBLE);
                                        rvMyCourses.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
                                        rvMyCourses.setAdapter(new CourseAdapter(listMyCourses));
                                    }
                                });
                            }
                            else {
                                mActivity.runOnUiThread(() -> {
                                    viewKhub.findViewById(R.id.ll_mycourses).setVisibility(View.GONE);
                                    rvMyCourses.setVisibility(View.GONE);
                                });
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    void getTrendingCourses() {

//        utils.showLoader(mActivity);
        ApiClient client = new ApiClient();
        Request get = client.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.KhubTrendingCourses, sh_Pref);

        utils.showLog(TAG, "url -" + AppUrls.KHUB_BASE_URL+AppUrls.KhubTrendingCourses);
        client.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
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
                    mActivity.runOnUiThread(new Runnable() {
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
                            Type type = new TypeToken<List<KhubCategoryCourse>>() {
                            }.getType();
                            listTrending.clear();
                            listTrending.addAll(gson.fromJson(jar.toString(), type));

                            if (listTrending.size() > 0) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewKhub.findViewById(R.id.ll_trending).setVisibility(View.VISIBLE);
                                        rvTrending.setVisibility(View.VISIBLE);
                                        rvTrending.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
                                        rvTrending.setAdapter(new TrendingCourseAdapter(listTrending));
                                    }
                                });
                            }
                            else {
                                mActivity.runOnUiThread(() -> {
                                    viewKhub.findViewById(R.id.ll_trending).setVisibility(View.GONE);
                                    rvTrending.setVisibility(View.GONE);
                                });
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    void getPopularCourses() {

//        utils.showLoader(mActivity);
        ApiClient client = new ApiClient();
        Request get = client.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.KhubPopularCourses, sh_Pref);

        utils.showLog(TAG, "url -" + AppUrls.KHUB_BASE_URL+AppUrls.KhubPopularCourses);
        client.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getTrendingCourses();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG, "response " + resp);

                if (!response.isSuccessful()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getTrendingCourses();
                        }
                    });
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jar = jsonObject.getJSONArray("result");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<KhubCategoryCourse>>() {
                            }.getType();
                            listPopular.clear();
                            listPopular.addAll(gson.fromJson(jar.toString(), type));

                            if (listPopular.size() > 0) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewKhub.findViewById(R.id.ll_popular).setVisibility(View.VISIBLE);
                                        rvPopularCourses.setVisibility(View.VISIBLE);
                                        rvPopularCourses.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
                                        rvPopularCourses.setAdapter(new PopularCourseAdapter(listPopular));
                                    }
                                });
                            }
                            else {
                                mActivity.runOnUiThread(() -> {
                                    viewKhub.findViewById(R.id.ll_popular).setVisibility(View.GONE);
                                    rvPopularCourses.setVisibility(View.GONE);
                                });
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       getTrendingCourses();
                    }
                });
            }
        });

    }

    void getCategories() {

        rvCategories.setVisibility(View.GONE);
        viewKhub.findViewById(R.id.pb_cat).setVisibility(View.VISIBLE);
        ApiClient client = new ApiClient();
        Request get = client.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.CATEGORIES, sh_Pref);

        utils.showLog(TAG, "url -" + AppUrls.KHUB_BASE_URL+AppUrls.CATEGORIES);
        client.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewKhub.findViewById(R.id.pb_cat).setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG, "response " + resp);
                mActivity.runOnUiThread(() -> {
                    viewKhub.findViewById(R.id.pb_cat).setVisibility(View.GONE);
                });

                if (!response.isSuccessful()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            utils.dismissDialog();
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
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvCategories.setVisibility(View.VISIBLE);
                                        rvCategories.setLayoutManager(new GridLayoutManager(mActivity, 3));
                                        adapter = new CategoryAdapter(listCategories);
                                        rvCategories.setAdapter(adapter);
                                        if (svQboxQuestions.getQuery().toString().length()>0){
                                            svQboxQuestions.setQuery(svQboxQuestions.getQuery().toString(),true);
                                        }
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

            }
        });

    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements Filterable {

        List<KhubCategoryObj> list, list_filtered;

        public CategoryAdapter(List<KhubCategoryObj> categoryObjs) {
            this.list = categoryObjs;
            this.list_filtered = categoryObjs;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_khub_cat_new, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvKhubCatName.setText(list_filtered.get(position).getKCatName());

            if (list_filtered.get(position).getKCatImage() != null && !list_filtered.get(position).getKCatImage().equalsIgnoreCase("")) {
                Picasso.with(mActivity).load(list_filtered.get(position).getKCatImage()).placeholder(R.drawable.ic_khub_extreme)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivKhubCat);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (list_filtered.get(position).getKCatName().equalsIgnoreCase("Brain power")){
                        Intent intent = new Intent(mActivity, BrainPowerActivity.class);
                        intent.putExtra("id", list_filtered.get(position).getId());
                        intent.putExtra("title", list_filtered.get(position).getKCatName());
                        intent.putExtra("image", list_filtered.get(position).getKCatImage());
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                    else {
                        Intent intent = new Intent(mActivity, KhubCategoryDetail.class);
                        intent.putExtra("id", list_filtered.get(position).getId());
                        intent.putExtra("title", list_filtered.get(position).getKCatName());
                        intent.putExtra("image", list_filtered.get(position).getKCatImage());
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    if (string.isEmpty()) {
                        list_filtered = list;
                    } else {

                        List<KhubCategoryObj> filteredList = new ArrayList<>();
                        for (KhubCategoryObj s : list) {

                            if (s.getKCatName().toLowerCase().contains(string.toLowerCase())) {
                                filteredList.add(s);
                            }
                        }

                        list_filtered = filteredList;

                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<KhubCategoryObj>) results.values;
                    if (list_filtered.size()>0){
                        rvCategories.setVisibility(View.VISIBLE);
//                        tvNoQuestions.setVisibility(View.GONE);
                    }
                    else {
                        rvCategories.setVisibility(View.GONE);
//                        tvNoQuestions.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();
                }
            };
        }

        public void update(List<KhubCategoryObj> listUpdated) {
            list_filtered = listUpdated;
            notifyDataSetChanged();
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

    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

        List<KhubMyCourses> listCat;

        public CourseAdapter(List<KhubMyCourses> listCat) {
            this.listCat = listCat;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_khub_mycourse, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);
            holder.tvCourseName.setText(listCat.get(position).getKCourseName());
            holder.tvAuthor.setText(listCat.get(position).getOwnerName());

            if (listCat.get(position).getKCourseImage() != null && !listCat.get(position).getKCourseImage().equalsIgnoreCase("")) {
                Picasso.with(mActivity).load(listCat.get(position).getKCourseImage()).placeholder(R.drawable.ic_khub_extreme)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivCourseImage);
            }
            holder.courseProgress.setInnerBackgroundColor(Color.parseColor("#ffffff"));
            holder.courseProgress.setTextSize(0);
            holder.courseProgress.setFinishedStrokeColor(Color.parseColor("#00D1FF"));
            holder.courseProgress.setProgress(listCat.get(position).getCProgress());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, KhubCourseInfoNew.class);
                    KhubCategoryCourse khubCategoryCourse = new KhubCategoryCourse();
                    khubCategoryCourse.setIsEnrolled(true);
                    khubCategoryCourse.setId(listCat.get(position).getId());
                    khubCategoryCourse.setKCourseDesc(listCat.get(position).getKCourseDesc());
                    khubCategoryCourse.setKCourseImage(listCat.get(position).getKCourseImage());
                    khubCategoryCourse.setKCourseName(listCat.get(position).getKCourseName());
                    khubCategoryCourse.setOwnerName(listCat.get(position).getOwnerName());
                    intent.putExtra("courseObj",(Serializable)khubCategoryCourse);
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return Math.min(listCat.size(),5);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvCourseName, tvAuthor;
            ImageView ivCourseImage;
            CircularProgressBar courseProgress;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                courseProgress = itemView.findViewById(R.id.cp_cProgress);
                tvAuthor = itemView.findViewById(R.id.tv_author);
                tvCourseName = itemView.findViewById(R.id.tv_courseName);
                ivCourseImage = itemView.findViewById(R.id.iv_course_image);
            }
        }
    }

    class TrendingCourseAdapter extends RecyclerView.Adapter<TrendingCourseAdapter.ViewHolder> {

        List<KhubCategoryCourse> listCat;

        public TrendingCourseAdapter(List<KhubCategoryCourse> listCat) {
            this.listCat = listCat;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_khub_popular, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);
            holder.tvCourseName.setText(listCat.get(position).getKCourseName());
            holder.tvAuthor.setText(listCat.get(position).getOwnerName());
            holder.fmProgress.setVisibility(View.GONE);

            if (listCat.get(position).getKCourseImage() != null && !listCat.get(position).getKCourseImage().equalsIgnoreCase("")) {
                Picasso.with(mActivity).load(listCat.get(position).getKCourseImage()).placeholder(R.drawable.ic_khub_extreme)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivCourseImage);
            }
            holder.courseProgress.setInnerBackgroundColor(Color.parseColor("#ffffff"));
            holder.courseProgress.setTextSize(0);
            holder.courseProgress.setFinishedStrokeColor(Color.parseColor("#00D1FF"));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, KhubCourseInfoNew.class);
                    KhubCategoryCourse khubCategoryCourse = listCat.get(position);
                    intent.putExtra("courseObj",(Serializable) khubCategoryCourse);
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listCat.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvCourseName, tvAuthor;
            ImageView ivCourseImage;
            CircularProgressBar courseProgress;
            FrameLayout fmProgress;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                courseProgress = itemView.findViewById(R.id.cp_cProgress);
                tvAuthor = itemView.findViewById(R.id.tv_author);
                tvCourseName = itemView.findViewById(R.id.tv_courseName);
                ivCourseImage = itemView.findViewById(R.id.iv_course_image);
                fmProgress = itemView.findViewById(R.id.fm_progress);
            }
        }
    }

    class PopularCourseAdapter extends RecyclerView.Adapter<PopularCourseAdapter.ViewHolder> {

        List<KhubCategoryCourse> listCat;

        public PopularCourseAdapter(List<KhubCategoryCourse> listCat) {
            this.listCat = listCat;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_khub_popular, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);
            holder.tvCourseName.setText(listCat.get(position).getKCourseName());
            holder.tvAuthor.setText(listCat.get(position).getOwnerName());
            holder.fmProgress.setVisibility(View.GONE);

            if (listCat.get(position).getKCourseImage() != null && !listCat.get(position).getKCourseImage().equalsIgnoreCase("")) {
                Picasso.with(mActivity).load(listCat.get(position).getKCourseImage()).placeholder(R.drawable.ic_khub_extreme)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivCourseImage);
            }
            holder.courseProgress.setInnerBackgroundColor(Color.parseColor("#ffffff"));
            holder.courseProgress.setTextSize(0);
            holder.courseProgress.setFinishedStrokeColor(Color.parseColor("#00D1FF"));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, KhubCourseInfoNew.class);
                    KhubCategoryCourse khubCategoryCourse = listCat.get(position);
                    khubCategoryCourse.setId(khubCategoryCourse.getCourseId());
                    intent.putExtra("courseObj",(Serializable) khubCategoryCourse);
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listCat.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvCourseName, tvAuthor;
            ImageView ivCourseImage;
            CircularProgressBar courseProgress;
            FrameLayout fmProgress;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                courseProgress = itemView.findViewById(R.id.cp_cProgress);
                tvAuthor = itemView.findViewById(R.id.tv_author);
                tvCourseName = itemView.findViewById(R.id.tv_courseName);
                ivCourseImage = itemView.findViewById(R.id.iv_course_image);
                fmProgress = itemView.findViewById(R.id.fm_progress);
            }
        }
    }
    void getThumbnail(String url, ImageView iv) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(url)
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {

                } else {
                    try {
                        String resp = responseBody.string();
                        JSONObject ParentjObject = new JSONObject(resp);
                        Picasso.with(mActivity).load(ParentjObject.getString("thumbnail_small")).into(iv);
                    } catch (Exception e) {

                    }
                }
            }
        });

    }

    class ViewPagerAdapter extends PagerAdapter {

        private Context context;

        public ViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return listBanners.size();
        }

        @Override
        public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
            return view == object;
        }

        @NotNull
        @Override
        public Object instantiateItem(@NotNull ViewGroup container, final int position) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.layout_intro_one, null);
            ImageView banImg = view.findViewById(R.id.image);
            Picasso.with(mActivity).load(listBanners.get(position).getBannerImage()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(banImg);
            banImg.setOnClickListener(view1 -> {
                if (listBanners.get(position).getIsAction()){
                    if (listBanners.get(position).getActionType().equalsIgnoreCase("form")){
                        Intent banIntent = new Intent(mActivity, KhubScholarships.class);
                        banIntent.putExtra("banner", listBanners.get(position));
                        startActivity(banIntent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                }
            });

            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);
            return view;

        }

        @Override
        public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {

            ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}