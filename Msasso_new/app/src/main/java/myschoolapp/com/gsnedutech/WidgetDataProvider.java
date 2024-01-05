package myschoolapp.com.gsnedutech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import myschoolapp.com.gsnedutech.Models.LiveVideoInfo;
import myschoolapp.com.gsnedutech.Models.ScheduleObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory{

    Context mContext = null;
    Intent intent;


    JSONArray schedulesArray = new JSONArray();
    List<ScheduleObj> scheduleEvents = new ArrayList<>();
    SharedPreferences sh_Pref;
    StudentObj sObj;


    public WidgetDataProvider(Context context, Intent intent) {

        mContext = context;
        this.intent = intent;

    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
    }

    private void initData() {
        sh_Pref = mContext.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);


        getLiveClasses();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return scheduleEvents.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.item_schedule_60);
        view.setTextViewText(R.id.tv_title, scheduleEvents.get(i).getEventTitle());
        view.setTextViewText(R.id.tv_duration, scheduleEvents.get(i).getDuration()+" mins");
        view.setTextViewText(R.id.tv_desc, scheduleEvents.get(i).getEventDesc());

        try {
            view.setTextViewText(R.id.tv_time,new SimpleDateFormat("hh:mm\na").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(scheduleEvents.get(i).getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        switch (scheduleEvents.get(i).getType()) {
            case "live":

                view.setInt(R.id.fl_background, "setBackgroundResource",
                        R.drawable.bg_red_gradient);
                view.setInt(R.id.ll_main, "setBackgroundResource",
                        R.drawable.bg_red_mask);
                break;
            case "K-Hub":
                view.setInt(R.id.fl_background, "setBackgroundResource",
                        R.drawable.bg_blue_gradient);
                view.setInt(R.id.ll_main, "setBackgroundResource",
                        R.drawable.bg_blue_mask);
                break;
            case "test":
                view.setInt(R.id.fl_background, "setBackgroundResource",
                        R.drawable.bg_green_gradient);
                view.setInt(R.id.ll_main, "setBackgroundResource",
                        R.drawable.bg_green_mask);
                break;

        }
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void getLiveClasses() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = null;
        get = new Request.Builder()
                .url(AppUrls.GetStudentLiveVideos + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId() + "&sectionId=" + sObj.getClassCourseSectionId() + "&status=active&filterDate=" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                .build();


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getExams();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        getExams();
                    } else {
                        String resp = responseBody.string();


                        JSONObject ParentjObject = new JSONObject(resp);
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<LiveVideoInfo>>() {
                        }.getType();

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("liveVideoInfo");
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject object = jsonArr.getJSONObject(i);
                                JSONArray newJar = object.getJSONArray("LiveVideos");
                                for (int j = 0; j < newJar.length(); j++) {
                                    schedulesArray.put(newJar.getJSONObject(j));
                                }
                            }





                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                getExams();

            }
        });

    }

    private void getExams() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId() + "&branchId=" + sObj.getBranchId() + "&flag=active&filterDate=" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                createScheduleList();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    createScheduleList();

                } else {
                    String resp = responseBody.string();


                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("StudentTest");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                JSONArray newJar = object.getJSONArray("Tests");
                                for (int j = 0; j < newJar.length(); j++) {
                                    schedulesArray.put(newJar.getJSONObject(j));
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                }
                createScheduleList();
            }
        });
    }

    private void createScheduleList() {



        for (int i = 0; i < schedulesArray.length(); i++) {
            try {

                JSONObject obj = schedulesArray.getJSONObject(i);

                if (obj.has("testName")) {
                    scheduleEvents.add(new ScheduleObj(obj.getString("testStartDate"), obj.getString("testName"), obj.getString("testCategoryName"), obj.getString("testDuration"), "test"));
                } else {
                    scheduleEvents.add(new ScheduleObj(obj.getString("liveStreamStartTime"), obj.getString("liveStreamName"), obj.getString("facultyName"), obj.getString("duration"), "live"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.v("tag","service executed");

    }
}
