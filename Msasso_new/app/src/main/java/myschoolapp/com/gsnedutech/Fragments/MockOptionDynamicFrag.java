package myschoolapp.com.gsnedutech.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.MockOptionTypeSelection;
import myschoolapp.com.gsnedutech.MockTestInfo;
import myschoolapp.com.gsnedutech.Models.StdnTestCategories;
import myschoolapp.com.gsnedutech.Models.StdnTestDefClsCourseSub;
import myschoolapp.com.gsnedutech.PracticeTest;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MockOptionDynamicFrag extends Fragment {

    private static final String TAG = MockOptionDynamicFrag.class.getName();
    MyUtils utils = new MyUtils();

    View viewMockOptionDynamicFrag;
    Unbinder unbinder;

    StdnTestDefClsCourseSub clsCourseSubObj;
    List<StdnTestCategories> categoryList = new ArrayList<>();
    String navto;


    @BindView(R.id.rv_mock_op)
    RecyclerView rvMockOp;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;





    private void getTestCategories(String s, String courseId) {
        ApiClient apiClient = new ApiClient();
        utils.showLog(TAG, "Log - " +new AppUrls().GetTestCategories + "schemaName=" + getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&instId=" + s+ "&courseId=" + courseId);
        Request request = apiClient.getRequest(new AppUrls().GetTestCategories + "schemaName=" + getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&instId=" + s+ "&courseId=" + courseId, sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    try {
                        String responce = response.body().string();
                        utils.showLog(TAG, "Students Subjects - " + responce);
                        JSONObject parentjObject = new JSONObject(responce);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("TestCategories");

                            List<List<StdnTestCategories>> stdnCategoryList = new ArrayList<>();

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StdnTestCategories>>() {}.getType();
                            stdnCategoryList.add(gson.fromJson(jsonArr.toString(), type));

                            categoryList.clear();
                            categoryList.addAll(stdnCategoryList.get(0));

                            utils.showLog(TAG,"size "+categoryList.size());

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvMockOp.setAdapter(new OptionsAdapter());
                                }
                            });
                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            getActivity().runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, getActivity(), message, sh_Pref);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }




    public MockOptionDynamicFrag(String navto, StdnTestDefClsCourseSub stdnTestDefClsCourseSub) {
        this.navto = navto;
        this.clsCourseSubObj = stdnTestDefClsCourseSub;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewMockOptionDynamicFrag = inflater.inflate(R.layout.fragment_mock_option_dynamic, container, false);
        unbinder = ButterKnife.bind(this, viewMockOptionDynamicFrag);

        rvMockOp.setLayoutManager(new LinearLayoutManager(getActivity()));
        sh_Pref = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        getTestCategories("1",clsCourseSubObj.getCourseId());

        return viewMockOptionDynamicFrag;
    }


    class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

        @NonNull
        @Override
        public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (navto.equalsIgnoreCase("mock")){
                return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_mock_option, parent, false));
            }else {
                return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_practice_option, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, final int position) {

            holder.setIsRecyclable(false);
            holder.tvType.setText(categoryList.get(position).getCategoryName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (categoryList.get(position).getCategoryName().equalsIgnoreCase("IIT-FOUNDATION") || categoryList.get(position).getCategoryName().equalsIgnoreCase("NEET-Foundation")) {
                        if (navto.equalsIgnoreCase("mock")) {
                            Intent testInfoIntent = new Intent(getActivity(), MockTestInfo.class);
                            testInfoIntent.putExtra("courseObj", clsCourseSubObj);
                            testInfoIntent.putExtra("testCategoryObj", categoryList.get(position));
                            testInfoIntent.putExtra(AppConst.TopicCCMapId, "0");
                            testInfoIntent.putExtra(AppConst.ChapterCCMapId, "0");
                            startActivity(testInfoIntent);
                            getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } else {
                            Intent intent = new Intent(getActivity(), PracticeTest.class);
                            intent.putExtra("classId", clsCourseSubObj.getClasses().get(0).getClassId());
                            intent.putExtra("className", clsCourseSubObj.getClasses().get(0).getClassName());
                            intent.putExtra("subjectId", "0");
                            intent.putExtra("subjectName", "0");
                            intent.putExtra("chapterId", "0");
                            intent.putExtra("chapterName", "0");
                            intent.putExtra("topicId", "0");
                            intent.putExtra("testType", categoryList.get(position).getCategoryName());
                            intent.putExtra("topicName", "0");
                            intent.putExtra("courseId", clsCourseSubObj.getCourseId());
                            intent.putExtra("contentType", clsCourseSubObj.getClasses().get(0).getSubjects().get(0).getContentType());
                            intent.putExtra("testCategoryId", categoryList.get(position).getCategoryId());
                            intent.putExtra(AppConst.ChapterCCMapId, "0");
                            intent.putExtra(AppConst.TopicCCMapId, "0");
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }

                    } else if (categoryList.get(position).getCategoryName().equalsIgnoreCase("CLASS-WISE")) {
                        if (navto.equalsIgnoreCase("mock")) {
                            Intent intent = new Intent(getActivity(), MockTestInfo.class);
                            intent.putExtra("classId", clsCourseSubObj.getClasses().get(0).getClassId());
                            intent.putExtra("className", clsCourseSubObj.getClasses().get(0).getClassName());
                            intent.putExtra("subjectId", "0");
                            intent.putExtra("subjectName", "0");
                            intent.putExtra("chapterId", "0");
                            intent.putExtra("chapterName", "0");
                            intent.putExtra("topicId", "0");
                            intent.putExtra("topicName", "0");
                            intent.putExtra("testType", "CLASS-WISE");
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("contentType", clsCourseSubObj.getClasses().get(0).getSubjects().get(0).getContentType());
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra(AppConst.TopicCCMapId, "0");
                            intent.putExtra(AppConst.ChapterCCMapId, "0");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), PracticeTest.class);
                            intent.putExtra("classId", clsCourseSubObj.getClasses().get(0).getClassId());
                            intent.putExtra("className", clsCourseSubObj.getClasses().get(0).getClassName());
                            intent.putExtra("subjectId", "0");
                            intent.putExtra("subjectName", "0");
                            intent.putExtra("chapterId", "0");
                            intent.putExtra("chapterName", "0");
                            intent.putExtra("topicId", "0");
                            intent.putExtra("testType", "CLASS-WISE");
                            intent.putExtra("topicName", "0");
                            intent.putExtra("courseId", clsCourseSubObj.getCourseId());
                            intent.putExtra("contentType", clsCourseSubObj.getClasses().get(0).getSubjects().get(0).getContentType());
                            intent.putExtra("testCategoryId", categoryList.get(position).getCategoryId());
                            startActivity(intent);
                        }

//                            startActivity(new Intent(MockOptionTypeSelection.this, MocKTest.class));
                        getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else {
                        Intent intent = new Intent(getActivity(), MockOptionTypeSelection.class);
                        intent.putExtra("courseObj", clsCourseSubObj);
                        intent.putExtra("testCategoryObj", categoryList.get(position));
                        intent.putExtra("type", categoryList.get(position).getCategoryName());
                        intent.putExtra("navto", navto);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                }
            });


        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvType;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvType = itemView.findViewById(R.id.tv_type);
            }
        }
    }
}