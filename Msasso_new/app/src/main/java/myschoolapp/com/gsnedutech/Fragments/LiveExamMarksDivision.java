package myschoolapp.com.gsnedutech.Fragments;

import android.content.Intent;
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
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.JEETestSectionDetail;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentOnlineTestActivity;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;


public class LiveExamMarksDivision extends Fragment {

    private static final String TAG = "SriRam -" + LiveExamMarksDivision.class.getName();

    View liveExamMarksDivisonView;
    Unbinder unbinder;

    @BindView(R.id.rv_section)
    RecyclerView rvSection;

    List<JEETestSectionDetail> listSectionDetails = new ArrayList<>();
    MyUtils utils = new MyUtils();
    StudentOnlineTestObj liveExam;

    public LiveExamMarksDivision() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        liveExamMarksDivisonView = inflater.inflate(R.layout.fragment_live_exam_marks_division, container, false);
        unbinder = ButterKnife.bind(this, liveExamMarksDivisonView);

        liveExam = (StudentOnlineTestObj)getActivity().getIntent().getSerializableExtra("live");
        getSections(liveExam.getTestCategory(),liveExam.getMjeeSectionTemplateName());




        return liveExamMarksDivisonView;
    }

    @OnClick({R.id.tv_start})
    public void onViewClicked() {
        Intent onlineTestIntent = new Intent(getActivity(), StudentOnlineTestActivity.class);
        onlineTestIntent.putExtra("studentId", getActivity().getIntent().getStringExtra("studentId"));
        onlineTestIntent.putExtra("live", (Serializable) liveExam);
        onlineTestIntent.putExtra("sections", (Serializable) listSectionDetails);
        startActivity(onlineTestIntent);
        getActivity().finish();
    }

    void getSections(String catId,String templateName){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTestSectons +"schemaName=" +getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&catId=" + catId + "&templateName=" + templateName)
                .build();

        utils.showLog(TAG,"url - "+AppUrls.GetStudentOnlineTestSectons +"schemaName=" +getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&catId=" + catId + "&templateName=" + templateName);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                ResponseBody resp = response.body();
                String responce = resp.string();

                utils.showLog(TAG,"response - "+responce);


                if (responce != null) {
                    try {
                        JSONObject ParentjObject = new JSONObject(responce);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("TestSections");
                            for (int i=0;i<jsonArr.length();i++){
                                JSONObject jOb = jsonArr.optJSONObject(i);
                                JSONArray jArray = jOb.getJSONArray("testSectionDetails");

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<JEETestSectionDetail>>() {
                                }.getType();
                                List<JEETestSectionDetail> list = new ArrayList<>();
                                list.clear();
                                list.addAll(gson.fromJson(jArray.toString(), type));
                                listSectionDetails.addAll(list);
                            }

                            utils.showLog(TAG,"Jee sections "+ listSectionDetails.size());

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvSection.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    rvSection.setAdapter(new JeeSectionsAdapter());
                                }
                            });


                        }
                    }catch (Exception e) {
                    }

                }
            }
        });
    }


    class JeeSectionsAdapter extends RecyclerView.Adapter<JeeSectionsAdapter.ViewHolder>{

        @NonNull
        @Override
        public JeeSectionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new JeeSectionsAdapter.ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_jee_section_details,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull JeeSectionsAdapter.ViewHolder viewHolder, int i) {
            viewHolder.tvSectionName.setText(listSectionDetails.get(i).getSectionName());
            viewHolder.tvQuestType.setText(listSectionDetails.get(i).getQuestType());
            viewHolder.tvPosMarks.setText(listSectionDetails.get(i).getCorrectAnswerMarks());
            viewHolder.tvNegMarks.setText(listSectionDetails.get(i).getWrongAnswerMarks());
            viewHolder.tvNumOfQuestions.setText(listSectionDetails.get(i).getNumQuestions());
            viewHolder.tvTotal.setText((Integer.parseInt(listSectionDetails.get(i).getNumQuestions())*(Integer.parseInt(listSectionDetails.get(i).getCorrectAnswerMarks())))+"");
        }

        @Override
        public int getItemCount() {
            return listSectionDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSectionName,tvQuestType,tvPosMarks,tvNegMarks,tvNumOfQuestions,tvTotal;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSectionName = itemView.findViewById(R.id.tv_jee_sec_name);
                tvQuestType = itemView.findViewById(R.id.tv_jee_q_type);
                tvPosMarks = itemView.findViewById(R.id.tv_jee_positive);
                tvNegMarks = itemView.findViewById(R.id.tv_jee_negative);
                tvNumOfQuestions = itemView.findViewById(R.id.tv_jee_questions);
                tvTotal = itemView.findViewById(R.id.tv_jee_total);
            }
        }
    }

}